package org.confluence.mod.common.data.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.data.DataMapProvider;
import org.confluence.mod.common.component.ValueComponent;
import org.confluence.mod.common.init.ModDataMaps;
import org.confluence.mod.common.init.block.*;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.mixin.accessor.DataMapProviderAccessor;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_guns.common.init.TGItems;
import org.confluence.terraentity.init.item.TEBoomerangItems;
import org.confluence.terraentity.init.item.TERideableItems;
import org.confluence.terraentity.init.item.TESummonItems;
import org.confluence.terraentity.init.item.TEWhipItems;

import java.util.concurrent.CompletableFuture;

public class ModDataMapProvider extends DataMapProvider {
    public ModDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        valueBuilder()
                .add(Items.DRAGON_EGG, 2000)
                .add(Items.NETHER_STAR, 1000)
                .add(Items.ANVIL, wrap(10, 0))
                .add(Items.TORCH, 10);
        valueBuilder()
                .add(Items.COPPER_INGOT, wrap(1, 50))
                .add(MaterialItems.TR_CRIMSON_INGOT, 3900)
                .add(MaterialItems.DEMONITE_INGOT, 3000)
                .add(Items.GOLD_INGOT, 1200)
                .add(Items.IRON_INGOT, 300)
                .add(Items.NETHERITE_INGOT, 4000)
                .add(MaterialItems.LEAD_INGOT, 450)
                .add(MaterialItems.METEORITE_INGOT, 1400)
                .add(MaterialItems.PLATINUM_INGOT, 1800)
                .add(MaterialItems.SILVER_INGOT, 600)
                .add(MaterialItems.TIN_INGOT, 225)
                .add(MaterialItems.TUNGSTEN_INGOT, 900)
                .add(MaterialItems.HELLSTONE_INGOT, 4000)
                .add(MaterialItems.ADAMANTITE_INGOT, 6000)
                .add(MaterialItems.CHLOROPHYTE_INGOT, 9000)
                .add(MaterialItems.SPECTRE_INGOT, 10000)
                .add(MaterialItems.TITANIUM_INGOT, 6800)
                .add(MaterialItems.LUMINITE_INGOT, 12000)
                .add(MaterialItems.SHROOMITE_INGOT, 10000)
                .add(MaterialItems.COBALT_INGOT, 2100)
                .add(MaterialItems.MYTHRIL_INGOT, 4400)
                .add(MaterialItems.ORICHALCUM_INGOT, 5200)
                .add(MaterialItems.PALLADIUM_INGOT, 2700)
                .add(MaterialItems.HALLOWED_INGOT, 4000);
        valueBuilder()
                .add(Items.COPPER_BLOCK, 150 * 9)
                .add(OreBlocks.TR_CRIMSON_BLOCK, 3900 * 9)
                .add(OreBlocks.DEMONITE_BLOCK, 3000 * 9)
                .add(Items.GOLD_BLOCK, 1200 * 9)
                .add(Items.IRON_BLOCK, 300 * 9)
                .add(Items.NETHERITE_BLOCK, 4000 * 9)
                .add(OreBlocks.LEAD_BLOCK, 450 * 9)
                .add(OreBlocks.METEORITE_BLOCK, 1400 * 9)
                .add(OreBlocks.PLATINUM_BLOCK, 1800 * 9)
                .add(OreBlocks.SILVER_BLOCK, 600 * 9)
                .add(OreBlocks.TIN_BLOCK, 225 * 9)
                .add(OreBlocks.TUNGSTEN_BLOCK, 900 * 9)
                .add(OreBlocks.HELLSTONE_BLOCK, 4000 * 9)
                .add(OreBlocks.ADAMANTITE_BLOCK, 6000 * 9)
                .add(OreBlocks.CHLOROPHYTE_BLOCK, 9000 * 9)
                .add(OreBlocks.SPECTRE_BLOCK, 10000 * 9)
                .add(OreBlocks.TITANIUM_BLOCK, 6800 * 9)
                .add(OreBlocks.LUMINITE_BLOCK, 12000 * 9)
                .add(OreBlocks.SHROOMITE_BLOCK, 10000 * 9)
                .add(OreBlocks.COBALT_BLOCK, 2100 * 9)
                .add(OreBlocks.MYTHRIL_BLOCK, 4400 * 9)
                .add(OreBlocks.ORICHALCUM_BLOCK, 5200 * 9)
                .add(OreBlocks.PALLADIUM_BLOCK, 2700 * 9)
                .add(OreBlocks.HALLOWED_BLOCK, 4000 * 9);
        valueBuilder()
                .add(Items.DIAMOND, 3000)
                .add(MaterialItems.AMBER, wrap(30, 0))
                .add(MaterialItems.RUBY, wrap(22, 50))
                .add(Items.EMERALD, 1500)
                .add(MaterialItems.SAPPHIRE, wrap(11, 25))
                .add(MaterialItems.SAPPHIRE, wrap(7, 50))
                .add(MaterialItems.TR_AMETHYST, wrap(3, 75))
                .add(Items.LAPIS_LAZULI, 50)
                .add(Items.REDSTONE, 40)
                .add(Items.COAL, 40)
                .add(Items.CHARCOAL, 40);
        valueBuilder()
                .add(Items.DIAMOND_BLOCK, 3000 * 9)
                .add(DecorativeBlocks.AMBER_BLOCK, wrap(30, 0) * 9)
                .add(MaterialItems.RUBY, wrap(22, 50) * 9)
                .add(Items.EMERALD_BLOCK, 1500 * 9)
                .add(DecorativeBlocks.SAPPHIRE_BLOCK, wrap(11, 25) * 9)
                .add(DecorativeBlocks.SAPPHIRE_BLOCK, wrap(7, 50) * 9)
                .add(DecorativeBlocks.TR_AMETHYST_BLOCK, wrap(3, 75) * 9)
                .add(Items.LAPIS_BLOCK, 50 * 9)
                .add(Items.REDSTONE_BLOCK, 40 * 9)
                .add(Items.COAL_BLOCK, 40 * 9);
        valueBuilder()
                .add(Items.RAW_COPPER, 50)
                .add(MaterialItems.RAW_TIN, 75)
                .add(Items.RAW_IRON, 100)
                .add(MaterialItems.RAW_LEAD, 150)
                .add(MaterialItems.RAW_SILVER, 150)
                .add(MaterialItems.RAW_TUNGSTEN, 225)
                .add(Items.RAW_GOLD, 300)
                .add(MaterialItems.RAW_PLATINUM, 450)
                .add(MaterialItems.RAW_METEORITE, 200)
                .add(MaterialItems.RAW_DEMONITE, 1000)
                .add(MaterialItems.RAW_TR_CRIMSON, 1300)
                .add(MaterialItems.RAW_HELLSTONE, 250)
                .add(MaterialItems.RAW_COBALT, 700)
                .add(MaterialItems.RAW_PALLADIUM, 900)
                .add(MaterialItems.RAW_MYTHRIL, 1100)
                .add(MaterialItems.RAW_ORICHALCUM, 1300)
                .add(MaterialItems.RAW_ADAMANTITE, 1500)
                .add(MaterialItems.RAW_TITANIUM, 1700)
                .add(MaterialItems.RAW_CHLOROPHYTE, 1500)
                .add(MaterialItems.RAW_LUMINITE, 3000);
        valueBuilder()
                .add(Items.RAW_COPPER_BLOCK, 50 * 9)
                .add(OreBlocks.RAW_TIN_BLOCK, 75 * 9)
                .add(Items.RAW_IRON_BLOCK, 100 * 9)
                .add(OreBlocks.RAW_LEAD_BLOCK, 150 * 9)
                .add(OreBlocks.RAW_SILVER_BLOCK, 150 * 9)
                .add(OreBlocks.RAW_TUNGSTEN_BLOCK, 225 * 9)
                .add(Items.RAW_GOLD_BLOCK, 300 * 9)
                .add(OreBlocks.RAW_PLATINUM_BLOCK, 450 * 9)
                .add(OreBlocks.RAW_METEORITE_BLOCK, 200 * 9)
                .add(OreBlocks.RAW_DEMONITE_BLOCK, 1000 * 9)
                .add(OreBlocks.RAW_TR_CRIMSON_BLOCK, 1300 * 9)
                .add(OreBlocks.RAW_HELLSTONE_BLOCK, 250 * 9)
                .add(OreBlocks.RAW_COBALT_BLOCK, 700 * 9)
                .add(OreBlocks.RAW_PALLADIUM_BLOCK, 900 * 9)
                .add(OreBlocks.RAW_MYTHRIL_BLOCK, 1100 * 9)
                .add(OreBlocks.RAW_ORICHALCUM_BLOCK, 1300 * 9)
                .add(OreBlocks.RAW_ADAMANTITE_BLOCK, 1500 * 9)
                .add(OreBlocks.RAW_TITANIUM_BLOCK, 1700 * 9)
                .add(OreBlocks.RAW_CHLOROPHYTE_BLOCK, 1500 * 9)
                .add(OreBlocks.RAW_LUMINITE_BLOCK, 3000 * 9);
        valueBuilder()
                .add(MaterialItems.GEL, 1)
                .add(MaterialItems.PINK_GEL, 3)
                .add(MaterialItems.SILK, 200)
                .add(MaterialItems.RAW_ASPHALT, 1);
        valueBuilder()
                // Adamantite Sword 2 gold 76 silver
                // Ash Wood Sword  20 copper
                .add(SwordItems.BAT_BAT, wrap(25, 0))
                // Beam Sword 3 gold
                .add(SwordItems.BEE_KEEPER, wrap(2, 0, 0))
                .add(SwordItems.BLADE_OF_GRASS, wrap(54, 0))
                // Bladed Glove 1 gold
                // Bladetongue 4 gold
                .add(SwordItems.BLOOD_BUTCHERER, wrap(27, 0))
                .add(SwordItems.BLUE_LIGHT_SABER, wrap(54, 0))
                // Blue Phasesaber 1 gold
                .add(SwordItems.BONE_SWORD, wrap(18, 0))
                // Boreal Wood Sword 20 copper
                // Brand of the Inferno 1 gold
                // Breaker Blade 3 gold
                .add(SwordItems.CACTUS_SWORD, wrap(3, 60))
                .add(SwordItems.CANDY_CANE_SWORD, wrap(27, 0))
                // Chlorophyte Claymore 5 gold 52 silver
                // Chlorophyte Saber 5 gold 52 silver
                // Christmas Tree Sword 10 gold
                // Classy Cane 50 silver
                // Cobalt Sword 1 gold 38 silver
                .add(SwordItems.COPPER_BOARD_SWORD, 90)
                .add(SwordItems.COPPER_SHORT_SWORD, 70)
                // Cutlass 3 gold 60 silver
                // Death Sickle 7 gold 50 silver
                // Ebonwood Sword 20 copper
                .add(SwordItems.ENCHANTED_SWORD, wrap(3, 0, 0))
                // Excalibur 4 gold 60 silver
                .add(SwordItems.EXOTIC_SCIMITAR, wrap(20, 0))
                // Fetid Baghnakhs 8 gold
                // Flying Dragon 5 gold
                // Flymeal 35 silver
                // Frostbrand 5 gold
                // Gladius 30 silver
                .add(SwordItems.GOLDEN_BOARD_SWORD, wrap(18, 0))
                .add(SwordItems.GOLDEN_SHORT_SWORD, wrap(14, 0))
                .add(SwordItems.GREEN_LIGHT_SABER, wrap(54, 0))
                // Green Phasesaber 1 gold
                // Ham Bat 1 gold
                .add(SwordItems.ICE_BLADE, wrap(40, 0))
                // Ice Sickle 5 gold
                // Influx Waver 10 gold
                .add(Items.IRON_SWORD, wrap(3, 60))
                .add(SwordItems.IRON_SHORT_SWORD, wrap(2, 80))
                .add(SwordItems.KATANA, wrap(2, 50, 0))
                // Keybrand 4 gold
                .add(SwordItems.LEAD_BOARD_SWORD, wrap(5, 40))
                .add(SwordItems.LEAD_SHORT_SWORD, wrap(4, 20))
                .add(SwordItems.LIGHTS_BANE, wrap(27, 0))
                .add(SwordItems.MANDIBLE_BLADE, wrap(10, 0))
                // Meowmere 20 gold
                .add(SwordItems.MURAMASA, wrap(1, 75, 0))
                // Mythril Sword 2 gold 7 silver
                // Night's Edge 4 gold
                .add(SwordItems.ORANGE_LIGHT_SABER, wrap(54, 0))
                // Orange Phasesaber 1 gold
                // Orichalcum Sword 2 gold 53 silver
                // Palladium Sword 1 gold 84 silver
                // Palm Wood Sword 20 copper
                // Pearlwood Sword 20 copper
                .add(SwordItems.PLATINUM_BOARD_SWORD, wrap(27, 0))
                .add(SwordItems.PLATINUM_SHORT_SWORD, wrap(21, 0))
                // Psycho Knife 10 gold
                .add(SwordItems.PURPLE_CLUBBERFISH, wrap(1, 0, 0))
                .add(SwordItems.PURPLE_LIGHT_SABER, wrap(54, 0))
                // Purple Phasesaber 1 gold
                .add(SwordItems.RED_LIGHT_SABER, wrap(54, 0))
                // Red Phasesaber 1 gold
                // Rich Mahogany Sword 20 copper
                // Ruler 2 silver
                // Seedler 10 gold
                // Shadewood Sword 20 copper
                .add(SwordItems.SILVER_BOARD_SWORD, wrap(9, 0))
                .add(SwordItems.SILVER_SHORT_SWORD, wrap(7, 0))
                // Slap Hand 5 gold
                // Star Wrath 20 gold
                .add(SwordItems.STARFURY, wrap(1, 0, 0))
                .add(SwordItems.STYLISH_SCISSORS, wrap(50, 0))
                .add(SwordItems.TENTACLE_MACE, wrap(50, 0))
                // Terra Blade 20 gold
                // The Horseman's Blade 10 gold
                .add(SwordItems.TIN_BOARD_SWORD, wrap(1, 35))
                .add(SwordItems.TIN_SHORT_SWORD, wrap(1, 5))
                // Titanium Sword 3 gold 22 silver
                // True Excalibur 10 gold
                // True Night's Edge 10 gold
                .add(SwordItems.TUNGSTEN_BOARD_SWORD, wrap(13, 50))
                .add(SwordItems.TUNGSTEN_SHORT_SWORD, wrap(10, 50))
                .add(SwordItems.VOLCANO, wrap(54, 0))
                .add(SwordItems.WHITE_LIGHT_SABER, wrap(54, 0))
                // White Phasesaber 1 gold
                .add(Items.WOODEN_SWORD, 20)
                .add(SwordItems.YELLOW_LIGHT_SABER, wrap(54, 0))
                // Yellow Phasesaber 1 gold
                .add(SwordItems.ZOMBIE_ARM, wrap(4, 0))

