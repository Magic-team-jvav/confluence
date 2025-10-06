package org.confluence.mod.common.item.potion;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.PotionItems;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToIntFunction;

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
        if (canUse(player.getItemInHand(hand), level, player)) return ItemUtils.startUsingInstantly(level, player, hand);
        return InteractionResultHolder.fail(player.getItemInHand(hand));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity living) {
        apply(itemStack, level, living);
        if (living instanceof Player player && !player.hasInfiniteMaterials()) {
            itemStack.shrink(1); // 创造模式不消耗
        }
        if (CommonConfigs.RETURN_POTION_GLASS_BOTTLE.get()) {
            if (itemStack.isEmpty()) {
                return getReturnItem();
            } else {
                if (living instanceof Player player && !player.hasInfiniteMaterials()) {
                    ItemStack itemstack = getReturnItem();
                    if (!player.getInventory().add(itemstack)) {
                        player.drop(itemstack, false);
                    }
                }
                return itemStack;
            }
        }
        return itemStack.isEmpty() ? ItemStack.EMPTY : itemStack;
    }

    protected ItemStack getReturnItem() {
        return PotionItems.BOTTLE.toStack();
    }

    protected boolean canUse(ItemStack itemStack, Level level, Player player) {
        return true;
    }

    protected abstract void apply(ItemStack itemStack, Level level, LivingEntity living);

    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        BlockState state = level.getBlockState(pos);
        if (!level.isClientSide && player != null && stack.is(PotionItems.BOTTLED_WATER.get())) {
            Block block = state.getBlock();
            Block newBlock = null;
            if (block == Blocks.SAND) newBlock = NatureBlocks.MOISTENED_SAND_BLOCK.get();
            else if (block == Blocks.RED_SAND) newBlock = NatureBlocks.MOISTENED_RED_SAND_BLOCK.get();
            else if (block == NatureBlocks.EBONSAND.get()) newBlock = NatureBlocks.MOISTENED_EBONSAND_BLOCK.get();
            else if (block == NatureBlocks.PEARLSAND.get()) newBlock = NatureBlocks.MOISTENED_PEARLSAND_BLOCK.get();
            else if (block == NatureBlocks.CRIMSAND.get()) newBlock = NatureBlocks.MOISTENED_CRIMSAND_BLOCK.get();
            if (newBlock != null) {
                level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0F, 1.0F);
                player.setItemInHand(context.getHand(), ItemUtils.createFilledResult(stack, player, PotionItems.BOTTLE.toStack()));
                player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                level.setBlockAndUpdate(pos, newBlock.defaultBlockState());
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    public static <T extends AbstractPotionItem> void use(Player player, float required, Class<T> type, ToIntFunction<T> function) {
        if (required <= 0.0F) return;
        List<Tuple<ItemStack, Integer>> potions = new ArrayList<>();
        Item item = player.getOffhandItem().getItem();
        if (type.isInstance(item)) {
            potions.add(new Tuple<>(player.getOffhandItem(), function.applyAsInt(type.cast(item))));
        }
        for (ItemStack itemStack : player.getInventory().items) {
            item = itemStack.getItem();
            if (type.isInstance(item)) {
                potions.add(new Tuple<>(itemStack, function.applyAsInt(type.cast(item))));
            }
        }
        if (potions.isEmpty()) return;
        potions.sort(Comparator.comparingInt(Tuple::getB));
        Level level = player.level();
        for (int i = 0; i < potions.size(); i++) {
            Tuple<ItemStack, Integer> left = potions.get(i);
            if (required <= left.getB()) {
                left.getA().finishUsingItem(level, player);
                return;
            }
            if (i == potions.size() - 1) {
                left.getA().finishUsingItem(level, player);
                return;
            } else {
                Tuple<ItemStack, Integer> right = potions.get(i + 1);
                if (right.getB() >= required) {
                    right.getA().finishUsingItem(level, player);
                    return;
                }
            }
        }
        potions.getLast().getA().finishUsingItem(level, player);
    }
}
