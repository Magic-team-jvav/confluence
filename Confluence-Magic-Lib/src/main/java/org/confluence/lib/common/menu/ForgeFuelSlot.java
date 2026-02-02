package org.confluence.lib.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;

public class ForgeFuelSlot extends Slot {
    private final RecipeType<?> recipeType;

    public ForgeFuelSlot(RecipeType<?> recipeType, Container furnaceContainer, int slot, int xPosition, int yPosition) {
        super(furnaceContainer, slot, xPosition, yPosition);
        this.recipeType = recipeType;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getBurnTime(recipeType) > 0 || isBucket(stack);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return isBucket(stack) ? 1 : super.getMaxStackSize(stack);
    }

    public static boolean isBucket(ItemStack stack) {
        return stack.is(Items.BUCKET);
    }
}
