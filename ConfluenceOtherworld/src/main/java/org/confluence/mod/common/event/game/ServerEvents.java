package org.confluence.mod.common.event.game;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.network.NetworkService;
import org.confluence.mod.common.block.functional.network.PathService;
import org.confluence.mod.common.data.saved.GlobalCloakData;
import org.confluence.mod.common.gameevent.GameEventSystem;
import org.confluence.mod.util.OverworldUtils;

@EventBusSubscriber(modid = Confluence.MODID)
public final class ServerEvents {
    @SubscribeEvent
    public static void serverAboutToStart(ServerAboutToStartEvent event) {
        PathService.INSTANCE.onServerStart();
        NetworkService.INSTANCE.onServerStart();
    }

    @SubscribeEvent
    public static void serverStarted(ServerStartedEvent event) {
        GlobalCloakData.INSTANCE.fix(OverworldUtils.getLevel(event.getServer()));
        GameEventSystem.INSTANCE.initEvents(event.getServer());
    }

    @SubscribeEvent
    public static void serverStop(ServerStoppedEvent event) {
        PathService.INSTANCE.onServerStop();
        NetworkService.INSTANCE.onServerStop();
    }
}
