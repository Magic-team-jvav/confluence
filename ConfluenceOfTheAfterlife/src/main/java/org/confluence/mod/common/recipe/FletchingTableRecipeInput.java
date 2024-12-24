package org.confluence.mod.common.recipe;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.confluence.mod.common.menu.FletchingTableMenu;

public class FletchingTableRecipeInput extends SimpleContainer implements RecipeInput {
    private final FletchingTableMenu menu;

    public FletchingTableRecipeInput(FletchingTableMenu menu) {
        super(3);
        this.menu = menu;
    }

    public void setTail(ItemStack tail) {
        setItem(0, tail);
    }

    public ItemStack getTail() {
        return getItem(0);
    }

    public void setBody(ItemStack body) {
        setItem(1, body);
    }

    public ItemStack getBody() {
        return getItem(1);
    }

    public void setHead(ItemStack head) {
        setItem(2, head);
    }

    public ItemStack getHead() {
        return getItem(2);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        menu.slotsChanged(this);
    }

    @Override
    public int size() {
        return getContainerSize();
    }
}
