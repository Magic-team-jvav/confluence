package org.confluence.mod.util;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Function4;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@OnlyIn(Dist.CLIENT)
public final class ClientUtils {
    public static final String GRAY_SUFFIX = ".gray";
    public static final String NEGATIVE_SUFFIX = ".negative";
    public static final float HALF_SQRT_3 = Mth.sqrt(3.0F) / 2.0F;

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

    private static final Set<ResourceLocation> failed = new HashSet<>();
    public static final Set<ResourceLocation> ORIGINAL = new HashSet<>();

    public static void clearCache() {
        failed.clear();
        ORIGINAL.clear();
    }

    public static ResourceLocation getGrayTexture(ResourceLocation original) {
        if (failed.contains(original)) return original;
        ResourceLocation gray = original.withSuffix(GRAY_SUFFIX);
        if (Minecraft.getInstance().getTextureManager().getTexture(gray, null) == null) {
            try {
                try (InputStream inputstream = Minecraft.getInstance().getResourceManager().getResourceOrThrow(original).open()) {
                    DynamicTexture texture = new DynamicTexture(copyWithGray(NativeImage.read(inputstream)));
                    Minecraft.getInstance().getTextureManager().register(gray, texture);
                }
                return gray;
            } catch (IOException ioexception) {
                failed.add(original);
                return original;
            }
        } else {
            return gray;
        }
    }

    public static void drawString(GuiGraphics guiGraphics, Font font, @Nullable String text, float x, float y, int color) {
        guiGraphics.drawString(font, text, x + 1, y, 0x000000, false);
        guiGraphics.drawString(font, text, x - 1, y, 0x000000, false);
        guiGraphics.drawString(font, text, x, y + 1, 0x000000, false);
        guiGraphics.drawString(font, text, x, y - 1, 0x000000, false);
        guiGraphics.drawString(font, text, x, y, color, false);
    }

