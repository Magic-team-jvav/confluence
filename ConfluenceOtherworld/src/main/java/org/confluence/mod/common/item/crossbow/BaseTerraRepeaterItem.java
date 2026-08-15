package org.confluence.mod.common.item.crossbow;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.api.projectile.*;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.lib.util.DelayTaskHolder;
import org.confluence.lib.util.LibEnchantmentUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.RepeaterContents;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.item.bow.BaseTerraBowItem;
import org.confluence.mod.common.item.tooltipcomponent.RepeaterComponent;
import org.confluence.mod.mixed.IAbstractArrow;
import org.confluence.mod.network.s2c.RepeaterShootingPayloadS2C;
import org.confluence.mod.util.ModUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.mesdag.portlib.attachment.IPortAttachmentHolder;
import org.mesdag.portlib.wrapper.common.extensions.IPortArrowItemExtension;
import org.mesdag.portlib.wrapper.common.extensions.IPortCrossbowItemExtension;
import org.mesdag.portlib.wrapper.common.extensions.IPortItemPropertiesExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * 1.20.1 连弩实现：右键装填弹仓，攻击键通过统一 RANGED 动作发射。
 *
 * <p>弹仓是不可变当前格式组件；并发箭作为同一批原子生成，后续 burst tick 复用首发冻结快照，
 * 但各自只在实际入世前扣除一枚弹药和一次耐久。切换武器、死亡或断开会让延迟任务停止，尚未
 * 执行的批次不会提前损失资源，也不能转移到另一把连弩。</p>
 */
public class BaseTerraRepeaterItem extends CrossbowItem implements IPortCrossbowItemExtension, ProjectileWeaponAction {
    public static final List<Component> TOOLTIP = TooltipItem.getTooltipsFromString("repeater", 2, ChatFormatting.GRAY);

    public static final String ATTACK_SPEED_TEXT = "attribute.name.repeater.attack_speed";
    public static final String KNOCKBACK_TEXT = "attribute.name.repeater.knockback";
    public static final String TORRENT_COUNT_TEXT = "attribute.name.repeater.torrent_count";
    public static final String CONCURRENCY_COUNT_TEXT = "attribute.name.repeater.concurrency_count";
    public static final String FIRING_INTERVAL_TEXT = "attribute.name.repeater.firing_interval";
    public static final String RELOAD_SPEED_TEXT = "attribute.name.repeater.reload_speed";
    public static final String ARROW_CAPACITY_TEXT = "attribute.name.repeater.arrow_capacity";

    public static final String REPEATER_CONTINUOUS_SHOOTING = "repeater.continuous_shooting";

    /**
     * 基础伤害
     */
    private final float baseDamage;
    /**
     * 击退
     */
    private final float baseKnockback;
    /**
     * 装弹速度
     */
    private final int baseReloadSpeed;
    /**
     * 射击间隔
     */
    private final int baseShootInterval;
    /**
     * 容量
     */
    private final int baseCapacity;
    /**
     * 基础箭矢速度
     */
    private final float baseArrowSpeed;
    /**
     * 连发个数（每次射击会射出多少支箭，每个间隔一帧）
     */
    private final IRandomCount baseBurstCount;
    /**
     * 并发个数（同时射出多少支箭，有一定的散射角度）
     */
    private final IRandomCount baseConcurrentCount;
    /**
     * 并发角度（并发个数时，每个箭的偏移角度）
     */
    private final IRandomCount baseConcurrentAngle;
    /**
     * 并发间隔（并发个数时，每个箭的间隔）
     */
    private final IRandomCount baseConcurrentInterval;
    /**
     * 弹药限制
     */
    private final AmmunitionRestrictions ammunitionRestrictions;

    private final ModifyArrowBuilder modifyArrowBuilder;

    /**
     * 构造连弩
     *
     * @param properties         物品属性
     * @param baseDamage         基础伤害
     * @param modifyArrowBuilder 箭矢修改构建器
     * @param repeaterBuilder    连弩构建器
     */
    public BaseTerraRepeaterItem(Properties properties, float baseDamage, ModifyArrowBuilder modifyArrowBuilder, Builder repeaterBuilder) {
        super(modifyArrowBuilder.buildProperties(properties.stacksTo(1)
                        .component(ModDataComponentTypes.REPEATER_CONTENTS, RepeaterContents.fromItems(repeaterBuilder.capacity))
                )
        );
        this.baseReloadSpeed = repeaterBuilder.reloadSpeed;
        this.baseShootInterval = repeaterBuilder.shootInterval;
        this.baseCapacity = repeaterBuilder.capacity;
        this.baseArrowSpeed = repeaterBuilder.arrowSpeed;
        this.baseBurstCount = repeaterBuilder.burstCount;
        this.baseConcurrentCount = repeaterBuilder.concurrentCount;
        this.ammunitionRestrictions = repeaterBuilder.ammunitionRestrictions;
        this.baseKnockback = repeaterBuilder.knockback;
        this.baseConcurrentAngle = repeaterBuilder.concurrentAngle;
        this.baseConcurrentInterval = repeaterBuilder.concurrentInterval;
        this.baseDamage = baseDamage;
        this.modifyArrowBuilder = modifyArrowBuilder;
    }

