package org.confluence.mod.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class GuiSprite {
    private final ResourceLocation path;
    private final int textureW;
    private final int textureH;
    private final int u;
    private final int v;
    private final int w;
    private final int h;
    private int x;
    private int y;

    public GuiSprite(ResourceLocation path, int textureW, int textureH, int u, int v, int w, int h) {
        this.path = path;
        this.textureW = textureW;
        this.textureH = textureH;
        this.u = u;
        this.v = v;
        this.w = w;
        this.h = h;
    }

    public int getTextureW() {
        return textureW;
    }

    public int getTextureH() {
        return textureH;
    }

    public int getU() {
        return u;
    }

    public int getV() {
        return v;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public GuiSprite setPos(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public GuiSprite setX(int x) {
        this.x = x;
        return this;
    }

    public GuiSprite setY(int y) {
        this.y = y;
        return this;
    }

    public void render(GuiGraphics guiGraphics) {
        guiGraphics.blitSprite(path, textureW, textureH, u, v, x, y, 0, w, h);
    }

    public boolean isHovered(double mouseX, double mouseY) {
        return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
    }
}
