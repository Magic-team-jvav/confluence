package org.confluence.mod.client.gameevent;

import net.minecraft.world.entity.player.Player;

@FunctionalInterface
public interface GameEventSyncCallback {
    void call(Player player, boolean start);
}
