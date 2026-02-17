package org.confluence.mod.client.gui.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.TranslatableEnum;

import java.util.Locale;

public class TerraStyleSoulHud implements LayeredDraw.Layer {
    private static final int[] SOUL = new int[]{0xa7d0e9};
    private static final int[] SOUL_LOW = new int[]{0x74a5c2};
    private static final int[] SOUL_HIGH = new int[]{0xfefff9};

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
//        Minecraft minecraft = Minecraft.getInstance();
//        if (minecraft.options.hideGui || !LibClientUtils.shouldDrawSurvivalElements(minecraft)) return;
//        LibClientUtils.setupOverlayRenderState(true, false);
//        minecraft.getProfiler().push("terra_style_hud");
//
//        ClientConfigs.soulStyle.render(guiGraphics, minecraft);
//
//        minecraft.getProfiler().pop();
    }

    public enum Soul implements TranslatableEnum {
        LEGACY {
            @Override
            public void render(GuiGraphics guiGraphics, Minecraft minecraft) {
                /*int widthMana = guiGraphics.guiWidth() - 21 + ClientConfigs.manaOffsetX;
                int heightMana = 4 + ClientConfigs.manaOffsetY;
                float currentMana = ClientPacketHandler.getCurrentMana();
                int maxManaCount = ClientPacketHandler.getMaxMana() / 20;
                float currentManaToBlit;
                float ts;
                for (int i = 0; i < maxManaCount; i++) {
                    currentManaToBlit = currentMana - (i + 1) * 20;
                    guiGraphics.blitSprite(LEGACY_TEXTURE, LEGACY_SIZE, LEGACY_SIZE, 0, 34, widthMana, heightMana + i * 12, 17, 16);
                    if (currentManaToBlit >= 0) {
                        guiGraphics.blitSprite(LEGACY_TEXTURE, LEGACY_SIZE, LEGACY_SIZE, 18, 34, widthMana + 2, heightMana + i * 12, 13, 16);
                    } else if (currentManaToBlit + 20 >= 0) {
                        ts = (currentManaToBlit + 20) / 20.0F;
                        guiGraphics.pose().pushPose();
                        guiGraphics.pose().translate(widthMana + 2 + 6.5F * (1 - ts), heightMana + i * 12 + 8.5F * (1 - ts), 0.0F);
                        guiGraphics.pose().scale(ts, ts, 1.0F);
                        guiGraphics.blitSprite(LEGACY_TEXTURE, LEGACY_SIZE, LEGACY_SIZE, 18, 34, 0, 0, 13, 16);
                        guiGraphics.pose().popPose();
                    }
                }*/
            }
        },
        OVERLAY {
            @Override
            public void render(GuiGraphics guiGraphics, Minecraft minecraft) {
//                if (minecraft.player != null && !ClientPacketHandler.isFallenSoulCoreActive()) return;
//                float currentSoul = ClientPacketHandler.getCurrentSoul() / 5.0F;
//                float maxSoul = ClientPacketHandler.getMaxSoul() / 5.0F;
//                int widthSoul = guiGraphics.guiWidth() / 2 + 10 + ClientConfigs.soulOffsetX;
//                int heightSoul = guiGraphics.guiHeight() - minecraft.gui.rightHeight + ClientConfigs.soulOffsetY;
//                minecraft.gui.rightHeight += 10;
//                RandomSource random = RandomSource.create(1234329);
//                colorDraw(guiGraphics, minecraft, random, ClientUtils.OVERLAY_TEXTURE, SOUL, SOUL_HIGH, SOUL_LOW, maxSoul, currentSoul, widthSoul, heightSoul, ClientUtils.OVERLAY_SIZE, 60, false);
            }
        };

        public abstract void render(GuiGraphics guiGraphics, Minecraft minecraft);

        @Override
        public Component getTranslatedName() {
            return Component.translatable("confluence.configuration.soulStyle." + name().toLowerCase(Locale.ROOT));
        }
    }
}
