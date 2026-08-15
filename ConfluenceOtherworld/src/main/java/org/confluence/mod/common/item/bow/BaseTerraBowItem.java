package org.confluence.mod.common.item.bow;

import PortLib.extensions.net.minecraft.world.item.ItemStack.PortItemStackExtension;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.confluence.lib.api.projectile.*;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.util.ModUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.mesdag.portlib.wrapper.common.extensions.IPortArrowItemExtension;
import org.mesdag.portlib.wrapper.common.extensions.IPortBowItemExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * 泰拉弓的服务端权威发射基类。
 *
 * <p>原版松手入口只负责读取服务端蓄力和弹药，实际弹幕创建、弹药消耗、耐久扣除、战斗快照、
 * 世界提交、声音和统计都交给 MagicLib 的发射事务处理。多重射击只消耗一份弹药和一次耐久，
 * 第一支箭保留原版拾取语义，其余分裂箭禁止拾取。</p>
 */
public class BaseTerraBowItem extends BowItem implements IPortBowItemExtension {
    private final float baseDamage;

    public BaseTerraBowItem(float baseDamage) {
        this(baseDamage, new Properties());
    }

    public BaseTerraBowItem(float baseDamage, Properties properties) {
        super(properties.stacksTo(1));
        if (!Float.isFinite(baseDamage) || baseDamage < 0.0F) {
            throw new IllegalArgumentException("Bow base damage must be finite and non-negative");
        }
        this.baseDamage = baseDamage;
    }

    // region 子类声明

    protected int getMultiShootCount() { return 1; }

    protected boolean canMultiShoot(ItemStack ammo) { return false; }

    protected Vec3 getMultiShootOffset(int shootingIndex, int shootingTotality) { return null; }

    @Nullable
    public BaseArrowEntity createCustomArrow(LivingEntity shooter, ItemStack ammo, ItemStack weapon) {
        return null;
    }

    protected float getInaccuracy() {return 0.0F;}

