package org.confluence.mod.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.soulskill.SoulSkillStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SoulSkillBox extends AbstractWidget {
    public static final ResourceLocation BOX_GRAY = Confluence.asResource("hud/soul_quick_skill_hud/box_gray");
    public static final ResourceLocation BOX = Confluence.asResource("hud/soul_quick_skill_hud/box");
    public static final ResourceLocation BOX_ACTIVATE = Confluence.asResource("hud/soul_quick_skill_hud/box_activate");
    public static final ResourceLocation BOX_ACTIVATE_FLAME = Confluence.asResource("hud/soul_quick_skill_hud/box_activate_flame");
    public static final ResourceLocation BOX_SELECT = Confluence.asResource("hud/soul_quick_skill_hud/box_select");
    public static final List<FormattedCharSequence> DEFAULT_CHAR_SEQUENCE;
    public static final int SKILL_SIZE = 16;
    public static final int BOX_SIZE = 32;
    public static final int BOX_GRAY_SIZE = 22;

    protected final Minecraft instance;
    protected final Font font;
    @Nullable
    private SoulSkillStack skillStack;
    public boolean isBox = true;
    public boolean isSkill = true;
    public boolean isActivate;
    public boolean isSelect;
    public boolean isFlame;

    static {
        DEFAULT_CHAR_SEQUENCE = SoulSkillStack.getSkillTooltipLines(SoulSkillStack.EMPTY);
    }

    public SoulSkillBox() {
        this(0, 0);
    }

    public SoulSkillBox(SoulSkillStack skillStack) {
        this(0, 0);
        setSkill(skillStack);
    }

    public SoulSkillBox(int x, int y) {
        this(x, y, BOX_SIZE, BOX_SIZE, Component.empty());
    }

    public SoulSkillBox(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        instance = Minecraft.getInstance();
        font = instance.font;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderWidget(guiGraphics, getX(), getY(), mouseX, mouseY, partialTick);
    }

    public void renderWidget(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, float partialTick) {
        renderWidget(guiGraphics, x, y);
    }

    public void renderWidget(GuiGraphics guiGraphics, int x, int y) {
        if (isBox) {
            renderBox(guiGraphics, x, y);
            if (isActivate) {
                renderBoxActivate(guiGraphics, x, y);
            }
            if (isSelect) {
                renderBoxSelect(guiGraphics, x, y);
            }
        }
        if (isSkill && skillStack != null) {
            renderSkillIcon(guiGraphics, skillStack, x + 8, y + 10 - 2);
        }
        if (isBox) {
            if (isFlame) {
                renderBoxActivateFlame(guiGraphics, x, y);
            }
        }
    }

    public static void renderBox(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blitSprite(BOX, x, y - 2, BOX_SIZE, BOX_SIZE);
    }

    public static void renderBoxActivate(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blitSprite(BOX_ACTIVATE, x, y - 2, BOX_SIZE, BOX_SIZE);
    }

    public static void renderBoxSelect(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blitSprite(BOX_SELECT, x, y - 2, BOX_SIZE, BOX_SIZE);
    }

    public static void renderBoxActivateFlame(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blitSprite(BOX_ACTIVATE_FLAME, x, y - 2, BOX_SIZE, BOX_SIZE);
    }

    public static void renderSkillIcon(GuiGraphics guiGraphics, SoulSkillStack skillStack, int x, int y) {
        // TODO 需要添加CD渲染
        guiGraphics.blitSprite(skillStack.getSoulSkill().getIcon(), x, y, SKILL_SIZE, SKILL_SIZE);
    }

    public static void renderBoxGray(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blitSprite(SoulSkillBox.BOX_GRAY, x, y, BOX_GRAY_SIZE, BOX_GRAY_SIZE);
    }

    public static void drawSkillStackName(GuiGraphics guiGraphics, Font font, int x, int y, SoulSkillStack currentSkillStack, boolean isCenter) {
        Component nameComponent = currentSkillStack.getNameComponent();
        int x1 = x;
        int y1 = y - font.lineHeight / 2;
        if (isCenter) {
            x1 -= font.width(nameComponent) / 2;
        }
        guiGraphics.drawString(font, nameComponent, x1, y1, -1);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public void setSkill(@Nullable SoulSkillStack skillStack) {
        this.skillStack = skillStack;
        if (skillStack != null) {
            setTooltip(Tooltip.create(skillStack.getNameComponent(), skillStack.getNarrationComponent()));
        } else {
            setTooltip(null);
        }
    }

    @Nullable
    public SoulSkillStack getSkillStack() {
        return skillStack;
    }

    @Override
    public @Nullable Tooltip getTooltip() {
        return super.getTooltip();
    }
}
