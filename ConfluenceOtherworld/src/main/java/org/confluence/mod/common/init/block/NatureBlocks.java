package org.confluence.mod.common.init.block;

import com.mojang.datafixers.DSL;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PlaceOnWaterBlockItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.block.TransparentLeavesBlock;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.common.SoulGlassBlock;
import org.confluence.mod.common.block.natural.*;
import org.confluence.mod.common.block.natural.MushroomBlock;
import org.confluence.mod.common.block.natural.sapling.BaseSaplingBlock;
import org.confluence.mod.common.block.natural.sapling.PineSaplingBlock;
import org.confluence.mod.common.block.natural.sapling.StoneSaplingBlock;
import org.confluence.mod.common.block.natural.spreadable.*;
import org.confluence.mod.common.block.natural.spreadable.extended.*;
import org.confluence.mod.common.init.ModFeatures;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.FoodItems;
import org.confluence.mod.common.init.item.ModItems;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraft.world.level.block.Blocks.*;
import static org.confluence.mod.common.block.natural.LogBlockSet.WoodSetType.*;

@ParametersAreNonnullByDefault
public class NatureBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Confluence.MODID);

    // 非环境树苗
    public static final DeferredBlock<StoneSaplingBlock> RUBY_SAPLING = registerWithItem("ruby_sapling", () -> new StoneSaplingBlock(ModFeatures.TreeGrowers.RUBY_GROWER));
    public static final DeferredBlock<StoneSaplingBlock> AMBER_SAPLING = registerWithItem("amber_sapling", () -> new StoneSaplingBlock(ModFeatures.TreeGrowers.AMBER_GROWER));
    public static final DeferredBlock<StoneSaplingBlock> TOPAZ_SAPLING = registerWithItem("topaz_sapling", () -> new StoneSaplingBlock(ModFeatures.TreeGrowers.TOPAZ_GROWER));
    public static final DeferredBlock<StoneSaplingBlock> JADE_SAPLING = registerWithItem("jade_sapling", () -> new StoneSaplingBlock(ModFeatures.TreeGrowers.JADE_GROWER));
    public static final DeferredBlock<StoneSaplingBlock> DIAMOND_SAPLING = registerWithItem("diamond_sapling", () -> new StoneSaplingBlock(ModFeatures.TreeGrowers.DIAMOND_GROWER));
    public static final DeferredBlock<StoneSaplingBlock> SAPPHIRE_SAPLING = registerWithItem("sapphire_sapling", () -> new StoneSaplingBlock(ModFeatures.TreeGrowers.SAPPHIRE_GROWER));
    public static final DeferredBlock<StoneSaplingBlock> AMETHYST_SAPLING = registerWithItem("amethyst_sapling", () -> new StoneSaplingBlock(ModFeatures.TreeGrowers.AMETHYST_GROWER));

    // 流体接触块
    public static final DeferredBlock<ThinHoneyBlock> THIN_HONEY_BLOCK = registerWithItem("thin_honey_block", () -> new ThinHoneyBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.HONEY_BLOCK).isSuffocating((blockState, blockGetter, blockPos) -> false).mapColor(MapColor.COLOR_ORANGE).isViewBlocking((state, level, pos) -> false)));
    public static final DeferredBlock<Block> LOOSE_HONEY_BLOCK = registerWithItem("loose_honey_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.HONEY_BLOCK).mapColor(MapColor.COLOR_ORANGE)));
    public static final DeferredBlock<AetheriumBlock> AETHERIUM_BLOCK = registerWithItem("aetherium_block", () -> new AetheriumBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(MapColor.COLOR_PINK).lightLevel(blockState -> 10)));
    public static final DeferredBlock<Block> DARK_AETHERIUM_BLOCK = registerWithItem("dark_aetherium_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(MapColor.COLOR_GRAY)));

    // 香蒲
    public static final DeferredBlock<CattailBlock> CATTAIL_BLOCK = registerWithoutItem("cattail_block", () -> new CattailBlock(BlockBehaviour.Properties.of().sound(SoundType.GRASS).mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<CattailBlock> JUNGLE_CATTAIL_BLOCK = registerWithoutItem("jungle_cattail_block", () -> new CattailBlock(BlockBehaviour.Properties.of().sound(SoundType.GRASS).mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<CattailBlock> GLOWING_MUSHROOM_CATTAIL_BLOCK = registerWithoutItem("glowing_mushroom_cattail_block", () -> new CattailBlock(BlockBehaviour.Properties.of().sound(SoundType.GRASS).mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<CattailBlock> HALLOW_CATTAIL_BLOCK = registerWithoutItem("hallow_cattail_block", () -> new CattailBlock(BlockBehaviour.Properties.of().sound(SoundType.GRASS).mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<CattailBlock> EBONY_CATTAIL_BLOCK = registerWithoutItem("ebony_cattail_block", () -> new CattailBlock(BlockBehaviour.Properties.of().sound(SoundType.GRASS).mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<CattailBlock> CRIMSON_CATTAIL_BLOCK = registerWithoutItem("crimson_cattail_block", () -> new CattailBlock(BlockBehaviour.Properties.of().sound(SoundType.GRASS).mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY)));

    // 环境辅助
    public static final DeferredBlock<ThinIceBlock> THIN_ICE_BLOCK = registerWithItem("thin_ice_block", ThinIceBlock::new);
    public static final DeferredBlock<TaperedTwoPartBlock> ICE_TAPERED_BLOCK = registerWithItem("ice_tapered_block", TaperedTwoPartBlock::new);
    public static final DeferredBlock<Block> HARDENED_SAND_BLOCK = registerWithItem("hardened_sand_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).mapColor(MapColor.SAND)));
    public static final DeferredBlock<MoistSandBlock> MOISTENED_SAND_BLOCK = registerWithItem("moistened_sand_block", () -> new MoistSandBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD).mapColor(MapColor.SAND), Blocks.SAND));
    public static final DeferredBlock<Block> HARDENED_RED_SAND_BLOCK = registerWithItem("hardened_red_sand_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).mapColor(MapColor.TERRACOTTA_RED)));
    public static final DeferredBlock<MoistSandBlock> MOISTENED_RED_SAND_BLOCK = registerWithItem("moistened_red_sand_block", () -> new MoistSandBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD).mapColor(MapColor.TERRACOTTA_RED), Blocks.RED_SAND));
    public static final DeferredBlock<Block> DIATOMACEOUS = registerWithItem("diatomaceous", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.TERRACOTTA_PINK)));
    public static final DeferredBlock<SandLayerBlock> SAND_LAYER_BLOCK = registerWithItem("sand_layer_block", SandLayerBlock::new);
    public static final DeferredBlock<SandLayerBlock> RED_SAND_LAYER_BLOCK = registerWithItem("red_sand_layer_block", SandLayerBlock::new);
    public static final DeferredBlock<Block> DESERT_FOSSIL = registerWithItem("desert_fossil", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).mapColor(MapColor.TERRACOTTA_RED)));
    public static final DeferredBlock<ColoredFallingBlock> SLUSH = registerWithItem("slush", () -> new ColoredFallingBlock(new ColorRGBA(-4532781), BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<ColoredFallingBlock> SILT_BLOCK = registerWithItem("silt_block", () -> new ColoredFallingBlock(new ColorRGBA(-9673114), BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.TERRACOTTA_GRAY)));
    public static final DeferredBlock<ColoredFallingBlock> MARINE_GRAVEL = registerWithItem("marine_gravel", () -> new ColoredFallingBlock(new ColorRGBA(-9588022), BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.TERRACOTTA_CYAN)));
    public static final DeferredBlock<RotatedPillarBlock> STONY_LOG = registerWithItem("stony_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.COLOR_LIGHT_GRAY)));
    public static final DeferredBlock<LifeCrystalBlock> LIFE_CRYSTAL_BLOCK = registerWithItem("life_crystal_block", () -> new LifeCrystalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_CLUSTER).mapColor(MapColor.COLOR_RED).lightLevel(state -> 7)), LifeCrystalBlock.BItem::new);
    public static final Supplier<BlockEntityType<LifeCrystalBlock.BEntity>> LIFE_CRYSTAL_BLOCK_ENTITY = ModBlocks.BLOCK_ENTITIES.register("life_crystal_block_entity", () -> BlockEntityType.Builder.of(LifeCrystalBlock.BEntity::new, LIFE_CRYSTAL_BLOCK.get()).build(DSL.remainderType()));

    public static final DeferredBlock<Block> GRANITE = registerWithItem("granite", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static final DeferredBlock<TaperedTwoPartBlock> GRANITE_TAPERED_BLOCK = registerWithItem("granite_tapered_block", TaperedTwoPartBlock::new);
    public static final DeferredBlock<Block> MARBLE = registerWithItem("marble", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static final DeferredBlock<TaperedTwoPartBlock> MARBLE_TAPERED_BLOCK = registerWithItem("marble_tapered_block", TaperedTwoPartBlock::new);
    public static final DeferredBlock<MushroomBlock> LIFE_MUSHROOM = registerWithoutItem("life_mushroom", () -> new MushroomBlock(ISpreadable.Type.PURE, Blocks.GRASS_BLOCK)); // 生命蘑菇

    // 腐化
    public static final DeferredBlock<SpreadingGrassBlock> CORRUPT_GRASS_BLOCK = registerWithItem("corrupt_grass_block", () -> new SpreadingGrassBlock(ISpreadable.Type.CORRUPT, BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK).mapColor(MapColor.COLOR_PURPLE)));
    public static final LogBlockSet EBONY_LOG_BLOCKS = LogBlockSet.builder("ebony", true, EBONY).sapling(properties -> new BaseSaplingBlock(ModFeatures.TreeGrowers.EBONY_GROWER, properties, null, CORRUPT_GRASS_BLOCK)).build();
    public static final DeferredBlock<SpreadingBlock> EBONSTONE = registerWithItem("ebonstone", () -> new SpreadingBlock(ISpreadable.Type.CORRUPT, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.TERRACOTTA_PURPLE)));
    public static final DeferredBlock<Block> COBBLED_EBONSTONE = registerWithItem("cobbled_ebonstone", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).mapColor(MapColor.TERRACOTTA_PURPLE)));
    public static final DeferredBlock<SpreadingBlock> EBONSANDSTONE = registerWithItem("ebonsandstone", () -> new SpreadingBlock(ISpreadable.Type.CORRUPT, BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).mapColor(MapColor.TERRACOTTA_PURPLE)));
    public static final DeferredBlock<SpreadingBlock> HARDENED_EBONSAND_BLOCK = registerWithItem("hardened_ebonsand_block", () -> new SpreadingBlock(ISpreadable.Type.CORRUPT, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.TERRACOTTA_PURPLE)));
    public static final DeferredBlock<SpreadingSandBlock> EBONSAND = registerWithItem("ebonsand", () -> new SpreadingSandBlock(ISpreadable.Type.CORRUPT, 0x372B4B, BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.COLOR_BLACK)));
    public static final DeferredBlock<SpreadableMoistenedSandBlock> MOISTENED_EBONSAND_BLOCK = registerWithItem("moistened_ebonsand_block", () -> new SpreadableMoistenedSandBlock(ISpreadable.Type.CORRUPT, BlockBehaviour.Properties.ofFullCopy(Blocks.MUD).mapColor(MapColor.COLOR_BLACK), EBONSAND));
    public static final DeferredBlock<SpreadingIceBlock> PURPLE_ICE = registerWithItem("purple_ice", () -> new SpreadingIceBlock(ISpreadable.Type.CORRUPT, BlockBehaviour.Properties.ofFullCopy(Blocks.ICE).mapColor(MapColor.COLOR_PURPLE)));
    public static final DeferredBlock<SpreadingIceBlock> PURPLE_PACKED_ICE = registerWithItem("purple_packed_ice", () -> new SpreadingIceBlock(ISpreadable.Type.CORRUPT, BlockBehaviour.Properties.ofFullCopy(Blocks.PACKED_ICE).mapColor(MapColor.COLOR_PURPLE)));
    public static final DeferredBlock<SandLayerBlock> EBONSAND_LAYER_BLOCK = registerWithItem("ebonsand_layer_block", SandLayerBlock::new);
    public static final DeferredBlock<MushroomBlock> VILE_MUSHROOM = registerWithoutItem("vile_mushroom", () -> new MushroomBlock(ISpreadable.Type.CORRUPT, CORRUPT_GRASS_BLOCK.get())); // 魔菇
    public static final DeferredBlock<SpreadingThornBlock> CORRUPTION_THORN = registerWithItem("corruption_thorn", () -> new SpreadingThornBlock(2, CORRUPT_GRASS_BLOCK.get(), ISpreadable.Type.CORRUPT));
    public static final DeferredBlock<BasePlantBlock> CORRUPT_GRASS = registerWithItem("corrupt_grass", () -> new BasePlantBlock(CORRUPT_GRASS_BLOCK.get())); // 腐化草
    public static final DeferredBlock<JungleGrassBlock> CORRUPT_JUNGLE_GRASS_BLOCK = registerWithItem("corrupt_jungle_grass_block", () -> new JungleGrassBlock(ISpreadable.Type.CORRUPT, BlockBehaviour.Properties.ofFullCopy(Blocks.MUD).mapColor(MapColor.COLOR_PURPLE))); // 腐化丛林草
    public static final DeferredBlock<ShadowOrbBlock> SHADOW_ORB = registerWithoutItem("shadow_orb", ShadowOrbBlock::new);
    public static final DeferredBlock<EvilCactusBlock> CORRUPT_CACTUS = registerWithItem("corrupt_cactus", () -> new EvilCactusBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CACTUS).mapColor(MapColor.COLOR_CYAN)));
    public static final DeferredBlock<TaperedTwoPartBlock> CORRUPT_TAPERED_BLOCK = registerWithItem("corrupt_tapered_block", TaperedTwoPartBlock::new);

    // 神圣
    public static final DeferredBlock<SpreadingGrassBlock> HALLOW_GRASS_BLOCK = registerWithItem("hallow_grass_block", () -> new SpreadingGrassBlock(ISpreadable.Type.HALLOW, BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK).mapColor(MapColor.COLOR_CYAN)));
    public static final DeferredBlock<BasePlantBlock> HALLOW_GRASS = registerWithItem("hallow_grass", () -> new BasePlantBlock(HALLOW_GRASS_BLOCK.get())); // 神圣草
    public static final LogBlockSet PEARL_LOG_BLOCKS = LogBlockSet.builder("pearl", true, PEARL).sapling(properties -> new BaseSaplingBlock(ModFeatures.TreeGrowers.PEARL_GROWER, properties, null, HALLOW_GRASS_BLOCK)).build();
    public static final DeferredBlock<PearlstoneBlock> PEARLSTONE = registerWithItem("pearlstone", () -> new PearlstoneBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.TERRACOTTA_MAGENTA).strength(8.0F)));
    public static final DeferredBlock<Block> COBBLED_PEARLSTONE = registerWithItem("cobbled_pearlstone", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).mapColor(MapColor.TERRACOTTA_MAGENTA)));
    public static final DeferredBlock<PearlstoneBlock> HARDENED_PEARLSAND_BLOCK = registerWithItem("hardened_pearlsand_block", () -> new PearlstoneBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.TERRACOTTA_PINK)));
    public static final DeferredBlock<PearlstoneBlock> PEARLSANDSTONE = registerWithItem("pearlsandstone", () -> new PearlstoneBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).mapColor(MapColor.TERRACOTTA_PINK).strength(8.0F)));
    public static final DeferredBlock<PearlsandBlock> PEARLSAND = registerWithItem("pearlsand", () -> new PearlsandBlock(0xEDD5F6, BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.TERRACOTTA_PINK)));
    public static final DeferredBlock<MoistenedPearlsandBlock> MOISTENED_PEARLSAND_BLOCK = registerWithItem("moistened_pearlsand_block", () -> new MoistenedPearlsandBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD).mapColor(MapColor.TERRACOTTA_PINK)));
    public static final DeferredBlock<SandLayerBlock> PEARLSAND_LAYER_BLOCK = registerWithItem("pearlsand_layer_block", SandLayerBlock::new);
    public static final DeferredBlock<SpreadingIceBlock> PINK_ICE = registerWithItem("pink_ice", () -> new SpreadingIceBlock(ISpreadable.Type.HALLOW, BlockBehaviour.Properties.ofFullCopy(Blocks.ICE).mapColor(MapColor.COLOR_PINK)));
    public static final DeferredBlock<PinkPackedIceBlock> PINK_PACKED_ICE = registerWithItem("pink_packed_ice", () -> new PinkPackedIceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PACKED_ICE).mapColor(MapColor.COLOR_PINK).randomTicks()));
    public static final DeferredBlock<AmethystClusterBlock> CRYSTAL_SHARDS = registerWithoutItem("crystal_shards", () -> new AmethystClusterBlock(7.0F, 3.0F, BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_CLUSTER).mapColor(MapColor.COLOR_PINK).lightLevel(state -> 7)));
    public static final DeferredBlock<AmethystClusterBlock> GELATIN_CRYSTAL = registerWithoutItem("gelatin_crystal", () -> new AmethystClusterBlock(7.0F, 3.0F, BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_CLUSTER).mapColor(MapColor.COLOR_PINK).lightLevel(state -> 9)));
    public static final DeferredBlock<EvilCactusBlock> HALLOW_CACTUS = registerWithItem("hallow_cactus", () -> new EvilCactusBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CACTUS).mapColor(MapColor.COLOR_CYAN)));
    public static final DeferredBlock<TaperedTwoPartBlock> HALLOW_TAPERED_BLOCK = registerWithItem("hallow_tapered_block", TaperedTwoPartBlock::new);


    // 猩红
    public static final DeferredBlock<SpreadingGrassBlock> CRIMSON_GRASS_BLOCK = registerWithItem("crimson_grass_block", () -> new SpreadingGrassBlock(ISpreadable.Type.CRIMSON, BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK).mapColor(MapColor.COLOR_RED)));
    public static final LogBlockSet SHADOW_LOG_BLOCKS = LogBlockSet.builder("shadow", true, SHADOW).sapling(properties -> new BaseSaplingBlock(ModFeatures.TreeGrowers.SHADOW_GROWER, properties, null, CRIMSON_GRASS_BLOCK)).build();
    public static final DeferredBlock<SpreadingBlock> CRIMSTONE = registerWithItem("crimstone", () -> new SpreadingBlock(ISpreadable.Type.CRIMSON, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.COLOR_RED)));
    public static final DeferredBlock<Block> COBBLED_CRIMSTONE = registerWithItem("cobbled_crimstone", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).mapColor(MapColor.COLOR_RED)));
    public static final DeferredBlock<SpreadingBlock> HARDENED_CRIMSAND_BLOCK = registerWithItem("hardened_crimsand_block", () -> new SpreadingBlock(ISpreadable.Type.CRIMSON, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.TERRACOTTA_RED)));
    public static final DeferredBlock<SpreadingBlock> CRIMSANDSTONE = registerWithItem("crimsandstone", () -> new SpreadingBlock(ISpreadable.Type.CRIMSON, BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).mapColor(MapColor.TERRACOTTA_RED)));
    public static final DeferredBlock<SpreadingSandBlock> CRIMSAND = registerWithItem("crimsand", () -> new SpreadingSandBlock(ISpreadable.Type.CRIMSON, 0x5313E0, BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.COLOR_RED)));
    public static final DeferredBlock<SpreadableMoistenedSandBlock> MOISTENED_CRIMSAND_BLOCK = registerWithItem("moistened_crimsand_block", () -> new SpreadableMoistenedSandBlock(ISpreadable.Type.CRIMSON, BlockBehaviour.Properties.ofFullCopy(Blocks.MUD).mapColor(MapColor.COLOR_RED), CRIMSAND));
    public static final DeferredBlock<MushroomBlock> VICIOUS_MUSHROOM = registerWithoutItem("vicious_mushroom", () -> new MushroomBlock(ISpreadable.Type.CRIMSON, CRIMSON_GRASS_BLOCK.get())); // 毒蘑菇
    public static final DeferredBlock<SpreadingThornBlock> CRIMSON_THORN = registerWithItem("crimson_thorn", () -> new SpreadingThornBlock(2, CRIMSON_GRASS_BLOCK.get(), ISpreadable.Type.CRIMSON));
    public static final DeferredBlock<BasePlantBlock> CRIMSON_GRASS = registerWithItem("crimson_grass", () -> new BasePlantBlock(CRIMSON_GRASS_BLOCK.get())); // 猩红草
    public static final DeferredBlock<SandLayerBlock> CRIMSAND_LAYER_BLOCK = registerWithItem("crimsand_layer_block", SandLayerBlock::new);
    public static final DeferredBlock<JungleGrassBlock> CRIMSON_JUNGLE_GRASS_BLOCK = registerWithItem("crimson_jungle_grass_block", () -> new JungleGrassBlock(ISpreadable.Type.CRIMSON, BlockBehaviour.Properties.ofFullCopy(Blocks.MUD))); // 腐化丛林草
    public static final DeferredBlock<SpreadingIceBlock> RED_ICE = registerWithItem("red_ice", () -> new SpreadingIceBlock(ISpreadable.Type.CRIMSON, BlockBehaviour.Properties.ofFullCopy(Blocks.ICE).mapColor(MapColor.COLOR_RED)));
    public static final DeferredBlock<SpreadingIceBlock> RED_PACKED_ICE = registerWithItem("red_packed_ice", () -> new SpreadingIceBlock(ISpreadable.Type.CRIMSON, BlockBehaviour.Properties.ofFullCopy(Blocks.PACKED_ICE).mapColor(MapColor.COLOR_RED)));
    public static final DeferredBlock<CrimsonHeartBlock> CRIMSON_HEART = registerWithoutItem("crimson_heart", CrimsonHeartBlock::new);
    public static final DeferredBlock<EvilCactusBlock> CRIMSON_CACTUS = registerWithItem("crimson_cactus", () -> new EvilCactusBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CACTUS).mapColor(MapColor.COLOR_RED)));
    public static final DeferredBlock<TaperedTwoPartBlock> CRIMSON_TAPERED_BLOCK = registerWithItem("crimson_tapered_block", TaperedTwoPartBlock::new);

    // 蘑菇地
    public static final DeferredBlock<MushroomGrassBlock> MUSHROOM_GRASS_BLOCK = registerWithItem("mushroom_grass_block", MushroomGrassBlock::new);
    public static final DeferredBlock<MushroomBlock> GLOWING_MUSHROOM = registerWithoutItem("glowing_mushroom", () -> new MushroomBlock(ISpreadable.Type.GLOWING, MUSHROOM_GRASS_BLOCK.get())); // 发光蘑菇
    public static final DeferredBlock<IndusiumBlock> GLOWING_MUSHROOM_INDUSIUM_BLOCK = registerWithItem("glowing_mushroom_indusium_block", IndusiumBlock::new);
    public static final DeferredBlock<RotatedPillarBlock> GLOWING_MUSHROOM_STEM_BLOCK = registerWithItem("glowing_mushroom_stem_block", () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUSHROOM_STEM)));
    public static final DeferredBlock<GlowingMushroomPileusBlock> GLOWING_MUSHROOM_PILEUS_BLOCK = registerWithItem("glowing_mushroom_pileus_block", () -> new GlowingMushroomPileusBlock(4, BlockBehaviour.Properties.ofFullCopy(Blocks.MUSHROOM_STEM)));

    public static final DeferredBlock<IndusiumBlock> LIFE_MUSHROOM_INDUSIUM_BLOCK = registerWithItem("life_mushroom_indusium_block", IndusiumBlock::new);
    public static final DeferredBlock<RotatedPillarBlock> LIFE_MUSHROOM_STEM_BLOCK = registerWithItem("life_mushroom_stem_block", () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUSHROOM_STEM)));
    public static final DeferredBlock<GlowingMushroomPileusBlock> LIFE_MUSHROOM_PILEUS_BLOCK = registerWithItem("life_mushroom_pileus_block", () -> new GlowingMushroomPileusBlock(0, BlockBehaviour.Properties.ofFullCopy(Blocks.MUSHROOM_STEM)));


    public static final DeferredBlock<Block> HANGING_MYCELIUM = registerWithItem("hanging_mycelium", () -> new HangingMyceliumBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.DIRT)
            .replaceable()
            .noCollission()
            .instabreak()
            .sound(SoundType.HANGING_ROOTS)
            .offsetType(BlockBehaviour.OffsetType.XZ)
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<MycelialDirtBlock> MYCELIAL_DIRT = registerWithItem("mycelial_dirt", () -> new MycelialDirtBlock(BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).strength(0.5F).sound(SoundType.ROOTED_DIRT)));

    public static final LogBlockSet GLOWING_MUSHROOM_LOG_BLOCKS = LogBlockSet.builder("glowing_mushroom", true, LogBlockSet.WoodSetType.GLOWING_MUSHROOM).log(null).strippedLog(null).wood(null).strippedWood(null).leaves(null).build();

    // 沙漠
    public static final LogBlockSet PALM_LOG_BLOCKS = LogBlockSet.builder("palm", true, PALM).leaves(PalmLeaves::new).sapling(properties -> new BaseSaplingBlock(ModFeatures.TreeGrowers.PALM_GROWER, properties, null, getSupplier(Blocks.SAND), getSupplier(Blocks.RED_SAND), getSupplier(Blocks.GRASS_BLOCK), NatureBlocks.MOISTENED_SAND_BLOCK, NatureBlocks.MOISTENED_RED_SAND_BLOCK)).build();
    public static final DeferredBlock<TaperedTwoPartBlock> DESERT_TAPERED_BLOCK = registerWithItem("desert_tapered_block", TaperedTwoPartBlock::new);
    public static final DeferredBlock<DesertPlantBlock> SMALL_DESERT_PLANT = registerWithItem("small_desert_plant", () -> new DesertPlantBlock(Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D)));
    public static final DeferredBlock<DesertPlantBlock> BIG_DESERT_PLANT = registerWithItem("big_desert_plant", () -> new DesertPlantBlock(Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D)));
    public static final DeferredBlock<SmallCactusBlock> SMALL_CACTUS = registerWithItem("small_cactus", SmallCactusBlock::new);
    public static final DeferredBlock<BasePlantBlock> DESERT_GRASS = registerWithItem("desert_grass", () -> new BasePlantBlock(Blocks.SAND, Blocks.RED_SAND));
    public static final DeferredBlock<BaseTallPlantBlock> DESERT_TALL_GRASS = registerWithItem("desert_tall_grass", () -> new BaseTallPlantBlock(Blocks.SAND, Blocks.RED_SAND));
    public static final DeferredBlock<Block> PACKED_DIRT = registerWithItem("packed_dirt", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).strength(1.0F, 1.0F).sound(SoundType.STONE).mapColor(MapColor.COLOR_BROWN)));

    // 萨瓦纳草原
    public static final LogBlockSet BAOBAB_LOG_BLOCKS = LogBlockSet.builder("baobab", true, BAOBAB).sapling(properties -> new BaseSaplingBlock(ModFeatures.TreeGrowers.BAOBAB_GROWER, properties, null, getSupplier(Blocks.GRASS_BLOCK), getSupplier(Blocks.DIRT))).build();

    // 黑森林
    public static final DeferredBlock<WhitePumpkinBlock> WHITE_PUMPKIN = registerWithItem("white_pumpkin", () -> new WhitePumpkinBlock(
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_WHITE)
                    .strength(1.0F)
                    .sound(SoundType.WOOD)
                    .pushReaction(PushReaction.DESTROY)
    ));
    public static final DeferredBlock<Block> WHITE_PUMPKIN_STEM = registerWithoutItem("white_pumpkin_stem", () -> new StemBlock(
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
    public static final DeferredBlock<Block> ATTACHED_WHITE_PUMPKIN_STEM = registerWithoutItem("attached_white_pumpkin_stem", () -> new AttachedStemBlock(
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
    public static final LogBlockSet YELLOW_WILLOW_LOG_BLOCKS = LogBlockSet.builder("yellow_willow", true, YELLOW_WILLOW).sapling(properties -> new BaseSaplingBlock(ModFeatures.TreeGrowers.YELLOW_WILLOW_GROWER, properties, null, getSupplier(Blocks.GRASS_BLOCK), getSupplier(Blocks.DIRT))).build();
    // 万圣节
    public static final LogBlockSet SPOOKY_LOG_BLOCKS = LogBlockSet.builder("spooky", true, SPOOKY).build();
    // 生命树
    public static final LogBlockSet LIVING_LOG_BLOCKS = LogBlockSet.builder("living", true, LIVING).leaves(properties -> new TransparentLeavesBlock(properties.noOcclusion())).build();
    // 生命红木
    public static final LogBlockSet LIVING_MAHOGANY_LOG_BLOCKS = LogBlockSet.builder("living_mahogany", true, LIVING_MAHOGANY).leaves(properties -> new TransparentLeavesBlock(properties.noOcclusion())).build();
    // 灰烬
    public static final DeferredBlock<Block> ASH_BLOCK = registerWithItem("ash_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).strength(1.0F, 1.0F).sound(SoundType.SAND).mapColor(MapColor.TERRACOTTA_GRAY)));
    public static final DeferredBlock<AshGrassBlock> ASH_GRASS_BLOCK = registerWithItem("ash_grass_block", AshGrassBlock::new);
    public static final LogBlockSet ASH_LOG_BLOCKS = LogBlockSet.builder("ash", false, ASH).leaves(null).sapling(properties -> new BaseSaplingBlock(ModFeatures.TreeGrowers.ASH_GROWER, properties, null, ASH_BLOCK, ASH_GRASS_BLOCK)).build();
    public static final DeferredBlock<BasePlantBlock> ASH_GRASS = registerWithItem("ash_grass", () -> new BasePlantBlock(ASH_GRASS_BLOCK.get()));

    // 丛林
    public static final DeferredBlock<JungleGrassBlock> JUNGLE_GRASS_BLOCK = registerWithItem("jungle_grass_block", () -> new JungleGrassBlock(ISpreadable.Type.JUNGLE, BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK).mapColor(MapColor.COLOR_GREEN)));
    public static final DeferredBlock<ThornBlock> JUNGLE_THORN = registerWithItem("jungle_thorn", () -> new ThornBlock(3.4f, Blocks.MOSS_BLOCK));
    public static final DeferredBlock<ThornBlock> PLANTERA_THORN = registerWithItem("plantera_thorn", () -> new ThornBlock(20, Blocks.MUD));
    public static final DeferredBlock<JungleSporeBlock> JUNGLE_SPORE = registerWithoutItem("jungle_spore", JungleSporeBlock::new);
    public static final DeferredBlock<NaturesGiftBlock> NATURES_GIFT = registerWithoutItem("natures_gift", () -> new NaturesGiftBlock(NaturesGiftBlock.BlockItemType.NATURES_GIFT));
    public static final DeferredBlock<JungleHiveBlock> JUNGLE_HIVE_BLOCK = registerWithItem("jungle_hive_block", JungleHiveBlock::new);
    public static final DeferredBlock<NaturesGiftBlock> JUNGLE_ROSE = registerWithItem("jungle_rose", () -> new NaturesGiftBlock(NaturesGiftBlock.BlockItemType.JUNGLE_ROSE));
    public static final DeferredBlock<LarvaBlock> LARVA = registerWithItem("larva", () -> new LarvaBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.DESTROY).noOcclusion().replaceable().instabreak()));
    public static final DeferredBlock<LifeFruitBlock> LIFE_FRUIT = registerWithoutItem("life_fruit", LifeFruitBlock::new);

    public static final DeferredBlock<MudPathBlock> JUNGLE_PATH = registerWithItem("jungle_path", () -> new MudPathBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD).mapColor(MapColor.COLOR_LIGHT_GRAY)));
    public static final DeferredBlock<MudPathBlock> MUSHROOM_PATH = registerWithItem("mushroom_path", () -> new MudPathBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD).mapColor(MapColor.COLOR_LIGHT_GRAY)));
    public static final DeferredBlock<AshPathBlock> ASH_PATH = registerWithItem("ash_path", () -> new AshPathBlock(BlockBehaviour.Properties.ofFullCopy(NatureBlocks.ASH_BLOCK.get()).mapColor(MapColor.COLOR_GRAY)));

    // 末地 - 通用
    public static final DeferredBlock<Block> END_DIRT = registerWithItem("end_dirt", EndDirtBlock::new);
    public static final DeferredBlock<VoidWeaveBlock> VOID_WEAVE = registerWithItem("void_weave", () -> new VoidWeaveBlock(BlockBehaviour.Properties.ofFullCopy(GLASS).mapColor(MapColor.COLOR_PURPLE)));

    // 末地 - 紫颂主题
    public static final DeferredBlock<Block> VOID_GRASS_BLOCK = registerWithItem("void_grass_block", () -> new EndGrassBlock(NatureBlocks.END_DIRT));
    public static final DeferredBlock<BasePlantBlock> VOID_GRASS = registerWithItem("void_grass", () -> new BasePlantBlock(VOID_GRASS_BLOCK.get()));
    public static final DeferredBlock<BasePlantBlock> VOID_VIOLET = registerWithItem("void_violet", () -> new BasePlantBlock(VOID_GRASS_BLOCK.get()));
    public static final DeferredBlock<BaseTallPlantBlock> TALL_VOID_GRASS = registerWithItem("tall_void_grass", () -> new BaseTallPlantBlock(VOID_GRASS_BLOCK.get()));
    public static final DeferredBlock<VoidTreeRootBlock> VOID_TREE_ROOT_BLOCK = registerWithItem("void_tree_root_block", VoidTreeRootBlock::new);
    public static final Supplier<BlockEntityType<VoidTreeRootBlock.BEntity>> VOID_TREE_ROOT_BLOCK_ENTITY = ModBlocks.BLOCK_ENTITIES.register("void_tree_root_block", () -> BlockEntityType.Builder.of(VoidTreeRootBlock.BEntity::new, VOID_TREE_ROOT_BLOCK.get()).build(DSL.remainderType()));
    public static final LogBlockSet VOID_LOG_BLOCKS = LogBlockSet.builder("void", true, VOID).sapling(properties -> new BaseSaplingBlock(ModFeatures.TreeGrowers.VOID_GROWER, properties, ModTags.Blocks.VOID_TREE_CAN_SURVIVE, VOID_GRASS_BLOCK, END_DIRT)).build();

    // 末地 - 倒悬主题
    public static final DeferredBlock<Block> INVERSE_GRASS_BLOCK = registerWithItem("inverse_grass_block", () -> new EndGrassBlock(() -> Blocks.END_STONE));
    public static final LogBlockSet GAZE_LOG_BLOCKS = LogBlockSet.builder("gaze", true, GAZE).build();
    public static final DeferredBlock<Block> INVERSE_TUBER = registerWithItem("inverse_tuber", () -> new CandyBlock(BlockBehaviour.Properties.ofFullCopy(STONE_BRICKS).mapColor(MapColor.COLOR_GREEN)));
    // 末地 - 月光主题
    public static final DeferredBlock<Block> MOONLIT_GRASS_BLOCK = registerWithItem("moonlit_grass_block", () -> new EndGrassBlock(() -> Blocks.END_STONE));
    public static final LogBlockSet MOONGLOW_WILLOW_LOG_BLOCKS = LogBlockSet.builder("moonglow_willow", true, MOONGLOW_WILLOW).build();

    // 地獄 - 黯虛主題
    public static final DeferredBlock<Block> GLOOM_OBSIDIAN = registerWithItem("gloom_obsidian", () -> new Block(BlockBehaviour.Properties.ofFullCopy(OBSIDIAN)));

    // 王朝木
    public static final LogBlockSet DYNASTY_LOG_BLOCKS = LogBlockSet.builder("dynasty", true, DYNASTY).leaves(null).build();

    // 松树
    public static final LogBlockSet PINE_LOG_BLOCKS = LogBlockSet.builder("pine", true, PINE).build();
    public static final DeferredBlock<PineSaplingBlock> PINE_SAPLING = registerWithItem("pine_sapling", () -> new PineSaplingBlock(ModFeatures.TreeGrowers.PINE_GROWER, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)));
    public static final DeferredBlock<PineSaplingBlock> PRUNED_PINE_SAPLING = registerWithItem("pruned_pine_sapling", () -> new PineSaplingBlock(ModFeatures.TreeGrowers.CHINESE_PINE_GROWER, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)));

    // 仙灵木
    public static final LogBlockSet FEY_LOG_BLOCKS = LogBlockSet.builder("fey", true, FEY).leaves(null).build();
    // 空岛
    public static final DeferredBlock<CloudBlock> CLOUD_BLOCK = registerWithItem("cloud_block", () -> new CloudBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.TERRACOTTA_WHITE)
            .mapColor(MapColor.SNOW)
            .strength(0.3F)
            .sound(SoundType.SNOW)
            .noOcclusion()
            .isValidSpawn(Blocks::never)
            .isRedstoneConductor((blockState, blockGetter, blockPos) -> false)
            .isSuffocating((blockState, blockGetter, blockPos) -> false)));
    public static final DeferredBlock<EvaporativeCloudBlock> EVAPORATIVE_CLOUD_BLOCK = registerWithItem("evaporative_cloud_block", () -> new EvaporativeCloudBlock(BlockBehaviour.Properties.of()
            .sound(SoundType.SNOW)
            .noOcclusion()
            .randomTicks()));
    public static final Supplier<BlockEntityType<EvaporativeCloudBlock.BEntity>> EVAPORATIVE_CLOUD_BLOCK_ENTITY = ModBlocks.BLOCK_ENTITIES.register("evaporative_cloud_block", () -> BlockEntityType.Builder.of(EvaporativeCloudBlock.BEntity::new, EVAPORATIVE_CLOUD_BLOCK.get()).build(DSL.remainderType()));
    public static final DeferredBlock<ParticleCloudBlock> RAIN_CLOUD_BLOCK = registerWithItem("rain_cloud_block", () -> new ParticleCloudBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_GRAY)
            .strength(0.3F)
            .sound(SoundType.SNOW)
            .noOcclusion()
            .isValidSpawn(Blocks::never)
            .isRedstoneConductor((blockState, blockGetter, blockPos) -> false)
            .isSuffocating((blockState, blockGetter, blockPos) -> false),
            ParticleTypes.FALLING_WATER));
    public static final DeferredBlock<ParticleCloudBlock> SNOW_CLOUD_BLOCK = registerWithItem("snow_cloud_block", () -> new ParticleCloudBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_GRAY)
            .strength(0.3F)
            .sound(SoundType.SNOW)
            .noOcclusion()
            .isValidSpawn(Blocks::never)
            .isRedstoneConductor((blockState, blockGetter, blockPos) -> false)
            .isSuffocating((blockState, blockGetter, blockPos) -> false),
            ParticleTypes.SNOWFLAKE));
    public static final DeferredBlock<StellarBlossomBlock> STELLAR_BLOSSOM = registerWithItem("stellar_blossom", () -> new StellarBlossomBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION).offsetType(BlockBehaviour.OffsetType.NONE)));
    public static final DeferredBlock<CloudWeaverBlock> CLOUDWEAVER = registerWithItem("cloudweaver", () -> new CloudWeaverBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION).offsetType(BlockBehaviour.OffsetType.NONE)));
    public static final DeferredBlock<FloatingWheatBlock> FLOATING_WHEAT = registerWithItem("floating_wheat", () -> new FloatingWheatBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).offsetType(BlockBehaviour.OffsetType.NONE)));

    public static final DeferredBlock<BalloonMelonBlock> BALLOON_MELON = registerWithItem("balloon_melon", BalloonMelonBlock::new);
    public static final DeferredBlock<BalloonAttachedStemBlock> BALLOON_ATTACHED_STEM = registerWithItem("balloon_attached_stem", () -> new BalloonAttachedStemBlock(
            BALLOON_MELON.getKey(),
            Confluence.asResourceKey(Registries.BLOCK,"balloon_stem"),
            FoodItems.BALLOON_SEED.getKey(),
            BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY)
    ));
    public static final DeferredBlock<BalloonStemBlock> BALLOON_STEM = registerWithItem("balloon_stem", () -> new BalloonStemBlock(
            BALLOON_MELON.getKey(),
            BALLOON_ATTACHED_STEM.getKey(),
            FoodItems.BALLOON_SEED.getKey()
    ));

    // 枝杈
    public static final DeferredBlock<BranchesBlock> AMBER_BRANCHES = registerWithItem("amber_branches", () -> new BranchesBlock(ModTags.Blocks.JEWELLERY_BRANCHES_ATTACHABLE, BlockTags.BASE_STONE_OVERWORLD));
    public static final DeferredBlock<BranchesBlock> RUBY_BRANCHES = registerWithItem("ruby_branches", () -> new BranchesBlock(ModTags.Blocks.JEWELLERY_BRANCHES_ATTACHABLE, BlockTags.BASE_STONE_OVERWORLD));
    public static final DeferredBlock<BranchesBlock> TOPAZ_BRANCHES = registerWithItem("topaz_branches", () -> new BranchesBlock(ModTags.Blocks.JEWELLERY_BRANCHES_ATTACHABLE, BlockTags.BASE_STONE_OVERWORLD));
    public static final DeferredBlock<BranchesBlock> JADE_BRANCHES = registerWithItem("jade_branches", () -> new BranchesBlock(ModTags.Blocks.JEWELLERY_BRANCHES_ATTACHABLE, BlockTags.BASE_STONE_OVERWORLD));
    public static final DeferredBlock<BranchesBlock> DIAMOND_BRANCHES = registerWithItem("diamond_branches", () -> new BranchesBlock(ModTags.Blocks.JEWELLERY_BRANCHES_ATTACHABLE, BlockTags.BASE_STONE_OVERWORLD));
    public static final DeferredBlock<BranchesBlock> SAPPHIRE_BRANCHES = registerWithItem("sapphire_branches", () -> new BranchesBlock(ModTags.Blocks.JEWELLERY_BRANCHES_ATTACHABLE, BlockTags.BASE_STONE_OVERWORLD));
    public static final DeferredBlock<BranchesBlock> AMETHYST_BRANCHES = registerWithItem("amethyst_branches", () -> new BranchesBlock(ModTags.Blocks.JEWELLERY_BRANCHES_ATTACHABLE, BlockTags.BASE_STONE_OVERWORLD));
    public static final DeferredBlock<BranchesBlock> ASH_BRANCHES = registerWithItem("ash_branches", () -> new BranchesBlock(ModTags.Blocks.ASH_LOG_BRANCHES_ATTACHABLE, ModTags.Blocks.ASH_LOG_BRANCHES_ATTACHABLE));

    // 藤蔓方块
    public static final DeferredBlock<BaseDroopingPlantsHeadBlock> YELLOW_WILLOW_DROOPING_LEAVES = registerWithItem("yellow_willow_drooping_leaves", () -> new BaseDroopingPlantsHeadBlock(14, false, false, () -> List.of(NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.LEAVES.get())));
    public static final DeferredBlock<BaseDroopingPlantsHeadBlock> GLOWING_MUSHROOM_VINE = registerWithItem("glowing_mushroom_vine", () -> new BaseDroopingPlantsHeadBlock(6, true, true));
    public static final DeferredBlock<BaseDroopingPlantsHeadBlock> FOREST_DROOPING_VINE = registerWithItem("forest_drooping_vine", () -> new BaseDroopingPlantsHeadBlock(10, true, true));
    public static final DeferredBlock<BaseDroopingPlantsHeadBlock> JUNGLE_DROOPING_VINE = registerWithItem("jungle_drooping_vine", () -> new BaseDroopingPlantsHeadBlock(10, true, true));
    public static final DeferredBlock<BaseDroopingPlantsHeadBlock> CORRUPT_DROOPING_VINE = registerWithItem("corrupt_drooping_vine", () -> new BaseDroopingPlantsHeadBlock(10, true, true));
    public static final DeferredBlock<BaseDroopingPlantsHeadBlock> CRIMSON_DROOPING_VINE = registerWithItem("crimson_drooping_vine", () -> new BaseDroopingPlantsHeadBlock(10, true, true));
    public static final DeferredBlock<BaseDroopingPlantsHeadBlock> HALLOW_DROOPING_VINE = registerWithItem("hallow_drooping_vine", () -> new BaseDroopingPlantsHeadBlock(10, true, true));
    public static final DeferredBlock<BaseDroopingPlantsHeadBlock> PINE_DROOPING_VINE = registerWithItem("pine_drooping_vine", () -> new BaseDroopingPlantsHeadBlock(8, false, true, () -> List.of(NatureBlocks.PINE_LOG_BLOCKS.LEAVES.get(), NatureBlocks.PINE_LOG_BLOCKS.LOG.get())));
    public static final DeferredBlock<BaseDroopingPlantsHeadBlock> SILENT_DROOPING_VINE = registerWithItem("silent_drooping_vine", () -> new BaseDroopingPlantsHeadBlock(10, false, true, () -> List.of(
            Blocks.END_STONE,
            VOID_GRASS_BLOCK.get(),
            END_DIRT.get(),
            MOONLIT_GRASS_BLOCK.get(),
            INVERSE_GRASS_BLOCK.get()
    )));

    //微光环境物块
    public static final DeferredBlock<ShimmerDroopingVinesBlock> SHIMMER_DROOPING_VINE = registerWithoutItem("shimmer_drooping_vine", () -> new ShimmerDroopingVinesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAVE_VINES)));
    public static final DeferredBlock<ShimmerDroopingVinesPlantBlock> SHIMMER_DROOPING_VINE_PLANT = registerWithoutItem("shimmer_drooping_vine_plant", () -> new ShimmerDroopingVinesPlantBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAVE_VINES_PLANT)));
    public static final DeferredBlock<ShimmerRiceBlock> SHIMMER_RICE = registerWithItem("shimmer_rice", () -> new ShimmerRiceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT)));
    public static final DeferredBlock<ShimmerCoralTubeBlock> SHIMMER_CORAL_TUBE = registerWithItem("shimmer_coral_tube", () -> new ShimmerCoralTubeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).instabreak().pushReaction(PushReaction.DESTROY).sound(SoundType.WET_GRASS)));

    public static final DeferredBlock<BlinkingRoyalShimmerlilyBlock> BLINKING_ROYAL_SHIMMERLILY = registerWithItem("blinking_royal_shimmerlily", () -> new BlinkingRoyalShimmerlilyBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LILY_PAD)), block -> new PlaceOnWaterBlockItem(block, new Item.Properties()));

    // 苔藓
    public static final DeferredBlock<BaseMossBlock> GREEN_MOSS = registerWithItem("green_moss", () -> new BaseMossBlock(5, BlockBehaviour.Properties.of()
            .noCollission()
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .strength(0.2F)
            .sound(SoundType.GLOW_LICHEN)
            .replaceable()));
    public static final DeferredBlock<BaseMossBlock> BROWN_MOSS = registerWithItem("brown_moss", () -> new BaseMossBlock(5, BlockBehaviour.Properties.of()
            .noCollission()
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .strength(0.2F)
            .sound(SoundType.GLOW_LICHEN)
            .replaceable()));
    public static final DeferredBlock<BaseMossBlock> RED_MOSS = registerWithItem("red_moss", () -> new BaseMossBlock(5, BlockBehaviour.Properties.of()
            .noCollission()
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .strength(0.2F)
            .sound(SoundType.GLOW_LICHEN)
            .replaceable()));
    public static final DeferredBlock<BaseMossBlock> BLUE_MOSS = registerWithItem("blue_moss", () -> new BaseMossBlock(5, BlockBehaviour.Properties.of()
            .noCollission()
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .strength(0.2F)
            .sound(SoundType.GLOW_LICHEN)
            .replaceable()));
    public static final DeferredBlock<BaseMossBlock> PURPLE_MOSS = registerWithItem("purple_moss", () -> new BaseMossBlock(5, BlockBehaviour.Properties.of()
            .noCollission()
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .strength(0.2F)
            .sound(SoundType.GLOW_LICHEN)
            .replaceable()));
    public static final DeferredBlock<BaseMossBlock> LAVA_MOSS = registerWithItem("lava_moss", () -> new BaseMossBlock(10, BlockBehaviour.Properties.of()
            .noCollission()
            .pushReaction(PushReaction.DESTROY)
            .strength(0.2F)
            .sound(SoundType.GLOW_LICHEN)
            .replaceable()));
    public static final DeferredBlock<BaseMossBlock> KRYPTON_MOSS = registerWithItem("krypton_moss", () -> new BaseMossBlock(5, BlockBehaviour.Properties.of()
            .noCollission()
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .strength(0.2F)
            .sound(SoundType.GLOW_LICHEN)
            .replaceable()));
    public static final DeferredBlock<BaseMossBlock> XENON_MOSS = registerWithItem("xenon_moss", () -> new BaseMossBlock(5, BlockBehaviour.Properties.of()
            .noCollission()
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .strength(0.2F)
            .sound(SoundType.GLOW_LICHEN)
            .replaceable()));
    public static final DeferredBlock<BaseMossBlock> ARGON_MOSS = registerWithItem("argon_moss", () -> new BaseMossBlock(5, BlockBehaviour.Properties.of()
            .noCollission()
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .strength(0.2F)
            .sound(SoundType.GLOW_LICHEN)
            .replaceable()));
    public static final DeferredBlock<BaseMossBlock> NEON_MOSS = registerWithItem("neon_moss", () -> new BaseMossBlock(5, BlockBehaviour.Properties.of()
            .noCollission()
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .strength(0.2F)
            .sound(SoundType.GLOW_LICHEN)
            .replaceable()));
    public static final DeferredBlock<BaseMossBlock> HELIUM_MOSS = registerWithItem("helium_moss", () -> new BaseMossBlock(5, BlockBehaviour.Properties.of()
            .noCollission()
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .strength(0.2F)
            .sound(SoundType.GLOW_LICHEN)
            .replaceable()));
    public static final DeferredBlock<BaseMossBlock> GLOWING_MUSHROOM_MOSS = registerWithItem("glowing_mushroom_moss", () -> new BaseMossBlock(5, BlockBehaviour.Properties.of()
            .noCollission()
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .strength(0.2F)
            .sound(SoundType.GLOW_LICHEN)
            .replaceable()));

    public static final DeferredBlock<CrimsonVenusFlytrapBlock> CRIMSON_VENUS_FLYTRAP_BLOCK = registerWithItem("crimson_venus_flytrap_block", CrimsonVenusFlytrapBlock::new);
    public static final DeferredBlock<BloodthirstCrystallizedBlock> BLOODTHIRST_CRYSTALLIZED_BLOCK = registerWithItem("bloodthirst_crystallized_block", BloodthirstCrystallizedBlock::new);
    public static final Supplier<BlockEntityType<BloodthirstCrystallizedBlock.BEntity>> BLOODTHIRST_CRYSTALLIZED_ENTITY = ModBlocks.BLOCK_ENTITIES.register("bloodthirst_crystallized_entity", () -> BlockEntityType.Builder.of(BloodthirstCrystallizedBlock.BEntity::new, BLOODTHIRST_CRYSTALLIZED_BLOCK.get()).build(DSL.remainderType()));
    public static final DeferredBlock<CorrodedWormRootsBlock> CORRODED_WORM_ROOTS_BLOCK = registerWithItem("corroded_worm_roots_block", CorrodedWormRootsBlock::new);
    public static final DeferredBlock<CorruptedOvariesBlock> CORRUPTED_OVARIES_BLOCK = registerWithItem("corrupted_ovaries_block", CorruptedOvariesBlock::new);
    public static final DeferredBlock<DecomposeTheSourceExtractBlock> DECOMPOSE_THE_SOURCE_EXTRACT_BLOCK = registerWithItem("decompose_the_source_extract_block", DecomposeTheSourceExtractBlock::new);
    public static final Supplier<BlockEntityType<DecomposeTheSourceExtractBlock.BEntity>> DECOMPOSE_THE_SOURCE_EXTRACT_ENTITY = ModBlocks.BLOCK_ENTITIES.register("decompose_the_source_extract_entity", () -> BlockEntityType.Builder.of(DecomposeTheSourceExtractBlock.BEntity::new, DECOMPOSE_THE_SOURCE_EXTRACT_BLOCK.get()).build(DSL.remainderType()));
    public static final DeferredBlock<ShimmerCrystalslBlock> SHIMMER_CRYSTALS_BLOCK = registerWithItem("shimmer_crystals_block", ShimmerCrystalslBlock::new);
    public static final DeferredBlock<LostPaperBlock> LOST_PAPER_BLOCK = registerWithItem("lost_paper", LostPaperBlock::new);
    public static final Supplier<BlockEntityType<LostPaperBlock.BEntity>> LOST_PAPER_ENTITY = ModBlocks.BLOCK_ENTITIES.register("lost_paper", () -> BlockEntityType.Builder.of(LostPaperBlock.BEntity::new, LOST_PAPER_BLOCK.get()).build(DSL.remainderType()));


    private static <B extends Block> DeferredBlock<B> registerWithoutItem(String id, Supplier<B> block) {
        return BLOCKS.register(id, block);
    }

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
}
