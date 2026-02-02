package org.confluence.lib.common.recipe;

import net.minecraft.world.inventory.AbstractContainerMenu;

public class EnvironmentRecipeInput extends MenuRecipeInput {
    private final EnvironmentLevelAccess access;

    public EnvironmentRecipeInput(AbstractContainerMenu menu, int size, EnvironmentLevelAccess access) {
        super(menu, size);
        this.access = access;
    }

    public EnvironmentLevelAccess getAccess() {
        return access;
    }
}
