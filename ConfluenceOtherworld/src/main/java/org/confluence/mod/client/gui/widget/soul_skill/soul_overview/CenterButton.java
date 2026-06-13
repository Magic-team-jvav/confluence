package org.confluence.mod.client.gui.widget.soul_skill.soul_overview;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.confluence.mod.client.gui.container.SoulOverviewScreen;
import org.jetbrains.annotations.NotNull;

/// 回到原点按钮 — 将视口平滑滚回屏幕中心
public class CenterButton extends OverviewNavButton {
    private static final int SIZE = 20;
    private static final int BG_COLOR = 0x40FFFFFF;
    private static final int HOVER_BG_COLOR = 0x80FFFFFF;
    private static final int LABEL_COLOR = 0xFFFFFFFF;

    public CenterButton(SoulOverviewScreen screen, int x, int y) {
        super(screen, x, y, SIZE, SIZE, Component.empty());
    }

    @Override
    protected Target navTarget() {
        return new Target(0, 0);
    }

    @Override
    protected void renderButton(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        renderBg(guiGraphics, x, y, BG_COLOR, HOVER_BG_COLOR, mouseX, mouseY);
        renderLabel(guiGraphics, x, y, "C", LABEL_COLOR);
    }

    @Override
    protected @NotNull MutableComponent getComponent() {
        return Component.translatable("confluence.screen.soul_overview.center");
    }
}