    /// 构造连弩
    ///
    /// @param baseDamage            基础伤害
    /// @param bowModifyArrowBuilder 箭矢修改构建器
    /// @param repeaterBuilder       连弩构建器
    public BaseTerraRepeaterItem(float baseDamage, ModifyArrowBuilder bowModifyArrowBuilder, Builder repeaterBuilder) {
        this(new Properties(), baseDamage, bowModifyArrowBuilder, repeaterBuilder);
    }

    public int getReloadSpeed(LivingEntity shooter, InteractionHand hand) {
        return getReloadSpeed(shooter, shooter.getItemInHand(hand));
    }

    /**
     * 返回当前物品的实际装填时间。
     *
     * <p>原版 1.20.1 的快速装填每级减少 5 tick。连弩拥有各自的基础装填时间，
     * 因而不能直接调用写死 25 tick 基准的原版静态方法，但必须保留相同的每级
     * 修正语义，并保证异常高等级附魔也不会产生零或负时间。</p>
     */
    public int getReloadSpeed(LivingEntity shooter, ItemStack stack) {
        int quickCharge = LibEnchantmentUtils.getEnchantmentLevel(
                Enchantments.QUICK_CHARGE, stack);
        return Math.max(1, baseReloadSpeed - quickCharge * 5);
    }

    public int getShootInterval(LivingEntity shooter, InteractionHand hand) {
        return getShootInterval(shooter, shooter.getItemInHand(hand));
    }

    /// 获取射击间隔
    public int getShootInterval(LivingEntity shooter, ItemStack stack) {
        return baseShootInterval;
    }

    public int getCapacity() {
        return this.baseCapacity;
    }

    public float getArrowSpeed(LivingEntity shooter, InteractionHand hand) {
        return this.baseArrowSpeed;
    }

    public int getBurstCount(LivingEntity shooter, InteractionHand hand) {
        int base = Math.max(1, Math.round(this.baseBurstCount.getCount(shooter.getRandom())));
        if (!(shooter.level() instanceof ServerLevel)) {
            return base;
        }
        int enchantmentBonus = LibEnchantmentUtils.getEnchantmentLevel(
                Enchantments.MULTISHOT, shooter.getItemInHand(hand));
        /*
         * 原版多重射击一级把一枚弹丸扩展为三枚，即额外两枚。连弩将这两枚
         * 转换为两个受控 burst tick，继续沿用逐次入世、逐次扣弹的事务语义。
         */
        return Math.max(1, base + enchantmentBonus * 2);
    }

    public int getConcurrentCount(LivingEntity shooter, InteractionHand hand) {
        return Math.round(this.baseConcurrentCount.getCount(shooter.getRandom()));
    }

    public float getConcurrentAngle(LivingEntity shooter, InteractionHand hand) {
        return this.baseConcurrentAngle.getCount(shooter.getRandom());
    }

    public float getConcurrentInterval(LivingEntity shooter, InteractionHand hand) {
        return this.baseConcurrentInterval.getCount(shooter.getRandom());
    }

    public float getDamage(LivingEntity shooter, InteractionHand hand) {
        return this.baseDamage;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (other.isEmpty()) {
            return action == ClickAction.SECONDARY
                    && unloadFirstContents(stack, player, access);
        }
        if (!getAllSupportedProjectiles(stack).test(other)) {
            return false;
        }
        int requested = switch (action) {
            case PRIMARY -> other.getCount();
            case SECONDARY -> 1;
        };
        int inserted = insertIntoContents(stack, other, requested);
        if (inserted <= 0) {
            return false;
        }
        if (!player.isCreative()) {
            other.shrink(inserted);
        }
        playAerialShootingSound(player);
        return true;
    }

