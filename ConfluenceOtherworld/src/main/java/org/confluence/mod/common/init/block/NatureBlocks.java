package org.confluence.mod.common.init.block;

import com.mojang.datafixers.DSL;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PlaceOnWaterBlockItem;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.block.TransparentLeavesBlock;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.common.PooBlock;
import org.confluence.mod.common.block.natural.*;
import org.confluence.mod.common.block.natural.MushroomBlock;
import org.confluence.mod.common.block.natural.herbs.*;
import org.confluence.mod.common.block.natural.sapling.BaseSaplingBlock;
import org.confluence.mod.common.block.natural.sapling.PineSaplingBlock;
import org.confluence.mod.common.block.natural.sapling.StoneSaplingBlock;
import org.confluence.mod.common.block.natural.spreadable.*;
import org.confluence.mod.common.block.natural.spreadable.extended.*;
import org.confluence.mod.common.init.ModFeatures;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.FoodItems;
import org.confluence.mod.common.init.item.ModItems;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraft.world.level.block.Blocks.*;
import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;
import static org.confluence.mod.common.block.natural.LogBlockSet.WoodSetType.*;
import static org.confluence.mod.common.init.block.DecorativeBlocks.LOST_PAPER_BLOCK;

@ParametersAreNonnullByDefault
public class NatureBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Confluence.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Confluence.MODID);

    // 非环境树苗
    public static final DeferredBlock<StoneSaplingBlock> RUBY_SAPLING = registerWithItem("ruby_sapling", "红玉树苗", () -> new StoneSaplingBlock(ModFeatures.TreeGrowers.RUBY_GROWER));
    public static final DeferredBlock<StoneSaplingBlock> AMBER_SAPLING = registerWithItem("amber_sapling", "琥珀树苗", () -> new StoneSaplingBlock(ModFeatures.TreeGrowers.AMBER_GROWER));
    public static final DeferredBlock<StoneSaplingBlock> TOPAZ_SAPLING = registerWithItem("topaz_sapling", "黄玉树苗", () -> new StoneSaplingBlock(ModFeatures.TreeGrowers.TOPAZ_GROWER));
    public static final DeferredBlock<StoneSaplingBlock> JADE_SAPLING = registerWithItem("jade_sapling", "翡翠树苗", () -> new StoneSaplingBlock(ModFeatures.TreeGrowers.JADE_GROWER));
    public static final DeferredBlock<StoneSaplingBlock> DIAMOND_SAPLING = registerWithItem("diamond_sapling", "钻石树苗", () -> new StoneSaplingBlock(ModFeatures.TreeGrowers.DIAMOND_GROWER));
    public static final DeferredBlock<StoneSaplingBlock> SAPPHIRE_SAPLING = registerWithItem("sapphire_sapling", "蓝玉树苗", () -> new StoneSaplingBlock(ModFeatures.TreeGrowers.SAPPHIRE_GROWER));
    public static final DeferredBlock<StoneSaplingBlock> AMETHYST_SAPLING = registerWithItem("amethyst_sapling", "紫晶树苗", () -> new StoneSaplingBlock(ModFeatures.TreeGrowers.AMETHYST_GROWER));

    // 流体接触块
    public static final DeferredBlock<ThinHoneyBlock> THIN_HONEY_BLOCK = registerWithItem("thin_honey_block", "稀薄蜂蜜块", () -> new ThinHoneyBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.HONEY_BLOCK).isSuffocating((blockState, blockGetter, blockPos) -> false).mapColor(MapColor.COLOR_ORANGE).isViewBlocking((state, level, pos) -> false)));
    public static final DeferredBlock<Block> LOOSE_HONEY_BLOCK = registerWithItem("loose_honey_block", "松散蜂蜜块", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.HONEY_BLOCK).mapColor(MapColor.COLOR_ORANGE)));
    public static final DeferredBlock<AetheriumBlock> AETHERIUM_BLOCK = registerWithItem("aetherium_block", "以太块", () -> new AetheriumBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(MapColor.COLOR_PINK).lightLevel(blockState -> 10)));
    public static final DeferredBlock<Block> DARK_AETHERIUM_BLOCK = registerWithItem("dark_aetherium_block", "黯淡以太块", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(MapColor.COLOR_GRAY)));

    // 香蒲
    public static final DeferredBlock<CattailBlock> CATTAIL_BLOCK = registerWithoutItem("cattail_block", "香蒲", () -> new CattailBlock(BlockBehaviour.Properties.of().sound(SoundType.GRASS).mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<CattailBlock> JUNGLE_CATTAIL_BLOCK = registerWithoutItem("jungle_cattail_block", "丛林香蒲", () -> new CattailBlock(BlockBehaviour.Properties.of().sound(SoundType.GRASS).mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<CattailBlock> GLOWING_MUSHROOM_CATTAIL_BLOCK = registerWithoutItem("glowing_mushroom_cattail_block", "发光蘑菇香蒲", () -> new CattailBlock(BlockBehaviour.Properties.of().sound(SoundType.GRASS).mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<CattailBlock> HALLOW_CATTAIL_BLOCK = registerWithoutItem("hallow_cattail_block", "神圣香蒲", () -> new CattailBlock(BlockBehaviour.Properties.of().sound(SoundType.GRASS).mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<CattailBlock> EBONY_CATTAIL_BLOCK = registerWithoutItem("ebony_cattail_block", "腐化香蒲", () -> new CattailBlock(BlockBehaviour.Properties.of().sound(SoundType.GRASS).mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<CattailBlock> CRIMSON_CATTAIL_BLOCK = registerWithoutItem("crimson_cattail_block", "猩红香蒲", () -> new CattailBlock(BlockBehaviour.Properties.of().sound(SoundType.GRASS).mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY)));

    // 环境辅助
    public static final DeferredBlock<ThinIceBlock> THIN_ICE_BLOCK = registerWithItem("thin_ice_block", "薄冰块", ThinIceBlock::new);
    public static final DeferredBlock<TaperedTwoPartBlock> ICE_TAPERED_BLOCK = registerWithItem("ice_tapered_block", "冰锥", TaperedTwoPartBlock::new);
    public static final DeferredBlock<TaperedTwoPartBlock> RED_ICE_TAPERED_BLOCK = registerWithItem("red_ice_tapered_block", "红色冰锥", TaperedTwoPartBlock::new);
    public static final DeferredBlock<TaperedTwoPartBlock> PURPLE_ICE_TAPERED_BLOCK = registerWithItem("purple_ice_tapered_block", "紫色冰锥", TaperedTwoPartBlock::new);
    public static final DeferredBlock<TaperedTwoPartBlock> PINK_ICE_TAPERED_BLOCK = registerWithItem("pink_ice_tapered_block", "粉色冰锥", TaperedTwoPartBlock::new);
    public static final DeferredBlock<Block> HARDENED_SAND_BLOCK = registerWithItem("hardened_sand_block", "硬化沙块", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).mapColor(MapColor.SAND)));
    public static final DeferredBlock<MoistSandBlock> MOISTENED_SAND_BLOCK = registerWithItem("moistened_sand_block", "湿润沙块", () -> new MoistSandBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD).mapColor(MapColor.SAND), Blocks.SAND));
    public static final DeferredBlock<Block> HARDENED_RED_SAND_BLOCK = registerWithItem("hardened_red_sand_block", "硬化红沙块", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).mapColor(MapColor.TERRACOTTA_RED)));
    public static final DeferredBlock<MoistSandBlock> MOISTENED_RED_SAND_BLOCK = registerWithItem("moistened_red_sand_block", "湿润红沙块", () -> new MoistSandBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD).mapColor(MapColor.TERRACOTTA_RED), Blocks.RED_SAND));
    public static final DeferredBlock<Block> DIATOMACEOUS = registerWithItem("diatomaceous", "硅藻土", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.TERRACOTTA_PINK)));
    public static final DeferredBlock<SandLayerBlock> SAND_LAYER_BLOCK = registerWithItem("sand_layer_block", "沙片", SandLayerBlock::new);
    public static final DeferredBlock<SandLayerBlock> RED_SAND_LAYER_BLOCK = registerWithItem("red_sand_layer_block", "红沙片", SandLayerBlock::new);
    public static final DeferredBlock<Block> DESERT_FOSSIL = registerWithItem("desert_fossil", "沙漠化石", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).mapColor(MapColor.TERRACOTTA_RED)));
    public static final DeferredBlock<ColoredFallingBlock> SLUSH = registerWithItem("slush", "雪泥块", () -> new ColoredFallingBlock(new ColorRGBA(-4532781), BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<ColoredFallingBlock> SILT_BLOCK = registerWithItem("silt_block", "泥沙块", () -> new ColoredFallingBlock(new ColorRGBA(-9673114), BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.TERRACOTTA_GRAY)));
    public static final DeferredBlock<ColoredFallingBlock> MARINE_GRAVEL = registerWithItem("marine_gravel", "海洋砂砾", () -> new ColoredFallingBlock(new ColorRGBA(-9588022), BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.TERRACOTTA_CYAN)));
    public static final DeferredBlock<RotatedPillarBlock> STONY_LOG = registerWithItem("stony_log", "石质原木", () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.COLOR_LIGHT_GRAY)));
    public static final DeferredBlock<LifeCrystalBlock> LIFE_CRYSTAL_BLOCK = registerWithItem("life_crystal_block", "生命水晶", () -> new LifeCrystalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_CLUSTER).mapColor(MapColor.COLOR_RED).lightLevel(state -> 7)), LifeCrystalBlock.BItem::new);
    public static final Supplier<BlockEntityType<LifeCrystalBlock.BEntity>> LIFE_CRYSTAL_BLOCK_ENTITY = ModBlocks.BLOCK_ENTITIES.register("life_crystal_block_entity", () -> BlockEntityType.Builder.of(LifeCrystalBlock.BEntity::new, LIFE_CRYSTAL_BLOCK.get()).build(DSL.remainderType()));

    // 小堆环境
    public static final DeferredBlock<SmallPilesBlock> SMALL_STONE_PILES = registerWithItem("small_stone_piles", "小型石块堆", () -> new SmallPilesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.STONE)));
    public static final DeferredBlock<SmallPilesBlock> SMALL_SANDSTONE_PILES = registerWithItem("small_sandstone_piles", "小型砂岩堆", () -> new SmallPilesBlock(BlockBehaviour.Properties.ofFullCopy(SANDSTONE).mapColor(MapColor.SAND)));
    public static final DeferredBlock<SmallPilesBlock> SMALL_RED_SANDSTONE_PILES = registerWithItem("small_red_sandstone_piles", "小型红砂岩堆", () -> new SmallPilesBlock(BlockBehaviour.Properties.ofFullCopy(SANDSTONE).mapColor(MapColor.TERRACOTTA_RED)));
    public static final DeferredBlock<SmallPilesBlock> SMALL_EBONSANDSTONE_PILES = registerWithItem("small_ebonsandstone_piles", "小型黑檀砂岩堆", () -> new SmallPilesBlock(BlockBehaviour.Properties.ofFullCopy(SANDSTONE).mapColor(MapColor.TERRACOTTA_PURPLE)));
    public static final DeferredBlock<SmallPilesBlock> SMALL_PEARLSANDSTONE_PILES = registerWithItem("small_pearlsandstone_piles", "小型珍珠砂岩堆", () -> new SmallPilesBlock(BlockBehaviour.Properties.ofFullCopy(SANDSTONE).mapColor(MapColor.TERRACOTTA_PINK)));
    public static final DeferredBlock<SmallPilesBlock> SMALL_CRIMSANDSTONE_PILES = registerWithItem("small_crimsandstone_piles", "小型猩红砂岩堆", () -> new SmallPilesBlock(BlockBehaviour.Properties.ofFullCopy(SANDSTONE).mapColor(MapColor.TERRACOTTA_RED)));
    public static final DeferredBlock<SmallPilesBlock> SMALL_GRANITE_PILES = registerWithItem("small_granite_piles", "小型花岗岩堆", () -> new SmallPilesBlock(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_BLUE)));
    public static final DeferredBlock<SmallPilesBlock> SMALL_MARBLE_PILES = registerWithItem("small_marble_piles", "小型大理石堆", () -> new SmallPilesBlock(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<SmallPilesBlock> SMALL_REMAINS_PILES = registerWithItem("small_remains_piles", "小型遗骸堆", () -> new SmallPilesBlock(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.TERRACOTTA_BLUE)));
    public static final DeferredBlock<SmallPilesBlock> ANTLION_EGGS = registerWithItem("antlion_eggs", "蚁狮卵", () -> new SmallPilesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.STONE).strength(0.05F)));

    public static final DeferredBlock<Block> GRANITE = registerWithItem("granite", "花岗岩", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static final DeferredBlock<TaperedTwoPartBlock> GRANITE_TAPERED_BLOCK = registerWithItem("granite_tapered_block", "花岗岩石笋", TaperedTwoPartBlock::new);
    public static final DeferredBlock<Block> MARBLE = registerWithItem("marble", "大理石", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static final DeferredBlock<TaperedTwoPartBlock> MARBLE_TAPERED_BLOCK = registerWithItem("marble_tapered_block", "大理石笋", TaperedTwoPartBlock::new);
    public static final DeferredBlock<MushroomBlock> LIFE_MUSHROOM = registerWithoutItem("life_mushroom", "生命蘑菇", () -> new MushroomBlock(ISpreadable.Type.PURE, Blocks.GRASS_BLOCK)); // 生命蘑菇

    // 腐化
    public static final DeferredBlock<SpreadingGrassBlock> CORRUPT_GRASS_BLOCK = registerWithItem("corrupt_grass_block", "腐化草方块", () -> new SpreadingGrassBlock(ISpreadable.Type.CORRUPT, BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK).mapColor(MapColor.COLOR_PURPLE)));
    public static final LogBlockSet EBONY_LOG_BLOCKS = LogBlockSet.builder("ebony", true, EBONY).sapling(properties -> new BaseSaplingBlock(ModFeatures.TreeGrowers.EBONY_GROWER, properties, null, CORRUPT_GRASS_BLOCK)).build((set, language) -> {
        language.add(set.CHISELED_PLANKS.get(), "雕纹乌木木板");
        language.add(set.BUTTON.get(), "乌木按钮");
        language.add(set.DOOR.get(), "乌木门");
        language.add(set.FENCE.get(), "乌木栅栏");
        language.add(set.FENCE_GATE.get(), "乌木栅栏门");
        language.add(set.HANGING_SIGN.get(), "悬挂式乌木告示牌");
        language.add(set.LEAVES.get(), "乌树树叶");
        language.add(set.LOG.get(), "乌木原木");
        language.add(set.PLANKS.get(), "乌木板");
        language.add(set.PRESSURE_PLATE.get(), "乌木压力板");
        language.add(set.SAPLING.get(), "乌木树苗");
        language.add(set.SIGN.get(), "乌木告示牌");
        language.add(set.SLAB.get(), "乌木台阶");
        language.add(set.STAIRS.get(), "乌木楼梯");
        language.add(set.TRAPDOOR.get(), "乌木活板门");
        language.add(set.WOOD.get(), "乌木");
        language.add(set.STRIPPED_LOG.get(), "去皮乌木原木");
        language.add(set.STRIPPED_WOOD.get(), "去皮乌木");
    });
    public static final DeferredBlock<SpreadingBlock> EBONSTONE = registerWithItem("ebonstone", "黑檀石块", () -> new SpreadingBlock(ISpreadable.Type.CORRUPT, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.TERRACOTTA_PURPLE)));
    public static final DeferredBlock<Block> COBBLED_EBONSTONE = registerWithItem("cobbled_ebonstone", "黑檀圆石", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).mapColor(MapColor.TERRACOTTA_PURPLE)));
    public static final DeferredBlock<SpreadingBlock> EBONSANDSTONE = registerWithItem("ebonsandstone", "黑檀砂岩", () -> new SpreadingBlock(ISpreadable.Type.CORRUPT, BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).mapColor(MapColor.TERRACOTTA_PURPLE)));
    public static final DeferredBlock<SpreadingBlock> HARDENED_EBONSAND_BLOCK = registerWithItem("hardened_ebonsand_block", "硬化黑檀沙块", () -> new SpreadingBlock(ISpreadable.Type.CORRUPT, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.TERRACOTTA_PURPLE)));
    public static final DeferredBlock<SpreadingSandBlock> EBONSAND = registerWithItem("ebonsand", "黑檀沙块", () -> new SpreadingSandBlock(ISpreadable.Type.CORRUPT, 0x372B4B, BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.COLOR_BLACK)));
    public static final DeferredBlock<SpreadableMoistenedSandBlock> MOISTENED_EBONSAND_BLOCK = registerWithItem("moistened_ebonsand_block", "湿润黑檀沙块", () -> new SpreadableMoistenedSandBlock(ISpreadable.Type.CORRUPT, BlockBehaviour.Properties.ofFullCopy(Blocks.MUD).mapColor(MapColor.COLOR_BLACK), EBONSAND));
    public static final DeferredBlock<SpreadingIceBlock> PURPLE_ICE = registerWithItem("purple_ice", "紫冰块", () -> new SpreadingIceBlock(ISpreadable.Type.CORRUPT, BlockBehaviour.Properties.ofFullCopy(Blocks.ICE).mapColor(MapColor.COLOR_PURPLE)));
    public static final DeferredBlock<SpreadingIceBlock> PURPLE_PACKED_ICE = registerWithItem("purple_packed_ice", "紫浮冰块", () -> new SpreadingIceBlock(ISpreadable.Type.CORRUPT, BlockBehaviour.Properties.ofFullCopy(Blocks.PACKED_ICE).mapColor(MapColor.COLOR_PURPLE)));
    public static final DeferredBlock<SandLayerBlock> EBONSAND_LAYER_BLOCK = registerWithItem("ebonsand_layer_block", "腐化沙片", SandLayerBlock::new);
    public static final DeferredBlock<MushroomBlock> VILE_MUSHROOM = registerWithoutItem("vile_mushroom", "魔菇", () -> new MushroomBlock(ISpreadable.Type.CORRUPT, CORRUPT_GRASS_BLOCK.get())); // 魔菇
    public static final DeferredBlock<SpreadingThornBlock> CORRUPTION_THORN = registerWithItem("corruption_thorn", "腐化荆棘", () -> new SpreadingThornBlock(2, CORRUPT_GRASS_BLOCK.get(), ISpreadable.Type.CORRUPT));
    public static final DeferredBlock<BasePlantBlock> CORRUPT_GRASS = registerWithItem("corrupt_grass", "腐化草", () -> new BasePlantBlock(CORRUPT_GRASS_BLOCK.get())); // 腐化草
    public static final DeferredBlock<JungleGrassBlock> CORRUPT_JUNGLE_GRASS_BLOCK = registerWithItem("corrupt_jungle_grass_block", "腐化丛林草方块", () -> new JungleGrassBlock(ISpreadable.Type.CORRUPT, BlockBehaviour.Properties.ofFullCopy(Blocks.MUD).mapColor(MapColor.COLOR_PURPLE))); // 腐化丛林草
    public static final DeferredBlock<ShadowOrbBlock> SHADOW_ORB = registerWithoutItem("shadow_orb", "暗影珠", ShadowOrbBlock::new);
    public static final DeferredBlock<EvilCactusBlock> CORRUPT_CACTUS = registerWithItem("corrupt_cactus", "腐化仙人掌", () -> new EvilCactusBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CACTUS).mapColor(MapColor.COLOR_CYAN)));
    public static final DeferredBlock<TaperedTwoPartBlock> CORRUPT_TAPERED_BLOCK = registerWithItem("corrupt_tapered_block", "腐化石笋", TaperedTwoPartBlock::new);

    // 神圣
    public static final DeferredBlock<SpreadingGrassBlock> HALLOW_GRASS_BLOCK = registerWithItem("hallow_grass_block", "神圣草方块", () -> new SpreadingGrassBlock(ISpreadable.Type.HALLOW, BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK).mapColor(MapColor.COLOR_CYAN)));
    public static final DeferredBlock<BasePlantBlock> HALLOW_GRASS = registerWithItem("hallow_grass", "神圣草", () -> new BasePlantBlock(HALLOW_GRASS_BLOCK.get())); // 神圣草
    public static final LogBlockSet PEARL_LOG_BLOCKS = LogBlockSet.builder("pearl", true, PEARL).sapling(properties -> new BaseSaplingBlock(ModFeatures.TreeGrowers.PEARL_GROWER, properties, null, HALLOW_GRASS_BLOCK)).build((set, language) -> {
        language.add(set.CHISELED_PLANKS.get(), "雕纹珍珠木板");
        language.add(set.BUTTON.get(), "珍珠木按钮");
        language.add(set.DOOR.get(), "珍珠木门");
        language.add(set.FENCE.get(), "珍珠木栅栏");
        language.add(set.FENCE_GATE.get(), "珍珠木栅栏门");
        language.add(set.HANGING_SIGN.get(), "悬挂式珍珠木告示牌");
        language.add(set.LEAVES.get(), "珍珠树叶");
        language.add(set.LOG.get(), "珍珠原木");
        language.add(set.PLANKS.get(), "珍珠木板");
        language.add(set.PRESSURE_PLATE.get(), "珍珠木压力板");
        language.add(set.SAPLING.get(), "珍珠树苗");
        language.add(set.SIGN.get(), "珍珠木告示牌");
        language.add(set.SLAB.get(), "珍珠木台阶");
        language.add(set.STAIRS.get(), "珍珠木楼梯");
        language.add(set.TRAPDOOR.get(), "珍珠木活板门");
        language.add(set.WOOD.get(), "Pearlwood", "珍珠木");
        language.add(set.STRIPPED_LOG.get(), "去皮珍珠原木");
        language.add(set.STRIPPED_WOOD.get(), "去皮珍珠木");
    });
    public static final DeferredBlock<PearlstoneBlock> PEARLSTONE = registerWithItem("pearlstone", "珍珠石块", () -> new PearlstoneBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.TERRACOTTA_MAGENTA).strength(8.0F)));
    public static final DeferredBlock<Block> COBBLED_PEARLSTONE = registerWithItem("cobbled_pearlstone", "珍珠圆石", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).mapColor(MapColor.TERRACOTTA_MAGENTA)));
    public static final DeferredBlock<PearlstoneBlock> HARDENED_PEARLSAND_BLOCK = registerWithItem("hardened_pearlsand_block", "硬化珍珠沙块", () -> new PearlstoneBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.TERRACOTTA_PINK)));
    public static final DeferredBlock<PearlstoneBlock> PEARLSANDSTONE = registerWithItem("pearlsandstone", "珍珠砂岩", () -> new PearlstoneBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).mapColor(MapColor.TERRACOTTA_PINK).strength(8.0F)));
    public static final DeferredBlock<PearlsandBlock> PEARLSAND = registerWithItem("pearlsand", "珍珠沙块", () -> new PearlsandBlock(0xEDD5F6, BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.TERRACOTTA_PINK)));
    public static final DeferredBlock<MoistenedPearlsandBlock> MOISTENED_PEARLSAND_BLOCK = registerWithItem("moistened_pearlsand_block", "湿润珍珠沙块", () -> new MoistenedPearlsandBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD).mapColor(MapColor.TERRACOTTA_PINK)));
    public static final DeferredBlock<SandLayerBlock> PEARLSAND_LAYER_BLOCK = registerWithItem("pearlsand_layer_block", "神圣沙片", SandLayerBlock::new);
    public static final DeferredBlock<SpreadingIceBlock> PINK_ICE = registerWithItem("pink_ice", "粉冰块", () -> new SpreadingIceBlock(ISpreadable.Type.HALLOW, BlockBehaviour.Properties.ofFullCopy(Blocks.ICE).mapColor(MapColor.COLOR_PINK)));
    public static final DeferredBlock<PinkPackedIceBlock> PINK_PACKED_ICE = registerWithItem("pink_packed_ice", "粉浮冰块", () -> new PinkPackedIceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PACKED_ICE).mapColor(MapColor.COLOR_PINK).randomTicks()));
    public static final DeferredBlock<AmethystClusterBlock> CRYSTAL_SHARDS = registerWithoutItem("crystal_shards", "水晶碎块", () -> new AmethystClusterBlock(7.0F, 3.0F, BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_CLUSTER).mapColor(MapColor.COLOR_PINK).lightLevel(state -> 7)));
    public static final DeferredBlock<AmethystClusterBlock> GELATIN_CRYSTAL = registerWithoutItem("gelatin_crystal", "明胶水晶", () -> new AmethystClusterBlock(7.0F, 3.0F, BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_CLUSTER).mapColor(MapColor.COLOR_PINK).lightLevel(state -> 9)));
    public static final DeferredBlock<EvilCactusBlock> HALLOW_CACTUS = registerWithItem("hallow_cactus", "神圣仙人掌", () -> new EvilCactusBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CACTUS).mapColor(MapColor.COLOR_CYAN)));
    public static final DeferredBlock<TaperedTwoPartBlock> HALLOW_TAPERED_BLOCK = registerWithItem("hallow_tapered_block", "神圣石笋", TaperedTwoPartBlock::new);

    // 猩红
    public static final DeferredBlock<SpreadingGrassBlock> CRIMSON_GRASS_BLOCK = registerWithItem("crimson_grass_block", "猩红草方块", () -> new SpreadingGrassBlock(ISpreadable.Type.CRIMSON, BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK).mapColor(MapColor.COLOR_RED)));
    public static final LogBlockSet SHADOW_LOG_BLOCKS = LogBlockSet.builder("shadow", true, SHADOW).sapling(properties -> new BaseSaplingBlock(ModFeatures.TreeGrowers.SHADOW_GROWER, properties, null, CRIMSON_GRASS_BLOCK)).build((set, language) -> {
        language.add(set.CHISELED_PLANKS.get(), "雕纹暗影木板");
        language.add(set.BUTTON.get(), "暗影木按钮");
        language.add(set.DOOR.get(), "暗影木门");
        language.add(set.FENCE.get(), "暗影木栅栏");
        language.add(set.FENCE_GATE.get(), "暗影木栅栏门");
        language.add(set.HANGING_SIGN.get(), "悬挂式暗影木告示牌");
        language.add(set.LEAVES.get(), "暗影树叶");
        language.add(set.LOG.get(), "暗影原木");
        language.add(set.PLANKS.get(), "暗影木板");
        language.add(set.PRESSURE_PLATE.get(), "暗影木压力板");
        language.add(set.SAPLING.get(), "暗影树苗");
        language.add(set.SIGN.get(), "暗影木告示牌");
        language.add(set.SLAB.get(), "暗影木台阶");
        language.add(set.STAIRS.get(), "暗影木楼梯");
        language.add(set.TRAPDOOR.get(), "暗影木活板门");
        language.add(set.WOOD.get(), "暗影木");
        language.add(set.STRIPPED_LOG.get(), "去皮暗影原木");
        language.add(set.STRIPPED_WOOD.get(), "去皮暗影木");
    });
    public static final DeferredBlock<SpreadingBlock> CRIMSTONE = registerWithItem("crimstone", "猩红石块", () -> new SpreadingBlock(ISpreadable.Type.CRIMSON, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.COLOR_RED)));
    public static final DeferredBlock<Block> COBBLED_CRIMSTONE = registerWithItem("cobbled_crimstone", "猩红圆石", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).mapColor(MapColor.COLOR_RED)));
    public static final DeferredBlock<SpreadingBlock> HARDENED_CRIMSAND_BLOCK = registerWithItem("hardened_crimsand_block", "硬化猩红沙块", () -> new SpreadingBlock(ISpreadable.Type.CRIMSON, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.TERRACOTTA_RED)));
    public static final DeferredBlock<SpreadingBlock> CRIMSANDSTONE = registerWithItem("crimsandstone", "猩红砂岩", () -> new SpreadingBlock(ISpreadable.Type.CRIMSON, BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).mapColor(MapColor.TERRACOTTA_RED)));
    public static final DeferredBlock<SpreadingSandBlock> CRIMSAND = registerWithItem("crimsand", "猩红沙块", () -> new SpreadingSandBlock(ISpreadable.Type.CRIMSON, 0x5313E0, BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.COLOR_RED)));
    public static final DeferredBlock<SpreadableMoistenedSandBlock> MOISTENED_CRIMSAND_BLOCK = registerWithItem("moistened_crimsand_block", "湿润猩红沙块", () -> new SpreadableMoistenedSandBlock(ISpreadable.Type.CRIMSON, BlockBehaviour.Properties.ofFullCopy(Blocks.MUD).mapColor(MapColor.COLOR_RED), CRIMSAND));
    public static final DeferredBlock<MushroomBlock> VICIOUS_MUSHROOM = registerWithoutItem("vicious_mushroom", "毒蘑菇", () -> new MushroomBlock(ISpreadable.Type.CRIMSON, CRIMSON_GRASS_BLOCK.get())); // 毒蘑菇
    public static final DeferredBlock<SpreadingThornBlock> CRIMSON_THORN = registerWithItem("crimson_thorn", "猩红荆棘", () -> new SpreadingThornBlock(2, CRIMSON_GRASS_BLOCK.get(), ISpreadable.Type.CRIMSON));
    public static final DeferredBlock<BasePlantBlock> CRIMSON_GRASS = registerWithItem("crimson_grass", "猩红草", () -> new BasePlantBlock(CRIMSON_GRASS_BLOCK.get())); // 猩红草
    public static final DeferredBlock<SandLayerBlock> CRIMSAND_LAYER_BLOCK = registerWithItem("crimsand_layer_block", "猩红沙片", SandLayerBlock::new);
    public static final DeferredBlock<JungleGrassBlock> CRIMSON_JUNGLE_GRASS_BLOCK = registerWithItem("crimson_jungle_grass_block", "猩红丛林草方块", () -> new JungleGrassBlock(ISpreadable.Type.CRIMSON, BlockBehaviour.Properties.ofFullCopy(Blocks.MUD))); // 腐化丛林草
    public static final DeferredBlock<SpreadingIceBlock> RED_ICE = registerWithItem("red_ice", "红冰块", () -> new SpreadingIceBlock(ISpreadable.Type.CRIMSON, BlockBehaviour.Properties.ofFullCopy(Blocks.ICE).mapColor(MapColor.COLOR_RED)));
    public static final DeferredBlock<SpreadingIceBlock> RED_PACKED_ICE = registerWithItem("red_packed_ice", "红浮冰块", () -> new SpreadingIceBlock(ISpreadable.Type.CRIMSON, BlockBehaviour.Properties.ofFullCopy(Blocks.PACKED_ICE).mapColor(MapColor.COLOR_RED)));
    public static final DeferredBlock<CrimsonHeartBlock> CRIMSON_HEART = registerWithoutItem("crimson_heart", "猩红之心", CrimsonHeartBlock::new);
    public static final DeferredBlock<EvilCactusBlock> CRIMSON_CACTUS = registerWithItem("crimson_cactus", "猩红仙人掌", () -> new EvilCactusBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CACTUS).mapColor(MapColor.COLOR_RED)));
    public static final DeferredBlock<TaperedTwoPartBlock> CRIMSON_TAPERED_BLOCK = registerWithItem("crimson_tapered_block", "猩红石笋", TaperedTwoPartBlock::new);
    public static final DeferredBlock<Block> PUPIL_BLOCK = registerWithItem("pupil_block", "瞳孔块", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).mapColor(MapColor.COLOR_PURPLE).sound(ModSoundEvents.Types.EYEBALL)));
    public static final DeferredBlock<Block> EYEBALL_BLOCK = registerWithItem("eyeball_block", "眼球块", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).mapColor(MapColor.COLOR_GRAY).sound(ModSoundEvents.Types.EYEBALL)));
    // 蘑菇地
    public static final DeferredBlock<MushroomGrassBlock> MUSHROOM_GRASS_BLOCK = registerWithItem("mushroom_grass_block", "蘑菇草方块", MushroomGrassBlock::new);
    public static final DeferredBlock<MushroomBlock> GLOWING_MUSHROOM = registerWithoutItem("glowing_mushroom", "发光蘑菇", () -> new MushroomBlock(ISpreadable.Type.GLOWING, MUSHROOM_GRASS_BLOCK.get())); // 发光蘑菇
    public static final DeferredBlock<IndusiumBlock> GLOWING_MUSHROOM_INDUSIUM_BLOCK = registerWithItem("glowing_mushroom_indusium_block", "发光蘑菇菌裙", IndusiumBlock::new);
    public static final DeferredBlock<RotatedPillarBlock> GLOWING_MUSHROOM_STEM_BLOCK = registerWithItem("glowing_mushroom_stem_block", "发光蘑菇柄", () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUSHROOM_STEM)));
    public static final DeferredBlock<GlowingMushroomPileusBlock> GLOWING_MUSHROOM_PILEUS_BLOCK = registerWithItem("glowing_mushroom_pileus_block", "发光蘑菇菌盖", () -> new GlowingMushroomPileusBlock(4, BlockBehaviour.Properties.ofFullCopy(Blocks.MUSHROOM_STEM)));

    public static final DeferredBlock<IndusiumBlock> LIFE_MUSHROOM_INDUSIUM_BLOCK = registerWithItem("life_mushroom_indusium_block", "生命蘑菇菌裙", IndusiumBlock::new);
    public static final DeferredBlock<RotatedPillarBlock> LIFE_MUSHROOM_STEM_BLOCK = registerWithItem("life_mushroom_stem_block", "生命蘑菇柄", () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUSHROOM_STEM)));
    public static final DeferredBlock<GlowingMushroomPileusBlock> LIFE_MUSHROOM_PILEUS_BLOCK = registerWithItem("life_mushroom_pileus_block", "生命蘑菇菌盖", () -> new GlowingMushroomPileusBlock(0, BlockBehaviour.Properties.ofFullCopy(Blocks.MUSHROOM_STEM)));

    public static final DeferredBlock<Block> HANGING_MYCELIUM = registerWithItem("hanging_mycelium", "悬挂菌丝", () -> new HangingMyceliumBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.DIRT)
            .replaceable()
            .noCollission()
            .instabreak()
            .sound(SoundType.HANGING_ROOTS)
            .offsetType(BlockBehaviour.OffsetType.XZ)
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<MycelialDirtBlock> MYCELIAL_DIRT = registerWithItem("mycelial_dirt", "菌化泥土", () -> new MycelialDirtBlock(BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).strength(0.5F).sound(SoundType.ROOTED_DIRT)));

    public static final LogBlockSet GLOWING_MUSHROOM_LOG_BLOCKS = LogBlockSet.builder("glowing_mushroom", true, LogBlockSet.WoodSetType.GLOWING_MUSHROOM).log(null).strippedLog(null).wood(null).strippedWood(null).leaves(null).build((set, language) -> {
        language.add(set.CHISELED_PLANKS.get(), "雕纹发光蘑菇木板");
        language.add(set.BUTTON.get(), "发光蘑菇按钮");
        language.add(set.DOOR.get(), "发光蘑菇门");
        language.add(set.FENCE.get(), "发光蘑菇栅栏");
        language.add(set.FENCE_GATE.get(), "发光蘑菇栅栏门");
        language.add(set.HANGING_SIGN.get(), "悬挂式发光蘑菇告示牌");
        language.add(set.PLANKS.get(), "发光蘑菇木板");
        language.add(set.PRESSURE_PLATE.get(), "发光蘑菇压力板");
        language.add(set.SIGN.get(), "发光蘑菇告示牌");
        language.add(set.SLAB.get(), "发光蘑菇台阶");
        language.add(set.STAIRS.get(), "发光蘑菇楼梯");
        language.add(set.TRAPDOOR.get(), "发光蘑菇活板门");
    });

    // 沙漠
    public static final LogBlockSet PALM_LOG_BLOCKS = LogBlockSet.builder("palm", true, PALM).leaves(PalmLeaves::new).sapling(properties -> new BaseSaplingBlock(ModFeatures.TreeGrowers.PALM_GROWER, properties, null, getSupplier(Blocks.SAND), getSupplier(Blocks.RED_SAND), getSupplier(Blocks.GRASS_BLOCK), NatureBlocks.MOISTENED_SAND_BLOCK, NatureBlocks.MOISTENED_RED_SAND_BLOCK)).build((set, language) -> {
        language.add(set.CHISELED_PLANKS.get(), "雕纹棕榈木板");
        language.add(set.BUTTON.get(), "棕榈木按钮");
        language.add(set.DOOR.get(), "棕榈木门");
        language.add(set.FENCE.get(), "棕榈木栅栏");
        language.add(set.FENCE_GATE.get(), "棕榈木栅栏门");
        language.add(set.HANGING_SIGN.get(), "悬挂式棕榈木告示牌");
        language.add(set.LEAVES.get(), "棕榈树叶");
        language.add(set.LOG.get(), "棕榈原木");
        language.add(set.PLANKS.get(), "棕榈木板");
        language.add(set.PRESSURE_PLATE.get(), "棕榈木压力板");
        language.add(set.SAPLING.get(), "棕榈树苗");
        language.add(set.SIGN.get(), "棕榈木告示牌");
        language.add(set.SLAB.get(), "棕榈木台阶");
        language.add(set.STAIRS.get(), "棕榈木楼梯");
        language.add(set.TRAPDOOR.get(), "棕榈木活板门");
        language.add(set.WOOD.get(), "棕榈木");
        language.add(set.STRIPPED_LOG.get(), "去皮棕榈原木");
        language.add(set.STRIPPED_WOOD.get(), "去皮棕榈木");
    });
    public static final DeferredBlock<TaperedTwoPartBlock> DESERT_TAPERED_BLOCK = registerWithItem("desert_tapered_block", "沙漠石笋", TaperedTwoPartBlock::new);
    public static final DeferredBlock<TaperedTwoPartBlock> RED_DESERT_TAPERED_BLOCK = registerWithItem("red_desert_tapered_block", "红沙岩锥", TaperedTwoPartBlock::new);
    public static final DeferredBlock<DesertPlantBlock> SMALL_DESERT_PLANT = registerWithItem("small_desert_plant", "小型沙漠植株", () -> new DesertPlantBlock(Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D)));
    public static final DeferredBlock<DesertPlantBlock> BIG_DESERT_PLANT = registerWithItem("big_desert_plant", "大型沙漠植株", () -> new DesertPlantBlock(Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D)));
    public static final DeferredBlock<SmallCactusBlock> SMALL_CACTUS = registerWithItem("small_cactus", "小型仙人掌", SmallCactusBlock::new);
    public static final DeferredBlock<BasePlantBlock> DESERT_GRASS = registerWithItem("desert_grass", "沙漠草", () -> new BasePlantBlock(Blocks.SAND, Blocks.RED_SAND));
    public static final DeferredBlock<BaseTallPlantBlock> DESERT_TALL_GRASS = registerWithItem("desert_tall_grass", "沙漠高草", () -> new BaseTallPlantBlock(Blocks.SAND, Blocks.RED_SAND));
    public static final DeferredBlock<Block> PACKED_DIRT = registerWithItem("packed_dirt", "板结泥土", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).strength(1.0F, 1.0F).sound(SoundType.STONE).mapColor(MapColor.COLOR_BROWN)));

    // 萨瓦纳草原
    public static final LogBlockSet BAOBAB_LOG_BLOCKS = LogBlockSet.builder("baobab", true, BAOBAB).sapling(properties -> new BaseSaplingBlock(ModFeatures.TreeGrowers.BAOBAB_GROWER, properties, null, getSupplier(Blocks.GRASS_BLOCK), getSupplier(Blocks.DIRT))).build((set, language) -> {
        language.add(set.BUTTON.get(), "猴面包木按钮");
        language.add(set.DOOR.get(), "猴面包木门");
        language.add(set.FENCE.get(), "猴面包木栅栏");
        language.add(set.FENCE_GATE.get(), "猴面包木栅栏门");
        language.add(set.HANGING_SIGN.get(), "悬挂式猴面包木告示牌");
        language.add(set.LEAVES.get(), "猴面包树叶");
        language.add(set.LOG.get(), "猴面包原木");
        language.add(set.PLANKS.get(), "猴面包木板");
        language.add(set.PRESSURE_PLATE.get(), "猴面包木压力板");
        language.add(set.SAPLING.get(), "猴面包树苗");
        language.add(set.SIGN.get(), "猴面包木告示牌");
        language.add(set.SLAB.get(), "猴面包木台阶");
        language.add(set.STAIRS.get(), "猴面包木楼梯");
        language.add(set.TRAPDOOR.get(), "猴面包木活板门");
        language.add(set.WOOD.get(), "猴面包木");
        language.add(set.CHISELED_PLANKS.get(), "雕纹猴面包木木板");
        language.add(set.STRIPPED_LOG.get(), "去皮猴面包原木");
        language.add(set.STRIPPED_WOOD.get(), "去皮猴面包木");
    });

    // 黑森林
    public static final DeferredBlock<WhitePumpkinBlock> WHITE_PUMPKIN = registerWithItem("white_pumpkin", "白南瓜", () -> new WhitePumpkinBlock(
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_WHITE)
                    .strength(1.0F)
                    .sound(SoundType.WOOD)
                    .pushReaction(PushReaction.DESTROY)
    ));
    public static final DeferredBlock<Block> WHITE_PUMPKIN_STEM = registerWithoutItem("white_pumpkin_stem", "白南瓜茎", () -> new StemBlock(
            WHITE_PUMPKIN.getKey(),
            ResourceKey.create(Registries.BLOCK, Confluence.asResource("attached_white_pumpkin_stem")),
            FoodItems.WHITE_PUMPKIN_SEED.getKey(),
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollission()
                    .randomTicks()
                    .instabreak()
                    .sound(SoundType.HARD_CROP)
                    .pushReaction(PushReaction.DESTROY)
    ));
    public static final DeferredBlock<Block> ATTACHED_WHITE_PUMPKIN_STEM = registerWithoutItem("attached_white_pumpkin_stem", "结果的白南瓜茎", () -> new AttachedStemBlock(
            WHITE_PUMPKIN_STEM.getKey(),
            WHITE_PUMPKIN.getKey(),
            FoodItems.WHITE_PUMPKIN_SEED.getKey(),
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.WOOD)
                    .pushReaction(PushReaction.DESTROY)
    ));
    // 黄柳
    public static final LogBlockSet YELLOW_WILLOW_LOG_BLOCKS = LogBlockSet.builder("yellow_willow", true, YELLOW_WILLOW).sapling(properties -> new BaseSaplingBlock(ModFeatures.TreeGrowers.YELLOW_WILLOW_GROWER, properties, null, getSupplier(Blocks.GRASS_BLOCK), getSupplier(Blocks.DIRT))).build((set, language) -> {
        language.add(set.CHISELED_PLANKS.get(), "雕纹黄柳木板");
        language.add(set.STRIPPED_LOG.get(), "去皮黄柳原木");
        language.add(set.STRIPPED_WOOD.get(), "去皮黄柳木");
        language.add(set.BUTTON.get(), "黄柳木按钮");
        language.add(set.DOOR.get(), "黄柳木门");
        language.add(set.FENCE.get(), "黄柳木栅栏");
        language.add(set.FENCE_GATE.get(), "黄柳木栅栏门");
        language.add(set.HANGING_SIGN.get(), "悬挂式黄柳木告示牌");
        language.add(set.LEAVES.get(), "黄柳树叶");
        language.add(set.LOG.get(), "黄柳原木");
        language.add(set.PLANKS.get(), "黄柳木板");
        language.add(set.PRESSURE_PLATE.get(), "黄柳木压力板");
        language.add(set.SAPLING.get(), "黄柳树苗");
        language.add(set.SIGN.get(), "黄柳木告示牌");
        language.add(set.SLAB.get(), "黄柳木台阶");
        language.add(set.STAIRS.get(), "黄柳木楼梯");
        language.add(set.TRAPDOOR.get(), "黄柳木活板门");
        language.add(set.WOOD.get(), "黄柳木");
    });
    // 万圣节
    public static final LogBlockSet SPOOKY_LOG_BLOCKS = LogBlockSet.builder("spooky", true, SPOOKY).build((set, language) -> {
        language.add(set.CHISELED_PLANKS.get(), "雕纹阴森木板");
        language.add(set.BUTTON.get(), "阴森按钮");
        language.add(set.DOOR.get(), "阴森木门");
        language.add(set.FENCE.get(), "阴森木栅栏");
        language.add(set.FENCE_GATE.get(), "阴森木栅栏门");
        language.add(set.HANGING_SIGN.get(), "阴森木悬挂告示牌");
        language.add(set.LEAVES.get(), "阴森木树叶");
        language.add(set.LOG.get(), "阴森原木");
        language.add(set.PLANKS.get(), "阴森木板");
        language.add(set.PRESSURE_PLATE.get(), "阴森木压力板");
        language.add(set.SIGN.get(), "阴森木告示牌");
        language.add(set.SLAB.get(), "阴森木台阶");
        language.add(set.STAIRS.get(), "阴森木楼梯");
        language.add(set.TRAPDOOR.get(), "阴森木活板门");
        language.add(set.WOOD.get(), "阴森木");
        language.add(set.STRIPPED_LOG.get(), "去皮阴森木原木");
        language.add(set.STRIPPED_WOOD.get(), "去皮阴森木");
    });
    // 生命树
    public static final LogBlockSet LIVING_LOG_BLOCKS = LogBlockSet.builder("living", true, LIVING).leaves(properties -> new TransparentLeavesBlock(properties.noOcclusion())).build((set, language) -> {
        language.add(set.CHISELED_PLANKS.get(), "雕纹生命木板");
        language.add(set.BUTTON.get(), "生命木按钮");
        language.add(set.DOOR.get(), "生命木门");
        language.add(set.FENCE.get(), "生命木栅栏");
        language.add(set.FENCE_GATE.get(), "生命木栅栏门");
        language.add(set.HANGING_SIGN.get(), "悬挂式生命木告示牌");
        language.add(set.LEAVES.get(), "生命树叶");
        language.add(set.LOG.get(), "生命原木");
        language.add(set.PLANKS.get(), "生命木板");
        language.add(set.PRESSURE_PLATE.get(), "生命木压力板");
        language.add(set.SIGN.get(), "生命木告示牌");
        language.add(set.SLAB.get(), "生命木台阶");
        language.add(set.STAIRS.get(), "生命木楼梯");
        language.add(set.TRAPDOOR.get(), "生命木活板门");
        language.add(set.WOOD.get(), "生命木");
        language.add(set.STRIPPED_LOG.get(), "去皮生命原木");
        language.add(set.STRIPPED_WOOD.get(), "去皮生命木");
    });
    // 生命红木
    public static final LogBlockSet LIVING_MAHOGANY_LOG_BLOCKS = LogBlockSet.builder("living_mahogany", true, LIVING_MAHOGANY).leaves(properties -> new TransparentLeavesBlock(properties.noOcclusion())).build((set, language) -> {
        language.add(set.CHISELED_PLANKS.get(), "雕纹生命红木板");
        language.add(set.BUTTON.get(), "生命红木按钮");
        language.add(set.DOOR.get(), "生命红木门");
        language.add(set.FENCE.get(), "生命红木栅栏");
        language.add(set.FENCE_GATE.get(), "生命红木栅栏门");
        language.add(set.HANGING_SIGN.get(), "悬挂式生命红木告示牌");
        language.add(set.LEAVES.get(), "生命红树叶");
        language.add(set.LOG.get(), "生命红木原木");
        language.add(set.PLANKS.get(), "生命红木板");
        language.add(set.PRESSURE_PLATE.get(), "生命红木压力板");
        language.add(set.SIGN.get(), "生命红木告示牌");
        language.add(set.SLAB.get(), "生命红木台阶");
        language.add(set.STAIRS.get(), "生命红木楼梯");
        language.add(set.TRAPDOOR.get(), "生命红木活板门");
        language.add(set.WOOD.get(), "生命红木");
        language.add(set.STRIPPED_LOG.get(), "去皮生命红木原木");
        language.add(set.STRIPPED_WOOD.get(), "去皮生命红木");
    });
    // 灰烬
    public static final DeferredBlock<Block> ASH_BLOCK = registerWithItem("ash_block", "灰烬块", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).strength(1.0F, 1.0F).sound(SoundType.SAND).mapColor(MapColor.TERRACOTTA_GRAY)));
    public static final DeferredBlock<AshGrassBlock> ASH_GRASS_BLOCK = registerWithItem("ash_grass_block", "灰烬草方块", AshGrassBlock::new);
    public static final LogBlockSet ASH_LOG_BLOCKS = LogBlockSet.builder("ash", false, ASH).leaves(null).sapling(properties -> new BaseSaplingBlock(ModFeatures.TreeGrowers.ASH_GROWER, properties, null, ASH_BLOCK, ASH_GRASS_BLOCK)).build((set, language) -> {
        language.add(set.BUTTON.get(), "灰烬木按钮");
        language.add(set.DOOR.get(), "灰烬木门");
        language.add(set.FENCE.get(), "灰烬木栅栏");
        language.add(set.FENCE_GATE.get(), "灰烬木栅栏门");
        language.add(set.HANGING_SIGN.get(), "悬挂式灰烬木告示牌");
        language.add(set.LOG.get(), "灰烬原木");
        language.add(set.PLANKS.get(), "灰烬木板");
        language.add(set.PRESSURE_PLATE.get(), "灰烬木压力板");
        language.add(set.SAPLING.get(), "灰烬树苗");
        language.add(set.SIGN.get(), "灰烬木告示牌");
        language.add(set.SLAB.get(), "灰烬木台阶");
        language.add(set.STAIRS.get(), "灰烬木楼梯");
        language.add(set.TRAPDOOR.get(), "灰烬木活板门");
        language.add(set.WOOD.get(), "灰烬木");
        language.add(set.CHISELED_PLANKS.get(), "雕纹灰烬木板");
        language.add(set.STRIPPED_LOG.get(), "去皮灰烬原木");
        language.add(set.STRIPPED_WOOD.get(), "去皮灰烬木");
    });
    public static final DeferredBlock<BasePlantBlock> ASH_GRASS = registerWithItem("ash_grass", "灰烬草", () -> new BasePlantBlock(ASH_GRASS_BLOCK.get()));

    //草药
    public static final DeferredBlock<BaseHerbBlock> WATERLEAF = registerWithoutItem("waterleaf", "水叶草", Waterleaf::new); // 幌菊
    public static final DeferredBlock<Fireblossom> FIREBLOSSOM = registerWithoutItem("fireblossom", "火焰花", Fireblossom::new); // 火焰花
    public static final DeferredBlock<Moonglow> MOONGLOW = registerWithoutItem("moonglow", "月光草", Moonglow::new); // 月光草
    public static final DeferredBlock<BaseHerbBlock> BLINKROOT = registerWithoutItem("blinkroot", "闪耀根", Blinkroot::new); // 闪耀根
    public static final DeferredBlock<BaseHerbBlock> SHIVERTHORN = registerWithoutItem("shiverthorn", "寒颤棘", ShiveringThorn::new); // 寒颤棘
    public static final DeferredBlock<BaseHerbBlock> DAYBLOOM = registerWithoutItem("daybloom", "太阳花", Daybloom::new); // 太阳花
    public static final DeferredBlock<DeathWeed> DEATHWEED = registerWithoutItem("deathweed", "死亡草", DeathWeed::new); // 死亡草
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BaseHerbBlock.BEntity>> HERBS_ENTITY = BLOCK_ENTITIES.register("herbs_entity", () -> BlockEntityType.Builder.of(BaseHerbBlock.BEntity::new,
            WATERLEAF.get(), FIREBLOSSOM.get(), MOONGLOW.get(), BLINKROOT.get(), SHIVERTHORN.get(), DAYBLOOM.get(), DEATHWEED.get()).build(DSL.remainderType()));

    public static final DeferredBlock<PooBlock> POO = registerWithItem("poo", "臭臭", () -> new PooBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD).mapColor(MapColor.COLOR_BROWN)));

    // 丛林
    public static final DeferredBlock<JungleGrassBlock> JUNGLE_GRASS_BLOCK = registerWithItem("jungle_grass_block", "丛林草方块", () -> new JungleGrassBlock(ISpreadable.Type.JUNGLE, BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK).mapColor(MapColor.COLOR_GREEN)));
    public static final DeferredBlock<ThornBlock> JUNGLE_THORN = registerWithItem("jungle_thorn", "丛林荆棘", () -> new ThornBlock(3.4f, Blocks.MOSS_BLOCK));
    public static final DeferredBlock<ThornBlock> PLANTERA_THORN = registerWithItem("plantera_thorn", "世纪之花荆棘", () -> new ThornBlock(20, Blocks.MUD));
    public static final DeferredBlock<JungleSporeBlock> JUNGLE_SPORE = registerWithoutItem("jungle_spore", "丛林孢子", JungleSporeBlock::new);
    public static final DeferredBlock<NaturesGiftBlock> NATURES_GIFT = registerWithoutItemWithEnglishName("natures_gift", "大自然的恩赐", "Nature's Gift", () -> new NaturesGiftBlock(NaturesGiftBlock.BlockItemType.NATURES_GIFT));
    public static final DeferredBlock<JungleHiveBlock> JUNGLE_HIVE_BLOCK = registerWithItem("jungle_hive_block", "丛林蜂巢", JungleHiveBlock::new);
    public static final DeferredBlock<NaturesGiftBlock> JUNGLE_ROSE = registerWithItem("jungle_rose", "丛林玫瑰", () -> new NaturesGiftBlock(NaturesGiftBlock.BlockItemType.JUNGLE_ROSE));
    public static final DeferredBlock<LarvaBlock> LARVA = registerWithItem("larva", "幼虫", () -> new LarvaBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.DESTROY).noOcclusion().replaceable().instabreak()));
    public static final DeferredBlock<LifeFruitBlock> LIFE_FRUIT = registerWithoutItem("life_fruit", "生命果", LifeFruitBlock::new);

    public static final DeferredBlock<MudPathBlock> JUNGLE_PATH = registerWithItem("jungle_path", "丛林草径", () -> new MudPathBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD).mapColor(MapColor.COLOR_LIGHT_GRAY)));
    public static final DeferredBlock<MudPathBlock> MUSHROOM_PATH = registerWithItem("mushroom_path", "蘑菇径", () -> new MudPathBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD).mapColor(MapColor.COLOR_LIGHT_GRAY)));
    public static final DeferredBlock<AshPathBlock> ASH_PATH = registerWithItem("ash_path", "灰径", () -> new AshPathBlock(BlockBehaviour.Properties.ofFullCopy(NatureBlocks.ASH_BLOCK.get()).mapColor(MapColor.COLOR_GRAY)));

    //蜘蛛洞
    public static final DeferredBlock<Block> SPIDER_NEST_STONE = registerWithItem("spider_nest_stone", "蜘蛛巢石", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE).strength(1.5F, 6.0F)));

    // 末地 - 通用
    public static final DeferredBlock<Block> END_DIRT = registerWithItem("end_dirt", "终末土", EndDirtBlock::new);
    public static final DeferredBlock<Block> END_MUD = registerWithItem("end_mud", "末地泥土", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD).mapColor(MapColor.COLOR_PURPLE)));
    public static final DeferredBlock<PassableGlowingBlock> VOID_WEAVE = registerWithItem("void_weave", "虚空构造体", () -> new PassableGlowingBlock(BlockBehaviour.Properties.ofFullCopy(GLASS).mapColor(MapColor.COLOR_PURPLE)));
    public static final DeferredBlock<Block> VOID_CRYSTAL_BLOCK = registerWithItem("void_crystal_block", "虚空水晶块", () -> new Block(BlockBehaviour.Properties.ofFullCopy(AMETHYST_BLOCK)));
    public static final DeferredBlock<BuddingVoidCrystal> BUDDING_VOID_CRYSTAL = registerWithItem("budding_void_crystal", "虚空水晶母岩", () -> new BuddingVoidCrystal(BlockBehaviour.Properties.ofFullCopy(AMETHYST_BLOCK).randomTicks()));
    public static final DeferredBlock<AmethystClusterBlock> SMALL_VOID_CRYSTAL_BUD = registerWithItem("small_void_crystal_bud", "小型虚空晶芽", () -> new AmethystClusterBlock(3.0F, 4.0F, BlockBehaviour.Properties.ofFullCopy(AMETHYST_CLUSTER).mapColor(MapColor.COLOR_PURPLE)));
    public static final DeferredBlock<AmethystClusterBlock> MEDIUM_VOID_CRYSTAL_BUD = registerWithItem("medium_void_crystal_bud", "中型虚空晶芽", () -> new AmethystClusterBlock(4.0F, 3.0F, BlockBehaviour.Properties.ofFullCopy(AMETHYST_CLUSTER).mapColor(MapColor.COLOR_PURPLE)));
    public static final DeferredBlock<AmethystClusterBlock> LARGE_VOID_CRYSTAL_BUD = registerWithItem("large_void_crystal_bud", "大型虚空晶芽", () -> new AmethystClusterBlock(5.0F, 3.0F, BlockBehaviour.Properties.ofFullCopy(AMETHYST_CLUSTER).mapColor(MapColor.COLOR_PURPLE)));
    public static final DeferredBlock<AmethystClusterBlock> VOID_CRYSTAL_CLUSTER = registerWithItem("void_crystal_cluster", "虚空水晶簇", () -> new AmethystClusterBlock(7.0F, 3.0F, BlockBehaviour.Properties.ofFullCopy(AMETHYST_CLUSTER).mapColor(MapColor.COLOR_PURPLE)));

    // 末地 - 紫颂主题
    public static final DeferredBlock<Block> VOID_GRASS_BLOCK = registerWithItem("void_grass_block", "虚空草方块", () -> new VoidGrassBlock(NatureBlocks.END_DIRT));
    public static final DeferredBlock<BasePlantBlock> VOID_VIOLET = registerWithItem("void_violet", "虚空紫罗兰", () -> new BasePlantBlock(VOID_GRASS_BLOCK.get()));
    public static final DeferredBlock<BasePlantBlock> VOID_GRASS = registerWithItem("void_grass", "虚空草", () -> new BasePlantBlock(VOID_GRASS_BLOCK.get()));
    public static final DeferredBlock<BaseTallPlantBlock> TALL_VOID_GRASS = registerWithItem("tall_void_grass", "虚空高草", () -> new BaseTallPlantBlock(VOID_GRASS_BLOCK.get()));
    public static final DeferredBlock<VoidTreeRootBlock> VOID_TREE_ROOT_BLOCK = registerWithItem("void_tree_root_block", "虚空树根", VoidTreeRootBlock::new);
    public static final Supplier<BlockEntityType<VoidTreeRootBlock.BEntity>> VOID_TREE_ROOT_BLOCK_ENTITY = ModBlocks.BLOCK_ENTITIES.register("void_tree_root_block", () -> BlockEntityType.Builder.of(VoidTreeRootBlock.BEntity::new, VOID_TREE_ROOT_BLOCK.get()).build(DSL.remainderType()));
    public static final LogBlockSet VOID_LOG_BLOCKS = LogBlockSet.builder("void", true, VOID).sapling(properties -> new BaseSaplingBlock(ModFeatures.TreeGrowers.VOID_GROWER, properties, ModTags.Blocks.END_PLANT_CAN_SURVIVE, VOID_GRASS_BLOCK, END_DIRT)).build((set, language) -> {
        language.add(set.CHISELED_PLANKS.get(), "錾制虚空木板");
        language.add(set.STRIPPED_LOG.get(), "去皮虚空原木");
        language.add(set.STRIPPED_WOOD.get(), "去皮虚空木");
        language.add(set.BUTTON.get(), "虚空木按钮");
        language.add(set.DOOR.get(), "虚空木门");
        language.add(set.FENCE.get(), "虚空木栅栏");
        language.add(set.FENCE_GATE.get(), "虚空木栅栏门");
        language.add(set.HANGING_SIGN.get(), "悬挂式虚空木告示牌");
        language.add(set.LEAVES.get(), "虚空树叶");
        language.add(set.LOG.get(), "虚空原木");
        language.add(set.PLANKS.get(), "虚空木板");
        language.add(set.PRESSURE_PLATE.get(), "虚空木压力板");
        language.add(set.SAPLING.get(), "虚空树苗");
        language.add(set.SIGN.get(), "虚空木告示牌");
        language.add(set.SLAB.get(), "虚空木台阶");
        language.add(set.STAIRS.get(), "虚空木楼梯");
        language.add(set.TRAPDOOR.get(), "虚空木活板门");
        language.add(set.WOOD.get(), "虚空木");
    });
    public static final DeferredBlock<DragonsBreathPepperBlock> DRAGONS_BREATH_PEPPER = registerWithoutItem("dragons_breath_pepper", "龙息辣椒", () -> new DragonsBreathPepperBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).strength(0.2F).sound(SoundType.CROP).randomTicks()));

    // 末地 - 倒悬主题
    public static final DeferredBlock<Block> VOID_HUMUS = registerWithItem("void_humus", "虚空腐殖土", () -> new Block(BlockBehaviour.Properties.ofFullCopy(DIRT).sound(SoundType.MUD)));
    public static final DeferredBlock<Block> INVERSE_GRASS_BLOCK = registerWithItem("inverse_grass_block", "晖落草方块", () -> new EndGrassBlock(() -> Blocks.END_STONE));
    public static final DeferredBlock<InversePlantBlock> TWILIGHT_ELLFLOWER = registerWithItem("twilight_ellflower", "落霞铃兰", () -> new InversePlantBlock(INVERSE_GRASS_BLOCK.get()));
    public static final DeferredBlock<InversePlantBlock> INVERSE_GRASS = registerWithItem("inverse_grass", "晖落草", () -> new InversePlantBlock(INVERSE_GRASS_BLOCK.get()));
    public static final DeferredBlock<InverseTallPlantBlock> TALL_INVERSE_GRASS = registerWithItem("tall_inverse_grass", "晖落高草", () -> new InverseTallPlantBlock(INVERSE_GRASS_BLOCK.get()));
    public static final DeferredBlock<InversePlantBlock> INVERSE_RHEUM_NOBILE = registerWithItem("inverse_rheum_nobile", "逆生塔黄", () -> new InversePlantBlock(INVERSE_GRASS_BLOCK.get()));
    public static final DeferredBlock<InverseTallPlantBlock> TALL_INVERSE_RHEUM_NOBILE = registerWithItem("tall_inverse_rheum_nobile", "高大逆生塔黄", () -> new InverseTallPlantBlock(INVERSE_GRASS_BLOCK.get()));
    public static final LogBlockSet GAZE_LOG_BLOCKS = LogBlockSet.builder("gaze", true, GAZE).build((set, language) -> {
        language.add(set.CHISELED_PLANKS.get(), "錾制凝视木板");
        language.add(set.BUTTON.get(), "垂眸木按钮");
        language.add(set.DOOR.get(), "垂眸木门");
        language.add(set.FENCE.get(), "垂眸木栅栏");
        language.add(set.FENCE_GATE.get(), "垂眸木栅栏门");
        language.add(set.HANGING_SIGN.get(), "悬挂式垂眸木告示牌");
        language.add(set.LEAVES.get(), "垂眸树叶");
        language.add(set.LOG.get(), "垂眸原木");
        language.add(set.PLANKS.get(), "垂眸木板");
        language.add(set.PRESSURE_PLATE.get(), "垂眸木压力板");
        language.add(set.SIGN.get(), "垂眸木告示牌");
        language.add(set.SLAB.get(), "垂眸木台阶");
        language.add(set.STAIRS.get(), "垂眸木楼梯");
        language.add(set.TRAPDOOR.get(), "垂眸木活板门");
        language.add(set.WOOD.get(), "垂眸木");
        language.add(set.STRIPPED_LOG.get(), "去皮垂眸原木");
        language.add(set.STRIPPED_WOOD.get(), "去皮垂眸木");
    });
    public static final DeferredBlock<Block> GAZE_TUBER = registerWithItem("gaze_tuber", "晖落块茎", () -> new CandyBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.COLOR_GREEN)));

    // 末地 - 月光主题
    public static final DeferredBlock<Block> MOONLIT_GRASS_BLOCK = registerWithItem("moonlit_grass_block", "月华草方块", () -> new EndGrassBlock(() -> Blocks.END_STONE));
    public static final LogBlockSet MOONGLOW_WILLOW_LOG_BLOCKS = LogBlockSet.builder("moonglow_willow", true, MOONGLOW_WILLOW).sapling(properties -> new BaseSaplingBlock(ModFeatures.TreeGrowers.MOONGLOW_WILLOW_GROWER, properties, ModTags.Blocks.END_PLANT_CAN_SURVIVE, MOONLIT_GRASS_BLOCK, END_DIRT)).build((set, language) -> {
        language.add(set.CHISELED_PLANKS.get(), "錾制月光柳木板");
        language.add(set.BUTTON.get(), "月辉柳木按钮");
        language.add(set.DOOR.get(), "月辉柳木门");
        language.add(set.FENCE.get(), "月辉柳木栅栏");
        language.add(set.FENCE_GATE.get(), "月辉柳木栅栏门");
        language.add(set.HANGING_SIGN.get(), "悬挂式月辉柳木告示牌");
        language.add(set.LEAVES.get(), "月辉柳树叶");
        language.add(set.LOG.get(), "月辉柳原木");
        language.add(set.PLANKS.get(), "月辉柳木板");
        language.add(set.PRESSURE_PLATE.get(), "月辉柳木压力板");
        language.add(set.SAPLING.get(), "月光柳树苗");
        language.add(set.SIGN.get(), "月辉柳木告示牌");
        language.add(set.SLAB.get(), "月辉柳木台阶");
        language.add(set.STAIRS.get(), "月辉柳木楼梯");
        language.add(set.TRAPDOOR.get(), "月辉柳木活板门");
        language.add(set.WOOD.get(), "月辉柳木");
        language.add(set.STRIPPED_LOG.get(), "去皮月辉柳原木");
        language.add(set.STRIPPED_WOOD.get(), "去皮月辉柳木");
    });
    public static final DeferredBlock<Block> DEAD_LUNAR_CORAL_BLOCK = registerWithItem("dead_lunar_coral_block", "失活的望月珊瑚块", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DEAD_BRAIN_CORAL_BLOCK)));
    public static final DeferredBlock<BaseCoralPlantBlock> DEAD_LUNAR_CORAL = registerWithItem("dead_lunar_coral", "失活的望月珊瑚", () -> new BaseCoralPlantBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_YELLOW)
                            .noCollission()
                            .instabreak()
                            .sound(SoundType.WET_GRASS)
                            .pushReaction(PushReaction.DESTROY)
    ));
    public static final DeferredBlock<BaseCoralWallFanBlock> DEAD_LUNAR_CORAL_WALL_FAN = registerWithoutItem("dead_lunar_coral_wall_fan", "墙上的失活望月珊瑚扇", () -> new BaseCoralWallFanBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_GRAY)
                            .forceSolidOn()
                            .instrument(NoteBlockInstrument.BASEDRUM)
                            .requiresCorrectToolForDrops()
                            .noCollission()
                            .instabreak()
    ));
    public static final DeferredBlock<LunarCoralFanBlock> DEAD_LUNAR_CORAL_FAN = registerWithItem("dead_lunar_coral_fan", "失活的望月珊瑚扇", () -> new LunarCoralFanBlock(
                    DEAD_LUNAR_CORAL_WALL_FAN.get(),
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_YELLOW)
                            .noCollission()
                            .instabreak()
                            .sound(SoundType.WET_GRASS)
                            .pushReaction(PushReaction.DESTROY)
    ));
    public static final DeferredBlock<LunarCoralBlock> LUNAR_CORAL_BLOCK = registerWithItem("lunar_coral_block", "望月珊瑚块", () -> new LunarCoralBlock(DEAD_LUNAR_CORAL_BLOCK.get(), BlockBehaviour.Properties.ofFullCopy(BRAIN_CORAL_BLOCK)));
    public static final DeferredBlock<LunarCoralPlantBlock> LUNAR_CORAL = registerWithItem("lunar_coral", "望月珊瑚", () -> new LunarCoralPlantBlock(
                    DEAD_LUNAR_CORAL.get(),
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_YELLOW)
                            .noCollission()
                            .instabreak()
                            .sound(SoundType.WET_GRASS)
                            .pushReaction(PushReaction.DESTROY)
    ));
    public static final DeferredBlock<LunarCoralWallFanBlock> LUNAR_CORAL_WALL_FAN = registerWithoutItem("lunar_coral_wall_fan", "墙上的望月珊瑚扇", () -> new LunarCoralWallFanBlock(
                    DEAD_LUNAR_CORAL_WALL_FAN.get(),
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_YELLOW)
                            .noCollission()
                            .instabreak()
                            .sound(SoundType.WET_GRASS)
                            .pushReaction(PushReaction.DESTROY)
    ));
    public static final DeferredBlock<LunarCoralFanBlock> LUNAR_CORAL_FAN = registerWithItem("lunar_coral_fan", "望月珊瑚扇", () -> new LunarCoralFanBlock(
                    DEAD_LUNAR_CORAL_FAN.get(),
                    LUNAR_CORAL_WALL_FAN.get(),
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_YELLOW)
                            .noCollission()
                            .instabreak()
                            .sound(SoundType.WET_GRASS)
                            .pushReaction(PushReaction.DESTROY)
    ));
    public static final DeferredBlock<Block> GLOW_CORALITE = registerWithItem("glow_coralite", "荧光珊瑚虫", () -> new Block(BlockBehaviour.Properties.ofFullCopy(DEAD_BRAIN_CORAL_BLOCK).sound(SoundType.MUD).lightLevel(state -> 10)));
    public static final DeferredBlock<BasePlantBlock> SILVER_GRASS = registerWithItem("silver_grass", "银月草", () -> new BasePlantBlock(MOONLIT_GRASS_BLOCK.get(), END_DIRT.get()));
    public static final DeferredBlock<BaseTallPlantBlock> TALL_SILVER_GRASS = registerWithItem("tall_silver_grass", "银月高草", () -> new BaseTallPlantBlock(MOONLIT_GRASS_BLOCK.get(), END_DIRT.get()));
    public static final DeferredBlock<PassableGlowingBlock> DREAM_BUBBLE = registerWithItem("dream_bubble", "梦幻泡影", () -> new PassableGlowingBlock(BlockBehaviour.Properties.ofFullCopy(GLASS).mapColor(MapColor.COLOR_PURPLE)));

    // 地獄 - 黯虛主題
    public static final DeferredBlock<Block> GLOOM_OBSIDIAN = registerWithItem("gloom_obsidian", "黯虚黑曜石", () -> new Block(BlockBehaviour.Properties.ofFullCopy(OBSIDIAN)));

    // 王朝木
    public static final LogBlockSet DYNASTY_LOG_BLOCKS = LogBlockSet.builder("dynasty", true, DYNASTY).leaves(null).build((set, language) -> {
        language.add(set.CHISELED_PLANKS.get(), "雕纹王朝木板");
        language.add(set.BUTTON.get(), "王朝木按钮");
        language.add(set.DOOR.get(), "王朝木门");
        language.add(set.FENCE.get(), "王朝木栅栏");
        language.add(set.FENCE_GATE.get(), "王朝木栅栏门");
        language.add(set.HANGING_SIGN.get(), "悬挂式王朝木告示牌");
        language.add(set.LOG.get(), "王朝原木");
        language.add(set.PLANKS.get(), "王朝木板");
        language.add(set.PRESSURE_PLATE.get(), "王朝木压力板");
        language.add(set.SIGN.get(), "王朝木告示牌");
        language.add(set.SLAB.get(), "王朝木台阶");
        language.add(set.STAIRS.get(), "王朝木楼梯");
        language.add(set.TRAPDOOR.get(), "王朝木活板门");
        language.add(set.WOOD.get(), "王朝木");
        language.add(set.STRIPPED_LOG.get(), "去皮王朝原木");
        language.add(set.STRIPPED_WOOD.get(), "去皮王朝木");
    });

    // 松树
    public static final LogBlockSet PINE_LOG_BLOCKS = LogBlockSet.builder("pine", true, PINE).build((set, language) -> {
        language.add(set.CHISELED_PLANKS.get(), "雕纹松树木板");
        language.add(set.BUTTON.get(), "松木按钮");
        language.add(set.DOOR.get(), "松木门");
        language.add(set.FENCE.get(), "松木栅栏");
        language.add(set.FENCE_GATE.get(), "松木栅栏门");
        language.add(set.HANGING_SIGN.get(), "悬挂式松木告示牌");
        language.add(set.LEAVES.get(), "松树树叶");
        language.add(set.LOG.get(), "松木原木");
        language.add(set.PLANKS.get(), "松木木板");
        language.add(set.PRESSURE_PLATE.get(), "松木压力板");
        language.add(set.SIGN.get(), "松木告示牌");
        language.add(set.SLAB.get(), "松木台阶");
        language.add(set.STAIRS.get(), "松木楼梯");
        language.add(set.TRAPDOOR.get(), "松木活板门");
        language.add(set.WOOD.get(), "松木");
        language.add(set.STRIPPED_LOG.get(), "去皮松木原木");
        language.add(set.STRIPPED_WOOD.get(), "去皮松木");
    });
    public static final DeferredBlock<PineSaplingBlock> PINE_SAPLING = registerWithItem("pine_sapling", "松树苗", () -> new PineSaplingBlock(ModFeatures.TreeGrowers.PINE_GROWER, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)));
    public static final DeferredBlock<PineSaplingBlock> PRUNED_PINE_SAPLING = registerWithItem("pruned_pine_sapling", "修枝松树苗", () -> new PineSaplingBlock(ModFeatures.TreeGrowers.CHINESE_PINE_GROWER, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)));

    // 仙灵木
    public static final LogBlockSet FEY_LOG_BLOCKS = LogBlockSet.builder("fey", true, FEY).leaves(null).build((set, language) -> {
        language.add(set.CHISELED_PLANKS.get(), "雕纹仙灵木板");
        language.add(set.BUTTON.get(), "仙灵按钮");
        language.add(set.DOOR.get(), "仙灵木门");
        language.add(set.FENCE.get(), "仙灵木栅栏");
        language.add(set.FENCE_GATE.get(), "仙灵木栅栏门");
        language.add(set.HANGING_SIGN.get(), "仙灵木悬挂告示牌");
        language.add(set.LOG.get(), "仙灵原木");
        language.add(set.PLANKS.get(), "仙灵木板");
        language.add(set.PRESSURE_PLATE.get(), "仙灵木压力板");
        language.add(set.SIGN.get(), "仙灵木告示牌");
        language.add(set.SLAB.get(), "仙灵木台阶");
        language.add(set.STAIRS.get(), "仙灵木楼梯");
        language.add(set.TRAPDOOR.get(), "仙灵木活板门");
        language.add(set.WOOD.get(), "仙灵木");
        language.add(set.STRIPPED_LOG.get(), "去皮仙灵木原木");
        language.add(set.STRIPPED_WOOD.get(), "去皮仙灵木");
    });
    // 空岛
    public static final DeferredBlock<CloudBlock> CLOUD_BLOCK = registerWithItem("cloud_block", "云块", () -> new CloudBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.TERRACOTTA_WHITE)
            .mapColor(MapColor.SNOW)
            .strength(0.3F)
            .sound(SoundType.SNOW)
            .noOcclusion()
            .isValidSpawn(Blocks::never)
            .isRedstoneConductor((blockState, blockGetter, blockPos) -> false)
            .isSuffocating((blockState, blockGetter, blockPos) -> false)));
    public static final DeferredBlock<EvaporativeCloudBlock> EVAPORATIVE_CLOUD_BLOCK = registerWithItem("evaporative_cloud_block", "蒸发云块", () -> new EvaporativeCloudBlock(BlockBehaviour.Properties.of()
            .sound(SoundType.SNOW)
            .noOcclusion()
            .randomTicks()));
    public static final Supplier<BlockEntityType<EvaporativeCloudBlock.BEntity>> EVAPORATIVE_CLOUD_BLOCK_ENTITY = ModBlocks.BLOCK_ENTITIES.register("evaporative_cloud_block", () -> BlockEntityType.Builder.of(EvaporativeCloudBlock.BEntity::new, EVAPORATIVE_CLOUD_BLOCK.get()).build(DSL.remainderType()));
    public static final DeferredBlock<ParticleCloudBlock> RAIN_CLOUD_BLOCK = registerWithItem("rain_cloud_block", "雨云块", () -> new ParticleCloudBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_GRAY)
            .strength(0.3F)
            .sound(SoundType.SNOW)
            .noOcclusion()
            .isValidSpawn(Blocks::never)
            .isRedstoneConductor((blockState, blockGetter, blockPos) -> false)
            .isSuffocating((blockState, blockGetter, blockPos) -> false),
            ParticleTypes.FALLING_WATER));
    public static final DeferredBlock<ParticleCloudBlock> SNOW_CLOUD_BLOCK = registerWithItem("snow_cloud_block", "雪云块", () -> new ParticleCloudBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_GRAY)
            .strength(0.3F)
            .sound(SoundType.SNOW)
            .noOcclusion()
            .isValidSpawn(Blocks::never)
            .isRedstoneConductor((blockState, blockGetter, blockPos) -> false)
            .isSuffocating((blockState, blockGetter, blockPos) -> false),
            ParticleTypes.SNOWFLAKE));
    public static final DeferredBlock<StellarBlossomBlock> STELLAR_BLOSSOM = registerWithItem("stellar_blossom", "星辰花", () -> new StellarBlossomBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION).offsetType(BlockBehaviour.OffsetType.NONE)));
    public static final DeferredBlock<CloudWeaverBlock> CLOUDWEAVER = registerWithItem("cloudweaver", "云织草", () -> new CloudWeaverBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION).offsetType(BlockBehaviour.OffsetType.NONE)));
    public static final DeferredBlock<FloatingWheatBlock> FLOATING_WHEAT = registerWithItem("floating_wheat", "飘飘麦", () -> new FloatingWheatBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).offsetType(BlockBehaviour.OffsetType.NONE)));

    public static final DeferredBlock<BalloonMelonBlock> BALLOON_MELON = registerWithItem("balloon_melon", "气球瓜", BalloonMelonBlock::new);
    public static final DeferredBlock<BalloonAttachedStemBlock> ATTACHED_BALLOON_STEM = registerWithoutItem("attached_balloon_stem", "结果的气球茎", () -> new BalloonAttachedStemBlock(
            BALLOON_MELON.getKey(),
            Confluence.asResourceKey(Registries.BLOCK,"balloon_stem"),
            FoodItems.BALLOON_SEED.getKey(),
            BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY)
    ));
    public static final DeferredBlock<BalloonStemBlock> BALLOON_STEM = registerWithoutItem("balloon_stem", "气球茎", () -> new BalloonStemBlock(
            BALLOON_MELON.getKey(),
            ATTACHED_BALLOON_STEM.getKey(),
            FoodItems.BALLOON_SEED.getKey()
    ));

    // 枝杈
    public static final DeferredBlock<BranchesBlock> AMBER_BRANCHES = registerWithItem("amber_branches", "琥珀石枝杈", () -> new BranchesBlock(ModTags.Blocks.JEWELLERY_BRANCHES_ATTACHABLE, BlockTags.BASE_STONE_OVERWORLD));
    public static final DeferredBlock<BranchesBlock> RUBY_BRANCHES = registerWithItem("ruby_branches", "红玉石枝杈", () -> new BranchesBlock(ModTags.Blocks.JEWELLERY_BRANCHES_ATTACHABLE, BlockTags.BASE_STONE_OVERWORLD));
    public static final DeferredBlock<BranchesBlock> TOPAZ_BRANCHES = registerWithItem("topaz_branches", "黄玉石枝杈", () -> new BranchesBlock(ModTags.Blocks.JEWELLERY_BRANCHES_ATTACHABLE, BlockTags.BASE_STONE_OVERWORLD));
    public static final DeferredBlock<BranchesBlock> JADE_BRANCHES = registerWithItem("jade_branches", "翡翠石枝杈", () -> new BranchesBlock(ModTags.Blocks.JEWELLERY_BRANCHES_ATTACHABLE, BlockTags.BASE_STONE_OVERWORLD));
    public static final DeferredBlock<BranchesBlock> DIAMOND_BRANCHES = registerWithItem("diamond_branches", "钻石枝杈", () -> new BranchesBlock(ModTags.Blocks.JEWELLERY_BRANCHES_ATTACHABLE, BlockTags.BASE_STONE_OVERWORLD));
    public static final DeferredBlock<BranchesBlock> SAPPHIRE_BRANCHES = registerWithItem("sapphire_branches", "蓝玉石枝杈", () -> new BranchesBlock(ModTags.Blocks.JEWELLERY_BRANCHES_ATTACHABLE, BlockTags.BASE_STONE_OVERWORLD));
    public static final DeferredBlock<BranchesBlock> AMETHYST_BRANCHES = registerWithItem("amethyst_branches", "紫晶石枝杈", () -> new BranchesBlock(ModTags.Blocks.JEWELLERY_BRANCHES_ATTACHABLE, BlockTags.BASE_STONE_OVERWORLD));
    public static final DeferredBlock<BranchesBlock> ASH_BRANCHES = registerWithItem("ash_branches", "灰烬枝杈", () -> new BranchesBlock(ModTags.Blocks.ASH_LOG_BRANCHES_ATTACHABLE, ModTags.Blocks.ASH_LOG_BRANCHES_ATTACHABLE));

    // 藤蔓方块
    public static final DeferredBlock<BaseDroopingPlantsHeadBlock> YELLOW_WILLOW_DROOPING_LEAVES = registerWithItem("yellow_willow_drooping_leaves", "黄柳垂叶", () -> new BaseDroopingPlantsHeadBlock(14, false, false, () -> List.of(NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.LEAVES.get())));
    public static final DeferredBlock<BaseDroopingPlantsHeadBlock> GLOWING_MUSHROOM_VINE = registerWithItem("glowing_mushroom_vine", "发光蘑菇垂藤", () -> new BaseDroopingPlantsHeadBlock(6, true, true));
    public static final DeferredBlock<BaseDroopingPlantsHeadBlock> FOREST_DROOPING_VINE = registerWithItem("forest_drooping_vine", "森林垂藤", () -> new BaseDroopingPlantsHeadBlock(10, true, true));
    public static final DeferredBlock<BaseDroopingPlantsHeadBlock> JUNGLE_DROOPING_VINE = registerWithItem("jungle_drooping_vine", "丛林垂藤", () -> new BaseDroopingPlantsHeadBlock(10, true, true));
    public static final DeferredBlock<BaseDroopingPlantsHeadBlock> CORRUPT_DROOPING_VINE = registerWithItem("corrupt_drooping_vine", "腐化垂藤", () -> new BaseDroopingPlantsHeadBlock(10, true, true));
    public static final DeferredBlock<BaseDroopingPlantsHeadBlock> CRIMSON_DROOPING_VINE = registerWithItem("crimson_drooping_vine", "猩红垂藤", () -> new BaseDroopingPlantsHeadBlock(10, true, true));
    public static final DeferredBlock<BaseDroopingPlantsHeadBlock> HALLOW_DROOPING_VINE = registerWithItem("hallow_drooping_vine", "神圣垂藤", () -> new BaseDroopingPlantsHeadBlock(10, true, true));
    public static final DeferredBlock<BaseDroopingPlantsHeadBlock> PINE_DROOPING_VINE = registerWithItem("pine_drooping_vine", "松树垂藤", () -> new BaseDroopingPlantsHeadBlock(8, false, true, () -> List.of(NatureBlocks.PINE_LOG_BLOCKS.LEAVES.get(), NatureBlocks.PINE_LOG_BLOCKS.LOG.get())));
    public static final DeferredBlock<BaseDroopingPlantsHeadBlock> SILENT_DROOPING_VINE = registerWithItem("silent_drooping_vine", "寂籁垂藤", () -> new BaseDroopingPlantsHeadBlock(10, false, true, () -> List.of(
            Blocks.END_STONE,
            VOID_GRASS_BLOCK.get(),
            END_DIRT.get(),
            MOONLIT_GRASS_BLOCK.get(),
            INVERSE_GRASS_BLOCK.get()
    )));
    public static final DeferredBlock<BaseDroopingPlantsHeadBlock> MOONGLOW_WILLOW_DROOPING_VINE = registerWithItem("moonglow_willow_drooping_vine", "月辉柳垂叶", () -> new BaseDroopingPlantsHeadBlock(10, false, true, () -> List.of(
            NatureBlocks.MOONGLOW_WILLOW_LOG_BLOCKS.LOG.get(),
            NatureBlocks.MOONGLOW_WILLOW_LOG_BLOCKS.LEAVES.get()
    )));
    public static final DeferredBlock<BaseDroopingPlantsHeadBlock> WITHERED_SEA_SILK = registerWithItem("withered_sea_silk", "枯海沧丝", () -> new BaseDroopingPlantsHeadBlock(10, Direction.UP, false, true, () -> List.of(
            Blocks.END_STONE,
            VOID_GRASS_BLOCK.get(),
            END_DIRT.get(),
            MOONLIT_GRASS_BLOCK.get(),
            INVERSE_GRASS_BLOCK.get()
    ), 15));

    //微光环境物块
    public static final DeferredBlock<ShimmerDroopingVinesBlock> SHIMMER_DROOPING_VINE = registerWithoutItem("shimmer_drooping_vine", "微光浆果", () -> new ShimmerDroopingVinesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAVE_VINES)));
    public static final DeferredBlock<ShimmerDroopingVinesPlantBlock> SHIMMER_DROOPING_VINE_PLANT = registerWithoutItem("shimmer_drooping_vine_plant", "微光垂藤", () -> new ShimmerDroopingVinesPlantBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAVE_VINES_PLANT)));
    public static final DeferredBlock<ShimmerRiceBlock> SHIMMER_RICE = registerWithItem("shimmer_rice", "微光水稻", () -> new ShimmerRiceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT)));
    public static final DeferredBlock<ShimmerCoralTubeBlock> SHIMMER_CORAL_TUBE = registerWithItem("shimmer_coral_tube", "微光珊瑚管", () -> new ShimmerCoralTubeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).instabreak().pushReaction(PushReaction.DESTROY).sound(SoundType.WET_GRASS)));

    public static final DeferredBlock<BlinkingRoyalShimmerlilyBlock> BLINKING_ROYAL_SHIMMERLILY = registerWithItem("blinking_royal_shimmerlily", "微光王莲", () -> new BlinkingRoyalShimmerlilyBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LILY_PAD)), block -> new PlaceOnWaterBlockItem(block, new Item.Properties()));

    // 苔藓
    public static final DeferredBlock<BaseMossBlock> GREEN_MOSS = registerWithItem("green_moss", "绿苔藓", () -> new BaseMossBlock(5, BlockBehaviour.Properties.of()
            .noCollission()
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .strength(0.2F)
            .sound(SoundType.GLOW_LICHEN)
            .replaceable()));
    public static final DeferredBlock<BaseMossBlock> BROWN_MOSS = registerWithItem("brown_moss", "棕苔藓", () -> new BaseMossBlock(5, BlockBehaviour.Properties.of()
            .noCollission()
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .strength(0.2F)
            .sound(SoundType.GLOW_LICHEN)
            .replaceable()));
    public static final DeferredBlock<BaseMossBlock> RED_MOSS = registerWithItem("red_moss", "红苔藓", () -> new BaseMossBlock(5, BlockBehaviour.Properties.of()
            .noCollission()
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .strength(0.2F)
            .sound(SoundType.GLOW_LICHEN)
            .replaceable()));
    public static final DeferredBlock<BaseMossBlock> BLUE_MOSS = registerWithItem("blue_moss", "蓝苔藓", () -> new BaseMossBlock(5, BlockBehaviour.Properties.of()
            .noCollission()
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .strength(0.2F)
            .sound(SoundType.GLOW_LICHEN)
            .replaceable()));
    public static final DeferredBlock<BaseMossBlock> PURPLE_MOSS = registerWithItem("purple_moss", "紫苔藓", () -> new BaseMossBlock(5, BlockBehaviour.Properties.of()
            .noCollission()
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .strength(0.2F)
            .sound(SoundType.GLOW_LICHEN)
            .replaceable()));
    public static final DeferredBlock<BaseMossBlock> LAVA_MOSS = registerWithItem("lava_moss", "熔岩苔藓", () -> new BaseMossBlock(10, BlockBehaviour.Properties.of()
            .noCollission()
            .pushReaction(PushReaction.DESTROY)
            .strength(0.2F)
            .sound(SoundType.GLOW_LICHEN)
            .replaceable()));
    public static final DeferredBlock<BaseMossBlock> KRYPTON_MOSS = registerWithItem("krypton_moss", "氪苔藓", () -> new BaseMossBlock(10, BlockBehaviour.Properties.of()
            .noCollission()
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .strength(0.2F)
            .sound(SoundType.GLOW_LICHEN)
            .replaceable()));
    public static final DeferredBlock<BaseMossBlock> XENON_MOSS = registerWithItem("xenon_moss", "氙苔藓", () -> new BaseMossBlock(10, BlockBehaviour.Properties.of()
            .noCollission()
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .strength(0.2F)
            .sound(SoundType.GLOW_LICHEN)
            .replaceable()));
    public static final DeferredBlock<BaseMossBlock> ARGON_MOSS = registerWithItem("argon_moss", "氩苔藓", () -> new BaseMossBlock(10, BlockBehaviour.Properties.of()
            .noCollission()
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .strength(0.2F)
            .sound(SoundType.GLOW_LICHEN)
            .replaceable()));
    public static final DeferredBlock<BaseMossBlock> NEON_MOSS = registerWithItem("neon_moss", "氖苔藓", () -> new BaseMossBlock(10, BlockBehaviour.Properties.of()
            .noCollission()
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .strength(0.2F)
            .sound(SoundType.GLOW_LICHEN)
            .replaceable()));
    public static final DeferredBlock<BaseMossBlock> HELIUM_MOSS = registerWithItem("helium_moss", "氦苔藓", () -> new BaseMossBlock(10, BlockBehaviour.Properties.of()
            .noCollission()
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .strength(0.2F)
            .sound(SoundType.GLOW_LICHEN)
            .replaceable()));
    public static final DeferredBlock<BaseMossBlock> GLOWING_MUSHROOM_MOSS = registerWithItem("glowing_mushroom_moss", "发光蘑菇苔藓", () -> new BaseMossBlock(8, BlockBehaviour.Properties.of()
            .noCollission()
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .strength(0.2F)
            .sound(SoundType.GLOW_LICHEN)
            .replaceable()));

    public static final DeferredBlock<CrimsonVenusFlytrapBlock> CRIMSON_VENUS_FLYTRAP_BLOCK = registerWithItem("crimson_venus_flytrap_block", "猩红捕蝇草", CrimsonVenusFlytrapBlock::new);
    public static final DeferredBlock<BloodthirstCrystallizedBlock> BLOODTHIRST_CRYSTALLIZED_BLOCK = registerWithItem("bloodthirst_crystallized_block", "嗜血结晶", BloodthirstCrystallizedBlock::new);
    public static final Supplier<BlockEntityType<BloodthirstCrystallizedBlock.BEntity>> BLOODTHIRST_CRYSTALLIZED_ENTITY = ModBlocks.BLOCK_ENTITIES.register("bloodthirst_crystallized_entity", () -> BlockEntityType.Builder.of(BloodthirstCrystallizedBlock.BEntity::new, BLOODTHIRST_CRYSTALLIZED_BLOCK.get()).build(DSL.remainderType()));
    public static final DeferredBlock<CorrodedWormRootsBlock> CORRODED_WORM_ROOTS_BLOCK = registerWithItem("corroded_worm_roots_block", "腐蚀蠕根", CorrodedWormRootsBlock::new);
    public static final DeferredBlock<CorruptedOvariesBlock> CORRUPTED_OVARIES_BLOCK = registerWithItem("corrupted_ovaries_block", "腐化卵巢", CorruptedOvariesBlock::new);
    public static final DeferredBlock<DecomposeTheSourceExtractBlock> DECOMPOSE_THE_SOURCE_EXTRACT_BLOCK = registerWithItem("decompose_the_source_extract_block", "分解源萃", DecomposeTheSourceExtractBlock::new);
    public static final Supplier<BlockEntityType<DecomposeTheSourceExtractBlock.BEntity>> DECOMPOSE_THE_SOURCE_EXTRACT_ENTITY = ModBlocks.BLOCK_ENTITIES.register("decompose_the_source_extract_entity", () -> BlockEntityType.Builder.of(DecomposeTheSourceExtractBlock.BEntity::new, DECOMPOSE_THE_SOURCE_EXTRACT_BLOCK.get()).build(DSL.remainderType()));
    public static final DeferredBlock<ShimmerCrystalslBlock> SHIMMER_CRYSTALS_BLOCK = registerWithItem("shimmer_crystals_block", "以太结晶", ShimmerCrystalslBlock::new);

    public static final Supplier<BlockEntityType<LostPaperBlock.BEntity>> LOST_PAPER_ENTITY = ModBlocks.BLOCK_ENTITIES.register("lost_paper", () -> BlockEntityType.Builder.of(LostPaperBlock.BEntity::new, LOST_PAPER_BLOCK.get()).build(DSL.remainderType()));

    private static <B extends Block> DeferredBlock<B> registerWithItem(String id, Supplier<B> block) {
        DeferredBlock<B> object = BLOCKS.register(id, block);
        ModItems.BLOCK_ITEMS.registerSimpleBlockItem(object);
        return object;
    }

    private static <B extends Block> DeferredBlock<B> registerWithItem(String id, Supplier<B> block, Function<B, BlockItem> function) {
        DeferredBlock<B> object = BLOCKS.register(id, block);
        ModItems.BLOCK_ITEMS.register(id, () -> function.apply(object.get()));
        return object;
    }

    private static Supplier<Block> getSupplier(Block block) {
        return () -> block;
    }

    private static <B extends Block> DeferredBlock<B> registerWithoutItem(String registryId, String chineseName, Supplier<B> block) {
        DeferredBlock<B> entry = BLOCKS.register(registryId, block);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <B extends Block> DeferredBlock<B> registerWithItem(String registryId, String chineseName, Supplier<B> block) {
        DeferredBlock<B> entry = registerWithItem(registryId, block);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <B extends Block> DeferredBlock<B> registerWithItem(String registryId, String chineseName, Supplier<B> block, Function<B, BlockItem> function) {
        DeferredBlock<B> entry = registerWithItem(registryId, block, function);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <B extends Block> DeferredBlock<B> registerWithoutItemWithEnglishName(String registryId, String chineseName, String englishName, Supplier<B> block) {
        DeferredBlock<B> entry = BLOCKS.register(registryId, block);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }
}
