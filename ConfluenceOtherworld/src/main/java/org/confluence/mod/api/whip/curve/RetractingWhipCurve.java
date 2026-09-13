package org.confluence.mod.api.whip.curve;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/// 从完整下抽姿态沿下方弧线平滑收回鞭身。
public final class RetractingWhipCurve implements WhipCurve {
    private final WhipCurve outbound;
    private final double returnStart;
    private final List<Vec3> returnPoints;

    public RetractingWhipCurve(WhipCurve outbound, double returnStart) {
        if (returnStart <= 0.0 || returnStart >= 1.0)
            throw new IllegalArgumentException("Return start must be between zero and one");
        this.outbound = outbound;
        this.returnStart = returnStart;
        this.returnPoints = List.copyOf(outbound.controlPoints(returnStart));
    }

    public boolean isReturning(double progress) {
        return progress > returnStart;
    }

    public double returnStart() {
        return returnStart;
    }

    @Override
    public List<Vec3> controlPoints(double progress) {
        double time = Mth.clamp(progress, 0.0, 1.0);
        if (time <= returnStart) return outbound.controlPoints(time);
        double t = (time - returnStart) / (1.0 - returnStart);
        Vec3 root = returnPoints.get(0);
        double remaining = 1.0 - smootherstep(t);
        double downwardArc = -0.18 * Math.sin(Math.PI * t);
        ArrayList<Vec3> result = new ArrayList<>(returnPoints.size());
        result.add(root);
        for (int index = 1; index < returnPoints.size(); index++) {
            double along = (double) index / (returnPoints.size() - 1);
            Vec3 start = returnPoints.get(index).subtract(root);
            double drop = downwardArc * Math.pow(along, 1.5);
            result.add(root.add(start.scale(remaining)).add(0.0, drop, 0.0));
        }
        return List.copyOf(result);
    }

    private static double smootherstep(double value) {
        return value * value * value * (value * (value * 6.0 - 15.0) + 10.0);
    }
}
