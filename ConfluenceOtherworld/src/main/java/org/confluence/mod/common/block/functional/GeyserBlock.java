package org.confluence.mod.common.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.util.TrapDamageHelper;
import org.jetbrains.annotations.Nullable;

public class GeyserBlock extends AbstractMechanicalBlock { // 热喷泉
    public static final BooleanProperty IS_FLOOR = BooleanProperty.create("is_floor");
    private static final VoxelShape SHAPE_FLOOR = Shapes.box(0.0625, 0.0, 0.0625, 0.9375, 0.5, 0.9375);
    private static final VoxelShape SHAPE_CEIL = Shapes.box(0.0625, 0.5, 0.0625, 0.9375, 1.0, 0.9375);

    public GeyserBlock() {
        super(Properties.copy(Blocks.STONE));
        registerDefaultState(stateDefinition.any().setValue(IS_FLOOR, true).setValue(StateProperties.DRIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(IS_FLOOR, StateProperties.DRIVE);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        return true;
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
        if (!pLevel.isClientSide) {
            execute(pState, (ServerLevel) pLevel, pPos, pLevel.hasNeighborSignal(pPos));
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction direction = pContext.getNearestLookingVerticalDirection();
        return defaultBlockState().setValue(IS_FLOOR, direction == Direction.DOWN);
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockPos below = pPos.below();
        BlockPos above = pPos.above();
        return pLevel.getBlockState(below).isFaceSturdy(pLevel, below, Direction.UP) || pLevel.getBlockState(above).isFaceSturdy(pLevel, above, Direction.DOWN);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return pState.getValue(IS_FLOOR) ? SHAPE_FLOOR : SHAPE_CEIL;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return Shapes.empty();
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, net.minecraft.world.entity.Entity pEntity) {
        if (!pLevel.isClientSide && pLevel.getBlockEntity(pPos) instanceof BEntity entity) {
            onExecute(pState, (ServerLevel) pLevel, pPos, -1, entity);
        }
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        pLevel.setBlockAndUpdate(pPos, pState.setValue(StateProperties.DRIVE, false));
    }

    @Override
    public void onExecute(BlockState state, ServerLevel level, BlockPos pos, int color, INetworkEntity networkEntity) {
        if (state.getValue(StateProperties.DRIVE)) return;
        int bx = pos.getX();
        int by = pos.getY();
        int bz = pos.getZ();
        double offsetY = state.getValue(IS_FLOOR) ? 12.0 : -12.0;
        // todo 粒子
        float damage = 4.0F;
        level.getEntities((net.minecraft.world.entity.Entity) null, new AABB(bx, by, bz, bx + 1.0, by + offsetY, bz + 1.0), entity -> entity instanceof LivingEntity)
                .forEach(entity -> entity.hurt(level.damageSources().lava(), TrapDamageHelper.applyDeadMansSweaterReduction((LivingEntity) entity, damage)));
        level.setBlockAndUpdate(pos, state.setValue(StateProperties.DRIVE, true));
        level.scheduleTick(pos, this, 66);
    }
}
