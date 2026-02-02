package org.confluence.terra_guns;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import org.confluence.lib.ConfluenceMagicLib;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.confluence.terra_guns.common.init.TGAttributes.ATTRIBUTES;
import static org.confluence.terra_guns.common.init.TGDataComponents.DATA_COMPONENTS;
import static org.confluence.terra_guns.common.init.TGEntities.ENTITY_TYPES;
import static org.confluence.terra_guns.common.init.TGItems.*;
import static org.confluence.terra_guns.common.init.TGMobEffects.MOB_EFFECTS;
import static org.confluence.terra_guns.common.init.TGSoundEvents.SOUNDS;
import static org.confluence.terra_guns.common.init.TGTabs.TABS;

@Mod(TerraGuns.MODID)
public class TerraGuns {
    public static final String MODID = "terra_guns";
    public static final Logger LOGGER = LoggerFactory.getLogger("Terra Guns");
    public static final boolean IS_CONFLUENCE_LOADED = ModList.get().isLoaded("confluence");

    public TerraGuns(IEventBus modEventBus, ModContainer modContainer) {
        ATTRIBUTES.register(modEventBus);
        DATA_COMPONENTS.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);
        GUNS.register(modEventBus);
        BULLETS.register(modEventBus);
        OTHER.register(modEventBus);
        MOB_EFFECTS.register(modEventBus);
        SOUNDS.register(modEventBus);
        if (!ConfluenceMagicLib.IS_CONFLUENCE_LOADED.get()) {
            TABS.register(modEventBus);
        }
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static String asResourceString(String path) {
        return asResource(path).toString();
    }
}
