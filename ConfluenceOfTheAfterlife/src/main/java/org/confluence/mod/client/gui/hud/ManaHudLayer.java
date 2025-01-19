package org.confluence.mod.client.gui.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.util.ClientUtils;
import org.jetbrains.annotations.NotNull;

public class ManaHudLayer implements LayeredDraw.Layer {
    public static final ResourceLocation ICON = Confluence.asResource("textures/gui/hud/icon.png");
    public static final Integer SIZE = 128;

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || !ClientUtils.shouldDrawSurvivalElements(minecraft)) return;
        ClientUtils.setupOverlayRenderState(true, false);
        minecraft.getProfiler().push("mana");

        int width = guiGraphics.guiWidth() - 23;
        int currentMana = ClientPacketHandler.getCurrentMana();
        int maxManaCount = ClientPacketHandler.getMaxMana() / 20;
        int currentManaToBlit;
        float ts;
        for (int i = 0; i < maxManaCount; i++) {
            currentManaToBlit = currentMana - (i + 1) * 20;
            guiGraphics.blit(ICON, width, 10 + i * 12, 0, 34, 17, 16, SIZE, SIZE);
            if (currentManaToBlit >= 0) {
                guiGraphics.blit(ICON, width + 2, 10 + i * 12, 18, 34, 13, 16, SIZE, SIZE);
            } else if (currentManaToBlit + 20 >= 0) {
                ts = ((float) (currentManaToBlit + 20)) / 20.0F;
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(width + 2 + 6.5F * (1 - ts), 10 + i * 12 + 8.5F * (1 - ts), 0.0F);
                guiGraphics.pose().scale(ts, ts, 1.0F);
                guiGraphics.blit(ICON, 0, 0, 18, 34, 13, 16, SIZE, SIZE);
                guiGraphics.pose().popPose();
            }
        }

        minecraft.getProfiler().pop();
    }
}
