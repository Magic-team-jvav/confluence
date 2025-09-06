package org.confluence.mod.mixed;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.confluence.lib.mixed.SelfGetter;

public interface IInventoryScreen extends SelfGetter<InventoryScreen> {
    void confluence$setExtraButton(Button button);

    void confluence$setExtraButtonVisibility(boolean visible, int leftPos);

    static IInventoryScreen of(InventoryScreen screen) {
        return (IInventoryScreen) screen;
    }
}
