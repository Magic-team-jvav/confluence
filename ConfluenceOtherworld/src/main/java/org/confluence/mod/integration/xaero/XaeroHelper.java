package org.confluence.mod.integration.xaero;

import net.minecraft.client.Minecraft;
import net.neoforged.fml.ModList;
import org.confluence.mod.integration.waystones.WaystonesHelper;

public class XaeroHelper {
    public static final boolean IS_LOADED = ModList.get().isLoaded("xaeroworldmap");
    private static Object collector;

    public static Object getCollector() {
        if (collector == null) {
            collector = new PylonWaypointElementCollector();
        }
        return collector;
    }

    public static void tick(Minecraft minecraft) {
        if (IS_LOADED && WaystonesHelper.IS_LOADED) {
            ((PylonWaypointElementCollector) getCollector()).update(minecraft);
        }
    }
}
