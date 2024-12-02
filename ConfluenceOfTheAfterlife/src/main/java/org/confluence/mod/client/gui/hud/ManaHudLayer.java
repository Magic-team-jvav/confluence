package org.confluence.mod.client.gui.hud;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.util.GuiUtils;

public class ManaHudLayer implements LayeredDraw.Layer {
    public static final ResourceLocation STAR_SINGLE = Confluence.asResource("textures/gui/hud/star_single.png");
    public static final ResourceLocation STAR_A = Confluence.asResource("textures/gui/hud/star_a.png");
    public static final ResourceLocation STAR_C = Confluence.asResource("textures/gui/hud/star_c.png");
    public static final ResourceLocation STAR_B = Confluence.asResource("textures/gui/hud/star_b.png");
    public static final ResourceLocation STAR_FILL = Confluence.asResource("textures/gui/hud/star_fill.png");

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || !GuiUtils.shouldDrawSurvivalElements(minecraft)) return;
        GuiUtils.setupOverlayRenderState(true, false);
        minecraft.getProfiler().push("mana");

        int screenWidth = guiGraphics.guiWidth();
        int currentMana = ClientPacketHandler.getCurrentMana();
        int maxManaCount = ClientPacketHandler.getMaxMana() / 20;
        int currentManaCount = currentMana / 20;
        float lastManaSize = (currentMana - currentManaCount * 20.0F) / 20.0F;
        int manaCount = -1;
        if (maxManaCount >= 2) {
            manaCount = maxManaCount - 2;
        }
        if (maxManaCount == 1) {//如果只有一个魔力格
            guiGraphics.blit(STAR_SINGLE, screenWidth - 20, 10, 0, 0, 15, 16, 15, 16);
        } else {//如果不止一个魔力格
            //绘制魔力条顶部
            guiGraphics.blit(STAR_A, screenWidth - 20, 10, 0, 0, 15, 13, 15, 13);

            //绘制魔力格底部
            guiGraphics.blit(STAR_C, screenWidth - 20, 23 + manaCount * 11, 0, 0, 15, 14, 15, 14);

            //绘制魔力格中部
            while (manaCount > 0) {
                guiGraphics.blit(STAR_B, screenWidth - 20, 12 + manaCount * 11, 0, 0, 15, 11, 15, 11);
                manaCount -= 1;
            }
        }

        //绘制最下方不完整的星
        if (lastManaSize != 0.0F) {
            PoseStack pose = guiGraphics.pose();
            pose.pushPose();
            pose.translate(screenWidth - 18 + (11 - 11 * lastManaSize) / 2, 12 + currentManaCount * 11 + (12 - 12 * lastManaSize) / 2, 0.0F);
            pose.scale(lastManaSize, lastManaSize, 0.0F);
            guiGraphics.blit(STAR_FILL, 0, 0, 0, 0, 11, 12, 11, 12);
            pose.popPose();
        }

        //绘制完整的星
        while (currentManaCount > 0) {
            guiGraphics.blit(STAR_FILL, screenWidth - 18, 1 + currentManaCount * 11, 0, 0, 11, 12, 11, 12);
            currentManaCount -= 1;
        }

        minecraft.getProfiler().pop();
    }
}
