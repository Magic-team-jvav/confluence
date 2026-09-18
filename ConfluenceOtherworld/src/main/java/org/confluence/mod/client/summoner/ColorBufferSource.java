package org.confluence.mod.client.summoner;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import org.jetbrains.annotations.NotNull;

public class ColorBufferSource implements MultiBufferSource {

    private final MultiBufferSource inner;
    private int colorARGB = -1;
    private float alpha = 1.0f;

    public ColorBufferSource(MultiBufferSource inner) {
        this.inner = inner;
    }

    public ColorBufferSource setColor(int argb) {
        this.colorARGB = argb;
        return this;
    }

    public ColorBufferSource setAlpha(float alpha) {
        this.alpha = alpha;
        return this;
    }

    @Override
    public @NotNull VertexConsumer getBuffer(@NotNull RenderType renderType) {
        VertexConsumer consumer = this.inner.getBuffer(renderType);
        if (this.colorARGB == -1 && this.alpha >= 1.0f) {
            return consumer;
        }
        return new ColorVertexConsumer(consumer, this.colorARGB, this.alpha);
    }

    private record ColorVertexConsumer(VertexConsumer inner, int colorARGB, float alpha) implements VertexConsumer {

        @Override
        public @NotNull VertexConsumer vertex(double x, double y, double z) {
            inner.vertex(x, y, z);
            return this;
        }

        @Override
        public @NotNull VertexConsumer color(int r, int g, int b, int a) {
            if (this.colorARGB != -1) {
                r = FastColor.ARGB32.red(this.colorARGB);
                g = FastColor.ARGB32.green(this.colorARGB);
                b = FastColor.ARGB32.blue(this.colorARGB);
                a = FastColor.ARGB32.alpha(this.colorARGB);
            }
            if (this.alpha < 1.0f) {
                a = (int) (a * this.alpha);
            }
            inner.color(r, g, b, a);
            return this;
        }

        @Override
        public @NotNull VertexConsumer uv(float u, float v) {
            inner.uv(u, v);
            return this;
        }

        @Override
        public @NotNull VertexConsumer overlayCoords(int u, int v) {
            inner.overlayCoords(u, v);
            return this;
        }

        @Override
        public @NotNull VertexConsumer uv2(int u, int v) {
            inner.uv2(u, v);
            return this;
        }

        @Override
        public @NotNull VertexConsumer normal(float x, float y, float z) {
            inner.normal(x, y, z);
            return this;
        }

        @Override
        public void endVertex() {
            inner.endVertex();
        }

        @Override
        public void defaultColor(int red, int green, int blue, int alpha) {
            inner.defaultColor(red, green, blue, alpha);
        }

        @Override
        public void unsetDefaultColor() {
            inner.unsetDefaultColor();
        }
    }
}
