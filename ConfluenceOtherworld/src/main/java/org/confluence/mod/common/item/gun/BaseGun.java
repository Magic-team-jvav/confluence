package org.confluence.mod.common.item.gun;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.projectile.*;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.api.client.animation.HandAnimationAction;
import org.confluence.mod.api.client.animation.HandAnimationApi;
import org.confluence.mod.api.client.animation.HandAnimationChannel;
import org.confluence.mod.api.client.animation.HandAnimationProfile;
import org.confluence.mod.api.event.GunEvent;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.combat.gun.AmmoStats;
import org.confluence.mod.common.combat.gun.Ballistics;
import org.confluence.mod.common.combat.gun.BallisticsResolver;
import org.confluence.mod.common.combat.gun.GunStats;
import org.confluence.mod.common.component.BulletPropertyComponent;
import org.confluence.mod.common.component.GunPropertyComponent;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;
import org.confluence.mod.common.entity.projectile.CustomBulletEntity;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.gun.GunSounds;
import org.confluence.mod.common.item.BaseBullet;
import org.confluence.mod.common.item.gun.definition.FireMode;
import org.confluence.mod.common.item.gun.definition.GunDefinition;
import org.confluence.mod.common.item.gun.definition.GunProjectilePattern;
import org.confluence.mod.util.ModGunUtils;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.event.PortEventHandler;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationProcessor;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/// 枪械的统一服务端动作实现。
///
/// <p>物品实例只保存不可变声明，不缓存某次射击创建的实体。每次请求都会重新选择服务端弹药、
/// 发布枪械数据事件、准备可回滚成本，并创建本次射击独有的弹幕布局。属性快照、冷却、实体加入世界、
/// 动画和声音由统一发射服务按固定顺序处理。</p>
public class BaseGun extends Item implements GeoItem, ProjectileWeaponAction {
    private static final int USE_DURATION = 72_000;

    /// 创建一枚尚未加入世界的具体子弹实体。
    @FunctionalInterface
    public interface BulletEntityFactory {
        BaseBulletEntity create(ServerPlayer player, ItemStack bullet);
    }

    /// 一次请求在事件处理后冻结的本体枪械数据。
    protected record ShotData(
            ItemStack ammo,
            float damage,
            float knockback,
            float velocity,
            int penetrate,
            float inaccuracy,
            float criticalChance
    ) {
        protected ShotData {
            ammo = Objects.requireNonNull(ammo, "Gun ammo snapshot must not be null").copyWithCount(1);
            requireFinite(damage, 0.0F, Float.MAX_VALUE, "Gun shot damage");
            requireFinite(knockback, 0.0F, Float.MAX_VALUE, "Gun shot knockback");
            requireFinite(velocity, Math.nextUp(0.0F), Float.MAX_VALUE, "Gun shot velocity");
            requireFinite(inaccuracy, 0.0F, 180.0F, "Gun shot inaccuracy");
            requireFinite(criticalChance, 0.0F, 1.0F, "Gun shot critical chance");
            if (penetrate < -1) {
                throw new IllegalArgumentException("Gun shot penetration must be -1 or non-negative");
            }
        }

        @Override
        public ItemStack ammo() {
            return ammo.copy();
        }
    }

    /// 一次枪械动作的资源成本及成功后的附加操作。
    ///
    /// <p>成本对象负责准备、提交和失败回滚；成功回调只在整批弹幕全部加入世界后运行，适合处理
    /// 魔力修补、自动魔力药水负面效果等不应在生成失败时发生的副作用。</p>
    protected record ShotCost(ProjectileCost cost, Runnable successAction) {
        protected ShotCost {
            Objects.requireNonNull(cost, "Gun projectile cost must not be null");
            Objects.requireNonNull(successAction, "Gun projectile cost success action must not be null");
        }
    }

    protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected final GunPropertyComponent component;
    protected final float inaccuracy;
    protected final float gravity;
    protected final int minBullets;
    protected final int maxBullets;
    protected final int manaCost;
    protected final FireMode fireMode;
    protected final @Nullable BulletEntityFactory bulletEntityFactory;
    private final HandAnimationProfile animationProfile;

    public BaseGun(Builder builder) {
        super(builder.buildProperties());
        this.component = new GunPropertyComponent(
                builder.cooldown, builder.damage, builder.velocity, builder.knockback,
                builder.critical, builder.penetrate, builder.rarity);
        this.inaccuracy = builder.inaccuracy;
        this.gravity = builder.gravity;
        this.minBullets = builder.minBullets;
        this.maxBullets = builder.maxBullets;
        this.manaCost = builder.manaCost;
        this.fireMode = builder.fireMode;
        this.bulletEntityFactory = builder.bulletEntityFactory;
        this.animationProfile = builder.animationProfile;
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    /// 从当前服务端武器、弹药和事件结果声明一次完整枪械动作。
    @Override
    public @Nullable ProjectileFireAction createProjectileFireAction(ProjectileFireContext context) {
        GunEvent.UseGunEvent useEvent = PortEventHandler.postEventWithReturn(
                new GunEvent.UseGunEvent(context.player(), this, getCooldown()));
        if (useEvent.isCanceled()) {
            return null;
        }
        if (useEvent.getCooldowns() < 0) {
            throw new IllegalArgumentException("Gun cooldown must be non-negative");
        }

        ItemStack selectedAmmo = selectAmmo(context);
        ShotData shot = resolveShotData(context, selectedAmmo);
        ShotCost shotCost = createShotCost(context, selectedAmmo);
        return ProjectileFireAction.builder(
                        damageChannel(),
                        shotCost.cost(),
                        (fireContext, snapshot) -> createLaunches(fireContext, snapshot, shot))
                .baseDamage(shot.damage())
                .baseVelocity(shot.velocity())
                .baseKnockback(shot.knockback())
                .criticalChanceBonus(shot.criticalChance())
                .triggers(
                        ProjectileFireTrigger.ATTACK_PRESSED,
                        ProjectileFireTrigger.USE_PRESSED,
                        ProjectileFireTrigger.CONTINUOUS_USE_TICK)
                .cooldownTicks(useEvent.getCooldowns())
                .successAction(fireContext -> {
                    shotCost.successAction().run();
                    onSuccessfulShot(fireContext, shot);
                })
                .build();
    }

    /// 在原版方块与实体交互均未消耗右键后开始枪械动作。
    ///
    /// <p>该入口由原版物品使用流程调用，因此箱子、门和工作台等交互天然优先。客户端只进入持续使用姿态；
    /// 是否存在弹药、能否扣除资源以及是否生成弹幕，全部由服务端统一发射服务重新判断。</p>
    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            InteractionHand hand
    ) {
        ItemStack weapon = player.getItemInHand(hand);
        player.startUsingItem(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            ServerProjectileFireService.fire(
                    serverPlayer,
                    hand,
                    ProjectileFireTrigger.USE_PRESSED);
        }
        return InteractionResultHolder.consume(weapon);
    }

