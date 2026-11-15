package org.confluence.mod.common.item.potion;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.confluence.mod.common.init.ModFluids;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.item.PotionItems;
import org.confluence.mod.common.init.item.SoulItems;
import org.confluence.mod.util.SoulcererUtils;

/// 玻璃瓶的右键优先级（各项按目标类型区分，互不冲突）：
///
/// 1. **潜行**：优先把玻璃瓶当成炼药瓶方块的放置器（此时不接液体、不装魂）；
/// 2. 对着**蜂蜜**液体源：装成蜂蜜瓶；
/// 3. 对着**水源**：装成瓶装水；
/// 4. 对着**普通方块表面**：直接把炼药瓶方块放下去（替代原来的合成配方）；
/// 5. 对着**空气**且处于**魂条**状态：装成魂瓶（消耗灵魂，不足时动作栏提示）。
///
/// 炼药瓶方块被破坏时掉落玻璃瓶，所以这个物品同时也承担"炼药瓶的搬运工"。
public class BottleItem extends Item {
    public BottleItem() {
        super(new Properties().stacksTo(16));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        /// 潜行优先放置炼药瓶：把玻璃瓶当方块用，避免和接液体抢右键。
        if (player.isShiftKeyDown() && placeBrewingVial(level, player, hand)) {
            return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
        }
        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = hit.getBlockPos();
            if (level.mayInteract(player, pos)) {
                FluidState fluid = level.getFluidState(pos);
                if (fluid.getFluidType() == ModFluids.HONEY.type().get()) {
                    return fillBottle(level, player, stack, Items.HONEY_BOTTLE.getDefaultInstance(), pos);
                }
                if (fluid.is(FluidTags.WATER)) {
                    return fillBottle(level, player, stack, PotionItems.BOTTLED_WATER.toStack(), pos);
                }
            }
            /// 非液体方块：玻璃瓶直接放置炼药瓶；放不下就什么都不做（不抢装魂）。
            if (placeBrewingVial(level, player, hand, hit)) {
                return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
            }
            return InteractionResultHolder.pass(stack);
        }
        /// 只有对着空气（没打中任何方块）且处于魂条状态，才把玻璃瓶装成魂瓶。
        if (SoulcererUtils.isSoulMode(player) && SoulItems.tryFillSoulBottle(player, hand)) {
            return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
        }
        return InteractionResultHolder.pass(stack);
    }

    /// 接液体的公共收尾：播放灌装音效、发流体拾取事件，并用 {@link ItemUtils#createFilledResult} 换瓶。
    private static InteractionResultHolder<ItemStack> fillBottle(Level level, Player player, ItemStack stack, ItemStack result, BlockPos pos) {
        level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
        level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
        return InteractionResultHolder.sidedSuccess(ItemUtils.createFilledResult(stack, player, result), level.isClientSide);
    }

    /// 沿视线取方块命中结果，再走一次放置。
    private static boolean placeBrewingVial(Level level, Player player, InteractionHand hand) {
        return placeBrewingVial(level, player, hand, getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE));
    }

    /// 用炼药瓶方块自己的 {@link BlockItem} 跑一遍原版放置流程（可替换方块、朝向、碰撞都由原版判定），
    /// 成功后消耗一个玻璃瓶；创造模式不消耗。
    private static boolean placeBrewingVial(Level level, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hit.getType() != HitResult.Type.BLOCK || !level.mayInteract(player, hit.getBlockPos())) return false;
        if (!(FunctionalBlocks.BREWING_VIAL.get().asItem() instanceof BlockItem blockItem)) return false;
        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty()) return false;
        BlockPlaceContext context = new BlockPlaceContext(new UseOnContext(player, hand, hit));
        if (!blockItem.place(context).consumesAction()) return false;
        if (!player.hasInfiniteMaterials()) {
            stack.consume(1, player);
        }
        return true;
    }
}
