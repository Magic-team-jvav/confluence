package org.confluence.mod.client.effect.lightning;

import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Bounded lights sampled only on the visible discharge, before OpalLight renders the world. */
final class ArcSurgeLightSamples {
    record Point(long id, Vec3 position, float red, float green, float blue, int brightness) {}
    private record Path(long id, List<BoltGeometry.Segment> segments, double length, int count) {}

    static List<Point> sample(BoltGeometry geometry, LightningRecipe recipe, double age) {
        double envelope = new LightningEnvelope(recipe).brightness(age + recipe.progressionTicks);
        if (envelope < 0.03) return List.of();
        Map<Long, List<BoltGeometry.Segment>> paths = new LinkedHashMap<>();
        for (var segment : geometry.segments()) paths.computeIfAbsent(segment.mask(), ignored -> new ArrayList<>()).add(segment);
        List<Point> output = new ArrayList<>();
        int[] budgets = {20, 14, 6};
        for (int depth = 0; depth <= 2; depth++) {
            List<Path> candidates = new ArrayList<>();
            for (var entry : paths.entrySet()) {
                var segments = entry.getValue();
                if (segments.get(0).depth() != depth) continue;
                double length = segments.stream().mapToDouble(s -> s.a().distanceTo(s.b())).sum();
                int count = Math.min(depth == 0 ? 16 : depth == 1 ? 6 : 2, Math.max(2, (int) Math.ceil(length / 1.5) + 1));
                candidates.add(new Path(entry.getKey(), segments, length, count));
            }
            int used = 0;
            // Round-robin gives each target a light before filling any one long path.
            for (int slot = 0; slot < 16 && used < budgets[depth]; slot++) {
                for (Path path : candidates) {
                    if (slot >= path.count || used >= budgets[depth]) continue;
                    double remaining = path.length * slot / (path.count - 1.0);
                    var chosen = path.segments.get(path.segments.size() - 1);
                    for (var s : path.segments) {
                        double length = s.a().distanceTo(s.b());
                        if (remaining <= length) { chosen = s; break; }
                        remaining -= length;
                    }
                    double t = LightningMath.clamp(remaining / Math.max(1.0E-9, chosen.a().distanceTo(chosen.b())), 0, 1);
                    Vec3 position = chosen.a().lerp(chosen.b(), t);
                    if (output.stream().anyMatch(p -> p.position.distanceToSqr(position) < 0.64)) continue;
                    float along = (float) (chosen.alongA() + (chosen.alongB() - chosen.alongA()) * t);
                    float[] color = LightningAppearance.colorAt(recipe, true, along);
                    int brightness = (int) Math.round((depth == 0 ? 13 : depth == 1 ? 10 : 7)
                            * envelope * Math.sqrt(Math.max(0, chosen.intensity())));
                    if (brightness <= 0) continue;
                    output.add(new Point((path.id << 8) | slot, position, color[0], color[1], color[2], Math.min(15, brightness)));
                    used++;
                }
            }
        }
        return List.copyOf(output);
    }
}
