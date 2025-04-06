package org.confluence.mod.common.block.natural;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.init.block.NatureBlocks;

public class CattailsBodyBlock extends GrowingPlantBodyBlock implements SimpleWaterloggedBlock, BonemealableBlock {
    public static final MapCodec<CattailsBodyBlock> CODEC = simpleCodec(CattailsBodyBlock::new);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

    public CattailsBodyBlock(BlockBehaviour.Properties properties) {
        super(properties, Direction.UP, SHAPE, true);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockPos blockpos = pos.relative(this.growthDirection);
        if (level.getBlockState(blockpos).is(Blocks.WATER)) {
            state = state.setValue(WATERLOGGED, true);
        }
        level.setBlockAndUpdate(pos, state);
    }

    @Override
    protected MapCodec<CattailsBodyBlock> codec() {
        return CODEC;
    }

    @Override
    protected GrowingPlantHeadBlock getHeadBlock() {
        BlockState state = this.defaultBlockState();
        if (state.is(NatureBlocks.CATTAILS_BODY.get())) {
            return NatureBlocks.CATTAILS_HEAD.get();
        } else if (state.is(NatureBlocks.JUNGLE_CATTAILS_BODY.get())) {
            return NatureBlocks.JUNGLE_CATTAILS_HEAD.get();
        } else if (state.is(NatureBlocks.GLOWING_MUSHROOM_CATTAILS_BODY.get())) {
            return NatureBlocks.GLOWING_MUSHROOM_CATTAILS_HEAD.get();
        } else if (state.is(NatureBlocks.HALLOW_CATTAILS_BODY.get())) {
            return NatureBlocks.HALLOW_CATTAILS_HEAD.get();
        } else if (state.is(NatureBlocks.EBONY_CATTAILS_BODY.get())) {
            return NatureBlocks.EBONY_CATTAILS_HEAD.get();
        } else if (state.is(NatureBlocks.TR_CRIMSON_CATTAILS_BODY.get())) {
            return NatureBlocks.TR_CRIMSON_CATTAILS_HEAD.get();
        } else {
            return NatureBlocks.CATTAILS_HEAD.get();
        }
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource randomSource, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }
}