    /**
     * 把弹仓首组弹药完整移到空光标。
     *
     * <p>先要求光标槽接受物品，再提交新的不可变弹仓，避免槽位拒绝写入时已经
     * 删除弹药。该操作只是取回真实弹药，创造模式也不能复制弹仓内容。</p>
     */
    private boolean unloadFirstContents(
            ItemStack weapon,
            Player player,
            SlotAccess access
    ) {
        RepeaterContents contents = weapon.getOrDefault(
                ModDataComponentTypes.REPEATER_CONTENTS.get(),
                RepeaterContents.EMPTY);
        List<ItemStack> stored = contents.nonEmptyStream().toList();
        if (stored.isEmpty()) {
            return false;
        }
        ItemStack removed = stored.get(0).copy();
        if (!access.set(removed)) {
            return false;
        }
        List<ItemStack> remaining = stored.stream()
                .skip(1)
                .map(ItemStack::copy)
                .toList();
        weapon.set(
                ModDataComponentTypes.REPEATER_CONTENTS.get(),
                RepeaterContents.fromItems(
                        remaining,
                        contents.getMaxItemCapacity()));
        playRemoveSound(player);
        return true;
    }

    public static boolean isCharged(ItemStack crossbowStack) {
        RepeaterContents contents = crossbowStack.get(ModDataComponentTypes.REPEATER_CONTENTS);
        return contents != null && !contents.isEmpty();
    }

    protected static Vector3f getProjectileShotVector(LivingEntity shooter, Vec3 distance, float angle) {
        Vector3f vector3f = distance.toVector3f().normalize();
        Vector3f vector3f1 = new Vector3f(vector3f).cross(new Vector3f(0.0F, 1.0F, 0.0F));
        if ((double) vector3f1.lengthSquared() <= 1.0E-7) {
            Vec3 vec3 = shooter.getUpVector(1.0F);
            vector3f1 = new Vector3f(vector3f).cross(vec3.toVector3f());
        }

        Vector3f vector3f2 = new Vector3f(vector3f).rotateAxis(Mth.HALF_PI, vector3f1.x, vector3f1.y, vector3f1.z);
        return new Vector3f(vector3f).rotateAxis(angle * Mth.DEG_TO_RAD, vector3f2.x, vector3f2.y, vector3f2.z);
    }

    protected static float getShotPitch(RandomSource random, int index) {
        return index == 0 ? 1.0F : getRandomShotPitch((index & 1) == 1, random);
    }

    protected static float getRandomShotPitch(boolean isHighPitched, RandomSource random) {
        float f = isHighPitched ? 0.63F : 0.43F;
        return 1.0F / (random.nextFloat() * 0.5F + 1.8F) + f;
    }

