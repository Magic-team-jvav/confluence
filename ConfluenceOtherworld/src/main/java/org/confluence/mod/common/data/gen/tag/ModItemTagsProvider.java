package org.confluence.mod.common.data.gen.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.lib.common.LibTags;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.DeathChestBlock;
import org.confluence.mod.common.block.natural.LogBlockSet;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.*;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.item.common.BaseDyeItem;
import org.confluence.mod.common.item.crossbow.BaseTerraRepeaterItem;
import org.confluence.mod.common.item.potion.AbstractPotionItem;
import org.confluence.mod.integration.waystones.WaystonesHelper;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.common.init.TCTags;
import org.confluence.terra_furniture.common.init.TFBlocks;
import org.confluence.terra_guns.common.init.TGTags;
import org.confluence.terraentity.init.TEItems;
import org.confluence.terraentity.init.TETags;
import org.confluence.terraentity.init.block.TEFigureBlocks;
import org.confluence.terraentity.init.item.TEBoomerangItems;
import org.confluence.terraentity.init.item.TESpawnEggItems;
import org.confluence.terraentity.init.item.TESummonItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@SuppressWarnings("all")
public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, CompletableFuture<TagLookup<Block>> b, @Nullable ExistingFileHelper helper) {
        super(output, provider, b, Confluence.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        IntrinsicTagAppender<Item> hook = tag(ModTags.Items.HOOK);
        HookItems.ITEMS.getEntries().forEach(item -> {
            hook.add(item.get());
        });
        IntrinsicTagAppender<Item> potions = tag(Tags.Items.POTIONS);
        PotionItems.ITEMS.getEntries().forEach(item -> {
            if (item.get() instanceof AbstractPotionItem item1) {
                potions.add(item1);
            }
        });
        IntrinsicTagAppender<Item> foods = tag(Tags.Items.FOODS);
        foods.add(PotionItems.ALE.get());
        FoodItems.ITEMS.getEntries().forEach(item -> {
            foods.add(item.get());
        });

        IntrinsicTagAppender<Item> notFlammableWood = tag(ItemTags.NON_FLAMMABLE_WOOD);
        for (LogBlockSet logBlockSet : LogBlockSet.LOG_BLOCK_SETS) {
            if (logBlockSet.ignitedByLava) continue;
            notFlammableWood.add(logBlockSet.PLANKS.asItem());
            if (logBlockSet.LOG.isBound()) notFlammableWood.add(logBlockSet.LOG.asItem());
            if (logBlockSet.WOOD.isBound()) notFlammableWood.add(logBlockSet.WOOD.asItem());
            if (logBlockSet.STRIPPED_LOG.isBound())
                notFlammableWood.add(logBlockSet.STRIPPED_LOG.asItem());
            if (logBlockSet.STRIPPED_WOOD.isBound())
                notFlammableWood.add(logBlockSet.STRIPPED_WOOD.asItem());
            if (logBlockSet.STAIRS.isBound()) notFlammableWood.add(logBlockSet.STAIRS.asItem());
            if (logBlockSet.SLAB.isBound()) notFlammableWood.add(logBlockSet.SLAB.asItem());
            if (logBlockSet.BUTTON.isBound()) notFlammableWood.add(logBlockSet.BUTTON.asItem());
            if (logBlockSet.FENCE.isBound()) notFlammableWood.add(logBlockSet.FENCE.asItem());
            if (logBlockSet.FENCE_GATE.isBound())
                notFlammableWood.add(logBlockSet.FENCE_GATE.asItem());
            if (logBlockSet.SIGN.isBound()) notFlammableWood.add(logBlockSet.SIGN.asItem());
            if (logBlockSet.PRESSURE_PLATE.isBound())
                notFlammableWood.add(logBlockSet.PRESSURE_PLATE.asItem());
            if (logBlockSet.DOOR.isBound()) notFlammableWood.add(logBlockSet.DOOR.asItem());
        }

        tag(ModTags.Items.REPEATER_ENCHANTABLE);
        tag(ModTags.Items.TOOLS_REPEATER);
        tag(ModTags.Items.MOUNT).addOptionalTag(TETags.Items.CURIOS_MOUNT);
        tag(ModTags.Items.PET).addOptionalTag(TETags.Items.CURIOS_PET);
        tag(ModTags.Items.LIGHT_PET).addOptionalTag(TETags.Items.CURIOS_LIGHT_PET);
        LightPetItems.ITEMS.getEntries().forEach(item -> tag(TETags.Items.CURIOS_LIGHT_PET).add(item.get()));

        IntrinsicTagAppender<Item> boats = tag(ItemTags.BOATS);
        BoatItems.BOAT_ITEMS.getEntries().forEach(item -> boats.add(item.get()));
        IntrinsicTagAppender<Item> chestBoats = tag(ItemTags.CHEST_BOATS);
        BoatItems.CHEST_BOAT_ITEMS.getEntries().forEach(item -> chestBoats.add(item.get()));
        IntrinsicTagAppender<Item> minecart = tag(ModTags.Items.MINECART);
        minecart.add(Items.MINECART);
        MinecartItems.ITEMS.getEntries().forEach(item -> minecart.add(item.get()));
        tag(ModTags.Items.PROVIDE_MANA).add(ModItems.STAR.get(), ModItems.SOUL_CAKE.get(), ModItems.SUGAR_PLUM.get());
        tag(ModTags.Items.PROVIDE_LIFE).add(ModItems.HEART.get(), ModItems.CANDY_APPLE.get(), ModItems.CANDY_CANE.get());
        tag(ModTags.Items.DESERT_FOSSIL).add(NatureBlocks.DESERT_FOSSIL.asItem());
        tag(ModTags.Items.SLUSH).add(NatureBlocks.SLUSH.asItem());
        tag(ModTags.Items.SILT_BLOCK).add(NatureBlocks.SILT_BLOCK.asItem());
        tag(ModTags.Items.MARINE_GRAVEL).add(NatureBlocks.MARINE_GRAVEL.asItem());
        tag(ModTags.Items.POO).add(DecorativeBlocks.POO_BLOCK.asItem(),ModBlocks.POO.asItem());
        tag(ModTags.Items.EXTRACT_SAND).add(Items.OBSIDIAN);
        tag(ModTags.Items.EXTRACT_HONEY_BLOCK).add(Items.BEE_NEST,NatureBlocks.JUNGLE_HIVE_BLOCK.asItem());
        tag(ModTags.Items.EXTRACT_MOSS).add(NatureBlocks.HELIUM_MOSS.asItem(),NatureBlocks.NEON_MOSS.asItem(),NatureBlocks.ARGON_MOSS.asItem(),NatureBlocks.XENON_MOSS.asItem(),NatureBlocks.KRYPTON_MOSS.asItem(),NatureBlocks.LAVA_MOSS.asItem());
        tag(ModTags.Items.JUNK).add(Blocks.LILY_PAD.asItem(), Items.LEATHER_BOOTS, Blocks.SEAGRASS.asItem());
        tag(ModTags.Items.CORALS).add(Blocks.TUBE_CORAL.asItem(), Blocks.TUBE_CORAL_FAN.asItem(), Blocks.TUBE_CORAL_BLOCK.asItem(), Blocks.BRAIN_CORAL.asItem(), Blocks.BRAIN_CORAL_FAN.asItem(), Blocks.BRAIN_CORAL_BLOCK.asItem(),
                Blocks.BUBBLE_CORAL.asItem(), Blocks.BUBBLE_CORAL_FAN.asItem(), Blocks.BUBBLE_CORAL_BLOCK.asItem(), Blocks.FIRE_CORAL.asItem(), Blocks.FIRE_CORAL_FAN.asItem(), Blocks.FIRE_CORAL_BLOCK.asItem(), Blocks.HORN_CORAL.asItem(), Blocks.HORN_CORAL_FAN.asItem(), Blocks.HORN_CORAL_BLOCK.asItem(),
                Blocks.DEAD_TUBE_CORAL.asItem(), Blocks.DEAD_TUBE_CORAL_FAN.asItem(), Blocks.DEAD_TUBE_CORAL_BLOCK.asItem(), Blocks.DEAD_BRAIN_CORAL.asItem(), Blocks.DEAD_BRAIN_CORAL_FAN.asItem(), Blocks.DEAD_BRAIN_CORAL_BLOCK.asItem(),
                Blocks.DEAD_BUBBLE_CORAL.asItem(), Blocks.DEAD_BUBBLE_CORAL_FAN.asItem(), Blocks.DEAD_BUBBLE_CORAL_BLOCK.asItem(), Blocks.DEAD_FIRE_CORAL.asItem(), Blocks.DEAD_FIRE_CORAL_FAN.asItem(), Blocks.DEAD_FIRE_CORAL_BLOCK.asItem(), Blocks.DEAD_HORN_CORAL.asItem(), Blocks.DEAD_HORN_CORAL_FAN.asItem(), Blocks.DEAD_HORN_CORAL_BLOCK.asItem());
        tag(ModTags.Items.EVIL_MATERIAL).add(
                MaterialItems.WORM_TOOTH.get(),
                MaterialItems.VERTEBRA.get(),
                MaterialItems.BLOOD_CLOT_POWDER.get(),
                MaterialItems.ROTTEN_BONE.get()
        );

        tag(Tags.Items.FOODS_RAW_FISH).add(
                FoodItems.SEA_BASS.get(),
                FoodItems.ATLANTIC_COD.get(),
                FoodItems.TROUT.get()
        );
        tag(ModTags.Items.SEAFOOD_DINNER_MATERIALS).add(
                FoodItems.FROSTY_MINNOW.get(),
                FoodItems.ARMORED_CAVE_FISH.get(),
                FoodItems.CHAOS_FISH.get(),
                FoodItems.SCARLET_TIGER_FISH.get(),
                FoodItems.DAMSEL_FISH.get(),
                FoodItems.PISCES_FIN_COD.get(),
                FoodItems.EBONY_KOI.get(),
                FoodItems.FLASHFIN_KOI.get(),
                FoodItems.BLOODY_PIRANHAS.get(),
                FoodItems.PRINCESS_FISH.get(),
                FoodItems.COLORFUL_MINERAL_FISH.get(),
                FoodItems.TILAPIA.get(),
                FoodItems.OBSIDIFISH.get(),
                FoodItems.NEON_GREASE_CARP.get(),
                FoodItems.MIRROR_FISH.get(),
                FoodItems.MOTTLED_OILFISH.get(),
                FoodItems.STINKY_FISH.get()
        );
        tag(ModTags.Items.INITIAL_WOOD).add(
                NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.PLANKS.asItem(),
                NatureBlocks.LIVING_LOG_BLOCKS.PLANKS.asItem(),
                NatureBlocks.LIVING_MAHOGANY_LOG_BLOCKS.PLANKS.asItem(),
                NatureBlocks.BAOBAB_LOG_BLOCKS.PLANKS.asItem(),
                Blocks.OAK_PLANKS.asItem(),
                Blocks.ACACIA_PLANKS.asItem(),
                Blocks.BAMBOO_PLANKS.asItem(),
                Blocks.BIRCH_PLANKS.asItem(),
                Blocks.CHERRY_PLANKS.asItem(),
                Blocks.DARK_OAK_PLANKS.asItem(),
                Blocks.JUNGLE_PLANKS.asItem(),
                Blocks.MANGROVE_PLANKS.asItem(),
                Blocks.SPRUCE_PLANKS.asItem()
        );
        tag(ModTags.Items.QUESTED_FISHES).add(
                QuestedFishes.AMANITA_FUNGIFIN.get(),
                QuestedFishes.ANGELFISH.get(),
                QuestedFishes.BATFISH.get(),
                QuestedFishes.BLOODY_MANOWAR.get(),
                QuestedFishes.BONEFISH.get(),
                QuestedFishes.BUMBLEBEE_TUNA.get(),
                QuestedFishes.BUNNYFISH.get(),
                QuestedFishes.CAPN_TUNABEARD.get(),
                QuestedFishes.CATFISH.get(),
                QuestedFishes.CLOUDFISH.get(),
                QuestedFishes.CLOWNFISH.get(),
                QuestedFishes.CURSEDFISH.get(),
                QuestedFishes.DEMONIC_HELLFISH.get(),
                QuestedFishes.DERPFISH.get(),
                QuestedFishes.DIRTFISH.get(),
                QuestedFishes.DYNAMITE_FISH.get(),
                QuestedFishes.EATER_OF_PLANKTON.get(),
                QuestedFishes.FALLEN_STARFISH.get(),
                QuestedFishes.THE_FISH_OF_CTHULHU.get(),
                QuestedFishes.FISHOTRON.get(),
                QuestedFishes.FISHRON.get(),
                QuestedFishes.GUIDE_VOODOO_FISH.get(),
                QuestedFishes.HARPYFISH.get(),
                QuestedFishes.HUNGERFISH.get(),
                QuestedFishes.ICHORFISH.get(),
                QuestedFishes.INFECTED_SCABBARDFISH.get(),
                QuestedFishes.JEWELFISH.get(),
                QuestedFishes.MIRAGE_FISH.get(),
                QuestedFishes.MUDFISH.get(),
                QuestedFishes.MUTANT_FLINXFIN.get(),
                QuestedFishes.PENGFISH.get(),
                QuestedFishes.PIXIEFISH.get(),
                QuestedFishes.SCARAB_FISH.get(),
                QuestedFishes.SCORPIO_FISH.get(),
                QuestedFishes.SLIMEFISH.get(),
                QuestedFishes.SPIDERFISH.get(),
                QuestedFishes.TROPICAL_BARRACUDA.get(),
                QuestedFishes.TUNDRA_TROUT.get(),
                QuestedFishes.UNICORN_FISH.get(),
                QuestedFishes.WYVERNTAIL.get(),
                QuestedFishes.ZOMBIE_FISH.get()
        );
        tag(ModTags.Items.EMBLEM).add(
                AccessoryItems.SUMMONER_EMBLEM.get(),
                TCItems.RANGER_EMBLEM.get(),
                TCItems.SORCERER_EMBLEM.get(),
                TCItems.WARRIOR_EMBLEM.get()
        );
        tag(ModTags.Items.GOLD_COOKING).add(
                BaitItems.GOLD_BUTTERFLY.get(),
                BaitItems.GOLD_DRAGONFLY.get(),
                BaitItems.GOLD_GRASSHOPPER.get(),
                BaitItems.GOLD_LADYBUG.get(),
                BaitItems.GOLD_WATER_STRIDER.get(),
                BaitItems.GOLD_WORM.get(),
                FoodItems.GOLD_GOLDFISH.get(),
                FoodItems.GOLDEN_CARP.get()
        );
        tag(ModTags.Items.SHADOW_SCALE_AND_TISSUE_SAMPLE).add(
                MaterialItems.SHADOW_SCALE.get(),
                MaterialItems.TISSUE_SAMPLE.get()
        );

        tag(ItemTags.BEACON_PAYMENT_ITEMS).add(
                MaterialItems.LEAD_INGOT.get(),
                MaterialItems.SILVER_INGOT.get(),
                MaterialItems.TUNGSTEN_INGOT.get(),
                MaterialItems.PLATINUM_INGOT.get(),
                MaterialItems.DEMONITE_INGOT.get(),
                MaterialItems.CRIMTANE_INGOT.get(),
                MaterialItems.HELLSTONE_INGOT.get(),
                MaterialItems.COBALT_INGOT.get(),
                MaterialItems.PALLADIUM_INGOT.get(),
                MaterialItems.ORICHALCUM_INGOT.get(),
                MaterialItems.ADAMANTITE_INGOT.get(),
                MaterialItems.TITANIUM_INGOT.get(),
                MaterialItems.HALLOWED_INGOT.get(),
                MaterialItems.CHLOROPHYTE_INGOT.get(),
                MaterialItems.SHROOMITE_INGOT.get(),
                MaterialItems.SPECTRE_INGOT.get(),
                MaterialItems.LUMINITE_INGOT.get(),
                MaterialItems.AMBER.get(),
                MaterialItems.AMETHYST.get(),
                MaterialItems.JADE.get(),
                MaterialItems.RUBY.get(),
                MaterialItems.SAPPHIRE.get(),
                MaterialItems.TOPAZ.get(),
                MaterialItems.OPAL.get(),
                MaterialItems.STURDY_FOSSIL.get(),
                MaterialItems.GELSTONE.get(),
                MaterialItems.COLD_CRYSTAL.get()
        );
        tag(ItemTags.SAPLINGS).add(
                NatureBlocks.RUBY_SAPLING.asItem(),
                NatureBlocks.AMBER_SAPLING.asItem(),
                NatureBlocks.TOPAZ_SAPLING.asItem(),
                NatureBlocks.JADE_SAPLING.asItem(),
                NatureBlocks.DIAMOND_SAPLING.asItem(),
                NatureBlocks.SAPPHIRE_SAPLING.asItem(),
                NatureBlocks.AMETHYST_SAPLING.asItem()
        );
        copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);

        tag(ModTags.Items.EVIL_INGOT).add(MaterialItems.DEMONITE_INGOT.get(), MaterialItems.CRIMTANE_INGOT.get());
        tag(ModTags.Items.LEAD_AND_IRON).addTags(Tags.Items.INGOTS_IRON, ModTags.Items.INGOTS_LEAD);
        tag(ModTags.Items.GOLD_AND_PLATINUM).addTags(Tags.Items.INGOTS_GOLD, ModTags.Items.INGOTS_PLATINUM);
        IntrinsicTagAppender<Item> torch = tag(ModTags.Items.TORCH);
        torch.add(Items.TORCH, Items.SOUL_TORCH);
//        for (Torches torches : Torches.values()) torch.add(torches.item.get());
        tag(ModTags.Items.PROVIDE_LIGHT).addTag(ModTags.Items.TORCH).add(
                Items.LANTERN,
                Items.SOUL_LANTERN,
                Items.LAVA_BUCKET,
                ToolItems.BOTTOMLESS_LAVA_BUCKET.get()
        );
        tag(ModTags.Items.BOTTOMLESS).add(
                ToolItems.BOTTOMLESS_WATER_BUCKET.get(),
                ToolItems.BOTTOMLESS_LAVA_BUCKET.get(),
                ToolItems.BOTTOMLESS_HONEY_BUCKET.get(),
                ToolItems.BOTTOMLESS_SHIMMER_BUCKET.get()
        );
        tag(Tags.Items.FOODS_FRUIT).add(
                FoodItems.APRICOT.get(), FoodItems.BANANA.get(), FoodItems.CHERRY.get(), FoodItems.COCONUT.get(),
                FoodItems.DRAGON_FRUIT.get(), FoodItems.GRAPE_FRUIT.get(), FoodItems.LEMON.get(),
                FoodItems.MANGO.get(), FoodItems.PEACH.get(), FoodItems.PINEAPPLE.get(),
                FoodItems.PLUM.get(), FoodItems.GRAPE.get(), FoodItems.SPICY_PEPPER.get(),
                FoodItems.STAR_FRUIT.get(), FoodItems.POMEGRANATE.get(), FoodItems.RAMBUTAN.get(),
                FoodItems.BLOOD_ORANGE.get(), FoodItems.ELDERBERRY.get(), FoodItems.BLACKCURRANT.get()
        );

        tag(ModTags.Items.COAL_ORE_SMELTING).add(OreBlocks.SANCTIFICATION_COAL_ORE.asItem(), OreBlocks.CORRUPTION_COAL_ORE.asItem(), OreBlocks.FLESHIFICATION_COAL_ORE.asItem());
        tag(ModTags.Items.IRON_ORE_SMELTING).add(OreBlocks.SANCTIFICATION_IRON_ORE.asItem(), OreBlocks.CORRUPTION_IRON_ORE.asItem(), OreBlocks.FLESHIFICATION_IRON_ORE.asItem());
        tag(ModTags.Items.TIN_ORE_SMELTING).addTag(ModTags.Items.ORES_TIN).add(MaterialItems.RAW_TIN.get());
        tag(ModTags.Items.COPPER_ORE_SMELTING).add(OreBlocks.SANCTIFICATION_COPPER_ORE.asItem(), OreBlocks.CORRUPTION_COPPER_ORE.asItem(), OreBlocks.FLESHIFICATION_COPPER_ORE.asItem());
        tag(ModTags.Items.LEAD_ORE_SMELTING).addTag(ModTags.Items.ORES_LEAD).add(MaterialItems.RAW_LEAD.get());
        tag(ModTags.Items.SILVER_ORE_SMELTING).addTag(ModTags.Items.ORES_SILVER).add(MaterialItems.RAW_SILVER.get());
        tag(ModTags.Items.TUNGSTEN_ORE_SMELTING).addTag(ModTags.Items.ORES_TUNGSTEN).add(MaterialItems.RAW_TUNGSTEN.get());
        tag(ModTags.Items.GOLD_ORE_SMELTING).add(OreBlocks.SANCTIFICATION_GOLD_ORE.asItem(), OreBlocks.CORRUPTION_GOLD_ORE.asItem(), OreBlocks.FLESHIFICATION_GOLD_ORE.asItem());
        tag(ModTags.Items.PLATINUM_ORE_SMELTING).addTag(ModTags.Items.ORES_PLATINUM).add(MaterialItems.RAW_PLATINUM.get());
        tag(ModTags.Items.DEMONITE_ORE_SMELTING).addTag(ModTags.Items.ORES_DEMONITE).add(MaterialItems.RAW_DEMONITE.get());
        tag(ModTags.Items.CRIMTANE_ORE_SMELTING).addTag(ModTags.Items.ORES_CRIMTANE).add(MaterialItems.RAW_CRIMTANE.get());
        tag(ModTags.Items.METEORITE_ORE_SMELTING).add(OreBlocks.METEORITE_ORE.asItem(), MaterialItems.RAW_METEORITE.get());
        tag(ModTags.Items.DIAMOND_ORE_SMELTING).add(OreBlocks.SANCTIFICATION_DIAMOND_ORE.asItem(), OreBlocks.CORRUPTION_DIAMOND_ORE.asItem(), OreBlocks.FLESHIFICATION_DIAMOND_ORE.asItem());
        tag(ModTags.Items.EMERALD_ORE_SMELTING).add(OreBlocks.SANCTIFICATION_EMERALD_ORE.asItem(), OreBlocks.CORRUPTION_EMERALD_ORE.asItem(), OreBlocks.FLESHIFICATION_EMERALD_ORE.asItem());
        tag(ModTags.Items.REDSTONE_ORE_SMELTING).add(OreBlocks.SANCTIFICATION_REDSTONE_ORE.asItem(), OreBlocks.CORRUPTION_REDSTONE_ORE.asItem(), OreBlocks.FLESHIFICATION_REDSTONE_ORE.asItem());

        tag(ModTags.Items.COBALT_ORE_SMELTING).addTag(ModTags.Items.ORES_COBALT).add(MaterialItems.RAW_COBALT.get());
        tag(ModTags.Items.PALLADIUM_ORE_SMELTING).addTag(ModTags.Items.ORES_PALLADIUM).add(MaterialItems.RAW_PALLADIUM.get());
        tag(ModTags.Items.MYTHRIL_ORE_SMELTING).addTag(ModTags.Items.ORES_MYTHRIL).add(MaterialItems.RAW_MYTHRIL.get());
        tag(ModTags.Items.ORICHALCUM_ORE_SMELTING).addTag(ModTags.Items.ORES_ORICHALCUM).add(MaterialItems.RAW_ORICHALCUM.get());
        tag(ModTags.Items.ADAMANTITE_ORE_SMELTING).addTag(ModTags.Items.ORES_ADAMANTITE).add(MaterialItems.RAW_ADAMANTITE.get());
        tag(ModTags.Items.TITANIUM_ORE_SMELTING).addTag(ModTags.Items.ORES_TITANIUM).add(MaterialItems.RAW_TITANIUM.get());

        tag(ItemTags.BOOKSHELF_BOOKS).add(
                ManaWeaponItems.WATER_BOLT.get()
        );
        tag(ItemTags.CAT_FOOD).add(
                FoodItems.SEA_BASS.get(),
                FoodItems.ATLANTIC_COD.get(),
                FoodItems.DAMSEL_FISH.get(),
                FoodItems.TROUT.get(),
                FoodItems.TUNA.get(),
                FoodItems.PARTIAL_MOUTH_FISH.get(),
                FoodItems.YELLOW_EEL.get(),
                FoodItems.TILAPIA.get()
        );
        // neoforge
        tag(Tags.Items.POTION_BOTTLE).add(
                PotionItems.BOTTLE.get(),
                PotionItems.BOTTLED_WATER.get()
        );
        tag(Tags.Items.BONES).add(
                MaterialItems.ROTTEN_BONE.get(),
                MaterialItems.VERTEBRA.get()
        );
        tag(Tags.Items.BRICKS).add(
                DecorativeBlocks.COPPER_BRICKS.asItem(),
                DecorativeBlocks.CRIMTANE_ORE_BRICKS.asItem(),
                DecorativeBlocks.CRIMSTONE_BRICKS.asItem(),
                DecorativeBlocks.GOLDEN_BRICKS.asItem(),
                DecorativeBlocks.IRON_BRICKS.asItem(),
                DecorativeBlocks.DEMONITE_ORE_BRICKS.asItem(),
                DecorativeBlocks.EBONSTONE_BRICKS.asItem(),
                DecorativeBlocks.BLUE_ICE_BRICKS.asItem(),
                DecorativeBlocks.PACKED_ICE_BRICKS.asItem(),
                DecorativeBlocks.LEAD_BRICKS.asItem(),
                DecorativeBlocks.METEORITE_BRICKS.asItem(),
                DecorativeBlocks.PEARLSTONE_BRICKS.asItem(),
                DecorativeBlocks.PLATINUM_BRICKS.asItem(),
                DecorativeBlocks.SILVER_BRICKS.asItem(),
                DecorativeBlocks.SNOW_BRICKS.asItem(),
                DecorativeBlocks.TUNGSTEN_BRICKS.asItem(),
                DecorativeBlocks.OBSIDIAN_BRICKS.asItem(),
                DecorativeBlocks.OBSIDIAN_SMALL_BRICKS.asItem(),
                DecorativeBlocks.CRYSTAL_BLOCK.asItem(),
                DecorativeBlocks.RAINBOW_BRICKS.asItem(),
                DecorativeBlocks.BLUE_BRICKS.asItem(),
                DecorativeBlocks.CHISELED_BLUE_BRICKS.asItem(),
                FunctionalBlocks.FRAGILE_BLUE_BRICKS.asItem(),
                DecorativeBlocks.GREEN_BRICKS.asItem(),
                DecorativeBlocks.CHISELED_GREEN_BRICKS.asItem(),
                FunctionalBlocks.FRAGILE_GREEN_BRICKS.asItem(),
                DecorativeBlocks.PINK_BRICKS.asItem(),
                DecorativeBlocks.CHISELED_PINK_BRICKS.asItem(),
                FunctionalBlocks.FRAGILE_PINK_BRICKS.asItem()
        );
        tag(Tags.Items.BUCKETS).add(
                ToolItems.HONEY_BUCKET.get(),
                ToolItems.BOTTOMLESS_SHIMMER_BUCKET.get(),
                ToolItems.BOTTOMLESS_WATER_BUCKET.get(),
                ToolItems.BOTTOMLESS_LAVA_BUCKET.get(),
                ToolItems.BOTTOMLESS_HONEY_BUCKET.get()
        );
        tag(Tags.Items.BUCKETS_LAVA).add(ToolItems.BOTTOMLESS_LAVA_BUCKET.get());
        tag(Tags.Items.BUCKETS_WATER).add(ToolItems.BOTTOMLESS_WATER_BUCKET.get());
        tag(Tags.Items.CROPS).add(
                MaterialItems.FLOATING_WHEAT_HEADS.get(),
                MaterialItems.WEAVING_CLOUD_COTTON.get(),
                MaterialItems.STAR_PETALS.get()
        );
        tag(Tags.Items.CROPS_WHEAT).add(
                MaterialItems.FLOATING_WHEAT_HEADS.get()
        );
        tag(Tags.Items.DUSTS).add(
                MaterialItems.BLOOD_CLOT_POWDER.get(),
                MaterialItems.PIXIE_DUST.get(),
                ConsumableItems.ROTTEN_BONE_DUST.get(),
                ConsumableItems.BLOODSTAINED_POWDER.get(),
                ConsumableItems.VILE_POWDER.get(),
                ConsumableItems.VICIOUS_POWDER.get(),
                ConsumableItems.PURIFICATION_POWDER.get()
        );
        tag(Tags.Items.DYED_WHITE).add(DecorativeBlocks.WHITE_PURE_GLASS.asItem());
        tag(Tags.Items.DYED_LIGHT_GRAY).add(DecorativeBlocks.LIGHT_GRAY_PURE_GLASS.asItem());
        tag(Tags.Items.DYED_GRAY).add(DecorativeBlocks.GRAY_PURE_GLASS.asItem());
        tag(Tags.Items.DYED_BLACK).add(DecorativeBlocks.BLACK_PURE_GLASS.asItem());
        tag(Tags.Items.DYED_BROWN).add(DecorativeBlocks.BROWN_PURE_GLASS.asItem());
        tag(Tags.Items.DYED_RED).add(DecorativeBlocks.RED_PURE_GLASS.asItem());
        tag(Tags.Items.DYED_ORANGE).add(DecorativeBlocks.ORANGE_PURE_GLASS.asItem());
        tag(Tags.Items.DYED_YELLOW).add(DecorativeBlocks.YELLOW_PURE_GLASS.asItem());
        tag(Tags.Items.DYED_LIME).add(DecorativeBlocks.LIME_PURE_GLASS.asItem());
        tag(Tags.Items.DYED_GREEN).add(DecorativeBlocks.GREEN_PURE_GLASS.asItem());
        tag(Tags.Items.DYED_CYAN).add(DecorativeBlocks.CYAN_PURE_GLASS.asItem());
        tag(Tags.Items.DYED_LIGHT_BLUE).add(DecorativeBlocks.LIGHT_BLUE_PURE_GLASS.asItem());
        tag(Tags.Items.DYED_BLUE).add(DecorativeBlocks.BLUE_PURE_GLASS.asItem());
        tag(Tags.Items.DYED_PURPLE).add(DecorativeBlocks.PURPLE_PURE_GLASS.asItem());
        tag(Tags.Items.DYED_MAGENTA).add(DecorativeBlocks.MAGENTA_PURE_GLASS.asItem());
        tag(Tags.Items.DYED_PINK).add(DecorativeBlocks.PINK_PURE_GLASS.asItem());

        tag(ModTags.Items.GEMS_RUBY).add(MaterialItems.RUBY.get());
        tag(ModTags.Items.GEMS_AMBER).add(MaterialItems.AMBER.get());
        tag(ModTags.Items.GEMS_TOPAZ).add(MaterialItems.TOPAZ.get());
        tag(ModTags.Items.GEMS_JADE).add(MaterialItems.JADE.get());
        tag(ModTags.Items.GEMS_SAPPHIRE).add(MaterialItems.SAPPHIRE.get());
        tag(ModTags.Items.GEMS_AMETHYST).add(MaterialItems.AMETHYST.get());
        tag(Tags.Items.GEMS).addTags(
                ModTags.Items.GEMS_RUBY,
                ModTags.Items.GEMS_AMBER,
                ModTags.Items.GEMS_TOPAZ,
                ModTags.Items.GEMS_JADE,
                ModTags.Items.GEMS_SAPPHIRE,
                ModTags.Items.GEMS_AMETHYST
        );

        tag(Tags.Items.INGOTS).add(
                MaterialItems.TIN_INGOT.get(),
                MaterialItems.LEAD_INGOT.get(),
                MaterialItems.SILVER_INGOT.get(),
                MaterialItems.TUNGSTEN_INGOT.get(),
                MaterialItems.PLATINUM_INGOT.get(),
                MaterialItems.METEORITE_INGOT.get(),
                MaterialItems.DEMONITE_INGOT.get(),
                MaterialItems.CRIMTANE_INGOT.get(),
                MaterialItems.HELLSTONE_INGOT.get(),
                MaterialItems.COBALT_INGOT.get(),
                MaterialItems.PALLADIUM_INGOT.get(),
                MaterialItems.MYTHRIL_INGOT.get(),
                MaterialItems.ORICHALCUM_INGOT.get(),
                MaterialItems.ADAMANTITE_INGOT.get(),
                MaterialItems.TITANIUM_INGOT.get(),
                MaterialItems.HALLOWED_INGOT.get(),
                MaterialItems.CHLOROPHYTE_INGOT.get(),
                MaterialItems.SHROOMITE_INGOT.get(),
                MaterialItems.SPECTRE_INGOT.get(),
                MaterialItems.LUMINITE_INGOT.get()
        );
        tag(Tags.Items.MUSHROOMS).add(
                MaterialItems.GLOWING_MUSHROOM.get(),
                MaterialItems.LIFE_MUSHROOM.get(),
                MaterialItems.VICIOUS_MUSHROOM.get(),
                MaterialItems.VILE_MUSHROOM.get()
        );
        tag(Tags.Items.NUGGETS).add(
                MaterialItems.TIN_NUGGET.get(),
                MaterialItems.LEAD_NUGGET.get(),
                MaterialItems.SILVER_NUGGET.get(),
                MaterialItems.TUNGSTEN_NUGGET.get(),
                MaterialItems.PLATINUM_NUGGET.get(),
                MaterialItems.METEORITE_NUGGET.get(),
                MaterialItems.DEMONITE_NUGGET.get(),
                MaterialItems.CRIMTANE_NUGGET.get(),
                MaterialItems.HELLSTONE_NUGGET.get()
        );

        tag(Tags.Items.SEEDS).add(
                FoodItems.FLOATING_WHEAT_SEED.get(),
                FoodItems.CLOUDWEAVER_SEED.get(),
                FoodItems.STELLAR_BLOSSOM_SEED.get(),
                FoodItems.WATERLEAF_SEED.get(),
                FoodItems.FIREBLOSSOM_SEED.get(),
                FoodItems.MOONGLOW_SEED.get(),
                FoodItems.BLINKROOT_SEED.get(),
                FoodItems.SHIVERTHORN_SEED.get(),
                FoodItems.DAYBLOOM_SEED.get(),
                FoodItems.DEATHWEED_SEED.get()
        );
        tag(Tags.Items.SEEDS_WHEAT).add(
                FoodItems.FLOATING_WHEAT_SEED.get()
        );

        IntrinsicTagAppender<Item> tools = tag(Tags.Items.TOOLS);
        IntrinsicTagAppender<Item> mining_tool_tools = tag(Tags.Items.MINING_TOOL_TOOLS); // 镐子
        IntrinsicTagAppender<Item> mining_loot_enchantable = tag(ItemTags.MINING_LOOT_ENCHANTABLE);
        IntrinsicTagAppender<Item> mining_enchantable = tag(ItemTags.MINING_ENCHANTABLE);
        IntrinsicTagAppender<Item> durability_enchantable = tag(ItemTags.DURABILITY_ENCHANTABLE);
        IntrinsicTagAppender<Item> skip_reset_strength = tag(LibTags.Items.SKIP_RESET_STRENGTH);
        IntrinsicTagAppender<Item> melee_weapon_tools = tag(Tags.Items.MELEE_WEAPON_TOOLS);
        IntrinsicTagAppender<Item> skip_using_slowdown = tag(LibTags.Items.SKIP_USING_SLOWDOWN);
        IntrinsicTagAppender<Item> ranged_weapon_tools = tag(Tags.Items.RANGED_WEAPON_TOOLS);
        IntrinsicTagAppender<Item> weapon_enchantable = tag(ItemTags.WEAPON_ENCHANTABLE);
        IntrinsicTagAppender<Item> sharp_weapon_enchantable = tag(ItemTags.SHARP_WEAPON_ENCHANTABLE);
        IntrinsicTagAppender<Item> crossbow_enchantable = tag(ItemTags.CROSSBOW_ENCHANTABLE);
        IntrinsicTagAppender<Item> repeater_enchantable = tag(ModTags.Items.REPEATER_ENCHANTABLE);
        IntrinsicTagAppender<Item> repeater_crossbow_enchantable = tag(ModTags.Items.REPEATER_CROSSBOW_ENCHANTABLE);
        IntrinsicTagAppender<Item> tools_crossbow = tag(Tags.Items.TOOLS_CROSSBOW);
        IntrinsicTagAppender<Item> tools_repeater = tag(ModTags.Items.TOOLS_REPEATER);
        IntrinsicTagAppender<Item> repeater_crossbow = tag(ModTags.Items.TOOLS_REPEATER_CROSSBOW);

        tag(ModTags.Items.PREFIX_UNIVERSAL_ONLY)
                .addTags(ModTags.Items.TOOLS_DRILL, ModTags.Items.TOOLS_CHAINSAW)
                .add(TEBoomerangItems.ITEMS.getEntries().stream().map(DeferredHolder::get).toArray(Item[]::new));
        tag(ModTags.Items.PREFIX_MELEE_ONLY)
                .addTags(ItemTags.SWORDS, ItemTags.AXES, ItemTags.PICKAXES, ItemTags.SHOVELS, ItemTags.HOES)
                .add(Items.MACE);
        tag(ModTags.Items.PREFIX_RANGED_ONLY)
                .addTags(Tags.Items.RANGED_WEAPON_TOOLS, TGTags.GUN)
                .add(Items.TRIDENT);
        tag(ModTags.Items.PREFIX_MAGIC_ONLY)
                .addTags(ModTags.Items.MANA_WEAPON, ModTags.Items.SUMMONER_WEAPON);
        tag(ModTags.Items.PREFIX_ACCESSORY_ONLY)
                .addTag(TCTags.ACCESSORY);

        IntrinsicTagAppender<Item> dye = tag(ModTags.Items.DYE);
        dye.add(VanityArmorItems.TEAM_DYE.get());

        IntrinsicTagAppender<Item> dyed = tag(Tags.Items.DYED);
        for (BaseDyeItem dyeItem : VanityArmorItems.COLORED_DYE_ITEMS) {
            dye.add(dyeItem);
            dyed.add(dyeItem);
        }

        PaintItems.PAINT_ITEMS.forEach(dyed::add);

        IntrinsicTagAppender<Item> arrows = tag(ItemTags.ARROWS);
        ArrowItems.ITEMS.getEntries().forEach(item -> arrows.add(item.get()));

        IntrinsicTagAppender<Item> gun = tag(TGTags.GUN);
        GunItems.ITEMS.getEntries().forEach(item -> gun.add(item.get()));

        IntrinsicTagAppender<Item> mana_weapon = tag(ModTags.Items.MANA_WEAPON);
        ManaWeaponItems.ITEMS.getEntries().forEach(item -> mana_weapon.add(item.get()));
        skip_using_slowdown.addTag(ModTags.Items.MANA_WEAPON);

        IntrinsicTagAppender<Item> fishing_rod = tag(Tags.Items.TOOLS_FISHING_ROD);
        IntrinsicTagAppender<Item> fishing_enchantable = tag(ItemTags.FISHING_ENCHANTABLE);
        FishingPoleItems.ITEMS.getEntries().forEach(item -> {
            Item value = item.get();
            fishing_rod.add(value);
            fishing_enchantable.add(value);
        });

        IntrinsicTagAppender<Item> axes = tag(ItemTags.AXES);
        AxeItems.ITEMS.getEntries().forEach(item -> {
            Item value = item.get();
            axes.add(value);
            melee_weapon_tools.add(value);
        });

        IntrinsicTagAppender<Item> pickaxes = tag(ItemTags.PICKAXES);
        PickaxeItems.ITEMS.getEntries().forEach(item -> pickaxes.add(item.get()));

        PickaxeAxeItems.ITEMS.getEntries().forEach(item -> {
            Item value = item.get();
            pickaxes.add(value);
            axes.add(value);
        });

        tools.addTag(ModTags.Items.TOOLS_HAMMER);
        mining_loot_enchantable.addTag(ModTags.Items.TOOLS_HAMMER);
        mining_enchantable.addTag(ModTags.Items.TOOLS_HAMMER);
        durability_enchantable.addTag(ModTags.Items.TOOLS_HAMMER);
        IntrinsicTagAppender<Item> hammer = tag(ModTags.Items.TOOLS_HAMMER);
        HammerItems.ITEMS.getEntries().forEach(item -> hammer.add(item.get()));

        mining_tool_tools.addTag(ModTags.Items.TOOLS_DRILL);
        mining_loot_enchantable.addTag(ModTags.Items.TOOLS_DRILL);
        mining_enchantable.addTag(ModTags.Items.TOOLS_DRILL);
        skip_reset_strength.addTag(ModTags.Items.TOOLS_DRILL);
        weapon_enchantable.addTag(ModTags.Items.TOOLS_DRILL);
        IntrinsicTagAppender<Item> drill = tag(ModTags.Items.TOOLS_DRILL);
        DrillItems.ITEMS.getEntries().forEach(item -> drill.add(item.get()));

        IntrinsicTagAppender<Item> hoes = tag(ItemTags.HOES);
        HoeItems.ITEMS.getEntries().forEach(item -> hoes.add(item.get()));

        IntrinsicTagAppender<Item> shovels = tag(ItemTags.SHOVELS);
        ShovelItems.ITEMS.getEntries().forEach(item -> shovels.add(item.get()));

        HoeShovelItems.ITEMS.getEntries().forEach(item -> {
            Item value = item.get();
            hoes.add(value);
            shovels.add(value);
        });

        HamaxeItems.ITEMS.getEntries().forEach(item -> {
            Item value = item.get();
            hammer.add(value);
            axes.add(value);
        });

        IntrinsicTagAppender<Item> tools_bows = tag(Tags.Items.TOOLS_BOW);
        IntrinsicTagAppender<Item> bow_enchantable = tag(ItemTags.BOW_ENCHANTABLE);
        BowItems.ITEMS.getEntries().forEach(item -> {
            Item value = item.get();
            durability_enchantable.add(value);
            bow_enchantable.add(value);
            ranged_weapon_tools.add(value);
            tools_bows.add(value);
            skip_using_slowdown.add(value);
        });

        CrossbowItems.ITEMS.getEntries().forEach(item -> {
            Item value = item.get();
            crossbow_enchantable.add(value);
            tools_crossbow.add(value);
            if (value instanceof BaseTerraRepeaterItem) {
                repeater_enchantable.add(value);
                tools_repeater.add(value);
            }
        });

        IntrinsicTagAppender<Item> swords = tag(ItemTags.SWORDS);
        SwordItems.ITEMS.getEntries().forEach(item -> {
            Item value = item.get();
            melee_weapon_tools.add(value);
            swords.add(value);
        });

        IntrinsicTagAppender<Item> head_armor = tag(ItemTags.HEAD_ARMOR);
        IntrinsicTagAppender<Item> chest_armor = tag(ItemTags.CHEST_ARMOR);
        IntrinsicTagAppender<Item> leg_armor = tag(ItemTags.LEG_ARMOR);
        IntrinsicTagAppender<Item> foot_armor = tag(ItemTags.FOOT_ARMOR);
        ArmorItems.ITEMS.getEntries().forEach(item -> {
            if (item.get() instanceof ArmorItem armor) {
                if (armor.getEquipmentSlot() == EquipmentSlot.HEAD) {
                    head_armor.add(armor);
                } else if (armor.getEquipmentSlot() == EquipmentSlot.CHEST) {
                    chest_armor.add(armor);
                } else if (armor.getEquipmentSlot() == EquipmentSlot.LEGS) {
                    leg_armor.add(armor);
                } else if (armor.getEquipmentSlot() == EquipmentSlot.FEET) {
                    foot_armor.add(armor);
                }
            }
        });

        IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> accessory = tag(TCTags.ACCESSORY);
        AccessoryItems.ITEMS.getEntries().forEach(item -> accessory.add(item.get()));
        accessory.add(ModItems.PARADOX_INTERACTIVE_MEDAL.get(), ModItems.BOREDOMS_PACT_FALLING_RESOLVE.get());
        IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> wings = tag(ModTags.Items.WINGS);
        AccessoryItems.WINGS.forEach(item -> wings.add(item.get()));
        wings.add(TCItems.CELESTIAL_STARBOARD.get());

        IntrinsicTagAppender<Item> tools_chainsaw = tag(ModTags.Items.TOOLS_CHAINSAW);
        skip_reset_strength.addTag(ModTags.Items.TOOLS_CHAINSAW);
        tools.addTag(ModTags.Items.TOOLS_CHAINSAW);
        mining_loot_enchantable.addTag(ModTags.Items.TOOLS_CHAINSAW);
        mining_enchantable.addTag(ModTags.Items.TOOLS_CHAINSAW);
        sharp_weapon_enchantable.addTag(ModTags.Items.TOOLS_CHAINSAW);
        ChainsawItems.ITEMS.getEntries().forEach(item -> tools_chainsaw.add(item.get()));

        IntrinsicTagAppender<Item> spear = tag(ModTags.Items.SPEAR);
        skip_reset_strength.addTag(ModTags.Items.SPEAR);
        melee_weapon_tools.addTag(ModTags.Items.SPEAR);
        sharp_weapon_enchantable.addTag(ModTags.Items.SPEAR);
        SpearItems.ITEMS.getEntries().forEach(item -> spear.add(item.get()));

        IntrinsicTagAppender<Item> lances = tag(ModTags.Items.TOOLS_LANCE);
        skip_reset_strength.addTag(ModTags.Items.TOOLS_LANCE);
        melee_weapon_tools.addTag(ModTags.Items.TOOLS_LANCE);
        weapon_enchantable.addTag(ModTags.Items.TOOLS_LANCE);
        LanceItems.ITEMS.getEntries().forEach(item -> lances.add(item.get()));

        TreasureBagItems.ITEMS.getEntries().forEach(item -> tag(ModTags.Items.TREASURE_BAG).add(item.get()));

        TESummonItems.ITEMS.getEntries().forEach(item -> tag(ModTags.Items.SUMMONER_WEAPON).add(item.get()));

        copy(ModTags.Blocks.COINS, ModTags.Items.COINS);
        tag(ModTags.Items.HARDMODE_RAW_MATERIALS).add(
                MaterialItems.RAW_COBALT.get(),
                MaterialItems.RAW_PALLADIUM.get(),
                MaterialItems.RAW_MYTHRIL.get(),
                MaterialItems.RAW_ORICHALCUM.get(),
                MaterialItems.RAW_ADAMANTITE.get(),
                MaterialItems.RAW_TITANIUM.get(),
                MaterialItems.RAW_CHLOROPHYTE.get(),
                MaterialItems.RAW_LUMINITE.get(),

                MaterialItems.SOUL_OF_FRIGHT.get(),
                MaterialItems.SOUL_OF_MIGHT.get(),
                MaterialItems.SOUL_OF_FLIGHT.get(),
                MaterialItems.SOUL_OF_NIGHT.get(),
                MaterialItems.SOUL_OF_LIGHT.get(),
                MaterialItems.SOUL_OF_SIGHT.get(),
                MaterialItems.SOUL_OF_BRIGHT.get(),

                MaterialItems.CRYSTAL_SHARDS.get(),
                MaterialItems.CURSED_FLAME.get(),
                MaterialItems.ICHOR.get(),
                MaterialItems.PIXIE_DUST.get(),
                MaterialItems.UNICORN_HORN.get(),
                MaterialItems.ANCIENT_CLOTH.get(),
                MaterialItems.SPIDER_FANG.get(),
                MaterialItems.SPELL_TOME.get(),
                MaterialItems.FORBIDDEN_FRAGMENT.get(),

                FoodItems.COLORFUL_MINERAL_FISH.get(),
                FoodItems.CHAOS_FISH.get()
        );
        tag(ModTags.Items.BOSS_SUMMONING).add(
                ConsumableItems.SUSPICIOUS_LOOKING_EYE.get(),
                ConsumableItems.SLIME_CROWN.get(),
                ConsumableItems.WORM_FOOD.get(),
                ConsumableItems.BLOODY_SPINE.get(),
                ConsumableItems.ABEEMINATION.get(),
                ConsumableItems.DEER_THING.get(),
                AccessoryItems.CLOTHIER_VOODOO_DOLL.get(),
                AccessoryItems.GUIDE_VOODOO_DOLL.get()
        );

        tag(ModTags.Items.INGOTS_TIN).add(MaterialItems.TIN_INGOT.get());
        tag(ModTags.Items.INGOTS_LEAD).add(MaterialItems.LEAD_INGOT.get());
        tag(ModTags.Items.INGOTS_SILVER).add(MaterialItems.SILVER_INGOT.get());
        tag(ModTags.Items.INGOTS_TUNGSTEN).add(MaterialItems.TUNGSTEN_INGOT.get());
        tag(ModTags.Items.INGOTS_PLATINUM).add(MaterialItems.PLATINUM_INGOT.get());
        tag(ModTags.Items.INGOTS_METEORITE).add(MaterialItems.METEORITE_INGOT.get());
        tag(ModTags.Items.INGOTS_DEMONITE).add(MaterialItems.DEMONITE_INGOT.get());
        tag(ModTags.Items.INGOTS_CRIMTANE).add(MaterialItems.CRIMTANE_INGOT.get());
        tag(ModTags.Items.INGOTS_HELLSTONE).add(MaterialItems.HELLSTONE_INGOT.get());
        tag(ModTags.Items.INGOTS_COBALT).add(MaterialItems.COBALT_INGOT.get());
        tag(ModTags.Items.INGOTS_PALLADIUM).add(MaterialItems.PALLADIUM_INGOT.get());
        tag(ModTags.Items.INGOTS_MYTHRIL).add(MaterialItems.MYTHRIL_INGOT.get());
        tag(ModTags.Items.INGOTS_ORICHALCUM).add(MaterialItems.ORICHALCUM_INGOT.get());
        tag(ModTags.Items.INGOTS_ADAMANTITE).add(MaterialItems.ADAMANTITE_INGOT.get());
        tag(ModTags.Items.INGOTS_TITANIUM).add(MaterialItems.TITANIUM_INGOT.get());
        tag(ModTags.Items.INGOTS_HALLOWED).add(MaterialItems.HALLOWED_INGOT.get());
        tag(ModTags.Items.INGOTS_CHLOROPHYTE).add(MaterialItems.CHLOROPHYTE_INGOT.get());

        tag(ModTags.Items.NUGGETS_TIN).add(MaterialItems.TIN_NUGGET.get());
        tag(ModTags.Items.NUGGETS_LEAD).add(MaterialItems.LEAD_NUGGET.get());
        tag(ModTags.Items.NUGGETS_SILVER).add(MaterialItems.SILVER_NUGGET.get());
        tag(ModTags.Items.NUGGETS_TUNGSTEN).add(MaterialItems.TUNGSTEN_NUGGET.get());
        tag(ModTags.Items.NUGGETS_PLATINUM).add(MaterialItems.PLATINUM_NUGGET.get());
        tag(ModTags.Items.NUGGETS_METEORITE).add(MaterialItems.METEORITE_NUGGET.get());
        tag(ModTags.Items.NUGGETS_DEMONITE).add(MaterialItems.DEMONITE_NUGGET.get());
        tag(ModTags.Items.NUGGETS_CRIMTANE).add(MaterialItems.CRIMTANE_NUGGET.get());
        tag(ModTags.Items.NUGGETS_HELLSTONE).add(MaterialItems.HELLSTONE_NUGGET.get());
        {
            copy(ModTags.Blocks.STORAGE_BLOCKS_TIN, ModTags.Items.STORAGE_BLOCKS_TIN);
            copy(ModTags.Blocks.STORAGE_BLOCKS_LEAD, ModTags.Items.STORAGE_BLOCKS_LEAD);
            copy(ModTags.Blocks.STORAGE_BLOCKS_SILVER, ModTags.Items.STORAGE_BLOCKS_SILVER);
            copy(ModTags.Blocks.STORAGE_BLOCKS_TUNGSTEN, ModTags.Items.STORAGE_BLOCKS_TUNGSTEN);
            copy(ModTags.Blocks.STORAGE_BLOCKS_PLATINUM, ModTags.Items.STORAGE_BLOCKS_PLATINUM);
            copy(ModTags.Blocks.STORAGE_BLOCKS_METEORITE, ModTags.Items.STORAGE_BLOCKS_METEORITE);
            copy(ModTags.Blocks.STORAGE_BLOCKS_DEMONITE, ModTags.Items.STORAGE_BLOCKS_DEMONITE);
            copy(ModTags.Blocks.STORAGE_BLOCKS_CRIMTANE, ModTags.Items.STORAGE_BLOCKS_CRIMTANE);
            copy(ModTags.Blocks.STORAGE_BLOCKS_HELLSTONE, ModTags.Items.STORAGE_BLOCKS_HELLSTONE);

            copy(ModTags.Blocks.STORAGE_BLOCKS_COBALT, ModTags.Items.STORAGE_BLOCKS_COBALT);
            copy(ModTags.Blocks.STORAGE_BLOCKS_PALLADIUM, ModTags.Items.STORAGE_BLOCKS_PALLADIUM);
            copy(ModTags.Blocks.STORAGE_BLOCKS_MYTHRIL, ModTags.Items.STORAGE_BLOCKS_MYTHRIL);
            copy(ModTags.Blocks.STORAGE_BLOCKS_ORICHALCUM, ModTags.Items.STORAGE_BLOCKS_ORICHALCUM);
            copy(ModTags.Blocks.STORAGE_BLOCKS_ADAMANTITE, ModTags.Items.STORAGE_BLOCKS_ADAMANTITE);
            copy(ModTags.Blocks.STORAGE_BLOCKS_TITANIUM, ModTags.Items.STORAGE_BLOCKS_TITANIUM);

            copy(ModTags.Blocks.STORAGE_BLOCKS_RUBY, ModTags.Items.STORAGE_BLOCKS_RUBY);
            copy(ModTags.Blocks.STORAGE_BLOCKS_AMBER, ModTags.Items.STORAGE_BLOCKS_AMBER);
            copy(ModTags.Blocks.STORAGE_BLOCKS_TOPAZ, ModTags.Items.STORAGE_BLOCKS_TOPAZ);
            copy(ModTags.Blocks.STORAGE_BLOCKS_JADE, ModTags.Items.STORAGE_BLOCKS_JADE);
            copy(ModTags.Blocks.STORAGE_BLOCKS_SAPPHIRE, ModTags.Items.STORAGE_BLOCKS_SAPPHIRE);
            copy(ModTags.Blocks.STORAGE_BLOCKS_AMETHYST, ModTags.Items.STORAGE_BLOCKS_AMETHYST);

            copy(ModTags.Blocks.STORAGE_BLOCKS_RAW_TIN, ModTags.Items.STORAGE_BLOCKS_RAW_TIN);
            copy(ModTags.Blocks.STORAGE_BLOCKS_RAW_LEAD, ModTags.Items.STORAGE_BLOCKS_RAW_LEAD);
            copy(ModTags.Blocks.STORAGE_BLOCKS_RAW_SILVER, ModTags.Items.STORAGE_BLOCKS_RAW_SILVER);
            copy(ModTags.Blocks.STORAGE_BLOCKS_RAW_TUNGSTEN, ModTags.Items.STORAGE_BLOCKS_RAW_TUNGSTEN);
            copy(ModTags.Blocks.STORAGE_BLOCKS_RAW_PLATINUM, ModTags.Items.STORAGE_BLOCKS_RAW_PLATINUM);
            copy(ModTags.Blocks.STORAGE_BLOCKS_RAW_METEORITE, ModTags.Items.STORAGE_BLOCKS_RAW_METEORITE);
            copy(ModTags.Blocks.STORAGE_BLOCKS_RAW_DEMONITE, ModTags.Items.STORAGE_BLOCKS_RAW_DEMONITE);
            copy(ModTags.Blocks.STORAGE_BLOCKS_RAW_CRIMTANE, ModTags.Items.STORAGE_BLOCKS_RAW_CRIMTANE);
            copy(ModTags.Blocks.STORAGE_BLOCKS_RAW_HELLSTONE, ModTags.Items.STORAGE_BLOCKS_RAW_HELLSTONE);

            copy(ModTags.Blocks.STORAGE_BLOCKS_RAW_COBALT, ModTags.Items.STORAGE_BLOCKS_RAW_COBALT);
            copy(ModTags.Blocks.STORAGE_BLOCKS_RAW_PALLADIUM, ModTags.Items.STORAGE_BLOCKS_RAW_PALLADIUM);
            copy(ModTags.Blocks.STORAGE_BLOCKS_RAW_MYTHRIL, ModTags.Items.STORAGE_BLOCKS_RAW_MYTHRIL);
            copy(ModTags.Blocks.STORAGE_BLOCKS_RAW_ORICHALCUM, ModTags.Items.STORAGE_BLOCKS_RAW_ORICHALCUM);
            copy(ModTags.Blocks.STORAGE_BLOCKS_RAW_ADAMANTITE, ModTags.Items.STORAGE_BLOCKS_RAW_ADAMANTITE);
            copy(ModTags.Blocks.STORAGE_BLOCKS_RAW_TITANIUM, ModTags.Items.STORAGE_BLOCKS_RAW_TITANIUM);

            copy(ModTags.Blocks.STORAGE_BLOCKS_STURDY_FOSSIL, ModTags.Items.STORAGE_BLOCKS_STURDY_FOSSIL);
            copy(ModTags.Blocks.STORAGE_BLOCKS_OPAL, ModTags.Items.STORAGE_BLOCKS_OPAL);
            copy(ModTags.Blocks.STORAGE_BLOCKS_GELSTONE, ModTags.Items.STORAGE_BLOCKS_GELSTONE);
            copy(ModTags.Blocks.STORAGE_BLOCKS_COLD_CRYSTAL, ModTags.Items.STORAGE_BLOCKS_COLD_CRYSTAL);

            copy(ModTags.Blocks.STORAGE_BLOCKS_FLOATING_WHEAT_BALE, ModTags.Items.STORAGE_BLOCKS_FLOATING_WHEAT_BALE);

            copy(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS);
        }
        {
            tag(ModTags.Items.RAW_MATERIALS_TIN).add(MaterialItems.RAW_TIN.get());
            tag(ModTags.Items.RAW_MATERIALS_LEAD).add(MaterialItems.RAW_LEAD.get());
            tag(ModTags.Items.RAW_MATERIALS_SILVER).add(MaterialItems.RAW_SILVER.get());
            tag(ModTags.Items.RAW_MATERIALS_TUNGSTEN).add(MaterialItems.RAW_TUNGSTEN.get());
            tag(ModTags.Items.RAW_MATERIALS_PLATINUM).add(MaterialItems.RAW_PLATINUM.get());
            tag(ModTags.Items.RAW_MATERIALS_METEORITE).add(MaterialItems.RAW_METEORITE.get());
            tag(ModTags.Items.RAW_MATERIALS_DEMONITE).add(MaterialItems.RAW_DEMONITE.get());
            tag(ModTags.Items.RAW_MATERIALS_CRIMTANE).add(MaterialItems.RAW_CRIMTANE.get());
            tag(ModTags.Items.RAW_MATERIALS_HELLSTONE).add(MaterialItems.RAW_HELLSTONE.get());

            tag(ModTags.Items.RAW_MATERIALS_COBALT).add(MaterialItems.RAW_COBALT.get());
            tag(ModTags.Items.RAW_MATERIALS_PALLADIUM).add(MaterialItems.RAW_PALLADIUM.get());
            tag(ModTags.Items.RAW_MATERIALS_MYTHRIL).add(MaterialItems.RAW_MYTHRIL.get());
            tag(ModTags.Items.RAW_MATERIALS_ORICHALCUM).add(MaterialItems.RAW_ORICHALCUM.get());
            tag(ModTags.Items.RAW_MATERIALS_ADAMANTITE).add(MaterialItems.RAW_ADAMANTITE.get());
            tag(ModTags.Items.RAW_MATERIALS_TITANIUM).add(MaterialItems.RAW_TITANIUM.get());

            tag(ModTags.Items.RAW_MATERIALS_CHLOROPHYTE).add(MaterialItems.RAW_CHLOROPHYTE.get());

            tag(ModTags.Items.RAW_MATERIALS_STURDY_FOSSIL).add(MaterialItems.STURDY_FOSSIL.get());
            tag(ModTags.Items.RAW_MATERIALS_OPAL).add(MaterialItems.OPAL.get());
            tag(ModTags.Items.RAW_MATERIALS_GELSTONE).add(MaterialItems.GELSTONE.get());
            tag(ModTags.Items.RAW_MATERIALS_COLD_CRYSTAL).add(MaterialItems.COLD_CRYSTAL.get());

            tag(ModTags.Items.RAW_MATERIALS_FLOATING_WHEAT).add(MaterialItems.FLOATING_WHEAT_HEADS.get());

            tag(Tags.Items.RAW_MATERIALS).addTags(
                    ModTags.Items.RAW_MATERIALS_TIN,
                    ModTags.Items.RAW_MATERIALS_LEAD,
                    ModTags.Items.RAW_MATERIALS_SILVER,
                    ModTags.Items.RAW_MATERIALS_TUNGSTEN,
                    ModTags.Items.RAW_MATERIALS_PLATINUM,
                    ModTags.Items.RAW_MATERIALS_METEORITE,
                    ModTags.Items.RAW_MATERIALS_DEMONITE,
                    ModTags.Items.RAW_MATERIALS_CRIMTANE,
                    ModTags.Items.RAW_MATERIALS_HELLSTONE,

                    ModTags.Items.RAW_MATERIALS_COBALT,
                    ModTags.Items.RAW_MATERIALS_PALLADIUM,
                    ModTags.Items.RAW_MATERIALS_MYTHRIL,
                    ModTags.Items.RAW_MATERIALS_ORICHALCUM,
                    ModTags.Items.RAW_MATERIALS_ADAMANTITE,
                    ModTags.Items.RAW_MATERIALS_TITANIUM,

                    ModTags.Items.RAW_MATERIALS_CHLOROPHYTE,

                    ModTags.Items.RAW_MATERIALS_STURDY_FOSSIL,
                    ModTags.Items.RAW_MATERIALS_OPAL,
                    ModTags.Items.RAW_MATERIALS_GELSTONE,
                    ModTags.Items.RAW_MATERIALS_COLD_CRYSTAL,

                    ModTags.Items.RAW_MATERIALS_FLOATING_WHEAT
            );
        }
        {
            tag(ItemTags.COAL_ORES).add(OreBlocks.SANCTIFICATION_COAL_ORE.asItem(), OreBlocks.CORRUPTION_COAL_ORE.asItem(), OreBlocks.FLESHIFICATION_COAL_ORE.asItem());
            tag(ItemTags.COPPER_ORES).add(OreBlocks.SANCTIFICATION_COPPER_ORE.asItem(), OreBlocks.CORRUPTION_COPPER_ORE.asItem(), OreBlocks.FLESHIFICATION_COPPER_ORE.asItem());
            tag(ItemTags.DIAMOND_ORES).add(OreBlocks.SANCTIFICATION_DIAMOND_ORE.asItem(), OreBlocks.CORRUPTION_DIAMOND_ORE.asItem(), OreBlocks.FLESHIFICATION_DIAMOND_ORE.asItem());
            tag(ItemTags.EMERALD_ORES).add(OreBlocks.SANCTIFICATION_EMERALD_ORE.asItem(), OreBlocks.CORRUPTION_EMERALD_ORE.asItem(), OreBlocks.FLESHIFICATION_EMERALD_ORE.asItem());
            tag(ItemTags.GOLD_ORES).add(OreBlocks.SANCTIFICATION_GOLD_ORE.asItem(), OreBlocks.CORRUPTION_GOLD_ORE.asItem(), OreBlocks.FLESHIFICATION_GOLD_ORE.asItem());
            tag(ItemTags.IRON_ORES).add(OreBlocks.SANCTIFICATION_IRON_ORE.asItem(), OreBlocks.CORRUPTION_IRON_ORE.asItem(), OreBlocks.FLESHIFICATION_IRON_ORE.asItem());
            tag(ItemTags.LAPIS_ORES).add(OreBlocks.SANCTIFICATION_LAPIS_ORE.asItem(), OreBlocks.CORRUPTION_LAPIS_ORE.asItem(), OreBlocks.FLESHIFICATION_LAPIS_ORE.asItem());
            tag(ItemTags.REDSTONE_ORES).add(OreBlocks.SANCTIFICATION_REDSTONE_ORE.asItem(), OreBlocks.CORRUPTION_REDSTONE_ORE.asItem(), OreBlocks.FLESHIFICATION_REDSTONE_ORE.asItem());

            copy(ModTags.Blocks.ORES_TIN, ModTags.Items.ORES_TIN);
            copy(ModTags.Blocks.ORES_LEAD, ModTags.Items.ORES_LEAD);
            copy(ModTags.Blocks.ORES_SILVER, ModTags.Items.ORES_SILVER);
            copy(ModTags.Blocks.ORES_TUNGSTEN, ModTags.Items.ORES_TUNGSTEN);
            copy(ModTags.Blocks.ORES_PLATINUM, ModTags.Items.ORES_PLATINUM);
            copy(ModTags.Blocks.ORES_METEORITE, ModTags.Items.ORES_METEORITE);
            copy(ModTags.Blocks.ORES_DEMONITE, ModTags.Items.ORES_DEMONITE);
            copy(ModTags.Blocks.ORES_CRIMTANE, ModTags.Items.ORES_CRIMTANE);
            copy(ModTags.Blocks.ORES_HELLSTONE, ModTags.Items.ORES_HELLSTONE);

            copy(ModTags.Blocks.ORES_COBALT, ModTags.Items.ORES_COBALT);
            copy(ModTags.Blocks.ORES_PALLADIUM, ModTags.Items.ORES_PALLADIUM);
            copy(ModTags.Blocks.ORES_MYTHRIL, ModTags.Items.ORES_MYTHRIL);
            copy(ModTags.Blocks.ORES_ORICHALCUM, ModTags.Items.ORES_ORICHALCUM);
            copy(ModTags.Blocks.ORES_ADAMANTITE, ModTags.Items.ORES_ADAMANTITE);
            copy(ModTags.Blocks.ORES_TITANIUM, ModTags.Items.ORES_TITANIUM);

            copy(ModTags.Blocks.ORES_RUBY, ModTags.Items.ORES_RUBY);
            copy(ModTags.Blocks.ORES_AMBER, ModTags.Items.ORES_AMBER);
            copy(ModTags.Blocks.ORES_TOPAZ, ModTags.Items.ORES_TOPAZ);
            copy(ModTags.Blocks.ORES_JADE, ModTags.Items.ORES_JADE);
            copy(ModTags.Blocks.ORES_SAPPHIRE, ModTags.Items.ORES_SAPPHIRE);
            copy(ModTags.Blocks.ORES_AMETHYST, ModTags.Items.ORES_AMETHYST);

            copy(Tags.Blocks.ORES, Tags.Items.ORES);
        }
        copy(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE, Tags.Items.ORES_IN_GROUND_DEEPSLATE);
        copy(Tags.Blocks.ORES_IN_GROUND_NETHERRACK, Tags.Items.ORES_IN_GROUND_NETHERRACK);
        copy(Tags.Blocks.ORES_IN_GROUND_STONE, Tags.Items.ORES_IN_GROUND_STONE);

        tag(ModTags.Items.MOSS_ITEM).add(
                NatureBlocks.BROWN_MOSS.asItem(),
                NatureBlocks.BROWN_MOSS.asItem(),
                NatureBlocks.RED_MOSS.asItem(),
                NatureBlocks.BLUE_MOSS.asItem(),
                NatureBlocks.PURPLE_MOSS.asItem(),
                NatureBlocks.LAVA_MOSS.asItem(),
                NatureBlocks.KRYPTON_MOSS.asItem(),
                NatureBlocks.XENON_MOSS.asItem(),
                NatureBlocks.ARGON_MOSS.asItem(),
                NatureBlocks.NEON_MOSS.asItem(),
                NatureBlocks.HELIUM_MOSS.asItem(),
                NatureBlocks.GLOWING_MUSHROOM_MOSS.asItem()
        );

        // 农作物掉落提升 再生法杖/再生之斧
        tag(ModTags.Items.CROP_FORTUNE).add(ToolItems.STAFF_OF_REGROWTH.get(), AxeItems.AXE_OF_REGROWTH.get());
        // 速发弓：恶魔弓、肌腱弓
        tag(ModTags.Items.FAST_BOW).add(
                BowItems.FOSSIL_BOW.get(),
                BowItems.DEMON_BOW.get(),
                BowItems.TENDON_BOW.get(),
                BowItems.DAEDALUS_STORM_BOW.get(),
                BowItems.MOLTEN_FURY.get(),
                BowItems.THE_BEES_KNEES.get()
        );

        tag(ModTags.Items.AMMO)
                .add(Items.FIREWORK_ROCKET, MaterialItems.FALLING_STAR.get())
                .addTag(ItemTags.ARROWS)
                .addOptionalTag(TGTags.AMMO);

        tag(ModTags.Items.HARDMODE)
                .addTag(ModTags.Items.HARDMODE_RAW_MATERIALS)
                .add(NatureBlocks.PEARL_LOG_BLOCKS.getAllItems().toArray(Item[]::new))
                .add(NatureBlocks.SPOOKY_LOG_BLOCKS.getAllItems().toArray(Item[]::new))
                .add( // 防止肉前出现这些任务
                        QuestedFishes.ICHORFISH.get(),
                        QuestedFishes.CURSEDFISH.get(),
                        QuestedFishes.FISHRON.get(),
                        QuestedFishes.DERPFISH.get(),
                        QuestedFishes.CAPN_TUNABEARD.get(),
                        QuestedFishes.HUNGERFISH.get(),
                        QuestedFishes.MIRAGE_FISH.get(),
                        QuestedFishes.PIXIEFISH.get(),
                        QuestedFishes.UNICORN_FISH.get()
                );

        tag(ModTags.Items.ABLE_TO_DESTROY_ALTAR).add(
                HammerItems.PWNHAMMER.get(),
                HammerItems.HAMMUSH.get()
        );
        tag(Tags.Items.FOODS_SOUP).add(
                FoodItems.BOWL_OF_SOUP.get(),
                FoodItems.GRUB_SOUP.get()
        );
        tag(Tags.Items.FOODS_BREAD).add(
                FoodItems.BOULDER_BREAD.get(),
                FoodItems.CLOUD_BREAD.get()
        );
        tag(ItemTags.MEAT).add(
                FoodItems.RAW_FROG.get(),
                FoodItems.RAW_SQUIRREL.get(),
                FoodItems.RAW_BIRD.get(),
                FoodItems.RAW_DUCK.get()
        );
        tag(Tags.Items.FOODS_RAW_MEAT).add(
                FoodItems.RAW_FROG.get(),
                FoodItems.RAW_SQUIRREL.get(),
                FoodItems.RAW_BIRD.get(),
                FoodItems.RAW_DUCK.get()
        );
        tag(Tags.Items.FERTILIZERS).add(ConsumableItems.FERTILIZER.get());
        tag(ItemTags.PARROT_POISONOUS_FOOD).add(
                FoodItems.CHOCOLATE_CHIP_COOKIE.get()
        );
        tag(ItemTags.PARROT_FOOD).add(
                FoodItems.FLOATING_WHEAT_SEED.get(),
                FoodItems.CLOUDWEAVER_SEED.get(),
                FoodItems.STELLAR_BLOSSOM_SEED.get(),
                FoodItems.WATERLEAF_SEED.get(),
                FoodItems.FIREBLOSSOM_SEED.get(),
                FoodItems.MOONGLOW_SEED.get(),
                FoodItems.BLINKROOT_SEED.get(),
                FoodItems.SHIVERTHORN_SEED.get(),
                FoodItems.DAYBLOOM_SEED.get(),
                FoodItems.DEATHWEED_SEED.get()
        );
        tag(ItemTags.VILLAGER_PLANTABLE_SEEDS).add(
                FoodItems.STELLAR_BLOSSOM_SEED.get(),
                FoodItems.CLOUDWEAVER_SEED.get(),
                FoodItems.FLOATING_WHEAT_SEED.get()
        );
        tag(ItemTags.LLAMA_TEMPT_ITEMS).add(DecorativeBlocks.FLOATING_WHEAT_BALE.asItem());
        tag(ItemTags.LLAMA_FOOD).add(MaterialItems.FLOATING_WHEAT_HEADS.get());
        tag(ItemTags.GOAT_FOOD).add(MaterialItems.FLOATING_WHEAT_HEADS.get());
        tag(Tags.Items.FOODS_GOLDEN).add(
                FoodItems.GOLDEN_CARP.get(),
                FoodItems.GOLDEN_DELIGHT.get()
        );
        tag(ItemTags.PIGLIN_LOVED).add(
                FoodItems.GOLDEN_CARP.get(),
                FoodItems.GOLDEN_DELIGHT.get()
        );
        tag(Tags.Items.FOODS_VEGETABLE).add(
                FoodItems.SPICY_PEPPER.get()
        );
        tag(ItemTags.FISHES).add(
                SwordItems.PURPLE_CLUBBERFISH.get(),
                ConsumableItems.BOMB_FISH.get(),
                PickaxeItems.REAVER_SHARK_PICKAXE.get(),
                QuestedFishes.AMANITA_FUNGIFIN.get(),
                QuestedFishes.ANGELFISH.get(),
                QuestedFishes.BATFISH.get(),
                QuestedFishes.BLOODY_MANOWAR.get(),
                QuestedFishes.BONEFISH.get(),
                QuestedFishes.BUMBLEBEE_TUNA.get(),
                QuestedFishes.BUNNYFISH.get(),
                QuestedFishes.CAPN_TUNABEARD.get(),
                QuestedFishes.CATFISH.get(),
                QuestedFishes.CLOUDFISH.get(),
                QuestedFishes.CLOWNFISH.get(),
                QuestedFishes.CURSEDFISH.get(),
                QuestedFishes.DEMONIC_HELLFISH.get(),
                QuestedFishes.DERPFISH.get(),
                QuestedFishes.DIRTFISH.get(),
                QuestedFishes.DYNAMITE_FISH.get(),
                QuestedFishes.EATER_OF_PLANKTON.get(),
                QuestedFishes.FALLEN_STARFISH.get(),
                QuestedFishes.THE_FISH_OF_CTHULHU.get(),
                QuestedFishes.FISHOTRON.get(),
                QuestedFishes.FISHRON.get(),
                QuestedFishes.GUIDE_VOODOO_FISH.get(),
                QuestedFishes.HARPYFISH.get(),
                QuestedFishes.HUNGERFISH.get(),
                QuestedFishes.ICHORFISH.get(),
                QuestedFishes.INFECTED_SCABBARDFISH.get(),
                QuestedFishes.JEWELFISH.get(),
                QuestedFishes.MIRAGE_FISH.get(),
                QuestedFishes.MUDFISH.get(),
                QuestedFishes.MUTANT_FLINXFIN.get(),
                QuestedFishes.PENGFISH.get(),
                QuestedFishes.PIXIEFISH.get(),
                QuestedFishes.SCARAB_FISH.get(),
                QuestedFishes.SCORPIO_FISH.get(),
                QuestedFishes.SLIMEFISH.get(),
                QuestedFishes.SPIDERFISH.get(),
                QuestedFishes.TROPICAL_BARRACUDA.get(),
                QuestedFishes.TUNDRA_TROUT.get(),
                QuestedFishes.UNICORN_FISH.get(),
                QuestedFishes.WYVERNTAIL.get(),
                QuestedFishes.ZOMBIE_FISH.get(),
                FoodItems.GOLDFISH.get(),
                FoodItems.SEA_BASS.get(),
                FoodItems.ATLANTIC_COD.get(),
                FoodItems.ARMORED_CAVE_FISH.get(),
                FoodItems.CHAOS_FISH.get(),
                FoodItems.SCARLET_TIGER_FISH.get(),
                FoodItems.DAMSEL_FISH.get(),
                FoodItems.PISCES_FIN_COD.get(),
                FoodItems.EBONY_KOI.get(),
                FoodItems.FLASHFIN_KOI.get(),
                FoodItems.PARTIAL_MOUTH_FISH.get(),
                FoodItems.FROSTY_MINNOW.get(),
                FoodItems.GOLDEN_CARP.get(),
                FoodItems.BLOODY_PIRANHAS.get(),
                FoodItems.NEON_GREASE_CARP.get(),
                FoodItems.OBSIDIFISH.get(),
                FoodItems.PRINCESS_FISH.get(),
                FoodItems.COLORFUL_MINERAL_FISH.get(),
                FoodItems.RED_SNAPPER.get(),
                FoodItems.TROUT.get(),
                FoodItems.ROCK_LOBSTER.get(),
                FoodItems.SALMON.get(),
                FoodItems.MIRROR_FISH.get(),
                FoodItems.STINKY_FISH.get(),
                FoodItems.TUNA.get(),
                FoodItems.MOTTLED_OILFISH.get(),
                FoodItems.YELLOW_EEL.get(),
                FoodItems.TILAPIA.get()
        );
        tag(Tags.Items.FOODS_EDIBLE_WHEN_PLACED).add(FoodItems.GREEN_DUMPLING.get());
        tag(ItemTags.CLUSTER_MAX_HARVESTABLES).addTag(ItemTags.PICKAXES);
        tag(ItemTags.COMPASSES).add(ToolItems.METEOR_COMPASS.get());
        tag(Tags.Items.FOODS_PIE).add(FoodItems.APPLE_PIE.get());
        tag(Tags.Items.FOODS_COOKIE).add(FoodItems.CHOCOLATE_CHIP_COOKIE.get());
        tag(ModTags.Items.EXPLOSIVE).add(
                Items.TNT,
                ConsumableItems.BOMB.get(),
                ConsumableItems.BOUNCY_BOMB.get(),
                ConsumableItems.STICKY_BOMB.get(),
                ConsumableItems.BOMB_FISH.get(),
                ConsumableItems.SCARAB_BOMB.get(),
                ConsumableItems.DYNAMITE.get(),
                ConsumableItems.BOUNCY_DYNAMITE.get(),
                ConsumableItems.STICKY_DYNAMITE.get(),
                ConsumableItems.GRENADE.get(),
                ConsumableItems.BOUNCY_GRENADE.get(),
                ConsumableItems.STICKY_GRENADE.get(),
                ConsumableItems.DIRT_BOMB.get(),
                ConsumableItems.STICKY_DIRT_BOMB.get(),
                ConsumableItems.DRY_BOMB.get(),
                ConsumableItems.WET_BOMB.get(),
                ConsumableItems.LAVA_BOMB.get(),
                ConsumableItems.HONEY_BOMB.get(),
                FunctionalBlocks.INSTANTANEOUS_EXPLOSION_TNT.asItem()
        );

        copy(Tags.Blocks.FENCE_GATES, Tags.Items.FENCE_GATES);
        copy(Tags.Blocks.STRIPPED_LOGS, Tags.Items.STRIPPED_LOGS);
        copy(BlockTags.STANDING_SIGNS, ItemTags.SIGNS);
        copy(BlockTags.CEILING_HANGING_SIGNS, ItemTags.HANGING_SIGNS);
        copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
        copy(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS);
        copy(BlockTags.PLANKS, ItemTags.PLANKS);
        copy(BlockTags.LOGS, ItemTags.LOGS);
        copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
        copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
        copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
        copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
        copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
        copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
        copy(BlockTags.LEAVES, ItemTags.LEAVES);
        copy(Tags.Blocks.GLASS_BLOCKS, Tags.Items.GLASS_BLOCKS);
        copy(BlockTags.RAILS, ItemTags.RAILS);
        copy(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
        copy(BlockTags.STONE_BRICKS, ItemTags.STONE_BRICKS);
        copy(BlockTags.ANVIL, ItemTags.ANVIL);
        copy(BlockTags.FENCE_GATES, ItemTags.FENCE_GATES);
        copy(BlockTags.DIRT, ItemTags.DIRT);
        copy(Tags.Blocks.ORE_RATES_SINGULAR, Tags.Items.ORE_RATES_SINGULAR);
        copy(Tags.Blocks.ORE_RATES_DENSE, Tags.Items.ORE_RATES_DENSE);
        copy(Tags.Blocks.SANDSTONE_BLOCKS, Tags.Items.SANDSTONE_BLOCKS);
        copy(Tags.Blocks.FENCE_GATES_WOODEN, Tags.Items.FENCE_GATES_WOODEN);
        copy(Tags.Blocks.PLAYER_WORKSTATIONS_FURNACES, Tags.Items.PLAYER_WORKSTATIONS_FURNACES);
        copy(Tags.Blocks.CHAINS, Tags.Items.CHAINS);
        copy(Tags.Blocks.ROPES, Tags.Items.ROPES);
        copy(Tags.Blocks.VILLAGER_JOB_SITES, Tags.Items.VILLAGER_JOB_SITES);
        copy(Tags.Blocks.CHESTS_TRAPPED, Tags.Items.CHESTS_TRAPPED);
        copy(Tags.Blocks.PLAYER_WORKSTATIONS_CRAFTING_TABLES, Tags.Items.PLAYER_WORKSTATIONS_CRAFTING_TABLES);
        copy(Tags.Blocks.GLASS_BLOCKS_COLORLESS, Tags.Items.GLASS_BLOCKS_COLORLESS);

        IntrinsicTagAppender<Item> wip = tag(LibTags.Items.WIP);
        wip.add(
                ConsumableItems.SMOKE_BOMB.get(),
                ConsumableItems.GOODIE_BAG.get(),
                PaintItems.ECHO_COATING.get(),
                ModItems.WHOOPIE_CUSHION.get(),
                ModItems.TOKYO_TEDDY_BEAR.get(),
                ModItems.FAILED_SKULL.get(),
                ModItems.KIND_MISIDE_RING.get(),
                ModItems.FERTILE_SINGULARITY.get(),
                ModItems.PERPLEXED_CAT_MEDAL.get(),
                ModItems.PULSAR.get(),
                HamaxeItems.HAEMORRHAXE.get(),
                HamaxeItems.SPECTRE_HAMAXE.get(),
                HamaxeItems.SOLAR_FLARE_HAMAXE.get(),
                HamaxeItems.VORTEX_HAMAXE.get(),
                HamaxeItems.NEBULA_HAMAXE.get(),
                HamaxeItems.STARDUST_HAMAXE.get(),
                HamaxeItems.THE_AXE.get(),
                HammerItems.HAMMUSH.get(),
                HammerItems.CHLOROPHYTE_WARHAMMER.get(),
                HammerItems.CHLOROPHYTE_JACKHAMMER.get(),
                PickaxeAxeItems.SHROOMITE_DIGGING_CLAW.get(),
                PickaxeItems.SPECTRE_PICKAXE.get(),
                PickaxeItems.SOLAR_FLARE_PICKAXE.get(),
                PickaxeItems.VORTEX_PICKAXE.get(),
                PickaxeItems.NEBULA_PICKAXE.get(),
                PickaxeItems.STARDUST_PICKAXE.get(),
                AccessoryItems.FROZEN_WINGS.get(),
                AccessoryItems.JETPACK.get(),
                AccessoryItems.LEAF_WINGS.get(),
                AccessoryItems.BAT_WINGS.get(),
                AccessoryItems.BUTTERFLY_WINGS.get(),
                AccessoryItems.FLAME_WINGS.get(),
                AccessoryItems.HOVERBOARD.get(),
                AccessoryItems.BONE_WINGS.get(),
                AccessoryItems.MOTHRON_WINGS.get(),
                AccessoryItems.SPECTRE_WINGS.get(),
                AccessoryItems.BEETLE_WINGS.get(),
                AccessoryItems.FESTIVE_WINGS.get(),
                AccessoryItems.SPOOKY_WINGS.get(),
                AccessoryItems.TATTERED_WINGS.get(),
                AccessoryItems.STEAMPUNK_WINGS.get(),
                AccessoryItems.BETSYS_WINGS.get(),
                AccessoryItems.EMPRESS_WINGS.get(),
                AccessoryItems.FISHRON_WINGS.get(),
                AccessoryItems.NEBULA_WINGS.get(),
                AccessoryItems.VORTEX_BOOSTER.get(),
                AccessoryItems.SOLAR_WINGS.get(),
                AccessoryItems.STARDUST_WINGS.get(),
                TCItems.EVERLASTING.get(),
                TCItems.BASE_POINT.get(),
                AxeItems.CHLOROPHYTE_GREATAXE.get(),
//                ConsumableItems.FALLEN_SOUL_CORE.get(),
                VanityArmorItems.MUMMY_MASK.get(),
                VanityArmorItems.MUMMY_SHIRT.get(),
                VanityArmorItems.MUMMY_PANTS.get(),
                VanityArmorItems.MUMMY_SHOES.get(),
                ArmorItems.GOGGLES.get(),
                ArmorItems.GREEN_CAP.get(),
                ArmorItems.WIZARD_HAT.get(),
                ArmorItems.MAGIC_HAT.get(),
                ArmorItems.MYSTIC_ROBE.get(),
                ArmorItems.HALLOWED_MASK.get(),
                ArmorItems.HALLOWED_HEADGEAR.get(),
                ArmorItems.HALLOWED_HOOD.get(),
                ArmorItems.HALLOWED_HELMET.get(),
                ArmorItems.HALLOWED_CHESTPLATE.get(),
                ArmorItems.HALLOWED_LEGGINGS.get(),
                ArmorItems.HALLOWED_BOOTS.get(),
                ArmorItems.SPIDER_HELMET.get(),
                ArmorItems.SPIDER_CHESTPLATE.get(),
                ArmorItems.SPIDER_LEGGINGS.get(),
                ArmorItems.SPIDER_BOOTS.get(),
                ArmorItems.CRYSTAL_ASSASSIN_HELMET.get(),
                ArmorItems.CRYSTAL_ASSASSIN_CHESTPLATE.get(),
                ArmorItems.CRYSTAL_ASSASSIN_LEGGINGS.get(),
                ArmorItems.CRYSTAL_ASSASSIN_BOOTS.get(),
                VanityArmorItems.TOP_HAT.get(),
                VanityArmorItems.TUXEDO_SHIRT.get(),
                VanityArmorItems.TUXEDO_PANTS.get(),
                VanityArmorItems.TUXEDO_SHOES.get(),
                VanityArmorItems.SUMMER_HAT.get(),
                VanityArmorItems.BUNNY_HOOD.get(),
                VanityArmorItems.PLUMBERS_HAT.get(),
                VanityArmorItems.PLUMBERS_SHIRT.get(),
                VanityArmorItems.PLUMBERS_PANTS.get(),
                VanityArmorItems.PLUMBERS_SHOES.get(),
                VanityArmorItems.HEROS_HAT.get(),
                VanityArmorItems.HEROS_SHIRT.get(),
                VanityArmorItems.HEROS_PANTS.get(),
                VanityArmorItems.HEROS_SHOES.get(),
                VanityArmorItems.ARCHAEOLOGISTS_HAT.get(),
                VanityArmorItems.ARCHAEOLOGISTS_JACKET.get(),
                VanityArmorItems.ARCHAEOLOGISTS_PANTS.get(),
                VanityArmorItems.ARCHAEOLOGISTS_SHOES.get(),
                VanityArmorItems.CLOTHIERS_HAT.get(),
                VanityArmorItems.CLOTHIERS_JACKET.get(),
                VanityArmorItems.CLOTHIERS_PANTS.get(),
                VanityArmorItems.CLOTHIERS_SHOES.get(),
                VanityArmorItems.ROBOT_HAT.get(),
                VanityArmorItems.MIME_MASK.get(),
                VanityArmorItems.THE_DOCTORS_SHIRT.get(),
                VanityArmorItems.THE_DOCTORS_PANTS.get(),
                VanityArmorItems.THE_DOCTORS_SHOES.get(),
                NatureBlocks.LOOSE_HONEY_BLOCK.asItem(),
                NatureBlocks.GREEN_MOSS.asItem(),
                NatureBlocks.BROWN_MOSS.asItem(),
                NatureBlocks.RED_MOSS.asItem(),
                NatureBlocks.BLUE_MOSS.asItem(),
                NatureBlocks.PURPLE_MOSS.asItem(),
                NatureBlocks.KRYPTON_MOSS.asItem(),
                NatureBlocks.ARGON_MOSS.asItem(),
                NatureBlocks.NEON_MOSS.asItem(),
                NatureBlocks.HELIUM_MOSS.asItem(),
                NatureBlocks.GLOWING_MUSHROOM_MOSS.asItem(),
                NatureBlocks.CRIMSON_VENUS_FLYTRAP_BLOCK.asItem(),
                NatureBlocks.BLOODTHIRST_CRYSTALLIZED_BLOCK.asItem(),
                NatureBlocks.CORRODED_WORM_ROOTS_BLOCK.asItem(),
                NatureBlocks.CORRUPTED_OVARIES_BLOCK.asItem(),
                NatureBlocks.SPOOKY_LOG_BLOCKS.STRIPPED_LOG.asItem(),
                NatureBlocks.SPOOKY_LOG_BLOCKS.WOOD.asItem(),
                NatureBlocks.SPOOKY_LOG_BLOCKS.STRIPPED_WOOD.asItem(),
                NatureBlocks.SPOOKY_LOG_BLOCKS.TRAPDOOR.asItem(),
                NatureBlocks.SPOOKY_LOG_BLOCKS.DOOR.asItem(),
                NatureBlocks.SPOOKY_LOG_BLOCKS.LOG.asItem(),
                NatureBlocks.SPOOKY_LOG_BLOCKS.LEAVES.asItem(),
                NatureBlocks.DECOMPOSE_THE_SOURCE_EXTRACT_BLOCK.asItem(),
                NatureBlocks.SMALL_DESERT_PLANT.asItem(),
                NatureBlocks.SMALL_CACTUS.asItem(),
                NatureBlocks.SHIMMER_CRYSTALS_BLOCK.asItem(),
                NatureBlocks.GLOWING_MUSHROOM_LOG_BLOCKS.TRAPDOOR.asItem(),
                NatureBlocks.GLOWING_MUSHROOM_LOG_BLOCKS.DOOR.asItem(),
                NatureBlocks.GLOWING_MUSHROOM_LOG_BLOCKS.CHISELED_PLANKS.asItem(),
                PotBlocks.OCEAN_POT.asItem(),
                OreBlocks.HALLOWED_BLOCK.asItem(),
                OreBlocks.RAW_CHLOROPHYTE_BLOCK.asItem(),
                OreBlocks.CHLOROPHYTE_BLOCK.asItem(),
                OreBlocks.SHROOMITE_BLOCK.asItem(),
                OreBlocks.SPECTRE_BLOCK.asItem(),
                OreBlocks.RAW_LUMINITE_BLOCK.asItem(),
                OreBlocks.LUMINITE_BLOCK.asItem(),
                OreBlocks.RAW_COBALT_BLOCK.asItem(),
                OreBlocks.COBALT_BLOCK.asItem(),
                OreBlocks.RAW_PALLADIUM_BLOCK.asItem(),
                OreBlocks.PALLADIUM_BLOCK.asItem(),
                OreBlocks.RAW_MYTHRIL_BLOCK.asItem(),
                OreBlocks.MYTHRIL_BLOCK.asItem(),
                OreBlocks.RAW_ORICHALCUM_BLOCK.asItem(),
                OreBlocks.ORICHALCUM_BLOCK.asItem(),
                OreBlocks.RAW_ADAMANTITE_BLOCK.asItem(),
                OreBlocks.ADAMANTITE_BLOCK.asItem(),
                OreBlocks.RAW_TITANIUM_BLOCK.asItem(),
                OreBlocks.TITANIUM_BLOCK.asItem(),
                DecorativeBlocks.OBSIDIAN_BRICKS_DOOR.asItem(),
                DecorativeBlocks.ENCHANTED_GREEN_BRICKS.asItem(),
                DecorativeBlocks.ENCHANTED_PINK_BRICKS.asItem(),
                DecorativeBlocks.LIHZAHRD_DOOR.asItem(),
                DecorativeBlocks.LIHZAHRD_BRICKS.asItem(),
                DecorativeBlocks.RAINBOW_BRICKS.asItem(),
                DecorativeBlocks.CLOUD_BLOCK_TRAMPOLINE.asItem(),
                DecorativeBlocks.FLESH_BLOCK.asItem(),
                DecorativeBlocks.LESION_BLOCK.asItem(),
                DecorativeBlocks.SANDSTONE_BRICKS.asItem(),
                DecorativeBlocks.SANDSTONE_BRICKS_STAIRS.asItem(),
                DecorativeBlocks.SANDSTONE_BRICKS_SLAB.asItem(),
                DecorativeBlocks.SANDSTONE_BRICKS_WALL.asItem(),
                DecorativeBlocks.RED_SANDSTONE_BRICKS.asItem(),
                DecorativeBlocks.RED_SANDSTONE_BRICKS_STAIRS.asItem(),
                DecorativeBlocks.RED_SANDSTONE_BRICKS_SLAB.asItem(),
                DecorativeBlocks.RED_SANDSTONE_BRICKS_WALL.asItem(),
                DecorativeBlocks.EBONSANDSTONE_BRICKS.asItem(),
                DecorativeBlocks.EBONSANDSTONE_BRICKS_STAIRS.asItem(),
                DecorativeBlocks.EBONSANDSTONE_BRICKS_SLAB.asItem(),
                DecorativeBlocks.EBONSANDSTONE_BRICKS_WALL.asItem(),
                DecorativeBlocks.PEARLSANDSTONE_BRICKS.asItem(),
                DecorativeBlocks.PEARLSANDSTONE_BRICKS_STAIRS.asItem(),
                DecorativeBlocks.PEARLSANDSTONE_BRICKS_SLAB.asItem(),
                DecorativeBlocks.PEARLSANDSTONE_BRICKS_WALL.asItem(),
                DecorativeBlocks.CRIMSANDSTONE_BRICKS.asItem(),
                DecorativeBlocks.CRIMSANDSTONE_BRICKS_STAIRS.asItem(),
                DecorativeBlocks.CRIMSANDSTONE_BRICKS_SLAB.asItem(),
                DecorativeBlocks.CRIMSANDSTONE_BRICKS_WALL.asItem(),
                DecorativeBlocks.SNOW_BRICKS_WALL.asItem(),
                DecorativeBlocks.CRIMTANE_ORE_BRICKS_STAIRS.asItem(),
                DecorativeBlocks.CRIMTANE_ORE_BRICKS_SLAB.asItem(),
                StatueBlocks.ARMOR_STATUE.asItem(),
                StatueBlocks.AXE_STATUE.asItem(),
                StatueBlocks.BOOMERANG_STATUE.asItem(),
                StatueBlocks.BOOT_STATUE.asItem(),
                StatueBlocks.BOW_STATUE.asItem(),
                StatueBlocks.GARGOYLE_STATUE.asItem(),
                StatueBlocks.GLOOM_STATUE.asItem(),
                StatueBlocks.HAMMER_STATUE.asItem(),
                StatueBlocks.PICKAXE_STATUE.asItem(),
                StatueBlocks.PILLAR_STATUE.asItem(),
                StatueBlocks.POT_STATUE.asItem(),
                StatueBlocks.POTION_STATUE.asItem(),
                StatueBlocks.REAPER_STATUE.asItem(),
                StatueBlocks.SHIELD_STATUE.asItem(),
                StatueBlocks.SPEAR_STATUE.asItem(),
                StatueBlocks.SUNFLOWER_STATUE.asItem(),
                StatueBlocks.SWORD_STATUE.asItem(),
                StatueBlocks.TREE_STATUE.asItem(),
                StatueBlocks.WOMEN_STATUE.asItem(),
                StatueBlocks.LIHZAHRD_STATUE.asItem(),
                StatueBlocks.LIHZAHRD_GUARDIAN_STATUE.asItem(),
                StatueBlocks.LIHZAHRD_WATCHER_STATUE.asItem(),
                StatueBlocks.ARMED_ZOMBIE_STATUE.asItem(),
                StatueBlocks.BONE_SKELETON_STATUE.asItem(),
                StatueBlocks.CORRUPT_STATUE.asItem(),
                StatueBlocks.DRIPPLER_STATUE.asItem(),
                StatueBlocks.EYEBALL_STATUE.asItem(),
                StatueBlocks.SKELETON_STATUE.asItem(),
                StatueBlocks.SLIME_STATUE.asItem(),
                StatueBlocks.BOMB_STATUE.asItem(),
                StatueBlocks.HEART_STATUE.asItem(),
                StatueBlocks.STAR_STATUE.asItem(),
                StatueBlocks.BOULDER_3X_STATUE.asItem(),
                StatueBlocks.BAST_STATUE.asItem(),
                TFBlocks.GLASS_KILN.asItem(),
                TFBlocks.LIVING_LOOM.asItem(),
                TFBlocks.ICE_MACHINE.asItem(),
                TFBlocks.FISH_BOWL.asItem(),
                TFBlocks.GOLD_FISH_BOWL.asItem(),
                TFBlocks.PUPFISH_BOWL.asItem(),
                TFBlocks.LAVA_SERPENT_BOWL.asItem(),
                TFBlocks.GLASS_BATHTUB.asItem(),
                TFBlocks.BLUE_DUNGEON_SOFA.asItem(),
                TFBlocks.BLUE_DUNGEON_LANTERN.asItem(),
                TFBlocks.BLUE_DUNGEON_LAMP.asItem(),
                TFBlocks.BLUE_DUNGEON_CLOCK.asItem(),
                TFBlocks.BLUE_DUNGEON_BATHTUB.asItem(),
                ChestBlocks.LIVING_WOOD_CHEST.asItem(),
                ChestBlocks.JUNGLE_CHEST.asItem(),
                ChestBlocks.CORRUPTION_CHEST.asItem(),
                ChestBlocks.CRIMSON_CHEST.asItem(),
                ChestBlocks.HALLOWED_CHEST.asItem(),
                ChestBlocks.ICE_CHEST.asItem(),
                ChestBlocks.DESERT_CHEST.asItem(),
                ChestBlocks.OCEAN_CHEST.asItem(),
                ChestBlocks.UNIVERSE_CHEST.asItem(),
                ChestBlocks.MECHANIC_SAFE_CHEST.asItem(),
                FunctionalBlocks.CHLOROPHYTE_EXTRACTINATOR.asItem(),
                FunctionalBlocks.BLEND_O_MATIC.asItem(),
                FunctionalBlocks.MEAT_GRINDER.asItem(),
                FunctionalBlocks.WOODEN_SPIKE.asItem(),
                FunctionalBlocks.ENCHANTED_FRAGILE_GREEN_BRICKS.asItem(),
                FunctionalBlocks.ENCHANTED_FRAGILE_PINK_BRICKS.asItem(),
                FunctionalBlocks.SILLY_BALLOON_MACHINE.asItem(),
                FunctionalBlocks.PLAYER_PRESSURE_PLATE.asItem(),
                FunctionalBlocks.LEVER.asItem(),
                FunctionalBlocks.GEYSER_BLOCK.asItem(),
                FunctionalBlocks.LAND_MINE.asItem(),
                FunctionalBlocks.SUPER_DART_TRAP.asItem(),
                FunctionalBlocks.FLAME_TRAP.asItem(),
                FunctionalBlocks.SPIKY_BALL_TRAP.asItem(),
                FunctionalBlocks.SPEAR_TRAP.asItem(),
                FunctionalBlocks.TREE_HOLES_BLOCK.asItem(),
                FunctionalBlocks.MAGIC_MAIL_BOX.asItem(),
                MaterialItems.ECTOPLASM.get(),
                MaterialItems.UNICORN_HORN.get(),
                MaterialItems.SPIDER_FANG.get(),
                HookItems.WEB_SLINGER.get(),
                HookItems.SLIME_HOOK.get(),
                HookItems.FISH_HOOK.get(),
                HookItems.IVY_WHIP.get(),
                HookItems.BAT_HOOK.get(),
                HookItems.CANDY_CANE_HOOK.get(),
                HookItems.DUAL_HOOK.get(),
                HookItems.HOOK_OF_DISSONANCE.get(),
                HookItems.THORN_HOOK.get(),
                HookItems.ILLUMINANT_HOOK.get(),
                HookItems.WORM_HOOK.get(),
                HookItems.TENDON_HOOK.get(),
                HookItems.SPOOKY_HOOK.get(),
                HookItems.CHRISTMAS_HOOK.get(),
                HookItems.ANTI_GRAVITY_HOOK.get(),
                HookItems.LUNAR_HOOK.get(),
                HookItems.STATIC_HOOK.get(),
                TEItems.DEBUG_ITEM.get(),
                DrillItems.CHLOROPHYTE_DRILL.get(),
                DrillItems.DRAX.get(),
                DrillItems.SOLAR_FLARE_DRILL.get(),
                DrillItems.VORTEX_DRILL.get(),
                DrillItems.NEBULA_DRILL.get(),
                DrillItems.STARDUST_DRILL.get(),
                TEFigureBlocks.FIGURE.asItem(),
                TEFigureBlocks.FIGURE2.asItem(),
                TEFigureBlocks.FIGURE3.asItem(),
                NatureBlocks.SPOOKY_LOG_BLOCKS.CHISELED_PLANKS.asItem(),
                NatureBlocks.FEY_LOG_BLOCKS.SIGN.asItem(),
                NatureBlocks.FEY_LOG_BLOCKS.HANGING_SIGN.asItem(),
                NatureBlocks.PINE_LOG_BLOCKS.SIGN.asItem(),
                NatureBlocks.PINE_LOG_BLOCKS.HANGING_SIGN.asItem(),
                DecorativeBlocks.THE_TWINS_RELIC.asItem(),
                DecorativeBlocks.SKELETRON_PRIME_RELIC.asItem(),
                LanceItems.HALLOWED_JOUSTING_LANCE.get(),
                LanceItems.SHADOW_JOUSTING_LANCE.get(),
                MaterialItems.MECHANICAL_WHEEL_PIECE.get(),
                MaterialItems.MECHANICAL_WAGON_PIECE.get(),
                MaterialItems.MECHANICAL_BATTERY_PIECE.get(),
                CrossbowItems.STAKE_LAUNCHER.get(),
                TESpawnEggItems.RETINAZER_SPAWN_EGG.get(),
                TESpawnEggItems.SPAZMATISM_SPAWN_EGG.get(),
                TESpawnEggItems.THE_DESTROYER_SPAWN_EGG.get(),
                TESpawnEggItems.THE_TWINS_SPAWN_EGG.get(),
                TESpawnEggItems.SKELETRON_PRIME_SPAWN_EGG.get(),
                TESpawnEggItems.PLANTERA_SPAWN_EGG.get(),
                TreasureBagItems.THE_TWINS_TREASURE_BAG.get(),
                TreasureBagItems.SKELETRON_PRIME_TREASURE_BAG.get()
        );
        Consumer<DeferredHolder<Item, ? extends Item>> wipAction = item -> wip.add(item.get());
        MinecartItems.ITEMS.getEntries().forEach(wipAction);
        LightPetItems.ITEMS.getEntries().forEach(wipAction);
        tag(TGTags.AUTOMATIC_GUN).add(
                ManaWeaponItems.BEE_GUN.get(),
                ManaWeaponItems.SPACE_GUN.get(),
                GunItems.STAR_CANNON.get()
        );
        tag(TGTags.AMMO).add(
                MaterialItems.FALLING_STAR.get()
        );
        IntrinsicTagAppender<Item> death = tag(ModTags.Items.DEATH);
        death.add(
                FunctionalBlocks.SHIMMER_TRAP.asItem(),
                FunctionalBlocks.GRAVITATION_TRAP.asItem(),
                FunctionalBlocks.PNEUMATIC_TRAP.asItem(),
                FunctionalBlocks.SPIKE.asItem(),
                FunctionalBlocks.WOODEN_SPIKE.asItem(),
                FunctionalBlocks.FRAGILE_SANDSTONE.asItem(),
                FunctionalBlocks.FRAGILE_BLUE_BRICKS.asItem(),
                FunctionalBlocks.FRAGILE_GREEN_BRICKS.asItem(),
                FunctionalBlocks.FRAGILE_PINK_BRICKS.asItem(),
                FunctionalBlocks.ENCHANTED_FRAGILE_BLUE_BRICKS.asItem(),
                FunctionalBlocks.ENCHANTED_FRAGILE_GREEN_BRICKS.asItem(),
                FunctionalBlocks.ENCHANTED_FRAGILE_PINK_BRICKS.asItem(),
                FunctionalBlocks.SCULK_TRAP.asItem(),
                FunctionalBlocks.INSTANTANEOUS_EXPLOSION_TNT.asItem(),
                FunctionalBlocks.DART_TRAP.asItem(),
                FunctionalBlocks.STONE_DART_TRAP.asItem(),
                FunctionalBlocks.DEEPSLATE_DART_TRAP.asItem(),
                FunctionalBlocks.GEYSER_BLOCK.asItem(),
                FunctionalBlocks.NORMAL_BOULDER.asItem(),
                FunctionalBlocks.OAK_LOG_BOULDER.asItem(),
                FunctionalBlocks.FOLLOWER_BOULDER.asItem(),
                FunctionalBlocks.EXPLODE_BOULDER.asItem(),
                FunctionalBlocks.ROLLING_CACTUS_BOULDER.asItem(),
                FunctionalBlocks.MECHANICAL_FRAGILE_SANDSTONE.asItem(),
                FunctionalBlocks.MECHANICAL_FRAGILE_OBSIDIAN_BRICKS.asItem(),
                FunctionalBlocks.LAND_MINE.asItem(),
                FunctionalBlocks.SUPER_DART_TRAP.asItem(),
                FunctionalBlocks.FLAME_TRAP.asItem(),
                FunctionalBlocks.SPIKY_BALL_TRAP.asItem(),
                FunctionalBlocks.SPEAR_TRAP.asItem(),
                HookItems.STATIC_HOOK.get()
        );
        for (DeferredBlock<DeathChestBlock> deathChest : ChestBlocks.DEATH_CHESTS) {
            death.add(deathChest.asItem());
        }

        tag(ModTags.Items.UNABLE_TO_APPLY_PREFIX).add(
                AccessoryItems.CLOTHIER_VOODOO_DOLL.get(),
                AccessoryItems.GUIDE_VOODOO_DOLL.get()
        );

        tag(ModTags.Items.ANTIGRAVITY).add(
                MaterialItems.SOUL_OF_LIGHT.get(),
                MaterialItems.SOUL_OF_NIGHT.get(),
                MaterialItems.SOUL_OF_FLIGHT.get(),
                MaterialItems.SOUL_OF_FRIGHT.get(),
                MaterialItems.SOUL_OF_MIGHT.get(),
                MaterialItems.SOUL_OF_SIGHT.get(),
                MaterialItems.SOUL_OF_BRIGHT.get(),
                MaterialItems.SOUL_OF_VOIGHT.get()
        );

        tag(ModTags.Items.SHOW_SIGNAL).add(
                ToolItems.RED_WRENCH.get(),
                ToolItems.GREEN_WRENCH.get(),
                ToolItems.BLUE_WRENCH.get(),
                ToolItems.YELLOW_WRENCH.get(),
                ToolItems.WIRE_CUTTER.get()
        );

        tag(ModTags.Items.ROBE).add(
                ArmorItems.AMETHYST_ROBE.get(),
                ArmorItems.TOPAZ_ROBE.get(),
                ArmorItems.SAPPHIRE_ROBE.get(),
                ArmorItems.JADE_ROBE.get(),
                ArmorItems.RUBY_ROBE.get(),
                ArmorItems.MYSTIC_ROBE.get(),
                ArmorItems.DIAMOND_ROBE.get(),
                ArmorItems.AMBER_ROBE.get()
        );

        tag(ModTags.Items.LAVA_PROOF_BAIT).add(
                BaitItems.HELL_BUTTERFLY.get(),
                BaitItems.MAGMA_SNAIL.get(),
                BaitItems.LAVAFLY.get()
        );

        IntrinsicTagAppender<Item> autoAttackBlacklist = tag(ModTags.Items.AUTO_ATTACK_BLACKLIST);
        for (DeferredHolder<Item, ? extends Item> entry : LanceItems.ITEMS.getEntries()) {
            autoAttackBlacklist.add(entry.get());
        }

        tag(ModTags.Items.AUTO_ATTACK_WHITELIST).add(
                SwordItems.ICE_BLADE.get(),
                SwordItems.MANDIBLE_BLADE.get(),
                SwordItems.PURPLE_CLUBBERFISH.get(),
                SwordItems.KATANA.get(),
                SwordItems.FALCON_BLADE.get(),
                SwordItems.BEE_KEEPER.get(),
                SwordItems.TERRAGRIM.get(),
                SwordItems.ENCHANTED_SWORD.get(),
                SwordItems.NIGHTS_EDGE.get(),
                SwordItems.DEVELOPER_SWORD.get(),
                SwordItems.COBALT_SWORD.get(),
                SwordItems.PALLADIUM_SWORD.get(),
                SwordItems.MYTHRIL_SWORD.get(),
                SwordItems.ORICHALCUM_SWORD.get(),
                SwordItems.ADAMANTITE_SWORD.get(),
                SwordItems.TITANIUM_SWORD.get(),
                SwordItems.MURAMASA.get()
        );
        tag(ModTags.Items.MIMIC_SUMMON_KEY).add(
                ToolItems.KEY_OF_LIGHT.get(),
                ToolItems.KEY_OF_NIGHT.get()
        );
        tag(ModTags.Items.REPEATER_CROSSBOW_ENCHANTABLE).addTag(
                ModTags.Items.REPEATER_ENCHANTABLE
        );
        tag(ModTags.Items.TOOLS_REPEATER_CROSSBOW).addTag(
                ModTags.Items.TOOLS_REPEATER
        );
        tag(Tags.Items.TOOLS_CROSSBOW).addTag(
                ModTags.Items.TOOLS_REPEATER_CROSSBOW
        );
        tag(ItemTags.CROSSBOW_ENCHANTABLE).addTag(
                ModTags.Items.REPEATER_CROSSBOW_ENCHANTABLE
        );
        IntrinsicTagAppender<Item> short_sword = tag(ModTags.Items.SHORT_SWORD);
        for (DeferredHolder<Item, ? extends Item> holder : SwordItems.ITEMS.getEntries()) {
            if (SwordItems.isShortSword(holder)) {
                short_sword.add(holder.get());
            }
        }

        WaystonesHelper.itemTag(this::tag);
    }
}
