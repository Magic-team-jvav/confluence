package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.SwimNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;

/// 敌对水生生物共用的水下导航、移动与目标选择基类。
///
/// 具体生物只补充属性和攻击方式，避免各自重复处理离水、游动与路径导航边界。
public abstract class BaseAquaticMonster extends BaseMonster {
    protected BaseAquaticMonster(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, false);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return 0.0F;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader level) {
        return level.isUnobstructed(this);
    }

    @Override
    protected boolean canTargetPlayer(LivingEntity target) {
        return isValidAquaticTarget(target);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level) {
            @Override
            protected PathFinder createPathFinder(int maxVisitedNodes) {
                nodeEvaluator = new SwimNodeEvaluator(false) {
                    @Override
                    public PathType getPathTypeOfMob(PathfindingContext context, int x, int y, int z, Mob mob) {
                        PathType type = super.getPathTypeOfMob(context, x, y, z, mob);
                        if (type != PathType.WATER) {
                            return type;
                        }
                        CollisionGetter collisions = context.level();
                        double offset = (int) (mob.getBbWidth() + 1.0F) * 0.5;
                        AABB box = mob.getBoundingBox().move(x + offset - mob.getX(), y - mob.getY(), z + offset - mob.getZ());
                        return collisions.getBlockCollisions(mob, box).iterator().hasNext() ? PathType.BLOCKED : type;
                    }
                };
                return new PathFinder(nodeEvaluator, maxVisitedNodes);
            }
        };
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return isValidAquaticTarget(target) && super.canAttack(target);
    }

    /// 判断目标是否处于当前水生物种能够攻击的位置；普通水生敌怪仅攻击水中目标。
    protected boolean isValidAquaticTarget(LivingEntity target) {
        return target.isInWaterOrBubble();
    }

    /// 1.21.1 的 `LivingEntity#canBreatheUnderwater()` 是 **final**
    /// （`LivingEntity.java:382`，本体改为查 `EntityTypeTags.CAN_BREATHE_UNDER_WATER`），
    /// 且它自己就带 `@Deprecated //FORGE: Use canDrownInFluidType instead`。
    /// NeoForge 给出的替代钩子是 `ILivingEntityExtension#canDrownInFluidType(FluidType)`
    /// （`ILivingEntityExtension.java:55`，水类型的默认实现恰好就是 `!canBreatheUnderwater()`），
    /// 因此在「水」这一类型上返回 false，语义与 1.20 的 `canBreatheUnderwater() { return true; }` 等价。
    @Override
    public boolean canDrownInFluidType(FluidType type) {
        return type != NeoForgeMod.WATER_TYPE.value() && super.canDrownInFluidType(type);
    }

    @Override
    public int getMaxHeadXRot() {
        return 1;
    }

    @Override
    public int getMaxHeadYRot() {
        return 1;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && flopsOnLand() && !isNoAi() && !isInWaterOrBubble() && onGround()) {
            setDeltaMovement(getDeltaMovement().add((random.nextFloat() * 2.0F - 1.0F) * 0.2F, 0.5, (random.nextFloat() * 2.0F - 1.0F) * 0.2F));
            setYRot(random.nextFloat() * 360.0F);
            setOnGround(false);
            hasImpulse = true;
        }
    }

    /// 鱼类离水后会挣扎；水母等无法主动离地的生物可关闭这一共用行为。
    protected boolean flopsOnLand() {
        return true;
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (isEffectiveAi() && isInWater()) {
            moveRelative(getSpeed(), travelVector);
            move(MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().scale(0.9));
            return;
        }
        super.travel(travelVector);
    }
}
