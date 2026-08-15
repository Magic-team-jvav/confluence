package org.confluence.mod.common.combat.gun;

import net.minecraft.world.phys.Vec3;

/**
 * 使用固定角速度转向，避免按速度分量插值产生抖动。
 */
public final class HomingController {
    private static final double EPSILON = 1.0E-10D;
    private static final Vec3 UP = new Vec3(0.0D, 1.0D, 0.0D);
    private static final Vec3 RIGHT = new Vec3(1.0D, 0.0D, 0.0D);

    private HomingController() {}

    /**
     * 在保持速度大小不变的前提下，最多转过指定弧度。
     */
    public static Vec3 rotateVelocityToward(Vec3 velocity, Vec3 targetOffset, double maxTurnRadians) {
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
        return current.scale(Math.cos(turn))
                .add(turnDirection.scale(Math.sin(turn)))
                .normalize()
                .scale(speed);
    }
}
