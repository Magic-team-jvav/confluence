package org.confluence.terraentity.npc.trade.drawer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.api.npc.trade.TradeLockRecipeDrawer;
import org.confluence.terraentity.registries.npc_trade_lock.variant.MoodLock;
import org.jetbrains.annotations.NotNull;

public class MoodLockRecipeDrawer extends TradeLockRecipeDrawer {
    @Override
    public int drawRecipe(@NotNull ITradeLock lock, GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        if (!(lock instanceof MoodLock moodLock)) {
            return y;
        }
        String moodIcon = moodLock.less() ? "mood_less" : "mood_greater";
        String symbol = moodLock.less() ? "≤" : "≥";
        var size = getRecipeSize();
        guiGraphics.blitSprite(ResourceLocation.fromNamespaceAndPath(TerraEntity.MODID, "shop_lock/" + moodIcon), x, y, size, size);
        drawTooltip(guiGraphics, x, y, size, size, mouseX, mouseY, I18n.get("terra_entity.trade_lock.drawer.mood.title") + " " + symbol + " " + moodLock.value());
        return y + size;
    }
}
