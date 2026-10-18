package org.confluence.mod.client.gui.hud.soulcerer;

import org.mesdag.portlib.client.PortDeltaTicker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.HumanoidArm;
import org.confluence.mod.client.gui.hud.BasicHudLayer;
import org.confluence.mod.client.gui.widget.soulcerer.skill.SoulSkillBox;
import org.confluence.mod.common.soulcerer.skill.SoulSkillStack;
import org.confluence.mod.util.PlayerUtils;

public class CurrentSelectedSkillHud extends BasicHudLayer {
    private HumanoidArm humanoidArm = HumanoidArm.RIGHT;

    public final SoulSkillBox box = new SoulSkillBox();

    public CurrentSelectedSkillHud() {
        super();
    }

    /// 绘制当前槽位的技能框。
    @Override
    protected void renderDrawLayer(GuiGraphics guiGraphics, PortDeltaTicker deltaTracker) {
        if (!PlayerUtils.isHoldingSoulWeapon(player)) {
            return;
        }
        box.render(guiGraphics, 0, 0, deltaTracker.getRealtimeDeltaTicks());
    }

    /// 更新主手方向和技能框的水平位置。
    @Override
    public void init(GuiGraphics guiGraphics, PortDeltaTicker deltaTracker) {
        super.init(guiGraphics, deltaTracker);
        HumanoidArm mainArm = getPlayerThrow().getMainArm();
        setLeftPos(getScreenWidth() / 2 + getOffset());
        if (mainArm != this.humanoidArm) {
            humanoidArm = mainArm;
        }
    }

    /// 更新当前技能框显示的技能，空栈用于清空显示。
    public void updateSkill(SoulSkillStack skillStack) {
        box.setSkill(skillStack);
    }

    /// 随屏幕尺寸变化重新定位当前技能框。
    @Override
    protected void sizeChange(int newScreenWidth, int newScreenHeight) {
        super.sizeChange(newScreenWidth, newScreenHeight);
        setLeftPos(newScreenWidth / 2 + getOffset());
        setTopPos(newScreenHeight - box.getHeight() + 4);
    }

    /// 同步调整 HUD 与技能框的横坐标。
    @Override
    public void setLeftPos(int var1) {
        super.setLeftPos(var1);
        box.setX(var1);
    }

    /// 同步调整 HUD 与技能框的纵坐标。
    public void setTopPos(int var1) {
        super.setTopPos(var1);
        box.setY(var1);
    }

    /// 取得当前技能框的显示宽度。
    @Override
    public int getWidth() {
        return box.getWidth();
    }

    /// 取得当前技能框的显示高度。
    @Override
    public int getHeight() {
        return box.getHeight();
    }

    /// 按主手方向计算技能框相对屏幕中心的偏移。
    private int getOffset() {
        return isRight() ? -box.getWidth() / 2 + 106 : -box.getWidth() / 2 - 106;
    }

    /// 判断当前记录的主手是否为右手。
    public boolean isRight() {
        return humanoidArm == HumanoidArm.RIGHT;
    }
}

