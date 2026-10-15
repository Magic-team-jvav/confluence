package org.confluence.mod.client.gui.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.TranslatableEnum;
import org.confluence.lib.util.LibRenderUtils;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.event.ModClientSetups;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.common.attachment.SoulStorage;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.util.ClientUtils;

import java.awt.*;
import java.util.Locale;

import static org.confluence.mod.util.ClientUtils.LEGACY_SIZE;
import static org.confluence.mod.util.ClientUtils.colorDraw;

public class TerraStyleManaHud implements LayeredDraw.Layer {
    private static final int[] MANA = new int[]{0x5a82e2, 0xa248d7};
    private static final int[] MANA_LOW = new int[]{0x5d11ba, 0xac1a91};
    private static final int[] MANA_HIGH = new int[]{0x90aff8, 0xff94bd};
    private static final int RESOURCE_PER_LEGACY_ICON = 20;
    private static final float RESOURCE_PER_OVERLAY_UNIT = 10.0F;
    private static final float SOUL_HUE_RANGE = 360.0F;
    private static final float SOUL_COLOR_SATURATION = 0.7F;
    private static final float SOUL_COLOR_HIGH_SATURATION = 0.35F;
    private static final float SOUL_COLOR_LOW_BRIGHTNESS = 0.65F;
    private static final int LEGACY_ICON_WIDTH = 17;
    private static final int LEGACY_ICON_HEIGHT = 16;
    private static final int LEGACY_ICON_SPACING = 12;
    private static final int OVERLAY_HUD_WIDTH = 81;
    private static final int OVERLAY_HUD_HEIGHT = 9;
    private static final int OVERLAY_HUD_SPACING = 10;
    private static final int OVERLAY_UNITS_PER_LAYER = 20;
    private static final int OVERLAY_LAYER_COUNT_OFFSET_X = 85;
    private static final int OVERLAY_LAYER_COUNT_OUTLINE_WIDTH = 1;
    private static final int DETAIL_TEXT_GAP = 2;
    private static final int DETAIL_TEXT_COLOR = 0xFFFFFFFF;
    private static final String RESOURCE_VALUE_SEPARATOR = "/";

    private static int getSoulColor(float saturation, float brightness) {
        return Color.HSBtoRGB(ClientConfigs.soulcererBackgroundHue / SOUL_HUE_RANGE, saturation, brightness) & 0xFFFFFF;
    }

    private static void setSoulColor(GuiGraphics guiGraphics, int color) {
        guiGraphics.setColor(((color >> 16) & 0xFF) / 255.0F, ((color >> 8) & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, 1.0F);
    }

    private static String formatResourceValue(float value) {
        return Integer.toString((int) value);
    }

    private static void renderResourceDetails(GuiGraphics guiGraphics, Minecraft minecraft, int x, int y, boolean rightAligned) {
        if (minecraft.player == null || !minecraft.player.isShiftKeyDown()) return;
        SoulStorage soulStorage = minecraft.player.getData(ModAttachmentTypes.SOUL_STORAGE);
        boolean soulMode = soulStorage.isSoulMode();
        float current = soulMode ? soulStorage.getCurrentSoul() : ClientPacketHandler.getCurrentMana();
        int maximum = soulMode ? soulStorage.getMaxSoul() : ClientPacketHandler.getMaxMana();
        String details = formatResourceValue(current) + RESOURCE_VALUE_SEPARATOR + maximum;
        int textX = rightAligned ? x - minecraft.font.width(details) : x;
        guiGraphics.drawString(minecraft.font, details, textX, y, DETAIL_TEXT_COLOR);
    }

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui) {
            return;
        }
        if (minecraft.player == null) {
            return;
        }
        if (minecraft.player.isSpectator()) {
            return;
        }
        if (!(minecraft.getCameraEntity() instanceof Player)) {
            return;
        }
        if (!minecraft.player.isCreative() && !LibRenderUtils.shouldDrawSurvivalElements(minecraft)) {
            return;
        }
        LibRenderUtils.setupOverlayRenderState(true, false);
        minecraft.getProfiler().push("terra_style_hud");

        ClientConfigs.manaStyle.render(guiGraphics, minecraft);

