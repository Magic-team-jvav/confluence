package org.confluence.mod.client.gui.widget.soulcerer.skill.overview;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import org.confluence.mod.client.gui.container.soulcerer.SoulOverviewScreen;
import org.confluence.mod.client.gui.widget.soulcerer.skill.SoulSkillBox;
import org.confluence.mod.client.handler.soulcerer.ClientSoulHandler;
import org.confluence.mod.common.soulcerer.skill.SoulSkill;
import org.confluence.mod.common.soulcerer.skill.SoulSkillStack;
import org.confluence.mod.util.PlayerUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 技能树中的一个节点，中心坐标相对于屏幕中心
 */
public class OverviewNode extends SoulSkillBox {

    private final SoulOverviewScreen screen;
    private final ResourceLocation nodeId;
    private final List<ResourceLocation> connections;
    private final List<Component> tooltipLines;
    private final int origX;
    private final int origY;

    private OverviewNode(SoulOverviewScreen screen, Builder builder) {
        super(builder.x, builder.y);
        this.origX = builder.x;
        this.origY = builder.y;
        this.screen = screen;
        this.nodeId = builder.nodeId;
        this.soulSkillStack = builder.soulSkill.getStack();
        this.connections = List.copyOf(builder.connections);
        this.tooltipLines = List.copyOf(builder.tooltipLines);
        isTooltip = true;
    }

    /** 屏幕中心相对坐标系中的中心点，不含 scroll 偏移 */
    public Vec2 getCenterPos() {
        return new Vec2(getOrigX() + (float) getWidth() / 2, getOrigY() + (float) getHeight() / 2);
    }

    /// 获取节点所表示的技能标识。
    public ResourceLocation skillId() {return getSkill().getId();}

    /// 获取节点对应的技能定义。
    public SoulSkill getSkill() {return soulSkillStack.getSoulSkill();}

    /// 获取节点显示的技能堆栈。
    public SoulSkillStack getSoulSkillStack() {return soulSkillStack;}

    /// 获取节点连接目标的标识列表。
    public List<ResourceLocation> getConnections() {return connections;}

