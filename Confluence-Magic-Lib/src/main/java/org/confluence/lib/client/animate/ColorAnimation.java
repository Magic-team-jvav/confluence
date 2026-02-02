package org.confluence.lib.client.animate;

import net.minecraft.util.Mth;

public class ColorAnimation {
    protected final ColorState color;

    public ColorAnimation(int red, int green, int blue) {
        this.color = new ColorState(red, green, blue);
    }

    public ColorAnimation(int color) {
        this.color = new ColorState(color);
    }

    public ColorAnimation(ColorState colorState) {
        this.color = colorState;
    }

    public int getColor() {
        return color.color();
    }

    public int getRed() {
        return color.red;
    }

    public int getGreen() {
        return color.green;
    }

    public int getBlue() {
        return color.blue;
    }

    public ColorState getColorState() {
        return color;
    }

    public void updateColor() {}

    public String getType() {
        return "default";
    }

    public static class ColorState {
        // 颜色的最大值
        private static final int MAX_COLOR_VALUE = 255;
        private static final int MIN_COLOR_VALUE = 0;

        private int red;
        private int green;
        private int blue;

        public ColorState(int color) {
            this((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF);
        }

        public ColorState(int red, int green, int blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        // 更新颜色的方法
        public void updateColor(int deltaR, int deltaG, int deltaB) {
            this.red = Mth.clamp(red + deltaR, MIN_COLOR_VALUE, MAX_COLOR_VALUE);
            this.green = Mth.clamp(green + deltaG, MIN_COLOR_VALUE, MAX_COLOR_VALUE);
            this.blue = Mth.clamp(blue + deltaB, MIN_COLOR_VALUE, MAX_COLOR_VALUE);
        }

        public void setColor(int color) {
            this.red = (color >> 16) & 0xFF;
            this.green = (color >> 8) & 0xFF;
            this.blue = color & 0xFF;
        }

        public int red() {
            return red;
        }

        public int green() {
            return green;
        }

        public int blue() {
            return blue;
        }

        public int color() {
            return (red << 16) + (green << 8) + blue;
        }
    }
}
