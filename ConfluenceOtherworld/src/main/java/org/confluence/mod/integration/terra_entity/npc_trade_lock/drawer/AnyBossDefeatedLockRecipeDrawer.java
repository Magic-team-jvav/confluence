package org.confluence.mod.integration.terra_entity.npc_trade_lock.drawer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.integration.terra_entity.npc_trade_lock.AnyBossDefeatedLock;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.api.npc.trade.TradeLockRecipeDrawer;

public class AnyBossDefeatedLockRecipeDrawer extends TradeLockRecipeDrawer {
    private static final ResourceLocation SPRITE = Confluence.asResource("shop_lock/any_boss_defeated");

    @Override
    public int drawRecipe(ITradeLock lock, GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        if (lock != AnyBossDefeatedLock.INSTANCE) {
            return y;
        }
        var size = getRecipeSize();
        guiGraphics.blitSprite(SPRITE, x, y, size, size);
        drawTooltip(guiGraphics, x, y, size, size, mouseX, mouseY, I18n.get("confluence.trade_lock.drawer.any_boss_defeated.title"));
        return y + size;
    }
}
