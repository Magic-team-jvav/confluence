package org.confluence.mod.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.confluence.terra_curio.common.recipe.AbstractAmountRecipe;
import org.jetbrains.annotations.Nullable;

public class ContainerResultSlot extends Slot {
    protected @Nullable Recipe<?> recipe;

    public ContainerResultSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    public void setCurrentRecipe(@Nullable Recipe<?> recipe) {
        this.recipe = recipe;
    }

    @Override
    public boolean mayPlace(ItemStack pStack) {
        return false;
    }

    @Override
    public void onTake(Player pPlayer, ItemStack pStack) {
        if (recipe != null) {
            AbstractAmountRecipe.extractContainer(container, recipe.getIngredients(), false);
            container.setChanged();
            updateMenu();
        }
    }

    protected void updateMenu() {}
}
