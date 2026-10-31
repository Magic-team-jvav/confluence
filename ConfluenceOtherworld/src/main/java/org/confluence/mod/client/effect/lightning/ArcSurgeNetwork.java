package org.confluence.mod.client.effect.lightning;

import com.google.gson.Gson;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.util.ConductorArcPath;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/** One discharge tree per shot: nearby channels reuse an established conducting path. */
final class ArcSurgeNetwork {
    private record Node(ArcSurgeGeometry geometry, List<Vec3> guide, Vec3 normal, int parent, int junction,
                        List<BoltGeometry.Segment> restTrunk) {}
    private final List<Node> nodes = new ArrayList<>();
    private final LightningRecipe budget;
    private final ArcSurgeEndpoints endpoints;
    private BoltGeometry cached;
    private double cachedAge = Double.NaN;
    private long cachedRevision;

    ArcSurgeNetwork(List<ArcSurgeLightning.Channel> channels, LightningRecipe recipe) {
        if (channels.isEmpty() || channels.size() > 3) throw new IllegalArgumentException("Expected 1..3 channels");
        budget = copy(recipe);
        budget.maxSegments = recipe.maxSegments * channels.size();
        endpoints = new ArcSurgeEndpoints(recipe.seed);
        for (int index = 0; index < channels.size(); index++) {
            var channel = channels.get(index);
            List<Vec3> guide = channel.guide();
            Vec3 source = guide.get(0), target = guide.get(guide.size() - 1);
            int parent = -1, junction = -1;
            double best = Double.POSITIVE_INFINITY;
            for (int j = 0; j < nodes.size(); j++) {
                Node candidate = nodes.get(j);
                // Target branches remain depth one, with their own depth-two terminal twigs.
                if (candidate.parent >= 0 || !mergeable(candidate.guide, guide)) continue;
                var trunk = candidate.restTrunk;
                double travelled = 0;
                for (int k = 0; k < trunk.size(); k++) {
                    travelled += trunk.get(k).a().distanceTo(trunk.get(k).b());
                    double fraction = (k + 1.0) / trunk.size();
                    if (fraction < 0.25 || fraction > 0.82) continue;
                    Vec3 root = trunk.get(k).b();
                    // Length approximates channel resistance; shared, already-ionized path is cheaper.
                    double cost = root.distanceTo(target) + 0.18 * travelled;
                    if (root.distanceTo(target) > 0.25 && cost < best) {
                        best = cost; parent = j; junction = k;
                    }
                }
            }
            LightningRecipe shape = copy(recipe);
            shape.maxSegments -= Math.min(48, Math.max(0, shape.maxSegments - 64));
            shape.seed = recipe.seed + index * 104729;
            if (parent >= 0) {
                Node owner = nodes.get(parent);
                Vec3 root = owner.restTrunk.get(junction).b();
                guide = ConductorArcPath.create(root, target, channel.normal(), 64);
                shape.maxBranchDepth = Math.min(1, shape.maxBranchDepth);
            }
            var geometry = new ArcSurgeGeometry(guide, channel.normal(), shape);
            var trunk = geometry.unclipped(0, guide.get(0)).segments().stream().filter(s -> s.depth() == 0).toList();
            nodes.add(new Node(geometry, guide, channel.normal(), parent, junction, trunk));
        }
    }

    static boolean mergeable(List<Vec3> a, List<Vec3> b) {
        Vec3 source = a.get(0), endA = a.get(a.size() - 1), endB = b.get(b.size() - 1);
        if (source.distanceToSqr(b.get(0)) > 0.01) return false;
        // Close enemies split near the end of one common trunk.
        if (endA.distanceTo(endB) <= 2.75) return true;
        Vec3 da = endA.subtract(source), db = endB.subtract(source);
        if (da.normalize().dot(db.normalize()) < Math.cos(Math.toRadians(12))) return false;
        // Exclude the unavoidable common muzzle: require sustained overlap away from it.
        double overlap = Math.min(da.length(), db.length());
        if (overlap < 2) return false;
        for (double t : new double[]{0.3, 0.5, 0.7}) {
            if (source.add(da.normalize().scale(overlap * t))
                    .distanceTo(source.add(db.normalize().scale(overlap * t))) > 1.25) return false;
        }
        return true;
    }

    BoltGeometry sample(double ticks, BiFunction<Vec3, Vec3, Vec3> clipper) {
        List<List<BoltGeometry.Segment>> trunks = new ArrayList<>();
        List<ArcSurgeEndpoints.Contact> contacts = new ArrayList<>();
        List<BoltGeometry.Segment> combined = new ArrayList<>();
        long nextMask = 0;
        for (Node node : nodes) {
            Vec3 root = node.guide.get(0);
            float along = 0;
            if (node.parent >= 0) {
                var junction = trunks.get(node.parent).get(node.junction);
                root = junction.b();
                along = junction.alongB();
            }
            BoltGeometry raw = node.geometry.unclipped(ticks, root);
            trunks.add(raw.segments().stream().filter(s -> s.depth() == 0).toList());
            long offset = nextMask;
            contacts.add(new ArcSurgeEndpoints.Contact(offset, node.normal));
            float width = node.parent >= 0 ? 0.72f : 1;
            for (var s : raw.segments()) {
                combined.add(new BoltGeometry.Segment(s.a(), s.b(), s.widthA() * width, s.widthB() * width,
                        s.intensity(), along + (1 - along) * s.alongA(), along + (1 - along) * s.alongB(),
                        s.depth() + (node.parent >= 0 ? 1 : 0), s.mask() + offset));
                nextMask = Math.max(nextMask, s.mask() + offset + 1);
            }
        }
        // Clip the assembled tree once: a hidden junction also removes all its descendants.
        BoltGeometry visible = ArcSurgeGeometry.clip(budget, combined, clipper);
        return endpoints.apply(visible, budget, contacts, ticks, clipper);
    }

    /** Revision is the world's tick; never reuse collision results across world updates. */
    BoltGeometry sample(double ticks, long revision, BiFunction<Vec3, Vec3, Vec3> clipper) {
        if (cached != null && ticks == cachedAge && revision == cachedRevision) return cached;
        cached = sample(ticks, clipper);
        cachedAge = ticks;
        cachedRevision = revision;
        return cached;
    }

    int mergedChannels() { return (int) nodes.stream().filter(n -> n.parent >= 0).count(); }

    private static LightningRecipe copy(LightningRecipe recipe) {
        return LightningRecipe.parse(new Gson().toJsonTree(recipe));
    }
}
