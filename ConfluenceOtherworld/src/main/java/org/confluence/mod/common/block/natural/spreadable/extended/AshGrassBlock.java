package org.confluence.mod.common.block.natural.spreadable.extended;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.confluence.mod.common.block.natural.spreadable.SpreadingGrassBlock;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.jetbrains.annotations.Nullable;

public class AshGrassBlock extends SpreadingGrassBlock {
    public AshGrassBlock() {
        super(Type.ASH, Properties.ofFullCopy(Blocks.GRASS_BLOCK).mapColor(MapColor.TERRACOTTA_ORANGE));
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        if (!serverLevel.isAreaLoaded(blockPos, 3)) return;
        if (isFullBlock(serverLevel, blockPos.above())) {
            serverLevel.setBlockAndUpdate(blockPos, NatureBlocks.ASH_BLOCK.get().defaultBlockState());
        } else {
            super.randomTick(blockState, serverLevel, blockPos, randomSource);
        }
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        if (itemAbility == ItemAbilities.SHOVEL_FLATTEN) {
            return NatureBlocks.ASH_PATH.get().defaultBlockState();
        }
        return null;
    }
}
