package org.confluence.mod.common.item.mana;

import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.function.BiFunction;

/** Incremental, obstacle-aware Euclidean Steiner heuristic; no global-optimality claim. */
final class RedGloveSteinerTree {
    static final int MAX_CHILDREN = 2;
    static final double MAX_BRANCH_REACH = 4;
    private static final int EDGE_CANDIDATES = 8, MEDIAN_ITERATIONS = 24;
    private static final double JUNCTION_SPACING = 0.3;

    private static final class Node {
        final Vec3 point;
        final int anchorPath, anchorVertex;
        final double bendBias;
        final List<Node> children = new ArrayList<>(2);
        Node parent;
        int terminals, load, family, path = -1;
        Node(Vec3 point, int anchorPath, int anchorVertex, double bendBias) {
            this.point = point; this.anchorPath = anchorPath; this.anchorVertex = anchorVertex; this.bendBias = bendBias;
            family = anchorPath;
        }
        Node(Vec3 point) { this(point, -1, -1, 0); }
        void attach(Node child) { children.add(child); child.parent = this; child.family = family; }
    }
    private record Edge(Node a, Node b, double distance) {}
    private record Insertion(Node a, Node b, Vec3 junction, double cost) {}

    private RedGloveSteinerTree() {}

    static void connect(List<RedGlovePattern.Path> paths, Vec3 source, Vec3 look, long seed,
                        List<RedGlovePattern.Target> targets, BiFunction<Vec3, Vec3, Vec3> clipper) {
        var visible = targets.stream().filter(t -> RedGlovePattern.inRange(source, look, t.point())
                && clear(source, t.point(), clipper)).toList();
        connect(paths, source, look, seed, visible, clipper,
                RedGlovePattern.distributeTargets(source, look, paths.size(), visible));
    }

