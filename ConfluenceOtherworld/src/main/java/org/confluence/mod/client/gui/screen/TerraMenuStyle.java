package org.confluence.mod.client.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import org.confluence.mod.client.ClientConfigs;

/// 泰拉界面模式下所有菜单的统一外观：主菜单那样的风景背景（不模糊）、半透明深蓝面板、
/// 按钮是蓝色面板（悬停变亮、金色描边、文字变黄）。点了主菜单的“经典菜单”后本次游戏恢复原版外观。
public final class TerraMenuStyle {
    public static final int TEXT = 0xFFFFFF;
    public static final int TEXT_HOVER = 0xFFE34A;
    public static final int TEXT_DISABLED = 0x9098A8;

    private TerraMenuStyle() {}

    public static boolean active() {
        return ClientConfigs.terrariaInterface && !TerraTitleScreen.useVanilla();
    }

    /// 按钮/滑条的底板
    public static void panel(GuiGraphics guiGraphics, int x, int y, int width, int height, boolean hovered, boolean enabled) {
        int background = !enabled ? 0xA0262A3A : hovered ? 0xE04862BE : 0xD02C3F88;
        int border = enabled && hovered ? 0xFFF0D060 : 0xFF0C142C;
        guiGraphics.fill(x, y, x + width, y + height, background);
        guiGraphics.fill(x + 1, y + 1, x + width - 1, y + 2, enabled ? 0x50FFFFFF : 0x20FFFFFF); // 上沿高光
        guiGraphics.renderOutline(x, y, width, height, border);
    }

    /// 列表、菜单底板（大面积的半透明深蓝）
    public static void listPanel(GuiGraphics guiGraphics, int x, int y, int right, int bottom) {
        guiGraphics.fill(x, y, right, bottom, 0x90101A3C);
    }

    public static int textColor(boolean hovered, boolean enabled) {
        return !enabled ? TEXT_DISABLED : hovered ? TEXT_HOVER : TEXT;
    }
}
