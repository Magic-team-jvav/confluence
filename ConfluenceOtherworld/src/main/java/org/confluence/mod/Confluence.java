package org.confluence.mod;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.worldgen.biome.DynamicBiomeUtils;
import org.confluence.lib.network.IPacket;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.gui.MergedConfigurationScreen;
import org.confluence.mod.client.summoner.SummonerClientEvents;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.component.prefix.ModPrefix;
import org.confluence.mod.common.init.*;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.summoner.SummonerEvents;
import org.confluence.mod.common.summoner.register.*;
import org.confluence.mod.integration.ageratum.AgeratumHelper;
import org.confluence.mod.integration.create.CreateHelper;
import org.confluence.mod.integration.terra_furniture.TFReferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Confluence.MODID)
public final class Confluence {
    public static final String MODID = ConfluenceMagicLib.CONFLUENCE_ID;
    public static final Logger LOGGER = LoggerFactory.getLogger("Confluence");
    public static GameRules.Key<GameRules.IntegerValue> SPREADABLE_CHANCE;

    // todo 1.3.0
    public static final boolean SOUL_SKILLS = false;
    public static final boolean THE_END_BIOMES = false;
    public static final boolean UNRELEASED_SPAWNS = LibUtils.isDev();

    private static void registerEntityAliases() {
        BuiltInRegistries.ENTITY_TYPE.addAlias(asResource("arrow_projectile"), asResource("arrow"));
        BuiltInRegistries.ENTITY_TYPE.addAlias(asResource("bee_arrow_projectile"), asResource("bee_arrow"));
        BuiltInRegistries.ENTITY_TYPE.addAlias(asResource("hell_bat_arrow_projectile"), asResource("hell_bat_arrow"));
        BuiltInRegistries.ENTITY_TYPE.addAlias(asResource("ice_blade_sword_projectile"), asResource("ice_blade_sword"));
        BuiltInRegistries.ENTITY_TYPE.addAlias(asResource("star_fury_projectile"), asResource("star_fury"));
        BuiltInRegistries.ENTITY_TYPE.addAlias(asResource("enchanted_sword_projectile"), asResource("enchanted_sword"));
        BuiltInRegistries.ENTITY_TYPE.addAlias(asResource("lights_bane_projectile"), asResource("lights_bane"));
        BuiltInRegistries.ENTITY_TYPE.addAlias(asResource("grass_projectile"), asResource("grass"));
        BuiltInRegistries.ENTITY_TYPE.addAlias(asResource("bee_projectile"), asResource("bee"));
        BuiltInRegistries.ENTITY_TYPE.addAlias(asResource("nights_edge_projectile"), asResource("nights_edge"));
        BuiltInRegistries.ENTITY_TYPE.addAlias(asResource("flower_projectile"), asResource("flower_power_petal"));
    }

    public Confluence(IEventBus eventBus, ModContainer container) {
        registerEntityAliases();
        ModDynamicBiomes.init();
        ModMiniBiomes.init();
        DynamicBiomeUtils.enable();
        StartupConfigs.register(container);
        CommonConfigs.register(container);
        if (LibUtils.isPhysicalClient()) {
            ClientConfigs.register(container);
            SummonerClientEvents.init(eventBus);
            container.registerExtensionPoint(IConfigScreenFactory.class, MergedConfigurationScreen::factory);
        }

        ModBlocks.register(eventBus);
        ModItems.register(eventBus);
        ModVillagers.register(eventBus);
        ModRecipes.register(eventBus);
        ModFeatures.register(eventBus);
        ModEnchantments.register(eventBus);
        AgeratumHelper.register(eventBus);
        CreateHelper.register(eventBus);
        ModAdvancements.register(eventBus);

        TFReferences.init();
        ModFluids.initialize();
        ModPrefix.initialize();

        ModTabs.TABS.register(eventBus);
        ModEntities.register(eventBus);
        ModCustomRegistries.register(eventBus);
        ModDataComponentTypes.TYPES.register(eventBus);
        ModSoundEvents.EVENTS.register(eventBus);
        ModAttachmentTypes.TYPES.register(eventBus);
        { // todo 合并注册
            SummonerAttachmentTypes.register(eventBus);
            SummonerRegistries.register(eventBus);
            SummonerAttachmentEntityTypes.register(eventBus);
            SummonerSoundEvents.register(eventBus);
            SummonerSummonMarks.init();
            SummonerEvents.init();
            SummonerParticleTypes.TYPES.register(eventBus);
        }
        ModEffects.EFFECTS.register(eventBus);
        ModMenuTypes.TYPES.register(eventBus);
        ModParticleTypes.TYPES.register(eventBus);
        ModChunkGenerators.GENERATORS.register(eventBus);
        ModCarvers.CARVERS.register(eventBus);
        ModStructures.TYPES.register(eventBus);
        ModLootTables.ItemConditions.TYPES.register(eventBus);
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

    public static <P extends IPacket> CustomPacketPayload.Type<P> createType(String id) {
        return new CustomPacketPayload.Type<>(asResource(id));
    }
}