    static void connect(List<RedGlovePattern.Path> paths, Vec3 source, Vec3 look, long seed,
                        List<RedGlovePattern.Target> targets, BiFunction<Vec3, Vec3, Vec3> clipper,
                        RedGlovePattern.TargetDistribution distribution) {
        var groups = distribution.groups();
        List<Node> nodes = new ArrayList<>();
        int backboneCount = paths.size();
        Map<Integer, Integer> owners = new HashMap<>();
        for (int family = 0; family < groups.size(); family++)
            for (var target : groups.get(family)) owners.put(target.id(), family);
        int[] familyLoads = new int[backboneCount];
        List<List<RedGlovePattern.Target>> foreignTargets = new ArrayList<>();
        for (var group : groups) foreignTargets.add(targets.stream().filter(t -> !group.contains(t)).toList());
        for (int p = 0; p < backboneCount; p++) {
            var points = paths.get(p).points();
            for (int vertex : RedGlovePattern.roots(points)) {
                nodes.add(new Node(points.get(vertex), p, vertex, RedGlovePattern.rootCost(points, vertex, 0) * 0.05));
            }
        }
        var ordered = targets.stream().filter(t -> RedGlovePattern.inRange(source, look, t.point()))
                .sorted(Comparator.comparingDouble((RedGlovePattern.Target t) -> t.point().distanceToSqr(source))
                        .thenComparingInt(RedGlovePattern.Target::id)).toList();
        for (var target : ordered) {
            Vec3 point = target.point();
            int family = owners.getOrDefault(target.id(), -1);
            if (family < 0) continue;
            if (!clear(source, point, clipper)) continue;
            Node covered = null;
            for (Node node : nodes) if (node.family == family
                    && (target.bounds().contains(node.point) || node.point.distanceToSqr(point) < 1.0E-12)) {
                covered = node; break;
            }
            if (covered != null) { covered.terminals++; credit(covered); familyLoads[family]++; continue; }
            if (RedGlovePattern.intersects(List.of(paths.get(family)), target.bounds(), 0)) {
                familyLoads[family]++; continue;
            }

            Node bestParent = null;
            double bestCost = Double.POSITIVE_INFINITY;
            for (Node node : nodes) {
                if (node.family != family || node.children.size() >= MAX_CHILDREN || reservedAnchor(node, nodes)) continue;
                double cost = node.point.distanceTo(point) + node.bendBias + 0.5 * node.load;
                if (node.point.distanceToSqr(point) > MAX_BRANCH_REACH * MAX_BRANCH_REACH) continue;
                if (cost < bestCost && clear(node.point, point, clipper)) { bestCost = cost; bestParent = node; }
            }

            // Scan all edges cheaply; only the closest bounded shortlist needs Fermat/LOS work.
            List<Edge> closest = new ArrayList<>(EDGE_CANDIDATES + 1);
            for (Node child : nodes) if (child.family == family && child.parent != null) {
                Edge edge = new Edge(child.parent, child, segmentDistance(point, child.parent.point, child.point));
                int index = 0;
                while (index < closest.size() && closest.get(index).distance <= edge.distance) index++;
                if (index < EDGE_CANDIDATES) {
                    closest.add(index, edge);
                    if (closest.size() > EDGE_CANDIDATES) closest.remove(EDGE_CANDIDATES);
                }
            }
            Insertion bestInsertion = null;
            for (Edge edge : closest) {
                Vec3 a = edge.a.point, b = edge.b.point;
                double oldLength = a.distanceTo(b);
                if (oldLength < JUNCTION_SPACING * 2) continue;
                Vec3 junction = geometricMedian(a, b, point);
                if (junction.distanceTo(point) > 1.0E-5
                        && (junction.distanceTo(a) < JUNCTION_SPACING || junction.distanceTo(b) < JUNCTION_SPACING)) {
                    double t = junction.subtract(a).dot(b.subtract(a)) / (oldLength * oldLength);
                    double margin = JUNCTION_SPACING / oldLength;
                    junction = a.lerp(b, Math.max(margin, Math.min(1 - margin, t)));
                }
                boolean duplicate = false;
                for (Node node : nodes) if (node.family == family && node != edge.a && node != edge.b
                        && node.point.distanceToSqr(junction) < JUNCTION_SPACING * JUNCTION_SPACING) {
                    duplicate = true; break;
                }
                if (duplicate) continue;
                // A cheap added length must not turn a remote target into a long, thin twig.
                // Every replacement arm stays local, including the moved existing connection.
                if (junction.distanceTo(a) > MAX_BRANCH_REACH || junction.distanceTo(b) > MAX_BRANCH_REACH
                        || junction.distanceTo(point) > MAX_BRANCH_REACH) continue;
                double cost = Math.max(0, a.distanceTo(junction) + b.distanceTo(junction) + point.distanceTo(junction) - oldLength)
                        + 0.5 * edge.b.load;
                if (cost >= bestCost || !clear(a, junction, clipper) || !clear(junction, b, clipper)
                        || !clear(junction, point, clipper)) continue;
                bestCost = cost;
                bestInsertion = new Insertion(edge.a, edge.b, junction, cost);
            }
            if (bestInsertion != null) {
                // Replace the old edge, rather than drawing it again under the new shared feeder.
                var insertion = bestInsertion;
                Node junction = new Node(insertion.junction);
                junction.load = insertion.b.load;
                insertion.a.children.remove(insertion.b);
                insertion.a.attach(junction); junction.attach(insertion.b); nodes.add(junction);
                if (junction.point.distanceToSqr(point) < 1.0E-10) { junction.terminals++; credit(junction); }
                else {
                    Node terminal = new Node(point); terminal.terminals = 1;
                    junction.attach(terminal); nodes.add(terminal); credit(terminal);
                }
            } else if (bestParent != null) {
                Node terminal = new Node(point); terminal.terminals = 1;
                bestParent.attach(terminal); nodes.add(terminal); credit(terminal);
            } else {
                // Remote targets and disconnected visibility pockets get their own full upstream current.
                var line = checkedLine(source, point, seed ^ target.id(), clipper);
                line = RedGlovePattern.avoidForeignHits(source, point, line, seed ^ target.id(), distribution.axis(),
                        (family - (backboneCount - 1) * 0.5) * 5, foreignTargets.get(family), clipper);
                int path;
                if (familyLoads[family] == 0) {
                    // Reuse the idle current instead of leaving it empty beside a new target-seeking trunk.
                    path = family;
                    paths.set(path, new RedGlovePattern.Path(0, -1, -1, line));
                    nodes.removeIf(node -> node.family == family);
                } else {
                    path = paths.size();
                    paths.add(new RedGlovePattern.Path(0, -1, -1, line));
                }
                Node upstream = new Node(point, path, line.size() - 1, 0);
                upstream.family = family; upstream.terminals = 1; upstream.load = 1; nodes.add(upstream);
            }
            familyLoads[family]++;
        }

        List<Node> order = new ArrayList<>(nodes.size());
        ArrayDeque<Node> queue = new ArrayDeque<>();
        for (Node node : nodes) if (node.parent == null) queue.addLast(node);
        while (!queue.isEmpty()) { Node node = queue.removeFirst(); order.add(node); queue.addAll(node.children); }
        for (int i = order.size() - 1; i >= 0; i--) {
            Node node = order.get(i); node.load = node.terminals;
            for (Node child : node.children) node.load += child.load;
        }
        long edgeSeed = seed;
        for (Node node : order) {
            if (node.parent == null) continue;
            Node parent = node.parent;
            int parentPath = parent.parent == null ? parent.anchorPath : parent.path;
            int vertex = parent.parent == null ? parent.anchorVertex : paths.get(parentPath).points().size() - 1;
            // Depth is a material band, not a limit on logical Steiner hierarchy.
            int band = parent.parent == null || node.load >= 4 ? 1 : 2;
            node.path = paths.size();
            paths.add(new RedGlovePattern.Path(band, parentPath, vertex,
                    checkedLine(parent.point, node.point, edgeSeed += 7919, clipper)));
        }
    }

