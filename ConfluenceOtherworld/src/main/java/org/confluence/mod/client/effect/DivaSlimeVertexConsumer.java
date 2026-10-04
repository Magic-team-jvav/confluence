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
    public VertexConsumer addVertex(float x, float y, float z) {
        localTransform.transformPosition(position.set(x, y, z));
        float hue = time + position.y * 0.65F + position.x * 0.25F;
        tint = Mth.hsvToRgb(hue - Mth.floor(hue), 0.60F, 1.0F);
        delegate.addVertex(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer setColor(int red, int green, int blue, int alpha) {
        delegate.setColor(red * (tint >> 16 & 255) / 255, green * (tint >> 8 & 255) / 255,
                blue * (tint & 255) / 255, alpha);
        return this;
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
        delegate.setUv(u, v);
        return this;
    }

    @Override
    public VertexConsumer setUv1(int u, int v) {
        delegate.setUv1(u, v);
        return this;
    }

    @Override
    public VertexConsumer setUv2(int u, int v) {
        delegate.setUv2(u, v);
        return this;
    }

    @Override
    public VertexConsumer setNormal(float x, float y, float z) {
        delegate.setNormal(x, y, z);
        return this;
    }
}
