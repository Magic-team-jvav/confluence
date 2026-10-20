package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import org.confluence.mod.common.entity.projectile.MonsterLaser;
import org.confluence.mod.common.gameevent.MartianEventHelper;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.jetbrains.annotations.Nullable;

import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/// 按工程师身份统计当前维度中已加载的存活炮塔，最多维持四座。
public final class MartianEngineer extends MartianHumanoidMonster {
    private static final int DEPLOY_RANGE = 5;
    private static final int DIAMETER = DEPLOY_RANGE * 2 + 1;
    private static final int POSITIONS = DIAMETER * DIAMETER * DIAMETER;
    private static final RawAnimation STAND = RawAnimation.begin().thenLoop("Stand");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("Walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("Run ME");
    public static final int LASER_INTERVAL = 10;
    private static final int SITE_SEARCH_INTERVAL = 20;
    private static final int MAX_PATH_PROBES = 24;
    private static final EntityDataAccessor<Boolean> TURRET_MODE =
            SynchedEntityData.defineId(MartianEngineer.class, EntityDataSerializers.BOOLEAN);
    private static final RawAnimation STAND_AIM = RawAnimation.begin().thenLoop("Stand Aim");
    private int deployCooldown;
    private int laserCooldown;
    private int quotaCheckCooldown;
    private int siteSearchCooldown;
    private int aimWarmup;
    private boolean turretQuotaFull;
    private boolean nextShotLeft = true;
    private @Nullable BlockPos firingSite;
    private @Nullable UUID firingTarget;

    public MartianEngineer(EntityType<? extends MartianEngineer> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(TURRET_MODE, false);
    }

    public boolean isTurretMode() {
        return entityData.get(TURRET_MODE);
    }

    public Vec3 getMuzzlePosition(boolean left) {
        return EngineerTurretGeometry.muzzle(position(), getYRot(), getScale(), left);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        return effect != null && !effect.is(ModEffects.SHIMMER) && super.canBeAffected(effect);
    }

    @Override
    protected BTNode createLandBehavior() {
        return SelectorNode.of(new BTNode() {
            @Override
            public boolean canStart() {
                LivingEntity target = getTarget();
                return !isPassenger() && target != null && target.isAlive() && canAttack(target);
            }

            @Override
            public BTStatus execute() {
                if (!canStart()) return BTStatus.FAILURE;
                tickEngineering((ServerLevel) level());
                return BTStatus.RUNNING;
            }

            @Override
            public void stop() {
                getNavigation().stop();
                abandonFiringSite();
                firingTarget = null;
                setSprinting(false);
            }
        }, new VanillaGoalAction(new WaterAvoidingRandomStrollGoal(this, 0.8D)),
                new VanillaGoalAction(new RandomLookAroundGoal(this)));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<MartianEngineer> controller = new AnimationController<>(this, "engineer", 4, state -> {
            RawAnimation movement = isSprinting() ? RUN : WALK;
            RawAnimation idleOrWalk = state.isMoving() ? movement : STAND;
            return state.setAndContinue(isTurretMode() ? STAND_AIM : idleOrWalk);
        });
        controllers.add(controller);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && (isNoAi() || isPassenger() || getTarget() == null)) {
            abandonFiringSite();
            firingTarget = null;
            setSprinting(false);
        }
    }

