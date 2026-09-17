package org.confluence.mod.client.effect;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/// 保留贴图明度和透明度，用原版实体顶点色承载空间渐变，不依赖自定义着色器。
public final class DivaSlimeVertexConsumer implements VertexConsumer {
    private static final Map<ResourceLocation, ResourceLocation> TEXTURES = new HashMap<>();
    private final VertexConsumer delegate;
    private final Matrix4f localTransform;
    private final float time;
    private final Vector3f position = new Vector3f();
    private int tint = 0xFFFFFF;

    public DivaSlimeVertexConsumer(VertexConsumer delegate, Matrix4f localTransform, float time) {
        this.delegate = delegate;
        this.localTransform = localTransform;
        this.time = time;
    }

    public static ResourceLocation texture(ResourceLocation source) {
        return TEXTURES.computeIfAbsent(source, key -> {
            Minecraft minecraft = Minecraft.getInstance();
            try (var input = minecraft.getResourceManager().open(key)) {
                NativeImage image = NativeImage.read(input);
                for (int y = 0; y < image.getHeight(); y++) {
                    for (int x = 0; x < image.getWidth(); x++) {
                        int pixel = image.getPixelRGBA(x, y);
                        int brightness = Math.max(pixel & 255, Math.max(pixel >>> 8 & 255, pixel >>> 16 & 255));
                        image.setPixelRGBA(x, y, pixel & 0xFF000000 | brightness * 0x010101);
                    }
                }
                return minecraft.getTextureManager().register("diva_slime_compat", new DynamicTexture(image));
            } catch (IOException exception) {
                org.confluence.mod.Confluence.LOGGER.warn("Unable to prepare diva slime shader compatibility texture: {}", key, exception);
                return key;
            }
        });
    }

    public static void clearTextures() {
        TEXTURES.forEach((source, generated) -> {
            if (!source.equals(generated))
                Minecraft.getInstance().getTextureManager().release(generated);
        });
        TEXTURES.clear();
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        localTransform.transformPosition(position.set((float) x, (float) y, (float) z));
        float hue = time + position.y * 0.65F + position.x * 0.25F;
        tint = Mth.hsvToRgb(hue - Mth.floor(hue), 1.0F, 1.0F);
        delegate.vertex(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        delegate.color(red * (tint >> 16 & 255) / 255, green * (tint >> 8 & 255) / 255,
                blue * (tint & 255) / 255, alpha);
        return this;
    }

    @Override
    public VertexConsumer uv(float u, float v) {
        delegate.uv(u, v);
        return this;
    }

    @Override
    public VertexConsumer overlayCoords(int u, int v) {
        delegate.overlayCoords(u, v);
        return this;
    }

    @Override
    public VertexConsumer uv2(int u, int v) {
        delegate.uv2(u, v);
        return this;
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        delegate.normal(x, y, z);
        return this;
    }

    @Override
    public void endVertex() {delegate.endVertex();}

    @Override
    public void defaultColor(int r, int g, int b, int a) {delegate.defaultColor(r, g, b, a);}

    @Override
    public void unsetDefaultColor() {delegate.unsetDefaultColor();}
}
