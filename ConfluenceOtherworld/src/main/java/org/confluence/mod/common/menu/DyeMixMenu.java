package org.confluence.mod.common.menu;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.item.VanityArmorItems;

public class DyeMixMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final SimpleContainer container;

    public DyeMixMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
    }

    public DyeMixMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(ModMenuTypes.DYE_MIX.get(), containerId);
        this.access = access;
        this.container = new SimpleContainer(3);

        addSlot(new Slot(container, 0, 17, 14) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(VanityArmorItems.RED_DYE);
            }
        });
        addSlot(new Slot(container, 1, 17, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(VanityArmorItems.GREEN_DYE);
            }
        });
        addSlot(new Slot(container, 2, 17, 56) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(VanityArmorItems.BLUE_DYE);
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

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY; // todo
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, FunctionalBlocks.DYE_VAT.get());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        access.execute((level, blockPos) -> clearContainer(player, container));
    }
}
