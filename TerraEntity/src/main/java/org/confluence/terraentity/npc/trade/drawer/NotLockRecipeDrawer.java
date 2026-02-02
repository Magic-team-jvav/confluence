package org.confluence.terraentity.npc.trade.drawer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.api.npc.trade.TradeLockRecipeDrawer;
import org.confluence.terraentity.registries.npc_trade_lock.variant.NotLock;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NotLockRecipeDrawer extends TradeLockRecipeDrawer {
    @Override
    public int drawRecipe(@NotNull ITradeLock lock, GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        if (!(lock instanceof NotLock notLock)) {
            return y;
        }
        return drawRecipeLocks(List.of(notLock.lock()), guiGraphics, x, y, "!", mouseX, mouseY, I18n.get("terra_entity.trade_lock.drawer.not.title"));
    }
}
