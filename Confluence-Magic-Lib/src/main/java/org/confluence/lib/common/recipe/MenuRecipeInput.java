package org.confluence.lib.common.recipe;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeInput;

public class MenuRecipeInput extends SimpleContainer implements RecipeInput {
    private final AbstractContainerMenu menu;
    private CraftingInput craftingInput;

    public MenuRecipeInput(AbstractContainerMenu menu, int size) {
        super(size);
        this.menu = menu;
    }

    @Override
    public int size() {
        return getContainerSize();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        menu.slotsChanged(this);
    }

    public CraftingInput asCraftingInput(boolean update) {
        if (update || craftingInput == null) this.craftingInput = CraftingInput.of(4, 4, getItems());
        return craftingInput;
    }
}
