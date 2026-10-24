package org.confluence.mod.client.gui.widget.soulcerer.skill;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.common.soulcerer.skill.SoulSkillStack;
import org.confluence.mod.util.PlayerUtils;

import java.util.ArrayList;
import java.util.List;

public class SoulSkillBox extends AbstractWidget {
    public static final ResourceLocation BOX_GRAY = Confluence.asResource("hud/soulcerer_quick_skill_hud/box_gray");
    public static final ResourceLocation BOX = Confluence.asResource("container/soulcerer_overview/box");
    public static final ResourceLocation BOX_ACTIVATE = Confluence.asResource("container/soulcerer_overview/box_activate");
    public static final ResourceLocation BOX_ACTIVATE_FLAME = Confluence.asResource("container/soulcerer_overview/box_activate_flame");
    public static final ResourceLocation BOX_SELECT = Confluence.asResource("container/soulcerer_overview/box_select");
    public static final int SKILL_SIZE = 16;
    public static final int BOX_SIZE = 32;
    public static final int BOX_GRAY_SIZE = 22;
    private static final int BOX_ACTIVATE_TEXTURE_HEIGHT = 128;
    private static final int BOX_FLAME_TEXTURE_HEIGHT = 256;
    private static final int COOLDOWN_OVERLAY_COLOR = Integer.MAX_VALUE;
    private static final int UNAVAILABLE_OVERLAY_COLOR = 0x99000000;
    private static final String STATE_LEARNED = "confluence.soulcerer_skill.state.learned";
    private static final String STATE_TEMPORARY = "confluence.soulcerer_skill.state.temporary";
    private static final String STATE_LOCKED = "confluence.soulcerer_skill.state.locked";
    private static final String STATE_EQUIPPED = "confluence.soulcerer_skill.state.equipped";
    private static final String STATE_UNAVAILABLE = "confluence.soulcerer_skill.state.unavailable";

    protected final Minecraft instance;
    protected final Font font;
    protected SoulSkillStack soulSkillStack = SoulSkillStack.Empty;
    public boolean isBox = true;
    public boolean isSkill = true;
    public boolean isActivate;
    public boolean isSelect;
    public boolean isFlame;
    public boolean isTooltip;

    public SoulSkillBox() {
        this(0, 0);
    }

    public SoulSkillBox(SoulSkillStack soulSkillStack) {
        this(0, 0);
        setSkill(soulSkillStack);
    }

    public SoulSkillBox(int x, int y) {
        this(x, y, BOX_SIZE, BOX_SIZE, Component.empty());
    }

    public SoulSkillBox(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        instance = Minecraft.getInstance();
        font = instance.font;
    }

