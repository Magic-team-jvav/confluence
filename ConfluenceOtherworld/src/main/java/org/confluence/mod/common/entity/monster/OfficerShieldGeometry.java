package org.confluence.mod.common.entity.monster;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** 渲染用圆角盒与其接触体积共用的尺寸。 */
public final class OfficerShieldGeometry {
    public static final float HALF_WIDTH = 0.78F;
    // 将底部抬高一个模型像素，防摩尔纹，同时保持顶部在 2.245 格。
    public static final float BOTTOM_Y = 1.0F / 16;
    public static final float HALF_HEIGHT = (2.245F - BOTTOM_Y) / 2;
    public static final float RADIUS = 0.22F;
    public static final float CENTER_Y = BOTTOM_Y + HALF_HEIGHT;

    private OfficerShieldGeometry() {}

    public static AABB bounds(Vec3 position, double scale) {
        Vec3 center = position.add(0, CENTER_Y * scale, 0);
        return new AABB(center.x - HALF_WIDTH * scale, center.y - HALF_HEIGHT * scale, center.z - HALF_WIDTH * scale,
                center.x + HALF_WIDTH * scale, center.y + HALF_HEIGHT * scale, center.z + HALF_WIDTH * scale);
    }

    /** 目标包围盒按盒子平坦核心膨胀后到扫掠中心的距离。
     * 剩余的球半径用于给出实际的圆角边缘与棱角。 */
    public static boolean intersects(Vec3 start, Vec3 end, double scale, AABB target) {
        double radius = RADIUS * scale;
        AABB expanded = target.inflate((HALF_WIDTH - RADIUS) * scale,
                (HALF_HEIGHT - RADIUS) * scale, (HALF_WIDTH - RADIUS) * scale);
        Vec3 a = start.add(0, CENTER_Y * scale, 0), b = end.add(0, CENTER_Y * scale, 0);
        if (distanceSquared(a, expanded) <= radius * radius || distanceSquared(b, expanded) <= radius * radius) return true;
        // 到凸盒的平方距离沿线段也是凸的。
        double low = 0, high = 1;
        for (int i = 0; i < 48; i++) {
            double left = (2 * low + high) / 3, right = (low + 2 * high) / 3;
            if (distanceSquared(a.lerp(b, left), expanded) <= distanceSquared(a.lerp(b, right), expanded)) high = right;
            else low = left;
        }
        return distanceSquared(a.lerp(b, (low + high) / 2), expanded) <= radius * radius + 1.0E-9;
    }

    private static double distanceSquared(Vec3 p, AABB box) {
        double x = Math.max(box.minX - p.x, Math.max(0, p.x - box.maxX));
        double y = Math.max(box.minY - p.y, Math.max(0, p.y - box.maxY));
        double z = Math.max(box.minZ - p.z, Math.max(0, p.z - box.maxZ));
        return x * x + y * y + z * z;
    }
}
