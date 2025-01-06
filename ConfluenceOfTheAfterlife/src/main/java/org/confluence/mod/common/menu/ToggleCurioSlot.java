package org.confluence.mod.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

public class ToggleCurioSlot extends Slot implements IToggleSlot {
    public boolean isActive = true;

    public ToggleCurioSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    public ToggleCurioSlot(ICurioStacksHandler handler, int slot, int x, int y) {
        super(new WrappedContainer(handler), slot, x, y);
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public boolean mayPickup(Player player) {
        return false;
    }

    @Override
    public void setEnable(boolean enable) {
        this.isActive = enable;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }

    public static class WrappedContainer implements Container {
        private final IDynamicStackHandler handler;

        public WrappedContainer(ICurioStacksHandler handler) {
            this.handler = handler.getStacks();
        }

        @Override
        public int getContainerSize() {
            return handler.getSlots();
        }

        @Override
        public boolean isEmpty() {
            for (int i = 0; i < handler.getSlots(); i++) {
                if (!handler.getStackInSlot(i).isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public ItemStack getItem(int slot) {
            return handler.getStackInSlot(slot);
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            return handler.extractItem(slot, amount, false);
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            ItemStack itemStack = getItem(slot);
            setItem(slot, ItemStack.EMPTY); // 还是会更新
            return itemStack;
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            handler.setStackInSlot(slot, stack);
        }

        @Override
        public void setChanged() {}

        @Override
        public boolean stillValid(Player player) {
            return true;
        }

        @Override
        public void clearContent() {}
    }
}
