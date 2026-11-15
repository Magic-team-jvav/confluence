package org.confluence.mod.common.worldgen.biome.injector;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

import java.util.function.Consumer;

/// 气候参数声明助手。
///
/// 这里的分档数值与原版 {@link net.minecraft.world.level.biome.OverworldBiomeBuilder} 完全一致，
/// 便于把区域声明写成和原版群系表一样的读法。各个分档内部都提供一个
/// `span(a, b)` 用来取两个档位的并集（如 `Temperature.span(WARM, HOT)` = `[0.2, 1.0]`）。
///
/// ## 关于 depth
///
/// `depth` 由 `NoiseRouterData` 的 depth 路由给出，量级大致在 `[-3, 2]`：
/// 地表附近约 0，越往下越大，天空里是负数。原版靠「最近邻」让地表群系覆盖天空，
/// 而本注入器用**包含判定**，所以：
///
/// - 想覆盖整列（含天空）用 {@link #COLUMN}；
/// - 想只做洞穴群系用 {@link #DEEP_UNDERGROUND}，天空和地表都会自动回落原版；
/// - {@link #COLUMN} 特意把 0.95 以上的「最底部」留给原版，以免吃掉深暗之域。
public final class ParameterBuilder {
    private ParameterBuilder() {}

    // ── 通用 ────────────────────────────────────────────────────────────────

    public static final Climate.Parameter FULL_RANGE = Climate.Parameter.span(-1.0F, 1.0F);
    /// 整列（含天空与最底部）。
    public static final Climate.Parameter ANY_DEPTH = Climate.Parameter.span(-4.0F, 2.5F);
    /// 整列但把最底部留给原版（保住深暗之域）。
    public static final Climate.Parameter COLUMN = Climate.Parameter.span(-4.0F, 0.95F);
    /// 地表及以上。
    public static final Climate.Parameter SURFACE = Climate.Parameter.span(-4.0F, 0.0F);
    /// 浅层地下。
    public static final Climate.Parameter UNDERGROUND = Climate.Parameter.span(0.2F, 0.9F);
    /// 洞穴层：大体对应地表以下 45–115 格。
    ///
    /// `depth` 是**相对本地地表的下沉量**而不是世界高度：`NoiseRouterData` 的 depth 路由是
    /// `yClampedGradient(-64, 320, 1.5, -1.5) + (-0.50375 + 地形偏移样条)`，样条把 0 点对齐到
    /// 地形表面，于是每 0.2 个 depth 单位约等于 25.6 格。所以 `span(0.2, 0.9)`（原版地下群系用的）
    /// 是地表以下 26–115 格，而 `span(0.6, 0.9)` 只有最底那 38 格。
    public static final Climate.Parameter CAVERN = Climate.Parameter.span(0.35F, 0.9F);
    /// 深层地下（原版地下群系里最深的一档）。
    public static final Climate.Parameter DEEP_UNDERGROUND = Climate.Parameter.span(0.6F, 0.9F);
    /// 最底部。
    public static final Climate.Parameter FLOOR = Climate.Parameter.span(0.95F, 2.5F);

    public static Climate.Parameter point(float value) {
        return Climate.Parameter.point(value);
    }

    public static Climate.Parameter span(float min, float max) {
        return Climate.Parameter.span(min, max);
    }

    /// 取两个参数区间的并集（下界取下界，上界取上界）。
    public static Climate.Parameter span(Climate.Parameter a, Climate.Parameter b) {
        return Climate.Parameter.span(Math.min(a.min(), b.min()), Math.max(a.max(), b.max()));
    }

    /// 声明一个参数点。`depth` 由调用方给出，`offset` 恒为 0。
    public static void add(
            Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> consumer,
            Climate.Parameter temperature,
            Climate.Parameter humidity,
            Climate.Parameter continentalness,
            Climate.Parameter erosion,
            Climate.Parameter depth,
            Climate.Parameter weirdness,
            ResourceKey<Biome> biome
    ) {
        consumer.accept(Pair.of(Climate.parameters(temperature, humidity, continentalness, erosion, depth, weirdness, 0.0F), biome));
    }

