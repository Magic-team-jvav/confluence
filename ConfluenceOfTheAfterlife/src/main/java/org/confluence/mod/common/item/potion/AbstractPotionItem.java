package org.confluence.mod.common.item.potion;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.PotionItems;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPotionItem extends Item {
    public AbstractPotionItem(Properties properties) {
        super(properties.stacksTo(16));
    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity entity) {
        return 20;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (canUse(level, player, hand)) return ItemUtils.startUsingInstantly(level, player, hand);
        return InteractionResultHolder.fail(player.getItemInHand(hand));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity living) {
        apply(itemStack, level, living);
        if (living instanceof Player player && !player.getAbilities().instabuild) {
            itemStack.shrink(1); // 创造模式不消耗
        }
        if (itemStack.isEmpty()) {
            return PotionItems.BOTTLE.toStack();
        } else {
            if (living instanceof Player player && !player.getAbilities().instabuild) {
                ItemStack itemstack = PotionItems.BOTTLE.toStack();
                if (!player.getInventory().add(itemstack)) {
                    player.drop(itemstack, false);
                }
            }
            return itemStack;
        }
    }

    protected boolean canUse(Level level, Player player, InteractionHand hand) {
        return true;
    }

    protected abstract void apply(ItemStack itemStack, Level level, LivingEntity living);

    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        BlockState state = level.getBlockState(pos);
        if (!level.isClientSide && stack.is(PotionItems.BOTTLED_WATER.get())) {
            if (player != null) {
            level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0F, 1.0F);
            player.setItemInHand(context.getHand(), ItemUtils.createFilledResult(stack, player, new ItemStack(PotionItems.BOTTLE.get())));
            player.awardStat(net.minecraft.stats.Stats.ITEM_USED.get(stack.getItem()));
            Map<Block, Block> sandMapping = new HashMap<>();
            sandMapping.put(Blocks.SAND, NatureBlocks.MOIST_SAND_BLOCK.get());
            sandMapping.put(Blocks.RED_SAND, NatureBlocks.RED_MOIST_SAND_BLOCK.get());
            sandMapping.put(NatureBlocks.EBONY_SAND.get(), NatureBlocks.EBONY_MOIST_SAND_BLOCK.get());
            sandMapping.put(NatureBlocks.PEARL_SAND.get(), NatureBlocks.PEARL_MOIST_SAND_BLOCK.get());
            sandMapping.put(NatureBlocks.TR_CRIMSON_SAND.get(), NatureBlocks.TR_CRIMSON_MOIST_SAND_BLOCK.get());
            Block newBlock = sandMapping.get(state.getBlock());
            if (newBlock != null) {
                level.setBlockAndUpdate(pos, newBlock.defaultBlockState());
                return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }
}
