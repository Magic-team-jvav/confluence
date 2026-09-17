package org.confluence.mod.client.effect;

import net.minecraft.util.Mth;

/// 残影（动作模糊）的通用观感参数。
public final class AfterimageStyle {
    public static final int BLUR_STEPS = 3;
    public static final double BLUR_FALLOFF = 2.0D;
    public static final float OLDEST_ALPHA = 0.1F;
    public static final float NEWEST_ALPHA = 0.45F;
    public static final float INTERPOLATION_ALPHA = 0.2F;

    private AfterimageStyle() {}

    public static float alphaAt(float factor) {
        float falloff = (float) Math.pow(factor, BLUR_FALLOFF);
        return Mth.lerp(falloff, OLDEST_ALPHA, NEWEST_ALPHA);
    }

    public static float interpolatedAlphaAt(float factor) {
        return alphaAt(factor) * INTERPOLATION_ALPHA;
    }
}
