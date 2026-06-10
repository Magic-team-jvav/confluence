package org.confluence.mod.common.init.block;

import com.mojang.datafixers.DSL;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
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
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, Confluence.MODID);
    public static final List<RegistryObject<RelicBlock>> RELIC_BLOCKS = new ArrayList<>();

    // 杂项
    public static final RegistryObject<MuralBlock> MURAL_BLOCK = registerWithItem("mural_block", () -> new MuralBlock(BlockBehaviour.Properties.copy(STONE)));
    public static final Supplier<BlockEntityType<MuralBlock.BEntity>> MURAL_ENTITY_BLOCK = ModBlocks.BLOCK_ENTITIES.register("mural_entity_block", () -> BlockEntityType.Builder.of(MuralBlock.BEntity::new, MURAL_BLOCK.get()).build(DSL.remainderType()));

    public static final RegistryObject<LostPaperBlock> LOST_PAPER_BLOCK = registerWithItem("lost_paper", LostPaperBlock::new);

    public static final RegistryObject<CarvedPumpkinBlock> CARVED_WHITE_PUMPKIN = registerWithItem("carved_white_pumpkin", () -> new CarvedPumpkinBlock(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).instrument(NoteBlockInstrument.DIDGERIDOO).strength(1.0F).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY)));
    public static final RegistryObject<CarvedPumpkinBlock> JOHNNY_O_LANTERN = registerWithItem("johnny_o_lantern", () -> new CarvedPumpkinBlock(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).mapColor(MapColor.COLOR_ORANGE).strength(1.0F).sound(SoundType.WOOD).lightLevel(p_187437_ -> 15).isValidSpawn(Blocks::always).pushReaction(PushReaction.DESTROY)));

    public static final RegistryObject<Block> GOLDEN_MELON = registerWithItem("golden_melon", () -> new Block(BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_YELLOW)));
    public static final RegistryObject<Block> ICE_MELON = registerWithItem("ice_melon", () -> new Block(BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_BLUE)));


    // 雕纹木板
    public static final RegistryObject<Block> CHISELED_OAK_PLANKS = registerWithCopy("chiseled_oak_planks", OAK_PLANKS);
    public static final RegistryObject<Block> CHISELED_SPRUCE_PLANKS = registerWithCopy("chiseled_spruce_planks", SPRUCE_PLANKS);
    public static final RegistryObject<Block> WOOD_STONE_SLATTED_BLOCKS = registerWithCopy("wood_stone_slatted_blocks", OAK_PLANKS);

    public static final RegistryObject<PooBlock> POO_BLOCK = registerWithItem("poo_block", () -> new PooBlock(BlockBehaviour.Properties.copy(Blocks.MUD).mapColor(MapColor.COLOR_BROWN)));

    // 砖
    public static final DecoBlockSet BLUE_ICE_BRICKS = DecoBlockSet.builder("blue_ice_bricks", () -> BlockBehaviour.Properties.copy(Blocks.BLUE_ICE).mapColor(MapColor.COLOR_LIGHT_BLUE)).stonecutting().build();
    public static final DecoBlockSet PACKED_ICE_BRICKS = DecoBlockSet.builder("packed_ice_bricks", () -> BlockBehaviour.Properties.copy(Blocks.PACKED_ICE).mapColor(MapColor.COLOR_LIGHT_BLUE)).stonecutting().build();
    public static final DecoBlockSet SANDSTONE_BRICKS = DecoBlockSet.builder("sandstone_bricks", () -> BlockBehaviour.Properties.copy(Blocks.SANDSTONE).mapColor(MapColor.SAND)).stonecutting().build();
    public static final DecoBlockSet RED_SANDSTONE_BRICKS = DecoBlockSet.builder("red_sandstone_bricks", () -> BlockBehaviour.Properties.copy(Blocks.RED_SANDSTONE).mapColor(MapColor.TERRACOTTA_RED)).stonecutting().build();
    public static final DecoBlockSet EBONSANDSTONE_BRICKS = DecoBlockSet.builder("ebonsandstone_bricks", () -> BlockBehaviour.Properties.copy(Blocks.SANDSTONE).mapColor(MapColor.TERRACOTTA_PURPLE)).stonecutting().build();
    public static final DecoBlockSet PEARLSANDSTONE_BRICKS = DecoBlockSet.builder("pearlsandstone_bricks", () -> BlockBehaviour.Properties.copy(Blocks.SANDSTONE).mapColor(MapColor.TERRACOTTA_WHITE)).stonecutting().build();
    public static final DecoBlockSet CRIMSANDSTONE_BRICKS = DecoBlockSet.builder("crimsandstone_bricks", () -> BlockBehaviour.Properties.copy(Blocks.SANDSTONE).mapColor(MapColor.TERRACOTTA_GRAY)).stonecutting().build();
    public static final DecoBlockSet SNOW_BRICKS = DecoBlockSet.builder("snow_bricks", () -> BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS).mapColor(MapColor.TERRACOTTA_WHITE)).stonecutting().build();
    public static final DecoBlockSet AETHERIUM_BRICKS = DecoBlockSet.builder("aetherium_bricks", () -> BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS).mapColor(MapColor.TERRACOTTA_WHITE)).stonecutting().build();
    public static final DecoBlockSet RAINBOW_BRICKS = DecoBlockSet.builder("rainbow_bricks", () -> BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS).mapColor(MapColor.TERRACOTTA_WHITE)).stonecutting().build();
    public static final RegistryObject<Block> CRYSTAL_BLOCK = registerWithItem("crystal_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.AMETHYST_BLOCK).mapColor(MapColor.TERRACOTTA_WHITE)));


    public static final RegistryObject<CloudBlock> FLOATING_WHEAT_BALE = registerWithItem("floating_wheat_bale", () -> new CloudBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_LIGHT_BLUE)
            .strength(0.3F)
            .sound(SoundType.SNOW)
            .isValidSpawn(Blocks::never)
            .isRedstoneConductor((blockState, blockGetter, blockPos) -> false)
            .isSuffocating((blockState, blockGetter, blockPos) -> false)));

    // 云
    public static final RegistryObject<CloudBlockTrampoline> CLOUD_BLOCK_TRAMPOLINE = registerWithItem("cloud_block_trampoline", () -> new CloudBlockTrampoline(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_PINK)
            .strength(0.3F)
            .sound(SoundType.SNOW)
            .noOcclusion()
            .isValidSpawn(Blocks::never)
            .isRedstoneConductor((blockState, blockGetter, blockPos) -> false)
            .isSuffocating((blockState, blockGetter, blockPos) -> false)));
    public static final RegistryObject<CloudBlock> BOUNCY_CLOUD_BLOCK = registerWithItem("bouncy_cloud_block", () -> new CloudBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_PINK)
            .strength(0.3F)
            .sound(SoundType.SNOW)
            .noOcclusion()
            .jumpFactor(2.5F)
            .isValidSpawn(Blocks::never)
            .isRedstoneConductor((blockState, blockGetter, blockPos) -> false)
            .isSuffocating((blockState, blockGetter, blockPos) -> false)));
    public static final RegistryObject<CloudBlock> STAR_CLOUD_BLOCK = registerWithItem("star_cloud_block", () -> new CloudBlock(BlockBehaviour.Properties.of()
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
    public static final RegistryObject<SwordInStoneBlock> SWORD_IN_STONE = registerWithItem("sword_in_stone", SwordInStoneBlock::new);

    public static final RegistryObject<CrispyHoneyBlock> CRISPY_HONEY_BLOCK = registerWithItem("crispy_honey_block", CrispyHoneyBlock::new);

    public static final RegistryObject<Block> ASPHALT_BLOCK = registerWithItem("asphalt_block", () -> new Block(BlockBehaviour.Properties.copy(BLACK_TERRACOTTA).friction(0.4F).speedFactor(1.2F)));
    public static final RegistryObject<Block> FLESH_BLOCK = registerWithItem("flesh_block", () -> new Block(BlockBehaviour.Properties.copy(STONE)));
    public static final RegistryObject<Block> LESION_BLOCK = registerWithItem("lesion_block", () -> new Block(BlockBehaviour.Properties.copy(STONE)));
    public static final RegistryObject<RemainsBlock> REMAINS_BLOCK = registerWithItem("remains_block", () -> new RemainsBlock(BlockBehaviour.Properties.of().strength(1.0f).pushReaction(PushReaction.DESTROY)));

    // 纯净玻璃
    public static final RegistryObject<ConnectedGlassBlock> PURE_GLASS = registerWithItem("pure_glass", () -> new ConnectedGlassBlock(BlockBehaviour.Properties.copy(GLASS)));
    public static final RegistryObject<ConnectedStainedGlassBlock> WHITE_PURE_GLASS = registerWithItem("white_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.WHITE, BlockBehaviour.Properties.copy(WHITE_STAINED_GLASS).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final RegistryObject<ConnectedStainedGlassBlock> LIGHT_GRAY_PURE_GLASS = registerWithItem("light_gray_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.LIGHT_GRAY, BlockBehaviour.Properties.copy(LIGHT_GRAY_STAINED_GLASS).mapColor(MapColor.COLOR_LIGHT_GRAY)));
    public static final RegistryObject<ConnectedStainedGlassBlock> GRAY_PURE_GLASS = registerWithItem("gray_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.GRAY, BlockBehaviour.Properties.copy(GRAY_STAINED_GLASS).mapColor(MapColor.COLOR_GRAY)));
    public static final RegistryObject<ConnectedStainedGlassBlock> BLACK_PURE_GLASS = registerWithItem("black_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.BLACK, BlockBehaviour.Properties.copy(BLACK_STAINED_GLASS).mapColor(MapColor.COLOR_BLACK)));
    public static final RegistryObject<ConnectedStainedGlassBlock> BROWN_PURE_GLASS = registerWithItem("brown_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.BROWN, BlockBehaviour.Properties.copy(BROWN_STAINED_GLASS).mapColor(MapColor.COLOR_BROWN)));
    public static final RegistryObject<ConnectedStainedGlassBlock> RED_PURE_GLASS = registerWithItem("red_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.RED, BlockBehaviour.Properties.copy(RED_STAINED_GLASS).mapColor(MapColor.COLOR_RED)));
    public static final RegistryObject<ConnectedStainedGlassBlock> ORANGE_PURE_GLASS = registerWithItem("orange_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.ORANGE, BlockBehaviour.Properties.copy(ORANGE_STAINED_GLASS).mapColor(MapColor.COLOR_ORANGE)));
    public static final RegistryObject<ConnectedStainedGlassBlock> YELLOW_PURE_GLASS = registerWithItem("yellow_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.YELLOW, BlockBehaviour.Properties.copy(YELLOW_STAINED_GLASS).mapColor(MapColor.COLOR_YELLOW)));
    public static final RegistryObject<ConnectedStainedGlassBlock> LIME_PURE_GLASS = registerWithItem("lime_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.LIME, BlockBehaviour.Properties.copy(LIME_STAINED_GLASS).mapColor(MapColor.COLOR_LIGHT_GREEN)));
    public static final RegistryObject<ConnectedStainedGlassBlock> GREEN_PURE_GLASS = registerWithItem("green_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.GREEN, BlockBehaviour.Properties.copy(GREEN_STAINED_GLASS).mapColor(MapColor.TERRACOTTA_GREEN)));
    public static final RegistryObject<ConnectedStainedGlassBlock> CYAN_PURE_GLASS = registerWithItem("cyan_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.CYAN, BlockBehaviour.Properties.copy(CYAN_STAINED_GLASS).mapColor(MapColor.COLOR_CYAN)));
    public static final RegistryObject<ConnectedStainedGlassBlock> LIGHT_BLUE_PURE_GLASS = registerWithItem("light_blue_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.LIGHT_BLUE, BlockBehaviour.Properties.copy(LIGHT_BLUE_STAINED_GLASS).mapColor(MapColor.COLOR_LIGHT_BLUE)));
    public static final RegistryObject<ConnectedStainedGlassBlock> BLUE_PURE_GLASS = registerWithItem("blue_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.BLUE, BlockBehaviour.Properties.copy(BLUE_STAINED_GLASS).mapColor(MapColor.TERRACOTTA_BLUE)));
    public static final RegistryObject<ConnectedStainedGlassBlock> PURPLE_PURE_GLASS = registerWithItem("purple_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.PURPLE, BlockBehaviour.Properties.copy(PURPLE_STAINED_GLASS).mapColor(MapColor.COLOR_PURPLE)));
    public static final RegistryObject<ConnectedStainedGlassBlock> MAGENTA_PURE_GLASS = registerWithItem("magenta_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.MAGENTA, BlockBehaviour.Properties.copy(MAGENTA_STAINED_GLASS).mapColor(MapColor.TERRACOTTA_MAGENTA)));
    public static final RegistryObject<ConnectedStainedGlassBlock> PINK_PURE_GLASS = registerWithItem("pink_pure_glass", () -> new ConnectedStainedGlassBlock(DyeColor.PINK, BlockBehaviour.Properties.copy(PINK_STAINED_GLASS).mapColor(MapColor.COLOR_PINK)));

    // 矿砖
    public static final DecoBlockSet COPPER_BRICKS = DecoBlockSet.builder("copper_bricks", () -> BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_ORANGE)).stonecutting().build();
    public static final RegistryObject<Block> CHISELED_COPPER_BRICKS = registerWithItem("chiseled_copper_bricks", () -> new Block(BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_ORANGE)));
    public static final RegistryObject<Block> COPPER_TILES = registerWithItem("copper_tiles", () -> new Block(BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_ORANGE)));
    public static final DecoBlockSet TIN_BRICKS = DecoBlockSet.builder("tin_bricks", () -> BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_YELLOW)).stonecutting().build();
    public static final RegistryObject<Block> CHISELED_TIN_BRICKS = registerWithItem("chiseled_tin_bricks", () -> new Block(BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_YELLOW)));
    public static final RegistryObject<Block> TIN_TILES = registerWithItem("tin_tiles", () -> new Block(BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_YELLOW)));
    public static final DecoBlockSet IRON_BRICKS = DecoBlockSet.builder("iron_bricks", () -> BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)).stonecutting().build();
    public static final RegistryObject<Block> CHISELED_IRON_BRICKS = registerWithItem("chiseled_iron_bricks", () -> new Block(BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)));
    public static final DecoBlockSet LEAD_BRICKS = DecoBlockSet.builder("lead_bricks", () -> BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_GRAY)).stonecutting().build();
    public static final RegistryObject<Block> CHISELED_LEAD_BRICKS = registerWithItem("chiseled_lead_bricks", () -> new Block(BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_GRAY)));
    public static final DecoBlockSet SILVER_BRICKS = DecoBlockSet.builder("silver_bricks", () -> BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_WHITE)).stonecutting().build();
    public static final RegistryObject<Block> CHISELED_SILVER_BRICKS = registerWithItem("chiseled_silver_bricks", () -> new Block(BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_GRAY)));
    public static final DecoBlockSet TUNGSTEN_BRICKS = DecoBlockSet.builder("tungsten_bricks", () -> BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_LIGHT_GREEN)).stonecutting().build();
    public static final RegistryObject<Block> CHISELED_TUNGSTEN_BRICKS = registerWithItem("chiseled_tungsten_bricks", () -> new Block(BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_LIGHT_GREEN)));
    public static final DecoBlockSet GOLDEN_BRICKS = DecoBlockSet.builder("golden_bricks", () -> BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_YELLOW)).stonecutting().build();
    public static final RegistryObject<Block> CHISELED_GOLDEN_BRICKS = registerWithItem("chiseled_golden_bricks", () -> new Block(BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_YELLOW)));
    public static final DecoBlockSet PLATINUM_BRICKS = DecoBlockSet.builder("platinum_bricks", () -> BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_CYAN)).stonecutting().build();
    public static final RegistryObject<Block> CHISELED_PLATINUM_BRICKS = registerWithItem("chiseled_platinum_bricks", () -> new Block(BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_CYAN)));
    public static final DecoBlockSet DEMONITE_ORE_BRICKS = DecoBlockSet.builder("demonite_ore_bricks", () -> BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_WHITE)).stonecutting().build();
    public static final DecoBlockSet EBONSTONE_BRICKS = DecoBlockSet.builder("ebonstone_bricks", () -> BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_PURPLE)).stonecutting().build();
    public static final DecoBlockSet METEORITE_BRICKS = DecoBlockSet.builder("meteorite_bricks", () -> BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_ORANGE)).stonecutting().build();
    public static final DecoBlockSet CRIMTANE_ORE_BRICKS = DecoBlockSet.builder("crimtane_ore_bricks", () -> BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_RED)).stonecutting().build();
    public static final DecoBlockSet CRIMSTONE_BRICKS = DecoBlockSet.builder("crimstone_bricks", () -> BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_RED)).stonecutting().build();
    public static final DecoBlockSet PEARLSTONE_BRICKS = DecoBlockSet.builder("pearlstone_bricks", () -> BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_PINK)).stonecutting().build();
    public static final DecoBlockSet HELLSTONE_BRICKS = DecoBlockSet.builder("hellstone_bricks",
                    () -> BlockBehaviour.Properties.copy(STONE_BRICKS)
                            .mapColor(MapColor.COLOR_RED)
                            .strength(12.0F, 1200.0F)
                            .requiresCorrectToolForDrops()
                            .lightLevel(value -> 10))
            .full(props -> new HellStoneBlock(true, props))
            .stair(HellStoneBlock::hotStair)
            .slab(HellStoneBlock::hotSlab)
            .wall(HellStoneBlock::hotWall)
            .itemProperties(new Item.Properties().fireResistant().stacksTo(64))
            .stonecutting()
            .build();

    public static final RegistryObject<Block> GREEN_CANDY_BLOCK = registerWithItem("green_candy_block", () -> new CandyBlock(BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.COLOR_GREEN)));
    public static final RegistryObject<Block> RED_CANDY_BLOCK = registerWithItem("red_candy_block", () -> new CandyBlock(BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.COLOR_RED)));
    public static final RegistryObject<Block> FROZEN_GEL_BLOCK = registerWithItem("frozen_gel_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.SLIME_BLOCK).friction(1f).speedFactor(1.06F)));
    public static final RegistryObject<Block> BLUE_GEL_BLOCK = registerWithItem("blue_gel_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).sound(SoundType.SLIME_BLOCK)));
    public static final RegistryObject<HalfTransparentBlock> PINK_GEL_BLOCK = registerWithItem("pink_gel_block", () -> new HalfTransparentBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).sound(SoundType.SLIME_BLOCK).noOcclusion()));

    // 天域
    public static final DecoBlockSet SUN_PLATE = DecoBlockSet.builder("sun_plate", () -> BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_BLUE)).stonecutting().build();
    public static final RegistryObject<DoorBlock> SKYWARE_DOOR = registerWithItem("skyware_door", () -> new DoorBlock(BlockBehaviour.Properties.of().mapColor(BLUE_ICE.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(4.0F).noOcclusion().pushReaction(PushReaction.DESTROY), BlockSetType.STONE));
    public static final RegistryObject<DoorBlock> SKYWARE_GLASS_DOOR = registerWithItem("skyware_glass_door", () -> new DoorBlock(BlockBehaviour.Properties.of().mapColor(BLUE_ICE.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(4.0F).noOcclusion().pushReaction(PushReaction.DESTROY), BlockSetType.STONE));
    public static final DecoBlockSet DISC_BLOCK = DecoBlockSet.builder("disc_block", () -> BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.COLOR_YELLOW)).stonecutting().build();
    public static final DecoBlockSet MOON_PLATE = DecoBlockSet.builder("moon_plate", () -> BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_BLUE)).stonecutting().build();
    // 黑曜石

    public static final DecoBlockSet OBSIDIAN_BRICKS = DecoBlockSet.builder("obsidian_bricks", () -> BlockBehaviour.Properties.copy(OBSIDIAN).mapColor(MapColor.COLOR_BLACK)).stonecutting().build();
    public static final RegistryObject<Block> OBSIDIAN_SMALL_BRICKS = registerWithCopy("obsidian_small_bricks", OBSIDIAN);
    public static final RegistryObject<DoorBlock> OBSIDIAN_BRICKS_DOOR = registerWithItem("obsidian_bricks_door", () -> new DoorBlock(
            BlockBehaviour.Properties.of()
                    .mapColor(OBSIDIAN.defaultMapColor())
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(5.0F)
                    .noOcclusion()
                    .ignitedByLava()
                    .pushReaction(PushReaction.DESTROY),
            BlockSetType.STONE
    ));
    public static final RegistryObject<Block> CHISELED_OBSIDIAN_BRICKS = registerWithCopy("chiseled_obsidian_bricks", OBSIDIAN);
    public static final RegistryObject<Block> SMOOTH_OBSIDIAN = registerWithCopy("smooth_obsidian", OBSIDIAN);
    public static final DecoBlockSet GLOOM_OBSIDIAN_BRICKS = DecoBlockSet.builder("gloom_obsidian_bricks", () -> BlockBehaviour.Properties.copy(Blocks.OBSIDIAN).mapColor(MapColor.COLOR_BLACK)).stonecutting().build();
    public static final DecoBlockSet CRYING_OBSIDIAN_BRICKS = DecoBlockSet.builder("crying_obsidian_bricks", () -> BlockBehaviour.Properties.copy(Blocks.CRYING_OBSIDIAN).mapColor(MapColor.COLOR_BLACK)).stonecutting().build();

    // 王朝木系列
    public static final RegistryObject<Block> WHITE_PAPER_PANE = registerWithCopy("white_paper_pane", OAK_PLANKS);
    public static final RegistryObject<Block> WHITE_PAPER_PANE_LAMP = registerWithItem("white_paper_pane_lamp", () -> new Block(BlockBehaviour.Properties.copy(OAK_PLANKS).lightLevel(state -> 15)));
    public static final RegistryObject<Block> MALACHITE_PAPER_PANE = registerWithCopy("malachite_paper_pane", OAK_PLANKS);
    public static final RegistryObject<Block> MALACHITE_PAPER_PANE_LAMP = registerWithItem("malachite_paper_pane_lamp", () -> new Block(BlockBehaviour.Properties.copy(OAK_PLANKS).lightLevel(state -> 15)));
    public static final RegistryObject<DoorBlock> TRADITIONAL_DYNASTY_DOOR = registerWithItem("traditional_dynasty_door", () -> new DoorBlock(BlockBehaviour.Properties.copy(OAK_DOOR).mapColor(DyeColor.BROWN).pushReaction(PushReaction.BLOCK), BlockSetType.OAK));

    // 松树
    public static final RegistryObject<DoorBlock> CHRISTMAS_PINE_DOOR = registerWithItem("christmas_pine_door", () -> new DoorBlock(BlockBehaviour.Properties.copy(OAK_DOOR).mapColor(DyeColor.BROWN).pushReaction(PushReaction.BLOCK), BlockSetType.OAK));
    public static final RegistryObject<TrapDoorBlock> CHRISTMAS_PINE_TRAPDOOR = registerWithItem("christmas_pine_trapdoor", () -> new TrapDoorBlock(BlockBehaviour.Properties.copy(OAK_DOOR).mapColor(DyeColor.BROWN).pushReaction(PushReaction.BLOCK), BlockSetType.OAK));

    // 花岗岩
    public static final DecoBlockSet GRANITE_BRICKS = DecoBlockSet.builder("granite_bricks", () -> BlockBehaviour.Properties.copy(STONE).mapColor(MapColor.TERRACOTTA_BLUE)).stonecutting().build();
    public static final RegistryObject<Block> GRANITE_COLUMN = registerWithItem("granite_column", () -> new Block(BlockBehaviour.Properties.copy(STONE).mapColor(MapColor.TERRACOTTA_BLUE)));
    public static final RegistryObject<Block> CRACKED_GRANITE_BRICKS = registerWithItem("cracked_granite_bricks", () -> new Block(BlockBehaviour.Properties.copy(STONE).mapColor(MapColor.TERRACOTTA_BLUE)));
    public static final RegistryObject<Block> POLISHED_GRANITE = registerWithItem("polished_granite", () -> new Block(BlockBehaviour.Properties.copy(STONE).mapColor(MapColor.TERRACOTTA_BLUE)));
    public static final RegistryObject<Block> CHISELED_GRANITE_BRICKS = registerWithItem("chiseled_granite_bricks", () -> new Block(BlockBehaviour.Properties.copy(STONE).mapColor(MapColor.TERRACOTTA_BLUE)));

    // 方解石
    public static final DecoBlockSet MARBLE_BRICKS = DecoBlockSet.builder("marble_bricks", () -> BlockBehaviour.Properties.copy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)).stonecutting().build();
    public static final RegistryObject<Block> MARBLE_COLUMN = registerWithItem("marble_column", () -> new Block(BlockBehaviour.Properties.copy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final RegistryObject<Block> CRACKED_MARBLE_BRICKS = registerWithItem("cracked_marble_bricks", () -> new Block(BlockBehaviour.Properties.copy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final RegistryObject<Block> MARBLE_SMALL_BRICKS = registerWithItem("marble_small_bricks", () -> new Block(BlockBehaviour.Properties.copy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final RegistryObject<Block> MARBLE_CHESSBOARD_BRICKS = registerWithItem("marble_chessboard_bricks", () -> new Block(BlockBehaviour.Properties.copy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final RegistryObject<Block> MARBLE_ETERNAL_CHESSBOARD_BRICKS = registerWithItem("marble_eternal_chessboard_bricks", () -> new Block(BlockBehaviour.Properties.copy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final RegistryObject<GlazedTerracottaBlock> GILDED_MARBLE = registerWithItem("gilded_marble", () -> new GlazedTerracottaBlock(BlockBehaviour.Properties.copy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final RegistryObject<GlazedTerracottaBlock> CHISELED_MARBLE_BRICKS = registerWithItem("chiseled_marble_bricks", () -> new GlazedTerracottaBlock(BlockBehaviour.Properties.copy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final RegistryObject<Block> POLISHED_MARBLE = registerWithItem("polished_marble", () -> new Block(BlockBehaviour.Properties.copy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)));

    // 地牢
    public static final DecoBlockSet BLUE_BRICKS = DecoBlockSet.builder("blue_bricks", () -> BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(DyeColor.BLUE).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))).stonecutting().build();
    public static final DecoBlockSet GREEN_BRICKS = DecoBlockSet.builder("green_bricks", () -> BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(DyeColor.GREEN).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))).stonecutting().build();
    public static final DecoBlockSet PINK_BRICKS = DecoBlockSet.builder("pink_bricks", () -> BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(DyeColor.PINK).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))).stonecutting().build();
    public static final RegistryObject<CrackedBricksBlock> CRACKED_BLUE_BRICKS = registerWithItem("cracked_blue_bricks", () -> new CrackedBricksBlock(BlockBehaviour.Properties.copy(STONE_BRICKS).strength(0).mapColor(DyeColor.BLUE)));
    public static final RegistryObject<CrackedBricksBlock> CRACKED_GREEN_BRICKS = registerWithItem("cracked_green_bricks", () -> new CrackedBricksBlock(BlockBehaviour.Properties.copy(STONE_BRICKS).strength(0).mapColor(DyeColor.GREEN)));
    public static final RegistryObject<CrackedBricksBlock> CRACKED_PINK_BRICKS = registerWithItem("cracked_pink_bricks", () -> new CrackedBricksBlock(BlockBehaviour.Properties.copy(STONE_BRICKS).strength(0).mapColor(DyeColor.PINK)));
    public static final RegistryObject<Block> CHISELED_BLUE_BRICKS = registerWithItem("chiseled_blue_bricks", () -> new Block(BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(DyeColor.BLUE).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final RegistryObject<Block> CHISELED_GREEN_BRICKS = registerWithItem("chiseled_green_bricks", () -> new Block(BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(DyeColor.GREEN).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final RegistryObject<Block> CHISELED_PINK_BRICKS = registerWithItem("chiseled_pink_bricks", () -> new Block(BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(DyeColor.PINK).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final RegistryObject<DoorBlock> DUNGEON_DOOR = registerWithItem("dungeon_door", () -> new DoorBlock(BlockBehaviour.Properties.copy(IRON_DOOR).mapColor(DyeColor.GRAY).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100)), BlockSetType.STONE));
    public static final RegistryObject<EnchantedBricksBlock> ENCHANTED_BLUE_BRICKS = registerWithItem("enchanted_blue_bricks", () -> new EnchantedBricksBlock(BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(DyeColor.BLUE).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final RegistryObject<EnchantedBricksBlock> ENCHANTED_GREEN_BRICKS = registerWithItem("enchanted_green_bricks", () -> new EnchantedBricksBlock(BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(DyeColor.BLUE).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final RegistryObject<EnchantedBricksBlock> ENCHANTED_PINK_BRICKS = registerWithItem("enchanted_pink_bricks", () -> new EnchantedBricksBlock(BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(DyeColor.BLUE).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final RegistryObject<RotatedPillarBlock> BLUE_BRICK_COLUMN = registerWithItem("blue_brick_column", () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(DyeColor.BLUE).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final RegistryObject<RotatedPillarBlock> GREEN_BRICK_COLUMN = registerWithItem("green_brick_column", () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(DyeColor.GREEN).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final RegistryObject<RotatedPillarBlock> PINK_BRICK_COLUMN = registerWithItem("pink_brick_column", () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(DyeColor.PINK).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));

    // 大宝石块
    public static final RegistryObject<Block> RUBY_BLOCK = registerWithItem("ruby_block", () -> new Block(BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.COLOR_RED)));
    public static final RegistryObject<Block> AMBER_BLOCK = registerWithItem("amber_block", () -> new Block(BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.COLOR_ORANGE)));
    public static final RegistryObject<Block> TOPAZ_BLOCK = registerWithItem("topaz_block", () -> new Block(BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.COLOR_YELLOW)));
    public static final RegistryObject<Block> JADE_BLOCK = registerWithItem("jade_block", () -> new Block(BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.EMERALD)));
    public static final RegistryObject<Block> SAPPHIRE_BLOCK = registerWithItem("sapphire_block", () -> new Block(BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.COLOR_BLUE)));
    public static final RegistryObject<Block> AMETHYST_BLOCK = registerWithItem("amethyst_block", () -> new Block(BlockBehaviour.Properties.copy(STONE_BRICKS).mapColor(MapColor.COLOR_PURPLE)));

    // 锁链
    public static final RegistryObject<ChainBlock> RUBY_CHAIN = registerChain("ruby_chain", properties -> new ChainBlock(properties.mapColor(MapColor.COLOR_RED)));
    public static final RegistryObject<ChainBlock> AMBER_CHAIN = registerChain("amber_chain", properties -> new ChainBlock(properties.mapColor(MapColor.COLOR_ORANGE)));
    public static final RegistryObject<ChainBlock> TOPAZ_CHAIN = registerChain("topaz_chain", properties -> new ChainBlock(properties.mapColor(MapColor.COLOR_YELLOW)));
    public static final RegistryObject<ChainBlock> JADE_CHAIN = registerChain("jade_chain", properties -> new ChainBlock(properties.mapColor(MapColor.EMERALD)));
    public static final RegistryObject<ChainBlock> SAPPHIRE_CHAIN = registerChain("sapphire_chain", properties -> new ChainBlock(properties.mapColor(MapColor.COLOR_BLUE)));
    public static final RegistryObject<ChainBlock> DIAMOND_CHAIN = registerChain("diamond_chain", properties -> new ChainBlock(properties.mapColor(MapColor.DIAMOND)));
    public static final RegistryObject<ChainBlock> AMETHYST_CHAIN = registerChain("amethyst_chain", properties -> new ChainBlock(properties.mapColor(MapColor.COLOR_PURPLE)));
    public static final RegistryObject<ChainBlock> SILK_CHAIN = registerChain("silk_chain", properties -> new ChainBlock(properties.mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final RegistryObject<ChainBlock> BONE_CHAIN = registerChain("bone_chain", properties -> new ChainBlock(properties.mapColor(MapColor.TERRACOTTA_WHITE)));


    public static final RegistryObject<SoulGlassBlock> SOUL_GLASS = registerWithItem("soul_glass", () -> new SoulGlassBlock(BlockBehaviour.Properties.copy(GLASS).mapColor(MapColor.COLOR_PURPLE)));
    public static final RegistryObject<Block> FLINX_FUR_BLOCK = registerWithItem("flinx_fur_block", () -> new Block(BlockBehaviour.Properties.copy(WHITE_WOOL).mapColor(MapColor.TERRACOTTA_PINK)));
    public static final RegistryObject<WoolCarpetBlock> FLINX_FUR_CARPET = registerWithItem("flinx_fur_carpet", () -> new WoolCarpetBlock(DyeColor.PINK, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_PINK).strength(0.1F).sound(SoundType.WOOL).ignitedByLava()));
    public static final RegistryObject<Block> RAINBOW_WOOL = registerWithItem("rainbow_wool", () -> new Block(BlockBehaviour.Properties.copy(WHITE_WOOL).mapColor(MapColor.TERRACOTTA_PINK)));
    public static final RegistryObject<WoolCarpetBlock> RAINBOW_CARPET = registerWithItem("rainbow_carpet", () -> new WoolCarpetBlock(DyeColor.PINK, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_PINK).strength(0.1F).sound(SoundType.WOOL).ignitedByLava()));

    // 气球
    public static final RegistryObject<Block> WHITE_BALLOON = registerWithItem("white_balloon", () -> new Block(BlockBehaviour.Properties.copy(WHITE_WOOL).mapColor(MapColor.SNOW).strength(0.1F)));
    public static final RegistryObject<Block> LIGHT_GRAY_BALLOON = registerWithItem("light_gray_balloon", () -> new Block(BlockBehaviour.Properties.copy(WHITE_WOOL).mapColor(MapColor.COLOR_LIGHT_GRAY).strength(0.1F)));
    public static final RegistryObject<Block> GRAY_BALLOON = registerWithItem("gray_balloon", () -> new Block(BlockBehaviour.Properties.copy(WHITE_WOOL).mapColor(MapColor.COLOR_GRAY).strength(0.1F)));
    public static final RegistryObject<Block> BLACK_BALLOON = registerWithItem("black_balloon", () -> new Block(BlockBehaviour.Properties.copy(WHITE_WOOL).mapColor(MapColor.COLOR_BLACK).strength(0.1F)));
    public static final RegistryObject<Block> BROWN_BALLOON = registerWithItem("brown_balloon", () -> new Block(BlockBehaviour.Properties.copy(WHITE_WOOL).mapColor(MapColor.COLOR_BROWN).strength(0.1F)));
    public static final RegistryObject<Block> RED_BALLOON = registerWithItem("red_balloon", () -> new Block(BlockBehaviour.Properties.copy(WHITE_WOOL).mapColor(MapColor.COLOR_RED).strength(0.1F)));
    public static final RegistryObject<Block> ORANGE_BALLOON = registerWithItem("orange_balloon", () -> new Block(BlockBehaviour.Properties.copy(WHITE_WOOL).mapColor(MapColor.COLOR_ORANGE).strength(0.1F)));
    public static final RegistryObject<Block> YELLOW_BALLOON = registerWithItem("yellow_balloon", () -> new Block(BlockBehaviour.Properties.copy(WHITE_WOOL).mapColor(MapColor.COLOR_YELLOW).strength(0.1F)));
    public static final RegistryObject<Block> LIME_BALLOON = registerWithItem("lime_balloon", () -> new Block(BlockBehaviour.Properties.copy(WHITE_WOOL).mapColor(MapColor.COLOR_LIGHT_GREEN).strength(0.1F)));
    public static final RegistryObject<Block> GREEN_BALLOON = registerWithItem("green_balloon", () -> new Block(BlockBehaviour.Properties.copy(WHITE_WOOL).mapColor(MapColor.COLOR_GREEN).strength(0.1F)));
    public static final RegistryObject<Block> CYAN_BALLOON = registerWithItem("cyan_balloon", () -> new Block(BlockBehaviour.Properties.copy(WHITE_WOOL).mapColor(MapColor.COLOR_CYAN).strength(0.1F)));
    public static final RegistryObject<Block> LIGHT_BLUE_BALLOON = registerWithItem("light_blue_balloon", () -> new Block(BlockBehaviour.Properties.copy(WHITE_WOOL).mapColor(MapColor.COLOR_BLUE).strength(0.1F)));
    public static final RegistryObject<Block> BLUE_BALLOON = registerWithItem("blue_balloon", () -> new Block(BlockBehaviour.Properties.copy(WHITE_WOOL).mapColor(MapColor.COLOR_BLUE).strength(0.1F)));
    public static final RegistryObject<Block> PURPLE_BALLOON = registerWithItem("purple_balloon", () -> new Block(BlockBehaviour.Properties.copy(WHITE_WOOL).mapColor(MapColor.COLOR_PURPLE).strength(0.1F)));
    public static final RegistryObject<Block> MAGENTA_BALLOON = registerWithItem("magenta_balloon", () -> new Block(BlockBehaviour.Properties.copy(WHITE_WOOL).mapColor(MapColor.COLOR_MAGENTA).strength(0.1F)));
    public static final RegistryObject<Block> PINK_BALLOON = registerWithItem("pink_balloon", () -> new Block(BlockBehaviour.Properties.copy(WHITE_WOOL).mapColor(MapColor.COLOR_PINK).strength(0.1F)));
    // 圣物
    public static final RegistryObject<RelicBlock> KING_SLIME_RELIC = registerRelic("king_slime_relic");
    public static final RegistryObject<RelicBlock> EYE_OF_CTHULHU_RELIC = registerRelic("eye_of_cthulhu_relic");
    public static final RegistryObject<RelicBlock> BRAIN_OF_CTHULHU_RELIC = registerRelic("brain_of_cthulhu_relic");
    public static final RegistryObject<RelicBlock> EATER_OF_WORLDS_RELIC = registerRelic("eater_of_worlds_relic");
    public static final RegistryObject<RelicBlock> QUEEN_BEE_RELIC = registerRelic("queen_bee_relic");
    public static final RegistryObject<RelicBlock> DEERCLOPS_RELIC = registerRelic("deerclops_relic");
    public static final RegistryObject<RelicBlock> SKELETRON_RELIC = registerRelic("skeletron_relic");
    public static final RegistryObject<RelicBlock> WALL_OF_FLESH_RELIC = registerRelic("wall_of_flesh_relic");
    public static final RegistryObject<RelicBlock> HILL_OF_FLESH_RELIC = registerRelic("hill_of_flesh_relic");
    public static final RegistryObject<RelicBlock> THE_TWINS_RELIC = registerRelic("the_twins_relic");
    public static final RegistryObject<RelicBlock> SKELETRON_PRIME_RELIC = registerRelic("skeletron_prime_relic");

    public static final Supplier<BlockEntityType<RelicBlock.BEntity>> RELIC_ENTITY = ModBlocks.BLOCK_ENTITIES.register("relic_entity", () -> BlockEntityType.Builder.of(RelicBlock.BEntity::new, RELIC_BLOCKS.stream().map(RegistryObject::get).toArray(Block[]::new)).build(DSL.remainderType()));

    // 神庙
    public static final BlockSetType LIHZAHRD = BlockSetType.register(new BlockSetType("confluence:lihzahrd",
            false,
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
    public static final RegistryObject<LihzahrdDoorBlock> LIHZAHRD_DOOR = registerWithItem("lihzahrd_door", () -> new LihzahrdDoorBlock(LIHZAHRD, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).requiresCorrectToolForDrops().noOcclusion().strength(100.0F, ModBlocks.getObsidianBasedExplosionResistance(1000.0F)).pushReaction(PushReaction.BLOCK)));
    public static final DecoBlockSet LIHZAHRD_BRICKS = DecoBlockSet.builder("lihzahrd_bricks", () -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).requiresCorrectToolForDrops().strength(100.0F, ModBlocks.getObsidianBasedExplosionResistance(1000.0F))).stonecutting().build();


    private static RegistryObject<Block> registerWithCopy(String newName, Block originalBlock) {
        return registerWithItem(newName, () -> new Block(BlockBehaviour.Properties.copy(originalBlock)));
    }

    private static <B extends Block> RegistryObject<B> registerChain(String newName, Function<BlockBehaviour.Properties, B> function) {
        RegistryObject<B> block = BLOCKS.register(newName, () -> function.apply(BlockBehaviour.Properties.copy(Blocks.CHAIN)));
        ModItems.BLOCK_ITEMS.register(newName, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    private static <B extends Block> RegistryObject<B> registerWithItem(String newName, Supplier<B> supplier) {
        RegistryObject<B> block = BLOCKS.register(newName, supplier);
        ModItems.BLOCK_ITEMS.register(newName, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    private static <B extends Block> RegistryObject<B> registerWithItem(String id, Supplier<B> block, Function<B, BlockItem> function) {
        RegistryObject<B> object = BLOCKS.register(id, block);
        ModItems.BLOCK_ITEMS.register(id, () -> function.apply(object.get()));
        return object;
    }

    public static RegistryObject<RelicBlock> registerRelic(String id) {
        RegistryObject<RelicBlock> block = registerWithItem(id, () -> new RelicBlock(BlockBehaviour.Properties.copy(GOLD_BLOCK).lightLevel(state -> 7)), RelicBlock.BItem::new);
        RELIC_BLOCKS.add(block);
        return block;
    }
}
