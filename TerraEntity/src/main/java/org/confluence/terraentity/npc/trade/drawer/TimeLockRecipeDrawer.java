package org.confluence.terraentity.npc.trade.drawer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.api.npc.trade.TradeLockRecipeDrawer;
import org.confluence.terraentity.registries.npc_trade_lock.variant.TimeLock;
import org.jetbrains.annotations.NotNull;

public class TimeLockRecipeDrawer extends TradeLockRecipeDrawer {
    @Override
    public int drawRecipe(@NotNull ITradeLock lock, GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        if (!(lock instanceof TimeLock timeLock)) {
            return y;
        }
        var timeMiddle = (timeLock.from() + timeLock.to()) / 2;
        timeMiddle = timeLock.reverse() ? (timeMiddle + 12) % 24 : timeMiddle;
        var timeIcon = getTimeIcon(timeMiddle);
        var size = getRecipeSize();
        guiGraphics.blitSprite(ResourceLocation.fromNamespaceAndPath(TerraEntity.MODID, "shop_lock/" + timeIcon), x, y, size, size);
        //time is exactly 72 times faster than normal time, gametime to real time = 24000 ticks / 20 ticks per second = 1200 seconds
        var realSecondsFrom = timeLock.from() * 72;
        var realMinutesFrom = (realSecondsFrom / 60) % 60;
        var realHoursFrom = (realSecondsFrom / 3600) / 24;

        var realSecondsTo = timeLock.to() * 72;
        var realMinutesTo = (realSecondsTo / 60) % 60;
        var realHoursTo = (realSecondsTo / 3600) / 24;

        drawTooltip(guiGraphics, x, y, size, size, mouseX, mouseY,
                I18n.get("terra_entity.trade_lock.drawer.time.title") + ": " + (
                        timeLock.reverse() ? I18n.get("terra_entity.trade_lock.drawer.time.title.expect") + " " : ""
                ) + String.format("%02d:%02d - %02d:%02d", realHoursFrom, realMinutesFrom, realHoursTo, realMinutesTo));
        return y + size;
    }

    private Object getTimeIcon(int timeMiddle) {
        if (timeMiddle >= 0 && timeMiddle <= 6000) {
            return "time"; //time_day
        } else if (timeMiddle > 6000 && timeMiddle <= 12000) {
            return "time"; //time_noon
        } else if (timeMiddle > 12000 && timeMiddle <= 18000) {
            return "time"; //time_sunset
        }
        return "time"; //time_night
    }
}
