package org.confluence.mod.util.color;

public record IntegerRGBA(int red, int green, int blue, int alpha) {
    public static IntegerRGBA of(int rgba) {
        return new IntegerRGBA((rgba & 0x00FF0000) >> 16, (rgba & 0x0000FF00) >> 8, rgba & 0x000000FF, (rgba & 0xFF000000) >> 24);
    }

    public IntegerRGBA mixture(IntegerRGBA another, float anotherRatio) {
        int r = Math.round(red - (red - another.red) * anotherRatio);
        int g = Math.round(green - (green - another.green) * anotherRatio);
        int b = Math.round(blue - (blue - another.blue) * anotherRatio);
        int a = Math.round(alpha - (alpha - another.alpha) * anotherRatio);
        return new IntegerRGBA(r, g, b, a);
    }

    public int get() {
        return (alpha << 24) + (red << 16) + (green << 8) + blue;
    }
}
