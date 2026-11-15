package org.confluence.mod.common.wind;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.Heightmap;

/// 风系统的纯函数与常量，**服务端与客户端共用同一份实现**，保证两端在没有任何额外同步的前提下算出完全一致的阵风与曝露度。
///
/// ## 单位
///
/// 规范单位是 **m/s**。方块与米按 1 方块 = 1 米换算，1 tick = 50 ms，即 1 m/s = 0.05 方块/tick。
///
/// 泰拉瑞亚 wiki 里的 **mph 是 miles per hour（英里每小时）**——既不是"米每小时"，也不是别的什么：
/// `1 mph = 1609.344 m / 3600 s = 0.44704 m/s`。
/// 本类所有 mph ↔ m/s 换算都只走 {@link #MPH_TO_MPS}，且 {@link #REF_SPEED}、{@link #WINDY_DAY_MIN}
/// 都是从 mph 数值**推导**出来的、不写死 m/s 字面量，因此不存在"把 20 当成 m/s"的余地。
public final class WindMath {
    private WindMath() {}

    /// 英里每小时 → 米每秒：`1609.344 / 3600`。
    public static final float MPH_TO_MPS = 0.44704F;
    /// m/s -> 方块/tick。
    public static final float BLOCKS_PER_TICK = 0.05F;

    /// 旧单位 1.0 对应的风速：**30 mph** = `30 * 0.44704` = 13.4112 m/s。
    /// 只用于旧存档迁移与美术系数归一化，**不参与任何 gameplay 判定**。
    public static final float REF_SPEED = 30.0F * MPH_TO_MPS;

    /// 泰拉瑞亚「大风天」阈值：**20 mph** = `20 * 0.44704` = 8.9408 m/s。风系统里唯一的 gameplay 阈值。
    public static final float WINDY_DAY_MIN = 20.0F * MPH_TO_MPS;

    /// 阵风振幅：瞬时风速在 0.75x ~ 1.25x 之间摆动。
    public static final float GUST_AMP = 0.25F;
    /// 曝露度衰减深度：地表以下 24 格衰减到 0。
    public static final float EXPOSURE_DEPTH = 24.0F;
    /// 低于该风速时，位置相关的风向量直接返回零，跳过一切计算。
    public static final float MIN_EFFECT_SPEED = 2.0F;

    /// 沙尘暴期间强制维持的风速（30 mph，原作沙尘暴是恒速恒向风）。
    public static final float SANDSTORM_FORCED_SPEED = REF_SPEED;

    public static float toMps(float mph) {
        return mph * MPH_TO_MPS;
    }

    public static float toMph(float mps) {
        return mps / MPH_TO_MPS;
    }

    public static float toBlocksPerTick(float mps) {
        return mps * BLOCKS_PER_TICK;
    }

    /// 确定性阵风函数。给定相同的 `gameTime` 与 `phase`，服务端与客户端的结果逐位相同，因此阵风不需要额外的网络同步。
    ///
    /// @param gameTime 世界游戏时间（客户端与服务端同 tick）
    /// @param phase    世界固定的阵风相位（随存档持久化并随包下发）
    public static float gust(long gameTime, float phase) {
        float t = (gameTime % 72000L) * 0.05F; // 秒，避免长时间运行后 float 精度丢失
        return 1.0F + GUST_AMP * (0.6F * Mth.sin(t + phase * Mth.TWO_PI)
                + 0.4F * Mth.sin(t * 2.5F + phase * 4.0F * Mth.TWO_PI));
    }

    /// 位置曝露度：地表露天为 1，地表以下 {@link #EXPOSURE_DEPTH} 格衰减到 0，屋檐/树冠下减半。
    ///
    /// 只作用于**风粒子**，不影响 HUD 读数（地下没有风粒子，但风速计照样显示全局风速，与原作一致）。
    public static float exposure(LevelReader level, BlockPos pos) {
        int surface = level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ());
        int depth = surface - pos.getY();
        float vertical = Mth.clamp(1.0F - depth / EXPOSURE_DEPTH, 0.0F, 1.0F);
        float sky = level.canSeeSky(pos) ? 1.0F : 0.35F;
        return vertical * sky;
    }

    public static float approach(float current, float target, float step) {
        return Mth.approach(current, target, step);
    }

    /// 角量插值：走最短角差，避免在 ±π 处绕远路。
    public static float approachRadians(float current, float target, float step) {
        return current + Mth.clamp(wrapRadians(target - current), -step, step);
    }

    /// 把角度规约到 [-π, π)。
    public static float wrapRadians(float angle) {
        float a = angle % Mth.TWO_PI;
        if (a >= Mth.PI) return a - Mth.TWO_PI;
        if (a < -Mth.PI) return a + Mth.TWO_PI;
        return a;
    }

    /// 角度约定：`dir = (cos θ, sin θ)` 映射到 `(x, z)`。
    /// 轴的对应与原版 `Direction` 完全一致：**+X = 东（EAST）、-X = 西（WEST）、+Z = 南（SOUTH）、-Z = 北（NORTH）**，
    /// 所以 θ = 0 是**吹向** +X（东），θ = 90° 是**吹向** +Z（南）。
    private static final String[] COMPASS = {
            "east", "southeast", "south", "southwest", "west", "northwest", "north", "northeast"
    };

    /// 方位名，**按气象学惯例：以风"从哪来"命名**（东风 = 从东边吹来的风，吹向西方）。
    ///
    /// 所以这里取的是 `θ + 180°` —— 与 `/confluence wind set <x> <z>` 的"吹向"分量正好相反半个方位。
    /// 真实风向标的箭头也是指向**风来的方向**，两者因此一致。
    /// （早期版本按"吹向"命名，于是"吹向东"被标成东风 —— 那是错的。）
    public static String compassKey(float angle) {
        return "info.confluence.wind.compass." + COMPASS[compassIndex(angle)];
    }

    /// 方位索引（0 = 东、1 = 东南、… 顺时针）。已按"来向"换算。
    private static int compassIndex(float angle) {
        int towards = Mth.floor(wrapRadians(angle) / Mth.PI * 4.0F + 0.5F) & 7;
        return (towards + 4) & 7; // +180°
    }
}
