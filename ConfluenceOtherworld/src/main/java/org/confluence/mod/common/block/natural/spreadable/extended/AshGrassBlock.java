package org.confluence.mod.common.block.natural.spreadable.extended;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.extensions.IForgeBlockState;
import org.confluence.mod.common.block.natural.spreadable.SpreadingGrassBlock;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.jetbrains.annotations.Nullable;

public class AshGrassBlock extends SpreadingGrassBlock implements IForgeBlockState {
    public AshGrassBlock() {
        super(Type.ASH, Properties.copy(Blocks.GRASS_BLOCK).mapColor(MapColor.TERRACOTTA_ORANGE));
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 3)) return;
        if (isFullBlock(level, pos.above())) {
            level.setBlockAndUpdate(pos, NatureBlocks.ASH_BLOCK.get().defaultBlockState());
        } else {
            super.randomTick(state, level, pos, random);
        }
    }

    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        if (toolAction == ToolActions.SHOVEL_FLATTEN) {
            return NatureBlocks.ASH_PATH.get().defaultBlockState();
        }
        return null;
    }
}
