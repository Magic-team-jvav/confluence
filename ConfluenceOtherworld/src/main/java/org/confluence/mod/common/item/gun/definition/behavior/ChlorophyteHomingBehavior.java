package org.confluence.mod.common.item.gun.definition.behavior;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.combat.gun.HomingController;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;

import java.util.Comparator;

public final class ChlorophyteHomingBehavior extends AbstractBulletBehavior {
    public static final ChlorophyteHomingBehavior INSTANCE = new ChlorophyteHomingBehavior();
    private static final double FORWARD_RANGE = 18.75D;
    private static final double HALF_WIDTH = 6.0D;
    private static final double HALF_HEIGHT = 5.0D;
    private static final double TURN_RATE = Math.toRadians(14.0D);

    private ChlorophyteHomingBehavior() {
        super("tooltip.confluence.ability.chlorophyte_homing");
    }

    @Override
    public void tick(BaseBulletEntity entity) {
        Vec3 velocity = entity.getDeltaMovement();
        if (velocity.lengthSqr() < 1.0E-10D) return;
        LivingEntity target;
        if (entity.level().isClientSide) {
            target = entity.getHomingTarget();
        } else {
            target = findTarget(entity);
            entity.setHomingTarget(target);
        }
        if (!BulletBehaviorSupport.isValidTarget(entity, target) || !isInsideRectangle(entity, target, velocity.normalize()))
            return;
        Vec3 toTarget = target.getBoundingBox().getCenter().subtract(entity.position());
        entity.setDeltaMovement(HomingController.rotateVelocityToward(velocity, toTarget, TURN_RATE));
    }

    private static LivingEntity findTarget(BaseBulletEntity entity) {
        Vec3 velocity = entity.getDeltaMovement();
        if (velocity.lengthSqr() < 1.0E-10D) return null;
        Vec3 direction = velocity.normalize();
        AABB searchBox = entity.getBoundingBox().expandTowards(direction.scale(FORWARD_RANGE)).inflate(HALF_WIDTH + HALF_HEIGHT + 1.0D);
        return entity.level().getEntitiesOfClass(LivingEntity.class, searchBox,
                        candidate -> BulletBehaviorSupport.isValidTarget(entity, candidate) && isInsideRectangle(entity, candidate, direction))
                .stream().min(Comparator.comparingDouble((LivingEntity candidate) -> entity.position()
                                .distanceToSqr(candidate.getBoundingBox().getCenter()))
                        .thenComparingInt(Entity::getId)).orElse(null);
    }

    private static boolean isInsideRectangle(BaseBulletEntity entity, LivingEntity target, Vec3 direction) {
        Vec3 offset = target.getBoundingBox().getCenter().subtract(entity.position());
        double forward = offset.dot(direction);
        double targetWidth = target.getBbWidth() * 0.5D;
        double targetHeight = target.getBbHeight() * 0.5D;
        if (forward < -targetWidth || forward > FORWARD_RANGE + targetWidth) return false;
        Vec3 side = direction.cross(new Vec3(0.0D, 1.0D, 0.0D));
        if (side.lengthSqr() <= 1.0E-10D) side = direction.cross(new Vec3(1.0D, 0.0D, 0.0D));
        side = side.normalize();
        Vec3 vertical = side.cross(direction).normalize();
        return Math.abs(offset.dot(side)) <= HALF_WIDTH + targetWidth
                && Math.abs(offset.dot(vertical)) <= HALF_HEIGHT + targetHeight;
    }
}
