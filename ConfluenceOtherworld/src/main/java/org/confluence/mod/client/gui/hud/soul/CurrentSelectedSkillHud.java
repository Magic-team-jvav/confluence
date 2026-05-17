package org.confluence.mod.client.gui.hud.soul;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.HumanoidArm;
import org.confluence.mod.client.gui.hud.BasicHudLayer;
import org.confluence.mod.client.gui.hud.soul.quick_skill.RouletteWheelSmallHud;
import org.confluence.mod.client.gui.widget.SoulSkillBox;
import org.confluence.mod.client.handler.SoulQuickSkillHudHolder;
import org.confluence.mod.common.soulskill.SoulSkillStack;
import org.jetbrains.annotations.Nullable;

public class CurrentSelectedSkillHud extends BasicHudLayer {
    private HumanoidArm humanoidArm = HumanoidArm.RIGHT;

    public final SoulSkillBox box = new SoulSkillBox(SoulSkillStack.EMPTY);

    public CurrentSelectedSkillHud() {
        super();
    }

    @Override
    protected void renderDrawLayer(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        box.render(guiGraphics, 0, 0, deltaTracker.getRealtimeDeltaTicks());
    }

    @Override
    public void init(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        super.init(guiGraphics, deltaTracker);
        HumanoidArm mainArm = getPlayerThrow().getMainArm();
        setLeftPos(getScreenWidth() / 2 + getOffset());
        if (mainArm != this.humanoidArm) {
            humanoidArm = mainArm;
        }
    }

    public void update(@Nullable SoulSkillStack skillStack) {
        box.setSkill(skillStack);
    }

    @Override
    protected void sizeChange(int newScreenWidth, int newScreenHeight) {
        super.sizeChange(newScreenWidth, newScreenHeight);
        setLeftPos(newScreenWidth / 2 + getOffset());
        setTopPos(newScreenHeight - box.getHeight() + 4);
    }

    @Override
    public void setLeftPos(int var1) {
        super.setLeftPos(var1);
        box.setX(var1);
    }

    public void setTopPos(int var1) {
        super.setTopPos(var1);
        box.setY(var1);
    }

    @Override
    public int getWidth() {
        return box.getWidth();
    }

    @Override
    public int getHeight() {
        return box.getHeight();
    }

    private int getOffset() {
        return isRight() ? -box.getWidth() / 2 + 106 : -box.getWidth() / 2 - 106;
    }

    public boolean isRight() {
        return humanoidArm == HumanoidArm.RIGHT;
    }
}
