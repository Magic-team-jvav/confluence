package org.confluence.mod.common.block.functional;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.confluence.mod.common.block.common.BaseChestBlock;
import org.confluence.mod.common.block.functional.network.INetworkBlock;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.block.functional.network.Network;
import org.confluence.mod.common.block.functional.network.NetworkNode;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.mixin.accessor.ChestBlockEntityAccessor;
import org.confluence.terra_curio.util.TCUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.stream.Collectors;

public class DeathChestBlock extends BaseChestBlock implements INetworkBlock {
    public DeathChestBlock() {
        super(Properties.ofFullCopy(Blocks.TRAPPED_CHEST).explosionResistance(ModBlocks.getObsidianBasedExplosionResistance(0.0F)), FunctionalBlocks.DEATH_CHEST_BLOCK_ENTITY::get);
    }

    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new Entity(pPos, pState);
    }

    protected @NotNull Stat<ResourceLocation> getOpenChestStat() {
        return Stats.CUSTOM.get(Stats.TRIGGER_TRAPPED_CHEST);
    }

    public boolean isSignalSource(@NotNull BlockState pState) {
        return true;
    }

    public int getSignal(@NotNull BlockState pBlockState, @NotNull BlockGetter pBlockAccess, @NotNull BlockPos pPos, @NotNull Direction pSide) {
        return Mth.clamp(ChestBlockEntity.getOpenCount(pBlockAccess, pPos), 0, 15);
    }

    public int getDirectSignal(@NotNull BlockState pBlockState, @NotNull BlockGetter pBlockAccess, @NotNull BlockPos pPos, @NotNull Direction pSide) {
        return pSide == Direction.UP ? pBlockState.getSignal(pBlockAccess, pPos, pSide) : 0;
    }

    @Override
    public void onRemove(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pNewState, boolean pMovedByPiston) {
        onNodeRemove(pState, pLevel, pPos, pNewState);
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(@NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer, @NotNull BlockHitResult pHit) {
        if (skipInteraction(pPlayer.getMainHandItem())) {
            return InteractionResult.PASS;
        }
        return super.useWithoutItem(pState, pLevel, pPos, pPlayer, pHit);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        ItemStack itemStack = new ItemStack(this);
        if (level.getBlockEntity(pos) instanceof Entity entity) {
            return setData(itemStack, entity.variant);
        }
        return itemStack;
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

    public static ItemStack setData(ItemStack itemStack, Variant variant) {
        TCUtils.updateItemStackNbt(itemStack, tag -> tag.putInt("VariantId", variant.getId()));
        itemStack.set(DataComponents.CUSTOM_NAME, Component.translatable("block.confluence.base_chest_block." + variant.getSerializedName().replace("unlocked", "death")).withStyle(style -> style.withItalic(false)));
        return itemStack;
    }

    public static class Entity extends BaseChestBlock.Entity implements INetworkEntity {
        private NetworkNode networkNode;
        private final Int2ObjectMap<Set<BlockPos>> connectedPoses;
        private final Int2ObjectMap<Set<BlockPos>> relativePoses;

        public Entity(BlockPos pPos, BlockState pBlockState) {
            super(FunctionalBlocks.DEATH_CHEST_BLOCK_ENTITY.get(), pPos, pBlockState);
            this.connectedPoses = new Int2ObjectOpenHashMap<>();
            this.relativePoses = new Int2ObjectOpenHashMap<>();
        }

        protected void signalOpenCount(@NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, int pEventId, int pEventParam) {
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
                        getItems().set(i, ModItems.DEAD_MANS_SWEATER.get().getDefaultInstance());
                        break;
                    }
                }
            }
            super.unpackLootTable(player);
        }

        @Override
        public void startOpen(@NotNull Player pPlayer) {
            super.startOpen(pPlayer);
            if (((ChestBlockEntityAccessor) this).getOpenersCounter().getOpenerCount() == 1) {
                ((INetworkBlock) getBlockState().getBlock()).execute(getBlockState(), (ServerLevel) getLevel(), getBlockPos(), true);
            }
        }

        @Override
        public void stopOpen(@NotNull Player pPlayer) {
            super.stopOpen(pPlayer);
            if (((ChestBlockEntityAccessor) this).getOpenersCounter().getOpenerCount() == 0) {
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
        public void loadAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registryLookup) {
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
        public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries) {
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
