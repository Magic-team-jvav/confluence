package org.confluence.mod.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.confluence.lib.common.menu.IToggleSlot;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.common.init.TCTags;
import org.confluence.terra_curio.common.item.curio.BaseCurioItem;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

public class ToggleCurioSlot extends Slot implements IToggleSlot {
    public boolean isActive = true;
    private final Player player;

    public ToggleCurioSlot(Player player, Container container, int slot, int x, int y) {
        super(container, slot, x, y);
        this.player = player;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (stack.getItem() instanceof BaseCurioItem baseCurioItem) {
            return baseCurioItem.canEquip(new SlotContext(TerraCurio.CURIO_SLOT, player, getSlotIndex(), false, true), stack);
        }
        return stack.is(TCTags.ACCESSORY);
    }

    @Override
    public boolean mayPickup(Player player) {
        ItemStack itemstack = getItem();
        return (itemstack.isEmpty() || player.isCreative() || !EnchantmentHelper.has(itemstack, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE)) && super.mayPickup(player);
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
            handler.setStackInSlot(slot, ItemStack.EMPTY);
            handler.setPreviousStackInSlot(slot, ItemStack.EMPTY);
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
