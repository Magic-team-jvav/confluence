package org.confluence.terra_curio.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class PlayerEmptyAutoAttackEvent extends PlayerEvent implements ICancellableEvent {
    private final ItemStack itemStack;

    public PlayerEmptyAutoAttackEvent(Player player, ItemStack itemStack) {
        super(player);
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
