package org.confluence.mod.client.gui.hud;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.TranslatableEnum;
import org.confluence.lib.util.LibClientUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.ClientConfigs;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Locale;

import static org.confluence.mod.util.ClientUtils.draw;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TerraStyleFoodHud implements LayeredDraw.Layer {
    private static final ResourceLocation LEGACY_TEXTURE = Confluence.asResource("textures/gui/hud/icon.png");
    private static final ResourceLocation OVERLAY_TEXTURE = Confluence.asResource("textures/gui/hud/overlay.png");
//    private static final int LEGACY_SIZE = 128;
    private static final int OVERLAY_SIZE = 128;
//    private static final int[] Food = new int[]{0xab311e, 0x5d11ba, 0x41a9ba, 0x37c438, 0xeed536};
//    private static final int[] Food_LOW = new int[]{0xab1f5d, 0x9d44ac, 0x12f7dd, 0x1fab7f, 0xf7b60b};
//    private static final int[] Food_HIGH = new int[]{0xffb5b5, 0xd6e7eb, 0xbdced0, 0xd6eead, 0xeff4ce};

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!ClientConfigs.terraStyleFood) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || !LibClientUtils.shouldDrawSurvivalElements(minecraft)) return;
        LibClientUtils.setupOverlayRenderState(true, false);
        minecraft.getProfiler().push("terra_style_hud");

        ClientConfigs.foodStyle.render(guiGraphics, minecraft);

        minecraft.getProfiler().pop();
    }

    public enum Food implements TranslatableEnum {
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
                int heightFood = guiGraphics.guiHeight() - minecraft.gui.rightHeight;
                minecraft.gui.rightHeight += 10;
                for (int i = 0; i < 10; i++) {
                    guiGraphics.blit(OVERLAY_TEXTURE, (widthFood + i * 8), heightFood, 60, 30, 9, 9, OVERLAY_SIZE, OVERLAY_SIZE);
                }
                if (hunger) {
                    draw(widthFood, heightFood, guiGraphics, foodI, white, white, white, OVERLAY_TEXTURE, OVERLAY_SIZE, 20, 30, false, 1, 20);
                } else {
                    draw(widthFood, heightFood, guiGraphics, foodI, white, white, white, OVERLAY_TEXTURE, OVERLAY_SIZE, 0, 30, false, 1, 20);
                }
                draw(widthFood, heightFood, guiGraphics, foodSaturationI, white, white, white, OVERLAY_TEXTURE, OVERLAY_SIZE, 40, 30, false, 1, 20);
            }
        };

        public abstract void render(GuiGraphics guiGraphics, Minecraft minecraft);

        @Override
        public Component getTranslatedName() {
            return Component.translatable("confluence.configuration.foodStyle." + name().toLowerCase(Locale.ROOT));
        }
    }
}