    public static void drawColor(GuiGraphics guiGraphics, int x, int y, int iconX, int iconY, ResourceLocation icon, int color, int colorHigh, int colorLow, int size, int part, int partDis) {
        float red = ((color >> 16) & 0xFF) / 255.0F;
        float green = ((color >> 8) & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        float redHigh = ((colorHigh >> 16) & 0xFF) / 255.0F;
        float greenHigh = ((colorHigh >> 8) & 0xFF) / 255.0F;
        float blueHigh = (colorHigh & 0xFF) / 255.0F;
        float redLow = ((colorLow >> 16) & 0xFF) / 255.0F;
        float greenLow = ((colorLow >> 8) & 0xFF) / 255.0F;
        float blueLow = (colorLow & 0xFF) / 255.0F;
        if (part >= 1) {
            RenderSystem.setShaderColor(red, green, blue, 1.0F);
            guiGraphics.blit(icon, x, y, iconX, iconY, 9, 9, size, size);
        }
        if (part >= 2) {
            RenderSystem.setShaderColor(redLow, greenLow, blueLow, 1.0F);
            guiGraphics.blit(icon, x, y, iconX + partDis, iconY, 9, 9, size, size);
        }
        if (part >= 3) {
            RenderSystem.setShaderColor(redHigh, greenHigh, blueHigh, 1.0F);
            guiGraphics.blit(icon, x, y, iconX + partDis * 2, iconY, 9, 9, size, size);
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static int colorHigh(int color) {
        return (color / 255 * 55 + 200);
    }

    public static int colorLow(int color, RandomSource random) {
        int colorT = (color - 60 + random.nextInt(121));
        if (colorT < 0) {colorT = 0;}
        if (colorT > 255) {colorT = 255;}
        return colorT;
    }

    public static Vector3i color(RandomSource random) {
        int R;
        int G;
        int B;
        do {
            R = random.nextInt(256);
            G = random.nextInt(256);
            B = random.nextInt(256) + 255 - R - G;
        } while (B > 255 || B < 0);
        int color = (R << 16) | (G << 8) | B;
        int colorHigh = (colorHigh(R) << 16) | (colorHigh(G) << 8) | colorHigh(B);
        int colorLow = (colorLow(R, random) << 16) | (colorLow(G, random) << 8) | colorLow(B, random);
        return new Vector3i(color, colorHigh, colorLow);
    }

    public static void draw(int x, int y, GuiGraphics guiGraphics, int count, int color, int colorHigh, int colorLow, ResourceLocation icon, int size, int uvX, int uvY, boolean left, int part, int partDis) {
        int countT = count / 2;
        int xT = left ? (x - 8) : (x + 80);
        for (int i = 0; i < countT; i++) {
            xT = left ? (x + i * 8) : (x + 72 - i * 8);
            drawColor(guiGraphics, xT, y, uvX, uvY, icon, color, colorHigh, colorLow, size, part, partDis);
        }
        if (count - countT * 2 == 1) {
            drawColor(guiGraphics, xT + (left ? 8 : -8), y, uvX + partDis / 2, uvY, icon, color, colorHigh, colorLow, size, part, partDis);
        }
    }

    private static Map<Integer, Vector3i> COLOR_R = new HashMap<>();

    public static void colorDraw(GuiGraphics guiGraphics, Minecraft minecraft, RandomSource random, ResourceLocation texture, int[] COLOR, int[] COLOR_HIGH, int[] COLOR_LOW, float max, float current, int x, int y, int size, int uvY, boolean left, int type) {
        colorDraw(guiGraphics, minecraft, random, texture, COLOR, COLOR_HIGH, COLOR_LOW, max, current, x, y, size, uvY, left, true, type);
    }

    public static void colorDraw(GuiGraphics guiGraphics, Minecraft minecraft, RandomSource random, ResourceLocation texture, int[] COLOR, int[] COLOR_HIGH, int[] COLOR_LOW, float current, int x, int y, int size, int uvY, boolean left, int type) {
        colorDraw(guiGraphics, minecraft, random, texture, COLOR, COLOR_HIGH, COLOR_LOW, 0.0F, current, x, y, size, uvY, left, false, type);
    }

    public static void colorDraw(GuiGraphics guiGraphics, Minecraft minecraft, RandomSource random, ResourceLocation texture, int[] COLOR, int[] COLOR_HIGH, int[] COLOR_LOW, float max, float current, int x, int y, int size, int uvY, boolean left, boolean background, int type) {
        int backCount = (int) (max / 2);
        int heartCount = (int) (current);
        if (max / 2 > (float) backCount) {backCount++;}
        if (current > (float) heartCount) {heartCount++;}
        for (int i = 0; i < backCount && i < 10 && background; i++) {
            guiGraphics.blit(texture, (x + i * 8) + ((backCount < 10 && !left) ? ((10 - backCount) * 8) : 0), y, 60, uvY, 9, 9, size, size);
        }
        int lineCount = heartCount / 20;
        int drawCount;
        int lineCountDraw = lineCount;
        Vector3i color = new Vector3i(0, 0, 0);
        Vector3i colorJ;
        for (int i = 0; i <= lineCount; i++) {
            colorJ = color;
            drawCount = (i == lineCount) ? (heartCount % 20) : 20;
            if (i < Math.min(COLOR.length, Math.min(COLOR_HIGH.length, COLOR_LOW.length))) {
                color = COLOR_R.computeIfAbsent(i + 10000 * type, k -> color(random));
                color.x = COLOR[i];
                if (lineCount - i < 3) {
                    draw(x, y, guiGraphics, drawCount, COLOR[i], COLOR_HIGH[i], COLOR_LOW[i], texture, size, 0, uvY, left, 3, 20);
                }
            } else {
                color = COLOR_R.computeIfAbsent(i + 10000 * type, k -> color(random));
                if (lineCount - i < 3) {
                    draw(x, y, guiGraphics, drawCount, color.x, color.y, color.z, texture, size, 0, uvY, left, 3, 20);
                }
            }
            if (drawCount != 20 && drawCount != 0) {
                lineCountDraw = lineCount + 1;
            }
            if (drawCount == 0) {
                color = colorJ;
            }
        }
        String drawString = Integer.toString(lineCountDraw);
        if (lineCountDraw > 1) {
            drawString(guiGraphics, minecraft.font, drawString, (left) ? (x - 3 - minecraft.font.width(Component.literal(drawString))) : (x + 85), y + 1, color.x);
        }
    }
}
