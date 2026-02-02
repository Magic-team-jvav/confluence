package org.confluence.lib.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.recipe.AbstractAmountRecipe;
import org.confluence.lib.common.recipe.MenuRecipeInput;
import org.jetbrains.annotations.Nullable;

public class AmountResultSlot<R extends AbstractAmountRecipe<?>> extends Slot {
    protected final MenuRecipeInput input;
    protected @Nullable R recipe;

    public AmountResultSlot(MenuRecipeInput input, Container result, int slot, int x, int y) {
        super(result, slot, x, y);
        this.input = input;
    }

    public void setCurrentRecipe(@Nullable R recipe) {
        this.recipe = recipe;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        if (recipe != null) {
            AbstractAmountRecipe.consumeShapeless(input, recipe.getIngredients());
            input.setChanged();
            updateMenu();
        }
    }

    protected void updateMenu() {}

    @Override
    public boolean isFake() {
        return true;
    }
}
