package org.confluence.mod.integration.terra_entity.npc_trade_lock.drawer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import org.confluence.mod.Confluence;
import org.confluence.mod.integration.terra_entity.npc_trade_lock.DateLock;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.api.npc.trade.TradeLockRecipeDrawer;

public class DateLockRecipeDrawer extends TradeLockRecipeDrawer {
    @Override
    public int drawRecipe(ITradeLock lock, GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        if (!(lock instanceof DateLock dateLock)) {
            return y;
        }
        String icon = "date";
        String text = "Date: ";
        if (isChristmas(dateLock)) {
            icon = "date_christmas";
            text = I18n.get("bestiary.confluence.filter.christmas.title") + ": ";
        } else if (isHalloweens(dateLock)) {
            icon = "date_halloweens";
            text = I18n.get("bestiary.confluence.filter.halloween.title") + ": ";
        }
        var size = getRecipeSize();
        guiGraphics.blitSprite(Confluence.asResource("shop_lock/" + icon), x, y, size, size);
        drawTooltip(guiGraphics, x, y, size, size, mouseX, mouseY, text + getDateString(dateLock));
        return y + size;
    }

    private boolean isHalloweens(DateLock lock) {
        return lock.equals(DateLock.HALLOWEENS);
    }

    private boolean isChristmas(DateLock lock) {
        return lock.equals(DateLock.CHRISTMAS);
    }

    private String getDateString(DateLock lock) {
        var from = lock.fromInclusive();
        var to = lock.toInclusive();
        var lunar = lock.isLunar() ? " (" + I18n.get("confluence.trade_lock.drawer.date.lunar") + ") " : "";
        return String.format("%s%02d/%02d - %02d/%02d", lunar, from.month() + 1, from.day(), to.month() + 1, to.day());
    }
}
