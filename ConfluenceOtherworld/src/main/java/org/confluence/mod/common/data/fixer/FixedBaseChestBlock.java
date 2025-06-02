package org.confluence.mod.common.data.fixer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
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
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.lib.mixin.fixer.ChestBlockEntityAccessor;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.ChestBlocks;
import org.confluence.mod.common.init.item.ToolItems;
import org.confluence.mod.mixed.IBaseContainerBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public class FixedBaseChestBlock extends ChestBlock {
    public static final BooleanProperty UNLOCKED = StateProperties.UNLOCKED;

    public FixedBaseChestBlock() {
        this(Properties.ofFullCopy(Blocks.CHEST), RegistriesFixer.BASE_CHEST_BLOCK_ENTITY::get);
    }

    public FixedBaseChestBlock(Properties properties, Supplier<BlockEntityType<? extends ChestBlockEntity>> supplier) {
        super(properties, supplier);
        registerDefaultState(defaultBlockState().setValue(UNLOCKED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(UNLOCKED));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new Entity(pPos, pState);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        if (params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof Entity entity) {
            return Collections.singletonList(setData(RegistriesFixer.BASE_CHEST_BLOCK.toStack(), entity.variant));
        }
        return Collections.emptyList();
    }

    @Override
    public float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
        return state.getValue(UNLOCKED) ? super.getExplosionResistance(state, level, pos, explosion) : 18000;
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        CompoundTag tag = LibUtils.getItemStackNbt(pStack);
        Variant variantId = Variant.byId(tag.getInt("VariantId"));
        BlockState state = pState.setValue(UNLOCKED, variantId.unlock < 0);
        pLevel.setBlockAndUpdate(pPos, state);
        if (pLevel.getBlockEntity(pPos) instanceof Entity entity) {
            entity.variant = variantId;
            entity.setBlockState(state);
        }
    }

    @Nullable
    @Override
    protected Direction candidatePartnerFacing(BlockPlaceContext pContext, Direction pDirection) {
        BlockPos relative = pContext.getClickedPos().relative(pDirection);
        if (pContext.getLevel().getBlockEntity(relative) instanceof Entity entity) {
            ItemStack itemStack = pContext.getItemInHand();

            if (LibUtils.getItemStackNbt(itemStack).getInt("VariantId") != entity.variant.id)
                return null;

            BlockState blockstate = pContext.getLevel().getBlockState(relative);
            return blockstate.is(this) && blockstate.getValue(TYPE) == ChestType.SINGLE ? blockstate.getValue(FACING) : null;
        }
        return null;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof Entity entity && entity.isLocked()) {
            boolean isShadow = stack.is(ToolItems.SHADOW_KEY);
            if ((isShadow && entity.variant == Variant.LOCKED_SHADOW) || (stack.is(ToolItems.GOLDEN_KEY) && entity.variant == Variant.LOCKED_GOLDEN) || (stack.is(ToolItems.GOLDEN_DUNGEON_KEY) && entity.variant == Variant.LOCKED_DUNGEON)) {
                int unlock = entity.variant.unlock;
                if (unlock > 0) {
                    if (!isShadow && !player.hasInfiniteMaterials()) {
                        stack.shrink(1);
                    }
                    entity.variant = Variant.byId(unlock);
                    Component name = Component.translatable("block.confluence.base_chest_block." + entity.variant.name);
                    ((IBaseContainerBlockEntity) entity).confluence$setCustomName(name);
                    Direction relativeDir = ChestBlock.getConnectedDirection(state);
                    boolean isDouble = false;
                    if (state.getValue(TYPE) != ChestType.SINGLE && level.getBlockEntity(pos.relative(relativeDir)) instanceof Entity entity1) {
                        entity1.variant = entity.variant;
                        ((IBaseContainerBlockEntity) entity).confluence$setCustomName(name);
                        isDouble = true;
                    }
                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.playSound(null, pos, SoundEvents.CHAIN_BREAK, SoundSource.BLOCKS);
                        double posX = pos.getX() + 0.5;
                        double posZ = pos.getZ() + 0.5;
                        if (isDouble) {
                            posX += relativeDir.getStepX() * 0.5;
                            posZ += relativeDir.getStepZ() * 0.5;
                        }
                        serverLevel.sendParticles(
                                new BlockParticleOption(ParticleTypes.BLOCK, Blocks.CHAIN.defaultBlockState()),
                                posX, pos.getY() + 0.5, posZ, 200, 0.0625, 0.0625, 0.0625, 0.15
                        );
                        level.setBlockAndUpdate(pos, state.setValue(UNLOCKED, true));
                    }
                }
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    // 修正方块状态
    @Override
    public InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHit) {
        if (!pState.getValue(UNLOCKED) && pLevel.getBlockEntity(pPos) instanceof Entity entity && !entity.isLocked()) {
            pLevel.setBlock(pPos, pState.setValue(UNLOCKED, true), 3);
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
    public boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.entity.Entity entity) {
        return state.getValue(UNLOCKED) && super.canEntityDestroy(state, level, pos, entity);
    }

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        return state.getValue(UNLOCKED) ? super.getDestroyProgress(state, player, level, pos) : 0;
    }

    public static ItemStack setData(ItemStack itemStack, Variant variant) {
        LibUtils.updateItemStackNbt(itemStack, tag -> tag.putInt("VariantId", variant.id));
        itemStack.set(DataComponents.CUSTOM_NAME, Component.translatable("block.confluence.base_chest_block." + variant.name).withStyle(style -> style.withItalic(false)));
        return itemStack;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : LibUtils.getTicker(blockEntityType, RegistriesFixer.BASE_CHEST_BLOCK_ENTITY.get(), Entity::baseTick);
    }

    public static class Entity extends ChestBlockEntity {
        public Variant variant = Variant.LOCKED_GOLDEN;

        public Entity(BlockPos pPos, BlockState pBlockState) {
            super(RegistriesFixer.BASE_CHEST_BLOCK_ENTITY.get(), pPos, pBlockState);
        }

        public Entity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
            super(pType, pPos, pBlockState);
        }

        public boolean isLocked() {
            return variant.unlock > 0;
        }

        @Override
        public boolean canOpen(Player pPlayer) {
            return !isLocked() && super.canOpen(pPlayer);
        }

        @Override
        protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.loadAdditional(tag, registries);
            this.variant = Variant.byId(tag.getInt("VariantId"));
        }

        @Override
        protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.saveAdditional(tag, registries);
            tag.putInt("VariantId", variant.id);
        }

        @Override
        public ClientboundBlockEntityDataPacket getUpdatePacket() {
            return ClientboundBlockEntityDataPacket.create(this);
        }

        @Override
        public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
            CompoundTag nbt = super.getUpdateTag(registries);
            nbt.putInt("VariantId", variant.id);
            return nbt;
        }

        @Override
        public boolean canTakeItem(Container target, int slot, ItemStack stack) {
            return !isLocked();
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack stack) {
            return !isLocked();
        }

        private static final ResourceKey<LootTable> IVY_CHESTS = ResourceKey.create(Registries.LOOT_TABLE, Confluence.asResource("chests/ivy_chests"));
        private static final ResourceKey<LootTable> LIVING_IVY_CHESTS = ResourceKey.create(Registries.LOOT_TABLE, Confluence.asResource("chests/living_ivy_chests"));

        public static void baseTick(Level level, BlockPos blockPos, BlockState blockState, Entity entity) {
            BlockState target = getBlockByVariant(entity.variant).defaultBlockState()
                    .setValue(FACING, blockState.getValue(FACING))
                    .setValue(WATERLOGGED, blockState.getValue(WATERLOGGED))
                    .setValue(TYPE, blockState.getValue(TYPE));
            ResourceKey<LootTable> lootTable = entity.lootTable;
            if (lootTable != null) {
                if ("chests/lvy_chests".equals(lootTable.location().getPath())) {
                    lootTable = IVY_CHESTS;
                } else if ("chests/living_lvy_chests".equals(lootTable.location().getPath())) {
                    lootTable = LIVING_IVY_CHESTS;
                }
            }
            long lootTableSeed = entity.lootTableSeed;
            entity.setLootTable(null);
            NonNullList<ItemStack> items = NonNullList.withSize(entity.getContainerSize(), ItemStack.EMPTY);
            for (int i = 0; i < entity.getContainerSize(); i++) {
                ItemStack itemStack = entity.getItem(i);
                if (!itemStack.isEmpty()) items.set(i, itemStack);
            }
            entity.clearContent();
            level.setBlockAndUpdate(blockPos, target.hasProperty(UNLOCKED) ? target.setValue(UNLOCKED, blockState.getValue(UNLOCKED)) : target);
            if (level.getBlockEntity(blockPos) instanceof ChestBlockEntity blockEntity) {
                ((ChestBlockEntityAccessor) blockEntity).callSetItems(items);
                blockEntity.setLootTable(lootTable);
                blockEntity.setLootTableSeed(lootTableSeed);
            }
        }

        private static Block getBlockByVariant(Variant variant) {
            return switch (variant) {
                case LOCKED_GOLDEN, UNLOCKED_GOLDEN -> ChestBlocks.GOLDEN_CHEST.get();
                case LOCKED_SHADOW, UNLOCKED_SHADOW -> ChestBlocks.SHADOW_CHEST.get();
                case UNLOCKED_FROZEN -> ChestBlocks.FROZEN_CHEST.get();
                case UNLOCKED_LVY -> ChestBlocks.IVY_CHEST.get();
                case UNLOCKED_WATER -> ChestBlocks.WATER_CHEST.get();
                case UNLOCKED_SKYWARE -> ChestBlocks.SKYWARE_CHEST.get();
                case UNLOCKED_SANDSTONE -> ChestBlocks.SANDSTONE_CHEST.get();
                case UNLOCKED_LIVING_WOOD -> ChestBlocks.LIVING_WOOD_CHEST.get();
                case LOCKED_DUNGEON, UNLOCKED_DUNGEON -> ChestBlocks.DUNGEON_CHEST.get();
                default -> Blocks.CHEST;
            };
        }
    }

    public enum Variant implements StringRepresentable {
        LOCKED_GOLDEN(0, "locked_golden", 1),
        UNLOCKED_GOLDEN(1, "unlocked_golden"),
        LOCKED_SHADOW(2, "locked_shadow", 3),
        UNLOCKED_SHADOW(3, "unlocked_shadow"),
        UNLOCKED_FROZEN(4, "unlocked_frozen"),
        UNLOCKED_LVY(5, "unlocked_lvy"),
        UNLOCKED_WATER(6, "unlocked_water"),
        UNLOCKED_SKYWARE(7, "unlocked_skyware"),
        UNLOCKED_NORMAL(8, "unlocked_normal"),
        UNLOCKED_SANDSTONE(9, "unlocked_sandstone"),
        UNLOCKED_LIVING_WOOD(10, "unlocked_living_wood"),
        LOCKED_DUNGEON(11, "locked_dungeon", 12),
        UNLOCKED_DUNGEON(12, "unlocked_dungeon");

        private static final IntFunction<Variant> BY_ID = ByIdMap.continuous(Variant::getId, Variant.values(), ByIdMap.OutOfBoundsStrategy.CLAMP);
        private final int id;
        private final String name;
        private final int unlock;

        Variant(int id, String name, int unlock) {
            this.id = id;
            this.name = name;
            this.unlock = unlock;
        }

        Variant(int id, String name) {
            this(id, name, -1);
        }

        public int getId() {
            return id;
        }

        public static Variant byId(int pId) {
            return BY_ID.apply(pId);
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    public static class Item extends BlockItem {
        public Item(Block block) {
            super(block, new Properties());
        }

        @Override
        public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity, int slotId, boolean isSelected) {
            if (entity instanceof ServerPlayer player) {
                CompoundTag tag = LibUtils.getItemStackNbt(stack);
                Variant variantId = Variant.byId(tag.getInt("VariantId"));
                ItemStack itemStack = new ItemStack(Entity.getBlockByVariant(variantId), stack.getCount());
                itemStack.set(DataComponents.BLOCK_STATE, new BlockItemStateProperties(Map.of("unlocked", variantId.unlock < 0 ? "true" : "false")));
                player.getInventory().setItem(slotId, itemStack);
            }
        }
    }
}
