package org.confluence.mod.common.entity.monster;

import net.minecraft.world.phys.Vec3;

/** 与 left_aim/right_aim 及 Stand Aim 的固定炮架姿态对应，不依赖客户端骨骼。 */
public final class EngineerTurretGeometry {
    private EngineerTurretGeometry() {}

    public static Vec3 muzzle(Vec3 feet, float yawDegrees, float entityScale, boolean left) {
        double side = left ? 1 : -1;
        // 外侧炮管前端，包含炮管自身的 ±22.5° 模型旋转。
        Vec3 point = new Vec3(side * 11.8097, 18.95671, -8.01);
        point = rotateX(point, new Vec3(side * 10.42388, 16.61732, 5.66667), 25);
        point = rotateX(point, new Vec3(side * 4.5, 12.5, 4.5), -45);
        point = rotateX(point, new Vec3(0.125, 9.25, 0.5), 22.5).add(0, -3.25, 0);
        double unit = 1.3 * entityScale / 16;
        double yaw = Math.toRadians(yawDegrees);
        // Gecko 实体模型相对 Minecraft 身体坐标有 180° 偏航。
        return feet.add((-point.x * Math.cos(yaw) + point.z * Math.sin(yaw)) * unit,
                point.y * unit, (-point.x * Math.sin(yaw) - point.z * Math.cos(yaw)) * unit);
    }

    private static Vec3 rotateX(Vec3 point, Vec3 pivot, double degrees) {
        double angle = Math.toRadians(degrees);
        Vec3 offset = point.subtract(pivot);
        return pivot.add(offset.x, offset.y * Math.cos(angle) - offset.z * Math.sin(angle),
                offset.y * Math.sin(angle) + offset.z * Math.cos(angle));
    }
}
