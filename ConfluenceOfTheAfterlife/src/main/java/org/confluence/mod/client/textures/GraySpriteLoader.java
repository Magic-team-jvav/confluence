package org.confluence.mod.client.textures;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.Executor;

public class GraySpriteLoader extends SpriteLoader {
    public GraySpriteLoader(ResourceLocation location, int maxSupportedTextureSize, int minWidth, int minHeight) {
        super(location, maxSupportedTextureSize, minWidth, minHeight);
    }

    @Override
    public @NotNull Preparations stitch(List<SpriteContents> contents, int mipLevel, @NotNull Executor executor) {
        for (SpriteContents content : contents) {
            covertToGray(content.getOriginalImage());
        }
        return super.stitch(contents, mipLevel, executor);
    }

    public static void covertToGray(NativeImage image) {
        int[] pixels = image.getPixelsRGBA();
        for (int i = 0; i < image.getHeight(); ++i) {
            for (int j = 0; j < image.getWidth(); ++j) {
                int color = pixels[j + i * image.getWidth()];
                int a = color >>> 24;
                int b = color >> 16 & 255;
                int g = color >> 8 & 255;
                int r = color & 255;
                int avg = (r + g + b) / 3;
                image.setPixelRGBA(j, i, a << 24 | avg << 16 | avg << 8 | avg);
            }
        }
    }
}
