package org.confluence.mod;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.api.event.GunEvent;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.ModKeyBindings;
import org.confluence.mod.client.event.gun.GunClientEvents;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.component.prefix.ModPrefix;
import org.confluence.mod.common.event.gun.GunEvents;
import org.confluence.mod.common.init.*;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.integration.terra_entity.TEEvents;
import org.confluence.mod.integration.terra_entity.init.ModTradeLockProviderTypes;
import org.confluence.mod.integration.terra_furniture.TFReferences;
import org.mesdag.portlib.network.PortNetworkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Confluence.MODID)
public final class Confluence {
    public static final String MODID = ConfluenceMagicLib.CONFLUENCE_ID;
    public static final Logger LOGGER = LoggerFactory.getLogger("Confluence");
    public static GameRules.Key<GameRules.IntegerValue> SPREADABLE_CHANCE;
    public static final PortNetworkHandler NETWORK_HANDLER = new PortNetworkHandler(MODID, "1");

    public Confluence(FMLJavaModLoadingContext context) {
        IEventBus eventBus = context.getModEventBus();
        StartupConfigs.register();
        CommonConfigs.register(context);
        GunEvents.init();
        if (LibUtils.isPhysicalClient()) {
            ClientConfigs.register(context);
            ModKeyBindings.init();
            GunClientEvents.init();
//            container.registerExtensionPoint(IConfigScreenFactory.class, MergedConfigurationScreen::factory);
        }

        TEEvents.register(eventBus);
        ModBlocks.register(eventBus);
        ModItems.register(eventBus);
        ModVillagers.register(eventBus);
        ModRecipes.register(eventBus);
        ModFeatures.register(eventBus);
        ModEnchantments.register(eventBus);
        ModAdvancements.register(eventBus);

        ModCustomRegistries.init();
        ModTrackTypeProviderTypes.init();
        ModGenerationProviderTypes.init();
        TFReferences.init();
        ModFluids.initialize();
        ModPrefix.initialize();

        ModTabs.TABS.register(eventBus);
        ModEntities.ENTITIES.register(eventBus);
        ModDataComponentTypes.TYPES.register(eventBus);
        ModSoundEvents.EVENTS.register(eventBus);
        ModAttachmentTypes.TYPES.register(eventBus);
        ModEffects.EFFECTS.register(eventBus);
        ModMenuTypes.TYPES.register(eventBus);
        ModParticleTypes.TYPES.register(eventBus);
        ModChunkGenerators.GENERATORS.register(eventBus);
        ModCarvers.CARVERS.register(eventBus);
        ModStructures.TYPES.register(eventBus);
        ModLootTables.ItemConditions.TYPES.register(eventBus);
        ModTradeLockProviderTypes.TYPES.register(eventBus);
        ModCommands.ARGUMENT_TYPE_INFOS.register(eventBus);
        ModDensityFunctionTypes.TYPES.register(eventBus);

        ModSoulSkills.register(eventBus);
    }

    public static void registerGameRules() {
        if (SPREADABLE_CHANCE == null) {
            SPREADABLE_CHANCE = GameRules.register("confluenceSpreadableChance", GameRules.Category.UPDATES, GameRules.IntegerValue.create(10, 0, 100, (server, value) -> {}));
        }
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static String asPlainId(String path) {
        return MODID + ':' + path;
    }

    public static <T> ResourceKey<T> asResourceKey(ResourceKey<? extends Registry<T>> registryKey, String path) {
        return ResourceKey.create(registryKey, asResource(path));
    }

    public static <T> ResourceKey<Registry<T>> asResourceKey(String path) {
        return ResourceKey.createRegistryKey(asResource(path));
    }

    public static <T> TagKey<T> asTagKey(ResourceKey<Registry<T>> registryKey, String path) {
        return TagKey.create(registryKey, asResource(path));
    }
}
