package org.confluence.lib.color;

import net.minecraft.util.Mth;
import org.joml.Vector3f;

import java.util.Objects;

public record FloatRGB(float red, float green, float blue) {
    public static final FloatRGB ZERO = new FloatRGB(0.0F, 0.0F, 0.0F);

    public static FloatRGB fromVector(Vector3f vector3f) {
        return new FloatRGB(vector3f.x, vector3f.y, vector3f.z);
    }

    public static FloatRGB fromInteger(int color) {
        return new FloatRGB((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F);
    }

    public FloatRGB mixture(FloatRGB another, float anotherRatio) {
        float r = Mth.clamp(red - (red - another.red) * anotherRatio, 0.0F, 1.0F);
        float g = Mth.clamp(green - (green - another.green) * anotherRatio, 0.0F, 1.0F);
        float b = Mth.clamp(blue - (blue - another.blue) * anotherRatio, 0.0F, 1.0F);
        return new FloatRGB(r, g, b);
    }

    public int get() {
        return ((int) (red * 255) << 16) + ((int) (green * 255) << 8) + (int) (blue * 255);
    }

    public Vector3f toVector() {
        return new Vector3f(red, green, blue);
    }

    public float[] toArray() {
        return new float[]{red, green, blue};
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        return o instanceof FloatRGB(float red1, float green1, float blue1) && red == red1 && blue == blue1 && green == green1;
    }

    @Override
    public int hashCode() {
        return Objects.hash(red, green, blue);
    }
}
