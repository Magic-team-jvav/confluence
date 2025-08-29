package org.confluence.mod.common.block.common;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.mixed.IChunkSection;
import org.confluence.mod.util.DynamicBiomeUtils;
import org.confluence.mod.util.OverworldUtils;
import org.confluence.terraentity.init.entity.TEMonsterEntities;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TombstoneBlock extends HorizontalDirectionalBlock implements EntityBlock, SimpleWaterloggedBlock {
    public static final MapCodec<TombstoneBlock> CODEC = simpleCodec(TombstoneBlock::new);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE = box(4, 0, 4, 12, 14, 12);

    public TombstoneBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    public TombstoneBlock() {
        this(BlockBehaviour.Properties.ofFullCopy(Blocks.SMOOTH_STONE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
        if (!level.isClientSide && pos.getY() < OverworldUtils.getSurfaceY() && LibDateUtils.isNight(LibDateUtils.getDayTime(level))) {
            IChunkSection iSection = DynamicBiomeUtils.getISection(level, pos);
            RandomSource random = player.getRandom();
            if (iSection != null && iSection.confluence$isGraveyard() && random.nextBoolean()) {
                TEMonsterEntities.GHOST.get().spawn((ServerLevel) level, pos.offset(
                        Mth.randomBetweenInclusive(random, -15, 15),
                        Mth.randomBetweenInclusive(random, -15, 15),
                        Mth.randomBetweenInclusive(random, -15, 15)
                ), MobSpawnType.MOB_SUMMONED);
            }
        }
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return facing == Direction.DOWN && !canSurvive(state, level, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof BEntity entity) {
            if (level.isClientSide) {
                return InteractionResult.PASS;
            }
            if (!otherPlayerIsEditingSign(player, entity) && player.mayBuild()) {
                openTextEdit(player, entity);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public void openTextEdit(Player player, BEntity entity) {
        entity.setAllowedPlayerEditor(player.getUUID());
        player.openTextEdit(entity, true);
    }

    public boolean otherPlayerIsEditingSign(Player player, BEntity entity) {
        UUID uuid = entity.getPlayerWhoMayEdit();
        return uuid != null && !uuid.equals(player.getUUID());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return LibUtils.getTicker(blockEntityType, ModBlocks.TOMBSTONE_ENTITY.get(), BEntity::tick);
    }

    @Override
    protected MapCodec<TombstoneBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    public static class BEntity extends SignBlockEntity {
        public BEntity(BlockPos pos, BlockState blockState) {
            super(ModBlocks.TOMBSTONE_ENTITY.get(), pos, blockState);
        }

        @Override
        public int getMaxTextLineWidth() {
            return 120;
        }
    }
}
