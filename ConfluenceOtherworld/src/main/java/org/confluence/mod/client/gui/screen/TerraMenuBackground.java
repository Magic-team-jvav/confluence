package org.confluence.mod.client.gui.screen;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import org.confluence.mod.client.gui.BackgroundLayer;

/// 泰拉风格菜单背景：直接用成就界面的 {@link BackgroundLayer}（天空、星星、可拖动的日月、云、四层风景），
/// 主菜单和其他菜单共用一份状态，在菜单之间切换时不重置（时间和云接着走）。
/// 世界外没有客户端刻，这里按真实时间补上图层的 tick（每 50 毫秒一次）。
public final class TerraMenuBackground {
    private static int lastWidth = -1;
    private static int lastHeight = -1;
    private static long lastTick;

    private TerraMenuBackground() {}

    public static void render(GuiGraphics guiGraphics, float partialTick) {
        int width = guiGraphics.guiWidth();
        int height = guiGraphics.guiHeight();
        long now = Util.getMillis();
        if (!BackgroundLayer.isEnabled() || width != lastWidth || height != lastHeight) {
            BackgroundLayer.initLayers(width, height);
            lastWidth = width;
            lastHeight = height;
            lastTick = now;
        }
        int ticks = 0;
        while (now - lastTick >= 50L && ticks++ < 10) {
            BackgroundLayer.tickLayers();
            lastTick += 50L;
        }
        if (now - lastTick > 500L) lastTick = now; // 卡顿很久时不补太多
        BackgroundLayer.renderLayers(guiGraphics, partialTick);
    }

    public static boolean clicked(double mouseX, double mouseY) {
        return BackgroundLayer.clickedLayers(mouseX, mouseY);
    }

    public static boolean dragged(double mouseX, double mouseY, double dragX, double dragY) {
        return BackgroundLayer.dragLayers(mouseX, mouseY, dragX, dragY);
    }

    public static boolean released(double mouseX, double mouseY) {
        return BackgroundLayer.releasedLayers(mouseX, mouseY);
    }
}
