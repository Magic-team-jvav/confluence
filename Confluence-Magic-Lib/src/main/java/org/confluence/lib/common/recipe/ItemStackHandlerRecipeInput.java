package org.confluence.lib.common.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;

public class ItemStackHandlerRecipeInput extends ItemStackHandler implements RecipeInput {
    public final BlockEntity blockEntity;

    public ItemStackHandlerRecipeInput(BlockEntity blockEntity, int size) {
        super(size);
        this.blockEntity = blockEntity;
    }

    public NonNullList<ItemStack> getItems() {
        return stacks;
    }

    public void setItems(NonNullList<ItemStack> items) {
        this.stacks = items;
    }

    @Override
    public boolean isEmpty() {
        return stacks.isEmpty();
    }

    @Override
    public ItemStack getItem(int pSlot) {
        return getStackInSlot(pSlot);
    }

    @Override
    public int size() {
        return getSlots();
    }
}
