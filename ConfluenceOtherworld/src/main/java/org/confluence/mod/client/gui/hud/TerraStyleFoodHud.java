package org.confluence.mod.client.gui.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.TranslatableEnum;
import org.confluence.lib.util.LibRenderUtils;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.event.ModClientSetups;

import java.util.Locale;

import static org.confluence.mod.util.ClientUtils.OVERLAY_SIZE;
import static org.confluence.mod.util.ClientUtils.draw;

public class TerraStyleFoodHud implements LayeredDraw.Layer {
    /// 泰拉界面饱食度条：与经验条一样细
    private static final int BAR_WIDTH = 60, BAR_HEIGHT = 5, ICON_SIZE = 7;

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!ClientConfigs.terraStyleFood || org.confluence.mod.common.CommonConfigs.DISABLE_HUNGER.get()) return; // 关闭饥饿时不显示
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || !LibRenderUtils.shouldDrawSurvivalElements(minecraft)) return;
        LibRenderUtils.setupOverlayRenderState(true, false);
        minecraft.getProfiler().push("terra_style_hud");

        (ClientConfigs.terrariaInterface ? Food.LEGACY : ClientConfigs.foodStyle).render(guiGraphics, minecraft);

        minecraft.getProfiler().pop();
    }

    public enum Food implements TranslatableEnum {
        LEGACY {
            /// 泰拉没有饥饿；开启饥饿时，泰拉界面在生命心下方画一条细长的饱食度条（与经验条同高 5 像素），
            /// 左侧一个缩小的原版鸡腿图标，不喧宾夺主。
            @Override
            public void render(GuiGraphics guiGraphics, Minecraft minecraft) {
                Player player = minecraft.player;
                if (player == null) return;
                int food = player.getFoodData().getFoodLevel();
                boolean hunger = player.hasEffect(MobEffects.HUNGER);
                int right = guiGraphics.guiWidth() - 128 + ClientConfigs.healthOffsetX + 100;
                int y = TerraStyleHealthHud.lastLegacyBottom + 2;
                int barX = right - BAR_WIDTH;
                // 图标缩到 7×7，与条垂直居中
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(barX - ICON_SIZE - 2, y - 1, 0.0F);
                guiGraphics.pose().scale(ICON_SIZE / 9.0F, ICON_SIZE / 9.0F, 1.0F);
                guiGraphics.blitSprite(ResourceLocation.withDefaultNamespace(hunger ? "hud/food_full_hunger" : "hud/food_full"), 0, 0, 9, 9);
                guiGraphics.pose().popPose();
                // 边框 + 底色
                guiGraphics.fill(barX, y, right, y + BAR_HEIGHT, 0xFF2A2233);
                guiGraphics.fill(barX + 1, y + 1, right - 1, y + BAR_HEIGHT - 1, 0xFF4A3F57);
                int inner = BAR_WIDTH - 2;
                int filled = Math.round(inner * Math.clamp(food / 20.0F, 0.0F, 1.0F));
                if (filled > 0) {
                    int main = hunger ? 0xFF6B8E3A : 0xFFC9783A;
                    int light = hunger ? 0xFF9CC25A : 0xFFF0A65A;
                    guiGraphics.fill(barX + 1, y + 1, barX + 1 + filled, y + BAR_HEIGHT - 1, main);
                    guiGraphics.fill(barX + 1, y + 1, barX + 1 + filled, y + 2, light);
                }
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
