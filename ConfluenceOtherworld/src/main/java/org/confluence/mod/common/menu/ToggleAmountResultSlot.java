package org.confluence.mod.common.menu;

import net.minecraft.world.Container;
import org.confluence.terra_curio.common.menu.AmountResultSlot;
import org.confluence.terra_curio.common.menu.RecipeInputContainer;

public class ToggleAmountResultSlot extends AmountResultSlot implements IToggleSlot {
    public boolean isActive = true;

    public ToggleAmountResultSlot(RecipeInputContainer input, Container result, int pSlot, int pX, int pY) {
        super(input, result, pSlot, pX, pY);
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
