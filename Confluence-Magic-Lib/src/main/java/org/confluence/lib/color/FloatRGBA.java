package org.confluence.lib.color;

import net.minecraft.util.Mth;

import java.util.Objects;

public record FloatRGBA(float red, float green, float blue, float alpha) {
    public FloatRGBA mixture(FloatRGBA another, float anotherRatio) {
        float r = Mth.clamp(red - (red - another.red) * anotherRatio, 0.0F, 1.0F);
        float g = Mth.clamp(green - (green - another.green) * anotherRatio, 0.0F, 1.0F);
        float b = Mth.clamp(blue - (blue - another.blue) * anotherRatio, 0.0F, 1.0F);
        float a = Mth.clamp(alpha - (alpha - another.alpha) * anotherRatio, 0.0F, 1.0F);
        return new FloatRGBA(r, g, b, a);
    }

    public int get() {
        return ((int) (alpha * 255) << 24) + ((int) (red * 255) << 16) + ((int) (green * 255) << 8) + (int) (blue * 255);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        return o instanceof FloatRGBA(float red1, float green1, float blue1, float alpha1) && red == red1 && blue == blue1 && green == green1 && alpha == alpha1;
    }

    @Override
    public int hashCode() {
        return Objects.hash(red, green, blue, alpha);
    }
}
