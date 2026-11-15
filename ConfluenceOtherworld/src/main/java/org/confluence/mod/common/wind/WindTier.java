package org.confluence.mod.common.wind;

import net.minecraft.util.Mth;

/// 风档位。
///
/// **除 {@link #WINDY} 的下界（20 mph，泰拉瑞亚「大风天」阈值）外，其余分档线只是视觉插值刻度**，
/// 用来决定风粒子密度、云速与雨倾角，不是原作概念，改动它们不会影响任何 gameplay 判定。
/// 所有 gameplay 判定（大风天事件、大风天专属生成、静风生物、沙尘暴门槛）统一挂在
/// {@link WindMath#WINDY_DAY_MIN} 这一条线上。
public enum WindTier {
    /// 静风：3 mph 以下，不产生风粒子。
    CALM(0.0F, WindMath.toMps(3.0F)),
    /// 微风：3 ~ 10 mph。
    LIGHT(WindMath.toMps(3.0F), WindMath.toMps(10.0F)),
    /// 有风：10 ~ 20 mph。
    BREEZY(WindMath.toMps(10.0F), WindMath.WINDY_DAY_MIN),
    /// 大风天：20 mph 以上（泰拉瑞亚大风天阈值）。
    WINDY(WindMath.WINDY_DAY_MIN, WindMath.toMps(45.0F)),
    /// 烈风：45 mph 以上，只会由事件或命令写入。
    GALE(WindMath.toMps(45.0F), Float.MAX_VALUE);

    private final float min;
    private final float max;

    WindTier(float min, float max) {
        this.min = min;
        this.max = max;
    }

    public float min() {
        return min;
    }

    public float max() {
        return max;
    }

    public static WindTier bySpeed(float mps) {
        if (mps < LIGHT.min) return CALM;
        if (mps < BREEZY.min) return LIGHT;
        if (mps < WINDY.min) return BREEZY;
        if (mps < GALE.min) return WINDY;
        return GALE;
    }

    /// 把 [0, 45] mph 线性映射到 [0, 1]，用于粒子密度等视觉插值。
    public static float intensity(float mps) {
        return Mth.clamp(mps / GALE.min, 0.0F, 1.0F);
    }
}
