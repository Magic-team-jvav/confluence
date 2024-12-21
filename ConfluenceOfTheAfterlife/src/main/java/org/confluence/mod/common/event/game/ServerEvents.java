package org.confluence.mod.common.event.game;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.network.NetworkService;
import org.confluence.mod.common.block.functional.network.PathService;
import org.confluence.mod.mixed.IWorldOptions;
import org.confluence.mod.network.s2c.SecretFlagSyncPacketS2C;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = Confluence.MODID)
public final class ServerEvents {
    @SubscribeEvent
    public static void serverAboutToStart(ServerAboutToStartEvent event) {
        PathService.INSTANCE.onServerStart();
        NetworkService.INSTANCE.onServerStart();
        SecretFlagSyncPacketS2C.sendToAll(((IWorldOptions) event.getServer().getWorldData().worldGenOptions()).confluence$getSecretFlag());
    }

    @SubscribeEvent
    public static void serverStop(ServerStoppedEvent event) {
        PathService.INSTANCE.onServerStop();
        NetworkService.INSTANCE.onServerStop();
        SecretFlagSyncPacketS2C.sendToAll(0L);
    }
}
