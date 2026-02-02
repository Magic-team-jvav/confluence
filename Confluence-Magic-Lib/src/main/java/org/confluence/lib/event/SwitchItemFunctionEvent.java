package org.confluence.lib.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public abstract class SwitchItemFunctionEvent extends PlayerEvent {
    private final ItemStack stack;

    public SwitchItemFunctionEvent(Player player, ItemStack stack) {
        super(player);
        this.stack = stack;
    }

    public ItemStack getStack() {
        return stack;
    }

    public static class Pre extends SwitchItemFunctionEvent implements ICancellableEvent {
        public Pre(Player player, ItemStack stack) {
            super(player, stack);
        }
    }

    public static class Post extends SwitchItemFunctionEvent {
        private final boolean enabled;

        public Post(Player player, ItemStack stack, boolean enabled) {
            super(player, stack);
            this.enabled = enabled;
        }

        public boolean isEnabled() {
            return enabled;
        }
    }
}
