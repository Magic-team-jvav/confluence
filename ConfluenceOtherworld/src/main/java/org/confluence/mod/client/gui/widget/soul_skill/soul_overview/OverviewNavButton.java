package org.confluence.mod.client.gui.widget.soul_skill.soul_overview;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import org.confluence.mod.client.gui.container.SoulOverviewScreen;
import org.confluence.mod.client.handler.SoulSkillClientHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 侧边栏导航按钮基类 — 点击后平滑滚动到目标位置
 */
public abstract class OverviewNavButton extends AbstractWidget {
    protected static final SoulSkillClientHolder HOLDER = SoulSkillClientHolder.INSTANCE;

    protected final SoulOverviewScreen screen;
    protected final Font font;

    public OverviewNavButton(SoulOverviewScreen screen, int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        this.screen = screen;
        this.font = Minecraft.getInstance().font;
    }

    /**
     * 导航目标：相对于屏幕中心的坐标，返回 null 表示无目标
     */
    @Nullable
    protected abstract Target navTarget();

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        super.onClick(mouseX, mouseY, button);
        Target target = navTarget();
        if (target != null) {
            screen.centerToOrigin();
            screen.navigateTo(target);
        }
        uiClickSound();
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderButton(guiGraphics, getX(), getY(), mouseX, mouseY);
        if (isHovered) {
            guiGraphics.renderTooltip(font, getComponent(), mouseX, mouseY);
        }
    }

    protected abstract void renderButton(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY);

    /**
     * 渲染按钮背景填充
     */
    protected void renderBg(GuiGraphics guiGraphics, int x, int y, int bgColor, int hoverBgColor, int mouseX, int mouseY) {
        boolean hovered = isMouseOver(mouseX, mouseY);
        guiGraphics.fill(x, y, x + width, y + height, hovered ? hoverBgColor : bgColor);
    }

    /**
     * 在按钮中心渲染文本
     */
    protected void renderLabel(GuiGraphics guiGraphics, int x, int y, String text, int color) {
        guiGraphics.drawCenteredString(font, text, x + width / 2, y + 5, color);
    }

    protected static void uiClickSound() {
        Minecraft.getInstance().getSoundManager()
                .play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput out) {}

    @NotNull
    protected abstract MutableComponent getComponent();

    /**
     * 导航目标
     *
     * @param scrollX 目标 scrollX（相对于屏幕中心）
     * @param scrollY 目标 scrollY（相对于屏幕中心）
     */
    public record Target(double scrollX, double scrollY) {}
}
