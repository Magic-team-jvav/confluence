package org.confluence.mod.client.gui;

public class ClickableArea {
    private final int w;
    private final int h;
    private int x;
    private int y;
    private boolean active = false;

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

    public ClickableArea setActive(boolean active) {
        this.active = active;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isHovered(double mouseX, double mouseY) {
        return active && mouseX >= x && mouseX < getEndX() && mouseY >= y && mouseY < getEndY();
    }
}
