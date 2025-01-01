package org.confluence.mod.common.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.terra_curio.TerraCurio;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import static org.confluence.mod.common.attachment.ExtraInventory.*;

public class ExtraInventoryMenu extends AbstractContainerMenu {
    private final ExtraInventory extraInventory;

    public ExtraInventoryMenu(int containerId, Inventory inventory) {
        super(ModMenuTypes.EXTRA_INVENTORY.get(), containerId);
        this.extraInventory = inventory.player.getData(ModAttachmentTypes.EXTRA_INVENTORY);
        for (int i = 0; i < extraInventory.getContainerSize(); i++) {
            if (i < COINS_START) {
                addSlot(new ToggleSlot(extraInventory, i, 8, i * 18 + 8));
            } else if (i < AMMO_START) {
                addSlot(new Slot(extraInventory, i, 81, (i - COINS_START) * 18 + 8));
            } else if (i < EQUIPMENT_START) {
                addSlot(new Slot(extraInventory, i, 99, (i - AMMO_START) * 18 + 8));
            } else if (i < DYE_START) {
                addSlot(new ToggleSlot(extraInventory, i, 121, (i - EQUIPMENT_START) * 18 + 8));
            } else {
                ToggleSlot slot;
                int j = i - DYE_START;
                if (j < SIZE_VANITY_ARMOR) {
                    slot = new ToggleSlot(extraInventory, i, 8, j * 18 + 8);
                } else if (j < SIZE_DYE_EXCEPT_ACCESSORY_DYE) {
                    slot = new ToggleSlot(extraInventory, i, 121, (j - SIZE_VANITY_ARMOR) * 18 + 8);
                } else {
                    slot = new ToggleSlot(extraInventory, i, -25, (j - SIZE_DYE_EXCEPT_ACCESSORY_DYE) * 18 + 8);
                }
                slot.isActive = false;
                addSlot(slot);
            }
        }
        CuriosApi.getCuriosInventory(inventory.player).ifPresent(handler -> {
            ICurioStacksHandler accessory = handler.getCurios().get(TerraCurio.CURIO_SLOT);
            if (accessory != null) {
                ToggleCurioSlot.WrappedContainer container = new ToggleCurioSlot.WrappedContainer(accessory);
                for (int j = 0; j < accessory.getSlots(); j++) {
                    addSlot(new ToggleCurioSlot(container, j, -25, j * 18 + 8));
                }
            }
        });

        for (int k = 0; k < 3; k++) {
            for (int l = 0; l < 9; l++) {
                addSlot(new Slot(inventory, l + k * 9 + 9, 8 + l * 18, 84 + k * 18));
            }
        }
        for (int m = 0; m < 9; m++) {
            addSlot(new Slot(inventory, m, 8 + m * 18, 142));
        }
    }

    public ExtraInventory getExtraInventory() {
        return extraInventory;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }
}
