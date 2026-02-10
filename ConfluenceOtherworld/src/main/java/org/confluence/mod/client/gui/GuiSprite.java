package org.confluence.mod.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class GuiSprite {
    protected final ResourceLocation path;
    protected final int textureW;
    protected final int textureH;
    protected final int u;
    protected final int v;
    protected final int w;
    protected final int h;
    protected int x;
    protected int y;

    protected @Nullable GuiSprite hovered;

    public GuiSprite(ResourceLocation path, int width, int height) {
        this(path, width, height, 0, 0, width, height);
    }

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

    public GuiSprite setHovered(GuiSprite hovered) {
        this.hovered = hovered;
        return this;
    }

    public @Nullable GuiSprite getHovered() {
        return hovered;
    }

    public void render(GuiGraphics guiGraphics) {
        guiGraphics.blitSprite(path, textureW, textureH, u, v, x, y, 0, w, h);
    }

    public void render(GuiGraphics guiGraphics, float partialTick) {
        render(guiGraphics);
    }

    public boolean isHovered(double mouseX, double mouseY) {
        return mouseX >= x && mouseX < getEndX() && mouseY >= y && mouseY < getEndY();
    }

    public void renderSelfAndHovered(GuiGraphics guiGraphics, double mouseX, double mouseY) {
        render(guiGraphics);
        if (hovered != null && hovered.isHovered(mouseX, mouseY)) {
            hovered.render(guiGraphics);
        }
    }

    public void renderHoveredAndSelf(GuiGraphics guiGraphics, double mouseX, double mouseY) {
        if (hovered != null && hovered.isHovered(mouseX, mouseY)) {
            hovered.render(guiGraphics);
        }
        render(guiGraphics);
    }

    public void renderSelfOrHovered(GuiGraphics guiGraphics, double mouseX, double mouseY) {
        if (hovered != null && hovered.isHovered(mouseX, mouseY)) {
            hovered.render(guiGraphics);
        } else {
            render(guiGraphics);
        }
    }

    public void renderAligned(GuiGraphics guiGraphics, int alignX, int alignY) {
        guiGraphics.blitSprite(path, textureW, textureH, u, v, x + (alignX - w) / 2, y + (alignY - h) / 2, w, h);
    }
}
