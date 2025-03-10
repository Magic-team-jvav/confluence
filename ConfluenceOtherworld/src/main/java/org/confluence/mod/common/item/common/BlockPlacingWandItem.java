package org.confluence.mod.common.item.common;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BlockPlacingWandItem extends Item {
    private final TagKey<Block> blockTags;
    private final Block blacklistBlock;

    public BlockPlacingWandItem(TagKey<Block> blockTags) {
        super(new Properties().stacksTo(1));
        this.blockTags = blockTags;
        this.blacklistBlock = Blocks.AIR;
    }

    public BlockPlacingWandItem(TagKey<Block> blockTags, Block blacklistBlock) {
        super(new Properties().stacksTo(1));
        this.blockTags = blockTags;
        this.blacklistBlock = blacklistBlock;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack heldItem = player.getItemInHand(usedHand);
        BlockHitResult hitResult = getPlayerPickedBlock(player);
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(heldItem);
        }
        BlockPos placePos = hitResult.getBlockPos().relative(hitResult.getDirection());
        if (!level.isEmptyBlock(placePos)) {
            return InteractionResultHolder.pass(heldItem);
        }
        ItemStack offHandItem = player.getItemInHand(InteractionHand.OFF_HAND);
        if (tryPlaceBlock(level, player, placePos, offHandItem, heldItem)) {
            return InteractionResultHolder.success(heldItem);
        }
        for (ItemStack itemStack : player.getInventory().items) {
            if (tryPlaceBlock(level, player, placePos, itemStack, heldItem)) {
                return InteractionResultHolder.success(heldItem);
            }
        }
        return InteractionResultHolder.pass(heldItem);
    }

    private boolean tryPlaceBlock(Level level, Player player, BlockPos placePos, ItemStack itemStack, ItemStack heldItem) {
        if (!isValidBlockItem(itemStack, heldItem)) {
            return false;
        }
        BlockState state = ((BlockItem) itemStack.getItem()).getBlock().defaultBlockState();
        boolean isLeaves = state.is(BlockTags.LEAVES);
        if (blacklistBlock == Blocks.AIR) {
            if (isLeaves) {
                state = state.setValue(LeavesBlock.PERSISTENT, true);
            }
        } else {
            if (isLeaves) {
                state = blacklistBlock.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
            } else {
                state = blacklistBlock.defaultBlockState();
            }
        }
        level.setBlock(placePos, state, 3);
        if (!player.isCreative()) {
            itemStack.shrink(1);
        }
        return true;
    }

    private BlockHitResult getPlayerPickedBlock(Player player) {
        Vec3 playerEyePos = player.getEyePosition(1.0f);
        Vec3 lookDirection = player.getLookAngle();
        Vec3 targetPos = playerEyePos.add(lookDirection.scale(5.0));
        ClipContext clipContext = new ClipContext(playerEyePos, targetPos, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player);
        return player.level().clip(clipContext);
    }

    private boolean isValidBlockItem(ItemStack itemStack, ItemStack heldItem) {
        if (itemStack.isEmpty() || itemStack == heldItem) {
            return false;
        }
        if (itemStack.getItem() instanceof BlockItem blockItem) {
            BlockState state = blockItem.getBlock().defaultBlockState();
            return state.is(blockTags) && !state.is(blacklistBlock);
        }
        return false;
    }
}
