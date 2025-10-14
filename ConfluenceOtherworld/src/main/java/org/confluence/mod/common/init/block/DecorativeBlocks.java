package org.confluence.mod.common.init.block;

import com.mojang.datafixers.DSL;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.common.*;
import org.confluence.mod.common.block.natural.*;
import org.confluence.mod.common.block.palettes.ConnectedGlassBlock;
import org.confluence.mod.common.block.palettes.ConnectedStainedGlassBlock;
import org.confluence.mod.common.init.item.ModItems;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraft.world.level.block.Blocks.*;

public class DecorativeBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Confluence.MODID);
    public static final List<DeferredBlock<RelicBlock>> RELIC_BLOCKS = new ArrayList<>();

    // 杂项
    public static final DeferredBlock<MuralBlock> MURAL_BLOCK = registerWithItem("mural_block", () -> new MuralBlock(BlockBehaviour.Properties.ofFullCopy(STONE)));
    public static final Supplier<BlockEntityType<MuralBlock.BEntity>> MURAL_ENTITY_BLOCK = ModBlocks.BLOCK_ENTITIES.register("mural_entity_block", () -> BlockEntityType.Builder.of(MuralBlock.BEntity::new, MURAL_BLOCK.get()).build(DSL.remainderType()));
    // 雕纹木板
    public static final DeferredBlock<Block> CHISELED_OAK_PLANKS = copyBlockRegister("chiseled_oak_planks", OAK_PLANKS);
    public static final DeferredBlock<Block> CHISELED_SPRUCE_PLANKS = copyBlockRegister("chiseled_spruce_planks", SPRUCE_PLANKS);
    public static final DeferredBlock<Block> WOOD_STONE_SLATTED_BLOCKS = copyBlockRegister("wood_stone_slatted_blocks", OAK_PLANKS);

    // 砖
    public static final DeferredBlock<Block> BLUE_ICE_BRICKS = registerWithItem("blue_ice_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(BLUE_ICE).mapColor(MapColor.COLOR_LIGHT_BLUE)));
    public static final DeferredBlock<StairBlock> BLUE_ICE_BRICKS_STAIRS = registerWithItem("blue_ice_bricks_stairs", () -> new StairBlock(BLUE_ICE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(BLUE_ICE).mapColor(MapColor.COLOR_LIGHT_BLUE)));
    public static final DeferredBlock<SlabBlock> BLUE_ICE_BRICKS_SLAB = registerWithItem("blue_ice_bricks_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(BLUE_ICE).mapColor(MapColor.COLOR_LIGHT_BLUE)));

    public static final DeferredBlock<Block> PACKED_ICE_BRICKS = registerWithItem("packed_ice_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(PACKED_ICE).mapColor(MapColor.COLOR_LIGHT_BLUE)));
    public static final DeferredBlock<StairBlock> PACKED_ICE_BRICKS_STAIRS = registerWithItem("packed_ice_bricks_stairs", () -> new StairBlock(PACKED_ICE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(PACKED_ICE).mapColor(MapColor.COLOR_LIGHT_BLUE)));
    public static final DeferredBlock<SlabBlock> PACKED_ICE_BRICKS_SLAB = registerWithItem("packed_ice_bricks_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(PACKED_ICE).mapColor(MapColor.COLOR_LIGHT_BLUE)));

    public static final DeferredBlock<Block> SANDSTONE_BRICKS = registerWithItem("sandstone_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(SANDSTONE).mapColor(MapColor.SAND)));
    public static final DeferredBlock<StairBlock> SANDSTONE_BRICKS_STAIRS = registerWithItem("sandstone_bricks_stairs", () -> new StairBlock(SANDSTONE.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(RED_SANDSTONE).mapColor(MapColor.SAND)));
    public static final DeferredBlock<SlabBlock> SANDSTONE_BRICKS_SLAB = registerWithItem("sandstone_bricks_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(SANDSTONE).mapColor(MapColor.SAND)));
    public static final DeferredBlock<WallBlock> SANDSTONE_BRICKS_WALL = registerWithItem("sandstone_bricks_wall", () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(SANDSTONE).mapColor(MapColor.SAND)));

    public static final DeferredBlock<Block> RED_SANDSTONE_BRICKS = registerWithItem("red_sandstone_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(RED_SANDSTONE).mapColor(MapColor.TERRACOTTA_RED)));
    public static final DeferredBlock<StairBlock> RED_SANDSTONE_BRICKS_STAIRS = registerWithItem("red_sandstone_bricks_stairs", () -> new StairBlock(RED_SANDSTONE.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(RED_SANDSTONE).mapColor(MapColor.TERRACOTTA_RED)));
    public static final DeferredBlock<SlabBlock> RED_SANDSTONE_BRICKS_SLAB = registerWithItem("red_sandstone_bricks_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(RED_SANDSTONE).mapColor(MapColor.TERRACOTTA_RED)));
    public static final DeferredBlock<WallBlock> RED_SANDSTONE_BRICKS_WALL = registerWithItem("red_sandstone_bricks_wall", () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(RED_SANDSTONE).mapColor(MapColor.TERRACOTTA_RED)));

    public static final DeferredBlock<Block> EBONSANDSTONE_BRICKS = registerWithItem("ebonsandstone_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(SANDSTONE).mapColor(MapColor.TERRACOTTA_PURPLE)));
    public static final DeferredBlock<StairBlock> EBONSANDSTONE_BRICKS_STAIRS = registerWithItem("ebonsandstone_bricks_stairs", () -> new StairBlock(SANDSTONE.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(SANDSTONE).mapColor(MapColor.TERRACOTTA_PURPLE)));
    public static final DeferredBlock<SlabBlock> EBONSANDSTONE_BRICKS_SLAB = registerWithItem("ebonsandstone_bricks_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(SANDSTONE).mapColor(MapColor.TERRACOTTA_PURPLE)));
    public static final DeferredBlock<WallBlock> EBONSANDSTONE_BRICKS_WALL = registerWithItem("ebonsandstone_bricks_wall", () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(SANDSTONE).mapColor(MapColor.TERRACOTTA_PURPLE)));

    public static final DeferredBlock<Block> PEARLSANDSTONE_BRICKS = registerWithItem("pearlsandstone_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(SANDSTONE).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<StairBlock> PEARLSANDSTONE_BRICKS_STAIRS = registerWithItem("pearlsandstone_bricks_stairs", () -> new StairBlock(SANDSTONE.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(SANDSTONE).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<SlabBlock> PEARLSANDSTONE_BRICKS_SLAB = registerWithItem("pearlsandstone_bricks_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(SANDSTONE).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<WallBlock> PEARLSANDSTONE_BRICKS_WALL = registerWithItem("pearlsandstone_bricks_wall", () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(SANDSTONE).mapColor(MapColor.TERRACOTTA_WHITE)));

    public static final DeferredBlock<Block> CRIMSANDSTONE_BRICKS = registerWithItem("crimsandstone_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(SANDSTONE).mapColor(MapColor.TERRACOTTA_GRAY)));
    public static final DeferredBlock<StairBlock> CRIMSANDSTONE_BRICKS_STAIRS = registerWithItem("crimsandstone_bricks_stairs", () -> new StairBlock(SANDSTONE.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(SANDSTONE).mapColor(MapColor.TERRACOTTA_GRAY)));
    public static final DeferredBlock<SlabBlock> CRIMSANDSTONE_BRICKS_SLAB = registerWithItem("crimsandstone_bricks_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(SANDSTONE).mapColor(MapColor.TERRACOTTA_GRAY)));
    public static final DeferredBlock<WallBlock> CRIMSANDSTONE_BRICKS_WALL = registerWithItem("crimsandstone_bricks_wall", () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(SANDSTONE).mapColor(MapColor.TERRACOTTA_GRAY)));

    public static final DeferredBlock<Block> SNOW_BRICKS = registerWithItem("snow_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<StairBlock> SNOW_BRICKS_STAIRS = registerWithItem("snow_bricks_stairs", () -> new StairBlock(SNOW_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<SlabBlock> SNOW_BRICKS_SLAB = registerWithItem("snow_bricks_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<WallBlock> SNOW_BRICKS_WALL = registerWithItem("snow_bricks_wall", () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_WHITE)));

    public static final DeferredBlock<Block> AETHERIUM_BRICKS = registerWithItem("aetherium_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_PINK)));

    public static final DeferredBlock<Block> CRYSTAL_BLOCK = registerWithItem("crystal_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(MapColor.TERRACOTTA_WHITE)));

    public static final DeferredBlock<Block> RAINBOW_BRICKS = copyBlockRegister("rainbow_bricks", STONE_BRICKS);

    public static final DeferredBlock<CloudBlock> FLOATING_WHEAT_BALE = registerWithItem("floating_wheat_bale", () -> new CloudBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_LIGHT_BLUE)
            .strength(0.3F)
            .sound(SoundType.SNOW)
            .isValidSpawn(Blocks::never)
            .isRedstoneConductor((blockState, blockGetter, blockPos) -> false)
            .isSuffocating((blockState, blockGetter, blockPos) -> false)));

    // 云
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
    // 石中剑
    public static final DeferredBlock<SwordInStoneBlock> SWORD_IN_STONE = registerWithItem("sword_in_stone", SwordInStoneBlock::new);

    public static final DeferredBlock<CrispyHoneyBlock> CRISPY_HONEY_BLOCK = registerWithItem("crispy_honey_block", CrispyHoneyBlock::new);

    public static final DeferredBlock<Block> ASPHALT_BLOCK = registerWithItem("asphalt_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(BLACK_TERRACOTTA).friction(0.4F).speedFactor(1.2F)));
    public static final DeferredBlock<Block> FLESH_BLOCK = registerWithItem("flesh_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE)));
    public static final DeferredBlock<Block> LESION_BLOCK = registerWithItem("lesion_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE)));
    public static final DeferredBlock<Block> REMAINS_BLOCK = registerWithItem("remains_block", () -> new RemainsBlock(BlockBehaviour.Properties.of().strength(1.0f).pushReaction(PushReaction.DESTROY)));

    // 纯净玻璃
    public static final DeferredBlock<Block> PURE_GLASS = registerWithItem("pure_glass", () -> new ConnectedGlassBlock(BlockBehaviour.Properties.ofFullCopy(GLASS)));
    public static final DeferredBlock<Block> WHITE_PURE_GLASS = registerWithItem("white_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.WHITE, BlockBehaviour.Properties.ofFullCopy(WHITE_STAINED_GLASS).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<Block> LIGHT_GRAY_PURE_GLASS = registerWithItem("light_gray_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.LIGHT_GRAY, BlockBehaviour.Properties.ofFullCopy(LIGHT_GRAY_STAINED_GLASS).mapColor(MapColor.COLOR_LIGHT_GRAY)));
    public static final DeferredBlock<Block> GRAY_PURE_GLASS = registerWithItem("gray_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.GRAY, BlockBehaviour.Properties.ofFullCopy(GRAY_STAINED_GLASS).mapColor(MapColor.COLOR_GRAY)));
    public static final DeferredBlock<Block> BLACK_PURE_GLASS = registerWithItem("black_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.BLACK, BlockBehaviour.Properties.ofFullCopy(BLACK_STAINED_GLASS).mapColor(MapColor.COLOR_BLACK)));
    public static final DeferredBlock<Block> BROWN_PURE_GLASS = registerWithItem("brown_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.BROWN, BlockBehaviour.Properties.ofFullCopy(BROWN_STAINED_GLASS).mapColor(MapColor.COLOR_BROWN)));
    public static final DeferredBlock<Block> RED_PURE_GLASS = registerWithItem("red_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.RED, BlockBehaviour.Properties.ofFullCopy(RED_STAINED_GLASS).mapColor(MapColor.COLOR_RED)));
    public static final DeferredBlock<Block> ORANGE_PURE_GLASS = registerWithItem("orange_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.ORANGE, BlockBehaviour.Properties.ofFullCopy(ORANGE_STAINED_GLASS).mapColor(MapColor.COLOR_ORANGE)));
    public static final DeferredBlock<Block> YELLOW_PURE_GLASS = registerWithItem("yellow_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.YELLOW, BlockBehaviour.Properties.ofFullCopy(YELLOW_STAINED_GLASS).mapColor(MapColor.COLOR_YELLOW)));
    public static final DeferredBlock<Block> LIME_PURE_GLASS = registerWithItem("lime_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.LIME, BlockBehaviour.Properties.ofFullCopy(LIME_STAINED_GLASS).mapColor(MapColor.COLOR_LIGHT_GREEN)));
    public static final DeferredBlock<Block> GREEN_PURE_GLASS = registerWithItem("green_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.GREEN, BlockBehaviour.Properties.ofFullCopy(GREEN_STAINED_GLASS).mapColor(MapColor.TERRACOTTA_GREEN)));
    public static final DeferredBlock<Block> CYAN_PURE_GLASS = registerWithItem("cyan_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.CYAN, BlockBehaviour.Properties.ofFullCopy(CYAN_STAINED_GLASS).mapColor(MapColor.COLOR_CYAN)));
    public static final DeferredBlock<Block> LIGHT_BLUE_PURE_GLASS = registerWithItem("light_blue_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.LIGHT_BLUE, BlockBehaviour.Properties.ofFullCopy(LIGHT_BLUE_STAINED_GLASS).mapColor(MapColor.COLOR_LIGHT_BLUE)));
    public static final DeferredBlock<Block> BLUE_PURE_GLASS = registerWithItem("blue_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.BLUE, BlockBehaviour.Properties.ofFullCopy(BLUE_STAINED_GLASS).mapColor(MapColor.TERRACOTTA_BLUE)));
    public static final DeferredBlock<Block> PURPLE_PURE_GLASS = registerWithItem("purple_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.PURPLE, BlockBehaviour.Properties.ofFullCopy(PURPLE_STAINED_GLASS).mapColor(MapColor.COLOR_PURPLE)));
    public static final DeferredBlock<Block> MAGENTA_PURE_GLASS = registerWithItem("magenta_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.MAGENTA, BlockBehaviour.Properties.ofFullCopy(MAGENTA_STAINED_GLASS).mapColor(MapColor.TERRACOTTA_MAGENTA)));
    public static final DeferredBlock<Block> PINK_PURE_GLASS = registerWithItem("pink_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.PINK, BlockBehaviour.Properties.ofFullCopy(PINK_STAINED_GLASS).mapColor(MapColor.COLOR_PINK)));

    // 矿砖
    public static final DeferredBlock<Block> COPPER_BRICKS = registerWithItem("copper_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_ORANGE)));
    public static final DeferredBlock<StairBlock> COPPER_BRICKS_STAIRS = registerWithItem("copper_bricks_stairs", () -> new StairBlock(COPPER_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_ORANGE)));
    public static final DeferredBlock<SlabBlock> COPPER_BRICKS_SLAB = registerWithItem("copper_bricks_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_ORANGE)));
    public static final DeferredBlock<Block> TIN_BRICKS = registerWithItem("tin_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_ORANGE)));
    public static final DeferredBlock<StairBlock> TIN_BRICKS_STAIRS = registerWithItem("tin_bricks_stairs", () -> new StairBlock(TIN_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_ORANGE)));
    public static final DeferredBlock<SlabBlock> TIN_BRICKS_SLAB = registerWithItem("tin_bricks_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_ORANGE)));
    public static final DeferredBlock<Block> IRON_BRICKS = registerWithItem("iron_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)));
    public static final DeferredBlock<StairBlock> IRON_BRICKS_STAIRS = registerWithItem("iron_bricks_stairs", () -> new StairBlock(IRON_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)));
    public static final DeferredBlock<SlabBlock> IRON_BRICKS_SLAB = registerWithItem("iron_bricks_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)));
    public static final DeferredBlock<Block> LEAD_BRICKS = registerWithItem("lead_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_GRAY)));
    public static final DeferredBlock<StairBlock> LEAD_BRICKS_STAIRS = registerWithItem("lead_bricks_stairs", () -> new StairBlock(LEAD_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_GRAY)));
    public static final DeferredBlock<SlabBlock> LEAD_BRICKS_SLAB = registerWithItem("lead_bricks_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_GRAY)));
    public static final DeferredBlock<Block> SILVER_BRICKS = registerWithItem("silver_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<StairBlock> SILVER_BRICKS_STAIRS = registerWithItem("silver_bricks_stairs", () -> new StairBlock(SILVER_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<SlabBlock> SILVER_BRICKS_SLAB = registerWithItem("silver_bricks_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<Block> TUNGSTEN_BRICKS = registerWithItem("tungsten_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_LIGHT_GREEN)));
    public static final DeferredBlock<StairBlock> TUNGSTEN_BRICKS_STAIRS = registerWithItem("tungsten_bricks_stairs", () -> new StairBlock(TUNGSTEN_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_LIGHT_GREEN)));
    public static final DeferredBlock<SlabBlock> TUNGSTEN_BRICKS_SLAB = registerWithItem("tungsten_bricks_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_LIGHT_GREEN)));
    public static final DeferredBlock<Block> GOLDEN_BRICKS = registerWithItem("golden_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_YELLOW)));
    public static final DeferredBlock<StairBlock> GOLDEN_BRICKS_STAIRS = registerWithItem("golden_bricks_stairs", () -> new StairBlock(GOLDEN_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_YELLOW)));
    public static final DeferredBlock<SlabBlock> GOLDEN_BRICKS_SLAB = registerWithItem("golden_bricks_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_YELLOW)));
    public static final DeferredBlock<Block> PLATINUM_BRICKS = registerWithItem("platinum_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_CYAN)));
    public static final DeferredBlock<StairBlock> PLATINUM_BRICKS_STAIRS = registerWithItem("platinum_bricks_stairs", () -> new StairBlock(PLATINUM_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_CYAN)));
    public static final DeferredBlock<SlabBlock> PLATINUM_BRICKS_SLAB = registerWithItem("platinum_bricks_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_CYAN)));
    public static final DeferredBlock<Block> DEMONITE_ORE_BRICKS = registerWithItem("demonite_ore_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<StairBlock> DEMONITE_ORE_BRICKS_STAIRS = registerWithItem("demonite_ore_bricks_stairs", () -> new StairBlock(DEMONITE_ORE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<SlabBlock> DEMONITE_ORE_BRICKS_SLAB = registerWithItem("demonite_ore_bricks_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<Block> EBONSTONE_BRICKS = registerWithItem("ebonstone_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_PURPLE)));
    public static final DeferredBlock<StairBlock> EBONSTONE_BRICKS_STAIRS = registerWithItem("ebonstone_bricks_stairs", () -> new StairBlock(EBONSTONE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_PURPLE)));
    public static final DeferredBlock<SlabBlock> EBONSTONE_BRICKS_SLAB = registerWithItem("ebonstone_bricks_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_PURPLE)));
    public static final DeferredBlock<Block> METEORITE_BRICKS = registerWithItem("meteorite_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_ORANGE)));
    public static final DeferredBlock<StairBlock> METEORITE_BRICKS_STAIRS = registerWithItem("meteorite_bricks_stairs", () -> new StairBlock(METEORITE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_ORANGE)));
    public static final DeferredBlock<SlabBlock> METEORITE_BRICKS_SLAB = registerWithItem("meteorite_bricks_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_ORANGE)));
    public static final DeferredBlock<Block> CRIMTANE_ORE_BRICKS = registerWithItem("crimtane_ore_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_RED)));
    public static final DeferredBlock<StairBlock> CRIMTANE_ORE_BRICKS_STAIRS = registerWithItem("crimtane_ore_bricks_stairs", () -> new StairBlock(CRIMTANE_ORE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_RED)));
    public static final DeferredBlock<SlabBlock> CRIMTANE_ORE_BRICKS_SLAB = registerWithItem("crimtane_ore_bricks_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_RED)));
    public static final DeferredBlock<Block> CRIMSTONE_BRICKS = registerWithItem("crimstone_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_RED)));
    public static final DeferredBlock<StairBlock> CRIMSTONE_BRICKS_STAIRS = registerWithItem("crimstone_bricks_stairs", () -> new StairBlock(CRIMSTONE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_RED)));
    public static final DeferredBlock<SlabBlock> CRIMSTONE_BRICKS_SLAB = registerWithItem("crimstone_bricks_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_RED)));
    public static final DeferredBlock<Block> PEARLSTONE_BRICKS = registerWithItem("pearlstone_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_PINK)));
    public static final DeferredBlock<StairBlock> PEARLSTONE_BRICKS_STAIRS = registerWithItem("pearlstone_bricks_stairs", () -> new StairBlock(PEARLSTONE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_PINK)));
    public static final DeferredBlock<SlabBlock> PEARLSTONE_BRICKS_SLAB = registerWithItem("pearlstone_bricks_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_PINK)));

    public static final DeferredBlock<Block> GREEN_CANDY_BLOCK = registerWithItem("green_candy_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.COLOR_GREEN)));
    public static final DeferredBlock<Block> RED_CANDY_BLOCK = registerWithItem("red_candy_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.COLOR_RED)));
    public static final DeferredBlock<Block> FROZEN_GEL_BLOCK = registerWithItem("frozen_gel_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.SLIME_BLOCK).friction(1f).speedFactor(1.06F)));
    public static final DeferredBlock<Block> BLUE_GEL_BLOCK = registerWithItem("blue_gel_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).sound(SoundType.SLIME_BLOCK)));
    public static final DeferredBlock<Block> PINK_GEL_BLOCK = registerWithItem("pink_gel_block", () -> new HalfTransparentBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).sound(SoundType.SLIME_BLOCK).noOcclusion()));

    // 天域
    public static final DeferredBlock<StairBlock> SUN_PLATE_STAIRS = registerWithItem("sun_plate_stairs", () -> new StairBlock(TUFF.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_BLUE)));
    public static final DeferredBlock<SlabBlock> SUN_PLATE_SLAB = registerWithItem("sun_plate_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_BLUE)));
    public static final DeferredBlock<DoorBlock> SKYWARE_DOOR = registerWithItem("skyware_door", () -> new DoorBlock(BlockSetType.STONE, BlockBehaviour.Properties.of().mapColor(BLUE_ICE.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(4.0F).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<DoorBlock> SKYWARE_GLASS_DOOR = registerWithItem("skyware_glass_door", () -> new DoorBlock(BlockSetType.STONE, BlockBehaviour.Properties.of().mapColor(BLUE_ICE.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(4.0F).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> SUN_PLATE = registerWithItem("sun_plate", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_BLUE)));
    public static final DeferredBlock<Block> DISC_BLOCK = registerWithItem("disc_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.COLOR_YELLOW)));

    // 黑曜石
    public static final DeferredBlock<Block> OBSIDIAN_BRICKS = copyBlockRegister("obsidian_bricks", OBSIDIAN);
    public static final DeferredBlock<Block> OBSIDIAN_SMALL_BRICKS = copyBlockRegister("obsidian_small_bricks", OBSIDIAN);
    public static final DeferredBlock<StairBlock> OBSIDIAN_BRICKS_STAIRS = registerWithItem("obsidian_bricks_stairs", () -> new StairBlock(TUFF.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(OBSIDIAN)));
    public static final DeferredBlock<SlabBlock> OBSIDIAN_BRICKS_SLAB = registerWithItem("obsidian_bricks_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(OBSIDIAN)));
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
    public static final DeferredBlock<Block> CHISELED_OBSIDIAN_BRICKS = copyBlockRegister("chiseled_obsidian_bricks", OBSIDIAN);
    public static final DeferredBlock<Block> SMOOTH_OBSIDIAN = copyBlockRegister("smooth_obsidian", OBSIDIAN);

    // 王朝木系列
    public static final DeferredBlock<Block> WHITE_PAPER_PANE = copyBlockRegister("white_paper_pane", OAK_PLANKS);
    public static final DeferredBlock<Block> WHITE_PAPER_PANE_LAMP = registerWithItem("white_paper_pane_lamp", () -> new Block(BlockBehaviour.Properties.ofFullCopy(OAK_PLANKS).lightLevel(state -> 15)));
    public static final DeferredBlock<Block> MALACHITE_PAPER_PANE = copyBlockRegister("malachite_paper_pane", OAK_PLANKS);
    public static final DeferredBlock<Block> MALACHITE_PAPER_PANE_LAMP = registerWithItem("malachite_paper_pane_lamp", () -> new Block(BlockBehaviour.Properties.ofFullCopy(OAK_PLANKS).lightLevel(state -> 15)));
    public static final DeferredBlock<DoorBlock> TRADITIONAL_DYNASTY_DOOR = registerWithItem("traditional_dynasty_door", () -> new DoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(OAK_DOOR).mapColor(DyeColor.BROWN).pushReaction(PushReaction.BLOCK)));

    // 花岗岩
    public static final DeferredBlock<Block> GRANITE_COLUMN = registerWithItem("granite_column", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_BLUE)));
    public static final DeferredBlock<Block> GRANITE_BRICKS = registerWithItem("granite_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_BLUE)));
    public static final DeferredBlock<Block> POLISHED_GRANITE = registerWithItem("polished_granite", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_BLUE)));

    // 方解石
    public static final DeferredBlock<Block> MARBLE_COLUMN = registerWithItem("marble_column", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<Block> MARBLE_BRICKS = registerWithItem("marble_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<Block> CRACKED_MARBLE_BRICKS = registerWithItem("cracked_marble_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<Block> MARBLE_SMALL_BRICKS = registerWithItem("marble_small_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<Block> GILDED_MARBLE = registerWithItem("gilded_marble", () -> new GlazedTerracottaBlock(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<Block> POLISHED_MARBLE = registerWithItem("polished_marble", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)));

    // 地牢
    public static final DeferredBlock<Block> BLUE_BRICKS = registerWithItem("blue_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(DyeColor.BLUE).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<Block> GREEN_BRICKS = registerWithItem("green_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(DyeColor.GREEN).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<Block> PINK_BRICKS = registerWithItem("pink_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(DyeColor.PINK).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<CrackedBricksBlock> CRACKED_BLUE_BRICKS = registerWithItem("cracked_blue_bricks", () -> new CrackedBricksBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).strength(0).mapColor(DyeColor.BLUE)));
    public static final DeferredBlock<CrackedBricksBlock> CRACKED_GREEN_BRICKS = registerWithItem("cracked_green_bricks", () -> new CrackedBricksBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).strength(0).mapColor(DyeColor.GREEN)));
    public static final DeferredBlock<CrackedBricksBlock> CRACKED_PINK_BRICKS = registerWithItem("cracked_pink_bricks", () -> new CrackedBricksBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).strength(0).mapColor(DyeColor.PINK)));
    public static final DeferredBlock<Block> CHISELED_BLUE_BRICKS = registerWithItem("chiseled_blue_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(DyeColor.BLUE).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<Block> CHISELED_GREEN_BRICKS = registerWithItem("chiseled_green_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(DyeColor.GREEN).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<Block> CHISELED_PINK_BRICKS = registerWithItem("chiseled_pink_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(DyeColor.PINK).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<StairBlock> BLUE_BRICK_STAIRS = registerWithItem("blue_brick_stairs", () -> new StairBlock(BLUE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(STONE_STAIRS).mapColor(DyeColor.BLUE).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<StairBlock> GREEN_BRICK_STAIRS = registerWithItem("green_brick_stairs", () -> new StairBlock(GREEN_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(STONE_STAIRS).mapColor(DyeColor.GREEN).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<StairBlock> PINK_BRICK_STAIRS = registerWithItem("pink_brick_stairs", () -> new StairBlock(PINK_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(STONE_STAIRS).mapColor(DyeColor.PINK).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<SlabBlock> BLUE_BRICK_SLAB = registerWithItem("blue_brick_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(STONE_SLAB).mapColor(DyeColor.BLUE).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<SlabBlock> GREEN_BRICK_SLAB = registerWithItem("green_brick_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(STONE_SLAB).mapColor(DyeColor.GREEN).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<SlabBlock> PINK_BRICK_SLAB = registerWithItem("pink_brick_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(STONE_SLAB).mapColor(DyeColor.PINK).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<DoorBlock> DUNGEON_DOOR = registerWithItem("dungeon_door", () -> new DoorBlock(BlockSetType.STONE, BlockBehaviour.Properties.ofFullCopy(IRON_DOOR).mapColor(DyeColor.GRAY).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<EnchantedBricksBlock> ENCHANTED_BLUE_BRICKS = registerWithItem("enchanted_blue_bricks", () -> new EnchantedBricksBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(DyeColor.BLUE).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<EnchantedBricksBlock> ENCHANTED_GREEN_BRICKS = registerWithItem("enchanted_green_bricks", () -> new EnchantedBricksBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(DyeColor.BLUE).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<EnchantedBricksBlock> ENCHANTED_PINK_BRICKS = registerWithItem("enchanted_pink_bricks", () -> new EnchantedBricksBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(DyeColor.BLUE).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<RotatedPillarBlock> BLUE_BRICK_COLUMN = registerWithItem("blue_brick_column", () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(DyeColor.BLUE).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<RotatedPillarBlock> GREEN_BRICK_COLUMN = registerWithItem("green_brick_column", () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(DyeColor.GREEN).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<RotatedPillarBlock> PINK_BRICK_COLUMN = registerWithItem("pink_brick_column", () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(DyeColor.PINK).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));

    // 大宝石块
    public static final DeferredBlock<Block> RUBY_BLOCK = registerWithItem("ruby_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.COLOR_RED)));
    public static final DeferredBlock<Block> AMBER_BLOCK = registerWithItem("amber_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.COLOR_ORANGE)));
    public static final DeferredBlock<Block> TOPAZ_BLOCK = registerWithItem("topaz_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.COLOR_YELLOW)));
    public static final DeferredBlock<Block> JADE_BLOCK = registerWithItem("jade_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.EMERALD)));
    public static final DeferredBlock<Block> SAPPHIRE_BLOCK = registerWithItem("sapphire_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.COLOR_BLUE)));
    public static final DeferredBlock<Block> AMETHYST_BLOCK = registerWithItem("amethyst_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.COLOR_PURPLE)));

    // 锁链
    public static final DeferredBlock<ChainBlock> RUBY_CHAIN = copyBlockRegister("ruby_chain", CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.COLOR_RED)));
    public static final DeferredBlock<ChainBlock> AMBER_CHAIN = copyBlockRegister("amber_chain", CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.COLOR_ORANGE)));
    public static final DeferredBlock<ChainBlock> TOPAZ_CHAIN = copyBlockRegister("topaz_chain", CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.COLOR_YELLOW)));
    public static final DeferredBlock<ChainBlock> JADE_CHAIN = copyBlockRegister("jade_chain", CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.EMERALD)));
    public static final DeferredBlock<ChainBlock> SAPPHIRE_CHAIN = copyBlockRegister("sapphire_chain", CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.COLOR_BLUE)));
    public static final DeferredBlock<ChainBlock> DIAMOND_CHAIN = copyBlockRegister("diamond_chain", CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.DIAMOND)));
    public static final DeferredBlock<ChainBlock> AMETHYST_CHAIN = copyBlockRegister("amethyst_chain", CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.COLOR_PURPLE)));
    public static final DeferredBlock<ChainBlock> SILK_CHAIN = copyBlockRegister("silk_chain", CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<ChainBlock> BONE_CHAIN = copyBlockRegister("bone_chain", CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.TERRACOTTA_WHITE)));

    // 圣物
    public static final DeferredBlock<RelicBlock> KING_SLIME_RELIC = registerRelic("king_slime_relic");
    public static final DeferredBlock<RelicBlock> EYE_OF_CTHULHU_RELIC = registerRelic("eye_of_cthulhu_relic");
    public static final DeferredBlock<RelicBlock> BRAIN_OF_CTHULHU_RELIC = registerRelic("brain_of_cthulhu_relic");
    public static final DeferredBlock<RelicBlock> EATER_OF_WORLDS_RELIC = registerRelic("eater_of_worlds_relic");
    public static final DeferredBlock<RelicBlock> SKELETRON_RELIC = registerRelic("skeletron_relic");

    public static final Supplier<BlockEntityType<RelicBlock.BEntity>> RELIC_ENTITY = ModBlocks.BLOCK_ENTITIES.register("relic_entity", () -> BlockEntityType.Builder.of(RelicBlock.BEntity::new, RELIC_BLOCKS.stream().map(DeferredBlock::get).toArray(Block[]::new)).build(DSL.remainderType()));

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

    private static <B extends Block> DeferredBlock<B> registerWithItem(String id, Supplier<B> block, Function<B, BlockItem> function) {
        DeferredBlock<B> object = BLOCKS.register(id, block);
        ModItems.BLOCK_ITEMS.register(id, () -> function.apply(object.get()));
        return object;
    }

    public static DeferredBlock<RelicBlock> registerRelic(String id) {
        DeferredBlock<RelicBlock> block = registerWithItem(id, () -> new RelicBlock(BlockBehaviour.Properties.ofFullCopy(GOLD_BLOCK).lightLevel(state -> 7)), RelicBlock.BItem::new);
        RELIC_BLOCKS.add(block);
        return block;
    }
}
