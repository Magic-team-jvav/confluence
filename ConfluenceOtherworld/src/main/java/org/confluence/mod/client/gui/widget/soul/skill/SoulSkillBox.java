package org.confluence.mod.client.gui.widget.soul.skill;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.soul.skill.SoulSkillStack;
import org.confluence.mod.util.PlayerUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SoulSkillBox extends AbstractWidget {
    public static final ResourceLocation BOX_GRAY = Confluence.asResource("hud/soul_quick_skill_hud/box_gray");
    public static final ResourceLocation BOX = Confluence.asResource("container/soul_overview/box");
    public static final ResourceLocation BOX_ACTIVATE = Confluence.asResource("container/soul_overview/box_activate");
    public static final ResourceLocation BOX_ACTIVATE_FLAME = Confluence.asResource("container/soul_overview/box_activate_flame");
    public static final ResourceLocation BOX_SELECT = Confluence.asResource("container/soul_overview/box_select");
    public static final int SKILL_SIZE = 16;
    public static final int BOX_SIZE = 32;
    public static final int BOX_GRAY_SIZE = 22;
    private static final int UNAVAILABLE_SKILL_OVERLAY_COLOR = 0x80000000;
    private static final int COOLDOWN_OVERLAY_COLOR = 0x7FFFFFFF;
    private static final int COOLDOWN_TEXT_COLOR = 0xFFFFFFFF;
    private static final String STATE_LEARNED_TRANSLATION_KEY = "confluence.soul_skill.state.learned";
    private static final String STATE_TEMPORARY_TRANSLATION_KEY = "confluence.soul_skill.state.temporary";
    private static final String STATE_LOCKED_TRANSLATION_KEY = "confluence.soul_skill.state.locked";
    private static final String STATE_EQUIPPED_TRANSLATION_KEY = "confluence.soul_skill.state.equipped";
    private static final String STATE_UNAVAILABLE_TRANSLATION_KEY = "confluence.soul_skill.state.unavailable";

    protected final Minecraft instance;
    protected final Font font;
    @Nullable
    protected SoulSkillStack soulSkillStack;
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

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderWidget(guiGraphics, getX(), getY(), mouseX, mouseY, partialTick);
    }

    public void renderWidget(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, float partialTick) {
        renderWidget(guiGraphics, x, y);
        renderTooltip(guiGraphics, mouseX, mouseY, partialTick);
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
        if (isSkill && !SoulSkillStack.isEmpty(soulSkillStack)) {
            renderSkillIcon(guiGraphics, soulSkillStack, getCooldownSkillStack(), x + 8, y + 10 - 2);
        }
        if (isBox) {
            if (isFlame) {
                renderBoxActivateFlame(guiGraphics, x, y);
            }
        }
    }

    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (SoulSkillStack.isEmpty(soulSkillStack) || !isTooltip || !isSkill) {
            return;
        }
        renderTooltip(font, guiGraphics, soulSkillStack, mouseX, mouseY, partialTick);
    }

    public static void renderTooltip(Font font, GuiGraphics guiGraphics, SoulSkillStack skillStack, int mouseX, int mouseY, float partialTick) {
        if (SoulSkillStack.isEmpty(skillStack)) {
            return;
        }
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(0, 0, 1000);
        guiGraphics.renderTooltip(font, getSkillTooltipLines(skillStack, skillStack.getSkillDescriptionLines()), Optional.empty(), mouseX, mouseY);
        poseStack.popPose();
    }

    public static List<Component> getSkillTooltipLines(SoulSkillStack skillStack, List<Component> tooltipLines) {
        List<Component> lines = new ArrayList<>(tooltipLines);
        Player player = Minecraft.getInstance().player;
        ResourceLocation skillId = skillStack.getSoulSkill().getId();
        boolean available = PlayerUtils.isSoulSkillAvailable(player, skillId);
        String stateKey;
        if (PlayerUtils.isSoulSkillLearned(player, skillId)) {
            stateKey = STATE_LEARNED_TRANSLATION_KEY;
        } else if (available) {
            stateKey = STATE_TEMPORARY_TRANSLATION_KEY;
        } else {
            stateKey = STATE_LOCKED_TRANSLATION_KEY;
        }
        lines.add(Component.translatable(stateKey).withStyle(ChatFormatting.GRAY));
        if (PlayerUtils.isSkillEquipped(player, skillId)) {
            lines.add(Component.translatable(STATE_EQUIPPED_TRANSLATION_KEY).withStyle(ChatFormatting.GRAY));
            if (!available) {
                lines.add(Component.translatable(STATE_UNAVAILABLE_TRANSLATION_KEY).withStyle(ChatFormatting.GRAY));
            }
        }
        return lines;
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
        renderSkillIcon(guiGraphics, skillStack, skillStack, x, y);
    }

    private static void renderSkillIcon(GuiGraphics guiGraphics, SoulSkillStack skillStack, @Nullable SoulSkillStack cooldownStack, int x, int y) {
        if (SoulSkillStack.isEmpty(skillStack)) {
            return;
        }
        guiGraphics.blitSprite(skillStack.getSoulSkill().getIcon(), x, y, SKILL_SIZE, SKILL_SIZE);
        if (!PlayerUtils.isSoulSkillAvailable(Minecraft.getInstance().player, skillStack.getSoulSkill().getId())) {
            guiGraphics.fill(x, y, x + SKILL_SIZE, y + SKILL_SIZE, UNAVAILABLE_SKILL_OVERLAY_COLOR);
        }
        if (cooldownStack != null && cooldownStack.getCd() > 0) {
            float cooldownPercent = cooldownStack.getMaxCd() > 0 ? Mth.clamp((float) cooldownStack.getCd() / cooldownStack.getMaxCd(), 0.0F, 1.0F) : 1.0F;
            int top = y + Mth.floor(SKILL_SIZE * (1.0F - cooldownPercent));
            int bottom = top + Mth.ceil(SKILL_SIZE * cooldownPercent);
            guiGraphics.fill(RenderType.guiOverlay(), x, top, x + SKILL_SIZE, bottom, COOLDOWN_OVERLAY_COLOR);
            long seconds = ((long) cooldownStack.getCd() + SharedConstants.TICKS_PER_SECOND - 1) / SharedConstants.TICKS_PER_SECOND;
            Font font = Minecraft.getInstance().font;
            guiGraphics.drawCenteredString(font, Long.toString(seconds), x + SKILL_SIZE / 2, y + (SKILL_SIZE - font.lineHeight) / 2, COOLDOWN_TEXT_COLOR);
        }
    }

    @Nullable
    protected SoulSkillStack getCooldownSkillStack() {
        return soulSkillStack;
    }

    public static void renderBoxGray(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blitSprite(SoulSkillBox.BOX_GRAY, x, y, BOX_GRAY_SIZE, BOX_GRAY_SIZE);
    }

    public static void drawSkillStackName(GuiGraphics guiGraphics, Font font, int x, int y, SoulSkillStack currentSkillStack, boolean isCenter) {
        if (SoulSkillStack.isEmpty(currentSkillStack)) {
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

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public void setSkill(@Nullable SoulSkillStack skillStack) {
        this.soulSkillStack = skillStack;
    }

    @Nullable
    public SoulSkillStack getSkillStack() {
        return soulSkillStack;
    }
}
