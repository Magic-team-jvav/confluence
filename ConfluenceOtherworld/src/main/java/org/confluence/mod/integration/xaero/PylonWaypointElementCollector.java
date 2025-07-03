package org.confluence.mod.integration.xaero;

import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.minecraft.client.player.LocalPlayer;
import org.confluence.mod.Confluence;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PylonWaypointElementCollector {
    private Map<UUID, PylonWaypointElement> elements = new HashMap<>();

    public Iterable<PylonWaypointElement> getElements() {
        return elements.values();
    }

    public void update(LocalPlayer player) {
        Map<UUID, PylonWaypointElement> neoElements = null;
        for (Waystone waystone : WaystonesAPI.getActivatedWaystones(player)) {
            if (Confluence.MODID.equals(waystone.getWaystoneType().getNamespace())) {
                if (neoElements == null) neoElements = new HashMap<>();
                neoElements.put(waystone.getWaystoneUid(), new PylonWaypointElement(waystone));
            }
        }
        this.elements = Objects.requireNonNullElseGet(neoElements, HashMap::new);
    }
}
