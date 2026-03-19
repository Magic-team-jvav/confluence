package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.confluence.mod.common.init.block.NatureBlocks;

import java.util.function.Supplier;

@SuppressWarnings("deprecation")
public class EndGrassBlock extends Block {
    private final Supplier<? extends Block> degenerateTo;

    public EndGrassBlock(Supplier<? extends Block> degenerateTo) {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT).mapColor(MapColor.COLOR_LIGHT_GRAY).randomTicks());
        this.degenerateTo = degenerateTo;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 3)) return;
        boolean isInverse = state.is(NatureBlocks.INVERSE_GRASS_BLOCK.get());
        BlockPos checkPos = isInverse ? pos.below() : pos.above();
        if (level.getBlockState(checkPos).isSolid()) {
            level.setBlockAndUpdate(pos, degenerateTo.get().defaultBlockState());
        }
    }
}
