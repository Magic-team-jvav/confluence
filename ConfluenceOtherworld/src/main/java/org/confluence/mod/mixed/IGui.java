package org.confluence.mod.mixed;

import net.minecraft.client.gui.Gui;

public interface IGui {
    void confluence$setShooting();

    static IGui of(Gui gui) {
        return (IGui) gui;
    }

    float confluence$getScale();

    void confluence$setScale(float scale);

    float confluence$getOldRepeaterCrosshairAngle();

    void confluence$setOldRepeaterCrosshairAngle(float oldRepeaterCrosshairAngle);
}
