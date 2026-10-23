package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.init.entity.ModEntities;
import org.jetbrains.annotations.Nullable;

/// 通用的「从天而降的追踪坠星」。
///
/// 星泣累积释放与星乐爆开的坠星共用它：从高处落下，并在水平方向上追踪最近的敌人。
public class HomingFallingStarProjectile extends AbstractManaProjectile {
    private static final double HOMING_SPEED = 0.85;
    private static final double HOMING_RANGE = 24.0;
    private static final int MAX_AGE = 200;

    public HomingFallingStarProjectile(EntityType<HomingFallingStarProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public HomingFallingStarProjectile(LivingEntity living) {
        this(ModEntities.HOMING_FALLING_STAR.get(), living.level());
    }

    /// 在目标上方生成一颗坠星。
    public static HomingFallingStarProjectile spawnAbove(Level level, @Nullable LivingEntity owner, double x, double y, double z) {
        HomingFallingStarProjectile star = new HomingFallingStarProjectile(ModEntities.HOMING_FALLING_STAR.get(), level);
        star.setOwner(owner);
        star.setPos(x, y, z);
        star.setDeltaMovement(0.0, -0.6, 0.0);
        return star;
    }

    @Override
    public void baseTick() {
        super.baseTick();
        LivingEntity target = findNearest();
        if (target != null) {
            Vec3 direction = target.position().add(0.0, target.getBbHeight() * 0.5, 0.0).subtract(position());
            if (direction.lengthSqr() > 1.0E-4) {
                Vec3 desired = direction.normalize().scale(HOMING_SPEED);
                setDeltaMovement(getDeltaMovement().scale(0.35).add(desired.scale(0.65)));
            }
        } else {
            setDeltaMovement(getDeltaMovement().add(0.0, -0.04, 0.0));
        }
        doSimpleMove();
        doAgeCheck(MAX_AGE);
    }

    private @Nullable LivingEntity findNearest() {
        LivingEntity nearest = null;
        double best = HOMING_RANGE * HOMING_RANGE;
        for (LivingEntity candidate : level().getEntitiesOfClass(LivingEntity.class, new AABB(blockPosition()).inflate(HOMING_RANGE))) {
            if (candidate == getOwner() || !candidate.isAlive() || candidate.isSpectator()) continue;
            if (!LibEntityUtils.canHit(getOwner(), candidate)) continue;
            double distance = candidate.distanceToSqr(this);
            if (distance < best) {
                best = distance;
                nearest = candidate;
            }
        }
        return nearest;
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (!level().isClientSide) discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (doPenetrateCheck(entity)) {
            doHurtAndKnockback(entity, 0.3, 0.2);
            doDiscardInMaxPenetrate(1);
        }
    }
}
