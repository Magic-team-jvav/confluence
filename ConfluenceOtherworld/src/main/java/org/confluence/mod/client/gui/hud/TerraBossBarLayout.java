package org.confluence.mod.client.gui.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import org.confluence.mod.client.ClientConfigs;

/// 泰拉界面：Boss 血条画在屏幕底部中间（泰拉 1.4 的位置），而不是原版的顶部。
/// 原版从 y=12 往下排，每条的高度由 BossEventProgress 事件给出（自定义样式会改它）；
/// 这里记下上一帧排到的最低处，整层往下平移，让最后一条贴着屏幕底边。
public final class TerraBossBarLayout {
    /// 原版第一条血条的 y
    private static final int VANILLA_TOP = 12;
    /// 原版一条的高度（名字 + 血条）
    private static final int VANILLA_INCREMENT = 19;
    private static final int BOTTOM_MARGIN = 4;
    private static int frameBottom;
    private static int lastBottom = VANILLA_TOP + VANILLA_INCREMENT;
    private static boolean shifted;

    private TerraBossBarLayout() {}

    public static boolean active() {
        if (!ClientConfigs.terrariaInterface) return false;
        LocalPlayer player = Minecraft.getInstance().player;
        return player != null && !player.isSpectator(); // 旁观模式是原版快捷栏布局，Boss 血条留在顶部
    }

    public static void pre(GuiGraphics guiGraphics) {
        frameBottom = 0;
        shifted = active();
        if (!shifted) return;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, guiGraphics.guiHeight() - BOTTOM_MARGIN - lastBottom, 0.0F);
    }

    /// 这一帧 Boss 血条区域的上边（没有 Boss 血条时是屏幕底边）；动作栏文字放在它上面
    public static int barsTop(int guiHeight) {
        if (!active() || frameBottom <= 0) return guiHeight;
        return guiHeight - BOTTOM_MARGIN - lastBottom + VANILLA_TOP - 9;
    }

    public static void onProgress(CustomizeGuiOverlayEvent.BossEventProgress event) {
        frameBottom = Math.max(frameBottom, event.getY() + event.getIncrement());
    }

    public static void post(GuiGraphics guiGraphics) {
        if (frameBottom > 0) lastBottom = frameBottom;
        if (!shifted) return;
        shifted = false;
        guiGraphics.pose().popPose();
    }
}
