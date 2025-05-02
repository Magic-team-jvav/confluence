package org.confluence.mod.common.data.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.DataMapProvider;
import org.confluence.mod.common.component.ValueComponent;
import org.confluence.mod.common.init.ModDataMaps;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.block.OreBlocks;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.mixin.accessor.DataMapProviderAccessor;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_guns.common.init.TGItems;
import org.confluence.terraentity.init.item.TEBoomerangItems;

import java.util.concurrent.CompletableFuture;

public class ModDataMapProvider extends DataMapProvider {
    public ModDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        valueBuilder()
            .add(Items.DRAGON_EGG, 10000)
            .add(Items.NETHER_STAR, 5000)

            .add(Items.NETHERITE_SWORD, 6000)
            .add(Items.NETHERITE_PICKAXE, 6000)
            .add(Items.NETHERITE_AXE, 6000)

            .add(Items.DIAMOND_SWORD, 3000)
            .add(Items.DIAMOND_AXE, 3000)

            .add(Items.GOLDEN_SWORD, 2800)
            .add(Items.GOLDEN_PICKAXE, 2800)
            .add(Items.GOLDEN_AXE, 2800)

            .add(Items.IRON_SWORD,2800)
            .add(Items.IRON_AXE,2800)

            .add(Items.COPPER_INGOT, 150)
            .add(Items.IRON_INGOT, 300)
            .add(Items.GOLD_INGOT, 1200)
            .add(Items.NETHERITE_INGOT,4000)

            .add(Items.DIAMOND, 3000)
            .add(Items.EMERALD, 1500)
            
            .add(Items.LAPIS_LAZULI, 50)
            .add(Items.REDSTONE, 40)
            .add(Items.COAL, 40)
            .add(Items.CHARCOAL, 40)

            .add(Blocks.COPPER_BLOCK.asItem(), 150 * 9)
            .add(Blocks.IRON_BLOCK.asItem(),  300 * 9)
            .add(Blocks.GOLD_BLOCK.asItem(),  1200 * 9)
            .add(Blocks.NETHERITE_BLOCK.asItem(),4000 * 9)

            .add(Blocks.DIAMOND_BLOCK.asItem(),3000 * 9)
            .add(Blocks.EMERALD_BLOCK.asItem(),1500 * 9)

            .add(Blocks.LAPIS_BLOCK.asItem(), 50 * 9)
            .add(Blocks.REDSTONE_BLOCK.asItem(), 40 * 9)
            .add(Blocks.COAL_BLOCK.asItem(), 40 * 9)

            .add(Items.ARROW,5)

            .add(Blocks.ANVIL.asItem(),20000)
            .add(Blocks.TORCH.asItem(),50)
            .add(Blocks.OAK_SAPLING.asItem(),100)

            .add(MaterialItems.TIN_INGOT, 225)
            .add(MaterialItems.LEAD_INGOT,450)
            .add(MaterialItems.SILVER_INGOT, 600)
            .add(MaterialItems.TUNGSTEN_INGOT, 900)
            .add(MaterialItems.PLATINUM_INGOT, 1800)
            .add(MaterialItems.METEORITE_INGOT,1400)
            .add(MaterialItems.DEMONITE_INGOT, 3000)
            .add(MaterialItems.TR_CRIMSON_INGOT, 3900)
            .add(MaterialItems.HELLSTONE_INGOT, 4000)

            .add(OreBlocks.TIN_BLOCK, 225 * 9)
            .add(OreBlocks.LEAD_BLOCK,450 * 9)
            .add(OreBlocks.SILVER_BLOCK, 600 * 9)
            .add(OreBlocks.TUNGSTEN_BLOCK, 900 * 9)
            .add(OreBlocks.PLATINUM_BLOCK, 1800 * 9)
            .add(OreBlocks.METEORITE_BLOCK,1400 * 9)
            .add(OreBlocks.DEMONITE_BLOCK, 3000 * 9)
            .add(OreBlocks.TR_CRIMSON_BLOCK, 3900 * 9)
            .add(OreBlocks.HELLSTONE_BLOCK, 4000 * 9)

