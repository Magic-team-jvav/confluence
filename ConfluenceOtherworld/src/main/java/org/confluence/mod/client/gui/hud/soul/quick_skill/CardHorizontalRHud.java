package org.confluence.mod.client.gui.hud.soul.quick_skill;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import org.confluence.mod.client.handler.SoulQuickSkillHudHolder;

public class CardHorizontalRHud extends BasicSoulQuickSkillHud {

    public CardHorizontalRHud() {
        super();
    }

    @Override
    protected void renderDrawLayer(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!isType()) {
            return;
        }
    }

    @Override
    public void open() {
        super.open();
    }

    @Override
    public void close() {
        super.open();
    }

    @Override
    public void update() {

    }

    @Override
    public SoulQuickSkillHudHolder.Type getType() {
        return SoulQuickSkillHudHolder.Type.CARD_HORIZONTAL_R;
    }

    @Override
    protected void drawTooltip(GuiGraphics guiGraphics, DeltaTracker deltaTracker, PoseStack poseStack, Font font) {

    }
}