    private static boolean reservedAnchor(Node candidate, List<Node> nodes) {
        if (candidate.anchorPath < 0 || !candidate.children.isEmpty()) return false;
        for (Node node : nodes) if (node != candidate && node.family == candidate.family
                && node.anchorPath >= 0 && !node.children.isEmpty()
                && node.point.distanceToSqr(candidate.point) < 0.75 * 0.75) return true;
        return false;
    }

    private static void credit(Node node) {
        for (Node cursor = node; cursor != null; cursor = cursor.parent) cursor.load++;
    }

    /** Fixed-iteration Weiszfeld approximation to the three-terminal Fermat/Steiner junction. */
    static Vec3 geometricMedian(Vec3 a, Vec3 b, Vec3 c) {
        Vec3 point = a.add(b).add(c).scale(1.0 / 3);
        for (int iteration = 0; iteration < MEDIAN_ITERATIONS; iteration++) {
            double da = point.distanceTo(a), db = point.distanceTo(b), dc = point.distanceTo(c);
            if (da < 1.0E-8) return a;
            if (db < 1.0E-8) return b;
            if (dc < 1.0E-8) return c;
            double wa = 1 / da, wb = 1 / db, wc = 1 / dc;
            Vec3 next = a.scale(wa).add(b.scale(wb)).add(c.scale(wc)).scale(1 / (wa + wb + wc));
            if (next.distanceToSqr(point) < 1.0E-12) return next;
            point = next;
        }
        return point;
    }

    private static double segmentDistance(Vec3 p, Vec3 a, Vec3 b) {
        Vec3 delta = b.subtract(a);
        double t = Math.max(0, Math.min(1, p.subtract(a).dot(delta) / Math.max(1.0E-12, delta.lengthSqr())));
        return p.distanceToSqr(a.add(delta.scale(t)));
    }

    private static boolean clear(Vec3 a, Vec3 b, BiFunction<Vec3, Vec3, Vec3> clipper) {
        return clipper.apply(a, b).distanceToSqr(b) < 1.0E-8;
    }

    private static List<Vec3> checkedLine(Vec3 a, Vec3 b, long seed, BiFunction<Vec3, Vec3, Vec3> clipper) {
        double length = a.distanceTo(b);
        int segments = length > 8 ? 32 : length > 2 ? 8 : 4;
        double amplitude = Math.min(1.92, Math.max(0.07, length * 0.06));
        for (int attempt = 0; attempt < 4; attempt++) {
            var line = RedGlovePattern.jagged(a, b, segments, seed, amplitude);
            boolean clear = true;
            for (int i = 1; i < line.size(); i++) if (!clear(line.get(i - 1), line.get(i), clipper)) {
                clear = false; break;
            }
            if (clear) return List.copyOf(line);
            amplitude *= 0.5;
        }
        // Only genuinely narrow visibility corridors need an exact straight edge.
        return List.of(a, a.lerp(b, 0.5), b);
    }
}
