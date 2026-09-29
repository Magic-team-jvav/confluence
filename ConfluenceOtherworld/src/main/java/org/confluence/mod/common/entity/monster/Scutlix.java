package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

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
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag tag) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData, tag);
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
        if (riderCache != null && riderCache.isAlive() && !riderCache.isRemoved()) {
            if (riderCache.getVehicle() == this || riderCache.startRiding(this, true)) {
                bindRider(riderCache);
                return riderCache;
            }
        }
        riderCache = null;
        if (riderUUID == null) return null;

        Entity resolved = serverLevel.getEntity(riderUUID);
        if (resolved instanceof RayGunner rayGunner && rayGunner.isAlive() && !rayGunner.isRemoved()
                && (rayGunner.getVehicle() == this || rayGunner.startRiding(this, true))) {
            bindRider(rayGunner);
            return rayGunner;
        }

        if (++unresolvedRiderTicks > LINK_GRACE_TICKS) {
            riderUUID = null;
            unresolvedRiderTicks = 0;
        }
        return null;
    }

    /**
     * Ensures this mount has its one initial rider. Event spawning calls this
     * synchronously before tagging or tracking the riding tree; normal entity
     * spawning reaches the same path from {@link #tick()}.
     */
    public boolean ensureRider(ServerLevel serverLevel) {
        if (serverLevel == null || serverLevel != level() || !isAlive() || isRemoved()) return false;

        RayGunner current = findCurrentRider();
        if (current != null) {
            bindRider(current);
            shareTargetWith(current);
            return true;
        }
        RayGunner restored = resolveSavedRider(serverLevel);
        if (restored != null) {
            shareTargetWith(restored);
            return true;
        }
        // A handled mount has already had a rider. Its death or permanent loss
        // must not manufacture a replacement.
        if (riderSpawnHandled) return false;

        if (!MonsterEntities.RAY_GUNNER.isPresent()) return false;
        EntityType<RayGunner> type = MonsterEntities.RAY_GUNNER.get();
        if (type == null) return false;
        RayGunner rider = type.create(serverLevel);
        if (rider == null) return false;

        rider.moveTo(getX(), getY() + getPassengersRidingOffset(), getZ(), getYRot(), 0.0F);
        rider.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(blockPosition()),
                MobSpawnType.JOCKEY, null, null);
        rider.setTarget(getTarget());

        if (!serverLevel.addFreshEntity(rider)) {
            rider.discard();
            return false;
        }
        if (!rider.startRiding(this, true)) {
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
    public double getPassengersRidingOffset() {
        return super.getPassengersRidingOffset() - 0.5D;
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
