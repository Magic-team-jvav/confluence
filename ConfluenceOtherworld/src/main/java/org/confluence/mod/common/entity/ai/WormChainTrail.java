package org.confluence.mod.common.entity.ai;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/// 以头部走过的三维轨迹为基准，按弧长为蠕虫体节取样。
///
/// 旧实现逐节做径向拉回，直线时看似等距，但急转、垂直钻行和网络插值时会形成折线，
/// 产生体节忽远忽近的“手风琴”效果。轨迹取样让所有体节沿同一条连续路径前进，
/// 相邻中心距由真实弧长约束，各种蠕虫只需提供自己的间距。
public final class WormChainTrail {
    // 轨迹点移动不足 0.001 方块时不重复记录，避免静止时队列无限增长。
    private static final double MIN_POINT_DISTANCE_SQR = 1.0E-6D;

    private final Deque<Vec3> points = new ArrayDeque<>();
    private int seededSegmentCount = -1;

    public void invalidate() {
        points.clear();
        seededSegmentCount = -1;
    }

    public List<Sample> sample(Vec3 headPosition, List<? extends Entity> segments, double spacing) {
        int count = segments.size();
        if (points.isEmpty() || seededSegmentCount != count) {
            seed(headPosition, segments);
        } else {
            Vec3 newest = points.peekFirst();
            if (newest == null || newest.distanceToSqr(headPosition) > MIN_POINT_DISTANCE_SQR) {
                points.addFirst(headPosition);
            }
        }

        double safeSpacing = Math.max(0.05D, spacing);
        trim(safeSpacing * (count + 2));
        List<Sample> result = new ArrayList<>(count);
        for (int index = 1; index <= count; index++) {
            double distance = index * safeSpacing;
            Vec3 position = sampleDistance(distance);
            // 用体节目标点前后各 45% 间距的轨迹点作有限差分，使朝向与位置来自同一条曲线。
            double tangentRadius = safeSpacing * 0.45D;
            Vec3 towardHead = sampleDistance(Math.max(0.0D, distance - tangentRadius));
            Vec3 towardTail = sampleDistance(distance + tangentRadius);
            Vec3 tangent = towardHead.subtract(towardTail);
            if (tangent.lengthSqr() <= MIN_POINT_DISTANCE_SQR) {
                tangent = index == 1 ? headPosition.subtract(position) : Vec3.ZERO;
            }
            result.add(new Sample(position, tangent));
        }
        return result;
    }

    private void seed(Vec3 headPosition, List<? extends Entity> segments) {
        points.clear();
        points.addLast(headPosition);
        Vec3 previous = headPosition;
        for (Entity segment : segments) {
            Vec3 position = segment.position();
            if (position.distanceToSqr(previous) > MIN_POINT_DISTANCE_SQR) {
                points.addLast(position);
                previous = position;
            }
        }
        seededSegmentCount = segments.size();
    }

    private Vec3 sampleDistance(double requestedDistance) {
        Vec3 previous = points.peekFirst();
        if (previous == null) return Vec3.ZERO;

        double traversed = 0.0D;
        for (Vec3 current : points) {
            if (current == previous) continue;
            double length = previous.distanceTo(current);
            if (length > 1.0E-7D && traversed + length >= requestedDistance) {
                double progress = (requestedDistance - traversed) / length;
                return previous.lerp(current, progress);
            }
            traversed += length;
            previous = current;
        }

        // 刚生成或结构变化后的短轨迹沿末端切线补足，避免尾部全部挤到最后一点。
        Vec3 last = points.peekLast();
        if (last == null) return previous;
        Vec3 beforeLast = null;
        for (Vec3 point : points) {
            if (point != last) beforeLast = point;
        }
        if (beforeLast == null) return last;
        Vec3 tailDirection = last.subtract(beforeLast);
        if (tailDirection.lengthSqr() <= MIN_POINT_DISTANCE_SQR) return last;
        return last.add(tailDirection.normalize().scale(requestedDistance - traversed));
    }

    private void trim(double retainedLength) {
        if (points.size() < 3) return;
        double length = 0.0D;
        Vec3 previous = null;
        for (Vec3 point : points) {
            if (previous != null) {
                length += previous.distanceTo(point);
                if (length > retainedLength) break;
            }
            previous = point;
        }
        while (length > retainedLength && points.size() > 2) {
            Vec3 last = points.removeLast();
            Vec3 newLast = points.peekLast();
            if (newLast != null) length -= newLast.distanceTo(last);
        }
    }

    public record Sample(Vec3 position, Vec3 tangent) {}
}
