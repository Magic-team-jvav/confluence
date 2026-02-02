package nowebsite.makertechno.the_trackers.api.examples;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import nowebsite.makertechno.the_trackers.TheTrackers;
import nowebsite.makertechno.the_trackers.api.component.ComponentBuilder;
import nowebsite.makertechno.the_trackers.api.component.StaticComponent;
import nowebsite.makertechno.the_trackers.core.track.TrackersMonitor;
import nowebsite.makertechno.the_trackers.core.track.WorldSingletonTracker;

//@EventBusSubscriber(Dist.CLIENT)
/** An example adding death point tag to world. */
public class TestAdd1 {
    private static final ComponentBuilder.BuilderResult posTo0 = new ComponentBuilder()
            .setComponentType(ComponentBuilder.ComponentType.DIRECT)
            .setIcon1(Items.DIAMOND.getDefaultInstance())
            .setIcon1Pattern("rainbow:2")
            .defineRescale(scale -> (float) (1F + Math.min(scale / 2.2, 1f) * Math.min(scale / 2.2, 1f)))
            .setSmoothMove(true)
            .build();

    private static Vec3 pos;

    @SubscribeEvent
    public static void onScreenOpen(ScreenEvent.Opening event) {
        if (event.getScreen() instanceof DeathScreen) {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                pos = player.position();
            }
        }
    }
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!event.isEndConquered() && pos != null) {
            WorldSingletonTracker tracker = TrackersMonitor.getTracker();
            if (tracker != null) {
                StaticComponent component = tracker.addStaticPosComponent(TheTrackers.MOD_ID, "death point", posTo0, true);
                component.posUpdater(vec3 -> {
                    if (vec3.distanceTo(pos) < 5) component.close();
                    return pos;
                });
            }
        }

    }
}
