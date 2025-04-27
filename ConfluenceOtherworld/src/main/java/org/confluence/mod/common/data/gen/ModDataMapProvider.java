package org.confluence.mod.common.data.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
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
            .add(Items.NETHERITE_SWORD, 6000)
            .add(Items.NETHERITE_PICKAXE, 6000)
            .add(Items.NETHERITE_AXE, 6000)

            .add(Items.NETHERITE_AXE, 3000)
            .add(Items.DIAMOND_SWORD, 3000)
            .add(Items.DIAMOND_AXE, 3000)

            .add(Items.GOLDEN_SWORD, 2800)
            .add(Items.GOLDEN_PICKAXE, 2800)
            .add(Items.GOLDEN_AXE, 2800)

            .add(Items.IRON_SWORD,2800)
            .add(Items.IRON_AXE,2800)

            .add(Items.COPPER_INGOT, 150)
            .add(Items.IRON_INGOT,  300)
            .add(Items.GOLD_INGOT,  1200)
            .add(Items.NETHERITE_INGOT,4000)

            .add(Items.ARROW,5)

            .add(Blocks.ANVIL.asItem(),20000)
            .add(Blocks.TORCH.asItem(),50)
            .add(Blocks.OAK_SAPLING.asItem(),100)

            .add(MaterialItems.TIN_INGOT.get(), 225)
            .add(MaterialItems.LEAD_INGOT.get(),450)
            .add(MaterialItems.SILVER_INGOT.get(), 600)
            .add(MaterialItems.TUNGSTEN_INGOT.get(), 900)
            .add(MaterialItems.PLATINUM_INGOT.get(), 1800)
            .add(MaterialItems.METEORITE_INGOT.get(),1400)
            .add(MaterialItems.DEMONITE_INGOT.get(), 3000)
            .add(MaterialItems.TR_CRIMSON_INGOT.get(), 3900)
            .add(MaterialItems.HELLSTONE_INGOT.get(), 4000)

            .add(MaterialItems.COBALT_INGOT.get(), 2100)
            .add(MaterialItems.PALLADIUM_INGOT.get(), 2700)
            .add(MaterialItems.MYTHRIL_INGOT.get(), 4400)
            .add(MaterialItems.ORICHALCUM_INGOT.get(), 5200)
            .add(MaterialItems.ADAMANTITE_INGOT.get(), 6000)
            .add(MaterialItems.TITANIUM_INGOT.get(), 6800)
            .add(MaterialItems.HALLOWED_INGOT.get(), 4000)
            .add(MaterialItems.CHLOROPHYTE_INGOT.get(), 9000)
            .add(MaterialItems.SHROOMITE_INGOT.get(), 10000)
            .add(MaterialItems.SPECTRE_INGOT.get(), 10000)
            .add(MaterialItems.LUMINITE_INGOT.get(), 12000)

            .add(Items.RAW_COPPER, 50)
            .add(Items.RAW_IRON, 100)
            .add(Items.RAW_GOLD, 300)

            .add(MaterialItems.RAW_TIN.get(), 75)
            .add(MaterialItems.RAW_LEAD.get(), 150)
            .add(MaterialItems.RAW_SILVER.get(), 150)
            .add(MaterialItems.RAW_TUNGSTEN.get(), 225)
            .add(MaterialItems.RAW_PLATINUM.get(), 450)
            .add(MaterialItems.RAW_METEORITE.get(), 200)
            .add(MaterialItems.RAW_DEMONITE.get(), 1000)
            .add(MaterialItems.RAW_TR_CRIMSON.get(), 1300)
            .add(MaterialItems.RAW_HELLSTONE.get(), 250)

            .add(MaterialItems.RAW_COBALT.get(), 700)
            .add(MaterialItems.RAW_PALLADIUM.get(), 900)
            .add(MaterialItems.RAW_MYTHRIL.get(), 1100)
            .add(MaterialItems.RAW_ORICHALCUM.get(), 1300)
            .add(MaterialItems.RAW_ADAMANTITE.get(), 1500)
            .add(MaterialItems.RAW_TITANIUM.get(), 1700)
            .add(MaterialItems.RAW_CHLOROPHYTE.get(), 1500)
            .add(MaterialItems.RAW_LUMINITE.get(), 3000)

            .add(MaterialItems.GEL.get(), 1)
            .add(MaterialItems.PINK_GEL.get(), 3)
            .add(MaterialItems.SILK.get(), 200)
            .add(MaterialItems.RAW_ASPHALT.get(), 1)

            .add(SwordItems.VOLCANO, 5400)
            .add(SwordItems.BLOOD_BUTCHERER.get(), 2700)
            .add(SwordItems.LIGHTS_BANE.get(), 2700)
            .add(SwordItems.ENCHANTED_SWORD.get(), 30000)
            .add(SwordItems.TERRAGRIM.get(), 50000)
            .add(SwordItems.ICE_BLADE.get(), 4000)
            .add(SwordItems.CACTUS_SWORD.get(), 360)
            .add(SwordItems.ZOMBIE_ARM.get(), 2800)
            .add(SwordItems.MANDIBLE_BLADE.get(), 7000)
            .add(SwordItems.COPPER_SHORT_SWORD.get(), 210)
            .add(SwordItems.COPPER_BOARD_SWORD.get(), 630)
            .add(SwordItems.TIN_SHORT_SWORD.get(), 315)
            .add(SwordItems.TIN_BOARD_SWORD.get(), 945)
            .add(SwordItems.KATANA.get(),100000)

            .add(TEBoomerangItems.FLAMARANG.get(), 20000)
            .add(TEBoomerangItems.TRIMARANG.get(), 20000)
            .add(TEBoomerangItems.ICE_BOOMERANG.get(), 10000)
            .add(TEBoomerangItems.SHROOMERANG.get(), 6000)
            .add(TEBoomerangItems.ENCHANTED_BOOMERANG.get(), 10000)
            .add(TEBoomerangItems.WOOD_BOOMERANG.get(), 10000)
            .add(SwordItems.STYLISH_SCISSORS.get(), 350)

            .add(BowItems.MOLTEN_FURY.get(), 5400)
            .add(BowItems.TENDON_BOW.get(), 3600)
            .add(BowItems.DEMON_BOW.get(), 3600)
            .add(BowItems.FOSSIL_BOW.get(), 3000)
            .add(BowItems.HUNTING_BOW.get(), 3800)
            .add(BowItems.PLATINUM_BOW.get(), 2100)
            .add(BowItems.PLATINUM_SHORT_BOW.get(), 2100)
            .add(BowItems.GOLDEN_BOW.get(), 1400)
            .add(BowItems.GOLDEN_SHORT_BOW.get(), 1400)
            .add(BowItems.TUNGSTEN_BOW.get(), 1050)
            .add(BowItems.TUNGSTEN_SHORT_BOW.get(), 1050)
            .add(BowItems.SILVER_BOW.get(), 700)
            .add(BowItems.SILVER_SHORT_BOW.get(), 700)
            .add(BowItems.LEAD_BOW.get(), 420)
            .add(BowItems.LEAD_SHORT_BOW.get(), 420)
            .add(BowItems.IRON_BOW.get(), 280)
            .add(BowItems.IRON_SHORT_BOW.get(), 280)
            .add(BowItems.TIN_BOW.get(), 150)
            .add(BowItems.TIN_SHORT_BOW.get(), 150)
            .add(BowItems.COPPER_BOW.get(), 70)
            .add(BowItems.COPPER_SHORT_BOW.get(), 70)
            .add(BowItems.WOODEN_SHORT_BOW.get(), 40)
            .add(Items.BOW, 40)

            .add(PickaxeItems.MOLTEN_PICKAXE, 5400)
            .add(PickaxeItems.DEATHBRINGER_PICKAXE.get(), 3600)
            .add(PickaxeItems.NIGHTMARE_PICKAXE.get(), 3600)
            .add(PickaxeItems.PLATINUM_PICKAXE.get(), 3000)
            .add(PickaxeItems.GOLDEN_PICKAXE.get(), 2000)
            .add(PickaxeItems.TUNGSTEN_PICKAXE.get(), 1500)
            .add(PickaxeItems.SILVER_PICKAXE.get(), 1000)
            .add(PickaxeItems.LEAD_PICKAXE.get(), 600)
            .add(Items.IRON_PICKAXE, 400)
            .add(PickaxeItems.TIN_PICKAXE.get(), 150)
            .add(PickaxeItems.COPPER_PICKAXE.get(), 50)

            .add(HammerItems.THE_BREAKER, 3000)
            .add(HammerItems.FLESH_GRINDER.get(), 3000)
            .add(HammerItems.PLATINUM_HAMMER.get(), 2400)
            .add(HammerItems.GOLDEN_HAMMER.get(), 1600)
            .add(HammerItems.TUNGSTEN_HAMMER.get(), 1200)
            .add(HammerItems.SILVER_HAMMER.get(), 800)
            .add(HammerItems.LEAD_HAMMER.get(), 480)
            .add(HammerItems.IRON_HAMMER.get(), 320)
            .add(HammerItems.TIN_HAMMER.get(), 120)
            .add(HammerItems.COPPER_HAMMER.get(), 80)
            .add(HammerItems.WOODEN_HAMMER.get(), 10)

            .add(AxeItems.WAR_AXE_OF_THE_NIGHT, 2700)
            .add(AxeItems.BLOOD_LUST_CLUSTER.get(), 2700)
            .add(AxeItems.PLATINUM_AXE.get(), 1200)
            .add(AxeItems.GOLDEN_AXE.get(), 800)
            .add(AxeItems.TUNGSTEN_AXE.get(), 600)
            .add(AxeItems.SILVER_AXE.get(), 400)
            .add(AxeItems.LEAD_AXE.get(), 240)
            .add(Items.IRON_AXE, 160)
            .add(AxeItems.TIN_AXE.get(), 60)
            .add(AxeItems.COPPER_AXE.get(), 40)

            .add(ManaWeaponItems.RUBY_STAFF.get(), 2800)
            .add(ManaWeaponItems.AMBER_STAFF.get(), 2800)
            .add(ManaWeaponItems.TOPAZ_STAFF.get(), 2800)
            .add(ManaWeaponItems.EMERALD_STAFF.get(), 2800)
            .add(ManaWeaponItems.SAPPHIRE_STAFF.get(), 2800)
            .add(ManaWeaponItems.DIAMOND_STAFF.get(), 2800)
            .add(ManaWeaponItems.AMETHYST_STAFF.get(), 2800)
            .add(ManaWeaponItems.WAND_OF_SPARKING.get(), 10000)
            .add(ManaWeaponItems.WAND_OF_FROSTING.get(), 3500)
            .add(ManaWeaponItems.THUNDER_ZAPPER.get(), 2100)
            .add(ManaWeaponItems.VILETHRON.get(), 15000)
            .add(ManaWeaponItems.WEATHER_PAIN.get(), 15000)
            .add(ManaWeaponItems.AQUA_SCEPTER.get(), 17500)
            .add(ManaWeaponItems.FLOWER_OF_FIRE.get(), 25000)
            .add(ManaWeaponItems.WATER_BOLT.get(), 15000)

             // 还没复活
            .add("confluence:bee_gun", 20000)
            .add("confluence:space_gun", 4000)

            .add(TCItems.AGLET, 25000)
            .add(TCItems.AMBER_HORSESHOE_BALLOON.get(), 30000)
            .add(TCItems.AMBHIPIAN_BOOTS.get(), 20000)
            .add(TCItems.ANCIENT_CHISEL.get(), 10000)
            .add(TCItems.ANGLER_EARRING.get(), 10000)
            .add(TCItems.ANKH_CHARM.get(), 30000)
            .add(TCItems.ANKH_SHIELD.get(), 50000)
            .add(TCItems.ANKLET_OF_THE_WIND.get(), 10000)
            .add(TCItems.ARCHITECT_GIZMO_PACK.get(), 40000)
            .add(TCItems.ARCTIC_DIVING_GEAR.get(), 50000)
            .add(TCItems.AVENGER_EMBLEM.get(), 60000)
            .add(TCItems.BALLOON_PUFFERFISH.get(), 25000)
            .add(TCItems.BAND_OF_REGENERATION.get(), 10000)
            .add(TCItems.BASE_POINT.get(), 60000)
            .add(TCItems.BEE_CLOAK.get(), 30000)
            .add(TCItems.BERSERKERS_GLOVE.get(), 100000)
            .add(TCItems.BEZOAR.get(), 20000)
            .add(TCItems.BLACK_BELT.get(), 30000)
            .add(TCItems.BLINDFOLD.get(), 20000)
            .add(TCItems.BLIZZARD_IN_A_BALLOON.get(), 30000)
            .add(TCItems.BLIZZARD_IN_A_BOTTLE.get(), 10000)
            .add(TCItems.BLUE_HORSESHOE_BALLOON.get(), 30000)
            .add(TCItems.BRAIN_OF_CONFUSION.get(), 20000)
            .add(TCItems.BRICK_LAYER.get(), 100000)
            .add(TCItems.BUNDLE_OF_BALLOONS.get(), 30000)
            .add(TCItems.BUNDLE_OF_HORSESHOE_BALLOONS.get(), 40000)
            .add(TCItems.CELESTIAL_SHELL.get(), 140000)
            .add(TCItems.CELESTIAL_STARBOARD.get(), 100000)
            .add(TCItems.CELESTIAL_STONE.get(), 80000)
            .add(TCItems.CELL_PHONE.get(), 80000)
            .add(TCItems.CLIMBING_CLAWS.get(), 25000)
            .add(TCItems.CLOUD_IN_A_BALLOON.get(), 30000)
            .add(TCItems.CLOUD_IN_A_BOTTLE.get(), 10000)
            .add(TCItems.COBALT_SHIELD.get(), 17500)
            .add(TCItems.COMPASS.get(), 1750)
            .add(TCItems.COPPER_WATCH.get(), 140)
            .add(TCItems.CROSS_NECKLACE.get(), 20000)
            .add(TCItems.DEMON_HEART.get(), 20000)
            .add(TCItems.DEPTH_METER.get(), 1750)
            .add(TCItems.DESTROYER_EMBLEM.get(), 60000)
            .add(TCItems.DETOXIFICATION_CAPSULE.get(), 25000)
            .add(TCItems.DIVING_GEAR.get(), 20000)
            .add(TCItems.DIVING_HELMET.get(), 1400)
            .add(TCItems.DPS_METER.get(), 50000)
            .add(TCItems.DUNERIDER_BOOTS.get(), 10000)
            .add(TCItems.ENERGY_BAR.get(), 20000)
            .add(TCItems.EVERLASTING.get(), 140000)
            .add(TCItems.EXPLORERS_EQUIPMENT.get(), 30000)
            .add(TCItems.EXTENDO_GRIP.get(), 100000)
            .add(TCItems.EYE_OF_THE_GOLEM.get(), 50000)
            .add(TCItems.FAIRY_BOOTS.get(), 60000)
            .add(TCItems.FART_IN_A_BALLOON.get(), 30000)
            .add(TCItems.FART_IN_A_JAR.get(), 10000)
            .add(TCItems.FAST_CLOCK.get(), 20000)
            .add(TCItems.FERAL_CLAWS.get(), 10000)
            .add(TCItems.FIRE_GAUNTLET.get(), 60000)
            .add(TCItems.FISH_FINDER.get(), 30000)
            .add(TCItems.FISHERMANS_POCKET_GUIDE.get(), 30000)
            .add(TCItems.FLASHLIGHT.get(), 20000)
            .add(TCItems.FLESH_KNUCKLES.get(), 80000)
            .add(TCItems.FLIPPER.get(), 140)
            .add(TCItems.FLOWER_BOOTS.get(), 60000)
            .add(TCItems.FLURRY_BOOTS.get(), 10000)
            .add(TCItems.FLYING_CARPET.get(), 10000)
            .add(TCItems.FROG_FLIPPER.get(), 20000)
            .add(TCItems.FROG_GEAR.get(), 50000)
            .add(TCItems.FROG_LEG.get(), 10000)
            .add(TCItems.FROG_WEBBING.get(), 20000)
            .add(TCItems.FROSTSPARK_BOOTS.get(), 70000)
            .add(TCItems.FROZEN_SHIELD.get(), 80000)
            .add(TCItems.FROZEN_TURTLE_SHELL.get(), 45000)
            .add(TCItems.GOBLIN_TECH.get(), 30000)
            .add(TCItems.GOLD_WATCH.get(), 1400)
            .add(TCItems.GPS.get(), 30000)
            .add(TCItems.GRAVITY_GLOBE.get(), 400000)
            .add(TCItems.GREEN_HORSESHOE_BALLOON.get(), 30000)
            .add(TCItems.HAND_DRILL.get(), 20000)
            .add(TCItems.HAND_OF_CREATION.get(), 80000)
            .add(TCItems.HAND_WARMER.get(), 10000)
            .add(TCItems.HERMES_BOOTS.get(), 10000)
            .add(TCItems.HERO_SHIELD.get(), 100000)
            .add(TCItems.HIVE_PACK.get(), 20000)
            .add(TCItems.HOLY_WATER.get(), 20000)
            .add(TCItems.HONEY_BALLOON.get(), 20000)
            .add(TCItems.HONEY_COMB.get(), 20000)
            .add(TCItems.ICE_SKATES.get(), 10000)
            .add(TCItems.INNER_TUBE.get(), 140)
            .add(TCItems.JELLYFISH_DIVING_GEAR.get(), 30000)
            .add(TCItems.JELLYFISH_NECKLACE.get(), 10000)
            .add(TCItems.LAVA_CHARM.get(), 60000)
            .add(TCItems.LAVA_WADERS.get(), 100000)
            .add(TCItems.LIFE_FORM_ANALYZER.get(), 100000)
            .add(TCItems.LIGHTNING_BOOTS.get(), 60000)
            .add(TCItems.LUCKY_HORSESHOE.get(), 3500)
            .add(TCItems.MAGIC_MIRROR.get(), 10000)
            .add(TCItems.MAGIC_QUIVER.get(), 50000)
            .add(TCItems.MAGILUMINESCENCE.get(), 10000)
            .add(TCItems.MAGMA_SKULL.get(), 25000)
            .add(TCItems.MAGMA_STONE.get(), 20000)
            .add(TCItems.MASTER_NINJA_GEAR.get(), 100000)
            .add(TCItems.MECHANICAL_GLOVE.get(), 50000)
            .add(TCItems.METAL_DETECTOR.get(), 10000)
            .add(TCItems.MOLTEN_CHARM.get(), 75000)
            .add(TCItems.MOLTEN_QUIVER.get(), 75000)
            .add(TCItems.MOLTEN_SKULL_ROSE.get(), 50000)
            .add(TCItems.MOON_CHARM.get(), 30000)
            .add(TCItems.MOON_SHELL.get(), 80000)
            .add(TCItems.MOON_STONE.get(), 75000)
            .add(TCItems.NEPTUNES_SHELL.get(), 75000)
            .add(TCItems.NUTRIENT_SOLUTION.get(), 30000)
            .add(TCItems.OBSIDIAN_HORSESHOE.get(), 12000)
            .add(TCItems.OBSIDIAN_ROSE.get(), 20000)
            .add(TCItems.OBSIDIAN_SHIELD.get(), 20000)
            .add(TCItems.OBSIDIAN_SKULL.get(), 3500)
            .add(TCItems.OBSIDIAN_SKULL_ROSE.get(), 30000)
            .add(TCItems.OBSIDIAN_WATER_WALKING_BOOTS.get(), 60000)
            .add(TCItems.PALADINS_SHIELD.get(), 60000)
            .add(TCItems.PANIC_NECKLACE.get(), 15000)
            .add(TCItems.PDA.get(), 50000)
            .add(TCItems.PINK_HORSESHOE_BALLOON.get(), 30000)
            .add(TCItems.PLATINUM_WATCH.get(), 2100)
            .add(TCItems.PORTABLE_CEMENT_MIXER.get(), 100000)
            .add(TCItems.POWER_GLOVE.get(), 40000)
            .add(TCItems.PUTRID_SCENT.get(), 80000)
            .add(TCItems.RADAR.get(), 25000)
            .add(TCItems.RANGER_EMBLEM.get(), 20000)
            .add(TCItems.RECON_SCOPE.get(), 100000)
            .add(TCItems.REK_3000.get(), 30000)
            .add(TCItems.RIFLE_SCOPE.get(), 30000)
            .add(TCItems.ROCKET_BOOTS.get(), 50000)
            .add(TCItems.ROYAL_GEL.get(), 20000)
            .add(TCItems.SAILFISH_BOOTS.get(), 10000)
            .add(TCItems.SANDSTORM_IN_A_BALLOON.get(), 30000)
            .add(TCItems.SANDSTORM_IN_A_BOTTLE.get(), 10000)
            .add(TCItems.SEARCHLIGHT.get(), 20000)
            .add(TCItems.SEXTANT.get(), 10000)
            .add(TCItems.SHACKLE.get(), 140)
            .add(TCItems.SHARK_TOOTH_NECKLACE.get(), 10000)
            .add(TCItems.SHARKRON_BALLOON.get(), 30000)
            .add(TCItems.SHIELD_OF_CTHULHU.get(), 20000)
            .add(TCItems.SHINY_RED_BALLOON.get(), 15000)
            .add(TCItems.SHINY_STONE.get(), 50000)
            .add(TCItems.SHOE_SPIKES.get(), 10000)
            .add(TCItems.SHOT_PUT.get(), 20000)
            .add(TCItems.SILVER_WATCH.get(), 700)
            .add(TCItems.SNIPER_SCOPE.get(), 60000)
            .add(TCItems.SOARING_INSIGNIA.get(), 100000)
            .add(TCItems.SORCERER_EMBLEM.get(), 20000)
            .add(TCItems.SPECTRE_BOOTS.get(), 20000)
            .add(TCItems.STALKERS_QUIVER.get(), 100000)
            .add(TCItems.STAR_CLOAK.get(), 20000)
            .add(TCItems.STAR_VEIL.get(), 20000)
            .add(TCItems.STEP_STOOL.get(), 25000)
            .add(TCItems.STINGER_NECKLACE.get(), 30000)
            .add(TCItems.STOPWATCH.get(), 50000)
            .add(TCItems.SUN_STONE.get(), 60000)
            .add(TCItems.SWEETHEART_NECKLACE.get(), 20000)
            .add(TCItems.TABI.get(), 30000)
            .add(TCItems.TALLY_COUNTER.get(), 10000)
            .add(TCItems.TERRASPARK_BOOTS.get(), 150000)
            .add(TCItems.THE_PLAN.get(), 20000)
            .add(TCItems.TIGER_CLIMBING_GEAR.get(), 10000)
            .add(TCItems.TIN_WATCH.get(), 210)
            .add(TCItems.TITAN_GLOVE.get(), 20000)
            .add(TCItems.TOOLBELT.get(), 100000)
            .add(TCItems.TOOLBOX.get(), 100000)
            .add(TCItems.TREASURE_MAGNET.get(), 40000)
            .add(TCItems.TRIFOLD_MAP.get(), 20000)
            .add(TCItems.TSUNAMI_IN_A_BOTTLE.get(), 10000)
            .add(TCItems.TUNGSTEN_WATCH.get(), 1050)
            .add(TCItems.VITAMINS.get(), 20000)
            .add(TCItems.WARRIOR_EMBLEM.get(), 20000)
            .add(TCItems.WATER_WALKING_BOOTS.get(), 40000)
            .add(TCItems.WEATHER_RADIO.get(), 10000)
            .add(TCItems.WHITE_HORSESHOE_BALLOON.get(), 30000)
            .add(TCItems.WORM_SCARF.get(), 20000)
            .add(TCItems.YELLOW_HORSESHOE_BALLOON.get(), 30000)


            .add(FoodItems.PAD_THAI.get(),5500)
            .add(TGItems.SHOTGUN.get(),50000)
            .add(TGItems.BOOMSTICK.get(),20000)
            .add(TGItems.HAND_GUN.get(),17500)
            .add(TGItems.THE_UNDERTAKER.get(),15000)
            .add(TGItems.MUSKET.get(),15000)
            .add(TGItems.MINISHARK.get(),350000)


            .add(TGItems.MUSKET_BULLET.get(),7)

            .add(ConsumableItems.GRENADE.get(),75)
            .add(ConsumableItems.BOMB.get(),300)
            .add(ConsumableItems.DYNAMITE.get(),2000)
            .add(ConsumableItems.SHURIKEN.get(),15)
            .add(ConsumableItems.PURIFICATION_POWDER.get(),75)

            .add(ToolItems.BUG_NET.get(),2500)


            .add(ArmorItems.MINING_HELMET.get(),40000)


            .add(ModBlocks.ROPE.get(),10)

            .add(FunctionalBlocks.PIGGY_BANK.get(),10000)

            .add(PotionItems.LESSER_HEALING_POTION.get(),300)
            .add(PotionItems.LESSER_MANA_POTION.get(),250)

            .add(PaintItems.PAINTBRUSH.get(),10000)
            .add(PaintItems.PAINT_ROLLER.get(),10000)
            .add(PaintItems.PAINT_SCRAPER.get(),10000)
            .add(PaintItems.RED_PAINT.get(),25)
            .add(PaintItems.DEEP_RED_PAINT.get(),25)
            .add(PaintItems.ORANGE_PAINT.get(),25)
            .add(PaintItems.DEEP_ORANGE_PAINT.get(),25)
            .add(PaintItems.YELLOW_PAINT.get(),25)
            .add(PaintItems.DEEP_YELLOW_PAINT.get(),25)
            .add(PaintItems.LIME_PAINT.get(),25)
            .add(PaintItems.DEEP_LIME_PAINT.get(),25)
            .add(PaintItems.GREEN_PAINT.get(),25)
            .add(PaintItems.DEEP_GREEN_PAINT.get(),25)
            .add(PaintItems.TEAL_PAINT.get(),25)
            .add(PaintItems.DEEP_TEAL_PAINT.get(),25)
            .add(PaintItems.CYAN_PAINT.get(),25)
            .add(PaintItems.DEEP_CYAN_PAINT.get(),25)
            .add(PaintItems.SKY_BLUE_PAINT.get(),25)
            .add(PaintItems.DEEP_SKY_BLUE_PAINT.get(),25)
            .add(PaintItems.BLUE_PAINT.get(),25)
            .add(PaintItems.DEEP_BLUE_PAINT.get(),25)
            .add(PaintItems.PURPLE_PAINT.get(),25)
            .add(PaintItems.DEEP_PURPLE_PAINT.get(),25)
            .add(PaintItems.VIOLET_PAINT.get(),25)
            .add(PaintItems.DEEP_VIOLET_PAINT.get(),25)
            .add(PaintItems.PINK_PAINT.get(),25)
            .add(PaintItems.DEEP_PINK_PAINT.get(),25)
            .add(PaintItems.BLACK_PAINT.get(),25)
            .add(PaintItems.GRAY_PAINT.get(),25)
            .add(PaintItems.WHITE_PAINT.get(),25)
            .add(PaintItems.BROWN_PAINT.get(),25)


            .add(VanityArmorItems.SILVER_DYE.get(),10000)
            .add(VanityArmorItems.BROWN_DYE.get(),10000)
            .add(VanityArmorItems.TEAM_DYE.get(),10000)


            .add(NatureBlocks.YELLOW_WILLOW_SAPLING.get(),10000)


            .add(Items.PUMPKIN_SEEDS.asItem(),250)
            .add(ModItems.GRASS_SEED.get(),20)

            .add(HookItems.GRAPPLING_HOOK.get(),20000)
            .add(TCItems.WORKSHOP.get(),100000)

            .add(TGItems.SHOTGUN.get(),50000);
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

        @Deprecated
        public ValueBuilder add(String id, int value) {
            return add(BuiltInRegistries.ITEM.get(ResourceLocation.parse(id)), value);
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