            .add(MaterialItems.COBALT_INGOT, 2100)
            .add(MaterialItems.PALLADIUM_INGOT, 2700)
            .add(MaterialItems.MYTHRIL_INGOT, 4400)
            .add(MaterialItems.ORICHALCUM_INGOT, 5200)
            .add(MaterialItems.ADAMANTITE_INGOT, 6000)
            .add(MaterialItems.TITANIUM_INGOT, 6800)
            .add(MaterialItems.HALLOWED_INGOT, 4000)
            .add(MaterialItems.CHLOROPHYTE_INGOT, 9000)
            .add(MaterialItems.SHROOMITE_INGOT, 10000)
            .add(MaterialItems.SPECTRE_INGOT, 10000)
            .add(MaterialItems.LUMINITE_INGOT, 12000)

            .add(OreBlocks.COBALT_BLOCK, 2100 * 9)
            .add(OreBlocks.PALLADIUM_BLOCK, 2700 * 9)
            .add(OreBlocks.MYTHRIL_BLOCK, 4400 * 9)
            .add(OreBlocks.ORICHALCUM_BLOCK, 5200 * 9)
            .add(OreBlocks.ADAMANTITE_BLOCK, 6000 * 9)
            .add(OreBlocks.TITANIUM_BLOCK, 6800 * 9)
            .add(OreBlocks.HALLOWED_BLOCK, 4000 * 9)
            .add(OreBlocks.CHLOROPHYTE_BLOCK, 9000 * 9)
            .add(OreBlocks.SHROOMITE_BLOCK, 10000 * 9)
            .add(OreBlocks.SPECTRE_BLOCK, 10000 * 9)
            .add(OreBlocks.LUMINITE_BLOCK, 12000 * 9)

            .add(Items.RAW_COPPER, 50)
            .add(Items.RAW_IRON, 100)
            .add(Items.RAW_GOLD, 300)

            .add(Blocks.RAW_COPPER_BLOCK.asItem(), 50 * 9)
            .add(Blocks.RAW_IRON_BLOCK.asItem(), 100 * 9)
            .add(Blocks.RAW_GOLD_BLOCK.asItem(), 300 * 9)

            .add(MaterialItems.RAW_TIN, 75)
            .add(MaterialItems.RAW_LEAD, 150)
            .add(MaterialItems.RAW_SILVER, 150)
            .add(MaterialItems.RAW_TUNGSTEN, 225)
            .add(MaterialItems.RAW_PLATINUM, 450)
            .add(MaterialItems.RAW_METEORITE, 200)
            .add(MaterialItems.RAW_DEMONITE, 1000)
            .add(MaterialItems.RAW_TR_CRIMSON, 1300)
            .add(MaterialItems.RAW_HELLSTONE, 250)

            .add(OreBlocks.RAW_TIN_BLOCK, 75 * 9)
            .add(OreBlocks.RAW_LEAD_BLOCK, 150 * 9)
            .add(OreBlocks.RAW_SILVER_BLOCK, 150 * 9)
            .add(OreBlocks.RAW_TUNGSTEN_BLOCK, 225 * 9)
            .add(OreBlocks.RAW_PLATINUM_BLOCK, 450 * 9)
            .add(OreBlocks.RAW_METEORITE_BLOCK, 200 * 9)
            .add(OreBlocks.RAW_DEMONITE_BLOCK, 1000 * 9)
            .add(OreBlocks.RAW_TR_CRIMSON_BLOCK, 1300 * 9)
            .add(OreBlocks.RAW_HELLSTONE_BLOCK, 250 * 9)

            .add(MaterialItems.RAW_COBALT, 700)
            .add(MaterialItems.RAW_PALLADIUM, 900)
            .add(MaterialItems.RAW_MYTHRIL, 1100)
            .add(MaterialItems.RAW_ORICHALCUM, 1300)
            .add(MaterialItems.RAW_ADAMANTITE, 1500)
            .add(MaterialItems.RAW_TITANIUM, 1700)
            .add(MaterialItems.RAW_CHLOROPHYTE, 1500)
            .add(MaterialItems.RAW_LUMINITE, 3000)

