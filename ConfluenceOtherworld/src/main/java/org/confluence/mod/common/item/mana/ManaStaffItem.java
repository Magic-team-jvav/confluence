package org.confluence.mod.common.item.mana;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshot;
import org.confluence.lib.api.projectile.ProjectileDamageChannel;
import org.confluence.lib.api.projectile.ProjectileFireAction;
import org.confluence.lib.api.projectile.ProjectileFireContext;
import org.confluence.lib.api.projectile.ProjectileFireTrigger;
import org.confluence.lib.api.projectile.ProjectileLaunch;
import org.confluence.lib.api.projectile.ProjectileWeaponAction;
import org.confluence.lib.api.projectile.ServerProjectileFireService;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.DamageSettableProjectile;
import org.confluence.mod.common.entity.projectile.ProjectileHitRules;
import org.confluence.mod.common.init.ModSoundEvents;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.world.entity.PortEquipmentSlotGroup;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;
import org.mesdag.portlib.wrapper.world.item.component.PortItemAttributeModifiers;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Otherworld 法杖的统一服务端弹幕动作基类。
 *
 * <p>物品只保存伤害、魔力、弹速、冷却和实体工厂等不可变声明。每次施法都会创建独立的
 * {@link ManaProjectileCost}、战斗快照和弹幕布局，并由 MagicLib 发射服务按“准备成本、生成整批、
 * 提交成本和冷却、加入世界、成功表现”的固定顺序执行。法杖本身不直接扣魔力或修改世界。</p>
 *
 * <p>子类可以覆写纯布局钩子调整位置、方向、速度倍率和实体专用状态；需要替换旧实体或写入
 * 物品状态的逻辑必须放在 {@link #onSuccessfulShot(ProjectileFireContext, DamageSettableProjectile)}
 * 对应钩子中，确保只在世界生成成功后发生。</p>
 */
public class ManaStaffItem<E extends DamageSettableProjectile> extends CustomRarityItem
        implements ProjectileWeaponAction {
    public static final ResourceLocation ID = Confluence.asResource("mana_staff");
    protected final ProjectileFactory<E> factory;
    protected final float damage;
    protected final int manaCost;
    protected final float velocity;
    protected final int cooldown;
    private @Nullable List<Component> tooltips;

    public ManaStaffItem(
            Properties properties,
            ModRarity rarity,
            ProjectileFactory<E> factory,
            float damage,
            int manaCost,
            float rawVelocity,
            int cooldown
    ) {
        super(properties, rarity);
        if (!Float.isFinite(damage) || damage < 0.0F) {
            throw new IllegalArgumentException("Staff damage must be finite and non-negative");
        }
        if (manaCost < 0) {
            throw new IllegalArgumentException("Staff mana cost must not be negative");
        }
        if (!Float.isFinite(rawVelocity) || rawVelocity <= 0.0F) {
            throw new IllegalArgumentException("Staff raw velocity must be finite and positive");
        }
        if (cooldown < 0) {
            throw new IllegalArgumentException("Staff cooldown must be non-negative");
        }
        this.damage = damage;
        this.factory = Objects.requireNonNull(factory, "Staff projectile factory must not be null");
        this.manaCost = manaCost;
        this.velocity = rawVelocity / 8.0F;
        this.cooldown = cooldown;
    }

    public ManaStaffItem(
            ModRarity rarity,
            ProjectileFactory<E> factory,
            float damage,
            int manaCost,
            float rawVelocity,
            int cooldown,
            Consumer<PortItemAttributeModifiers.Builder> consumer
    ) {
        this(new Properties().stacksTo(1), rarity, factory, damage, manaCost, rawVelocity, cooldown);
        addAttributeModifiers(consumer);
    }

    /**
     * @param rawVelocity 除以八之前的泰拉瑞亚风格弹速
     */
    public ManaStaffItem(
            ModRarity rarity,
            ProjectileFactory<E> factory,
            float damage,
            int manaCost,
            float rawVelocity,
            int cooldown,
            double critChance
    ) {
        this(new Properties().stacksTo(1), rarity, factory, damage, manaCost, rawVelocity, cooldown);
        if (critChance == 0.0) return;
        addAttributeModifiers(builder -> builder.add(
                LibAttributes.getCriticalChance(),
                new PortAttributeModifier(ID, critChance, PortAttributeModifier.Operation.ADD_VALUE),
                PortEquipmentSlotGroup.MAINHAND));
    }

    public ManaStaffItem<E> withTooltip(Component... tooltips) {
        this.tooltips = Arrays.asList(tooltips);
        return this;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 20;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    /**
     * 普通法杖的原版使用入口只提交有限的 USE_PRESSED 服务端动作。
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (player instanceof ServerPlayer serverPlayer) {
            ServerProjectileFireService.fire(serverPlayer, usedHand, ProjectileFireTrigger.USE_PRESSED);
        }
        return InteractionResultHolder.success(stack);
    }

    /**
     * 为当前服务端请求构建一次独立的法杖发射动作。
     */
    @Override
    public @Nullable ProjectileFireAction createProjectileFireAction(ProjectileFireContext context) {
        Objects.requireNonNull(context, "Projectile fire context must not be null");
        if (!supportsTrigger(context.trigger())) {
            return null;
        }

        ManaProjectileCost cost = new ManaProjectileCost(resolveManaCost(context), this::isManaFree);
        AtomicReference<E> createdProjectile = new AtomicReference<>();
        return ProjectileFireAction.builder(
                        ProjectileDamageChannel.MAGIC,
                        cost,
                        (fireContext, snapshot) -> List.of(createLaunch(
                                fireContext, snapshot, createdProjectile)))
                .baseDamage(damage)
                .baseVelocity(velocity)
                .baseKnockback(baseKnockback(context))
                .triggers(context.trigger())
                .cooldownTicks(resolveCooldown(context))
                .validator(this::validateAction)
                .successAction(fireContext -> {
                    cost.finishSuccessfulAction();
                    E projectile = Objects.requireNonNull(
                            createdProjectile.get(), "Staff transaction completed without a projectile");
                    if (shouldAwardUsageStat(fireContext)) {
                        fireContext.player().awardStat(Stats.ITEM_USED.get(this));
                    }
                    playSuccessfulShot(fireContext, projectile);
                    rayTrace(fireContext, projectile);
                    onSuccessfulShot(fireContext, projectile);
                })
                .build();
    }

    /**
     * 默认只接受一次普通使用；持续法术覆写为服务端持续使用 tick。
     */
    protected boolean supportsTrigger(ProjectileFireTrigger trigger) {
        return trigger == ProjectileFireTrigger.USE_PRESSED
                || trigger == ProjectileFireTrigger.ATTACK_PRESSED;
    }

    /**
     * 在准备魔力成本和实体之前校验当前动作。
     *
     * <p>普通单击法杖只依赖统一服务已经完成的持有物校验，因此默认允许；持续施法类必须覆盖本方法，
     * 将服务端玩家的真实使用状态纳入校验，不能仅相信网络入口携带的触发类型。</p>
     */
    protected boolean validateAction(ProjectileFireContext context) {
        return true;
    }

    /**
     * 判断玩家是否仍在用请求中的手持续使用同一件武器。
     *
     * <p>{@link ProjectileFireContext#weapon()} 是防御性副本，所以这里按物品与 NBT 比较；
     * 同时检查实时手中物品，防止换手、换栈或脱离使用状态后继续伪造持续脉冲。</p>
     */
    protected final boolean isActivelyUsingCurrentWeapon(ProjectileFireContext context) {
        ServerPlayer player = context.player();
        ItemStack heldStack = player.getItemInHand(context.hand());
        return player.isUsingItem()
                && player.getUsedItemHand() == context.hand()
                && ItemStack.isSameItemSameTags(player.getUseItem(), heldStack)
                && ItemStack.isSameItemSameTags(heldStack, context.weapon());
    }

    /**
     * 返回当前请求的基础魔力成本；持续法术可按每次脉冲拆分。
     */
    protected float resolveManaCost(ProjectileFireContext context) {
        return manaCost;
    }

    /**
     * 特殊武器可在服务端声明本次动作免魔力。
     */
    protected boolean isManaFree(ProjectileFireContext context) {
        return false;
    }

    /**
     * 返回成功生成后提交的冷却。
     */
    protected int resolveCooldown(ProjectileFireContext context) {
        return cooldown;
    }

    /**
     * 返回由快照统一解析的基础击退。
     *
     * <p>现有法术实体仍保留各自命中点声明的击退强度，因此默认值为零；显式迁移了击退声明的
     * 新法术可以覆写本方法。</p>
     */
    protected float baseKnockback(ProjectileFireContext context) {
        return 0.0F;
    }

    /**
     * 创建、配置并返回一枚尚未加入世界的弹幕描述。
     */
    protected ProjectileLaunch createLaunch(
            ProjectileFireContext context,
            ProjectileCombatSnapshot snapshot,
            AtomicReference<E> createdProjectile
    ) {
        E projectile = Objects.requireNonNull(
                factory.create(context.player()), "Staff projectile factory returned null");
        if (!createdProjectile.compareAndSet(null, projectile)) {
            throw new IllegalStateException("Staff projectile pattern may only create one projectile");
        }
        projectile.setDefaultVelocity(snapshot.resolvedVelocity());
        configureProjectile(context, snapshot, projectile);
        return new ProjectileLaunch(
                projectile,
                launchPosition(context, snapshot, projectile),
                launchDirection(context, snapshot, projectile),
                velocityMultiplier(context, snapshot, projectile));
    }

    /**
     * 配置不修改世界的实体专用状态。
     */
    protected void configureProjectile(
            ProjectileFireContext context,
            ProjectileCombatSnapshot snapshot,
            E projectile
    ) {}

    /**
     * 默认从玩家眼睛略下方生成。
     */
    protected Vec3 launchPosition(
            ProjectileFireContext context,
            ProjectileCombatSnapshot snapshot,
            E projectile
    ) {
        return new Vec3(
                context.player().getX(),
                context.player().getEyeY() - 0.1,
                context.player().getZ());
    }

    /**
     * 默认沿服务端冻结的玩家视角发射。
     */
    protected Vec3 launchDirection(
            ProjectileFireContext context,
            ProjectileCombatSnapshot snapshot,
            E projectile
    ) {
        return context.viewVector();
    }

    /**
     * 默认使用完整快照弹速。
     */
    protected float velocityMultiplier(
            ProjectileFireContext context,
            ProjectileCombatSnapshot snapshot,
            E projectile
    ) {
        return 1.0F;
    }

    /**
     * 普通法杖只在整次动作成功后播放一次射击声。
     */
    protected void playSuccessfulShot(ProjectileFireContext context, E projectile) {
        context.level().playSound(
                null,
                context.player().getX(),
                context.player().getEyeY(),
                context.player().getZ(),
                getShootSound(),
                SoundSource.PLAYERS,
                1.0F,
                1.0F);
    }

    /**
     * 补偿法杖发射后第一 tick 内过快弹幕穿过近距离目标的问题。
     *
     * <p>1.21 侧会在弹幕加入世界后沿初始弹道额外扫一小段。1.20 重写后发射事务会先冻结前缀与魔法伤害快照，
     * 所以这里直接使用弹幕最终保存的默认速度，保证前缀调整后的速度、伤害和命中预算仍然一致。</p>
     */
    protected void rayTrace(ProjectileFireContext context, E projectile) {
        Vec3 startVec = projectile.position();
        Vec3 endVec = startVec.add(context.viewVector().scale(projectile.getDefaultVelocity()));
        AABB searchBox = new AABB(startVec, endVec).inflate(0.3);
        for (Entity victim : context.level().getEntities(
                context.player(),
                searchBox,
                projectile::canHitEntity)) {
            AABB hitBox = victim.getBoundingBox().inflate(0.3);
            if (!hitBox.contains(startVec) && hitBox.clip(startVec, endVec).isEmpty()) {
                continue;
            }
            Entity impacted = ProjectileHitRules.impactedEntity(victim);
            context.player().setLastHurtMob(impacted);
            if (impacted.hurt(projectile.getDamageSource(), projectile.getCalculatedDamage())) {
                projectile.recordSuccessfulHit(impacted);
            }
        }
    }

    protected void onSuccessfulShot(ProjectileFireContext context, E projectile) {}

    /** 持续施法在按下使用键时自行记一次统计，普通法杖按成功动作记。 */
    protected boolean shouldAwardUsageStat(ProjectileFireContext context) {
        return true;
    }

    protected SoundEvent getShootSound() {
        return ModSoundEvents.REGULAR_STAFF_SHOOT.get();
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getMaxStackSize() == 1;
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            @Nullable Level level,
            List<Component> tooltipComponents,
            TooltipFlag tooltipFlag
    ) {
        tooltipComponents.add(Component.translatable(
                "tooltip.confluence.attack_damage", damage).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable(
                "tooltip.confluence.mana_cost", manaCost).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable(
                "tooltip.confluence.velocity", velocity).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable(
                "tooltip.confluence.cooldown", cooldown).withStyle(ChatFormatting.GRAY));
        if (tooltips != null) {
            tooltipComponents.addAll(tooltips);
        }
    }

    @FunctionalInterface
    public interface ProjectileFactory<E extends Projectile> {
        E create(ServerPlayer player);
    }
}
