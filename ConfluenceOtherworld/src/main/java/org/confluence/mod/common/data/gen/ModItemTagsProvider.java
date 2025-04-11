package org.confluence.mod.common.data.gen;

import com.xiaohunao.terra_moment.common.init.TMItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
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
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.lib.common.LibTags;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.block.OreBlocks;
import org.confluence.mod.common.init.item.*;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.common.init.TCTags;
//import org.confluence.terra_guns.common.init.TGItems;
import org.confluence.terraentity.init.TEItems;
import org.confluence.terraentity.init.TETags;
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
                MaterialItems.TR_CRIMSON_INGOT.get(),
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
                MaterialItems.TR_CRIMSON_INGOT.get(),
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
                MaterialItems.TR_AMETHYST.get(),
                MaterialItems.TR_EMERALD.get(),
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
                NatureBlocks.EMERALD_SAPLING.asItem(),
                NatureBlocks.DIAMOND_SAPLING.asItem(),
                NatureBlocks.SAPPHIRE_SAPLING.asItem(),
                NatureBlocks.TR_AMETHYST_SAPLING.asItem(),
                NatureBlocks.ASH_SAPLING.asItem(),
                NatureBlocks.LIVING_SAPLING.asItem(),
                NatureBlocks.YELLOW_WILLOW_SAPLING.asItem()
        );

        tag(ModTags.Items.EVIL_INGOT).add(MaterialItems.DEMONITE_INGOT.get(), MaterialItems.TR_CRIMSON_INGOT.get());
        tag(ModTags.Items.LEAD_AND_IRON).add(Items.IRON_INGOT, MaterialItems.LEAD_INGOT.get());
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
        tag(ModTags.Items.TR_CRIMSON_ORE_SMELTING).add(
                OreBlocks.TR_CRIMSON_ORE.asItem(), OreBlocks.DEEPSLATE_TR_CRIMSON_ORE.asItem(), OreBlocks.SANCTIFICATION_TR_CRIMSON_ORE.asItem(), OreBlocks.CORRUPTION_TR_CRIMSON_ORE.asItem(), OreBlocks.FLESHIFICATION_TR_CRIMSON_ORE.asItem(),
                MaterialItems.RAW_TR_CRIMSON.get()

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
        tag(ModTags.Items.TR_EMERALD_ORE_SMELTING).add(
                OreBlocks.TR_EMERALD_ORE.asItem(), OreBlocks.DEEPSLATE_RUBY_ORE.asItem(), OreBlocks.SANCTIFICATION_RUBY_ORE.asItem(), OreBlocks.CORRUPTION_RUBY_ORE.asItem(), OreBlocks.FLESHIFICATION_RUBY_ORE.asItem()
        );
        tag(ModTags.Items.DIAMOND_ORE_SMELTING).add(
                OreBlocks.SANCTIFICATION_DIAMOND_ORE.asItem(), OreBlocks.CORRUPTION_DIAMOND_ORE.asItem(), OreBlocks.FLESHIFICATION_DIAMOND_ORE.asItem()
        );
        tag(ModTags.Items.SAPPHIRE_ORE_SMELTING).add(
                OreBlocks.SAPPHIRE_ORE.asItem(), OreBlocks.DEEPSLATE_SAPPHIRE_ORE.asItem(), OreBlocks.SANCTIFICATION_SAPPHIRE_ORE.asItem(), OreBlocks.CORRUPTION_SAPPHIRE_ORE.asItem(), OreBlocks.FLESHIFICATION_SAPPHIRE_ORE.asItem()
        );
        tag(ModTags.Items.TR_AMETHYST_ORE_SMELTING).add(
                OreBlocks.TR_AMETHYST_ORE.asItem(), OreBlocks.DEEPSLATE_TR_AMETHYST_ORE.asItem(), OreBlocks.SANCTIFICATION_TR_AMETHYST_ORE.asItem(), OreBlocks.CORRUPTION_TR_AMETHYST_ORE.asItem(), OreBlocks.FLESHIFICATION_TR_AMETHYST_ORE.asItem()
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
        tag(Tags.Items.BONES).add(
                MaterialItems.ROTTEN_BONE.get(),
                MaterialItems.VERTEBRA.get()
        );
        tag(Tags.Items.BRICKS).add(
                DecorativeBlocks.TR_COPPER_BRICKS.asItem(),
                DecorativeBlocks.TR_CRIMSON_ORE_BRICKS.asItem(),
                DecorativeBlocks.TR_CRIMSON_ROCK_BRICKS.asItem(),
                DecorativeBlocks.TR_GOLD_BRICKS.asItem(),
                DecorativeBlocks.TR_IRON_BRICKS.asItem(),
                DecorativeBlocks.TR_STONE_BRICKS.asItem(),
                DecorativeBlocks.DEMONITE_ORE_BRICKS.asItem(),
                DecorativeBlocks.EBONY_ROCK_BRICKS.asItem(),
                DecorativeBlocks.BLUE_ICE_BRICKS.asItem(),
                DecorativeBlocks.PACKED_ICE_BRICKS.asItem(),
                DecorativeBlocks.LEAD_BRICKS.asItem(),
                DecorativeBlocks.METEORITE_BRICKS.asItem(),
                DecorativeBlocks.PEARL_ROCK_BRICKS.asItem(),
                DecorativeBlocks.PLATINUM_BRICKS.asItem(),
                DecorativeBlocks.SILVER_BRICKS.asItem(),
                DecorativeBlocks.SNOW_BRICKS.asItem(),
                DecorativeBlocks.TUNGSTEN_BRICKS.asItem(),
                DecorativeBlocks.TR_LAVA_BRICKS.asItem(),
                DecorativeBlocks.TR_OBSIDIAN_BRICKS.asItem(),
                DecorativeBlocks.TR_OBSIDIAN_SMALL_BRICKS.asItem(),
                DecorativeBlocks.CRYSTAL_BLOCKS.asItem(),
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
                MaterialItems.TR_EMERALD.get(),
                MaterialItems.SAPPHIRE.get(),
                MaterialItems.TR_AMETHYST.get()
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
        tag(ModTags.Items.GEMS_TR_EMERALD).add(
                MaterialItems.TR_EMERALD.get()
        );
        tag(ModTags.Items.GEMS_SAPPHIRE).add(
                MaterialItems.SAPPHIRE.get()
        );
        tag(ModTags.Items.GEMS_TR_AMETHYST).add(
                MaterialItems.TR_AMETHYST.get()
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
                MaterialItems.TR_CRIMSON_INGOT.get(),
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
                MaterialItems.RAW_TR_CRIMSON.get()
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

        IntrinsicTagAppender<Item> tools = tag(Tags.Items.TOOLS);
        PickaxeAxeItems.acceptTag(tools);
        AxeItems.acceptTag(tools);
        PickaxeItems.acceptTag(tools);
        HamaxeItems.acceptTag(tools);
        HammerItems.acceptTag(tools);
        FishingPoleItems.acceptTag(tools);

        ManaWeaponItems.acceptTag(tag(ModTags.Items.MANA_WEAPON));
        IntrinsicTagAppender<Item> weapons = tag(ModTags.Items.WEAPONS);
        ManaWeaponItems.acceptTag(weapons);
        IntrinsicTagAppender<Item> mining_tool_tools = tag(Tags.Items.MINING_TOOL_TOOLS);
        PickaxeItems.acceptTag(mining_tool_tools);
        PickaxeAxeItems.acceptTag(mining_tool_tools);
        AxeItems.acceptTag(mining_tool_tools);
        HamaxeItems.acceptTag(mining_tool_tools);
        HammerItems.acceptTag(mining_tool_tools);
        DrillItems.acceptTag(mining_tool_tools);
        IntrinsicTagAppender<Item> prefix_universal_only = tag(ModTags.Items.PREFIX_UNIVERSAL_ONLY);
        DrillItems.acceptTag(prefix_universal_only);
        TEBoomerangItems.acceptTag(prefix_universal_only);

        tag(ModTags.Items.COINS).add(
                ModItems.COPPER_COIN.get(),
                ModItems.SILVER_COIN.get(),
                ModItems.GOLDEN_COIN.get(),
                ModItems.PLATINUM_COIN.get()
                // 不要加回来，会出bug。ModItems.EMERALD_COIN.get()
        );
        tag(TETags.Items.HONEY_TRANSLATION_BUCKET).add(ToolItems.HONEY_BUCKET.get());
        tag(TETags.Items.HONEY_TRANSLATION_NOT_CONSUMED).add(ToolItems.BOTTOMLESS_HONEY_BUCKET.get());
        tag(ModTags.Items.HARDMODE_RAW_MATERIALS).add(
                MaterialItems.RAW_COBALT.get(),
                MaterialItems.RAW_PALLADIUM.get(),
                MaterialItems.RAW_MYTHRIL.get(),
                MaterialItems.RAW_ORICHALCUM.get(),
                MaterialItems.RAW_ADAMANTITE.get(),
                MaterialItems.RAW_TITANIUM.get()
        );
        tag(ModTags.Items.WINGS).add(
                AccessoryItems.FLEDGLING_WINGS.get(),
                TCItems.CELESTIAL_STARBOARD.get()
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
        tag(ModTags.Items.INGOTS_CRIMSON).add(MaterialItems.TR_CRIMSON_INGOT.get());
        tag(ModTags.Items.CRIMSON_BLOCK).add(OreBlocks.TR_CRIMSON_BLOCK.asItem());
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
        tag(ModTags.Items.RAW_MATERIALS_CRIMSON).add(MaterialItems.RAW_TR_CRIMSON.get());
        tag(ModTags.Items.RAW_MATERIALS_CRIMSON_BLOCK).add(OreBlocks.RAW_TR_CRIMSON_BLOCK.asItem());
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
                OreBlocks.DEEPSLATE_TR_EMERALD_ORE.asItem(),
                OreBlocks.DEEPSLATE_SAPPHIRE_ORE.asItem(),
                OreBlocks.DEEPSLATE_COBALT_ORE.asItem(),
                OreBlocks.DEEPSLATE_PALLADIUM_ORE.asItem(),
                OreBlocks.DEEPSLATE_MYTHRIL_ORE.asItem(),
                OreBlocks.DEEPSLATE_ORICHALCUM_ORE.asItem(),
                OreBlocks.DEEPSLATE_ADAMANTITE_ORE.asItem(),
                OreBlocks.DEEPSLATE_TITANIUM_ORE.asItem(),
                OreBlocks.DEEPSLATE_TR_AMETHYST_ORE.asItem()
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
                OreBlocks.TR_EMERALD_ORE.asItem(),
                OreBlocks.SAPPHIRE_ORE.asItem(),
                OreBlocks.TR_AMETHYST_ORE.asItem(),
                OreBlocks.DEMONITE_ORE.asItem(),
                OreBlocks.TR_CRIMSON_ORE.asItem()
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
                NatureBlocks.GROWING_MUSHROOM_MOSS.asItem()
        );
        // 农作物掉落提升 再生法杖/再生之斧
        tag(ModTags.Items.CROP_FORTUNE).add(AxeItems.STAFF_OF_REGROWTH.get(), AxeItems.DRILL_OF_REGROWTH.get());
        // 速发弓：恶魔弓、肌腱弓
        tag(ModTags.Items.FAST_BOW).add(
                BowItems.DEMON_BOW.get(),
                BowItems.TENDON_BOW.get(),
                BowItems.DAEDALUS_STORM_BOW.get(),
                BowItems.MOLTEN_FURY.get(),
                BowItems.THE_BEES_KNEES.get()
        );

        AccessoryItems.acceptTag(tag(TCTags.ACCESSORY));
        IntrinsicTagAppender<Item> ammo = tag(ModTags.Items.AMMO)
                .addTag(ItemTags.ARROWS)
                .add(Items.FIREWORK_ROCKET, MaterialItems.FALLING_STAR.get());
        //TODO 枪！
//        TGItems.ITEM_BULLETS.getEntries().forEach(item -> ammo.add(item.get()));
        IntrinsicTagAppender<Item> dye = tag(ModTags.Items.DYE);
        VanityArmorItems.DYE_ITEMS.forEach(dye::add);
        dye.add(VanityArmorItems.TEAM_DYE.get());

        // Bow 附魔
        IntrinsicTagAppender<Item> durabilityEnchantable = tag(ItemTags.DURABILITY_ENCHANTABLE);
        BowItems.ITEMS.getEntries().forEach(item -> {
            Item value = item.get();
            durabilityEnchantable.add(value);
            tag(ItemTags.BOW_ENCHANTABLE).add(value);
            tag(Tags.Items.RANGED_WEAPON_TOOLS).add(value);
            weapons.add(value);
            tag(Tags.Items.TOOLS_BOW).add(value);
        });
        //  FishingPole 附魔
        FishingPoleItems.acceptTag(tag(ItemTags.FISHING_ENCHANTABLE));
        // Sword 附魔
        SwordItems.ITEMS.getEntries().forEach(item -> {
            Item value = item.get();
            tag(ItemTags.SHARP_WEAPON_ENCHANTABLE).add(value);
            tag(ItemTags.SWORD_ENCHANTABLE).add(value);
            durabilityEnchantable.add(value);
            tag(ItemTags.WEAPON_ENCHANTABLE).add(value);
            tag(Tags.Items.MELEE_WEAPON_TOOLS).add(value);
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
                .add(NatureBlocks.PEARL_LOG_BLOCKS.getAllItems().stream().map(Supplier::get).toArray(Item[]::new));

        TESummonItems.ITEMS.getEntries().forEach(item -> tag(ModTags.Items.SUMMONER_WEAPON).add(item.get()));

        tag(ModTags.Items.ABLE_TO_DESTROY_ALTAR).add(
                HammerItems.PWNHAMMER.get()
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
        tag(Tags.Items.MUSIC_DISCS).add(ModItems.ALPHA.get());
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
                QuestedFishes.TR_CLOWNFISH.get(),
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
                FoodItems.TR_SALMON.get(),
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

        IntrinsicTagAppender<Item> wip = tag(LibTags.Items.WIP);
        wip.add(
                ModItems.WHOOPIE_CUSHION.get(),
                ConsumableItems.GOODIE_BAG.get(),
                PaintItems.ECHO_COATING.get(),
                FoodItems.BOULDER_BREAD.get(),
                FunctionalBlocks.ANNOUNCEMENT_BOX.asItem(),
                TEItems.HOUSE_DETECTOR.get(),
                ToolItems.TARGET_DUMMY.get(),
                DrillItems.DRAX.get(),
                ModItems.TOKYO_TEDDY_BEAR.get(),
                ModItems.ICE_TOFU_BRICK.get(),
                ModItems.FAILED_SKULL.get(),
                ModItems.KIND_MISIDE_RING.get(),
                ModItems.FERTILE_SINGULARITY.get(),
                ModItems.PERPLEXED_CAT_MEDAL.get(),
                ModItems.CANDY_SWORD.get(),
                ModItems.PULSAR.get(),
                ModItems.MYSTERIOUS_NOTE.get(),
                ModItems.HARDMODE_CONVERTOR.get()
        );
        Consumer<DeferredHolder<Item, ? extends Item>> wipAction = item -> wip.add(item.get());
        MinecartItems.ITEMS.getEntries().forEach(wipAction);
        //TODO 枪！
//        TGItems.ITEM_GUNS.getEntries().forEach(wipAction);
//        TGItems.ITEM_BULLETS.getEntries().forEach(wipAction);
        LightPetItems.ITEMS.getEntries().forEach(wipAction);
        TMItems.ITEMS.getEntries().forEach(wipAction);
    }
}
