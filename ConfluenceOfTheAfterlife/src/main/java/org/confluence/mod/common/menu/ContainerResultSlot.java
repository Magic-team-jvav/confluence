package org.confluence.mod.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.confluence.terra_curio.common.recipe.AbstractAmountRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ContainerResultSlot extends Slot {
    protected @Nullable AbstractAmountRecipe recipe;

    public ContainerResultSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    public void setCurrentRecipe(@Nullable AbstractAmountRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack pStack) {
        return false;
    }

    @Override
    public void onTake(@NotNull Player pPlayer, @NotNull ItemStack pStack) {
        if (recipe != null) {
            AbstractAmountRecipe.extractContainer(container, recipe.getIngredients());
            container.setChanged();
            updateMenu();
        }
    }

    protected void updateMenu() {}
}
