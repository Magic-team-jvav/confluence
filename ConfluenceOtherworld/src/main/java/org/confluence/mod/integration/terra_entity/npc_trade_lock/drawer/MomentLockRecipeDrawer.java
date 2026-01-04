package org.confluence.mod.integration.terra_entity.npc_trade_lock.drawer;

import net.minecraft.client.gui.GuiGraphics;
import org.confluence.mod.Confluence;
import org.confluence.mod.integration.terra_entity.npc_trade_lock.MomentLock;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.api.npc.trade.TradeLockRecipeDrawer;
import org.jetbrains.annotations.NotNull;

public class MomentLockRecipeDrawer extends TradeLockRecipeDrawer {
    @Override
    public void drawRecipe(@NotNull ITradeLock lock, GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        if (!(lock instanceof MomentLock lock1)) {
            return;
        }
        var size = getRecipeSize();
        guiGraphics.blitSprite(Confluence.asResource("shop_lock_moment"), x, y, size, size);
        drawTooltip(guiGraphics, x, y, size, size, mouseX, mouseY, "Moment: " + lock1.moment());
    }
}
