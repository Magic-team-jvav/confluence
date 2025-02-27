package org.confluence.mod.util.color;

public record IntegerRGB(int red, int green, int blue) {
    public static final IntegerRGB HALLOW_A = of(0xFF0051);
    public static final IntegerRGB HALLOW_B = of(0x12FFE2);
    public static final IntegerRGB HALLOW_C = of(0xFFFA00);
    public static final IntegerRGB BLACK = of(0x000000);
    public static final IntegerRGB GRAY = of(0x828282);
    public static final IntegerRGB WHITE = of(0xFFFFFF);
    public static final IntegerRGB BLUE = of(0x9696FF);
    public static final IntegerRGB GREEN = of(0x96FF96);
    public static final IntegerRGB ORANGE = of(0xFFC896);
    public static final IntegerRGB LIGHT_RED = of(0xFF9696);
    public static final IntegerRGB PINK = of(0xFF96FF);
    public static final IntegerRGB LIGHT_PURPLE = of(0xD2A0FF);
    public static final IntegerRGB LIME = of(0x96FF0A);
    public static final IntegerRGB YELLOW = of(0xFFFF0A);
    public static final IntegerRGB CYAN = of(0x05C8FF);
    public static final IntegerRGB RED = of(0xFF2864);
    public static final IntegerRGB PURPLE = of(0xB428FF);

    public static IntegerRGB of(int rgb) {
        return new IntegerRGB((rgb & 0xFF0000) >> 16, (rgb & 0x00FF00) >> 8, rgb & 0x0000FF);
    }

    public IntegerRGB mixture(IntegerRGB another, float anotherRatio) {
        int r = Math.round(red - (red - another.red) * anotherRatio);
        int g = Math.round(green - (green - another.green) * anotherRatio);
        int b = Math.round(blue - (blue - another.blue) * anotherRatio);
        return new IntegerRGB(r, g, b);
    }

    public int mixture(int rgb, float ratio) {
        int r = Math.round(red - (red - ((rgb & 0xFF0000) >> 16)) * ratio);
        int g = Math.round(green - (green - ((rgb & 0x00FF00) >> 8)) * ratio);
        int b = Math.round(blue - (blue - (rgb & 0x0000FF)) * ratio);
        return (r << 16) + (g << 8) + b;
    }

    public int get() {
        return (red << 16) + (green << 8) + blue;
    }
}
