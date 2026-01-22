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
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.init.ModTags;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.neoforged.neoforge.common.CommonHooks.canCropGrow;

public class BaseDroopingPlantsHeadBlock extends GrowingPlantHeadBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<BaseDroopingPlantsHeadBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("side").forGetter(block -> block.side),
            Codec.INT.fieldOf("max_age").forGetter(block -> block.maxAge),
            Codec.BOOL.fieldOf("is_natural_growth").forGetter(block -> block.isNaturalGrowth),
            Codec.BOOL.fieldOf("is_climbable").forGetter(block -> block.isClimbable),
            BuiltInRegistries.BLOCK.byNameCodec().listOf().fieldOf("attached_block").forGetter(block -> block.attachedBlock)
    ).apply(instance, BaseDroopingPlantsHeadBlock::new));

    public static final int DEFAULT_MAX_AGE = 25;
    protected static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected final int side;
    protected final int maxAge;
    protected final boolean isNaturalGrowth;
    protected final boolean isClimbable;
    protected final List<Block> attachedBlock;
    private final Set<Block> attachedBlockCache;

    public BaseDroopingPlantsHeadBlock(int side, boolean isNaturalGrowth, boolean isClimbable) {
        this(side, DEFAULT_MAX_AGE, isNaturalGrowth, isClimbable, List.of());
    }

    public BaseDroopingPlantsHeadBlock(int side, boolean isNaturalGrowth, boolean isClimbable, List<Block> attachedBlock) {
        this(side, DEFAULT_MAX_AGE, isNaturalGrowth, isClimbable, attachedBlock);
    }

    public BaseDroopingPlantsHeadBlock(int side, int maxAge, boolean isNaturalGrowth, boolean isClimbable, List<Block> attachedBlock) {
        super(Properties.of().noCollission().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY), Direction.DOWN, createShape(side), false, 0.1);
        this.side = side;
        this.maxAge = maxAge;
        this.isNaturalGrowth = isNaturalGrowth;
        this.isClimbable = isClimbable;
        this.attachedBlock = attachedBlock;
        this.attachedBlockCache = Set.copyOf(attachedBlock);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0).setValue(WATERLOGGED, false));
    }

    private static VoxelShape createShape(int side) {
        double margin = (16.0 - side) / 2.0;
        return Block.box(margin, 0, margin, 16.0 - margin, 16, 16.0 - margin);
    }

    @Override
    protected MapCodec<? extends BaseDroopingPlantsHeadBlock> codec() {
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
        return isNaturalGrowth || state.getValue(AGE) < maxAge;
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
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) state = this.defaultBlockState();
        boolean isWater = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        return state.setValue(WATERLOGGED, isWater).setValue(AGE, context.getLevel().getRandom().nextInt(Math.max(1, maxAge)));
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(AGE) < maxAge) {
            if (random.nextDouble() < 0.1) {
                BlockPos growPos = pos.relative(this.growthDirection);
                if (this.canGrowInto(level.getBlockState(growPos))) {
                    level.setBlockAndUpdate(growPos, this.getGrowIntoState(state, level.random));
                }
            }
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos supportPos = pos.above();
        BlockState supportState = level.getBlockState(supportPos);
        if (supportState.is(this)) return true;
        if (attachedBlockCache.isEmpty()) {
            return supportState.is(ModTags.Blocks.DROOPING_VINE_CAN_SURVIVE);
        }
        return attachedBlockCache.contains(supportState.getBlock());
    }

    @Override
    public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
        return isClimbable;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }
}
