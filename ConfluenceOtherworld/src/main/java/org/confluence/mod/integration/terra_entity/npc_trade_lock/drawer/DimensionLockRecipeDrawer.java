package org.confluence.mod.integration.terra_entity.npc_trade_lock.drawer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import org.confluence.mod.Confluence;
import org.confluence.mod.integration.terra_entity.npc_trade_lock.DimensionLock;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.api.npc.trade.TradeLockRecipeDrawer;
import org.jetbrains.annotations.NotNull;

public class DimensionLockRecipeDrawer extends TradeLockRecipeDrawer {
    @Override
    public int drawRecipe(@NotNull ITradeLock lock, GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        if (!(lock instanceof DimensionLock dimensionLock)) {
            return y;
        }
        var size = getRecipeSize();
        guiGraphics.blitSprite(Confluence.asResource("shop_lock/dimension"), x, y, size, size);
        drawTooltip(guiGraphics, x, y, size, size, mouseX, mouseY,
                I18n.get("confluence.trade_lock.drawer.dimension.title") + ": " + dimensionLock.dimension().location());
        return y + size;
    }
}
