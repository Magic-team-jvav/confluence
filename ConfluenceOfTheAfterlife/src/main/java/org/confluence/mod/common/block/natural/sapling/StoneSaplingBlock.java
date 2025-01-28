package org.confluence.mod.common.block.natural.sapling;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;

public class StoneSaplingBlock extends SaplingBlock {
    public StoneSaplingBlock(TreeGrower pTreeGrower, Properties pProperties) {
        super(pTreeGrower, pProperties);
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockPos below = pPos.below();
        BlockState blockBelow = pLevel.getBlockState(below);
        return blockBelow.is(Blocks.DEEPSLATE) || blockBelow.is(Blocks.SCULK) || blockBelow.is(Blocks.STONE);
    }
}
