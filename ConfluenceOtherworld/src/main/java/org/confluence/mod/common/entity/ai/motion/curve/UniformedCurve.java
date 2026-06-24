package org.confluence.mod.common.entity.ai.motion.curve;

import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.api.entity.animation.Curve;

import java.util.ArrayList;
import java.util.List;

public abstract class UniformedCurve implements Curve {

    private List<Double> arcLengths; // 存储累积弧长
    private double totalLength;      // 总弧长
    private static final int SEGMENTS = 1000; // 分段数量

    // 预计算弧长表
    protected void precomputeArcLengths() {
        arcLengths = new ArrayList<>();
        arcLengths.add(0.0);
        Vec3 previousPoint = cal(0);
        double length = 0.0;

        for (int i = 1; i <= SEGMENTS; i++) {
            double t = (double) i / SEGMENTS;
            Vec3 currentPoint = cal(t);
            double dx = currentPoint.x - previousPoint.x;
            double dy = currentPoint.y - previousPoint.y;
            double dz = currentPoint.z - previousPoint.z;
            length += Math.sqrt(dx * dx + dy * dy + dz * dz);
            arcLengths.add(length);
            previousPoint = currentPoint;
        }
        totalLength = length;
    }

    // 根据弧长比例查找对应的参数t
    private double findTByArcLength(double s) {
        double targetLength = s * totalLength;
        int low = 0, high = SEGMENTS;
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (arcLengths.get(mid) < targetLength) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }
        if (low == 0) return 0.0;
        double prevLength = arcLengths.get(low - 1);
        double nextLength = arcLengths.get(low);
        double segmentLength = nextLength - prevLength;
        double segmentProgress = targetLength - prevLength;
        double t = (low - 1 + segmentProgress / segmentLength) / SEGMENTS;
        return Math.min(t, 1.0);
    }

    // 实现匀速运动：根据弧长比例s计算点
    @Override
    public Vec3 calUniformed(double s) {
        double t = findTByArcLength(s);
        return cal(t);
    }
}
