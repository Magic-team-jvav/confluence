package nowebsite.makertechno.the_trackers.core.event;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import nowebsite.makertechno.the_trackers.TheTrackers;
import nowebsite.makertechno.the_trackers.core.config.TConfig;
import nowebsite.makertechno.the_trackers.core.track.TrackersMonitor;

@EventBusSubscriber(modid = TheTrackers.MOD_ID, value = Dist.CLIENT)
public class TModClientGame {
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        if (TConfig.available) TrackersMonitor.trigger();
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        TrackersMonitor.quit();
    }
}
