package org.confluence.mod.common.data.fixer;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.confluence.lib.mixin.fixer.ChestBlockEntityAccessor;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.block.functional.DeathChestBlock;
import org.confluence.mod.common.block.functional.network.INetworkBlock;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.block.functional.network.Network;
import org.confluence.mod.common.block.functional.network.NetworkNode;
import org.confluence.mod.common.init.block.ChestBlocks;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.item.VanityArmorItems;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FixedDeathChestBlock extends FixedBaseChestBlock implements INetworkBlock {
    public FixedDeathChestBlock() {
        super(Properties.ofFullCopy(Blocks.TRAPPED_CHEST).explosionResistance(ModBlocks.getObsidianBasedExplosionResistance(0.0F)), RegistriesFixer.DEATH_CHEST_BLOCK_ENTITY::get);
    }

    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new BEntity(pPos, pState);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        if (params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof BEntity entity) {
            return Collections.singletonList(setData(RegistriesFixer.DEATH_CHEST_BLOCK.toStack(), entity.variant));
        }
        return Collections.emptyList();
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
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        ItemStack itemStack = new ItemStack(this);
        if (level.getBlockEntity(pos) instanceof BEntity entity) {
            return setData(itemStack, entity.variant);
        }
        return itemStack;
    }

    @Override
    public void onExecute(BlockState state, ServerLevel level, BlockPos pos, int color, INetworkEntity networkEntity) {
        execution(state, level, pos, color, true);
    }

    @Override
    public void onUnExecute(BlockState state, ServerLevel level, BlockPos pos, int color, INetworkEntity networkEntity) {
        execution(state, level, pos, color, false);
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

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : LibUtils.getTicker(blockEntityType, RegistriesFixer.DEATH_CHEST_BLOCK_ENTITY.get(), BEntity::deathTick);
    }

    public static ItemStack setData(ItemStack itemStack, Variant variant) {
        LibUtils.updateItemStackNbt(itemStack, tag -> tag.putInt("VariantId", variant.getId()));
        itemStack.set(DataComponents.CUSTOM_NAME, Component.translatable("block.confluence.base_chest_block." + variant.getSerializedName().replace("unlocked", "death")).withStyle(style -> style.withItalic(false)));
        return itemStack;
    }

    public static class BEntity extends FixedBaseChestBlock.BEntity implements INetworkEntity {
        private NetworkNode networkNode;
        private final Int2ObjectMap<Set<BlockPos>> connectedPoses;
        private final Int2ObjectMap<Set<BlockPos>> relativePoses;

        public BEntity(BlockPos pPos, BlockState pBlockState) {
            super(RegistriesFixer.DEATH_CHEST_BLOCK_ENTITY.get(), pPos, pBlockState);
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

        public static void deathTick(Level level, BlockPos blockPos, BlockState blockState, BEntity entity) {
            ChestBlock target;
            if (entity.variant == Variant.UNLOCKED_GOLDEN) {
                target = ChestBlocks.DEATH_GOLDEN_CHEST.get();
            } else {
                target = ChestBlocks.DEATH_WOODEN_CHEST.get();
            }
            ResourceKey<LootTable> lootTable = entity.lootTable;
            long lootTableSeed = entity.lootTableSeed;
            entity.setLootTable(null);
            NonNullList<ItemStack> items = NonNullList.withSize(entity.getContainerSize(), ItemStack.EMPTY);
            for (int i = 0; i < entity.getContainerSize(); i++) {
                ItemStack itemStack = entity.getItem(i);
                if (!itemStack.isEmpty()) items.set(i, itemStack);
            }
            entity.clearContent();
            level.setBlockAndUpdate(blockPos, target.defaultBlockState()
                    .setValue(FACING, blockState.getValue(FACING))
                    .setValue(WATERLOGGED, blockState.getValue(WATERLOGGED))
                    .setValue(TYPE, blockState.getValue(TYPE)));
            if (level.getBlockEntity(blockPos) instanceof DeathChestBlock.BEntity blockEntity) {
                ((ChestBlockEntityAccessor) blockEntity).callSetItems(items);
                blockEntity.setLootTable(lootTable);
                blockEntity.setLootTableSeed(lootTableSeed);
                blockEntity.getConnectedPoses().putAll(entity.connectedPoses);
                blockEntity.getRelativePoses().putAll(entity.relativePoses);
            }
        }
    }
}
