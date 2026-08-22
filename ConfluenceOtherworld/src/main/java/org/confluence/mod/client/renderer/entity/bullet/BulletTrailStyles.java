package org.confluence.mod.client.renderer.entity.bullet;

import org.confluence.mod.Confluence;

import java.util.Map;

/// Visual language for the ammunition families in TerraGuns.
///
/// <p>The textures are white masks, so the existing per-ammo trail colors can
/// tint them without requiring a separate PNG for every bullet.</p>
final class BulletTrailStyles {
    private static final BulletTrailStyle STANDARD = style("trace_01", "flare_01", 0.12F, 0.008F, 0.10F, 0.92F, 20, true);
    private static final BulletTrailStyle ENERGY = style("trace_05", "flare_01", 0.15F, 0.012F, 0.12F, 0.96F, 20, true);
    private static final BulletTrailStyle BEAM = style("trace_07", "flare_01", 0.18F, 0.018F, 0.14F, 1.0F, 20, true);
    private static final BulletTrailStyle SPARK = style("trace_05", "circle_05", 0.14F, 0.008F, 0.13F, 0.95F, 18, true);

    private static final Map<String, BulletTrailStyle> STYLES = Map.ofEntries(
            Map.entry("meteor_shot", style("trace_05", "circle_05", 0.13F, 0.006F, 0.11F, 0.95F, 18, true)),
            Map.entry("crystal_bullet", style("trace_07", "flare_01", 0.14F, 0.010F, 0.12F, 0.95F, 20, true)),
            Map.entry("cursed_bullet", SPARK),
            Map.entry("chlorophyte_bullet", style("trace_05", "flare_01", 0.23F, 0.026F, 0.07F, 0.90F, 256, true)),
            Map.entry("high_velocity_bullet", style("trace_07", "flare_01", 0.10F, 0.003F, 0.09F, 1.0F, 16, true)),
            Map.entry("ichor_bullet", style("trace_01", "flare_01", 0.14F, 0.006F, 0.11F, 0.95F, 20, true)),
            Map.entry("venom_bullet", SPARK),
            Map.entry("party_bullet", style("trace_01", "circle_05", 0.16F, 0.010F, 0.13F, 0.92F, 20, true)),
            Map.entry("nano_bullet", ENERGY),
            Map.entry("exploding_bullet", style("trace_05", "flare_01", 0.17F, 0.014F, 0.14F, 0.96F, 20, true)),
            Map.entry("golden_bullet", style("trace_01", "flare_01", 0.13F, 0.008F, 0.11F, 0.94F, 20, true)),
            Map.entry("luminite_bullet", BEAM));

    private BulletTrailStyles() {}

    static BulletTrailStyle get(String bulletId) {
        return STYLES.getOrDefault(bulletId, STANDARD);
    }

    private static BulletTrailStyle style(String trail, String head, float headWidth, float tailWidth, float headSize, float opacity, int maxPoints, boolean additive) {
        return new BulletTrailStyle(Confluence.asResource("textures/vfx/trails/" + trail + ".png"), Confluence.asResource("textures/vfx/heads/" + head + ".png"), headWidth, tailWidth, headSize, opacity, maxPoints, additive);
    }
}
