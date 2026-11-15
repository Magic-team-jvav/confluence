// Adapted from minecraft-lightning skill. See META-INF/arc-surge-lightning-NOTICES.md.
package org.confluence.mod.client.effect.lightning;

import org.confluence.mod.Confluence;

/** Deterministic shared leader / return / restrike timeline, evaluated in fractional ticks. */
final class LightningEnvelope {
    private final LightningRecipe r;
    LightningEnvelope(LightningRecipe r) { this.r = r; }
    double propagation(double ticks) { return LightningMath.clamp(ticks / r.progressionTicks, 0, 1); }
    double brightness(double ticks) {
        if (ticks < 0 || ticks >= r.durationTicks) return 0;
        if (ticks < r.progressionTicks) return .32;
        double power = Math.exp(-Math.max(0, ticks - r.progressionTicks - .6) * .5);
        for (int i = 0; i < (int) r.restrikeCount; i++) {
            double at = r.progressionTicks + .9 + (i + .35 * LightningMath.unit(r.seed, 110 + i))
                * (r.durationTicks - r.progressionTicks - 2) / Math.max(1, r.restrikeCount);
            if (ticks >= at) power = Math.max(power, (.85 + .15 * LightningMath.unit(r.seed, 210 + i))
                * Math.exp(-Math.max(0, ticks - at - .35) * 1.3));
        }
        return power * (1 - LightningMath.smooth((ticks - r.durationTicks + 1.5) / 1.5))
            * (1 - r.flicker * .2 * (.5 + .5 * Math.sin(ticks * 19 + r.seed)));
    }
    boolean finished(double ticks) { return ticks >= r.durationTicks; }
}
