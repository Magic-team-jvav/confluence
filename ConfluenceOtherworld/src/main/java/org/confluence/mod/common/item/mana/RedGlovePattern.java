package org.confluence.mod.common.item.mana;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.util.ConductorArcPath;

import java.util.*;
import java.util.function.BiFunction;

/** Common/server-safe discharge geometry. Every transmitted line is also an attack line. */
public final class RedGlovePattern {
    public static final double RANGE = 32, HALF_ANGLE = 20, HIT_RADIUS = 0.16;
    public static final int RANDOM_TICKS = 3, PULSE_TICKS = 2;
    public record Target(int id, Vec3 point, AABB bounds) {}
    public record Path(int depth, int parent, int rootIndex, List<Vec3> points) {
        public Path {
            points = List.copyOf(points);
            if (depth < 0 || depth > 2 || points.size() < 2 || points.size() > 96)
                throw new IllegalArgumentException("Invalid red glove path");
        }
    }
    private record Root(int parent, int vertex, double cost) {}
    private RedGlovePattern() {}

    public static int trunkCount(long seed) { return new SplittableRandom(seed).nextInt(5, 9); }

    public static boolean inRange(Vec3 source, Vec3 look, Vec3 point) {
        Vec3 delta = point.subtract(source);
        if (delta.lengthSqr() < 1.0E-10) return true;
        return delta.lengthSqr() <= RANGE * RANGE
                && delta.normalize().dot(look.normalize()) >= Math.cos(Math.toRadians(HALF_ANGLE));
    }

