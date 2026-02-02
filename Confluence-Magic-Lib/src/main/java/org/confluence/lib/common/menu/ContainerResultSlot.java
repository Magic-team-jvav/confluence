package org.confluence.lib.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.confluence.lib.common.recipe.AbstractAmountRecipe;
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
            AbstractAmountRecipe.consumeShapeless(container.getContainerSize(), container::getItem, recipe.getIngredients());
            container.setChanged();
            updateMenu();
        }
    }

    protected void updateMenu() {}

    @Override
    public boolean isFake() {
        return true;
    }
}
