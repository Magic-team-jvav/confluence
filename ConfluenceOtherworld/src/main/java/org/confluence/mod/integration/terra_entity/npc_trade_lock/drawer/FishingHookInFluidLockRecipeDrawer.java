package org.confluence.mod.integration.terra_entity.npc_trade_lock.drawer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import org.confluence.mod.Confluence;
import org.confluence.mod.integration.terra_entity.npc_trade_lock.FishingHookInFluidLock;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.api.npc.trade.TradeLockRecipeDrawer;

import java.util.List;

public class FishingHookInFluidLockRecipeDrawer extends TradeLockRecipeDrawer {
    private static final ResourceLocation REQUIRES_HOOK_SPRITE = Confluence.asResource("shop_lock/fishing_requires_hook");
    private static final ResourceLocation IN_LIQUID_SPRITE = Confluence.asResource("shop_lock/fishing_in_liquid");

    @Override
    public int drawRecipe(ITradeLock lock, GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        if (!(lock instanceof FishingHookInFluidLock fishingHookLock)) {
            return y;
        }
        List<TagKey<Fluid>> tags = fishingHookLock.tags();
        boolean requiresFishingHook = fishingHookLock.requiresFishingHook();

        if (requiresFishingHook) {
            var size = getRecipeSize();
            guiGraphics.blitSprite(REQUIRES_HOOK_SPRITE, x, y, size, size);
            drawTooltip(guiGraphics, x, y, size, size, mouseX, mouseY, I18n.get("confluence.trade_lock.drawer.fishing.requires_hook"));
            x += size;
        }

        if (!tags.isEmpty()) {
            var size = getRecipeSize();
            for (var tag : tags) {
                guiGraphics.blitSprite(IN_LIQUID_SPRITE, x, y, size, size);
                drawTooltip(guiGraphics, x, y, size, size, mouseX, mouseY,
                        I18n.get("confluence.trade_lock.drawer.fishing.fishing_in") + " " + tag.location());
                x += size;
            }
        }

        return y;
    }
}
