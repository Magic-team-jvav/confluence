package org.confluence.mod.common.event.game;

import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.network.NetworkService;
import org.confluence.mod.common.block.functional.network.PathService;
import org.confluence.mod.common.data.saved.GlobalCloakData;
import org.confluence.mod.common.gameevent.GameEventSystem;
import org.confluence.mod.common.worldgen.TheEndBiomeHolder;
import org.confluence.mod.common.worldgen.biome.injector.ConfluenceBiomeInjector;
import org.confluence.mod.util.OverworldUtils;

@EventBusSubscriber(modid = Confluence.MODID)
public final class ServerEvents {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void serverAboutToStart(ServerAboutToStartEvent event) {
        PathService.INSTANCE.onServerStart();
        NetworkService.INSTANCE.onServerStart();
        MinecraftServer server = event.getServer();
        if (LibUtils.isDev()) {
            TheEndBiomeHolder.open(server);
        }
        OverworldUtils.open(server);
        // 必须在 loadLevel() 之前完成：原版第一次读取 possibleBiomes() 发生在
        // createLevels() 里，晚于本事件。详见 ConfluenceBiomeInjector#install。
        ConfluenceBiomeInjector.install(server);
    }

    @SubscribeEvent
    public static void serverStarted(ServerStartedEvent event) {
        GlobalCloakData.INSTANCE.fix(OverworldUtils.getLevel(event.getServer()));
        GameEventSystem.INSTANCE.open(event.getServer());
    }

    @SubscribeEvent
    public static void serverStopping(ServerStoppingEvent event) {
        GameEventSystem.INSTANCE.close(event.getServer());
        if (LibUtils.isDev()) {
            TheEndBiomeHolder.close();
        }
        ConfluenceBiomeInjector.uninstall();
        OverworldUtils.close();
    }

    @SubscribeEvent
    public static void serverStopped(ServerStoppedEvent event) {
        PathService.INSTANCE.onServerStop();
        NetworkService.INSTANCE.onServerStop();
    }
}