    public static List<Path> create(Vec3 source, Vec3 look, long seed, List<Target> targets,
                                    BiFunction<Vec3, Vec3, Vec3> clipper) {
        return create(source, look, seed, targets, clipper, (a, b) -> Vec3.ZERO);
    }
    public static List<Path> create(Vec3 source, Vec3 look, long seed, List<Target> targets,
                                    BiFunction<Vec3, Vec3, Vec3> clipper,
                                    BiFunction<Vec3, Vec3, Vec3> normalAt) {
        List<Path> paths = new ArrayList<>();
        var random = new SplittableRandom(seed);
        int count = random.nextInt(5, 9);
        Vec3 forward = look.normalize(), side = perpendicular(forward), up = forward.cross(side).normalize();
        double rotation = random.nextDouble(0, Math.PI * 2);
        for (int i = 0; i < count; i++) {
            double angle = rotation + i * Math.PI * 2 / count + random.nextDouble(-0.16, 0.16);
            double radius = Math.sqrt((i + 0.3 + random.nextDouble(0.4)) / count) * Math.tan(Math.toRadians(HALF_ANGLE));
            Vec3 direction = forward.add(side.scale(Math.cos(angle) * radius)).add(up.scale(Math.sin(angle) * radius)).normalize();
            Vec3 intended = source.add(direction.scale(RANGE)), hit = clipper.apply(source, intended);
            Vec3 normal = hit.distanceToSqr(intended) > 1.0E-8 ? normalAt.apply(source, intended) : Vec3.ZERO;
            List<Vec3> guide;
            if (normal.lengthSqr() > 0.5) {
                guide = new ArrayList<>(ConductorArcPath.wave(ConductorArcPath.create(source, hit, normal, 32), normal, seed + i * 7919L, 0));
                var fractal = jagged(source, hit, 32, seed + i * 7919L, Math.min(2, Math.max(0.2, source.distanceTo(hit) * 0.06)));
                for (int vertex = 1; vertex < guide.size() - 2; vertex++) {
                    Vec3 offset = fractal.get(vertex).subtract(source.lerp(hit, vertex / 32.0));
                    double height = Math.max(0, guide.get(vertex).subtract(hit).dot(normal));
                    double inward = -offset.dot(normal);
                    if (inward > height * 0.7 && inward > 1.0E-10) offset = offset.scale(height * 0.7 / inward);
                    guide.set(vertex, guide.get(vertex).add(offset));
                }
            } else guide = jagged(source, intended, 32, seed + i * 7919L, 1.92);
            List<Vec3> points = clipped(guide, clipper);
            paths.add(new Path(0, -1, -1, points));
        }
        // A very narrow aperture can stop every random trunk right at the muzzle.
        // Keep the same 5..8 count, but guide one existing trunk through a verified opening.
        if (paths.stream().noneMatch(path -> !roots(path.points).isEmpty())) {
            for (Target target : targets) {
                if (target.point.distanceToSqr(source) < 1.0E-8 || !inRange(source, forward, target.point)
                        || !clear(source, target.point, clipper)) continue;
                Vec3 direction = target.point.subtract(source).normalize();
                paths.set(0, new Path(0, -1, -1, clipped(straight(source, source.add(direction.scale(RANGE)), 32), clipper)));
                break;
            }
        }
        int twigs = 0;
        for (Target target : targets) {
            if (!inRange(source, forward, target.point) || intersects(paths, target.bounds, 0)) continue;
            if (!clear(source, target.point, clipper)) continue;
            List<Root> roots = new ArrayList<>();
            for (int p = 0; p < paths.size(); p++) {
                Path parent = paths.get(p);
                if (parent.depth > 1 || parent.depth == 1 && twigs >= 2) continue;
                if (parent.depth == 1 && parent.points.get(parent.points.size() - 1).distanceTo(target.point) > 2.75) continue;
                for (int vertex : roots(parent.points)) {
                    Vec3 root = parent.points.get(vertex);
                    roots.add(new Root(p, vertex, root.distanceTo(target.point) + (parent.depth == 0 ? 0.3 : 0)));
                }
            }
            roots.sort(Comparator.comparingDouble(Root::cost));
            Path branch = null;
            for (Root root : roots) {
                var parent = paths.get(root.parent);
                Vec3 start = parent.points.get(root.vertex);
                if (!clear(start, target.point, clipper)) continue;
                List<Vec3> points = clipped(jagged(start, target.point, 8, seed ^ target.id, 0.17), clipper);
                if (points.get(points.size() - 1).distanceToSqr(target.point) > 1.0E-8)
                    points = clipped(straight(start, target.point, 8), clipper);
                if (points.get(points.size() - 1).distanceToSqr(target.point) < 1.0E-8) {
                    branch = new Path(parent.depth + 1, root.parent, root.vertex, points); break;
                }
            }
            if (branch == null) {
                // A doorway may hide every lateral junction. Follow the already-clear parent
                // back to the source, then the verified source-to-target line; never tunnel.
                Root root = roots.stream().filter(r -> paths.get(r.parent).depth == 0).findFirst().orElse(null);
                if (root != null) {
                    var parent = paths.get(root.parent);
                    List<Vec3> route = new ArrayList<>();
                    for (int i = root.vertex; i >= 0; i--) route.add(parent.points.get(i));
                    route.addAll(straight(source, target.point, 8).subList(1, 9));
                    var checked = clipped(route, clipper);
                    if (checked.get(checked.size() - 1).distanceToSqr(target.point) < 1.0E-8)
                        branch = new Path(1, root.parent, root.vertex, checked);
                }
            }
            if (branch != null) { paths.add(branch); if (branch.depth == 2) twigs++; }
        }
        // Only two small terminal twigs for the entire cast; target coverage has priority.
        for (int i = twigs; i < 2; i++) {
            int parentId = i % count;
            Path parent = paths.get(parentId);
            List<Integer> roots = roots(parent.points);
            if (roots.isEmpty()) continue;
            int vertex = roots.get(roots.size() / 2);
            Vec3 root = parent.points.get(vertex);
            Vec3 tip = root.add(forward.scale(1.2)).add(side.scale(i % 2 == 0 ? 0.7 : -0.7)).add(up.scale(0.25));
            var branchPoints = clipped(jagged(root, tip, 4, seed + 301 + i, 0.12), clipper);
            int branchId = paths.size();
            paths.add(new Path(1, parentId, vertex, branchPoints));
            List<Integer> twigRoots = roots(branchPoints);
            if (twigRoots.isEmpty()) continue;
            int v = twigRoots.get(twigRoots.size() / 2);
            Vec3 a = branchPoints.get(v);
            paths.add(new Path(2, branchId, v, clipped(jagged(a, a.add(up.scale(0.5)).add(forward.scale(0.4)), 4,
                    seed + 601 + i, 0.07), clipper)));
        }
        return List.copyOf(paths);
    }

