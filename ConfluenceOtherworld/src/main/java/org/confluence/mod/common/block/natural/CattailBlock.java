package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.init.ModTags;

import javax.annotation.Nullable;

public class CattailBlock extends Block implements SimpleWaterloggedBlock, BonemealableBlock {
    public static final EnumProperty<CattailPart> PART = EnumProperty.create("part", CattailPart.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty AIR_HEIGHT = IntegerProperty.create("air_height", 0, 3);
    public static final IntegerProperty MAX_AIR_HEIGHT = IntegerProperty.create("max_air_height", 1, 3);

    protected static final VoxelShape SHAPE = box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
    protected static final VoxelShape TOP_SHAPE = box(2.0, 0.0, 2.0, 14.0, 10.0, 14.0);

    public CattailBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(PART, CattailPart.TOP)
                .setValue(WATERLOGGED, false)
                .setValue(AIR_HEIGHT, 0)
                .setValue(MAX_AIR_HEIGHT, 3));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART, WATERLOGGED, AIR_HEIGHT, MAX_AIR_HEIGHT);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(PART) == CattailPart.TOP ? TOP_SHAPE : SHAPE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        FluidState fluid = level.getFluidState(pos);
        BlockState below = level.getBlockState(pos.below());
        if (below.is(this)) {
            boolean emerging = below.getValue(WATERLOGGED) && !fluid.is(FluidTags.WATER);
            int maxHeight = emerging ? level.random.nextInt(1, 4) : below.getValue(MAX_AIR_HEIGHT);
            int rawHeight = emerging ? 1 : (fluid.is(FluidTags.WATER) ? 0 : below.getValue(AIR_HEIGHT) + 1);
            int safeHeight = Math.min(rawHeight, 3);
            return getCattailState(fluid.is(FluidTags.WATER), safeHeight, maxHeight);
        } else if (fluid.is(FluidTags.WATER) && below.isFaceSturdy(level, pos.below(), Direction.UP) && below.is(ModTags.Blocks.CATTAIL_CAN_SURVIVE)) {
            return getCattailState(true, 0, 1);
        }
        return null;
    }

    private BlockState getCattailState(boolean waterlogged, int airHeight, int maxAirHeight) {
        return this.defaultBlockState()
                .setValue(WATERLOGGED, waterlogged)
                .setValue(AIR_HEIGHT, airHeight)
                .setValue(MAX_AIR_HEIGHT, maxAirHeight)
                .setValue(PART, CattailPart.TOP);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(PART) == CattailPart.TOP && state.getValue(AIR_HEIGHT) < state.getValue(MAX_AIR_HEIGHT) && random.nextFloat() < 0.15F) {
            this.grow(level, pos, 1);
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        if (!belowState.is(this)) {
            return level.getFluidState(pos).is(FluidTags.WATER) && belowState.isFaceSturdy(level, below, Direction.UP) && belowState.is(ModTags.Blocks.CATTAIL_CAN_SURVIVE);
        }
        return true;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!level.isClientSide() && !oldState.is(state.getBlock())) {
            BlockState updatedState = state.updateShape(Direction.UP, level.getBlockState(pos.above()), level, pos, pos.above());
            if (updatedState != state) {
                level.setBlock(pos, updatedState, 3);
            }
            level.neighborChanged(pos.above(), this, pos);
            level.neighborChanged(pos.below(), this, pos);
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED))
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        if (facing == Direction.DOWN && !this.canSurvive(state, level, currentPos))
            return Blocks.AIR.defaultBlockState();

        if (facing == Direction.UP) {
            if (facingState.is(this)) {
                if (state.getValue(PART) == CattailPart.TOP) {
                    CattailPart bodyPart = state.getValue(WATERLOGGED) ?
                            CattailPart.SUBMERGED : (state.getValue(AIR_HEIGHT) == 1 ? CattailPart.TRANSITION : CattailPart.STEM);
                    state = state.setValue(PART, bodyPart);
                }
            } else if (facingState.isAir() || !facingState.is(this)) {
                state = state.setValue(PART, CattailPart.TOP);
            }
        }
        if (facing.getAxis() == Direction.Axis.Y && facingState.is(this)) {
            state = state.setValue(MAX_AIR_HEIGHT, facingState.getValue(MAX_AIR_HEIGHT));
        }
        return state;
    }

    private void grow(ServerLevel level, BlockPos pos, int steps) {
        BlockPos.MutableBlockPos buildPos = pos.mutable();
        while (buildPos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(buildPos.above()).is(this))
            buildPos.move(Direction.UP);
        BlockState currentTop = level.getBlockState(buildPos);
        int currentAirH = currentTop.getValue(AIR_HEIGHT);
        int maxH = currentTop.getValue(MAX_AIR_HEIGHT);
        for (int i = 0; i < steps; i++) {
            BlockPos above = buildPos.above();
            if (above.getY() >= level.getMaxBuildHeight()) break;
            FluidState fluidAbove = level.getFluidState(above);
            boolean isWater = fluidAbove.is(FluidTags.WATER);
            if (!isWater) {
                if (currentAirH == 0) maxH = level.random.nextInt(1, 4);
                if (!level.isEmptyBlock(above) || currentAirH >= maxH) break;
            }
            buildPos.move(Direction.UP);
            if (!isWater) currentAirH = Math.min(currentAirH + 1, 3);
            level.setBlock(buildPos, getCattailState(isWater, isWater ? 0 : currentAirH, maxH), 3);
            if (!isWater) break;
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        BlockPos.MutableBlockPos topPos = pos.mutable();
        while (level.getBlockState(topPos.above()).is(this)) topPos.move(Direction.UP);
        BlockState topState = level.getBlockState(topPos);
        return topState.getValue(AIR_HEIGHT) < topState.getValue(MAX_AIR_HEIGHT);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        this.grow(level, pos, random.nextInt(2, 4));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public enum CattailPart implements StringRepresentable {
        SUBMERGED("submerged"),
        TRANSITION("transition"),
        STEM("stem"),
        TOP("top");
        private final String name;

        CattailPart(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
