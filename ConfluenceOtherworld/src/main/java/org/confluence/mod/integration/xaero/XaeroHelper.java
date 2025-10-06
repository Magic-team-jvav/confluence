package org.confluence.mod.integration.xaero;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.mod.integration.waystones.WaystonesHelper;
import org.confluence.mod.network.c2s.WormholeToPlayerPacketC2S;
import xaero.map.radar.tracker.PlayerTrackerMapElement;

import java.util.UUID;

public class XaeroHelper {
    public static final boolean IS_LOADED = ModList.get().isLoaded("xaeroworldmap");
    public static final String versionRange = "[1.39.9,)";
    private static Object collector;

    public static Object getCollector() {
        if (collector == null) {
            collector = new PylonWaypointElementCollector();
        }
        return collector;
    }

    public static void tick(LocalPlayer player) {
        if (IS_LOADED && WaystonesHelper.IS_LOADED) {
            ((PylonWaypointElementCollector) getCollector()).update(player);
        }
    }

    public static boolean teleport(Object o) {
        UUID uuid = null;
        if (o instanceof RemotePlayer player) {
            uuid = player.getUUID();
        } else if (o instanceof PlayerTrackerMapElement<?> element) {
            uuid = element.getPlayerId();
        }
        if (uuid != null) {
            PacketDistributor.sendToServer(new WormholeToPlayerPacketC2S(uuid, WormholeToPlayerPacketC2S.ByMod.XAEROS_MAP));
            return true;
        }
        return false;
    }
}
