package org.confluence.mod.common.data.gen;

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
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.block.OreBlocks;
import org.confluence.mod.common.init.item.*;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.common.init.TCTags;
import org.confluence.terra_guns.common.init.TGItems;
import org.confluence.terraentity.init.TETags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;


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
        tag(ModTags.Items.DESERT_FOSSIL).add(NatureBlocks.DESERT_FOSSIL.get().asItem());
        tag(ModTags.Items.SLUSH).add(NatureBlocks.SLUSH.get().asItem());
        tag(ModTags.Items.MARINE_GRAVEL).add(NatureBlocks.MARINE_GRAVEL.get().asItem());
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
        tag(ModTags.Items.SHADOW_SCALE_AND_TISSUE_SAMPLE).add(
                MaterialItems.SHADOW_SCALE.get(),
                MaterialItems.TISSUE_SAMPLE.get()
        );

        // 可烧的木材
        tag(ModTags.Items.WOODEN_COMBUSTIBLES).add(
                // 乌木
                NatureBlocks.EBONY_LOG_BLOCKS.getPlanks().asItem(),
                NatureBlocks.EBONY_LOG_BLOCKS.getLog().asItem(),
                NatureBlocks.EBONY_LOG_BLOCKS.getStrippedLog().asItem(),
                NatureBlocks.EBONY_LOG_BLOCKS.getStrippedWood().asItem(),
                NatureBlocks.EBONY_LOG_BLOCKS.getDoor().asItem(),
                NatureBlocks.EBONY_LOG_BLOCKS.getSign().asItem(),
                NatureBlocks.EBONY_LOG_BLOCKS.getStairs().asItem(),
                NatureBlocks.EBONY_LOG_BLOCKS.getSlab().asItem(),
                NatureBlocks.EBONY_LOG_BLOCKS.getTrapdoor().asItem(),
                NatureBlocks.EBONY_LOG_BLOCKS.getPressurePlate().asItem(),
                NatureBlocks.EBONY_LOG_BLOCKS.getWood().asItem(),
                NatureBlocks.EBONY_LOG_BLOCKS.getLeaves().asItem(),
                NatureBlocks.EBONY_LOG_BLOCKS.getFence().asItem(),
                NatureBlocks.EBONY_LOG_BLOCKS.getFenceGate().asItem(),

                // 珍珠木
                NatureBlocks.PEARL_LOG_BLOCKS.getButton().asItem(),
                NatureBlocks.PEARL_LOG_BLOCKS.getPlanks().asItem(),
                NatureBlocks.PEARL_LOG_BLOCKS.getLog().asItem(),
                NatureBlocks.PEARL_LOG_BLOCKS.getStrippedLog().asItem(),
                NatureBlocks.PEARL_LOG_BLOCKS.getStrippedWood().asItem(),
                NatureBlocks.PEARL_LOG_BLOCKS.getDoor().asItem(),
                NatureBlocks.PEARL_LOG_BLOCKS.getSign().asItem(),
                NatureBlocks.PEARL_LOG_BLOCKS.getStairs().asItem(),
                NatureBlocks.PEARL_LOG_BLOCKS.getSlab().asItem(),
                NatureBlocks.PEARL_LOG_BLOCKS.getTrapdoor().asItem(),
                NatureBlocks.PEARL_LOG_BLOCKS.getPressurePlate().asItem(),
                NatureBlocks.PEARL_LOG_BLOCKS.getWood().asItem(),
                NatureBlocks.PEARL_LOG_BLOCKS.getLeaves().asItem(),
                NatureBlocks.PEARL_LOG_BLOCKS.getFence().asItem(),
                NatureBlocks.PEARL_LOG_BLOCKS.getFenceGate().asItem(),

                // 暗影木
                NatureBlocks.SHADOW_LOG_BLOCKS.getButton().asItem(),
                NatureBlocks.SHADOW_LOG_BLOCKS.getPlanks().asItem(),
                NatureBlocks.SHADOW_LOG_BLOCKS.getLog().asItem(),
                NatureBlocks.SHADOW_LOG_BLOCKS.getStrippedLog().asItem(),
                NatureBlocks.SHADOW_LOG_BLOCKS.getStrippedWood().asItem(),
                NatureBlocks.SHADOW_LOG_BLOCKS.getDoor().asItem(),
                NatureBlocks.SHADOW_LOG_BLOCKS.getTrapdoor().asItem(),
                NatureBlocks.SHADOW_LOG_BLOCKS.getSign().asItem(),
                NatureBlocks.SHADOW_LOG_BLOCKS.getStairs().asItem(),
                NatureBlocks.SHADOW_LOG_BLOCKS.getSlab().asItem(),
                NatureBlocks.SHADOW_LOG_BLOCKS.getWood().asItem(),
                NatureBlocks.SHADOW_LOG_BLOCKS.getFence().asItem(),
                NatureBlocks.SHADOW_LOG_BLOCKS.getFenceGate().asItem(),
                NatureBlocks.SHADOW_LOG_BLOCKS.getPressurePlate().asItem(),

                // 沙漠风情木
                NatureBlocks.PALM_LOG_BLOCKS.getButton().asItem(),
                NatureBlocks.PALM_LOG_BLOCKS.getPlanks().asItem(),
                NatureBlocks.PALM_LOG_BLOCKS.getLog().asItem(),
                NatureBlocks.PALM_LOG_BLOCKS.getDoor().asItem(),
                NatureBlocks.PALM_LOG_BLOCKS.getTrapdoor().asItem(),
                NatureBlocks.PALM_LOG_BLOCKS.getSign().asItem(),
                NatureBlocks.PALM_LOG_BLOCKS.getStairs().asItem(),
                NatureBlocks.PALM_LOG_BLOCKS.getSlab().asItem(),
                NatureBlocks.PALM_LOG_BLOCKS.getWood().asItem(),
                NatureBlocks.PALM_LOG_BLOCKS.getStrippedLog().asItem(),
                NatureBlocks.PALM_LOG_BLOCKS.getStrippedWood().asItem(),
                NatureBlocks.PALM_LOG_BLOCKS.getFence().asItem(),
                NatureBlocks.PALM_LOG_BLOCKS.getFenceGate().asItem(),
                NatureBlocks.PALM_LOG_BLOCKS.getPressurePlate().asItem(),

                // 猴面包木
                NatureBlocks.BAOBAB_LOG_BLOCKS.getButton().asItem(),
                NatureBlocks.BAOBAB_LOG_BLOCKS.getPlanks().asItem(),
                NatureBlocks.BAOBAB_LOG_BLOCKS.getLog().asItem(),
                NatureBlocks.BAOBAB_LOG_BLOCKS.getStrippedLog().asItem(),
                NatureBlocks.BAOBAB_LOG_BLOCKS.getStrippedWood().asItem(),
                NatureBlocks.BAOBAB_LOG_BLOCKS.getDoor().asItem(),
                NatureBlocks.BAOBAB_LOG_BLOCKS.getTrapdoor().asItem(),
                NatureBlocks.BAOBAB_LOG_BLOCKS.getSign().asItem(),
                NatureBlocks.BAOBAB_LOG_BLOCKS.getStairs().asItem(),
                NatureBlocks.BAOBAB_LOG_BLOCKS.getSlab().asItem(),
                NatureBlocks.BAOBAB_LOG_BLOCKS.getWood().asItem(),
                NatureBlocks.BAOBAB_LOG_BLOCKS.getFence().asItem(),
                NatureBlocks.BAOBAB_LOG_BLOCKS.getFenceGate().asItem(),
                NatureBlocks.BAOBAB_LOG_BLOCKS.getPressurePlate().asItem(),

                // 黄柳木
                NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getButton().asItem(),
                NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getPlanks().asItem(),
                NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getLog().asItem(),
                NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getStrippedLog().asItem(),
                NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getStrippedWood().asItem(),
                NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getDoor().asItem(),
                NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getTrapdoor().asItem(),
                NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getSign().asItem(),
                NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getStairs().asItem(),
                NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getSlab().asItem(),
                NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getWood().asItem(),
                NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getFence().asItem(),
                NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getFenceGate().asItem(),
                NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getPressurePlate().asItem(),

                // 生命木
                NatureBlocks.LIVING_LOG_BLOCKS.getButton().asItem(),
                NatureBlocks.LIVING_LOG_BLOCKS.getPlanks().asItem(),
                NatureBlocks.LIVING_LOG_BLOCKS.getLog().asItem(),
                NatureBlocks.LIVING_LOG_BLOCKS.getStrippedLog().asItem(),
                NatureBlocks.LIVING_LOG_BLOCKS.getStrippedWood().asItem(),
                NatureBlocks.LIVING_LOG_BLOCKS.getDoor().asItem(),
                NatureBlocks.LIVING_LOG_BLOCKS.getTrapdoor().asItem(),
                NatureBlocks.LIVING_LOG_BLOCKS.getSign().asItem(),
                NatureBlocks.LIVING_LOG_BLOCKS.getStairs().asItem(),
                NatureBlocks.LIVING_LOG_BLOCKS.getSlab().asItem(),
                NatureBlocks.LIVING_LOG_BLOCKS.getWood().asItem(),
                NatureBlocks.LIVING_LOG_BLOCKS.getFence().asItem(),
                NatureBlocks.LIVING_LOG_BLOCKS.getFenceGate().asItem(),
                NatureBlocks.LIVING_LOG_BLOCKS.getPressurePlate().asItem()
        );
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
                NatureBlocks.SHADOW_SAPLING.get().asItem(),
                NatureBlocks.EBONY_SAPLING.get().asItem(),
                NatureBlocks.PALM_SAPLING.get().asItem(),
                NatureBlocks.PEARL_SAPLING.get().asItem(),
                NatureBlocks.RUBY_SAPLING.get().asItem(),
                NatureBlocks.AMBER_SAPLING.get().asItem(),
                NatureBlocks.TOPAZ_SAPLING.get().asItem(),
                NatureBlocks.EMERALD_SAPLING.get().asItem(),
                NatureBlocks.DIAMOND_SAPLING.get().asItem(),
                NatureBlocks.SAPPHIRE_SAPLING.get().asItem(),
                NatureBlocks.TR_AMETHYST_SAPLING.get().asItem(),
                NatureBlocks.ASH_SAPLING.get().asItem(),
                NatureBlocks.LIVING_SAPLING.get().asItem(),
                NatureBlocks.YELLOW_WILLOW_SAPLING.get().asItem()
        );

        tag(ModTags.Items.DEMONITE_AND_CRIMSON_INGOT).add(MaterialItems.DEMONITE_INGOT.get(), MaterialItems.TR_CRIMSON_INGOT.get());
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
        tag(ModTags.Items.FRUIT).add(
                Items.APPLE, Items.MELON_SLICE, FoodItems.APRICOT.get(),
                FoodItems.BANANA.get(), FoodItems.CHERRY.get(), FoodItems.COCONUT.get(),
                FoodItems.DRAGON_FRUIT.get(), FoodItems.GRAPE_FRUIT.get(), FoodItems.LEMON.get(),
                FoodItems.MANGO.get(), FoodItems.PEACH.get(), FoodItems.PINEAPPLE.get(),
                FoodItems.PLUM.get(), FoodItems.GRAPE.get(), FoodItems.SPICY_PEPPER.get(),
                FoodItems.STAR_FRUIT.get(), FoodItems.POMEGRANATE.get(), FoodItems.RAMBUTAN.get(),
                FoodItems.BLOOD_ORANGE.get(), FoodItems.ELDERBERRY.get(), FoodItems.BLACKCURRANT.get()
        );
        tag(ModTags.Items.COAL_ORE_SMELTING).add(
                OreBlocks.SANCTIFICATION_COAL_ORE.asItem(), OreBlocks.CORRUPTION_COAL_ORE.asItem(), OreBlocks.FLESHIFICATION_COAL_ORE.get().asItem()
        );
        tag(ModTags.Items.IRON_ORE_SMELTING).add(
                OreBlocks.SANCTIFICATION_IRON_ORE.asItem(), OreBlocks.CORRUPTION_IRON_ORE.asItem(), OreBlocks.FLESHIFICATION_IRON_ORE.get().asItem()
        );
        tag(ModTags.Items.TIN_ORE_SMELTING).add(
                OreBlocks.TIN_ORE.asItem(), OreBlocks.DEEPSLATE_TIN_ORE.asItem(), OreBlocks.SANCTIFICATION_TIN_ORE.asItem(), OreBlocks.CORRUPTION_TIN_ORE.asItem(), OreBlocks.FLESHIFICATION_TIN_ORE.get().asItem(),
                MaterialItems.RAW_TIN.get()
        );
        tag(ModTags.Items.COPPER_ORE_SMELTING).add(
                OreBlocks.SANCTIFICATION_COPPER_ORE.asItem(), OreBlocks.CORRUPTION_COPPER_ORE.asItem(), OreBlocks.FLESHIFICATION_COPPER_ORE.get().asItem()
        );
        tag(ModTags.Items.LEAD_ORE_SMELTING).add(
                OreBlocks.LEAD_ORE.asItem(), OreBlocks.DEEPSLATE_LEAD_ORE.asItem(), OreBlocks.SANCTIFICATION_LEAD_ORE.asItem(), OreBlocks.CORRUPTION_LEAD_ORE.asItem(), OreBlocks.FLESHIFICATION_LEAD_ORE.get().asItem(),
                MaterialItems.RAW_LEAD.get()
        );
        tag(ModTags.Items.SILVER_ORE_SMELTING).add(
                OreBlocks.SILVER_ORE.asItem(), OreBlocks.DEEPSLATE_SILVER_ORE.asItem(), OreBlocks.SANCTIFICATION_SILVER_ORE.asItem(), OreBlocks.CORRUPTION_SILVER_ORE.asItem(), OreBlocks.FLESHIFICATION_SILVER_ORE.get().asItem(),
                MaterialItems.RAW_SILVER.get()
        );
        tag(ModTags.Items.TUNGSTEN_ORE_SMELTING).add(
                OreBlocks.TUNGSTEN_ORE.asItem(), OreBlocks.DEEPSLATE_TUNGSTEN_ORE.asItem(), OreBlocks.SANCTIFICATION_TUNGSTEN_ORE.asItem(), OreBlocks.CORRUPTION_TUNGSTEN_ORE.asItem(), OreBlocks.FLESHIFICATION_TUNGSTEN_ORE.get().asItem(),
                MaterialItems.RAW_TUNGSTEN.get()
        );
        tag(ModTags.Items.GOLD_ORE_SMELTING).add(
                OreBlocks.SANCTIFICATION_GOLD_ORE.asItem(), OreBlocks.CORRUPTION_GOLD_ORE.asItem(), OreBlocks.FLESHIFICATION_GOLD_ORE.get().asItem()
        );
        tag(ModTags.Items.PLATINUM_ORE_SMELTING).add(
                OreBlocks.PLATINUM_ORE.asItem(), OreBlocks.DEEPSLATE_PLATINUM_ORE.asItem(), OreBlocks.SANCTIFICATION_PLATINUM_ORE.asItem(), OreBlocks.CORRUPTION_PLATINUM_ORE.asItem(), OreBlocks.FLESHIFICATION_PLATINUM_ORE.get().asItem(),
                MaterialItems.RAW_PLATINUM.get()
        );
        tag(ModTags.Items.DEMONITE_ORE_SMELTING).add(
                OreBlocks.DEMONITE_ORE.asItem(), OreBlocks.DEEPSLATE_DEMONITE_ORE.asItem(), OreBlocks.SANCTIFICATION_DEMONITE_ORE.asItem(), OreBlocks.CORRUPTION_DEMONITE_ORE.asItem(), OreBlocks.FLESHIFICATION_DEMONITE_ORE.get().asItem(),
                MaterialItems.RAW_DEMONITE.get()
        );
        tag(ModTags.Items.TR_CRIMSON_ORE_SMELTING).add(
                OreBlocks.TR_CRIMSON_ORE.asItem(), OreBlocks.DEEPSLATE_TR_CRIMSON_ORE.asItem(), OreBlocks.SANCTIFICATION_TR_CRIMSON_ORE.asItem(), OreBlocks.CORRUPTION_TR_CRIMSON_ORE.asItem(), OreBlocks.FLESHIFICATION_TR_CRIMSON_ORE.get().asItem(),
                MaterialItems.RAW_TR_CRIMSON.get()

        );
        tag(ModTags.Items.RUBY_ORE_SMELTING).add(
                OreBlocks.RUBY_ORE.asItem(), OreBlocks.DEEPSLATE_RUBY_ORE.asItem(), OreBlocks.SANCTIFICATION_RUBY_ORE.asItem(), OreBlocks.CORRUPTION_RUBY_ORE.asItem(), OreBlocks.FLESHIFICATION_RUBY_ORE.get().asItem()
        );
        tag(ModTags.Items.AMBER_ORE_SMELTING).add(
                OreBlocks.AMBER_ORE.asItem(), OreBlocks.RED_SAND_AMBER_ORE.asItem(), OreBlocks.SANCTIFICATION_AMBER_ORE.asItem(), OreBlocks.CORRUPTION_AMBER_ORE.asItem(), OreBlocks.FLESHIFICATION_AMBER_ORE.get().asItem()
        );
        tag(ModTags.Items.TOPAZ_ORE_SMELTING).add(
                OreBlocks.TOPAZ_ORE.asItem(), OreBlocks.DEEPSLATE_TOPAZ_ORE.asItem(), OreBlocks.SANCTIFICATION_TOPAZ_ORE.asItem(), OreBlocks.CORRUPTION_TOPAZ_ORE.asItem(), OreBlocks.FLESHIFICATION_TOPAZ_ORE.get().asItem()
        );
        tag(ModTags.Items.TR_EMERALD_ORE_SMELTING).add(
                OreBlocks.TR_EMERALD_ORE.asItem(), OreBlocks.DEEPSLATE_RUBY_ORE.asItem(), OreBlocks.SANCTIFICATION_RUBY_ORE.asItem(), OreBlocks.CORRUPTION_RUBY_ORE.asItem(), OreBlocks.FLESHIFICATION_RUBY_ORE.get().asItem()
        );
        tag(ModTags.Items.DIAMOND_ORE_SMELTING).add(
                OreBlocks.SANCTIFICATION_DIAMOND_ORE.asItem(), OreBlocks.CORRUPTION_DIAMOND_ORE.asItem(), OreBlocks.FLESHIFICATION_DIAMOND_ORE.get().asItem()
        );
        tag(ModTags.Items.SAPPHIRE_ORE_SMELTING).add(
                OreBlocks.SAPPHIRE_ORE.asItem(), OreBlocks.DEEPSLATE_SAPPHIRE_ORE.asItem(), OreBlocks.SANCTIFICATION_SAPPHIRE_ORE.asItem(), OreBlocks.CORRUPTION_SAPPHIRE_ORE.asItem(), OreBlocks.FLESHIFICATION_SAPPHIRE_ORE.get().asItem()
        );
        tag(ModTags.Items.TR_AMETHYST_ORE_SMELTING).add(
                OreBlocks.TR_AMETHYST_ORE.asItem(), OreBlocks.DEEPSLATE_TR_AMETHYST_ORE.asItem(), OreBlocks.SANCTIFICATION_TR_AMETHYST_ORE.asItem(), OreBlocks.CORRUPTION_TR_AMETHYST_ORE.asItem(), OreBlocks.FLESHIFICATION_TR_AMETHYST_ORE.get().asItem()
        );
        tag(ModTags.Items.REDSTONE_ORE_SMELTING).add(
                OreBlocks.SANCTIFICATION_REDSTONE_ORE.asItem(), OreBlocks.CORRUPTION_REDSTONE_ORE.asItem(), OreBlocks.FLESHIFICATION_REDSTONE_ORE.get().asItem()
        );
        tag(ItemTags.DIRT).add(
                NatureBlocks.CORRUPT_GRASS_BLOCK.get().asItem(),
                NatureBlocks.ASH_BLOCK.get().asItem(),
                NatureBlocks.TR_CRIMSON_GRASS_BLOCK.get().asItem(),
                NatureBlocks.HALLOW_GRASS_BLOCK.get().asItem(),
                NatureBlocks.ASH_GRASS_BLOCK.get().asItem(),
                NatureBlocks.MUSHROOM_GRASS_BLOCK.get().asItem(),
                NatureBlocks.JUNGLE_GRASS_BLOCK.get().asItem()
        );
        tag(ItemTags.CAT_FOOD).add(
                FoodItems.SEA_BASS.get(),
                FoodItems.ATLANTIC_COD.get(),
                FoodItems.DAMSEL_FISH.get(),
                FoodItems.TROUT.get(),
                FoodItems.TUNA.get(),
                FoodItems.PARTIAL_MOUTH_FISH.get()
        );
        tag(ItemTags.PLANKS).add(
                NatureBlocks.EBONY_LOG_BLOCKS.getPlanks().asItem(), NatureBlocks.SHADOW_LOG_BLOCKS.getPlanks().asItem(), NatureBlocks.PALM_LOG_BLOCKS.getPlanks().asItem(),
                NatureBlocks.SPOOKY_LOG_BLOCKS.getPlanks().asItem(), NatureBlocks.ASH_LOG_BLOCKS.getPlanks().asItem(), NatureBlocks.PEARL_LOG_BLOCKS.getPlanks().asItem(),
                NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getPlanks().asItem(), NatureBlocks.LIVING_LOG_BLOCKS.getPlanks().asItem(), NatureBlocks.BAOBAB_LOG_BLOCKS.getPlanks().asItem()
        );
        tag(ItemTags.LOGS).add(
                NatureBlocks.EBONY_LOG_BLOCKS.getLog().asItem(), NatureBlocks.SHADOW_LOG_BLOCKS.getLog().asItem(), NatureBlocks.PALM_LOG_BLOCKS.getLog().asItem(),
                NatureBlocks.ASH_LOG_BLOCKS.getLog().asItem(), NatureBlocks.PEARL_LOG_BLOCKS.getLog().asItem(),
                NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getLog().asItem(), NatureBlocks.LIVING_LOG_BLOCKS.getLog().asItem(), NatureBlocks.BAOBAB_LOG_BLOCKS.getLog().asItem()
        );
        tag(ItemTags.WOODEN_SLABS).add(
                NatureBlocks.EBONY_LOG_BLOCKS.getSlab().asItem(), NatureBlocks.SHADOW_LOG_BLOCKS.getSlab().asItem(), NatureBlocks.PALM_LOG_BLOCKS.getSlab().asItem(),
                NatureBlocks.SPOOKY_LOG_BLOCKS.getSlab().asItem(), NatureBlocks.ASH_LOG_BLOCKS.getSlab().asItem(), NatureBlocks.PEARL_LOG_BLOCKS.getSlab().asItem(),
                NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getSlab().asItem(), NatureBlocks.LIVING_LOG_BLOCKS.getSlab().asItem(), NatureBlocks.BAOBAB_LOG_BLOCKS.getSlab().asItem()
        );
        tag(ItemTags.WOODEN_FENCES).add(
                NatureBlocks.EBONY_LOG_BLOCKS.getFence().asItem(), NatureBlocks.SHADOW_LOG_BLOCKS.getFence().asItem(), NatureBlocks.PALM_LOG_BLOCKS.getFence().asItem(),
                NatureBlocks.SPOOKY_LOG_BLOCKS.getFence().asItem(), NatureBlocks.ASH_LOG_BLOCKS.getFence().asItem(), NatureBlocks.PEARL_LOG_BLOCKS.getFence().asItem(),
                NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getFence().asItem(), NatureBlocks.LIVING_LOG_BLOCKS.getFence().asItem(), NatureBlocks.BAOBAB_LOG_BLOCKS.getFence().asItem()
        );
        tag(ItemTags.WOODEN_DOORS).add(
                NatureBlocks.EBONY_LOG_BLOCKS.getDoor().asItem(), NatureBlocks.SHADOW_LOG_BLOCKS.getDoor().asItem(), NatureBlocks.PALM_LOG_BLOCKS.getDoor().asItem(),
                NatureBlocks.SPOOKY_LOG_BLOCKS.getDoor().asItem(), NatureBlocks.ASH_LOG_BLOCKS.getDoor().asItem(), NatureBlocks.PEARL_LOG_BLOCKS.getDoor().asItem(),
                NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getDoor().asItem(), NatureBlocks.LIVING_LOG_BLOCKS.getDoor().asItem(), NatureBlocks.BAOBAB_LOG_BLOCKS.getDoor().asItem()
        );
        tag(ItemTags.WOODEN_TRAPDOORS).add(
                NatureBlocks.EBONY_LOG_BLOCKS.getTrapdoor().asItem(), NatureBlocks.SHADOW_LOG_BLOCKS.getTrapdoor().asItem(), NatureBlocks.PALM_LOG_BLOCKS.getTrapdoor().asItem(),
                NatureBlocks.SPOOKY_LOG_BLOCKS.getTrapdoor().asItem(), NatureBlocks.ASH_LOG_BLOCKS.getTrapdoor().asItem(), NatureBlocks.PEARL_LOG_BLOCKS.getTrapdoor().asItem(),
                NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getTrapdoor().asItem(), NatureBlocks.LIVING_LOG_BLOCKS.getTrapdoor().asItem(), NatureBlocks.BAOBAB_LOG_BLOCKS.getTrapdoor().asItem()
        );
        tag(ItemTags.WOODEN_PRESSURE_PLATES).add(
                NatureBlocks.EBONY_LOG_BLOCKS.getPressurePlate().asItem(), NatureBlocks.SHADOW_LOG_BLOCKS.getPressurePlate().asItem(), NatureBlocks.PALM_LOG_BLOCKS.getPressurePlate().asItem(),
                NatureBlocks.SPOOKY_LOG_BLOCKS.getPressurePlate().asItem(), NatureBlocks.ASH_LOG_BLOCKS.getPressurePlate().asItem(), NatureBlocks.PEARL_LOG_BLOCKS.getPressurePlate().asItem(),
                NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getPressurePlate().asItem(), NatureBlocks.LIVING_LOG_BLOCKS.getPressurePlate().asItem(), NatureBlocks.BAOBAB_LOG_BLOCKS.getPressurePlate().asItem()
        );
        tag(ItemTags.WOODEN_STAIRS).add(
                NatureBlocks.EBONY_LOG_BLOCKS.getStairs().asItem(), NatureBlocks.SHADOW_LOG_BLOCKS.getStairs().asItem(), NatureBlocks.PALM_LOG_BLOCKS.getStairs().asItem(),
                NatureBlocks.SPOOKY_LOG_BLOCKS.getStairs().asItem(), NatureBlocks.ASH_LOG_BLOCKS.getStairs().asItem(), NatureBlocks.PEARL_LOG_BLOCKS.getStairs().asItem(),
                NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getStairs().asItem(), NatureBlocks.LIVING_LOG_BLOCKS.getStairs().asItem(), NatureBlocks.BAOBAB_LOG_BLOCKS.getStairs().asItem()
        );
        tag(ModTags.Items.CHARCOAL_CAN_BE_BURNED).add(
                NatureBlocks.EBONY_LOG_BLOCKS.getLog().asItem(), NatureBlocks.SHADOW_LOG_BLOCKS.getLog().asItem(), NatureBlocks.PALM_LOG_BLOCKS.getLog().asItem(),
                NatureBlocks.ASH_LOG_BLOCKS.getLog().asItem(), NatureBlocks.PEARL_LOG_BLOCKS.getLog().asItem(),
                NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getLog().asItem(), NatureBlocks.LIVING_LOG_BLOCKS.getLog().asItem(), NatureBlocks.BAOBAB_LOG_BLOCKS.getLog().asItem()
        );
        // neoforge
        tag(Tags.Items.BONES).add(
                MaterialItems.ROTTEN_BONE.get(),
                MaterialItems.VERTEBRA.get()
        );
        tag(Tags.Items.BRICKS).add(
                DecorativeBlocks.TR_COPPER_BRICKS.get().asItem(),
                DecorativeBlocks.TR_CRIMSON_ORE_BRICKS.get().asItem(),
                DecorativeBlocks.TR_CRIMSON_ROCK_BRICKS.get().asItem(),
                DecorativeBlocks.TR_GOLD_BRICKS.get().asItem(),
                DecorativeBlocks.TR_IRON_BRICKS.get().asItem(),
                DecorativeBlocks.TR_STONE_BRICKS.get().asItem(),
                DecorativeBlocks.DEMONITE_ORE_BRICKS.get().asItem(),
                DecorativeBlocks.EBONY_ROCK_BRICKS.get().asItem(),
                DecorativeBlocks.BLUE_ICE_BRICKS.get().asItem(),
                DecorativeBlocks.PACKED_ICE_BRICKS.get().asItem(),
                DecorativeBlocks.LEAD_BRICKS.get().asItem(),
                DecorativeBlocks.METEORITE_BRICKS.get().asItem(),
                DecorativeBlocks.PEARL_ROCK_BRICKS.get().asItem(),
                DecorativeBlocks.PLATINUM_BRICKS.get().asItem(),
                DecorativeBlocks.SILVER_BRICKS.get().asItem(),
                DecorativeBlocks.SNOW_BRICKS.get().asItem(),
                DecorativeBlocks.TUNGSTEN_BRICKS.get().asItem(),
                DecorativeBlocks.TR_LAVA_BRICKS.get().asItem(),
                DecorativeBlocks.TR_OBSIDIAN_BRICKS.get().asItem(),
                DecorativeBlocks.TR_OBSIDIAN_SMALL_BRICKS.get().asItem(),
                DecorativeBlocks.CRYSTAL_BLOCKS.get().asItem(),
                DecorativeBlocks.RAINBOW_BRICKS.get().asItem()
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
                MaterialItems.BLOOD_CLOT_POWDER.get()
        );
        tag(Tags.Items.DYED_WHITE).add(DecorativeBlocks.WHITE_PURE_GLASS.get().asItem());
        tag(Tags.Items.DYED_LIGHT_GRAY).add(DecorativeBlocks.LIGHT_GRAY_PURE_GLASS.get().asItem());
        tag(Tags.Items.DYED_GRAY).add(DecorativeBlocks.GRAY_PURE_GLASS.get().asItem());
        tag(Tags.Items.DYED_BLACK).add(DecorativeBlocks.BLACK_PURE_GLASS.get().asItem());
        tag(Tags.Items.DYED_BROWN).add(DecorativeBlocks.BROWN_PURE_GLASS.get().asItem());
        tag(Tags.Items.DYED_RED).add(DecorativeBlocks.RED_PURE_GLASS.get().asItem());
        tag(Tags.Items.DYED_ORANGE).add(DecorativeBlocks.ORANGE_PURE_GLASS.get().asItem());
        tag(Tags.Items.DYED_YELLOW).add(DecorativeBlocks.YELLOW_PURE_GLASS.get().asItem());
        tag(Tags.Items.DYED_LIME).add(DecorativeBlocks.LIME_PURE_GLASS.get().asItem());
        tag(Tags.Items.DYED_GREEN).add(DecorativeBlocks.GREEN_PURE_GLASS.get().asItem());
        tag(Tags.Items.DYED_CYAN).add(DecorativeBlocks.CYAN_PURE_GLASS.get().asItem());
        tag(Tags.Items.DYED_LIGHT_BLUE).add(DecorativeBlocks.LIGHT_BLUE_PURE_GLASS.get().asItem());
        tag(Tags.Items.DYED_BLUE).add(DecorativeBlocks.BLUE_PURE_GLASS.get().asItem());
        tag(Tags.Items.DYED_PURPLE).add(DecorativeBlocks.PURPLE_PURE_GLASS.get().asItem());
        tag(Tags.Items.DYED_MAGENTA).add(DecorativeBlocks.MAGENTA_PURE_GLASS.get().asItem());
        tag(Tags.Items.DYED_PINK).add(DecorativeBlocks.PINK_PURE_GLASS.get().asItem());
        tag(Tags.Items.GEMS).add(
                MaterialItems.RUBY.get(),
                MaterialItems.AMBER.get(),
                MaterialItems.TOPAZ.get(),
                MaterialItems.TR_EMERALD.get(),
                MaterialItems.SAPPHIRE.get(),
                MaterialItems.TR_AMETHYST.get()
        );

        tag(Tags.Items.GUNPOWDERS).add(
                MaterialItems.BLOOD_CLOT_POWDER.get()
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
                MaterialItems.MITHRIL_INGOT.get(),
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
                FoodItems.STELLAR_BLOSSOM_SEED.get()
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
                MaterialItems.RAW_MITHRIL.get(),
                MaterialItems.RAW_ORICHALCUM.get(),
                MaterialItems.RAW_DEMONITE.get(),
                MaterialItems.RAW_PALLADIUM.get(),
                MaterialItems.RAW_PLATINUM.get(),
                MaterialItems.RAW_SILVER.get(),
                MaterialItems.RAW_TIN.get(),
                MaterialItems.RAW_TUNGSTEN.get(),
                MaterialItems.RAW_TR_CRIMSON.get()
        );

        BowItems.acceptTag(tag(Tags.Items.TOOLS_BOW));
        PaintItems.acceptTag(tag(Tags.Items.DYED));
        ArrowItems.acceptTag(tag(ItemTags.ARROWS));
        HammerItems.acceptTag(tag(ModTags.Items.HAMMER));
        IntrinsicTagAppender<Item> pickaxes = tag(ItemTags.PICKAXES);
        PickaxeItems.acceptTag(pickaxes);
        PickaxeAxeItems.acceptTag(pickaxes);
        IntrinsicTagAppender<Item> axes = tag(ItemTags.AXES);
        AxeItems.acceptTag(axes);
        PickaxeAxeItems.acceptTag(axes);

        SwordItems.acceptTag(tag(Tags.Items.MELEE_WEAPON_TOOLS));
        ManaWeaponItems.acceptTag(tag(ModTags.Items.MANA_WEAPON));
        BowItems.acceptTag(tag(Tags.Items.RANGED_WEAPON_TOOLS));
        IntrinsicTagAppender<Item> mining_tool_tools = tag(Tags.Items.MINING_TOOL_TOOLS);
        PickaxeItems.acceptTag(mining_tool_tools);
        PickaxeAxeItems.acceptTag(mining_tool_tools);
        AxeItems.acceptTag(mining_tool_tools);
        HammerItems.acceptTag(mining_tool_tools);
        DrillItems.acceptTag(mining_tool_tools);
        IntrinsicTagAppender<Item> prefix_universal_only = tag(ModTags.Items.PREFIX_UNIVERSAL_ONLY);
        DrillItems.acceptTag(prefix_universal_only);
        BoomerangItems.acceptTag(prefix_universal_only);

        copy(BlockTags.RAILS, ItemTags.RAILS);
        copy(Tags.Blocks.ORES, Tags.Items.ORES);
        copy(Tags.Blocks.ORE_RATES_DENSE, Tags.Items.ORE_RATES_DENSE);
        copy(Tags.Blocks.ORE_BEARING_GROUND_NETHERRACK, Tags.Items.ORE_BEARING_GROUND_NETHERRACK);
        copy(Tags.Blocks.OBSIDIANS, Tags.Items.OBSIDIANS);
        copy(Tags.Blocks.HIDDEN_FROM_RECIPE_VIEWERS, Tags.Items.HIDDEN_FROM_RECIPE_VIEWERS);
        copy(Tags.Blocks.GRAVELS, Tags.Items.GRAVELS);
        copy(Tags.Blocks.GLASS_BLOCKS_CHEAP, Tags.Items.GLASS_BLOCKS_CHEAP);
        copy(Tags.Blocks.GLASS_BLOCKS_COLORLESS, Tags.Items.GLASS_BLOCKS_COLORLESS);
        copy(Tags.Blocks.GLASS_BLOCKS, Tags.Items.GLASS_BLOCKS);
        copy(Tags.Blocks.FENCE_GATES_WOODEN, Tags.Items.FENCE_GATES_WOODEN);
        copy(Tags.Blocks.FENCE_GATES, Tags.Items.FENCE_GATES);
        copy(Tags.Blocks.FENCES_WOODEN, Tags.Items.FENCES_WOODEN);
        copy(Tags.Blocks.FENCES, Tags.Items.FENCES);
        copy(Tags.Blocks.CHAINS, Tags.Items.CHAINS);
        copy(Tags.Blocks.DYED, Tags.Items.DYED);
        copy(Tags.Blocks.COBBLESTONES_NORMAL, Tags.Items.COBBLESTONES_NORMAL);
        copy(Tags.Blocks.COBBLESTONES, Tags.Items.COBBLESTONES);
        copy(Tags.Blocks.ORES_COAL, Tags.Items.ORES_COAL);
        copy(Tags.Blocks.ORES_COPPER, Tags.Items.ORES_COPPER);
        copy(Tags.Blocks.ORES_DIAMOND, Tags.Items.ORES_DIAMOND);
        copy(Tags.Blocks.ORES_EMERALD, Tags.Items.ORES_EMERALD);
        copy(Tags.Blocks.ORES_GOLD, Tags.Items.ORES_GOLD);
        copy(Tags.Blocks.ORES_IRON, Tags.Items.ORES_IRON);
        copy(Tags.Blocks.ORES_LAPIS, Tags.Items.ORES_LAPIS);
        copy(Tags.Blocks.ORES_REDSTONE, Tags.Items.ORES_REDSTONE);
        copy(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE, Tags.Items.ORES_IN_GROUND_DEEPSLATE);
        copy(Tags.Blocks.ORES_IN_GROUND_NETHERRACK, Tags.Items.ORES_IN_GROUND_NETHERRACK);
        copy(Tags.Blocks.ORES_IN_GROUND_STONE, Tags.Items.ORES_IN_GROUND_STONE);
        copy(Tags.Blocks.PLAYER_WORKSTATIONS_CRAFTING_TABLES, Tags.Items.PLAYER_WORKSTATIONS_CRAFTING_TABLES);
        copy(Tags.Blocks.PLAYER_WORKSTATIONS_FURNACES, Tags.Items.PLAYER_WORKSTATIONS_FURNACES);
        copy(Tags.Blocks.ROPES, Tags.Items.ROPES);
        copy(Tags.Blocks.SANDS, Tags.Items.SANDS);
        copy(Tags.Blocks.SANDSTONE_BLOCKS, Tags.Items.SANDSTONE_BLOCKS);
        copy(Tags.Blocks.SANDSTONE_RED_BLOCKS, Tags.Items.SANDSTONE_RED_BLOCKS);
        tag(ModTags.Items.COINS).add(
                ModItems.COPPER_COIN.get(),
                ModItems.SILVER_COIN.get(),
                ModItems.GOLDEN_COIN.get(),
                ModItems.PLATINUM_COIN.get(),
                ModItems.EMERALD_COIN.get()
        );
        tag(TETags.Items.HONEY_TRANSLATION_BUCKET).add(ToolItems.HONEY_BUCKET.get());
        tag(TETags.Items.HONEY_TRANSLATION_NOT_CONSUMED).add(ToolItems.BOTTOMLESS_HONEY_BUCKET.get());
        tag(ModTags.Items.HARDMODE_ORES).add(
                MaterialItems.RAW_COBALT.get(),
                MaterialItems.RAW_PALLADIUM.get(),
                MaterialItems.RAW_MITHRIL.get(),
                MaterialItems.RAW_ORICHALCUM.get(),
                MaterialItems.RAW_ADAMANTITE.get(),
                MaterialItems.RAW_TITANIUM.get()
        );
        tag(ModTags.Items.WINGS).add(
                AccessoryItems.FLEDGLING_WINGS.get(),
                TCItems.CELESTIAL_STARBOARD.get()
        );
        tag(ModTags.Items.BOSS_SUMMING).add(
                ConsumableItems.SUSPICIOUS_LOOKING_EYE.get(),
                ConsumableItems.SLIME_CROWN.get()
        );
        tag(ModTags.Items.INGOTS_TIN).add(MaterialItems.TIN_INGOT.get());
        tag(ModTags.Items.INGOTS_SILVER).add(MaterialItems.SILVER_INGOT.get());
        tag(ModTags.Items.INGOTS_TUNGSTEN).add(MaterialItems.TUNGSTEN_INGOT.get());
        tag(ModTags.Items.INGOTS_PLATINUM).add(MaterialItems.PLATINUM_INGOT.get());
        tag(ModTags.Items.MOSS_ITEM).add(
                NatureBlocks.BROWN_MOSS.get().asItem(),
                NatureBlocks.BROWN_MOSS.get().asItem(),
                NatureBlocks.RED_MOSS.get().asItem(),
                NatureBlocks.BLUE_MOSS.get().asItem(),
                NatureBlocks.PURPLE_MOSS.get().asItem(),
                NatureBlocks.LAVA_MOSS.get().asItem(),
                NatureBlocks.KRYPTON_MOSS.get().asItem(),
                NatureBlocks.XENON_MOSS.get().asItem(),
                NatureBlocks.ARGON_MOSS.get().asItem(),
                NatureBlocks.NEON_MOSS.get().asItem(),
                NatureBlocks.HELIUM_MOSS.get().asItem(),
                NatureBlocks.GROWING_MUSHROOM_MOSS.get().asItem()
        );
        // 农作物掉落提升 再生法杖/再生之斧
        tag(ModTags.Items.CROP_FORTUNE).add(AxeItems.STAFF_OF_REGROWTH.get(), AxeItems.DRILL_OF_REGROWTH.get());
        // 速发弓：恶魔弓、肌腱弓
        tag(ModTags.Items.FAST_BOW).add(
                BowItems.DEMON_BOW.get(),
                BowItems.TENDON_BOW.get(),
                BowItems.DAEDALUS_STORM_BOW.get(),
                BowItems.MOLTEN_FURY.asItem()
        );

        AccessoryItems.acceptTag(tag(TCTags.ACCESSORY));
        IntrinsicTagAppender<Item> ammo = tag(ModTags.Items.AMMO);
        ammo.addTag(ItemTags.ARROWS);
        ammo.add(Items.FIREWORK_ROCKET);
        TGItems.ITEM_BULLETS.getEntries().forEach(item -> ammo.add(item.get()));
        IntrinsicTagAppender<Item> dye = tag(ModTags.Items.DYE);
        VanityArmorItems.DYE_ITEMS.forEach(dye::add);

        // Bow 附魔
        BowItems.ITEMS.getEntries().forEach(item -> {
            tag(ItemTags.DURABILITY_ENCHANTABLE).add(item.get());
            tag(ItemTags.WEAPON_ENCHANTABLE).add(item.get());
            tag(ItemTags.BOW_ENCHANTABLE).add(item.get());
        });
        //  FishingPole 附魔
        FishingPoleItems.ITEMS.getEntries().forEach(item -> {
            tag(ItemTags.FISHING_ENCHANTABLE).add(item.get());
        });
        // Sword 附魔
        SwordItems.ITEMS.getEntries().forEach(item -> {
            tag(ItemTags.SHARP_WEAPON_ENCHANTABLE).add(item.get());
            tag(ItemTags.SWORD_ENCHANTABLE).add(item.get());
            tag(ItemTags.DURABILITY_ENCHANTABLE).add(item.get());
            tag(ItemTags.WEAPON_ENCHANTABLE).add(item.get());
        });

        // Tool 附魔
        ToolItems.ITEMS.getEntries().forEach(item -> {
            tag(ItemTags.DURABILITY_ENCHANTABLE).add(item.get());


        });

        // Armor 附魔
        ArmorItems.ITEMS.getEntries().forEach(item -> {
            if (item.get() instanceof ArmorItem armor) {
                tag(ItemTags.DURABILITY_ENCHANTABLE).add(armor);
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
    }
}
