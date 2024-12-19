package org.confluence.mod.common.data.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.confluence.mod.common.block.natural.LogBlockSet;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.block.OreBlocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static org.confluence.mod.Confluence.MODID;
import static org.confluence.mod.common.init.block.ModBlocks.*;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        LogBlockSet.acceptTags(this);
        OreBlocks.acceptTags(this);
        tag(ModTags.Blocks.JEWELLERY_BRANCHES_ATTACHABLE).add(NatureBlocks.STONY_LOG.get());
        tag(ModTags.Blocks.ASH_LOG_BRANCHES_ATTACHABLE).add(NatureBlocks.ASH_LOG_BLOCKS.getLog().get());
        tag(BlockTags.ANVIL).add(
            FunctionalBlocks.LEAD_ANVIL.get(),
            FunctionalBlocks.CHIPPED_LEAD_ANVIL.get(),
            FunctionalBlocks.DAMAGED_LEAD_ANVIL.get()
        );
        tag(ModTags.Blocks.VINES).add(
            Blocks.VINE,
            Blocks.WEEPING_VINES,
            Blocks.WEEPING_VINES_PLANT,
            Blocks.TWISTING_VINES,
            Blocks.TWISTING_VINES_PLANT,
            Blocks.CAVE_VINES,
            Blocks.CAVE_VINES_PLANT
        );
        tag(ModTags.Blocks.EASY_CRASH).add(
            NatureBlocks.THIN_ICE_BLOCK.get(),
            SWORD_IN_STONE.get(),
            CRACKED_BLUE_BRICK.get(),
            CRACKED_GREEN_BRICK.get(),
            CRACKED_PINK_BRICK.get(),
            CRISPY_HONEY_BLOCK.get()
        );
        tag(BlockTags.RAILS).add(FunctionalBlocks.EVER_POWERED_RAIL.get());

        tag((BlockTags.STONE_ORE_REPLACEABLES)).add(
            NatureBlocks.HARDENED_SAND_BLOCK.get(),
            NatureBlocks.RED_HARDENED_SAND_BLOCK.get(),
            NatureBlocks.EBONY_STONE.get(),
            NatureBlocks.PEARL_STONE.get(),
            NatureBlocks.TR_CRIMSON_STONE.get()
        );
        tag(BlockTags.DIRT).add(
            NatureBlocks.CORRUPT_GRASS_BLOCK.get(),
            NatureBlocks.ASH_BLOCK.get(),
            NatureBlocks.TR_CRIMSON_GRASS_BLOCK.get(),
            NatureBlocks.HALLOW_GRASS_BLOCK.get(),
            NatureBlocks.ASH_GRASS_BLOCK.get(),
            NatureBlocks.MUSHROOM_GRASS_BLOCK.get()
        );
        tag(BlockTags.SAND).add(
            NatureBlocks.TR_CRIMSON_SAND.get(),
            NatureBlocks.EBONY_SAND.get(),
            NatureBlocks.PEARL_SAND.get()
        );
        tag((BlockTags.ICE)).add(
            NatureBlocks.RED_ICE.get(),
            NatureBlocks.RED_PACKED_ICE.get(),
            NatureBlocks.PINK_ICE.get(),
            NatureBlocks.PINK_PACKED_ICE.get(),
            NatureBlocks.PURPLE_ICE.get(),
            NatureBlocks.PURPLE_PACKED_ICE.get()
        );
        /*IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block> mineableWithHoe = tag(BlockTags.MINEABLE_WITH_HOE);
        mineableWithHoe.add(
        );
         */
        // 镐子
        IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block> mineableWithPickaxe = tag(BlockTags.MINEABLE_WITH_PICKAXE);
        mineableWithPickaxe.add(
            DecorativeBlocks.BIG_RUBY_BLOCK.get(),
            DecorativeBlocks.BIG_AMBER_BLOCK.get(),
            DecorativeBlocks.BIG_TOPAZ_BLOCK.get(),
            DecorativeBlocks.BIG_SAPPHIRE_BLOCK.get(),
            DecorativeBlocks.BIG_TR_AMETHYST_BLOCK.get(),
            DecorativeBlocks.BIG_TR_EMERALD_BLOCK.get(),
            DecorativeBlocks.SNOW_BRICKS.get(),
            DecorativeBlocks.TR_COPPER_BRICKS.get(),
            DecorativeBlocks.TR_COPPER_PLATE.get(),
            DecorativeBlocks.TIN_BRICKS.get(),
            DecorativeBlocks.TIN_PLATE.get(),
            DecorativeBlocks.TR_IRON_BRICKS.get(),
            DecorativeBlocks.LEAD_BRICKS.get(),
            DecorativeBlocks.SILVER_BRICKS.get(),
            DecorativeBlocks.TUNGSTEN_BRICKS.get(),
            DecorativeBlocks.TR_GOLD_BRICKS.get(),
            DecorativeBlocks.PLATINUM_BRICKS.get(),
            DecorativeBlocks.EBONY_ORE_BRICKS.get(),
            DecorativeBlocks.EBONY_ROCK_BRICKS.get(),
            DecorativeBlocks.METEORITE_BRICKS.get(),
            DecorativeBlocks.TR_CRIMSON_ORE_BRICKS.get(),
            DecorativeBlocks.TR_CRIMSON_ROCK_BRICKS.get(),
            DecorativeBlocks.PEARL_ROCK_BRICKS.get(),
            DecorativeBlocks.GREEN_CANDY_BLOCK.get(),
            DecorativeBlocks.RED_CANDY_BLOCK.get(),
            DecorativeBlocks.SUN_PLATE.get(),
            DecorativeBlocks.TR_LAVA_BEAM.get(),
            DecorativeBlocks.TR_LAVA_BRICKS.get(),
            DecorativeBlocks.TR_OBSIDIAN_BEAM.get(),
            DecorativeBlocks.TR_OBSIDIAN_BRICKS.get(),
            DecorativeBlocks.TR_OBSIDIAN_PLATE.get(),
            DecorativeBlocks.TR_OBSIDIAN_SMALL_BRICKS.get(),
            DecorativeBlocks.TR_SMOOTH_OBSIDIAN.get(),
            DecorativeBlocks.TR_GRANITE_COLUMN.get(),
            DecorativeBlocks.MARBLE_COLUMN.get(),
            DecorativeBlocks.CHISELED_TR_OBSIDIAN_BRICKS.get(),
            DecorativeBlocks.CRYSTAL_BLOCKS.get(),
            DecorativeBlocks.BLUE_BRICKS.get(),
            DecorativeBlocks.GREEN_BRICKS.get(),
            DecorativeBlocks.PINK_BRICKS.get(),
            DecorativeBlocks.RUBY_CHAIN.get(),
            DecorativeBlocks.AMBER_CHAIN.get(),
            DecorativeBlocks.TOPAZ_CHAIN.get(),
            DecorativeBlocks.EMERALD_CHAIN.get(),
            DecorativeBlocks.SAPPHIRE_CHAIN.get(),
            DecorativeBlocks.DIAMOND_CHAIN.get(),
            DecorativeBlocks.AMETHYST_CHAIN.get(),
            DecorativeBlocks.SILK_CHAIN.get(),
            DecorativeBlocks.BONE_CHAIN.get(),
            NatureBlocks.EBONY_COBBLESTONE.get(),
            NatureBlocks.TR_CRIMSON_COBBLESTONE.get(),
            NatureBlocks.PEARL_COBBLESTONE.get(),
            NatureBlocks.HARDENED_SAND_BLOCK.get(),
            NatureBlocks.RED_HARDENED_SAND_BLOCK.get(),
            NatureBlocks.EBONY_HARDENED_SAND_BLOCK.get(),
            NatureBlocks.TR_CRIMSON_HARDENED_SAND_BLOCK.get(),
            NatureBlocks.PEARL_HARDENED_SAND_BLOCK.get(),
            NatureBlocks.EBONY_STONE.get(),
            NatureBlocks.EBONY_SANDSTONE.get(),
            NatureBlocks.TR_CRIMSON_STONE.get(),
            NatureBlocks.TR_CRIMSON_SANDSTONE.get(),
            NatureBlocks.PEARL_STONE.get(),
            NatureBlocks.PEARL_SANDSTONE.get(),
            DecorativeBlocks.BLUE_BRICKS.get(),
            DecorativeBlocks.PINK_BRICKS.get(),
            DecorativeBlocks.GREEN_BRICKS.get(),
            NatureBlocks.DESERT_FOSSIL.get(),
            FunctionalBlocks.EXTRACTINATOR.get(),
            FunctionalBlocks.DART_TRAP.get(),
            NatureBlocks.STONY_LOG.get(),
            FunctionalBlocks.SIGNAL_ADAPTER.get(),
            FunctionalBlocks.SWITCH.get(),
            FunctionalBlocks.TIMERS_BLOCK_1_1.get(),
            FunctionalBlocks.TIMERS_BLOCK_3_1.get(),
            FunctionalBlocks.TIMERS_BLOCK_5_1.get(),
            FunctionalBlocks.TIMERS_BLOCK_1_2.get(),
            FunctionalBlocks.TIMERS_BLOCK_1_4.get()
        );
        // 铲子
        IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block> mineableWithShovel = tag(BlockTags.MINEABLE_WITH_SHOVEL);
        mineableWithShovel.add(
            NatureBlocks.SLUSH.get(),
            NatureBlocks.MARINE_GRAVEL.get(),
            NatureBlocks.DIATOMACEOUS.get(),
            NatureBlocks.EBONY_SAND.get(),
            NatureBlocks.PEARL_SAND.get(),
            NatureBlocks.TR_CRIMSON_SAND.get(),
            NatureBlocks.EBONY_SAND_LAYER_BLOCK.get(),
            NatureBlocks.PEARL_SAND_LAYER_BLOCK.get(),
            NatureBlocks.TR_CRIMSON_SAND_LAYER_BLOCK.get(),
            NatureBlocks.ASH_BLOCK.get()
        );
        // 1，铁铅铜锡金
        tag(ModTags.Blocks.NEEDS_1_LEVEL).add(
                OreBlocks.RAW_TIN_BLOCK.get(),
                OreBlocks.DEEPSLATE_TIN_ORE.get(),
                OreBlocks.SANCTIFICATION_TIN_ORE.get(),
                OreBlocks.CORRUPTION_TIN_ORE.get(),
                OreBlocks.FLESHIFICATION_TIN_ORE.get(),
                OreBlocks.TIN_BLOCK.get(),
                OreBlocks.TIN_ORE.get(),
                OreBlocks.SANCTIFICATION_COPPER_ORE.get(),
                OreBlocks.CORRUPTION_COPPER_ORE.get(),
                OreBlocks.FLESHIFICATION_COPPER_ORE.get(),
                OreBlocks.SANCTIFICATION_IRON_ORE.get(),
                OreBlocks.CORRUPTION_IRON_ORE.get(),
                OreBlocks.FLESHIFICATION_IRON_ORE.get(),
                OreBlocks.RAW_LEAD_BLOCK.get(),
                OreBlocks.DEEPSLATE_LEAD_ORE.get(),
                OreBlocks.SANCTIFICATION_LEAD_ORE.get(),
                OreBlocks.CORRUPTION_LEAD_ORE.get(),
                OreBlocks.FLESHIFICATION_LEAD_ORE.get(),
                OreBlocks.LEAD_BLOCK.get(),
                OreBlocks.LEAD_ORE.get()
        );
        // 2,银钨，宝石
        tag(ModTags.Blocks.NEEDS_2_LEVEL).add(
                OreBlocks.RAW_SILVER_BLOCK.get(),
                OreBlocks.DEEPSLATE_SILVER_ORE.get(),
                OreBlocks.SANCTIFICATION_SILVER_ORE.get(),
                OreBlocks.CORRUPTION_SILVER_ORE.get(),
                OreBlocks.FLESHIFICATION_SILVER_ORE.get(),
                OreBlocks.SILVER_BLOCK.get(),
                OreBlocks.SILVER_ORE.get(),
                OreBlocks.RAW_TUNGSTEN_BLOCK.get(),
                OreBlocks.DEEPSLATE_TUNGSTEN_ORE.get(),
                OreBlocks.SANCTIFICATION_TUNGSTEN_ORE.get(),
                OreBlocks.CORRUPTION_TUNGSTEN_ORE.get(),
                OreBlocks.FLESHIFICATION_TUNGSTEN_ORE.get(),
                OreBlocks.TUNGSTEN_BLOCK.get(),
                OreBlocks.TUNGSTEN_ORE.get(),
                OreBlocks.SANCTIFICATION_DIAMOND_ORE.get(),
                OreBlocks.CORRUPTION_DIAMOND_ORE.get(),
                OreBlocks.FLESHIFICATION_DIAMOND_ORE.get(),
                OreBlocks.SANCTIFICATION_RUBY_ORE.get(),
                OreBlocks.CORRUPTION_RUBY_ORE.get(),
                OreBlocks.FLESHIFICATION_RUBY_ORE.get(),
                OreBlocks.RUBY_ORE.get(),
                OreBlocks.SANCTIFICATION_AMBER_ORE.get(),
                OreBlocks.CORRUPTION_AMBER_ORE.get(),
                OreBlocks.FLESHIFICATION_AMBER_ORE.get(),
                OreBlocks.AMBER_ORE.get(),
                OreBlocks.SANCTIFICATION_TOPAZ_ORE.get(),
                OreBlocks.CORRUPTION_TOPAZ_ORE.get(),
                OreBlocks.FLESHIFICATION_TOPAZ_ORE.get(),
                OreBlocks.TOPAZ_ORE.get(),
                OreBlocks.SANCTIFICATION_EMERALD_ORE.get(),
                OreBlocks.CORRUPTION_EMERALD_ORE.get(),
                OreBlocks.FLESHIFICATION_EMERALD_ORE.get(),
                OreBlocks.TR_EMERALD_ORE.get(),
                OreBlocks.SANCTIFICATION_SAPPHIRE_ORE.get(),
                OreBlocks.CORRUPTION_SAPPHIRE_ORE.get(),
                OreBlocks.FLESHIFICATION_SAPPHIRE_ORE.get(),
                OreBlocks.SAPPHIRE_ORE.get(),
                OreBlocks.SANCTIFICATION_TR_AMETHYST_ORE.get(),
                OreBlocks.CORRUPTION_TR_AMETHYST_ORE.get(),
                OreBlocks.FLESHIFICATION_TR_AMETHYST_ORE.get(),
                OreBlocks.TR_AMETHYST_ORE.get(),
                DecorativeBlocks.BIG_RUBY_BLOCK.get(),
                DecorativeBlocks.BIG_AMBER_BLOCK.get(),
                DecorativeBlocks.BIG_TOPAZ_BLOCK.get(),
                DecorativeBlocks.BIG_SAPPHIRE_BLOCK.get(),
                DecorativeBlocks.BIG_TR_AMETHYST_BLOCK.get(),
                DecorativeBlocks.BIG_TR_EMERALD_BLOCK.get(),
                DecorativeBlocks.SNOW_BRICKS.get(),
                DecorativeBlocks.TR_COPPER_BRICKS.get(),
                DecorativeBlocks.TR_COPPER_PLATE.get(),
                DecorativeBlocks.TIN_BRICKS.get(),
                DecorativeBlocks.TIN_PLATE.get(),
                DecorativeBlocks.TR_IRON_BRICKS.get(),
                DecorativeBlocks.LEAD_BRICKS.get(),
                DecorativeBlocks.SILVER_BRICKS.get(),
                DecorativeBlocks.TUNGSTEN_BRICKS.get(),
                DecorativeBlocks.TR_GOLD_BRICKS.get(),
                DecorativeBlocks.PLATINUM_BRICKS.get(),
                DecorativeBlocks.EBONY_ORE_BRICKS.get(),
                DecorativeBlocks.EBONY_ROCK_BRICKS.get(),
                DecorativeBlocks.METEORITE_BRICKS.get(),
                DecorativeBlocks.TR_CRIMSON_ORE_BRICKS.get(),
                DecorativeBlocks.TR_CRIMSON_ROCK_BRICKS.get(),
                DecorativeBlocks.PEARL_ROCK_BRICKS.get(),
                DecorativeBlocks.GREEN_CANDY_BLOCK.get(),
                DecorativeBlocks.RED_CANDY_BLOCK.get(),
                DecorativeBlocks.SUN_PLATE.get(),
                DecorativeBlocks.TR_LAVA_BEAM.get(),
                DecorativeBlocks.TR_LAVA_BRICKS.get(),
                DecorativeBlocks.TR_OBSIDIAN_BEAM.get(),
                DecorativeBlocks.TR_OBSIDIAN_BRICKS.get(),
                DecorativeBlocks.TR_OBSIDIAN_PLATE.get(),
                DecorativeBlocks.TR_OBSIDIAN_SMALL_BRICKS.get(),
                DecorativeBlocks.TR_SMOOTH_OBSIDIAN.get(),
                DecorativeBlocks.TR_GRANITE_COLUMN.get(),
                DecorativeBlocks.MARBLE_COLUMN.get(),
                DecorativeBlocks.CHISELED_TR_OBSIDIAN_BRICKS.get(),
                DecorativeBlocks.CRYSTAL_BLOCKS.get(),
                DecorativeBlocks.BLUE_BRICKS.get(),
                DecorativeBlocks.GREEN_BRICKS.get(),
                DecorativeBlocks.PINK_BRICKS.get(),
                DecorativeBlocks.RUBY_CHAIN.get(),
                DecorativeBlocks.AMBER_CHAIN.get(),
                DecorativeBlocks.TOPAZ_CHAIN.get(),
                DecorativeBlocks.EMERALD_CHAIN.get(),
                DecorativeBlocks.SAPPHIRE_CHAIN.get(),
                DecorativeBlocks.DIAMOND_CHAIN.get(),
                DecorativeBlocks.AMETHYST_CHAIN.get(),
                DecorativeBlocks.SILK_CHAIN.get(),
                DecorativeBlocks.BONE_CHAIN.get(),
                NatureBlocks.EBONY_COBBLESTONE.get(),
                NatureBlocks.TR_CRIMSON_COBBLESTONE.get(),
                NatureBlocks.PEARL_COBBLESTONE.get(),
                NatureBlocks.HARDENED_SAND_BLOCK.get(),
                NatureBlocks.RED_HARDENED_SAND_BLOCK.get(),
                FunctionalBlocks.EXTRACTINATOR.get(),
                FunctionalBlocks.DART_TRAP.get(),
                NatureBlocks.STONY_LOG.get(),
                FunctionalBlocks.SIGNAL_ADAPTER.get(),
                FunctionalBlocks.SWITCH.get(),
                FunctionalBlocks.TIMERS_BLOCK_1_1.get(),
                FunctionalBlocks.TIMERS_BLOCK_3_1.get(),
                FunctionalBlocks.TIMERS_BLOCK_5_1.get(),
                FunctionalBlocks.TIMERS_BLOCK_1_2.get(),
                FunctionalBlocks.TIMERS_BLOCK_1_4.get()
        );
        // 3,铂金
        tag(ModTags.Blocks.NEEDS_3_LEVEL).add(
                OreBlocks.RAW_PLATINUM_BLOCK.get(),
                OreBlocks.DEEPSLATE_PLATINUM_ORE.get(),
                OreBlocks.SANCTIFICATION_PLATINUM_ORE.get(),
                OreBlocks.CORRUPTION_PLATINUM_ORE.get(),
                OreBlocks.FLESHIFICATION_PLATINUM_ORE.get(),
                OreBlocks.PLATINUM_BLOCK.get(),
                OreBlocks.PLATINUM_ORE.get(),
                NatureBlocks.DESERT_FOSSIL.get()
        );
        // 4,邪恶矿,陨石
        tag(ModTags.Blocks.NEEDS_4_LEVEL).add(
                OreBlocks.EBONY_ORE.get(),
                OreBlocks.DEEPSLATE_EBONY_ORE.get(),
                OreBlocks.SANCTIFICATION_EBONY_ORE.get(),
                OreBlocks.CORRUPTION_EBONY_ORE.get(),
                OreBlocks.FLESHIFICATION_EBONY_ORE.get(),
                OreBlocks.EBONY_BLOCK.get(),
                OreBlocks.RAW_EBONY_BLOCK.get(),
                OreBlocks.DEEPSLATE_TR_CRIMSON_ORE.get(),
                OreBlocks.SANCTIFICATION_TR_CRIMSON_ORE.get(),
                OreBlocks.CORRUPTION_TR_CRIMSON_ORE.get(),
                OreBlocks.FLESHIFICATION_TR_CRIMSON_ORE.get(),
                OreBlocks.RAW_TR_CRIMSON_BLOCK.get(),
                OreBlocks.TR_CRIMSON_BLOCK.get(),
                OreBlocks.TR_CRIMSON_ORE.get(),
                OreBlocks.METEORITE_ORE.get(),
                OreBlocks.METEORITE_BLOCK.get()
        );
        // 5，狱石，邪恶石头
        tag(ModTags.Blocks.NEEDS_5_LEVEL).add(
            NatureBlocks.EBONY_HARDENED_SAND_BLOCK.get(),
            NatureBlocks.TR_CRIMSON_HARDENED_SAND_BLOCK.get(),
            NatureBlocks.PEARL_HARDENED_SAND_BLOCK.get(),
            NatureBlocks.EBONY_STONE.get(),
            NatureBlocks.EBONY_SANDSTONE.get(),
            NatureBlocks.TR_CRIMSON_STONE.get(),
            NatureBlocks.TR_CRIMSON_SANDSTONE.get(),
            NatureBlocks.PEARL_STONE.get(),
            NatureBlocks.PEARL_SANDSTONE.get(),
            DecorativeBlocks.BLUE_BRICKS.get(),
            DecorativeBlocks.PINK_BRICKS.get(),
            DecorativeBlocks.GREEN_BRICKS.get(),
            OreBlocks.HELLSTONE.get(),
            OreBlocks.HELLSTONE_BLOCK.get(),
            OreBlocks.RAW_HELLSTONE_BLOCK.get(),
            OreBlocks.ASH_HELLSTONE.get()
        );
        tag(ModTags.Blocks.NEEDS_1_LEVEL).addTags(ModTags.Blocks.NEEDS_2_LEVEL, ModTags.Blocks.NEEDS_3_LEVEL, ModTags.Blocks.NEEDS_4_LEVEL, ModTags.Blocks.NEEDS_5_LEVEL, ModTags.Blocks.NEEDS_6_LEVEL, ModTags.Blocks.NEEDS_7_LEVEL, ModTags.Blocks.NEEDS_8_LEVEL, ModTags.Blocks.NEEDS_9_LEVEL);
        tag(ModTags.Blocks.NEEDS_2_LEVEL).addTags(ModTags.Blocks.NEEDS_3_LEVEL, ModTags.Blocks.NEEDS_4_LEVEL, ModTags.Blocks.NEEDS_5_LEVEL, ModTags.Blocks.NEEDS_6_LEVEL, ModTags.Blocks.NEEDS_7_LEVEL, ModTags.Blocks.NEEDS_8_LEVEL, ModTags.Blocks.NEEDS_9_LEVEL);
        tag(ModTags.Blocks.NEEDS_3_LEVEL).addTags(ModTags.Blocks.NEEDS_4_LEVEL, ModTags.Blocks.NEEDS_5_LEVEL, ModTags.Blocks.NEEDS_6_LEVEL, ModTags.Blocks.NEEDS_7_LEVEL, ModTags.Blocks.NEEDS_8_LEVEL, ModTags.Blocks.NEEDS_9_LEVEL);
        tag(ModTags.Blocks.NEEDS_4_LEVEL).addTags(ModTags.Blocks.NEEDS_5_LEVEL, ModTags.Blocks.NEEDS_6_LEVEL, ModTags.Blocks.NEEDS_7_LEVEL, ModTags.Blocks.NEEDS_8_LEVEL, ModTags.Blocks.NEEDS_9_LEVEL);
        tag(ModTags.Blocks.NEEDS_5_LEVEL).addTags(ModTags.Blocks.NEEDS_6_LEVEL, ModTags.Blocks.NEEDS_7_LEVEL, ModTags.Blocks.NEEDS_8_LEVEL, ModTags.Blocks.NEEDS_9_LEVEL);
        tag(ModTags.Blocks.NEEDS_6_LEVEL).addTags(ModTags.Blocks.NEEDS_7_LEVEL, ModTags.Blocks.NEEDS_8_LEVEL, ModTags.Blocks.NEEDS_9_LEVEL);
        tag(ModTags.Blocks.NEEDS_7_LEVEL).addTags(ModTags.Blocks.NEEDS_8_LEVEL, ModTags.Blocks.NEEDS_9_LEVEL);
        tag(ModTags.Blocks.NEEDS_8_LEVEL).addTags(ModTags.Blocks.NEEDS_9_LEVEL);
        tag(ModTags.Blocks.NEEDS_9_LEVEL).add(DecorativeBlocks.LIHZAHRD_BRICKS.get());

        tag(ModTags.Blocks.MINEABLE_WITH_PICKAXE_AXE).addTags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.MINEABLE_WITH_AXE);
        tag(ModTags.Blocks.DROOPING_VINE_CAN_SURVIVE).add(
            NatureBlocks.MUSHROOM_GRASS_BLOCK.get(),
            NatureBlocks.HALLOW_GRASS_BLOCK.get(),
            NatureBlocks.TR_CRIMSON_GRASS_BLOCK.get(),
            NatureBlocks.CORRUPT_GRASS_BLOCK.get(),
            Blocks.MUD,
            Blocks.DIRT,
            Blocks.GRASS_BLOCK,
            Blocks.DIRT_PATH,
            Blocks.COARSE_DIRT,
            Blocks.PODZOL,
            Blocks.ROOTED_DIRT,
            Blocks.MUDDY_MANGROVE_ROOTS,
            OreBlocks.HELLSTONE.get(),
            OreBlocks.ASH_HELLSTONE.get(),
            OreBlocks.RAW_HELLSTONE_BLOCK.get(),
            OreBlocks.HELLSTONE_BLOCK.get()
        );
        tag(ModTags.Blocks.DROOPING_VINE_CAN_SURVIVE).addTag(BlockTags.LEAVES);
        tag(ModTags.Blocks.COIN_PILE).add(COPPER_COIN_PILE.get(), SILVER_COIN_PILE.get(), GOLDEN_COIN_PILE.get(), PLATINUM_COIN_PILE.get(), EMERALD_COIN_PILE.get());

        tag(ModTags.Blocks.NEEDS_5_LEVEL).add(
            OreBlocks.DEEPSLATE_COBALT_ORE.get(),
            OreBlocks.RAW_COBALT_BLOCK.get(),
            OreBlocks.COBALT_BLOCK.get(),
            OreBlocks.DEEPSLATE_PALLADIUM_ORE.get(),
            OreBlocks.RAW_PALLADIUM_BLOCK.get(),
            OreBlocks.PALLADIUM_BLOCK.get()
        );
        tag(ModTags.Blocks.NEEDS_6_LEVEL).add(
            OreBlocks.DEEPSLATE_MITHRIL_ORE.get(),
            OreBlocks.RAW_MITHRIL_BLOCK.get(),
            OreBlocks.MITHRIL_BLOCK.get(),
            OreBlocks.DEEPSLATE_ORICHALCUM_ORE.get(),
            OreBlocks.RAW_ORICHALCUM_BLOCK.get(),
            OreBlocks.ORICHALCUM_BLOCK.get()
        );
        tag(ModTags.Blocks.NEEDS_7_LEVEL).add(
            OreBlocks.DEEPSLATE_ADAMANTITE_ORE.get(),
            OreBlocks.RAW_ADAMANTITE_BLOCK.get(),
            OreBlocks.ADAMANTITE_BLOCK.get(),
            OreBlocks.DEEPSLATE_TITANIUM_ORE.get(),
            OreBlocks.RAW_TITANIUM_BLOCK.get(),
            OreBlocks.TITANIUM_BLOCK.get()
        );
        tag(ModTags.Blocks.NEEDS_8_LEVEL).add(OreBlocks.HALLOWED_BLOCK.get());
        tag(ModTags.Blocks.NEEDS_9_LEVEL).add(
            OreBlocks.CHLOROPHYTE_ORE.get(),
            OreBlocks.RAW_CHLOROPHYTE_BLOCK.get(),
            OreBlocks.CHLOROPHYTE_BLOCK.get(),
            OreBlocks.SHROOMITE_BLOCK.get(),
            OreBlocks.SPECTRE_BLOCK.get(),
            OreBlocks.RAW_LUMINITE_BLOCK.get(),
            OreBlocks.LUMINITE_BLOCK.get()
        );
    }

    @Override
    public @NotNull IntrinsicTagAppender<Block> tag(@NotNull TagKey<Block> tag) {
        return super.tag(tag);
    }
}
