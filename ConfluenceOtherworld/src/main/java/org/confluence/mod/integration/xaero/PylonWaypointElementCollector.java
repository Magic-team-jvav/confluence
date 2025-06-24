package org.confluence.mod.integration.xaero;

import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.core.IPlayerWaystoneData;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.client.Minecraft;
import org.confluence.mod.Confluence;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PylonWaypointElementCollector {
    private Map<UUID, PylonWaypointElement> elements = new HashMap<>();

    public Iterable<PylonWaypointElement> getElements() {
        return elements.values();
    }

    public void update(Minecraft minecraft) {
        if (elements == null) {
            this.elements = new HashMap<>();
        }

        Map<UUID, PylonWaypointElement> neoElements = null;
        IPlayerWaystoneData playerWaystoneData = PlayerWaystoneManager.getPlayerWaystoneData(minecraft.level);
        for (Waystone waystone : playerWaystoneData.getWaystones(minecraft.player)) {
            if (Confluence.MODID.equals(waystone.getWaystoneType().getNamespace())) {
                if (neoElements == null) neoElements = new HashMap<>();
                neoElements.put(waystone.getWaystoneUid(), new PylonWaypointElement(waystone));
            }
        }
        if (neoElements != null) this.elements = neoElements;
    }
}
