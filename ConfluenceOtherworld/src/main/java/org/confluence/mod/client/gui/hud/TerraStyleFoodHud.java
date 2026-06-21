package org.confluence.mod.client.gui.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.confluence.lib.util.LibRenderUtils;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.event.ModClientSetups;
import org.mesdag.portlib.client.PortDeltaTicker;
import org.mesdag.portlib.client.PortGuiLayer;
import org.mesdag.portlib.wrapper.common.PortTranslatableEnum;

import java.util.Locale;

import static org.confluence.mod.client.event.ModClientSetups.OVERLAY_SIZE;
import static org.confluence.mod.util.ClientUtils.draw;

public class TerraStyleFoodHud implements PortGuiLayer {
    @Override
    public void render(GuiGraphics guiGraphics, PortDeltaTicker deltaTracker) {
        if (!ClientConfigs.terraStyleFood) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || !LibRenderUtils.shouldDrawSurvivalElements(minecraft))
            return;
        LibRenderUtils.setupOverlayRenderState(true, false);
        minecraft.getProfiler().push("terra_style_hud");

        ClientConfigs.foodStyle.render(guiGraphics, minecraft);

        minecraft.getProfiler().pop();
    }

    public enum Food implements PortTranslatableEnum {
        LEGACY {
            @Override
            public void render(GuiGraphics guiGraphics, Minecraft minecraft) {

            }
        },
        OVERLAY {
            @Override
            public void render(GuiGraphics guiGraphics, Minecraft minecraft) {
                float food = 0.0F;
                float foodSaturation = 0.0F;
                boolean hunger = false;
                Player player = minecraft.player;
                if (player != null) {
                    food = player.getFoodData().getFoodLevel();
                    foodSaturation = player.getFoodData().getSaturationLevel();
                    hunger = player.hasEffect(MobEffects.HUNGER);
                }
                int foodI = (int) food;
                int foodSaturationI = (int) foodSaturation;
                if ((float) foodI - food > 0.0F) {
                    foodI++;
                }
                if ((float) foodSaturationI - foodSaturation > 0.0F) {
                    foodSaturationI++;
                }
                int white = 0xFFFFFF;
                int widthFood = guiGraphics.guiWidth() / 2 + 10;
                int heightFood = guiGraphics.guiHeight() - ((ForgeGui) minecraft.gui).rightHeight;
                ((ForgeGui) minecraft.gui).rightHeight += 10;
                for (int i = 0; i < 10; i++) {
                    guiGraphics.blitSprite(ModClientSetups.OVERLAY_SPRITE, OVERLAY_SIZE, OVERLAY_SIZE, 60, 30, (widthFood + i * 8), heightFood, 9, 9);
                }
                if (hunger) {
                    draw(widthFood, heightFood, guiGraphics, foodI, white, white, white, ModClientSetups.OVERLAY_SPRITE, OVERLAY_SIZE, 20, 30, false, 1, 20);
                } else {
                    draw(widthFood, heightFood, guiGraphics, foodI, white, white, white, ModClientSetups.OVERLAY_SPRITE, OVERLAY_SIZE, 0, 30, false, 1, 20);
                }
                draw(widthFood, heightFood, guiGraphics, foodSaturationI, white, white, white, ModClientSetups.OVERLAY_SPRITE, OVERLAY_SIZE, 40, 30, false, 1, 20);
            }
        };

        public abstract void render(GuiGraphics guiGraphics, Minecraft minecraft);

        @Override
        public Component getTranslatedName() {
            return Component.translatable("confluence.configuration.foodStyle." + name().toLowerCase(Locale.ROOT));
        }
    }
}
