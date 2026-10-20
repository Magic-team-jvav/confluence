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
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import org.confluence.mod.common.entity.projectile.MonsterLaser;
import org.confluence.mod.common.gameevent.MartianEventHelper;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.ModEntities;
import org.jetbrains.annotations.Nullable;

import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.Comparator;
import java.util.UUID;

/// 激光枪手共用一套射击行为，骑手身份恢复独立于战斗调度。
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
        updateMountedState();
    }

    @Override
    protected BTNode createLandBehavior() {
        return SelectorNode.of(new VanillaGoalAction(new MountScutlixGoal()), new BTNode() {
            private int pathDelay;

            @Override
            public boolean canStart() {
                return (!isPassenger() || isMountedOnLivingScutlix()) && usableTarget(getTarget());
            }

            @Override
            public BTStatus execute() {
                LivingEntity target = getTarget();
                if (!canStart()) {
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
                tickShooting((ServerLevel) level(), target);
                return BTStatus.RUNNING;
            }

            @Override
            public void stop() {
                pathDelay = 0;
                getNavigation().stop();
                aimTicks = 0;
                entityData.set(DATA_AIMING, false);
            }
        }, new VanillaGoalAction(new WaterAvoidingRandomStrollGoal(this, 0.8D)),
                new VanillaGoalAction(new RandomLookAroundGoal(this)));
    }

    @Override
    public void tick() {
        if (level() instanceof ServerLevel serverLevel) resolveMount(serverLevel);
        updateMountedState();
        super.tick();
        /// 身份恢复不受 AI 开关影响，停止战斗时只复位瞄准表现。
        if (!level().isClientSide && (isNoAi() || isPassenger() && !isMountedOnLivingScutlix() || !usableTarget(getTarget()))) {
            entityData.set(DATA_AIMING, false);
            aimTicks = 0;
        }
    }

    private void tickShooting(ServerLevel serverLevel, LivingEntity target) {
        if (shotCooldown > 0) shotCooldown--;
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
        if (isPassenger() || mountCache != null && (!mountCache.isAlive() || mountCache.isRemoved())) {
            mountUUID = null;
            unresolvedMountTicks = 0;
        }

        mountCache = null;
        if (mountUUID == null) return;
        Entity resolved = serverLevel.getEntity(mountUUID);
        if (resolved instanceof Scutlix scutlix) {
            if (!scutlix.isAlive() || scutlix.isRemoved() || scutlix.isVehicle()) {
                mountUUID = null;
                unresolvedMountTicks = 0;
                return;
            }
            if (scutlix.tryMountGunner(this)) {
                shareTargetWith(scutlix);
                return;
            }
        }
        if (++unresolvedMountTicks > LINK_GRACE_TICKS) {
            mountUUID = null;
            unresolvedMountTicks = 0;
        }
    }

    private final class MountScutlixGoal extends Goal {
        private static final double SEARCH_RANGE = 32.0;
        private @Nullable Scutlix candidate;
        private @Nullable Path path;
        private int nextSearchTick;
        private int candidateOffset;
        private int pathDelay;
        private int approachTicks;
        private boolean pathFailed;

        @Override
        public boolean canUse() {
            if (isPassenger() || !isAlive() || tickCount < nextSearchTick) {
                return false;
            }
            nextSearchTick = tickCount + 20;
            var mounts = level().getEntitiesOfClass(Scutlix.class, getBoundingBox().inflate(SEARCH_RANGE, 8, SEARCH_RANGE),
                    mount -> mount.canAcceptGunner(RayGunner.this) && distanceToSqr(mount) <= SEARCH_RANGE * SEARCH_RANGE);
            mounts.sort(Comparator.comparingDouble(RayGunner.this::distanceToSqr));
            if (mounts.isEmpty()) {
                return false;
            }
            int start = candidateOffset % mounts.size();
            for (int index = 0; index < Math.min(8, mounts.size()); index++) {
                Scutlix mount = mounts.get((start + index) % mounts.size());
                if (mount.isWithinBoardingRange(RayGunner.this)) {
                    candidate = mount;
                    path = null;
                    candidateOffset = 0;
                    return true;
                }
                Path approach = getNavigation().createPath(mount, 0);
                if (approach != null && approach.canReach()) {
                    candidate = mount;
                    path = approach;
                    candidateOffset = 0;
                    return true;
                }
            }
            candidateOffset = (start + 8) % mounts.size();
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return candidate != null && candidate.canAcceptGunner(RayGunner.this)
                    && distanceToSqr(candidate) <= SEARCH_RANGE * SEARCH_RANGE && approachTicks < 200 && !pathFailed;
        }

        @Override
        public void start() {
            approachTicks = 0;
            pathDelay = 10;
            pathFailed = path != null && !getNavigation().moveTo(path, 1.2);
        }

        @Override
        public void tick() {
            if (candidate == null || !candidate.canAcceptGunner(RayGunner.this)) {
                return;
            }
            approachTicks++;
            if (candidate.tryMountGunner(RayGunner.this)) {
                getNavigation().stop();
            } else if (--pathDelay <= 0) {
                pathDelay = 10;
                path = getNavigation().createPath(candidate, 0);
                pathFailed = path == null || !path.canReach() || !getNavigation().moveTo(path, 1.2);
            }
        }

        @Override
        public void stop() {
            candidate = null;
            path = null;
            getNavigation().stop();
            nextSearchTick = tickCount + 20;
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
        MartianEventHelper.inheritEventMembership(this, laser);
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
            /// 模型只有坐姿瞄准动画，没有单独的坐姿待机动画。
            /// 骑手持续保持坐姿，由瞄准状态控制蓄力和射击。
            if (isPassenger()) return state.setAndContinue(SIT_AIM);
            if (isAiming()) {
                return state.setAndContinue(state.isMoving() ? WALK_AIM : STAND_AIM);
            }
            return state.setAndContinue(state.isMoving() ? WALK_GUN : STAND_GUN);
        }));
    }

    @Override
    protected boolean hasEntityContactAttack() {
        /// 骑乘时由蝎怪结算接触伤害，运输和禁用 AI 时也不额外攻击。
        return !isNoAi() && !isPassenger();
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