            .add(OreBlocks.RAW_COBALT_BLOCK, 700 * 9)
            .add(OreBlocks.RAW_PALLADIUM_BLOCK, 900 * 9)
            .add(OreBlocks.RAW_MYTHRIL_BLOCK, 1100 * 9)
            .add(OreBlocks.RAW_ORICHALCUM_BLOCK, 1300 * 9)
            .add(OreBlocks.RAW_ADAMANTITE_BLOCK, 1500 * 9)
            .add(OreBlocks.RAW_TITANIUM_BLOCK, 1700 * 9)
            .add(OreBlocks.RAW_CHLOROPHYTE_BLOCK, 1500 * 9)
            .add(OreBlocks.RAW_LUMINITE_BLOCK, 3000 * 9)

            .add(MaterialItems.GEL, 1)
            .add(MaterialItems.PINK_GEL, 3)
            .add(MaterialItems.SILK, 200)
            .add(MaterialItems.RAW_ASPHALT, 1)

            .add(SwordItems.VOLCANO, 5400)
            .add(SwordItems.BLOOD_BUTCHERER, 2700)
            .add(SwordItems.LIGHTS_BANE, 2700)
            .add(SwordItems.ENCHANTED_SWORD, 30000)
            .add(SwordItems.TERRAGRIM, 50000)
            .add(SwordItems.ICE_BLADE, 4000)
            .add(SwordItems.CACTUS_SWORD, 360)
            .add(SwordItems.ZOMBIE_ARM, 2800)
            .add(SwordItems.MANDIBLE_BLADE, 7000)
            .add(SwordItems.COPPER_SHORT_SWORD, 210)
            .add(SwordItems.COPPER_BOARD_SWORD, 630)
            .add(SwordItems.TIN_SHORT_SWORD, 315)
            .add(SwordItems.TIN_BOARD_SWORD, 945)
            .add(SwordItems.KATANA,100000)

            .add(TEBoomerangItems.FLAMARANG, 20000)
            .add(TEBoomerangItems.TRIMARANG, 20000)
            .add(TEBoomerangItems.ICE_BOOMERANG, 10000)
            .add(TEBoomerangItems.SHROOMERANG, 6000)
            .add(TEBoomerangItems.ENCHANTED_BOOMERANG, 10000)
            .add(TEBoomerangItems.WOOD_BOOMERANG, 10000)
            .add(SwordItems.STYLISH_SCISSORS, 350)

            .add(BowItems.MOLTEN_FURY, 5400)
            .add(BowItems.TENDON_BOW, 3600)
            .add(BowItems.DEMON_BOW, 3600)
            .add(BowItems.FOSSIL_BOW, 3000)
            .add(BowItems.HUNTING_BOW, 3800)
            .add(BowItems.PLATINUM_BOW, 2100)
            .add(BowItems.PLATINUM_SHORT_BOW, 2100)
            .add(BowItems.GOLDEN_BOW, 1400)
            .add(BowItems.GOLDEN_SHORT_BOW, 1400)
            .add(BowItems.TUNGSTEN_BOW, 1050)
            .add(BowItems.TUNGSTEN_SHORT_BOW, 1050)
            .add(BowItems.SILVER_BOW, 700)
            .add(BowItems.SILVER_SHORT_BOW, 700)
            .add(BowItems.LEAD_BOW, 420)
            .add(BowItems.LEAD_SHORT_BOW, 420)
            .add(BowItems.IRON_BOW, 280)
            .add(BowItems.IRON_SHORT_BOW, 280)
            .add(BowItems.TIN_BOW, 150)
            .add(BowItems.TIN_SHORT_BOW, 150)
            .add(BowItems.COPPER_BOW, 70)
            .add(BowItems.COPPER_SHORT_BOW, 70)
            .add(BowItems.WOODEN_SHORT_BOW, 40)
            .add(Items.BOW, 40)

