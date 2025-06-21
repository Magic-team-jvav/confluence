package org.confluence.mod.common.data.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
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
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.*;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.integration.waystones.WaystonesHelper;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_furniture.common.init.TFBlocks;
import org.confluence.terra_guns.common.init.TGTags;
import org.confluence.terraentity.init.item.TEBoomerangItems;
import org.confluence.terraentity.init.item.TESummonItems;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, CompletableFuture<TagLookup<Block>> b, @Nullable ExistingFileHelper helper) {
        super(output, provider, b, Confluence.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        HookItems.acceptTag(tag(ModTags.Items.HOOK));
        PotionItems.acceptTag(tag(Tags.Items.POTIONS));
        FoodItems.acceptTag(tag(Tags.Items.FOODS));
        IntrinsicTagAppender<Item> minecart = tag(ModTags.Items.MINECART);
        minecart.add(Items.MINECART);
        MinecartItems.ITEMS.getEntries().forEach(item -> minecart.add(item.get()));
        tag(ModTags.Items.PROVIDE_MANA).add(ModItems.STAR.get(), ModItems.SOUL_CAKE.get(), ModItems.SUGAR_PLUM.get());
        tag(ModTags.Items.PROVIDE_LIFE).add(ModItems.HEART.get(), ModItems.CANDY_APPLE.get(), ModItems.CANDY_CANE.get());
        tag(ModTags.Items.DESERT_FOSSIL).add(NatureBlocks.DESERT_FOSSIL.asItem());
        tag(ModTags.Items.SLUSH).add(NatureBlocks.SLUSH.asItem());
        tag(ModTags.Items.SILT_BLOCK).add(NatureBlocks.SILT_BLOCK.asItem());
        tag(ModTags.Items.MARINE_GRAVEL).add(NatureBlocks.MARINE_GRAVEL.asItem());
        tag(ModTags.Items.JUNK).add(Blocks.LILY_PAD.asItem(), Items.LEATHER_BOOTS, Blocks.SEAGRASS.asItem());
        tag(ModTags.Items.CORALS).add(Blocks.TUBE_CORAL.asItem(), Blocks.TUBE_CORAL_FAN.asItem(), Blocks.TUBE_CORAL_BLOCK.asItem(), Blocks.BRAIN_CORAL.asItem(), Blocks.BRAIN_CORAL_FAN.asItem(), Blocks.BRAIN_CORAL_BLOCK.asItem(),
                Blocks.BUBBLE_CORAL.asItem(), Blocks.BUBBLE_CORAL_FAN.asItem(), Blocks.BUBBLE_CORAL_BLOCK.asItem(), Blocks.FIRE_CORAL.asItem(), Blocks.FIRE_CORAL_FAN.asItem(), Blocks.FIRE_CORAL_BLOCK.asItem(), Blocks.HORN_CORAL.asItem(), Blocks.HORN_CORAL_FAN.asItem(), Blocks.HORN_CORAL_BLOCK.asItem(),
                Blocks.DEAD_TUBE_CORAL.asItem(), Blocks.DEAD_TUBE_CORAL_FAN.asItem(), Blocks.DEAD_TUBE_CORAL_BLOCK.asItem(), Blocks.DEAD_BRAIN_CORAL.asItem(), Blocks.DEAD_BRAIN_CORAL_FAN.asItem(), Blocks.DEAD_BRAIN_CORAL_BLOCK.asItem(),
                Blocks.DEAD_BUBBLE_CORAL.asItem(), Blocks.DEAD_BUBBLE_CORAL_FAN.asItem(), Blocks.DEAD_BUBBLE_CORAL_BLOCK.asItem(), Blocks.DEAD_FIRE_CORAL.asItem(), Blocks.DEAD_FIRE_CORAL_FAN.asItem(), Blocks.DEAD_FIRE_CORAL_BLOCK.asItem(), Blocks.DEAD_HORN_CORAL.asItem(), Blocks.DEAD_HORN_CORAL_FAN.asItem(), Blocks.DEAD_HORN_CORAL_BLOCK.asItem());
        tag(ModTags.Items.EVIL_MATERIAL).add(
                MaterialItems.CRIMTANE_INGOT.get(),
                MaterialItems.CRIMTANE_INGOT.get(),
                MaterialItems.DEMONITE_INGOT.get(),
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
                FoodItems.OBSIDIAN_FISH.get(),
                FoodItems.NEON_GREASE_CARP.get(),
                FoodItems.MIRROR_FISH.get(),
                FoodItems.MOTTLED_OILFISH.get(),
                FoodItems.STINKY_FISH.get()
        );
        tag(ModTags.Items.INITIAL_WOOD).add(
                NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getPlanks().asItem(),
                NatureBlocks.LIVING_LOG_BLOCKS.getPlanks().asItem(),
                NatureBlocks.LIVING_MAHOGANY_BLOCKS.getPlanks().asItem(),
                NatureBlocks.BAOBAB_LOG_BLOCKS.getPlanks().asItem(),
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
                QuestedFishes.CAP_TUNABEARD.get(),
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
                BaitItems.GOLD_WORM.get()
        );
        tag(ModTags.Items.SHADOW_SCALE_AND_TISSUE_SAMPLE).add(
                MaterialItems.SHADOW_SCALE.get(),
                MaterialItems.TISSUE_SAMPLE.get()
        );

        // 可烧的木材
        tag(ModTags.Items.WOODEN_COMBUSTIBLES)
                .add(NatureBlocks.EBONY_LOG_BLOCKS.getAllItems().stream().map(Supplier::get).toArray(Item[]::new))
                .add(NatureBlocks.PEARL_LOG_BLOCKS.getAllItems().stream().map(Supplier::get).toArray(Item[]::new))
                .add(NatureBlocks.SHADOW_LOG_BLOCKS.getAllItems().stream().map(Supplier::get).toArray(Item[]::new))
                .add(NatureBlocks.PALM_LOG_BLOCKS.getAllItems().stream().map(Supplier::get).toArray(Item[]::new))
                .add(NatureBlocks.BAOBAB_LOG_BLOCKS.getAllItems().stream().map(Supplier::get).toArray(Item[]::new))
                .add(NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getAllItems().stream().map(Supplier::get).toArray(Item[]::new))
                .add(NatureBlocks.LIVING_LOG_BLOCKS.getAllItems().stream().map(Supplier::get).toArray(Item[]::new))
                .add(NatureBlocks.LIVING_MAHOGANY_BLOCKS.getAllItems().stream().map(Supplier::get).toArray(Item[]::new))
                .add(NatureBlocks.SPOOKY_LOG_BLOCKS.getAllItems().stream().map(Supplier::get).toArray(Item[]::new));
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
                MaterialItems.TOPAZ.get()
        );
        tag(ItemTags.SAPLINGS).add(
                NatureBlocks.SHADOW_SAPLING.asItem(),
                NatureBlocks.EBONY_SAPLING.asItem(),
                NatureBlocks.PALM_SAPLING.asItem(),
                NatureBlocks.PEARL_SAPLING.asItem(),
                NatureBlocks.RUBY_SAPLING.asItem(),
                NatureBlocks.AMBER_SAPLING.asItem(),
                NatureBlocks.TOPAZ_SAPLING.asItem(),
                NatureBlocks.JADE_SAPLING.asItem(),
                NatureBlocks.DIAMOND_SAPLING.asItem(),
                NatureBlocks.SAPPHIRE_SAPLING.asItem(),
                NatureBlocks.AMETHYST_SAPLING.asItem(),
                NatureBlocks.ASH_SAPLING.asItem(),
                NatureBlocks.LIVING_SAPLING.asItem(),
                NatureBlocks.YELLOW_WILLOW_SAPLING.asItem()
        );

        tag(ModTags.Items.EVIL_INGOT).add(MaterialItems.DEMONITE_INGOT.get(), MaterialItems.CRIMTANE_INGOT.get());
        tag(ModTags.Items.LEAD_AND_IRON).addTags(Tags.Items.INGOTS_IRON, ModTags.Items.INGOTS_LEAD);
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
                Items.APPLE, Items.MELON_SLICE, FoodItems.APRICOT.get(),
                FoodItems.BANANA.get(), FoodItems.CHERRY.get(), FoodItems.COCONUT.get(),
                FoodItems.DRAGON_FRUIT.get(), FoodItems.GRAPE_FRUIT.get(), FoodItems.LEMON.get(),
                FoodItems.MANGO.get(), FoodItems.PEACH.get(), FoodItems.PINEAPPLE.get(),
                FoodItems.PLUM.get(), FoodItems.GRAPE.get(), FoodItems.SPICY_PEPPER.get(),
                FoodItems.STAR_FRUIT.get(), FoodItems.POMEGRANATE.get(), FoodItems.RAMBUTAN.get(),
                FoodItems.BLOOD_ORANGE.get(), FoodItems.ELDERBERRY.get(), FoodItems.BLACKCURRANT.get()
        );
        tag(ModTags.Items.COAL_ORE_SMELTING).add(
                OreBlocks.SANCTIFICATION_COAL_ORE.asItem(), OreBlocks.CORRUPTION_COAL_ORE.asItem(), OreBlocks.FLESHIFICATION_COAL_ORE.asItem()
        );
        tag(ModTags.Items.IRON_ORE_SMELTING).add(
                OreBlocks.SANCTIFICATION_IRON_ORE.asItem(), OreBlocks.CORRUPTION_IRON_ORE.asItem(), OreBlocks.FLESHIFICATION_IRON_ORE.asItem()
        );
        tag(ModTags.Items.TIN_ORE_SMELTING).add(
                OreBlocks.TIN_ORE.asItem(), OreBlocks.DEEPSLATE_TIN_ORE.asItem(), OreBlocks.SANCTIFICATION_TIN_ORE.asItem(), OreBlocks.CORRUPTION_TIN_ORE.asItem(), OreBlocks.FLESHIFICATION_TIN_ORE.asItem(),
                MaterialItems.RAW_TIN.get()
        );
        tag(ModTags.Items.COPPER_ORE_SMELTING).add(
                OreBlocks.SANCTIFICATION_COPPER_ORE.asItem(), OreBlocks.CORRUPTION_COPPER_ORE.asItem(), OreBlocks.FLESHIFICATION_COPPER_ORE.asItem()
        );
        tag(ModTags.Items.LEAD_ORE_SMELTING).add(
                OreBlocks.LEAD_ORE.asItem(), OreBlocks.DEEPSLATE_LEAD_ORE.asItem(), OreBlocks.SANCTIFICATION_LEAD_ORE.asItem(), OreBlocks.CORRUPTION_LEAD_ORE.asItem(), OreBlocks.FLESHIFICATION_LEAD_ORE.asItem(),
                MaterialItems.RAW_LEAD.get()
        );
        tag(ModTags.Items.SILVER_ORE_SMELTING).add(
                OreBlocks.SILVER_ORE.asItem(), OreBlocks.DEEPSLATE_SILVER_ORE.asItem(), OreBlocks.SANCTIFICATION_SILVER_ORE.asItem(), OreBlocks.CORRUPTION_SILVER_ORE.asItem(), OreBlocks.FLESHIFICATION_SILVER_ORE.asItem(),
                MaterialItems.RAW_SILVER.get()
        );
        tag(ModTags.Items.TUNGSTEN_ORE_SMELTING).add(
                OreBlocks.TUNGSTEN_ORE.asItem(), OreBlocks.DEEPSLATE_TUNGSTEN_ORE.asItem(), OreBlocks.SANCTIFICATION_TUNGSTEN_ORE.asItem(), OreBlocks.CORRUPTION_TUNGSTEN_ORE.asItem(), OreBlocks.FLESHIFICATION_TUNGSTEN_ORE.asItem(),
                MaterialItems.RAW_TUNGSTEN.get()
        );
        tag(ModTags.Items.GOLD_ORE_SMELTING).add(
                OreBlocks.SANCTIFICATION_GOLD_ORE.asItem(), OreBlocks.CORRUPTION_GOLD_ORE.asItem(), OreBlocks.FLESHIFICATION_GOLD_ORE.asItem()
        );
        tag(ModTags.Items.PLATINUM_ORE_SMELTING).add(
                OreBlocks.PLATINUM_ORE.asItem(), OreBlocks.DEEPSLATE_PLATINUM_ORE.asItem(), OreBlocks.SANCTIFICATION_PLATINUM_ORE.asItem(), OreBlocks.CORRUPTION_PLATINUM_ORE.asItem(), OreBlocks.FLESHIFICATION_PLATINUM_ORE.asItem(),
                MaterialItems.RAW_PLATINUM.get()
        );
        tag(ModTags.Items.DEMONITE_ORE_SMELTING).add(
                OreBlocks.DEMONITE_ORE.asItem(), OreBlocks.DEEPSLATE_DEMONITE_ORE.asItem(), OreBlocks.SANCTIFICATION_DEMONITE_ORE.asItem(), OreBlocks.CORRUPTION_DEMONITE_ORE.asItem(), OreBlocks.FLESHIFICATION_DEMONITE_ORE.asItem(),
                MaterialItems.RAW_DEMONITE.get()
        );
        tag(ModTags.Items.CRIMTANE_ORE_SMELTING).add(
                OreBlocks.CRIMTANE_ORE.asItem(), OreBlocks.DEEPSLATE_CRIMTANE_ORE.asItem(), OreBlocks.SANCTIFICATION_CRIMTANE_ORE.asItem(), OreBlocks.CORRUPTION_CRIMTANE_ORE.asItem(), OreBlocks.FLESHIFICATION_CRIMTANE_ORE.asItem(),
                MaterialItems.RAW_CRIMTANE.get()

        );
        tag(ModTags.Items.RUBY_ORE_SMELTING).add(
                OreBlocks.RUBY_ORE.asItem(), OreBlocks.DEEPSLATE_RUBY_ORE.asItem(), OreBlocks.SANCTIFICATION_RUBY_ORE.asItem(), OreBlocks.CORRUPTION_RUBY_ORE.asItem(), OreBlocks.FLESHIFICATION_RUBY_ORE.asItem()
        );
        tag(ModTags.Items.AMBER_ORE_SMELTING).add(
                OreBlocks.AMBER_ORE.asItem(), OreBlocks.RED_SAND_AMBER_ORE.asItem(), OreBlocks.SANCTIFICATION_AMBER_ORE.asItem(), OreBlocks.CORRUPTION_AMBER_ORE.asItem(), OreBlocks.FLESHIFICATION_AMBER_ORE.asItem()
        );
        tag(ModTags.Items.TOPAZ_ORE_SMELTING).add(
                OreBlocks.TOPAZ_ORE.asItem(), OreBlocks.DEEPSLATE_TOPAZ_ORE.asItem(), OreBlocks.SANCTIFICATION_TOPAZ_ORE.asItem(), OreBlocks.CORRUPTION_TOPAZ_ORE.asItem(), OreBlocks.FLESHIFICATION_TOPAZ_ORE.asItem()
        );
        tag(ModTags.Items.JADE_ORE_SMELTING).add(
                OreBlocks.JADE_ORE.asItem(), OreBlocks.DEEPSLATE_RUBY_ORE.asItem(), OreBlocks.SANCTIFICATION_RUBY_ORE.asItem(), OreBlocks.CORRUPTION_RUBY_ORE.asItem(), OreBlocks.FLESHIFICATION_RUBY_ORE.asItem()
        );
        tag(ModTags.Items.DIAMOND_ORE_SMELTING).add(
                OreBlocks.SANCTIFICATION_DIAMOND_ORE.asItem(), OreBlocks.CORRUPTION_DIAMOND_ORE.asItem(), OreBlocks.FLESHIFICATION_DIAMOND_ORE.asItem()
        );
        tag(ModTags.Items.SAPPHIRE_ORE_SMELTING).add(
                OreBlocks.SAPPHIRE_ORE.asItem(), OreBlocks.DEEPSLATE_SAPPHIRE_ORE.asItem(), OreBlocks.SANCTIFICATION_SAPPHIRE_ORE.asItem(), OreBlocks.CORRUPTION_SAPPHIRE_ORE.asItem(), OreBlocks.FLESHIFICATION_SAPPHIRE_ORE.asItem()
        );
        tag(ModTags.Items.AMETHYST_ORE_SMELTING).add(
                OreBlocks.AMETHYST_ORE.asItem(), OreBlocks.DEEPSLATE_AMETHYST_ORE.asItem(), OreBlocks.SANCTIFICATION_AMETHYST_ORE.asItem(), OreBlocks.CORRUPTION_AMETHYST_ORE.asItem(), OreBlocks.FLESHIFICATION_AMETHYST_ORE.asItem()
        );
        tag(ModTags.Items.REDSTONE_ORE_SMELTING).add(
                OreBlocks.SANCTIFICATION_REDSTONE_ORE.asItem(), OreBlocks.CORRUPTION_REDSTONE_ORE.asItem(), OreBlocks.FLESHIFICATION_REDSTONE_ORE.asItem()
        );
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
        tag(Tags.Items.GEMS).add(
                MaterialItems.RUBY.get(),
                MaterialItems.AMBER.get(),
                MaterialItems.TOPAZ.get(),
                MaterialItems.JADE.get(),
                MaterialItems.SAPPHIRE.get(),
                MaterialItems.AMETHYST.get()
        );
        tag(ModTags.Items.GEMS_RUBY).add(
                MaterialItems.RUBY.get()
        );
        tag(ModTags.Items.GEMS_AMBER).add(
                MaterialItems.AMBER.get()
        );
        tag(ModTags.Items.GEMS_TOPAZ).add(
                MaterialItems.TOPAZ.get()
        );
        tag(ModTags.Items.GEMS_JADE).add(
                MaterialItems.JADE.get()
        );
        tag(ModTags.Items.GEMS_SAPPHIRE).add(
                MaterialItems.SAPPHIRE.get()
        );
        tag(ModTags.Items.GEMS_AMETHYST).add(
                MaterialItems.AMETHYST.get()
        );

        tag(ModTags.Items.NUGGETS_TIN).add(
                MaterialItems.LEAD_NUGGET.get()
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
                MaterialItems.LEAD_NUGGET.get()
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
        tag(Tags.Items.RAW_MATERIALS).add(
                MaterialItems.RAW_ADAMANTITE.get(),
                MaterialItems.RAW_CHLOROPHYTE.get(),
                MaterialItems.RAW_COBALT.get(),
                MaterialItems.RAW_LEAD.get(),
                MaterialItems.RAW_HELLSTONE.get(),
                MaterialItems.RAW_LUMINITE.get(),
                MaterialItems.RAW_METEORITE.get(),
                MaterialItems.RAW_MYTHRIL.get(),
                MaterialItems.RAW_ORICHALCUM.get(),
                MaterialItems.RAW_DEMONITE.get(),
                MaterialItems.RAW_PALLADIUM.get(),
                MaterialItems.RAW_PLATINUM.get(),
                MaterialItems.RAW_SILVER.get(),
                MaterialItems.RAW_TIN.get(),
                MaterialItems.RAW_TUNGSTEN.get(),
                MaterialItems.RAW_CRIMTANE.get()
        );

        PaintItems.acceptTag(tag(Tags.Items.DYED));
        ArrowItems.acceptTag(tag(ItemTags.ARROWS));
        IntrinsicTagAppender<Item> hammer = tag(ModTags.Items.HAMMERS);
        HamaxeItems.acceptTag(hammer);
        HammerItems.acceptTag(hammer);
        IntrinsicTagAppender<Item> pickaxes = tag(ItemTags.PICKAXES);
        PickaxeItems.acceptTag(pickaxes);
        PickaxeAxeItems.acceptTag(pickaxes);
        IntrinsicTagAppender<Item> axes = tag(ItemTags.AXES);
        HamaxeItems.acceptTag(axes);
        AxeItems.acceptTag(axes);
        PickaxeAxeItems.acceptTag(axes);
        IntrinsicTagAppender<Item> hoes = tag(ItemTags.HOES);
        HoeShovelItems.acceptTag(hoes);
        IntrinsicTagAppender<Item> shovels = tag(ItemTags.SHOVELS);
        HoeShovelItems.acceptTag(shovels);

        IntrinsicTagAppender<Item> tools = tag(Tags.Items.TOOLS);
        PickaxeAxeItems.acceptTag(tools);
        AxeItems.acceptTag(tools);
        PickaxeItems.acceptTag(tools);
        HamaxeItems.acceptTag(tools);
        HoeShovelItems.acceptTag(hammer);
        HammerItems.acceptTag(tools);
        FishingPoleItems.acceptTag(tools);

        ManaWeaponItems.acceptTag(tag(ModTags.Items.MANA_WEAPON));
        IntrinsicTagAppender<Item> weapons = tag(ModTags.Items.WEAPONS);
        ManaWeaponItems.acceptTag(weapons);
        GunItems.acceptTag(weapons);
        GunItems.acceptTag(tag(TGTags.GUN));
        IntrinsicTagAppender<Item> mining_tool_tools = tag(Tags.Items.MINING_TOOL_TOOLS);
        PickaxeItems.acceptTag(mining_tool_tools);
        PickaxeAxeItems.acceptTag(mining_tool_tools);
        AxeItems.acceptTag(mining_tool_tools);
        HamaxeItems.acceptTag(mining_tool_tools);
        HoeShovelItems.acceptTag(mining_tool_tools);
        HammerItems.acceptTag(mining_tool_tools);
        DrillItems.acceptTag(mining_tool_tools);
        IntrinsicTagAppender<Item> prefix_universal_only = tag(ModTags.Items.PREFIX_UNIVERSAL_ONLY);
        DrillItems.acceptTag(prefix_universal_only);
        TEBoomerangItems.acceptTag(prefix_universal_only);

        copy(ModTags.Blocks.COINS, ModTags.Items.COINS);
        tag(ModTags.Items.HARDMODE_RAW_MATERIALS).add(
                MaterialItems.RAW_COBALT.get(),
                MaterialItems.RAW_PALLADIUM.get(),
                MaterialItems.RAW_MYTHRIL.get(),
                MaterialItems.RAW_ORICHALCUM.get(),
                MaterialItems.RAW_ADAMANTITE.get(),
                MaterialItems.RAW_TITANIUM.get()
        );
        tag(ModTags.Items.BOSS_SUMMONING).add(
                ConsumableItems.SUSPICIOUS_LOOKING_EYE.get(),
                ConsumableItems.SLIME_CROWN.get(),
                ConsumableItems.WORM_FOOD.get(),
                ConsumableItems.BLOODY_SPINE.get(),
                ConsumableItems.ABEEMINATION.get()
        );

        tag(ModTags.Items.INGOTS_TIN).add(MaterialItems.TIN_INGOT.get());
        tag(ModTags.Items.TIN_BLOCK).add(OreBlocks.TIN_BLOCK.asItem());
        tag(ModTags.Items.INGOTS_LEAD).add(MaterialItems.LEAD_INGOT.get());
        tag(ModTags.Items.LEAD_BLOCK).add(OreBlocks.LEAD_BLOCK.asItem());
        tag(ModTags.Items.INGOTS_SILVER).add(MaterialItems.SILVER_INGOT.get());
        tag(ModTags.Items.SILVER_BLOCK).add(OreBlocks.SILVER_BLOCK.asItem());
        tag(ModTags.Items.INGOTS_TUNGSTEN).add(MaterialItems.TUNGSTEN_INGOT.get());
        tag(ModTags.Items.TUNGSTEN_BLOCK).add(OreBlocks.TUNGSTEN_BLOCK.asItem());
        tag(ModTags.Items.INGOTS_PLATINUM).add(MaterialItems.PLATINUM_INGOT.get());
        tag(ModTags.Items.PLATINUM_BLOCK).add(OreBlocks.PLATINUM_BLOCK.asItem());
        tag(ModTags.Items.INGOTS_METEORITE).add(MaterialItems.METEORITE_INGOT.get());
        tag(ModTags.Items.METEORITE_BLOCK).add(OreBlocks.METEORITE_BLOCK.asItem());
        tag(ModTags.Items.INGOTS_DEMONITE).add(MaterialItems.DEMONITE_INGOT.get());
        tag(ModTags.Items.DEMONITE_BLOCK).add(OreBlocks.DEMONITE_BLOCK.asItem());
        tag(ModTags.Items.INGOTS_CRIMTANE).add(MaterialItems.CRIMTANE_INGOT.get());
        tag(ModTags.Items.CRIMTANE_BLOCK).add(OreBlocks.CRIMTANE_BLOCK.asItem());
        tag(ModTags.Items.INGOTS_HELLSTONE).add(MaterialItems.HELLSTONE_INGOT.get());
        tag(ModTags.Items.HELLSTONE_BLOCK).add(OreBlocks.HELLSTONE_BLOCK.asItem());

        tag(ModTags.Items.RAW_MATERIALS_TIN).add(MaterialItems.RAW_TIN.get());
        tag(ModTags.Items.RAW_MATERIALS_TIN_BLOCK).add(OreBlocks.RAW_TIN_BLOCK.asItem());
        tag(ModTags.Items.RAW_MATERIALS_LEAD).add(MaterialItems.RAW_LEAD.get());
        tag(ModTags.Items.RAW_MATERIALS_LEAD_BLOCK).add(OreBlocks.RAW_LEAD_BLOCK.asItem());
        tag(ModTags.Items.RAW_MATERIALS_SILVER).add(MaterialItems.RAW_SILVER.get());
        tag(ModTags.Items.RAW_MATERIALS_SILVER_BLOCK).add(OreBlocks.RAW_SILVER_BLOCK.asItem());
        tag(ModTags.Items.RAW_MATERIALS_TUNGSTEN).add(MaterialItems.RAW_TUNGSTEN.get());
        tag(ModTags.Items.RAW_MATERIALS_TUNGSTEN_BLOCK).add(OreBlocks.RAW_TUNGSTEN_BLOCK.asItem());
        tag(ModTags.Items.RAW_MATERIALS_PLATINUM).add(MaterialItems.RAW_PLATINUM.get());
        tag(ModTags.Items.RAW_MATERIALS_PLATINUM_BLOCK).add(OreBlocks.RAW_PLATINUM_BLOCK.asItem());
        tag(ModTags.Items.RAW_MATERIALS_METEORITE).add(MaterialItems.RAW_METEORITE.get());
        tag(ModTags.Items.RAW_MATERIALS_METEORITE_BLOCK).add(OreBlocks.RAW_METEORITE_BLOCK.asItem());
        tag(ModTags.Items.RAW_MATERIALS_DEMONITE).add(MaterialItems.RAW_DEMONITE.get());
        tag(ModTags.Items.RAW_MATERIALS_DEMONITE_BLOCK).add(OreBlocks.RAW_DEMONITE_BLOCK.asItem());
        tag(ModTags.Items.RAW_MATERIALS_CRIMTANE).add(MaterialItems.RAW_CRIMTANE.get());
        tag(ModTags.Items.RAW_MATERIALS_CRIMTANE_BLOCK).add(OreBlocks.RAW_CRIMTANE_BLOCK.asItem());
        tag(ModTags.Items.RAW_MATERIALS_HELLSTONE).add(MaterialItems.RAW_HELLSTONE.get());
        tag(ModTags.Items.RAW_MATERIALS_HELLSTONE_BLOCK).add(OreBlocks.RAW_HELLSTONE_BLOCK.asItem());

        tag(Tags.Items.ORES_COAL).add(
                OreBlocks.SANCTIFICATION_COAL_ORE.asItem(),
                OreBlocks.CORRUPTION_COAL_ORE.asItem(),
                OreBlocks.FLESHIFICATION_COAL_ORE.asItem()
        );
        tag(ItemTags.COAL_ORES).add(
                OreBlocks.SANCTIFICATION_COAL_ORE.asItem(),
                OreBlocks.CORRUPTION_COAL_ORE.asItem(),
                OreBlocks.FLESHIFICATION_COAL_ORE.asItem()
        );
        tag(Tags.Items.ORES_COPPER).add(
                OreBlocks.SANCTIFICATION_COPPER_ORE.asItem(),
                OreBlocks.CORRUPTION_COPPER_ORE.asItem(),
                OreBlocks.FLESHIFICATION_COPPER_ORE.asItem()
        );

        tag(ItemTags.COPPER_ORES).add(
                OreBlocks.SANCTIFICATION_COPPER_ORE.asItem(),
                OreBlocks.CORRUPTION_COPPER_ORE.asItem(),
                OreBlocks.FLESHIFICATION_COPPER_ORE.asItem()
        );
        tag(Tags.Items.ORES_DIAMOND).add(
                OreBlocks.SANCTIFICATION_DIAMOND_ORE.asItem(),
                OreBlocks.CORRUPTION_DIAMOND_ORE.asItem(),
                OreBlocks.FLESHIFICATION_DIAMOND_ORE.asItem()
        );

        tag(ItemTags.DIAMOND_ORES).add(
                OreBlocks.SANCTIFICATION_DIAMOND_ORE.asItem(),
                OreBlocks.CORRUPTION_DIAMOND_ORE.asItem(),
                OreBlocks.FLESHIFICATION_DIAMOND_ORE.asItem()
        );
        tag(Tags.Items.ORES_EMERALD).add(
                OreBlocks.SANCTIFICATION_EMERALD_ORE.asItem(),
                OreBlocks.CORRUPTION_EMERALD_ORE.asItem(),
                OreBlocks.FLESHIFICATION_EMERALD_ORE.asItem()
        );

        tag(ItemTags.EMERALD_ORES).add(
                OreBlocks.SANCTIFICATION_EMERALD_ORE.asItem(),
                OreBlocks.CORRUPTION_EMERALD_ORE.asItem(),
                OreBlocks.FLESHIFICATION_EMERALD_ORE.asItem()
        );
        tag(Tags.Items.ORES_GOLD).add(
                OreBlocks.SANCTIFICATION_GOLD_ORE.asItem(),
                OreBlocks.CORRUPTION_GOLD_ORE.asItem(),
                OreBlocks.FLESHIFICATION_GOLD_ORE.asItem()
        );

        tag(ItemTags.GOLD_ORES).add(
                OreBlocks.SANCTIFICATION_GOLD_ORE.asItem(),
                OreBlocks.CORRUPTION_GOLD_ORE.asItem(),
                OreBlocks.FLESHIFICATION_GOLD_ORE.asItem()
        );
        tag(Tags.Items.ORES_IRON).add(
                OreBlocks.SANCTIFICATION_IRON_ORE.asItem(),
                OreBlocks.CORRUPTION_IRON_ORE.asItem(),
                OreBlocks.FLESHIFICATION_IRON_ORE.asItem()
        );

        tag(ItemTags.IRON_ORES).add(
                OreBlocks.SANCTIFICATION_IRON_ORE.asItem(),
                OreBlocks.CORRUPTION_IRON_ORE.asItem(),
                OreBlocks.FLESHIFICATION_IRON_ORE.asItem()
        );
        tag(Tags.Items.ORES_LAPIS).add(
                OreBlocks.SANCTIFICATION_LAPIS_ORE.asItem(),
                OreBlocks.CORRUPTION_LAPIS_ORE.asItem(),
                OreBlocks.FLESHIFICATION_LAPIS_ORE.asItem()
        );

        tag(ItemTags.LAPIS_ORES).add(
                OreBlocks.SANCTIFICATION_LAPIS_ORE.asItem(),
                OreBlocks.CORRUPTION_LAPIS_ORE.asItem(),
                OreBlocks.FLESHIFICATION_LAPIS_ORE.asItem()
        );
        tag(Tags.Items.ORES_REDSTONE).add(
                OreBlocks.SANCTIFICATION_REDSTONE_ORE.asItem(),
                OreBlocks.CORRUPTION_REDSTONE_ORE.asItem(),
                OreBlocks.FLESHIFICATION_REDSTONE_ORE.asItem()
        );

        tag(ItemTags.REDSTONE_ORES).add(
                OreBlocks.SANCTIFICATION_REDSTONE_ORE.asItem(),
                OreBlocks.CORRUPTION_REDSTONE_ORE.asItem(),
                OreBlocks.FLESHIFICATION_REDSTONE_ORE.asItem()
        );

        tag(ModTags.Items.ORES_TIN).add(
                OreBlocks.TIN_ORE.asItem(),
                OreBlocks.DEEPSLATE_TIN_ORE.asItem(),
                OreBlocks.SANCTIFICATION_TIN_ORE.asItem(),
                OreBlocks.CORRUPTION_TIN_ORE.asItem(),
                OreBlocks.FLESHIFICATION_TIN_ORE.asItem()
        );

        tag(ModTags.Items.ORES_LEAD).add(
                OreBlocks.LEAD_ORE.asItem(),
                OreBlocks.DEEPSLATE_LEAD_ORE.asItem(),
                OreBlocks.SANCTIFICATION_LEAD_ORE.asItem(),
                OreBlocks.CORRUPTION_LEAD_ORE.asItem(),
                OreBlocks.FLESHIFICATION_LEAD_ORE.asItem()
        );

        tag(ModTags.Items.ORES_SILVER).add(
                OreBlocks.SILVER_ORE.asItem(),
                OreBlocks.DEEPSLATE_SILVER_ORE.asItem(),
                OreBlocks.SANCTIFICATION_SILVER_ORE.asItem(),
                OreBlocks.CORRUPTION_SILVER_ORE.asItem(),
                OreBlocks.FLESHIFICATION_SILVER_ORE.asItem()
        );

        tag(ModTags.Items.ORES_TUNGSTEN).add(
                OreBlocks.TUNGSTEN_ORE.asItem(),
                OreBlocks.DEEPSLATE_TUNGSTEN_ORE.asItem(),
                OreBlocks.SANCTIFICATION_TUNGSTEN_ORE.asItem(),
                OreBlocks.CORRUPTION_TUNGSTEN_ORE.asItem(),
                OreBlocks.FLESHIFICATION_TUNGSTEN_ORE.asItem()
        );

        tag(ModTags.Items.ORES_PLATINUM).add(
                OreBlocks.PLATINUM_ORE.asItem(),
                OreBlocks.DEEPSLATE_PLATINUM_ORE.asItem(),
                OreBlocks.SANCTIFICATION_PLATINUM_ORE.asItem(),
                OreBlocks.CORRUPTION_PLATINUM_ORE.asItem(),
                OreBlocks.FLESHIFICATION_PLATINUM_ORE.asItem()
        );

        tag(Tags.Items.ORES_IN_GROUND_DEEPSLATE).add(
                OreBlocks.DEEPSLATE_TIN_ORE.asItem(),
                OreBlocks.DEEPSLATE_LEAD_ORE.asItem(),
                OreBlocks.DEEPSLATE_SILVER_ORE.asItem(),
                OreBlocks.DEEPSLATE_TUNGSTEN_ORE.asItem(),
                OreBlocks.DEEPSLATE_PLATINUM_ORE.asItem(),
                OreBlocks.DEEPSLATE_RUBY_ORE.asItem(),
                OreBlocks.DEEPSLATE_TOPAZ_ORE.asItem(),
                OreBlocks.DEEPSLATE_JADE_ORE.asItem(),
                OreBlocks.DEEPSLATE_SAPPHIRE_ORE.asItem(),
                OreBlocks.DEEPSLATE_COBALT_ORE.asItem(),
                OreBlocks.DEEPSLATE_PALLADIUM_ORE.asItem(),
                OreBlocks.DEEPSLATE_MYTHRIL_ORE.asItem(),
                OreBlocks.DEEPSLATE_ORICHALCUM_ORE.asItem(),
                OreBlocks.DEEPSLATE_ADAMANTITE_ORE.asItem(),
                OreBlocks.DEEPSLATE_TITANIUM_ORE.asItem(),
                OreBlocks.DEEPSLATE_AMETHYST_ORE.asItem()
        );

        tag(Tags.Items.ORES_IN_GROUND_NETHERRACK).add(
                OreBlocks.HELLSTONE.asItem()
        );
        tag(Tags.Items.ORES_IN_GROUND_STONE).add(
                OreBlocks.TIN_ORE.asItem(),
                OreBlocks.LEAD_ORE.asItem(),
                OreBlocks.SILVER_ORE.asItem(),
                OreBlocks.TUNGSTEN_ORE.asItem(),
                OreBlocks.PLATINUM_ORE.asItem(),
                OreBlocks.RUBY_ORE.asItem(),
                OreBlocks.TOPAZ_ORE.asItem(),
                OreBlocks.JADE_ORE.asItem(),
                OreBlocks.SAPPHIRE_ORE.asItem(),
                OreBlocks.AMETHYST_ORE.asItem(),
                OreBlocks.DEMONITE_ORE.asItem(),
                OreBlocks.CRIMTANE_ORE.asItem()
        );

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
        tag(ModTags.Items.CROP_FORTUNE).add(AxeItems.STAFF_OF_REGROWTH.get(), AxeItems.AXE_OF_REGROWTH.get());
        // 速发弓：恶魔弓、肌腱弓
        tag(ModTags.Items.FAST_BOW).add(
                BowItems.FOSSIL_BOW.get(),
                BowItems.DEMON_BOW.get(),
                BowItems.TENDON_BOW.get(),
                BowItems.DAEDALUS_STORM_BOW.get(),
                BowItems.MOLTEN_FURY.get(),
                BowItems.THE_BEES_KNEES.get()
        );

        AccessoryItems.acceptTags(this);
        tag(ModTags.Items.AMMO)
                .add(Items.FIREWORK_ROCKET, MaterialItems.FALLING_STAR.get())
                .addTag(ItemTags.ARROWS)
                .addOptionalTag(TGTags.AMMO);
        IntrinsicTagAppender<Item> dye = tag(ModTags.Items.DYE);
        VanityArmorItems.DYE_ITEMS.forEach(item -> dye.add(item.get()));
        dye.add(VanityArmorItems.TEAM_DYE.get());

        // Bow 附魔
        IntrinsicTagAppender<Item> durabilityEnchantable = tag(ItemTags.DURABILITY_ENCHANTABLE);
        IntrinsicTagAppender<Item> skipUsingSlowdown = tag(LibTags.Items.SKIP_USING_SLOWDOWN);
        BowItems.ITEMS.getEntries().forEach(item -> {
            Item value = item.get();
            durabilityEnchantable.add(value);
            tag(ItemTags.BOW_ENCHANTABLE).add(value);
            tag(Tags.Items.RANGED_WEAPON_TOOLS).add(value);
            weapons.add(value);
            tag(Tags.Items.TOOLS_BOW).add(value);
            skipUsingSlowdown.add(value);
        });
        //  FishingPole 附魔
        FishingPoleItems.acceptTag(tag(ItemTags.FISHING_ENCHANTABLE));
        // Sword 附魔
        IntrinsicTagAppender<Item> meleeWeaponTools = tag(Tags.Items.MELEE_WEAPON_TOOLS);
        SwordItems.ITEMS.getEntries().forEach(item -> {
            Item value = item.get();
            tag(ItemTags.SHARP_WEAPON_ENCHANTABLE).add(value);
            tag(ItemTags.SWORD_ENCHANTABLE).add(value);
            durabilityEnchantable.add(value);
            tag(ItemTags.WEAPON_ENCHANTABLE).add(value);
            meleeWeaponTools.add(value);
            weapons.add(value);
            tag(ItemTags.SWORDS).add(value);
        });

        // Tool 附魔
        //ToolItems.ITEMS.getEntries().forEach(item -> tag(ItemTags.DURABILITY_ENCHANTABLE).add(item.get())); 不是所有工具都能附魔！
        HoeItems.acceptTag(durabilityEnchantable);
        HoeItems.acceptTag(tag(ItemTags.HOES));
        ShovelItems.acceptTag(durabilityEnchantable);
        ShovelItems.acceptTag(tag(ItemTags.SHOVELS));
        HamaxeItems.acceptTag(durabilityEnchantable);
        HoeShovelItems.acceptTag(tag(ItemTags.HOES));
        HoeShovelItems.acceptTag(tag(ItemTags.SHOVELS));
        HoeShovelItems.acceptTag(durabilityEnchantable);


        // Armor 附魔
        ArmorItems.ITEMS.getEntries().forEach(item -> {
            if (item.get() instanceof ArmorItem armor) {
                durabilityEnchantable.add(armor);
                tag(ItemTags.ARMOR_ENCHANTABLE).add(armor);
                tag(ItemTags.EQUIPPABLE_ENCHANTABLE).add(armor);
                if (armor.getEquipmentSlot() == EquipmentSlot.HEAD) {
                    tag(ItemTags.HEAD_ARMOR_ENCHANTABLE).add(armor);
                    tag(ItemTags.HEAD_ARMOR).add(armor);
                } else if (armor.getEquipmentSlot() == EquipmentSlot.CHEST) {
                    tag(ItemTags.CHEST_ARMOR_ENCHANTABLE).add(armor);
                    tag(ItemTags.CHEST_ARMOR).add(armor);
                } else if (armor.getEquipmentSlot() == EquipmentSlot.LEGS) {
                    tag(ItemTags.LEG_ARMOR_ENCHANTABLE).add(armor);
                    tag(ItemTags.LEG_ARMOR).add(armor);
                } else if (armor.getEquipmentSlot() == EquipmentSlot.FEET) {
                    tag(ItemTags.FOOT_ARMOR).add(armor);
                    tag(ItemTags.FOOT_ARMOR_ENCHANTABLE).add(armor);
                }
            }
        });

        LightPetItems.ITEMS.getEntries().forEach(item -> tag(ModTags.Items.LIGHT_PET).add(item.get()));
        TreasureBagItems.ITEMS.getEntries().forEach(item -> tag(ModTags.Items.TREASURE_BAG).add(item.get()));

        tag(ModTags.Items.HARDMODE)
                .addTag(ModTags.Items.HARDMODE_RAW_MATERIALS)
                .add(NatureBlocks.PEARL_LOG_BLOCKS.getAllItems().stream().map(Supplier::get).toArray(Item[]::new))
                .add(NatureBlocks.PEARL_SAPLING.asItem());

        TESummonItems.ITEMS.getEntries().forEach(item -> tag(ModTags.Items.SUMMONER_WEAPON).add(item.get()));

        tag(ModTags.Items.ABLE_TO_DESTROY_ALTAR).add(
                HammerItems.PWNHAMMER.get(),
                HammerItems.HAMMUSH.get()
        );
        tag(Tags.Items.FOODS).add(
                PotionItems.ALE.get()
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
        tag(ItemTags.NON_FLAMMABLE_WOOD).add(
                NatureBlocks.ASH_LOG_BLOCKS.getLog().asItem(),
                NatureBlocks.ASH_LOG_BLOCKS.getWood().asItem(),
                NatureBlocks.ASH_LOG_BLOCKS.getStrippedLog().asItem(),
                NatureBlocks.ASH_LOG_BLOCKS.getStrippedWood().asItem(),
                NatureBlocks.ASH_LOG_BLOCKS.getPlanks().asItem(),
                NatureBlocks.ASH_LOG_BLOCKS.getStairs().asItem(),
                NatureBlocks.ASH_LOG_BLOCKS.getSlab().asItem(),
                NatureBlocks.ASH_LOG_BLOCKS.getButton().asItem(),
                NatureBlocks.ASH_LOG_BLOCKS.getFence().asItem(),
                NatureBlocks.ASH_LOG_BLOCKS.getFenceGate().asItem(),
                NatureBlocks.ASH_LOG_BLOCKS.getSign().asItem(),
                NatureBlocks.ASH_LOG_BLOCKS.getPressurePlate().asItem(),
                NatureBlocks.ASH_LOG_BLOCKS.getDoor().asItem()
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
                QuestedFishes.CAP_TUNABEARD.get(),
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
                FoodItems.OBSIDIAN_FISH.get(),
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
        copy(BlockTags.SIGNS, ItemTags.SIGNS);
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
        copy(Tags.Blocks.GRAVELS, Tags.Items.GRAVELS);
        copy(Tags.Blocks.CHAINS, Tags.Items.CHAINS);
        copy(Tags.Blocks.ROPES, Tags.Items.ROPES);
        copy(Tags.Blocks.VILLAGER_JOB_SITES, Tags.Items.VILLAGER_JOB_SITES);
        copy(Tags.Blocks.CHESTS_TRAPPED, Tags.Items.CHESTS_TRAPPED);
        copy(Tags.Blocks.PLAYER_WORKSTATIONS_CRAFTING_TABLES, Tags.Items.PLAYER_WORKSTATIONS_CRAFTING_TABLES);
        copy(Tags.Blocks.GLASS_BLOCKS_COLORLESS, Tags.Items.GLASS_BLOCKS_COLORLESS);

        IntrinsicTagAppender<Item> wip = tag(LibTags.Items.WIP);
        wip.add(
                ConsumableItems.ARTISAN_LOAF.get(),
                ConsumableItems.SMOKE_BOMB.get(),
                ConsumableItems.BEENADE.get(),
                ConsumableItems.SPIKY_BALL.get(),
                ConsumableItems.BEENADE.get(),
                ConsumableItems.HOLY_WATER.get(),
                ConsumableItems.UNHOLY_WATER.get(),
                ConsumableItems.BLOOD_WATER.get(),
                ConsumableItems.GOODIE_BAG.get(),
                ConsumableItems.ADVANCED_COMBAT_TECHNIQUES.get(),
                ConsumableItems.ADVANCED_COMBAT_TECHNIQUES_VOLUME_TWO.get(),
                ConsumableItems.GUIDE_VOODOO_DOLL.get(),
                ConsumableItems.CLOTHIER_VOODOO_DOLL.get(),
                PaintItems.ECHO_COATING.get(),
                ToolItems.SUPER_ABSORBANT_SPONGE.get(),
                ToolItems.HONEY_ABSORBANT_SPONGE.get(),
                ToolItems.LAVA_ABSORBANT_SPONGE.get(),
                ToolItems.ULTRA_ABSORBANT_SPONGE.get(),
                ToolItems.TEMPLE_KEY.get(),
                ToolItems.JUNGLE_KEY.get(),
                ToolItems.CORRUPTION_KEY.get(),
                ToolItems.CRIMSON_KEY.get(),
                ToolItems.HALLOWED_KEY.get(),
                ToolItems.FROZEN_KEY.get(),
                ToolItems.DESERT_KEY.get(),
                ToolItems.TARGET_DUMMY.get(),
                ToolItems.BINOCULARS.get(),
                ModItems.WHOOPIE_CUSHION.get(),
                ModItems.TOKYO_TEDDY_BEAR.get(),
                ModItems.ICE_TOFU_BRICK.get(),
                ModItems.FAILED_SKULL.get(),
                ModItems.KIND_MISIDE_RING.get(),
                ModItems.FERTILE_SINGULARITY.get(),
                ModItems.PERPLEXED_CAT_MEDAL.get(),
                ModItems.PULSAR.get(),
                ModItems.HIVE_WAND.get(),
                HamaxeItems.HAEMORRHAXE.get(),
                HamaxeItems.SPECTRE_HAMAXE.get(),
                HamaxeItems.SOLAR_FLARE_HAMAXE.get(),
                HamaxeItems.VORTEX_HAMAXE.get(),
                HamaxeItems.NEBULA_HAMAXE.get(),
                HamaxeItems.STARDUST_HAMAXE.get(),
                HamaxeItems.THE_AXE.get(),
                HammerItems.RICH_MAHOGANY_HAMMER.get(),
                HammerItems.PALM_WOOD_HAMMER.get(),
                HammerItems.SPRUCE_WOOD_HAMMER.get(),
                HammerItems.EBONWOOD_HAMMER.get(),
                HammerItems.SHADEWOOD_HAMMER.get(),
                HammerItems.ASH_WOOD_HAMMER.get(),
                HammerItems.PEARLWOOD_HAMMER.get(),
                HammerItems.ROCKFISH.get(),
                HammerItems.HAMMUSH.get(),
                HammerItems.CHLOROPHYTE_WARHAMMER.get(),
                HammerItems.CHLOROPHYTE_JACKHAMMER.get(),
                PickaxeAxeItems.SHROOMITE_DIGGING_CLAW.get(),
                PickaxeItems.COBALT_PICKAXE.get(),
                PickaxeItems.PALLADIUM_PICKAXE.get(),
                PickaxeItems.MYTHRIL_PICKAXE.get(),
                PickaxeItems.ORICHALCUM_PICKAXE.get(),
                PickaxeItems.ADAMANTITE_PICKAXE.get(),
                PickaxeItems.TITANIUM_PICKAXE.get(),
                PickaxeItems.SPECTRE_PICKAXE.get(),
                PickaxeItems.CHLOROPHYTE_PICKAXE.get(),
                PickaxeItems.SOLAR_FLARE_PICKAXE.get(),
                PickaxeItems.VORTEX_PICKAXE.get(),
                PickaxeItems.NEBULA_PICKAXE.get(),
                PickaxeItems.STARDUST_PICKAXE.get(),
                AccessoryItems.FIN_WINGS.get(),
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
                AxeItems.COBALT_WARAXE.get(),
                AxeItems.PALLADIUM_WARAXE.get(),
                AxeItems.MYTHRIL_WARAXE.get(),
                AxeItems.ORICHALCUM_WARAXE.get(),
                AxeItems.ADAMANTITE_WARAXE.get(),
                AxeItems.TITANIUM_WARAXE.get(),
                AxeItems.CHLOROPHYTE_GREATAXE.get(),
                AxeItems.LUCY_THE_AXE.get(),
                ArmorItems.GOGGLES.get(),
                ArmorItems.GREEN_CAP.get(),
                ArmorItems.WIZARD_HAT.get(),
                ArmorItems.MAGIC_HAT.get(),
                ArmorItems.AMETHYST_ROBE.get(),
                ArmorItems.TOPAZ_ROBE.get(),
                ArmorItems.SAPPHIRE_ROBE.get(),
                ArmorItems.JADE_ROBE.get(),
                ArmorItems.RUBY_ROBE.get(),
                ArmorItems.MYSTIC_ROBE.get(),
                ArmorItems.DIAMOND_ROBE.get(),
                ArmorItems.AMBER_ROBE.get(),
                ArmorItems.COBALT_MASK.get(),
                ArmorItems.COBALT_HAT.get(),
                ArmorItems.COBALT_HELMET.get(),
                ArmorItems.COBALT_CHESTPLATE.get(),
                ArmorItems.COBALT_LEGGINGS.get(),
                ArmorItems.COBALT_BOOTS.get(),
                ArmorItems.PALLADIUM_MASK.get(),
                ArmorItems.PALLADIUM_HEADGEAR.get(),
                ArmorItems.PALLADIUM_HELMET.get(),
                ArmorItems.PALLADIUM_CHESTPLATE.get(),
                ArmorItems.PALLADIUM_LEGGINGS.get(),
                ArmorItems.PALLADIUM_BOOTS.get(),
                ArmorItems.MYTHRIL_HOOD.get(),
                ArmorItems.MYTHRIL_HAT.get(),
                ArmorItems.MYTHRIL_HELMET.get(),
                ArmorItems.MYTHRIL_CHESTPLATE.get(),
                ArmorItems.MYTHRIL_LEGGINGS.get(),
                ArmorItems.MYTHRIL_BOOTS.get(),
                ArmorItems.ORICHALCUM_HEADGEAR.get(),
                ArmorItems.ORICHALCUM_MASK.get(),
                ArmorItems.ORICHALCUM_HELMET.get(),
                ArmorItems.ORICHALCUM_CHESTPLATE.get(),
                ArmorItems.ORICHALCUM_LEGGINGS.get(),
                ArmorItems.ORICHALCUM_BOOTS.get(),
                ArmorItems.ADAMANTITE_HEADGEAR.get(),
                ArmorItems.ADAMANTITE_MASK.get(),
                ArmorItems.ADAMANTITE_HELMET.get(),
                ArmorItems.ADAMANTITE_CHESTPLATE.get(),
                ArmorItems.ADAMANTITE_LEGGINGS.get(),
                ArmorItems.ADAMANTITE_BOOTS.get(),
                ArmorItems.TITANIUM_HEADGEAR.get(),
                ArmorItems.TITANIUM_MASK.get(),
                ArmorItems.TITANIUM_HELMET.get(),
                ArmorItems.TITANIUM_CHESTPLATE.get(),
                ArmorItems.TITANIUM_LEGGINGS.get(),
                ArmorItems.TITANIUM_BOOTS.get(),
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
                VanityArmorItems.DEAD_MANS_SWEATER.get(),
                VanityArmorItems.ROBE.get(),
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
                VanityArmorItems.FAMILIAR_WIG.get(),
                VanityArmorItems.FAMILIAR_SHIRT.get(),
                VanityArmorItems.FAMILIAR_PANTS.get(),
                VanityArmorItems.FAMILIAR_SHOES.get(),
                VanityArmorItems.MIME_MASK.get(),
                VanityArmorItems.THE_DOCTORS_SHIRT.get(),
                VanityArmorItems.THE_DOCTORS_PANTS.get(),
                VanityArmorItems.THE_DOCTORS_SHOES.get(),
                VanityArmorItems.TEAM_DYE.get(),
                LanceItems.DARK_LANCE.get(),
                ManaWeaponItems.MAGIC_DAGGER.get(),
                ManaWeaponItems.CRYSTAL_STORM.get(),
                ManaWeaponItems.CURSED_FLAMES.get(),
                ManaWeaponItems.FLOWER_OF_FROST.get(),
                NatureBlocks.ASH_LOG_BLOCKS.getLeaves().asItem(),
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
                NatureBlocks.COCONUT_BLOCK.asItem(),
                NatureBlocks.CRIMSON_VENUS_FLYTRAP_BLOCK.asItem(),
                NatureBlocks.BLOODTHIRST_CRYSTALLIZED_BLOCK.asItem(),
                NatureBlocks.CORRODED_WORM_ROOTS_BLOCK.asItem(),
                NatureBlocks.CORRUPTED_OVARIES_BLOCK.asItem(),
                NatureBlocks.SPOOKY_LOG_BLOCKS.getStrippedLog().asItem(),
                NatureBlocks.SPOOKY_LOG_BLOCKS.getWood().asItem(),
                NatureBlocks.SPOOKY_LOG_BLOCKS.getStrippedWood().asItem(),
                NatureBlocks.SPOOKY_LOG_BLOCKS.getTrapdoor().asItem(),
                NatureBlocks.SPOOKY_LOG_BLOCKS.getDoor().asItem(),
                NatureBlocks.SPOOKY_LOG_BLOCKS.getLog().asItem(),
                NatureBlocks.SPOOKY_LOG_BLOCKS.getLeaves().asItem(),
                NatureBlocks.DECOMPOSE_THE_SOURCE_EXTRACT_BLOCK.asItem(),
                PotBlocks.OCEAN_POT.asItem(),
                OreBlocks.METEORITE_BLOCK.asItem(),
                OreBlocks.HALLOWED_BLOCK.asItem(),
                OreBlocks.RAW_CHLOROPHYTE_BLOCK.asItem(),
                OreBlocks.CHLOROPHYTE_ORE.asItem(),
                OreBlocks.SHROOMITE_BLOCK.asItem(),
                OreBlocks.SPECTRE_BLOCK.asItem(),
                OreBlocks.RAW_LUMINITE_BLOCK.asItem(),
                OreBlocks.LUMINITE_BLOCK.asItem(),
                OreBlocks.HELLSTONE_BLOCK.asItem(),
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
                DecorativeBlocks.ASPHALT_BLOCK.asItem(),
                DecorativeBlocks.FLESH_BLOCK.asItem(),
                DecorativeBlocks.LESION_BLOCK.asItem(),
                DecorativeBlocks.REMAINS_BLOCK.asItem(),
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
                StatueBlocks.BAST_STATUE.asItem(),
                TFBlocks.GLASS_KILN.asItem(),
                TFBlocks.LIVING_LOOM.asItem(),
                TFBlocks.ICE_MACHINE.asItem(),
                TFBlocks.FISH_BOWL.asItem(),
                TFBlocks.GOLD_FISH_BOWL.asItem(),
                TFBlocks.PUPFISH_BOWL.asItem(),
                TFBlocks.LAVA_SERPENT_BOWL.asItem(),
                TFBlocks.TRASH_CAN.asItem(),
                TFBlocks.GLASS_BATHTUB.asItem(),
                TFBlocks.BLUE_BRICK_SOFA.asItem(),
                TFBlocks.BLUE_BRICK_TOILET.asItem(),
                TFBlocks.BLUE_BRICK_SINK.asItem(),
                TFBlocks.BLUE_BRICK_CANDLE.asItem(),
                TFBlocks.BLUE_BRICK_CHANDELIER.asItem(),
                TFBlocks.BLUE_BRICK_LANTERN.asItem(),
                TFBlocks.BLUE_BRICK_LAMP.asItem(),
                TFBlocks.BLUE_BRICK_CANDELABRAS.asItem(),
                TFBlocks.BLUE_BRICK_CLOCK.asItem(),
                TFBlocks.BLUE_BRICK_BATHTUB.asItem(),
                FunctionalBlocks.JUNGLE_CHEST.asItem(),
                FunctionalBlocks.CORRUPTION_CHEST.asItem(),
                FunctionalBlocks.CRIMSON_CHEST.asItem(),
                FunctionalBlocks.HALLOWED_CHEST.asItem(),
                FunctionalBlocks.ICE_CHEST.asItem(),
                FunctionalBlocks.DESERT_CHEST.asItem(),
                FunctionalBlocks.BEWITCHING_TABLE.asItem(),
                FunctionalBlocks.CHLOROPHYTE_EXTRACTINATOR.asItem(),
                FunctionalBlocks.BLEND_O_MATIC.asItem(),
                FunctionalBlocks.MEAT_GRINDER.asItem(),
                FunctionalBlocks.LIFE_CAMPFIRE.asItem(),
                FunctionalBlocks.SPIKE.asItem(),
                FunctionalBlocks.WOODEN_SPIKE.asItem(),
                FunctionalBlocks.FRAGILE_BLUE_BRICKS.asItem(),
                FunctionalBlocks.FRAGILE_GREEN_BRICKS.asItem(),
                FunctionalBlocks.FRAGILE_PINK_BRICKS.asItem(),
                FunctionalBlocks.ENCHANTED_FRAGILE_BLUE_BRICKS.asItem(),
                FunctionalBlocks.ENCHANTED_FRAGILE_GREEN_BRICKS.asItem(),
                FunctionalBlocks.ENCHANTED_FRAGILE_PINK_BRICKS.asItem(),
                FunctionalBlocks.SILLY_BALLOON_MACHINE.asItem(),
                FunctionalBlocks.PLAYER_PRESSURE_PLATE.asItem(),
                FunctionalBlocks.LEVER.asItem(),
                FunctionalBlocks.GEYSER_BLOCK.asItem(),
                FunctionalBlocks.DETONATOR.asItem(),
                FunctionalBlocks.LAND_MINE.asItem(),
                FunctionalBlocks.SUPER_DART_TRAP.asItem(),
                FunctionalBlocks.FLAME_TRAP.asItem(),
                FunctionalBlocks.SPIKY_BALL_TRAP.asItem(),
                FunctionalBlocks.SPEAR_TRAP.asItem(),
                FunctionalBlocks.TREE_HOLES_BLOCK.asItem(),
                FunctionalBlocks.MAGIC_MAIL_BOX.asItem(),
                MaterialItems.RAW_ASPHALT.get(),
                MaterialItems.SOUL_OF_LIGHT.get(),
                MaterialItems.SOUL_OF_NIGHT.get(),
                MaterialItems.SOUL_OF_FLIGHT.get(),
                MaterialItems.SOUL_OF_FRIGHT.get(),
                MaterialItems.SOUL_OF_MIGHT.get(),
                MaterialItems.SOUL_OF_SIGHT.get(),
                MaterialItems.SPELL_TOME.get(),
                CrateBlocks.HALLOWED_CRATE.asItem(),
                CrateBlocks.OBSIDIAN_CRATE.asItem(),
                CrateBlocks.PEARLWOOD_CRATE.asItem(),
                CrateBlocks.MYTHRIL_CRATE.asItem(),
                CrateBlocks.TITANIUM_CRATE.asItem(),
                CrateBlocks.BRAMBLE_CRATE.asItem(),
                CrateBlocks.WILD_CRATE.asItem(),
                CrateBlocks.AZURE_CRATE.asItem(),
                CrateBlocks.DEFILED_CRATE.asItem(),
                CrateBlocks.HEMATIC_CRATE.asItem(),
                CrateBlocks.DIVINE_CRATE.asItem(),
                CrateBlocks.STOCKADE_CRATE.asItem(),
                CrateBlocks.BOREAL_CRATE.asItem(),
                CrateBlocks.MIRAGE_CRATE.asItem(),
                CrateBlocks.HELLSTONE_CRATE.asItem(),
                CrateBlocks.SEASIDE_CRATE.asItem(),
                LanceItems.DARK_LANCE.get(),
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
                TreasureBagItems.WALL_OF_FLESH_TREASURE_BAG.get(),
                NatureBlocks.LIFE_MUSHROOM_INDUSIUM_BLOCK.asItem(),
                NatureBlocks.LIFE_MUSHROOM_STEM_BLOCK.asItem(),
                NatureBlocks.LIFE_MUSHROOM_PILEUS_BLOCK.asItem(),
                NatureBlocks.DESERT_TAPERED_BLOCK.asItem(),
                NatureBlocks.SMALL_DESERT_PLANT.asItem(),
                NatureBlocks.BIG_DESERT_PLANT.asItem(),
                NatureBlocks.SMALL_CACTUS.asItem()
        );
        Consumer<DeferredHolder<Item, ? extends Item>> wipAction = item -> wip.add(item.get());
        MinecartItems.ITEMS.getEntries().forEach(wipAction);
        DrillItems.ITEMS.getEntries().forEach(wipAction);
        LightPetItems.ITEMS.getEntries().forEach(wipAction);

        tag(TGTags.GUN).add(
                GunItems.STAR_CANNON.get()
        );
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

        IntrinsicTagAppender<Item> skipResetStrength = tag(LibTags.Items.SKIP_RESET_STRENGTH);
        DrillItems.ITEMS.getEntries().forEach(item -> skipResetStrength.add(item.get()));
        LanceItems.ITEMS.getEntries().forEach(item -> {
            Item value = item.get();
            skipResetStrength.add(value);
            meleeWeaponTools.add(value);
            weapons.add(value);
            tag(ModTags.Items.LANCES).add(value);
        });

        tag(ModTags.Items.SHOW_SIGNAL).add(
                ToolItems.RED_WRENCH.get(),
                ToolItems.GREEN_WRENCH.get(),
                ToolItems.BLUE_WRENCH.get(),
                ToolItems.YELLOW_WRENCH.get(),
                ToolItems.WIRE_CUTTER.get()
        );
        WaystonesHelper.itemTag(this::tag);
    }

    @Override
    public IntrinsicTagAppender<Item> tag(TagKey<Item> tag) {
        return super.tag(tag);
    }
}
