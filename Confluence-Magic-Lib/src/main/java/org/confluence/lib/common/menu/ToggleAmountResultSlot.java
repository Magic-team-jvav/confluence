package org.confluence.lib.common.menu;

import net.minecraft.world.Container;
import org.confluence.lib.common.recipe.AbstractAmountRecipe;
import org.confluence.lib.common.recipe.MenuRecipeInput;

public class ToggleAmountResultSlot<R extends AbstractAmountRecipe<?>> extends AmountResultSlot<R> implements IToggleSlot {
    public boolean isActive = true;

    public ToggleAmountResultSlot(MenuRecipeInput input, Container result, int pSlot, int pX, int pY) {
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
