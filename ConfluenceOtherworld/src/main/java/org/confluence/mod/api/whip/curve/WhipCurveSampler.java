package org.confluence.mod.api.whip.curve;

import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/// 将当前时间点的鞭身控制点转换为近似等弧长折线。
///
/// 先生成平滑曲线，再按累计弧长重新采样，保证模型分段间距和碰撞采样稳定。
public final class WhipCurveSampler {
    private static final int DENSE_STEPS_PER_SPAN = 16;
    private static final int MIN_SEGMENTS = 8;
    private static final double PARAMETER_EPSILON = 1.0E-5;

    private WhipCurveSampler() {}

    public static List<Vec3> sample(WhipCurve curve, double progress, double scale, double segmentSpacing) {
        if (scale <= 0.0) {
            throw new IllegalArgumentException("Whip curve scale must be positive");
        }
        if (segmentSpacing <= 0.0) {
            throw new IllegalArgumentException("Whip segment spacing must be positive");
        }
        List<Vec3> source = curve.controlPoints(progress);
        if (source == null || source.size() < 2) {
            throw new IllegalArgumentException("Whip curve must provide at least two control points");
        }
        ArrayList<Vec3> controls = new ArrayList<>(source.size());
        for (Vec3 point : source) {
            if (point == null) {
                throw new IllegalArgumentException("Whip curve control points cannot contain null");
            }
            controls.add(point.scale(scale));
        }

        List<Vec3> dense = createDenseCurve(controls);
        double totalLength = length(dense);
        if (totalLength <= PARAMETER_EPSILON) {
            return List.of(dense.get(0), dense.get(dense.size() - 1));
        }

        int segmentCount = Math.max((int) Math.ceil(totalLength / segmentSpacing), MIN_SEGMENTS);
        return resampleByArcLength(dense, totalLength, segmentCount);
    }

    /// 将已经完成世界坐标变换的控制点重新采样成平滑折线。
    public static List<Vec3> sampleControlPoints(List<Vec3> source, double segmentSpacing) {
        if (segmentSpacing <= 0.0) {
            throw new IllegalArgumentException("Whip segment spacing must be positive");
        }
        if (source.size() < 2) {
            throw new IllegalArgumentException("Whip curve must provide at least two control points");
        }
        ArrayList<Vec3> controls = new ArrayList<>(source.size());
        for (Vec3 point : source) {
            if (point == null) {
                throw new IllegalArgumentException("Whip curve control points cannot contain null");
            }
            controls.add(point);
        }

        List<Vec3> dense = createDenseCurve(controls);
        double totalLength = length(dense);
        if (totalLength <= PARAMETER_EPSILON) {
            return List.of(dense.get(0), dense.get(dense.size() - 1));
        }
        int segmentCount = Math.max((int) Math.ceil(totalLength / segmentSpacing), MIN_SEGMENTS);
        return resampleByArcLength(dense, totalLength, segmentCount);
    }

    private static List<Vec3> createDenseCurve(List<Vec3> controls) {
        ArrayList<Vec3> dense = new ArrayList<>((controls.size() - 1) * DENSE_STEPS_PER_SPAN + 1);
        for (int span = 0; span < controls.size() - 1; span++) {
            Vec3 p1 = controls.get(span);
            Vec3 p2 = controls.get(span + 1);
            Vec3 p0 = span > 0 ? controls.get(span - 1) : p1;
            Vec3 p3 = span + 2 < controls.size()
                    ? controls.get(span + 2)
                    : p2;
            for (int step = 0; step < DENSE_STEPS_PER_SPAN; step++) {
                double t = (double) step / DENSE_STEPS_PER_SPAN;
                dense.add(uniformCatmullRom(p0, p1, p2, p3, t));
            }
        }
        dense.add(controls.get(controls.size() - 1));
        return dense;
    }

    /// 普通 Catmull-Rom 曲线。
    private static Vec3 uniformCatmullRom(Vec3 p0, Vec3 p1, Vec3 p2, Vec3 p3, double progress) {
        double t2 = progress * progress;
        double t3 = t2 * progress;
        return p1.scale(2.0)
                .add(p2.subtract(p0).scale(progress))
                .add(p0.scale(2.0)
                        .subtract(p1.scale(5.0))
                        .add(p2.scale(4.0))
                        .subtract(p3)
                        .scale(t2))
                .add(p0.scale(-1.0)
                        .add(p1.scale(3.0))
                        .subtract(p2.scale(3.0))
                        .add(p3)
                        .scale(t3))
                .scale(0.5);
    }

    private static double length(List<Vec3> points) {
        double result = 0.0;
        for (int index = 1; index < points.size(); index++) {
            result += points.get(index).distanceTo(points.get(index - 1));
        }
        return result;
    }

    private static List<Vec3> resampleByArcLength(List<Vec3> dense, double totalLength, int segmentCount) {
        ArrayList<Vec3> result = new ArrayList<>(segmentCount + 1);
        result.add(dense.get(0));
        double spacing = totalLength / segmentCount;
        double walked = 0.0;
        int denseIndex = 1;
        Vec3 previous = dense.get(0);

        for (int segment = 1; segment < segmentCount; segment++) {
            double targetDistance = spacing * segment;
            while (denseIndex < dense.size()) {
                Vec3 next = dense.get(denseIndex);
                double edgeLength = previous.distanceTo(next);
                if (walked + edgeLength >= targetDistance) {
                    double local = edgeLength <= PARAMETER_EPSILON
                            ? 0.0
                            : (targetDistance - walked) / edgeLength;
                    result.add(previous.lerp(next, local));
                    break;
                }
                walked += edgeLength;
                previous = next;
                denseIndex++;
            }
        }
        result.add(dense.get(dense.size() - 1));
        return result;
    }
}
