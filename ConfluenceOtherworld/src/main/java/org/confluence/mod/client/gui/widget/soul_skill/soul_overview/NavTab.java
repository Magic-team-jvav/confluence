package org.confluence.mod.client.gui.widget.soul_skill.soul_overview;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.confluence.mod.client.gui.container.SoulOverviewScreen;
import org.confluence.mod.common.init.ModSoulSkills;
import org.confluence.mod.common.init.ModSoulSkills.SkillCategory;
import org.confluence.mod.common.soulskill.SoulSkill;
import org.jetbrains.annotations.NotNull;

/**
 * 侧边栏分类导航标签 — 点击后平滑跳转到对应分类的第一个技能
 */
public class NavTab extends OverviewNavButton {
    private static final int SIZE = 20;

    private final SkillCategory category;
    private final int color;

    /**
     * 通过已注册的分类信息构建
     */
    public NavTab(SoulOverviewScreen screen, SkillCategory category, int x, int y) {
        super(screen, x, y, SIZE, SIZE, Component.empty());
        this.category = category;
        this.color = category.color;
        setTooltip(Tooltip.create(Component.empty()));
    }

    @Override
    protected Target navTarget() {
        SoulSkill first = ModSoulSkills.getFirstInCategory(category);
        if (first == null) return null;
        OverviewNode node = screen.nodeById.get(first.getId());
        if (node == null) return null;
        return new Target(-node.getOrigX() - node.getWidth() / 2f, -node.getOrigY() - node.getHeight() / 2f);
    }

    @Override
    protected void renderButton(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        renderBg(guiGraphics, x, y, color & 0x40FFFFFF, color | 0x80000000, mouseX, mouseY);
        renderLabel(guiGraphics, x, y, category.label, 0xFFFFFFFF);
    }

    @Override
    protected @NotNull MutableComponent getComponent() {
        return Component.translatable(category.translationKey);
    }
}
