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
import org.confluence.mod.common.block.palettes.DecoBlockSet;
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

    public static final DeferredBlock<LostPaperBlock> LOST_PAPER_BLOCK = registerWithItem("lost_paper", LostPaperBlock::new);

    public static final DeferredBlock<CarvedPumpkinBlock> CARVED_WHITE_PUMPKIN = registerWithItem("carved_white_pumpkin", () -> new CarvedPumpkinBlock(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).instrument(NoteBlockInstrument.DIDGERIDOO).strength(1.0F).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<CarvedPumpkinBlock> JOHNNY_O_LANTERN = registerWithItem("johnny_o_lantern", () -> new CarvedPumpkinBlock(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).mapColor(MapColor.COLOR_ORANGE).strength(1.0F).sound(SoundType.WOOD).lightLevel(p_187437_ -> 15).isValidSpawn(Blocks::always).pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<Block> GOLDEN_MELON = registerWithItem("golden_melon", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_YELLOW)));
    public static final DeferredBlock<Block> ICE_MELON = registerWithItem("ice_melon", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_BLUE)));


    // 雕纹木板
    public static final DeferredBlock<Block> CHISELED_OAK_PLANKS = copyBlockRegister("chiseled_oak_planks", OAK_PLANKS);
    public static final DeferredBlock<Block> CHISELED_SPRUCE_PLANKS = copyBlockRegister("chiseled_spruce_planks", SPRUCE_PLANKS);
    public static final DeferredBlock<Block> WOOD_STONE_SLATTED_BLOCKS = copyBlockRegister("wood_stone_slatted_blocks", OAK_PLANKS);

    public static final DeferredBlock<PooBlock> POO_BLOCK = registerWithItem("poo_block", () -> new PooBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD).mapColor(MapColor.COLOR_BROWN)));

    // 砖
    public static final DecoBlockSet BLUE_ICE_BRICKS = DecoBlockSet.builder("blue_ice_bricks", () -> BlockBehaviour.Properties.ofFullCopy(Blocks.BLUE_ICE).mapColor(MapColor.COLOR_LIGHT_BLUE)).stonecutting().build();
    public static final DecoBlockSet PACKED_ICE_BRICKS = DecoBlockSet.builder("packed_ice_bricks", () -> BlockBehaviour.Properties.ofFullCopy(Blocks.PACKED_ICE).mapColor(MapColor.COLOR_LIGHT_BLUE)).stonecutting().build();
    public static final DecoBlockSet SANDSTONE_BRICKS = DecoBlockSet.builder("sandstone_bricks", () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).mapColor(MapColor.SAND)).stonecutting().build();
    public static final DecoBlockSet RED_SANDSTONE_BRICKS = DecoBlockSet.builder("red_sandstone_bricks", () -> BlockBehaviour.Properties.ofFullCopy(Blocks.RED_SANDSTONE).mapColor(MapColor.TERRACOTTA_RED)).stonecutting().build();
    public static final DecoBlockSet EBONSANDSTONE_BRICKS = DecoBlockSet.builder("ebonsandstone_bricks", () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).mapColor(MapColor.TERRACOTTA_PURPLE)).stonecutting().build();
    public static final DecoBlockSet PEARLSANDSTONE_BRICKS = DecoBlockSet.builder("pearlsandstone_bricks", () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).mapColor(MapColor.TERRACOTTA_WHITE)).stonecutting().build();
    public static final DecoBlockSet CRIMSANDSTONE_BRICKS = DecoBlockSet.builder("crimsandstone_bricks", () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).mapColor(MapColor.TERRACOTTA_GRAY)).stonecutting().build();
    public static final DecoBlockSet SNOW_BRICKS = DecoBlockSet.builder("snow_bricks", () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(MapColor.TERRACOTTA_WHITE)).stonecutting().build();
    public static final DecoBlockSet AETHERIUM_BRICKS = DecoBlockSet.builder("aetherium_bricks", () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(MapColor.TERRACOTTA_WHITE)).stonecutting().build();
    public static final DecoBlockSet RAINBOW_BRICKS = DecoBlockSet.builder("rainbow_bricks", () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(MapColor.TERRACOTTA_WHITE)).stonecutting().build();
    public static final DeferredBlock<Block> CRYSTAL_BLOCK = registerWithItem("crystal_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(MapColor.TERRACOTTA_WHITE)));


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
    public static final DeferredBlock<CloudBlock> STAR_CLOUD_BLOCK = registerWithItem("star_cloud_block", () -> new CloudBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.TERRACOTTA_WHITE)
            .mapColor(MapColor.SNOW)
            .strength(0.3F)
            .sound(SoundType.SNOW)
            .noOcclusion()
            .lightLevel(state -> 11)
            .isValidSpawn(Blocks::never)
            .isRedstoneConductor((blockState, blockGetter, blockPos) -> false)
            .isSuffocating((blockState, blockGetter, blockPos) -> false)));
    // 石中剑
    public static final DeferredBlock<SwordInStoneBlock> SWORD_IN_STONE = registerWithItem("sword_in_stone", SwordInStoneBlock::new);

    public static final DeferredBlock<CrispyHoneyBlock> CRISPY_HONEY_BLOCK = registerWithItem("crispy_honey_block", CrispyHoneyBlock::new);

    public static final DeferredBlock<Block> ASPHALT_BLOCK = registerWithItem("asphalt_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(BLACK_TERRACOTTA).friction(0.4F).speedFactor(1.2F)));
    public static final DeferredBlock<Block> FLESH_BLOCK = registerWithItem("flesh_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE)));
    public static final DeferredBlock<Block> LESION_BLOCK = registerWithItem("lesion_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE)));
    public static final DeferredBlock<RemainsBlock> REMAINS_BLOCK = registerWithItem("remains_block", () -> new RemainsBlock(BlockBehaviour.Properties.of().strength(1.0f).pushReaction(PushReaction.DESTROY)));

    // 纯净玻璃
    public static final DeferredBlock<ConnectedGlassBlock> PURE_GLASS = registerWithItem("pure_glass", () -> new ConnectedGlassBlock(BlockBehaviour.Properties.ofFullCopy(GLASS)));
    public static final DeferredBlock<ConnectedStainedGlassBlock> WHITE_PURE_GLASS = registerWithItem("white_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.WHITE, BlockBehaviour.Properties.ofFullCopy(WHITE_STAINED_GLASS).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<ConnectedStainedGlassBlock> LIGHT_GRAY_PURE_GLASS = registerWithItem("light_gray_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.LIGHT_GRAY, BlockBehaviour.Properties.ofFullCopy(LIGHT_GRAY_STAINED_GLASS).mapColor(MapColor.COLOR_LIGHT_GRAY)));
    public static final DeferredBlock<ConnectedStainedGlassBlock> GRAY_PURE_GLASS = registerWithItem("gray_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.GRAY, BlockBehaviour.Properties.ofFullCopy(GRAY_STAINED_GLASS).mapColor(MapColor.COLOR_GRAY)));
    public static final DeferredBlock<ConnectedStainedGlassBlock> BLACK_PURE_GLASS = registerWithItem("black_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.BLACK, BlockBehaviour.Properties.ofFullCopy(BLACK_STAINED_GLASS).mapColor(MapColor.COLOR_BLACK)));
    public static final DeferredBlock<ConnectedStainedGlassBlock> BROWN_PURE_GLASS = registerWithItem("brown_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.BROWN, BlockBehaviour.Properties.ofFullCopy(BROWN_STAINED_GLASS).mapColor(MapColor.COLOR_BROWN)));
    public static final DeferredBlock<ConnectedStainedGlassBlock> RED_PURE_GLASS = registerWithItem("red_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.RED, BlockBehaviour.Properties.ofFullCopy(RED_STAINED_GLASS).mapColor(MapColor.COLOR_RED)));
    public static final DeferredBlock<ConnectedStainedGlassBlock> ORANGE_PURE_GLASS = registerWithItem("orange_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.ORANGE, BlockBehaviour.Properties.ofFullCopy(ORANGE_STAINED_GLASS).mapColor(MapColor.COLOR_ORANGE)));
    public static final DeferredBlock<ConnectedStainedGlassBlock> YELLOW_PURE_GLASS = registerWithItem("yellow_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.YELLOW, BlockBehaviour.Properties.ofFullCopy(YELLOW_STAINED_GLASS).mapColor(MapColor.COLOR_YELLOW)));
    public static final DeferredBlock<ConnectedStainedGlassBlock> LIME_PURE_GLASS = registerWithItem("lime_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.LIME, BlockBehaviour.Properties.ofFullCopy(LIME_STAINED_GLASS).mapColor(MapColor.COLOR_LIGHT_GREEN)));
    public static final DeferredBlock<ConnectedStainedGlassBlock> GREEN_PURE_GLASS = registerWithItem("green_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.GREEN, BlockBehaviour.Properties.ofFullCopy(GREEN_STAINED_GLASS).mapColor(MapColor.TERRACOTTA_GREEN)));
    public static final DeferredBlock<ConnectedStainedGlassBlock> CYAN_PURE_GLASS = registerWithItem("cyan_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.CYAN, BlockBehaviour.Properties.ofFullCopy(CYAN_STAINED_GLASS).mapColor(MapColor.COLOR_CYAN)));
    public static final DeferredBlock<ConnectedStainedGlassBlock> LIGHT_BLUE_PURE_GLASS = registerWithItem("light_blue_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.LIGHT_BLUE, BlockBehaviour.Properties.ofFullCopy(LIGHT_BLUE_STAINED_GLASS).mapColor(MapColor.COLOR_LIGHT_BLUE)));
    public static final DeferredBlock<ConnectedStainedGlassBlock> BLUE_PURE_GLASS = registerWithItem("blue_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.BLUE, BlockBehaviour.Properties.ofFullCopy(BLUE_STAINED_GLASS).mapColor(MapColor.TERRACOTTA_BLUE)));
    public static final DeferredBlock<ConnectedStainedGlassBlock> PURPLE_PURE_GLASS = registerWithItem("purple_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.PURPLE, BlockBehaviour.Properties.ofFullCopy(PURPLE_STAINED_GLASS).mapColor(MapColor.COLOR_PURPLE)));
    public static final DeferredBlock<ConnectedStainedGlassBlock> MAGENTA_PURE_GLASS = registerWithItem("magenta_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.MAGENTA, BlockBehaviour.Properties.ofFullCopy(MAGENTA_STAINED_GLASS).mapColor(MapColor.TERRACOTTA_MAGENTA)));
    public static final DeferredBlock<ConnectedStainedGlassBlock> PINK_PURE_GLASS = registerWithItem("pink_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.PINK, BlockBehaviour.Properties.ofFullCopy(PINK_STAINED_GLASS).mapColor(MapColor.COLOR_PINK)));

    // 矿砖
    public static final DecoBlockSet COPPER_BRICKS = DecoBlockSet.builder("copper_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_ORANGE)).stonecutting().build();
    public static final DeferredBlock<Block> CHISELED_COPPER_BRICKS = registerWithItem("chiseled_copper_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_ORANGE)));
    public static final DeferredBlock<Block> COPPER_TILES = registerWithItem("copper_tiles", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_ORANGE)));
    public static final DecoBlockSet TIN_BRICKS = DecoBlockSet.builder("tin_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_YELLOW)).stonecutting().build();
    public static final DeferredBlock<Block> CHISELED_TIN_BRICKS = registerWithItem("chiseled_tin_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_YELLOW)));
    public static final DeferredBlock<Block> TIN_TILES = registerWithItem("tin_tiles", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_YELLOW)));
    public static final DecoBlockSet IRON_BRICKS = DecoBlockSet.builder("iron_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)).stonecutting().build();
    public static final DeferredBlock<Block> CHISELED_IRON_BRICKS = registerWithItem("chiseled_iron_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)));
    public static final DecoBlockSet LEAD_BRICKS = DecoBlockSet.builder("lead_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_GRAY)).stonecutting().build();
    public static final DeferredBlock<Block> CHISELED_LEAD_BRICKS = registerWithItem("chiseled_lead_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_GRAY)));
    public static final DecoBlockSet SILVER_BRICKS = DecoBlockSet.builder("silver_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_WHITE)).stonecutting().build();
    public static final DeferredBlock<Block> CHISELED_SILVER_BRICKS = registerWithItem("chiseled_silver_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_GRAY)));
    public static final DecoBlockSet TUNGSTEN_BRICKS = DecoBlockSet.builder("tungsten_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_LIGHT_GREEN)).stonecutting().build();
    public static final DeferredBlock<Block> CHISELED_TUNGSTEN_BRICKS = registerWithItem("chiseled_tungsten_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_LIGHT_GREEN)));
    public static final DecoBlockSet GOLDEN_BRICKS = DecoBlockSet.builder("golden_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_YELLOW)).stonecutting().build();
    public static final DeferredBlock<Block> CHISELED_GOLDEN_BRICKS = registerWithItem("chiseled_golden_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_YELLOW)));
    public static final DecoBlockSet PLATINUM_BRICKS = DecoBlockSet.builder("platinum_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_CYAN)).stonecutting().build();
    public static final DeferredBlock<Block> CHISELED_PLATINUM_BRICKS = registerWithItem("chiseled_platinum_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_CYAN)));
    public static final DecoBlockSet DEMONITE_ORE_BRICKS = DecoBlockSet.builder("demonite_ore_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_WHITE)).stonecutting().build();
    public static final DecoBlockSet EBONSTONE_BRICKS = DecoBlockSet.builder("ebonstone_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_PURPLE)).stonecutting().build();
    public static final DecoBlockSet METEORITE_BRICKS = DecoBlockSet.builder("meteorite_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_ORANGE)).stonecutting().build();
    public static final DecoBlockSet CRIMTANE_ORE_BRICKS = DecoBlockSet.builder("crimtane_ore_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_RED)).stonecutting().build();
    public static final DecoBlockSet CRIMSTONE_BRICKS = DecoBlockSet.builder("crimstone_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_RED)).stonecutting().build();
    public static final DecoBlockSet PEARLSTONE_BRICKS = DecoBlockSet.builder("pearlstone_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_PINK)).stonecutting().build();
    public static final DecoBlockSet HELLSTONE_BRICKS = DecoBlockSet.builder("hellstone_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_RED)).stonecutting().build();

    public static final DeferredBlock<Block> GREEN_CANDY_BLOCK = registerWithItem("green_candy_block", () -> new CandyBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.COLOR_GREEN)));
    public static final DeferredBlock<Block> RED_CANDY_BLOCK = registerWithItem("red_candy_block", () -> new CandyBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.COLOR_RED)));
    public static final DeferredBlock<Block> FROZEN_GEL_BLOCK = registerWithItem("frozen_gel_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.SLIME_BLOCK).friction(1f).speedFactor(1.06F)));
    public static final DeferredBlock<Block> BLUE_GEL_BLOCK = registerWithItem("blue_gel_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).sound(SoundType.SLIME_BLOCK)));
    public static final DeferredBlock<HalfTransparentBlock> PINK_GEL_BLOCK = registerWithItem("pink_gel_block", () -> new HalfTransparentBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).sound(SoundType.SLIME_BLOCK).noOcclusion()));

    // 天域
    public static final DecoBlockSet SUN_PLATE = DecoBlockSet.builder("sun_plate", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_BLUE)).stonecutting().build();
    public static final DeferredBlock<DoorBlock> SKYWARE_DOOR = registerWithItem("skyware_door", () -> new DoorBlock(BlockSetType.STONE, BlockBehaviour.Properties.of().mapColor(BLUE_ICE.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(4.0F).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<DoorBlock> SKYWARE_GLASS_DOOR = registerWithItem("skyware_glass_door", () -> new DoorBlock(BlockSetType.STONE, BlockBehaviour.Properties.of().mapColor(BLUE_ICE.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(4.0F).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DecoBlockSet DISC_BLOCK = DecoBlockSet.builder("disc_block", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.COLOR_YELLOW)).stonecutting().build();
    public static final DecoBlockSet MOON_PLATE = DecoBlockSet.builder("moon_plate", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_BLUE)).stonecutting().build();
    // 黑曜石

    public static final DecoBlockSet OBSIDIAN_BRICKS = DecoBlockSet.builder("obsidian_bricks", () -> BlockBehaviour.Properties.ofFullCopy(OBSIDIAN).mapColor(MapColor.COLOR_BLACK)).stonecutting().build();
    public static final DeferredBlock<Block> OBSIDIAN_SMALL_BRICKS = copyBlockRegister("obsidian_small_bricks", OBSIDIAN);
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
    public static final DecoBlockSet GLOOM_OBSIDIAN_BRICKS = DecoBlockSet.builder("gloom_obsidian_bricks", () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN).mapColor(MapColor.COLOR_BLACK)).stonecutting().build();
    public static final DecoBlockSet CRYING_OBSIDIAN_BRICKS = DecoBlockSet.builder("crying_obsidian_bricks", () -> BlockBehaviour.Properties.ofFullCopy(Blocks.CRYING_OBSIDIAN).mapColor(MapColor.COLOR_BLACK)).stonecutting().build();

    // 王朝木系列
    public static final DeferredBlock<Block> WHITE_PAPER_PANE = copyBlockRegister("white_paper_pane", OAK_PLANKS);
    public static final DeferredBlock<Block> WHITE_PAPER_PANE_LAMP = registerWithItem("white_paper_pane_lamp", () -> new Block(BlockBehaviour.Properties.ofFullCopy(OAK_PLANKS).lightLevel(state -> 15)));
    public static final DeferredBlock<Block> MALACHITE_PAPER_PANE = copyBlockRegister("malachite_paper_pane", OAK_PLANKS);
    public static final DeferredBlock<Block> MALACHITE_PAPER_PANE_LAMP = registerWithItem("malachite_paper_pane_lamp", () -> new Block(BlockBehaviour.Properties.ofFullCopy(OAK_PLANKS).lightLevel(state -> 15)));
    public static final DeferredBlock<DoorBlock> TRADITIONAL_DYNASTY_DOOR = registerWithItem("traditional_dynasty_door", () -> new DoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(OAK_DOOR).mapColor(DyeColor.BROWN).pushReaction(PushReaction.BLOCK)));

    // 松树
    public static final DeferredBlock<DoorBlock> CHRISTMAS_PINE_DOOR = registerWithItem("christmas_pine_door", () -> new DoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(OAK_DOOR).mapColor(DyeColor.BROWN).pushReaction(PushReaction.BLOCK)));
    public static final DeferredBlock<TrapDoorBlock> CHRISTMAS_PINE_TRAPDOOR = registerWithItem("christmas_pine_trapdoor", () -> new TrapDoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(OAK_DOOR).mapColor(DyeColor.BROWN).pushReaction(PushReaction.BLOCK)));

    // 花岗岩
    public static final DecoBlockSet GRANITE_BRICKS = DecoBlockSet.builder("granite_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_BLUE)).stonecutting().build();
    public static final DeferredBlock<Block> GRANITE_COLUMN = registerWithItem("granite_column", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_BLUE)));
    public static final DeferredBlock<Block> CRACKED_GRANITE_BRICKS = registerWithItem("cracked_granite_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_BLUE)));
    public static final DeferredBlock<Block> POLISHED_GRANITE = registerWithItem("polished_granite", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_BLUE)));
    public static final DeferredBlock<Block> CHISELED_GRANITE_BRICKS = registerWithItem("chiseled_granite_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_BLUE)));

    // 方解石
    public static final DecoBlockSet MARBLE_BRICKS = DecoBlockSet.builder("marble_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)).stonecutting().build();
    public static final DeferredBlock<Block> MARBLE_COLUMN = registerWithItem("marble_column", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<Block> CRACKED_MARBLE_BRICKS = registerWithItem("cracked_marble_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<Block> MARBLE_SMALL_BRICKS = registerWithItem("marble_small_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<Block> MARBLE_CHESSBOARD_BRICKS = registerWithItem("marble_chessboard_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<Block> MARBLE_ETERNAL_CHESSBOARD_BRICKS = registerWithItem("marble_eternal_chessboard_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<GlazedTerracottaBlock> GILDED_MARBLE = registerWithItem("gilded_marble", () -> new GlazedTerracottaBlock(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<GlazedTerracottaBlock> CHISELED_MARBLE_BRICKS = registerWithItem("chiseled_marble_bricks", () -> new GlazedTerracottaBlock(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<Block> POLISHED_MARBLE = registerWithItem("polished_marble", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)));

    // 地牢
    public static final DecoBlockSet BLUE_BRICKS = DecoBlockSet.builder("blue_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(DyeColor.BLUE).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))).stonecutting().build();
    public static final DecoBlockSet GREEN_BRICKS = DecoBlockSet.builder("green_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(DyeColor.GREEN).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))).stonecutting().build();
    public static final DecoBlockSet PINK_BRICKS = DecoBlockSet.builder("pink_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(DyeColor.PINK).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))).stonecutting().build();
    public static final DeferredBlock<CrackedBricksBlock> CRACKED_BLUE_BRICKS = registerWithItem("cracked_blue_bricks", () -> new CrackedBricksBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).strength(0).mapColor(DyeColor.BLUE)));
    public static final DeferredBlock<CrackedBricksBlock> CRACKED_GREEN_BRICKS = registerWithItem("cracked_green_bricks", () -> new CrackedBricksBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).strength(0).mapColor(DyeColor.GREEN)));
    public static final DeferredBlock<CrackedBricksBlock> CRACKED_PINK_BRICKS = registerWithItem("cracked_pink_bricks", () -> new CrackedBricksBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).strength(0).mapColor(DyeColor.PINK)));
    public static final DeferredBlock<Block> CHISELED_BLUE_BRICKS = registerWithItem("chiseled_blue_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(DyeColor.BLUE).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<Block> CHISELED_GREEN_BRICKS = registerWithItem("chiseled_green_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(DyeColor.GREEN).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<Block> CHISELED_PINK_BRICKS = registerWithItem("chiseled_pink_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(DyeColor.PINK).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
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


    public static final DeferredBlock<SoulGlassBlock> SOUL_GLASS = registerWithItem("soul_glass", () -> new SoulGlassBlock(BlockBehaviour.Properties.ofFullCopy(GLASS).mapColor(MapColor.COLOR_PURPLE)));
    public static final DeferredBlock<Block> FLINX_FUR_BLOCK = registerWithItem("flinx_fur_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.TERRACOTTA_PINK)));
    public static final DeferredBlock<WoolCarpetBlock> FLINX_FUR_CARPET = registerWithItem("flinx_fur_carpet", () -> new WoolCarpetBlock(DyeColor.PINK, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_PINK).strength(0.1F).sound(SoundType.WOOL).ignitedByLava()));
    public static final DeferredBlock<Block> RAINBOW_WOOL = registerWithItem("rainbow_wool", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.TERRACOTTA_PINK)));
    public static final DeferredBlock<WoolCarpetBlock> RAINBOW_CARPET = registerWithItem("rainbow_carpet", () -> new WoolCarpetBlock(DyeColor.PINK, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_PINK).strength(0.1F).sound(SoundType.WOOL).ignitedByLava()));

    // 气球
    public static final DeferredBlock<Block> WHITE_BALLOON = registerWithItem("white_balloon", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.SNOW).strength(0.1F)));
    public static final DeferredBlock<Block> LIGHT_GRAY_BALLOON = registerWithItem("light_gray_balloon", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.COLOR_LIGHT_GRAY).strength(0.1F)));
    public static final DeferredBlock<Block> GRAY_BALLOON = registerWithItem("gray_balloon", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.COLOR_GRAY).strength(0.1F)));
    public static final DeferredBlock<Block> BLACK_BALLOON = registerWithItem("black_balloon", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.COLOR_BLACK).strength(0.1F)));
    public static final DeferredBlock<Block> BROWN_BALLOON = registerWithItem("brown_balloon", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.COLOR_BROWN).strength(0.1F)));
    public static final DeferredBlock<Block> RED_BALLOON = registerWithItem("red_balloon", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.COLOR_RED).strength(0.1F)));
    public static final DeferredBlock<Block> ORANGE_BALLOON = registerWithItem("orange_balloon", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.COLOR_ORANGE).strength(0.1F)));
    public static final DeferredBlock<Block> YELLOW_BALLOON = registerWithItem("yellow_balloon", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.COLOR_YELLOW).strength(0.1F)));
    public static final DeferredBlock<Block> LIME_BALLOON = registerWithItem("lime_balloon", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.COLOR_LIGHT_GREEN).strength(0.1F)));
    public static final DeferredBlock<Block> GREEN_BALLOON = registerWithItem("green_balloon", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.COLOR_GREEN).strength(0.1F)));
    public static final DeferredBlock<Block> CYAN_BALLOON = registerWithItem("cyan_balloon", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.COLOR_CYAN).strength(0.1F)));
    public static final DeferredBlock<Block> LIGHT_BLUE_BALLOON = registerWithItem("light_blue_balloon", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.COLOR_BLUE).strength(0.1F)));
    public static final DeferredBlock<Block> BLUE_BALLOON = registerWithItem("blue_balloon", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.COLOR_BLUE).strength(0.1F)));
    public static final DeferredBlock<Block> PURPLE_BALLOON = registerWithItem("purple_balloon", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.COLOR_PURPLE).strength(0.1F)));
    public static final DeferredBlock<Block> MAGENTA_BALLOON = registerWithItem("magenta_balloon", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.COLOR_MAGENTA).strength(0.1F)));
    public static final DeferredBlock<Block> PINK_BALLOON = registerWithItem("pink_balloon", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.COLOR_PINK).strength(0.1F)));
    // 圣物
    public static final DeferredBlock<RelicBlock> KING_SLIME_RELIC = registerRelic("king_slime_relic");
    public static final DeferredBlock<RelicBlock> EYE_OF_CTHULHU_RELIC = registerRelic("eye_of_cthulhu_relic");
    public static final DeferredBlock<RelicBlock> BRAIN_OF_CTHULHU_RELIC = registerRelic("brain_of_cthulhu_relic");
    public static final DeferredBlock<RelicBlock> EATER_OF_WORLDS_RELIC = registerRelic("eater_of_worlds_relic");
    public static final DeferredBlock<RelicBlock> QUEEN_BEE_RELIC = registerRelic("queen_bee_relic");
    public static final DeferredBlock<RelicBlock> DEERCLOPS_RELIC = registerRelic("deerclops_relic");
    public static final DeferredBlock<RelicBlock> SKELETRON_RELIC = registerRelic("skeletron_relic");
    public static final DeferredBlock<RelicBlock> WALL_OF_FLESH_RELIC = registerRelic("wall_of_flesh_relic");
    public static final DeferredBlock<RelicBlock> HILL_OF_FLESH_RELIC = registerRelic("hill_of_flesh_relic");
    public static final DeferredBlock<RelicBlock> THE_TWINS_RELIC = registerRelic("the_twins_relic");
    public static final DeferredBlock<RelicBlock> SKELETRON_PRIME_RELIC = registerRelic("skeletron_prime_relic");

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
    public static final DecoBlockSet LIHZAHRD_BRICKS = DecoBlockSet.builder("lihzahrd_bricks", () -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).requiresCorrectToolForDrops().strength(100.0F, ModBlocks.getObsidianBasedExplosionResistance(1000.0F))).stonecutting().build();


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
