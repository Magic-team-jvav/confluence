package org.confluence.mod.client.entity.renderer;

import org.joml.Vector3f;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** 三渲二轮廓。 */
public final class ShieldOutlineProjection {
    private record Point(double x, double y) {}
    private ShieldOutlineProjection() {}

    public static List<Vector3f> outline(Vector3f[] surface, Vector3f eye) {
        float distance = eye.length();
        if (distance < 0.001F) return List.of();
        Vector3f normal = new Vector3f(eye).div(distance);
        Vector3f right = new Vector3f(Math.abs(normal.y) < 0.99F ? new Vector3f(0, 1, 0) : new Vector3f(1, 0, 0)).cross(normal).normalize();
        Vector3f up = new Vector3f(normal).cross(right).normalize();
        List<Point> points = new ArrayList<>(surface.length);
        for (Vector3f vertex : surface) {
            double denominator = distance - vertex.dot(normal);
            // 包围或穿过视点的轮廓没有有限的平面投影。
            if (denominator <= 0.001) return List.of();
            double factor = distance / denominator;
            points.add(new Point(vertex.dot(right) * factor, vertex.dot(up) * factor));
        }
        points.sort(Comparator.comparingDouble(Point::x).thenComparingDouble(Point::y));
        List<Point> unique = new ArrayList<>();
        for (Point p : points) {
            if (unique.isEmpty() || Math.abs(p.x - unique.get(unique.size() - 1).x) > 1.0E-7
                    || Math.abs(p.y - unique.get(unique.size() - 1).y) > 1.0E-7) unique.add(p);
        }
        if (unique.size() < 3) return List.of();
        List<Point> hull = new ArrayList<>();
        for (Point p : unique) append(hull, p, 0);
        int lower = hull.size();
        for (int i = unique.size() - 2; i >= 0; i--) append(hull, unique.get(i), lower - 1);
        hull.remove(hull.size() - 1);
        List<Vector3f> result = new ArrayList<>(hull.size());
        for (Point p : hull) result.add(new Vector3f(right).mul((float) p.x).fma((float) p.y, up));
        return result;
    }

    private static void append(List<Point> hull, Point p, int floor) {
        while (hull.size() >= floor + 2) {
            Point a = hull.get(hull.size() - 2), b = hull.get(hull.size() - 1);
            if ((b.x - a.x) * (p.y - a.y) - (b.y - a.y) * (p.x - a.x) > 1.0E-9) break;
            hull.remove(hull.size() - 1);
        }
        hull.add(p);
    }
}