            .add(PickaxeItems.MOLTEN_PICKAXE, 5400)
            .add(PickaxeItems.DEATHBRINGER_PICKAXE, 3600)
            .add(PickaxeItems.NIGHTMARE_PICKAXE, 3600)
            .add(PickaxeItems.PLATINUM_PICKAXE, 3000)
            .add(PickaxeItems.GOLDEN_PICKAXE, 2000)
            .add(PickaxeItems.TUNGSTEN_PICKAXE, 1500)
            .add(PickaxeItems.SILVER_PICKAXE, 1000)
            .add(PickaxeItems.LEAD_PICKAXE, 600)
            .add(Items.IRON_PICKAXE, 400)
            .add(PickaxeItems.TIN_PICKAXE, 150)
            .add(PickaxeItems.COPPER_PICKAXE, 50)

            .add(HammerItems.THE_BREAKER, 3000)
            .add(HammerItems.FLESH_GRINDER, 3000)
            .add(HammerItems.PLATINUM_HAMMER, 2400)
            .add(HammerItems.GOLDEN_HAMMER, 1600)
            .add(HammerItems.TUNGSTEN_HAMMER, 1200)
            .add(HammerItems.SILVER_HAMMER, 800)
            .add(HammerItems.LEAD_HAMMER, 480)
            .add(HammerItems.IRON_HAMMER, 320)
            .add(HammerItems.TIN_HAMMER, 120)
            .add(HammerItems.COPPER_HAMMER, 80)
            .add(HammerItems.WOODEN_HAMMER, 10)

            .add(AxeItems.WAR_AXE_OF_THE_NIGHT, 2700)
            .add(AxeItems.BLOOD_LUST_CLUSTER, 2700)
            .add(AxeItems.PLATINUM_AXE, 1200)
            .add(AxeItems.GOLDEN_AXE, 800)
            .add(AxeItems.TUNGSTEN_AXE, 600)
            .add(AxeItems.SILVER_AXE, 400)
            .add(AxeItems.LEAD_AXE, 240)
            .add(Items.IRON_AXE, 160)
            .add(AxeItems.TIN_AXE, 60)
            .add(AxeItems.COPPER_AXE, 40)

            .add(ManaWeaponItems.RUBY_STAFF, 2800)
            .add(ManaWeaponItems.AMBER_STAFF, 2800)
            .add(ManaWeaponItems.TOPAZ_STAFF, 2800)
            .add(ManaWeaponItems.EMERALD_STAFF, 2800)
            .add(ManaWeaponItems.SAPPHIRE_STAFF, 2800)
            .add(ManaWeaponItems.DIAMOND_STAFF, 2800)
            .add(ManaWeaponItems.AMETHYST_STAFF, 2800)
            .add(ManaWeaponItems.WAND_OF_SPARKING, 10000)
            .add(ManaWeaponItems.WAND_OF_FROSTING, 3500)
            .add(ManaWeaponItems.THUNDER_ZAPPER, 2100)
            .add(ManaWeaponItems.VILETHRON, 15000)
            .add(ManaWeaponItems.WEATHER_PAIN, 15000)
            .add(ManaWeaponItems.AQUA_SCEPTER, 17500)
            .add(ManaWeaponItems.FLOWER_OF_FIRE, 25000)
            .add(ManaWeaponItems.WATER_BOLT, 15000)

            .add(ManaWeaponItems.BEE_GUN, 20000)
            .add(ManaWeaponItems.SPACE_GUN, 4000)