    private void playRemoveSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playAerialShootingSound(Entity entity) {
        entity.playSound(ModSoundEvents.REPEATER_ITEM_AERIAL_SHOOTING.get());
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean shootingPerform(Level level, LivingEntity shooter, InteractionHand hand, ItemStack weapon, float velocity, float inaccuracy, @Nullable LivingEntity target, boolean isConsume) {
        if (!(level instanceof ServerLevel) || !(shooter instanceof ServerPlayer player)) {
            return false;
        }
        return ServerProjectileFireService.fire(
                player, hand, ProjectileFireTrigger.ATTACK_PRESSED)
                == ProjectileFireResult.SUCCESS;
    }

    protected boolean shootingPerformContinuousShooting(Player player, ItemStack itemStack, int countCount, DelayTaskHolder delayTaskHolder, InteractionHand hand, Level level, float shootingPower) {
        // 新实现由首发动作保存冻结快照并安排受控 continuation；旧入口仅保留二进制兼容。
        return false;
    }

    /**
     * 从当前不可变弹仓声明一次攻击键触发的 RANGED burst。
     */
    @Override
    public @Nullable ProjectileFireAction createProjectileFireAction(ProjectileFireContext context) {
        if (context.trigger() != ProjectileFireTrigger.ATTACK_PRESSED
                && context.trigger() != ProjectileFireTrigger.VANILLA_CROSSBOW_RELEASE) {
            return null;
        }
        int requestedBurst = getBurstCount(context.player(), context.hand());
        RepeaterShotPlan plan = createShotPlan(context.weapon(), requestedBurst);
        if (plan == null) {
            return null;
        }

        int punch = EnchantmentHelper.getItemEnchantmentLevel(
                Enchantments.PUNCH_ARROWS, context.weapon());
        int durabilityUse = getDurabilityUse(plan.ammunition());
        RepeaterContentsProjectileCost cost = new RepeaterContentsProjectileCost(
                plan.expectedContents(), plan.remainingContents(), durabilityUse);
        AtomicReference<ProjectileCombatSnapshot> frozenSnapshot = new AtomicReference<>();
        return ProjectileFireAction.builder(
                        ProjectileDamageChannel.RANGED,
                        cost,
                        (fireContext, snapshot) -> {
                            frozenSnapshot.set(snapshot);
                            return createRepeaterLaunches(
                                    fireContext.player(),
                                    fireContext.hand(),
                                    snapshot.weapon(),
                                    plan.ammunition(),
                                    snapshot,
                                    null);
                        })
                .baseDamage(baseDamage)
                .baseVelocity(baseArrowSpeed)
                .baseKnockback(baseKnockback + punch)
                .inherentCritical(true)
                .triggers(
                        ProjectileFireTrigger.ATTACK_PRESSED,
                        ProjectileFireTrigger.VANILLA_CROSSBOW_RELEASE)
                .cooldownTicks(Math.max(0, baseShootInterval))
                .validator(fireContext -> !isCharged(fireContext.weapon())
                        ? false
                        : fireContext.matchesCurrentWeapon())
                .successAction(fireContext -> {
                    ProjectileCombatSnapshot snapshot = frozenSnapshot.get();
                    if (snapshot == null) {
                        throw new IllegalStateException("Repeater transaction completed without a frozen snapshot");
                    }
                    afterSuccessfulShot(fireContext.player(), fireContext.hand());
                    scheduleBurst(fireContext.player(), fireContext.hand(), plan, snapshot);
                })
                .build();
    }

    /**
     * 从弹仓头部拆出当前批次的一枚弹药，并保留尚未执行的 burst 次数。
     */
    private @Nullable RepeaterShotPlan createShotPlan(ItemStack weapon, int requestedBurst) {
        RepeaterContents contents = weapon.getOrDefault(
                ModDataComponentTypes.REPEATER_CONTENTS.get(), RepeaterContents.EMPTY);
        if (contents.isEmpty() || requestedBurst < 1) {
            return null;
        }

        ItemStack ammunition = ItemStack.EMPTY;
        List<ItemStack> remaining = new ArrayList<>();
        for (ItemStack stored : contents.nonEmptyStream().toList()) {
            if (ammunition.isEmpty()) {
                ammunition = stored.copyWithCount(1);
                if (stored.getCount() > 1) {
                    remaining.add(stored.copyWithCount(stored.getCount() - 1));
                }
            } else {
                remaining.add(stored.copy());
            }
        }
        if (ammunition.isEmpty()) {
            return null;
        }
        return new RepeaterShotPlan(
                contents,
                RepeaterContents.fromItems(remaining, contents.getMaxItemCapacity()),
                ammunition,
                requestedBurst - 1);
    }

    /**
     * 创建一次 burst tick 的并发箭矢；不播放声音、不改弹仓、不写世界。
     */
    private List<ProjectileLaunch> createRepeaterLaunches(
            ServerPlayer shooter,
            InteractionHand hand,
            ItemStack frozenWeapon,
            ItemStack ammunition,
            ProjectileCombatSnapshot snapshot,
            @Nullable LivingEntity target
    ) {
        int count = Math.max(1, getConcurrentCount(shooter, hand));
        float spread = getConcurrentAngle(shooter, hand);
        float interval = getConcurrentInterval(shooter, hand);
        float center = (count - 1) * 0.5F;
        boolean firework = ammunition.is(Items.FIREWORK_ROCKET);
        float velocityMultiplier = firework ? 0.507F : 1.0F;
        List<ProjectileLaunch> launches = new ArrayList<>(count);
        for (int index = 0; index < count; index++) {
            float relative = index - center;
            Projectile projectile = createRepeaterProjectile(
                    shooter.serverLevel(), shooter, frozenWeapon, ammunition, firework);
            shootProjectile(
                    shooter,
                    projectile,
                    index,
                    snapshot.resolvedVelocity() * velocityMultiplier,
                    1.0F + modifyArrowBuilder.inaccuracy,
                    relative * spread,
                    target);
            if (interval != 0.0F) {
                BaseTerraBowItem.transformAndApplyOffsetToProjectile(
                        projectile, new Vec3(0.0, 0.0, relative * interval));
            }
            BaseTerraBowItem.processArrowBaseEffects(
                    shooter, hand, frozenWeapon, projectile, index, count);
            if (projectile instanceof IAbstractArrow arrow) {
                arrow.confluence$setDamageNotAffectedBySpeedBonus(true);
                if (index > 0) {
                    arrow.confluence$setDisappearingOnGround(true);
                }
            }
            launches.add(new ProjectileLaunch(
                    projectile,
                    projectile.position(),
                    projectile.getDeltaMovement(),
                    velocityMultiplier));
        }
        return List.copyOf(launches);
    }

    /**
     * 创建原版箭、泰拉箭或烟花，并在事务安装快照前完成纯实体配置。
     */
    private Projectile createRepeaterProjectile(
            ServerLevel level,
            ServerPlayer shooter,
            ItemStack weapon,
            ItemStack ammunition,
            boolean firework
    ) {
        if (firework) {
            return new FireworkRocketEntity(
                    level,
                    ammunition,
                    shooter,
                    shooter.getX(),
                    shooter.getEyeY() - 0.15,
                    shooter.getZ(),
                    true);
        }
        IPortArrowItemExtension arrowItem = ammunition.getItem() instanceof IPortArrowItemExtension extension
                ? extension
                : (IPortArrowItemExtension) Items.ARROW;
        AbstractArrow arrow = arrowItem.createArrow(level, ammunition, shooter, weapon);
        arrow.setBaseDamage(baseDamage);
        arrow.setCritArrow(false);
        int punch = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, weapon);
        if (punch > 0) {
            arrow.setKnockback(punch);
        }
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, weapon) > 0) {
            arrow.setSecondsOnFire(100);
        }
        if (arrow instanceof BaseArrowEntity terraArrow) {
            modifyArrowEntity(terraArrow);
        } else {
            int power = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, weapon);
            if (power > 0) {
                arrow.setBaseDamage(arrow.getBaseDamage() + power * 0.5D + 0.5D);
            }
        }
        return arrow;
    }

    /**
     * 在首批成功后按每 tick 一发继续 burst，所有子批次复用同一冻结快照。
     */
    private void scheduleBurst(
            ServerPlayer player,
            InteractionHand hand,
            RepeaterShotPlan plan,
            ProjectileCombatSnapshot snapshot
    ) {
        if (plan.remainingBurstShots() <= 0) {
            return;
        }
        DelayTaskHolder holder = DelayTaskHolder.of((IPortAttachmentHolder) player);
        if (holder.containsTask(hand, REPEATER_CONTINUOUS_SHOOTING)) {
            return;
        }
        holder.addTask(hand, REPEATER_CONTINUOUS_SHOOTING, DelayTaskHolder.createTaskBilder()
                .repeatCount(plan.remainingBurstShots())
                .removedTick(1)
                .resultRun((tick, maxTick, task) -> {
                    ItemStack currentWeapon = player.getItemInHand(hand);
                    RepeaterShotPlan nextPlan = createShotPlan(currentWeapon, 1);
                    if (nextPlan == null) {
                        task.remove();
                        return 0;
                    }
                    try {
                        List<ProjectileLaunch> launches = createRepeaterLaunches(
                                player,
                                hand,
                                snapshot.weapon(),
                                nextPlan.ammunition(),
                                snapshot,
                                null);
                        RepeaterContentsProjectileCost cost = new RepeaterContentsProjectileCost(
                                nextPlan.expectedContents(),
                                nextPlan.remainingContents(),
                                getDurabilityUse(nextPlan.ammunition()));
                        ProjectileFireResult result = ServerProjectileFireService.continueBurst(
                                player, hand, snapshot, launches, cost);
                        if (result != ProjectileFireResult.SUCCESS) {
                            task.remove();
                            return 0;
                        }
                        afterSuccessfulShot(player, hand);
                    } catch (RuntimeException exception) {
                        Confluence.LOGGER.error("Repeater burst continuation failed", exception);
                        task.remove();
                    }
                    return 0;
                }).build());
    }

    /**
     * 只在对应批次真正进入世界后运行声音、动画包、进度和统计。
     */
    private void afterSuccessfulShot(ServerPlayer player, InteractionHand hand) {
        player.level().playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.CROSSBOW_SHOOT,
                player.getSoundSource(),
                1.0F,
                getShotPitch(player.getRandom(), 0));
        RepeaterShootingPayloadS2C.sendToClient(player);
        ItemStack weapon = player.getItemInHand(hand);
        CriteriaTriggers.SHOT_CROSSBOW.trigger(player, weapon);
        player.awardStat(Stats.ITEM_USED.get(weapon.getItem()));
    }

    /**
     * 不可变 burst 内容；访问弹药时始终返回防御性副本。
     */
    private record RepeaterShotPlan(
            RepeaterContents expectedContents,
            RepeaterContents remainingContents,
            ItemStack ammunition,
            int remainingBurstShots
    ) {
        private RepeaterShotPlan {
            if (expectedContents == null || remainingContents == null || ammunition == null
                    || ammunition.isEmpty()) {
                throw new IllegalArgumentException("Repeater shot plan must contain valid ammunition");
            }
            if (remainingBurstShots < 0) {
                throw new IllegalArgumentException("Repeater remaining burst count must not be negative");
            }
            ammunition = ammunition.copyWithCount(1);
        }

        @Override
        public ItemStack ammunition() {
            return ammunition.copy();
        }
    }

    @Override
    public void shoot(ServerLevel level, LivingEntity shooter, InteractionHand hand, ItemStack weapon, List<ItemStack> projectileItems, float velocity, float inaccuracy, boolean isCrit, @Nullable LivingEntity target) {
        throw new UnsupportedOperationException(
                "Legacy repeater shooting bridge is disabled; use the projectile action service");
    }

    @Override
    public void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float velocity, float inaccuracy, float angle, @Nullable LivingEntity target) {
        Vector3f vector3f;
        if (target != null) {
            double d0 = target.getX() - shooter.getX();
            double d1 = target.getZ() - shooter.getZ();
            double d2 = Math.sqrt(d0 * d0 + d1 * d1);
            double d3 = target.getY(0.3333333333333333) - projectile.getY() + d2 * 0.2F;
            vector3f = getProjectileShotVector(shooter, new Vec3(d0, d3, d1), angle);
        } else {
            Vec3 vec3 = shooter.getUpVector(1.0F);
            Quaternionf quaternionf = new Quaternionf().setAngleAxis(angle * Mth.DEG_TO_RAD, vec3.x, vec3.y, vec3.z);
            Vec3 vec31 = shooter.getViewVector(1.0F);
            vector3f = vec31.toVector3f().rotate(quaternionf);
        }

        projectile.shoot(vector3f.x(), vector3f.y(), vector3f.z(), velocity, inaccuracy);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        RepeaterContents contents = itemstack.getOrDefault(
                ModDataComponentTypes.REPEATER_CONTENTS.get(), RepeaterContents.EMPTY);
        if (!contents.isFull() && !player.getProjectile(itemstack).isEmpty()) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(itemstack);
        }
        return InteractionResultHolder.pass(itemstack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft) {
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        return stack;
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {
        int i = this.getUseDuration(stack, entity) - count;
        float f = getPowerForTime(i, stack, entity);
        if (f < 1.0F) {
            return;
        }
        completeLoading(entity, stack);
    }

    /**
     * 把本轮装填结果提交到当前连弩。
     *
     * <p>主动松手和达到完整装填时间会共用此入口。完整装填 tick 必须先提交弹仓再结束使用，
     * 不能只依赖不同加载器版本对 {@code stopUsingItem()} 的回调细节，否则服务端可能已经
     * 停止拉弩却没有写入弹药。</p>
     */
    private boolean completeLoading(LivingEntity entity, ItemStack stack) {
        if (!tryLoadProjectiles(entity, stack)) {
            return false;
        }
        Level level = entity.level();
        if (level.isClientSide) {
            entity.playSound(ModSoundEvents.BOW_COOLDOWN_RECOVERY.get());
        }
        return true;
    }

    protected boolean tryLoadProjectiles(LivingEntity shooter, ItemStack weapon) {
        if (shooter.level().isClientSide) {
            return true;
        }
        ItemStack ammunition = shooter.getProjectile(weapon);
        if (ammunition.isEmpty() || !getAllSupportedProjectiles(weapon).test(ammunition)) {
            return false;
        }
        RepeaterContents contents = weapon.getOrDefault(
                ModDataComponentTypes.REPEATER_CONTENTS.get(), RepeaterContents.EMPTY);
        int available = contents.getMaxItemCapacity() - contents.getItemsTotalCount();
        if (available <= 0) {
            return false;
        }
        boolean creative = shooter instanceof Player player && player.isCreative();
        int inserted = insertIntoContents(
                weapon,
                ammunition,
                creative ? available : Math.min(available, ammunition.getCount()));
        if (inserted <= 0) {
            return false;
        }
        if (!creative) {
            ammunition.shrink(inserted);
        }
        return true;
    }

    /**
     * 向不可变弹仓插入同种弹药，返回实际插入数。
     */
    private int insertIntoContents(ItemStack weapon, ItemStack ammunition, int requested) {
        if (requested <= 0 || ammunition.isEmpty()
                || !getAllSupportedProjectiles(weapon).test(ammunition)) {
            return 0;
        }
        RepeaterContents contents = weapon.getOrDefault(
                ModDataComponentTypes.REPEATER_CONTENTS.get(), RepeaterContents.EMPTY);
        int inserted = Math.min(
                requested,
                contents.getMaxItemCapacity() - contents.getItemsTotalCount());
        if (inserted <= 0) {
            return 0;
        }
        List<ItemStack> items = new ArrayList<>(contents.nonEmptyStream().toList());
        boolean merged = false;
        for (int index = 0; index < items.size(); index++) {
            ItemStack stored = items.get(index);
            if (ItemStack.isSameItemSameTags(stored, ammunition)) {
                stored.grow(inserted);
                items.set(index, stored);
                merged = true;
                break;
            }
        }
        if (!merged) {
            items.add(ammunition.copyWithCount(inserted));
        }
        weapon.set(
                ModDataComponentTypes.REPEATER_CONTENTS.get(),
                RepeaterContents.fromItems(items, contents.getMaxItemCapacity()));
        return inserted;
    }

    protected float getPowerForTime(int timeLeft, ItemStack stack, LivingEntity shooter) {
        float f = (float) timeLeft / (float) getReloadSpeed(shooter, stack);
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int count) {
        if (level.isClientSide) {
            return;
        }
        float progress = (float) (stack.getUseDuration(livingEntity) - count)
                / (float) getReloadSpeed(livingEntity, stack);
        if (progress >= 1.0F) {
            completeLoading(livingEntity, stack);
            livingEntity.stopUsingItem();
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return getReloadSpeed(entity, stack) + 3;
    }

    @Override
    public Predicate<ItemStack> getSupportedHeldProjectiles(ItemStack stack) {
        return ammo -> ammunitionRestrictions.test(ammo, stack);
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles(ItemStack stack) {
        return ammo -> ammunitionRestrictions.test(ammo, stack);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return ModUtils.supportsEnchantment(stack, enchantment);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return ItemStack.isSameItem(oldStack, newStack);
    }

    public void modifyArrowEntity(BaseArrowEntity entity) {
        modifyArrowBuilder.applyModifiers(entity);
    }

    @Override
    public void appendHoverText(ItemStack weapon, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(tooltip(ARROW_CAPACITY_TEXT).append(getTotalSize(weapon).getItemsTotalCount() + "/" + baseCapacity).withStyle(ChatFormatting.DARK_GRAY));
        tooltipComponents.add(tooltip(ATTACK_SPEED_TEXT).append(String.valueOf(baseArrowSpeed)).withStyle(ChatFormatting.DARK_GRAY));
        tooltipComponents.add(tooltip(KNOCKBACK_TEXT).append(String.valueOf(baseKnockback)).withStyle(ChatFormatting.DARK_GRAY));
        if (!IRandomCount.is(baseBurstCount, 1)) {
            tooltipComponents.add(tooltip(TORRENT_COUNT_TEXT).append(IRandomCount.getString(baseBurstCount)).withStyle(ChatFormatting.DARK_GRAY));
        }
        if (!IRandomCount.is(baseConcurrentCount, 1)) {
            tooltipComponents.add(tooltip(CONCURRENCY_COUNT_TEXT).append(IRandomCount.getString(baseConcurrentCount)).withStyle(ChatFormatting.DARK_GRAY));
        }
        tooltipComponents.add(tooltip(FIRING_INTERVAL_TEXT).append(String.valueOf(baseShootInterval / 20f)).withStyle(ChatFormatting.DARK_GRAY));
        tooltipComponents.add(tooltip(RELOAD_SPEED_TEXT).append(String.valueOf(baseReloadSpeed / 20f)).withStyle(ChatFormatting.DARK_GRAY));
        tooltipComponents.addAll(TOOLTIP);
    }

    private static RepeaterContents getTotalSize(ItemStack weapon) {
        return weapon.getComponents().getOrDefault(ModDataComponentTypes.REPEATER_CONTENTS.get(), RepeaterContents.EMPTY);
    }

    private static MutableComponent tooltip(String text) {
        return Component.translatable(text).append(": ");
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.ofNullable(stack.get(ModDataComponentTypes.REPEATER_CONTENTS)).map(RepeaterComponent::new);
    }

    public float getBaseKnockback() {
        return baseKnockback;
    }

    public static class Builder {
        public static final AmmunitionRestrictions DEFAULT_AMMUNITION_RESTRICTIONS =
                (ammunitionStack, weaponStack) -> ammunitionStack.is(ItemTags.ARROWS) || ammunitionStack.is(Items.FIREWORK_ROCKET);
        public static final AmmunitionRestrictions DEFAULT_AMMUNITION_RESTRICTIONS_ARROWS =
                (ammunitionStack, weaponStack) -> ammunitionStack.is(ItemTags.ARROWS) || ammunitionStack.is(Items.FIREWORK_ROCKET);
        public static final AmmunitionRestrictions DEFAULT_AMMUNITION_RESTRICTIONS_FIREWORK_ROCKET =
                (ammunitionStack, weaponStack) -> ammunitionStack.is(Items.FIREWORK_ROCKET);

        /**
         * 装弹速度
         */
        private int reloadSpeed = Mth.floor(1.25F * 20);
        /**
         * 射击间隔
         */
        private int shootInterval = 5;
        /**
         * 容量
         */
        private int capacity = 5;
        /**
         * 基础箭矢速度
         */
        private float arrowSpeed = 3.15F;
        /**
         * 击退
         */
        private float knockback = 0;
        /**
         * 连发个数（每次射击会射出多少支箭，每个间隔一帧）
         */
        private IRandomCount burstCount = IRandomCount.DEFAULT;
        /**
         * 并发个数（同时射出多少支箭，有一定的散射角度）
         */
        private IRandomCount concurrentCount = IRandomCount.DEFAULT;
        /**
         * 并发角度（并发个数时，每个箭的偏移角度）
         */
        private IRandomCount concurrentAngle = IRandomCount.DEFAULT_EMPTY;
        /**
         * 并发间隔（并发个数时，每个箭的间隔）
         */
        private IRandomCount concurrentInterval = IRandomCount.DEFAULT_EMPTY;
        /**
         * 弹药限制
         */
        private AmmunitionRestrictions ammunitionRestrictions = DEFAULT_AMMUNITION_RESTRICTIONS;

        public Builder reloadTick(int reloadSpeed) {
            this.reloadSpeed = reloadSpeed;
            return this;
        }

        public Builder shootInterval(int shootInterval) {
            this.shootInterval = shootInterval;
            return this;
        }

        public Builder capacity(int capacity) {
            this.capacity = capacity;
            return this;
        }

        public Builder arrowSpeed(float arrowSpeed) {
            this.arrowSpeed = arrowSpeed;
            return this;
        }

        public Builder burstCount(IRandomCount burstCount) {
            this.burstCount = burstCount;
            return this;
        }

        public Builder burstCount(int burstCount) {
            this.burstCount = IRandomCount.create(burstCount);
            return this;
        }

        public Builder concurrentCount(IRandomCount concurrentCount) {
            this.concurrentCount = concurrentCount;
            return this;
        }

        public Builder concurrentCount(int concurrentCount) {
            this.concurrentCount = IRandomCount.create(concurrentCount);
            return this;
        }

        public Builder concurrentAngle(IRandomCount concurrentAngle) {
            this.concurrentAngle = concurrentAngle;
            return this;
        }

        public Builder concurrentAngle(float concurrentAngle) {
            this.concurrentAngle = IRandomCount.create(concurrentAngle);
            return this;
        }

        public Builder concurrentInterval(IRandomCount concurrentInterval) {
            this.concurrentInterval = concurrentInterval;
            return this;
        }

        public Builder concurrentInterval(float concurrentInterval) {
            this.concurrentInterval = IRandomCount.create(concurrentInterval);
            return this;
        }

        public Builder ammunitionRestrictions(AmmunitionRestrictions ammunitionRestrictions) {
            this.ammunitionRestrictions = ammunitionRestrictions;
            return this;
        }

        public Builder knockback(float knockback) {
            this.knockback = knockback;
            return this;
        }

    }

    @FunctionalInterface
    public interface AmmunitionRestrictions {
        boolean test(ItemStack ammunitionStack, ItemStack weaponStack);
    }

    public static class ModifyArrowBuilder {
        public List<UnaryOperator<Properties>> modifyProperties = new java.util.ArrayList<>();
        public List<Consumer<BaseArrowEntity>> modifyArrowBuilder = new java.util.ArrayList<>();
        public int multiShoot = 1;
        public java.util.function.Predicate<net.minecraft.world.item.ItemStack> canMultiShoot = ammo -> false;
        public float inaccuracy;

        public void applyModifiers(BaseArrowEntity modifyArrow) {
            modifyArrowBuilder.forEach(m -> m.accept(modifyArrow));
        }

        public ModifyArrowBuilder setUnBreakable() {
            this.modifyProperties.add(IPortItemPropertiesExtension::unbreakable);
            return this;
        }

        public ModifyArrowBuilder setRarity(ModRarity rarity) {
            this.modifyProperties.add(p -> p.component(ConfluenceMagicLib.MOD_RARITY, rarity));
            return this;
        }

        public ModifyArrowBuilder setInaccuracy(float inaccuracy) {
            this.inaccuracy = inaccuracy;
            return this;
        }

        public Properties buildProperties(Properties properties) {
            for (UnaryOperator<Properties> f : modifyProperties) {
                f.apply(properties);
            }
            return properties;
        }
    }
}
