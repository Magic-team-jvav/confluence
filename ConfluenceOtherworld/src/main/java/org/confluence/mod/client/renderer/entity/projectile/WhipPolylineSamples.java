package org.confluence.mod.client.renderer.entity.projectile;

import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/// 按弧长从鞭子折线上提取模型放置点。
///
/// <p>该类只处理已经确定的世界坐标曲线，不参与挥动轨迹计算。固定间距与固定数量
/// 共用同一份累计弧长数据，避免两种模式在折线转角处使用不同的插值规则。</p>
public final class WhipPolylineSamples {
    private static final double EPSILON = 1.0E-6;

    private WhipPolylineSamples() {}

    public static List<Sample> fixedSpacing(
            List<Vec3> curve,
            double spacing
    ) {
        if (!Double.isFinite(spacing) || spacing <= 0.0) {
            throw new IllegalArgumentException(
                    "Whip segment spacing must be finite and positive");
        }
        ArcLength arc = ArcLength.of(curve);
        if (arc.totalLength() <= EPSILON) {
            return List.of();
        }
        int count = (int) Math.floor(
                (arc.totalLength() + EPSILON) / spacing);
        ArrayList<Sample> result = new ArrayList<>(count);
        for (int index = 1; index <= count; index++) {
            result.add(arc.sample(
                    Math.min(index * spacing, arc.totalLength())));
        }
        return List.copyOf(result);
    }

    public static List<Sample> fixedCount(
            List<Vec3> curve,
            int count
    ) {
        if (count <= 0) {
            throw new IllegalArgumentException(
                    "Whip segment count must be positive");
        }
        ArcLength arc = ArcLength.of(curve);
        if (arc.totalLength() <= EPSILON) {
            return List.of();
        }
        ArrayList<Sample> result = new ArrayList<>(count);
        for (int index = 1; index <= count; index++) {
            result.add(arc.sample(
                    arc.totalLength() * index / count));
        }
        return List.copyOf(result);
    }

    public static Sample tip(List<Vec3> curve) {
        ArcLength arc = ArcLength.of(curve);
        return arc.sample(arc.totalLength());
    }

    public record Sample(Vec3 position, Vec3 tangent) {
        public Sample {
            position = Objects.requireNonNull(
                    position, "Whip sample position must not be null");
            tangent = Objects.requireNonNull(
                    tangent, "Whip sample tangent must not be null");
        }
    }

    private record ArcLength(
            List<Vec3> points,
            double[] cumulative,
            double totalLength
    ) {
        private static ArcLength of(List<Vec3> curve) {
            Objects.requireNonNull(curve, "Whip curve must not be null");
            if (curve.size() < 2) {
                throw new IllegalArgumentException(
                        "Whip curve must contain at least two points");
            }
            List<Vec3> points = List.copyOf(curve);
            double[] cumulative = new double[points.size()];
            for (int index = 1; index < points.size(); index++) {
                cumulative[index] = cumulative[index - 1]
                        + points.get(index).distanceTo(points.get(index - 1));
            }
            return new ArcLength(
                    points, cumulative, cumulative[cumulative.length - 1]);
        }

        private Sample sample(double distance) {
            double target = Math.max(0.0, Math.min(distance, totalLength));
            for (int index = 1; index < points.size(); index++) {
                if (cumulative[index] + EPSILON < target) {
                    continue;
                }
                Vec3 from = points.get(index - 1);
                Vec3 to = points.get(index);
                Vec3 edge = to.subtract(from);
                double edgeLength = edge.length();
                if (edgeLength <= EPSILON) {
                    continue;
                }
                double local = (target - cumulative[index - 1])
                        / edgeLength;
                return new Sample(
                        from.lerp(to, Math.max(0.0, Math.min(local, 1.0))),
                        edge.scale(1.0 / edgeLength));
            }
            Vec3 to = points.get(points.size() - 1);
            for (int index = points.size() - 2; index >= 0; index--) {
                Vec3 edge = to.subtract(points.get(index));
                if (edge.lengthSqr() > EPSILON * EPSILON) {
                    return new Sample(to, edge.normalize());
                }
            }
            return new Sample(to, new Vec3(0.0, 0.0, 1.0));
        }
    }
}
