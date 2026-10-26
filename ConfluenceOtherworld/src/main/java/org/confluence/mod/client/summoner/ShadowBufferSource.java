package org.confluence.mod.client.summoner;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.world.inventory.InventoryMenu;
import org.jetbrains.annotations.NotNull;

final class ShadowBufferSource implements MultiBufferSource {

    private static final VertexConsumer DISCARD = new VertexConsumer() {
        @Override
        public @NotNull VertexConsumer vertex(double x, double y, double z) {
            return this;
        }

        @Override
        public @NotNull VertexConsumer color(int r, int g, int b, int a) {
            return this;
        }

        @Override
        public @NotNull VertexConsumer uv(float u, float v) {
            return this;
        }

        @Override
        public @NotNull VertexConsumer overlayCoords(int u, int v) {
            return this;
        }

        @Override
        public @NotNull VertexConsumer uv2(int u, int v) {
            return this;
        }

        @Override
        public @NotNull VertexConsumer normal(float x, float y, float z) {
            return this;
        }

        @Override
        public void endVertex() {
        }

        @Override
        public void defaultColor(int red, int green, int blue, int alpha) {
        }

        @Override
        public void unsetDefaultColor() {
        }
    };

    private final MultiBufferSource inner;

    ShadowBufferSource(MultiBufferSource inner, float alpha) {
        this.inner = new ColorBufferSource(inner).setAlpha(alpha);
    }

    @Override
    public @NotNull VertexConsumer getBuffer(@NotNull RenderType type) {
        if (type == LyraRenderTypes.getTrail() || (type.format() != DefaultVertexFormat.NEW_ENTITY && type.format() != DefaultVertexFormat.BLOCK)) {
            return DISCARD;
        }
        RenderType shadowType = type == Sheets.translucentItemSheet() ? RenderType.entityTranslucent(InventoryMenu.BLOCK_ATLAS) : type;
        return inner.getBuffer(shadowType);
    }
}
