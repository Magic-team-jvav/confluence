package org.confluence.mod.client.gui.hud.soul.quick_skill;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.gui.hud.BasicHudLayer;
import org.confluence.mod.client.handler.SoulQuickSkillHudHolder;

public abstract class BasicSoulQuickSkillHud extends BasicHudLayer {
    protected boolean active = false;
    protected final SoulQuickSkillHudHolder hudHolder;

    public BasicSoulQuickSkillHud() {
        super();
        hudHolder = SoulQuickSkillHudHolder.INSTANCE;
    }

    public void open() {
        active = true;
    }

    public void close() {
        active = false;
    }

    public boolean isActive() {
        return active;
    }

    public abstract void update();

    public abstract SoulQuickSkillHudHolder.Type getType();

    public boolean isType() {
        return ClientConfigs.soulQuickSkillStyle == getType();
    }

    protected abstract void drawTooltip(GuiGraphics guiGraphics, DeltaTracker deltaTracker, PoseStack poseStack, Font font);
}
