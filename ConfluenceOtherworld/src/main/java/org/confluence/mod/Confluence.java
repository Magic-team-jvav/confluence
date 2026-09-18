package org.confluence.mod;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.ModKeyBindings;
import org.confluence.mod.client.effect.EctoMistHelper;
import org.confluence.mod.client.event.GameClientEvents;
import org.confluence.mod.client.event.ModClientEvents;
import org.confluence.mod.client.gui.MergedConfigurationScreen;
import org.confluence.mod.client.summoner.SummonerClientEvents;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.advancement.ShimmerTransmutationTrigger;
import org.confluence.mod.common.component.prefix.ModPrefix;
import org.confluence.mod.common.event.ItemGroupEvents;
import org.confluence.mod.common.event.ModEvents;
import org.confluence.mod.common.event.NetworkEvents;
import org.confluence.mod.common.event.game.*;
import org.confluence.mod.common.event.game.entity.EntityEvents;
import org.confluence.mod.common.event.game.entity.ItemEvents;
import org.confluence.mod.common.event.game.entity.LivingEntityEvents;
import org.confluence.mod.common.event.game.entity.PlayerEvents;
import org.confluence.mod.common.init.*;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.summoner.SummonerAttachmentTypes;
import org.confluence.mod.common.summoner.SummonerEvents;
import org.confluence.mod.common.summoner.SummonerRegistries;
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

    // todo 1.3.0
    public static final boolean SOUL_SKILLS = false;
    public static final boolean THE_END_BIOMES = false;

    public Confluence(FMLJavaModLoadingContext context) {
        IEventBus eventBus = context.getModEventBus();
        StartupConfigs.register();
        CommonConfigs.register(context);
        NetworkEvents.init();
        ModEvents.init();
        SummonerRegistries.init();
        SummonerAttachmentTypes.init();
        SummonerEvents.init();
        ModDataMaps.init();
        EntityEvents.init();
        ItemEvents.init();
        LivingEntityEvents.init();
        PlayerEvents.init();
        GameEvents.init();
        GunEvents.init();
        LevelEvents.init();
        TickEvents.init();
        ItemGroupEvents.init();
        ServerEvents.init();
        if (LibUtils.isPhysicalClient()) {
            ClientConfigs.register(context);
            ModKeyBindings.init();
            ModClientEvents.init();
            SummonerClientEvents.init();
            EctoMistHelper.init();
            GameClientEvents.init();
            context.registerExtensionPoint(
                    ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new ConfigScreenHandler.ConfigScreenFactory(MergedConfigurationScreen::factory)
            );
        }

        ModBlocks.init(eventBus);
        ModItems.init();
        ModVillagers.register(eventBus);
        ModRecipes.register(eventBus);
        ModFeatures.register(eventBus);
        ModEnchantments.ENCHANTMENTS.register(eventBus);
        CriteriaTriggers.register(ShimmerTransmutationTrigger.INSTANCE);

        ModCustomRegistries.register(eventBus);
        TFReferences.init();
        ModFluids.initialize();
        ModPrefix.initialize();

        ModTabs.TABS.register(eventBus);
        ModEntities.register(eventBus);
        ModDataComponentTypes.init();
        ModSoundEvents.EVENTS.register(eventBus);
        ModAttachmentTypes.init();
        ModEffects.EFFECTS.register(eventBus);
        ModMenuTypes.TYPES.register(eventBus);
        ModParticleTypes.init();
        ModChunkGenerators.GENERATORS.register(eventBus);
        ModCarvers.CARVERS.register(eventBus);
        ModStructures.TYPES.register(eventBus);
        ModLootTables.ItemConditions.TYPES.register(eventBus);
        ModCommands.ARGUMENT_TYPE_INFOS.register(eventBus);
        ModDensityFunctionTypes.TYPES.register(eventBus);

//        MixinEnvironment.getCurrentEnvironment().setOption(MixinEnvironment.Option.DUMP_TARGET_ON_FAILURE, true);
    }

    public static void registerGameRules() {
        if (SPREADABLE_CHANCE == null) {
            SPREADABLE_CHANCE = GameRules.register("confluenceSpreadableChance", GameRules.Category.MISC, new GameRules.Type<>(
                    () -> IntegerArgumentType.integer(0, 100),
                    type -> new GameRules.IntegerValue(type, 10),
                    (server, value) -> {},
                    GameRules.GameRuleTypeVisitor::visitInteger
            ));
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
