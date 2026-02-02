package org.confluence.terra_furniture;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.confluence.terra_furniture.common.init.TFBlocks;
import org.confluence.terra_furniture.common.init.TFEntities;
import org.confluence.terra_furniture.common.init.TFItems;
import org.confluence.terra_furniture.common.init.TFRegistries;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(TerraFurniture.MODID)
public class TerraFurniture {
    public static final String MODID = "terra_furniture";
    public static final Logger LOGGER = LoggerFactory.getLogger("Terra Furniture");

    public TerraFurniture(IEventBus modEventBus, ModContainer modContainer) {
        TFItems.register(modEventBus);
        TFBlocks.register(modEventBus);
        TFEntities.ENTITIES.register(modEventBus);
        TFRegistries.register(modEventBus);
    }

    @Contract("_ -> new")
    public static @NotNull ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
