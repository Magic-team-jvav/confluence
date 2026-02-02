package org.confluence.terraentity.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.confluence.terraentity.entity.proj.YoyosEntity;

public class YoyosThrowingEvent extends PlayerEvent {
    private final ItemStack yoyosItem;
    private final YoyosEntity yoyosEntity;

    public YoyosThrowingEvent(Player player, ItemStack yoyosItem, YoyosEntity yoyosEntity) {
        super(player);
        this.yoyosItem = yoyosItem;
        this.yoyosEntity = yoyosEntity;
    }

    public ItemStack getYoyosItem() {
        return yoyosItem;
    }

    public YoyosEntity getYoyosEntity() {
        return yoyosEntity;
    }
}