    /// 覆盖整列的群系（邪恶群系、灰烬系列等）。
    public static void addColumn(
            Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> consumer,
            Climate.Parameter temperature,
            Climate.Parameter humidity,
            Climate.Parameter continentalness,
            Climate.Parameter erosion,
            Climate.Parameter weirdness,
            ResourceKey<Biome> biome
    ) {
        add(consumer, temperature, humidity, continentalness, erosion, COLUMN, weirdness, biome);
    }

    /// 只在洞穴里出现的群系。
    public static void addUnderground(
            Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> consumer,
            Climate.Parameter temperature,
            Climate.Parameter humidity,
            Climate.Parameter continentalness,
            Climate.Parameter erosion,
            Climate.Parameter weirdness,
            ResourceKey<Biome> biome
    ) {
        add(consumer, temperature, humidity, continentalness, erosion, DEEP_UNDERGROUND, weirdness, biome);
    }

    // ── 原版分档 ────────────────────────────────────────────────────────────

    public static final class Temperature {
        public static final Climate.Parameter ICY = span(-1.0F, -0.45F);
        public static final Climate.Parameter COOL = span(-0.45F, -0.15F);
        public static final Climate.Parameter NEUTRAL = span(-0.15F, 0.2F);
        public static final Climate.Parameter WARM = span(0.2F, 0.55F);
        public static final Climate.Parameter HOT = span(0.55F, 1.0F);
        public static final Climate.Parameter FROZEN = ICY;
        public static final Climate.Parameter UNFROZEN = span(-0.45F, 1.0F);
        public static final Climate.Parameter FULL_RANGE = ParameterBuilder.FULL_RANGE;

        /// 两个档位的并集，例如 `span(WARM, HOT)` = `[0.2, 1.0]`。
        public static Climate.Parameter span(float min, float max) {
            return ParameterBuilder.span(min, max);
        }

        public static Climate.Parameter span(Climate.Parameter from, Climate.Parameter to) {
            return ParameterBuilder.span(from, to);
        }
    }

    public static final class Humidity {
        public static final Climate.Parameter ARID = span(-1.0F, -0.35F);
        public static final Climate.Parameter DRY = span(-0.35F, -0.1F);
        public static final Climate.Parameter NEUTRAL = span(-0.1F, 0.1F);
        public static final Climate.Parameter WET = span(0.1F, 0.3F);
        public static final Climate.Parameter HUMID = span(0.3F, 1.0F);
        public static final Climate.Parameter FULL_RANGE = ParameterBuilder.FULL_RANGE;

        public static Climate.Parameter span(float min, float max) {
            return ParameterBuilder.span(min, max);
        }

        public static Climate.Parameter span(Climate.Parameter from, Climate.Parameter to) {
            return ParameterBuilder.span(from, to);
        }
    }

    public static final class Continentalness {
        public static final Climate.Parameter MUSHROOM_FIELDS = span(-1.2F, -1.05F);
        public static final Climate.Parameter DEEP_OCEAN = span(-1.05F, -0.455F);
        public static final Climate.Parameter OCEAN = span(-0.455F, -0.19F);
        public static final Climate.Parameter COAST = span(-0.19F, -0.11F);
        public static final Climate.Parameter NEAR_INLAND = span(-0.11F, 0.03F);
        public static final Climate.Parameter MID_INLAND = span(0.03F, 0.3F);
        public static final Climate.Parameter FAR_INLAND = span(0.3F, 1.0F);
        public static final Climate.Parameter INLAND = span(-0.11F, 0.55F);
        /// 陆地：海岸到远内陆，只排除海洋（含深海）与蘑菇岛。
        ///
        /// 覆盖群系要「整片替换地表」时用这个而不是 {@link #INLAND}：大陆度是平滑场，
        /// `INLAND` 只覆盖近内陆，站在海岸线上的玩家周围几千格都会落在区间外，
        /// 表现为「区域接管了这一列但一个群系都换不出来」。
        public static final Climate.Parameter LAND = span(-0.19F, 1.0F);
        public static final Climate.Parameter FULL_RANGE = ParameterBuilder.FULL_RANGE;

