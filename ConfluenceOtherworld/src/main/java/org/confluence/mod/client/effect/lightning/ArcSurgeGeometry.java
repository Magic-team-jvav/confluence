package org.confluence.mod.client.effect.lightning;

import net.minecraft.world.phys.Vec3;
import org.confluence.mod.util.ConductorArcPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/** Warps the skill's cached fractal tree onto the conductor-field guide. */
final class ArcSurgeGeometry {
    private final List<Vec3> guide;
    private final Vec3 normal;
    private final LightningRecipe recipe;
    private final BoltGeometry tree;
    private final double length;
    private final Vec3 penultimate;

    ArcSurgeGeometry(List<Vec3> guide, Vec3 normal, LightningRecipe recipe) {
        this.guide = List.copyOf(guide);
        this.normal = normal.normalize();
        this.recipe = recipe;
        length = Math.max(1.0E-6, guide.get(0).distanceTo(guide.get(guide.size() - 1)));
        // Scale coarse bending with channel length; a fixed 0.3-block offset disappears at range.
        LightningRecipe shape = LightningRecipe.parse(new com.google.gson.Gson().toJsonTree(recipe));
        shape.strikeDisplacement = Math.max(recipe.strikeDisplacement, Math.min(2.4, length * 0.09));
        shape.maxBranchDepth = Math.min(2, recipe.maxBranchDepth);
        tree = BoltGeometry.generateThreeLevel(Vec3.ZERO, new Vec3(0, 0, length), shape);
        penultimate = tree.segments().stream().filter(s -> s.depth() == 0).reduce((a, b) -> b).orElseThrow().a();
    }

    BoltGeometry sample(double ticks, BiFunction<Vec3, Vec3, Vec3> clipper) {
        return sample(ticks, clipper, guide.get(0));
    }

    BoltGeometry sample(double ticks, BiFunction<Vec3, Vec3, Vec3> clipper, Vec3 root) {
        return clip(recipe, unclipped(ticks, root).segments(), clipper);
    }

    BoltGeometry unclipped(double ticks, Vec3 root) {
        List<Vec3> waved = ConductorArcPath.wave(guide, normal, recipe.seed, ticks);
        Vec3[] side = new Vec3[waved.size()];
        Vec3[] up = new Vec3[waved.size()];
        Vec3 previous = null;
        for (int i = 0; i < waved.size(); i++) {
            Vec3 tangent = waved.get(Math.min(i + 1, waved.size() - 1))
                    .subtract(waved.get(Math.max(0, i - 1))).normalize();
            if (tangent.lengthSqr() < 0.5) tangent = new Vec3(0, 0, 1);
            Vec3 a = previous == null ? tangent.cross(normal.lengthSqr() > 0.5 ? normal : new Vec3(0, 1, 0))
                    : previous.subtract(tangent.scale(previous.dot(tangent)));
            if (a.lengthSqr() < 1.0E-8) a = tangent.cross(Math.abs(tangent.x) < 0.9 ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0));
            side[i] = a.normalize();
            up[i] = tangent.cross(side[i]).normalize();
            previous = side[i];
        }
        Map<Vec3, Vec3> mapped = new HashMap<>();
        List<BoltGeometry.Segment> output = new ArrayList<>();
        Vec3 delta = root.subtract(guide.get(0));
        for (var segment : tree.segments()) {
            Vec3 a = mapped.computeIfAbsent(segment.a(), p -> attach(p, warp(p, waved, side, up), root, delta));
            Vec3 b = mapped.computeIfAbsent(segment.b(), p -> attach(p, warp(p, waved, side, up), root, delta));
            output.add(new BoltGeometry.Segment(a, b, segment.widthA(), segment.widthB(), segment.intensity(),
                    segment.alongA(), segment.alongB(), segment.depth(), segment.mask()));
        }
        return BoltGeometry.sampled(recipe, output);
    }

    private Vec3 attach(Vec3 local, Vec3 point, Vec3 root, Vec3 delta) {
        if (local.equals(Vec3.ZERO)) return root;
        if (normal.lengthSqr() > 0.5 && local.equals(penultimate)) return point;
        double t = LightningMath.clamp(local.z / length, 0, 1);
        return point.add(delta.scale((1 - t) * (1 - t)));
    }

    static BoltGeometry clip(LightningRecipe recipe, List<BoltGeometry.Segment> input,
                             BiFunction<Vec3, Vec3, Vec3> clipper) {
        Set<Vec3> visibleRoots = new HashSet<>();
        List<BoltGeometry.Segment> output = new ArrayList<>();
        long path = -1;
        boolean stopped = false;
        for (var segment : input) {
            if (path != segment.mask()) {
                path = segment.mask();
                stopped = segment.depth() > 0 && !visibleRoots.contains(segment.a());
            }
            if (stopped) continue;
            Vec3 a = segment.a();
            Vec3 b = segment.b();
            Vec3 clipped = clipper.apply(a, b);
            double distance = a.distanceTo(b);
            double ratio = distance < 1.0E-9 ? 0 : Math.min(1, a.distanceTo(clipped) / distance);
            visibleRoots.add(segment.a());
            if (a.distanceToSqr(clipped) > 1.0E-12) {
                output.add(new BoltGeometry.Segment(a, clipped, segment.widthA(),
                        (float) (segment.widthA() + (segment.widthB() - segment.widthA()) * ratio),
                        segment.intensity(), segment.alongA(),
                        (float) (segment.alongA() + (segment.alongB() - segment.alongA()) * ratio),
                        segment.depth(), segment.mask()));
            }
            stopped = clipped.distanceToSqr(b) > 1.0E-10;
            if (!stopped) visibleRoots.add(segment.b());
        }
        return BoltGeometry.sampled(recipe, output);
    }

    private Vec3 warp(Vec3 p, List<Vec3> waved, Vec3[] side, Vec3[] up) {
        if (p.equals(Vec3.ZERO)) return waved.get(0);
        if (p.equals(penultimate) && normal.lengthSqr() > 0.5) return waved.get(waved.size() - 2);
        double t = LightningMath.clamp(p.z / length, 0, 1);
        double index = t * (waved.size() - 1);
        int i = Math.min(waved.size() - 2, (int) index);
        double f = index - i;
        Vec3 center = waved.get(i).lerp(waved.get(i + 1), f);
        Vec3 offset = side[i].lerp(side[i + 1], f).normalize().scale(p.x)
                .add(up[i].lerp(up[i + 1], f).normalize().scale(p.y));
        double taper = Math.min(1, t * 12) * Math.min(1, (1 - t) * 12);
        offset = offset.scale(taper);
        if (normal.lengthSqr() > 0.5) {
            double height = Math.max(0, center.subtract(waved.get(waved.size() - 1)).dot(normal));
            double inward = -offset.dot(normal);
            if (inward > height * 0.7 && inward > 1.0E-12) offset = offset.scale(height * 0.7 / inward);
        }
        return center.add(offset);
    }
}
