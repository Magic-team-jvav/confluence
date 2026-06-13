package org.confluence.mod.common.event.game;

import net.minecraft.server.MinecraftServer;
import org.confluence.mod.common.block.functional.network.NetworkService;
import org.confluence.mod.common.block.functional.network.PathService;
import org.confluence.mod.common.data.saved.GlobalCloakData;
import org.confluence.mod.common.gameevent.GameEventSystem;
import org.confluence.mod.common.worldgen.TheEndBiomeHolder;
import org.confluence.mod.util.OverworldUtils;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.event.PortEventPriority;
import org.mesdag.portlib.event.server.PortServerAboutToStartEvent;
import org.mesdag.portlib.event.server.PortServerStartedEvent;
import org.mesdag.portlib.event.server.PortServerStoppedEvent;
import org.mesdag.portlib.event.server.PortServerStoppingEvent;

public final class ServerEvents {
    public static void init() {
        PortEventHandler.addListener(PortEventPriority.LOWEST, ServerEvents::serverAboutToStart);
        PortEventHandler.addListener(ServerEvents::serverStarted);
        PortEventHandler.addListener(ServerEvents::serverStopping);
        PortEventHandler.addListener(ServerEvents::serverStopped);
    }

    public static void serverAboutToStart(PortServerAboutToStartEvent event) {
        PathService.INSTANCE.onServerStart();
        NetworkService.INSTANCE.onServerStart();
        MinecraftServer server = event.getServer();
        TheEndBiomeHolder.open(server);
        OverworldUtils.open(server);
    }

    public static void serverStarted(PortServerStartedEvent event) {
        GlobalCloakData.INSTANCE.fix(OverworldUtils.getLevel(event.getServer()));
        GameEventSystem.INSTANCE.open(event.getServer());
    }

    public static void serverStopping(PortServerStoppingEvent event) {
        GameEventSystem.INSTANCE.close(event.getServer());
        TheEndBiomeHolder.close();
        OverworldUtils.close();
    }

    public static void serverStopped(PortServerStoppedEvent event) {
        PathService.INSTANCE.onServerStop();
        NetworkService.INSTANCE.onServerStop();
    }
}
