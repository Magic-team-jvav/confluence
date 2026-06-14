package org.confluence.mod.integration.terra_entity.npc_trade_lock.drawer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.gameevent.GameEvent;
import org.confluence.mod.integration.terra_entity.npc_trade_lock.GameEventLock;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.api.npc.trade.TradeLockRecipeDrawer;

public class GameEventLockRecipeDrawer extends TradeLockRecipeDrawer {
    @Override
    public int drawRecipe(ITradeLock lock, GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        if (!(lock instanceof GameEventLock gameEventLock)) {
            return y;
        }
        ResourceKey<? extends GameEvent> key = gameEventLock.key();

        var size = getRecipeSize();
        guiGraphics.blitSprite(Confluence.asResource("shop_lock/moment"), x, y, size, size);

        ResourceLocation location = GameEvent.REGISTRY_KEY.location();
        drawTooltip(guiGraphics, x, y, size, size, mouseX, mouseY, I18n.get(location.toLanguageKey()) + ": " + I18n.get(key.location().toLanguageKey(location.getPath())));
        return y + size;
    }
}
