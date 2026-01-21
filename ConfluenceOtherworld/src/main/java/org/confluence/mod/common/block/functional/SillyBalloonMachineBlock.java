package org.confluence.mod.common.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.network.INetworkBlock;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.jetbrains.annotations.Nullable;
import org.mesdag.particlestorm.PSGameClient;
import org.mesdag.particlestorm.particle.ParticleEmitter;

public class SillyBalloonMachineBlock extends Block implements EntityBlock, INetworkBlock {
    public SillyBalloonMachineBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(StateProperties.DRIVE, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(StateProperties.DRIVE);
    }

    @Override
    public void onExecute(BlockState state, ServerLevel level, BlockPos pos, int color, INetworkEntity networkEntity) {
        level.setBlockAndUpdate(pos, state.cycle(StateProperties.DRIVE));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BEntity(pos, state);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        onNodeRemove(state, level, pos, newState);
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? LibUtils.getTicker(blockEntityType, FunctionalBlocks.SILLY_BALLOON_MACHINE_ENTITY.get(), BEntity::clientTick) : null;
    }

    public static class BEntity extends AbstractMechanicalBlock.BEntity {
        private ParticleEmitter emitter;

        public BEntity(BlockPos pPos, BlockState pBlockState) {
            super(FunctionalBlocks.SILLY_BALLOON_MACHINE_ENTITY.get(), pPos, pBlockState);
        }

        public static void clientTick(Level level, BlockPos blockPos, BlockState blockState, BEntity blockEntity) {
            if (blockEntity.emitter == null || blockEntity.emitter.isRemoved()) {
                blockEntity.emitter = new ParticleEmitter(level, blockPos.getCenter(), Confluence.asResource("balloon"));
                PSGameClient.LOADER.addEmitter(blockEntity.emitter, false);
            }
            blockEntity.emitter.active = blockState.getValue(StateProperties.DRIVE);
        }

        @Override
        public void setRemoved() {
            if (level != null && level.isClientSide && emitter != null) {
                emitter.remove();
            }
            super.setRemoved();
        }
    }
}
