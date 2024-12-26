package org.confluence.mod.common.item.potion;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.item.PotionItems;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractPotionItem extends Item {
    public AbstractPotionItem(Properties properties) {
        super(properties.stacksTo(16));
    }

    @Override
    public int getUseDuration(@NotNull ItemStack itemStack, @NotNull LivingEntity entity) {
        return 20;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack itemStack) {
        return UseAnim.DRINK;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (canUse(level, player, hand)) return ItemUtils.startUsingInstantly(level, player, hand);
        return InteractionResultHolder.fail(player.getItemInHand(hand));
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull LivingEntity living) {
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
}
