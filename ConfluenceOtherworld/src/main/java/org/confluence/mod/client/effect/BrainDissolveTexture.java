package org.confluence.mod.client.effect;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.mod.Confluence;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// 在贴图上生成溶解遮罩，继续使用原版实体通道，不接管光影包的帧缓冲。
public final class BrainDissolveTexture {
    private static final int STEPS = 64;
    private static final Map<ResourceLocation, Frames> TEXTURES = new HashMap<>();

    public static ResourceLocation texture(ResourceLocation source, float visibility) {
        int step = Mth.clamp(Math.round(visibility * STEPS), 0, STEPS);
        if (step == STEPS) return source;
        Minecraft minecraft = Minecraft.getInstance();
        Frames frames = TEXTURES.computeIfAbsent(source, key -> {
            try (var input = minecraft.getResourceManager().open(key)) {
                return new Frames(NativeImage.read(input), new ResourceLocation[STEPS]);
            } catch (IOException exception) {
                Confluence.LOGGER.warn("Unable to load brain dissolve texture: {}", key, exception);
                return new Frames(null, new ResourceLocation[STEPS]);
            }
        });
        if (frames.source == null) return source;
        if (frames.textures[step] != null) return frames.textures[step];

        NativeImage original = frames.source;
        NativeImage image = new NativeImage(original.getWidth(), original.getHeight(), false);
        float threshold = 1.0F - step / (float) STEPS;
        float edge = (float) Math.pow(threshold, 0.9);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = original.getPixelRGBA(x, y);
                // 固定纹理坐标噪声，避免相机移动或不同幻象之间随机闪烁。
                int hash = (x * 64 / image.getWidth()) * 374761393 + (y * 64 / image.getHeight()) * 668265263;
                hash = (hash ^ (hash >>> 13)) * 1274126177;
                float noise = ((hash ^ (hash >>> 16)) & 0xFFFF) / 65536.0F;
                if (noise < threshold) pixel = 0;
                else if (noise < edge) pixel &= 0xFFFF00FF;
                image.setPixelRGBA(x, y, pixel);
            }
        }
        return frames.textures[step] = minecraft.getTextureManager().register("brain_dissolve", new DynamicTexture(image));
    }

    public static void clearTextures() {
        for (Frames frames : TEXTURES.values()) {
            if (frames.source != null) frames.source.close();
            for (ResourceLocation texture : frames.textures) {
                if (texture != null) Minecraft.getInstance().getTextureManager().release(texture);
            }
        }
        TEXTURES.clear();
    }

    private record Frames(NativeImage source, ResourceLocation[] textures) {}
}
