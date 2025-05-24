package org.confluence.mod.common.block.natural.spreadable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.init.block.NatureBlocks;

public class MushroomGrassBlock extends SpreadingGrassBlock implements BonemealableBlock {
    public MushroomGrassBlock() {
        super(ISpreadable.Type.GLOWING, Properties.ofFullCopy(Blocks.MUD));
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        if (!serverLevel.isAreaLoaded(blockPos, 3)) return;
        if (isFullBlock(serverLevel, blockPos.above())) {
            serverLevel.setBlockAndUpdate(blockPos, Blocks.MUD.defaultBlockState());
        } else {
            super.randomTick(blockState, serverLevel, blockPos, randomSource);
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return level.getBlockState(pos.above()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        BlockState plantToGrow = NatureBlocks.GLOWING_MUSHROOM.get().defaultBlockState();
        int attempts = 128;
        int range = 4;
        for (int i = 0; i < attempts; i++) {
            int dx = random.nextInt(range * 2 + 1) - range;
            int dz = random.nextInt(range * 2 + 1) - range;
            int dy = random.nextInt(2);
            BlockPos targetPos = pos.offset(dx, dy, dz);
            BlockState targetBlockState = level.getBlockState(targetPos);
            if (targetBlockState.isAir() && plantToGrow.canSurvive(level, targetPos)) {
                level.setBlock(targetPos, plantToGrow, 3);
            }
        }
    }

}
