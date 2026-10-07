package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

/// 为每位火星工程师提供共享的四座特斯拉炮塔限额。
public final class MartianEngineer extends MartianHumanoidMonster {
    public static final String EVENT_TAG = "confluence:martian_madness";
    private static final int DEPLOY_RANGE = 5;
    private static final int DIAMETER = DEPLOY_RANGE * 2 + 1;
    private static final int POSITIONS = DIAMETER * DIAMETER * DIAMETER;
    private static final RawAnimation STAND = RawAnimation.begin().thenLoop("Stand");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("Walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("Run ME");
    private int deployCooldown;

    public MartianEngineer(EntityType<? extends MartianEngineer> type, Level level) {
        super(type, level);
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
                return target != null && target.isAlive() && canAttack(target);
            }

            @Override
            public BTStatus execute() {
                return canStart() ? BTStatus.RUNNING : BTStatus.FAILURE;
            }

            @Override
            public void stop() {
                getNavigation().stop();
            }
        }, new VanillaGoalAction(new WaterAvoidingRandomStrollGoal(this, 0.8D)),
                new VanillaGoalAction(new RandomLookAroundGoal(this)));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "engineer", 4, state -> state.setAndContinue(
                state.isMoving() ? (isSprinting() ? RUN : WALK) : STAND)));
    }

    @Override
    public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel serverLevel) || !isAlive()) return;
        if (deployCooldown > 0) deployCooldown--;
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive() || !canAttack(target)) {
            setSprinting(false);
            return;
        }
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
        if (deployCooldown != 0) return;
        if (!hasTurretCapacity(serverLevel)) {
            deployCooldown = 20;
            return;
        }
        BlockPos site = findDeployPosition(serverLevel);
        if (site == null) {
            deployCooldown = 40;
            return;
        }
        if (!MonsterEntities.TESLA_TURRET.isBound()) {
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
        if (getTags().contains(EVENT_TAG)) turret.addTag(EVENT_TAG);
        if (!serverLevel.noCollision(turret) || !serverLevel.addFreshEntity(turret)) {
            turret.discard();
            deployCooldown = 20;
            return;
        }
        deployCooldown = 200;
    }

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

    /** Requires a sturdy floor, 4 x 2 x 4 clear blocks, and no lava above that footprint. */
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
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        deployCooldown = tag.contains("DeployCooldown") ? Math.max(0, tag.getInt("DeployCooldown")) : 0;
    }
}
