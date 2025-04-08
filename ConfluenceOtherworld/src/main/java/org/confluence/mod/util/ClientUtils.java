package org.confluence.mod.util;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.xiaohunao.mine_team.common.team.Team;
import com.xiaohunao.mine_team.common.team.TeamManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.confluence.lib.util.LibClientUtils;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.VanityArmorItems;
import org.confluence.mod.common.item.vanity_armor.BaseDyeItem;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.OptionalInt;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public final class ClientUtils {
    public static final String GRAY_SUFFIX = ".gray";
    public static final String NEGATIVE_SUFFIX = ".negative";
    public static final Set<ResourceLocation> ORIGINAL = new HashSet<>();
    private static final Set<ResourceLocation> failed = new HashSet<>();

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

    public static OptionalInt getVanityDyeColor(ExtraInventory extraInventory, int index, Player player) {
        if (index != -1) {
            ItemStack vanityArmorDye = extraInventory.getVanityArmorDye(index);
            if (!vanityArmorDye.isEmpty()) {
                Item item = vanityArmorDye.getItem();
                if (item instanceof BaseDyeItem dyeItem) {
                    return OptionalInt.of(dyeItem.color);
                } else if (item == VanityArmorItems.TEAM_DYE.get()) {
                    Team team = TeamManager.getTeam(player);
                    if (team != null) {
                        return OptionalInt.of(0xFF << 24 | team.getColor());
                    }
                }
            }
        }
        return OptionalInt.empty();
    }

    /**
     * 获取实体所的发光强度
     *
     * @param returnValue 原发光强度
     * @return 目标发光强度，值域在[-15, 15]。负数代表仅水下光照
     */
    public static int getLuminance(Entity entity, int returnValue) {
        int luminance = 0;
        if (entity instanceof LivingEntity living) {
            if (living.getItemBySlot(EquipmentSlot.HEAD).is(ModTags.Items.PROVIDE_LIGHT)) {
                luminance += 10;
            }
            if (living.hasEffect(ModEffects.SHINE)) {
                luminance += 10;
            }
        }
        return returnValue >= 0 ? Math.min(returnValue + luminance, 15) : Math.max(returnValue - luminance, -15);
    }
}
