package org.confluence.mod.common.block.natural;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.init.ModTags;

import java.util.Arrays;

import static net.neoforged.neoforge.common.CommonHooks.canCropGrow;

public class BaseDroopingPlantsHeadBlock extends GrowingPlantHeadBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<BaseDroopingPlantsHeadBlock> CODEC = RecordCodecBuilder.mapCodec(
        builder -> builder.group(
                Codec.INT.fieldOf("side").forGetter(baseDroopingPlantsheadBlock -> baseDroopingPlantsheadBlock.side),
                Codec.INT.fieldOf("maxAge").forGetter(baseDroopingPlantsHeadBlock -> baseDroopingPlantsHeadBlock.maxAge),
                Codec.BOOL.fieldOf("isNaturalGrowth").forGetter(baseDroopingPlantsheadBlock -> baseDroopingPlantsheadBlock.isNaturalGrowth),
                Codec.BOOL.fieldOf("isClimbable").forGetter(baseDroopingPlantsheadBlock -> baseDroopingPlantsheadBlock.isClimbable),
                BuiltInRegistries.BLOCK.byNameCodec().listOf().fieldOf("attachedBlock").forGetter(baseDroopingPlantsheadBlock -> Arrays.asList(baseDroopingPlantsheadBlock.attachedBlock))).
            apply(builder, (side, maxAge, isNaturalGrowth, isClimbable, attachedBlock) -> new BaseDroopingPlantsHeadBlock(side, maxAge, isNaturalGrowth, isClimbable, attachedBlock.toArray(new Block[0])))
    );
    public static final int DEFAULT_MAX_AGE = 25;
    protected static VoxelShape SHAPE;
    private final boolean isNaturalGrowth;
    private final int side;
    private final int maxAge;
    private final Block[] attachedBlock;
    private final boolean isClimbable;
    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;


    public BaseDroopingPlantsHeadBlock(int side, boolean isNaturalGrowth, boolean isClimbable) {
        super(Properties.of().noCollission().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY), Direction.DOWN, SHAPE, false, 0.1);
        this.isNaturalGrowth = isNaturalGrowth;
        this.side = side;
        this.isClimbable = isClimbable;
        this.attachedBlock = new Block[0];
        this.maxAge = DEFAULT_MAX_AGE;
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
    }


    public BaseDroopingPlantsHeadBlock(int side, boolean isNaturalGrowth, boolean isClimbable, Block... attachedBlock) {
        super(Properties.of().noCollission().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY), Direction.DOWN, SHAPE, false, 0.1);
        this.isNaturalGrowth = isNaturalGrowth;
        this.side = side;
        this.isClimbable = isClimbable;
        this.attachedBlock = attachedBlock;
        this.maxAge = DEFAULT_MAX_AGE;
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
    }

    public BaseDroopingPlantsHeadBlock(int side, int maxAge, boolean isNaturalGrowth, boolean isClimbable, Block... attachedBlock) {
        super(Properties.of().noCollission().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY), Direction.DOWN, SHAPE, false, 0.1);
        this.isNaturalGrowth = isNaturalGrowth;
        this.side = side;
        this.isClimbable = isClimbable;
        this.attachedBlock = attachedBlock;
        this.maxAge = maxAge;
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0).setValue(WATERLOGGED, false));
    }

    @Override
    protected MapCodec<BaseDroopingPlantsHeadBlock> codec() {
        return CODEC;
    }

    @Override
    protected Block getBodyBlock() {
        return this;
    }

    @Override
    protected int getBlocksToGrowWhenBonemealed(RandomSource random) {
        return NetherVines.getBlocksToGrowWhenBonemealed(random);
    }

    @Override
    protected boolean canGrowInto(BlockState state) {
        return NetherVines.isValidGrowthState(state);
    }


    @Override
    public boolean isRandomlyTicking(BlockState state) {
        if (maxAge != DEFAULT_MAX_AGE) {
            return state.getValue(AGE) < maxAge;
        }
        return isNaturalGrowth;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState getStateForPlacement(LevelAccessor level) {
        if (maxAge != 0) {
            return this.defaultBlockState().setValue(AGE, level.getRandom().nextInt(maxAge));
        }
        return this.defaultBlockState().setValue(AGE, level.getRandom().nextInt(25));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState();
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        BlockState blockState = level.getBlockState(pos);
        if (blockState.getFluidState().is(Fluids.WATER)) {
            state = state.setValue(WATERLOGGED, true);
        }
        return state;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (maxAge == 0) {
            super.randomTick(state, level, pos, random);
            return;
        }
        if (state.getValue(AGE) >= maxAge) return;
        if (canCropGrow(level, pos.relative(this.growthDirection), state, random.nextDouble() < 0.1)) {
            BlockPos blockPos = pos.relative(this.growthDirection);
            if (this.canGrowInto(level.getBlockState(blockPos))) level.setBlockAndUpdate(blockPos, this.getGrowIntoState(state, level.random));
        }
    }

    @Override
    public boolean canSurvive(BlockState blockstate, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.above();
        blockstate = level.getBlockState(blockpos);
        if (attachedBlock.length > 0) {
            return blockstate.is(this) || Arrays.asList(attachedBlock).contains(blockstate.getBlock());
        } else {
            return blockstate.is(this) || blockstate.is(ModTags.Blocks.DROOPING_VINE_CAN_SURVIVE);
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        double halfSide = (16 - side) / 2.0;

        return SHAPE = Block.box(halfSide, 0, halfSide, 16 - halfSide, 16, 16 - halfSide);
    }

    @Override
    public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
        return isClimbable && (state.is(BlockTags.CLIMBABLE) || state.is(this));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }
}
