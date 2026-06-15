package org.confluence.mod.common.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.jetbrains.annotations.Nullable;

import static org.confluence.lib.common.block.StateProperties.DRIVE;
import static org.confluence.lib.common.block.StateProperties.SIGNAL;

public class TimersBlock extends AbstractMechanicalBlock {
    private final int duration;

    public TimersBlock(int duration) {
        super(Properties.copy(Blocks.COMPARATOR));
        if (duration <= 0) throw new RuntimeException("Duration cannot less equal 0!");
        this.duration = duration;
        registerDefaultState(stateDefinition.any().setValue(DRIVE, false).setValue(SIGNAL, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(DRIVE, SIGNAL);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (skipInteraction(player.getMainHandItem())) return InteractionResult.PASS;
        if (!level.isClientSide && player.isCrouching()) {
            state = state.cycle(DRIVE);
            if (!state.getValue(DRIVE)) { // 使网络收到负脉冲
                state = state.setValue(SIGNAL, false);
                execute(state, (ServerLevel) level, pos, false);
            }
            level.setBlockAndUpdate(pos, state);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
        if (pLevel.isClientSide && pLevel.hasNeighborSignal(pPos)) {
            pLevel.setBlockAndUpdate(pPos, pState.cycle(DRIVE)); // 目前仅能用红石控制其开关
        }
    }

    @Override
    public void onExecute(BlockState state, ServerLevel level, BlockPos pos, int color, INetworkEntity networkEntity) {}

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.or(
                Block.box(0.0, 11.0, 0.0, 16.0, 16.0, 16.0),
                Block.box(0.5, 4.0, 0.5, 15.5, 11.0, 15.5),
                Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0)
        );
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return LibUtils.getTicker(pBlockEntityType, FunctionalBlocks.MECHANICAL_BLOCK_ENTITY.get(), TimersBlock::timer);
    }

    private static void timer(Level level, BlockPos blockPos, BlockState blockState, BEntity entity) {
        if (level.isClientSide || !blockState.getValue(DRIVE)) return;
        TimersBlock timersBlock = (TimersBlock) blockState.getBlock();
        if ((level.getGameTime() + entity.getOrCreateNetworkNode().getId()) % timersBlock.duration == 0) {
            level.setBlockAndUpdate(blockPos, blockState.setValue(SIGNAL, true));
            timersBlock.execute(blockState, (ServerLevel) level, blockPos, true);
        } else if (blockState.getValue(SIGNAL)) { // 确保只激活一次负脉冲
            level.setBlockAndUpdate(blockPos, blockState.setValue(SIGNAL, false));
            timersBlock.execute(blockState, (ServerLevel) level, blockPos, false);
        }
    }
}
