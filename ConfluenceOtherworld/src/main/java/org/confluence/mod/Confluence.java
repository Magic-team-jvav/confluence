package org.confluence.mod;

import net.minecraft.core.Registry;
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
import org.confluence.lib.common.recipe.EnvironmentLevelAccess;
import org.confluence.lib.common.worldgen.biome.DynamicBiomeUtils;
import org.confluence.lib.network.IPacket;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.gui.MergedConfigurationScreen;
import org.confluence.mod.client.summoner.SummonerClientEvents;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.component.prefix.ModPrefix;
import org.confluence.mod.common.data.TranslationConsumer;
import org.confluence.mod.common.init.*;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.init.item.SoulItems;
import org.confluence.mod.common.summoner.SummonerEvents;
import org.confluence.mod.common.summoner.register.*;
import org.confluence.mod.common.worldgen.secret_seed.ForTheWorthy;
import org.confluence.mod.integration.ageratum.AgeratumHelper;
import org.confluence.mod.integration.create.CreateHelper;
import org.confluence.mod.integration.irons_spell.IronSpellHelper;
import org.confluence.mod.integration.terra_furniture.TFReferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Mod(Confluence.MODID)
public final class Confluence {
    public static final String MODID = ConfluenceMagicLib.CONFLUENCE_ID;
    public static final List<Consumer<TranslationConsumer>> LANGUAGE_PROVIDERS = new ArrayList<>();
    public static final Logger LOGGER = LoggerFactory.getLogger("Confluence");
    public static GameRules.Key<GameRules.IntegerValue> SPREADABLE_CHANCE;

    // todo 1.3.0
    /// 魂师总开关：控制施法、灵魂/魔力模式切换、法器与技能同步等全部魂师机制。
    ///
    /// 原先跟随 `LibUtils.isDev()`，打包（production）环境里整个魂师玩法不可用；
    /// 现已解封为常开（含打包环境）。需要临时关掉时把这里改回 `LibUtils.isDev()`。
    public static final boolean SOUL_SKILLS = true;
    public static final boolean THE_END_BIOMES = LibUtils.isDev();
    public static final boolean UNRELEASED_SPAWNS = LibUtils.isDev();
    public static final boolean UNRELEASED_EVENTS = LibUtils.isDev();

    public Confluence(IEventBus eventBus, ModContainer container) {
        {
            LibUtils.setForTheWorthyPredicate(ForTheWorthy::isEnabled);
            EnvironmentLevelAccess.setGraveyardPredicate((player, level, pos) -> ModBlockCounters.isGraveyard(level, pos) || ModBlockCounters.isGraveyard(level, player.blockPosition()));
            LibEntityUtils.setTeamGetter(player -> PlayerSpecialData.of(player).getTeam());
        }
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
        SoulItems.register(eventBus);
        ModVillagers.register(eventBus);
        ModRecipes.register(eventBus);
        ModFeatures.register(eventBus);
        ModEnchantments.register(eventBus);
        AgeratumHelper.register(eventBus);
        CreateHelper.register(eventBus);
        IronSpellHelper.registerEvents();
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
        ModLootTables.EntryTypes.TYPES.register(eventBus);
        ModLootTables.ItemConditions.TYPES.register(eventBus);
        ModLootTables.ItemFunctions.TYPES.register(eventBus);
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
