package org.confluence.mod.common.entity.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/// 按实际位移和最小转弯半径限制头部转向，不修改体节位置。
public final class WormSteering {
    private WormSteering() {}

    public static Vec3 turn(Vec3 current, Vec3 desired, double maxRadians) {
        if (desired.lengthSqr() < 1.0E-10) return Vec3.ZERO;
        if (current.lengthSqr() < 1.0E-10) return desired.normalize();
        current = current.normalize();
        desired = desired.normalize();
        double dot = Mth.clamp(current.dot(desired), -1.0, 1.0);
        if (dot >= Math.cos(maxRadians)) return desired;
        Vec3 tangent = desired.subtract(current.scale(dot));
        if (tangent.lengthSqr() < 1.0E-10) {
            Vec3 axis = Math.abs(current.y) < 0.9 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
            tangent = current.cross(axis);
        }
        return current.scale(Math.cos(maxRadians)).add(tangent.normalize().scale(Math.sin(maxRadians))).normalize();
    }

    public static Vec3 limit(Vec3 previousDirection, Vec3 movement, double radius, double maxRadians) {
        double speed = movement.length();
        if (speed < 1.0E-7) return Vec3.ZERO;
        return turn(previousDirection, movement, Math.min(maxRadians, speed / radius)).scale(speed);
    }
}
