package org.confluence.mod;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.data.fixer.RegistriesFixer;
import org.confluence.mod.common.init.*;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.integration.terra_entity.TEEvents;
import org.confluence.mod.integration.waystones.WaystonesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Confluence.MODID)
public class Confluence {
    public static final String MODID = ConfluenceMagicLib.CONFLUENCE_ID;
    public static final Logger LOGGER = LoggerFactory.getLogger("Confluence");
    public static GameRules.Key<GameRules.IntegerValue> SPREADABLE_CHANCE;

    public Confluence(IEventBus eventBus, ModContainer container) {
        StartupConfigs.register(container);
        CommonConfigs.register(container);
        if (FMLEnvironment.dist.isClient()) {
            ClientConfigs.register(container);
            container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }
        TEEvents.init(eventBus);
        ModBlocks.register(eventBus);
        ModItems.register(eventBus);
        ModVillagers.register(eventBus);
        ModRecipes.register(eventBus);
        ModFeatures.register(eventBus);
        ModFluids.initialize();
        ModCriterionTriggers.TRIGGERS.register(eventBus);
        ModTabs.TABS.register(eventBus);
        ModEntities.ENTITIES.register(eventBus);
        ModDataComponentTypes.TYPES.register(eventBus);
        ModSoundEvents.EVENTS.register(eventBus);
        ModAttachmentTypes.TYPES.register(eventBus);
        ModEffects.EFFECTS.register(eventBus);
        ModJukeboxSongs.SONGS.register(eventBus);
        ModMenuTypes.TYPES.register(eventBus);
        ModParticleTypes.TYPES.register(eventBus);
        ModChunkGenerators.GENERATORS.register(eventBus);
        ModCarvers.CARVERS.register(eventBus);
        ModStructures.TYPES.register(eventBus);
        ModEquipmentSets.SETS.register(eventBus);
        ModHookTypes.TYPES.register(eventBus);
        ModLootTables.ItemConditions.TYPES.register(eventBus);
        RegistriesFixer.init();
        WaystonesHelper.register(eventBus);
    }

    public static void registerGameRules() {
        if (SPREADABLE_CHANCE == null) {
            SPREADABLE_CHANCE = GameRules.register("confluenceSpreadableChance", GameRules.Category.UPDATES, GameRules.IntegerValue.create(10, 0, 100, (server, value) -> {}));
        }
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static String asDescriptionId(String path) {
        return MODID + "." + path;
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
