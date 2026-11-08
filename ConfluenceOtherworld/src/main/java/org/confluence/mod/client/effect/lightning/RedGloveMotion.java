package org.confluence.mod.client.effect.lightning;

import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.item.mana.RedGlovePattern;

import java.util.ArrayList;
import java.util.List;

/** Cached tree coordinates; only presentation moves, never the authoritative damage snapshot. */
final class RedGloveMotion {
    private record Channel(RedGlovePattern.Path path, double[] distance, double[] follow) {}
    private final List<Channel> channels = new ArrayList<>();
    private double length;

    RedGloveMotion(List<RedGlovePattern.Path> paths) {
        for (var path : paths) {
            var points = path.points();
            double[] distance = new double[points.size()], follow = new double[points.size()];
            double rootFollow = 1;
            if (path.parent() >= 0 && path.parent() < channels.size()) {
                Channel parent = channels.get(path.parent());
                int root = path.rootIndex();
                if (root < 0 || root >= parent.distance.length) throw new IllegalArgumentException("Invalid arc junction");
                distance[0] = parent.distance[root];
                rootFollow = parent.follow[root];
                // Shared endpoints are sent as doubles, intermediate vertices as relative floats.
                // Resolve the common point once instead of allowing a quantization seam.
                var canonical = new ArrayList<>(points);
                canonical.set(0, parent.path.points().get(root));
                path = new RedGlovePattern.Path(path.depth(), path.parent(), root, canonical);
                points = path.points();
            }
            for (int i = 1; i < points.size(); i++)
                distance[i] = distance[i - 1] + points.get(i - 1).distanceTo(points.get(i));
            double span = distance[distance.length - 1] - distance[0];
            for (int i = 0; i < points.size(); i++)
                follow[i] = rootFollow * (span > 1.0E-9 ? 1 - (distance[i] - distance[0]) / span : 0);
            length = Math.max(length, distance[distance.length - 1]);
            channels.add(new Channel(path, distance, follow));
        }
    }

    BoltGeometry sample(LightningRecipe recipe, double age, Vec3 displacement) {
        List<BoltGeometry.Segment> segments = new ArrayList<>();
        if (age < 0 || age >= ArcSurgeLightning.RED_GLOVE_LIFETIME_TICKS) return BoltGeometry.sampled(recipe, segments);
        double cutoff = length * Math.max(0, age - ArcSurgeLightning.RED_GLOVE_HOLD_TICKS)
                / ArcSurgeLightning.RED_GLOVE_RETRACT_TICKS;
        for (int mask = 0; mask < channels.size(); mask++) {
            Channel channel = channels.get(mask);
            var path = channel.path;
            var points = path.points();
            float scale = path.depth() == 0 ? 1 : path.depth() == 1 ? 0.70f : 0.38f;
            for (int i = 1; i < points.size(); i++) {
                double da = channel.distance[i - 1], db = channel.distance[i];
                if (db <= cutoff || db - da < 1.0E-9) continue;
                double t = Math.max(0, (cutoff - da) / (db - da));
                Vec3 a = points.get(i - 1).add(displacement.scale(channel.follow[i - 1]));
                Vec3 b = points.get(i).add(displacement.scale(channel.follow[i]));
                float alongA = (i - 1f) / (points.size() - 1), alongB = i / (float) (points.size() - 1);
                float width = (float) recipe.strikeWidth * scale;
                double start = channel.distance[0], span = channel.distance[channel.distance.length - 1] - start;
                float wa = width * ArcSurgeEndpoints.taper(da - start, span, path.depth() == 0);
                float wb = width * ArcSurgeEndpoints.taper(db - start, span, path.depth() == 0);
                segments.add(new BoltGeometry.Segment(a.lerp(b, t), b, wa + (wb - wa) * (float) t, wb, scale,
                        alongA + (alongB - alongA) * (float) t, alongB, path.depth(), mask));
            }
        }
        return BoltGeometry.sampled(recipe, segments);
    }

    List<RedGlovePattern.Path> particlePaths(Vec3 displacement) {
        List<RedGlovePattern.Path> result = new ArrayList<>();
        for (Channel channel : channels) {
            List<Vec3> points = new ArrayList<>();
            for (int i = 0; i < channel.follow.length; i++)
                points.add(channel.path.points().get(i).add(displacement.scale(channel.follow[i])));
            result.add(new RedGlovePattern.Path(channel.path.depth(), channel.path.parent(), channel.path.rootIndex(), points));
        }
        return result;
    }
}
