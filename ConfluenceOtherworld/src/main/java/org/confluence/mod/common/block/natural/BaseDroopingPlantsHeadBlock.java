package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.init.ModTags;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class BaseDroopingPlantsHeadBlock extends GrowingPlantHeadBlock implements SimpleWaterloggedBlock {
    public static final EnumProperty<VinePart> PART = EnumProperty.create("part", VinePart.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;


    protected final int side;
    protected final int maxAgeValue;
    protected final Direction growthDirection;
    protected final boolean isNaturalGrowth;
    protected final boolean isClimbable;
    protected final Supplier<List<Block>> attachedBlocksSupplier;
    private final int lightLevel;

    private BaseDroopingPlantsHeadBlock(int side, int maxAge, Direction growthDirection, boolean isNaturalGrowth, boolean isClimbable, Supplier<List<Block>> attachedBlocksSupplier, int lightLevel) {
        super(Properties.of()
                        .noCollission()
                        .instabreak()
                        .sound(SoundType.GRASS)
                        .pushReaction(PushReaction.DESTROY)
                        .randomTicks()
                        .lightLevel((state) -> lightLevel)
                , growthDirection, createShape(side, growthDirection), true, 0.1);
        this.side = side;
        this.maxAgeValue = Math.min(maxAge, 25);
        this.growthDirection = growthDirection;
        this.isNaturalGrowth = isNaturalGrowth;
        this.isClimbable = isClimbable;
        this.attachedBlocksSupplier = attachedBlocksSupplier;
        this.lightLevel = lightLevel;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AGE, 0)
                .setValue(WATERLOGGED, false)
                .setValue(PART, VinePart.HEAD));
    }

    public BaseDroopingPlantsHeadBlock(int side, boolean isNaturalGrowth, boolean isClimbable) {
        this(side, 25, Direction.DOWN, isNaturalGrowth, isClimbable, List::of, 0);
    }

    public BaseDroopingPlantsHeadBlock(int side, Direction growthDirection, boolean isNaturalGrowth, boolean isClimbable) {
        this(side, 25, growthDirection, isNaturalGrowth, isClimbable, List::of, 0);
    }

    public BaseDroopingPlantsHeadBlock(int side, boolean isNaturalGrowth, boolean isClimbable, Supplier<List<Block>> attachedBlock) {
        this(side, 25, Direction.DOWN, isNaturalGrowth, isClimbable, attachedBlock, 0);
    }

    public BaseDroopingPlantsHeadBlock(int side, Direction growthDirection, boolean isNaturalGrowth, boolean isClimbable, Supplier<List<Block>> attachedBlock) {
        this(side, 25, growthDirection, isNaturalGrowth, isClimbable, attachedBlock, 0);
    }

    public BaseDroopingPlantsHeadBlock(int side, Direction growthDirection, boolean isNaturalGrowth, boolean isClimbable, Supplier<List<Block>> attachedBlock, int lightLevel) {
        this(side, 25, growthDirection, isNaturalGrowth, isClimbable, attachedBlock, lightLevel);
    }

    public BaseDroopingPlantsHeadBlock(int side, int maxAge, Direction growthDirection, boolean isNaturalGrowth, boolean isClimbable) {
        this(side, maxAge, growthDirection, isNaturalGrowth, isClimbable, List::of, 0);
    }

    @Override
    protected int getBlocksToGrowWhenBonemealed(RandomSource random) {
        return NetherVines.getBlocksToGrowWhenBonemealed(random);
    }

    @Override
    protected boolean canGrowInto(BlockState state) {
        return state.isAir() || state.is(Blocks.WATER);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos supportPos = pos.relative(growthDirection.getOpposite());
        BlockState supportState = level.getBlockState(supportPos);
        if (supportState.is(this)) return true;
        return isAnchor(supportState);
    }

    private boolean isAnchor(BlockState state) {
        List<Block> anchors = attachedBlocksSupplier.get();
        if (anchors != null && !anchors.isEmpty()) {
            for (Block block : anchors) {
                if (state.is(block)) return true;
            }
        }
        return state.is(ModTags.Blocks.DROOPING_VINE_CAN_SURVIVE);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        BlockState updatedState = super.updateShape(state, facing, facingState, level, currentPos, facingPos);
        if (updatedState.isAir()) return updatedState;
        return calculatePart(level, currentPos, updatedState);
    }

    private BlockState calculatePart(LevelReader level, BlockPos pos, BlockState state) {
        boolean hasVineBelow = level.getBlockState(pos.relative(growthDirection)).is(this);
        boolean isSupportedByAnchor = isAnchor(level.getBlockState(pos.relative(growthDirection.getOpposite())));
        if (!hasVineBelow) {
            return state.setValue(PART, VinePart.HEAD);
        } else {
            if (isSupportedByAnchor && !level.getBlockState(pos.relative(growthDirection.getOpposite())).is(this)) {
                return state.setValue(PART, VinePart.TAIL);
            }
            return state.setValue(PART, VinePart.BODY);
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return this.isNaturalGrowth && state.getValue(AGE) < this.maxAgeValue;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelAccessor level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = this.defaultBlockState();
        if (!this.canSurvive(state, level, pos)) return null;
        boolean isWater = level.getFluidState(pos).getType() == Fluids.WATER;
        state = state.setValue(WATERLOGGED, isWater).setValue(AGE, level.getRandom().nextInt(maxAgeValue));
        return calculatePart(level, pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED, PART);
    }

    @Override
    protected Block getBodyBlock() {return this;}

    @Override
    public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
        return isClimbable;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    private static VoxelShape createShape(int side, Direction direction) {
        double margin = (16.0 - side) / 2.0;
        return switch (direction.getAxis()) {
            case X -> Block.box(0.0, margin, margin, 16.0, 16.0 - margin, 16.0 - margin);
            case Y -> Block.box(margin, 0.0, margin, 16.0 - margin, 16.0, 16.0 - margin);
            case Z -> Block.box(margin, margin, 0.0, 16.0 - margin, 16.0 - margin, 16.0);
        };
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClientSide) {
        return state.getValue(AGE) < this.maxAgeValue;
    }

    public enum VinePart implements StringRepresentable {
        HEAD("head"),
        BODY("body"),
        TAIL("tail");
        private final String name;

        VinePart(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
