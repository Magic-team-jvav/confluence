package org.confluence.mod.mixed;

import net.minecraft.client.gui.components.Button;

public interface IInventoryScreen {
    void confluence$setExtraButton(Button button);

    void confluence$setExtraButtonVisibility(boolean visible, int leftPos);
}