            .add(TCItems.AGLET, 25000)
            .add(TCItems.AMBER_HORSESHOE_BALLOON, 30000)
            .add(TCItems.AMBHIPIAN_BOOTS, 20000)
            .add(TCItems.ANCIENT_CHISEL, 10000)
            .add(TCItems.ANGLER_EARRING, 10000)
            .add(TCItems.ANKH_CHARM, 30000)
            .add(TCItems.ANKH_SHIELD, 50000)
            .add(TCItems.ANKLET_OF_THE_WIND, 10000)
            .add(TCItems.ARCHITECT_GIZMO_PACK, 40000)
            .add(TCItems.ARCTIC_DIVING_GEAR, 50000)
            .add(TCItems.AVENGER_EMBLEM, 60000)
            .add(TCItems.BALLOON_PUFFERFISH, 25000)
            .add(TCItems.BAND_OF_REGENERATION, 10000)
            .add(TCItems.BASE_POINT, 60000)
            .add(TCItems.BEE_CLOAK, 30000)
            .add(TCItems.BERSERKERS_GLOVE, 100000)
            .add(TCItems.BEZOAR, 20000)
            .add(TCItems.BLACK_BELT, 30000)
            .add(TCItems.BLINDFOLD, 20000)
            .add(TCItems.BLIZZARD_IN_A_BALLOON, 30000)
            .add(TCItems.BLIZZARD_IN_A_BOTTLE, 10000)
            .add(TCItems.BLUE_HORSESHOE_BALLOON, 30000)
            .add(TCItems.BRAIN_OF_CONFUSION, 20000)
            .add(TCItems.BRICK_LAYER, 100000)
            .add(TCItems.BUNDLE_OF_BALLOONS, 30000)
            .add(TCItems.BUNDLE_OF_HORSESHOE_BALLOONS, 40000)
            .add(TCItems.CELESTIAL_SHELL, 140000)
            .add(TCItems.CELESTIAL_STARBOARD, 100000)
            .add(TCItems.CELESTIAL_STONE, 80000)
            .add(TCItems.CELL_PHONE, 80000)
            .add(TCItems.CLIMBING_CLAWS, 25000)
            .add(TCItems.CLOUD_IN_A_BALLOON, 30000)
            .add(TCItems.CLOUD_IN_A_BOTTLE, 10000)
            .add(TCItems.COBALT_SHIELD, 17500)
            .add(TCItems.COMPASS, 1750)
            .add(TCItems.COPPER_WATCH, 140)
            .add(TCItems.CROSS_NECKLACE, 20000)
            .add(TCItems.DEMON_HEART, 20000)
            .add(TCItems.DEPTH_METER, 1750)
            .add(TCItems.DESTROYER_EMBLEM, 60000)
            .add(TCItems.DETOXIFICATION_CAPSULE, 25000)
            .add(TCItems.DIVING_GEAR, 20000)
            .add(TCItems.DIVING_HELMET, 1400)
            .add(TCItems.DPS_METER, 50000)
            .add(TCItems.DUNERIDER_BOOTS, 10000)
            .add(TCItems.ENERGY_BAR, 20000)
            .add(TCItems.EVERLASTING, 140000)
            .add(TCItems.EXPLORERS_EQUIPMENT, 30000)
            .add(TCItems.EXTENDO_GRIP, 100000)
            .add(TCItems.EYE_OF_THE_GOLEM, 50000)
            .add(TCItems.FAIRY_BOOTS, 60000)
            .add(TCItems.FART_IN_A_BALLOON, 30000)
            .add(TCItems.FART_IN_A_JAR, 10000)
            .add(TCItems.FAST_CLOCK, 20000)
            .add(TCItems.FERAL_CLAWS, 10000)
            .add(TCItems.FIRE_GAUNTLET, 60000)
            .add(TCItems.FISH_FINDER, 30000)
            .add(TCItems.FISHERMANS_POCKET_GUIDE, 30000)
            .add(TCItems.FLASHLIGHT, 20000)
            .add(TCItems.FLESH_KNUCKLES, 80000)
            .add(TCItems.FLIPPER, 140)
            .add(TCItems.FLOWER_BOOTS, 60000)
            .add(TCItems.FLURRY_BOOTS, 10000)
            .add(TCItems.FLYING_CARPET, 10000)
            .add(TCItems.FROG_FLIPPER, 20000)
            .add(TCItems.FROG_GEAR, 50000)
            .add(TCItems.FROG_LEG, 10000)
            .add(TCItems.FROG_WEBBING, 20000)
            .add(TCItems.FROSTSPARK_BOOTS, 70000)
            .add(TCItems.FROZEN_SHIELD, 80000)
            .add(TCItems.FROZEN_TURTLE_SHELL, 45000)
            .add(TCItems.GOBLIN_TECH, 30000)
            .add(TCItems.GOLD_WATCH, 1400)
            .add(TCItems.GPS, 30000)
            .add(TCItems.GRAVITY_GLOBE, 400000)
            .add(TCItems.GREEN_HORSESHOE_BALLOON, 30000)
            .add(TCItems.HAND_DRILL, 20000)
            .add(TCItems.HAND_OF_CREATION, 80000)
            .add(TCItems.HAND_WARMER, 10000)
            .add(TCItems.HERMES_BOOTS, 10000)
            .add(TCItems.HERO_SHIELD, 100000)
            .add(TCItems.HIVE_PACK, 20000)
            .add(TCItems.HOLY_WATER, 20000)
            .add(TCItems.HONEY_BALLOON, 20000)
            .add(TCItems.HONEY_COMB, 20000)
            .add(TCItems.ICE_SKATES, 10000)
            .add(TCItems.INNER_TUBE, 140)
            .add(TCItems.JELLYFISH_DIVING_GEAR, 30000)
            .add(TCItems.JELLYFISH_NECKLACE, 10000)
            .add(TCItems.LAVA_CHARM, 60000)
            .add(TCItems.LAVA_WADERS, 100000)
            .add(TCItems.LIFE_FORM_ANALYZER, 100000)
            .add(TCItems.LIGHTNING_BOOTS, 60000)
            .add(TCItems.LUCKY_HORSESHOE, 3500)
            .add(TCItems.MAGIC_MIRROR, 10000)
            .add(TCItems.MAGIC_QUIVER, 50000)
            .add(TCItems.MAGILUMINESCENCE, 10000)
            .add(TCItems.MAGMA_SKULL, 25000)
            .add(TCItems.MAGMA_STONE, 20000)
            .add(TCItems.MASTER_NINJA_GEAR, 100000)
            .add(TCItems.MECHANICAL_GLOVE, 50000)
            .add(TCItems.METAL_DETECTOR, 10000)
            .add(TCItems.MOLTEN_CHARM, 75000)
            .add(TCItems.MOLTEN_QUIVER, 75000)
            .add(TCItems.MOLTEN_SKULL_ROSE, 50000)
            .add(TCItems.MOON_CHARM, 30000)
            .add(TCItems.MOON_SHELL, 80000)
            .add(TCItems.MOON_STONE, 75000)
            .add(TCItems.NEPTUNES_SHELL, 75000)
            .add(TCItems.NUTRIENT_SOLUTION, 30000)
            .add(TCItems.OBSIDIAN_HORSESHOE, 12000)
            .add(TCItems.OBSIDIAN_ROSE, 20000)
            .add(TCItems.OBSIDIAN_SHIELD, 20000)
            .add(TCItems.OBSIDIAN_SKULL, 3500)
            .add(TCItems.OBSIDIAN_SKULL_ROSE, 30000)
            .add(TCItems.OBSIDIAN_WATER_WALKING_BOOTS, 60000)
            .add(TCItems.PALADINS_SHIELD, 60000)
            .add(TCItems.PANIC_NECKLACE, 15000)
            .add(TCItems.PDA, 50000)
            .add(TCItems.PINK_HORSESHOE_BALLOON, 30000)
            .add(TCItems.PLATINUM_WATCH, 2100)
            .add(TCItems.PORTABLE_CEMENT_MIXER, 100000)
            .add(TCItems.POWER_GLOVE, 40000)
            .add(TCItems.PUTRID_SCENT, 80000)
            .add(TCItems.RADAR, 25000)
            .add(TCItems.RANGER_EMBLEM, 20000)
            .add(TCItems.RECON_SCOPE, 100000)
            .add(TCItems.REK_3000, 30000)
            .add(TCItems.RIFLE_SCOPE, 30000)
            .add(TCItems.ROCKET_BOOTS, 50000)
            .add(TCItems.ROYAL_GEL, 20000)
            .add(TCItems.SAILFISH_BOOTS, 10000)
            .add(TCItems.SANDSTORM_IN_A_BALLOON, 30000)
            .add(TCItems.SANDSTORM_IN_A_BOTTLE, 10000)
            .add(TCItems.SEARCHLIGHT, 20000)
            .add(TCItems.SEXTANT, 10000)
            .add(TCItems.SHACKLE, 140)
            .add(TCItems.SHARK_TOOTH_NECKLACE, 10000)
            .add(TCItems.SHARKRON_BALLOON, 30000)
            .add(TCItems.SHIELD_OF_CTHULHU, 20000)
            .add(TCItems.SHINY_RED_BALLOON, 15000)
            .add(TCItems.SHINY_STONE, 50000)
            .add(TCItems.SHOE_SPIKES, 10000)
            .add(TCItems.SHOT_PUT, 20000)
            .add(TCItems.SILVER_WATCH, 700)
            .add(TCItems.SNIPER_SCOPE, 60000)
            .add(TCItems.SOARING_INSIGNIA, 100000)
            .add(TCItems.SORCERER_EMBLEM, 20000)
            .add(TCItems.SPECTRE_BOOTS, 20000)
            .add(TCItems.STALKERS_QUIVER, 100000)
            .add(TCItems.STAR_CLOAK, 20000)
            .add(TCItems.STAR_VEIL, 20000)
            .add(TCItems.STEP_STOOL, 25000)
            .add(TCItems.STINGER_NECKLACE, 30000)
            .add(TCItems.STOPWATCH, 50000)
            .add(TCItems.SUN_STONE, 60000)
            .add(TCItems.SWEETHEART_NECKLACE, 20000)
            .add(TCItems.TABI, 30000)
            .add(TCItems.TALLY_COUNTER, 10000)
            .add(TCItems.TERRASPARK_BOOTS, 150000)
            .add(TCItems.THE_PLAN, 20000)
            .add(TCItems.TIGER_CLIMBING_GEAR, 10000)
            .add(TCItems.TIN_WATCH, 210)
            .add(TCItems.TITAN_GLOVE, 20000)
            .add(TCItems.TOOLBELT, 100000)
            .add(TCItems.TOOLBOX, 100000)
            .add(TCItems.TREASURE_MAGNET, 40000)
            .add(TCItems.TRIFOLD_MAP, 20000)
            .add(TCItems.TSUNAMI_IN_A_BOTTLE, 10000)
            .add(TCItems.TUNGSTEN_WATCH, 1050)
            .add(TCItems.VITAMINS, 20000)
            .add(TCItems.WARRIOR_EMBLEM, 20000)
            .add(TCItems.WATER_WALKING_BOOTS, 40000)
            .add(TCItems.WEATHER_RADIO, 10000)
            .add(TCItems.WHITE_HORSESHOE_BALLOON, 30000)
            .add(TCItems.WORM_SCARF, 20000)
            .add(TCItems.YELLOW_HORSESHOE_BALLOON, 30000)


