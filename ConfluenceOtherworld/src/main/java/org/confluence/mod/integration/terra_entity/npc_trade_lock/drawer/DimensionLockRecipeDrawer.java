package org.confluence.mod.integration.terra_entity.npc_trade_lock.drawer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
import org.confluence.mod.integration.terra_entity.npc_trade_lock.DimensionLock;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.api.npc.trade.TradeLockRecipeDrawer;

public class DimensionLockRecipeDrawer extends TradeLockRecipeDrawer {
    private static final ResourceLocation SPRITE = Confluence.asResource("shop_lock/dimension");

    @Override
    public int drawRecipe(ITradeLock lock, GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        if (!(lock instanceof DimensionLock(
                ResourceKey<Level> dimension
        ))) {
            return y;
        }
        var size = getRecipeSize();
        guiGraphics.blitSprite(SPRITE, x, y, size, size);
        drawTooltip(guiGraphics, x, y, size, size, mouseX, mouseY,
                I18n.get("confluence.trade_lock.drawer.dimension.title") + ": " + I18n.get(dimension.location().toLanguageKey(Registries.DIMENSION_TYPE.location().getPath())));
        return y + size;
    }
}
