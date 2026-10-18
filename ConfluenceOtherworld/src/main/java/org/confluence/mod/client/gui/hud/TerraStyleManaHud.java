package org.confluence.mod.client.gui.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraft.util.Mth;
import org.confluence.lib.util.LibRenderUtils;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.event.ModClientSetups;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.mesdag.portlib.client.PortDeltaTicker;
import org.mesdag.portlib.client.PortGuiLayer;
import org.mesdag.portlib.wrapper.common.PortTranslatableEnum;
import org.confluence.mod.common.attachment.SoulStorage;
import org.confluence.mod.util.PlayerUtils;

import java.util.Locale;

import static org.confluence.mod.client.event.ModClientSetups.LEGACY_SIZE;
import static org.confluence.mod.util.ClientUtils.colorDraw;

public class TerraStyleManaHud implements PortGuiLayer {
    private static final int[] MANA = new int[]{0x5a82e2, 0xa248d7};
    private static final int[] MANA_LOW = new int[]{0x5d11ba, 0xac1a91};
    private static final int[] MANA_HIGH = new int[]{0x90aff8, 0xff94bd};

    /// 灵魂条统一使用 #abfffe 淡蓝色：调色板改为中性亮度，最终颜色由染色决定。
    private static final int[] SOUL = new int[]{0x9dfbff, 0x7ffff3};
    private static final int[] SOUL_LOW = new int[]{0xF0FFFF, 0xF0FFFF};
    private static final int[] SOUL_HIGH = new int[]{0x32fffb, 0x32d6ff};
    private static final float SOUL_TINT_RED = 0xAB / 255.0F;
    private static final float SOUL_TINT_GREEN = 1.0F;
    private static final float SOUL_TINT_BLUE = 0xFE / 255.0F;
    private static final int RESOURCE_PER_LEGACY_ICON = 20;
    private static final int OVERLAY_RESOURCE_DIVISOR = 10;
    private static final int OVERLAY_HUD_SPACING = 10;
    /// overlay 图集中魔力条使用的行（星星）。
    private static final int OVERLAY_DEFAULT_UV_Y = 10;
    /// overlay 图集中灵魂条使用的最下面一行图例。
    private static final int OVERLAY_SOUL_UV_Y = 60;

    @Override
    public void render(GuiGraphics guiGraphics, PortDeltaTicker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || !LibRenderUtils.shouldDrawSurvivalElements(minecraft))
            return;
        LibRenderUtils.setupOverlayRenderState(true, false);
        minecraft.getProfiler().push("terra_style_hud");

        ClientConfigs.manaStyle.render(guiGraphics, minecraft);

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

    public enum Mana implements PortTranslatableEnum {
        LEGACY {
            @Override
            public void render(GuiGraphics guiGraphics, Minecraft minecraft) {
                int widthMana = guiGraphics.guiWidth() - 21 + ClientConfigs.manaOffsetX;
                int heightMana = 4 + ClientConfigs.manaOffsetY;
                float currentMana = getCurrentResource(minecraft);
                int maxMana = getMaxResource(minecraft);
                int maxManaCount = Mth.ceil((double) maxMana / RESOURCE_PER_LEGACY_ICON);
                boolean soulMode = PlayerUtils.isSoulMode(minecraft.player);
                if (soulMode) {
                    guiGraphics.setColor(SOUL_TINT_RED, SOUL_TINT_GREEN, SOUL_TINT_BLUE, 1.0F);
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
                int heightMana = guiGraphics.guiHeight() - ((ForgeGui) minecraft.gui).rightHeight + ClientConfigs.manaOffsetY;
                ((ForgeGui) minecraft.gui).rightHeight += OVERLAY_HUD_SPACING;
                RandomSource random = RandomSource.create(1919810);
                if (soulMode) {
                    guiGraphics.setColor(SOUL_TINT_RED, SOUL_TINT_GREEN, SOUL_TINT_BLUE, 1.0F);
                }
                colorDraw(guiGraphics, minecraft, random, ModClientSetups.OVERLAY_SPRITE,
                        soulMode ? SOUL : MANA, soulMode ? SOUL_HIGH : MANA_HIGH, soulMode ? SOUL_LOW : MANA_LOW,
                        maxMana, currentMana, widthMana, heightMana, ModClientSetups.OVERLAY_SIZE,
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
}