            .add(FoodItems.PAD_THAI,5500)
            .add(TGItems.SHOTGUN,50000)
            .add(TGItems.BOOMSTICK,20000)
            .add(TGItems.HAND_GUN,17500)
            .add(TGItems.THE_UNDERTAKER,15000)
            .add(TGItems.MUSKET,15000)
            .add(TGItems.FLINTLOCK_PISTOL,50000)
            .add(TGItems.MINISHARK,350000)


            .add(TGItems.MUSKET_BULLET,7)

            .add(ConsumableItems.GRENADE,75)
            .add(ConsumableItems.BOMB,300)
            .add(ConsumableItems.DYNAMITE,2000)
            .add(ConsumableItems.SHURIKEN,15)
            .add(ConsumableItems.PURIFICATION_POWDER,75)

            .add(ToolItems.BUG_NET,2500)


            .add(ArmorItems.MINING_HELMET,40000)


            .add(ModBlocks.ROPE,10)

            .add(FunctionalBlocks.PIGGY_BANK,10000)
            .add(FunctionalBlocks.SAFE,40000)

            .add(PotionItems.LESSER_HEALING_POTION,300)
            .add(PotionItems.LESSER_MANA_POTION,250)

            .add(PaintItems.PAINTBRUSH,10000)
            .add(PaintItems.PAINT_ROLLER,10000)
            .add(PaintItems.PAINT_SCRAPER,10000)
            .add(PaintItems.RED_PAINT,25)
            .add(PaintItems.DEEP_RED_PAINT,25)
            .add(PaintItems.ORANGE_PAINT,25)
            .add(PaintItems.DEEP_ORANGE_PAINT,25)
            .add(PaintItems.YELLOW_PAINT,25)
            .add(PaintItems.DEEP_YELLOW_PAINT,25)
            .add(PaintItems.LIME_PAINT,25)
            .add(PaintItems.DEEP_LIME_PAINT,25)
            .add(PaintItems.GREEN_PAINT,25)
            .add(PaintItems.DEEP_GREEN_PAINT,25)
            .add(PaintItems.TEAL_PAINT,25)
            .add(PaintItems.DEEP_TEAL_PAINT,25)
            .add(PaintItems.CYAN_PAINT,25)
            .add(PaintItems.DEEP_CYAN_PAINT,25)
            .add(PaintItems.SKY_BLUE_PAINT,25)
            .add(PaintItems.DEEP_SKY_BLUE_PAINT,25)
            .add(PaintItems.BLUE_PAINT,25)
            .add(PaintItems.DEEP_BLUE_PAINT,25)
            .add(PaintItems.PURPLE_PAINT,25)
            .add(PaintItems.DEEP_PURPLE_PAINT,25)
            .add(PaintItems.VIOLET_PAINT,25)
            .add(PaintItems.DEEP_VIOLET_PAINT,25)
            .add(PaintItems.PINK_PAINT,25)
            .add(PaintItems.DEEP_PINK_PAINT,25)
            .add(PaintItems.BLACK_PAINT,25)
            .add(PaintItems.GRAY_PAINT,25)
            .add(PaintItems.WHITE_PAINT,25)
            .add(PaintItems.BROWN_PAINT,25)


