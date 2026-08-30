package org.confluence.mod.common.item.common;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class BlockPlacingWandItem extends BlockItem {
    protected final @Nullable TagKey<Block> targetBlock;
    protected final BiFunction<BlockPlaceContext, BlockState, BlockState> transferTo;

    public BlockPlacingWandItem(@Nullable TagKey<Block> targetBlock, Block transferTo) {
        super(transferTo, new Properties().stacksTo(1));
        this.targetBlock = targetBlock;
        this.transferTo = (context, state) -> state;
    }

    public BlockPlacingWandItem(@Nullable TagKey<Block> targetBlock, Block defaultBLock, BiFunction<BlockPlaceContext, BlockState, BlockState> transferTo) {
        super(defaultBLock, new Properties().stacksTo(1));
        this.targetBlock = targetBlock;
        this.transferTo = transferTo;
    }

    @Override
    public @Nullable BlockPlaceContext updatePlacementContext(BlockPlaceContext context) {
        Player player = context.getPlayer();
        if (player == null) return null;
        ItemStack heldItem = context.getItemInHand();
        ItemStack offHandItem = player.getOffhandItem();
        if (isValidBlockItem(offHandItem, heldItem)) {
            return new BlockPlaceContext(context.getLevel(), player, context.getHand(), offHandItem, context.getHitResult());
        } else {
            for (ItemStack itemStack : player.getInventory().items) {
                if (isValidBlockItem(itemStack, heldItem)) {
                    return new BlockPlaceContext(context.getLevel(), player, context.getHand(), itemStack, context.getHitResult());
                }
            }
        }
        return null;
    }

    @Override
    protected @Nullable BlockState getPlacementState(BlockPlaceContext context) {
        BlockState state = super.getPlacementState(context);
        if (state == null) return null;
        return transferTo.apply(context, state);
    }

    @Override
    public String getDescriptionId() {
        return getOrCreateDescriptionId();
    }

    protected boolean isValidBlockItem(ItemStack itemStack, ItemStack heldItem) {
        if (itemStack.isEmpty() || itemStack == heldItem || itemStack.getItem() instanceof BlockPlacingWandItem) {
            return false;
        }
        if (itemStack.getItem() instanceof BlockItem blockItem) {
            if (targetBlock == null) {
                return blockItem.getBlock() == getBlock();
            }
            return blockItem.getBlock().builtInRegistryHolder().is(targetBlock) && blockItem.getBlock() != getBlock();
        }
        return false;
    }
}