    /// 自动枪在玩家持续按住右键时，按服务端冷却尝试后续射击。
    ///
    /// <p>标记为手动枪的物品只响应首次按下，不会因持续使用而重复发射。冷却、资源与同 tick 去重
    /// 仍由统一发射服务负责，本方法不缓存任何玩家状态。</p>
    @Override
    public void onUseTick(
            Level level,
            LivingEntity living,
            ItemStack weapon,
            int remainingUseDuration
    ) {
        if (!level.isClientSide
                && living instanceof ServerPlayer player
                && (CommonConfigs.AUTO_FIRE_ALL_GUNS.get() || isAutomatic(weapon))) {
            ServerProjectileFireService.fire(
                    player,
                    player.getUsedItemHand(),
                    ProjectileFireTrigger.CONTINUOUS_USE_TICK);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return USE_DURATION;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    /// 普通枪械使用远程伤害通道；魔力枪覆写为魔法伤害通道。
    protected ProjectileDamageChannel damageChannel() {
        return ProjectileDamageChannel.RANGED;
    }

    /// 服务端选择本次实际弹药；空栈会由成本准备阶段明确拒绝。
    protected ItemStack selectAmmo(ProjectileFireContext context) {
        ItemStack ammo = ModGunUtils.getAmmo(context.player(), context.weapon());
        GunEvent.GunFireEvent event = PortEventHandler.postEventWithReturn(
                new GunEvent.GunFireEvent(context.player(), this, ammo, !ammo.isEmpty()));
        if (!event.isFire()) {
            return ItemStack.EMPTY;
        }
        return Objects.requireNonNull(event.getAmmo(), "Gun fire event ammo must not be null");
    }

    /// 合并枪械与弹药数据，再由现有枪械事件扩展点给出最终动作数值。
    protected ShotData resolveShotData(ProjectileFireContext context, ItemStack ammo) {
        GunPropertyComponent gun = context.weapon().get(ModDataComponentTypes.GUN_PROPERTY);
        if (gun == null) {
            gun = component;
        }
        BulletPropertyComponent bullet = ammo.get(ModDataComponentTypes.BULLET_PROPERTY);
        if (bullet == null && ammo.getItem() instanceof BaseBullet baseBullet) {
            bullet = baseBullet.getDefinition().component();
        }
        if (bullet == null) {
            bullet = BulletPropertyComponent.EMPTY;
        }
        Ballistics data = BallisticsResolver.resolve(
                new GunStats(
                        gun.damage(), gun.velocity(), gun.knockback(),
                        gun.critical(), gun.penetrate(), inaccuracy),
                new AmmoStats(
                        bullet.damage(), bullet.velocity(), bullet.velocityMultiplier(),
                        bullet.knockback(), bullet.penetrate()));
        GunEvent.AmmoDataEvent event = PortEventHandler.postEventWithReturn(new GunEvent.AmmoDataEvent(
                context.player(), this, context.weapon(), data.damage(), data.critical(),
                data.knockback(), data.velocity(), data.penetrate(), data.inaccuracy()));
        float criticalChance = event.getCritical();
        requireFinite(criticalChance, 0.0F, 1.0F, "Gun shot critical chance");
        return new ShotData(
                ammo.isEmpty() ? defaultVisualAmmo() : ammo,
                event.getDamage(), event.getKnockback(), event.getVelocity(), event.getPenetrate(),
                event.getInaccuracy(), criticalChance);
    }

    /// 普通枪械准备精确一份弹药成本；创造、无限与取消消耗返回空成本。
    protected ProjectileCost createProjectileCost(ProjectileFireContext context, ItemStack selectedAmmo) {
        if (selectedAmmo.isEmpty()) {
            return ignored -> Optional.empty();
        }
        return ignored -> prepareAmmoCost(context.player(), context.weapon(), selectedAmmo);
    }

    /// 创建本次请求独占的成本计划。
    ///
    /// <p>普通枪械只有弹药成本；魔力枪可覆写本方法，返回同时带有成功后副作用的请求局部计划。
    /// 该对象不得缓存到物品单例字段。</p>
    protected ShotCost createShotCost(ProjectileFireContext context, ItemStack selectedAmmo) {
        return new ShotCost(createProjectileCost(context, selectedAmmo), () -> {});
    }

    private Optional<PreparedProjectileCost> prepareAmmoCost(
            ServerPlayer player,
            ItemStack gun,
            ItemStack selectedAmmo
    ) {
        BulletPropertyComponent bullet = selectedAmmo.get(ModDataComponentTypes.BULLET_PROPERTY);
        boolean infinite = bullet != null && bullet.infinity();
        GunEvent.ShrinkBulletEvent event = PortEventHandler.postEventWithReturn(
                new GunEvent.ShrinkBulletEvent(player, this, gun, selectedAmmo, infinite));
        ItemStack chargedStack = Objects.requireNonNull(
                event.getBulletStack(), "Gun ammo cost stack must not be null");
        if (player.isCreative() || event.isInfinity() || event.isCanceled()) {
            return Optional.of(PreparedProjectileCost.none());
        }
        int amount = event.getShrink();
        if (amount < 0) {
            throw new IllegalArgumentException("Gun ammo shrink amount must not be negative");
        }
        if (amount == 0) {
            return Optional.of(PreparedProjectileCost.none());
        }
        if (chargedStack.isEmpty() || chargedStack.getCount() < amount) {
            return Optional.empty();
        }
        ItemStack expected = chargedStack.copy();
        boolean[] consumed = {false};
        return Optional.of(PreparedProjectileCost.once(() -> {
            if (!ItemStack.isSameItemSameTags(chargedStack, expected)
                    || chargedStack.getCount() < amount) {
                throw new IllegalStateException("Prepared gun ammo changed before commit");
            }
            chargedStack.shrink(amount);
            consumed[0] = true;
        }, () -> {
            if (consumed[0]) {
                chargedStack.grow(amount);
                consumed[0] = false;
            }
        }));
    }

    /// 创建只属于本次发射的一组子弹布局。
    protected List<ProjectileLaunch> createLaunches(
            ProjectileFireContext context,
            ProjectileCombatSnapshot snapshot,
            ShotData shot
    ) {
        int count = maxBullets > 1
                ? context.player().getRandom().nextInt(minBullets, maxBullets + 1)
                : 1;
        List<ProjectileLaunch> launches = new ArrayList<>(count);
        for (int index = 0; index < count; index++) {
            BaseBulletEntity projectile = createBulletEntity(context.player(), shot.ammo());
            if (projectile == null) {
                throw new IllegalStateException("Gun bullet factory returned null");
            }
            projectile.colorID(getColorID());
            projectile.penetrate = shot.penetrate();
            Vec3 viewDirection = context.player().getViewVector(1.0F);
            projectile.shoot(
                    viewDirection.x,
                    viewDirection.y,
                    viewDirection.z,
                    Math.max(0.0F, snapshot.resolvedVelocity()),
                    Math.max(0.0F, shot.inaccuracy()));
            Vec3 velocity = projectile.getDeltaMovement();
            projectile.setInitialVelocity(velocity);
            Vec3 origin = projectile.position().add(viewDirection.scale(0.18D));
            projectile.setPos(origin);
            launches.add(new ProjectileLaunch(projectile, origin, velocity));
        }
        return List.copyOf(launches);
    }

    /// 创建普通、重力或注册工厂指定的子弹实体。
    protected BaseBulletEntity createBulletEntity(ServerPlayer player, ItemStack bullet) {
        if (bulletEntityFactory != null) {
            return bulletEntityFactory.create(player, bullet);
        }
        return gravity == 0.0F
                ? new BaseBulletEntity(player, bullet)
                : new CustomBulletEntity(player, gravity, bullet);
    }

    /// 只在整批实体成功加入世界后执行声音、动画和武器特有成功效果。
    protected void onSuccessfulShot(ProjectileFireContext context, ShotData shot) {
        SoundEvent sound = GunSounds.getSound(this);
        if (sound == null) {
            throw new IllegalStateException("Missing gun sound registration for " + getDescriptionId());
        }
        context.level().playSound(null, context.player().blockPosition(), sound, SoundSource.PLAYERS, 1.0F, 1.0F);
        ItemStack weapon = context.currentWeaponForCommit();
        if (weapon != null && weapon.getItem() == this) {
            fireAnimator(weapon, context.player());
        }
    }

    /// 魔力枪没有实体弹药时使用的纯表现弹药；普通枪不会借此绕过成本。
    protected ItemStack defaultVisualAmmo() {
        return org.confluence.mod.common.init.item.GunItems.DUMMY_BULLET.toStack();
    }

    public int getManaCost() {
        return manaCost;
    }

    public GunDefinition getDefinition() {
        return new GunDefinition(
                component.cooldown(),
                component.damage(),
                component.velocity(),
                component.knockback(),
                component.critical(),
                component.penetrate(),
                inaccuracy,
                component.rarity(),
                fireMode,
                projectilePattern());
    }

    public boolean isAutomatic(ItemStack stack) {
        return !stack.is(ModTags.Items.MANUAL_GUN)
                && (fireMode == FireMode.AUTOMATIC || stack.is(ModTags.Items.AUTOMATIC_GUN));
    }

    private GunProjectilePattern projectilePattern() {
        if (maxBullets > 1) {
            return GunProjectilePattern.shotgun(minBullets, maxBullets);
        }
        if (gravity > 0.0F) {
            return GunProjectilePattern.gravity(gravity);
        }
        return GunProjectilePattern.single();
    }

    public String getColorID() {
        return "";
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.confluence.ranged_damage", component.damage()).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.confluence.critical_chance", String.format("%.1f", component.critical() * 100)).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.confluence.knockback", component.knockback()).withStyle(ChatFormatting.GRAY));
    }

    public int getCooldown() {
        return component.cooldown();
    }

    public HandAnimationProfile getAnimationProfile() {
        return animationProfile;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        for (HandAnimationChannel channel : animationProfile.channels()) {
            AnimationController<BaseGun> controller = new AnimationController<>(this, channel.name(), state -> {
                if (!state.getController().isPlayingTriggeredAnimation()) {
                    channel.idle().ifPresent(idle ->
                            state.getController().setAnimation(idle.rawAnimation()));
                }
                return PlayState.CONTINUE;
            });
            channel.animations().forEach((action, clip) ->
                    controller.triggerableAnim(action.id(), channel.triggeredAnimation(action)));
            controllers.add(controller);
        }
    }

    public void fireAnimator(ItemStack stack, ServerPlayer player) {
        HandAnimationApi.stop(
                this, stack, player, animationProfile, HandAnimationAction.INSPECT);
        playAnimator(stack, player, HandAnimationAction.SHOOT);
    }

    public void pickAnimator(ItemStack stack, ServerPlayer player) {
        playAnimator(stack, player, HandAnimationAction.DRAW);
    }

    public void reloadAnimator(ItemStack stack, ServerPlayer player) {
        playAnimator(stack, player, HandAnimationAction.RELOAD);
    }

    public void putAwayAnimator(ItemStack stack, ServerPlayer player) {
        playAnimator(stack, player, HandAnimationAction.PUT_AWAY);
    }

    public void inspectAnimator(ItemStack stack, ServerPlayer player) {
        playAnimator(stack, player, HandAnimationAction.INSPECT);
    }

    public boolean isShootAnimationName(@Nullable String animationName) {
        return animationProfile.isAnimation(HandAnimationAction.SHOOT, animationName);
    }

    public boolean isShootAnimationPlaying(long instanceId) {
        return isAnimationPlaying(instanceId, HandAnimationAction.SHOOT);
    }

    public boolean isCameraAnimationPlaying(long instanceId) {
        return isAnimationPlaying(instanceId, HandAnimationAction.DRAW)
                || isAnimationPlaying(instanceId, HandAnimationAction.PUT_AWAY)
                || isAnimationPlaying(instanceId, HandAnimationAction.INSPECT)
                || isAnimationPlaying(instanceId, HandAnimationAction.SHOOT);
    }

    /// 查询当前物品栈是否仍在播放收枪动画。
    ///
    /// <p>该方法供第一人称手持修正使用，使收枪补帧期间继续保持 1.21 TerraGuns 的遮挡行为。
    /// 动作和通道声明由公共动画档案提供，渲染层只负责查询当前播放状态。</p>
    public boolean isPutAwayAnimationPlaying(ItemStack stack) {
        return isAnimationPlaying(GeoItem.getId(stack), HandAnimationAction.PUT_AWAY);
    }

    public boolean isAnimationPlaying(long instanceId, HandAnimationAction action) {
        return cache.getManagerForId(instanceId).getAnimationControllers().values().stream()
                .filter(AnimationController::isPlayingTriggeredAnimation)
                .map(AnimationController::getCurrentAnimation)
                .filter(Objects::nonNull)
                .map(AnimationProcessor.QueuedAnimation::animation)
                .anyMatch(animation -> animationProfile.isAnimation(action, animation.name()));
    }

    public boolean playAnimator(
            ItemStack stack,
            ServerPlayer player,
            HandAnimationAction action
    ) {
        return HandAnimationApi.play(this, stack, player, animationProfile, action);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return false;
    }

    private static float requireFinite(float value, float minimum, float maximum, String field) {
        if (!Float.isFinite(value) || value < minimum || value > maximum) {
            throw new IllegalArgumentException(
                    field + " must be finite and within [" + minimum + ", " + maximum + "]");
        }
        return value;
    }

    /// 枪械不可变声明的构建器。
    public static class Builder {
        private final int cooldown;
        private final float damage;
        private final float velocity;
        private float knockback;
        private float critical;
        private int penetrate;
        private float inaccuracy;
        private ModRarity rarity = ModRarity.WHITE;
        private float gravity;
        private int minBullets = 1;
        private int maxBullets = 1;
        private int manaCost;
        private FireMode fireMode = FireMode.MANUAL;
        private @Nullable BulletEntityFactory bulletEntityFactory;
        private HandAnimationProfile animationProfile = HandAnimationProfile.legacy();
        private Item.Properties properties = new Properties();

        public Builder(int cooldown, float damage, float velocity) {
            this.cooldown = cooldown;
            this.damage = damage;
            this.velocity = velocity;
        }

        public Builder knockback(float value) {
            this.knockback = value;
            return this;
        }

        public Builder critical(float value) {
            this.critical = value;
            return this;
        }

        public Builder penetrate(int value) {
            this.penetrate = value;
            return this;
        }

        public Builder inaccuracy(float value) {
            this.inaccuracy = value;
            return this;
        }

        public Builder rarity(ModRarity value) {
            this.rarity = value;
            return this;
        }

        public Builder gravity(float value) {
            this.gravity = value;
            return this;
        }

        public Builder fireMode(FireMode value) {
            this.fireMode = Objects.requireNonNull(value);
            return this;
        }

        public Builder automatic() {return fireMode(FireMode.AUTOMATIC);}

        public Builder manual() {return fireMode(FireMode.MANUAL);}

        public Builder bullets(int minimum, int maximum) {
            if (minimum < 1 || maximum < minimum) {
                throw new IllegalArgumentException("Gun bullet count must satisfy 1 <= min <= max");
            }
            this.minBullets = minimum;
            this.maxBullets = maximum;
            return this;
        }

        public Builder manaCost(int value) {
            this.manaCost = value;
            return this;
        }

        public Builder bulletFactory(BulletEntityFactory value) {
            this.bulletEntityFactory = value;
            return this;
        }

        public Builder animationProfile(HandAnimationProfile value) {
            this.animationProfile = Objects.requireNonNull(value);
            return this;
        }

        public Builder handgunAnimations() {return animationProfile(HandAnimationProfile.handgun()); }
        public Builder properties(Item.Properties value) {
            this.properties = Objects.requireNonNull(value);
            return this;
        }

        private Item.Properties buildProperties() {
            GunPropertyComponent value = new GunPropertyComponent(
                    cooldown, damage, velocity, knockback, critical, penetrate, rarity);
            properties.component(ModDataComponentTypes.GUN_PROPERTY, value);
            return properties.stacksTo(1);
        }

        public BaseGun build() {
            return new BaseGun(this);
        }
    }

}
