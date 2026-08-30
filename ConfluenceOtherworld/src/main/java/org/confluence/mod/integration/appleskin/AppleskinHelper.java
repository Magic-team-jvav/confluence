package org.confluence.mod.integration.appleskin;

import net.neoforged.neoforge.common.NeoForge;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.gui.hud.TerraStyleFoodHud;
import squeek.appleskin.api.event.HUDOverlayEvent;

import java.util.function.Consumer;

public class AppleskinHelper {
    public static final boolean IS_LOADED = LibUtils.isModLoaded("appleskin");

    public static void addListeners() {
        if (IS_LOADED) {
            NeoForge.EVENT_BUS.addListener((Consumer<HUDOverlayEvent.Exhaustion>) event -> {
                if (ClientConfigs.terraStyleFood && ClientConfigs.foodStyle == TerraStyleFoodHud.Food.LEGACY) {
                    event.setCanceled(true);
                }
            });
            NeoForge.EVENT_BUS.addListener((Consumer<HUDOverlayEvent.Saturation>) event -> {
                if (ClientConfigs.terraStyleFood) {
                    event.setCanceled(true);
                }
            });
            NeoForge.EVENT_BUS.addListener((Consumer<HUDOverlayEvent.HungerRestored>) event -> {
                if (ClientConfigs.terraStyleFood && ClientConfigs.foodStyle == TerraStyleFoodHud.Food.LEGACY) {
                    event.setCanceled(true);
                }
            });
            NeoForge.EVENT_BUS.addListener((Consumer<HUDOverlayEvent.HealthRestored>) event -> {
                if (ClientConfigs.terraStyleFood && ClientConfigs.foodStyle == TerraStyleFoodHud.Food.LEGACY) {
                    event.setCanceled(true);
                }
            });
        }
    }
}
