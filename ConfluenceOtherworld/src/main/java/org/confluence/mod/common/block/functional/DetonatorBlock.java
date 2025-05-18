package org.confluence.mod.common.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.mod.common.block.functional.network.INetworkEntity;

import java.util.function.Predicate;

public class DetonatorBlock extends AbstractMechanicalBlock {
    private static final VoxelShape DRIVE = box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    private static final AABB TOUCH_AABB = new AABB(0.0, 0.5, 0.0, 1.0, 1.0, 1.0);
    private static final Predicate<net.minecraft.world.entity.Entity> PREDICATE = EntitySelector.NO_SPECTATORS.and(entity -> !entity.isIgnoringBlockTriggers());

    public DetonatorBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(stateDefinition.any()
                .setValue(StateProperties.SIGNAL, false)
                .setValue(StateProperties.DRIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(StateProperties.SIGNAL, StateProperties.DRIVE);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(StateProperties.DRIVE) ? DRIVE : Shapes.block();
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
        if (!pLevel.isClientSide && pLevel.hasNeighborSignal(pPos)) {
            execute(pState, (ServerLevel) pLevel, pPos, true);
        }
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, net.minecraft.world.entity.Entity entity, float fallDistance) {
        super.fallOn(level, state, pos, entity, fallDistance);
        if (fallDistance > 2.0F && !state.getValue(StateProperties.DRIVE) && entity instanceof ServerPlayer serverPlayer) {
            level.setBlockAndUpdate(pos, state.setValue(StateProperties.SIGNAL, true).setValue(StateProperties.DRIVE, true));
            execute(state, serverPlayer.serverLevel(), pos, true);
            level.scheduleTick(pos, this, 20);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(StateProperties.DRIVE)) {
            if (!level.getEntitiesOfClass(LivingEntity.class, TOUCH_AABB.move(pos), PREDICATE).isEmpty() ||
                    (level.getBlockEntity(pos) instanceof Entity entity && entity.getOrCreateNetworkNode().hasSignal(pos))
            ) {
                level.scheduleTick(pos, this, 20);
            } else {
                level.setBlockAndUpdate(pos, state.setValue(StateProperties.SIGNAL, false).setValue(StateProperties.DRIVE, false));
            }
        }
    }

    @Override
    public void onExecute(BlockState pState, ServerLevel pLevel, BlockPos pPos, int pColor, INetworkEntity pEntity) {}
}
