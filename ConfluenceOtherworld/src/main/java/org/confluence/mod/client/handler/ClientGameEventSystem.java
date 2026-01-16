package org.confluence.mod.client.handler;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.gameevent.GameEvent;
import org.confluence.mod.common.gameevent.SlimeRainGameEvent;

public final class ClientGameEventSystem {
    public static void handle(Player player, ResourceKey<? extends GameEvent> key, boolean start) {
        if (key == SlimeRainGameEvent.KEY) {
            handleSlimeRain(player, start);
        }
    }

    public static void handleSlimeRain(Player player, boolean start) {

    }
}
