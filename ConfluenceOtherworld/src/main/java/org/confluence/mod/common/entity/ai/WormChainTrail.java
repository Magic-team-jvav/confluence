package org.confluence.mod.common.entity.ai;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Function;

/// 以头部走过的三维轨迹为基准，按弧长为蠕虫体节取样。
///
/// 旧实现逐节做径向拉回，直线时看似等距，但急转、垂直钻行和网络插值时会形成折线，
/// 产生体节忽远忽近的“手风琴”效果。轨迹取样让所有体节沿同一条连续路径前进，
/// 相邻中心距由真实弧长约束，各种蠕虫只需提供自己的间距。
public final class WormChainTrail {
    // 轨迹点移动不足 0.001 方块时不重复记录，避免静止时队列无限增长。
    private static final double MIN_POINT_DISTANCE_SQR = 1.0E-6D;
    private static final double EPSILON = 1.0E-7D;
    private static final int SMOOTHING_PASSES = 2;

    private final Deque<Vec3> points = new ArrayDeque<>();
    private int seededSegmentCount = -1;
    private Vec3 headTangent = Vec3.ZERO;

    public void invalidate() {
        points.clear();
        seededSegmentCount = -1;
        headTangent = Vec3.ZERO;
    }

    public List<Sample> sample(Vec3 headPosition, List<? extends Entity> segments, double spacing) {
        return sample(headPosition, segments, spacing, Entity::position);
    }

    public List<Sample> sample(Vec3 headPosition, List<? extends Entity> segments, double spacing, Function<Entity, Vec3> positionOf) {
        int count = segments.size();
        if (points.isEmpty() || seededSegmentCount != count) {
            seed(headPosition, segments, positionOf);
        } else {
            Vec3 newest = points.peekFirst();
            double movementSqr = newest == null ? 0.0D : newest.distanceToSqr(headPosition);
            if (newest != null && movementSqr > Math.max(16.0D, spacing * spacing * 9.0D)) {
                shift(headPosition.subtract(newest));
            } else if (newest == null || movementSqr > MIN_POINT_DISTANCE_SQR) {
                points.addFirst(headPosition);
            }
        }

        double safeSpacing = Math.max(0.05D, spacing);
        trim(safeSpacing * (count + 3));
        List<Vec3> curve = buildSmoothCurve();
        double tangentRadius = safeSpacing * 0.5;
        headTangent = headPosition.subtract(sampleDistance(curve, tangentRadius));
        List<Sample> result = new ArrayList<>(count);
        for (int index = 1; index <= count; index++) {
            double distance = index * safeSpacing;
            Vec3 position = sampleDistance(curve, distance);
            Vec3 tangent = sampleDistance(curve, distance - tangentRadius)
                    .subtract(sampleDistance(curve, distance + tangentRadius));
            result.add(new Sample(position, tangent));
        }
        return result;
    }

    private void seed(Vec3 headPosition, List<? extends Entity> segments, Function<Entity, Vec3> positionOf) {
        points.clear();
        points.addLast(headPosition);
        Vec3 previous = headPosition;
        for (Entity segment : segments) {
            Vec3 position = positionOf.apply(segment);
            if (position.distanceToSqr(previous) > MIN_POINT_DISTANCE_SQR) {
                points.addLast(position);
                previous = position;
            }
        }
        seededSegmentCount = segments.size();
    }

    private void shift(Vec3 offset) {
        List<Vec3> shifted = points.stream().map(point -> point.add(offset)).toList();
        points.clear();
        points.addAll(shifted);
    }

    private List<Vec3> buildSmoothCurve() {
        List<Vec3> curve = new ArrayList<>(points);
        for (int pass = 0; pass < SMOOTHING_PASSES && curve.size() >= 3; pass++) {
            List<Vec3> smoothed = new ArrayList<>(curve.size() * 2);
            smoothed.add(curve.get(0));
            for (int index = 0; index + 1 < curve.size(); index++) {
                Vec3 from = curve.get(index);
                Vec3 to = curve.get(index + 1);
                smoothed.add(from.lerp(to, 0.25D));
                smoothed.add(from.lerp(to, 0.75D));
            }
            smoothed.add(curve.get(curve.size() - 1));
            curve = smoothed;
        }
        return curve;
    }

    private Vec3 sampleDistance(List<Vec3> curve, double requestedDistance) {
        if (curve.isEmpty()) return Vec3.ZERO;
        Vec3 previous = curve.get(0);

        double traversed = 0.0D;
        for (Vec3 current : curve) {
            if (current == previous) continue;
            double length = previous.distanceTo(current);
            if (length > EPSILON && traversed + length >= requestedDistance) {
                double progress = (requestedDistance - traversed) / length;
                return previous.lerp(current, progress);
            }
            traversed += length;
            previous = current;
        }

        // 刚生成或结构变化后的短轨迹沿末端切线补足，避免尾部全部挤到最后一点。
        Vec3 last = curve.get(curve.size() - 1);
        if (curve.size() < 2) return last;
        Vec3 beforeLast = curve.get(curve.size() - 2);
        Vec3 tailDirection = last.subtract(beforeLast);
        if (tailDirection.lengthSqr() <= EPSILON) return last;
        return last.add(tailDirection.normalize().scale(requestedDistance - traversed));
    }

    private void trim(double retainedLength) {
        if (points.size() < 3) return;
        double length = 0.0D;
        Vec3 previous = null;
        for (Vec3 point : points) {
            if (previous != null) {
                length += previous.distanceTo(point);
            }
            previous = point;
        }
        while (points.size() > 2) {
            Vec3 last = points.peekLast();
            points.removeLast();
            Vec3 newLast = points.peekLast();
            if (newLast == null) break;
            double edge = newLast.distanceTo(last);
            if (length - edge < retainedLength) {
                points.addLast(last);
                break;
            }
            length -= edge;
        }
    }

    public Vec3 headTangent() {
        return headTangent;
    }

    public record Sample(Vec3 position, Vec3 tangent) {}
}
