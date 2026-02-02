package org.confluence.terra_furniture.common.block.sittable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.terra_furniture.TerraFurniture;
import org.confluence.terra_furniture.api.utils.DelayableTaskMgr;
import org.confluence.terra_furniture.api.utils.DelayableConsumerTask;
import org.confluence.terra_furniture.common.block.func.TFBlockSetType;
import org.confluence.terra_furniture.common.datagen.empowered.BlockDataGenerator;
import org.confluence.terra_furniture.common.entity.RideableEntityNull;
import org.confluence.terra_furniture.common.init.TFBlocks;
import org.confluence.terra_furniture.references.TFConfluenceRefs;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Triplet;

import java.util.function.Consumer;
import java.util.function.Supplier;

/** It's not a joke, u can get a poo Lol*/
public class ToiletBlock extends ChairBlock{
    public static final int TICKS_TO_POOP = 60;

    public ToiletBlock(TFBlockSetType type, BlockState state, Consumer<Properties> properties, float yOff) {
        super(type, state, properties, yOff);
    }

    public ToiletBlock(TFBlockSetType type, BlockState state, Properties properties, float yOff) {
        super(type, state, properties, yOff);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) return InteractionResult.PASS;
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof ToiletBE toiletBlock)) {
            TerraFurniture.LOGGER.error("ToiletBE block entity is missing, it's an unexpected state.");
            return InteractionResult.FAIL;
        }
        InteractionResult result = toiletBlock.useAct(level, pos, player);
        if (result != InteractionResult.SUCCESS) return result;

        toiletBlock.getServerTaskMgr().createLoopTask(new DelayableConsumerTask<>(
                supplier -> {
                    Triplet<RideableEntityNull, ServerLevel, BlockPos> triplet = supplier.get();
                    TFConfluenceRefs.poop_task.accept(triplet.getA(), triplet.getB(), triplet.getC());
                },
                TICKS_TO_POOP
        ));
        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ToiletBE(blockPos, blockState);
    }

    public static class ToiletBE extends ChairBE {
        private final DelayableTaskMgr<Supplier<Triplet<RideableEntityNull, ServerLevel, BlockPos>>> serverTaskMgr = new DelayableTaskMgr<>();
        public ToiletBE(BlockPos pos, BlockState blockState) {
            super(TFBlocks.TOILET_ENTITY.get(), pos, blockState);
        }
        public void tickAtServer() {
            if (this.delayer < PLAYER_SIT_ON_CHECK_DELAY) ++this.delayer;
            else if (this.sit != null) {
                if (this.sit.getFirstPassenger() != null) serverTaskMgr.tick(() -> new Triplet<>(this.sit, (ServerLevel) this.level, this.worldPosition));
                else {
                    this.sit.remove(Entity.RemovalReason.DISCARDED);
                    this.sit = null;
                    serverTaskMgr.clear();
                }
            }
        }

        public DelayableTaskMgr<Supplier<Triplet<RideableEntityNull, ServerLevel, BlockPos>>> getServerTaskMgr() {
            return serverTaskMgr;
        }
    }

    @Override
    public @Nullable BlockDataGenerator<? super ChairBlock> getGenerator() {
        return null;
    }
}
