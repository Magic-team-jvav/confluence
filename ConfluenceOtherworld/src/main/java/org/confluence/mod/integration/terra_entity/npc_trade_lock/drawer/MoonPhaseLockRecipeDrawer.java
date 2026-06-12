package org.confluence.mod.integration.terra_entity.npc_trade_lock.drawer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.MoonPhase;
import org.confluence.mod.integration.terra_entity.npc_trade_lock.MoonPhaseLock;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.api.npc.trade.TradeLockRecipeDrawer;

import java.util.List;

public class MoonPhaseLockRecipeDrawer extends TradeLockRecipeDrawer {
    @Override
    public int drawRecipe(ITradeLock lock, GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        if (!(lock instanceof MoonPhaseLock(List<MoonPhase> moonPhases))) {
            return y;
        }
        var size = getRecipeSize();
        for (var moonPhase : moonPhases) {
            String moonPhaseTexture = getMoonPhaseTexture(moonPhase);
            guiGraphics.blit(Confluence.asResource("textures/environment/specific_moon/tr_" + moonPhaseTexture), x, y, size, size, 0, 0, 50, 50, 50, 50);
            drawTooltip(guiGraphics, x, y, size, size, mouseX, mouseY,
                    I18n.get("confluence.trade_lock.drawer.moon_phase.title") + ": " + moonPhase.getSerializedName());
            x += size;
        }
        return y + size;
    }

    private String getMoonPhaseTexture(MoonPhase moonPhase) {
        return switch (moonPhase) {
            case FULL_MOON -> "full_moon.png";
            case WANING_GIBBOUS -> "waning_gibbous.png";
            case THIRD_QUARTER -> "third_quarter.png";
            case WAXING_CRESCENT -> "waxing_crescent.png";
            case NEW_MOON -> "new_moon.png";
            case WANING_CRESCENT -> "waning_crescent.png";
            case FIRST_QUARTER -> "first_quarter.png";
            case WAXING_GIBBOUS -> "waxing_gibbous.png";
        };
    }
}
