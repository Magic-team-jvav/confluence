package org.confluence.mod.util;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.lib.common.worldgen.biome.BlockCounters;
import org.confluence.lib.common.worldgen.biome.BlockCounts;
import org.confluence.lib.mixed.ILevelChunkSection;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.block.OreBlocks;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.common.PortTags;

import java.util.function.Predicate;

/// Confluence 自己的方块计数器（对应泰拉瑞亚 `SceneMetrics` 里那套 tile 统计）。
///
/// 计数机制在 magiclib（{@link BlockCounters}），这里只做注册：
/// 第三方模组可以照这个写法加自己的计数器，不需要改 magiclib。
///
/// **注册必须在模组初始化阶段完成**（本类的静态初始化即可），
/// 计数数组长度在第一个区块 section 创建时固化。
public final class ModBlockCounters {
    /// 墓地判定阈值：墓碑数减去向日葵数达到这个值算墓地
    public static final int GRAVEYARD_THRESHOLD = 7;

    public static final BlockCounters.Counter CRIMSON = register("crimson", block -> block.is(ModTags.Blocks.CRIMSON_BLOCKS));
    public static final BlockCounters.Counter CRIMSON_SAND = register("crimson_sand", block -> block.is(ModTags.Blocks.CRIMSON_DESERT_BLOCKS));
    public static final BlockCounters.Counter CRIMSON_ICE = register("crimson_ice", block -> block.is(ModTags.Blocks.CRIMSON_TUNDRA_BLOCKS));
    public static final BlockCounters.Counter CORRUPT = register("corrupt", block -> block.is(ModTags.Blocks.CORRUPTION_BLOCKS));
    public static final BlockCounters.Counter CORRUPT_SAND = register("corrupt_sand", block -> block.is(ModTags.Blocks.CORRUPTED_DESERT_BLOCKS));
    public static final BlockCounters.Counter CORRUPT_ICE = register("corrupt_ice", block -> block.is(ModTags.Blocks.CORRUPTED_TUNDRA_BLOCKS));
    public static final BlockCounters.Counter HALLOW = register("hallow", block -> block.is(ModTags.Blocks.HALLOW_BLOCKS));
    public static final BlockCounters.Counter HALLOW_SAND = register("hallow_sand", block -> block.is(ModTags.Blocks.HALLOW_DESERT_BLOCKS));
    public static final BlockCounters.Counter HALLOW_ICE = register("hallow_ice", block -> block.is(ModTags.Blocks.HALLOW_TUNDRA_BLOCKS));
    public static final BlockCounters.Counter GLOWING_MUSHROOM = register("glowing_mushroom", block -> block.is(ModTags.Blocks.GLOWING_MUSHROOM_BLOCKS));
    public static final BlockCounters.Counter SUNFLOWER = register("sunflower", block -> block.is(Blocks.SUNFLOWER));
    public static final BlockCounters.Counter TOMB = register("tomb", block -> block.is(ModTags.Blocks.TOMBSTONE));
    public static final BlockCounters.Counter WATER = register("water", block -> block.getFluidState().is(PortTags.Fluids.WATER));
    public static final BlockCounters.Counter CHLOROPHYTE = register("chlorophyte", block -> block.is(OreBlocks.CHLOROPHYTE_ORE.get()));

    // ---- 迷你生物群系用的计数器 ----

    /// 蜘蛛窝：蛛网
    public static final BlockCounters.Counter COBWEB = register("cobweb", block -> block.is(Blocks.COBWEB));
    /// 花岗岩洞：**只认 `NatureBlocks.GRANITE`**。
    ///
    /// 全仓只有 `GraniteCaveStructure` 会放置这个方块，所以它是"签名方块" ——
    /// 判出来的一定是花岗岩洞（或玩家自己砌的花岗岩）。
    /// 原版 `Blocks.GRANITE` 地形在窗口里随便就有几万格（实测在陨石坑附近 50584），
    /// 混进来只会让任何地下都判成花岗岩洞，所以不数。
    public static final BlockCounters.Counter GRANITE = register("granite", block -> block.is(NatureBlocks.GRANITE.get()));
    /// 大理石洞：结构是用**原版方解石**造的（`MarbleCaveStructure` 的调色板是 `[AIR, CALCITE]`），
    /// `NatureBlocks.MARBLE` 只是方解石的切石产物，所以天然大理石洞里必须靠方解石来认。
    /// 注意：原版紫水晶洞也有方解石外壳，阈值要压过它的量。
    public static final BlockCounters.Counter MARBLE = register("marble", block ->
            block.is(Blocks.CALCITE) || block.is(NatureBlocks.MARBLE.get()));
    /// 蜂巢
    public static final BlockCounters.Counter HIVE = register("hive", block -> block.is(NatureBlocks.JUNGLE_HIVE_BLOCK.get()));
    /// 蜂巢里的蜂蜜
    public static final BlockCounters.Counter HONEY = register("honey", block -> block.getFluidState().is(PortTags.Fluids.HONEY));
    /// 微光
    public static final BlockCounters.Counter SHIMMER = register("shimmer", block -> block.getFluidState().is(ModTags.Fluids.SHIMMER));
    /// 陨石
    public static final BlockCounters.Counter METEORITE = register("meteorite", block ->
            block.is(OreBlocks.METEORITE_ORE.get()) || block.is(OreBlocks.RAW_METEORITE_BLOCK.get()) || block.is(OreBlocks.METEORITE_BLOCK.get()));

    private ModBlockCounters() {}

    /// 强制触发本类的静态初始化，从而完成计数器注册。
    ///
    /// 必须在**任何区块 section 创建之前**调用：计数数组长度在第一个 `BlockCounts` 分配时固化，
    /// 若那时注册表还是空的，之后注册会抛异常。
    public static void init() {}

    private static BlockCounters.Counter register(String path, Predicate<BlockState> predicate) {
        return BlockCounters.register(Confluence.asResource(path), predicate);
    }

    /// 有效墓碑数：向日葵会抵消墓碑
    public static int effectiveTombstones(BlockCounts counts) {
        return TOMB.get(counts) - SUNFLOWER.get(counts);
    }

    /// 墓地判定：墓碑数（向日葵会抵消）达到阈值。
    ///
    /// 这是 Confluence 的语义，所以放在模组侧；magiclib 只负责计数。
    public static boolean isGraveyard(@Nullable ILevelChunkSection section) {
        return section != null && effectiveTombstones(section.confluence$getBlockCounts()) >= GRAVEYARD_THRESHOLD;
    }
}
