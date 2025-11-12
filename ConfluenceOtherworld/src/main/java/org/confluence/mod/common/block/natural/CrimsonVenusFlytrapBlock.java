package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.confluence.mod.common.init.block.NatureBlocks;

public class CrimsonVenusFlytrapBlock extends Block {
    public CrimsonVenusFlytrapBlock() {
        super(BlockBehaviour.Properties.of().strength(1.0f).noCollission().pushReaction(PushReaction.DESTROY));
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide) entity.hurt(level.damageSources().magic(), 2.0F);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState stateBelow = level.getBlockState(pos.below());
        return isGrassBlock(stateBelow);
    }

    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos)) level.destroyBlock(pos, true);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (!state.canSurvive(level, currentPos)) level.scheduleTick(currentPos, this, 1);
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    private boolean isGrassBlock(BlockState blockState) {
        return blockState.is(NatureBlocks.CRIMSON_GRASS_BLOCK) || blockState.is(NatureBlocks.CRIMSON_JUNGLE_GRASS_BLOCK);
    }
}
