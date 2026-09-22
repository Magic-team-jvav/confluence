package org.confluence.mod.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.item.bow.ShortBowItem;

public final class BowHandler {
    public static void releaseFullyDrawnBow(Minecraft minecraft, LocalPlayer player) {
        if (minecraft.gameMode == null || !player.isUsingItem()) return;
        ItemStack stack = player.getUseItem();
        if (stack.getItem() instanceof ShortBowItem && !stack.is(ModTags.Items.AUTOMATIC_BOW))
            return;
        if (!(stack.getItem() instanceof BowItem) || (!CommonConfigs.AUTO_RELEASE_ALL_BOWS.get() && !stack.is(ModTags.Items.AUTOMATIC_BOW))) {
            return;
        }
        int usedTicks = stack.getUseDuration() - player.getUseItemRemainingTicks();
        int fullDrawTicks = stack.getItem() instanceof ShortBowItem ? ShortBowItem.MAX_DRAW_DURATION : BowItem.MAX_DRAW_DURATION;
        if (usedTicks >= fullDrawTicks) minecraft.gameMode.releaseUsingItem(player);
    }
}
