package org.confluence.mod.common.block.natural;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import org.confluence.mod.common.init.ModTags;

import java.util.function.ToIntFunction;

public class BaseMossBlock extends MultifaceBlock implements BonemealableBlock, SimpleWaterloggedBlock {
    public static final MapCodec<BaseMossBlock> CODEC = RecordCodecBuilder.mapCodec(
        builder -> builder.group(
                Codec.INT.fieldOf("lightLevel").forGetter(baseMossBlock -> baseMossBlock.lightLevel),
                Codec.BOOL.fieldOf("isLavaLogged").forGetter(baseMossBlock -> baseMossBlock.isIgnitedByLava))
            .apply(builder, BaseMossBlock::new)
    );

    private final int lightLevel;
    private final boolean isIgnitedByLava;
    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private final MultifaceSpreader spreader = new MultifaceSpreader(this);

    public BaseMossBlock(int lightLevel) {
        super(BlockBehaviour.Properties.of().lightLevel(BaseMossBlock.emission(lightLevel)).noCollission().ignitedByLava().pushReaction(PushReaction.DESTROY).strength(0.2F).sound(SoundType.GLOW_LICHEN).replaceable());
        this.lightLevel = lightLevel;
        this.isIgnitedByLava = true;
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false));
    }

    public BaseMossBlock(int lightLevel, boolean isIgnitedByLava) {
        super(BlockBehaviour.Properties.of().lightLevel(BaseMossBlock.emission(lightLevel)).noCollission().pushReaction(PushReaction.DESTROY).strength(0.2F).sound(SoundType.GLOW_LICHEN).replaceable());
        this.lightLevel = lightLevel;
        this.isIgnitedByLava = isIgnitedByLava;
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false));
    }

    @Override
    protected BlockState updateShape(
        BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public static ToIntFunction<BlockState> emission(int lightLevel) {
        return face -> MultifaceBlock.hasAnyFace(face) ? lightLevel : 0;
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        return !useContext.getItemInHand().is(ModTags.Items.MOSS_ITEM) || super.canBeReplaced(state, useContext);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getFluidState().isEmpty();
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return Direction.stream().anyMatch(p_153316_ -> this.spreader.canSpreadInAnyDirection(state, level, pos, p_153316_.getOpposite()));
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        this.spreader.spreadFromRandomFaceTowardRandomDirection(state, level, pos, random);
    }

    @Override
    public MultifaceSpreader getSpreader() {
        return this.spreader;
    }

    @Override
    protected MapCodec<BaseMossBlock> codec() {
        return CODEC;
    }
}
