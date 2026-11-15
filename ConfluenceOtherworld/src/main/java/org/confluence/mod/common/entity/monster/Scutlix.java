package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.gameevent.MartianEventHelper;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.UUID;

public final class Scutlix extends BaseWarriorMonster {
    public enum CombatState {
        MOUNTED
    }

    private static final int LINK_GRACE_TICKS = 200;
    private static final int SPAWN_RETRY_TICKS = 20;
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("Walk");

    private boolean riderSpawnHandled;
    private int riderSpawnRetryTicks;
    private int unresolvedRiderTicks;
    private boolean mountedStateApplied;
    private @Nullable UUID riderUUID;
    private @Nullable RayGunner riderCache;

    public Scutlix(EntityType<? extends Scutlix> type, Level level) {
        super(type, level);
    }

    /// 怪物蛋只生成坐骑本体，背上不会制造骑手；骑手仅由火星暴乱事件等生成路径创建。
    /// 标记会写入存档，因此由怪物蛋生成的 Scutlix 重载后也不会补生骑手。
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
        if (spawnType == MobSpawnType.SPAWN_EGG) riderSpawnHandled = true;
        return result;
    }

    @Override
    public void tick() {
        if (!level().isClientSide) updateMountedState(findCurrentRider() != null);
        super.tick();
        if (!(level() instanceof ServerLevel serverLevel) || !isAlive()) return;

        RayGunner rider = findCurrentRider();
        if (rider != null) {
            bindRider(rider);
            shareTargetWith(rider);
            return;
        }

        rider = resolveSavedRider(serverLevel);
        if (rider != null) {
            shareTargetWith(rider);
            return;
        }

        if (riderSpawnHandled) return;
        if (riderSpawnRetryTicks > 0) {
            riderSpawnRetryTicks--;
            return;
        }
        if (!ensureRider(serverLevel)) riderSpawnRetryTicks = SPAWN_RETRY_TICKS;
    }

    private @Nullable RayGunner findCurrentRider() {
        for (Entity passenger : getPassengers()) {
            if (passenger instanceof RayGunner rayGunner && rayGunner.isAlive() && !rayGunner.isRemoved()) {
                return rayGunner;
            }
        }
        return null;
    }

    private @Nullable RayGunner resolveSavedRider(ServerLevel serverLevel) {
        RayGunner rider = riderCache;
        if (rider == null || !rider.isAlive() || rider.isRemoved()) {
            Entity resolved = riderUUID == null ? null : serverLevel.getEntity(riderUUID);
            rider = resolved instanceof RayGunner gunner ? gunner : null;
        }
        if (rider != null && rider.isAlive() && !rider.isRemoved()) {
            if (rider.getVehicle() == this || tryMountGunner(rider)) {
                bindRider(rider);
                return rider;
            }
            if (rider.isPassenger()) {
                clearRiderLink();
                return null;
            }
        }
        riderCache = null;
        if (riderUUID != null && ++unresolvedRiderTicks > LINK_GRACE_TICKS) {
            clearRiderLink();
        }
        return null;
    }

    private void clearRiderLink() {
        riderCache = null;
        riderUUID = null;
        unresolvedRiderTicks = 0;
    }

    boolean canAcceptGunner(RayGunner gunner) {
        return isAlive() && !isRemoved() && !isPassenger() && getPassengers().isEmpty()
                && gunner.isAlive() && !gunner.isRemoved() && !gunner.isPassenger() && gunner.level() == level();
    }

    boolean isWithinBoardingRange(RayGunner gunner) {
        return gunner.getBoundingBox().inflate(0.5).intersects(getBoundingBox());
    }

    boolean tryMountGunner(RayGunner gunner) {
        if (!canAcceptGunner(gunner) || !isWithinBoardingRange(gunner) || !gunner.startRiding(this)) {
            return false;
        }
        bindRider(gunner);
        shareTargetWith(gunner);
        return true;
    }

    /// 确保坐骑只生成一次初始骑手，事件生成和常规生成共用此入口。
    public boolean ensureRider(ServerLevel serverLevel) {
        if (serverLevel == null || serverLevel != level() || !isAlive() || isRemoved()) return false;

        RayGunner current = findCurrentRider();
        if (current != null) {
            bindRider(current);
            shareTargetWith(current);
            return true;
        }
        if (!getPassengers().isEmpty()) return false;
        RayGunner restored = resolveSavedRider(serverLevel);
        if (restored != null) {
            shareTargetWith(restored);
            return true;
        }
        /// 已经生成过骑手的坐骑，在骑手死亡或永久丢失后
        /// 不会再自动补充骑手。
        if (riderSpawnHandled) return false;

        if (!MonsterEntities.RAY_GUNNER.isBound()) return false;
        EntityType<RayGunner> type = MonsterEntities.RAY_GUNNER.get();
        if (type == null) return false;
        RayGunner rider = type.create(serverLevel);
        if (rider == null) return false;

        Vec3 ridingPosition = getPassengerRidingPosition(rider);
        rider.moveTo(ridingPosition.x, ridingPosition.y, ridingPosition.z, getYRot(), 0.0F);
        rider.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(blockPosition()),
                MobSpawnType.JOCKEY, null);
        rider.setTarget(getTarget());

        MartianEventHelper.inheritEventMembership(this, rider);
        if (!serverLevel.addFreshEntity(rider)) {
            rider.discard();
            return false;
        }
        if (!rider.startRiding(this)) {
            rider.discard();
            return false;
        }

        bindRider(rider);
        riderSpawnHandled = true;
        return true;
    }

    private void bindRider(RayGunner rider) {
        if (rider == null || rider.getVehicle() != this) return;
        riderCache = rider;
        riderUUID = rider.getUUID();
        unresolvedRiderTicks = 0;
        riderSpawnHandled = true;
        rider.bindMount(this);
        updateMountedState(true);
    }

    private void updateMountedState(boolean mounted) {
        if (level().isClientSide || mounted == mountedStateApplied) return;
        mountedStateApplied = mounted;
        setSpecialState(CombatState.MOUNTED, mounted);
    }

    private void shareTargetWith(RayGunner rider) {
        LivingEntity mountTarget = getTarget();
        LivingEntity riderTarget = rider.getTarget();
        if (isUsableTarget(mountTarget) && !isUsableTarget(riderTarget)) {
            rider.setTarget(mountTarget);
        } else if (!isUsableTarget(mountTarget) && isUsableTarget(riderTarget)) {
            setTarget(riderTarget);
        }
    }

    private boolean isUsableTarget(@Nullable LivingEntity target) {
        return target != null && target.isAlive() && !target.isRemoved() && target.level() == level() && canAttack(target);
    }

    public @Nullable UUID getRiderUUID() {
        return riderUUID;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return getPassengers().isEmpty() && passenger instanceof RayGunner;
    }

    /// 骑乘高度整体下移半格，避免 Ray Gunner 悬在甲壳上方。
    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float partialTick) {
        return super.getPassengerAttachmentPoint(entity, dimensions, partialTick).add(0.0, -0.5, 0.0);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "scutlix", 4,
                state -> state.isMoving() ? state.setAndContinue(WALK) : PlayState.STOP));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.SCUTLIX_FREE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.SCUTLIX_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.SCUTLIX_DEATH.get();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("RiderSpawnHandled", riderSpawnHandled);
        tag.putInt("RiderSpawnRetryTicks", riderSpawnRetryTicks);
        if (riderUUID != null) tag.putUUID("Rider", riderUUID);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        riderSpawnHandled = tag.contains("RiderSpawnHandled")
                ? tag.getBoolean("RiderSpawnHandled")
                : tag.getBoolean("RiderSpawned");
        riderSpawnRetryTicks = tag.contains("RiderSpawnRetryTicks")
                ? Math.max(0, Math.min(SPAWN_RETRY_TICKS, tag.getInt("RiderSpawnRetryTicks"))) : 0;
        riderUUID = tag.hasUUID("Rider") ? tag.getUUID("Rider") : null;
        riderCache = null;
        unresolvedRiderTicks = 0;
        setSpecialState(CombatState.MOUNTED, false);
        mountedStateApplied = false;
    }
}
