package org.confluence.mod.common.data.fixer;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.item.ToolItems;
import org.confluence.mod.common.item.common.DemonConch;

public class FixedDemonConch extends DemonConch {
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof ServerPlayer player) {
            ItemStack itemStack = ToolItems.DEMON_CONCH.toStack();
            itemStack.applyComponents(stack.getComponents());
            player.getInventory().setItem(slotId, itemStack);
        }
    }
}
