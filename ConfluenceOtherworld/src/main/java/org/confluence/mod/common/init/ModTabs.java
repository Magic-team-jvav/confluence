package org.confluence.mod.common.init;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.item.GroupItem;
import org.confluence.lib.util.WipNotDisplayOutput;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.natural.LogBlockSet;
import org.confluence.mod.common.block.palettes.DecoBlockSet;
import org.confluence.mod.common.init.block.*;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.integration.waystones.WaystonesHelper;
import org.confluence.mod.util.EnchantmentUtils;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.common.init.TCTabs;
import org.confluence.terra_furniture.common.init.TFBlocks;
import org.confluence.terra_furniture.common.init.TFRegistries;
import org.confluence.terra_guns.common.init.TGItems;
import org.confluence.terraentity.init.TEItems;
import org.confluence.terraentity.init.item.TEBoomerangItems;
import org.confluence.terraentity.init.item.TESummonItems;
import org.confluence.terraentity.init.item.TEWhipItems;
import org.confluence.terraentity.init.item.TEYoyosItems;

import java.util.Collection;
import java.util.List;

@SuppressWarnings("unused")
public final class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Confluence.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> NATURAL_BLOCKS = TABS.register("natural_blocks",
            () -> CreativeModeTab.builder().icon(IconItems.NATURE_ICON::toStack)
                    .title(Component.translatable("creativetab.confluence.natural_blocks"))
                    .displayItems((parameters, output) -> {
                        output = new WipNotDisplayOutput(output);

                        LogBlockSet.acceptNature(output);
                        acceptAll(PotBlocks.BLOCKS, output, "pot");

                        CreativeModeTab.Output pine = GroupItem.belongsTo(NatureBlocks.PINE_LOG_BLOCKS.id, output);
                        pine.accept(NatureBlocks.PRUNED_PINE_SAPLING);
                        pine.accept(NatureBlocks.PINE_DROOPING_VINE);

                        CreativeModeTab.Output ash = GroupItem.belongsTo(NatureBlocks.ASH_LOG_BLOCKS.id, output);
                        ash.accept(NatureBlocks.ASH_BRANCHES);

                        CreativeModeTab.Output yellow_willow = GroupItem.belongsTo(NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.id, output);
                        yellow_willow.accept(NatureBlocks.YELLOW_WILLOW_DROOPING_LEAVES);

                        CreativeModeTab.Output moonglow_willow = GroupItem.belongsTo(NatureBlocks.MOONGLOW_WILLOW_LOG_BLOCKS.id, output);
                        moonglow_willow.accept(NatureBlocks.MOONGLOW_WILLOW_DROOPING_VINE);

                        CreativeModeTab.Output void_output = GroupItem.belongsTo(NatureBlocks.VOID_LOG_BLOCKS.id, output);
                        void_output.accept(NatureBlocks.SILENT_DROOPING_VINE);

                        CreativeModeTab.Output stone_tree = GroupItem.belongsTo("stone_tree", output);
                        stone_tree.accept(NatureBlocks.STONY_LOG);
                        stone_tree.accept(NatureBlocks.AMBER_BRANCHES);
                        stone_tree.accept(NatureBlocks.RUBY_BRANCHES);
                        stone_tree.accept(NatureBlocks.TOPAZ_BRANCHES);
                        stone_tree.accept(NatureBlocks.JADE_BRANCHES);
                        stone_tree.accept(NatureBlocks.DIAMOND_BRANCHES);
                        stone_tree.accept(NatureBlocks.SAPPHIRE_BRANCHES);
                        stone_tree.accept(NatureBlocks.AMETHYST_BRANCHES);
                        stone_tree.accept(NatureBlocks.RUBY_SAPLING);
                        stone_tree.accept(NatureBlocks.AMBER_SAPLING);
                        stone_tree.accept(NatureBlocks.TOPAZ_SAPLING);
                        stone_tree.accept(NatureBlocks.JADE_SAPLING);
                        stone_tree.accept(NatureBlocks.DIAMOND_SAPLING);
                        stone_tree.accept(NatureBlocks.SAPPHIRE_SAPLING);
                        stone_tree.accept(NatureBlocks.AMETHYST_SAPLING);

                        CreativeModeTab.Output natural_environment = GroupItem.belongsTo("natural_environment", output);
                        natural_environment.accept(NatureBlocks.FOREST_DROOPING_VINE);
                        natural_environment.accept(ModItems.CATTAIL);
                        natural_environment.accept(NatureBlocks.SILT_BLOCK);
                        natural_environment.accept(NatureBlocks.LIFE_CRYSTAL_BLOCK);
                        natural_environment.accept(NatureBlocks.GRANITE);
                        natural_environment.accept(NatureBlocks.GRANITE_TAPERED_BLOCK);
                        natural_environment.accept(NatureBlocks.MARBLE);
                        natural_environment.accept(NatureBlocks.MARBLE_TAPERED_BLOCK);

                        CreativeModeTab.Output corruption = GroupItem.belongsTo("corruption", output);
                        corruption.accept(NatureBlocks.CORRUPT_GRASS_BLOCK);
                        corruption.accept(NatureBlocks.EBONSTONE);
                        corruption.accept(NatureBlocks.COBBLED_EBONSTONE);
                        corruption.accept(NatureBlocks.EBONSANDSTONE);
                        corruption.accept(NatureBlocks.HARDENED_EBONSAND_BLOCK);
                        corruption.accept(NatureBlocks.EBONSAND);
                        corruption.accept(NatureBlocks.MOISTENED_EBONSAND_BLOCK);
                        corruption.accept(NatureBlocks.PURPLE_ICE);
                        corruption.accept(NatureBlocks.PURPLE_PACKED_ICE);
                        corruption.accept(NatureBlocks.EBONSAND_LAYER_BLOCK);
                        corruption.accept(NatureBlocks.CORRUPTION_THORN);
                        corruption.accept(NatureBlocks.CORRUPT_GRASS);
                        corruption.accept(NatureBlocks.CORRUPT_JUNGLE_GRASS_BLOCK);
                        corruption.accept(NatureBlocks.CORRUPT_CACTUS);
                        corruption.accept(NatureBlocks.CORRUPT_TAPERED_BLOCK);
                        corruption.accept(ModItems.EBONY_CATTAIL);
                        corruption.accept(NatureBlocks.CORRUPT_DROOPING_VINE);

                        CreativeModeTab.Output hallow = GroupItem.belongsTo("hallow", output);
                        hallow.accept(NatureBlocks.HALLOW_GRASS_BLOCK);
                        hallow.accept(NatureBlocks.HALLOW_GRASS);
                        hallow.accept(NatureBlocks.PEARLSTONE);
                        hallow.accept(NatureBlocks.COBBLED_PEARLSTONE);
                        hallow.accept(NatureBlocks.HARDENED_PEARLSAND_BLOCK);
                        hallow.accept(NatureBlocks.PEARLSANDSTONE);
                        hallow.accept(NatureBlocks.PEARLSAND);
                        hallow.accept(NatureBlocks.MOISTENED_PEARLSAND_BLOCK);
                        hallow.accept(NatureBlocks.PEARLSAND_LAYER_BLOCK);
                        hallow.accept(NatureBlocks.PINK_ICE);
                        hallow.accept(NatureBlocks.PINK_PACKED_ICE);
                        hallow.accept(NatureBlocks.HALLOW_CACTUS);
                        hallow.accept(NatureBlocks.HALLOW_TAPERED_BLOCK);
                        hallow.accept(ModItems.HALLOW_CATTAIL);
                        hallow.accept(NatureBlocks.HALLOW_DROOPING_VINE);

                        CreativeModeTab.Output crimson = GroupItem.belongsTo("crimson", output);
                        crimson.accept(NatureBlocks.CRIMSON_GRASS_BLOCK);
                        crimson.accept(NatureBlocks.CRIMSTONE);
                        crimson.accept(NatureBlocks.COBBLED_CRIMSTONE);
                        crimson.accept(NatureBlocks.HARDENED_CRIMSAND_BLOCK);
                        crimson.accept(NatureBlocks.CRIMSANDSTONE);
                        crimson.accept(NatureBlocks.CRIMSAND);
                        crimson.accept(NatureBlocks.MOISTENED_CRIMSAND_BLOCK);
                        crimson.accept(NatureBlocks.CRIMSON_THORN);
                        crimson.accept(NatureBlocks.CRIMSON_GRASS);
                        crimson.accept(NatureBlocks.CRIMSAND_LAYER_BLOCK);
                        crimson.accept(NatureBlocks.CRIMSON_JUNGLE_GRASS_BLOCK);
                        crimson.accept(NatureBlocks.RED_ICE);
                        crimson.accept(NatureBlocks.RED_PACKED_ICE);
                        crimson.accept(NatureBlocks.CRIMSON_CACTUS);
                        crimson.accept(NatureBlocks.CRIMSON_TAPERED_BLOCK);
                        crimson.accept(ModItems.CRIMSON_CATTAIL);
                        crimson.accept(NatureBlocks.CRIMSON_DROOPING_VINE);

                        CreativeModeTab.Output mushroom = GroupItem.belongsTo("mushroom", output);
                        mushroom.accept(NatureBlocks.MUSHROOM_GRASS_BLOCK);
                        mushroom.accept(NatureBlocks.MUSHROOM_PATH);
                        mushroom.accept(NatureBlocks.GLOWING_MUSHROOM_INDUSIUM_BLOCK);
                        mushroom.accept(NatureBlocks.GLOWING_MUSHROOM_STEM_BLOCK);
                        mushroom.accept(NatureBlocks.GLOWING_MUSHROOM_PILEUS_BLOCK);
                        mushroom.accept(NatureBlocks.LIFE_MUSHROOM_INDUSIUM_BLOCK);
                        mushroom.accept(NatureBlocks.LIFE_MUSHROOM_STEM_BLOCK);
                        mushroom.accept(NatureBlocks.LIFE_MUSHROOM_PILEUS_BLOCK);
                        mushroom.accept(NatureBlocks.HANGING_MYCELIUM);
                        mushroom.accept(NatureBlocks.MYCELIAL_DIRT);
                        mushroom.accept(ModItems.GLOWING_MUSHROOM_CATTAIL);
                        mushroom.accept(NatureBlocks.GLOWING_MUSHROOM_VINE);

                        CreativeModeTab.Output desert = GroupItem.belongsTo("desert", output);
                        desert.accept(NatureBlocks.DESERT_TAPERED_BLOCK);
                        desert.accept(NatureBlocks.SMALL_DESERT_PLANT);
                        desert.accept(NatureBlocks.BIG_DESERT_PLANT);
                        desert.accept(NatureBlocks.SMALL_CACTUS);
                        desert.accept(NatureBlocks.DESERT_GRASS);
                        desert.accept(NatureBlocks.DESERT_TALL_GRASS);
                        desert.accept(NatureBlocks.PACKED_DIRT);
                        desert.accept(NatureBlocks.HARDENED_SAND_BLOCK);
                        desert.accept(NatureBlocks.MOISTENED_SAND_BLOCK);
                        desert.accept(NatureBlocks.HARDENED_RED_SAND_BLOCK);
                        desert.accept(NatureBlocks.MOISTENED_RED_SAND_BLOCK);
                        desert.accept(NatureBlocks.SAND_LAYER_BLOCK);
                        desert.accept(NatureBlocks.RED_SAND_LAYER_BLOCK);
                        desert.accept(NatureBlocks.DESERT_FOSSIL);

                        CreativeModeTab.Output ocean = GroupItem.belongsTo("ocean", output);
                        ocean.accept(NatureBlocks.DIATOMACEOUS);
                        ocean.accept(NatureBlocks.MARINE_GRAVEL);

                        CreativeModeTab.Output snow = GroupItem.belongsTo("snow", output);
                        snow.accept(NatureBlocks.SLUSH);
                        snow.accept(NatureBlocks.THIN_ICE_BLOCK);
                        snow.accept(NatureBlocks.ICE_TAPERED_BLOCK);


                        CreativeModeTab.Output jungle = GroupItem.belongsTo("jungle", output);
                        jungle.accept(NatureBlocks.JUNGLE_GRASS_BLOCK);
                        jungle.accept(NatureBlocks.JUNGLE_THORN);
                        jungle.accept(NatureBlocks.PLANTERA_THORN);
                        jungle.accept(NatureBlocks.JUNGLE_HIVE_BLOCK);
                        jungle.accept(NatureBlocks.JUNGLE_ROSE);
                        jungle.accept(NatureBlocks.LARVA);
                        jungle.accept(NatureBlocks.JUNGLE_PATH);
                        jungle.accept(NatureBlocks.THIN_HONEY_BLOCK);
                        jungle.accept(NatureBlocks.LOOSE_HONEY_BLOCK);
                        jungle.accept(ModItems.JUNGLE_CATTAIL);
                        jungle.accept(NatureBlocks.JUNGLE_DROOPING_VINE);

                        CreativeModeTab.Output end = GroupItem.belongsTo("end", output);
                        end.accept(NatureBlocks.END_DIRT);
                        end.accept(NatureBlocks.VOID_WEAVE);
                        end.accept(NatureBlocks.VOID_GRASS_BLOCK);
                        end.accept(NatureBlocks.VOID_GRASS);
                        end.accept(NatureBlocks.VOID_VIOLET);
                        end.accept(NatureBlocks.TALL_VOID_GRASS);
                        end.accept(NatureBlocks.VOID_TREE_ROOT_BLOCK);
                        end.accept(NatureBlocks.INVERSE_GRASS_BLOCK);
                        end.accept(NatureBlocks.GAZE_TUBER);
                        end.accept(NatureBlocks.MOONLIT_GRASS_BLOCK);
                        end.accept(NatureBlocks.DEAD_LUNAR_CORAL_BLOCK);
                        end.accept(NatureBlocks.DEAD_LUNAR_CORAL);
                        end.accept(NatureBlocks.DEAD_LUNAR_CORAL_FAN);
                        end.accept(NatureBlocks.LUNAR_CORAL_BLOCK);
                        end.accept(NatureBlocks.LUNAR_CORAL);
                        end.accept(NatureBlocks.LUNAR_CORAL_FAN);
                        end.accept(NatureBlocks.SILVER_GRASS);
                        end.accept(NatureBlocks.TALL_SILVER_GRASS);
                        end.accept(NatureBlocks.WITHERED_SEA_SILK);

                        CreativeModeTab.Output nether = GroupItem.belongsTo("nether", output);
                        nether.accept(NatureBlocks.ASH_BLOCK);
                        nether.accept(NatureBlocks.ASH_GRASS_BLOCK);
                        nether.accept(NatureBlocks.ASH_PATH);
                        nether.accept(NatureBlocks.ASH_GRASS);
                        nether.accept(NatureBlocks.GLOOM_OBSIDIAN);

                        CreativeModeTab.Output skyland = GroupItem.belongsTo("skyland", output);
                        skyland.accept(NatureBlocks.CLOUD_BLOCK);
                        skyland.accept(NatureBlocks.EVAPORATIVE_CLOUD_BLOCK);
                        skyland.accept(NatureBlocks.RAIN_CLOUD_BLOCK);
                        skyland.accept(NatureBlocks.SNOW_CLOUD_BLOCK);
                        skyland.accept(DecorativeBlocks.CLOUD_BLOCK_TRAMPOLINE.get());
                        skyland.accept(DecorativeBlocks.BOUNCY_CLOUD_BLOCK.get());
                        skyland.accept(DecorativeBlocks.STAR_CLOUD_BLOCK.get());

                        CreativeModeTab.Output crops = GroupItem.belongsTo("crops", output);
                        crops.accept(NatureBlocks.STELLAR_BLOSSOM);
                        crops.accept(NatureBlocks.CLOUDWEAVER);
                        crops.accept(NatureBlocks.FLOATING_WHEAT);
                        crops.accept(NatureBlocks.BALLOON_MELON);
                        crops.accept(NatureBlocks.WHITE_PUMPKIN);

                        CreativeModeTab.Output shimmer = GroupItem.belongsTo("shimmer", output);
                        shimmer.accept(NatureBlocks.SHIMMER_RICE);
                        shimmer.accept(NatureBlocks.SHIMMER_CORAL_TUBE);
                        shimmer.accept(NatureBlocks.BLINKING_ROYAL_SHIMMERLILY);
                        shimmer.accept(NatureBlocks.SHIMMER_CRYSTALS_BLOCK);
                        shimmer.accept(NatureBlocks.AETHERIUM_BLOCK);
                        shimmer.accept(NatureBlocks.DARK_AETHERIUM_BLOCK);

                        CreativeModeTab.Output moss = GroupItem.belongsTo("moss", output);
                        moss.accept(NatureBlocks.GREEN_MOSS);
                        moss.accept(NatureBlocks.BROWN_MOSS);
                        moss.accept(NatureBlocks.RED_MOSS);
                        moss.accept(NatureBlocks.BLUE_MOSS);
                        moss.accept(NatureBlocks.PURPLE_MOSS);
                        moss.accept(NatureBlocks.LAVA_MOSS);
                        moss.accept(NatureBlocks.KRYPTON_MOSS);
                        moss.accept(NatureBlocks.XENON_MOSS);
                        moss.accept(NatureBlocks.ARGON_MOSS);
                        moss.accept(NatureBlocks.NEON_MOSS);
                        moss.accept(NatureBlocks.HELIUM_MOSS);
                        moss.accept(NatureBlocks.GLOWING_MUSHROOM_MOSS);

                        CreativeModeTab.Output special_plants = GroupItem.belongsTo("special_plants", output);
                        special_plants.accept(NatureBlocks.CRIMSON_VENUS_FLYTRAP_BLOCK);
                        special_plants.accept(NatureBlocks.BLOODTHIRST_CRYSTALLIZED_BLOCK);
                        special_plants.accept(NatureBlocks.CORRODED_WORM_ROOTS_BLOCK);
                        special_plants.accept(NatureBlocks.CORRUPTED_OVARIES_BLOCK);
                        special_plants.accept(NatureBlocks.DECOMPOSE_THE_SOURCE_EXTRACT_BLOCK);



                        CreativeModeTab.Output sanctificationOres = GroupItem.belongsTo("sanctification_ores", output);
                        sanctificationOres.accept(OreBlocks.SANCTIFICATION_COAL_ORE);
                        sanctificationOres.accept(OreBlocks.SANCTIFICATION_COPPER_ORE);
                        sanctificationOres.accept(OreBlocks.SANCTIFICATION_TIN_ORE);
                        sanctificationOres.accept(OreBlocks.SANCTIFICATION_IRON_ORE);
                        sanctificationOres.accept(OreBlocks.SANCTIFICATION_LEAD_ORE);
                        sanctificationOres.accept(OreBlocks.SANCTIFICATION_SILVER_ORE);
                        sanctificationOres.accept(OreBlocks.SANCTIFICATION_TUNGSTEN_ORE);
                        sanctificationOres.accept(OreBlocks.SANCTIFICATION_GOLD_ORE);
                        sanctificationOres.accept(OreBlocks.SANCTIFICATION_PLATINUM_ORE);
                        sanctificationOres.accept(OreBlocks.SANCTIFICATION_EMERALD_ORE);
                        sanctificationOres.accept(OreBlocks.SANCTIFICATION_DIAMOND_ORE);
                        sanctificationOres.accept(OreBlocks.SANCTIFICATION_LAPIS_ORE);
                        sanctificationOres.accept(OreBlocks.SANCTIFICATION_REDSTONE_ORE);
                        sanctificationOres.accept(OreBlocks.SANCTIFICATION_RUBY_ORE);
                        sanctificationOres.accept(OreBlocks.SANCTIFICATION_AMBER_ORE);
                        sanctificationOres.accept(OreBlocks.SANCTIFICATION_TOPAZ_ORE);
                        sanctificationOres.accept(OreBlocks.SANCTIFICATION_JADE_ORE);
                        sanctificationOres.accept(OreBlocks.SANCTIFICATION_SAPPHIRE_ORE);
                        sanctificationOres.accept(OreBlocks.SANCTIFICATION_AMETHYST_ORE);
                        sanctificationOres.accept(OreBlocks.SANCTIFICATION_DEMONITE_ORE);
                        sanctificationOres.accept(OreBlocks.SANCTIFICATION_CRIMTANE_ORE);

                        CreativeModeTab.Output corruptionOres = GroupItem.belongsTo("corruption_ores", output);
                        corruptionOres.accept(OreBlocks.CORRUPTION_COAL_ORE);
                        corruptionOres.accept(OreBlocks.CORRUPTION_COPPER_ORE);
                        corruptionOres.accept(OreBlocks.CORRUPTION_TIN_ORE);
                        corruptionOres.accept(OreBlocks.CORRUPTION_IRON_ORE);
                        corruptionOres.accept(OreBlocks.CORRUPTION_LEAD_ORE);
                        corruptionOres.accept(OreBlocks.CORRUPTION_SILVER_ORE);
                        corruptionOres.accept(OreBlocks.CORRUPTION_TUNGSTEN_ORE);
                        corruptionOres.accept(OreBlocks.CORRUPTION_GOLD_ORE);
                        corruptionOres.accept(OreBlocks.CORRUPTION_PLATINUM_ORE);
                        corruptionOres.accept(OreBlocks.CORRUPTION_EMERALD_ORE);
                        corruptionOres.accept(OreBlocks.CORRUPTION_DIAMOND_ORE);
                        corruptionOres.accept(OreBlocks.CORRUPTION_LAPIS_ORE);
                        corruptionOres.accept(OreBlocks.CORRUPTION_REDSTONE_ORE);
                        corruptionOres.accept(OreBlocks.CORRUPTION_RUBY_ORE);
                        corruptionOres.accept(OreBlocks.CORRUPTION_AMBER_ORE);
                        corruptionOres.accept(OreBlocks.CORRUPTION_TOPAZ_ORE);
                        corruptionOres.accept(OreBlocks.CORRUPTION_JADE_ORE);
                        corruptionOres.accept(OreBlocks.CORRUPTION_SAPPHIRE_ORE);
                        corruptionOres.accept(OreBlocks.CORRUPTION_AMETHYST_ORE);
                        corruptionOres.accept(OreBlocks.CORRUPTION_DEMONITE_ORE);
                        corruptionOres.accept(OreBlocks.CORRUPTION_CRIMTANE_ORE);

                        CreativeModeTab.Output fleshificationOres = GroupItem.belongsTo("fleshification_ores", output);
                        fleshificationOres.accept(OreBlocks.FLESHIFICATION_COAL_ORE);
                        fleshificationOres.accept(OreBlocks.FLESHIFICATION_COPPER_ORE);
                        fleshificationOres.accept(OreBlocks.FLESHIFICATION_TIN_ORE);
                        fleshificationOres.accept(OreBlocks.FLESHIFICATION_IRON_ORE);
                        fleshificationOres.accept(OreBlocks.FLESHIFICATION_LEAD_ORE);
                        fleshificationOres.accept(OreBlocks.FLESHIFICATION_SILVER_ORE);
                        fleshificationOres.accept(OreBlocks.FLESHIFICATION_TUNGSTEN_ORE);
                        fleshificationOres.accept(OreBlocks.FLESHIFICATION_GOLD_ORE);
                        fleshificationOres.accept(OreBlocks.FLESHIFICATION_PLATINUM_ORE);
                        fleshificationOres.accept(OreBlocks.FLESHIFICATION_EMERALD_ORE);
                        fleshificationOres.accept(OreBlocks.FLESHIFICATION_DIAMOND_ORE);
                        fleshificationOres.accept(OreBlocks.FLESHIFICATION_LAPIS_ORE);
                        fleshificationOres.accept(OreBlocks.FLESHIFICATION_REDSTONE_ORE);
                        fleshificationOres.accept(OreBlocks.FLESHIFICATION_RUBY_ORE);
                        fleshificationOres.accept(OreBlocks.FLESHIFICATION_AMBER_ORE);
                        fleshificationOres.accept(OreBlocks.FLESHIFICATION_TOPAZ_ORE);
                        fleshificationOres.accept(OreBlocks.FLESHIFICATION_JADE_ORE);
                        fleshificationOres.accept(OreBlocks.FLESHIFICATION_SAPPHIRE_ORE);
                        fleshificationOres.accept(OreBlocks.FLESHIFICATION_AMETHYST_ORE);
                        fleshificationOres.accept(OreBlocks.FLESHIFICATION_DEMONITE_ORE);
                        fleshificationOres.accept(OreBlocks.FLESHIFICATION_CRIMTANE_ORE);

                        CreativeModeTab.Output normalOres = GroupItem.belongsTo("normal_ores", output);
                        normalOres.accept(OreBlocks.TIN_ORE);
                        normalOres.accept(OreBlocks.DEEPSLATE_TIN_ORE);
                        normalOres.accept(OreBlocks.LEAD_ORE);
                        normalOres.accept(OreBlocks.DEEPSLATE_LEAD_ORE);
                        normalOres.accept(OreBlocks.SILVER_ORE);
                        normalOres.accept(OreBlocks.DEEPSLATE_SILVER_ORE);
                        normalOres.accept(OreBlocks.TUNGSTEN_ORE);
                        normalOres.accept(OreBlocks.DEEPSLATE_TUNGSTEN_ORE);
                        normalOres.accept(OreBlocks.PLATINUM_ORE);
                        normalOres.accept(OreBlocks.DEEPSLATE_PLATINUM_ORE);
                        normalOres.accept(OreBlocks.METEORITE_ORE);
                        normalOres.accept(OreBlocks.DEMONITE_ORE);
                        normalOres.accept(OreBlocks.DEEPSLATE_DEMONITE_ORE);
                        normalOres.accept(OreBlocks.CRIMTANE_ORE);
                        normalOres.accept(OreBlocks.DEEPSLATE_CRIMTANE_ORE);
                        normalOres.accept(OreBlocks.CHLOROPHYTE_ORE);
                        normalOres.accept(OreBlocks.OPAL_ORE);
                        normalOres.accept(OreBlocks.GELSTONE_ORE);
                        normalOres.accept(OreBlocks.COLD_CRYSTAL_ORE);
                        normalOres.accept(OreBlocks.HELLSTONE);
                        normalOres.accept(OreBlocks.ASH_HELLSTONE);
                        normalOres.accept(OreBlocks.RUBY_ORE);
                        normalOres.accept(OreBlocks.DEEPSLATE_RUBY_ORE);
                        normalOres.accept(OreBlocks.AMBER_ORE);
                        normalOres.accept(OreBlocks.RED_SAND_AMBER_ORE);
                        normalOres.accept(OreBlocks.TOPAZ_ORE);
                        normalOres.accept(OreBlocks.DEEPSLATE_TOPAZ_ORE);
                        normalOres.accept(OreBlocks.JADE_ORE);
                        normalOres.accept(OreBlocks.DEEPSLATE_JADE_ORE);
                        normalOres.accept(OreBlocks.SAPPHIRE_ORE);
                        normalOres.accept(OreBlocks.DEEPSLATE_SAPPHIRE_ORE);
                        normalOres.accept(OreBlocks.AMETHYST_ORE);
                        normalOres.accept(OreBlocks.DEEPSLATE_AMETHYST_ORE);
                        normalOres.accept(OreBlocks.DEEPSLATE_COBALT_ORE);
                        normalOres.accept(OreBlocks.DEEPSLATE_PALLADIUM_ORE);
                        normalOres.accept(OreBlocks.DEEPSLATE_MYTHRIL_ORE);
                        normalOres.accept(OreBlocks.DEEPSLATE_ORICHALCUM_ORE);
                        normalOres.accept(OreBlocks.DEEPSLATE_ADAMANTITE_ORE);
                        normalOres.accept(OreBlocks.DEEPSLATE_TITANIUM_ORE);
                        normalOres.accept(OreBlocks.LUNARTEAR_ORE);
                        normalOres.accept(OreBlocks.DRAGONSAL_ORE);
                        normalOres.accept(OreBlocks.SPORE_ROOT_BLOCK);
                        normalOres.accept(OreBlocks.WINTER_MARROW_BLOCK);

                        CreativeModeTab.Output rawOreBlocks = GroupItem.belongsTo("raw_ore_blocks", output);
                        rawOreBlocks.accept(OreBlocks.RAW_TIN_BLOCK);
                        rawOreBlocks.accept(OreBlocks.RAW_LEAD_BLOCK);
                        rawOreBlocks.accept(OreBlocks.RAW_SILVER_BLOCK);
                        rawOreBlocks.accept(OreBlocks.RAW_TUNGSTEN_BLOCK);
                        rawOreBlocks.accept(OreBlocks.RAW_PLATINUM_BLOCK);
                        rawOreBlocks.accept(OreBlocks.RAW_METEORITE_BLOCK);
                        rawOreBlocks.accept(OreBlocks.RAW_DEMONITE_BLOCK);
                        rawOreBlocks.accept(OreBlocks.RAW_CRIMTANE_BLOCK);
                        rawOreBlocks.accept(OreBlocks.RAW_CHLOROPHYTE_BLOCK);
                        rawOreBlocks.accept(OreBlocks.RAW_HELLSTONE_BLOCK);
                        rawOreBlocks.accept(OreBlocks.RAW_COBALT_BLOCK);
                        rawOreBlocks.accept(OreBlocks.RAW_PALLADIUM_BLOCK);
                        rawOreBlocks.accept(OreBlocks.RAW_MYTHRIL_BLOCK);
                        rawOreBlocks.accept(OreBlocks.RAW_ORICHALCUM_BLOCK);
                        rawOreBlocks.accept(OreBlocks.RAW_ADAMANTITE_BLOCK);
                        rawOreBlocks.accept(OreBlocks.RAW_TITANIUM_BLOCK);
                        rawOreBlocks.accept(OreBlocks.RAW_LUMINITE_BLOCK);

                        CreativeModeTab.Output oreStorageBlocks = GroupItem.belongsTo("ore_storage_blocks", output);
                        oreStorageBlocks.accept(OreBlocks.TIN_BLOCK);
                        oreStorageBlocks.accept(OreBlocks.LEAD_BLOCK);
                        oreStorageBlocks.accept(OreBlocks.SILVER_BLOCK);
                        oreStorageBlocks.accept(OreBlocks.TUNGSTEN_BLOCK);
                        oreStorageBlocks.accept(OreBlocks.PLATINUM_BLOCK);
                        oreStorageBlocks.accept(OreBlocks.METEORITE_BLOCK);
                        oreStorageBlocks.accept(OreBlocks.DEMONITE_BLOCK);
                        oreStorageBlocks.accept(OreBlocks.CRIMTANE_BLOCK);
                        oreStorageBlocks.accept(OreBlocks.HALLOWED_BLOCK);
                        oreStorageBlocks.accept(OreBlocks.CHLOROPHYTE_BLOCK);
                        oreStorageBlocks.accept(OreBlocks.SHROOMITE_BLOCK);
                        oreStorageBlocks.accept(OreBlocks.SPECTRE_BLOCK);
                        oreStorageBlocks.accept(OreBlocks.LUMINITE_BLOCK);
                        oreStorageBlocks.accept(OreBlocks.HELLSTONE_BLOCK);
                        oreStorageBlocks.accept(OreBlocks.HELLSTONE_BRICKS);
                        oreStorageBlocks.accept(OreBlocks.COBALT_BLOCK);
                        oreStorageBlocks.accept(OreBlocks.PALLADIUM_BLOCK);
                        oreStorageBlocks.accept(OreBlocks.MYTHRIL_BLOCK);
                        oreStorageBlocks.accept(OreBlocks.ORICHALCUM_BLOCK);
                        oreStorageBlocks.accept(OreBlocks.ADAMANTITE_BLOCK);
                        oreStorageBlocks.accept(OreBlocks.TITANIUM_BLOCK);
                        oreStorageBlocks.accept(OreBlocks.STURDY_FOSSIL_BLOCK);
                        oreStorageBlocks.accept(OreBlocks.OPAL_BLOCK);
                        oreStorageBlocks.accept(OreBlocks.GELSTONE_BLOCK);
                        oreStorageBlocks.accept(OreBlocks.COLD_CRYSTAL_BLOCK);
                    })
                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .build()
    );
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BUILDING_BLOCKS = TABS.register("building_blocks",
            () -> CreativeModeTab.builder().icon(IconItems.BLOCKS_ICON::toStack)
                    .title(Component.translatable("creativetab.confluence.building_blocks"))
                    .displayItems((parameters, output) -> {
                        output = new WipNotDisplayOutput(output);
                        LogBlockSet.acceptBuilding(output);
                        DecoBlockSet.acceptBuilding(output);

                        CreativeModeTab.Output granite_bricks = GroupItem.belongsTo(DecorativeBlocks.GRANITE_BRICKS.id, output);
                        granite_bricks.accept(DecorativeBlocks.CRACKED_GRANITE_BRICKS.get());
                        granite_bricks.accept(DecorativeBlocks.CHISELED_GRANITE_BRICKS.get());
                        granite_bricks.accept(DecorativeBlocks.GRANITE_COLUMN.get());
                        granite_bricks.accept(DecorativeBlocks.POLISHED_GRANITE.get());

                        CreativeModeTab.Output copper_bricks = GroupItem.belongsTo("granite_bricks", output);
                        copper_bricks.accept(DecorativeBlocks.CHISELED_COPPER_BRICKS.get());
                        copper_bricks.accept(DecorativeBlocks.COPPER_TILES.get());

                        CreativeModeTab.Output tin_bricks = GroupItem.belongsTo("tin_bricks", output);
                        tin_bricks.accept(DecorativeBlocks.CHISELED_TIN_BRICKS.get());
                        tin_bricks.accept(DecorativeBlocks.TIN_TILES.get());

                        CreativeModeTab.Output iron_bricks = GroupItem.belongsTo(DecorativeBlocks.IRON_BRICKS.id, output);
                        iron_bricks.accept(DecorativeBlocks.CHISELED_IRON_BRICKS.get());

                        CreativeModeTab.Output lead_bricks = GroupItem.belongsTo(DecorativeBlocks.LEAD_BRICKS.id, output);
                        lead_bricks.accept(DecorativeBlocks.CHISELED_LEAD_BRICKS.get());

                        CreativeModeTab.Output silver_bricks = GroupItem.belongsTo(DecorativeBlocks.SILVER_BRICKS.id, output);
                        silver_bricks.accept(DecorativeBlocks.CHISELED_SILVER_BRICKS.get());

                        CreativeModeTab.Output tungsten_bricks = GroupItem.belongsTo(DecorativeBlocks.TUNGSTEN_BRICKS.id, output);
                        tungsten_bricks.accept(DecorativeBlocks.CHISELED_TUNGSTEN_BRICKS.get());

                        CreativeModeTab.Output golden_bricks = GroupItem.belongsTo(DecorativeBlocks.GOLDEN_BRICKS.id, output);
                        golden_bricks.accept(DecorativeBlocks.CHISELED_GOLDEN_BRICKS.get());

                        CreativeModeTab.Output platinum_bricks = GroupItem.belongsTo(DecorativeBlocks.PLATINUM_BRICKS.id, output);
                        platinum_bricks.accept(DecorativeBlocks.CHISELED_PLATINUM_BRICKS.get());

                        CreativeModeTab.Output marble_bricks = GroupItem.belongsTo(DecorativeBlocks.MARBLE_BRICKS.id, output);
                        marble_bricks.accept(DecorativeBlocks.MARBLE_COLUMN.get());
                        marble_bricks.accept(DecorativeBlocks.CRACKED_MARBLE_BRICKS.get());
                        marble_bricks.accept(DecorativeBlocks.CHISELED_MARBLE_BRICKS.get());
                        marble_bricks.accept(DecorativeBlocks.MARBLE_CHESSBOARD_BRICKS.get());
                        marble_bricks.accept(DecorativeBlocks.MARBLE_ETERNAL_CHESSBOARD_BRICKS.get());
                        marble_bricks.accept(DecorativeBlocks.MARBLE_SMALL_BRICKS.get());
                        marble_bricks.accept(DecorativeBlocks.GILDED_MARBLE.get());
                        marble_bricks.accept(DecorativeBlocks.POLISHED_MARBLE.get());

                        CreativeModeTab.Output glass = GroupItem.belongsTo("glass", output);
                        glass.accept(DecorativeBlocks.PURE_GLASS.get());
                        glass.accept(DecorativeBlocks.WHITE_PURE_GLASS.get());
                        glass.accept(DecorativeBlocks.LIGHT_GRAY_PURE_GLASS.get());
                        glass.accept(DecorativeBlocks.GRAY_PURE_GLASS.get());
                        glass.accept(DecorativeBlocks.BLACK_PURE_GLASS.get());
                        glass.accept(DecorativeBlocks.BROWN_PURE_GLASS.get());
                        glass.accept(DecorativeBlocks.RED_PURE_GLASS.get());
                        glass.accept(DecorativeBlocks.ORANGE_PURE_GLASS.get());
                        glass.accept(DecorativeBlocks.YELLOW_PURE_GLASS.get());
                        glass.accept(DecorativeBlocks.LIME_PURE_GLASS.get());
                        glass.accept(DecorativeBlocks.GREEN_PURE_GLASS.get());
                        glass.accept(DecorativeBlocks.CYAN_PURE_GLASS.get());
                        glass.accept(DecorativeBlocks.LIGHT_BLUE_PURE_GLASS.get());
                        glass.accept(DecorativeBlocks.BLUE_PURE_GLASS.get());
                        glass.accept(DecorativeBlocks.PURPLE_PURE_GLASS.get());
                        glass.accept(DecorativeBlocks.MAGENTA_PURE_GLASS.get());
                        glass.accept(DecorativeBlocks.PINK_PURE_GLASS.get());
                        glass.accept(DecorativeBlocks.SOUL_GLASS.get());

                        CreativeModeTab.Output special_building = GroupItem.belongsTo("special_building", output);
                        special_building.accept(DecorativeBlocks.LOST_PAPER_BLOCK.get());
                        special_building.accept(DecorativeBlocks.WOOD_STONE_SLATTED_BLOCKS.get());
                        special_building.accept(DecorativeBlocks.CRISPY_HONEY_BLOCK.get());
                        special_building.accept(DecorativeBlocks.ASPHALT_BLOCK.get());
                        special_building.accept(DecorativeBlocks.SWORD_IN_STONE.get());
                        special_building.accept(DecorativeBlocks.REMAINS_BLOCK.get());
                        special_building.accept(DecorativeBlocks.POO_BLOCK.get());
                        special_building.accept(DecorativeBlocks.FLOATING_WHEAT_BALE.get());
                        special_building.accept(DecorativeBlocks.MURAL_BLOCK.get());
                        special_building.accept(DecorativeBlocks.CARVED_WHITE_PUMPKIN.get());
                        special_building.accept(DecorativeBlocks.JOHNNY_O_LANTERN.get());
                        special_building.accept(DecorativeBlocks.CRYSTAL_BLOCK.get());
                        special_building.accept(DecorativeBlocks.FLESH_BLOCK.get());
                        special_building.accept(DecorativeBlocks.LESION_BLOCK.get());
                        special_building.accept(DecorativeBlocks.BLUE_GEL_BLOCK.get());
                        special_building.accept(DecorativeBlocks.PINK_GEL_BLOCK.get());
                        special_building.accept(DecorativeBlocks.FROZEN_GEL_BLOCK.get());
                        special_building.accept(DecorativeBlocks.RED_CANDY_BLOCK.get());
                        special_building.accept(DecorativeBlocks.GREEN_CANDY_BLOCK.get());
                        special_building.accept(DecorativeBlocks.WHITE_PAPER_PANE.get());
                        special_building.accept(DecorativeBlocks.WHITE_PAPER_PANE_LAMP.get());
                        special_building.accept(DecorativeBlocks.MALACHITE_PAPER_PANE.get());
                        special_building.accept(DecorativeBlocks.MALACHITE_PAPER_PANE_LAMP.get());

                        CreativeModeTab.Output chains = GroupItem.belongsTo("chains", output);
                        chains.accept(DecorativeBlocks.RUBY_CHAIN.get());
                        chains.accept(DecorativeBlocks.AMBER_CHAIN.get());
                        chains.accept(DecorativeBlocks.TOPAZ_CHAIN.get());
                        chains.accept(DecorativeBlocks.JADE_CHAIN.get());
                        chains.accept(DecorativeBlocks.SAPPHIRE_CHAIN.get());
                        chains.accept(DecorativeBlocks.DIAMOND_CHAIN.get());
                        chains.accept(DecorativeBlocks.AMETHYST_CHAIN.get());
                        chains.accept(DecorativeBlocks.SILK_CHAIN.get());
                        chains.accept(DecorativeBlocks.BONE_CHAIN.get());

                        CreativeModeTab.Output blue_bricks = GroupItem.belongsTo(DecorativeBlocks.BLUE_BRICKS.id, output);
                        blue_bricks.accept(DecorativeBlocks.BLUE_BRICK_COLUMN.get());
                        blue_bricks.accept(DecorativeBlocks.CHISELED_BLUE_BRICKS.get());
                        blue_bricks.accept(DecorativeBlocks.CRACKED_BLUE_BRICKS.get());
                        blue_bricks.accept(DecorativeBlocks.ENCHANTED_BLUE_BRICKS.get());

                        CreativeModeTab.Output green_bricks = GroupItem.belongsTo(DecorativeBlocks.GREEN_BRICKS.id, output);
                        green_bricks.accept(DecorativeBlocks.GREEN_BRICK_COLUMN.get());
                        green_bricks.accept(DecorativeBlocks.CHISELED_GREEN_BRICKS.get());
                        green_bricks.accept(DecorativeBlocks.CRACKED_GREEN_BRICKS.get());
                        green_bricks.accept(DecorativeBlocks.ENCHANTED_GREEN_BRICKS.get());

                        CreativeModeTab.Output pink_bricks = GroupItem.belongsTo(DecorativeBlocks.PINK_BRICKS.id, output);
                        pink_bricks.accept(DecorativeBlocks.PINK_BRICK_COLUMN.get());
                        pink_bricks.accept(DecorativeBlocks.CHISELED_PINK_BRICKS.get());
                        pink_bricks.accept(DecorativeBlocks.CRACKED_PINK_BRICKS.get());
                        pink_bricks.accept(DecorativeBlocks.ENCHANTED_PINK_BRICKS.get());

                        CreativeModeTab.Output doors = GroupItem.belongsTo("doors", output);
                        doors.accept(DecorativeBlocks.DUNGEON_DOOR.get());
                        doors.accept(DecorativeBlocks.LIHZAHRD_DOOR.get());
                        doors.accept(DecorativeBlocks.TRADITIONAL_DYNASTY_DOOR.get());
                        doors.accept(DecorativeBlocks.CHRISTMAS_PINE_DOOR.get());
                        doors.accept(DecorativeBlocks.CHRISTMAS_PINE_TRAPDOOR.get());

                        CreativeModeTab.Output boss_relics = GroupItem.belongsTo("boss_relics", output);
                        boss_relics.accept(DecorativeBlocks.KING_SLIME_RELIC.get());
                        boss_relics.accept(DecorativeBlocks.EYE_OF_CTHULHU_RELIC.get());
                        boss_relics.accept(DecorativeBlocks.BRAIN_OF_CTHULHU_RELIC.get());
                        boss_relics.accept(DecorativeBlocks.EATER_OF_WORLDS_RELIC.get());
                        boss_relics.accept(DecorativeBlocks.QUEEN_BEE_RELIC.get());
                        boss_relics.accept(DecorativeBlocks.DEERCLOPS_RELIC.get());
                        boss_relics.accept(DecorativeBlocks.SKELETRON_RELIC.get());
                        boss_relics.accept(DecorativeBlocks.WALL_OF_FLESH_RELIC.get());
                        boss_relics.accept(DecorativeBlocks.HILL_OF_FLESH_RELIC.get());
                        boss_relics.accept(DecorativeBlocks.THE_TWINS_RELIC.get());
                        boss_relics.accept(DecorativeBlocks.SKELETRON_PRIME_RELIC.get());

                        CreativeModeTab.Output balloons = GroupItem.belongsTo("balloons", output);
                        balloons.accept(DecorativeBlocks.WHITE_BALLOON.get());
                        balloons.accept(DecorativeBlocks.LIGHT_GRAY_BALLOON.get());
                        balloons.accept(DecorativeBlocks.GRAY_BALLOON.get());
                        balloons.accept(DecorativeBlocks.BLACK_BALLOON.get());
                        balloons.accept(DecorativeBlocks.BROWN_BALLOON.get());
                        balloons.accept(DecorativeBlocks.RED_BALLOON.get());
                        balloons.accept(DecorativeBlocks.ORANGE_BALLOON.get());
                        balloons.accept(DecorativeBlocks.YELLOW_BALLOON.get());
                        balloons.accept(DecorativeBlocks.LIME_BALLOON.get());
                        balloons.accept(DecorativeBlocks.GREEN_BALLOON.get());
                        balloons.accept(DecorativeBlocks.CYAN_BALLOON.get());
                        balloons.accept(DecorativeBlocks.LIGHT_BLUE_BALLOON.get());
                        balloons.accept(DecorativeBlocks.BLUE_BALLOON.get());
                        balloons.accept(DecorativeBlocks.PURPLE_BALLOON.get());
                        balloons.accept(DecorativeBlocks.MAGENTA_BALLOON.get());
                        balloons.accept(DecorativeBlocks.PINK_BALLOON.get());

                        CreativeModeTab.Output gem_blocks = GroupItem.belongsTo("gem_blocks", output);
                        gem_blocks.accept(DecorativeBlocks.RUBY_BLOCK.get());
                        gem_blocks.accept(DecorativeBlocks.AMBER_BLOCK.get());
                        gem_blocks.accept(DecorativeBlocks.TOPAZ_BLOCK.get());
                        gem_blocks.accept(DecorativeBlocks.JADE_BLOCK.get());
                        gem_blocks.accept(DecorativeBlocks.SAPPHIRE_BLOCK.get());
                        gem_blocks.accept(DecorativeBlocks.AMETHYST_BLOCK.get());

                        CreativeModeTab.Output fur_wool = GroupItem.belongsTo("fur_wool", output);
                        fur_wool.accept(DecorativeBlocks.FLINX_FUR_BLOCK.get());
                        fur_wool.accept(DecorativeBlocks.FLINX_FUR_CARPET.get());
                        fur_wool.accept(DecorativeBlocks.RAINBOW_WOOL.get());
                        fur_wool.accept(DecorativeBlocks.RAINBOW_CARPET.get());

                        acceptAll(StatueBlocks.BLOCKS, output, "statue");
                    })
                    .withTabsBefore(NATURAL_BLOCKS.getId())
                    .build()
    );
    /* 家具 */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MECHANICAL = TABS.register("mechanical",
            () -> CreativeModeTab.builder().icon(IconItems.MECHANICAL_ICON::toStack)
                    .title(Component.translatable("creativetab.confluence.mechanical"))
                    .displayItems((parameters, output) -> {
                        output = new WipNotDisplayOutput(output);

                        output.accept(ModBlocks.ENEMY_BANNER);

                        CreativeModeTab.Output wiring_tools = GroupItem.belongsTo("wiring_tools", output);
                        wiring_tools.accept(ToolItems.RED_WRENCH.get());
                        wiring_tools.accept(ToolItems.BLUE_WRENCH.get());
                        wiring_tools.accept(ToolItems.GREEN_WRENCH.get());
                        wiring_tools.accept(ToolItems.YELLOW_WRENCH.get());
                        wiring_tools.accept(ToolItems.WIRE_CUTTER.get());

                        CreativeModeTab.Output boulders = GroupItem.belongsTo("boulders", output);
                        boulders.accept(FunctionalBlocks.NORMAL_BOULDER.get());
                        boulders.accept(FunctionalBlocks.OAK_LOG_BOULDER.get());
                        boulders.accept(FunctionalBlocks.FOLLOWER_BOULDER.get());
                        boulders.accept(FunctionalBlocks.EXPLODE_BOULDER.get());
                        boulders.accept(FunctionalBlocks.BOUNCY_BOULDER.get());
                        boulders.accept(FunctionalBlocks.POO_BOULDER.get());
                        boulders.accept(FunctionalBlocks.SPIDER_BOULDER.get());
                        boulders.accept(FunctionalBlocks.LAVA_BOULDER.get());
                        boulders.accept(FunctionalBlocks.RAINBOW_BOULDER.get());
                        boulders.accept(FunctionalBlocks.LIFECRYSTAL_BOULDER.get());
                        boulders.accept(FunctionalBlocks.GHOULDER.get());
                        boulders.accept(FunctionalBlocks.ROLLING_CACTUS_BOULDER.get());

                        CreativeModeTab.Output trigger = GroupItem.belongsTo("trigger", output);
                        trigger.accept(FunctionalBlocks.PLAYER_PRESSURE_PLATE.get());
                        trigger.accept(FunctionalBlocks.STONE_PRESSURE_PLATE.get());
                        trigger.accept(FunctionalBlocks.DEEPSLATE_PRESSURE_PLATE.get());
                        trigger.accept(FunctionalBlocks.STONE_PRESSURE_BLOCK.get());
                        trigger.accept(FunctionalBlocks.DEEPSLATE_PRESSURE_BLOCK.get());
                        trigger.accept(FunctionalBlocks.SWITCH.get());
                        trigger.accept(FunctionalBlocks.LEVER.get());
                        trigger.accept(FunctionalBlocks.TIMERS_BLOCK_1_1.get());
                        trigger.accept(FunctionalBlocks.TIMERS_BLOCK_3_1.get());
                        trigger.accept(FunctionalBlocks.TIMERS_BLOCK_5_1.get());
                        trigger.accept(FunctionalBlocks.TIMERS_BLOCK_1_2.get());
                        trigger.accept(FunctionalBlocks.TIMERS_BLOCK_1_4.get());

                        CreativeModeTab.Output redstone_circuit_traps = GroupItem.belongsTo("redstone_circuit_traps", output);
                        redstone_circuit_traps.accept(FunctionalBlocks.SIGNAL_ADAPTER.get());
                        redstone_circuit_traps.accept(FunctionalBlocks.DETONATOR.get());
                        redstone_circuit_traps.accept(FunctionalBlocks.INSTANTANEOUS_EXPLOSION_TNT.get());
                        redstone_circuit_traps.accept(FunctionalBlocks.LAND_MINE.get());
                        redstone_circuit_traps.accept(FunctionalBlocks.DART_TRAP.get());
                        redstone_circuit_traps.accept(FunctionalBlocks.STONE_DART_TRAP.get());
                        redstone_circuit_traps.accept(FunctionalBlocks.DEEPSLATE_DART_TRAP.get());
                        redstone_circuit_traps.accept(FunctionalBlocks.SUPER_DART_TRAP.get());
                        redstone_circuit_traps.accept(FunctionalBlocks.FLAME_TRAP.get());
                        redstone_circuit_traps.accept(FunctionalBlocks.SPIKY_BALL_TRAP.get());
                        redstone_circuit_traps.accept(FunctionalBlocks.SPEAR_TRAP.get());
                        redstone_circuit_traps.accept(FunctionalBlocks.SHIMMER_TRAP.get());
                        redstone_circuit_traps.accept(FunctionalBlocks.GRAVITATION_TRAP.get());
                        redstone_circuit_traps.accept(FunctionalBlocks.PNEUMATIC_TRAP.get());
                        redstone_circuit_traps.accept(FunctionalBlocks.SCULK_TRAP.get());
                        redstone_circuit_traps.accept(FunctionalBlocks.FRAGILE_SANDSTONE.get());
                        redstone_circuit_traps.accept(FunctionalBlocks.MECHANICAL_FRAGILE_SANDSTONE.get());
                        redstone_circuit_traps.accept(FunctionalBlocks.MECHANICAL_FRAGILE_OBSIDIAN_BRICKS.get());
                        redstone_circuit_traps.accept(FunctionalBlocks.FRAGILE_BLUE_BRICKS.get());
                        redstone_circuit_traps.accept(FunctionalBlocks.FRAGILE_GREEN_BRICKS.get());
                        redstone_circuit_traps.accept(FunctionalBlocks.FRAGILE_PINK_BRICKS.get());
                        redstone_circuit_traps.accept(FunctionalBlocks.ENCHANTED_FRAGILE_BLUE_BRICKS.get());
                        redstone_circuit_traps.accept(FunctionalBlocks.ENCHANTED_FRAGILE_GREEN_BRICKS.get());
                        redstone_circuit_traps.accept(FunctionalBlocks.ENCHANTED_FRAGILE_PINK_BRICKS.get());
                        redstone_circuit_traps.accept(FunctionalBlocks.EVER_POWERED_RAIL.get());
                        redstone_circuit_traps.accept(FunctionalBlocks.GEYSER_BLOCK.get());

                        CreativeModeTab.Output crafting_stations = GroupItem.belongsTo("crafting_stations", output);
                        crafting_stations.accept(FunctionalBlocks.SHARPENING_STATION.get());
                        crafting_stations.accept(FunctionalBlocks.BEWITCHING_TABLE.get());
                        crafting_stations.accept(FunctionalBlocks.EXTRACTINATOR.get());
                        crafting_stations.accept(FunctionalBlocks.SKY_MILL.get());
                        crafting_stations.accept(FunctionalBlocks.HEAVY_WORK_BENCH.get());
                        crafting_stations.accept(FunctionalBlocks.CRYSTAL_BALL.get());
                        crafting_stations.accept(FunctionalBlocks.HELLFORGE.get());
                        crafting_stations.accept(FunctionalBlocks.ADAMANTITE_FORGE.get());
                        crafting_stations.accept(FunctionalBlocks.TITANIUM_FORGE.get());
                        crafting_stations.accept(FunctionalBlocks.ALCHEMY_TABLE.get());
                        crafting_stations.accept(FunctionalBlocks.SAWMILL.get());
                        crafting_stations.accept(FunctionalBlocks.LOOM.get());
                        crafting_stations.accept(FunctionalBlocks.DYE_VAT.get());
                        crafting_stations.accept(FunctionalBlocks.SOLIDIFIER.get());
                        crafting_stations.accept(FunctionalBlocks.KEG.get());
                        crafting_stations.accept(FunctionalBlocks.CHLOROPHYTE_EXTRACTINATOR.get());
                        crafting_stations.accept(FunctionalBlocks.BLEND_O_MATIC.get());
                        crafting_stations.accept(FunctionalBlocks.MEAT_GRINDER.get());
                        crafting_stations.accept(FunctionalBlocks.CRIMSON_ALTAR.get());
                        crafting_stations.accept(FunctionalBlocks.DEMON_ALTAR.get());
                        crafting_stations.accept(FunctionalBlocks.LEAD_ANVIL.get());
                        crafting_stations.accept(FunctionalBlocks.CHIPPED_LEAD_ANVIL.get());
                        crafting_stations.accept(FunctionalBlocks.DAMAGED_LEAD_ANVIL.get());
                        crafting_stations.accept(FunctionalBlocks.MYTHRIL_ANVIL.get());
                        crafting_stations.accept(FunctionalBlocks.ORICHALCUM_ANVIL.get());
                        crafting_stations.accept(FunctionalBlocks.COOKING_POT.get());
                        crafting_stations.accept(FunctionalBlocks.CAULDRON.get());
                        crafting_stations.accept(TFBlocks.GLASS_KILN);
                        crafting_stations.accept(TFBlocks.LIVING_LOOM);
                        crafting_stations.accept(TFBlocks.ICE_MACHINE);

                        CreativeModeTab.Output storage = GroupItem.belongsTo("storage", output);
                        storage.accept(ChestBlocks.CRIMSON_CHEST.get());
                        storage.accept(ChestBlocks.CORRUPTION_CHEST.get());
                        storage.accept(ChestBlocks.JUNGLE_CHEST.get());
                        storage.accept(ChestBlocks.ICE_CHEST.get());
                        storage.accept(ChestBlocks.DESERT_CHEST.get());
                        storage.accept(ChestBlocks.OCEAN_CHEST.get());
                        storage.accept(ChestBlocks.UNIVERSE_CHEST.get());
                        storage.accept(ChestBlocks.HALLOWED_CHEST.get());
                        storage.accept(ChestBlocks.MECHANIC_SAFE_CHEST.get());
                        storage.accept(FunctionalBlocks.PIGGY_BANK.get());
                        storage.accept(FunctionalBlocks.SAFE.get());
                        storage.accept(FunctionalBlocks.MAGIC_MAIL_BOX.get());
                        storage.accept(TFBlocks.TRASH_CAN);

                        CreativeModeTab.Output souls = GroupItem.belongsTo("souls", output);
                        souls.accept(FunctionalBlocks.SOUL_OF_BRIGHT_IN_A_BOTTLE.get());
                        souls.accept(FunctionalBlocks.SOUL_OF_FLIGHT_IN_A_BOTTLE.get());
                        souls.accept(FunctionalBlocks.SOUL_OF_FRIGHT_IN_A_BOTTLE.get());
                        souls.accept(FunctionalBlocks.SOUL_OF_LIGHT_IN_A_BOTTLE.get());
                        souls.accept(FunctionalBlocks.SOUL_OF_MIGHT_IN_A_BOTTLE.get());
                        souls.accept(FunctionalBlocks.SOUL_OF_SIGHT_IN_A_BOTTLE.get());
                        souls.accept(FunctionalBlocks.SOUL_OF_NIGHT_IN_A_BOTTLE.get());
                        souls.accept(FunctionalBlocks.SOUL_OF_VOIGHT_IN_A_BOTTLE.get());

                        CreativeModeTab.Output misc_functional = GroupItem.belongsTo("misc_functional", output);
                        misc_functional.accept(FunctionalBlocks.SPIKE.get());
                        misc_functional.accept(FunctionalBlocks.WOODEN_SPIKE.get());
                        misc_functional.accept(FunctionalBlocks.LIFE_CAMPFIRE.get());
                        misc_functional.accept(FunctionalBlocks.SILLY_BALLOON_MACHINE.get());
                        misc_functional.accept(FunctionalBlocks.WEATHER_VANE.get());
                        misc_functional.accept(FunctionalBlocks.HEART_LANTERN.get());
                        misc_functional.accept(FunctionalBlocks.STAR_IN_A_BOTTLE.get());
                        misc_functional.accept(FunctionalBlocks.AMMO_BOX.get());
                        misc_functional.accept(FunctionalBlocks.ANNOUNCEMENT_BOX.get());
                        misc_functional.accept(FunctionalBlocks.TREE_HOLES_BLOCK.get());
                        misc_functional.accept(FunctionalBlocks.LOCK_BLOCK.get());
                        misc_functional.accept(FunctionalBlocks.TUFF_BOOTH.get());
                        misc_functional.accept(FunctionalBlocks.WATER_CANDLE);
                        misc_functional.accept(FunctionalBlocks.PEACE_CANDLE);
                        misc_functional.accept(FunctionalBlocks.ECHO_BLOCK.get());
                        WaystonesHelper.accept(output);
                    })
                    .withTabsBefore(TFRegistries.FURNITURE.getId())
                    .build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MATERIALS = TABS.register("materials",
            () -> CreativeModeTab.builder().icon(IconItems.MATERIAL_ICON::toStack)
                    .title(Component.translatable("creativetab.confluence.materials"))
                    .displayItems((parameters, output) -> {
                        output = new WipNotDisplayOutput(output);

                        CreativeModeTab.Output metal_materials = GroupItem.belongsTo("metal_materials", output);
                        metal_materials.accept(MaterialItems.RAW_TIN.get());
                        metal_materials.accept(MaterialItems.TIN_INGOT.get());
                        metal_materials.accept(MaterialItems.TIN_NUGGET.get());
                        metal_materials.accept(MaterialItems.RAW_LEAD.get());
                        metal_materials.accept(MaterialItems.LEAD_INGOT.get());
                        metal_materials.accept(MaterialItems.LEAD_NUGGET.get());
                        metal_materials.accept(MaterialItems.RAW_SILVER.get());
                        metal_materials.accept(MaterialItems.SILVER_INGOT.get());
                        metal_materials.accept(MaterialItems.SILVER_NUGGET.get());
                        metal_materials.accept(MaterialItems.RAW_TUNGSTEN.get());
                        metal_materials.accept(MaterialItems.TUNGSTEN_INGOT.get());
                        metal_materials.accept(MaterialItems.TUNGSTEN_NUGGET.get());
                        metal_materials.accept(MaterialItems.RAW_PLATINUM.get());
                        metal_materials.accept(MaterialItems.PLATINUM_INGOT.get());
                        metal_materials.accept(MaterialItems.PLATINUM_NUGGET.get());
                        metal_materials.accept(MaterialItems.RAW_METEORITE.get());
                        metal_materials.accept(MaterialItems.METEORITE_INGOT.get());
                        metal_materials.accept(MaterialItems.METEORITE_NUGGET.get());
                        metal_materials.accept(MaterialItems.RAW_DEMONITE.get());
                        metal_materials.accept(MaterialItems.DEMONITE_INGOT.get());
                        metal_materials.accept(MaterialItems.DEMONITE_NUGGET.get());
                        metal_materials.accept(MaterialItems.RAW_CRIMTANE.get());
                        metal_materials.accept(MaterialItems.CRIMTANE_INGOT.get());
                        metal_materials.accept(MaterialItems.CRIMTANE_NUGGET.get());
                        metal_materials.accept(MaterialItems.RAW_HELLSTONE.get());
                        metal_materials.accept(MaterialItems.HELLSTONE_INGOT.get());
                        metal_materials.accept(MaterialItems.HELLSTONE_NUGGET.get());
                        metal_materials.accept(MaterialItems.RAW_COBALT.get());
                        metal_materials.accept(MaterialItems.COBALT_INGOT.get());
                        metal_materials.accept(MaterialItems.RAW_PALLADIUM.get());
                        metal_materials.accept(MaterialItems.PALLADIUM_INGOT.get());
                        metal_materials.accept(MaterialItems.RAW_MYTHRIL.get());
                        metal_materials.accept(MaterialItems.MYTHRIL_INGOT.get());
                        metal_materials.accept(MaterialItems.RAW_ORICHALCUM.get());
                        metal_materials.accept(MaterialItems.ORICHALCUM_INGOT.get());
                        metal_materials.accept(MaterialItems.RAW_ADAMANTITE.get());
                        metal_materials.accept(MaterialItems.ADAMANTITE_INGOT.get());
                        metal_materials.accept(MaterialItems.RAW_TITANIUM.get());
                        metal_materials.accept(MaterialItems.TITANIUM_INGOT.get());
                        metal_materials.accept(MaterialItems.RAW_CHLOROPHYTE.get());
                        metal_materials.accept(MaterialItems.CHLOROPHYTE_INGOT.get());
                        metal_materials.accept(MaterialItems.RAW_LUMINITE.get());
                        metal_materials.accept(MaterialItems.LUMINITE_INGOT.get());
                        metal_materials.accept(MaterialItems.HALLOWED_INGOT.get());
                        metal_materials.accept(MaterialItems.SHROOMITE_INGOT.get());
                        metal_materials.accept(MaterialItems.SPECTRE_INGOT.get());

                        CreativeModeTab.Output natural_materials = GroupItem.belongsTo("natural_materials", output);
                        natural_materials.accept(MaterialItems.RUBY.get());
                        natural_materials.accept(MaterialItems.AMBER.get());
                        natural_materials.accept(MaterialItems.TOPAZ.get());
                        natural_materials.accept(MaterialItems.JADE.get());
                        natural_materials.accept(MaterialItems.SAPPHIRE.get());
                        natural_materials.accept(MaterialItems.AMETHYST.get());
                        natural_materials.accept(MaterialItems.PEARL.get());
                        natural_materials.accept(MaterialItems.BLACK_PEARL.get());
                        natural_materials.accept(MaterialItems.PINK_PEARL.get());
                        natural_materials.accept(MaterialItems.COLD_CRYSTAL.get());
                        natural_materials.accept(MaterialItems.VOID_CRYSTAL.get());
                        natural_materials.accept(MaterialItems.OPAL.get());
                        natural_materials.accept(MaterialItems.STURDY_FOSSIL.get());
                        natural_materials.accept(MaterialItems.HEIM.get());
                        natural_materials.accept(MaterialItems.GELSTONE.get());
                        natural_materials.accept(MaterialItems.WINTER_MARROW.get());
                        natural_materials.accept(MaterialItems.LUNARTEAR.get());
                        natural_materials.accept(MaterialItems.DRAGONSAL.get());
                        natural_materials.accept(MaterialItems.AETHERIUM_SHARD.get());

                        CreativeModeTab.Output souls_special = GroupItem.belongsTo("souls_special", output);
                        souls_special.accept(MaterialItems.SOUL_OF_LIGHT.get());
                        souls_special.accept(MaterialItems.SOUL_OF_NIGHT.get());
                        souls_special.accept(MaterialItems.SOUL_OF_FLIGHT.get());
                        souls_special.accept(MaterialItems.SOUL_OF_MIGHT.get());
                        souls_special.accept(MaterialItems.SOUL_OF_SIGHT.get());
                        souls_special.accept(MaterialItems.SOUL_OF_FRIGHT.get());
                        souls_special.accept(MaterialItems.SOUL_OF_VOIGHT.get());
                        souls_special.accept(MaterialItems.SOUL_OF_BRIGHT.get());

                        CreativeModeTab.Output monster_drops = GroupItem.belongsTo("monster_drops", output);
                        monster_drops.accept(MaterialItems.ROTTEN_CHUNK.get());
                        monster_drops.accept(MaterialItems.WORM_TOOTH.get());
                        monster_drops.accept(MaterialItems.VERTEBRA.get());
                        monster_drops.accept(MaterialItems.BLOOD_CLOT_POWDER.get());
                        monster_drops.accept(MaterialItems.ROTTEN_BONE.get());
                        monster_drops.accept(MaterialItems.FILAMENTOUS_FIN.get());
                        monster_drops.accept(MaterialItems.LENS.get());
                        monster_drops.accept(MaterialItems.BLACK_LENS.get());
                        monster_drops.accept(MaterialItems.TATTERED_CLOTH.get());
                        monster_drops.accept(MaterialItems.ANTLION_MANDIBLE.get());
                        monster_drops.accept(MaterialItems.STINGER.get());
                        monster_drops.accept(MaterialItems.MAN_EATER_VINE.get());
                        monster_drops.accept(MaterialItems.BLACK_INK.get());
                        monster_drops.accept(MaterialItems.SHARK_FIN.get());
                        monster_drops.accept(MaterialItems.SHADOW_SCALE.get());
                        monster_drops.accept(MaterialItems.TISSUE_SAMPLE.get());
                        monster_drops.accept(MaterialItems.ROYAL_WAX.get());
                        monster_drops.accept(MaterialItems.SPIDER_FANG.get());
                        monster_drops.accept(MaterialItems.GEL.get());
                        monster_drops.accept(MaterialItems.PINK_GEL.get());
                        monster_drops.accept(MaterialItems.HARPY_FEATHER.get());
                        monster_drops.accept(MaterialItems.GIANT_HARPY_FEATHER.get());
                        monster_drops.accept(MaterialItems.FLINX_FUR.get());
                        monster_drops.accept(MaterialItems.CRYSTAL_SHARDS.get());
                        monster_drops.accept(MaterialItems.DARK_SHARD.get());
                        monster_drops.accept(MaterialItems.LIGHT_SHARD.get());
                        monster_drops.accept(MaterialItems.CURSED_FLAME.get());
                        monster_drops.accept(MaterialItems.ICHOR.get());
                        monster_drops.accept(MaterialItems.PIXIE_DUST.get());
                        monster_drops.accept(MaterialItems.UNICORN_HORN.get());
                        monster_drops.accept(MaterialItems.FORBIDDEN_FRAGMENT.get());
                        monster_drops.accept(MaterialItems.FROST_CORE.get());
                        monster_drops.accept(MaterialItems.HOOK.get());
                        monster_drops.accept(MaterialItems.ANCIENT_CLOTH.get());
                        monster_drops.accept(MaterialItems.ECTOPLASM.get());
                        monster_drops.accept(ModItems.WHOOPIE_CUSHION.get());
                        monster_drops.accept(MaterialItems.MECHANICAL_WHEEL_PIECE.get());
                        monster_drops.accept(MaterialItems.MECHANICAL_WAGON_PIECE.get());
                        monster_drops.accept(MaterialItems.MECHANICAL_BATTERY_PIECE.get());

                        CreativeModeTab.Output plants_herbs = GroupItem.belongsTo("plants_herbs", output);
                        plants_herbs.accept(MaterialItems.WATERLEAF.get());
                        plants_herbs.accept(MaterialItems.FIREBLOSSOM.get());
                        plants_herbs.accept(MaterialItems.MOONGLOW.get());
                        plants_herbs.accept(MaterialItems.BLINKROOT.get());
                        plants_herbs.accept(MaterialItems.SHIVERTHORN.get());
                        plants_herbs.accept(MaterialItems.DAYBLOOM.get());
                        plants_herbs.accept(MaterialItems.DEATHWEED.get());
                        plants_herbs.accept(MaterialItems.JUNGLE_SPORE.get());
                        plants_herbs.accept(MaterialItems.SPORE_ROOT.get());
                        plants_herbs.accept(MaterialItems.FALLING_STAR.get());
                        plants_herbs.accept(MaterialItems.STAR_PETALS.get());
                        plants_herbs.accept(MaterialItems.FLOATING_WHEAT_HEADS.get());
                        plants_herbs.accept(MaterialItems.WEAVING_CLOUD_COTTON.get());

                        CreativeModeTab.Output crafting_materials = GroupItem.belongsTo("crafting_materials", output);
                        crafting_materials.accept(MaterialItems.PEARLWOOD_STICK.get());
                        crafting_materials.accept(MaterialItems.AETHERIUM_GOLD.get());
                        crafting_materials.accept(MaterialItems.SILK.get());
                        crafting_materials.accept(MaterialItems.RAW_ASPHALT.get());
                        crafting_materials.accept(MaterialItems.SPELL_TOME.get());
                        crafting_materials.accept(MaterialItems.CHINA_PLATE.get());
                        crafting_materials.accept(MaterialItems.CHINA_BOWL.get());
                        crafting_materials.accept(MaterialItems.EMPTY_BULLET.get());
                        crafting_materials.accept(MaterialItems.EXPLOSIVE_POWDER.get());
                        crafting_materials.accept(MaterialItems.GOLD_DUST.get());
                        crafting_materials.accept(MaterialItems.COG.get());
                        crafting_materials.accept(MaterialItems.NANITES.get());
                        crafting_materials.accept(MaterialItems.CONFETTI.get());
                        crafting_materials.accept(MaterialItems.VIAL_OF_VENOM.get());
                        crafting_materials.accept(MaterialItems.BELL.get());
                        crafting_materials.accept(MaterialItems.HARP.get());
                    })
                    .withTabsBefore(MECHANICAL.getId())
                    .build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MISC = TABS.register("misc",
            () -> CreativeModeTab.builder().icon(IconItems.PRECIOUS_ICON::toStack)
                    .title(Component.translatable("creativetab.confluence.misc"))
                    .displayItems((parameters, output) -> {
                        output = new WipNotDisplayOutput(output);

//                        acceptAll(ModItems.ITEMS, output);

                        // 如果删掉上边两行就会崩
                        acceptAll(TreasureBagItems.ITEMS, output, "treasure_bag");
                        acceptAll(ModBlocks.TOMBSTONES.keySet(), GroupItem.belongsTo("tombstone", output));
                        acceptAll(BaitItems.ITEMS, output, "bait");
                        acceptAll(QuestedFishes.ITEMS, output, "quested_fish");
                        acceptAll(CrateBlocks.BLOCKS, output, "crate");
                        acceptAll(PaintItems.ITEMS, output, "paint");

                        CreativeModeTab.Output coins = GroupItem.belongsTo("coins", output);
                        coins.accept(ModItems.COPPER_COIN.get());
                        coins.accept(ModItems.SILVER_COIN.get());
                        coins.accept(ModItems.GOLD_COIN.get());
                        coins.accept(ModItems.PLATINUM_COIN.get());
                        coins.accept(ModItems.EMERALD_COIN.get());

                        CreativeModeTab.Output throwing_weapons = GroupItem.belongsTo("throwing_weapons", output);
                        throwing_weapons.accept(ConsumableItems.SHURIKEN.get());
                        throwing_weapons.accept(ConsumableItems.THROWING_KNIVE.get());
                        throwing_weapons.accept(ConsumableItems.BONE_THROWING_KNIFE.get());
                        throwing_weapons.accept(ConsumableItems.FROST_DAGGERFISH.get());
                        throwing_weapons.accept(ConsumableItems.JAVELIN.get());
                        throwing_weapons.accept(ConsumableItems.DUNGEON_DEMON_BONE.get());
                        throwing_weapons.accept(ConsumableItems.SPIKY_BALL.get());

                        CreativeModeTab.Output bombs_explosives = GroupItem.belongsTo("bombs_explosives", output);
                        bombs_explosives.accept(ConsumableItems.BOMB.get());
                        bombs_explosives.accept(ConsumableItems.BOUNCY_BOMB.get());
                        bombs_explosives.accept(ConsumableItems.STICKY_BOMB.get());
                        bombs_explosives.accept(ConsumableItems.SCARAB_BOMB.get());
                        bombs_explosives.accept(ConsumableItems.BOMB_FISH.get());
                        bombs_explosives.accept(ConsumableItems.DYNAMITE.get());
                        bombs_explosives.accept(ConsumableItems.BOUNCY_DYNAMITE.get());
                        bombs_explosives.accept(ConsumableItems.STICKY_DYNAMITE.get());
                        bombs_explosives.accept(ConsumableItems.GRENADE.get());
                        bombs_explosives.accept(ConsumableItems.BOUNCY_GRENADE.get());
                        bombs_explosives.accept(ConsumableItems.STICKY_GRENADE.get());
                        bombs_explosives.accept(ConsumableItems.BEENADE.get());
                        bombs_explosives.accept(ConsumableItems.SMOKE_BOMB.get());
                        bombs_explosives.accept(ConsumableItems.DIRT_BOMB.get());
                        bombs_explosives.accept(ConsumableItems.STICKY_DIRT_BOMB.get());
                        bombs_explosives.accept(ConsumableItems.DRY_BOMB.get());
                        bombs_explosives.accept(ConsumableItems.WET_BOMB.get());
                        bombs_explosives.accept(ConsumableItems.LAVA_BOMB.get());
                        bombs_explosives.accept(ConsumableItems.HONEY_BOMB.get());

                        CreativeModeTab.Output boss_event_summons = GroupItem.belongsTo("boss_event_summons", output);
                        boss_event_summons.accept(ConsumableItems.SUSPICIOUS_LOOKING_EYE.get());
                        boss_event_summons.accept(ConsumableItems.SLIME_CROWN.get());
                        boss_event_summons.accept(ConsumableItems.WORM_FOOD.get());
                        boss_event_summons.accept(ConsumableItems.BLOODY_SPINE.get());
                        boss_event_summons.accept(ConsumableItems.ABEEMINATION.get());
                        boss_event_summons.accept(ConsumableItems.DEER_THING.get());
                        boss_event_summons.accept(AccessoryItems.CLOTHIER_VOODOO_DOLL.get());
                        boss_event_summons.accept(AccessoryItems.GUIDE_VOODOO_DOLL.get());
                        boss_event_summons.accept(ConsumableItems.BLOOD_TEAR.get());
                        boss_event_summons.accept(ConsumableItems.GOBLIN_BATTLE_STANDARD.get());

                        CreativeModeTab.Output environment_items = GroupItem.belongsTo("environment_items", output);
                        environment_items.accept(ConsumableItems.HOLY_WATER.get());
                        environment_items.accept(ConsumableItems.UNHOLY_WATER.get());
                        environment_items.accept(ConsumableItems.BLOOD_WATER.get());
                        environment_items.accept(ConsumableItems.VILE_POWDER.get());
                        environment_items.accept(ConsumableItems.VICIOUS_POWDER.get());
                        environment_items.accept(ConsumableItems.PURIFICATION_POWDER.get());
                        environment_items.accept(ConsumableItems.FERTILIZER.get());
                        environment_items.accept(ConsumableItems.ROTTEN_BONE_DUST.get());
                        environment_items.accept(ConsumableItems.BLOODSTAINED_POWDER.get());
                        environment_items.accept(ModItems.GRASS_SEED.get());
                        environment_items.accept(ModItems.JUNGLE_GRASS_SEED.get());
                        environment_items.accept(ModItems.MUSHROOM_GRASS_SEED.get());
                        environment_items.accept(ModItems.CORRUPT_SEED.get());
                        environment_items.accept(ModItems.CRIMSON_SEED.get());
                        environment_items.accept(ModItems.HALLOWED_SEED.get());
                        environment_items.accept(ModItems.ASH_GRASS_SEED.get());

                        CreativeModeTab.Output gain = GroupItem.belongsTo("gain", output);
                        gain.accept(ConsumableItems.MANA_CRYSTAL.get());
                        gain.accept(ConsumableItems.LIFE_CRYSTAL.get());
                        gain.accept(ConsumableItems.LIFE_FRUIT.get());
                        gain.accept(ConsumableItems.VITAL_CRYSTAL.get());
                        gain.accept(ConsumableItems.ARCANE_CRYSTAL.get());
                        gain.accept(ConsumableItems.MINECART_UPGRADE_KIT.get());
                        gain.accept(ConsumableItems.ARTISAN_LOAF.get());
                        gain.accept(ConsumableItems.GALAXY_PEARL.get());
                        gain.accept(ConsumableItems.AEGIS_APPLE.get());
                        gain.accept(ConsumableItems.AMBROSIA.get());
                        gain.accept(ConsumableItems.GUMMY_WORM.get());
                        gain.accept(ConsumableItems.ADVANCED_COMBAT_TECHNIQUES.get());
                        gain.accept(ConsumableItems.ADVANCED_COMBAT_TECHNIQUES_VOLUME_TWO.get());
                        gain.accept(ConsumableItems.PEDDLERS_SATCHEL.get());
                        gain.accept(TCItems.DEMON_HEART.get());

                        CreativeModeTab.Output loot_gifts = GroupItem.belongsTo("loot_gifts", output);
                        loot_gifts.accept(ConsumableItems.CHRISTMAS_GIFT.get());
                        loot_gifts.accept(ConsumableItems.RED_ENVELOPE.get());
                        loot_gifts.accept(ConsumableItems.GOODIE_BAG.get());
                        loot_gifts.accept(ConsumableItems.DELUXE_PACKAGE.get());
                        loot_gifts.accept(ConsumableItems.HERB_BAG.get());
                        loot_gifts.accept(ConsumableItems.CAN_OF_WORMS.get());
                        loot_gifts.accept(ConsumableItems.GOLDEN_LOCK_BOX.get());
                        loot_gifts.accept(ConsumableItems.OBSIDIAN_LOCK_BOX.get());
                        loot_gifts.accept(ConsumableItems.CLAM.get());
                        loot_gifts.accept(ConsumableItems.PINE_CONE.get());
                        loot_gifts.accept(ConsumableItems.SUGAR_TANGERINE.get());

                    })
                    .withTabsBefore(MATERIALS.getId())
                    .build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FOOD_AND_POTIONS = TABS.register("food_and_potions",
            () -> CreativeModeTab.builder().icon(IconItems.POTION_ICON::toStack)
                    .title(Component.translatable("creativetab.confluence.food_and_potions"))
                    .displayItems((parameters, output) -> {
                        output = new WipNotDisplayOutput(output);
                        // 不要改成group，已经够清晰了
                        acceptAll(PotionItems.ITEMS, output);
                        acceptAll(FoodItems.ITEMS, output);
                    })
                    .withTabsBefore(MISC.getId())
                    .build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TOOLS = TABS.register("tools",
            () -> CreativeModeTab.builder().icon(IconItems.TOOLS_ICON::toStack)
                    .title(Component.translatable("creativetab.confluence.tools"))
                    .displayItems((parameters, output) -> {
                        output = new WipNotDisplayOutput(output);
                        acceptAll(AxeItems.ITEMS, output, "axe");
                        acceptAll(PickaxeItems.ITEMS, output, "pickaxe");
                        acceptAll(PickaxeAxeItems.ITEMS, output, "pickaxe_axe");
                        acceptAll(DrillItems.ITEMS, output, "drill");
                        acceptAll(ChainsawItems.ITEMS, output, "chainsaw");
                        acceptAll(HamaxeItems.ITEMS, output, "hamaxe");
                        acceptAll(HoeShovelItems.ITEMS, output, "how_shovel");
                        acceptAll(GardenShearsItems.ITEMS, output, "garden_shears");
                        acceptAll(HammerItems.ITEMS, output, "hammer");
                        acceptAll(HookItems.ITEMS, output, "hook");
                        acceptAll(MinecartItems.ITEMS, output, "minecart");
                        acceptAll(FishingPoleItems.ITEMS, output, "fishing_pole");
                        acceptAll(HoeItems.ITEMS, output, "hoe");
                        acceptAll(ShovelItems.ITEMS, output, "shovel");
                        acceptAll(BoatItems.BOAT_ITEMS, output, "boat");
                        acceptAll(BoatItems.CHEST_BOAT_ITEMS, output, "chest_boat");

                        CreativeModeTab.Output wand = GroupItem.belongsTo("wand", output);
                        wand.accept(ModItems.LIVING_WOOD_WAND);
                        wand.accept(ModItems.LEAF_WAND);
                        wand.accept(ModItems.LIVING_MAHOGANY_WAND);
                        wand.accept(ModItems.RICH_MAHOGANY_LEAF_WAND);
                        wand.accept(ModItems.HIVE_WAND);

                        CreativeModeTab.Output wiring_tools = GroupItem.belongsTo("wiring_tools", output);
                        wiring_tools.accept(ToolItems.RED_WRENCH.get());
                        wiring_tools.accept(ToolItems.BLUE_WRENCH.get());
                        wiring_tools.accept(ToolItems.GREEN_WRENCH.get());
                        wiring_tools.accept(ToolItems.YELLOW_WRENCH.get());
                        wiring_tools.accept(ToolItems.WIRE_CUTTER.get());

                        CreativeModeTab.Output keys = GroupItem.belongsTo("keys", output);
                        keys.accept(ToolItems.GOLDEN_DUNGEON_KEY.get());
                        keys.accept(ToolItems.GOLDEN_KEY.get());
                        keys.accept(ToolItems.SHADOW_KEY.get());
                        keys.accept(ToolItems.TEMPLE_KEY.get());
                        keys.accept(ToolItems.JUNGLE_KEY.get());
                        keys.accept(ToolItems.CORRUPTION_KEY.get());
                        keys.accept(ToolItems.CRIMSON_KEY.get());
                        keys.accept(ToolItems.HALLOWED_KEY.get());
                        keys.accept(ToolItems.FROZEN_KEY.get());
                        keys.accept(ToolItems.DESERT_KEY.get());
                        keys.accept(ToolItems.OCEAN_KEY.get());
                        keys.accept(ToolItems.UNIVERSE_KEY.get());
                        keys.accept(ToolItems.RUST_IRON_KEY.get());
                        keys.accept(ToolItems.MECHANIC_SAFE_KEY.get());
                        keys.accept(ToolItems.KEY_OF_LIGHT.get());
                        keys.accept(ToolItems.KEY_OF_NIGHT.get());

                        CreativeModeTab.Output buckets_liquids = GroupItem.belongsTo("buckets_liquids", output);
                        buckets_liquids.accept(ToolItems.HONEY_BUCKET.get());
                        buckets_liquids.accept(ToolItems.BOTTOMLESS_SHIMMER_BUCKET.get());
                        buckets_liquids.accept(ToolItems.BOTTOMLESS_WATER_BUCKET.get());
                        buckets_liquids.accept(ToolItems.BOTTOMLESS_LAVA_BUCKET.get());
                        buckets_liquids.accept(ToolItems.BOTTOMLESS_HONEY_BUCKET.get());
                        buckets_liquids.accept(ToolItems.EMPTY_DROPPER.get());
                        buckets_liquids.accept(ToolItems.MAGIC_SAND_DROPPER.get());
                        buckets_liquids.accept(ToolItems.MAGIC_HONEY_DROPPER.get());
                        buckets_liquids.accept(ToolItems.MAGIC_LAVA_DROPPER.get());
                        buckets_liquids.accept(ToolItems.MAGIC_WATER_DROPPER.get());
                        buckets_liquids.accept(ToolItems.SUPER_ABSORBANT_SPONGE.get());
                        buckets_liquids.accept(ToolItems.HONEY_ABSORBANT_SPONGE.get());
                        buckets_liquids.accept(ToolItems.LAVA_ABSORBANT_SPONGE.get());
                        buckets_liquids.accept(ToolItems.ULTRA_ABSORBANT_SPONGE.get());

                        CreativeModeTab.Output nets = GroupItem.belongsTo("nets", output);
                        nets.accept(ToolItems.BUG_NET.get());
                        nets.accept(ToolItems.DEV_BUG_NET.get());
                        nets.accept(ToolItems.GOLDEN_BUG_NET.get());
                        nets.accept(ToolItems.LAVAPROOF_BUG_NET.get());

                        CreativeModeTab.Output ropes = GroupItem.belongsTo("ropes", output);
                        ropes.accept(ToolItems.ROPE_COIL.get());
                        ropes.accept(ToolItems.VINE_ROPE_COIL.get());
                        ropes.accept(ToolItems.SILK_ROPE_COIL.get());
                        ropes.accept(ToolItems.WEB_ROPE_COIL.get());
                        ropes.accept(ModBlocks.ROPE);
                        ropes.accept(ModBlocks.VINE_ROPE);
                        ropes.accept(ModBlocks.SILK_ROPE);
                        ropes.accept(ModBlocks.WEB_ROPE);
                        output.accept(ModBlocks.PINE_NEEDLE_HANDMADE_ROPE_SET);

                        CreativeModeTab.Output utility_tools = GroupItem.belongsTo("utility_tools", output);
                        utility_tools.accept(ToolItems.ICE_MIRROR.get());
                        utility_tools.accept(TCItems.MAGIC_MIRROR);
                        utility_tools.accept(TCItems.CELL_PHONE);
                        utility_tools.accept(ToolItems.MAGIC_CONCH.get());
                        utility_tools.accept(ToolItems.DEMON_CONCH.get());
                        utility_tools.accept(ToolItems.METEOR_COMPASS.get());
                        utility_tools.accept(ToolItems.DUNGEON_COMPASS.get());
                        utility_tools.accept(ToolItems.BINOCULARS.get());
                        utility_tools.accept(ToolItems.ENCUMBERING_STONE.get());
                        utility_tools.accept(ToolItems.NPC_INVITATION.get());
                        utility_tools.accept(ToolItems.GUIDE_TO_CRITTER_COMPANIONSHIP.get());
                        utility_tools.accept(ToolItems.GUIDE_TO_ENVIRONMENTAL_PRESERVATION.get());
                        utility_tools.accept(ToolItems.GUIDE_TO_PEACEFUL_COEXISTENCE.get());
                        utility_tools.accept(ModItems.SCRYING_ORB.get());
                        utility_tools.accept(ModItems.ENEMY_BANNER.get());
                    })
                    .withTabsBefore(FOOD_AND_POTIONS.getId())
                    .build());
    /* 饰品 */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ARMORS = TABS.register("armors",
            () -> CreativeModeTab.builder().icon(IconItems.ARMOR_ICON::toStack)
                    .title(Component.translatable("creativetab.confluence.armors"))
                    .displayItems((parameters, output) -> {
                        output = new WipNotDisplayOutput(output);
                        CreativeModeTab.Output cactus_armor = GroupItem.belongsTo("cactus_armor", output);
                        cactus_armor.accept(ArmorItems.CACTUS_HELMET.get());
                        cactus_armor.accept(ArmorItems.CACTUS_CHESTPLATE.get());
                        cactus_armor.accept(ArmorItems.CACTUS_LEGGINGS.get());
                        cactus_armor.accept(ArmorItems.CACTUS_BOOTS.get());

                        CreativeModeTab.Output plank_armor = GroupItem.belongsTo("plank_armor", output);
                        plank_armor.accept(ArmorItems.PLANK_HELMET.get());
                        plank_armor.accept(ArmorItems.PLANK_CHESTPLATE.get());
                        plank_armor.accept(ArmorItems.PLANK_LEGGINGS.get());
                        plank_armor.accept(ArmorItems.PLANK_BOOTS.get());

                        CreativeModeTab.Output ebony_armor = GroupItem.belongsTo("ebony_armor", output);
                        ebony_armor.accept(ArmorItems.EBONY_HELMET.get());
                        ebony_armor.accept(ArmorItems.EBONY_CHESTPLATE.get());
                        ebony_armor.accept(ArmorItems.EBONY_LEGGINGS.get());
                        ebony_armor.accept(ArmorItems.EBONY_BOOTS.get());

                        CreativeModeTab.Output shadow_plank_armor = GroupItem.belongsTo("shadow_plank_armor", output);
                        shadow_plank_armor.accept(ArmorItems.SHADOW_PLANK_HELMET.get());
                        shadow_plank_armor.accept(ArmorItems.SHADOW_PLANK_CHESTPLATE.get());
                        shadow_plank_armor.accept(ArmorItems.SHADOW_PLANK_LEGGINGS.get());
                        shadow_plank_armor.accept(ArmorItems.SHADOW_PLANK_BOOTS.get());

                        CreativeModeTab.Output pearl_armor = GroupItem.belongsTo("pearl_armor", output);
                        pearl_armor.accept(ArmorItems.PEARL_HELMET.get());
                        pearl_armor.accept(ArmorItems.PEARL_CHESTPLATE.get());
                        pearl_armor.accept(ArmorItems.PEARL_LEGGINGS.get());
                        pearl_armor.accept(ArmorItems.PEARL_BOOTS.get());

                        CreativeModeTab.Output ash_armor = GroupItem.belongsTo("ash_armor", output);
                        ash_armor.accept(ArmorItems.ASH_HELMET.get());
                        ash_armor.accept(ArmorItems.ASH_CHESTPLATE.get());
                        ash_armor.accept(ArmorItems.ASH_LEGGINGS.get());
                        ash_armor.accept(ArmorItems.ASH_BOOTS.get());

                        CreativeModeTab.Output pumpkin_armor = GroupItem.belongsTo("pumpkin_armor", output);
                        pumpkin_armor.accept(ArmorItems.PUMPKIN_HELMET.get());
                        pumpkin_armor.accept(ArmorItems.PUMPKIN_CHESTPLATE.get());
                        pumpkin_armor.accept(ArmorItems.PUMPKIN_LEGGINGS.get());
                        pumpkin_armor.accept(ArmorItems.PUMPKIN_BOOTS.get());

                        CreativeModeTab.Output rain_wear = GroupItem.belongsTo("rain_wear", output);
                        rain_wear.accept(ArmorItems.RAIN_CAP.get());
                        rain_wear.accept(ArmorItems.RAINCOAT.get());

                        CreativeModeTab.Output snow_insulated_wear = GroupItem.belongsTo("snow_insulated_wear", output);
                        snow_insulated_wear.accept(ArmorItems.SNOW_CAPS.get());
                        snow_insulated_wear.accept(ArmorItems.SNOW_SUITS.get());
                        snow_insulated_wear.accept(ArmorItems.INSULATED_PANTS.get());
                        snow_insulated_wear.accept(ArmorItems.INSULATED_SHOES.get());

                        CreativeModeTab.Output pink_snow_insulated_wear = GroupItem.belongsTo("pink_snow_insulated_wear", output);
                        pink_snow_insulated_wear.accept(ArmorItems.PINK_SNOW_CAPS.get());
                        pink_snow_insulated_wear.accept(ArmorItems.PINK_SNOW_SUITS.get());
                        pink_snow_insulated_wear.accept(ArmorItems.PINK_INSULATED_PANTS.get());
                        pink_snow_insulated_wear.accept(ArmorItems.PINK_INSULATED_SHOES.get());

                        CreativeModeTab.Output obsidian_armor = GroupItem.belongsTo("obsidian_armor", output);
                        obsidian_armor.accept(ArmorItems.OBSIDIAN_HELMET.get());
                        obsidian_armor.accept(ArmorItems.OBSIDIAN_CHESTPLATE.get());
                        obsidian_armor.accept(ArmorItems.OBSIDIAN_LEGGINGS.get());
                        obsidian_armor.accept(ArmorItems.OBSIDIAN_BOOTS.get());

                        CreativeModeTab.Output gladiator_armor = GroupItem.belongsTo("gladiator_armor", output);
                        gladiator_armor.accept(ArmorItems.GLADIATOR_HELMET.get());
                        gladiator_armor.accept(ArmorItems.GLADIATOR_CHESTPLATE.get());
                        gladiator_armor.accept(ArmorItems.GLADIATOR_LEGGINGS.get());
                        gladiator_armor.accept(ArmorItems.GLADIATOR_BOOTS.get());

                        CreativeModeTab.Output meteor_armor = GroupItem.belongsTo("meteor_armor", output);
                        meteor_armor.accept(ArmorItems.METEOR_HELMET.get());
                        meteor_armor.accept(ArmorItems.METEOR_CHESTPLATE.get());
                        meteor_armor.accept(ArmorItems.METEOR_LEGGINGS.get());
                        meteor_armor.accept(ArmorItems.METEOR_BOOTS.get());

                        CreativeModeTab.Output copper_armor = GroupItem.belongsTo("copper_armor", output);
                        copper_armor.accept(ArmorItems.COPPER_HELMET.get());
                        copper_armor.accept(ArmorItems.COPPER_CHESTPLATE.get());
                        copper_armor.accept(ArmorItems.COPPER_LEGGINGS.get());
                        copper_armor.accept(ArmorItems.COPPER_BOOTS.get());

                        CreativeModeTab.Output tin_armor = GroupItem.belongsTo("tin_armor", output);
                        tin_armor.accept(ArmorItems.TIN_HELMET.get());
                        tin_armor.accept(ArmorItems.TIN_CHESTPLATE.get());
                        tin_armor.accept(ArmorItems.TIN_LEGGINGS.get());
                        tin_armor.accept(ArmorItems.TIN_BOOTS.get());

                        CreativeModeTab.Output lead_armor = GroupItem.belongsTo("lead_armor", output);
                        lead_armor.accept(ArmorItems.LEAD_HELMET.get());
                        lead_armor.accept(ArmorItems.LEAD_CHESTPLATE.get());
                        lead_armor.accept(ArmorItems.LEAD_LEGGINGS.get());
                        lead_armor.accept(ArmorItems.LEAD_BOOTS.get());

                        CreativeModeTab.Output silver_armor = GroupItem.belongsTo("silver_armor", output);
                        silver_armor.accept(ArmorItems.SILVER_HELMET.get());
                        silver_armor.accept(ArmorItems.SILVER_CHESTPLATE.get());
                        silver_armor.accept(ArmorItems.SILVER_LEGGINGS.get());
                        silver_armor.accept(ArmorItems.SILVER_BOOTS.get());

                        CreativeModeTab.Output tungsten_armor = GroupItem.belongsTo("tungsten_armor", output);
                        tungsten_armor.accept(ArmorItems.TUNGSTEN_HELMET.get());
                        tungsten_armor.accept(ArmorItems.TUNGSTEN_CHESTPLATE.get());
                        tungsten_armor.accept(ArmorItems.TUNGSTEN_LEGGINGS.get());
                        tungsten_armor.accept(ArmorItems.TUNGSTEN_BOOTS.get());

                        CreativeModeTab.Output golden_armor = GroupItem.belongsTo("golden_armor", output);
                        golden_armor.accept(ArmorItems.GOLDEN_HELMET.get());
                        golden_armor.accept(ArmorItems.GOLDEN_CHESTPLATE.get());
                        golden_armor.accept(ArmorItems.GOLDEN_LEGGINGS.get());
                        golden_armor.accept(ArmorItems.GOLDEN_BOOTS.get());

                        CreativeModeTab.Output platinum_armor = GroupItem.belongsTo("platinum_armor", output);
                        platinum_armor.accept(ArmorItems.PLATINUM_HELMET.get());
                        platinum_armor.accept(ArmorItems.PLATINUM_CHESTPLATE.get());
                        platinum_armor.accept(ArmorItems.PLATINUM_LEGGINGS.get());
                        platinum_armor.accept(ArmorItems.PLATINUM_BOOTS.get());

                        CreativeModeTab.Output fossil_armor = GroupItem.belongsTo("fossil_armor", output);
                        fossil_armor.accept(ArmorItems.FOSSIL_HELMET.get());
                        fossil_armor.accept(ArmorItems.FOSSIL_CHESTPLATE.get());
                        fossil_armor.accept(ArmorItems.FOSSIL_LEGGINGS.get());
                        fossil_armor.accept(ArmorItems.FOSSIL_BOOTS.get());

                        CreativeModeTab.Output bee_armor = GroupItem.belongsTo("bee_armor", output);
                        bee_armor.accept(ArmorItems.BEE_HELMET.get());
                        bee_armor.accept(ArmorItems.BEE_CHESTPLATE.get());
                        bee_armor.accept(ArmorItems.BEE_LEGGINGS.get());
                        bee_armor.accept(ArmorItems.BEE_BOOTS.get());

                        CreativeModeTab.Output ninja_armor = GroupItem.belongsTo("ninja_armor", output);
                        ninja_armor.accept(ArmorItems.NINJA_HELMET.get());
                        ninja_armor.accept(ArmorItems.NINJA_CHESTPLATE.get());
                        ninja_armor.accept(ArmorItems.NINJA_LEGGINGS.get());
                        ninja_armor.accept(ArmorItems.NINJA_BOOTS.get());

                        CreativeModeTab.Output hunters_armor = GroupItem.belongsTo("hunters_armor", output);
                        hunters_armor.accept(ArmorItems.HUNERS_HELMET.get());
                        hunters_armor.accept(ArmorItems.HUNERS_CHESTPLATE.get());
                        hunters_armor.accept(ArmorItems.HUNERS_LEGGINGS.get());
                        hunters_armor.accept(ArmorItems.HUNERS_BOOTS.get());

                        CreativeModeTab.Output scale_mail_armor = GroupItem.belongsTo("scale_mail_armor", output);
                        scale_mail_armor.accept(ArmorItems.SCALE_MAIL_HELMET.get());
                        scale_mail_armor.accept(ArmorItems.SCALE_MAIL_CHESTPLATE.get());
                        scale_mail_armor.accept(ArmorItems.SCALE_MAIL_LEGGINGS.get());
                        scale_mail_armor.accept(ArmorItems.SCALE_MAIL_BOOTS.get());

                        CreativeModeTab.Output guards_armor = GroupItem.belongsTo("guards_armor", output);
                        guards_armor.accept(ArmorItems.GUARDS_HELMET.get());
                        guards_armor.accept(ArmorItems.GUARDS_CHESTPLATE.get());
                        guards_armor.accept(ArmorItems.GUARDS_LEGGINGS.get());
                        guards_armor.accept(ArmorItems.GUARDS_BOOTS.get());

                        CreativeModeTab.Output spelunker_armor = GroupItem.belongsTo("spelunker_armor", output);
                        spelunker_armor.accept(ArmorItems.SPELUNKER_HELMET.get());
                        spelunker_armor.accept(ArmorItems.SPELUNKER_CHESTPLATE.get());
                        spelunker_armor.accept(ArmorItems.SPELUNKER_LEGGINGS.get());
                        spelunker_armor.accept(ArmorItems.SPELUNKER_BOOTS.get());

                        CreativeModeTab.Output spore_root_armor = GroupItem.belongsTo("spore_root_armor", output);
                        spore_root_armor.accept(ArmorItems.SPORE_ROOT_HELMET.get());
                        spore_root_armor.accept(ArmorItems.SPORE_ROOT_CHESTPLATE.get());
                        spore_root_armor.accept(ArmorItems.SPORE_ROOT_LEGGINGS.get());
                        spore_root_armor.accept(ArmorItems.SPORE_ROOT_BOOTS.get());

                        CreativeModeTab.Output cold_crystal_armor = GroupItem.belongsTo("cold_crystal_armor", output);
                        cold_crystal_armor.accept(ArmorItems.COLD_CRYSTAL_HELMET.get());
                        cold_crystal_armor.accept(ArmorItems.COLD_CRYSTAL_CHESTPLATE.get());
                        cold_crystal_armor.accept(ArmorItems.COLD_CRYSTAL_LEGGINGS.get());
                        cold_crystal_armor.accept(ArmorItems.COLD_CRYSTAL_BOOTS.get());

                        CreativeModeTab.Output heim_armor = GroupItem.belongsTo("heim_armor", output);
                        heim_armor.accept(ArmorItems.HEIM_HELMET.get());
                        heim_armor.accept(ArmorItems.HEIM_CHESTPLATE.get());
                        heim_armor.accept(ArmorItems.HEIM_LEGGINGS.get());
                        heim_armor.accept(ArmorItems.HEIM_BOOTS.get());

                        CreativeModeTab.Output shadow_armor = GroupItem.belongsTo("shadow_armor", output);
                        shadow_armor.accept(ArmorItems.SHADOW_HELMET.get());
                        shadow_armor.accept(ArmorItems.SHADOW_CHESTPLATE.get());
                        shadow_armor.accept(ArmorItems.SHADOW_LEGGINGS.get());
                        shadow_armor.accept(ArmorItems.SHADOW_BOOTS.get());

                        CreativeModeTab.Output crimson_armor = GroupItem.belongsTo("crimson_armor", output);
                        crimson_armor.accept(ArmorItems.CRIMSON_HELMET.get());
                        crimson_armor.accept(ArmorItems.CRIMSON_CHESTPLATE.get());
                        crimson_armor.accept(ArmorItems.CRIMSON_LEGGINGS.get());
                        crimson_armor.accept(ArmorItems.CRIMSON_BOOTS.get());

                        CreativeModeTab.Output mining_armor = GroupItem.belongsTo("mining_armor", output);
                        mining_armor.accept(ArmorItems.MINING_HELMET.get());
                        mining_armor.accept(ArmorItems.MINING_CHESTPLATE.get());
                        mining_armor.accept(ArmorItems.MINING_LEGGINGS.get());
                        mining_armor.accept(ArmorItems.MINING_BOOTS.get());

                        CreativeModeTab.Output angler_wear = GroupItem.belongsTo("angler_wear", output);
                        angler_wear.accept(ArmorItems.ANGLER_HAT.get());
                        angler_wear.accept(ArmorItems.ANGLER_VEST.get());
                        angler_wear.accept(ArmorItems.ANGLER_PANTS.get());

                        CreativeModeTab.Output molten_armor = GroupItem.belongsTo("molten_armor", output);
                        molten_armor.accept(ArmorItems.MOLTEN_HELMET.get());
                        molten_armor.accept(ArmorItems.MOLTEN_CHESTPLATE.get());
                        molten_armor.accept(ArmorItems.MOLTEN_LEGGINGS.get());
                        molten_armor.accept(ArmorItems.MOLTEN_BOOTS.get());

                        CreativeModeTab.Output necro_armor = GroupItem.belongsTo("necro_armor", output);
                        necro_armor.accept(ArmorItems.NECRO_HELMET.get());
                        necro_armor.accept(ArmorItems.NECRO_CHESTPLATE.get());
                        necro_armor.accept(ArmorItems.NECRO_LEGGINGS.get());
                        necro_armor.accept(ArmorItems.NECRO_BOOTS.get());

                        CreativeModeTab.Output jungle_armor = GroupItem.belongsTo("jungle_armor", output);
                        jungle_armor.accept(ArmorItems.JUNGLE_HELMET.get());
                        jungle_armor.accept(ArmorItems.JUNGLE_CHESTPLATE.get());
                        jungle_armor.accept(ArmorItems.JUNGLE_LEGGINGS.get());
                        jungle_armor.accept(ArmorItems.JUNGLE_BOOTS.get());

                        CreativeModeTab.Output spider_armor = GroupItem.belongsTo("spider_armor", output);
                        spider_armor.accept(ArmorItems.SPIDER_HELMET.get());
                        spider_armor.accept(ArmorItems.SPIDER_CHESTPLATE.get());
                        spider_armor.accept(ArmorItems.SPIDER_LEGGINGS.get());
                        spider_armor.accept(ArmorItems.SPIDER_BOOTS.get());

                        CreativeModeTab.Output tiki_armor = GroupItem.belongsTo("tiki_armor", output);
                        tiki_armor.accept(ArmorItems.TIKI_MASK.get());
                        tiki_armor.accept(ArmorItems.TIKI_SHIRT.get());
                        tiki_armor.accept(ArmorItems.TIKI_LEGGINGS.get());
                        tiki_armor.accept(ArmorItems.TIKI_BOOTS.get());

                        CreativeModeTab.Output cobalt_armor = GroupItem.belongsTo("cobalt_armor", output);
                        cobalt_armor.accept(ArmorItems.COBALT_MASK.get());
                        cobalt_armor.accept(ArmorItems.COBALT_HAT.get());
                        cobalt_armor.accept(ArmorItems.COBALT_HELMET.get());
                        cobalt_armor.accept(ArmorItems.COBALT_CHESTPLATE.get());
                        cobalt_armor.accept(ArmorItems.COBALT_LEGGINGS.get());
                        cobalt_armor.accept(ArmorItems.COBALT_BOOTS.get());

                        CreativeModeTab.Output palladium_armor = GroupItem.belongsTo("palladium_armor", output);
                        palladium_armor.accept(ArmorItems.PALLADIUM_MASK.get());
                        palladium_armor.accept(ArmorItems.PALLADIUM_HEADGEAR.get());
                        palladium_armor.accept(ArmorItems.PALLADIUM_HELMET.get());
                        palladium_armor.accept(ArmorItems.PALLADIUM_CHESTPLATE.get());
                        palladium_armor.accept(ArmorItems.PALLADIUM_LEGGINGS.get());
                        palladium_armor.accept(ArmorItems.PALLADIUM_BOOTS.get());

                        CreativeModeTab.Output mythril_armor = GroupItem.belongsTo("mythril_armor", output);
                        mythril_armor.accept(ArmorItems.MYTHRIL_HOOD.get());
                        mythril_armor.accept(ArmorItems.MYTHRIL_HAT.get());
                        mythril_armor.accept(ArmorItems.MYTHRIL_HELMET.get());
                        mythril_armor.accept(ArmorItems.MYTHRIL_CHESTPLATE.get());
                        mythril_armor.accept(ArmorItems.MYTHRIL_LEGGINGS.get());
                        mythril_armor.accept(ArmorItems.MYTHRIL_BOOTS.get());

                        CreativeModeTab.Output orichalcum_armor = GroupItem.belongsTo("orichalcum_armor", output);
                        orichalcum_armor.accept(ArmorItems.ORICHALCUM_HEADGEAR.get());
                        orichalcum_armor.accept(ArmorItems.ORICHALCUM_MASK.get());
                        orichalcum_armor.accept(ArmorItems.ORICHALCUM_HELMET.get());
                        orichalcum_armor.accept(ArmorItems.ORICHALCUM_CHESTPLATE.get());
                        orichalcum_armor.accept(ArmorItems.ORICHALCUM_LEGGINGS.get());
                        orichalcum_armor.accept(ArmorItems.ORICHALCUM_BOOTS.get());

                        CreativeModeTab.Output adamantite_armor = GroupItem.belongsTo("adamantite_armor", output);
                        adamantite_armor.accept(ArmorItems.ADAMANTITE_HEADGEAR.get());
                        adamantite_armor.accept(ArmorItems.ADAMANTITE_MASK.get());
                        adamantite_armor.accept(ArmorItems.ADAMANTITE_HELMET.get());
                        adamantite_armor.accept(ArmorItems.ADAMANTITE_CHESTPLATE.get());
                        adamantite_armor.accept(ArmorItems.ADAMANTITE_LEGGINGS.get());
                        adamantite_armor.accept(ArmorItems.ADAMANTITE_BOOTS.get());

                        CreativeModeTab.Output titanium_armor = GroupItem.belongsTo("titanium_armor", output);
                        titanium_armor.accept(ArmorItems.TITANIUM_HEADGEAR.get());
                        titanium_armor.accept(ArmorItems.TITANIUM_MASK.get());
                        titanium_armor.accept(ArmorItems.TITANIUM_HELMET.get());
                        titanium_armor.accept(ArmorItems.TITANIUM_CHESTPLATE.get());
                        titanium_armor.accept(ArmorItems.TITANIUM_LEGGINGS.get());
                        titanium_armor.accept(ArmorItems.TITANIUM_BOOTS.get());

                        CreativeModeTab.Output crystal_assassin_armor = GroupItem.belongsTo("crystal_assassin_armor", output);
                        crystal_assassin_armor.accept(ArmorItems.CRYSTAL_ASSASSIN_HELMET.get());
                        crystal_assassin_armor.accept(ArmorItems.CRYSTAL_ASSASSIN_CHESTPLATE.get());
                        crystal_assassin_armor.accept(ArmorItems.CRYSTAL_ASSASSIN_LEGGINGS.get());
                        crystal_assassin_armor.accept(ArmorItems.CRYSTAL_ASSASSIN_BOOTS.get());

                        CreativeModeTab.Output hallowed_armor = GroupItem.belongsTo("hallowed_armor", output);
                        hallowed_armor.accept(ArmorItems.HALLOWED_HEADGEAR.get());
                        hallowed_armor.accept(ArmorItems.HALLOWED_HOOD.get());
                        hallowed_armor.accept(ArmorItems.HALLOWED_MASK.get());
                        hallowed_armor.accept(ArmorItems.HALLOWED_HELMET.get());
                        hallowed_armor.accept(ArmorItems.HALLOWED_CHESTPLATE.get());
                        hallowed_armor.accept(ArmorItems.HALLOWED_LEGGINGS.get());
                        hallowed_armor.accept(ArmorItems.HALLOWED_BOOTS.get());

                        CreativeModeTab.Output magic_robes = GroupItem.belongsTo("magic_robes", output);
                        magic_robes.accept(ArmorItems.AMETHYST_ROBE.get());
                        magic_robes.accept(ArmorItems.TOPAZ_ROBE.get());
                        magic_robes.accept(ArmorItems.SAPPHIRE_ROBE.get());
                        magic_robes.accept(ArmorItems.JADE_ROBE.get());
                        magic_robes.accept(ArmorItems.RUBY_ROBE.get());
                        magic_robes.accept(ArmorItems.MYSTIC_ROBE.get());
                        magic_robes.accept(ArmorItems.DIAMOND_ROBE.get());
                        magic_robes.accept(ArmorItems.AMBER_ROBE.get());
                        magic_robes.accept(ArmorItems.SOUL_ROBE.get());
                        magic_robes.accept(VanityArmorItems.ROBE.get());

                        output.accept(ArmorItems.GOGGLES.get());
                        output.accept(ArmorItems.GREEN_CAP.get());
                        output.accept(ArmorItems.VIKING_HELMET.get());
                        output.accept(ArmorItems.WIZARD_HAT.get());
                        output.accept(ArmorItems.MAGIC_HAT.get());
                        output.accept(ArmorItems.SOUL_HOOD.get());
                        output.accept(VanityArmorItems.DEAD_MANS_SWEATER.get());
                        output.accept(VanityArmorItems.TOP_HAT.get());
                        output.accept(VanityArmorItems.SUMMER_HAT.get());
                        output.accept(VanityArmorItems.BUNNY_HOOD.get());
                        output.accept(VanityArmorItems.ROBOT_HAT.get());
                        output.accept(VanityArmorItems.MIME_MASK.get());
                        output.accept(VanityArmorItems.GOLD_CROWN.get());
                        output.accept(VanityArmorItems.PLATINUM_CROWN.get());
                        output.accept(VanityArmorItems.SUNGLASSES.get());
                        output.accept(VanityArmorItems.AVIATORS.get());
                        output.accept(VanityArmorItems.EYE_PATCH.get());
                        output.accept(VanityArmorItems.HALLOWED_CROWN.get());
                        output.accept(ArmorItems.FLINX_FUR_COAT.get());
                        output.accept(ArmorItems.FLINX_FUR_COAT.get());
                        output.accept(TCItems.DIVING_HELMET);

                        CreativeModeTab.Output dyes = GroupItem.belongsTo("dyes", output);
                        dyes.accept(VanityArmorItems.DYE.get());
                        dyes.accept(VanityArmorItems.RED_DYE.get());
                        dyes.accept(VanityArmorItems.BRIGHT_RED_DYE.get());
                        dyes.accept(VanityArmorItems.ORANGE_DYE.get());
                        dyes.accept(VanityArmorItems.BRIGHT_ORANGE_DYE.get());
                        dyes.accept(VanityArmorItems.YELLOW_DYE.get());
                        dyes.accept(VanityArmorItems.BRIGHT_YELLOW_DYE.get());
                        dyes.accept(VanityArmorItems.LIME_DYE.get());
                        dyes.accept(VanityArmorItems.BRIGHT_LIME_DYE.get());
                        dyes.accept(VanityArmorItems.GREEN_DYE.get());
                        dyes.accept(VanityArmorItems.BRIGHT_GREEN_DYE.get());
                        dyes.accept(VanityArmorItems.TEAL_DYE.get());
                        dyes.accept(VanityArmorItems.BRIGHT_TEAL_DYE.get());
                        dyes.accept(VanityArmorItems.CYAN_DYE.get());
                        dyes.accept(VanityArmorItems.BRIGHT_CYAN_DYE.get());
                        dyes.accept(VanityArmorItems.SKY_BLUE_DYE.get());
                        dyes.accept(VanityArmorItems.BRIGHT_SKY_BLUE_DYE.get());
                        dyes.accept(VanityArmorItems.BLUE_DYE.get());
                        dyes.accept(VanityArmorItems.BRIGHT_BLUE_DYE.get());
                        dyes.accept(VanityArmorItems.PURPLE_DYE.get());
                        dyes.accept(VanityArmorItems.BRIGHT_PURPLE_DYE.get());
                        dyes.accept(VanityArmorItems.VIOLET_DYE.get());
                        dyes.accept(VanityArmorItems.BRIGHT_VIOLET_DYE.get());
                        dyes.accept(VanityArmorItems.PINK_DYE.get());
                        dyes.accept(VanityArmorItems.BRIGHT_PINK_DYE.get());
                        dyes.accept(VanityArmorItems.BLACK_DYE.get());
                        dyes.accept(VanityArmorItems.GRAY_DYE.get());
                        dyes.accept(VanityArmorItems.SILVER_DYE.get());
                        dyes.accept(VanityArmorItems.BROWN_DYE.get());
                        dyes.accept(VanityArmorItems.TEAM_DYE.get());

                        CreativeModeTab.Output tuxedo_set = GroupItem.belongsTo("tuxedo_set", output);
                        tuxedo_set.accept(VanityArmorItems.TUXEDO_SHIRT.get());
                        tuxedo_set.accept(VanityArmorItems.TUXEDO_PANTS.get());
                        tuxedo_set.accept(VanityArmorItems.TUXEDO_SHOES.get());

                        CreativeModeTab.Output plumbers_set = GroupItem.belongsTo("plumbers_set", output);
                        plumbers_set.accept(VanityArmorItems.PLUMBERS_HAT.get());
                        plumbers_set.accept(VanityArmorItems.PLUMBERS_SHIRT.get());
                        plumbers_set.accept(VanityArmorItems.PLUMBERS_PANTS.get());
                        plumbers_set.accept(VanityArmorItems.PLUMBERS_SHOES.get());

                        CreativeModeTab.Output heros_set = GroupItem.belongsTo("heros_set", output);
                        heros_set.accept(VanityArmorItems.HEROS_HAT.get());
                        heros_set.accept(VanityArmorItems.HEROS_SHIRT.get());
                        heros_set.accept(VanityArmorItems.HEROS_PANTS.get());
                        heros_set.accept(VanityArmorItems.HEROS_SHOES.get());

                        CreativeModeTab.Output archaeologists_set = GroupItem.belongsTo("archaeologists_set", output);
                        archaeologists_set.accept(VanityArmorItems.ARCHAEOLOGISTS_HAT.get());
                        archaeologists_set.accept(VanityArmorItems.ARCHAEOLOGISTS_JACKET.get());
                        archaeologists_set.accept(VanityArmorItems.ARCHAEOLOGISTS_PANTS.get());
                        archaeologists_set.accept(VanityArmorItems.ARCHAEOLOGISTS_SHOES.get());

                        CreativeModeTab.Output clothiers_set = GroupItem.belongsTo("clothiers_set", output);
                        clothiers_set.accept(VanityArmorItems.CLOTHIERS_HAT.get());
                        clothiers_set.accept(VanityArmorItems.CLOTHIERS_JACKET.get());
                        clothiers_set.accept(VanityArmorItems.CLOTHIERS_PANTS.get());
                        clothiers_set.accept(VanityArmorItems.CLOTHIERS_SHOES.get());

                        CreativeModeTab.Output familiar_set = GroupItem.belongsTo("familiar_set", output);
                        familiar_set.accept(VanityArmorItems.FAMILIAR_WIG.get());
                        familiar_set.accept(VanityArmorItems.FAMILIAR_SHIRT.get());
                        familiar_set.accept(VanityArmorItems.FAMILIAR_PANTS.get());
                        familiar_set.accept(VanityArmorItems.FAMILIAR_SHOES.get());

                        CreativeModeTab.Output doctors_set = GroupItem.belongsTo("doctors_set", output);
                        doctors_set.accept(VanityArmorItems.THE_DOCTORS_SHIRT.get());
                        doctors_set.accept(VanityArmorItems.THE_DOCTORS_PANTS.get());
                        doctors_set.accept(VanityArmorItems.THE_DOCTORS_SHOES.get());

                        CreativeModeTab.Output guy_fawkes_set = GroupItem.belongsTo("guy_fawkes_set", output);
                        guy_fawkes_set.accept(VanityArmorItems.GUY_FAWKES_MASK.get());
                        guy_fawkes_set.accept(VanityArmorItems.GUY_FAWKES_HAT.get());
                        guy_fawkes_set.accept(VanityArmorItems.GUY_FAWKES_MASK_SET.get());

                        CreativeModeTab.Output mummy_set = GroupItem.belongsTo("mummy_set", output);
                        mummy_set.accept(VanityArmorItems.MUMMY_MASK.get());
                        mummy_set.accept(VanityArmorItems.MUMMY_SHIRT.get());
                        mummy_set.accept(VanityArmorItems.MUMMY_PANTS.get());
                        mummy_set.accept(VanityArmorItems.MUMMY_SHOES.get());
                    })
                    .withTabsBefore(TCTabs.ACCESSORIES.getId()).build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WARRIORS = TABS.register("warriors",
            () -> CreativeModeTab.builder().icon(IconItems.MELEE_ICON::toStack)
                    .title(Component.translatable("creativetab.confluence.warriors"))
                    .displayItems((parameters, output) -> {
                        output = new WipNotDisplayOutput(output);

                        CreativeModeTab.Output short_swords = GroupItem.belongsTo("short_swords", output);
                        short_swords.accept(SwordItems.COPPER_SHORT_SWORD.get());
                        short_swords.accept(SwordItems.TIN_SHORT_SWORD.get());
                        short_swords.accept(SwordItems.IRON_SHORT_SWORD.get());
                        short_swords.accept(SwordItems.LEAD_SHORT_SWORD.get());
                        short_swords.accept(SwordItems.SILVER_SHORT_SWORD.get());
                        short_swords.accept(SwordItems.TUNGSTEN_SHORT_SWORD.get());
                        short_swords.accept(SwordItems.GOLDEN_SHORT_SWORD.get());
                        short_swords.accept(SwordItems.PLATINUM_SHORT_SWORD.get());
                        short_swords.accept(SwordItems.GLADIUS.get());

                        CreativeModeTab.Output pre_hardmode_broadswords = GroupItem.belongsTo("pre_hardmode_broadswords", output);
                        pre_hardmode_broadswords.accept(SwordItems.CACTUS_SWORD.get());
                        pre_hardmode_broadswords.accept(SwordItems.EBONWOOD_SWORD.get());
                        pre_hardmode_broadswords.accept(SwordItems.SHADEWOOD_SWORD.get());
                        pre_hardmode_broadswords.accept(SwordItems.ASH_WOOD_SWORD.get());
                        pre_hardmode_broadswords.accept(SwordItems.PEARLWOOD_SWORD.get());
                        pre_hardmode_broadswords.accept(SwordItems.COPPER_BROADSWORD.get());
                        pre_hardmode_broadswords.accept(SwordItems.TIN_BROADSWORD.get());
                        pre_hardmode_broadswords.accept(SwordItems.LEAD_BROADSWORD.get());
                        pre_hardmode_broadswords.accept(SwordItems.SILVER_BROADSWORD.get());
                        pre_hardmode_broadswords.accept(SwordItems.TUNGSTEN_BROADSWORD.get());
                        pre_hardmode_broadswords.accept(SwordItems.GOLDEN_BROADSWORD.get());
                        pre_hardmode_broadswords.accept(SwordItems.PLATINUM_BROADSWORD.get());
                        pre_hardmode_broadswords.accept(SwordItems.FAKE_SWORD.get());
                        pre_hardmode_broadswords.accept(SwordItems.CANDY_CANE_SWORD.get());
                        pre_hardmode_broadswords.accept(SwordItems.FALCON_BLADE.get());
                        pre_hardmode_broadswords.accept(SwordItems.ZOMBIE_ARM.get());
                        pre_hardmode_broadswords.accept(SwordItems.MANDIBLE_BLADE.get());
                        pre_hardmode_broadswords.accept(SwordItems.BONE_SWORD.get());
                        pre_hardmode_broadswords.accept(SwordItems.STYLISH_SCISSORS.get());
                        pre_hardmode_broadswords.accept(SwordItems.EXOTIC_SCIMITAR.get());
                        pre_hardmode_broadswords.accept(SwordItems.BREATHING_REED.get());
                        pre_hardmode_broadswords.accept(SwordItems.UMBRELLA.get());
                        pre_hardmode_broadswords.accept(SwordItems.TRAGIC_UMBRELLA.get());
                        pre_hardmode_broadswords.accept(SwordItems.KATANA.get());
                        pre_hardmode_broadswords.accept(SwordItems.TERRAGRIM.get());
                        pre_hardmode_broadswords.accept(SwordItems.PURPLE_CLUBBERFISH.get());
                        pre_hardmode_broadswords.accept(SwordItems.LIGHTS_BANE.get());
                        pre_hardmode_broadswords.accept(SwordItems.BLOOD_BUTCHERER.get());
                        pre_hardmode_broadswords.accept(SwordItems.VOLCANO.get());
                        pre_hardmode_broadswords.accept(SwordItems.BAT_BAT.get());
                        pre_hardmode_broadswords.accept(SwordItems.TENTACLE_MACE.get());
                        pre_hardmode_broadswords.accept(SwordItems.BEE_KEEPER.get());
                        pre_hardmode_broadswords.accept(SwordItems.ICE_BLADE.get());
                        pre_hardmode_broadswords.accept(SwordItems.STARFURY.get());
                        pre_hardmode_broadswords.accept(SwordItems.ENCHANTED_SWORD.get());
                        pre_hardmode_broadswords.accept(SwordItems.BLADE_OF_GRASS.get());
                        pre_hardmode_broadswords.accept(SwordItems.NIGHTS_EDGE.get());
                        pre_hardmode_broadswords.accept(SwordItems.STAR_STEEL_SWORD.get());
                        pre_hardmode_broadswords.accept(SwordItems.RED_PHASEBLADE.get());
                        pre_hardmode_broadswords.accept(SwordItems.ORANGE_PHASEBLADE.get());
                        pre_hardmode_broadswords.accept(SwordItems.YELLOW_PHASEBLADE.get());
                        pre_hardmode_broadswords.accept(SwordItems.GREEN_PHASEBLADE.get());
                        pre_hardmode_broadswords.accept(SwordItems.BLUE_PHASEBLADE.get());
                        pre_hardmode_broadswords.accept(SwordItems.PURPLE_PHASEBLADE.get());
                        pre_hardmode_broadswords.accept(SwordItems.WHITE_PHASEBLADE.get());
                        pre_hardmode_broadswords.accept(SwordItems.BROKEN_SWEET_SWORD.get());
                        pre_hardmode_broadswords.accept(SwordItems.SWEET_SWORD.get());
                        pre_hardmode_broadswords.accept(SwordItems.MURAMASA.get());
                        pre_hardmode_broadswords.accept(SwordItems.DEVELOPER_SWORD.get());
                        pre_hardmode_broadswords.accept(SwordItems.CROWBAR.get());

                        CreativeModeTab.Output hardmode_broadswords = GroupItem.belongsTo("hardmode_broadswords", output);
                        hardmode_broadswords.accept(SwordItems.COBALT_SWORD.get());
                        hardmode_broadswords.accept(SwordItems.PALLADIUM_SWORD.get());
                        hardmode_broadswords.accept(SwordItems.MYTHRIL_SWORD.get());
                        hardmode_broadswords.accept(SwordItems.ORICHALCUM_SWORD.get());
                        hardmode_broadswords.accept(SwordItems.ADAMANTITE_SWORD.get());
                        hardmode_broadswords.accept(SwordItems.TITANIUM_SWORD.get());
                        hardmode_broadswords.accept(SwordItems.BREAKER_BLADE.get());
                        hardmode_broadswords.accept(SwordItems.WAFFLES_IRON.get());

                        CreativeModeTab.Output yoyo = GroupItem.belongsTo("yoyo", output);
                        yoyo.accept(TEYoyosItems.AMAZON.get());
                        yoyo.accept(TEYoyosItems.ARTERY.get());
                        yoyo.accept(TEYoyosItems.CASCADE.get());
                        yoyo.accept(TEYoyosItems.CODE_1.get());
                        yoyo.accept(TEYoyosItems.HIVE_FIVE.get());
                        yoyo.accept(TEYoyosItems.MALAISE.get());
                        yoyo.accept(TEYoyosItems.RALLY.get());
                        yoyo.accept(TEYoyosItems.VALOR.get());
                        yoyo.accept(TEYoyosItems.WOODEN_YOYO.get());
                        acceptAll(TEBoomerangItems.ITEMS, output, "boomerang");
                        acceptAll(SpearItems.ITEMS, output, "spear");
                        acceptAll(LanceItems.ITEMS, output, "lance");
                    })
                    .withTabsBefore(ARMORS.getId())
                    .build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SHOOTERS = TABS.register("rangers",
            () -> CreativeModeTab.builder().icon(IconItems.REMOTE_ICON::toStack)
                    .title(Component.translatable("creativetab.confluence.rangers"))
                    .displayItems((parameters, output) -> {
                        output = new WipNotDisplayOutput(output);

                        CreativeModeTab.Output short_bow = GroupItem.belongsTo("short_bow", output);
                        short_bow.accept(BowItems.WOODEN_SHORT_BOW.get());
                        short_bow.accept(BowItems.EBONWOOD_SHORT_BOW.get());
                        short_bow.accept(BowItems.SHADEWOOD_SHORT_BOW.get());
                        short_bow.accept(BowItems.ASH_WOOD_SHORT_BOW.get());
                        short_bow.accept(BowItems.PEARLWOOD_SHORT_BOW.get());
                        short_bow.accept(BowItems.COPPER_SHORT_BOW.get());
                        short_bow.accept(BowItems.TIN_SHORT_BOW.get());
                        short_bow.accept(BowItems.IRON_SHORT_BOW.get());
                        short_bow.accept(BowItems.LEAD_SHORT_BOW.get());
                        short_bow.accept(BowItems.SILVER_SHORT_BOW.get());
                        short_bow.accept(BowItems.TUNGSTEN_SHORT_BOW.get());
                        short_bow.accept(BowItems.GOLDEN_SHORT_BOW.get());
                        short_bow.accept(BowItems.PLATINUM_SHORT_BOW.get());

                        CreativeModeTab.Output bow = GroupItem.belongsTo("bow", output);
                        bow.accept(BowItems.EBONWOOD_BOW.get());
                        bow.accept(BowItems.SHADEWOOD_BOW.get());
                        bow.accept(BowItems.ASH_WOOD_BOW.get());
                        bow.accept(BowItems.PEARLWOOD_BOW.get());
                        bow.accept(BowItems.COPPER_BOW.get());
                        bow.accept(BowItems.TIN_BOW.get());
                        bow.accept(BowItems.IRON_BOW.get());
                        bow.accept(BowItems.LEAD_BOW.get());
                        bow.accept(BowItems.SILVER_BOW.get());
                        bow.accept(BowItems.TUNGSTEN_BOW.get());
                        bow.accept(BowItems.GOLDEN_BOW.get());
                        bow.accept(BowItems.PLATINUM_BOW.get());
                        bow.accept(BowItems.HUNTING_BOW.get());
                        bow.accept(BowItems.FOSSIL_BOW.get());
                        bow.accept(BowItems.DEMON_BOW.get());
                        bow.accept(BowItems.TENDON_BOW.get());
                        bow.accept(BowItems.THE_BEES_KNEES.get());
                        bow.accept(BowItems.MOLTEN_FURY.get());
                        bow.accept(BowItems.HELLWING_BOW.get());
                        bow.accept(BowItems.SCAREBOW.get());
                        bow.accept(BowItems.DAEDALUS_STORM_BOW.get());
                        bow.accept(BowItems.DEVELOPER_BOW.get());

                        acceptAll(CrossbowItems.ITEMS, output, "crossbow");
                        acceptAll(ArrowItems.ITEMS, output, "arrow");

                        CreativeModeTab.Output gun = GroupItem.belongsTo("gun", output);
                        acceptAll(TGItems.GUNS, gun);
                        acceptAll(GunItems.ITEMS, gun);
                        gun.accept(ManaWeaponItems.BEE_GUN);
                        gun.accept(ManaWeaponItems.SPACE_GUN);
                        acceptAll(TGItems.BULLETS, output, "bullet");
                    })
                    .withTabsBefore(WARRIORS.getId())
                    .build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAGES = TABS.register("mages",
            () -> CreativeModeTab.builder().icon(IconItems.MAGIC_ICON::toStack)
                    .title(Component.translatable("creativetab.confluence.mages"))
                    .displayItems((parameters, output) -> {
                        output = new WipNotDisplayOutput(output);

                        acceptAll(ManaWeaponItems.ITEMS, output);
                        HolderLookup.RegistryLookup<Enchantment> registryLookup = parameters.holders().lookupOrThrow(Registries.ENCHANTMENT);
                        output.accept(EnchantmentUtils.enchantedBook(registryLookup, ModEnchantments.MANA_REGENERATION, 3));
                        output.accept(EnchantmentUtils.enchantedBook(registryLookup, ModEnchantments.EFFICIENT_MAGIC, 1));
                        output.accept(EnchantmentUtils.enchantedBook(registryLookup, ModEnchantments.MANA_MENDING, 3));
                        output.accept(EnchantmentUtils.enchantedBook(registryLookup, ModEnchantments.CELESTIAL_ABSORPTION, 2));
                        output.accept(EnchantmentUtils.enchantedBook(registryLookup, ModEnchantments.SOOTHED_MANA, 2));
                        output.accept(EnchantmentUtils.enchantedBook(registryLookup, ModEnchantments.ARCANE_PROTECTION, 4));
                        output.accept(EnchantmentUtils.enchantedBook(registryLookup, ModEnchantments.SPELL_DESPERATION, 2));
                        output.accept(EnchantmentUtils.enchantedBook(registryLookup, ModEnchantments.MYSTIC_SURGE, 2));
                    })
                    .withTabsBefore(SHOOTERS.getId())
                    .build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SUMMONERS = TABS.register("summoners",
            () -> CreativeModeTab.builder().icon(IconItems.SUMMON_ICON::toStack)
                    .title(Component.translatable("creativetab.confluence.summoners"))
                    .displayItems((parameters, output) -> {
                        output = new WipNotDisplayOutput(output);

                        acceptAll(TESummonItems.ITEMS, output);
                        acceptAll(TEWhipItems.ITEMS, output);
                        acceptAll(LightPetItems.ITEMS, output);
                    })
                    .withTabsAfter(TEItems.NEO_TERRA.getId())
                    .withTabsBefore(MAGES.getId())
                    .build());
    /* 生物 */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DEVELOPER = TABS.register("developer",
            () -> CreativeModeTab.builder().icon(IconItems.DEVELOPER_ICON::toStack)
                    .title(Component.translatable("creativetab.confluence.developer"))
                    .displayItems((parameters, output) -> {
                        output = new WipNotDisplayOutput(output);

                        output.accept(ModBlocks.ANDESITE_CASING);
                        acceptAll(ModItems.HIDDEN, output);
                        output.accept(FoodItems.PINK_COLA);
                        output.accept(FoodItems.DONGDONGS_FLATBREAD);
                        output.accept(FoodItems.PIGLIN_STEW);
                        output.accept(TEBoomerangItems.BeiDou_BOOMERANG);
                        output.accept(ToolItems.DEV_BUG_NET);
                        output.accept(SwordItems.DEVELOPER_SWORD);
                        output.accept(TEBoomerangItems.DEVELOPER_BOOMERANG);
                        output.accept(BowItems.DEVELOPER_BOW);
                        output.accept(FishingPoleItems.DEV_FISHING_ROD);
                        output.accept(ModBlocks.TEST_BLOCK);
                        output.accept(ModBlocks.AETHERIUM_CAULDRON);
                        output.accept(ModBlocks.HONEY_CAULDRON);
                    })
                    .withTabsBefore(TEItems.NEO_TERRA.getId())
                    .build());

    private static void acceptAll(DeferredRegister.Items register, CreativeModeTab.Output output) {
        register.getEntries().forEach(holder -> output.accept(holder.get()));
    }

    private static void acceptAll(DeferredRegister.Items register, CreativeModeTab.Output output, String group) {
        List<ItemStack> values = register.getEntries().stream()
                .map(holder -> holder.get().getDefaultInstance())
                .toList();
        output.accept(GroupItem.of(Confluence.asResource(group), values));
    }

    private static void acceptAll(DeferredRegister.Blocks register, CreativeModeTab.Output output) {
        register.getEntries().forEach(holder -> {
            Item item = holder.get().asItem();
            if (item != Items.AIR) {
                output.accept(item);
            }
        });
    }

    private static void acceptAll(DeferredRegister.Blocks register, CreativeModeTab.Output output, String group) {
        List<ItemStack> values = register.getEntries().stream()
                .filter(holder -> holder.get().asItem() != Items.AIR)
                .map(holder -> holder.get().asItem().getDefaultInstance())
                .toList();
        output.accept(GroupItem.of(Confluence.asResource(group), values));
    }

    private static <T extends ItemLike> void acceptAll(Collection<? extends DeferredHolder<T, ? extends T>> holders, CreativeModeTab.Output output) {
        holders.forEach(holder -> output.accept(holder.get()));
    }
}
