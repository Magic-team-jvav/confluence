package org.confluence.mod.common.block.functional;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.block.functional.network.INetworkBlock;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.block.functional.network.NetworkNode;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public abstract class AbstractMechanicalBlock extends Block implements EntityBlock, INetworkBlock {
    public AbstractMechanicalBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        onNodeRemove(state, level, pos, newState);
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new Entity(pos, state);
    }

    public static class Entity extends BlockEntity implements INetworkEntity {
        private NetworkNode networkNode;
        private final Int2ObjectMap<Set<BlockPos>> connectedPoses;
        private final Int2ObjectMap<Set<BlockPos>> relativePoses;

        public Entity(BlockEntityType<? extends Entity> entityType, BlockPos pos, BlockState blockState) {
            super(entityType, pos, blockState);
            this.connectedPoses = new Int2ObjectOpenHashMap<>();
            this.relativePoses = new Int2ObjectOpenHashMap<>();
        }

        public Entity(BlockPos pos, BlockState state) {
            this(FunctionalBlocks.MECHANICAL_BLOCK_ENTITY.get(), pos, state);
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
        protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.loadAdditional(tag, registries);
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
        public ClientboundBlockEntityDataPacket getUpdatePacket() {
            return ClientboundBlockEntityDataPacket.create(this);
        }

        @Override
        public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
            return serializePoses(new CompoundTag(), "connectedPoses", connectedPoses);
        }

        @Override
        public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
            deserializePoses(tag, "connectedPoses", connectedPoses);
        }

        @Override
        public BlockEntity getSelf() {
            return this;
        }

        @Override
        public void setNetworkNode(NetworkNode node) {
            this.networkNode = node;
        }

        @Nullable
        @Override
        public NetworkNode getNetworkNode() {
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
    }
}
