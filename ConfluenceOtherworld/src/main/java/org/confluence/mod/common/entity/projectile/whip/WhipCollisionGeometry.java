package org.confluence.mod.common.entity.projectile.whip;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * 鞭子折线的无状态碰撞几何。
 *
 * <p>这里不读取实体或世界状态，只判断目标包围盒是否与当前折线或相邻逻辑帧之间的扫掠区域相交。
 * 独立出来后，服务端攻击实体只负责收集候选和结算伤害，几何边界可以在普通单元测试中精确验证。</p>
 */
final class WhipCollisionGeometry {
    private static final int SWEEP_SUBSTEPS = 4;

    private WhipCollisionGeometry() {}

    static boolean intersectsSweptCurve(
            List<Vec3> previous,
            List<Vec3> current,
            AABB box
    ) {
        if (current.size() < 2) {
            return false;
        }
        if (previous.size() < 2) {
            return intersectsCurve(current, box);
        }
        if (intersectsCurve(previous, box)
                || intersectsCurve(current, box)) {
            return true;
        }

        int segmentCount = Math.max(
                previous.size(), current.size()) - 1;
        for (int segment = 0; segment < segmentCount; segment++) {
            double from = (double) segment / segmentCount;
            double to = (double) (segment + 1) / segmentCount;
            Vec3 previousFrom = samplePolyline(previous, from);
            Vec3 previousTo = samplePolyline(previous, to);
            Vec3 currentFrom = samplePolyline(current, from);
            Vec3 currentTo = samplePolyline(current, to);
            for (int step = 1; step < SWEEP_SUBSTEPS; step++) {
                double progress = (double) step / SWEEP_SUBSTEPS;
                Vec3 sweptFrom = previousFrom.lerp(
                        currentFrom, progress);
                Vec3 sweptTo = previousTo.lerp(currentTo, progress);
                if (intersectsSegment(box, sweptFrom, sweptTo)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean intersectsCurve(
            List<Vec3> points,
            AABB box
    ) {
        for (int index = 1; index < points.size(); index++) {
            if (intersectsSegment(
                    box,
                    points.get(index - 1),
                    points.get(index))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断线段是否接触包围盒，同时覆盖线段整体位于盒内的情况。
     * {@link AABB#clip(Vec3, Vec3)} 只返回线段与盒子边界的交点；当两个端点都已经
     * 位于盒内时不存在“穿入边界”，因此必须先检查端点，否则近距离命中会被漏掉。
     */
    private static boolean intersectsSegment(
            AABB box,
            Vec3 from,
            Vec3 to
    ) {
        return box.contains(from)
                || box.contains(to)
                || box.clip(from, to).isPresent();
    }

    private static Vec3 samplePolyline(
            List<Vec3> points,
            double progress
    ) {
        double scaled = Mth.clamp(progress, 0.0, 1.0)
                * (points.size() - 1);
        int from = Math.min((int) scaled, points.size() - 2);
        return points.get(from).lerp(
                points.get(from + 1), scaled - from);
    }
}
