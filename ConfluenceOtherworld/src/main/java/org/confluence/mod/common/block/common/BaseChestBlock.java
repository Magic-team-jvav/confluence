package org.confluence.mod.common.block.common;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.mod.common.init.block.ChestBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class BaseChestBlock extends ChestBlock {
    public static final BooleanProperty UNLOCKED = StateProperties.UNLOCKED;
    @Nullable
    private final Key key;

    public BaseChestBlock(@Nullable Key key) {
        this(Properties.ofFullCopy(Blocks.CHEST), ChestBlocks.BASE_CHEST_ENTITY::get, key);
    }

    public BaseChestBlock(Properties properties, Supplier<BlockEntityType<? extends ChestBlockEntity>> supplier, @Nullable Key key) {
        super(properties, supplier);
        this.key = key;
        registerDefaultState(defaultBlockState().setValue(UNLOCKED, key == null));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(UNLOCKED));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new BEntity(pPos, pState);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        ItemStack stack = asItem().getDefaultInstance();
        setupComponent(stack, state.getValue(UNLOCKED));
        return Collections.singletonList(stack);
    }

    @Override
    public float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
        return state.getValue(UNLOCKED) ? super.getExplosionResistance(state, level, pos, explosion) : 18000;
    }

    @Nullable
    @Override
    protected Direction candidatePartnerFacing(BlockPlaceContext context, Direction direction) {
        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos().relative(direction));
        if (blockstate.is(this) && blockstate.getValue(UNLOCKED) == unlocked(context.getItemInHand())) {
            return blockstate.getValue(TYPE) == ChestType.SINGLE ? blockstate.getValue(FACING) : null;
        }
        return null;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(UNLOCKED, unlocked(context.getItemInHand()));
    }

    protected boolean unlocked(ItemStack stack) {
        return stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).properties().getOrDefault("unlocked", "true").equals("true");
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!state.getValue(UNLOCKED)) {
            if (key != null && !key.useKeyOn(stack, state, level, pos, player, hand, hitResult)) {
                return ItemInteractionResult.FAIL;
            }
            return ItemInteractionResult.SUCCESS;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        ItemStack stack = super.getCloneItemStack(state, target, level, pos, player);
        setupComponent(stack, state.getValue(UNLOCKED));
        return stack;
    }

    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.entity.Entity entity) {
        return state.getValue(UNLOCKED) && super.canEntityDestroy(state, level, pos, entity);
    }

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        return state.getValue(UNLOCKED) ? super.getDestroyProgress(state, player, level, pos) : 0;
    }

    public static void setupComponent(ItemStack stack, boolean unlocked) {
        stack.set(DataComponents.BLOCK_STATE, new BlockItemStateProperties(Map.of("unlocked", unlocked ? "true" : "false")));
    }

    public static class BItem extends BlockItem {
        public BItem(Block block, Properties properties) {
            super(block, properties.component(DataComponents.BLOCK_STATE, new BlockItemStateProperties(Map.of("unlocked", "true"))));
        }

        @Override
        public void onCraftedPostProcess(ItemStack stack, Level level) {
            setupComponent(stack, true);
        }
    }

    public static class BEntity extends ChestBlockEntity {
        public BEntity(BlockPos pPos, BlockState pBlockState) {
            this(ChestBlocks.BASE_CHEST_ENTITY.get(), pPos, pBlockState);
        }

        public BEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
            super(pType, pPos, pBlockState);
        }

        @Override
        protected Component getDefaultName() {
            return getBlockState().getBlock().getName();
        }

        @Override
        public boolean canOpen(Player pPlayer) {
            return getBlockState().getValue(UNLOCKED) && super.canOpen(pPlayer);
        }

        @Override
        public boolean canTakeItem(Container target, int slot, ItemStack stack) {
            return getBlockState().getValue(UNLOCKED);
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack stack) {
            return getBlockState().getValue(UNLOCKED);
        }
    }

    @FunctionalInterface
    public interface Key {
        boolean useKeyOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult);
    }
}
