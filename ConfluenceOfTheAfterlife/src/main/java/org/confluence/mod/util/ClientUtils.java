package org.confluence.mod.util;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Function4;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public final class ClientUtils {
    public static void drawImage(ResourceLocation loc, GuiGraphics g, int x, int y, int wid, int hig, int imWid, int imHig) {
        g.blit(loc, x, y, 0, 0, wid, hig, imWid, imHig);
    }

    public static void drawImage(ResourceLocation loc, GuiGraphics g, int x, int y, int imWid, int imHig) {
        drawImage(loc, g, x, y, imWid, imHig, imWid, imHig);
    }

    public static Button buildIconButton(GuiGraphics g, ResourceLocation loc, int x, int y, int wid, int hig, int icWid, int icHig, Button.OnPress onPress) {
        drawImage(loc, g, x + ((wid - icWid) / 2), y + ((hig - icHig) / 2), icWid, icHig);
        return Button.builder(Component.empty(), onPress).bounds(x, y, wid, hig).build();
    }

    public static Button buildIconButton(GuiGraphics g, ResourceLocation loc, int x, int y, int wid, int hig, int icWid, int icHig, Button.OnPress onPress, Component hoverText) {
        drawImage(loc, g, x + ((wid - icWid) / 2), y + ((hig - icHig) / 2), icWid, icHig);
        return Button.builder(Component.empty(), onPress).bounds(x, y, wid, hig).tooltip(Tooltip.create(hoverText)).build();
    }

    public static Button buildItemButton(GuiGraphics g, Item it, int x, int y, int wid, int hig, int icWid, int icHig, Button.OnPress onPress, Component hoverText, Minecraft mc) {
        BakedModel model = mc.getItemRenderer().getModel(it.getDefaultInstance(), null, null, 0);
        if (model instanceof SimpleBakedModel simple) {
            g.blit(x + ((wid - icWid) / 2), y + ((hig - icHig) / 2), 0, icWid, icHig, simple.getParticleIcon());
        }
        return Button.builder(Component.empty(), onPress).bounds(x, y, wid, hig).tooltip(Tooltip.create(hoverText)).build();
    }


    public static void drawItemWithoutTooltip(GuiGraphics guiGraphics, Item item, Minecraft mc, float x, float y, int w, int h, float scale) {
        drawItem(guiGraphics, item, mc, x, y, w, h, scale, false, 0, 0);
    }

    public static void drawItem(GuiGraphics guiGraphics, Item item, Minecraft mc, float x, float y, int w, int h, float scale, boolean renderTooltip, int mouseX, int mouseY) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 1);
        guiGraphics.pose().scale(scale, scale, 0);
        guiGraphics.renderItem(item.getDefaultInstance(), 0, 0);

        guiGraphics.pose().popPose();
        if (renderTooltip && mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h) {
            guiGraphics.renderTooltip(mc.font, item.getDefaultInstance().getTooltipLines(Item.TooltipContext.EMPTY, mc.player, TooltipFlag.ADVANCED), Optional.empty(), mouseX, mouseY);
        }
    }

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
     * @param path        文件全路径，比如<code>FMLPaths.GAMEDIR.getPrefab().resolve("redstone.png")</code>
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

    public static void writeGrayImageToFile(NativeImage nativeImage, Path path) {
        writeImageToFile(nativeImage, path, (a, r, g, b) -> {
            int avg = (r + g + b) / 3;
            return a << 24 | avg << 16 | avg << 8 | avg;
        });
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
                image.setPixelRGBA(j, i, a << 24 | avg << 16 | avg << 8 | avg);
            }
        }
        return image;
    }

    public static @NotNull ResourceLocation getGrayTexture(ResourceLocation original) {
        ResourceLocation gray = original.withSuffix(".gray");
        if (Minecraft.getInstance().getTextureManager().getTexture(gray, null) == null) {
            try {
                try (InputStream inputstream = Minecraft.getInstance().getResourceManager().getResourceOrThrow(original).open()) {
                    DynamicTexture texture = new DynamicTexture(copyWithGray(NativeImage.read(inputstream)));
                    Minecraft.getInstance().getTextureManager().register(gray, texture);
                }
                return gray;
            } catch (IOException ioexception) {
                return original;
            }
        } else {
            return gray;
        }
    }
}
