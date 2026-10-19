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
import org.confluence.mod.util.ClientUtils;
import org.confluence.mod.util.PlayerUtils;

import java.util.Locale;

import static org.confluence.mod.util.ClientUtils.LEGACY_SIZE;
import static org.confluence.mod.util.ClientUtils.SOUL_TINT_BLUE;
import static org.confluence.mod.util.ClientUtils.SOUL_TINT_GREEN;
import static org.confluence.mod.util.ClientUtils.SOUL_TINT_RED;
import static org.confluence.mod.util.ClientUtils.colorDraw;

public class TerraStyleManaHud implements LayeredDraw.Layer {
    private static final int[] MANA = new int[]{0x5a82e2, 0xa248d7};
    private static final int[] MANA_LOW = new int[]{0x5d11ba, 0xac1a91};
    private static final int[] MANA_HIGH = new int[]{0x90aff8, 0xff94bd};
    /// 灵魂条统一使用 #abfffe 淡蓝色：调色板改为中性亮度，最终颜色由染色决定。
    private static final int[] SOUL = new int[]{0x9dfbff, 0x7ffff3};
    private static final int[] SOUL_LOW = new int[]{0xF0FFFF, 0xF0FFFF};
    private static final int[] SOUL_HIGH = new int[]{0x32fffb, 0x32d6ff};
    private static final int RESOURCE_PER_LEGACY_ICON = 20;
    private static final int LEGACY_ICON_SPACING = 12;
    /// 精致样式（LEGACY）灵魂条专用图元：图集里「魂器底座 + 骷髅」一对，每 20 灵魂一格自右上向下排布。
    ///
    /// 底座 14x15（图集 33,35），骷髅 10x11（图集 51,37）；骷髅在底座内再内缩 2 像素。
    private static final int SOUL_LEGACY_FRAME_U = 33;
    private static final int SOUL_LEGACY_FRAME_V = 35;
    private static final int SOUL_LEGACY_FRAME_W = 14;
    private static final int SOUL_LEGACY_FRAME_H = 15;
    private static final int SOUL_LEGACY_FILL_U = 51;
    private static final int SOUL_LEGACY_FILL_V = 37;
    private static final int SOUL_LEGACY_FILL_W = 10;
    private static final int SOUL_LEGACY_FILL_H = 11;
    /// 底座在魔力条 17 像素宽口径内的居中偏移。
    private static final int SOUL_LEGACY_FRAME_OFFSET_X = 1;
    /// 骷髅相对底座的内缩。
    private static final int SOUL_LEGACY_FILL_OFFSET_X = 2;
    private static final int SOUL_LEGACY_FILL_OFFSET_Y = 2;
    private static final int OVERLAY_RESOURCE_DIVISOR = 10;
    private static final int OVERLAY_HUD_SPACING = 10;
    /// overlay 图集中魔力条使用的行（星星）。
    private static final int OVERLAY_DEFAULT_UV_Y = 10;
    /// overlay 图集中灵魂条使用的最下面一行图例。
    private static final int OVERLAY_SOUL_UV_Y = 60;

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || minecraft.player == null
                || minecraft.player.isCreative() || minecraft.player.isSpectator()
                || !(minecraft.getCameraEntity() instanceof Player)) return;
        LibRenderUtils.setupOverlayRenderState(true, false);
        minecraft.getProfiler().push("terra_style_hud");

        /// 灵魂条与魔力条各有独立的样式开关，同一时间只会画其中一个。
        if (PlayerUtils.isSoulMode(minecraft.player)) {
            ClientConfigs.soulStyle.render(guiGraphics, minecraft);
        } else {
            ClientConfigs.manaStyle.render(guiGraphics, minecraft);
        }

        minecraft.getProfiler().pop();
    }

    /// 读取当前资源模式对应的客户端真值。
    private static float getCurrentResource(Minecraft minecraft) {
        return PlayerUtils.isSoulMode(minecraft.player) ? SoulStorage.of(minecraft.player).getCurrentSoul() : ClientPacketHandler.getCurrentMana();
    }

    /// 读取当前资源模式对应的资源上限。
    private static int getMaxResource(Minecraft minecraft) {
        return PlayerUtils.isSoulMode(minecraft.player) ? SoulStorage.of(minecraft.player).getMaxSoul() : ClientPacketHandler.getMaxMana();
    }

    /// 精致样式的灵魂条：每 20 灵魂一格，自右上向下排布「魂器底座 + 骷髅」。
    ///
    /// 不足一格的余量沿用魔力条的做法，把骷髅按比例从右下角缩放，不再额外染色。
    private static void renderLegacySoulBar(GuiGraphics guiGraphics, float currentSoul, int maxSoul, int x, int y) {
        int soulCount = Math.ceilDiv(Math.max(maxSoul, 1), RESOURCE_PER_LEGACY_ICON);
        int frameX = x + SOUL_LEGACY_FRAME_OFFSET_X;
        for (int i = 0; i < soulCount; i++) {
            int iconCapacity = Math.min(RESOURCE_PER_LEGACY_ICON, maxSoul - i * RESOURCE_PER_LEGACY_ICON);
            float remaining = currentSoul - i * RESOURCE_PER_LEGACY_ICON - iconCapacity;
            int height = y + i * LEGACY_ICON_SPACING;
            blitSoulFrame(guiGraphics, frameX, height);
            if (remaining >= 0) {
                blitSoulFill(guiGraphics, frameX + SOUL_LEGACY_FILL_OFFSET_X, height + SOUL_LEGACY_FILL_OFFSET_Y);
            } else if (remaining + iconCapacity >= 0) {
                float scale = (remaining + iconCapacity) / iconCapacity;
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(
                        frameX + SOUL_LEGACY_FILL_OFFSET_X + SOUL_LEGACY_FILL_W * 0.5F * (1 - scale),
                        height + SOUL_LEGACY_FILL_OFFSET_Y + SOUL_LEGACY_FILL_H * 0.5F * (1 - scale),
                        0.0F);
                guiGraphics.pose().scale(scale, scale, 1.0F);
                blitSoulFill(guiGraphics, 0, 0);
                guiGraphics.pose().popPose();
            }
        }
    }

    private static void blitSoulFrame(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blitSprite(ModClientSetups.LEGACY_SPRITE, LEGACY_SIZE, LEGACY_SIZE,
                SOUL_LEGACY_FRAME_U, SOUL_LEGACY_FRAME_V, x, y, SOUL_LEGACY_FRAME_W, SOUL_LEGACY_FRAME_H);
    }

    private static void blitSoulFill(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blitSprite(ModClientSetups.LEGACY_SPRITE, LEGACY_SIZE, LEGACY_SIZE,
                SOUL_LEGACY_FILL_U, SOUL_LEGACY_FILL_V, x, y, SOUL_LEGACY_FILL_W, SOUL_LEGACY_FILL_H);
    }

    public enum Mana implements TranslatableEnum {
        LEGACY {
            @Override
            public void render(GuiGraphics guiGraphics, Minecraft minecraft) {
                int widthMana = guiGraphics.guiWidth() - 21 + ClientConfigs.manaOffsetX;
                int heightMana = 4 + ClientConfigs.manaOffsetY;
                float currentMana = getCurrentResource(minecraft);
                int maxMana = getMaxResource(minecraft);
                /// 灵魂条走独立图元，不再用魔力条染色，避免把新的底座/骷髅一起染成青色。
                if (PlayerUtils.isSoulMode(minecraft.player)) {
                    renderLegacySoulBar(guiGraphics, currentMana, maxMana, widthMana, heightMana);
                    return;
                }
                int maxManaCount = Math.ceilDiv(maxMana, RESOURCE_PER_LEGACY_ICON);
                float currentManaToBlit;
                float ts;
                for (int i = 0; i < maxManaCount; i++) {
                    int iconCapacity = Math.min(RESOURCE_PER_LEGACY_ICON, maxMana - i * RESOURCE_PER_LEGACY_ICON);
                    currentManaToBlit = currentMana - i * RESOURCE_PER_LEGACY_ICON - iconCapacity;
                    guiGraphics.blitSprite(ModClientSetups.LEGACY_SPRITE, LEGACY_SIZE, LEGACY_SIZE, 0, 34, widthMana, heightMana + i * LEGACY_ICON_SPACING, 17, 16);
                    if (currentManaToBlit >= 0) {
                        guiGraphics.blitSprite(ModClientSetups.LEGACY_SPRITE, LEGACY_SIZE, LEGACY_SIZE, 18, 34, widthMana + 2, heightMana + i * LEGACY_ICON_SPACING, 13, 16);
                    } else if (currentManaToBlit + iconCapacity >= 0) {
                        ts = (currentManaToBlit + iconCapacity) / iconCapacity;
                        guiGraphics.pose().pushPose();
                        guiGraphics.pose().translate(widthMana + 2 + 6.5F * (1 - ts), heightMana + i * LEGACY_ICON_SPACING + 8.5F * (1 - ts), 0.0F);
                        guiGraphics.pose().scale(ts, ts, 1.0F);
                        guiGraphics.blitSprite(ModClientSetups.LEGACY_SPRITE, LEGACY_SIZE, LEGACY_SIZE, 18, 34, 0, 0, 13, 16);
                        guiGraphics.pose().popPose();
                    }
                }
            }
        },
        OVERLAY {
            @Override
            public void render(GuiGraphics guiGraphics, Minecraft minecraft) {
                float resourceCurrent = getCurrentResource(minecraft);
                int resourceMax = getMaxResource(minecraft);
                float currentMana = resourceCurrent / OVERLAY_RESOURCE_DIVISOR;
                float maxMana = (float) resourceMax / OVERLAY_RESOURCE_DIVISOR;
                boolean soulMode = PlayerUtils.isSoulMode(minecraft.player);
                int widthMana = guiGraphics.guiWidth() / 2 + 10 + ClientConfigs.manaOffsetX;
                int heightMana = guiGraphics.guiHeight() - minecraft.gui.rightHeight + ClientConfigs.manaOffsetY;
                minecraft.gui.rightHeight += OVERLAY_HUD_SPACING;
                RandomSource random = RandomSource.create(1919810);
                if (soulMode) {
                    guiGraphics.setColor(SOUL_TINT_RED, SOUL_TINT_GREEN, SOUL_TINT_BLUE, 1.0F);
                }
                colorDraw(guiGraphics, minecraft, random, ModClientSetups.OVERLAY_SPRITE,
                        soulMode ? SOUL : MANA, soulMode ? SOUL_HIGH : MANA_HIGH, soulMode ? SOUL_LOW : MANA_LOW,
                        maxMana, currentMana, widthMana, heightMana, ClientUtils.OVERLAY_SIZE,
                        soulMode ? OVERLAY_SOUL_UV_Y : OVERLAY_DEFAULT_UV_Y, false);
                if (soulMode) {
                    guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
                }
            }
        };

        public abstract void render(GuiGraphics guiGraphics, Minecraft minecraft);

        @Override
        public Component getTranslatedName() {
            return Component.translatable("confluence.configuration.manaStyle." + name().toLowerCase(Locale.ROOT));
        }
    }

    /// 灵魂条独立的样式开关：画法与 {@link Mana} 完全一致，只是用自己的翻译键，
    /// 让 HUD 设置里能分别调「魔力值样式」和「灵魂值样式」。
    public enum Soul implements TranslatableEnum {
        LEGACY(Mana.LEGACY),
        OVERLAY(Mana.OVERLAY);

        private final Mana style;

        Soul(Mana style) {
            this.style = style;
        }

        /// 该灵魂样式对应的实际画法。
        public Mana style() {
            return style;
        }

        public void render(GuiGraphics guiGraphics, Minecraft minecraft) {
            style.render(guiGraphics, minecraft);
        }

        @Override
        public Component getTranslatedName() {
            return Component.translatable("confluence.configuration.soulStyle." + name().toLowerCase(Locale.ROOT));
        }
    }
}