    public void modifyArrowEntity(BaseArrowEntity entity) {}

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return ModUtils.supportsEnchantment(stack, enchantment);
    }

    /**
     * 统一设置动作基础伤害；分裂箭平分本次武器伤害。
     */
    @Override
    public AbstractArrow customArrow(AbstractArrow arrow, ItemStack projectileStack, ItemStack weaponStack) {
        int multiShoot = canMultiShoot(projectileStack) ? getMultiShootCount() : 1;
        arrow.setBaseDamage(baseDamage / multiShoot);
        return arrow;
    }

    /**
     * 原版弓松手的唯一权威入口。
     *
     * <p>Forge 的 ArrowLoose 事件仍在事务前参与蓄力裁定，但不会提前拆弹药、损耗耐久或播放声音。</p>
     */
    @Override
    public void releaseUsing(ItemStack weapon, Level level, LivingEntity living, int timeLeft) {
        if (!(level instanceof ServerLevel) || !(living instanceof ServerPlayer player)) {
            return;
        }
        if (player.getProjectile(weapon).isEmpty() && !player.isCreative()) {
            return;
        }
        int chargeTicks = getUseDuration(weapon) - timeLeft;
        chargeTicks = ForgeEventFactory.onArrowLoose(weapon, level, player, chargeTicks, true);
        if (chargeTicks < 0) {
            return;
        }
        float power = getPowerForCharge(weapon, chargeTicks);
        if (power < 0.1F) {
            return;
        }

        InteractionHand hand = player.getUsedItemHand();
        fireBowAction(
                player, hand, weapon, ProjectileFireTrigger.VANILLA_BOW_RELEASE, power);
    }

    /**
     * 在配置允许或物品属于自动发射标签时，蓄力完成便主动走原版松手流程。
     * 这里只结束当前使用动作，弹药、伤害和冷却仍由 {@link #releaseUsing} 的服务端事务处理。
     */
    @Override
    public void onUseTick(Level level, LivingEntity living, ItemStack weapon, int remainingUseDuration) {
        if (level.isClientSide || !(living instanceof ServerPlayer player)) {
            return;
        }
        if (!CommonConfigs.AUTO_RELEASE_ALL_BOWS.get()
                && !weapon.is(ModTags.Items.AUTOMATIC_BOW)) {
            return;
        }
        int chargeTicks = getUseDuration(weapon) - remainingUseDuration;
        if (getPowerForCharge(weapon, chargeTicks) >= 1.0F) {
            /*
             * 这里不能依赖 ServerPlayer#releaseUsingItem 重新绕一圈原版释放流程。
             * 真实客户端松手时它没有问题，但 GameTest 与部分服务端自动触发场景中，
             * FakePlayer 的持续使用状态可能先被清掉，导致本次满蓄自动发射没有稳定地
             * 进入我们的权威事务。直接调用本物品的 releaseUsing 可以保留同一套弹药、
             * 快照、冷却和声音逻辑，同时只提交一次弹幕。
             */
            releaseUsing(weapon, level, living, remainingUseDuration);
            player.stopUsingItem();
        }
    }

    /**
     * 使用玩家当前弹药提交一次完整的弓发射事务。
     *
     * <p>这个方法是普通弓与特殊弓共享的唯一事务入口：弹药选择、无限弹药判定、成本准备、
     * 战斗快照和世界提交都留在基类。特殊弓只需要选择调用时机，并通过受保护钩子改变速度、
     * 成本或弹幕布局，不应该在子类里再次拼装相同事务。</p>
     *
     * @param player  服务端射手
     * @param hand    实际使用武器的手
     * @param weapon  当前武器栈
     * @param trigger 本次发射入口
     * @param power   子类定义的归一化发射强度；普通弓使用蓄力值
     * @return 发射服务返回的精确结果
     */
    protected final ProjectileFireResult fireBowAction(
            ServerPlayer player,
            InteractionHand hand,
            ItemStack weapon,
            ProjectileFireTrigger trigger,
            float power
    ) {
        ItemStack selectedAmmo = player.getProjectile(weapon);
        boolean consumeAmmo;
        ItemStack projectileAmmo;
        if (selectedAmmo.isEmpty()) {
            if (!player.isCreative()) {
                return ProjectileFireResult.NO_RESOURCE;
            }
            projectileAmmo = Items.ARROW.getDefaultInstance();
            consumeAmmo = false;
        } else {
            boolean infinite = player.isCreative()
                    || selectedAmmo.getItem() instanceof IPortArrowItemExtension extension
                    && extension.isInfinite(selectedAmmo, weapon, player)
                    || selectedAmmo.is(Items.ARROW)
                    && EnchantmentHelper.getItemEnchantmentLevel(
                    Enchantments.INFINITY_ARROWS, weapon) > 0;
            projectileAmmo = selectedAmmo.copyWithCount(1);
            consumeAmmo = !infinite;
        }
        if (!consumeAmmo) {
            PortItemStackExtension.setIntangibleProjectile(projectileAmmo, true);
        }

        boolean fullPull = isFullPull(trigger, power);
        BowProjectileCost cost = new BowProjectileCost(
                selectedAmmo,
                consumeAmmo,
                getShotDurabilityUse(trigger, projectileAmmo));
        ProjectileFireAction action = ProjectileFireAction.builder(
                        ProjectileDamageChannel.RANGED,
                        cost,
                        (context, snapshot) -> createProjectileLaunches(
                                context,
                                snapshot,
                                projectileAmmo,
                                fullPull,
                                !consumeAmmo,
                                trigger))
                .baseDamage(baseDamage)
                .baseVelocity(getShotVelocity(trigger, power))
                .baseKnockback(getShotKnockback(trigger, weapon))
                .inherentCritical(isShotCritical(trigger, power))
                .triggers(trigger)
                .cooldownTicks(getShotCooldown(trigger, weapon))
                .validator(context -> isFireContextValid(context, trigger))
                .successAction(context -> onSuccessfulFire(context, trigger, power))
                .build();
        return ServerProjectileFireService.fire(player, hand, trigger, action);
    }

    /**
     * 构造本次尚未入世的弹幕列表。
     *
     * <p>普通弓直接生成面向准星的一批箭。代达罗斯风暴弓这类复杂武器可以覆盖此方法，
     * 改为从天空、侧面或其他位置生成，但仍不能在这里扣弹药或直接把实体加入世界。</p>
     */
    protected List<ProjectileLaunch> createProjectileLaunches(
            ProjectileFireContext context,
            ProjectileCombatSnapshot snapshot,
            ItemStack projectileAmmo,
            boolean fullPull,
            boolean intangible,
            ProjectileFireTrigger trigger
    ) {
        return createArrowLaunches(
                context, snapshot, projectileAmmo, fullPull, intangible);
    }

    /**
     * 返回本次动作应用属性修正前的弹速。
     */
    protected float getShotVelocity(ProjectileFireTrigger trigger, float power) {
        return getActionVelocity(power);
    }

    /**
     * 返回本次动作的基础击退；普通弓沿用冲击附魔。
     */
    protected float getShotKnockback(ProjectileFireTrigger trigger, ItemStack weapon) {
        return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, weapon);
    }

    /**
     * 返回成功提交后消耗的耐久；特殊持续发射弓可以覆盖为零。
     */
    protected int getShotDurabilityUse(ProjectileFireTrigger trigger, ItemStack projectileAmmo) {
        return getDurabilityUse(projectileAmmo);
    }

    /**
     * 判定战斗快照是否携带必定暴击。
     */
    protected boolean isShotCritical(ProjectileFireTrigger trigger, float power) {
        return power >= 1.0F;
    }

    /**
     * 判定箭实体是否获得满蓄力语义。
     */
    protected boolean isFullPull(ProjectileFireTrigger trigger, float power) {
        return power >= 1.0F;
    }

    /**
     * 普通快速弓仅在松手发射时添加冷却。
     */
    protected int getShotCooldown(ProjectileFireTrigger trigger, ItemStack weapon) {
        return trigger == ProjectileFireTrigger.VANILLA_BOW_RELEASE
                && weapon.is(ModTags.Items.FAST_BOW) ? 5 : 0;
    }

    /**
     * 在提交前复核持续使用动作仍属于当前手中的同一把弓。
     *
     * <p>松手动作由原版入口同步提交，不要求玩家仍处于持续使用状态；持续脉冲则必须防止
     * 切换物品或停止使用后残留的一次发射。</p>
     */
    protected boolean isFireContextValid(
            ProjectileFireContext context,
            ProjectileFireTrigger trigger
    ) {
        if (trigger != ProjectileFireTrigger.CONTINUOUS_USE_TICK) {
            return true;
        }
        return context.player().isUsingItem()
                && context.player().getUsedItemHand() == context.hand()
                && ItemStack.isSameItemSameTags(
                context.player().getUseItem(),
                context.player().getItemInHand(context.hand()));
    }

    /**
     * 成功提交后的公共表现；特殊入口默认不播放普通弓松手反馈。
     */
    protected void onSuccessfulFire(
            ProjectileFireContext context,
            ProjectileFireTrigger trigger,
            float power
    ) {
        if (trigger == ProjectileFireTrigger.VANILLA_BOW_RELEASE) {
            onSuccessfulRelease(context, power);
        }
    }

    /**
     * 创建本次多重射击声明数量的尚未入世箭矢描述。
     */
    protected List<ProjectileLaunch> createArrowLaunches(
            ProjectileFireContext context,
            ProjectileCombatSnapshot snapshot,
            ItemStack projectileAmmo,
            boolean fullPull
    ) {
        return createArrowLaunches(
                context,
                snapshot,
                projectileAmmo,
                fullPull,
                PortItemStackExtension.getIntangibleProjectile(projectileAmmo));
    }

    /**
     * 创建箭矢批次，并显式传递本次弹药是否不可回收。
     *
     * <p>布尔值来自服务端弹药计划，不依赖视觉弹药副本上的桥接标记，供创造、无限附魔和
     * 外部自定义无限弹药共享同一安全语义。</p>
     */
    protected List<ProjectileLaunch> createArrowLaunches(
            ProjectileFireContext context,
            ProjectileCombatSnapshot snapshot,
            ItemStack projectileAmmo,
            boolean fullPull,
            boolean intangible
    ) {
        int count = canMultiShoot(projectileAmmo) ? getMultiShootCount() : 1;
        if (count < 1) {
            throw new IllegalArgumentException("Bow multishot count must be positive");
        }
        List<ProjectileLaunch> launches = new ArrayList<>(count);
        for (int index = 0; index < count; index++) {
            Projectile projectile = createArrowProjectile(
                    context.level(),
                    context.player(),
                    context.weapon(),
                    projectileAmmo,
                    fullPull,
                    intangible);
            shootProjectile(
                    context.player(), projectile, 0, snapshot.resolvedVelocity(),
                    1.0F + getInaccuracy(), 0.0F, null);
            Vec3 offset = getMultiShootOffset(index, count);
            if (offset != null) {
                transformAndApplyOffsetToProjectile(projectile, offset);
            }
            processArrowBaseEffects(
                    context.player(), context.hand(), context.weapon(), projectile, index, count);
            launches.add(new ProjectileLaunch(
                    projectile, projectile.position(), projectile.getDeltaMovement()));
        }
        return List.copyOf(launches);
    }

    /**
     * 创建并配置具体箭矢。武器自定义实体优先于弹药实体，确保普通木箭也能触发武器专有效果。
     */
    protected Projectile createArrowProjectile(
            ServerLevel level,
            ServerPlayer shooter,
            ItemStack weapon,
            ItemStack ammo,
            boolean fullPull
    ) {
        // PortLib 会把无形弹药标记消费进箭实体；这里先冻结标记，并在所有外部自定义箭工厂
        // 返回后再次落实拾取语义，避免附属实现遗漏桥接步骤而复制无限弹药。
        boolean intangible = PortItemStackExtension.getIntangibleProjectile(ammo);
        BaseArrowEntity custom = createCustomArrow(shooter, ammo, weapon);
        AbstractArrow arrow;
        if (custom != null) {
            modifyArrowEntity(custom);
            arrow = custom;
        } else {
            IPortArrowItemExtension arrowItem = ammo.getItem() instanceof IPortArrowItemExtension extension
                    ? extension
                    : (IPortArrowItemExtension) Items.ARROW;
            arrow = arrowItem.createArrow(level, ammo, shooter, weapon);
        }
        arrow = customArrow(arrow, ammo, weapon);
        if (intangible) {
            arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        }

        // 统一快照负责暴击和击退；箭实体不再在命中时重复随机或读取实时属性。
        arrow.setCritArrow(false);
        int punch = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, weapon);
        if (punch > 0) {
            arrow.setKnockback(punch);
        }
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, weapon) > 0) {
            arrow.setSecondsOnFire(100);
        }
        if (!(arrow instanceof BaseArrowEntity)) {
            int power = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, weapon);
            if (power > 0) {
                arrow.setBaseDamage(arrow.getBaseDamage() + power * 0.5D + 0.5D);
            }
        } else if (fullPull) {
            ((BaseArrowEntity) arrow).fullPull = true;
        }
        return arrow;
    }

    /**
     * 调用可被附属覆盖的原创建钩子，并在最后落实服务端弹药计划声明的不可回收语义。
     */
    protected Projectile createArrowProjectile(
            ServerLevel level,
            ServerPlayer shooter,
            ItemStack weapon,
            ItemStack ammo,
            boolean fullPull,
            boolean intangible
    ) {
        Projectile projectile = createArrowProjectile(level, shooter, weapon, ammo, fullPull);
        if (intangible && projectile instanceof AbstractArrow arrow) {
            arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        }
        return projectile;
    }

    /**
     * 成功生成后才播放声音、记录统计并同步另一只手的快速弓冷却。
     */
    protected void onSuccessfulRelease(ProjectileFireContext context, float power) {
        context.level().playSound(
                null,
                context.player().getX(),
                context.player().getY(),
                context.player().getZ(),
                SoundEvents.ARROW_SHOOT,
                SoundSource.PLAYERS,
                1.0F,
                1.0F / (context.level().getRandom().nextFloat() * 0.4F + 1.2F) + power * 0.5F);
        context.player().awardStat(Stats.ITEM_USED.get(this));
        if (context.weapon().is(ModTags.Items.FAST_BOW)) {
            ItemStack otherHand = context.player().getItemInHand(
                    context.hand() == InteractionHand.MAIN_HAND
                            ? InteractionHand.OFF_HAND
                            : InteractionHand.MAIN_HAND);
            if (otherHand.getItem() instanceof BowItem) {
                context.player().getCooldowns().addCooldown(otherHand.getItem(), 5);
            }
        }
    }

    /**
     * 根据当前弓种类计算蓄力，避免兼容桥绕过短弓/快速弓曲线。
     */
    protected float getPowerForCharge(ItemStack weapon, int chargeTicks) {
        if (this instanceof ShortBowItem shortBow) {
            return shortBow.getShortPowerForTime(chargeTicks);
        }
        if (weapon.is(ModTags.Items.FAST_BOW)) {
            return getFastBowPowerForTime(chargeTicks);
        }
        return BowItem.getPowerForTime(chargeTicks);
    }

    /**
     * 返回应用 RANGED_VELOCITY 前的动作弹速。
     */
    protected float getActionVelocity(float power) {
        return this instanceof ShortBowItem shortBow
                ? power * shortBow.getVelocityMultiplier()
                : power * 6.0F;
    }

    /**
     * 禁止任何代码重新调用会在事务外生成实体的旧 PortLib 桥。
     */
    @Override
    public final void shoot(
            ServerLevel level,
            LivingEntity shooter,
            InteractionHand hand,
            ItemStack weapon,
            List<ItemStack> projectileItems,
            float velocity,
            float inaccuracy,
            boolean isCrit,
            @Nullable LivingEntity target
    ) {
        throw new UnsupportedOperationException(
                "Legacy bow shooting bridge is disabled; use the authoritative release transaction");
    }

    public static void processArrowBaseEffects(
            LivingEntity shooter,
            InteractionHand hand,
            ItemStack weapon,
            Projectile projectile,
            int projectileIndex,
            int multiShootCount
    ) {
        if (!(projectile instanceof AbstractArrow abstractArrow)) {
            return;
        }
        if (projectileIndex > 0) {
            abstractArrow.pickup = AbstractArrow.Pickup.DISALLOWED;
        }
        ShortBowItem.applyToArrow(weapon, abstractArrow);
        processArrowSpecialEffects(shooter, abstractArrow, multiShootCount);
    }

    public static void processArrowSpecialEffects(
            LivingEntity shooter,
            AbstractArrow abstractArrow,
            int multiShootCount
    ) {
        if (abstractArrow instanceof BaseArrowEntity terraArrow
                && multiShootCount > 1 && !terraArrow.hasAutoDiscard()) {
            terraArrow.setAutoDiscard(100);
        }
    }

    public static void transformAndApplyOffsetToProjectile(Projectile projectile, Vec3 offset) {
        Vec3 initDirection = projectile.getDeltaMovement();
        float yaw = (float) (-Math.atan2(initDirection.z, initDirection.x));
        float pitch = (float) (Math.atan2(
                initDirection.y,
                Math.sqrt(initDirection.x * initDirection.x + initDirection.z * initDirection.z)));
        Quaternionf rotation = new Quaternionf().rotateY(yaw).rotateZ(pitch);
        Vec3 transformed = new Vec3(rotation.transform(offset.toVector3f()));
        projectile.setPos(projectile.position().add(transformed));
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    public static float getFastBowPowerForTime(int chargeTicks) {
        float value = chargeTicks / 20.0F;
        value = (value * value + value * 2.0F) / 3.0F * 0.5F + 0.5F;
        return Math.min(value, 1.0F);
    }

    public float getBaseDamage() {
        return baseDamage;
    }
}
