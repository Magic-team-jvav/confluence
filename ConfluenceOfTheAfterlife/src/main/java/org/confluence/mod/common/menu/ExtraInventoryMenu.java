package org.confluence.mod.common.menu;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.common.init.ModTags;
import org.confluence.terra_curio.TerraCurio;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Optional;

import static org.confluence.mod.common.attachment.ExtraInventory.*;

public class ExtraInventoryMenu extends AbstractContainerMenu {
    private final ExtraInventory extraInventory;
    private final int invStart;
    private final int hotBar;
    private final int invEnd;

    public ExtraInventoryMenu(int containerId, Inventory inventory) {
        super(ModMenuTypes.EXTRA_INVENTORY.get(), containerId);
        Player player = inventory.player;
        this.extraInventory = player.getData(ModAttachmentTypes.EXTRA_INVENTORY);
        int count = extraInventory.getContainerSize();
        for (int i = 0; i < count; i++) {
            if (i < COINS_START) {
                addSlot(new ToggleSlot(extraInventory, i, 8, i * 18 + 8) { // vanity armor
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
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
                });
            } else if (i < AMMO_START) {
                addSlot(new Slot(extraInventory, i, 81, (i - COINS_START) * 18 + 8) { // coins
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return stack.is(ModTags.Items.COINS);
                    }
                });
            } else if (i < EQUIPMENT_START) {
                addSlot(new Slot(extraInventory, i, 99, (i - AMMO_START) * 18 + 8) { // ammo
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return stack.is(ModTags.Items.AMMO);
                    }
                });
            } else if (i < DYE_START) {
                addSlot(new ToggleSlot(extraInventory, i, 121, (i - EQUIPMENT_START) * 18 + 8) { // equipment
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return stack.is(switch (getSlotIndex() - EQUIPMENT_START) {
                            case 1 -> ModTags.Items.LIGHT_PET;
                            case 2 -> ModTags.Items.MINECART;
                            case 3 -> ModTags.Items.HOOK;
                            default -> ModTags.Items.PET;
                        });
                    }
                });
            } else {
                addSlot(getDyeSlot(i));
            }
        }
        Optional<ICuriosItemHandler> optional = CuriosApi.getCuriosInventory(player);
        if (optional.isPresent()) {
            ICurioStacksHandler accessory = optional.get().getCurios().get(TerraCurio.CURIO_SLOT);
            if (accessory != null) {
                ToggleCurioSlot.WrappedContainer container = new ToggleCurioSlot.WrappedContainer(accessory);
                count += accessory.getSlots();
                for (int j = 0; j < accessory.getSlots(); j++) {
                    addSlot(new ToggleCurioSlot(container, j, -25, j * 18 + 8));
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

    private @NotNull ToggleSlot getDyeSlot(int i) {
        int j = i - DYE_START;
        int x, y;
        if (j < SIZE_VANITY_ARMOR) { // vanity armor dye
            x = 8;
            y = j * 18 + 8;
        } else if (j < SIZE_DYE_EXCEPT_ACCESSORY_DYE) { // equipment dye
            x = 121;
            y = (j - SIZE_VANITY_ARMOR) * 18 + 8;
        } else { // accessory dye
            x = -25;
            y = (j - SIZE_DYE_EXCEPT_ACCESSORY_DYE) * 18 + 8;
        }
        ToggleSlot slot = new ToggleSlot(extraInventory, i, x, y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(ModTags.Items.DYE);
            }
        };
        slot.isActive = false;
        return slot;
    }

    public ExtraInventory getExtraInventory() {
        return extraInventory;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < extraInventory.getContainerSize()) {
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
    public boolean stillValid(@NotNull Player player) {
        return true;
    }
}
