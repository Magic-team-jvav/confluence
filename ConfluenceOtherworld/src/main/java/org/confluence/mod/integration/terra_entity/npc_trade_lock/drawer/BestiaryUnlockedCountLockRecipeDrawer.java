package org.confluence.mod.integration.terra_entity.npc_trade_lock.drawer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import org.confluence.mod.Confluence;
import org.confluence.mod.integration.terra_entity.npc_trade_lock.BestiaryUnlockedCountLock;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.api.npc.trade.TradeLockRecipeDrawer;
import org.jetbrains.annotations.NotNull;

public class BestiaryUnlockedCountLockRecipeDrawer extends TradeLockRecipeDrawer {
    @Override
    public int drawRecipe(@NotNull ITradeLock lock, GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        if (!(lock instanceof BestiaryUnlockedCountLock bestiaryUnlockedCountLock)) {
            return y;
        }
        var size = getRecipeSize();
        guiGraphics.blitSprite(Confluence.asResource("shop_lock/bestiary_unlocked_count"), x, y, size, size);
        drawTooltip(guiGraphics, x, y, size, size, mouseX, mouseY, I18n.get("confluence.trade_lock.drawer.bestiary.title") + ": " + bestiaryUnlockedCountLock.count());
        return y + size;
    }
}
