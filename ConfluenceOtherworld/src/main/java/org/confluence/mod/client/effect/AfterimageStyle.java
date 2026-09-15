package org.confluence.mod.client.effect;

import net.minecraft.util.Mth;

/// 残影（动作模糊）的通用观感参数，见 [AfterimageHelper]
public final class AfterimageStyle {
    /// 相邻两个快照之间插值的分段数，越大拖影越接近连续
    public static final int BLUR_STEPS = 3;
    /// 动态模糊的衰减指数，越大则拖尾收得越紧、中段越淡
    public static final double BLUR_FALLOFF = 2.0D;
    /// 最远端残影的透明度
    public static final float OLDEST_ALPHA = 0.1F;
    /// 最近端残影的透明度
    public static final float NEWEST_ALPHA = 0.45F;
    /// 插值补帧残影相对快照的透明度倍率，越小则"实残影 + 虚连接"的层次越明显
    public static final float INTERPOLATION_ALPHA = 0.2F;
    /// 残影颜色（黑白即可，纯黑剪影）
    public static final int GHOST_RGB = 0x000000;
    /// 保留本体材质原色的残影调色（纯白等于不改色）
    public static final int COLORED_RGB = 0xFFFFFF;

    private AfterimageStyle() {}

    /// 快照的透明度：factor 从 0（最旧）到 1（最新），指数衰减模拟动态模糊
    public static float alphaAt(float factor) {
        float falloff = (float) Math.pow(factor, BLUR_FALLOFF);
        return Mth.lerp(falloff, OLDEST_ALPHA, NEWEST_ALPHA);
    }

    /// 插值补帧残影的透明度，比快照更透明
    public static float interpolatedAlphaAt(float factor) {
        return alphaAt(factor) * INTERPOLATION_ALPHA;
    }

    /// 由透明度和调色合成残影的 ARGB 颜色
    public static int colorAt(float alpha, int rgb) {
        return Mth.clamp((int) (alpha * 255.0F), 0, 255) << 24 | rgb;
    }

    /// 由透明度合成纯黑剪影的 ARGB 颜色
    public static int colorAt(float alpha) {
        return colorAt(alpha, GHOST_RGB);
    }
}
