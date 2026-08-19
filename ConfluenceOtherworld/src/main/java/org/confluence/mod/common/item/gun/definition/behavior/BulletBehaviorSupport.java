package org.confluence.mod.common.item.gun.definition.behavior;

import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;

import java.util.Comparator;

final class BulletBehaviorSupport {
    private static final double RICOCHET_POSITION_EPSILON = 0.02D;

    static boolean isValidTarget(BaseBulletEntity entity, LivingEntity target) {
        Entity owner = entity.getOwner();
        return target != null && target instanceof Enemy && target.isAlive() && entity.canHitTarget(target)
                && (owner == null || !owner.isAlliedTo(target));
    }

    static LivingEntity findNearestTargetInRange(BaseBulletEntity entity, double range) {
        return entity.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(range),
                        candidate -> isValidTarget(entity, candidate) && homingDistance(entity, candidate) <= range)
                .stream().min(Comparator.comparingDouble((LivingEntity candidate) -> homingDistance(entity, candidate))
                        .thenComparingInt(Entity::getId)).orElse(null);
    }

    static double homingDistance(BaseBulletEntity entity, LivingEntity target) {
        Vec3 offset = target.getBoundingBox().getCenter().subtract(entity.position());
        return Math.sqrt(offset.x * offset.x + offset.z * offset.z) + Math.abs(offset.y);
    }

    static void moveOutsideBlock(BaseBulletEntity entity, BlockHitResult result) {
        Direction direction = result.getDirection();
        Vec3 normal = new Vec3(direction.getStepX(), direction.getStepY(), direction.getStepZ());
        double halfExtent = Math.max(entity.getBbWidth(), entity.getBbHeight()) * 0.5D;
        Vec3 safePosition = result.getLocation().add(normal.scale(halfExtent + RICOCHET_POSITION_EPSILON));
        entity.setPos(safePosition.x, safePosition.y, safePosition.z);
    }

    static void explode(BaseBulletEntity entity) {
        if (!entity.level().isClientSide) {
            entity.level().explode(entity, entity.getX(), entity.getY(), entity.getZ(), 2.0F, Level.ExplosionInteraction.NONE);
        }
    }

    static void applyEffect(Entity target, MobEffect effect, int duration, int amplifier) {
        if (target instanceof LivingEntity living && !living.level().isClientSide) {
            living.addEffect(new MobEffectInstance(effect, duration, amplifier));
        }
    }

    private BulletBehaviorSupport() {}
}
