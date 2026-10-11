package org.confluence.mod.common.item.gun.definition.behavior;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils.TargetPolicy;
import org.confluence.lib.util.LibEntityUtils.WeaponTargeting;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;

import java.util.Comparator;

public final class ChlorophyteHomingBehavior extends AbstractBulletBehavior {
    public static final ChlorophyteHomingBehavior INSTANCE = new ChlorophyteHomingBehavior();
    private static final double EPSILON = 1.0E-10D;
    private static final Vec3 UP = new Vec3(0.0D, 1.0D, 0.0D);
    private static final Vec3 RIGHT = new Vec3(1.0D, 0.0D, 0.0D);
    private static final double FORWARD_RANGE = 18.75D;
    private static final double HALF_WIDTH = 6.0D;
    private static final double HALF_HEIGHT = 5.0D;
    private static final double TURN_RATE = Math.toRadians(14.0D);

    private ChlorophyteHomingBehavior() {
        super("tooltip.confluence.ability.chlorophyte_homing", new WeaponTargeting(TargetPolicy.ENEMY_ONLY));
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
        if (!BulletBehaviorSupport.canKeepTarget(entity, target) || !isInsideRectangle(entity, target, velocity.normalize()))
            return;
        Vec3 toTarget = target.getBoundingBox().getCenter().subtract(entity.position());
        entity.setDeltaMovement(rotateVelocityToward(velocity, toTarget, TURN_RATE));
    }

    private static LivingEntity findTarget(BaseBulletEntity entity) {
        Vec3 velocity = entity.getDeltaMovement();
        if (velocity.lengthSqr() < 1.0E-10D) return null;
        Vec3 direction = velocity.normalize();
        AABB searchBox = entity.getBoundingBox().expandTowards(direction.scale(FORWARD_RANGE)).inflate(HALF_WIDTH + HALF_HEIGHT + 1.0D);
        return LibEntityUtils.nearest(entity.level().getEntitiesOfClass(LivingEntity.class, searchBox),
                candidate -> BulletBehaviorSupport.canAcquireTarget(entity, candidate) && isInsideRectangle(entity, candidate, direction),
                Comparator.comparingDouble((LivingEntity candidate) -> entity.position()
                        .distanceToSqr(candidate.getBoundingBox().getCenter())).thenComparingInt(Entity::getId));
    }

    private static boolean isInsideRectangle(BaseBulletEntity entity, LivingEntity target, Vec3 direction) {
        return LibEntityUtils.insideForwardRectangle(entity.position(), direction, target,
                FORWARD_RANGE, HALF_WIDTH, HALF_HEIGHT);
    }

    /// 限制每次转向的角度，同时保持射弹速度不变。
    private static Vec3 rotateVelocityToward(Vec3 velocity, Vec3 targetOffset, double maxTurnRadians) {
        double speed = velocity.length();
        if (speed <= EPSILON || targetOffset.lengthSqr() <= EPSILON || maxTurnRadians <= 0.0D) {
            return velocity;
        }

        Vec3 current = velocity.scale(1.0D / speed);
        Vec3 desired = targetOffset.normalize();
        double dot = Math.max(-1.0D, Math.min(1.0D, current.dot(desired)));
        double angle = Math.acos(dot);
        if (angle <= maxTurnRadians) {
            return desired.scale(speed);
        }

        Vec3 turnDirection = desired.subtract(current.scale(dot));
        if (turnDirection.lengthSqr() <= EPSILON) {
            Vec3 reference = Math.abs(current.y) < 0.9D ? UP : RIGHT;
            turnDirection = reference.subtract(current.scale(reference.dot(current)));
        }
        turnDirection = turnDirection.normalize();
        double turn = Math.min(Math.PI, maxTurnRadians);
        return current.scale(Math.cos(turn)).add(turnDirection.scale(Math.sin(turn))).normalize().scale(speed);
    }
}
