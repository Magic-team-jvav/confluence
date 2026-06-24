package org.confluence.mod.common.entity.ai.keyframe;

import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class FrameUtil {

    // 在两个关键帧之间进行线性插值
    public static double linearInterpolateBetween(Keyframe kf0, Keyframe kf1, double t) {
        double t0 = kf0.time;
        double t1 = kf1.time;
        double v0 = kf0.value;
        double v1 = kf1.value;
        return v0 + (v1 - v0) * (t - t0) / (t1 - t0);
    }

    // Catmull-Rom样条插值，t 在 [0, 1] 之间
    public static Vec3 interpolate(List<Vec3> points, double t, int index) {
        Vec3 p0 = index > 0 ? points.get(index - 1) : points.get(0);
        Vec3 p1 = points.get(index);
        Vec3 p2 = points.get(index + 1);
        Vec3 p3 = index + 2 < points.size() ? points.get(index + 2) : points.get(points.size() - 1);

        double alpha = 0.5; // 可以调整 alpha 来控制曲线的平滑度

        double x = 0.5 * ((2 * p1.x) +
                (-p0.x + p2.x) * t +
                (2 * p0.x - 5 * p1.x + 4 * p2.x - p3.x) * t * t +
                (-p0.x + 3 * p1.x - 3 * p2.x + p3.x) * t * t * t);

        double y = 0.5 * ((2 * p1.y) +
                (-p0.y + p2.y) * t +
                (2 * p0.y - 5 * p1.y + 4 * p2.y - p3.y) * t * t +
                (-p0.y + 3 * p1.y - 3 * p2.y + p3.y) * t * t * t);

        double z = 0.5 * ((2 * p1.z) +
                (-p0.z + p2.z) * t +
                (2 * p0.z - 5 * p1.z + 4 * p2.z - p3.z) * t * t +
                (-p0.z + 3 * p1.z - 3 * p2.z + p3.z) * t * t * t);

        return new Vec3(x, y, z);
    }

    // 获取插值后的点列表
    public static List<Vec3> getInterpolatedPoints(List<Vec3> points, int numSegments) {
        List<Vec3> interpolatedPoints = new ArrayList<>();

        for (int i = 0; i < points.size() - 1; i++) {
            for (int j = 0; j < numSegments; j++) {
                double t = (double) j / numSegments;
                interpolatedPoints.add(interpolate(points, t, i));
            }
        }

        // 移除多余的点（最后一个段会重复添加最后一个点）
        interpolatedPoints.remove(interpolatedPoints.size() - 1);

        return interpolatedPoints;
    }

}
