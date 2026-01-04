package org.confluence.mod.integration.terra_entity.npc_trade_lock.drawer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import org.confluence.mod.Confluence;
import org.confluence.mod.integration.terra_entity.npc_trade_lock.FishingHookInFluidLock;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.api.npc.trade.TradeLockRecipeDrawer;
import org.jetbrains.annotations.NotNull;

public class FishingHookInFluidLockRecipeDrawer extends TradeLockRecipeDrawer {
    @Override
    public int drawRecipe(@NotNull ITradeLock lock, GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        if (!(lock instanceof FishingHookInFluidLock fishingHookInFluidLock)) {
            return y;
        }

        if (fishingHookInFluidLock.requiresFishingHook()) {
            var size = getRecipeSize();
            guiGraphics.blitSprite(Confluence.asResource("shop_lock/fishing_requires_hook"), x, y, size, size);
            drawTooltip(guiGraphics, x, y, size, size, mouseX, mouseY, I18n.get("confluence.trade_lock.drawer.fishing.requires_hook"));
            x += size;
        }

        if (!fishingHookInFluidLock.tags().isEmpty()) {
            var size = getRecipeSize();
            for (var tag : fishingHookInFluidLock.tags()) {
                guiGraphics.blitSprite(Confluence.asResource("shop_lock/fishing_in_liquid"), x, y, size, size);
                drawTooltip(guiGraphics, x, y, size, size, mouseX, mouseY,
                        I18n.get("confluence.trade_lock.drawer.fishing.fishing_in") + " " + tag.location());
                x += size;
            }
        }

        return y;
    }
}
