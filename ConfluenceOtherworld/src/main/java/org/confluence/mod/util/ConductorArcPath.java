package org.confluence.mod.util;

import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SplittableRandom;

/** Point charge / grounded infinite plane field line, with a separate bounded visual wave. */
public final class ConductorArcPath {
    private ConductorArcPath() {}

    public record Branch(int rootIndex, List<Vec3> points, Vec3 normal, Vec3 planePoint,
                         boolean landsOnPlane, long seed) {}

    /** Decorative channels; their roots are indices into the actual main path. */
    public static List<Branch> branches(List<Vec3> trunk, Vec3 outwardNormal, long seed) {
        double chord = trunk.get(0).distanceTo(trunk.get(trunk.size() - 1));
        if (chord < 0.35 || trunk.size() < 6) return List.of();
        Vec3 normal = outwardNormal.normalize();
        Vec3 plane = trunk.get(trunk.size() - 1);
        SplittableRandom random = new SplittableRandom(seed ^ 0x5A17B4A9L);
        List<Branch> result = new ArrayList<>();
        for (int index = 0; index < 3; index++) {
            double fraction = new double[]{0.28, 0.56, 0.90}[index] + random.nextDouble(-0.045, 0.045);
            int rootIndex = Math.max(2, Math.min(trunk.size() - 3, (int) (fraction * (trunk.size() - 1))));
            Vec3 root = trunk.get(rootIndex);
            Vec3 tangent = trunk.get(rootIndex + 1).subtract(trunk.get(rootIndex - 1)).normalize();
            Vec3 reference = normal.lengthSqr() > 0.5 ? normal : new Vec3(0, 1, 0);
            Vec3 side = tangent.cross(reference);
            if (side.lengthSqr() < 1.0E-8) side = tangent.cross(Math.abs(tangent.x) < 0.9
                    ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0));
            side = side.normalize();
            Vec3 bend = tangent.cross(side).normalize();
            double angle = random.nextDouble() * Math.PI * 2;
            Vec3 spread = side.scale(Math.cos(angle)).add(bend.scale(Math.sin(angle)));
            double length = Math.max(0.3, Math.min(2.8, chord * random.nextDouble(0.10, 0.19)));
            double height = root.subtract(plane).dot(normal);
            boolean surface = normal.lengthSqr() > 0.5 && index == 2 && height < length;
            Vec3 end;
            List<Vec3> points;
            if (surface) {
                Vec3 lateral = spread.subtract(normal.scale(spread.dot(normal))).normalize();
                if (lateral.lengthSqr() < 0.5) lateral = side;
                end = root.subtract(normal.scale(height)).add(lateral.scale(length * 0.65));
                points = create(root, end, normal, 12);
            } else {
                end = root.add(tangent.scale(0.45).add(spread.scale(0.9)).normalize().scale(length));
                end = outsidePlane(end, plane, normal);
                Vec3 control = outsidePlane(root.add(tangent.scale(length * 0.55)), plane, normal);
                List<Vec3> curve = new ArrayList<>();
                for (int i = 0; i <= 12; i++) {
                    double t = i / 12.0;
                    curve.add(interpolate(interpolate(root, control, t), interpolate(control, end, t), t));
                }
                points = List.copyOf(curve);
            }
            result.add(new Branch(rootIndex, points, normal, plane, surface, random.nextLong()));
        }
        return List.copyOf(result);
    }

    public static List<Vec3> animateBranch(Branch branch, Vec3 root, double tick) {
        Vec3 delta = root.subtract(branch.points.get(0));
        List<Vec3> attached = new ArrayList<>();
        for (int i = 0; i < branch.points.size(); i++) {
            double t = i / (double) (branch.points.size() - 1);
            double factor = branch.landsOnPlane ? (i >= branch.points.size() - 2 ? 0 : (1 - t) * (1 - t)) : 1;
            attached.add(outsidePlane(branch.points.get(i).add(delta.scale(factor)), branch.planePoint, branch.normal));
        }
        List<Vec3> waved = wave(attached, branch.landsOnPlane ? branch.normal : Vec3.ZERO, branch.seed, tick);
        List<Vec3> result = new ArrayList<>();
        for (Vec3 point : waved) result.add(outsidePlane(point, branch.planePoint, branch.normal));
        result.set(0, root);
        return List.copyOf(result);
    }

    private static Vec3 outsidePlane(Vec3 point, Vec3 plane, Vec3 normal) {
        double height = point.subtract(plane).dot(normal);
        return height < 0 ? point.subtract(normal.scale(height)) : point;
    }

    public static List<Vec3> create(Vec3 source, Vec3 contact, Vec3 outwardNormal, int segments) {
        segments = Math.max(8, Math.min(96, segments));
        double chord = source.distanceTo(contact);
        Vec3 normal = outwardNormal.normalize();
        // Integrate in contact-relative coordinates, including near the world border.
        Vec3 charge = source.subtract(contact);
        double height = charge.dot(normal);
        if (chord < 1.0E-6 || normal.lengthSqr() < 0.5 || height <= 1.0E-5) {
            return straight(source, contact, segments);
        }
        Vec3 image = charge.subtract(normal.scale(2 * height));
        List<Vec3> reverse = new ArrayList<>();
        Vec3 point = Vec3.ZERO;
        reverse.add(point);
        double step = Math.max(0.005, Math.min(0.25, chord / 160));
        double length = 0;
        boolean arrived = false;
        for (int i = 0; i < 2048 && length <= chord * 5 + 1; i++) {
            if (point.distanceTo(charge) <= step * 1.5) {
                reverse.add(charge);
                arrived = true;
                break;
            }
            // RK4 along -E: start at the chosen point on the plane and trace back to +q.
            Vec3 k1 = reverseField(point, charge, image);
            Vec3 k2 = reverseField(point.add(k1.scale(step * 0.5)), charge, image);
            Vec3 k3 = reverseField(point.add(k2.scale(step * 0.5)), charge, image);
            Vec3 k4 = reverseField(point.add(k3.scale(step)), charge, image);
            Vec3 next = point.add(k1.add(k2.scale(2)).add(k3.scale(2)).add(k4).scale(step / 6));
            if (!finite(next) || next.dot(normal) < -1.0E-7) break;
            length += point.distanceTo(next);
            reverse.add(next);
            point = next;
        }
        List<Vec3> result;
        if (arrived) {
            Collections.reverse(reverse);
            result = resample(reverse, contact, segments);
        } else {
            // Bounded degenerate-case fallback, retaining the same surface-normal boundary.
            Vec3 control = contact.add(normal.scale(Math.max(height, chord * 0.65)));
            result = new ArrayList<>();
            for (int i = 0; i <= segments; i++) {
                double t = i / (double) segments;
                result.add(source.scale((1 - t) * (1 - t))
                        .add(control.scale(2 * t * (1 - t))).add(contact.scale(t * t)));
            }
        }
        result.set(0, source);
        result.set(segments, contact);
        // Preserve an exact normal final segment after finite polyline resampling.
        double tail = Math.min(chord / segments * 0.6, height * 0.5);
        result.set(segments - 1, contact.add(normal.scale(tail)));
        return List.copyOf(result);
    }

    public static List<Vec3> wave(List<Vec3> base, Vec3 outwardNormal, long seed, double tick) {
        Vec3 normal = outwardNormal.normalize();
        Vec3 end = base.get(base.size() - 1);
        double chord = base.get(0).distanceTo(end);
        double amplitude = Math.min(0.16, chord * 0.018);
        SplittableRandom random = new SplittableRandom(seed);
        double phaseA = random.nextDouble() * Math.PI * 2;
        double phaseB = random.nextDouble() * Math.PI * 2;
        List<Vec3> result = new ArrayList<>(base.size());
        for (int i = 0; i < base.size(); i++) {
            Vec3 point = base.get(i);
            // Pin both endpoints and the normal landing segment.
            if (i == 0 || i >= base.size() - 2) {
                result.add(point);
                continue;
            }
            double t = i / (double) (base.size() - 1);
            double envelope = 16 * t * t * (1 - t) * (1 - t);
            double gain = amplitude * envelope;
            if (normal.lengthSqr() > 0.5) {
                gain = Math.min(gain, Math.max(0, point.subtract(end).dot(normal)) * 0.3);
            }
            Vec3 tangent = base.get(i + 1).subtract(base.get(i - 1)).normalize();
            Vec3 reference = normal.lengthSqr() > 0.5 ? normal : new Vec3(0, 1, 0);
            Vec3 side = tangent.cross(reference);
            if (side.lengthSqr() < 1.0E-8) {
                reference = Math.abs(tangent.x) < 0.9 ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0);
                side = tangent.cross(reference);
            }
            side = side.normalize();
            Vec3 bend = tangent.cross(side).normalize();
            double a = 0.65 * Math.sin(t * Math.PI * 6 - tick * 0.7 + phaseA)
                    + 0.35 * Math.sin(t * Math.PI * 14 + tick * 0.95 + phaseB);
            double b = 0.5 * Math.sin(t * Math.PI * 8 - tick * 0.6 + phaseB);
            result.add(point.add(side.scale(gain * a)).add(bend.scale(gain * b)));
        }
        return List.copyOf(result);
    }

    private static Vec3 reverseField(Vec3 point, Vec3 charge, Vec3 image) {
        Vec3 toCharge = charge.subtract(point);
        Vec3 fromImage = point.subtract(image);
        double r = Math.max(1.0E-12, toCharge.lengthSqr());
        double s = Math.max(1.0E-12, fromImage.lengthSqr());
        Vec3 field = toCharge.scale(1 / (r * Math.sqrt(r)))
                .add(fromImage.scale(1 / (s * Math.sqrt(s))));
        // Vec3.normalize() treats weak fields as zero; field strength is irrelevant to tracing.
        double magnitude = field.length();
        return magnitude > 1.0E-20 ? field.scale(1 / magnitude) : Vec3.ZERO;
    }

    private static List<Vec3> straight(Vec3 start, Vec3 end, int segments) {
        List<Vec3> result = new ArrayList<>(segments + 1);
        for (int i = 0; i <= segments; i++) result.add(interpolate(start, end, i / (double) segments));
        return List.copyOf(result);
    }

    private static List<Vec3> resample(List<Vec3> points, Vec3 origin, int segments) {
        double[] length = new double[points.size()];
        for (int i = 1; i < points.size(); i++) {
            length[i] = length[i - 1] + points.get(i).distanceTo(points.get(i - 1));
        }
        List<Vec3> result = new ArrayList<>(segments + 1);
        int edge = 1;
        for (int i = 0; i <= segments; i++) {
            double distance = length[length.length - 1] * i / segments;
            while (edge < length.length - 1 && length[edge] < distance) edge++;
            double span = length[edge] - length[edge - 1];
            double t = span > 1.0E-12 ? (distance - length[edge - 1]) / span : 0;
            result.add(interpolate(points.get(edge - 1), points.get(edge), t).add(origin));
        }
        return result;
    }

    private static boolean finite(Vec3 point) {
        return Double.isFinite(point.x) && Double.isFinite(point.y) && Double.isFinite(point.z);
    }

    private static Vec3 interpolate(Vec3 start, Vec3 end, double t) {
        // Keep this numerical utility independent of Mth's game/logging initialization.
        return new Vec3(start.x + (end.x - start.x) * t,
                start.y + (end.y - start.y) * t, start.z + (end.z - start.z) * t);
    }
}
