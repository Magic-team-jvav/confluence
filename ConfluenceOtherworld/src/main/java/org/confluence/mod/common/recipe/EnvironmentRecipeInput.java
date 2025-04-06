package org.confluence.mod.common.recipe;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.CraftingInput;
import org.confluence.terra_curio.common.menu.RecipeInputContainer;

public class EnvironmentRecipeInput extends RecipeInputContainer {
    private final EnvironmentLevelAccess access;
    private CraftingInput craftingInput;

    public EnvironmentRecipeInput(AbstractContainerMenu menu, int size, EnvironmentLevelAccess access) {
        super(menu, size);
        this.access = access;
    }

    public EnvironmentLevelAccess getAccess() {
        return access;
    }

    public CraftingInput asCraftingInput(boolean update) {
        if (update || craftingInput == null) this.craftingInput = CraftingInput.of(4, 4, getItems());
        return craftingInput;
    }
}
