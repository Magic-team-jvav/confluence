package org.confluence.mod.mixed;

import net.minecraft.world.entity.player.Player;
import org.confluence.lib.mixed.SelfGetter;

public interface IPlayer extends SelfGetter<Player> {
    boolean confluence$isVoidSeaSwimming();

    static IPlayer of(Player player) {
        return (IPlayer) player;
    }
}
