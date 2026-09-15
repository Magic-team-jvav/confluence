package org.confluence.mod.common.worldgen.biome;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.common.worldgen.biome.injector.BiomeRegion;
import org.confluence.mod.common.worldgen.biome.injector.BiomeRegionType;

import java.util.Set;
import java.util.function.Consumer;

import static org.confluence.mod.common.worldgen.biome.injector.ParameterBuilder.*;

/// 地下发光蘑菇群系的主世界噪声区域。
///
/// ## 只用深度闸
///
/// 洞穴群系的定义条件就是「在地下多深」，所以这里**只保留 `depth`**，其余五条气候轴全部放开。
/// 这既符合泰拉瑞亚（发光蘑菇地跟地表是什么群系无关），也是唯一能让它稳定被找到的写法：
/// 其余气候轴都是**平滑大尺度场**，在 `/locate biome` 那 6400 格窗口里近似常量，
/// 只要玩家站的地方不在区间内，整个窗口就是 0 命中。诊断日志里
/// `owns quart column ... but no parameter box covers H=514` 就是这种「整段出界」。
///
/// ## 生成率
///
/// ```
/// 区域带宽 weight 1 / 总计 30 = 3.3% 的列
///   × 深度 UNDERGROUND [0.2, 0.9]（地表以下 26–115 格，约 90 格厚）≈ 占整个地下体积 70%
/// ⇒ 整个地下体积的约 2.3%
/// ```
///
/// 换成人话：**往下挖 26 格以上的洞穴，约 2% 的体积是蘑菇地**，挖洞时能碰上但不至于到处都是。
/// {@link #WEIGHT} 是单个区域的旋钮，但**整体缩放要看 `BiomeRegionType#vanillaWeight()`**
/// —— 只有相对比例有意义，且区域权重最小是 1（详见那边的文档）。
///
/// 深度闸特意取到 26 格这么浅，有两个原因：一是这个带厚约 90 格，
/// 比 `/locate biome` 的 y 采样步长（64 格）厚，保证搜索不会整条漏掉；
/// 二是泰拉瑞亚的发光蘑菇地也常常出现在地下层而不只是最深处的洞穴层。
///
/// ## 与旧声明的差异
///
/// 旧声明是「温度 `point(-0.10)` + 湿度 `[0.3,0.4]` + 远内陆 + 侵蚀度 `[0.05,0.55]` +
/// 深度 `[0.6,0.9]`」五条窄轴相乘，实测对全世界约万分之一，等同于不生成。
public final class GlowingMushroomRegion implements BiomeRegion {
    public static final ResourceLocation ID = Confluence.asResource("glowing_mushroom");

    /// 相对份额。单个区域的旋钮；整体缩放请改 {@link BiomeRegionType#vanillaWeight()}。
    public static final int WEIGHT = 1;

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public int weight() {
        return WEIGHT;
    }

    @Override
    public Set<ResourceKey<Biome>> biomes() {
        return Set.of(ModBiomes.GLOWING_MUSHROOM);
    }

    @Override
    public void addBiomes(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> consumer) {
        add(consumer,
                Temperature.FULL_RANGE,
                Humidity.FULL_RANGE,
                Continentalness.FULL_RANGE,
                Erosion.FULL_RANGE,
                UNDERGROUND,
                Weirdness.FULL_RANGE,
                ModBiomes.GLOWING_MUSHROOM);
    }
}
