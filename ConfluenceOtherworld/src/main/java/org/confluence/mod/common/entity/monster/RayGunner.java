package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import org.confluence.mod.common.entity.projectile.MonsterLaser;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.ModEntities;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.UUID;

/** Mounted or dismounted Martian marksman. */
public final class RayGunner extends MartianHumanoidMonster {
    public enum CombatState {
        MOUNTED
    }

    public static final int SHOT_INTERVAL = 14;
    private static final int AIM_WINDUP_TICKS = 6;
    private static final int LINK_GRACE_TICKS = 200;
    private static final double MINIMUM_RANGE_SQUARED = 36.0D;
    private static final double PREFERRED_RANGE_SQUARED = 196.0D;
    private static final double MAXIMUM_SHOT_RANGE_SQUARED = 1024.0D;
    private static final EntityDataAccessor<Boolean> DATA_AIMING =
            SynchedEntityData.defineId(RayGunner.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation STAND_GUN = RawAnimation.begin().thenLoop("Stand Gun");
    private static final RawAnimation WALK_GUN = RawAnimation.begin().thenLoop("Walk Gun");
    private static final RawAnimation STAND_AIM = RawAnimation.begin().thenLoop("Stand Aim");
    private static final RawAnimation WALK_AIM = RawAnimation.begin().thenLoop("Walk Aim");
    private static final RawAnimation SIT_AIM = RawAnimation.begin().thenLoop("Sit Aim");

    private int shotCooldown;
    private int aimTicks;
    private int unresolvedMountTicks;
    private boolean mountedStateApplied;
    private @Nullable UUID mountUUID;
    private @Nullable Scutlix mountCache;

    public RayGunner(EntityType<? extends RayGunner> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_AIMING, false);
    }

    public boolean isAiming() {
        return entityData.get(DATA_AIMING);
    }

    public int getShotCooldown() {
        return shotCooldown;
    }

    public @Nullable UUID getMountUUID() {
        return mountUUID;
    }

    void bindMount(Scutlix mount) {
        if (mount == null || getVehicle() != mount) return;
        mountCache = mount;
        mountUUID = mount.getUUID();
        unresolvedMountTicks = 0;
    }

    @Override
    protected BTNode createLandBehavior() {
        return SelectorNode.of(new BTNode() {
            private int pathDelay;

            @Override
            public boolean canStart() {
                return usableTarget(getTarget());
            }

            @Override
            public BTStatus execute() {
                LivingEntity target = getTarget();
                if (!usableTarget(target)) {
                    getNavigation().stop();
                    return BTStatus.FAILURE;
                }

                if (isMountedOnLivingScutlix()) {
                    getNavigation().stop();
                } else if (--pathDelay <= 0) {
                    pathDelay = 5;
                    double distance = distanceToSqr(target);
                    if (distance < MINIMUM_RANGE_SQUARED) {
                        Vec3 away = position().subtract(target.position()).multiply(1.0D, 0.0D, 1.0D);
                        if (away.lengthSqr() > 1.0E-6D) {
                            away = away.normalize().scale(7.0D);
                            getNavigation().moveTo(getX() + away.x, getY(), getZ() + away.z, 1.1D);
                        }
                    } else if (distance > PREFERRED_RANGE_SQUARED || !hasLineOfSight(target)) {
                        getNavigation().moveTo(target, 1.0D);
                    } else {
                        getNavigation().stop();
                    }
                }
                faceCombatPosition(target.getEyePosition(), 25.0F, 25.0F);
                return BTStatus.RUNNING;
            }

            @Override
            public void stop() {
                pathDelay = 0;
                getNavigation().stop();
            }
        }, new VanillaGoalAction(new WaterAvoidingRandomStrollGoal(this, 0.8D)),
                new VanillaGoalAction(new RandomLookAroundGoal(this)));
    }

    @Override
    public void tick() {
        if (level() instanceof ServerLevel serverLevel) resolveMount(serverLevel);
        updateMountedState();
        super.tick();
        if (!(level() instanceof ServerLevel serverLevel) || !isAlive()) return;

        if (shotCooldown > 0) shotCooldown--;
        LivingEntity target = getTarget();
        boolean aiming = usableTarget(target) && distanceToSqr(target) <= MAXIMUM_SHOT_RANGE_SQUARED
                && hasLineOfSight(target);
        entityData.set(DATA_AIMING, aiming);
        if (aiming) {
            aimTicks = Math.min(AIM_WINDUP_TICKS, aimTicks + 1);
            faceCombatPosition(target.getEyePosition(), 25.0F, 25.0F);
            if (aimTicks >= AIM_WINDUP_TICKS && shotCooldown == 0 && shoot(serverLevel, target)) {
                shotCooldown = SHOT_INTERVAL;
            }
        } else {
            aimTicks = 0;
        }
    }

