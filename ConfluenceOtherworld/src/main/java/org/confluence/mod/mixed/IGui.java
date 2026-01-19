package org.confluence.mod.mixed;

import net.minecraft.client.gui.Gui;

public interface IGui {
    void confluence$setShooting();

    static IGui of(Gui gui) {
        return (IGui) gui;
    }
}
