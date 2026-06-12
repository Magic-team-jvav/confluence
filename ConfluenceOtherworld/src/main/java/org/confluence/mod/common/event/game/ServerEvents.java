package org.confluence.mod.common.event.game;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.network.NetworkService;
import org.confluence.mod.common.block.functional.network.PathService;
import org.confluence.mod.common.data.saved.GlobalCloakData;
import org.confluence.mod.common.gameevent.GameEventSystem;
import org.confluence.mod.common.worldgen.TheEndBiomeHolder;
import org.confluence.mod.util.OverworldUtils;

@Mod.EventBusSubscriber(modid = Confluence.MODID)
public final class ServerEvents {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void serverAboutToStart(ServerAboutToStartEvent event) {
        PathService.INSTANCE.onServerStart();
        NetworkService.INSTANCE.onServerStart();
        MinecraftServer server = event.getServer();
        TheEndBiomeHolder.open(server);
        OverworldUtils.open(server);
    }

    @SubscribeEvent
    public static void serverStarted(ServerStartedEvent event) {
        GlobalCloakData.INSTANCE.fix(OverworldUtils.getLevel(event.getServer()));
        GameEventSystem.INSTANCE.open(event.getServer());
    }

    @SubscribeEvent
    public static void serverStopping(ServerStoppingEvent event) {
        GameEventSystem.INSTANCE.close(event.getServer());
        TheEndBiomeHolder.close();
        OverworldUtils.close();
    }

    @SubscribeEvent
    public static void serverStopped(ServerStoppedEvent event) {
        PathService.INSTANCE.onServerStop();
        NetworkService.INSTANCE.onServerStop();
    }
}
