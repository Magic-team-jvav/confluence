package org.confluence.mod.client.gui.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.common.TranslatableEnum;
import org.confluence.lib.util.LibRenderUtils;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.event.ModClientSetups;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.util.ClientUtils;

import java.util.Locale;

import static org.confluence.mod.util.ClientUtils.LEGACY_SIZE;
import static org.confluence.mod.util.ClientUtils.colorDraw;

public class TerraStyleManaHud implements LayeredDraw.Layer {
    private static final int[] MANA = new int[]{0x5a82e2, 0xa248d7};
    private static final int[] MANA_LOW = new int[]{0x5d11ba, 0xac1a91};
    private static final int[] MANA_HIGH = new int[]{0x90aff8, 0xff94bd};

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || !LibRenderUtils.shouldDrawSurvivalElements(minecraft)) return;
        LibRenderUtils.setupOverlayRenderState(true, false);
        minecraft.getProfiler().push("terra_style_hud");

        ClientConfigs.manaStyle.render(guiGraphics, minecraft);

        minecraft.getProfiler().pop();
    }

    public enum Mana implements TranslatableEnum {
        LEGACY {
            @Override
            public void render(GuiGraphics guiGraphics, Minecraft minecraft) {
//                if (minecraft.player != null && ClientPacketHandler.isFallenSoulCoreActive()) return;
                int widthMana = guiGraphics.guiWidth() - 21 + ClientConfigs.manaOffsetX;
                int heightMana = 4 + ClientConfigs.manaOffsetY;
                float currentMana = ClientPacketHandler.getCurrentMana();
                int maxManaCount = ClientPacketHandler.getMaxMana() / 20;
                float currentManaToBlit;
                float ts;
                for (int i = 0; i < maxManaCount; i++) {
                    currentManaToBlit = currentMana - (i + 1) * 20;
                    guiGraphics.blitSprite(ModClientSetups.LEGACY_SPRITE, LEGACY_SIZE, LEGACY_SIZE, 0, 34, widthMana, heightMana + i * 12, 17, 16);
                    if (currentManaToBlit >= 0) {
                        guiGraphics.blitSprite(ModClientSetups.LEGACY_SPRITE, LEGACY_SIZE, LEGACY_SIZE, 18, 34, widthMana + 2, heightMana + i * 12, 13, 16);
                    } else if (currentManaToBlit + 20 >= 0) {
                        ts = (currentManaToBlit + 20) / 20.0F;
                        guiGraphics.pose().pushPose();
                        guiGraphics.pose().translate(widthMana + 2 + 6.5F * (1 - ts), heightMana + i * 12 + 8.5F * (1 - ts), 0.0F);
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
//                if (minecraft.player != null && ClientPacketHandler.isFallenSoulCoreActive()) return;
                float currentMana = ClientPacketHandler.getCurrentMana() / 10.0F;
                float maxMana = ClientPacketHandler.getMaxMana() / 10.0F;
                int widthMana = guiGraphics.guiWidth() / 2 + 10 + ClientConfigs.manaOffsetX;
                int heightMana = guiGraphics.guiHeight() - minecraft.gui.rightHeight + ClientConfigs.manaOffsetY;
                minecraft.gui.rightHeight += 10;
                RandomSource random = RandomSource.create(1919810);
                colorDraw(guiGraphics, minecraft, random, ModClientSetups.OVERLAY_SPRITE, MANA, MANA_HIGH, MANA_LOW, maxMana, currentMana, widthMana, heightMana, ClientUtils.OVERLAY_SIZE, 10, false);
            }
        };

        public abstract void render(GuiGraphics guiGraphics, Minecraft minecraft);

        @Override
        public Component getTranslatedName() {
            return Component.translatable("confluence.configuration.manaStyle." + name().toLowerCase(Locale.ROOT));
        }
    }
}
