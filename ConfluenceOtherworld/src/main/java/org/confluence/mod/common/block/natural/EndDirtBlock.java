package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.confluence.mod.common.init.block.NatureBlocks;

public class EndDirtBlock extends Block implements BonemealableBlock {

    public EndDirtBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.SAND).mapColor(MapColor.COLOR_LIGHT_GRAY));
    }


    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        if (level.getBlockState(pos.above()).propagatesSkylightDown(level, pos)) {
            for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
                if (level.getBlockState(blockpos).is(NatureBlocks.VOID_GRASS_BLOCK.get())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        boolean flag = false;

        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
            BlockState blockstate = level.getBlockState(blockpos);
            if (blockstate.is(NatureBlocks.VOID_GRASS_BLOCK.get())) {
                flag = true;
                break;
            }
        }

        if (flag) {
            level.setBlock(pos, NatureBlocks.VOID_GRASS_BLOCK.get().defaultBlockState(), 3);
        }
    }

    @Override
    public BonemealableBlock.Type getType() {
        return BonemealableBlock.Type.NEIGHBOR_SPREADER;
    }
}
