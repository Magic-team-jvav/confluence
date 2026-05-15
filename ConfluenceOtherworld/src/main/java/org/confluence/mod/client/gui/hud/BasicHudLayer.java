package org.confluence.mod.client.gui.hud;

import java.util.Objects;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;

public abstract class BasicHudLayer extends IHudLayer {
    private int screenWidth;
    private int screenHeight;

    public int getScreenWidth() {
        return this.screenWidth;
    }

    protected void setScreenWidth(int var1) {
        this.screenWidth = var1;
    }

    public int getScreenHeight() {
        return this.screenHeight;
    }

    protected void setScreenHeight(int var1) {
        this.screenHeight = var1;
    }

    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (getMinecraft().isPaused() || getMinecraft().screen != null || getMinecraft().options.hideGui) {
            return;
        }
        this.init(guiGraphics, deltaTracker);
        this.renderDrawLayer(guiGraphics, deltaTracker);
    }

    protected abstract void renderDrawLayer(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

    public void init(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        int newScreenWidth = guiGraphics.guiWidth();
        int newScreenHeight = guiGraphics.guiHeight();
        LocalPlayer newPlayer = this.getMinecraft().player;
        if (newPlayer != null && this.getPlayer() != newPlayer) {
            this.playerChange(newPlayer);
        }

        boolean isWidthChange = newScreenWidth != this.screenWidth;
        boolean isHeightChange = newScreenHeight != this.screenHeight;
        if (isWidthChange || isHeightChange) {
            this.sizeChange(newScreenWidth, newScreenHeight);
        }

    }

    public void playerChange(LocalPlayer newPlayer) {
        this.setPlayer(newPlayer);
    }

    protected void sizeChange(int newScreenWidth, int newScreenHeight) {
        this.screenWidth = newScreenWidth;
        this.screenHeight = newScreenHeight;
    }
}
