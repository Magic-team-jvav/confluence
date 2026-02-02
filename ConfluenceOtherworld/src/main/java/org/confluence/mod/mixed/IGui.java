package org.confluence.mod.mixed;

import net.minecraft.client.gui.Gui;
import org.confluence.lib.mixed.SelfGetter;

public interface IGui extends SelfGetter<Gui> {
    void confluence$setShooting();

    float confluence$getScale();

    void confluence$setScale(float scale);

    float confluence$getOldRepeaterCrosshairAngle();

    void confluence$setOldRepeaterCrosshairAngle(float oldRepeaterCrosshairAngle);

    static IGui of(Gui gui) {
        return (IGui) gui;
    }
}
