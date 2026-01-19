package org.confluence.mod.mixed;

import net.minecraft.client.gui.Gui;

public interface IGui {
    static IGui of(Gui gui) {
        return (IGui) gui;
    }

    void confluence$setShooting();
}