        minecraft.getProfiler().pop();
    }

    public enum Mana implements TranslatableEnum {
        LEGACY {
            @Override
            public void render(GuiGraphics guiGraphics, Minecraft minecraft) {
                int widthMana = guiGraphics.guiWidth() - 21 + ClientConfigs.manaOffsetX;
                int heightMana = 4 + ClientConfigs.manaOffsetY;
                SoulStorage soulStorage = minecraft.player == null ? null : minecraft.player.getData(ModAttachmentTypes.SOUL_STORAGE);
                boolean soulMode = soulStorage != null && soulStorage.isSoulMode();
                float currentMana = soulMode ? soulStorage.getCurrentSoul() : ClientPacketHandler.getCurrentMana();
                int maxMana = soulMode ? soulStorage.getMaxSoul() : ClientPacketHandler.getMaxMana();
                int maxManaCount = Math.ceilDiv(maxMana, RESOURCE_PER_LEGACY_ICON);
                if (soulMode) {
                    setSoulColor(guiGraphics, getSoulColor(SOUL_COLOR_SATURATION, 1.0F));
                }
                float currentManaToBlit;
                float ts;
                for (int i = 0; i < maxManaCount; i++) {
                    int iconCapacity = Math.min(RESOURCE_PER_LEGACY_ICON, maxMana - i * RESOURCE_PER_LEGACY_ICON);
                    currentManaToBlit = currentMana - i * RESOURCE_PER_LEGACY_ICON - iconCapacity;
                    guiGraphics.blitSprite(ModClientSetups.LEGACY_SPRITE, LEGACY_SIZE, LEGACY_SIZE, 0, 34, widthMana, heightMana + i * 12, 17, 16);
                    if (currentManaToBlit >= 0) {
                        guiGraphics.blitSprite(ModClientSetups.LEGACY_SPRITE, LEGACY_SIZE, LEGACY_SIZE, 18, 34, widthMana + 2, heightMana + i * 12, 13, 16);
                    } else if (currentManaToBlit + iconCapacity >= 0) {
                        ts = (currentManaToBlit + iconCapacity) / iconCapacity;
                        guiGraphics.pose().pushPose();
                        guiGraphics.pose().translate(widthMana + 2 + 6.5F * (1 - ts), heightMana + i * 12 + 8.5F * (1 - ts), 0.0F);
                        guiGraphics.pose().scale(ts, ts, 1.0F);
                        guiGraphics.blitSprite(ModClientSetups.LEGACY_SPRITE, LEGACY_SIZE, LEGACY_SIZE, 18, 34, 0, 0, 13, 16);
                        guiGraphics.pose().popPose();
                    }
                }
                if (soulMode) {
                    guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
                }
                int columnHeight = maxManaCount > 0 ? (maxManaCount - 1) * LEGACY_ICON_SPACING + LEGACY_ICON_HEIGHT : 0;
                renderResourceDetails(guiGraphics, minecraft, widthMana + LEGACY_ICON_WIDTH, heightMana + columnHeight + DETAIL_TEXT_GAP, true);
            }
        },
        OVERLAY {
            @Override
            public void render(GuiGraphics guiGraphics, Minecraft minecraft) {
                SoulStorage soulStorage = minecraft.player == null ? null : minecraft.player.getData(ModAttachmentTypes.SOUL_STORAGE);
                boolean soulMode = soulStorage != null && soulStorage.isSoulMode();
                float currentMana = (soulMode ? soulStorage.getCurrentSoul() : ClientPacketHandler.getCurrentMana()) / RESOURCE_PER_OVERLAY_UNIT;
                float maxMana = (soulMode ? soulStorage.getMaxSoul() : ClientPacketHandler.getMaxMana()) / RESOURCE_PER_OVERLAY_UNIT;
                int widthMana = guiGraphics.guiWidth() / 2 + 10 + ClientConfigs.manaOffsetX;
                int heightMana = guiGraphics.guiHeight() - minecraft.gui.rightHeight + ClientConfigs.manaOffsetY;
                minecraft.gui.rightHeight += OVERLAY_HUD_SPACING;
                RandomSource random = RandomSource.create(1919810);
                int[] colors = MANA;
                int[] colorsHigh = MANA_HIGH;
                int[] colorsLow = MANA_LOW;
                if (soulMode) {
                    int color = getSoulColor(SOUL_COLOR_SATURATION, 1.0F);
                    int colorHigh = getSoulColor(SOUL_COLOR_HIGH_SATURATION, 1.0F);
                    int colorLow = getSoulColor(SOUL_COLOR_SATURATION, SOUL_COLOR_LOW_BRIGHTNESS);
                    colors = new int[]{color, color};
                    colorsHigh = new int[]{colorHigh, colorHigh};
                    colorsLow = new int[]{colorLow, colorLow};
                    setSoulColor(guiGraphics, color);
                }
                colorDraw(guiGraphics, minecraft, random, ModClientSetups.OVERLAY_SPRITE, colors, colorsHigh, colorsLow, maxMana, currentMana, widthMana, heightMana, ClientUtils.OVERLAY_SIZE, 10, false);
                if (soulMode) {
                    guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
                }
                int layerCount = Math.ceilDiv((int) Math.ceil(currentMana), OVERLAY_UNITS_PER_LAYER);
                int detailX = widthMana + OVERLAY_HUD_WIDTH + DETAIL_TEXT_GAP;
                if (layerCount > 1) {
                    detailX = widthMana + OVERLAY_LAYER_COUNT_OFFSET_X + minecraft.font.width(Integer.toString(layerCount)) + OVERLAY_LAYER_COUNT_OUTLINE_WIDTH + DETAIL_TEXT_GAP;
                }
                renderResourceDetails(guiGraphics, minecraft, detailX, heightMana + (OVERLAY_HUD_HEIGHT - minecraft.font.lineHeight) / 2, false);
            }
        };

        public abstract void render(GuiGraphics guiGraphics, Minecraft minecraft);

        @Override
        public Component getTranslatedName() {
            return Component.translatable("confluence.configuration.manaStyle." + name().toLowerCase(Locale.ROOT));
        }
    }
}
