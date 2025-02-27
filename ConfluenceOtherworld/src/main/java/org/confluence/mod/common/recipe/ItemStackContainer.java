package org.confluence.mod.common.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;

public class ItemStackContainer extends ItemStackHandler implements Container, RecipeInput {
    public final BlockEntity blockEntity;

    public ItemStackContainer(BlockEntity blockEntity, int size) {
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
    public int getContainerSize() {
        return getSlots();
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

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        return extractItem(pSlot, pAmount, false);
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        return extractItem(pSlot, 64, true);
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        setStackInSlot(pSlot, pStack);
    }

    @Override
    public void setChanged() {
        blockEntity.setChanged();
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    @Override
    public void clearContent() {
        stacks.clear();
    }
}
