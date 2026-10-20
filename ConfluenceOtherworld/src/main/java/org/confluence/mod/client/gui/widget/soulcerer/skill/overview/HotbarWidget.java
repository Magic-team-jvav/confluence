package org.confluence.mod.client.gui.widget.soulcerer.skill.overview;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.container.soulcerer.SoulOverviewScreen;
import org.confluence.mod.client.gui.widget.soulcerer.skill.SoulSkillBox;
import org.confluence.mod.client.handler.soulcerer.ClientSoulHandler;
import org.confluence.mod.common.soulcerer.skill.SoulSkill;
import org.confluence.mod.common.soulcerer.skill.SoulSkillStack;
import org.confluence.mod.util.PlayerUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * 底部快捷栏
 */
public class HotbarWidget extends AbstractWidget {

    public static final ResourceLocation BOTTOM_BOX_CENTRE = Confluence.asResource("container/soulcerer_overview/hotbar_box_centre");
    public static final ResourceLocation BOTTOM_BOX_HEAD = Confluence.asResource("container/soulcerer_overview/hotbar_box_head");
    public static final ResourceLocation BOTTOM_BOX_TAIL = Confluence.asResource("container/soulcerer_overview/hotbar_box_tail");
    private static final int CAP_WIDTH = 16;
    private static final int CENTRE_WIDTH = 20;
    private static final int BACKGROUND_HEIGHT = 32;
    private final List<HotbarSlot> slots = new ArrayList<>();
    private final SoulOverviewScreen screen;

    public HotbarWidget(SoulOverviewScreen screen, int x, int y) {
        super(x, y, 0, BACKGROUND_HEIGHT, Component.empty());
        this.screen = screen;

        for (int i = 0; i < PlayerUtils.getEquippedSoulSkillMaxNumber(Minecraft.getInstance().player); i++) {
            slots.add(new HotbarSlot(x, y, i));
        }
        updateSize();
    }

    /// 绘制快捷栏背景。
    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
    }

    /// 按槽位数量拼接快捷栏背景。
    protected void renderBackground(GuiGraphics guiGraphics) {
        if (slots.isEmpty()) {
            return;
        }
        guiGraphics.blitSprite(BOTTOM_BOX_HEAD, getX(), getY(), CAP_WIDTH, BACKGROUND_HEIGHT);
        int x = getX() + CAP_WIDTH;
        if (slots.size() > 1) {
            for (int i = 1; i < slots.size(); i++) {
                guiGraphics.blitSprite(BOTTOM_BOX_CENTRE, x, getY(), CENTRE_WIDTH, BACKGROUND_HEIGHT);
                x += CENTRE_WIDTH;
            }
        }
        guiGraphics.blitSprite(BOTTOM_BOX_TAIL, x, getY(), CAP_WIDTH, BACKGROUND_HEIGHT);
    }

    /// 设置快捷栏横坐标并更新槽位位置。
    @Override
    public void setX(int x) {
        super.setX(x);
        updateBoxPos();
    }

    /// 设置快捷栏纵坐标并更新槽位位置。
    @Override
    public void setY(int y) {
        super.setY(y);
        updateBoxPos();
    }

    /// 根据槽位数量更新快捷栏宽度。
    public void updateSize() {
        setWidth(slots.size() * 20 + 8);
    }

    /// 按快捷栏位置排列各个槽位。
    public void updateBoxPos() {
        for (int i = 0; i < slots.size(); i++) {
            HotbarSlot slot = slots.get(i);
            slot.setX(getX() + 8 + i * 20);
            slot.setY(getY() + 9);
        }
    }

    /// 用当前装备数据刷新槽位显示。
    public void rebuildFromHolder() {
        int slotCount = PlayerUtils.getEquippedSoulSkillMaxNumber(Minecraft.getInstance().player);
        if (slots.size() != slotCount) {
            slots.clear();
            for (int i = 0; i < slotCount; i++) {
                slots.add(new HotbarSlot(getX(), getY(), i));
            }
            updateSize();
            updateBoxPos();
        }
        var equipped = PlayerUtils.getEquippedSoulSkills(Minecraft.getInstance().player);
        int equippedSize = equipped.size();
        for (int i = 0; i < slots.size(); i++) {
            slots.get(i).setSkillStack(i < equippedSize ? equipped.get(i) : SoulSkillStack.Empty);
        }
    }

    /// 转发各槽位的朗读更新。
    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        for (HotbarSlot slot : slots) {
            slot.updateWidgetNarration(narrationElementOutput);
        }
    }

    /// 获取槽位列表的只读视图。
    public List<HotbarSlot> getSlots() {
        return Collections.unmodifiableList(slots);
    }

    /// 沿用快捷栏基类的点击处理。
    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
    }

    /// 将点击处理交给独立槽位控件。
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    /**
     * 快捷栏中的单个槽位
     */
    public class HotbarSlot extends AbstractWidget {
        private final int slotIndex;
        private SoulSkillStack skillStack = SoulSkillStack.Empty;

        public HotbarSlot(int x, int y, int slotIndex) {
            super(x, y, 16, 16, Component.empty());
            this.slotIndex = slotIndex;
        }

        /// 绘制槽位选中状态、技能图标及提示。
        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            boolean isSelectedSlot = screen.getSelectedHotbarSlot() == slotIndex;

            // 渲染选中的槽位高亮
            if (isSelectedSlot) {
                SoulSkillBox.renderBoxSelect(guiGraphics, getX() - 8, getY() - 8);
            }

            if (skillStack.isEmpty()) {
                return;
            }

            SoulSkillBox.renderSkillIcon(guiGraphics, skillStack, getX(), getY());
            if (isHovered) {
                SoulSkillBox.renderTooltip(screen.getFont(), guiGraphics, skillStack, mouseX, mouseY, partialTick);
            }
        }

        /// 处理槽位装卸技能和选中状态。
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
            if (button == 1) {
                if (ClientSoulHandler.unequipSoulSkill(slotIndex)) {
                    skillStack = SoulSkillStack.Empty;
                }
                return;
            }

            // 左键点击槽位：如果有选中的技能，装配到该槽；否则选择该槽
            SoulSkill selectedSkill = screen.theCurrentlySelectedSkill;
            if (selectedSkill != null) {
                SoulSkillStack stack = selectedSkill.getStack();
                if (!ClientSoulHandler.equipSoulSkill(slotIndex, stack)) {
                    return;
                }
                skillStack = PlayerUtils.getSkillStack(Minecraft.getInstance().player, slotIndex);
            } else {
                // 如果没有选中的技能
                // 选择了相同的槽位则取消选择 否则 选择该槽位
                int slotIndex = screen.getSelectedHotbarSlot() == this.slotIndex ? -1 : this.slotIndex;
                screen.setSelectedHotbarSlot(slotIndex);
            }

            screen.uiButtonClickSound();
        }

        /// 允许鼠标左键和右键操作槽位。
        @Override
        protected boolean isValidClickButton(int button) {
            return button == 0 || button == 1;
        }

        /// 不追加槽位朗读内容。
        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}

        /// 获取槽位当前显示的技能堆栈。
        public SoulSkillStack getSkillStack() {
            return skillStack;
        }

        /// 更新槽位显示的技能堆栈。
        public void setSkillStack(SoulSkillStack skillStack) {
            this.skillStack = skillStack;
        }

        /// 获取槽位在快捷栏中的索引。
        public int getSlotIndex() {
            return slotIndex;
        }
    }

}
