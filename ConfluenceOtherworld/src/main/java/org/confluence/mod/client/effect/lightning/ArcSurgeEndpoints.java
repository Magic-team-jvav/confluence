package org.confluence.mod.client.effect.lightning;

import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.function.BiFunction;

/** Endpoint dressing is added AFTER collision; the original hit position never moves. */
final class ArcSurgeEndpoints {
    record Contact(long path, Vec3 normal) {}
    private final double[] angles = new double[4];

    ArcSurgeEndpoints(long seed) {
        var random = new SplittableRandom(seed ^ 0x71C0FFEE);
        for (int i = 0; i < angles.length; i++) angles[i] = i * Math.PI / 2 + random.nextDouble(-0.3, 0.3);
    }

    BoltGeometry apply(BoltGeometry visible, LightningRecipe recipe, List<Contact> contacts, double age,
                       BiFunction<Vec3, Vec3, Vec3> clipper) {
        return apply(visible, recipe, contacts, age, clipper, true);
    }
    BoltGeometry apply(BoltGeometry visible, LightningRecipe recipe, List<Contact> contacts, double age,
                       BiFunction<Vec3, Vec3, Vec3> clipper, boolean petals) {
        Map<Long, List<BoltGeometry.Segment>> paths = new LinkedHashMap<>();
        for (var s : visible.segments()) paths.computeIfAbsent(s.mask(), ignored -> new ArrayList<>()).add(s);
        Set<Long> targets = new HashSet<>();
        for (var c : contacts) targets.add(c.path);
        List<BoltGeometry.Segment> output = new ArrayList<>(recipe.maxSegments);
        Map<Long, Vec3> anchors = new HashMap<>();
        for (var entry : paths.entrySet()) {
            var path = entry.getValue();
            if (!targets.contains(entry.getKey())) { output.addAll(path); continue; }
            double length = path.stream().mapToDouble(s -> s.a().distanceTo(s.b())).sum(), distance = 0;
            boolean emitter = path.get(0).depth() == 0;
            for (int i = 0; i < path.size(); i++) {
                var s = path.get(i);
                double span = s.a().distanceTo(s.b());
                float wa = s.widthA() * taper(distance, length, emitter);
                float wb = s.widthB() * taper(distance + span, length, emitter);
                if (i == path.size() - 1 && span > 1.0E-5
                        && visible.segments().size() + contacts.size() <= recipe.maxSegments) {
                    // Put the contact flare on the incident side, as an actual parent vertex.
                    double t = 1 - Math.min(0.5, 0.035 / span);
                    Vec3 anchor = s.a().lerp(s.b(), t);
                    float w = wa + (wb - wa) * (float) t;
                    float along = s.alongA() + (s.alongB() - s.alongA()) * (float) t;
                    output.add(segment(s.a(), anchor, wa, w, s.intensity(), s.alongA(), along, s.depth(), s.mask()));
                    output.add(segment(anchor, s.b(), w, wb, s.intensity(), along, s.alongB(), s.depth(), s.mask()));
                    anchors.put(s.mask(), anchor);
                } else output.add(segment(s.a(), s.b(), wa, wb, s.intensity(), s.alongA(), s.alongB(), s.depth(), s.mask()));
                distance += span;
            }
        }
        long next = paths.keySet().stream().mapToLong(Long::longValue).max().orElse(0) + 1;
        Set<Vec3> emitted = new HashSet<>();
        for (var contact : contacts) {
            var path = paths.get(contact.path);
            if (path == null || path.isEmpty()) continue; // Occluded target: no orphan flash.
            var first = path.get(0); var last = path.get(path.size() - 1);
            if (first.depth() == 0 && emitted.add(first.a())) {
                Vec3 forward = first.b().subtract(first.a()).normalize();
                star(output, first.a(), forward, 0.09, 0.045f, first.alongA(), 1, next++, age, false, clipper, recipe.maxSegments);
            }
            Vec3 anchor = anchors.get(contact.path);
            if (anchor == null) continue;
            Vec3 inward = last.a().subtract(last.b()).normalize();
            Vec3 normal = contact.normal.lengthSqr() > 0.5 && inward.dot(contact.normal) > 0.7 ? contact.normal : inward;
            int depth = Math.min(2, last.depth() + 1);
            star(output, anchor, normal, 0.11, 0.055f, last.alongB(), depth, next++, age, false, clipper, recipe.maxSegments);
            if (petals) star(output, anchor, normal, 0.36, 0.013f, last.alongB(), depth, next++, age, true, clipper, recipe.maxSegments);
        }
        return BoltGeometry.sampled(recipe, output);
    }

    static float taper(double distance, double length, boolean source) {
        double entry = source ? 0.10 + 0.90 * LightningMath.smooth(distance / Math.min(0.35, length * 0.2)) : 1;
        double exit = 0.12 + 0.88 * LightningMath.smooth((length - distance) / Math.min(0.22, length * 0.2));
        return (float) (entry * exit);
    }

    /** Return each ray to its root so all rays use one MAX-unioned path, not one FBO pass per ray. */
    private void star(List<BoltGeometry.Segment> output, Vec3 root, Vec3 normal, double radius, float width,
                      float along, int depth, long mask, double age, boolean forks,
                      BiFunction<Vec3, Vec3, Vec3> clipper, int budget) {
        Vec3 side = normal.cross(Math.abs(normal.y) < 0.9 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0)).normalize();
        Vec3 up = normal.cross(side).normalize();
        double pulse = 1 + 0.15 * Math.sin(age * 9 + angles[0]);
        int rays = forks ? 4 : 3, steps = forks ? 3 : 1;
        for (int ray = 0; ray < rays; ray++) {
            if (output.size() + steps * 2 > budget) break;
            double angle = forks ? angles[ray] : ray * Math.PI * 2 / 3;
            Vec3 direction = side.scale(Math.cos(angle)).add(up.scale(Math.sin(angle)));
            List<Vec3> points = new ArrayList<>(); points.add(root);
            for (int step = 1; step <= steps; step++) {
                double t = step / (double) steps;
                Vec3 target = root.add(direction.scale(radius * pulse * t))
                        .add(normal.scale(radius * (forks ? 0.20 : 0.08) * t))
                        .add(side.scale(forks ? Math.sin(step * 5 + angle) * radius * 0.15 * Math.sin(Math.PI * t) : 0));
                Vec3 previous = points.get(points.size() - 1), clipped = clipper.apply(previous, target);
                if (previous.distanceToSqr(clipped) > 1.0E-12) points.add(clipped);
                if (clipped.distanceToSqr(target) > 1.0E-10) break;
            }
            for (int i = 0; i < points.size() - 1; i++) {
                float a = width * (1 - i / (float) (points.size() - 1));
                float b = width * (1 - (i + 1f) / (points.size() - 1));
                output.add(segment(points.get(i), points.get(i + 1), a, b, forks ? 0.8f : 1.6f, along, along, depth, mask));
            }
            for (int i = points.size() - 1; i > 0; i--) {
                float a = width * (1 - i / (float) (points.size() - 1));
                float b = width * (1 - (i - 1f) / (points.size() - 1));
                output.add(segment(points.get(i), points.get(i - 1), a, b, forks ? 0.8f : 1.6f, along, along, depth, mask));
            }
        }
    }

    private static BoltGeometry.Segment segment(Vec3 a, Vec3 b, float wa, float wb, float intensity,
                                                 float alongA, float alongB, int depth, long mask) {
        return new BoltGeometry.Segment(a, b, wa, wb, intensity, alongA, alongB, depth, mask);
    }
}
