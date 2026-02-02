package com.xiaohunao.enemybanner.blocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Map;

public class EnemyBannerBlock extends BaseEntityBlock {
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final MapCodec<EnemyBannerBlock> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(propertiesCodec()).apply(instance, EnemyBannerBlock::new));

    public static final DirectionProperty CLICKED_FACE = BlockStateProperties.FACING;
    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;

    private static final Map<Direction, VoxelShape> SHAPES = Maps.newEnumMap(
            ImmutableMap.of(
                    Direction.UP, Block.box(4.0F, 0.0F, 4.0F, 12.0F, 16.0F, 12.0F),
                    Direction.DOWN, Block.box(4.0F, 0.0F, 4.0F, 12.0F, 16.0F, 12.0F),
                    Direction.NORTH, Block.box(0.0F, 0.0F, 14.0F, 16.0F, 12.5F, 16.0F),
                    Direction.SOUTH, Block.box(0.0F, 0.0F, 0.0F, 16.0F, 12.5F, 2.0F),
                    Direction.WEST, Block.box(14.0F, 0.0F, 0.0F, 16.0F, 12.5F, 16.0F),
                    Direction.EAST, Block.box(0.0F, 0.0F, 0.0F, 2.0F, 12.5F, 16.0F)));

    protected EnemyBannerBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(CLICKED_FACE, Direction.NORTH)
                .setValue(ROTATION, 0)
        );
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
    @Override
    protected boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        Direction clickedFace = state.getValue(CLICKED_FACE);
        BlockPos relative = pos.relative(clickedFace.getOpposite(), 1);
        return !level.getBlockState(relative).isAir();
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPES.get(state.getValue(CLICKED_FACE));
    }

    @Override
    protected @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState, @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos neighborPos) {
        return direction == state.getValue(CLICKED_FACE).getOpposite() && !state.canSurvive(level, pos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        builder.add(CLICKED_FACE, ROTATION);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(ROTATION, RotationSegment.convertToSegment(context.getRotation() + 180.0F))
                .setValue(CLICKED_FACE, context.getClickedFace());
    }

    @Override
    public @NotNull BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new EnemyBannerBlockEntity(pos, state);
    }

    @Override
    protected @NotNull MapCodec<? extends EnemyBannerBlock> codec() {
        return CODEC;
    }
}
