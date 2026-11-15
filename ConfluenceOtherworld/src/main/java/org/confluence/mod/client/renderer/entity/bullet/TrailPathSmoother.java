package org.confluence.mod.client.renderer.entity.bullet;

import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/// Builds a render-only quadratic Bezier curve from spatial trail samples.
final class TrailPathSmoother {
    private static final int SUBDIVISIONS = 5;
    private static final double DUPLICATE_EPSILON_SQR = 1.0E-8D;

    private TrailPathSmoother() {}

    static List<Vec3> smooth(List<Vec3> history, Vec3 head, int maxControlPoints) {
        int start = Math.max(0, history.size() - Math.max(1, maxControlPoints - 1));
        List<Vec3> controls = new ArrayList<>(Math.min(maxControlPoints, history.size() + 1));
        for (int index = start; index < history.size(); index++)
            appendDistinct(controls, history.get(index));
        appendDistinct(controls, head);
        if (controls.size() > maxControlPoints) controls.remove(0);
        if (controls.size() < 2) return controls;
        List<Vec3> curve = quadraticBezierChain(controls);
        curve.set(curve.size() - 1, head);
        return curve;
    }

    private static List<Vec3> quadraticBezierChain(List<Vec3> controls) {
        List<Vec3> curve = new ArrayList<>((controls.size() - 1) * SUBDIVISIONS + 1);
        curve.add(controls.get(0));
        for (int index = 0; index < controls.size() - 1; index++) {
            Vec3 start = index == 0 ? controls.get(0)
                    : midpoint(controls.get(index - 1), controls.get(index));
            Vec3 control = controls.get(index);
            Vec3 end = index + 1 == controls.size() - 1 ? controls.get(controls.size() - 1)
                    : midpoint(controls.get(index), controls.get(index + 1));
            for (int sample = 1; sample <= SUBDIVISIONS; sample++) {
                appendDistinct(curve, quadraticBezier(start, control, end, sample / (double) SUBDIVISIONS));
            }
        }
        return curve;
    }

    private static Vec3 quadraticBezier(Vec3 start, Vec3 control, Vec3 end, double value) {
        double inverse = 1.0D - value;
        return start.scale(inverse * inverse).add(control.scale(2.0D * inverse * value)).add(end.scale(value * value));
    }

    private static Vec3 midpoint(Vec3 first, Vec3 second) {return first.add(second).scale(0.5D);}

    private static void appendDistinct(List<Vec3> points, Vec3 point) {
        if (points.isEmpty() || points.get(points.size() - 1).distanceToSqr(point) > DUPLICATE_EPSILON_SQR) {
            points.add(point);
        }
    }
}
