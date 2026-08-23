package org.confluence.mod.mixed;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.mixed.SelfGetter;

public interface IPlayer extends SelfGetter<Player> {
    void confluence$setCurrentBait(ItemStack bait);

    ItemStack confluence$getCurrentBait();

    static IPlayer of(Player player) {
        return (IPlayer) player;
    }
}