    /// 按控件位置绘制技能框及提示。
    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderWidget(guiGraphics, getX(), getY(), mouseX, mouseY, partialTick);
    }

    /// 在指定位置绘制技能框并处理提示。
    public void renderWidget(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, float partialTick) {
        renderWidget(guiGraphics, x, y);
        renderTooltip(guiGraphics, mouseX, mouseY, partialTick);
    }

    /// 绘制技能框、技能图标和激活状态。
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
        if (isSkill && !soulSkillStack.isEmpty()) {
            renderSkillIcon(guiGraphics, soulSkillStack, x + 8, y + 10 - 2);
        }
        if (isBox) {
            if (isFlame) {
                renderBoxActivateFlame(guiGraphics, x, y);
            }
        }
    }

    /// 在允许显示时绘制当前技能提示。
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (soulSkillStack.isEmpty() || !isTooltip || !isSkill) {
            return;
        }
        renderTooltip(font, guiGraphics, soulSkillStack, mouseX, mouseY, partialTick);
    }

    /// 在顶层绘制非空技能的提示文本。
    ///
    /// 行序固定：名称 → 伤害 / 冷却 / BUFF持续时间 / 灵魂消耗 → 状态 → 技能描述。
    public static void renderTooltip(Font font, GuiGraphics guiGraphics, SoulSkillStack skillStack, int mouseX, int mouseY, float partialTick) {
        if (skillStack.isEmpty()) {
            return;
        }
        List<Component> components = new ArrayList<>(skillStack.getSkillHeaderLines(Minecraft.getInstance().player, ClientConfigs.weaponTooltipBaseStats));
        components.addAll(getSkillStateComponents(skillStack));
        Component description = skillStack.getSkillDescriptionLine();
        if (description != null) {
            components.add(description);
        }
        List<FormattedCharSequence> lines = components.stream().map(Component::getVisualOrderText).toList();
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(0, 0, 1000);
        guiGraphics.renderTooltip(font, lines, mouseX, mouseY);
        poseStack.popPose();
    }

    /// 绘制普通技能框。
    public static void renderBox(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blitSprite(BOX, x, y - 2, BOX_SIZE, BOX_SIZE);
    }

    /// 绘制技能激活边框。
    public static void renderBoxActivate(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blitSprite(BOX_ACTIVATE, BOX_SIZE, BOX_ACTIVATE_TEXTURE_HEIGHT, 0, 0, x, y - 2, BOX_SIZE, BOX_SIZE);
    }

    /// 绘制技能选中边框。
    public static void renderBoxSelect(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blitSprite(BOX_SELECT, x, y - 2, BOX_SIZE, BOX_SIZE);
    }

    /// 绘制技能激活时的火焰边框。
    public static void renderBoxActivateFlame(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blitSprite(BOX_ACTIVATE_FLAME, BOX_SIZE, BOX_FLAME_TEXTURE_HEIGHT, 0, 0, x, y - 2, BOX_SIZE, BOX_SIZE);
    }

    /// 绘制非空技能的图标。
    public static void renderSkillIcon(GuiGraphics guiGraphics, SoulSkillStack skillStack, int x, int y) {
        if (skillStack.isEmpty()) {
            return;
        }
        guiGraphics.blitSprite(skillStack.getSoulSkill().getIcon(), x, y, SKILL_SIZE, SKILL_SIZE);
        if (!PlayerUtils.isSoulSkillAvailable(Minecraft.getInstance().player, skillStack.getSoulSkill().getId())) {
            guiGraphics.fill(RenderType.guiOverlay(), x, y, x + SKILL_SIZE, y + SKILL_SIZE, UNAVAILABLE_OVERLAY_COLOR);
        }
        renderSkillCooldown(guiGraphics, skillStack.getCd(), skillStack.getMaxCd(), x, y);
    }

    /// 按剩余冷却绘制物品式遮罩和向上取整的秒数。
    public static void renderSkillCooldown(GuiGraphics guiGraphics, int cooldown, int maxCooldown, int x, int y) {
        if (cooldown <= 0 || maxCooldown <= 0) {
            return;
        }
        float percent = Mth.clamp((float) cooldown / maxCooldown, 0.0F, 1.0F);
        int top = y + Mth.floor(SKILL_SIZE * (1.0F - percent));
        int bottom = top + Mth.ceil(SKILL_SIZE * percent);
        guiGraphics.fill(RenderType.guiOverlay(), x, top, x + SKILL_SIZE, bottom, COOLDOWN_OVERLAY_COLOR);
        String seconds = Integer.toString(Mth.ceil((double) cooldown / SharedConstants.TICKS_PER_SECOND));
        Font font = Minecraft.getInstance().font;
        guiGraphics.drawString(font, seconds, x + (SKILL_SIZE - font.width(seconds)) / 2, y + (SKILL_SIZE - font.lineHeight) / 2, -1, true);
    }

    /// 分别提供学习、当前可用与已装备状态的提示。
    public static List<Component> getSkillStateComponents(SoulSkillStack skillStack) {
        ResourceLocation id = skillStack.getSoulSkill().getId();
        boolean learned = PlayerUtils.isSoulSkillLearned(Minecraft.getInstance().player, id);
        boolean available = PlayerUtils.isSoulSkillAvailable(Minecraft.getInstance().player, id);
        List<Component> lines = new ArrayList<>();
        lines.add(Component.translatable(learned ? STATE_LEARNED : available ? STATE_TEMPORARY : STATE_LOCKED));
        if (PlayerUtils.isSoulSkillEquipped(Minecraft.getInstance().player, id)) {
            lines.add(Component.translatable(STATE_EQUIPPED));
            if (!available) {
                lines.add(Component.translatable(STATE_UNAVAILABLE));
            }
        }
        return lines;
    }

    /// 绘制灰色技能框。
    public static void renderBoxGray(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blitSprite(SoulSkillBox.BOX_GRAY, x, y, BOX_GRAY_SIZE, BOX_GRAY_SIZE);
    }

    /// 绘制技能名称并按需居中。
    public static void drawSkillStackName(GuiGraphics guiGraphics, Font font, int x, int y, SoulSkillStack currentSkillStack, boolean isCenter) {
        if (currentSkillStack.isEmpty()) {
            return;
        }
        Component nameComponent = currentSkillStack.getNameComponent();
        int x1 = x;
        int y1 = y - font.lineHeight / 2;
        if (isCenter) {
            x1 -= font.width(nameComponent) / 2;
        }
        guiGraphics.drawString(font, nameComponent, x1, y1, -1);
    }

    /// 不追加技能框朗读内容。
    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    /// 设置技能框显示的技能堆栈。
    public void setSkill(SoulSkillStack skillStack) {
        this.soulSkillStack = skillStack;
    }

    /// 获取技能框当前的技能堆栈。
    public SoulSkillStack getSkillStack() {
        return soulSkillStack;
    }
}
