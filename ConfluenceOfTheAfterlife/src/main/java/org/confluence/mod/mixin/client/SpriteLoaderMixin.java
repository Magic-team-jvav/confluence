package org.confluence.mod.mixin.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(SpriteLoader.class)
public abstract class SpriteLoaderMixin {
    @Shadow
    @Final
    private ResourceLocation location;

    @ModifyVariable(method = "stitch", at = @At("HEAD"), argsOnly = true)
    private List<SpriteContents> generateGraySprites(List<SpriteContents> contents) {
        if (location.equals(TextureAtlas.LOCATION_BLOCKS)) {
            List<SpriteContents> neoContents = Lists.newArrayList(contents);
            for (SpriteContents content : contents) {
                ResourceLocation name = content.name();
                if (name.getPath().startsWith("item/")) continue;
                NativeImage neoImage = confluence$copyWithGray(content.getOriginalImage());
                SpriteContents neoContent = new SpriteContents(
                        name.withSuffix(".gray"),
                        new FrameSize(content.width(), content.height()),
                        neoImage,
                        content.metadata());
                neoContents.add(neoContent);
            }
            return neoContents;
        }
        return contents;
    }

    @Unique
    private static NativeImage confluence$copyWithGray(NativeImage original) {
        int width = original.getWidth();
        int height = original.getHeight();
        NativeImage image = new NativeImage(original.format(), width, height, false);
        image.copyFrom(original);
        int[] pixels = original.getPixelsRGBA();
        int[] average = new int[pixels.length];
        int u = 0;
        int d = 255;
        for (int i = 0; i < height; i++) {
            int i1 = i * width;
            for (int j = 0; j < width; j++) {
                int index = j + i1;
                int color = pixels[index];
                int a = color >>> 24;
                int b = color >> 16 & 255;
                int g = color >> 8 & 255;
                int r = color & 255;
                int avg = (int) (r * 0.3F + g * 0.59F + b * 0.11F);
                if (avg > u) u = avg;
                if (avg < d) d = avg;
                average[index] = a << 8 | avg;
            }
        }
        int i1 = u - d;
        float x;
        int y;
        if (94.72F < i1) {
            x = 94.72F / i1;
            y = 105;
        } else {
            x = 1.0F;
            y = 199 - i1;
        }
        for (int i = 0; i < height; i++) {
            int i2 = i * width;
            for (int j = 0; j < width; j++) {
                int color = average[j + i2];
                int avg = color & 255;
                avg = (int) ((avg - d) * x) + y;
                int a = color >> 8 & 255;
                image.setPixelRGBA(j, i, a << 24 | avg << 16 | avg << 8 | avg);
            }
        }
        return image;
    }
}
