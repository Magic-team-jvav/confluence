package nowebsite.makertechno.the_trackers;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import nowebsite.makertechno.the_trackers.core.config.TConfig;
import nowebsite.makertechno.the_trackers.data.datagen.ModDataGenerators;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(value = TheTrackers.MOD_ID, dist = Dist.CLIENT)
public class TheTrackers {
    public static final String MOD_ID = "the_trackers";
    public static final Logger LOGGER = LoggerFactory.getLogger("TheTrackers");

    public TheTrackers(@NotNull IEventBus modEventBus, @NotNull ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, TConfig.SPEC);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modEventBus.addListener(ModDataGenerators::gatherData);
    }

}
