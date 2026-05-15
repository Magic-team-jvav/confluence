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
    public static final ResourceLocation BOX = Confluence.asResource("hud/soul_quick_skill_hud/box");
    public static final ResourceLocation BOX_ACTIVATE = Confluence.asResource("hud/soul_quick_skill_hud/box_activate");
    public static final ResourceLocation BOX_ACTIVATE_FLAME = Confluence.asResource("hud/soul_quick_skill_hud/box_activate_flame");
    public static final ResourceLocation BOX_SELECT = Confluence.asResource("hud/soul_quick_skill_hud/box_select");
    public static final List<FormattedCharSequence> DEFAULT_CHAR_SEQUENCE;

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
        this(x, y, 32, 32, Component.empty());
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
        if (isBox) {
            guiGraphics.blitSprite(BOX, x, y - 2, 32, 32);
            if (isActivate) {
                guiGraphics.blitSprite(BOX_ACTIVATE, x, y - 2, 32, 32);
            }
            if (isSelect) {
                guiGraphics.blitSprite(BOX_SELECT, x, y - 2, 32, 32);
            }
        }
        if (isSkill && skillStack != null) {
            guiGraphics.blitSprite(skillStack.getSoulSkill().getIcon(), x + 8, y + 10 - 2, 16, 16);
        }
        if (isBox) {
            if (isFlame) {
                guiGraphics.blitSprite(BOX_ACTIVATE_FLAME, x, y - 2, 32, 32);
            }
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public void setSkill(@Nullable SoulSkillStack skillStack) {
        this.skillStack = skillStack;
        if (skillStack != null) {
            setTooltip(Tooltip.create(SoulSkillStack.getMessage(skillStack), skillStack.getComponent()));
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
