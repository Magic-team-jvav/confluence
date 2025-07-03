package org.confluence.mod.common.block.functional;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.mod.common.block.common.BaseChestBlock;
import org.confluence.mod.common.block.functional.network.INetworkBlock;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.block.functional.network.Network;
import org.confluence.mod.common.block.functional.network.NetworkNode;
import org.confluence.mod.common.init.block.ChestBlocks;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.item.VanityArmorItems;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.stream.Collectors;

public class DeathChestBlock extends BaseChestBlock implements INetworkBlock {
    public DeathChestBlock() {
        super(Properties.ofFullCopy(Blocks.TRAPPED_CHEST).explosionResistance(ModBlocks.getObsidianBasedExplosionResistance(0.0F)), ChestBlocks.DEATH_CHEST_ENTITY::get, null);
    }

    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new Entity(pPos, pState);
    }

    protected Stat<ResourceLocation> getOpenChestStat() {
        return Stats.CUSTOM.get(Stats.TRIGGER_TRAPPED_CHEST);
    }

    public boolean isSignalSource(BlockState pState) {
        return true;
    }

    public int getSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
        return Mth.clamp(ChestBlockEntity.getOpenCount(pBlockAccess, pPos), 0, 15);
    }

    public int getDirectSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
        return pSide == Direction.UP ? pBlockState.getSignal(pBlockAccess, pPos, pSide) : 0;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        onNodeRemove(pState, pLevel, pPos, pNewState);
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHit) {
        if (skipInteraction(pPlayer.getMainHandItem())) {
            return InteractionResult.PASS;
        }
        return super.useWithoutItem(pState, pLevel, pPos, pPlayer, pHit);
    }

    @Override
    public void onExecute(BlockState pState, ServerLevel pLevel, BlockPos pPos, int pColor, INetworkEntity pEntity) {
        execution(pState, pLevel, pPos, pColor, true);
    }

    @Override
    public void onUnExecute(BlockState pState, ServerLevel pLevel, BlockPos pPos, int pColor, INetworkEntity pEntity) {
        execution(pState, pLevel, pPos, pColor, false);
    }

    private void execution(BlockState pState, ServerLevel pLevel, BlockPos pPos, int pColor, boolean hasSignal) {
        if (pColor == -1) return;
        BlockPos relative = pPos.relative(getConnectedDirection(pState));
        if (pState.getValue(TYPE) != ChestType.SINGLE && pLevel.getBlockEntity(relative) instanceof INetworkEntity entity) {
            Network network = entity.getOrCreateNetworkNode().getNetwork(pColor);
            if (network != null && hasSignal != network.hasSignal()) {
                network.setSignal(hasSignal);
                network.getNodes().stream()
                        .map(NetworkNode::getEntity)
                        .collect(Collectors.toSet())
                        .forEach(entity1 -> INetworkBlock.internalExecute(pLevel, relative, pColor, hasSignal, entity1));
            }
        }
    }

    public static class Entity extends BaseChestBlock.Entity implements INetworkEntity {
        private NetworkNode networkNode;
        private final Int2ObjectMap<Set<BlockPos>> connectedPoses;
        private final Int2ObjectMap<Set<BlockPos>> relativePoses;

        public Entity(BlockPos pPos, BlockState pBlockState) {
            super(ChestBlocks.DEATH_CHEST_ENTITY.get(), pPos, pBlockState);
            this.connectedPoses = new Int2ObjectOpenHashMap<>();
            this.relativePoses = new Int2ObjectOpenHashMap<>();
        }

        protected void signalOpenCount(Level pLevel, BlockPos pPos, BlockState pState, int pEventId, int pEventParam) {
            super.signalOpenCount(pLevel, pPos, pState, pEventId, pEventParam);
            if (pEventId != pEventParam) {
                Block block = pState.getBlock();
                pLevel.updateNeighborsAt(pPos, block);
                pLevel.updateNeighborsAt(pPos.below(), block);
            }
        }

        @Override
        public void unpackLootTable(@Nullable Player player) {
            if (getLootTable() != null) {
                for (int i = 0; i < getContainerSize(); i++) {
                    if (getItems().get(i).isEmpty()) {
                        getItems().set(i, VanityArmorItems.DEAD_MANS_SWEATER.get().getDefaultInstance());
                        break;
                    }
                }
            }
            super.unpackLootTable(player);
        }

        @Override
        public void startOpen(Player pPlayer) {
            super.startOpen(pPlayer);
            if (openersCounter.getOpenerCount() == 1) {
                ((INetworkBlock) getBlockState().getBlock()).execute(getBlockState(), (ServerLevel) getLevel(), getBlockPos(), true);
            }
        }

        @Override
        public void stopOpen(Player pPlayer) {
            super.stopOpen(pPlayer);
            if (openersCounter.getOpenerCount() == 0) {
                ((INetworkBlock) getBlockState().getBlock()).execute(getBlockState(), (ServerLevel) getLevel(), getBlockPos(), false);
            }
        }

        @Override
        public void onLoad() {
            super.onLoad();
            onNodeLoad();
        }

        @Override
        public void onChunkUnloaded() {
            super.onChunkUnloaded();
            onNodeUnload();
        }

        @Override
        public void loadAdditional(CompoundTag tag, HolderLookup.Provider registryLookup) {
            super.loadAdditional(tag, registryLookup);
            deserializePoses(tag, "connectedPoses", connectedPoses);
            deserializePoses(tag, "relativePoses", relativePoses);
        }

        @Override
        protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.saveAdditional(tag, registries);
            serializePoses(tag, "connectedPoses", connectedPoses);
            serializePoses(tag, "relativePoses", relativePoses);
        }

        @Override
        public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
            return serializePoses(super.getUpdateTag(registries), "connectedPoses", connectedPoses);
        }

        @Override
        public BlockEntity getSelf() {
            return this;
        }

        @Override
        public void setNetworkNode(NetworkNode node) {
            this.networkNode = node;
        }

        @Override
        public @Nullable NetworkNode getNetworkNode() {
            return networkNode;
        }

        @Override
        public Int2ObjectMap<Set<BlockPos>> getConnectedPoses() {
            return connectedPoses;
        }

        @Override
        public Int2ObjectMap<Set<BlockPos>> getRelativePoses() {
            return relativePoses;
        }

        @Override
        public void connectTo(int color, BlockPos relatedPos, INetworkEntity related) {
            if (getBlockState().getValue(TYPE) == ChestType.SINGLE ||
                    !relatedPos.equals(getBlockPos().relative(ChestBlock.getConnectedDirection(getBlockState())))
            ) {
                INetworkEntity.super.connectTo(color, relatedPos, related); // 确保大箱子之间不连接
            }
        }
    }
}