                // Bananarang 12 gold
                // Bloody Machete 1 gold
                // Combat Wrench 50 silver
                .add(TEBoomerangItems.ENCHANTED_BOOMERANG, wrap(1, 0, 0))
                .add(TEBoomerangItems.FLAMARANG, wrap(2, 0, 0))
                // Flying Knife 8 gold
                // Fruitcake Chakram 1 gold
                .add(TEBoomerangItems.ICE_BOOMERANG, wrap(1, 0, 0))
                // Light Disc 15 gold
                // Paladin's Hammer 10 gold
                // Possessed Hatchet 7 gold
                .add(TEBoomerangItems.SHROOMERANG, wrap(60, 0))
                // Thorn Chakram 1 gold
                .add(TEBoomerangItems.TRIMARANG, wrap(2, 0, 0))
                .add(TEBoomerangItems.WOOD_BOOMERANG, wrap(20, 0));
        valueBuilder() // 探索
                .add(ModBlocks.ROPE, 2)
                .add(SwordItems.UMBRELLA, wrap(20, 0))
                .add(SwordItems.TRAGIC_UMBRELLA, wrap(2, 0, 0))
                .add(TCItems.MAGIC_MIRROR, wrap(1, 0, 0))
                .add(ToolItems.ICE_MIRROR, wrap(1, 0, 0))
                .add(ToolItems.BINOCULARS, wrap(3, 0, 0))
                // 信号枪
                .add(SwordItems.BREATHING_REED, wrap(20, 0))
                .add(ToolItems.ROPE_COIL, 20)
                // 耍蛇者长笛
                .add(ToolItems.MAGIC_CONCH, wrap(1, 0, 0))
                .add(ToolItems.DEMON_CONCH, wrap(1, 0, 0))
                .add(TCItems.CELL_PHONE, wrap(8, 0, 0));
        // 贝壳电话
        valueBuilder()
                .add(ToolItems.SHADOW_KEY, wrap(1, 75, 0));
        valueBuilder()
                .add(LightPetItems.SHADOW_ORB, wrap(1, 50, 0))
                .add(LightPetItems.CRIMSON_HEART, wrap(1, 50, 0))
                .add(LightPetItems.MAGIC_LANTERN, wrap(2, 0, 0));
        valueBuilder()
                .add(TERideableItems.SLIMY_SADDLE, wrap(5, 0, 0))
                .add(TERideableItems.HONEYED_GOGGLES, wrap(5, 0, 0));
        valueBuilder() // 影响地形
                .add(AxeItems.STAFF_OF_REGROWTH, wrap(50, 0))
                .add(ModItems.HIVE_WAND, wrap(50, 0))
                .add(ModItems.LIVING_WOOD_WAND, wrap(25, 0))
                .add(ModItems.LEAF_WAND, wrap(25, 0))
                .add(ModItems.LIVING_MAHOGANY_WAND, wrap(25, 0))
                .add(ModItems.RICH_MAHOGANY_LEAF_WAND, wrap(25, 0))
                .add(ConsumableItems.PURIFICATION_POWDER, 15)
                .add(ConsumableItems.VILE_POWDER, 20)
                .add(ConsumableItems.VICIOUS_POWDER, 20);
        valueBuilder() // 电路
                .add(ToolItems.RED_WRENCH, wrap(40, 0))
                .add(ToolItems.GREEN_WRENCH, wrap(40, 0))
                .add(ToolItems.BLUE_WRENCH, wrap(40, 0))
                .add(ToolItems.YELLOW_WRENCH, wrap(40, 0))
                .add(ToolItems.WIRE_CUTTER, wrap(40, 0));
        valueBuilder()
                // Adamantite Glaive 1 gold 80 silver
                // Chlorophyte Partisan 3 gold 60 silver
                // Cobalt Naginata 90 silver
                .add(LanceItems.DARK_LANCE, wrap(2, 50));
        // Ghastly Glaive 1 gold
        // Gungnir 4 gold 60 silver
        // Mushroom Spear 14 gold
        // Mythril Halberd 1 gold 35 silver
        // North Pole 9 gold
        // Obsidian Swordfish 1 gold
        // Orichalcum Halberd 1 gold 65 silver
        // Palladium Pike 1 gold 20 silver
        // Spear 2 silver
        // Storm Spear 30 silver
        // Swordfish 50 silver
        // The Rotted Fork 1 gold 50 silver
        // Titanium Trident 2 gold 10 silver
        // Trident 20 silver
        valueBuilder()
                // Aerial Bane 5 gold
                // Ash Wood Bow 20 copper
                // Blood Rain Bow 1 gold
                // Boreal Wood Bow 20 copper
                .add(BowItems.COPPER_BOW, 70)
                .add(BowItems.COPPER_SHORT_BOW, 70)
                .add(BowItems.DAEDALUS_STORM_BOW, wrap(8, 0, 0))
                .add(BowItems.DEMON_BOW, 3600)
                // Ebonwood Bow 20 copper
                // Eventide 5 gold
                .add(BowItems.GOLDEN_BOW, 1400)
                .add(BowItems.GOLDEN_SHORT_BOW, 1400)
                // Hellwing Bow 2 gold 50 silver
                // Ice Bow 5 gold
                .add(BowItems.IRON_BOW, 280)
                .add(BowItems.IRON_SHORT_BOW, 280)
                .add(BowItems.LEAD_BOW, 420)
                .add(BowItems.LEAD_SHORT_BOW, 420)
                // Marrow 54 silver
                .add(BowItems.MOLTEN_FURY, 5400)
                // Palm Wood Bow 20 copper
                // Pearlwood Bow 20 copper
                // Phantasm 10 gold
                // Phantom Phoenix 1 gold
                .add(BowItems.PLATINUM_BOW, 2100)
                .add(BowItems.PLATINUM_SHORT_BOW, 2100)
                // Pulse Bow 9 gold
                // Rich Mahogany Bow 20 copper
                // Shadewood Bow 20 copper
                // Shadowflame Bow 2 gold
                .add(BowItems.SILVER_BOW, 700)
                .add(BowItems.SILVER_SHORT_BOW, 700)
                .add(BowItems.TENDON_BOW, 3600)
                .add(BowItems.THE_BEES_KNEES, wrap(2, 0, 0))
                .add(BowItems.TIN_BOW, 150)
                .add(BowItems.TIN_SHORT_BOW, 150)
                .add(BowItems.TUNGSTEN_BOW, 1050)
                .add(BowItems.TUNGSTEN_SHORT_BOW, 1050)
                .add(Items.BOW, 40)
                .add(BowItems.FOSSIL_BOW, 3000)
                .add(BowItems.HUNTING_BOW, 3800)
                .add(BowItems.WOODEN_SHORT_BOW, 40);
        valueBuilder()
                .add(PickaxeItems.REAVER_SHARK_PICKAXE, wrap(1, 50, 0))
                .add(Items.NETHERITE_PICKAXE, 6000)
                .add(PickaxeItems.MOLTEN_PICKAXE, 5400)
                .add(PickaxeItems.DEATHBRINGER_PICKAXE, 3600)
                .add(PickaxeItems.NIGHTMARE_PICKAXE, 3600)
                .add(PickaxeItems.PLATINUM_PICKAXE, 3000)
                .add(PickaxeItems.FOSSIL_PICKAXE, wrap(30, 0))
                .add(PickaxeItems.BONE_PICKAXE, wrap(30, 0))
                .add(Items.GOLDEN_PICKAXE, 2800)
                .add(PickaxeItems.GOLDEN_PICKAXE, 2000)
                .add(PickaxeItems.TUNGSTEN_PICKAXE, 1500)
                .add(PickaxeItems.SILVER_PICKAXE, 1000)
                .add(PickaxeItems.LEAD_PICKAXE, 600)
                .add(Items.IRON_PICKAXE, 400)
                .add(PickaxeItems.TIN_PICKAXE, 150)
                .add(PickaxeItems.COPPER_PICKAXE, 50);
        valueBuilder()
                .add(HammerItems.ROCKFISH, wrap(1, 50, 0))
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
                .add(HammerItems.WOODEN_HAMMER, 10);
        valueBuilder()
                .add(FishingPoleItems.WOOD_FISHING_POLE, 60)
                .add(FishingPoleItems.REINFORCED_FISHING_POLE, wrap(24, 0))
                .add(FishingPoleItems.FISHER_OF_SOULS, wrap(2, 40, 0))
                .add(FishingPoleItems.FLESHCATCHER, wrap(3, 12, 0))
                .add(FishingPoleItems.SCARAB_FISHING_ROD, wrap(2, 0, 0))
                .add(FishingPoleItems.CHUM_CASTER, wrap(2, 0, 0))
                .add(FishingPoleItems.FIBERGLASS_FISHING_POLE, wrap(1, 0, 0))
                .add(FishingPoleItems.MECHANICS_ROD, wrap(4, 0, 0))
                .add(FishingPoleItems.SITTING_DUCKS_FISHING_POLE, wrap(7, 0, 0))
                .add(FishingPoleItems.HOTLINE_FISHING_HOOK, wrap(10, 0, 0))
                .add(FishingPoleItems.HOTLINE_FISHING_HOOK, wrap(17, 20, 0));
        valueBuilder()
                .add(AxeItems.AXE_OF_REGROWTH, wrap(1, 50, 0))
                .add(AxeItems.LUCY_THE_AXE, wrap(1, 50, 0))
                .add(Items.NETHERITE_AXE, 6000)
                .add(Items.DIAMOND_AXE, 3000)
                .add(Items.GOLDEN_AXE, 2800)
                .add(Items.IRON_AXE, 2800)
                .add(AxeItems.WAR_AXE_OF_THE_NIGHT, 2700)
                .add(AxeItems.BLOOD_LUST_CLUSTER, 2700)
                .add(AxeItems.PLATINUM_AXE, 1200)
                .add(AxeItems.GOLDEN_AXE, 800)
                .add(AxeItems.TUNGSTEN_AXE, 600)
                .add(AxeItems.SILVER_AXE, 400)
                .add(AxeItems.LEAD_AXE, 240)
                .add(Items.IRON_AXE, 160)
                .add(AxeItems.TIN_AXE, 60)
                .add(AxeItems.COPPER_AXE, 40);
        valueBuilder()
                .add(ManaWeaponItems.WAND_OF_SPARKING, 10000)
                .add(ManaWeaponItems.WAND_OF_FROSTING, 3500)
                .add(ManaWeaponItems.AMETHYST_STAFF, 2800)
                .add(ManaWeaponItems.TOPAZ_STAFF, 2800)
                .add(ManaWeaponItems.SAPPHIRE_STAFF, 2800)
                .add(ManaWeaponItems.EMERALD_STAFF, 2800)
                .add(ManaWeaponItems.RUBY_STAFF, 2800)
                .add(ManaWeaponItems.DIAMOND_STAFF, 2800)
                .add(ManaWeaponItems.AMBER_STAFF, 2800)
                .add(ManaWeaponItems.VILETHRON, 15000)
                // 猩红魔杖
                .add(ManaWeaponItems.WEATHER_PAIN, 15000)
                // 魔法飞弹
                .add(ManaWeaponItems.AQUA_SCEPTER, 17500)
                // 烈焰火鞭
                .add(ManaWeaponItems.BEE_GUN, 20000)
                .add(ManaWeaponItems.SPACE_GUN, 4000)
                .add(ManaWeaponItems.WATER_BOLT, 15000)
                // 恶魔之镰
                // 骷髅头法书
                .add(ManaWeaponItems.FLOWER_OF_FIRE, 25000)
                .add(ManaWeaponItems.THUNDER_ZAPPER, 2100);
        // 灰冲击枪
        valueBuilder()
                .add(TESummonItems.SLIME_STAFF, wrap(2, 0, 0))
                .add(TESummonItems.HORNET_STAFF, wrap(70, 0))
                // 小鬼法杖
                // 吸血鬼青蛙法杖
                .add(TESummonItems.FINCH_STAFF, wrap(1, 0, 0));
        // 小雪怪法杖
        // 阿比盖尔的花
        // 眼球激光塔
        valueBuilder()
                .add(TEWhipItems.LEATHER_WHIP, wrap(2, 0, 0));
        // 荆鞭
        // 脊柱骨鞭
        valueBuilder()
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
                .add(TCItems.WORKSHOP, wrap(2, 0, 0));
        // todo AccessoryItems
        valueBuilder()
                .add(FoodItems.PAD_THAI, 5500);
        valueBuilder()
                .add(TGItems.BOOMSTICK, wrap(2, 0, 0))
                // Candy Corn Rifle 10 gold
                // Chain Gun 9 gold
                // Clockwork Assault Rifle 3 gold
                .add(TGItems.FLINTLOCK_PISTOL, wrap(1, 0, 0))
                // Gatligator 7 gold
                .add(TGItems.HAND_GUN, wrap(1, 75, 0))
                // Megashark 7 gold
                .add(TGItems.MINISHARK, wrap(7, 0, 0))
                .add(TGItems.MUSKET, wrap(1, 50, 0))
                // Onyx Blaster 5 gold
                // Pew-matic Horn 1 gold 50 silver
                // Phoenix Blaster 3 gold 50 silver
                // Quad-Barrel Shotgun 7 gold
                // Red Ryder 2 gold
                // Revolver 2 gold
                // S.D.M.G. 15 gold
                .add(TGItems.SHOTGUN, wrap(5, 0, 0))
                // Sniper Rifle 8 gold
                .add(TGItems.TACTICAL_SHOTGUN, wrap(8, 0, 0))
                .add(TGItems.THE_UNDERTAKER, wrap(1, 50, 0))
                // Uzi 7 gold
                // Venus Magnum 5 gold
                // Vortex Beater 10 gold
                // Xenopopper 10 gold
                .add(TGItems.MUSKET_BULLET, 1)
                .add(TGItems.METEOR_SHOT, 1)
                .add(TGItems.SILVER_BULLET, 3)
                .add(TGItems.CRYSTAL_BULLET, 6)
                .add(TGItems.CURSED_BULLET, 6)
                .add(TGItems.CHLOROPHYTE_BULLET, 10)
                .add(TGItems.HIGH_VELOCITY_BULLET, 8)
                .add(TGItems.ICHOR_BULLET, 6)
                .add(TGItems.VENOM_BULLET, 8)
                .add(TGItems.PARTY_BULLET, 2)
                .add(TGItems.NANO_BULLET, 8)
                .add(TGItems.EXPLODING_BULLET, 8)
                .add(TGItems.GOLDEN_BULLET, 8)
                .add(TGItems.ENDLESS_MUSKET_POUCH, wrap(1, 0, 0))
                .add(TGItems.LUMINITE_BULLET, 2)
                .add(TGItems.TUNGSTEN_BULLET, 3);
        valueBuilder()
                .add(ConsumableItems.SHURIKEN, 15)
                .add(ConsumableItems.THROWING_KNIVES, 50)
                // 毒刀 12铜
                .add(ConsumableItems.GRENADE, 75)
                .add(ConsumableItems.STICKY_GRENADE, 15)
                .add(ConsumableItems.BOUNCY_GRENADE, 20)
                .add(ConsumableItems.SPIKY_BALL, 16)
                .add(MaterialItems.DUNGEON_DEMON_BONE, 10)
                // 骨投刀 10铜
                // 星形茴香 5铜
                // 莫洛托夫鸡尾酒 1 银
                // 寒霜飞鱼 16 铜
                .add(ConsumableItems.JAVELIN, 5)
                // 骨头标枪 10铜
                .add(ConsumableItems.BEENADE, wrap(5, 0));
        valueBuilder()
                .add(ConsumableItems.BOMB, 60)
                .add(ConsumableItems.STICKY_BOMB, wrap(1, 0))
                .add(ConsumableItems.BOUNCY_BOMB, 80)
                .add(ConsumableItems.STICKY_DIRT_BOMB, wrap(1, 0))
                .add(ConsumableItems.DYNAMITE, wrap(4, 0))
                .add(ConsumableItems.STICKY_DYNAMITE, wrap(4, 0))
                .add(ConsumableItems.BOUNCY_DYNAMITE, wrap(4, 0))
                .add(ConsumableItems.BOMB_FISH, wrap(2, 0));
        // 快乐手榴弹
        valueBuilder()
                .add(Items.ARROW, 1)
                .add(ArrowItems.FLAMING_ARROW, 2)
                .add(ArrowItems.UNHOLY_ARROW, 8)
                .add(ArrowItems.STAR_ARROW, 20)
                .add(ArrowItems.HELLFIRE_ARROW, 20)
                // Holy Arrow 16 copper
                // Cursed Arrow 8 copper
                .add(ArrowItems.FROSTBURN_ARROW, 3)
                // Chlorophyte Arrow 20 copper
                // Ichor Arrow 8 copper
                // Venom Arrow 18 copper
                .add(ArrowItems.BONE_ARROW, 3)
                // Endless Quiver 1 gold
                // Luminite Arrow 2 copper
                .add(ArrowItems.SHIMMER_ARROW, 3);
        valueBuilder() // 其它
                .add(ConsumableItems.DRY_BOMB, wrap(5, 0))
                .add(ConsumableItems.WET_BOMB, wrap(5, 0))
                .add(ConsumableItems.LAVA_BOMB, wrap(5, 0))
                .add(ConsumableItems.HONEY_BOMB, wrap(5, 0))
                .add(ToolItems.BUG_NET, wrap(5, 0))
                .add(ToolItems.GOLDEN_BUG_NET, wrap(5, 0, 0))
        ;
        valueBuilder()
                .add(ArmorItems.MINING_HELMET, wrap(80, 0));
        valueBuilder()
                .add(FunctionalBlocks.PIGGY_BANK, wrap(20, 0))
                .add(FunctionalBlocks.SAFE, wrap(80, 0));
        valueBuilder()
                .add(PotionItems.LESSER_HEALING_POTION, 300)
                .add(PotionItems.LESSER_MANA_POTION, 250);
        valueBuilder()
                .add(PaintItems.PAINTBRUSH, wrap(20, 0))
                .add(PaintItems.PAINT_ROLLER, wrap(20, 0))
                .add(PaintItems.PAINT_SCRAPER, wrap(20, 0))
                .add(PaintItems.RED_PAINT, 5)
                .add(PaintItems.DEEP_RED_PAINT, 5)
                .add(PaintItems.ORANGE_PAINT, 5)
                .add(PaintItems.DEEP_ORANGE_PAINT, 5)
                .add(PaintItems.YELLOW_PAINT, 5)
                .add(PaintItems.DEEP_YELLOW_PAINT, 5)
                .add(PaintItems.LIME_PAINT, 5)
                .add(PaintItems.DEEP_LIME_PAINT, 5)
                .add(PaintItems.GREEN_PAINT, 5)
                .add(PaintItems.DEEP_GREEN_PAINT, 5)
                .add(PaintItems.TEAL_PAINT, 5)
                .add(PaintItems.DEEP_TEAL_PAINT, 5)
                .add(PaintItems.CYAN_PAINT, 5)
                .add(PaintItems.DEEP_CYAN_PAINT, 5)
                .add(PaintItems.SKY_BLUE_PAINT, 5)
                .add(PaintItems.DEEP_SKY_BLUE_PAINT, 5)
                .add(PaintItems.BLUE_PAINT, 5)
                .add(PaintItems.DEEP_BLUE_PAINT, 5)
                .add(PaintItems.PURPLE_PAINT, 5)
                .add(PaintItems.DEEP_PURPLE_PAINT, 5)
                .add(PaintItems.VIOLET_PAINT, 5)
                .add(PaintItems.DEEP_VIOLET_PAINT, 5)
                .add(PaintItems.PINK_PAINT, 5)
                .add(PaintItems.DEEP_PINK_PAINT, 5)
                .add(PaintItems.BLACK_PAINT, 5)
                .add(PaintItems.GRAY_PAINT, 5)
                .add(PaintItems.WHITE_PAINT, 5)
                .add(PaintItems.BROWN_PAINT, 5);

