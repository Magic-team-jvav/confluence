package org.confluence.mod.api.event;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

public class GetCustomDiggingPowerEvent extends Event {
    private final ItemStack itemStack;
    private int power;

    public GetCustomDiggingPowerEvent(ItemStack itemStack, int power) {
        this.itemStack = itemStack;
        this.power = power;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getPower() {
        return power;
    }
}
