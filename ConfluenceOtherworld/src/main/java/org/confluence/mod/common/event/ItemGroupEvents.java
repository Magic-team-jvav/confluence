package org.confluence.mod.common.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.confluence.lib.api.event.CustomGroupItemIconEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTabs;
import org.confluence.mod.common.init.block.*;
import org.confluence.mod.common.init.item.*;
import org.confluence.terra_curio.common.init.TCItems;

@EventBusSubscriber(modid = Confluence.MODID)
public final class ItemGroupEvents {
    @SubscribeEvent
    public static void customGroupItemIcon(CustomGroupItemIconEvent event) {
        event.register(ModTabs.NATURAL_BLOCKS.getKey(), helper -> {
            helper.register(Confluence.asResource("ebony"), NatureBlocks.EBONY_LOG_BLOCKS.LOG.toStack());
            helper.register(Confluence.asResource("pearl"), NatureBlocks.PEARL_LOG_BLOCKS.LOG.toStack());
            helper.register(Confluence.asResource("shadow"), NatureBlocks.SHADOW_LOG_BLOCKS.LOG.toStack());
            helper.register(Confluence.asResource("palm"), NatureBlocks.PALM_LOG_BLOCKS.LOG.toStack());
            helper.register(Confluence.asResource("baobab"), NatureBlocks.BAOBAB_LOG_BLOCKS.LOG.toStack());
            helper.register(Confluence.asResource("yellow_willow"), NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.LOG.toStack());
            helper.register(Confluence.asResource("spooky"), NatureBlocks.SPOOKY_LOG_BLOCKS.LOG.toStack());
            helper.register(Confluence.asResource("living"), NatureBlocks.LIVING_LOG_BLOCKS.LOG.toStack());
            helper.register(Confluence.asResource("living_mahogany"), NatureBlocks.LIVING_MAHOGANY_LOG_BLOCKS.LOG.toStack());
            helper.register(Confluence.asResource("ash"), NatureBlocks.ASH_LOG_BLOCKS.LOG.toStack());
            helper.register(Confluence.asResource("void"), NatureBlocks.VOID_LOG_BLOCKS.LOG.toStack());
            helper.register(Confluence.asResource("gaze"), NatureBlocks.GAZE_LOG_BLOCKS.LOG.toStack());
            helper.register(Confluence.asResource("moonglow_willow"), NatureBlocks.MOONGLOW_WILLOW_LOG_BLOCKS.LOG.toStack());
            helper.register(Confluence.asResource("dynasty"), NatureBlocks.DYNASTY_LOG_BLOCKS.LOG.toStack());
            helper.register(Confluence.asResource("pine"), NatureBlocks.PINE_LOG_BLOCKS.LOG.toStack());
            helper.register(Confluence.asResource("fey"), NatureBlocks.FEY_LOG_BLOCKS.LOG.toStack());
            helper.register(Confluence.asResource("stone_tree"), NatureBlocks.STONY_LOG.toStack());
            helper.register(Confluence.asResource("pot"), PotBlocks.FOREST_POT.toStack());

            helper.register(Confluence.asResource("natural_environment"), NatureBlocks.LIFE_CRYSTAL_BLOCK.toStack());
            helper.register(Confluence.asResource("corruption"), NatureBlocks.CORRUPT_GRASS_BLOCK.toStack());
            helper.register(Confluence.asResource("hallow"), NatureBlocks.HALLOW_GRASS_BLOCK.toStack());
            helper.register(Confluence.asResource("crimson"), NatureBlocks.CRIMSON_GRASS_BLOCK.toStack());
            helper.register(Confluence.asResource("mushroom"), NatureBlocks.MUSHROOM_GRASS_BLOCK.toStack());
            helper.register(Confluence.asResource("desert"), NatureBlocks.HARDENED_SAND_BLOCK.toStack());
            helper.register(Confluence.asResource("jungle"), NatureBlocks.JUNGLE_GRASS_BLOCK.toStack());
            helper.register(Confluence.asResource("end"), NatureBlocks.VOID_GRASS_BLOCK.toStack());
            helper.register(Confluence.asResource("nether"), NatureBlocks.ASH_GRASS_BLOCK.toStack());
            helper.register(Confluence.asResource("skyland"), NatureBlocks.CLOUD_BLOCK.toStack());
            helper.register(Confluence.asResource("snow"), NatureBlocks.THIN_ICE_BLOCK.toStack());
            helper.register(Confluence.asResource("ocean"), NatureBlocks.DIATOMACEOUS.toStack());
            helper.register(Confluence.asResource("crops"), NatureBlocks.WHITE_PUMPKIN.toStack());
            helper.register(Confluence.asResource("shimmer"), NatureBlocks.AETHERIUM_BLOCK.toStack());
            helper.register(Confluence.asResource("moss"), NatureBlocks.LAVA_MOSS.toStack());
            helper.register(Confluence.asResource("special_plants"), NatureBlocks.CRIMSON_VENUS_FLYTRAP_BLOCK.toStack());
            helper.register(Confluence.asResource("miscellaneous"), DecorativeBlocks.LOST_PAPER_BLOCK.toStack());
            helper.register(Confluence.asResource("sanctification_ores"), OreBlocks.SANCTIFICATION_DIAMOND_ORE.toStack());
            helper.register(Confluence.asResource("corruption_ores"), OreBlocks.CORRUPTION_DIAMOND_ORE.toStack());
            helper.register(Confluence.asResource("fleshification_ores"), OreBlocks.FLESHIFICATION_DIAMOND_ORE.toStack());
            helper.register(Confluence.asResource("normal_ores"), OreBlocks.PLATINUM_ORE.toStack());
            helper.register(Confluence.asResource("raw_ore_blocks"), OreBlocks.RAW_PLATINUM_BLOCK.toStack());
            helper.register(Confluence.asResource("ore_storage_blocks"), OreBlocks.PLATINUM_BLOCK.toStack());
        });

        event.register(ModTabs.BUILDING_BLOCKS.getKey(), helper -> {
            helper.register(Confluence.asResource("gloom_obsidian_bricks"), DecorativeBlocks.GLOOM_OBSIDIAN_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("blue_ice_bricks"), DecorativeBlocks.BLUE_ICE_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("packed_ice_bricks"), DecorativeBlocks.PACKED_ICE_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("sandstone_bricks"), DecorativeBlocks.SANDSTONE_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("red_sandstone_bricks"), DecorativeBlocks.RED_SANDSTONE_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("ebonsandstone_bricks"), DecorativeBlocks.EBONSANDSTONE_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("pearlsandstone_bricks"), DecorativeBlocks.PEARLSANDSTONE_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("crimsandstone_bricks"), DecorativeBlocks.CRIMSANDSTONE_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("snow_bricks"), DecorativeBlocks.SNOW_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("aetherium_bricks"), DecorativeBlocks.AETHERIUM_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("rainbow_bricks"), DecorativeBlocks.RAINBOW_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("copper_bricks"), DecorativeBlocks.COPPER_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("tin_bricks"), DecorativeBlocks.TIN_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("iron_bricks"), DecorativeBlocks.IRON_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("lead_bricks"), DecorativeBlocks.LEAD_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("silver_bricks"), DecorativeBlocks.SILVER_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("tungsten_bricks"), DecorativeBlocks.TUNGSTEN_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("golden_bricks"), DecorativeBlocks.GOLDEN_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("platinum_bricks"), DecorativeBlocks.PLATINUM_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("demonite_ore_bricks"), DecorativeBlocks.DEMONITE_ORE_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("ebonstone_bricks"), DecorativeBlocks.EBONSTONE_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("meteorite_bricks"), DecorativeBlocks.METEORITE_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("crimtane_ore_bricks"), DecorativeBlocks.CRIMTANE_ORE_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("crimstone_bricks"), DecorativeBlocks.CRIMSTONE_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("pearlstone_bricks"), DecorativeBlocks.PEARLSTONE_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("sun_plate"), DecorativeBlocks.SUN_PLATE.FULL.toStack());
            helper.register(Confluence.asResource("disc_block"), DecorativeBlocks.DISC_BLOCK.FULL.toStack());
            helper.register(Confluence.asResource("moon_plate"), DecorativeBlocks.MOON_PLATE.FULL.toStack());
            helper.register(Confluence.asResource("obsidian_bricks"), DecorativeBlocks.OBSIDIAN_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("granite_bricks"), DecorativeBlocks.GRANITE_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("marble_bricks"), DecorativeBlocks.MARBLE_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("blue_bricks"), DecorativeBlocks.BLUE_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("green_bricks"), DecorativeBlocks.GREEN_BRICKS.FULL.toStack());
            helper.register(Confluence.asResource("pink_bricks"), DecorativeBlocks.PINK_BRICKS.FULL.toStack());

            helper.register(Confluence.asResource("glass"), DecorativeBlocks.PURE_GLASS.toStack());
            helper.register(Confluence.asResource("special_building"), DecorativeBlocks.SWORD_IN_STONE.toStack());
            helper.register(Confluence.asResource("chains"), DecorativeBlocks.DIAMOND_CHAIN.toStack());
            helper.register(Confluence.asResource("doors"), DecorativeBlocks.TRADITIONAL_DYNASTY_DOOR.toStack());
            helper.register(Confluence.asResource("statue"), StatueBlocks.A_STATUE.toStack());
            helper.register(Confluence.asResource("boss_relics"), DecorativeBlocks.KING_SLIME_RELIC.toStack());
            helper.register(Confluence.asResource("balloons"), DecorativeBlocks.RED_BALLOON.toStack());
            helper.register(Confluence.asResource("gem_blocks"), DecorativeBlocks.RUBY_BLOCK.toStack());
            helper.register(Confluence.asResource("fur_wool"), DecorativeBlocks.RAINBOW_WOOL.toStack());
        });

        event.register(ModTabs.MECHANICAL.getKey(), helper -> {
            helper.register(Confluence.asResource("boulders"), FunctionalBlocks.NORMAL_BOULDER.toStack());
            helper.register(Confluence.asResource("redstone_circuit_traps"), FunctionalBlocks.DART_TRAP.toStack());
            helper.register(Confluence.asResource("trigger"), FunctionalBlocks.SWITCH.toStack());
            helper.register(Confluence.asResource("crafting_stations"), FunctionalBlocks.HEAVY_WORK_BENCH.toStack());
            helper.register(Confluence.asResource("storage"), ChestBlocks.ICE_CHEST.toStack());
            helper.register(Confluence.asResource("souls"), FunctionalBlocks.SOUL_OF_LIGHT_IN_A_BOTTLE.toStack());
            helper.register(Confluence.asResource("misc_functional"), FunctionalBlocks.SPIKE.toStack());
        });

        event.register(ModTabs.MATERIALS.getKey(), helper -> {
            helper.register(Confluence.asResource("metal_materials"), MaterialItems.PLATINUM_INGOT.toStack());
            helper.register(Confluence.asResource("natural_materials"), MaterialItems.RUBY.toStack());
            helper.register(Confluence.asResource("souls_special"), MaterialItems.SOUL_OF_LIGHT.toStack());
            helper.register(Confluence.asResource("monster_drops"), MaterialItems.LENS.toStack());
            helper.register(Confluence.asResource("plants_herbs"), MaterialItems.DAYBLOOM.toStack());
            helper.register(Confluence.asResource("crafting_materials"), MaterialItems.SILK.toStack());
        });

        event.register(ModTabs.MISC.getKey(), helper -> {
            helper.register(Confluence.asResource("treasure_bag"), TreasureBagItems.KING_SLIME_TREASURE_BAG.toStack());
            helper.register(Confluence.asResource("tombstone"), ModBlocks.TOMBSTONE.toStack());
            helper.register(Confluence.asResource("bait"), BaitItems.MASTER_BAIT.toStack());
            helper.register(Confluence.asResource("quested_fish"), QuestedFishes.FALLEN_STARFISH.toStack());
            // 跳过了宝匣和油漆，投掷武器
            helper.register(Confluence.asResource("bombs_explosives"), ConsumableItems.BOMB.toStack());
            // 跳过了boss召唤物
            helper.register(Confluence.asResource("environment_items"), ConsumableItems.PURIFICATION_POWDER.toStack());
            helper.register(Confluence.asResource("gain"), ConsumableItems.LIFE_CRYSTAL.toStack());
            helper.register(Confluence.asResource("loot_gifts"), ConsumableItems.CHRISTMAS_GIFT.toStack());
        });

        event.register(ModTabs.TOOLS.getKey(), helper -> {
            // 跳过了绳子，魔棒，电路工具，钥匙，虫网,园艺剪，船，箱船
            helper.register(Confluence.asResource("buckets_liquids"), ToolItems.BOTTOMLESS_SHIMMER_BUCKET.toStack());
            helper.register(Confluence.asResource("utility_tools"), TCItems.MAGIC_MIRROR.toStack());
            helper.register(Confluence.asResource("axe"), AxeItems.PLATINUM_AXE.toStack());
            helper.register(Confluence.asResource("pickaxe"), PickaxeItems.PLATINUM_PICKAXE.toStack());
            helper.register(Confluence.asResource("pickaxe_axe"), PickaxeAxeItems.PICKAXE_AXE.toStack());
            helper.register(Confluence.asResource("drill"), DrillItems.TITANIUM_DRILL.toStack());
            helper.register(Confluence.asResource("chainsaw"), ChainsawItems.TITANIUM_CHAINSAW.toStack());
            helper.register(Confluence.asResource("hamaxe"), HamaxeItems.METEOR_HAMAXE.toStack());
            helper.register(Confluence.asResource("how_shovel"), HoeShovelItems.METEOR_HOE_SHOVEL.toStack());
            helper.register(Confluence.asResource("hammer"), HammerItems.PLATINUM_HAMMER.toStack());
            helper.register(Confluence.asResource("hook"), HookItems.GRAPPLING_HOOK.toStack());
            helper.register(Confluence.asResource("minecart"), MinecartItems.DIAMOND_MINECART.toStack());
            helper.register(Confluence.asResource("fishing_pole"), FishingPoleItems.GOLDEN_FISHING_ROD.toStack());
            helper.register(Confluence.asResource("hoe"), HoeItems.PLATINUM_HOE.toStack());
            helper.register(Confluence.asResource("shovel"), ShovelItems.PLATINUM_SHOVEL.toStack());
        });
        // 跳过了近战武器
    }
}
