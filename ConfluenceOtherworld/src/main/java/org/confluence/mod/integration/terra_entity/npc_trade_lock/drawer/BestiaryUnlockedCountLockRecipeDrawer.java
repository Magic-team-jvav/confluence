package org.confluence.mod.integration.terra_entity.npc_trade_lock.drawer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.integration.terra_entity.npc_trade_lock.BestiaryUnlockedCountLock;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.api.npc.trade.TradeLockRecipeDrawer;

public class BestiaryUnlockedCountLockRecipeDrawer extends TradeLockRecipeDrawer {
    private static final ResourceLocation SPRITE = Confluence.asResource("shop_lock/bestiary_unlocked_count");

    @Override
    public int drawRecipe(ITradeLock lock, GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        if (!(lock instanceof BestiaryUnlockedCountLock bestiaryLock)) {
            return y;
        }
        int count = bestiaryLock.count();
        var size = getRecipeSize();
        guiGraphics.blitSprite(SPRITE, x, y, size, size);
        drawTooltip(guiGraphics, x, y, size, size, mouseX, mouseY, I18n.get("confluence.trade_lock.drawer.bestiary.title") + ": " + count);
        return y + size;
    }
}
