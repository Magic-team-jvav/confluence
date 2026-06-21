package org.confluence.mod.client.gui.widget.soul_skill.soul_overview;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.container.SoulOverviewScreen;
import org.confluence.mod.client.gui.widget.soul_skill.SoulSkillBox;
import org.confluence.mod.client.handler.SoulSkillClientHolder;
import org.confluence.mod.common.soulskill.SoulSkill;
import org.confluence.mod.common.soulskill.SoulSkillStack;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.client.gui.components.PortSprite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/// 底部快捷栏
public class HotbarWidget extends AbstractWidget {
    protected static final SoulSkillClientHolder HOLDER = SoulSkillClientHolder.INSTANCE;

    public static final PortSprite BOTTOM_BOX_CENTRE = new PortSprite(Confluence.asResource("container/soul_overview/hotbar_box_centre"), 20, 32);
    public static final PortSprite BOTTOM_BOX_HEAD = new PortSprite(Confluence.asResource("container/soul_overview/hotbar_box_head"), 16, 32);
    public static final PortSprite BOTTOM_BOX_TAIL = new PortSprite(Confluence.asResource("container/soul_overview/hotbar_box_tail"), 16, 32);
    private final List<HotbarSlot> slots = new ArrayList<>();
    private final SoulOverviewScreen screen;

    public HotbarWidget(SoulOverviewScreen screen, int x, int y) {
        super(x, y, 0, 32, Component.empty());
        this.screen = screen;

        for (int i = 0; i < HOLDER.getEquippedSkillMaxNumber(); i++) {
            slots.add(new HotbarSlot(x, y, i));
        }
        updateSize();
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
    }

    protected void renderBackground(GuiGraphics guiGraphics) {
        if (slots.isEmpty()) {
            return;
        }
        guiGraphics.blitSprite(BOTTOM_BOX_HEAD, getX(), getY(), 16, 32);
        int x = getX() + 16;
        if (slots.size() > 1) {
            for (int i = 1; i < slots.size(); i++) {
                guiGraphics.blitSprite(BOTTOM_BOX_CENTRE, x, getY(), 20, 32);
                x += 20;
            }
        }
        guiGraphics.blitSprite(BOTTOM_BOX_TAIL, x, getY(), 16, 32);
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        updateBoxPos();
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        updateBoxPos();
    }

    public void updateSize() {
        setWidth(slots.size() * 20 + 8);
    }

    public void updateBoxPos() {
        for (int i = 0; i < slots.size(); i++) {
            HotbarSlot slot = slots.get(i);
            slot.setX(getX() + 8 + i * 20);
            slot.setY(getY() + 9);
        }
    }

    public void rebuildFromHolder() {
        var equipped = HOLDER.getEquippedSkills();
        int equippedSize = equipped.size();
        for (int i = 0; i < slots.size(); i++) {
            slots.get(i).setSkillStack(i < equippedSize ? equipped.get(i) : null);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        for (HotbarSlot slot : slots) {
            slot.updateWidgetNarration(narrationElementOutput);
        }
    }

    public List<HotbarSlot> getSlots() {
        return Collections.unmodifiableList(slots);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    /// 快捷栏中的单个槽位
    public class HotbarSlot extends AbstractWidget {
        private final int slotIndex;
        @Nullable
        private SoulSkillStack skillStack;

        public HotbarSlot(int x, int y, int slotIndex) {
            super(x, y, 16, 16, Component.empty());
            this.slotIndex = slotIndex;
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            boolean isSelectedSlot = screen.getSelectedHotbarSlot() == slotIndex;

            // 渲染选中的槽位高亮
            if (isSelectedSlot) {
                SoulSkillBox.renderBoxSelect(guiGraphics, getX() - 8, getY() - 8);
            }

            if (skillStack == null) {
                return;
            }

            SoulSkillBox.renderSkillIcon(guiGraphics, skillStack, getX(), getY());
            if (isHovered) {
                SoulSkillBox.renderTooltip(screen.getFont(), guiGraphics, skillStack, mouseX, mouseY, partialTick);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (active && visible) {
                if (this.isValidClickButton(button)) {
                    boolean flag = this.clicked(mouseX, mouseY);
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
            if (button == 1) {
                HOLDER.unequipSkill(slotIndex);
                skillStack = null;
                return;
            }

            // 左键点击槽位：如果有选中的技能，装配到该槽；否则选择该槽
            SoulSkill selectedSkill = screen.theCurrentlySelectedSkill;
            if (selectedSkill != null) {
                // TODO 可能会导致技能重置cd
                SoulSkillStack stack = selectedSkill.getStack();
                HOLDER.equipSkill(slotIndex, stack);
                skillStack = stack;
            } else {
                // 如果没有选中的技能
                // 选择了相同的槽位则取消选择 否则 选择该槽位
                int slotIndex = screen.getSelectedHotbarSlot() == this.slotIndex ? -1 : this.slotIndex;
                screen.setSelectedHotbarSlot(slotIndex);
            }

            screen.uiButtonClickSound();
        }

        @Override
        protected boolean isValidClickButton(int button) {
            return button == 0 || button == 1;
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}

        @Nullable
        public SoulSkillStack getSkillStack() {
            return skillStack;
        }

        public void setSkillStack(@Nullable SoulSkillStack skillStack) {
            this.skillStack = skillStack;
        }

        public int getSlotIndex() {
            return slotIndex;
        }
    }

}
