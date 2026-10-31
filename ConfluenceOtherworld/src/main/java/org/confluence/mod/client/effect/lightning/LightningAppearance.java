// Adapted from minecraft-lightning skill. See META-INF/arc-surge-lightning-NOTICES.md.
package org.confluence.mod.client.effect.lightning;

import org.confluence.mod.Confluence;

/** Pure material contract shared by both ribbon passes and their regression checks. */
final class LightningAppearance {
    private LightningAppearance() {}
    /** Constant material coordinates describe endpoint flares, not zero-length spatial paths. */
    static double propagationFraction(double alongA, double alongB, double progress) {
        if (alongA > progress) return -1;
        if (Math.abs(alongB - alongA) < 1.0E-9) return 1;
        if (alongA == progress) return -1;
        return LightningMath.clamp((progress - alongA) / (alongB - alongA), 0, 1);
    }
    /** Immutable per-mesh material; never retained across live parameter edits. */
    record Palette(int base, boolean gradient, int start, int end, double ratio) {
        float[] colorAt(double along) {
            int rgb=materialColor(base,gradient,start,end,ratio,along);
            return new float[]{((rgb>>16)&255)/255f,((rgb>>8)&255)/255f,(rgb&255)/255f};
        }
    }
    static Palette palette(LightningRecipe r, boolean halo) {
        boolean gradient=usesGradient(r,halo);
        return new Palette(parse(halo?r.glowColor:r.coreColor),gradient,
            gradient?parse(r.gradientStart):0,gradient?parse(r.gradientEnd):0,r.gradientRatio);
    }

    static double ribbonScale(LightningRecipe r, boolean halo, double baseWidth) {
        return halo ? r.glowWidth / baseWidth : 2;
    }

    static float[] colorAt(LightningRecipe r, boolean halo, double along) {
        int rgb = materialColor(halo ? r.glowColor : r.coreColor, usesGradient(r, halo),
            r.gradientStart, r.gradientEnd, r.gradientRatio, along);
        return new float[]{((rgb >> 16) & 255) / 255f, ((rgb >> 8) & 255) / 255f, (rgb & 255) / 255f};
    }

    static boolean usesGradient(LightningRecipe r, boolean halo) {
        return usesGradient(r.gradientEnabled, r.gradientTarget, halo);
    }

    static boolean usesGradient(boolean enabled, String target, boolean halo) {
        return enabled && ("both".equals(target) || (halo ? "glow" : "core").equals(target));
    }

    static boolean vividCore(LightningRecipe r) {
        return usesGradient(r, false) && "vivid".equals(r.gradientCoreMode);
    }

    /** Shared material tint for ribbons and UI swatches, before gain or scene blending. */
    static int materialColor(String baseColor, boolean gradient, String start, String end, double ratio, double along) {
        return materialColor(parse(baseColor),gradient,gradient?parse(start):0,gradient?parse(end):0,ratio,along);
    }
    private static int materialColor(int base, boolean gradient, int a, int b, double ratio, double along) {
        int rgb = base;
        if (gradient) {
            double t = gradientMix(along, ratio);
            int gr = (int)Math.round(((a >> 16) & 255) + (((b >> 16) & 255) - ((a >> 16) & 255)) * t);
            int gg = (int)Math.round(((a >> 8) & 255) + (((b >> 8) & 255) - ((a >> 8) & 255)) * t);
            int gb = (int)Math.round((a & 255) + ((b & 255) - (a & 255)) * t);
            // The original skill multiplies the gradient by each pass's base tint.
            rgb = ((int)(((base >> 16) & 255) * gr / 255.0) << 16)
                | ((int)(((base >> 8) & 255) * gg / 255.0) << 8)
                | (int)((base & 255) * gb / 255.0);
        }
        return rgb;
    }

    /** End-color weight; a larger ratio gives the start color more of the path. */
    static double gradientMix(double along, double percentage) {
        double x = LightningMath.clamp(along, 0, 1), ratio = percentage / 100;
        if (ratio <= 0) return 1;
        if (ratio >= 1) return 0;
        // Preserve the old interpolation and channel rounding exactly at the default.
        if (ratio == .5) return x;
        double endWeight = x * (1 - ratio), startWeight = (1 - x) * ratio;
        return endWeight / (endWeight + startWeight);
    }

    private static int parse(String value) { return Integer.parseInt(value.substring(1), 16); }
}
