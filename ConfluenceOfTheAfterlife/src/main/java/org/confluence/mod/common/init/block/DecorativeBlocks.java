package org.confluence.mod.common.init.block;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.natural.CloudBlock;
import org.confluence.mod.common.block.palettes.ConnectedGlassBlock;
import org.confluence.mod.common.block.palettes.ConnectedStainedGlassBlock;
import org.confluence.mod.common.init.item.ModItems;

import java.util.function.Function;
import java.util.function.Supplier;


public class DecorativeBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Confluence.MODID);

    //TODO 暂未注册BeamLikeBlock
    public static final Supplier<Block> TR_OAK_BEAM = copyBlockRegister("tr_oak_beam", Blocks.OAK_PLANKS);
    public static final Supplier<Block> TR_OAK_PLANKS = copyBlockRegister("tr_oak_planks", Blocks.OAK_PLANKS);
    public static final Supplier<Block> TR_NORTHLAND_BEAM = copyBlockRegister("tr_northland_beam", Blocks.OAK_PLANKS);
    public static final Supplier<Block> TR_NORTHLAND_PLANKS = copyBlockRegister("tr_northland_planks", Blocks.OAK_PLANKS);
    public static final Supplier<Block> WOOD_STONE_SLATTED_BLOCKS = copyBlockRegister("wood_stone_slatted_blocks", Blocks.OAK_PLANKS);
    public static final Supplier<Block> BLUE_ICE_BRICKS = copyBlockRegister("blue_ice_bricks", Blocks.BLUE_ICE);
    public static final Supplier<Block> PACKED_ICE_BRICKS = copyBlockRegister("packed_ice_bricks", Blocks.PACKED_ICE);
    public static final Supplier<Block> SNOW_BRICKS = copyBlockRegister("snow_bricks", Blocks.STONE_BRICKS);
    public static final Supplier<Block> TR_STONE_BRICKS = copyBlockRegister("tr_stone_bricks", Blocks.STONE_BRICKS);

    public static final Supplier<Block> PURE_GLASS = registerWithItem("pure_glass", () -> new ConnectedGlassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
    public static final Supplier<Block> WHITE_PURE_GLASS = registerWithItem("white_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.WHITE, BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_STAINED_GLASS)));
    public static final Supplier<Block> LIGHT_GRAY_PURE_GLASS = registerWithItem("light_gray_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.LIGHT_GRAY, BlockBehaviour.Properties.ofFullCopy(Blocks.LIGHT_GRAY_STAINED_GLASS)));
    public static final Supplier<Block> GRAY_PURE_GLASS = registerWithItem("gray_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.GRAY, BlockBehaviour.Properties.ofFullCopy(Blocks.GRAY_STAINED_GLASS)));
    public static final Supplier<Block> BLACK_PURE_GLASS = registerWithItem("black_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.BLACK, BlockBehaviour.Properties.ofFullCopy(Blocks.BLACK_STAINED_GLASS)));
    public static final Supplier<Block> BROWN_PURE_GLASS = registerWithItem("brown_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.BROWN, BlockBehaviour.Properties.ofFullCopy(Blocks.BROWN_STAINED_GLASS)));
    public static final Supplier<Block> RED_PURE_GLASS = registerWithItem("red_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.RED, BlockBehaviour.Properties.ofFullCopy(Blocks.RED_STAINED_GLASS)));
    public static final Supplier<Block> ORANGE_PURE_GLASS = registerWithItem("orange_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.ORANGE, BlockBehaviour.Properties.ofFullCopy(Blocks.ORANGE_STAINED_GLASS)));
    public static final Supplier<Block> YELLOW_PURE_GLASS = registerWithItem("yellow_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.YELLOW, BlockBehaviour.Properties.ofFullCopy(Blocks.YELLOW_STAINED_GLASS)));
    public static final Supplier<Block> LIME_PURE_GLASS = registerWithItem("lime_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.LIME, BlockBehaviour.Properties.ofFullCopy(Blocks.LIME_STAINED_GLASS)));
    public static final Supplier<Block> GREEN_PURE_GLASS = registerWithItem("green_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.GREEN, BlockBehaviour.Properties.ofFullCopy(Blocks.GREEN_STAINED_GLASS)));
    public static final Supplier<Block> CYAN_PURE_GLASS = registerWithItem("cyan_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.CYAN, BlockBehaviour.Properties.ofFullCopy(Blocks.CYAN_STAINED_GLASS)));
    public static final Supplier<Block> LIGHT_BLUE_PURE_GLASS = registerWithItem("light_blue_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.LIGHT_BLUE, BlockBehaviour.Properties.ofFullCopy(Blocks.LIGHT_BLUE_STAINED_GLASS)));
    public static final Supplier<Block> BLUE_PURE_GLASS = registerWithItem("blue_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.BLUE, BlockBehaviour.Properties.ofFullCopy(Blocks.BLUE_STAINED_GLASS)));
    public static final Supplier<Block> PURPLE_PURE_GLASS = registerWithItem("purple_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.PURPLE, BlockBehaviour.Properties.ofFullCopy(Blocks.PURPLE_STAINED_GLASS)));
    public static final Supplier<Block> MAGENTA_PURE_GLASS = registerWithItem("magenta_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.MAGENTA, BlockBehaviour.Properties.ofFullCopy(Blocks.MAGENTA_STAINED_GLASS)));
    public static final Supplier<Block> PINK_PURE_GLASS = registerWithItem("pink_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.PINK, BlockBehaviour.Properties.ofFullCopy(Blocks.PINK_STAINED_GLASS)));


    public static final Supplier<Block> TR_COPPER_BRICKS = copyBlockRegister("tr_copper_bricks", Blocks.COPPER_BLOCK);
    public static final Supplier<Block> ANCIENT_COPPER_BRICKS = copyBlockRegister("ancient_copper_bricks", Blocks.COPPER_BLOCK);
    public static final Supplier<Block> TR_COPPER_PLATE = copyBlockRegister("tr_copper_plate", Blocks.COPPER_BLOCK);
    public static final Supplier<Block> TIN_BRICKS = copyBlockRegister("tin_bricks", Blocks.COPPER_BLOCK);
    public static final Supplier<Block> ANCIENT_TIN_BRICKS = copyBlockRegister("ancient_tin_bricks", Blocks.COPPER_BLOCK);
    public static final Supplier<Block> TIN_PLATE = copyBlockRegister("tin_plate", Blocks.COPPER_BLOCK);
    public static final Supplier<Block> TR_IRON_BRICKS = copyBlockRegister("tr_iron_bricks", Blocks.IRON_BLOCK);
    public static final Supplier<Block> ANCIENT_IRON_BRICKS = copyBlockRegister("ancient_iron_bricks", Blocks.IRON_BLOCK);
//    public static final Supplier<Block> TR_IRON_PLATE = copyBlockRegister("tr_iron_plate", () -> new  BeamLikeBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK);

    public static final Supplier<Block> LEAD_BRICKS = copyBlockRegister("lead_bricks", Blocks.IRON_BLOCK);
    public static final Supplier<Block> ANCIENT_LEAD_BRICKS = copyBlockRegister("ancient_lead_bricks", Blocks.IRON_BLOCK);
    public static final Supplier<Block> SILVER_BRICKS = copyBlockRegister("silver_bricks", Blocks.IRON_BLOCK);
    public static final Supplier<Block> ANCIENT_SILVER_BRICKS = copyBlockRegister("ancient_silver_bricks", Blocks.IRON_BLOCK);
    public static final Supplier<Block> TUNGSTEN_BRICKS = copyBlockRegister("tungsten_bricks", Blocks.IRON_BLOCK);
    public static final Supplier<Block> ANCIENT_TUNGSTEN_BRICKS = copyBlockRegister("ancient_tungsten_bricks", Blocks.IRON_BLOCK);
    public static final Supplier<Block> TR_GOLD_BRICKS = copyBlockRegister("tr_gold_bricks", Blocks.IRON_BLOCK);
    public static final Supplier<Block> ANCIENT_GOLD_BRICKS = copyBlockRegister("ancient_gold_bricks", Blocks.IRON_BLOCK);
    public static final Supplier<Block> PLATINUM_BRICKS = copyBlockRegister("platinum_bricks", Blocks.IRON_BLOCK);
    public static final Supplier<Block> ANCIENT_PLATINUM_BRICKS = copyBlockRegister("ancient_platinum_bricks", Blocks.IRON_BLOCK);
    public static final Supplier<Block> EBONY_ORE_BRICKS = copyBlockRegister("ebony_ore_bricks", Blocks.IRON_BLOCK);
    public static final Supplier<Block> EBONY_ROCK_BRICKS = copyBlockRegister("ebony_rock_bricks", Blocks.IRON_BLOCK);
    public static final Supplier<Block> METEORITE_BRICKS = copyBlockRegister("meteorite_bricks", Blocks.IRON_BLOCK);
    public static final Supplier<Block> TR_CRIMSON_ORE_BRICKS = copyBlockRegister("tr_crimson_ore_bricks", Blocks.IRON_BLOCK);
    public static final Supplier<Block> TR_CRIMSON_ROCK_BRICKS = copyBlockRegister("tr_crimson_rock_bricks", Blocks.IRON_BLOCK);
    public static final Supplier<Block> PEARL_ROCK_BRICKS = copyBlockRegister("pearl_rock_bricks", Blocks.STONE_BRICKS);
    public static final Supplier<Block> GREEN_CANDY_BLOCK = copyBlockRegister("green_candy_block", Blocks.STONE_BRICKS);
    public static final Supplier<Block> RED_CANDY_BLOCK = copyBlockRegister("red_candy_block", Blocks.STONE_BRICKS);
    public static final Supplier<Block> FROZEN_GEL_BLOCK = copyBlockRegister("frozen_gel_block", Blocks.SLIME_BLOCK);
    public static final Supplier<Block> BLUE_GEL_BLOCK = copyBlockRegister("blue_gel_block", Blocks.SLIME_BLOCK);
    public static final Supplier<Block> PINK_GEL_BLOCK = copyBlockRegister("pink_gel_block", Blocks.SLIME_BLOCK);
    public static final Supplier<Block> SUN_PLATE = copyBlockRegister("sun_plate", Blocks.STONE_BRICKS);
    public static final Supplier<Block> DISC_BLOCK = copyBlockRegister("disc_block", Blocks.STONE_BRICKS);
    public static final Supplier<Block> TR_LAVA_BEAM = copyBlockRegister("tr_lava_beam", Blocks.STONE_BRICKS);
    public static final Supplier<Block> TR_LAVA_BRICKS = copyBlockRegister("tr_lava_bricks", Blocks.STONE_BRICKS);
    public static final Supplier<Block> TR_OBSIDIAN_BEAM = copyBlockRegister("tr_obsidian_beam", Blocks.STONE_BRICKS);
    public static final Supplier<Block> TR_OBSIDIAN_BRICKS = copyBlockRegister("tr_obsidian_bricks", Blocks.STONE_BRICKS);
    public static final Supplier<Block> TR_OBSIDIAN_PLATE = copyBlockRegister("tr_obsidian_plate", Blocks.STONE_BRICKS);
    public static final Supplier<Block> TR_OBSIDIAN_SMALL_BRICKS = copyBlockRegister("tr_obsidian_small_bricks", Blocks.STONE_BRICKS);
    public static final Supplier<Block> TR_SMOOTH_OBSIDIAN = copyBlockRegister("tr_smooth_obsidian", Blocks.STONE_BRICKS);
    public static final Supplier<Block> TR_GRANITE_COLUMN = copyBlockRegister("tr_granite_column", Blocks.STONE_BRICKS);
    public static final Supplier<Block> MARBLE_COLUMN = copyBlockRegister("marble_column", Blocks.STONE_BRICKS);
    public static final Supplier<Block> POLISHED_MARBLE = copyBlockRegister("polished_marble", Blocks.STONE_BRICKS);


    public static final Supplier<Block> CHISELED_TR_OBSIDIAN_BRICKS = copyBlockRegister("chiseled_tr_obsidian_bricks", Blocks.STONE_BRICKS);
    public static final Supplier<Block> BLUE_BRICKS = copyBlockRegister("blue_bricks", Blocks.STONE_BRICKS);
    public static final Supplier<Block> GREEN_BRICKS = copyBlockRegister("green_bricks", Blocks.STONE_BRICKS);
    public static final Supplier<Block> PINK_BRICKS = copyBlockRegister("pink_bricks", Blocks.STONE_BRICKS);
    public static final Supplier<Block> CHISELED_BLUE_BRICKS = copyBlockRegister("chiseled_blue_bricks", Blocks.STONE_BRICKS);
    public static final Supplier<Block> CHISELED_GREEN_BRICKS = copyBlockRegister("chiseled_green_bricks", Blocks.STONE_BRICKS);
    public static final Supplier<Block> CHISELED_PINK_BRICKS = copyBlockRegister("chiseled_pink_bricks", Blocks.STONE_BRICKS);
    public static final Supplier<Block> AETHERIUM_BRICKS = copyBlockRegister("aetherium_bricks", Blocks.STONE_BRICKS);
    public static final Supplier<Block> CRYSTAL_BLOCKS = copyBlockRegister("crystal_blocks", Blocks.AMETHYST_BLOCK);
    public static final Supplier<Block> RAINBOW_BRICKS = copyBlockRegister("rainbow_bricks", Blocks.STONE_BRICKS);
    public static final Supplier<CloudBlock> FLOATING_WHEAT_BALE = registerWithItem("floating_wheat_bale", CloudBlock::new);
    // 大宝石块
    public static final Supplier<Block> BIG_RUBY_BLOCK = copyBlockRegister("big_ruby_block", Blocks.DIAMOND_BLOCK);
    public static final Supplier<Block> BIG_AMBER_BLOCK = copyBlockRegister("big_amber_block", Blocks.DIAMOND_BLOCK);
    public static final Supplier<Block> BIG_TOPAZ_BLOCK = copyBlockRegister("big_topaz_block", Blocks.DIAMOND_BLOCK);
    public static final Supplier<Block> BIG_TR_EMERALD_BLOCK = copyBlockRegister("big_tr_emerald_block", Blocks.DIAMOND_BLOCK);
    public static final Supplier<Block> BIG_SAPPHIRE_BLOCK = copyBlockRegister("big_sapphire_block", Blocks.DIAMOND_BLOCK);
    public static final Supplier<Block> BIG_TR_AMETHYST_BLOCK = copyBlockRegister("big_tr_amethyst_block", Blocks.DIAMOND_BLOCK);
    public static final Supplier<Block> TR_POLISHED_GRANITE = copyBlockRegister("tr_polished_granite", Blocks.DIAMOND_BLOCK);


    // 锁链
    public static final Supplier<ChainBlock> RUBY_CHAIN = copyBlockRegister("ruby_chain", Blocks.CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.COLOR_RED)));
    public static final Supplier<ChainBlock> AMBER_CHAIN = copyBlockRegister("amber_chain", Blocks.CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.COLOR_ORANGE)));
    public static final Supplier<ChainBlock> TOPAZ_CHAIN = copyBlockRegister("topaz_chain", Blocks.CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.COLOR_YELLOW)));
    public static final Supplier<ChainBlock> EMERALD_CHAIN = copyBlockRegister("emerald_chain", Blocks.CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.EMERALD)));
    public static final Supplier<ChainBlock> SAPPHIRE_CHAIN = copyBlockRegister("sapphire_chain", Blocks.CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.COLOR_BLUE)));
    public static final Supplier<ChainBlock> DIAMOND_CHAIN = copyBlockRegister("diamond_chain", Blocks.CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.DIAMOND)));
    public static final Supplier<ChainBlock> AMETHYST_CHAIN = copyBlockRegister("amethyst_chain", Blocks.CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.COLOR_PURPLE)));
    public static final Supplier<ChainBlock> SILK_CHAIN = copyBlockRegister("silk_chain", Blocks.CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final Supplier<ChainBlock> BONE_CHAIN = copyBlockRegister("bone_chain", Blocks.CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.TERRACOTTA_WHITE)));

    // 丛林蜥蜴砖
    public static final Supplier<Block> LIHZAHRD_BRICKS = registerWithItem("lihzahrd_bricks", () -> new Block(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_BROWN)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops()
            .strength(100.0F, ModBlocks.getObsidianBasedExplosionResistance(1000.0F))
    ));

    private static Supplier<Block> copyBlockRegister(String newName, Block originalBlock) {
        DeferredBlock<Block> block = BLOCKS.registerSimpleBlock(newName, BlockBehaviour.Properties.ofFullCopy(originalBlock));
        ModItems.BLOCK_ITEMS.registerSimpleBlockItem(newName, block);
        return block;
    }

    private static <B extends Block> Supplier<B> copyBlockRegister(String newName, Block originalBlock, Function<BlockBehaviour.Properties, B> function) {
        DeferredBlock<B> block = BLOCKS.register(newName, () -> function.apply(BlockBehaviour.Properties.ofFullCopy(originalBlock)));
        ModItems.BLOCK_ITEMS.registerSimpleBlockItem(newName, block);
        return block;
    }

    private static <B extends Block> Supplier<B> registerWithItem(String newName, Supplier<B> supplier) {
        DeferredBlock<B> block = BLOCKS.register(newName, supplier);
        ModItems.BLOCK_ITEMS.registerSimpleBlockItem(newName, block);
        return block;
    }
}
