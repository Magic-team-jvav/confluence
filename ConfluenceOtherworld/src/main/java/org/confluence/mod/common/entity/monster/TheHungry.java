package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.boss.BaseBoss;
import org.confluence.mod.common.entity.boss.BossOwnerTracker;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.Optional;
import java.util.UUID;

/// 饿鬼的公共实体实现，同时服务于血肉墙从属、肉丘嘴部从属和独立生成变体。
///
/// Boss 从属使用精确 UUID 恢复主体，并围绕生成锚点活动；没有绑定记录的实体
/// 才作为独立野怪寻找玩家。这样区块反向加载不会把暂时找不到主体的从属错误转换成
/// 永久野怪，也不需要再注册一套重复的 {@code hungry} 实体。
public class TheHungry extends BaseFlyingMonster {
    @Override
    protected int contactDetectionInterval() {
        return 5;
    }

    @Override
    protected double contactAttackInflation() {
        return 0.3;
    }

    private static final int OWNER_RESOLVE_GRACE_TICKS = 100;
    private static final RawAnimation BAIT = RawAnimation.begin().thenLoop("bait");
    private static final String LEASH_X_TAG = "LeashX";
    private static final String LEASH_Y_TAG = "LeashY";
    private static final String LEASH_Z_TAG = "LeashZ";
    private static final String SUPPRESS_LOOT_TAG = "SuppressLoot";
    private static final String FREE_TAG = "Free";
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(TheHungry.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Vector3f> ANCHOR = SynchedEntityData.defineId(TheHungry.class, EntityDataSerializers.VECTOR3);

    private final BossOwnerTracker<BaseBoss> ownerTracker = new BossOwnerTracker<>(BaseBoss.class);
    private Vec3 leashPos = Vec3.ZERO;
    private Vec3 anchor = Vec3.ZERO;
    private final double minimumDistance;
    private final double maximumDistance;
    private int unresolvedOwnerTicks;
    private boolean suppressLoot;
    private boolean free;

    public TheHungry(EntityType<? extends TheHungry> type, Level level) {
        super(type, level);
        /// 1.21 的饿鬼直接更新位置，不参与方块碰撞。这里使用原版 noPhysics
        /// 表达相同能力，避免系绳扑击被墙面或血肉墙周围地形卡住。
        noPhysics = true;
        int distanceOffset = random.nextInt(7);
        minimumDistance = 8.0 + distanceOffset;
        maximumDistance = 64.0 + distanceOffset;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(OWNER_UUID, Optional.empty());
        entityData.define(ANCHOR, new Vector3f());
    }

    public void setMaster(BaseBoss master, Vec3 relativeAnchor) {
        ownerTracker.bind(this, master);
        entityData.set(OWNER_UUID, Optional.of(master.getUUID()));
        leashPos = relativeAnchor;
        setAnchor(master.position().add(relativeAnchor));
        unresolvedOwnerTicks = 0;
    }

    public @Nullable BaseBoss getMaster() {
        return ownerTracker.resolve(this);
    }

    public @Nullable UUID getMasterUUID() {
        return entityData.get(OWNER_UUID).orElse(null);
    }

    public boolean isOwnedBy(BaseBoss boss) {
        return boss.getUUID().equals(getMasterUUID());
    }

    public Vec3 getLeashPos() {
        return leashPos;
    }

    public Vec3 getAnchor() {
        return anchor;
    }

    private void setAnchor(Vec3 anchor) {
        this.anchor = anchor;
        entityData.set(ANCHOR, anchor.toVector3f());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (key == ANCHOR) anchor = new Vec3(entityData.get(ANCHOR));
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    double minimumDistance() {
        return minimumDistance;
    }

    double maximumDistance() {
        return maximumDistance;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseFlyingMonster.createFlyingAttributes()
                .add(Attributes.MAX_HEALTH, 35.0)
                .add(Attributes.ATTACK_DAMAGE, 8.0);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new HungryMovementAction(TheHungry.this);
            }
        };
    }