    /// 根据选中和装备状态绘制节点。
    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        boolean isSelected = screen.theCurrentlySelectedSkill == getSkill();
        boolean isEquipped = PlayerUtils.isSoulSkillEquipped(Minecraft.getInstance().player, getSkill());
        isSelect = isSelected || isHovered();
        isActivate = isEquipped;
        isFlame = isEquipped;
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        int iconOffset = (BOX_SIZE - SKILL_SIZE) / 2;
        SoulSkillBox.renderSkillCooldown(guiGraphics,
                PlayerUtils.getSoulSkillCooldown(Minecraft.getInstance().player, skillId()),
                PlayerUtils.getSoulSkillMaxCooldown(Minecraft.getInstance().player, skillId()),
                getX() + iconOffset, getY() + iconOffset);
    }

    /// 优先绘制节点自定义提示文本。
    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (soulSkillStack.isEmpty() || !isTooltip || !isSkill || !isHovered) {
            return;
        }
        if (tooltipLines.isEmpty()) {
            super.renderTooltip(guiGraphics, mouseX, mouseY, partialTick);
            return;
        }
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(0, 0, 1000);
        List<Component> lines = new ArrayList<>(tooltipLines);
        lines.addAll(SoulSkillBox.getSkillStateComponents(soulSkillStack));
        guiGraphics.renderTooltip(font, lines, Optional.empty(), mouseX, mouseY);
        poseStack.popPose();
    }

    /// 左键切换技能选择，右键卸下该技能。
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (active && visible && isValidClickButton(button) && clicked(mouseX, mouseY)) {
            playDownSound(Minecraft.getInstance().getSoundManager());
            onClick(mouseX, mouseY, button);
            return true;
        }
        return false;
    }

    public void onClick(double mouseX, double mouseY, int button) {
        super.onClick(mouseX, mouseY);
        if (button == 0) {
            boolean alreadySelected = screen.theCurrentlySelectedSkill == getSkill();
            screen.theCurrentlySelectedSkill = alreadySelected ? null : getSkill();
        } else {
            for (int skillIndex : PlayerUtils.getSoulSkillIndexes(Minecraft.getInstance().player, getSkill())) {
                ClientSoulHandler.unequipSoulSkill(skillIndex);
            }
        }
    }

    /// 允许鼠标左键和右键操作节点。
    @Override
    protected boolean isValidClickButton(int button) {
        return button == 0 || button == 1;
    }

    /// 设置节点的横坐标。
    @Override
    public void setX(int x) {
        super.setX(x);
    }

    /// 设置节点的纵坐标。
    @Override
    public void setY(int y) {
        super.setY(y);
    }

    /// 获取节点自身的标识。
    public ResourceLocation getNodeId() {return nodeId;}

    /// 判断节点是否与可见界面相交。
    public boolean isOnScreen(int screenWidth, int screenHeight) {
        return getX() + width > 0 && getX() < screenWidth && getY() + height > 0 && getY() < screenHeight;
    }

    /// 获取节点相对界面中心的原始横坐标。
    public int getOrigX() {
        return origX;
    }

    /// 获取节点相对界面中心的原始纵坐标。
    public int getOrigY() {
        return origY;
    }

    public static class Builder {
        final ResourceLocation nodeId;
        final SoulSkill soulSkill;
        int x, y;
        final List<ResourceLocation> connections = new ArrayList<>();
        final List<Component> tooltipLines = new ArrayList<>();

        private Builder(ResourceLocation nodeId, SoulSkill soulSkill) {
            this.nodeId = nodeId;
            this.soulSkill = soulSkill;
        }

        /// 按中心坐标设置节点的原始位置。
        public Builder pos(int x, int y) {
            this.x = x - SoulSkillBox.BOX_SIZE / 2;
            this.y = y - SoulSkillBox.BOX_SIZE / 2;
            return this;
        }

        /// 按节点标识追加连接目标。
        public Builder connections(ResourceLocation... nodeIds) {
            connections.addAll(Arrays.asList(nodeIds));
            return this;
        }

        /// 按技能定义追加连接目标。
        public Builder connections(SoulSkill... soulSkill) {
            return connections(Arrays.stream(soulSkill)
                    .map(SoulSkill::getId)
                    .toArray(ResourceLocation[]::new));
        }

        /// 解析技能提供者并追加连接目标。
        @SafeVarargs
        public final Builder connections(Supplier<SoulSkill>... soulSkill) {
            return connections(Arrays.stream(soulSkill)
                    .map(Supplier::get)
                    .toArray(SoulSkill[]::new));
        }

        /// 清空节点的连接目标。
        public Builder clearConnections() {
            connections.clear();
            return this;
        }

        /// 追加节点的自定义提示文本。
        public Builder addTooltip(Component component) {
            tooltipLines.add(component);
            return this;
        }

        /// 清空节点的自定义提示文本。
        public Builder clearTooltip() {
            tooltipLines.clear();
            return this;
        }

        /// 为指定总览界面构建节点。
        public OverviewNode build(SoulOverviewScreen screen) {
            return new OverviewNode(screen, this);
        }

        /// 使用节点标识和技能定义创建构建器。
        public static Builder of(ResourceLocation nodeId, SoulSkill soulSkill) {
            return new Builder(nodeId, soulSkill);
        }

        /// 以技能标识创建节点构建器。
        public static Builder of(SoulSkill soulSkill) {
            return of(soulSkill.getId(), soulSkill);
        }

        /// 解析技能提供者并使用指定节点标识。
        public static Builder of(ResourceLocation nodeId, Supplier<SoulSkill> supplier) {
            return of(nodeId, supplier.get());
        }

        /// 解析技能提供者创建节点构建器。
        public static Builder of(Supplier<SoulSkill> supplier) {
            return of(supplier.get());
        }
    }
}
