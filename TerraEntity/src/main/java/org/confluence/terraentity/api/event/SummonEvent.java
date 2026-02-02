package org.confluence.terraentity.api.event;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.confluence.terraentity.api.entity.ISummonMob;

public class SummonEvent extends PlayerEvent {
    private final ItemStack itemStack;
    private final ISummonMob summon;

    public SummonEvent(Player player, ItemStack itemStack, ISummonMob summon) {
        super(player);
        this.itemStack = itemStack;
        this.summon = summon;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public ISummonMob getSummonMob() {
        return summon;
    }

    public static class Pre<T extends Mob> extends PlayerEvent implements ICancellableEvent {
        private final ItemStack itemStack;
        private final EntityType<T> summonType;

        public Pre(Player player, ItemStack itemStack, EntityType<T> summonType) {
            super(player);
            this.itemStack = itemStack;
            this.summonType = summonType;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }

        public EntityType<T> getSummonType() {
            return summonType;
        }
    }
}
