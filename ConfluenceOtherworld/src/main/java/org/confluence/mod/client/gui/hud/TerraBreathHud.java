package org.confluence.mod.client.gui.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import org.confluence.lib.util.LibRenderUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.ClientConfigs;

/// 泰拉风格的呼吸条：泰拉在角色头顶显示一排气泡。第一人称下“头顶”就是准星上方，
/// 所以在屏幕中央、准星上方画 10 个气泡；不满的气泡像泰拉的心一样缩小，耗尽的气泡消失。
public class TerraBreathHud implements LayeredDraw.Layer {
    /// 自绘的泰拉风格气泡（11×11，非泰拉原素材）
    private static final ResourceLocation AIR = Confluence.asResource("hud/terra_bubble");
    private static final ResourceLocation AIR_BURSTING = Confluence.asResource("hud/terra_bubble_burst");
    private static final int BUBBLES = 10;
    private static final int SIZE = 11;
    private static final int SPACING = 10;
    /// 气泡行底边到准星的距离
    private static final int ABOVE_CROSSHAIR = 22;

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!ClientConfigs.terrariaInterface) return;
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.options.hideGui || !LibRenderUtils.shouldDrawSurvivalElements(minecraft)) return;
        int maxAir = player.getMaxAirSupply();
        if (maxAir <= 0) return;
        int air = Math.clamp(player.getAirSupply(), 0, maxAir);
        if (!player.isEyeInFluid(FluidTags.WATER) && air >= maxAir) return;

        LibRenderUtils.setupOverlayRenderState(true, false);
        int width = SIZE + (BUBBLES - 1) * SPACING;
        int left = (guiGraphics.guiWidth() - width) / 2;
        int y = guiGraphics.guiHeight() / 2 - ABOVE_CROSSHAIR - SIZE;
        float perBubble = maxAir / (float) BUBBLES;
        for (int i = 0; i < BUBBLES; i++) {
            float fill = Math.clamp((air - i * perBubble) / perBubble, 0.0F, 1.0F);
            int x = left + i * SPACING;
            if (fill >= 1.0F) {
                guiGraphics.blitSprite(AIR, x, y, SIZE, SIZE);
            } else if (fill > 0.0F) {
                // 正在消耗的气泡：缩小，快没了时显示破裂
                float scale = 0.4F + 0.6F * fill;
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(x + SIZE * 0.5F * (1.0F - scale), y + SIZE * 0.5F * (1.0F - scale), 0.0F);
                guiGraphics.pose().scale(scale, scale, 1.0F);
                guiGraphics.blitSprite(fill < 0.25F ? AIR_BURSTING : AIR, 0, 0, SIZE, SIZE);
                guiGraphics.pose().popPose();
            }
        }
    }
}
