package org.confluence.mod.util;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.confluence.lib.util.LibClientUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.data.saved.Team;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.item.VanityArmorItems;
import org.confluence.mod.common.item.common.BaseDyeItem;
import org.confluence.mod.common.worldgen.secret_seed.TheConstant;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public final class ClientUtils {
    public static final String GRAY_SUFFIX = ".gray";
    public static final String NEGATIVE_SUFFIX = ".negative";
    public static final Set<ResourceLocation> ORIGINAL = new HashSet<>();
    public static final ResourceLocation LEGACY_TEXTURE = Confluence.asResource("hud/icon");
    public static final ResourceLocation OVERLAY_TEXTURE = Confluence.asResource("hud/overlay");
    public static final ResourceLocation ICON_0 = Confluence.asResource("hud/icon_0");
    private static final Set<ResourceLocation> failed = new HashSet<>();
    public static final int LEGACY_SIZE = 128;
    public static final int OVERLAY_SIZE = 128;
    /// 以这个命名的bone就是专门的死亡模型
    public static final String DEATH_BONE_NAME = "_death";
    /// 以这个后缀命名的bone需要爆整个bone
    public static final String ENTIRE_BONE_SUFFIX = "_entire";

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
                    DynamicTexture texture = new DynamicTexture(LibClientUtils.copyWithGray(NativeImage.read(inputstream)));
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
            guiGraphics.blitSprite(icon, size, size, iconX, iconY, x, y, 9, 9);
        }
        if (part >= 2) {
            RenderSystem.setShaderColor(redLow, greenLow, blueLow, 1.0F);
            guiGraphics.blitSprite(icon, size, size, iconX + partDis, iconY, x, y, 9, 9);
        }
        if (part >= 3) {
            RenderSystem.setShaderColor(redHigh, greenHigh, blueHigh, 1.0F);
            guiGraphics.blitSprite(icon, size, size, iconX + partDis * 2, iconY, x, y, 9, 9);
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

    public static void colorDraw(GuiGraphics guiGraphics, Minecraft minecraft, RandomSource random, ResourceLocation texture, int[] COLOR, int[] COLOR_HIGH, int[] COLOR_LOW, float max, float current, int x, int y, int size, int uvY, boolean left) {
        colorDraw(guiGraphics, minecraft, random, texture, COLOR, COLOR_HIGH, COLOR_LOW, max, current, x, y, size, uvY, left, true);
    }

    public static void colorDraw(GuiGraphics guiGraphics, Minecraft minecraft, RandomSource random, ResourceLocation texture, int[] COLOR, int[] COLOR_HIGH, int[] COLOR_LOW, float current, int x, int y, int size, int uvY, boolean left) {
        colorDraw(guiGraphics, minecraft, random, texture, COLOR, COLOR_HIGH, COLOR_LOW, 0.0F, current, x, y, size, uvY, left, false);
    }

    public static void colorDraw(GuiGraphics guiGraphics, Minecraft minecraft, RandomSource random, ResourceLocation texture, int[] COLOR, int[] COLOR_HIGH, int[] COLOR_LOW, float max, float current, int x, int y, int size, int uvY, boolean left, boolean background) {
        int backCount = (int) (max / 2);
        int heartCount = (int) (current);
        if (max / 2 > (float) backCount) {backCount++;}
        if (current > (float) heartCount) {heartCount++;}
        for (int i = 0; i < backCount && i < 10 && background; i++) {
            guiGraphics.blitSprite(texture, size, size, 60, uvY, (x + i * 8) + ((backCount < 10 && !left) ? ((10 - backCount) * 8) : 0), y, 9, 9);
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
                color = color(random);
                color.x = COLOR[i];
                if (lineCount - i < 2) {
                    draw(x, y, guiGraphics, drawCount, COLOR[i], COLOR_HIGH[i], COLOR_LOW[i], texture, size, 0, uvY, left, 3, 20);
                }
            } else {
                color = color(random);
                if (lineCount - i < 2) {
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

    public static int getVanityDyeARGB(ExtraInventory extraInventory, int index, Player player) {
        if (index != -1) {
            ItemStack vanityArmorDye = extraInventory.getVanityArmor(index, true);
            if (!vanityArmorDye.isEmpty()) {
                Item item = vanityArmorDye.getItem();
                if (item instanceof BaseDyeItem) {
                    return BaseDyeItem.getARGB(vanityArmorDye);
                } else if (item == VanityArmorItems.TEAM_DYE.get()) {
                    Team team = PlayerSpecialData.of(player).getTeam();
                    return FastColor.ARGB32.opaque(team.getColor().getTextureDiffuseColor());
                }
            }
        }
        return -1;
    }

    public static <T extends BlockEntity> BlockEntityRendererProvider<T> rendererProvider(Supplier<BlockEntityRenderer<T>> factory) {
        return context -> factory.get();
    }

    public static AABB getRenderBoundingBox3x(BlockPos pos) {
        return new AABB(
                pos.getX() - 1,
                pos.getY() - 1,
                pos.getZ() - 1,
                pos.getX() + 2,
                pos.getY() + 2,
                pos.getZ() + 2
        );
    }

    private static final PoseStack boulderSun = new PoseStack();

    public static void renderBoulderSun(Minecraft minecraft) {
        if (ModSecretSeeds.BOULDER_WORLD.match() && minecraft.level.rainLevel <= 0.9F) {
            MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
            BlockState blockState = FunctionalBlocks.NORMAL_BOULDER.get().defaultBlockState();
            boulderSun.pushPose();
            boulderSun.mulPose(Axis.ZP.rotation(minecraft.level.getTimeOfDay(0) * Mth.TWO_PI));
            boulderSun.translate(-5, 100, -5);
            boulderSun.scale(10, 10, 10);
            minecraft.getBlockRenderer().renderSingleBlock(blockState, boulderSun, bufferSource, 0xF000F0, OverlayTexture.NO_OVERLAY);
            boulderSun.popPose();
        }
    }

    public static void postTheConstantEffect(boolean post) {
        GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
        PostChain postChain = gameRenderer.currentEffect();
        if (post) {
            if (postChain == null || !TheConstant.POST_EFFECT.toString().equals(postChain.getName())) {
                gameRenderer.loadEffect(TheConstant.POST_EFFECT);
            }
            gameRenderer.effectActive = true;
        } else {
            if (postChain != null && TheConstant.POST_EFFECT.toString().equals(postChain.getName())) {
                gameRenderer.effectActive = false;
            }
        }
    }
}
