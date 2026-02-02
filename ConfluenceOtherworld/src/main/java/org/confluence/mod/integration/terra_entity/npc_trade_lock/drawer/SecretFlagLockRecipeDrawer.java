package org.confluence.mod.integration.terra_entity.npc_trade_lock.drawer;

import net.minecraft.client.gui.GuiGraphics;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.api.npc.trade.TradeLockRecipeDrawer;

public class SecretFlagLockRecipeDrawer extends TradeLockRecipeDrawer {
    /**
     * Should be hidden from player?
     * No.
     * @see org.confluence.mod.mixed.IWorldOptions
     */
    @Override
    public int drawRecipe(ITradeLock lock, GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        return y;
    }
}