    /// 饿鬼资源只有持续摆动的 {@code bait} 动画。
    ///
    /// 从属状态、独立野怪状态和返回锚点阶段共用同一套身体摆动，因此控制器持续播放，
    /// 不根据水平速度停顿。这样既与 1.21 行为一致，也避免悬停时模型变成静态贴图。
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "bait", 0, state -> state.setAndContinue(BAIT)));
    }

    @Override
    public void tick() {
        if (!level().isClientSide) prepareServerTick();
        super.tick();
    }

    @Override
    public boolean hasLineOfSight(Entity entity) {
        return distanceToSqr(entity) < 32.0 * 32.0;
    }

    private void prepareServerTick() {
        BaseBoss master = getMaster();
        if (master != null && master.isAlive()) {
            unresolvedOwnerTicks = 0;
            setAnchor(free ? Vec3.ZERO : master.position().add(leashPos));
            if (free) {
                setTarget(level().getNearestPlayer(this, 40.0));
            } else if (getTarget() == null && master.getTarget() != null && master.getTarget().isAlive()) {
                setTarget(master.getTarget());
            }
            return;
        }
        if (getMasterUUID() != null) {
            setTarget(null);
            if (++unresolvedOwnerTicks > OWNER_RESOLVE_GRACE_TICKS) discard();
            return;
        }
        if (!free) {
            if (tickCount > 0 && tickCount % 60 == 0) discard();
            return;
        }
        if (tickCount > 0 && tickCount % 60 == 0) hurt(damageSources().starve(), 1.0F);
        setTarget(level().getNearestPlayer(this, 40.0));
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        BaseBoss master = getMaster();
        return master == null
                ? super.canAttack(target)
                : master.canAttack(target);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Entity attacker = source.getEntity();
        if (attacker != null && attacker.getType().is(ModTags.EntityTypes.FLESH_ALLIANCE)) {
            return false;
        }
        return super.hurt(source, amount);
    }

    /// 血肉墙上的饿鬼被击败后会脱离系绳，生成一个不再掉落战利品的自由饿鬼。
    /// 肉丘使用独立的槽位恢复规则，因此只对血肉墙注册的饿鬼类型执行该转换。
    @Override
    public void die(DamageSource source) {
        BaseBoss master = getMaster();
        Vec3 deathPosition = position();
        super.die(source);
        if (!(level() instanceof ServerLevel serverLevel) || master == null || !master.isAlive() || getType() != MonsterEntities.THE_HUNGRY.get()) {
            return;
        }
        TheHungry freeHungry = MonsterEntities.THE_HUNGRY.get().create(serverLevel);
        if (freeHungry == null) {
            return;
        }
        freeHungry.setPos(deathPosition);
        freeHungry.setDeltaMovement(getDeltaMovement());
        freeHungry.setMaster(master, master.position().scale(-1.0));
        freeHungry.setFree(true);
        freeHungry.suppressLoot = true;
        serverLevel.addFreshEntity(freeHungry);
    }

    @Override
    protected boolean shouldDropLoot() {
        return !suppressLoot && super.shouldDropLoot();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ownerTracker.save(tag);
        tag.putDouble(LEASH_X_TAG, leashPos.x);
        tag.putDouble(LEASH_Y_TAG, leashPos.y);
        tag.putDouble(LEASH_Z_TAG, leashPos.z);
        tag.putBoolean(SUPPRESS_LOOT_TAG, suppressLoot);
        tag.putBoolean(FREE_TAG, free);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        ownerTracker.load(tag);
        entityData.set(OWNER_UUID, Optional.ofNullable(ownerTracker.getOwnerUUID()));
        leashPos = new Vec3(tag.getDouble(LEASH_X_TAG), tag.getDouble(LEASH_Y_TAG), tag.getDouble(LEASH_Z_TAG));
        suppressLoot = tag.getBoolean(SUPPRESS_LOOT_TAG);
        free = tag.getBoolean(FREE_TAG);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.THE_HUNGRY_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.THE_HUNGRY_DEATH.get();
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void remove(RemovalReason reason) {
        ownerTracker.unbind(this);
        super.remove(reason);
    }

    @Override
    protected boolean hasPushableBody() {
        return true;
    }
}
