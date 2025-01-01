package org.confluence.terraentity;


import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.confluence.terraentity.init.*;
import org.slf4j.Logger;

@Mod(TerraEntity.MODID)
public class TerraEntity {
    public static final String MODID = "terra_entity";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static ResourceLocation space(String path) {return ResourceLocation.fromNamespaceAndPath(MODID, path);}


    public TerraEntity (IEventBus modEventBus, ModContainer modContainer) {
        TEEntities.ENTITIES.register(modEventBus);
        TESounds.SOUNDS.register(modEventBus);
        TEParticles.PARTICLES.register(modEventBus);
        TEItems.SPAWN_EGGS.register(modEventBus);
        TEItems.TABS.register(modEventBus);
        TEEffects.EFFECTS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static ResourceLocation asResource(String id, String path) {
        return ResourceLocation.fromNamespaceAndPath(id, path);
    }
}
