package org.confluence.mod.common.item.sword.legacy;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IInventoryTick {
    void accept(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected);
}
