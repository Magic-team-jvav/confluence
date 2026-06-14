package org.confluence.mod.common.menu;

import PortLib.extensions.net.minecraft.world.item.ItemStack.PortItemStackExtension;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.menu.ToggleSlot;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.common.init.ModTags;
import org.confluence.terra_curio.TerraCurio;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Optional;

import static org.confluence.mod.common.attachment.ExtraInventory.*;

public class ExtraInventoryMenu extends AbstractContainerMenu {
    public final ExtraInventory extraInventory;
    public final int invStart;
    private final int hotBar;
    private final int invEnd;

    public ExtraInventoryMenu(int containerId, Inventory inventory) {
        super(ModMenuTypes.EXTRA_INVENTORY.get(), containerId);
        Player player = inventory.player;
        this.extraInventory = ExtraInventory.of(player);
        Optional<ICuriosItemHandler> optional = CuriosApi.getCuriosInventory(player);
        int count = extraInventory.getContainerSize();
        for (int i = VANITY_ARMOR_START; i < count; i++) {
            if (i < VANITY_ARMOR_DYE_START) { // 0, 1, 2, 3
                addSlot(new ToggleSlot(extraInventory, i, 8, i * 18 + 8) { // vanity armor
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        if (stack.getItem() instanceof ArmorItem armorItem) {
                            EquipmentSlot equipmentSlot = armorItem.getEquipmentSlot();
                            int slotIndex = getSlotIndex();
                            if (slotIndex == 0) return equipmentSlot == EquipmentSlot.HEAD;
                            if (slotIndex == 1) return equipmentSlot == EquipmentSlot.CHEST;
                            if (slotIndex == 2) return equipmentSlot == EquipmentSlot.LEGS;
                            if (slotIndex == 3) return equipmentSlot == EquipmentSlot.FEET;
                        }
                        return false;
                    }

                    @Override
                    public boolean mayPickup(Player player) {
                        ItemStack itemstack = getItem();
                        if (!itemstack.isEmpty()) {player.isCreative();}
                        return super.mayPickup(player);
                    }
                });
            } else if (i < COINS_START) { // 4, 5, 6 ,7
                addSlot(new DyeToggleSlot(extraInventory, i, 8, (i - VANITY_ARMOR_DYE_START) * 18 + 8));
            } else if (i < AMMO_START) { // 8, 9, 10, 11
                addSlot(new CoinsSlot(extraInventory, i, 81, (i - COINS_START) * 18 + 8));
            } else if (i < EQUIPMENT_START) { // 12, 13, 14, 15
                addSlot(new Slot(extraInventory, i, 99, (i - AMMO_START) * 18 + 8) { // ammo
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return stack.is(ModTags.Items.AMMO);
                    }
                });
            } else if (i < EQUIPMENT_DYE_START) { // 16, 17, 18, 19, 20
                int j = i - EQUIPMENT_START;
                int x = j == MOUNT_INDEX ? 148 : 121;
                int y = j == MOUNT_INDEX ? 8 : j * 18 + 8;
                addSlot(new ToggleSlot(extraInventory, i, x, y) { // equipment
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return stack.is(switch (j) {
                            case LIGHT_PET_INDEX -> ModTags.Items.LIGHT_PET;
                            case MINECART_INDEX -> ModTags.Items.MINECART;
                            case HOOK_INDEX -> ModTags.Items.HOOK;
                            case MOUNT_INDEX -> ModTags.Items.MOUNT;
                            default -> ModTags.Items.PET;
                        });
                    }
                });
            } else if (i < TRASH_START) { // 21, 22, 23, 24, 25
                int j = i - EQUIPMENT_DYE_START;
                int x = j == MOUNT_INDEX ? 148 : 121;
                int y = j == MOUNT_INDEX ? 8 : j * 18 + 8;
                addSlot(new DyeToggleSlot(extraInventory, i, x, y));
            } else if (i < ACCESSORY_DYE_START) { // 26
                addSlot(new Slot(extraInventory, i, 152, 166));
            } else { // 27...
                addSlot(new DyeToggleSlot(extraInventory, i, -25, (i - ACCESSORY_DYE_START) * 18 + 8));
            }
        }
        if (optional.isPresent()) {
            ICurioStacksHandler accessory = optional.get().getCurios().get(TerraCurio.CURIO_SLOT);
            if (accessory != null) {
                ToggleCurioSlot.WrappedContainer container = new ToggleCurioSlot.WrappedContainer(accessory);
                count += accessory.getSlots();
                for (int j = 0; j < accessory.getSlots(); j++) {
                    addSlot(new ToggleCurioSlot(player, container, j, -25, j * 18 + 8));
                }
            }
        }
        this.invStart = count;
        for (int k = 0; k < 3; k++) {
            for (int l = 0; l < 9; l++) {
                addSlot(new Slot(inventory, l + k * 9 + 9, 8 + l * 18, 84 + k * 18));
            }
        }
        this.hotBar = invStart + 27;
        for (int m = 0; m < 9; m++) {
            addSlot(new Slot(inventory, m, 8 + m * 18, 142));
        }
        this.invEnd = hotBar + 9;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index >= invStart - extraInventory.getSizeAccessoryDye() && index < invStart) {
                if (!moveItemStackTo(itemstack1, invStart, invEnd, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < extraInventory.getContainerSize()) {
                if (!moveItemStackTo(itemstack1, invStart, invEnd, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(itemstack1, 0, invStart, false)) {
                if (index < hotBar) {
                    if (!moveItemStackTo(itemstack1, hotBar, invEnd, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < invEnd && !moveItemStackTo(itemstack1, 8, hotBar, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            }

            slot.setChanged();
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
            broadcastChanges();
        }

        return itemstack;
    }

    @Override
    protected boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        if (startIndex <= TRASH_START && endIndex >= TRASH_START) {
            return super.moveItemStackTo(stack, startIndex, TRASH_START, reverseDirection) ||
                    super.moveItemStackTo(stack, ACCESSORY_DYE_START, endIndex, reverseDirection);
        }
        return super.moveItemStackTo(stack, startIndex, endIndex, reverseDirection);
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        boolean isThrow = clickType == ClickType.THROW;
        if ((slotId >= 0 && slotId < slots.size()) && (slotId == TRASH_START || isThrow)) {
            ClickAction action = button == 0 ? ClickAction.PRIMARY : ClickAction.SECONDARY;
            Slot slot = slots.get(isThrow ? TRASH_START : slotId);
            ItemStack carried = isThrow ? slots.get(slotId).getItem() : getCarried();
            ItemStack slotItem = slot.getItem();
            if (tryItemClickBehaviourOverride(player, action, slot, slotItem, carried)) return;
            if (isThrow) {
                slot.setByPlayer(carried);
                slots.get(slotId).setByPlayer(ItemStack.EMPTY);
            } else if (carried.isEmpty()) {
                int j3 = action == ClickAction.PRIMARY ? slotItem.getCount() : (slotItem.getCount() + 1) / 2;
                Optional<ItemStack> optional = slot.tryRemove(j3, Integer.MAX_VALUE, player);
                if (optional.isPresent()) {
                    ItemStack itemStack = optional.get();
                    setCarried(itemStack);
                    slot.onTake(player, itemStack);
                }
            } else if (PortItemStackExtension.isSameItemSameComponents(carried, slotItem)) {
                slotItem.grow(Math.min(slot.getMaxStackSize(slotItem) - slotItem.getCount(), carried.getCount()));
                setCarried(ItemStack.EMPTY);
            } else {
                slot.setByPlayer(carried);
                setCarried(ItemStack.EMPTY);
            }
        } else {
            super.clicked(slotId, button, clickType, player);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public static class CoinsSlot extends Slot {
        public CoinsSlot(ExtraInventory extraInventory, int i, int x, int y) {
            super(extraInventory, i, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.is(ModTags.Items.COINS);
        }
    }
}
