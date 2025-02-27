package org.confluence.mod.common.recipe;

import net.minecraft.world.inventory.AbstractContainerMenu;
import org.confluence.terra_curio.common.menu.RecipeInputContainer;

public class EnvironmentRecipeInput extends RecipeInputContainer {
    private final EnvironmentLevelAccess access;

    public EnvironmentRecipeInput(AbstractContainerMenu menu, int size, EnvironmentLevelAccess access) {
        super(menu, size);
        this.access = access;
    }

    public EnvironmentLevelAccess getAccess() {
        return access;
    }
}