        public static Climate.Parameter span(float min, float max) {
            return ParameterBuilder.span(min, max);
        }

        public static Climate.Parameter span(Climate.Parameter from, Climate.Parameter to) {
            return ParameterBuilder.span(from, to);
        }
    }

    public static final class Erosion {
        public static final Climate.Parameter EROSION_0 = span(-1.0F, -0.78F);
        public static final Climate.Parameter EROSION_1 = span(-0.78F, -0.375F);
        public static final Climate.Parameter EROSION_2 = span(-0.375F, -0.2225F);
        public static final Climate.Parameter EROSION_3 = span(-0.2225F, 0.05F);
        public static final Climate.Parameter EROSION_4 = span(0.05F, 0.45F);
        public static final Climate.Parameter EROSION_5 = span(0.45F, 0.55F);
        public static final Climate.Parameter EROSION_6 = span(0.55F, 1.0F);
        public static final Climate.Parameter FULL_RANGE = ParameterBuilder.FULL_RANGE;

        public static Climate.Parameter span(float min, float max) {
            return ParameterBuilder.span(min, max);
        }

        public static Climate.Parameter span(Climate.Parameter from, Climate.Parameter to) {
            return ParameterBuilder.span(from, to);
        }
    }

    public static final class Depth {
        public static final Climate.Parameter SURFACE = Climate.Parameter.point(0.0F);
        public static final Climate.Parameter UNDERGROUND = span(0.2F, 0.9F);
        public static final Climate.Parameter FLOOR = Climate.Parameter.point(1.1F);
        /// 下界维度的 depth 恒为 0，用这个区间等价于「整列」。
        public static final Climate.Parameter NETHER_RANGE = span(0.0F, 1.1F);
        public static final Climate.Parameter FULL_RANGE = ParameterBuilder.ANY_DEPTH;

        public static Climate.Parameter span(float min, float max) {
            return ParameterBuilder.span(min, max);
        }

        public static Climate.Parameter span(Climate.Parameter from, Climate.Parameter to) {
            return ParameterBuilder.span(from, to);
        }
    }

    public static final class Weirdness {
        public static final Climate.Parameter MID_SLICE_NORMAL_ASCENDING = span(-1.0F, -0.93333334F);
        public static final Climate.Parameter HIGH_SLICE_NORMAL_ASCENDING = span(-0.93333334F, -0.7666667F);
        public static final Climate.Parameter PEAK_NORMAL = span(-0.7666667F, -0.56666666F);
        public static final Climate.Parameter HIGH_SLICE_NORMAL_DESCENDING = span(-0.56666666F, -0.4F);
        public static final Climate.Parameter MID_SLICE_NORMAL_DESCENDING = span(-0.4F, -0.26666668F);
        public static final Climate.Parameter LOW_SLICE_NORMAL_DESCENDING = span(-0.26666668F, -0.05F);
        public static final Climate.Parameter VALLEY = span(-0.05F, 0.05F);
        public static final Climate.Parameter LOW_SLICE_VARIANT_ASCENDING = span(0.05F, 0.26666668F);
        public static final Climate.Parameter MID_SLICE_VARIANT_ASCENDING = span(0.26666668F, 0.4F);
        public static final Climate.Parameter HIGH_SLICE_VARIANT_ASCENDING = span(0.4F, 0.56666666F);
        public static final Climate.Parameter PEAK_VARIANT = span(0.56666666F, 0.7666667F);
        public static final Climate.Parameter HIGH_SLICE_VARIANT_DESCENDING = span(0.7666667F, 0.93333334F);
        public static final Climate.Parameter MID_SLICE_VARIANT_DESCENDING = span(0.93333334F, 1.0F);
        public static final Climate.Parameter FULL_RANGE = ParameterBuilder.FULL_RANGE;

        public static Climate.Parameter span(float min, float max) {
            return ParameterBuilder.span(min, max);
        }

        public static Climate.Parameter span(Climate.Parameter from, Climate.Parameter to) {
            return ParameterBuilder.span(from, to);
        }
    }
}
