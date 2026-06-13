package org.confluence.mod.client.gui.widget.soul_skill.soul_overview;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import org.confluence.mod.client.gui.container.SoulOverviewScreen;
import org.confluence.mod.client.gui.widget.soul_skill.SoulSkillBox;
import org.confluence.mod.client.handler.SoulSkillClientHolder;
import org.confluence.mod.common.soulskill.SoulSkill;
import org.confluence.mod.common.soulskill.SoulSkillStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 技能树中的一个节点，中心坐标相对于屏幕中心
 */
public class OverviewNode extends SoulSkillBox {
    protected static final SoulSkillClientHolder HOLDER = SoulSkillClientHolder.INSTANCE;

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

    /**
     * 屏幕中心相对坐标系中的中心点，不含 scroll 偏移
     */
    public Vec2 getCenterPos() {
        return new Vec2(getOrigX() + (float) getWidth() / 2, getOrigY() + (float) getHeight() / 2);
    }

    public ResourceLocation skillId() {return getSkill().id();}

    public SoulSkill getSkill() {return soulSkillStack.getSoulSkill();}

    public SoulSkillStack getSoulSkillStack() {return soulSkillStack;}

    public List<ResourceLocation> getConnections() {return connections;}

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        boolean isSelected = screen.theCurrentlySelectedSkill == getSkill();
        boolean isEquipped = HOLDER.isSkillEquipped(getSkill());
        isSelect = isSelected || isHovered();
        isActivate = isEquipped;
        isFlame = isEquipped;
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (SoulSkillStack.isEmpty(soulSkillStack) || !isTooltip || !isSkill || !isHovered) {
            return;
        }
        if (tooltipLines.isEmpty()) {
            super.renderTooltip(guiGraphics, mouseX, mouseY, partialTick);
            return;
        }
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(0, 0, 1000);
        guiGraphics.renderTooltip(font, tooltipLines, Optional.empty(), mouseX, mouseY);
        poseStack.popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (active && visible) {
            if (isValidClickButton(button)) {
                boolean flag = clicked(mouseX, mouseY);
                if (flag) {
                    playDownSound(Minecraft.getInstance().getSoundManager());
                    onClick(mouseX, mouseY, button);
                    return true;
                }
            }

        }
        return false;
    }

    public void onClick(double mouseX, double mouseY, int button) {
        if (button == 0) {
            boolean alreadySelected = screen.theCurrentlySelectedSkill == getSkill();
            screen.theCurrentlySelectedSkill = alreadySelected ? null : getSkill();
        } else {
            for (int skillIndex : HOLDER.getSkillIndexes(getSkill())) {
                HOLDER.unequipSkill(skillIndex);
            }
        }
    }

    @Override
    protected boolean isValidClickButton(int button) {
        return button == 0 || button == 1;
    }

    @Override
    public void setX(int x) {
        super.setX(x);
    }

    @Override
    public void setY(int y) {
        super.setY(y);
    }

    public ResourceLocation getNodeId() {return nodeId;}

    public boolean isOnScreen(int screenWidth, int screenHeight) {
        return getX() + width > 0 && getX() < screenWidth && getY() + height > 0 && getY() < screenHeight;
    }

    public int getOrigX() {
        return origX;
    }

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

        public Builder pos(int x, int y) {
            this.x = x - SoulSkillBox.BOX_SIZE / 2;
            this.y = y - SoulSkillBox.BOX_SIZE / 2;
            return this;
        }

        public Builder connections(ResourceLocation... nodeIds) {
            connections.addAll(Arrays.asList(nodeIds));
            return this;
        }

        public Builder connections(SoulSkill... soulSkill) {
            return connections(Arrays.stream(soulSkill)
                    .map(SoulSkill::id)
                    .toArray(ResourceLocation[]::new));
        }

        @SafeVarargs
        public final Builder connections(Supplier<SoulSkill>... soulSkill) {
            return connections(Arrays.stream(soulSkill)
                    .map(Supplier::get)
                    .toArray(SoulSkill[]::new));
        }

        public Builder clearConnections() {
            connections.clear();
            return this;
        }

        public Builder addTooltip(Component component) {
            tooltipLines.add(component);
            return this;
        }

        public Builder clearTooltip() {
            tooltipLines.clear();
            return this;
        }

        public OverviewNode build(SoulOverviewScreen screen) {
            return new OverviewNode(screen, this);
        }

        public static Builder of(ResourceLocation nodeId, SoulSkill soulSkill) {
            return new Builder(nodeId, soulSkill);
        }

        public static Builder of(SoulSkill soulSkill) {
            return of(soulSkill.id(), soulSkill);
        }

        public static Builder of(ResourceLocation nodeId, Supplier<SoulSkill> supplier) {
            return of(nodeId, supplier.get());
        }

        public static Builder of(Supplier<SoulSkill> supplier) {
            return of(supplier.get());
        }
    }
}
