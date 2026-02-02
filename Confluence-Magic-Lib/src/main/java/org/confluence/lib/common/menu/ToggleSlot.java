package org.confluence.lib.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class ToggleSlot extends Slot implements IToggleSlot {
    public boolean isActive = true;

    public ToggleSlot(Container container, int slot, int x, int y)  {
        super(container, slot, x, y);
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void setEnable(boolean enable) {
        this.isActive = enable;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
