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

    /// `vertex`→`addVertex`、`color`→`setColor`、`uv`→`setUv`、`overlayCoords`→`setUv1`/`setOverlay`、
    /// `uv2`→`setUv2`/`setLight`、`normal`→`setNormal`，**`endVertex()` 已删除**；
    private record ColorVertexConsumer(VertexConsumer inner, int colorARGB, float alpha) implements VertexConsumer {

        @Override
        public @NotNull VertexConsumer addVertex(float x, float y, float z) {
            inner.addVertex(x, y, z);
            return this;
        }

        @Override
        public @NotNull VertexConsumer setColor(int r, int g, int b, int a) {
            if (this.colorARGB != -1) {
                r = FastColor.ARGB32.red(this.colorARGB);
                g = FastColor.ARGB32.green(this.colorARGB);
                b = FastColor.ARGB32.blue(this.colorARGB);
                a = FastColor.ARGB32.alpha(this.colorARGB);
            }
            if (this.alpha < 1.0f) {
                a = (int) (a * this.alpha);
            }
            inner.setColor(r, g, b, a);
            return this;
        }

        @Override
        public @NotNull VertexConsumer setUv(float u, float v) {
            inner.setUv(u, v);
            return this;
        }

        @Override
        public @NotNull VertexConsumer setUv1(int u, int v) {
            inner.setUv1(u, v);
            return this;
        }

        @Override
        public @NotNull VertexConsumer setUv2(int u, int v) {
            inner.setUv2(u, v);
            return this;
        }

        @Override
        public @NotNull VertexConsumer setNormal(float x, float y, float z) {
            inner.setNormal(x, y, z);
            return this;
        }
    }
}
