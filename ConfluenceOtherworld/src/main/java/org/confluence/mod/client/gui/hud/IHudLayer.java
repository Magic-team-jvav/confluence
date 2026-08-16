package org.confluence.mod.client.gui.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;

public abstract class IHudLayer implements LayeredDraw.Layer {
    private int leftPos;
    private int topPos;
    protected Minecraft minecraft;
    protected LocalPlayer player;
    protected Font font;

    public IHudLayer() {
        this.minecraft = Minecraft.getInstance();
        this.player = this.minecraft.player;
        this.font = this.minecraft.font;
    }

    public int getLeftPos() {
        return this.leftPos;
    }

    public void setLeftPos(int var1) {
        this.leftPos = var1;
    }

    public int getTopPos() {
        return this.topPos;
    }

    public void setTopPos(int var1) {
        this.topPos = var1;
    }

    protected Minecraft getMinecraft() {
        return this.minecraft;
    }

    protected void setMinecraft(Minecraft var1) {
        this.minecraft = var1;
    }

    @Nullable
    protected LocalPlayer getPlayer() {
        return this.player;
    }

    protected void setPlayer(@Nullable LocalPlayer var1) {
        this.player = var1;
    }

    public Font getFont() {
        return this.font;
    }

    public void setFont(Font var1) {
        this.font = var1;
    }

    public void init(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
    }

    public void playerChange(LocalPlayer newPlayer) {
        this.player = newPlayer;
    }

    public LocalPlayer getPlayerThrow() {
        return this.player;
    }

    public int getX() {
        return getLeftPos();
    }

    public void setX(int topPos) {
        setLeftPos(topPos);
    }

    public int getY() {
        return getTopPos();
    }

    public void setY(int leftPos) {
        setTopPos(leftPos);
    }

    public int getWidth() {
        return -1;
    }

    public int getHeight() {
        return -1;
    }
}
