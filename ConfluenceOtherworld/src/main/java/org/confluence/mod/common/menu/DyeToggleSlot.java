package org.confluence.mod.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.menu.ToggleSlot;
import org.confluence.mod.common.init.ModTags;

public class DyeToggleSlot extends ToggleSlot {
    public DyeToggleSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
        this.isActive = false;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.is(ModTags.Items.DYE);
    }
}