    public static boolean intersects(List<Path> paths, AABB bounds) {
        return intersects(paths, bounds, HIT_RADIUS);
    }
    public static boolean intersects(List<Path> paths, AABB bounds, double radius) {
        AABB hitbox = bounds.inflate(radius);
        for (Path path : paths) for (int i = 1; i < path.points.size(); i++) {
            Vec3 a = path.points.get(i - 1), b = path.points.get(i);
            if (a.distanceToSqr(b) < 1.0E-12) continue;
            if (hitbox.contains(a) || hitbox.contains(b) || hitbox.clip(a, b).isPresent()) return true;
        }
        return false;
    }

    /** Roots are chosen by true polyline arc length, not by an uneven vertex index. */
    public static List<Integer> roots(List<Vec3> points) {
        double length = length(points), distance = 0;
        List<Integer> result = new ArrayList<>();
        if (length < 1.0E-6) return result;
        for (int i = 1; i < points.size() - 1; i++) {
            distance += points.get(i - 1).distanceTo(points.get(i));
            if (distance >= length * 0.25 && distance <= length * 0.75) result.add(i);
        }
        return result;
    }

    public static double length(List<Vec3> points) {
        double length = 0;
        for (int i = 1; i < points.size(); i++) length += points.get(i - 1).distanceTo(points.get(i));
        return length;
    }

    private static boolean clear(Vec3 a, Vec3 b, BiFunction<Vec3, Vec3, Vec3> clipper) {
        return clipper.apply(a, b).distanceToSqr(b) < 1.0E-8;
    }

    private static List<Vec3> clipped(List<Vec3> points, BiFunction<Vec3, Vec3, Vec3> clipper) {
        List<Vec3> result = new ArrayList<>(); result.add(points.get(0));
        for (int i = 1; i < points.size(); i++) {
            Vec3 end = clipper.apply(result.get(result.size() - 1), points.get(i));
            result.add(end);
            if (end.distanceToSqr(points.get(i)) > 1.0E-8) break;
        }
        if (result.size() < 2) result.add(result.get(0));
        if (result.size() == 2 && result.get(0).distanceToSqr(result.get(1)) > 1.0E-12)
            result.add(1, result.get(0).lerp(result.get(1), 0.5));
        return List.copyOf(result);
    }

    private static List<Vec3> jagged(Vec3 a, Vec3 b, int segments, long seed, double amplitude) {
        var random = new SplittableRandom(seed);
        Vec3 axis = b.subtract(a).normalize(), side = perpendicular(axis), up = axis.cross(side).normalize();
        List<Vec3> result = new ArrayList<>(List.of(a, b));
        // Deterministic midpoint displacement, matching the skill's fixed-endpoint topology.
        while (result.size() - 1 < segments) {
            List<Vec3> refined = new ArrayList<>(); refined.add(result.get(0));
            for (int i = 1; i < result.size(); i++) {
                Vec3 previous = result.get(i - 1), next = result.get(i);
                double limit = Math.min(amplitude, previous.distanceTo(next) * 0.28);
                refined.add(previous.lerp(next, 0.5).add(side.scale(random.nextDouble(-1, 1) * limit))
                        .add(up.scale(random.nextDouble(-1, 1) * limit * 0.7)));
                refined.add(next);
            }
            result = refined; amplitude *= 0.6;
        }
        double length = a.distanceTo(b);
        for (int i = 1; i < result.size() - 1; i++) {
            double t = i / (double) (result.size() - 1);
            Vec3 center = a.lerp(b, t), delta = result.get(i).subtract(center);
            delta = delta.subtract(axis.scale(delta.dot(axis)));
            double limit = length * 0.2 * Math.sin(Math.PI * t);
            if (delta.length() > limit) delta = delta.scale(limit / delta.length());
            result.set(i, center.add(delta));
        }
        return result;
    }

    private static List<Vec3> straight(Vec3 a, Vec3 b, int segments) {
        List<Vec3> result = new ArrayList<>();
        for (int i = 0; i <= segments; i++) result.add(a.lerp(b, i / (double) segments));
        return result;
    }

    private static Vec3 perpendicular(Vec3 axis) {
        return axis.cross(Math.abs(axis.y) < 0.9 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0)).normalize();
    }
}
