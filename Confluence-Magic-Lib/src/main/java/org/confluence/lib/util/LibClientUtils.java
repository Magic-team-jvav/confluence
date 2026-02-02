package org.confluence.lib.util;

import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.datafixers.util.Function4;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.item.IFunctionCouldEnable;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class LibClientUtils {
    public static final float HALF_SQRT_3 = (float) (Math.sqrt(3) / 2.0);
    public static final Quaternionf ANGLE_45 = Axis.YP.rotationDegrees(45);
    public static final Quaternionf ANGLE_180 = Axis.ZP.rotationDegrees(180);
    public static final Quaternionf ANGLE_N90 = Axis.YP.rotationDegrees(-90);
    public static final int[] FULL_BRIGHT = {0xF000F0, 0xF000F0, 0xF000F0, 0xF000F0};
    public static final float INV_255 = 1.0F / 255.0F;
    public static final ClampedItemPropertyFunction COULD_ENABLE_PROPERTY_FUNCTION = (stack, level, living, seed) -> {
        CompoundTag tag = LibUtils.getItemStackNbtIfPresent(stack);
        return tag != null && tag.getBoolean(IFunctionCouldEnable.DISABLE_KEY) ? 0 : 1;
    };

    public static void setupOverlayRenderState(boolean blend, boolean depthTest) {
        if (blend) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
        } else {
            RenderSystem.disableBlend();
        }

        if (depthTest) {
            RenderSystem.enableDepthTest();
        } else {
            RenderSystem.disableDepthTest();
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
    }

    public static boolean shouldDrawSurvivalElements(Minecraft minecraft) {
        return minecraft.gameMode.canHurtPlayer() && minecraft.getCameraEntity() instanceof Player;
    }

    /**
     * 将游戏缓存的贴图写入文件
     *
     * @param nativeImage 游戏缓存的贴图
     * @param path        文件全路径，比如<code>FMLPaths.GAMEDIR.get().resolve("redstone.png")</code>
     * @param argbMixer   argb的混合方法
     */
    public static void writeImageToFile(NativeImage nativeImage, Path path, Function4<Integer, Integer, Integer, Integer, Integer> argbMixer) {
        int[] pixels = nativeImage.getPixelsRGBA();
        Path parent = path.getParent();
        try {
            if (!Files.exists(parent)) Files.createDirectories(parent);
            BufferedImage image = new BufferedImage(nativeImage.getWidth(), nativeImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            for (int i = 0; i < nativeImage.getHeight(); ++i) {
                for (int j = 0; j < nativeImage.getWidth(); ++j) {
                    int color = pixels[j + i * nativeImage.getWidth()];
                    int a = color >>> 24;
                    int b = color >> 16 & 255;
                    int g = color >> 8 & 255;
                    int r = color & 255;
                    image.setRGB(j, i, argbMixer.apply(a, r, g, b));
                }
            }
            ImageIO.write(image, "png", path.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeRawImageToFile(NativeImage nativeImage, Path path) {
        writeImageToFile(nativeImage, path, (a, r, g, b) -> a << 24 | r << 16 | g << 8 | b);
    }

    public static NativeImage copyWithGray(NativeImage original) {
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
                int b = color & 255;
                int g = color >> 8 & 255;
                int r = color >> 16 & 255;
                int avg = (int) (r * 0.3F + g * 0.59F + b * 0.11F);
                if (avg > u) u = avg;
                if (avg < d) d = avg;
                average[index] = a << 8 | avg;
            }
        }
        int i1 = u - d;
        float x;
        int y;
        if (94 < i1) { // 94.72F < i1
            x = 94.72F / i1;
            y = 105;
        } else {
            x = 1.0F;
            y = 199 - i1; // 94.72F - i1 + 105
        }
        for (int i = 0; i < height; i++) {
            int i2 = i * width;
            for (int j = 0; j < width; j++) {
                int color = average[j + i2];
                int avg = color & 255;
                avg = (int) ((avg - d) * x) + y;
                int a = color >> 8 & 255;
                image.setPixelRGBA(j, i, FastColor.ARGB32.color(a, avg, avg, avg));
            }
        }
        return image;
    }

    public static NativeImage copyWithNegative(NativeImage original) {
        NativeImage image = new NativeImage(original.format(), original.getWidth(), original.getHeight(), false);
        image.copyFrom(original);
        image.applyToAllPixels(color -> {
            int a = color >>> 24;
            int b = color & 255;
            int g = color >> 8 & 255;
            int r = color >> 16 & 255;
            return FastColor.ARGB32.color(a, 255 - r, 255 - g, 255 - b);
        });
        return image;
    }

    public static NativeImage replaceWithBlueWhite(int width, int height) {
        NativeImage blueWhite = new NativeImage(width, height, false);
        for (int i = 0; i < height; i++) {
            int color = i % 4 < 2 ? -256 : -1;
            for (int j = 0; j < width; j++) {
                blueWhite.setPixelRGBA(j, i, color);
            }
        }
        return blueWhite;
    }

    public static NativeImage getGuiItem(Item item, int size) {
        return getGuiItem(item.getDefaultInstance(), size);
    }

    public static NativeImage getGuiItem(ItemStack stack, int size) {
        TextureTarget target = new TextureTarget(size, size, true, Minecraft.ON_OSX);
        target.setClearColor(0, 0, 0, 0);
        Minecraft minecraft = Minecraft.getInstance();
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        target.clear(Minecraft.ON_OSX);
        target.bindWrite(true);
        RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0, size, size, 0, -1000F, 1000F), VertexSorting.ORTHOGRAPHIC_Z);

        Matrix4fStack view = RenderSystem.getModelViewStack();
        view.pushMatrix();
        view.translation(0F, 0F, 0F);
        RenderSystem.applyModelViewMatrix();

        Lighting.setupNetherLevel();
        GuiGraphics guiGraphics = new GuiGraphics(minecraft, bufferSource);
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        float scale = size / 16F;
        pose.scale(scale, scale, 1);
        guiGraphics.renderItem(stack, 0, 0, 251017);
        pose.popPose();

        target.bindRead();
        RenderSystem.bindTexture(target.getColorTextureId());
        NativeImage image = new NativeImage(size, size, false);
        image.downloadTexture(0, false);
        image.flipY();
        target.unbindRead();
        target.unbindWrite();
        view.popMatrix();
        RenderSystem.applyModelViewMatrix();
        return image;
    }
}
