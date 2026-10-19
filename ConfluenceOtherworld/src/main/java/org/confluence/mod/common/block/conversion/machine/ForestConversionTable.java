package org.confluence.mod.common.block.conversion.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.block.conversion.ConversionTable;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.jetbrains.annotations.Nullable;

public class ForestConversionTable extends ConversionTable {
    @Override
    protected @Nullable Block getTarget(BlockState source, boolean hardmode) {
        if (source.is(NatureBlocks.CORRUPTION_THORN.get())
                || source.is(NatureBlocks.CRIMSON_THORN.get())
                || source.is(NatureBlocks.PLANTERA_THORN.get())) {
            return Blocks.AIR;
        }
        if (source.is(BlockTags.BASE_STONE_OVERWORLD)
                || source.is(Blocks.STONE)
                || source.is(Blocks.SANDSTONE)
                || source.is(Blocks.RED_SANDSTONE)
                || source.is(Blocks.ICE)
                || source.is(Blocks.PACKED_ICE)
                || source.is(Blocks.BLUE_ICE)
                || source.is(ModTags.Blocks.PURE_CONVERSION_SANDSTONE)
                || source.is(ModTags.Blocks.PURE_CONVERSION_ICE)
                || source.is(ModTags.Blocks.PURE_CONVERSION_PACKED_ICE)) {
            return Blocks.STONE;
        }
        if (source.is(Blocks.GRASS_BLOCK) || source.is(ModTags.Blocks.PURE_CONVERSION_GRASS_BLOCK)) {
            return Blocks.GRASS_BLOCK;
        }
        if (source.is(Blocks.DIRT)
                || source.is(Blocks.COARSE_DIRT)
                || source.is(Blocks.PODZOL)
                || source.is(Blocks.MYCELIUM)
                || source.is(Blocks.SAND)
                || source.is(Blocks.RED_SAND)
                || source.is(Blocks.SNOW_BLOCK)
                || source.is(NatureBlocks.HARDENED_SAND_BLOCK.get())
                || source.is(NatureBlocks.MOISTENED_SAND_BLOCK.get())
                || source.is(NatureBlocks.SAND_LAYER_BLOCK.get())
                || source.is(NatureBlocks.RED_SAND_LAYER_BLOCK.get())
                || source.is(NatureBlocks.EBONSAND_LAYER_BLOCK.get())
                || source.is(NatureBlocks.CRIMSAND_LAYER_BLOCK.get())
                || source.is(NatureBlocks.PEARLSAND_LAYER_BLOCK.get())
                || source.is(ModTags.Blocks.PURE_CONVERSION_SAND)
                || source.is(ModTags.Blocks.PURE_CONVERSION_HARDENED_SAND_BLOCK)
                || source.is(ModTags.Blocks.PURE_CONVERSION_MOIST_SAND_BLOCK)) {
            return Blocks.DIRT;
        }
        return null;
    }

    // 土层接触空气时长成草皮；埋在地下时保持泥土，邻接结果不写入状态缓存。
    @Override
    public @Nullable BlockState get(BlockState source, boolean hardmode, LevelReader level, BlockPos pos) {
        BlockState target = super.get(source, hardmode);
        if (target != null && target.is(Blocks.DIRT)) {
            for (Direction direction : Direction.values()) {
                BlockPos neighbor = pos.relative(direction);
                if (level.hasChunkAt(neighbor) && !level.getBlockState(neighbor).isSolidRender(level, neighbor)) {
                    return Blocks.GRASS_BLOCK.defaultBlockState();
                }
            }
        }
        return target;
    }
}