        valueBuilder()
                .add(VanityArmorItems.SILVER_DYE, wrap(20, 0))
                .add(VanityArmorItems.BROWN_DYE, wrap(20, 0))
                .add(VanityArmorItems.TEAM_DYE, wrap(20, 0));

        valueBuilder()
                .add(NatureBlocks.YELLOW_WILLOW_SAPLING, wrap(20, 0));

        valueBuilder()
                .add(ModItems.GRASS_SEED, 4)
                .add(ModItems.ASH_GRASS_SEED, 30)
                .add(ModItems.CORRUPT_SEED, wrap(1, 0))
                .add(ModItems.TR_CRIMSON_SEED, wrap(1, 0))
                .add(Items.SUNFLOWER, wrap(10, 0))
                .add(Items.OAK_SAPLING, 2)
                .add(Items.PUMPKIN_SEEDS, 50)
                .add(ModItems.HALLOWED_SEED, wrap(4, 0))
                .add(ModItems.MUSHROOM_GRASS_SEED, 30);
        valueBuilder()
                .add(HookItems.GRAPPLING_HOOK, wrap(40, 0))
                .add(HookItems.AMETHYST_HOOK, wrap(40, 0))
                .add(HookItems.TOPAZ_HOOK, wrap(40, 0))
                .add(HookItems.SAPPHIRE_HOOK, wrap(40, 0))
                .add(HookItems.EMERALD_HOOK, wrap(40, 0))
                .add(HookItems.RUBY_HOOK, wrap(40, 0))
                .add(HookItems.AMBER_HOOK, wrap(40, 0))
                .add(HookItems.DIAMOND_HOOK, wrap(40, 0))
                .add(HookItems.WEB_SLINGER, wrap(40, 0))
                .add(HookItems.SKELETRON_HAND, wrap(40, 0))
                .add(HookItems.SLIME_HOOK, wrap(40, 0))
                .add(HookItems.FISH_HOOK, wrap(40, 0))
                .add(HookItems.IVY_WHIP, wrap(40, 0))
                .add(HookItems.BAT_HOOK, wrap(1, 50, 0))
                .add(HookItems.CANDY_CANE_HOOK, wrap(2, 0, 0));
        valueBuilder() // 小玩具物品
                .add(ModItems.WHOOPIE_CUSHION, 20)
                .add(ConsumableItems.SMOKE_BOMB, 4);
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

    private static int wrap(int platinum, int gold, int silver, int copper) {
        return platinum * 1000000 + gold * 10000 + silver * 100 + copper;
    }

    private static int wrap(int gold, int silver, int copper) {
        return gold * 10000 + silver * 100 + copper;
    }

    private static int wrap(int silver, int copper) {
        return silver * 100 + copper;
    }
}