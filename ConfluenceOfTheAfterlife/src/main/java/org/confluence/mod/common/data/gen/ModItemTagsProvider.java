package org.confluence.mod.common.data.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.block.OreBlocks;
import org.confluence.mod.common.init.item.*;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.common.init.TCTags;
import org.confluence.terraentity.init.TETags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, CompletableFuture<TagLookup<Block>> b, @Nullable ExistingFileHelper helper) {
        super(output, provider, b, Confluence.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        HookItems.acceptTag(tag(ModTags.Items.HOOK));
        IntrinsicTagAppender<Item> minecart = tag(ModTags.Items.MINECART);
        minecart.add(Items.MINECART);
        MinecartItems.ITEMS.getEntries().forEach(item -> minecart.add(item.get()));
        tag(ModTags.Items.PROVIDE_MANA).add(ModItems.STAR.get(), ModItems.SOUL_CAKE.get(), ModItems.SUGAR_PLUM.get());
        tag(ModTags.Items.PROVIDE_LIFE).add(ModItems.HEART.get(), ModItems.CANDY_APPLE.get(), ModItems.CANDY_CANE.get());
        tag(ModTags.Items.DESERT_FOSSIL).add(NatureBlocks.DESERT_FOSSIL.get().asItem());
        tag(ModTags.Items.GRAVEL).add(Blocks.GRAVEL.asItem());
        tag(ModTags.Items.SLUSH).add(NatureBlocks.SLUSH.get().asItem());
        tag(ModTags.Items.MARINE_GRAVEL).add(NatureBlocks.MARINE_GRAVEL.get().asItem());
        tag(ModTags.Items.JUNK).add(Blocks.LILY_PAD.asItem(), Items.LEATHER_BOOTS, Blocks.SEAGRASS.asItem());
        tag(ModTags.Items.CORAL).add(Blocks.TUBE_CORAL.asItem(), Blocks.TUBE_CORAL_FAN.asItem(), Blocks.TUBE_CORAL_BLOCK.asItem(), Blocks.BRAIN_CORAL.asItem(), Blocks.BRAIN_CORAL_FAN.asItem(), Blocks.BRAIN_CORAL_BLOCK.asItem(),
                Blocks.BUBBLE_CORAL.asItem(), Blocks.BUBBLE_CORAL_FAN.asItem(), Blocks.BUBBLE_CORAL_BLOCK.asItem(), Blocks.FIRE_CORAL.asItem(), Blocks.FIRE_CORAL_FAN.asItem(), Blocks.FIRE_CORAL_BLOCK.asItem(), Blocks.HORN_CORAL.asItem(), Blocks.HORN_CORAL_FAN.asItem(), Blocks.HORN_CORAL_BLOCK.asItem(),
                Blocks.DEAD_TUBE_CORAL.asItem(), Blocks.DEAD_TUBE_CORAL_FAN.asItem(), Blocks.DEAD_TUBE_CORAL_BLOCK.asItem(), Blocks.DEAD_BRAIN_CORAL.asItem(), Blocks.DEAD_BRAIN_CORAL_FAN.asItem(), Blocks.DEAD_BRAIN_CORAL_BLOCK.asItem(),
                Blocks.DEAD_BUBBLE_CORAL.asItem(), Blocks.DEAD_BUBBLE_CORAL_FAN.asItem(), Blocks.DEAD_BUBBLE_CORAL_BLOCK.asItem(), Blocks.DEAD_FIRE_CORAL.asItem(), Blocks.DEAD_FIRE_CORAL_FAN.asItem(), Blocks.DEAD_FIRE_CORAL_BLOCK.asItem(), Blocks.DEAD_HORN_CORAL.asItem(), Blocks.DEAD_HORN_CORAL_FAN.asItem(), Blocks.DEAD_HORN_CORAL_BLOCK.asItem());
        tag(ModTags.Items.TR_PLANKS).add(
                NatureBlocks.EBONY_LOG_BLOCKS.getPlanks().asItem(), NatureBlocks.SHADOW_LOG_BLOCKS.getPlanks().asItem(), NatureBlocks.PALM_LOG_BLOCKS.getPlanks().asItem(),
                NatureBlocks.SPOOKY_LOG_BLOCKS.getPlanks().asItem(), NatureBlocks.ASH_LOG_BLOCKS.getPlanks().asItem(), NatureBlocks.PEARL_LOG_BLOCKS.getPlanks().asItem(), Blocks.OAK_PLANKS.asItem(), Blocks.SPRUCE_PLANKS.asItem(),
                Blocks.ACACIA_PLANKS.asItem(), Blocks.DARK_OAK_PLANKS.asItem(), Blocks.JUNGLE_PLANKS.asItem(), Blocks.MANGROVE_PLANKS.asItem(), Blocks.CHERRY_PLANKS.asItem(), Blocks.BAMBOO_PLANKS.asItem(), Blocks.CRIMSON_PLANKS.asItem(),
                Blocks.BIRCH_PLANKS.asItem(), Blocks.WARPED_PLANKS.asItem());

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
        tag(ModTags.Items.SAPLING).add(
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
                NatureBlocks.LIVING_SAPLING.get().asItem()
        );

        // 堆肥
        tag(ModTags.Items.COMPOST).add(
                FoodItems.STELLAR_BLOSSOM_SEED.get(),
                FoodItems.CLOUDWEAVER_SEED.get(),
                FoodItems.FLOATING_WHEAT_SEED.get(),
                FoodItems.WATERLEAF_SEED.get(),
                FoodItems.FLAMEFLOWERS_SEED.get(),
                FoodItems.MOONSHINE_GRASS_SEED.get(),
                FoodItems.SHINE_ROOT_SEED.get(),
                FoodItems.SHIVERINGTHORNS_SEED.get(),
                FoodItems.DAYBLOOM_SEED.get(),
                FoodItems.DEATHWEED_SEED.get(),

                MaterialItems.WATERLEAF.get(),
                MaterialItems.FLAMEFLOWERS.get(),
                MaterialItems.MOONSHINE_GRASS.get(),
                MaterialItems.SHINE_ROOT.get(),
                MaterialItems.SHIVERINGTHORNS.get(),
                MaterialItems.DAYBLOOM.get(),
                MaterialItems.DEATHWEED.get(),

                MaterialItems.STAR_PETALS.get(),
                MaterialItems.FLOATING_WHEAT_HEADS.get(),
                MaterialItems.WEAVING_CLOUD_COTTON.get(),
                MaterialItems.CARRION.get(),
                MaterialItems.VERTEBRA.get(),

                NatureBlocks.EBONY_LOG_BLOCKS.getLeaves().asItem(),
                NatureBlocks.BAOBAB_LOG_BLOCKS.getLeaves().asItem(),
                NatureBlocks.LIVING_LOG_BLOCKS.getLeaves().asItem(),
                NatureBlocks.PALM_LOG_BLOCKS.getLeaves().asItem(),
                NatureBlocks.SHADOW_LOG_BLOCKS.getLeaves().asItem(),
                NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getLeaves().asItem(),
                NatureBlocks.PEARL_LOG_BLOCKS.getLeaves().asItem(),


                NatureBlocks.TR_CRIMSON_MUSHROOM.get().asItem(),
                NatureBlocks.EBONY_MUSHROOM.get().asItem(),
                NatureBlocks.GLOWING_MUSHROOM.get().asItem(),
                NatureBlocks.LIFE_MUSHROOM.get().asItem(),
                NatureBlocks.JUNGLE_SPORE.get().asItem(),
                NatureBlocks.JUNGLE_ROSE.get().asItem(),
                NatureBlocks.CORRUPT_GRASS.get().asItem(),
                NatureBlocks.TR_CRIMSON_GRASS.get().asItem(),
                NatureBlocks.HALLOW_GRASS.get().asItem(),
                NatureBlocks.NATURES_GIFT.get().asItem(),
                NatureBlocks.YELLOW_WILLOW_DROOPING_LEAVES.get().asItem(),
                NatureBlocks.GLOWING_MUSHROOM_DROOPING_VINE.get().asItem(),
                NatureBlocks.FOREST_DROOPING_VINE.get().asItem(),
                NatureBlocks.JUNGLE_DROOPING_VINE.get().asItem(),
                NatureBlocks.CORRUPT_DROOPING_VINE.get().asItem(),
                NatureBlocks.TR_CRIMSON_DROOPING_VINE.get().asItem(),
                NatureBlocks.HALLOW_DROOPING_VINE.get().asItem()
        );
        tag(ModTags.Items.EBONY_AND_CRIMSON_INGOT).add(MaterialItems.EBONY_INGOT.get(), MaterialItems.TR_CRIMSON_INGOT.get());
        tag(ModTags.Items.LEAD_AND_IRON).add(Items.IRON_INGOT, MaterialItems.LEAD_INGOT.get());
        IntrinsicTagAppender<Item> torch = tag(ModTags.Items.TORCH);
        torch.add(Items.TORCH, Items.SOUL_TORCH);
//        for (Torches torches : Torches.values()) torch.add(torches.item.get());
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
        tag(ModTags.Items.EBONY_ORE_SMELTING).add(
                OreBlocks.EBONY_ORE.asItem(), OreBlocks.DEEPSLATE_EBONY_ORE.asItem(), OreBlocks.SANCTIFICATION_EBONY_ORE.asItem(), OreBlocks.CORRUPTION_EBONY_ORE.asItem(), OreBlocks.FLESHIFICATION_EBONY_ORE.get().asItem(),
                MaterialItems.RAW_EBONY.get()
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

        BowItems.acceptTag(tag(Tags.Items.TOOLS_BOW));
        ArrowItems.acceptTag(tag(ItemTags.ARROWS));
        HammerItems.acceptTag(tag(ModTags.Items.HAMMER));
        IntrinsicTagAppender<Item> pickaxes = tag(ItemTags.PICKAXES);
        PickaxeItems.acceptTag(pickaxes);
        PickaxeAxeItems.acceptTag(pickaxes);
        IntrinsicTagAppender<Item> axes = tag(ItemTags.AXES);
        AxeItems.acceptTag(axes);
        PickaxeAxeItems.acceptTag(axes);

        SwordItems.acceptTag(tag(Tags.Items.MELEE_WEAPON_TOOLS));
        ManaStaffItems.acceptTag(tag(ModTags.Items.MANA_WEAPON));
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
        tag(ModTags.Items.COIN).add(ModItems.COPPER_COIN.get(), ModItems.SILVER_COIN.get(), ModItems.GOLDEN_COIN.get(), ModItems.PLATINUM_COIN.get());
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

        AccessoryItems.acceptTag(tag(TCTags.ACCESSORY));
    }
}
