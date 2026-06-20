package org.confluence.mod.common.item.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.client.gui.container.WormholeScreen;
import org.confluence.mod.client.handler.WormholeHandler;
import org.confluence.mod.common.CommonConfigs;

public class WormholePotionItem extends AbstractPotionItem {
    public WormholePotionItem(Properties properties) {
        super(properties);
    }

    @Override
    protected void apply(ItemStack itemStack, Level level, LivingEntity living) {
        if (!level.isClientSide() || !(living instanceof Player player)) {
            return;
        }
        WormholeHandler.openScreen();
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity living) {
        apply(itemStack, level, living);
        if (!CommonConfigs.RETURN_POTION_GLASS_BOTTLE.get()) {
            return itemStack.isEmpty() ? ItemStack.EMPTY : itemStack;
        }

        if (itemStack.isEmpty()) {
            return getReturnItem();
        }

        if (!(living instanceof Player player) || player.hasInfiniteMaterials()) {
            return itemStack;
        }

        ItemStack itemstack = getReturnItem();
        if (player.getInventory().add(itemstack)) {
            return itemStack;
        }

        player.drop(itemstack, false);
        return itemStack;
    }
}
