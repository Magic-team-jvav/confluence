package org.confluence.mod.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.Nullable;

public class ClickableArea {
    private final int w;
    private final int h;
    private int x;
    private int y;

    private @Nullable GuiSprite hovered;

    public ClickableArea(int w, int h) {
        this.w = w;
        this.h = h;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getEndX() {
        return x + w;
    }

    public int getEndY() {
        return y + h;
    }

    public ClickableArea setPos(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public ClickableArea setHovered(GuiSprite hovered) {
        this.hovered = hovered;
        return this;
    }

    public @Nullable GuiSprite getHovered() {
        return hovered;
    }

    public boolean isHovered(double mouseX, double mouseY) {
        return mouseX >= x && mouseX < getEndX() && mouseY >= y && mouseY < getEndY();
    }

    public void renderHovered(GuiGraphics guiGraphics, double mouseX, double mouseY) {
        if (hovered != null && hovered.isHovered(mouseX, mouseY)) hovered.render(guiGraphics);
    }
}
