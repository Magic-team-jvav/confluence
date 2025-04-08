package org.confluence.mod.client.gui.hud;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.common.TranslatableEnum;
import org.confluence.lib.util.LibClientUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.handler.ClientPacketHandler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Locale;

import static org.confluence.mod.util.ClientUtils.colorDraw;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TerraStyleManaHud implements LayeredDraw.Layer {
    private static final ResourceLocation LEGACY_TEXTURE = Confluence.asResource("textures/gui/hud/icon.png");
    private static final ResourceLocation OVERLAY_TEXTURE = Confluence.asResource("textures/gui/hud/overlay.png");
    private static final int LEGACY_SIZE = 128;
    private static final int OVERLAY_SIZE = 128;
    private static final int[] MANA = new int[]{0x5a82e2, 0xa248d7};
    private static final int[] MANA_LOW = new int[]{0x5d11ba, 0xac1a91};
    private static final int[] MANA_HIGH = new int[]{0x90aff8, 0xff94bd};

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || !LibClientUtils.shouldDrawSurvivalElements(minecraft)) return;
        LibClientUtils.setupOverlayRenderState(true, false);
        minecraft.getProfiler().push("terra_style_hud");

        ClientConfigs.manaStyle.render(guiGraphics, minecraft);

        minecraft.getProfiler().pop();
    }

    public enum Mana implements TranslatableEnum {
        LEGACY {
            @Override
            public void render(GuiGraphics guiGraphics, Minecraft minecraft) {
                int widthMana = guiGraphics.guiWidth() - 21;
                int heightMana = 4;
                int currentMana = ClientPacketHandler.getCurrentMana();
                int maxManaCount = ClientPacketHandler.getMaxMana() / 20;
                int currentManaToBlit;
                float ts;
                for (int i = 0; i < maxManaCount; i++) {
                    currentManaToBlit = currentMana - (i + 1) * 20;
                    guiGraphics.blit(LEGACY_TEXTURE, widthMana, heightMana + i * 12, 0, 34, 17, 16, LEGACY_SIZE, LEGACY_SIZE);
                    if (currentManaToBlit >= 0) {
                        guiGraphics.blit(LEGACY_TEXTURE, widthMana + 2, heightMana + i * 12, 18, 34, 13, 16, LEGACY_SIZE, LEGACY_SIZE);
                    } else if (currentManaToBlit + 20 >= 0) {
                        ts = ((float) (currentManaToBlit + 20)) / 20.0F;
                        guiGraphics.pose().pushPose();
                        guiGraphics.pose().translate(widthMana + 2 + 6.5F * (1 - ts), heightMana + i * 12 + 8.5F * (1 - ts), 0.0F);
                        guiGraphics.pose().scale(ts, ts, 1.0F);
                        guiGraphics.blit(LEGACY_TEXTURE, 0, 0, 18, 34, 13, 16, LEGACY_SIZE, LEGACY_SIZE);
                        guiGraphics.pose().popPose();
                    }
                }
            }
        },
        OVERLAY {
            @Override
            public void render(GuiGraphics guiGraphics, Minecraft minecraft) {
                float currentMana = ClientPacketHandler.getCurrentMana() / 10.0F;
                float maxMana = ClientPacketHandler.getMaxMana() / 10.0F;
                int widthMana = guiGraphics.guiWidth() / 2 + 10;
                int heightMana = guiGraphics.guiHeight() - minecraft.gui.rightHeight;
                minecraft.gui.rightHeight += 10;
                RandomSource random = RandomSource.create(1919810);
                colorDraw(guiGraphics, minecraft, random, OVERLAY_TEXTURE, MANA, MANA_HIGH, MANA_LOW, maxMana, currentMana, widthMana, heightMana, OVERLAY_SIZE, 10, false);
            }
        };

        public abstract void render(GuiGraphics guiGraphics, Minecraft minecraft);

        @Override
        public Component getTranslatedName() {
            return Component.translatable("confluence.configuration.manaStyle." + name().toLowerCase(Locale.ROOT));
        }
    }
}
