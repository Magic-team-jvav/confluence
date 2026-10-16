package org.confluence.mod.common.init.block;

import com.mojang.datafixers.DSL;
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
import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

public class DecorativeBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Confluence.MODID);
    public static final List<DeferredBlock<RelicBlock>> RELIC_BLOCKS = new ArrayList<>();

    // 杂项
    public static final DeferredBlock<MuralBlock> MURAL_BLOCK = registerWithItem("mural_block", "壁画", () -> new MuralBlock(BlockBehaviour.Properties.ofFullCopy(STONE)));
    public static final Supplier<BlockEntityType<MuralBlock.BEntity>> MURAL_ENTITY_BLOCK = ModBlocks.BLOCK_ENTITIES.register("mural_entity_block", () -> BlockEntityType.Builder.of(MuralBlock.BEntity::new, MURAL_BLOCK.get()).build(DSL.remainderType()));

    public static final DeferredBlock<LostPaperBlock> LOST_PAPER_BLOCK = registerWithItem("lost_paper", "遗落纸张", LostPaperBlock::new);

    public static final DeferredBlock<GardenGnomeBlock> GARDEN_GNOME = registerWithItem("garden_gnome", "花园侏儒", () -> new GardenGnomeBlock(BlockBehaviour.Properties.ofFullCopy(STONE).strength(0.5F).noOcclusion()));
    public static final Supplier<BlockEntityType<GardenGnomeBlock.BEntity>> GARDEN_GNOME_ENTITY = ModBlocks.BLOCK_ENTITIES.register("garden_gnome", () -> BlockEntityType.Builder.of(GardenGnomeBlock.BEntity::new, GARDEN_GNOME.get()).build(DSL.remainderType()));

    public static final DeferredBlock<CarvedPumpkinBlock> CARVED_WHITE_PUMPKIN = registerWithItem("carved_white_pumpkin", "雕刻白南瓜", () -> new CarvedPumpkinBlock(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).instrument(NoteBlockInstrument.DIDGERIDOO).strength(1.0F).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<CarvedPumpkinBlock> JOHNNY_O_LANTERN = registerWithItem("johnny_o_lantern", "白南瓜灯", () -> new CarvedPumpkinBlock(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).mapColor(MapColor.COLOR_ORANGE).strength(1.0F).sound(SoundType.WOOD).lightLevel(p_187437_ -> 15).isValidSpawn(Blocks::always).pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<Block> GOLDEN_MELON = registerWithItem("golden_melon", "金西瓜", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_YELLOW)));
    public static final DeferredBlock<Block> ICE_MELON = registerWithItem("ice_melon", "冰西瓜", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_BLUE)));

    // 雕纹木板
    public static final DeferredBlock<Block> CHISELED_OAK_PLANKS = copyBlockRegister("chiseled_oak_planks", "雕纹橡木木板", OAK_PLANKS);
    public static final DeferredBlock<Block> CHISELED_SPRUCE_PLANKS = copyBlockRegister("chiseled_spruce_planks", "雕纹云杉木板", SPRUCE_PLANKS);
    public static final DeferredBlock<Block> WOOD_STONE_SLATTED_BLOCKS = copyBlockRegister("wood_stone_slatted_blocks", "木石板条块", OAK_PLANKS);

    public static final DeferredBlock<PooBlock> POO_BLOCK = registerWithItem("poo_block", "臭臭块", () -> new PooBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD).mapColor(MapColor.COLOR_BROWN)));

    // 砖
    public static final DecoBlockSet BLUE_ICE_BRICKS = DecoBlockSet.builder("blue_ice_bricks", () -> BlockBehaviour.Properties.ofFullCopy(Blocks.BLUE_ICE).mapColor(MapColor.COLOR_LIGHT_BLUE)).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "蓝冰砖");
        language.add(set.SLAB.get(), "蓝冰砖台阶");
        language.add(set.STAIRS.get(), "蓝冰砖楼梯");
        language.add(set.WALL.get(), "蓝冰砖墙");
    });
    public static final DecoBlockSet PACKED_ICE_BRICKS = DecoBlockSet.builder("packed_ice_bricks", () -> BlockBehaviour.Properties.ofFullCopy(Blocks.PACKED_ICE).mapColor(MapColor.COLOR_LIGHT_BLUE)).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "浮冰砖");
        language.add(set.SLAB.get(), "浮冰砖台阶");
        language.add(set.STAIRS.get(), "浮冰砖楼梯");
        language.add(set.WALL.get(), "浮冰砖墙");
    });
    public static final DecoBlockSet SANDSTONE_BRICKS = DecoBlockSet.builder("sandstone_bricks", () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).mapColor(MapColor.SAND)).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "砂岩砖");
        language.add(set.SLAB.get(), "砂岩砖台阶");
        language.add(set.STAIRS.get(), "砂岩砖楼梯");
        language.add(set.WALL.get(), "砂岩砖墙");
    });
    public static final DecoBlockSet RED_SANDSTONE_BRICKS = DecoBlockSet.builder("red_sandstone_bricks", () -> BlockBehaviour.Properties.ofFullCopy(Blocks.RED_SANDSTONE).mapColor(MapColor.TERRACOTTA_RED)).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "红砂岩砖");
        language.add(set.SLAB.get(), "红砂岩砖台阶");
        language.add(set.STAIRS.get(), "红砂岩砖楼梯");
        language.add(set.WALL.get(), "红砂岩砖墙");
    });
    public static final DecoBlockSet EBONSANDSTONE_BRICKS = DecoBlockSet.builder("ebonsandstone_bricks", () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).mapColor(MapColor.TERRACOTTA_PURPLE)).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "黑檀砂岩砖");
        language.add(set.SLAB.get(), "黑檀砂岩砖台阶");
        language.add(set.STAIRS.get(), "黑檀砂岩砖楼梯");
        language.add(set.WALL.get(), "黑檀砂岩砖墙");
    });
    public static final DecoBlockSet PEARLSANDSTONE_BRICKS = DecoBlockSet.builder("pearlsandstone_bricks", () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).mapColor(MapColor.TERRACOTTA_WHITE)).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "珍珠砂岩砖");
        language.add(set.SLAB.get(), "珍珠砂岩砖台阶");
        language.add(set.STAIRS.get(), "珍珠砂岩砖楼梯");
        language.add(set.WALL.get(), "珍珠砂岩砖墙");
    });
    public static final DecoBlockSet CRIMSANDSTONE_BRICKS = DecoBlockSet.builder("crimsandstone_bricks", () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).mapColor(MapColor.TERRACOTTA_GRAY)).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "猩红砂岩砖");
        language.add(set.SLAB.get(), "猩红砂岩砖台阶");
        language.add(set.STAIRS.get(), "猩红砂岩砖楼梯");
        language.add(set.WALL.get(), "猩红砂岩砖墙");
    });
    public static final DecoBlockSet SNOW_BRICKS = DecoBlockSet.builder("snow_bricks", () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(MapColor.TERRACOTTA_WHITE)).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "雪砖");
        language.add(set.SLAB.get(), "雪砖台阶");
        language.add(set.STAIRS.get(), "雪砖楼梯");
        language.add(set.WALL.get(), "雪砖墙");
    });
    public static final DecoBlockSet AETHERIUM_BRICKS = DecoBlockSet.builder("aetherium_bricks", () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).mapColor(MapColor.TERRACOTTA_WHITE)).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "以太砖");
        language.add(set.SLAB.get(), "以太台阶");
        language.add(set.STAIRS.get(), "以太楼梯");
        language.add(set.WALL.get(), "以太墙");
    });
    public static final DeferredBlock<Block> CRYSTAL_BLOCK = registerWithItem("crystal_block", "水晶块", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(MapColor.TERRACOTTA_WHITE)));

    public static final DeferredBlock<CloudBlock> FLOATING_WHEAT_BALE = registerWithItem("floating_wheat_bale", "飘飘麦捆", () -> new CloudBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_LIGHT_BLUE)
            .strength(0.3F)
            .sound(SoundType.SNOW)
            .isValidSpawn(Blocks::never)
            .isRedstoneConductor((blockState, blockGetter, blockPos) -> false)
            .isSuffocating((blockState, blockGetter, blockPos) -> false)));

    // 云
    public static final DeferredBlock<CloudBlockTrampoline> CLOUD_BLOCK_TRAMPOLINE = registerWithItem("cloud_block_trampoline", "云块蹦床", () -> new CloudBlockTrampoline(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_PINK)
            .strength(0.3F)
            .sound(SoundType.SNOW)
            .noOcclusion()
            .isValidSpawn(Blocks::never)
            .isRedstoneConductor((blockState, blockGetter, blockPos) -> false)
            .isSuffocating((blockState, blockGetter, blockPos) -> false)));
    public static final DeferredBlock<CloudBlock> BOUNCY_CLOUD_BLOCK = registerWithItem("bouncy_cloud_block", "弹力云块", () -> new CloudBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_PINK)
            .strength(0.3F)
            .sound(SoundType.SNOW)
            .noOcclusion()
            .jumpFactor(2.5F)
            .isValidSpawn(Blocks::never)
            .isRedstoneConductor((blockState, blockGetter, blockPos) -> false)
            .isSuffocating((blockState, blockGetter, blockPos) -> false)));
    public static final DeferredBlock<CloudBlock> STAR_CLOUD_BLOCK = registerWithItem("star_cloud_block", "星星云", () -> new CloudBlock(BlockBehaviour.Properties.of()
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
    public static final DeferredBlock<SwordInStoneBlock> SWORD_IN_STONE = registerWithItem("sword_in_stone", "石中剑", SwordInStoneBlock::new);

    public static final DeferredBlock<CrispyHoneyBlock> CRISPY_HONEY_BLOCK = registerWithItem("crispy_honey_block", "松脆蜂蜜块", CrispyHoneyBlock::new);

    public static final DeferredBlock<Block> ASPHALT_BLOCK = registerWithItem("asphalt_block", "沥青块", () -> new Block(BlockBehaviour.Properties.ofFullCopy(BLACK_TERRACOTTA).friction(0.4F).speedFactor(1.2F)));
    public static final DeferredBlock<Block> FLESH_BLOCK = registerWithItem("flesh_block", "血肉块", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE)));
    public static final DeferredBlock<Block> LESION_BLOCK = registerWithItem("lesion_block", "病变块", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE)));
    public static final DeferredBlock<RemainsBlock> REMAINS_BLOCK = registerWithItem("remains_block", "遗骸", () -> new RemainsBlock(BlockBehaviour.Properties.of().strength(1.0f).pushReaction(PushReaction.DESTROY)));

    // 纯净玻璃
    public static final DeferredBlock<ConnectedGlassBlock> PURE_GLASS = registerWithItem("pure_glass", "纯净玻璃", () -> new ConnectedGlassBlock(BlockBehaviour.Properties.ofFullCopy(GLASS)));
    public static final DeferredBlock<ConnectedStainedGlassBlock> WHITE_PURE_GLASS = registerWithItem("white_pure_glass", "白色纯净玻璃", () -> new ConnectedStainedGlassBlock(DyeColor.WHITE, BlockBehaviour.Properties.ofFullCopy(WHITE_STAINED_GLASS).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<ConnectedStainedGlassBlock> LIGHT_GRAY_PURE_GLASS = registerWithItem("light_gray_pure_glass", "淡灰色纯净玻璃", () -> new ConnectedStainedGlassBlock(DyeColor.LIGHT_GRAY, BlockBehaviour.Properties.ofFullCopy(LIGHT_GRAY_STAINED_GLASS).mapColor(MapColor.COLOR_LIGHT_GRAY)));
    public static final DeferredBlock<ConnectedStainedGlassBlock> GRAY_PURE_GLASS = registerWithItem("gray_pure_glass", "灰色纯净玻璃", () -> new ConnectedStainedGlassBlock(DyeColor.GRAY, BlockBehaviour.Properties.ofFullCopy(GRAY_STAINED_GLASS).mapColor(MapColor.COLOR_GRAY)));
    public static final DeferredBlock<ConnectedStainedGlassBlock> BLACK_PURE_GLASS = registerWithItem("black_pure_glass", "黑色纯净玻璃", () -> new ConnectedStainedGlassBlock(DyeColor.BLACK, BlockBehaviour.Properties.ofFullCopy(BLACK_STAINED_GLASS).mapColor(MapColor.COLOR_BLACK)));
    public static final DeferredBlock<ConnectedStainedGlassBlock> BROWN_PURE_GLASS = registerWithItem("brown_pure_glass", "棕色纯净玻璃", () -> new ConnectedStainedGlassBlock(DyeColor.BROWN, BlockBehaviour.Properties.ofFullCopy(BROWN_STAINED_GLASS).mapColor(MapColor.COLOR_BROWN)));
    public static final DeferredBlock<ConnectedStainedGlassBlock> RED_PURE_GLASS = registerWithItem("red_pure_glass", "红色纯净玻璃", () -> new ConnectedStainedGlassBlock(DyeColor.RED, BlockBehaviour.Properties.ofFullCopy(RED_STAINED_GLASS).mapColor(MapColor.COLOR_RED)));
    public static final DeferredBlock<ConnectedStainedGlassBlock> ORANGE_PURE_GLASS = registerWithItem("orange_pure_glass", "橙色纯净玻璃", () -> new ConnectedStainedGlassBlock(DyeColor.ORANGE, BlockBehaviour.Properties.ofFullCopy(ORANGE_STAINED_GLASS).mapColor(MapColor.COLOR_ORANGE)));
    public static final DeferredBlock<ConnectedStainedGlassBlock> YELLOW_PURE_GLASS = registerWithItem("yellow_pure_glass", "黄色纯净玻璃", () -> new ConnectedStainedGlassBlock(DyeColor.YELLOW, BlockBehaviour.Properties.ofFullCopy(YELLOW_STAINED_GLASS).mapColor(MapColor.COLOR_YELLOW)));
    public static final DeferredBlock<ConnectedStainedGlassBlock> LIME_PURE_GLASS = registerWithItem("lime_pure_glass", "黄绿色纯净玻璃", () -> new ConnectedStainedGlassBlock(DyeColor.LIME, BlockBehaviour.Properties.ofFullCopy(LIME_STAINED_GLASS).mapColor(MapColor.COLOR_LIGHT_GREEN)));
    public static final DeferredBlock<ConnectedStainedGlassBlock> GREEN_PURE_GLASS = registerWithItem("green_pure_glass", "绿色纯净玻璃", () -> new ConnectedStainedGlassBlock(DyeColor.GREEN, BlockBehaviour.Properties.ofFullCopy(GREEN_STAINED_GLASS).mapColor(MapColor.TERRACOTTA_GREEN)));
    public static final DeferredBlock<ConnectedStainedGlassBlock> CYAN_PURE_GLASS = registerWithItem("cyan_pure_glass", "青色纯净玻璃", () -> new ConnectedStainedGlassBlock(DyeColor.CYAN, BlockBehaviour.Properties.ofFullCopy(CYAN_STAINED_GLASS).mapColor(MapColor.COLOR_CYAN)));
    public static final DeferredBlock<ConnectedStainedGlassBlock> LIGHT_BLUE_PURE_GLASS = registerWithItem("light_blue_pure_glass", "淡蓝色纯净玻璃", () -> new ConnectedStainedGlassBlock(DyeColor.LIGHT_BLUE, BlockBehaviour.Properties.ofFullCopy(LIGHT_BLUE_STAINED_GLASS).mapColor(MapColor.COLOR_LIGHT_BLUE)));
    public static final DeferredBlock<ConnectedStainedGlassBlock> BLUE_PURE_GLASS = registerWithItem("blue_pure_glass", "蓝色纯净玻璃", () -> new ConnectedStainedGlassBlock(DyeColor.BLUE, BlockBehaviour.Properties.ofFullCopy(BLUE_STAINED_GLASS).mapColor(MapColor.TERRACOTTA_BLUE)));
    public static final DeferredBlock<ConnectedStainedGlassBlock> PURPLE_PURE_GLASS = registerWithItem("purple_pure_glass", "紫色纯净玻璃", () -> new ConnectedStainedGlassBlock(DyeColor.PURPLE, BlockBehaviour.Properties.ofFullCopy(PURPLE_STAINED_GLASS).mapColor(MapColor.COLOR_PURPLE)));
    public static final DeferredBlock<ConnectedStainedGlassBlock> MAGENTA_PURE_GLASS = registerWithItem("magenta_pure_glass", "品红纯净玻璃", () -> new ConnectedStainedGlassBlock(DyeColor.MAGENTA, BlockBehaviour.Properties.ofFullCopy(MAGENTA_STAINED_GLASS).mapColor(MapColor.TERRACOTTA_MAGENTA)));
    public static final DeferredBlock<ConnectedStainedGlassBlock> PINK_PURE_GLASS = registerWithItem("pink_pure_glass", "粉红纯净玻璃", () -> new ConnectedStainedGlassBlock(DyeColor.PINK, BlockBehaviour.Properties.ofFullCopy(PINK_STAINED_GLASS).mapColor(MapColor.COLOR_PINK)));

    // 矿砖
    public static final DecoBlockSet COPPER_BRICKS = DecoBlockSet.builder("copper_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_ORANGE)).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "铜砖");
        language.add(set.SLAB.get(), "铜砖台阶");
        language.add(set.STAIRS.get(), "铜砖楼梯");
        language.add(set.WALL.get(), "铜砖墙");
    });
    public static final DeferredBlock<Block> CHISELED_COPPER_BRICKS = registerWithItem("chiseled_copper_bricks", "雕纹铜砖", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_ORANGE)));
    public static final DeferredBlock<Block> COPPER_TILES = registerWithItem("copper_tiles", "铜瓦", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_ORANGE)));
    public static final DecoBlockSet TIN_BRICKS = DecoBlockSet.builder("tin_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_YELLOW)).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "锡砖");
        language.add(set.SLAB.get(), "锡砖台阶");
        language.add(set.STAIRS.get(), "锡砖楼梯");
        language.add(set.WALL.get(), "锡砖墙");
    });
    public static final DeferredBlock<Block> CHISELED_TIN_BRICKS = registerWithItem("chiseled_tin_bricks", "雕纹锡砖", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_YELLOW)));
    public static final DeferredBlock<Block> TIN_TILES = registerWithItem("tin_tiles", "锡瓦", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_YELLOW)));
    public static final DecoBlockSet IRON_BRICKS = DecoBlockSet.builder("iron_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "铁砖");
        language.add(set.SLAB.get(), "铁砖台阶");
        language.add(set.STAIRS.get(), "铁砖楼梯");
        language.add(set.WALL.get(), "铁砖墙");
    });
    public static final DeferredBlock<Block> CHISELED_IRON_BRICKS = registerWithItem("chiseled_iron_bricks", "雕纹铁砖", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)));
    public static final DecoBlockSet LEAD_BRICKS = DecoBlockSet.builder("lead_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_GRAY)).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "铅砖");
        language.add(set.SLAB.get(), "铅砖台阶");
        language.add(set.STAIRS.get(), "铅砖楼梯");
        language.add(set.WALL.get(), "铅砖墙");
    });
    public static final DeferredBlock<Block> CHISELED_LEAD_BRICKS = registerWithItem("chiseled_lead_bricks", "雕纹铅砖", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_GRAY)));
    public static final DecoBlockSet SILVER_BRICKS = DecoBlockSet.builder("silver_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_WHITE)).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "银砖");
        language.add(set.SLAB.get(), "银砖台阶");
        language.add(set.STAIRS.get(), "银砖楼梯");
        language.add(set.WALL.get(), "银砖墙");
    });
    public static final DeferredBlock<Block> CHISELED_SILVER_BRICKS = registerWithItem("chiseled_silver_bricks", "雕纹银砖", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_GRAY)));
    public static final DecoBlockSet TUNGSTEN_BRICKS = DecoBlockSet.builder("tungsten_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_LIGHT_GREEN)).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "钨砖");
        language.add(set.SLAB.get(), "钨砖台阶");
        language.add(set.STAIRS.get(), "钨砖楼梯");
        language.add(set.WALL.get(), "钨砖墙");
    });
    public static final DeferredBlock<Block> CHISELED_TUNGSTEN_BRICKS = registerWithItem("chiseled_tungsten_bricks", "雕纹钨砖", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_LIGHT_GREEN)));
    public static final DecoBlockSet GOLDEN_BRICKS = DecoBlockSet.builder("golden_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_YELLOW)).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "金砖");
        language.add(set.SLAB.get(), "金砖台阶");
        language.add(set.STAIRS.get(), "金砖楼梯");
        language.add(set.WALL.get(), "金砖墙");
    });
    public static final DeferredBlock<Block> CHISELED_GOLDEN_BRICKS = registerWithItem("chiseled_golden_bricks", "雕纹金砖", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_YELLOW)));
    public static final DecoBlockSet PLATINUM_BRICKS = DecoBlockSet.builder("platinum_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_CYAN)).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "铂金砖");
        language.add(set.SLAB.get(), "铂金砖台阶");
        language.add(set.STAIRS.get(), "铂金砖楼梯");
        language.add(set.WALL.get(), "铂金砖墙");
    });
    public static final DeferredBlock<Block> CHISELED_PLATINUM_BRICKS = registerWithItem("chiseled_platinum_bricks", "雕纹铂金砖", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_CYAN)));
    public static final DecoBlockSet DEMONITE_ORE_BRICKS = DecoBlockSet.builder("demonite_ore_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_WHITE)).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "魔矿砖");
        language.add(set.SLAB.get(), "魔矿砖台阶");
        language.add(set.STAIRS.get(), "魔矿砖楼梯");
        language.add(set.WALL.get(), "魔矿砖墙");
    });
    public static final DecoBlockSet EBONSTONE_BRICKS = DecoBlockSet.builder("ebonstone_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_PURPLE)).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "黑檀石砖");
        language.add(set.SLAB.get(), "黑檀石砖台阶");
        language.add(set.STAIRS.get(), "黑檀石砖楼梯");
        language.add(set.WALL.get(), "黑檀石砖墙");
    });
    public static final DecoBlockSet METEORITE_BRICKS = DecoBlockSet.builder("meteorite_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_ORANGE)).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "陨石砖");
        language.add(set.SLAB.get(), "陨石砖台阶");
        language.add(set.STAIRS.get(), "陨石砖楼梯");
        language.add(set.WALL.get(), "陨石砖墙");
    });
    public static final DecoBlockSet CRIMTANE_ORE_BRICKS = DecoBlockSet.builder("crimtane_ore_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_RED)).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "猩红矿砖");
        language.add(set.SLAB.get(), "猩红矿砖台阶");
        language.add(set.STAIRS.get(), "猩红矿砖楼梯");
        language.add(set.WALL.get(), "猩红矿砖墙");
    });
    public static final DecoBlockSet CRIMSTONE_BRICKS = DecoBlockSet.builder("crimstone_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_RED)).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "猩红石砖");
        language.add(set.SLAB.get(), "猩红石砖台阶");
        language.add(set.STAIRS.get(), "猩红石砖楼梯");
        language.add(set.WALL.get(), "猩红石砖墙");
    });
    public static final DecoBlockSet PEARLSTONE_BRICKS = DecoBlockSet.builder("pearlstone_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_PINK)).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "珍珠石砖");
        language.add(set.SLAB.get(), "珍珠石砖台阶");
        language.add(set.STAIRS.get(), "珍珠石砖楼梯");
        language.add(set.WALL.get(), "珍珠石砖墙");
    });
    public static final DecoBlockSet HELLSTONE_BRICKS = DecoBlockSet.builder("hellstone_bricks",
            () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS)
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
            .build((set, language) -> {
                language.add(set.FULL.get(), "狱石砖");
                language.add(set.SLAB.get(), "狱石砖台阶");
                language.add(set.STAIRS.get(), "狱石砖楼梯");
                language.add(set.WALL.get(), "狱石砖墙");
            });

    public static final DeferredBlock<Block> GREEN_CANDY_BLOCK = registerWithItem("green_candy_block", "绿色糖块", () -> new CandyBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.COLOR_GREEN)));
    public static final DeferredBlock<Block> RED_CANDY_BLOCK = registerWithItem("red_candy_block", "红色糖块", () -> new CandyBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.COLOR_RED)));
    public static final DeferredBlock<Block> FROZEN_GEL_BLOCK = registerWithItem("frozen_gel_block", "冻凝胶块", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.SLIME_BLOCK).friction(1f).speedFactor(1.06F)));
    public static final DeferredBlock<Block> BLUE_GEL_BLOCK = registerWithItem("blue_gel_block", "凝胶块", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).sound(SoundType.SLIME_BLOCK)));
    public static final DeferredBlock<Block> RAINBOW_GEL_BLOCK = registerWithItem("rainbow_gel_block", "彩虹凝胶块", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).sound(SoundType.SLIME_BLOCK)));
    public static final DecoBlockSet RAINBOW_GEL_BRICKS = DecoBlockSet.builder("rainbow_gel_bricks", () -> BlockBehaviour.Properties.ofFullCopy(MUD_BRICKS).mapColor(MapColor.COLOR_BLUE).sound(SoundType.SLIME_BLOCK)).stonecutting().skipCreativeTab().build((set, language) -> {
        language.add(set.FULL.get(), "彩虹凝胶砖");
        language.add(set.SLAB.get(), "彩虹凝胶砖台阶");
        language.add(set.STAIRS.get(), "彩虹凝胶砖楼梯");
        language.add(set.WALL.get(), "彩虹凝胶砖墙");
    });
    public static final DecoBlockSet BLUE_GEL_BRICKS = DecoBlockSet.builder("blue_gel_bricks", () -> BlockBehaviour.Properties.ofFullCopy(MUD_BRICKS).mapColor(MapColor.COLOR_BLUE).sound(SoundType.SLIME_BLOCK)).stonecutting().skipCreativeTab().build((set, language) -> {
        language.add(set.FULL.get(), "蓝色凝胶砖");
        language.add(set.SLAB.get(), "蓝色凝胶砖台阶");
        language.add(set.STAIRS.get(), "蓝色凝胶砖楼梯");
        language.add(set.WALL.get(), "蓝色凝胶砖墙");
    });
    public static final DecoBlockSet PINK_GEL_BRICKS = DecoBlockSet.builder("pink_gel_bricks", () -> BlockBehaviour.Properties.ofFullCopy(MUD_BRICKS).mapColor(MapColor.COLOR_PINK).sound(SoundType.SLIME_BLOCK)).stonecutting().skipCreativeTab().build((set, language) -> {
        language.add(set.FULL.get(), "粉色凝胶砖");
        language.add(set.SLAB.get(), "粉色凝胶砖台阶");
        language.add(set.STAIRS.get(), "粉色凝胶砖楼梯");
        language.add(set.WALL.get(), "粉色凝胶砖墙");
    });

    public static final DeferredBlock<HalfTransparentBlock> PINK_GEL_BLOCK = registerWithItem("pink_gel_block", "粉凝胶块", () -> new HalfTransparentBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).sound(SoundType.SLIME_BLOCK).noOcclusion()));

    // 天域
    public static final DecoBlockSet SUN_PLATE = DecoBlockSet.builder("sun_plate", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_BLUE)).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "日盘块");
        language.add(set.SLAB.get(), "日盘台阶");
        language.add(set.STAIRS.get(), "日盘楼梯");
        language.add(set.WALL.get(), "日盘墙");
    });
    public static final DeferredBlock<DoorBlock> SKYWARE_DOOR = registerWithItem("skyware_door", "天域门", () -> new DoorBlock(BlockSetType.STONE, BlockBehaviour.Properties.of().mapColor(BLUE_ICE.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(4.0F).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<DoorBlock> SKYWARE_GLASS_DOOR = registerWithItem("skyware_glass_door", "天域玻璃门", () -> new DoorBlock(BlockSetType.STONE, BlockBehaviour.Properties.of().mapColor(BLUE_ICE.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(4.0F).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DecoBlockSet DISC_BLOCK = DecoBlockSet.builder("disc_block", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.COLOR_YELLOW)).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "飞盘块");
        language.add(set.SLAB.get(), "飞盘台阶");
        language.add(set.STAIRS.get(), "飞盘楼梯");
        language.add(set.WALL.get(), "飞盘墙");
    });
    public static final DecoBlockSet MOON_PLATE = DecoBlockSet.builder("moon_plate", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.TERRACOTTA_BLUE)).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "月盘块");
        language.add(set.SLAB.get(), "月盘台阶");
        language.add(set.STAIRS.get(), "月盘楼梯");
        language.add(set.WALL.get(), "月盘墙");
    });
    public static final DeferredBlock<Block> FALLEN_STAR_BLOCK = registerWithItem("fallen_star_block", "坠落之星块", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).lightLevel(state -> 9).mapColor(MapColor.COLOR_YELLOW)));

    // 黑曜石

    public static final DecoBlockSet OBSIDIAN_BRICKS = DecoBlockSet.builder("obsidian_bricks", () -> BlockBehaviour.Properties.ofFullCopy(OBSIDIAN).mapColor(MapColor.COLOR_BLACK)).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "黑曜石砖");
        language.add(set.SLAB.get(), "黑曜石砖台阶");
        language.add(set.STAIRS.get(), "黑曜石砖楼梯");
        language.add(set.WALL.get(), "黑曜石砖墙");
    });
    public static final DeferredBlock<Block> OBSIDIAN_SMALL_BRICKS = copyBlockRegister("obsidian_small_bricks", "切制黑曜石砖", OBSIDIAN);
    public static final DeferredBlock<DoorBlock> OBSIDIAN_BRICKS_DOOR = registerWithItem("obsidian_bricks_door", "黑曜石砖门", () -> new DoorBlock(
            BlockSetType.STONE,
            BlockBehaviour.Properties.of()
                    .mapColor(OBSIDIAN.defaultMapColor())
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(5.0F)
                    .noOcclusion()
                    .ignitedByLava()
                    .pushReaction(PushReaction.DESTROY)
    ));
    public static final DeferredBlock<Block> CHISELED_OBSIDIAN_BRICKS = copyBlockRegister("chiseled_obsidian_bricks", "雕纹黑曜石块", OBSIDIAN);
    public static final DeferredBlock<Block> SMOOTH_OBSIDIAN = copyBlockRegister("smooth_obsidian", "光滑黑曜石块", OBSIDIAN);
    public static final DecoBlockSet GLOOM_OBSIDIAN_BRICKS = DecoBlockSet.builder("gloom_obsidian_bricks", () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN).mapColor(MapColor.COLOR_BLACK)).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "黯虚黑曜石砖");
        language.add(set.SLAB.get(), "黯虚黑曜石砖台阶");
        language.add(set.STAIRS.get(), "黯虚黑曜石砖楼梯");
        language.add(set.WALL.get(), "黯虚黑曜石砖墙");
    });
    public static final DecoBlockSet CRYING_OBSIDIAN_BRICKS = DecoBlockSet.builder("crying_obsidian_bricks", () -> BlockBehaviour.Properties.ofFullCopy(Blocks.CRYING_OBSIDIAN).mapColor(MapColor.COLOR_BLACK)).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "哭泣的黑曜石砖");
        language.add(set.SLAB.get(), "哭泣的黑曜石砖台阶");
        language.add(set.STAIRS.get(), "哭泣的黑曜石砖楼梯");
        language.add(set.WALL.get(), "哭泣的黑曜石砖墙");
    });

    // 王朝木系列
    public static final DeferredBlock<Block> WHITE_PAPER_PANE = copyBlockRegister("white_paper_pane", "缟白纸窗格", OAK_PLANKS);
    public static final DeferredBlock<Block> WHITE_PAPER_PANE_LAMP = registerWithItem("white_paper_pane_lamp", "缟白纸窗格灯", () -> new Block(BlockBehaviour.Properties.ofFullCopy(OAK_PLANKS).lightLevel(state -> 15)));
    public static final DeferredBlock<Block> MALACHITE_PAPER_PANE = copyBlockRegister("malachite_paper_pane", "石绿纸窗格", OAK_PLANKS);
    public static final DeferredBlock<Block> MALACHITE_PAPER_PANE_LAMP = registerWithItem("malachite_paper_pane_lamp", "石绿纸窗格灯", () -> new Block(BlockBehaviour.Properties.ofFullCopy(OAK_PLANKS).lightLevel(state -> 15)));
    public static final DeferredBlock<DoorBlock> TRADITIONAL_DYNASTY_DOOR = registerWithItem("traditional_dynasty_door", "传统王朝木门", () -> new DoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(OAK_DOOR).mapColor(DyeColor.BROWN).pushReaction(PushReaction.BLOCK)));

    // 松树
    public static final DeferredBlock<DoorBlock> CHRISTMAS_PINE_DOOR = registerWithItem("christmas_pine_door", "圣诞松木门", () -> new DoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(OAK_DOOR).mapColor(DyeColor.BROWN).pushReaction(PushReaction.BLOCK)));
    public static final DeferredBlock<TrapDoorBlock> CHRISTMAS_PINE_TRAPDOOR = registerWithItem("christmas_pine_trapdoor", "圣诞松木活板门", () -> new TrapDoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(OAK_DOOR).mapColor(DyeColor.BROWN).pushReaction(PushReaction.BLOCK)));

    // 花岗岩
    public static final DecoBlockSet GRANITE_BRICKS = DecoBlockSet.builder("granite_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_BLUE)).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "花岗岩砖");
        language.add(set.SLAB.get(), "花岗岩台阶");
        language.add(set.STAIRS.get(), "花岗岩楼梯");
        language.add(set.WALL.get(), "花岗岩墙");
    });
    public static final DeferredBlock<Block> GRANITE_COLUMN = registerWithItem("granite_column", "花岗岩柱", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_BLUE)));
    public static final DeferredBlock<Block> CRACKED_GRANITE_BRICKS = registerWithItem("cracked_granite_bricks", "裂纹花岗岩砖", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_BLUE)));
    public static final DeferredBlock<Block> POLISHED_GRANITE = registerWithItem("polished_granite", "磨制花岗岩", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_BLUE)));
    public static final DeferredBlock<Block> CHISELED_GRANITE_BRICKS = registerWithItem("chiseled_granite_bricks", "雕纹花岗岩砖", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_BLUE)));

    // 方解石
    public static final DecoBlockSet MARBLE_BRICKS = DecoBlockSet.builder("marble_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "大理石砖");
        language.add(set.SLAB.get(), "大理石砖");
        language.add(set.STAIRS.get(), "大理石砖");
        language.add(set.WALL.get(), "大理石砖");
    });
    public static final DeferredBlock<Block> MARBLE_COLUMN = registerWithItem("marble_column", "大理石柱", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<Block> CRACKED_MARBLE_BRICKS = registerWithItem("cracked_marble_bricks", "裂纹大理石砖", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<Block> MARBLE_SMALL_BRICKS = registerWithItem("marble_small_bricks", "大理细石砖", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<Block> MARBLE_CHESSBOARD_BRICKS = registerWithItem("marble_chessboard_bricks", "棋盘大理石砖", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<Block> MARBLE_ETERNAL_CHESSBOARD_BRICKS = registerWithItem("marble_eternal_chessboard_bricks", "永恒棋盘大理石砖", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<GlazedTerracottaBlock> GILDED_MARBLE = registerWithItem("gilded_marble", "镶金大理石", () -> new GlazedTerracottaBlock(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<GlazedTerracottaBlock> CHISELED_MARBLE_BRICKS = registerWithItem("chiseled_marble_bricks", "雕纹大理石砖", () -> new GlazedTerracottaBlock(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<Block> POLISHED_MARBLE = registerWithItem("polished_marble", "磨制大理石", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)));

    // 地牢
    public static final DecoBlockSet BLUE_BRICKS = DecoBlockSet.builder("blue_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(DyeColor.BLUE).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "蓝色地牢砖");
        language.add(set.SLAB.get(), "蓝色地牢砖台阶");
        language.add(set.STAIRS.get(), "蓝色地牢砖楼梯");
        language.add(set.WALL.get(), "蓝色地牢砖墙");
    });
    public static final DecoBlockSet GREEN_BRICKS = DecoBlockSet.builder("green_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(DyeColor.GREEN).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "绿色地牢砖");
        language.add(set.SLAB.get(), "绿色地牢砖台阶");
        language.add(set.STAIRS.get(), "绿色地牢砖楼梯");
        language.add(set.WALL.get(), "绿色地牢砖墙");
    });
    public static final DecoBlockSet PINK_BRICKS = DecoBlockSet.builder("pink_bricks", () -> BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(DyeColor.PINK).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "粉色地牢砖");
        language.add(set.SLAB.get(), "粉色地牢砖台阶");
        language.add(set.STAIRS.get(), "粉色地牢砖楼梯");
        language.add(set.WALL.get(), "粉色地牢砖墙");
    });
    public static final DeferredBlock<CrackedBricksBlock> CRACKED_BLUE_BRICKS = registerWithItem("cracked_blue_bricks", "碎裂蓝色地牢砖", () -> new CrackedBricksBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).strength(0).mapColor(DyeColor.BLUE)));
    public static final DeferredBlock<CrackedBricksBlock> CRACKED_GREEN_BRICKS = registerWithItem("cracked_green_bricks", "碎裂绿色地牢砖", () -> new CrackedBricksBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).strength(0).mapColor(DyeColor.GREEN)));
    public static final DeferredBlock<CrackedBricksBlock> CRACKED_PINK_BRICKS = registerWithItem("cracked_pink_bricks", "碎裂粉色地牢砖", () -> new CrackedBricksBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).strength(0).mapColor(DyeColor.PINK)));
    public static final DeferredBlock<Block> CHISELED_BLUE_BRICKS = registerWithItem("chiseled_blue_bricks", "雕纹蓝色地牢砖", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(DyeColor.BLUE).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<Block> CHISELED_GREEN_BRICKS = registerWithItem("chiseled_green_bricks", "雕纹绿色地牢砖", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(DyeColor.GREEN).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<Block> CHISELED_PINK_BRICKS = registerWithItem("chiseled_pink_bricks", "雕纹粉色地牢砖", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(DyeColor.PINK).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<DoorBlock> DUNGEON_DOOR = registerWithItem("dungeon_door", "地牢门", () -> new DoorBlock(BlockSetType.STONE, BlockBehaviour.Properties.ofFullCopy(IRON_DOOR).mapColor(DyeColor.GRAY).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<EnchantedBricksBlock> ENCHANTED_BLUE_BRICKS = registerWithItem("enchanted_blue_bricks", "蕴魔蓝色地牢砖", () -> new EnchantedBricksBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(DyeColor.BLUE).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<EnchantedBricksBlock> ENCHANTED_GREEN_BRICKS = registerWithItem("enchanted_green_bricks", "蕴魔绿色地牢砖", () -> new EnchantedBricksBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(DyeColor.BLUE).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<EnchantedBricksBlock> ENCHANTED_PINK_BRICKS = registerWithItem("enchanted_pink_bricks", "蕴魔粉色地牢砖", () -> new EnchantedBricksBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(DyeColor.BLUE).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<RotatedPillarBlock> BLUE_BRICK_COLUMN = registerWithItem("blue_brick_column", "蓝色地牢砖柱", () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(DyeColor.BLUE).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<RotatedPillarBlock> GREEN_BRICK_COLUMN = registerWithItem("green_brick_column", "绿色地牢砖柱", () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(DyeColor.GREEN).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));
    public static final DeferredBlock<RotatedPillarBlock> PINK_BRICK_COLUMN = registerWithItem("pink_brick_column", "粉色地牢砖柱", () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(DyeColor.PINK).pushReaction(PushReaction.BLOCK).strength(5.0F, ModBlocks.getObsidianBasedExplosionResistance(100))));

    // 大宝石块
    public static final DeferredBlock<Block> RUBY_BLOCK = registerWithItem("ruby_block", "红玉块", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.COLOR_RED)));
    public static final DeferredBlock<Block> AMBER_BLOCK = registerWithItem("amber_block", "琥珀块", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.COLOR_ORANGE)));
    public static final DeferredBlock<Block> TOPAZ_BLOCK = registerWithItem("topaz_block", "黄玉块", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.COLOR_YELLOW)));
    public static final DeferredBlock<Block> JADE_BLOCK = registerWithItem("jade_block", "翡翠块", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.EMERALD)));
    public static final DeferredBlock<Block> SAPPHIRE_BLOCK = registerWithItem("sapphire_block", "蓝玉块", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.COLOR_BLUE)));
    public static final DeferredBlock<Block> AMETHYST_BLOCK = registerWithItem("amethyst_block", "紫晶块", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.COLOR_PURPLE)));

    // 锁链
    public static final DeferredBlock<ChainBlock> RUBY_CHAIN = copyBlockRegister("ruby_chain", "红玉锁链", CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.COLOR_RED)));
    public static final DeferredBlock<ChainBlock> AMBER_CHAIN = copyBlockRegister("amber_chain", "琥珀锁链", CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.COLOR_ORANGE)));
    public static final DeferredBlock<ChainBlock> TOPAZ_CHAIN = copyBlockRegister("topaz_chain", "黄玉锁链", CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.COLOR_YELLOW)));
    public static final DeferredBlock<ChainBlock> JADE_CHAIN = copyBlockRegister("jade_chain", "翡翠锁链", CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.EMERALD)));
    public static final DeferredBlock<ChainBlock> SAPPHIRE_CHAIN = copyBlockRegister("sapphire_chain", "蓝玉锁链", CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.COLOR_BLUE)));
    public static final DeferredBlock<ChainBlock> DIAMOND_CHAIN = copyBlockRegister("diamond_chain", "钻石锁链", CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.DIAMOND)));
    public static final DeferredBlock<ChainBlock> AMETHYST_CHAIN = copyBlockRegister("amethyst_chain", "紫晶锁链", CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.COLOR_PURPLE)));
    public static final DeferredBlock<ChainBlock> SILK_CHAIN = copyBlockRegister("silk_chain", "蛛丝锁链", CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<ChainBlock> BONE_CHAIN = copyBlockRegister("bone_chain", "骨头锁链", CHAIN, properties -> new ChainBlock(properties.mapColor(MapColor.TERRACOTTA_WHITE)));

    public static final DeferredBlock<SoulGlassBlock> SOUL_GLASS = registerWithItem("soul_glass", "灵魂玻璃", () -> new SoulGlassBlock(BlockBehaviour.Properties.ofFullCopy(GLASS).mapColor(MapColor.COLOR_PURPLE)));
    public static final DeferredBlock<Block> FLINX_FUR_BLOCK = registerWithItem("flinx_fur_block", "小雪怪皮毛块", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.TERRACOTTA_PINK)));
    public static final DeferredBlock<WoolCarpetBlock> FLINX_FUR_CARPET = registerWithItem("flinx_fur_carpet", "小雪怪皮毛地毯", () -> new WoolCarpetBlock(DyeColor.PINK, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_PINK).strength(0.1F).sound(SoundType.WOOL).ignitedByLava()));
    public static final DeferredBlock<Block> RAINBOW_WOOL = registerWithItem("rainbow_wool", "彩虹羊毛", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.TERRACOTTA_PINK)));
    public static final DeferredBlock<WoolCarpetBlock> RAINBOW_CARPET = registerWithItem("rainbow_carpet", "彩虹地毯", () -> new WoolCarpetBlock(DyeColor.PINK, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_PINK).strength(0.1F).sound(SoundType.WOOL).ignitedByLava()));

    // 气球
    public static final DeferredBlock<Block> WHITE_BALLOON = registerWithItem("white_balloon", "白色气球", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.SNOW).strength(0.1F)));
    public static final DeferredBlock<Block> LIGHT_GRAY_BALLOON = registerWithItem("light_gray_balloon", "淡灰色气球", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.COLOR_LIGHT_GRAY).strength(0.1F)));
    public static final DeferredBlock<Block> GRAY_BALLOON = registerWithItem("gray_balloon", "灰色气球", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.COLOR_GRAY).strength(0.1F)));
    public static final DeferredBlock<Block> BLACK_BALLOON = registerWithItem("black_balloon", "黑色气球", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.COLOR_BLACK).strength(0.1F)));
    public static final DeferredBlock<Block> BROWN_BALLOON = registerWithItem("brown_balloon", "棕色气球", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.COLOR_BROWN).strength(0.1F)));
    public static final DeferredBlock<Block> RED_BALLOON = registerWithItem("red_balloon", "红色气球", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.COLOR_RED).strength(0.1F)));
    public static final DeferredBlock<Block> ORANGE_BALLOON = registerWithItem("orange_balloon", "橙色气球", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.COLOR_ORANGE).strength(0.1F)));
    public static final DeferredBlock<Block> YELLOW_BALLOON = registerWithItem("yellow_balloon", "黄色气球", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.COLOR_YELLOW).strength(0.1F)));
    public static final DeferredBlock<Block> LIME_BALLOON = registerWithItem("lime_balloon", "黄绿色气球", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.COLOR_LIGHT_GREEN).strength(0.1F)));
    public static final DeferredBlock<Block> GREEN_BALLOON = registerWithItem("green_balloon", "绿色气球", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.COLOR_GREEN).strength(0.1F)));
    public static final DeferredBlock<Block> CYAN_BALLOON = registerWithItem("cyan_balloon", "青色气球", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.COLOR_CYAN).strength(0.1F)));
    public static final DeferredBlock<Block> LIGHT_BLUE_BALLOON = registerWithItem("light_blue_balloon", "淡蓝色气球", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.COLOR_BLUE).strength(0.1F)));
    public static final DeferredBlock<Block> BLUE_BALLOON = registerWithItem("blue_balloon", "蓝色气球", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.COLOR_BLUE).strength(0.1F)));
    public static final DeferredBlock<Block> PURPLE_BALLOON = registerWithItem("purple_balloon", "紫色气球", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.COLOR_PURPLE).strength(0.1F)));
    public static final DeferredBlock<Block> MAGENTA_BALLOON = registerWithItem("magenta_balloon", "品红气球", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.COLOR_MAGENTA).strength(0.1F)));
    public static final DeferredBlock<Block> PINK_BALLOON = registerWithItem("pink_balloon", "粉红气球", () -> new Block(BlockBehaviour.Properties.ofFullCopy(WHITE_WOOL).mapColor(MapColor.COLOR_PINK).strength(0.1F)));
    // 圣物
    public static final DeferredBlock<RelicBlock> KING_SLIME_RELIC = registerRelic("king_slime_relic", "史莱姆王圣物");
    public static final DeferredBlock<RelicBlock> EYE_OF_CTHULHU_RELIC = registerRelicWithEnglishName("eye_of_cthulhu_relic", "克苏鲁之眼圣物", "Eye of Cthulhu Relic");
    public static final DeferredBlock<RelicBlock> BRAIN_OF_CTHULHU_RELIC = registerRelicWithEnglishName("brain_of_cthulhu_relic", "克苏鲁之脑圣物", "Brain of Cthulhu Relic");
    public static final DeferredBlock<RelicBlock> EATER_OF_WORLDS_RELIC = registerRelicWithEnglishName("eater_of_worlds_relic", "世界吞噬怪圣物", "Eater of Worlds Relic");
    public static final DeferredBlock<RelicBlock> QUEEN_BEE_RELIC = registerRelic("queen_bee_relic", "蜂王圣物");
    public static final DeferredBlock<RelicBlock> DEERCLOPS_RELIC = registerRelic("deerclops_relic", "独眼巨鹿圣物");
    public static final DeferredBlock<RelicBlock> SKELETRON_RELIC = registerRelic("skeletron_relic", "骷髅王圣物");
    public static final DeferredBlock<RelicBlock> WALL_OF_FLESH_RELIC = registerRelicWithEnglishName("wall_of_flesh_relic", "血肉墙圣物", "Wall of Flesh Relic");
    public static final DeferredBlock<RelicBlock> HILL_OF_FLESH_RELIC = registerRelic("hill_of_flesh_relic", "血肉山圣物");
    public static final DeferredBlock<RelicBlock> THE_TWINS_RELIC = registerRelic("the_twins_relic", "双子魔眼圣物");
    public static final DeferredBlock<RelicBlock> SKELETRON_PRIME_RELIC = registerRelic("skeletron_prime_relic", "机械骷髅王圣物");

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
    public static final DeferredBlock<LihzahrdDoorBlock> LIHZAHRD_DOOR = registerWithItem("lihzahrd_door", "丛林蜥蜴门", () -> new LihzahrdDoorBlock(LIHZAHRD, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).requiresCorrectToolForDrops().noOcclusion().strength(100.0F, ModBlocks.getObsidianBasedExplosionResistance(1000.0F)).pushReaction(PushReaction.BLOCK)));
    public static final DecoBlockSet LIHZAHRD_BRICKS = DecoBlockSet.builder("lihzahrd_bricks", () -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).requiresCorrectToolForDrops().strength(100.0F, ModBlocks.getObsidianBasedExplosionResistance(1000.0F))).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "丛林蜥蜴砖");
        language.add(set.SLAB.get(), "丛林蜥蜴砖台阶");
        language.add(set.STAIRS.get(), "丛林蜥蜴砖楼梯");
        language.add(set.WALL.get(), "丛林蜥蜴砖墙");
    });
    public static final DecoBlockSet EXPOSED_LIHZAHRD_BRICKS = DecoBlockSet.builder("exposed_lihzahrd_bricks", () -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).requiresCorrectToolForDrops().strength(100.0F, ModBlocks.getObsidianBasedExplosionResistance(1000.0F))).stonecutting().build((set, language) -> {
        language.add(set.FULL.get(), "斑驳丛林蜥蜴砖");
        language.add(set.SLAB.get(), "斑驳丛林蜥蜴砖台阶");
        language.add(set.STAIRS.get(), "斑驳丛林蜥蜴砖楼梯");
        language.add(set.WALL.get(), "斑驳丛林蜥蜴砖墙");
    });
    public static final DeferredBlock<Block> LIHZAHRD_COLUMN = registerWithItem("lihzahrd_column", "丛林蜥蜴柱", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).requiresCorrectToolForDrops().strength(100.0F, ModBlocks.getObsidianBasedExplosionResistance(1000.0F))));
    public static final DeferredBlock<Block> POLISHED_LIHZAHRD = registerWithItem("polished_lihzahrd", "磨制丛林蜥蜴石", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).requiresCorrectToolForDrops().strength(100.0F, ModBlocks.getObsidianBasedExplosionResistance(1000.0F))));
    public static final DeferredBlock<Block> LIHZAHRD_TILES = registerWithItem("lihzahrd_tiles", "丛林蜥蜴瓦", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).requiresCorrectToolForDrops().strength(100.0F, ModBlocks.getObsidianBasedExplosionResistance(1000.0F))));
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

    private static DeferredBlock<Block> copyBlockRegister(String registryId, String chineseName, Block originalBlock) {
        DeferredBlock<Block> entry = copyBlockRegister(registryId, originalBlock);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <B extends Block> DeferredBlock<B> copyBlockRegister(String registryId, String chineseName, Block originalBlock, Function<BlockBehaviour.Properties, B> function) {
        DeferredBlock<B> entry = copyBlockRegister(registryId, originalBlock, function);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <B extends Block> DeferredBlock<B> registerWithItem(String registryId, String chineseName, Supplier<B> supplier) {
        DeferredBlock<B> entry = registerWithItem(registryId, supplier);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <B extends Block> DeferredBlock<B> registerWithItem(String registryId, String chineseName, Supplier<B> block, Function<B, BlockItem> function) {
        DeferredBlock<B> entry = registerWithItem(registryId, block, function);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredBlock<RelicBlock> registerRelic(String registryId, String chineseName) {
        DeferredBlock<RelicBlock> entry = registerRelic(registryId);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredBlock<RelicBlock> registerRelicWithEnglishName(String registryId, String chineseName, String englishName) {
        DeferredBlock<RelicBlock> entry = registerRelic(registryId);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }
}
