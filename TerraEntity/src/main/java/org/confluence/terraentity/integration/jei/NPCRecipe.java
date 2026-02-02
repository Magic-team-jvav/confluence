package org.confluence.terraentity.integration.jei;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.confluence.terraentity.api.npc.trade.ITrade;

public class NPCRecipe {
    ITrade trade;
    ResourceLocation id;

    DrawCallback drawResultCallback = (g, x, y)->{};

    public NPCRecipe(ITrade t, ResourceLocation npc) {
        trade = t;
        id = npc;
    }

    public NPCRecipe setDrawResultCallback(DrawCallback callback) {
        drawResultCallback = callback;
        return this;
    }


    @FunctionalInterface
    public interface DrawCallback {
        void draw(GuiGraphics g, int x, int y);
    }
}