    private boolean isMountedOnLivingScutlix() {
        return getVehicle() instanceof Scutlix scutlix && scutlix.isAlive() && !scutlix.isRemoved();
    }

    private void updateMountedState() {
        if (level().isClientSide) return;
        boolean mounted = isMountedOnLivingScutlix();
        if (mounted == mountedStateApplied) return;
        mountedStateApplied = mounted;
        setSpecialState(CombatState.MOUNTED, mounted);
    }

    private void resolveMount(ServerLevel serverLevel) {
        Entity vehicle = getVehicle();
        if (vehicle instanceof Scutlix scutlix && scutlix.isAlive() && !scutlix.isRemoved()) {
            bindMount(scutlix);
            shareTargetWith(scutlix);
            return;
        }
        if (vehicle instanceof Scutlix) {
            stopRiding();
            mountUUID = null;
            unresolvedMountTicks = 0;
        }

        mountCache = null;
        if (mountUUID == null) return;
        Entity resolved = serverLevel.getEntity(mountUUID);
        if (resolved instanceof Scutlix scutlix && scutlix.isAlive() && !scutlix.isRemoved()
                && (getVehicle() == scutlix || startRiding(scutlix, true))) {
            bindMount(scutlix);
            shareTargetWith(scutlix);
            return;
        }
        if (++unresolvedMountTicks > LINK_GRACE_TICKS) {
            mountUUID = null;
            unresolvedMountTicks = 0;
        }
    }

    private void shareTargetWith(Scutlix mount) {
        LivingEntity riderTarget = getTarget();
        LivingEntity mountTarget = mount.getTarget();
        if (usableTarget(riderTarget) && !usableTarget(mountTarget)) {
            mount.setTarget(riderTarget);
        } else if (!usableTarget(riderTarget) && usableTarget(mountTarget)) {
            setTarget(mountTarget);
        }
    }

    private boolean usableTarget(@Nullable LivingEntity target) {
        return target != null && target.isAlive() && !target.isRemoved() && target.level() == level() && canAttack(target);
    }

    private boolean shoot(ServerLevel serverLevel, LivingEntity target) {
        if (target == null || !ModEntities.MONSTER_LASER.isPresent()) return false;
        MonsterLaser laser = ModEntities.MONSTER_LASER.get().create(serverLevel);
        if (laser == null) return false;
        laser.configureShot(this, target, new Vec3(getX(), getEyeY() - 0.1D * getScale(), getZ()), MonsterLaser.Variant.RAY_GUNNER);
        if (!serverLevel.addFreshEntity(laser)) {
            laser.discard();
            return false;
        }
        playSound(ModSoundEvents.ITEM_LASER_SHOOT.get(), 0.8F, 1.0F);
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "ray_gunner", 3, state -> {
            // The supplied asset has a dedicated seated aiming pose but no seated idle pose.
            // Keep the rider seated at all times; aiming state still controls the wind-up and shot.
            if (isPassenger()) return state.setAndContinue(SIT_AIM);
            if (isAiming()) {
                return state.setAndContinue(state.isMoving() ? WALK_AIM : STAND_AIM);
            }
            return state.setAndContinue(state.isMoving() ? WALK_GUN : STAND_GUN);
        }));
    }

    @Override
    protected boolean hasEntityContactAttack() {
        // While mounted, the Scutlix owns the encounter's contact hit. This prevents the
        // overlapping passenger hitbox from applying a second, independently-timed strike.
        return !isMountedOnLivingScutlix();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("ShotCooldown", shotCooldown);
        if (mountUUID != null) tag.putUUID("Scutlix", mountUUID);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        shotCooldown = tag.contains("ShotCooldown")
                ? Mth.clamp(tag.getInt("ShotCooldown"), 0, SHOT_INTERVAL) : 0;
        mountUUID = tag.hasUUID("Scutlix") ? tag.getUUID("Scutlix")
                : tag.hasUUID("Owner") ? tag.getUUID("Owner") : null;
        mountCache = null;
        unresolvedMountTicks = 0;
        aimTicks = 0;
        setSpecialState(CombatState.MOUNTED, false);
        mountedStateApplied = false;
        entityData.set(DATA_AIMING, false);
    }
}
