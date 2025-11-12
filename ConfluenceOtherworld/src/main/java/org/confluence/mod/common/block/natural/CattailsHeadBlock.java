package org.confluence.mod.common.block.natural;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.jetbrains.annotations.Nullable;

public class CattailsHeadBlock extends GrowingPlantHeadBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<CattailsHeadBlock> CODEC = simpleCodec(CattailsHeadBlock::new);
    public static final IntegerProperty AGE = BlockStateProperties.AGE_25;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty PLACE = BooleanProperty.create("place");
    protected static final VoxelShape SHAPE = box(2.0, 0.0, 2.0, 14.0, 10.0, 14.0);
    protected static final int MAX_AGE = 3;

    public CattailsHeadBlock(Properties properties) {
        super(properties, Direction.UP, SHAPE, true, 0.20);
        registerDefaultState(stateDefinition.any().setValue(PLACE, false).setValue(WATERLOGGED, false));
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int currentAge = state.getValue(AGE);
        BlockState lastState = getBodyBlock().defaultBlockState();
        BlockState aboveState = level.getBlockState(pos.above());
        boolean isAirUp = aboveState.isAir();
        boolean isAirNow = level.getBlockState(pos).getValue(WATERLOGGED);
        if (currentAge >= 3 || !(aboveState.isAir() || aboveState.is(Blocks.WATER))) return;
        if (isAirUp && isAirNow) {
            level.setBlock(pos.above(), state.trySetValue(WATERLOGGED, false).trySetValue(AGE, random.nextInt(1, 4)), 3);
        } else {
            level.setBlock(pos.above(), state.trySetValue(WATERLOGGED, !isAirUp).trySetValue(AGE, isAirUp ? (currentAge + 1) : 0), 3);
        }
        level.setBlock(pos, lastState.trySetValue(WATERLOGGED, isAirNow), 3);
    }

    @Override
    public BlockState getStateForPlacement(LevelAccessor level) {
        return defaultBlockState().setValue(AGE, level.getRandom().nextInt(MAX_AGE));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        if (state.is(this)) return state.setValue(PLACE, true);
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        if (fluidstate.is(FluidTags.WATER) && fluidstate.getAmount() == 8) {
            BlockState newState = super.getStateForPlacement(context);
            if (newState != null) return newState.setValue(PLACE, true).setValue(WATERLOGGED, true);
        }
        return null;
    }

    @Override
    protected Block getBodyBlock() {
        BlockState state = this.defaultBlockState();
        if (state.is(NatureBlocks.CATTAILS_HEAD.get())) {
            return NatureBlocks.CATTAILS_BODY.get();
        } else if (state.is(NatureBlocks.JUNGLE_CATTAILS_HEAD.get())) {
            return NatureBlocks.JUNGLE_CATTAILS_BODY.get();
        } else if (state.is(NatureBlocks.GLOWING_MUSHROOM_CATTAILS_HEAD.get())) {
            return NatureBlocks.GLOWING_MUSHROOM_CATTAILS_BODY.get();
        } else if (state.is(NatureBlocks.HALLOW_CATTAILS_HEAD.get())) {
            return NatureBlocks.HALLOW_CATTAILS_BODY.get();
        } else if (state.is(NatureBlocks.EBONY_CATTAILS_HEAD.get())) {
            return NatureBlocks.EBONY_CATTAILS_BODY.get();
        } else if (state.is(NatureBlocks.CRIMSON_CATTAILS_HEAD.get())) {
            return NatureBlocks.CRIMSON_CATTAILS_BODY.get();
        } else {
            return NatureBlocks.CATTAILS_BODY.get();
        }
    }

    @Override
    protected boolean canAttachTo(BlockState state) {
        return !state.is(Blocks.MAGMA_BLOCK);
    }

    @Override
    protected int getBlocksToGrowWhenBonemealed(RandomSource random) {
        return 1;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    protected boolean canGrowInto(BlockState state) {
        return state.is(Blocks.AIR);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        BlockPos growPos = pos.relative(this.growthDirection);
        int currentAge = state.getValue(AGE);
        BlockState targetState = level.getBlockState(growPos);
        FluidState targetFluid = targetState.getFluidState();
        boolean isTargetWater = targetFluid.is(FluidTags.WATER) && targetFluid.getAmount() == 8;
        if (currentAge >= MAX_AGE) {
            if (isTargetWater || canGrowInto(targetState)) {
                level.setBlockAndUpdate(pos, state.setValue(AGE, 0));
            }
        } else if (isTargetWater || canGrowInto(targetState)) {
            level.setBlockAndUpdate(growPos, getGrowIntoState(state, random).setValue(WATERLOGGED, isTargetWater));
        }
    }

    @Override
    protected MapCodec<CattailsHeadBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, PLACE, WATERLOGGED);
    }

    @Override
    protected BlockState updateBodyAfterConvertedFromHead(BlockState head, BlockState body) {
        return body.setValue(WATERLOGGED, head.getValue(WATERLOGGED));
    }
}
