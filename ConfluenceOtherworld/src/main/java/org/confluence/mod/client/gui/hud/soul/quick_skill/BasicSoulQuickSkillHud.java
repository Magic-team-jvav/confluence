package org.confluence.mod.client.gui.hud.soul.quick_skill;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.gui.hud.BasicHudLayer;
import org.confluence.mod.client.handler.SoulSkillClientHolder;

public abstract class BasicSoulQuickSkillHud extends BasicHudLayer {
    protected boolean active = false;
    protected final SoulSkillClientHolder soulSkillHolder;
    protected boolean isInit;

    public BasicSoulQuickSkillHud() {
        super();
        soulSkillHolder = SoulSkillClientHolder.INSTANCE;
    }

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!isInit){
            update();
        }
        super.render(guiGraphics, deltaTracker);
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

    public abstract SoulSkillClientHolder.Type getType();

    public boolean isType() {
        return ClientConfigs.soulQuickSkillStyle == getType();
    }
}