    /// 部署、选址与射击作为同一个工程战斗行为执行。
    private void tickEngineering(ServerLevel serverLevel) {
        if (deployCooldown > 0) {
            deployCooldown--;
        }
        if (laserCooldown > 0) {
            laserCooldown--;
        }
        if (siteSearchCooldown > 0) {
            siteSearchCooldown--;
        }
        if (quotaCheckCooldown > 0) {
            quotaCheckCooldown--;
        }
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive() || !canAttack(target)) {
            abandonFiringSite();
            firingTarget = null;
            setSprinting(false);
            return;
        }
        if (!target.getUUID().equals(firingTarget)) {
            abandonFiringSite();
            firingTarget = target.getUUID();
        }
        if (quotaCheckCooldown == 0) {
            turretQuotaFull = !hasTurretCapacity(serverLevel);
            quotaCheckCooldown = 20;
        }
        if (turretQuotaFull) {
            tickTurretMode(serverLevel, target);
            return;
        }
        abandonFiringSite();
        tickBuilderMovement(target);
        tryDeployTurret(serverLevel, target);
    }

    private void tickBuilderMovement(LivingEntity target) {
        double distance = distanceToSqr(target);
        if (distance < 64.0D) {
            getNavigation().moveTo(2.0D * getX() - target.getX(), getY(),
                    2.0D * getZ() - target.getZ(), 1.0D);
            setSprinting(true);
        } else if (distance > 256.0D) {
            getNavigation().moveTo(target, 0.85D);
            setSprinting(false);
        } else {
            getNavigation().stop();
            setSprinting(false);
        }
    }

    private void tryDeployTurret(ServerLevel serverLevel, LivingEntity target) {
        if (deployCooldown != 0) return;
        if (!hasTurretCapacity(serverLevel)) {
            turretQuotaFull = true;
            deployCooldown = 20;
            return;
        }
        BlockPos site = findDeployPosition(serverLevel);
        if (site == null) {
            deployCooldown = 40;
            return;
        }
        EntityType<TeslaTurret> turretType = MonsterEntities.TESLA_TURRET.get();
        TeslaTurret turret = turretType.create(serverLevel);
        if (turret == null) {
            deployCooldown = 40;
            return;
        }
        turret.moveTo(site.getX() + 0.5D, site.getY(), site.getZ() + 0.5D, getYRot(), 0.0F);
        turret.bindEngineer(this);
        turret.setTarget(target);
        MartianEventHelper.inheritEventMembership(this, turret);
        if (!serverLevel.noCollision(turret) || !serverLevel.addFreshEntity(turret)) {
            turret.discard();
            deployCooldown = 20;
            return;
        }
        deployCooldown = 200;
        quotaCheckCooldown = 0;
    }

    private void tickTurretMode(ServerLevel level, LivingEntity target) {
        setSprinting(false);
        if (firingSite != null && !isFiringSiteUsable(level, firingSite, target)) {
            abandonFiringSite();
        }
        if (firingSite == null) {
            setTurretMode(false);
            if (siteSearchCooldown == 0) {
                FiringPosition candidate = findFiringPosition(level, target);
                siteSearchCooldown = SITE_SEARCH_INTERVAL;
                if (candidate != null) {
                    firingSite = candidate.site();
                    getNavigation().moveTo(candidate.path(), 1.0);
                    return;
                }
            }
            retreatFromTarget(target);
            return;
        }
        Vec3 siteCenter = Vec3.atBottomCenterOf(firingSite);
        Vec3 displacement = position().subtract(siteCenter);
        boolean reachedHeight = onGround() ? Math.abs(displacement.y) < 0.5
                : displacement.y >= -0.5 && displacement.y <= 2;
        if (displacement.horizontalDistanceSqr() > 0.75 * 0.75 || !reachedHeight) {
            setTurretMode(false);
            if (getNavigation().isDone()) {
                abandonFiringSite();
                siteSearchCooldown = SITE_SEARCH_INTERVAL;
            }
            return;
        }
        getNavigation().stop();
        getMoveControl().setWantedPosition(getX(), getY(), getZ(), 0);
        setSpeed(0);
        setDeltaMovement(getDeltaMovement().multiply(0, 1, 0));
        if (!onGround()) {
            setTurretMode(false);
            return;
        }

        faceCombatDirection(target.getEyePosition().subtract(getEyePosition()), 30, 15);
        float desiredYaw = yawTo(position(), target);
        if (Math.abs(Mth.wrapDegrees(getYRot() - desiredYaw)) > 5) {
            setTurretMode(false);
            return;
        }
        if (!hasClearFiringPaths(position(), getYRot(), target)) {
            abandonFiringSite();
            return;
        }
        if (!isTurretMode()) {
            setTurretMode(true);
            return;
        }
        if (aimWarmup > 0) {
            aimWarmup--;
            return;
        }
        if (laserCooldown == 0) {
            fireLaser(level, target);
        }
    }

    private void fireLaser(ServerLevel level, LivingEntity target) {
        MonsterLaser laser = ModEntities.MONSTER_LASER.get().create(level);
        if (laser == null) {
            return;
        }
        laser.configureShot(this, target, getMuzzlePosition(nextShotLeft), MonsterLaser.Variant.MARTIAN_ENGINEER);
        MartianEventHelper.inheritEventMembership(this, laser);
        if (level.addFreshEntity(laser)) {
            nextShotLeft = !nextShotLeft;
            laserCooldown = LASER_INTERVAL;
            playSound(ModSoundEvents.ITEM_LASER_SHOOT.get(), 0.8F, 1.0F);
        } else {
            laser.discard();
        }
    }

    private void setTurretMode(boolean enabled) {
        if (isTurretMode() != enabled) {
            entityData.set(TURRET_MODE, enabled);
            aimWarmup = enabled ? 4 : 0;
        }
    }

    private void abandonFiringSite() {
        if (firingSite != null || isTurretMode()) {
            getNavigation().stop();
        }
        firingSite = null;
        siteSearchCooldown = 0;
        setTurretMode(false);
    }

    private void retreatFromTarget(LivingEntity target) {
        Vec3 away = position().subtract(target.position()).multiply(1, 0, 1).normalize();
        if (away.lengthSqr() < 1.0E-6) {
            away = new Vec3(1, 0, 0);
        }
        Vec3 destination = position().add(away.scale(6));
        getNavigation().moveTo(destination.x, destination.y, destination.z, 1.0);
        setSprinting(position().subtract(target.position()).horizontalDistance() < 12);
    }

    private @Nullable FiringPosition findFiringPosition(ServerLevel level, LivingEntity target) {
        List<ScoredSite> candidates = new ArrayList<>();
        BlockPos center = blockPosition();
        double minimumDistance = Math.min(24, Math.max(12,
                position().subtract(target.position()).horizontalDistance() + 3));
        for (int dx = -12; dx <= 12; dx += 2) {
            for (int dz = -12; dz <= 12; dz += 2) {
                for (int dy = 8; dy >= -3; dy--) {
                    BlockPos site = center.offset(dx, dy, dz);
                    Vec3 point = Vec3.atBottomCenterOf(site);
                    double targetDistance = point.subtract(target.position()).horizontalDistance();
                    if (targetDistance < minimumDistance || !isFiringSiteUsable(level, site, target)) {
                        continue;
                    }
                    double travel = point.subtract(position()).horizontalDistance();
                    double score = dy * 24 + targetDistance * 2 - travel;
                    candidates.add(new ScoredSite(site, score));
                }
            }
        }
        candidates.sort(Comparator.comparingDouble(ScoredSite::score).reversed());
        int probes = Math.min(MAX_PATH_PROBES, candidates.size());
        for (int index = 0; index < probes; index++) {
            BlockPos site = candidates.get(index).site();
            Path path = getNavigation().createPath(site, 0);
            if (path != null && path.canReach()) {
                return new FiringPosition(site, path);
            }
        }
        return null;
    }

    private boolean isFiringSiteUsable(ServerLevel level, BlockPos site, LivingEntity target) {
        Vec3 point = Vec3.atBottomCenterOf(site);
        double distance = point.subtract(target.position()).horizontalDistance();
        if (distance < 10 || distance > 36 || site.getY() <= level.getMinBuildHeight()
                || site.getY() + getBbHeight() >= level.getMaxBuildHeight()
                || !level.hasChunksAt(site.offset(-1, -1, -1), site.offset(1, 2, 1))) {
            return false;
        }
        BlockPos floor = site.below();
        if (!level.getBlockState(floor).isFaceSturdy(level, floor, Direction.UP)
                || !level.getFluidState(site).isEmpty() || !level.getFluidState(site.above()).isEmpty()
                || !level.getFluidState(floor).isEmpty()
                || !level.noCollision(this, getBoundingBox().move(point.subtract(position())))) {
            return false;
        }
        return hasClearFiringPaths(point, yawTo(point, target), target);
    }

    private static float yawTo(Vec3 position, LivingEntity target) {
        Vec3 offset = target.position().subtract(position);
        return (float) Math.toDegrees(Math.atan2(offset.z, offset.x)) - 90;
    }

    private boolean hasClearFiringPaths(Vec3 position, float yaw, LivingEntity target) {
        Vec3 body = position.add(0, getBbHeight() * 0.65, 0);
        Vec3 left = EngineerTurretGeometry.muzzle(position, yaw, getScale(), true);
        Vec3 right = EngineerTurretGeometry.muzzle(position, yaw, getScale(), false);
        Vec3 destination = target.getEyePosition();
        return clearRay(body, left) && clearRay(left, destination)
                && clearRay(body, right) && clearRay(right, destination);
    }

    private boolean clearRay(Vec3 start, Vec3 end) {
        return level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
    }

    private record ScoredSite(BlockPos site, double score) {}

    private record FiringPosition(BlockPos site, Path path) {}

    public static boolean hasTurretCapacity(int engineers, int turrets) {
        return engineers > 0 && turrets >= 0 && (long) turrets < (long) engineers * 4L;
    }

    private static boolean hasTurretCapacity(ServerLevel level) {
        int engineers = 0;
        int turrets = 0;
        for (Entity entity : level.getAllEntities()) {
            if (!entity.isAlive() || entity.isRemoved()) continue;
            if (entity instanceof MartianEngineer) engineers++;
            else if (entity instanceof TeslaTurret) turrets++;
        }
        return hasTurretCapacity(engineers, turrets);
    }

    private @Nullable BlockPos findDeployPosition(ServerLevel level) {
        BlockPos center = blockPosition();
        int start = random.nextInt(POSITIONS);
        for (int attempt = 0; attempt < POSITIONS; attempt++) {
            int index = (start + attempt * 17) % POSITIONS;
            int dx = index % DIAMETER - DEPLOY_RANGE;
            int dz = index / DIAMETER % DIAMETER - DEPLOY_RANGE;
            int dy = index / (DIAMETER * DIAMETER) - DEPLOY_RANGE;
            BlockPos site = center.offset(dx, dy, dz);
            if (canDeployTurretAt(level, site)) return site;
        }
        return null;
    }

    /// 部署位置需要坚固地面、4×2×4 的空地，且该区域上方不能有岩浆。
    public static boolean canDeployTurretAt(ServerLevel level, BlockPos site) {
        if (level == null || site == null || site.getY() <= level.getMinBuildHeight()
                || site.getY() + 2 > level.getMaxBuildHeight()) return false;
        BlockPos floor = site.below();
        if (!level.isLoaded(floor)) return false;
        BlockState support = level.getBlockState(floor);
        if (!support.isFaceSturdy(level, floor, Direction.UP)
                || !level.getFluidState(floor).isEmpty()) return false;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -1; dx <= 2; dx++) {
            for (int dz = -1; dz <= 2; dz++) {
                int x = site.getX() + dx;
                int z = site.getZ() + dz;
                cursor.set(x, site.getY(), z);
                if (!level.isLoaded(cursor)) return false;
                for (int dy = 0; dy < 2; dy++) {
                    cursor.set(x, site.getY() + dy, z);
                    if (!level.getBlockState(cursor).isAir() || !level.getFluidState(cursor).isEmpty()) return false;
                }
                for (int y = site.getY() + 2; y < level.getMaxBuildHeight(); y++) {
                    cursor.set(x, y, z);
                    if (level.getFluidState(cursor).is(FluidTags.LAVA)) return false;
                }
            }
        }
        return true;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("DeployCooldown", deployCooldown);
        tag.putInt("EngineerLaserCooldown", laserCooldown);
        tag.putBoolean("EngineerNextShotLeft", nextShotLeft);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        deployCooldown = Mth.clamp(tag.getInt("DeployCooldown"), 0, 200);
        laserCooldown = Mth.clamp(tag.getInt("EngineerLaserCooldown"), 0, LASER_INTERVAL);
        nextShotLeft = !tag.contains("EngineerNextShotLeft") || tag.getBoolean("EngineerNextShotLeft");
        firingTarget = null;
        quotaCheckCooldown = 0;
        turretQuotaFull = false;
        abandonFiringSite();
    }
}
