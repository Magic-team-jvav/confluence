package org.confluence.mod.client.renderer.entity.projectile;

import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// 缓存一条鞭子曲线的累计弧长，供所有外观层共同采样。
final class WhipPolylineSamples {
    private static final double EPSILON = 1.0E-6;
    private final List<Vec3> points;
    private final double[] cumulative;
    private final double totalLength;

    private WhipPolylineSamples(List<Vec3> curve) {
        Objects.requireNonNull(curve, "Whip curve must not be null");
        if (curve.size() < 2) {
            throw new IllegalArgumentException("Whip curve must contain at least two points");
        }
        points = List.copyOf(curve);
        cumulative = new double[points.size()];
        for (int index = 1; index < points.size(); index++) {
            cumulative[index] = cumulative[index - 1] + points.get(index).distanceTo(points.get(index - 1));
        }
        totalLength = cumulative[cumulative.length - 1];
    }

    static WhipPolylineSamples of(List<Vec3> curve) {
        return new WhipPolylineSamples(curve);
    }

    Vec3 start() {
        return points.get(0);
    }

    List<Sample> fixedSpacing(double spacing) {
        if (!Double.isFinite(spacing) || spacing <= 0.0) {
            throw new IllegalArgumentException("Whip segment spacing must be finite and positive");
        }
        if (totalLength <= EPSILON) return List.of();
        int count = (int) Math.ceil(totalLength / spacing);
        ArrayList<Sample> result = new ArrayList<>(count);
        for (int index = 1; index <= count; index++) {
            result.add(sample(Math.min(index * spacing, totalLength)));
        }
        return result;
    }

    List<Sample> fixedCount(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Whip segment count must be positive");
        }
        if (totalLength <= EPSILON) return List.of();
        ArrayList<Sample> result = new ArrayList<>(count);
        for (int index = 1; index <= count; index++) {
            result.add(sample(totalLength * index / count));
        }
        return result;
    }

    Sample tip() {
        return sample(totalLength);
    }

    private Sample sample(double distance) {
        double target = Math.max(0.0, Math.min(distance, totalLength));
        for (int index = 1; index < points.size(); index++) {
            if (cumulative[index] + EPSILON < target) continue;
            Vec3 from = points.get(index - 1);
            Vec3 edge = points.get(index).subtract(from);
            double edgeLength = edge.length();
            if (edgeLength <= EPSILON) continue;
            double progress = (target - cumulative[index - 1]) / edgeLength;
            return new Sample(from.add(edge.scale(Math.max(0.0, Math.min(progress, 1.0)))));
        }
        return new Sample(points.get(points.size() - 1));
    }

    record Sample(Vec3 position) {}
}
