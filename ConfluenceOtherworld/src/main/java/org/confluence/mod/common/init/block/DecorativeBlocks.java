package org.confluence.mod.common.init.block;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.common.CrackedBricksBlock;
import org.confluence.mod.common.block.common.EnchantedBricksBlock;
import org.confluence.mod.common.block.common.LihzahrdDoorBlock;
import org.confluence.mod.common.block.natural.*;
import org.confluence.mod.common.block.palettes.ConnectedGlassBlock;
import org.confluence.mod.common.block.palettes.ConnectedStainedGlassBlock;
import org.confluence.mod.common.init.item.ModItems;

import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraft.world.level.block.Blocks.*;


public class DecorativeBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Confluence.MODID);

    // 杂项
    public static final DeferredBlock<Block> CHISELED_OAK_PLANKS = copyBlockRegister("chiseled_oak_planks", Blocks.OAK_PLANKS);
    public static final DeferredBlock<Block> CHISELED_SPRUCE_PLANKS = copyBlockRegister("chiseled_spruce_planks", Blocks.OAK_PLANKS);
    public static final DeferredBlock<Block> CHISELED_EBONY_PLANKS = copyBlockRegister("chiseled_ebony_planks", Blocks.OAK_PLANKS);
    public static final DeferredBlock<Block> CHISELED_SHADOW_PLANKS = copyBlockRegister("chiseled_shadow_planks", Blocks.OAK_PLANKS);
    public static final DeferredBlock<Block> CHISELED_PEARL_PLANKS = copyBlockRegister("chiseled_pearl_planks", Blocks.OAK_PLANKS);
    public static final DeferredBlock<Block> CHISELED_PALM_PLANKS = copyBlockRegister("chiseled_palm_planks", Blocks.OAK_PLANKS);
    public static final DeferredBlock<Block> CHISELED_BAOBAB_PLANKS = copyBlockRegister("chiseled_baobab_planks", Blocks.OAK_PLANKS);
    public static final DeferredBlock<Block> CHISELED_YELLOW_WILLOW_PLANKS = copyBlockRegister("chiseled_yellow_willow_planks", Blocks.OAK_PLANKS);
    public static final DeferredBlock<Block> CHISELED_LIVING_PLANKS = copyBlockRegister("chiseled_living_planks", Blocks.OAK_PLANKS);
    public static final DeferredBlock<Block> CHISELED_LIVING_MAHOGANY_PLANKS = copyBlockRegister("chiseled_living_mahogany_planks", Blocks.OAK_PLANKS);
    public static final DeferredBlock<Block> CHISELED_ASH_PLANKS = copyBlockRegister("chiseled_ash_planks", Blocks.OAK_PLANKS);
    public static final DeferredBlock<Block> WOOD_STONE_SLATTED_BLOCKS = copyBlockRegister("wood_stone_slatted_blocks", Blocks.OAK_PLANKS);
    public static final DeferredBlock<Block> BLUE_ICE_BRICKS = copyBlockRegister("blue_ice_bricks", Blocks.BLUE_ICE);
    public static final DeferredBlock<Block> PACKED_ICE_BRICKS = copyBlockRegister("packed_ice_bricks", Blocks.PACKED_ICE);
    public static final DeferredBlock<Block> SNOW_BRICKS = copyBlockRegister("snow_bricks", Blocks.STONE_BRICKS);
    public static final DeferredBlock<Block> AETHERIUM_BRICKS = copyBlockRegister("aetherium_bricks", Blocks.STONE_BRICKS);
    public static final DeferredBlock<Block> CRYSTAL_BLOCK = copyBlockRegister("crystal_block", Blocks.AMETHYST_BLOCK);
    public static final DeferredBlock<Block> RAINBOW_BRICKS = copyBlockRegister("rainbow_bricks", Blocks.STONE_BRICKS);
    public static final DeferredBlock<CloudBlock> FLOATING_WHEAT_BALE = registerWithItem("floating_wheat_bale", () -> new CloudBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_LIGHT_BLUE)
            .strength(0.3F)
            .sound(SoundType.SNOW)
            .isValidSpawn(Blocks::never)
            .isRedstoneConductor((blockState, blockGetter, blockPos) -> false)
            .isSuffocating((blockState, blockGetter, blockPos) -> false)));
    public static final DeferredBlock<CloudBlockTrampoline> CLOUD_BLOCK_TRAMPOLINE = registerWithItem("cloud_block_trampoline", () -> new CloudBlockTrampoline(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_PINK)
            .strength(0.3F)
            .sound(SoundType.SNOW)
            .noOcclusion()
            .isValidSpawn(Blocks::never)
            .isRedstoneConductor((blockState, blockGetter, blockPos) -> false)
            .isSuffocating((blockState, blockGetter, blockPos) -> false)));
    public static final DeferredBlock<CloudBlock> BOUNCY_CLOUD_BLOCK = registerWithItem("bouncy_cloud_block", () -> new CloudBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_PINK)
            .strength(0.3F)
            .sound(SoundType.SNOW)
            .noOcclusion()
            .jumpFactor(2.5F)
            .isValidSpawn(Blocks::never)
            .isRedstoneConductor((blockState, blockGetter, blockPos) -> false)
            .isSuffocating((blockState, blockGetter, blockPos) -> false)));
    public static final DeferredBlock<SwordInStoneBlock> SWORD_IN_STONE = registerWithItem("sword_in_stone", SwordInStoneBlock::new);
    public static final DeferredBlock<CrispyHoneyBlock> CRISPY_HONEY_BLOCK = registerWithItem("crispy_honey_block", CrispyHoneyBlock::new);
    public static final DeferredBlock<Block> ASPHALT_BLOCK = registerWithItem("asphalt_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(BLACK_TERRACOTTA).friction(0.4F).speedFactor(1.2F)));
    public static final DeferredBlock<Block> FLESH_BLOCK = registerWithItem("flesh_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE)));
    public static final DeferredBlock<Block> LESION_BLOCK = registerWithItem("lesion_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE)));
    public static final DeferredBlock<Block> REMAINS_BLOCK = registerWithItem("remains_block", () -> new RemainsBlock(BlockBehaviour.Properties.of().strength(1.0f).pushReaction(PushReaction.DESTROY)));

    // 纯净玻璃
    public static final DeferredBlock<Block> PURE_GLASS = registerWithItem("pure_glass", () -> new ConnectedGlassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
    public static final DeferredBlock<Block> WHITE_PURE_GLASS = registerWithItem("white_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.WHITE, BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_STAINED_GLASS)));
    public static final DeferredBlock<Block> LIGHT_GRAY_PURE_GLASS = registerWithItem("light_gray_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.LIGHT_GRAY, BlockBehaviour.Properties.ofFullCopy(Blocks.LIGHT_GRAY_STAINED_GLASS)));
    public static final DeferredBlock<Block> GRAY_PURE_GLASS = registerWithItem("gray_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.GRAY, BlockBehaviour.Properties.ofFullCopy(Blocks.GRAY_STAINED_GLASS)));
    public static final DeferredBlock<Block> BLACK_PURE_GLASS = registerWithItem("black_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.BLACK, BlockBehaviour.Properties.ofFullCopy(Blocks.BLACK_STAINED_GLASS)));
    public static final DeferredBlock<Block> BROWN_PURE_GLASS = registerWithItem("brown_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.BROWN, BlockBehaviour.Properties.ofFullCopy(Blocks.BROWN_STAINED_GLASS)));
    public static final DeferredBlock<Block> RED_PURE_GLASS = registerWithItem("red_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.RED, BlockBehaviour.Properties.ofFullCopy(Blocks.RED_STAINED_GLASS)));
    public static final DeferredBlock<Block> ORANGE_PURE_GLASS = registerWithItem("orange_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.ORANGE, BlockBehaviour.Properties.ofFullCopy(Blocks.ORANGE_STAINED_GLASS)));
    public static final DeferredBlock<Block> YELLOW_PURE_GLASS = registerWithItem("yellow_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.YELLOW, BlockBehaviour.Properties.ofFullCopy(Blocks.YELLOW_STAINED_GLASS)));
    public static final DeferredBlock<Block> LIME_PURE_GLASS = registerWithItem("lime_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.LIME, BlockBehaviour.Properties.ofFullCopy(Blocks.LIME_STAINED_GLASS)));
    public static final DeferredBlock<Block> GREEN_PURE_GLASS = registerWithItem("green_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.GREEN, BlockBehaviour.Properties.ofFullCopy(Blocks.GREEN_STAINED_GLASS)));
    public static final DeferredBlock<Block> CYAN_PURE_GLASS = registerWithItem("cyan_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.CYAN, BlockBehaviour.Properties.ofFullCopy(Blocks.CYAN_STAINED_GLASS)));
    public static final DeferredBlock<Block> LIGHT_BLUE_PURE_GLASS = registerWithItem("light_blue_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.LIGHT_BLUE, BlockBehaviour.Properties.ofFullCopy(Blocks.LIGHT_BLUE_STAINED_GLASS)));
    public static final DeferredBlock<Block> BLUE_PURE_GLASS = registerWithItem("blue_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.BLUE, BlockBehaviour.Properties.ofFullCopy(Blocks.BLUE_STAINED_GLASS)));
    public static final DeferredBlock<Block> PURPLE_PURE_GLASS = registerWithItem("purple_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.PURPLE, BlockBehaviour.Properties.ofFullCopy(Blocks.PURPLE_STAINED_GLASS)));
    public static final DeferredBlock<Block> MAGENTA_PURE_GLASS = registerWithItem("magenta_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.MAGENTA, BlockBehaviour.Properties.ofFullCopy(Blocks.MAGENTA_STAINED_GLASS)));
    public static final DeferredBlock<Block> PINK_PURE_GLASS = registerWithItem("pink_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.PINK, BlockBehaviour.Properties.ofFullCopy(Blocks.PINK_STAINED_GLASS)));

    // 矿砖
    public static final DeferredBlock<Block> COPPER_BRICKS = copyBlockRegister("copper_bricks", Blocks.COPPER_BLOCK);
    public static final DeferredBlock<Block> TIN_BRICKS = copyBlockRegister("tin_bricks", Blocks.COPPER_BLOCK);
    public static final DeferredBlock<Block> IRON_BRICKS = copyBlockRegister("iron_bricks", Blocks.IRON_BLOCK);
    public static final DeferredBlock<Block> LEAD_BRICKS = copyBlockRegister("lead_bricks", Blocks.IRON_BLOCK);
    public static final DeferredBlock<Block> SILVER_BRICKS = copyBlockRegister("silver_bricks", Blocks.IRON_BLOCK);
    public static final DeferredBlock<Block> TUNGSTEN_BRICKS = copyBlockRegister("tungsten_bricks", Blocks.IRON_BLOCK);
    public static final DeferredBlock<Block> GOLDEN_BRICKS = copyBlockRegister("golden_bricks", Blocks.IRON_BLOCK);
    public static final DeferredBlock<Block> PLATINUM_BRICKS = copyBlockRegister("platinum_bricks", Blocks.IRON_BLOCK);
    public static final DeferredBlock<Block> DEMONITE_ORE_BRICKS = copyBlockRegister("demonite_ore_bricks", Blocks.IRON_BLOCK);
    public static final DeferredBlock<Block> EBONSTONE_BRICKS = copyBlockRegister("ebonstone_bricks", Blocks.IRON_BLOCK);
    public static final DeferredBlock<Block> METEORITE_BRICKS = copyBlockRegister("meteorite_bricks", Blocks.IRON_BLOCK);
    public static final DeferredBlock<Block> CRIMTANE_ORE_BRICKS = copyBlockRegister("crimtane_ore_bricks", Blocks.IRON_BLOCK);
    public static final DeferredBlock<Block> CRIMSTONE_BRICKS = copyBlockRegister("crimstone_bricks", Blocks.IRON_BLOCK);
    public static final DeferredBlock<Block> PEARL_ROCK_BRICKS = copyBlockRegister("pearl_rock_bricks", Blocks.STONE_BRICKS);
    public static final DeferredBlock<Block> GREEN_CANDY_BLOCK = copyBlockRegister("green_candy_block", Blocks.STONE_BRICKS);
    public static final DeferredBlock<Block> RED_CANDY_BLOCK = copyBlockRegister("red_candy_block", Blocks.STONE_BRICKS);
    public static final DeferredBlock<Block> FROZEN_GEL_BLOCK = registerWithItem("frozen_gel_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.SLIME_BLOCK)));
    public static final DeferredBlock<Block> BLUE_GEL_BLOCK = registerWithItem("blue_gel_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).sound(SoundType.SLIME_BLOCK)));
    public static final DeferredBlock<Block> PINK_GEL_BLOCK = registerWithItem("pink_gel_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).sound(SoundType.SLIME_BLOCK)));

    // 天域
    public static final DeferredBlock<StairBlock> SUN_PLATE_STAIRS = registerWithItem("sun_plate_stairs", () -> new StairBlock(TUFF.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static final DeferredBlock<SlabBlock> SUN_PLATE_SLAB = registerWithItem("sun_plate_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(STONE)));
    public static final DeferredBlock<DoorBlock> SKYWARE_DOOR = registerWithItem("skyware_door", () -> new DoorBlock(BlockSetType.STONE, BlockBehaviour.Properties.of().mapColor(BLUE_ICE.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(4.0F).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<DoorBlock> SKYWARE_GLASS_DOOR = registerWithItem("skyware_glass_door", () -> new DoorBlock(BlockSetType.STONE, BlockBehaviour.Properties.of().mapColor(BLUE_ICE.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(4.0F).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> SUN_PLATE = copyBlockRegister("sun_plate", Blocks.STONE_BRICKS);
    public static final DeferredBlock<Block> DISC_BLOCK = copyBlockRegister("disc_block", Blocks.STONE_BRICKS);

    // 黑曜石
    public static final DeferredBlock<Block> OBSIDIAN_BRICKS = copyBlockRegister("obsidian_bricks", Blocks.OBSIDIAN);
    public static final DeferredBlock<Block> OBSIDIAN_SMALL_BRICKS = copyBlockRegister("obsidian_small_bricks", Blocks.OBSIDIAN);
    public static final DeferredBlock<StairBlock> OBSIDIAN_BRICKS_STAIRS = registerWithItem("obsidian_bricks_stairs", () -> new StairBlock(TUFF.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)));
    public static final DeferredBlock<SlabBlock> OBSIDIAN_BRICKS_SLAB = registerWithItem("obsidian_bricks_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)));
    public static final DeferredBlock<DoorBlock> OBSIDIAN_BRICKS_DOOR = registerWithItem("obsidian_bricks_door", () -> new DoorBlock(
            BlockSetType.STONE,
            BlockBehaviour.Properties.of()
                    .mapColor(OBSIDIAN.defaultMapColor())
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(5.0F)
                    .noOcclusion()
                    .ignitedByLava()
                    .pushReaction(PushReaction.DESTROY)
    ));
    public static final DeferredBlock<Block> CHISELED_OBSIDIAN_BRICKS = copyBlockRegister("chiseled_obsidian_bricks", Blocks.OBSIDIAN);
    public static final DeferredBlock<Block> SMOOTH_OBSIDIAN = copyBlockRegister("smooth_obsidian", Blocks.OBSIDIAN);

    // 花岗岩
    public static final DeferredBlock<Block> GRANITE_COLUMN = copyBlockRegister("granite_column", Blocks.STONE_BRICKS);

    // 地牢
    public static final DeferredBlock<Block> BLUE_BRICKS = registerWithItem("blue_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(DyeColor.BLUE).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<Block> GREEN_BRICKS = registerWithItem("green_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(DyeColor.GREEN).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<Block> PINK_BRICKS = registerWithItem("pink_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(DyeColor.PINK).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<CrackedBricksBlock> CRACKED_BLUE_BRICKS = registerWithItem("cracked_blue_bricks", () -> new CrackedBricksBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).strength(0).mapColor(DyeColor.BLUE)));
    public static final DeferredBlock<CrackedBricksBlock> CRACKED_GREEN_BRICKS = registerWithItem("cracked_green_bricks", () -> new CrackedBricksBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).strength(0).mapColor(DyeColor.GREEN)));
    public static final DeferredBlock<CrackedBricksBlock> CRACKED_PINK_BRICKS = registerWithItem("cracked_pink_bricks", () -> new CrackedBricksBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).strength(0).mapColor(DyeColor.PINK)));
    public static final DeferredBlock<Block> CHISELED_BLUE_BRICKS = registerWithItem("chiseled_blue_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(DyeColor.BLUE).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<Block> CHISELED_GREEN_BRICKS = registerWithItem("chiseled_green_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(DyeColor.GREEN).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<Block> CHISELED_PINK_BRICKS = registerWithItem("chiseled_pink_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(DyeColor.PINK).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<StairBlock> BLUE_BRICK_STAIRS = registerWithItem("blue_brick_stairs", () -> new StairBlock(BLUE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_STAIRS).mapColor(DyeColor.BLUE).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<StairBlock> GREEN_BRICK_STAIRS = registerWithItem("green_brick_stairs", () -> new StairBlock(GREEN_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_STAIRS).mapColor(DyeColor.GREEN).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<StairBlock> PINK_BRICK_STAIRS = registerWithItem("pink_brick_stairs", () -> new StairBlock(PINK_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_STAIRS).mapColor(DyeColor.PINK).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<SlabBlock> BLUE_BRICK_SLAB = registerWithItem("blue_brick_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(STONE_SLAB).mapColor(DyeColor.BLUE).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<SlabBlock> GREEN_BRICK_SLAB = registerWithItem("green_brick_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(STONE_SLAB).mapColor(DyeColor.GREEN).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<SlabBlock> PINK_BRICK_SLAB = registerWithItem("pink_brick_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(STONE_SLAB).mapColor(DyeColor.PINK).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<DoorBlock> DUNGEON_DOOR = registerWithItem("dungeon_door", () -> new DoorBlock(BlockSetType.STONE, BlockBehaviour.Properties.ofFullCopy(IRON_DOOR).mapColor(DyeColor.GRAY).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<EnchantedBricksBlock> ENCHANTED_BLUE_BRICKS = registerWithItem("enchanted_blue_bricks", () -> new EnchantedBricksBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(DyeColor.BLUE).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<EnchantedBricksBlock> ENCHANTED_GREEN_BRICKS = registerWithItem("enchanted_green_bricks", () -> new EnchantedBricksBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(DyeColor.BLUE).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<EnchantedBricksBlock> ENCHANTED_PINK_BRICKS = registerWithItem("enchanted_pink_bricks", () -> new EnchantedBricksBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(DyeColor.BLUE).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<RotatedPillarBlock> BLUE_BRICK_COLUMN = registerWithItem("blue_brick_column", () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(DyeColor.BLUE).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<RotatedPillarBlock> GREEN_BRICK_COLUMN = registerWithItem("green_brick_column", () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(DyeColor.GREEN).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<RotatedPillarBlock> PINK_BRICK_COLUMN = registerWithItem("pink_brick_column", () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(DyeColor.PINK).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));

    // 大宝石块
    public static final DeferredBlock<Block> RUBY_BLOCK = copyBlockRegister("ruby_block", Blocks.DIAMOND_BLOCK);
    public static final DeferredBlock<Block> AMBER_BLOCK = copyBlockRegister("amber_block", Blocks.DIAMOND_BLOCK);
    public static final DeferredBlock<Block> TOPAZ_BLOCK = copyBlockRegister("topaz_block", Blocks.DIAMOND_BLOCK);
    public static final DeferredBlock<Block> PURE_EMERALD_BLOCK = copyBlockRegister("pure_emerald_block", Blocks.DIAMOND_BLOCK);
    public static final DeferredBlock<Block> SAPPHIRE_BLOCK = copyBlockRegister("sapphire_block", Blocks.DIAMOND_BLOCK);
    public static final DeferredBlock<Block> AMETHYST_BLOCK = copyBlockRegister("amethyst_block", Blocks.DIAMOND_BLOCK);
    public static final DeferredBlock<Block> POLISHED_GRANITE = copyBlockRegister("polished_granite", Blocks.DIAMOND_BLOCK);

    // 锁链
    public static final DeferredBlock<ChainBlock> RUBY_CHAIN = copyBlockRegister("ruby_chain", Blocks.CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.COLOR_RED)));
    public static final DeferredBlock<ChainBlock> AMBER_CHAIN = copyBlockRegister("amber_chain", Blocks.CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.COLOR_ORANGE)));
    public static final DeferredBlock<ChainBlock> TOPAZ_CHAIN = copyBlockRegister("topaz_chain", Blocks.CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.COLOR_YELLOW)));
    public static final DeferredBlock<ChainBlock> EMERALD_CHAIN = copyBlockRegister("emerald_chain", Blocks.CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.EMERALD)));
    public static final DeferredBlock<ChainBlock> SAPPHIRE_CHAIN = copyBlockRegister("sapphire_chain", Blocks.CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.COLOR_BLUE)));
    public static final DeferredBlock<ChainBlock> DIAMOND_CHAIN = copyBlockRegister("diamond_chain", Blocks.CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.DIAMOND)));
    public static final DeferredBlock<ChainBlock> AMETHYST_CHAIN = copyBlockRegister("amethyst_chain", Blocks.CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.COLOR_PURPLE)));
    public static final DeferredBlock<ChainBlock> SILK_CHAIN = copyBlockRegister("silk_chain", Blocks.CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<ChainBlock> BONE_CHAIN = copyBlockRegister("bone_chain", Blocks.CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.TERRACOTTA_WHITE)));

    // 神庙
    public static final BlockSetType LIHZAHRD = BlockSetType.register(new BlockSetType("confluence:lihzahrd",
            false,
            false,
            false,
            BlockSetType.PressurePlateSensitivity.MOBS,
            SoundType.STONE,
            SoundEvents.IRON_DOOR_CLOSE,
            SoundEvents.IRON_DOOR_OPEN,
            SoundEvents.IRON_TRAPDOOR_CLOSE,
            SoundEvents.IRON_TRAPDOOR_OPEN,
            SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF,
            SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON,
            SoundEvents.STONE_BUTTON_CLICK_OFF,
            SoundEvents.STONE_BUTTON_CLICK_ON
    ));
    public static final DeferredBlock<LihzahrdDoorBlock> LIHZAHRD_DOOR = registerWithItem("lihzahrd_door", () -> new LihzahrdDoorBlock(LIHZAHRD, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).requiresCorrectToolForDrops().noOcclusion().strength(100.0F, ModBlocks.getObsidianBasedExplosionResistance(1000.0F)).pushReaction(PushReaction.BLOCK)));
    public static final DeferredBlock<Block> LIHZAHRD_BRICKS = registerWithItem("lihzahrd_bricks", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).requiresCorrectToolForDrops().strength(100.0F, ModBlocks.getObsidianBasedExplosionResistance(1000.0F))));


    private static DeferredBlock<Block> copyBlockRegister(String newName, Block originalBlock) {
        DeferredBlock<Block> block = BLOCKS.registerSimpleBlock(newName, BlockBehaviour.Properties.ofFullCopy(originalBlock));
        ModItems.BLOCK_ITEMS.registerSimpleBlockItem(newName, block);
        return block;
    }

    private static <B extends Block> DeferredBlock<B> copyBlockRegister(String newName, Block originalBlock, Function<BlockBehaviour.Properties, B> function) {
        DeferredBlock<B> block = BLOCKS.register(newName, () -> function.apply(BlockBehaviour.Properties.ofFullCopy(originalBlock)));
        ModItems.BLOCK_ITEMS.registerSimpleBlockItem(newName, block);
        return block;
    }

    private static <B extends Block> DeferredBlock<B> registerWithItem(String newName, Supplier<B> supplier) {
        DeferredBlock<B> block = BLOCKS.register(newName, supplier);
        ModItems.BLOCK_ITEMS.registerSimpleBlockItem(newName, block);
        return block;
    }
}