            .add(VanityArmorItems.SILVER_DYE,10000)
            .add(VanityArmorItems.BROWN_DYE,10000)
            .add(VanityArmorItems.TEAM_DYE,10000)


            .add(NatureBlocks.YELLOW_WILLOW_SAPLING,10000)


            .add(Items.PUMPKIN_SEEDS,250)
            .add(ModItems.GRASS_SEED,20)

            .add(HookItems.GRAPPLING_HOOK,20000)
            .add(TCItems.WORKSHOP,100000)

            .add(TGItems.SHOTGUN,50000);
    }

    public ValueBuilder valueBuilder() {
        return (ValueBuilder) ((DataMapProviderAccessor) this).getBuilders().computeIfAbsent(ModDataMaps.VALUE, k -> new ValueBuilder());
    }

    public static class ValueBuilder extends DataMapProvider.Builder<ValueComponent, Item> {
        public ValueBuilder() {
            super(ModDataMaps.VALUE);
        }

        public ValueBuilder add(ItemLike itemLike, int value) {
            return (ValueBuilder) super.add(itemLike.asItem().builtInRegistryHolder(), new ValueComponent(value), false);
        }
    }

    /**
     * 将钱币数转成cost整数
     *
     * @param a 铂金
     * @param b 金
     * @param c 银
     * @param d 铜
     */
    private int warp(int a, int b, int c, int d) {
        return a * 1000000 + b * 10000 + c * 100 + d;
    }

    /**
     * 将钱币数转成cost整数
     *
     * @param b 金
     * @param c 银
     * @param d 铜
     */
    private int warp(int b, int c, int d) {
        return b * 10000 + c * 100 + d;
    }

    /**
     * 将钱币数转成cost整数
     *
     * @param c 银
     * @param d 铜
     */
    private int warp(int c, int d) {
        return c * 100 + d;
    }
}