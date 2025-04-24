package org.confluence.mod.common.init.block;

import com.mojang.datafixers.DSL;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.block.TransparentLeavesBlock;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.natural.MushroomBlock;
import org.confluence.mod.common.block.natural.*;
import org.confluence.mod.common.block.natural.sapling.AshSaplingBlock;
import org.confluence.mod.common.block.natural.sapling.BaseSaplingBlock;
import org.confluence.mod.common.block.natural.sapling.StoneSaplingBlock;
import org.confluence.mod.common.block.natural.spreadable.*;
import org.confluence.mod.common.init.ModFeatures;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.ModItems;

import java.util.function.Function;
import java.util.function.Supplier;

import static org.confluence.mod.common.block.natural.LogBlockSet.WoodSetType.*;

public class NatureBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Confluence.MODID);

    // 非环境树苗
    public static final DeferredBlock<Block> RUBY_SAPLING = registerWithItem("ruby_sapling", () -> new StoneSaplingBlock(ModFeatures.TreeGrowers.RUBY_GROWER));
    public static final DeferredBlock<Block> AMBER_SAPLING = registerWithItem("amber_sapling", () -> new StoneSaplingBlock(ModFeatures.TreeGrowers.AMBER_GROWER));
    public static final DeferredBlock<Block> TOPAZ_SAPLING = registerWithItem("topaz_sapling", () -> new StoneSaplingBlock(ModFeatures.TreeGrowers.TOPAZ_GROWER));
    public static final DeferredBlock<Block> EMERALD_SAPLING = registerWithItem("emerald_sapling", () -> new StoneSaplingBlock(ModFeatures.TreeGrowers.EMERALD_GROWER));
    public static final DeferredBlock<Block> DIAMOND_SAPLING = registerWithItem("diamond_sapling", () -> new StoneSaplingBlock(ModFeatures.TreeGrowers.DIAMOND_GROWER));
    public static final DeferredBlock<Block> SAPPHIRE_SAPLING = registerWithItem("sapphire_sapling", () -> new StoneSaplingBlock(ModFeatures.TreeGrowers.SAPPHIRE_GROWER));
    public static final DeferredBlock<Block> TR_AMETHYST_SAPLING = registerWithItem("tr_amethyst_sapling", () -> new StoneSaplingBlock(ModFeatures.TreeGrowers.TR_AMETHYST_GROWER));
    public static final DeferredBlock<Block> LIVING_SAPLING = registerWithItem("living_sapling", () -> new BaseSaplingBlock(ModFeatures.TreeGrowers.LIVING_GROWER, Blocks.GRASS_BLOCK, Blocks.DIRT));
    public static final DeferredBlock<Block> YELLOW_WILLOW_SAPLING = registerWithItem("yellow_willow_sapling", () -> new BaseSaplingBlock(ModFeatures.TreeGrowers.YELLOW_WILLOW_GROWER, Blocks.GRASS_BLOCK, Blocks.DIRT));
    public static final DeferredBlock<Block> BAOBAB_SAPLING = registerWithItem("baobab_sapling", () -> new BaseSaplingBlock(ModFeatures.TreeGrowers.BAOBAB_GROWER, Blocks.GRASS_BLOCK, Blocks.DIRT));

    // 流体接触块
    public static final DeferredBlock<ThinHoneyBlock> THIN_HONEY_BLOCK = registerWithItem("thin_honey_block", () -> new ThinHoneyBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.HONEY_BLOCK)));
    public static final DeferredBlock<Block> LOOSE_HONEY_BLOCK = registerWithItem("loose_honey_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.HONEY_BLOCK)));
    public static final DeferredBlock<AetheriumBlock> AETHERIUM_BLOCK = registerWithItem("aetherium_block", () -> new AetheriumBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).lightLevel(blockState -> 10)));
    public static final DeferredBlock<Block> DARK_AETHERIUM_BLOCK = registerWithItem("dark_aetherium_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK)));

    //香蒲
    public static final DeferredBlock<CattailsBodyBlock> CATTAILS_BODY = registerWithoutItem("cattails_body", () -> new CattailsBodyBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<CattailsBodyBlock> JUNGLE_CATTAILS_BODY = registerWithoutItem("jungle_cattails_body", () -> new CattailsBodyBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<CattailsBodyBlock> GLOWING_MUSHROOM_CATTAILS_BODY = registerWithoutItem("glowing_mushroom_cattails_body", () -> new CattailsBodyBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<CattailsBodyBlock> HALLOW_CATTAILS_BODY = registerWithoutItem("hallow_cattails_body", () -> new CattailsBodyBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<CattailsBodyBlock> EBONY_CATTAILS_BODY = registerWithoutItem("ebony_cattails_body", () -> new CattailsBodyBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<CattailsBodyBlock> TR_CRIMSON_CATTAILS_BODY = registerWithoutItem("tr_crimson_cattails_body", () -> new CattailsBodyBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<CattailsHeadBlock> CATTAILS_HEAD = registerWithoutItem("cattails_head", () -> new CattailsHeadBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS)));
    public static final DeferredBlock<CattailsHeadBlock> JUNGLE_CATTAILS_HEAD = registerWithoutItem("jungle_cattails_head", () -> new CattailsHeadBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS)));
    public static final DeferredBlock<CattailsHeadBlock> GLOWING_MUSHROOM_CATTAILS_HEAD = registerWithoutItem("glowing_mushroom_cattails_head", () -> new CattailsHeadBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS)));
    public static final DeferredBlock<CattailsHeadBlock> HALLOW_CATTAILS_HEAD = registerWithoutItem("hallow_cattails_head", () -> new CattailsHeadBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS)));
    public static final DeferredBlock<CattailsHeadBlock> EBONY_CATTAILS_HEAD = registerWithoutItem("ebony_cattails_head", () -> new CattailsHeadBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS)));
    public static final DeferredBlock<CattailsHeadBlock> TR_CRIMSON_CATTAILS_HEAD = registerWithoutItem("tr_crimson_cattails_head", () -> new CattailsHeadBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS)));

    // 环境辅助
    public static final DeferredBlock<ThinIceBlock> THIN_ICE_BLOCK = registerWithItem("thin_ice_block", ThinIceBlock::new);
    public static final DeferredBlock<Block> HARDENED_SAND_BLOCK = registerWithItem("hardened_sand_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE)));
    public static final DeferredBlock<Block> MOIST_SAND_BLOCK = registerWithItem("moist_sand_block", () -> new MoistSandBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD), Blocks.SAND));
    public static final DeferredBlock<Block> RED_HARDENED_SAND_BLOCK = registerWithItem("red_hardened_sand_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE)));
    public static final DeferredBlock<Block> RED_MOIST_SAND_BLOCK = registerWithItem("red_moist_sand_block", () -> new MoistSandBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD), Blocks.RED_SAND));
    public static final DeferredBlock<Block> DIATOMACEOUS = registerWithItem("diatomaceous", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SAND)));
    public static final DeferredBlock<SandLayerBlock> SAND_LAYER_BLOCK = registerWithItem("sand_layer_block", SandLayerBlock::new);
    public static final DeferredBlock<SandLayerBlock> RED_SAND_LAYER_BLOCK = registerWithItem("red_sand_layer_block", SandLayerBlock::new);
    public static final DeferredBlock<Block> DESERT_FOSSIL = registerWithItem("desert_fossil", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE)));
    public static final DeferredBlock<Block> SLUSH = registerWithItem("slush", () -> new ColoredFallingBlock(new ColorRGBA(1531531531), BlockBehaviour.Properties.ofFullCopy(Blocks.SAND)));
    public static final DeferredBlock<Block> SILT_BLOCK = registerWithItem("silt_block", () -> new ColoredFallingBlock(new ColorRGBA(1531531531), BlockBehaviour.Properties.ofFullCopy(Blocks.SAND)));
    public static final DeferredBlock<Block> MARINE_GRAVEL = registerWithItem("marine_gravel", () -> new ColoredFallingBlock(new ColorRGBA(1531531531), BlockBehaviour.Properties.ofFullCopy(Blocks.SAND)));
    public static final DeferredBlock<RotatedPillarBlock> STONY_LOG = registerWithItem("stony_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static final DeferredBlock<LifeCrystalBlock> LIFE_CRYSTAL_BLOCK = registerWithItem("life_crystal_block", () -> new LifeCrystalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_CLUSTER).mapColor(MapColor.COLOR_RED).lightLevel(state -> 5)), LifeCrystalBlock.Item::new);
    public static final Supplier<BlockEntityType<LifeCrystalBlock.Entity>> LIFE_CRYSTAL_BLOCK_ENTITY = ModBlocks.BLOCK_ENTITIES.register("life_crystal_block_entity", () -> BlockEntityType.Builder.of(LifeCrystalBlock.Entity::new, LIFE_CRYSTAL_BLOCK.get()).build(null));

    // 腐化
    public static final DeferredBlock<Block> CORRUPT_GRASS_BLOCK = registerWithItem("corrupt_grass_block", () -> new SpreadingGrassBlock(ISpreadable.Type.CORRUPT, BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK).mapColor(MapColor.COLOR_PURPLE)));
    public static final DeferredBlock<Block> EBONY_SAPLING = registerWithItem("ebony_sapling", () -> new BaseSaplingBlock(ModFeatures.TreeGrowers.EBONY_GROWER, CORRUPT_GRASS_BLOCK.get()));
    public static final LogBlockSet EBONY_LOG_BLOCKS = LogBlockSet.builder("ebony", true).createDefault(EBONY, true).build();
    public static final DeferredBlock<Block> EBONY_STONE = registerWithItem("ebony_stone", () -> new SpreadingBlock(ISpreadable.Type.CORRUPT, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static final DeferredBlock<Block> EBONY_COBBLESTONE = registerWithItem("ebony_cobblestone", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE)));
    public static final DeferredBlock<Block> EBONY_SANDSTONE = registerWithItem("ebony_sandstone", () -> new SpreadingBlock(ISpreadable.Type.CORRUPT, BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE)));
    public static final DeferredBlock<Block> EBONY_HARDENED_SAND_BLOCK = registerWithItem("ebony_hardened_sand_block", () -> new SpreadingBlock(ISpreadable.Type.CORRUPT, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static final DeferredBlock<Block> EBONY_SAND = registerWithItem("ebony_sand", () -> new SpreadingSandBlock(ISpreadable.Type.CORRUPT, 0x372B4B, BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.COLOR_BLACK)));
    public static final DeferredBlock<Block> EBONY_MOIST_SAND_BLOCK = registerWithItem("ebony_moist_sand_block", () -> new SpreadableMoistSandBlock(ISpreadable.Type.CORRUPT, BlockBehaviour.Properties.ofFullCopy(Blocks.MUD).mapColor(MapColor.COLOR_BLACK), EBONY_SAND.get()));
    public static final DeferredBlock<Block> PURPLE_ICE = registerWithItem("purple_ice", () -> new IceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ICE)));
    public static final DeferredBlock<Block> PURPLE_PACKED_ICE = registerWithItem("purple_packed_ice", () -> new IceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PACKED_ICE)));
    public static final DeferredBlock<SandLayerBlock> EBONY_SAND_LAYER_BLOCK = registerWithItem("ebony_sand_layer_block", SandLayerBlock::new);
    public static final DeferredBlock<Block> VILE_MUSHROOM = registerWithoutItem("vile_mushroom", () -> new MushroomBlock(ISpreadable.Type.CORRUPT, CORRUPT_GRASS_BLOCK.get())); // 魔菇
    public static final DeferredBlock<ThornBlock> CORRUPTION_THORN = registerWithItem("corruption_thorn", () -> new SpreadingThornBlock(2, CORRUPT_GRASS_BLOCK.get(), ISpreadable.Type.CORRUPT));
    public static final DeferredBlock<Block> CORRUPT_GRASS = registerWithItem("corrupt_grass", () -> new BasePlantBlock(CORRUPT_GRASS_BLOCK.get())); // 腐化草
    public static final DeferredBlock<Block> CORRUPT_JUNGLE_GRASS_BLOCK = registerWithItem("corrupt_jungle_grass_block", () -> new JungleGrassBlock(ISpreadable.Type.CORRUPT, BlockBehaviour.Properties.ofFullCopy(Blocks.MUD))); // 腐化丛林草
    public static final DeferredBlock<ShadowOrbBlock> SHADOW_ORB = registerWithoutItem("shadow_orb", ShadowOrbBlock::new);
    public static final DeferredBlock<CrimsonHeartBlock> CRIMSON_HEART = registerWithoutItem("crimson_heart", CrimsonHeartBlock::new);

    //  神圣
    public static final DeferredBlock<Block> HALLOW_GRASS_BLOCK = registerWithItem("hallow_grass_block", () -> new SpreadingGrassBlock(ISpreadable.Type.HALLOW, BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK).mapColor(MapColor.COLOR_CYAN)));
    public static final DeferredBlock<Block> HALLOW_GRASS = registerWithItem("hallow_grass", () -> new BasePlantBlock(HALLOW_GRASS_BLOCK.get())); // 神圣草
    public static final DeferredBlock<Block> PEARL_SAPLING = registerWithItem("pearl_sapling", () -> new BaseSaplingBlock(ModFeatures.TreeGrowers.PEARL_GROWER, HALLOW_GRASS_BLOCK.get()));
    public static final LogBlockSet PEARL_LOG_BLOCKS = LogBlockSet.builder("pearl", true).createDefault(PEARL, true).build();
    public static final DeferredBlock<Block> PEARL_STONE = registerWithItem("pearl_stone", () -> new SpreadingBlock(ISpreadable.Type.HALLOW, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static final DeferredBlock<Block> PEARL_COBBLESTONE = registerWithItem("pearl_cobblestone", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE)));
    public static final DeferredBlock<Block> PEARL_HARDENED_SAND_BLOCK = registerWithItem("pearl_hardened_sand_block", () -> new SpreadingBlock(ISpreadable.Type.HALLOW, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static final DeferredBlock<Block> PEARL_SANDSTONE = registerWithItem("pearl_sandstone", () -> new SpreadingBlock(ISpreadable.Type.HALLOW, BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE)));
    public static final DeferredBlock<Block> PEARL_SAND = registerWithItem("pearl_sand", () -> new SpreadingSandBlock(ISpreadable.Type.HALLOW, 0xEDD5F6, BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.COLOR_LIGHT_GRAY)));
    public static final DeferredBlock<Block> PEARL_MOIST_SAND_BLOCK = registerWithItem("pearl_moist_sand_block", () -> new SpreadableMoistSandBlock(ISpreadable.Type.HALLOW, BlockBehaviour.Properties.ofFullCopy(Blocks.MUD).mapColor(MapColor.COLOR_LIGHT_GRAY), PEARL_SAND.get()));
    public static final DeferredBlock<Block> RED_ICE = registerWithItem("red_ice", () -> new IceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ICE)));
    public static final DeferredBlock<Block> RED_PACKED_ICE = registerWithItem("red_packed_ice", () -> new IceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PACKED_ICE)));
    public static final DeferredBlock<Block> LIFE_MUSHROOM = registerWithoutItem("life_mushroom", () -> new MushroomBlock(ISpreadable.Type.PURE, Blocks.GRASS_BLOCK)); // 生命蘑菇
    public static final DeferredBlock<SandLayerBlock> PEARL_SAND_LAYER_BLOCK = registerWithItem("pearl_sand_layer_block", SandLayerBlock::new);

    // 猩红
    public static final DeferredBlock<Block> TR_CRIMSON_GRASS_BLOCK = registerWithItem("tr_crimson_grass_block", () -> new SpreadingGrassBlock(ISpreadable.Type.CRIMSON, BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK).mapColor(MapColor.COLOR_RED)));
    public static final DeferredBlock<Block> SHADOW_SAPLING = registerWithItem("shadow_sapling", () -> new BaseSaplingBlock(ModFeatures.TreeGrowers.SHADOW_GROWER, TR_CRIMSON_GRASS_BLOCK.get()));
    public static final LogBlockSet SHADOW_LOG_BLOCKS = LogBlockSet.builder("shadow", true).createDefault(SHADOW, true).build();
    public static final DeferredBlock<Block> TR_CRIMSON_STONE = registerWithItem("tr_crimson_stone", () -> new SpreadingBlock(ISpreadable.Type.CRIMSON, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static final DeferredBlock<Block> TR_CRIMSON_COBBLESTONE = registerWithItem("tr_crimson_cobblestone", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE)));
    public static final DeferredBlock<Block> TR_CRIMSON_HARDENED_SAND_BLOCK = registerWithItem("tr_crimson_hardened_sand_block", () -> new SpreadingBlock(ISpreadable.Type.CRIMSON, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static final DeferredBlock<Block> TR_CRIMSON_SANDSTONE = registerWithItem("tr_crimson_sandstone", () -> new SpreadingBlock(ISpreadable.Type.CRIMSON, BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE)));
    public static final DeferredBlock<Block> TR_CRIMSON_SAND = registerWithItem("tr_crimson_sand", () -> new SpreadingSandBlock(ISpreadable.Type.CRIMSON, 0x5313E0, BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.COLOR_RED)));
    public static final DeferredBlock<Block> TR_CRIMSON_MOIST_SAND_BLOCK = registerWithItem("tr_crimson_moist_sand_block", () -> new SpreadableMoistSandBlock(ISpreadable.Type.CRIMSON, BlockBehaviour.Properties.ofFullCopy(Blocks.MUD).mapColor(MapColor.COLOR_RED), TR_CRIMSON_SAND.get()));
    public static final DeferredBlock<Block> PINK_ICE = registerWithItem("pink_ice", () -> new IceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ICE)));
    public static final DeferredBlock<Block> PINK_PACKED_ICE = registerWithItem("pink_packed_ice", () -> new IceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PACKED_ICE)));
    public static final DeferredBlock<Block> VICIOUS_MUSHROOM = registerWithoutItem("vicious_mushroom", () -> new MushroomBlock(ISpreadable.Type.CRIMSON, TR_CRIMSON_GRASS_BLOCK.get())); // 毒蘑菇
    public static final DeferredBlock<ThornBlock> CRIMSON_THORN = registerWithItem("crimson_thorn", () -> new SpreadingThornBlock(2, TR_CRIMSON_GRASS_BLOCK.get(), ISpreadable.Type.CRIMSON));
    public static final DeferredBlock<Block> TR_CRIMSON_GRASS = registerWithItem("tr_crimson_grass", () -> new BasePlantBlock(TR_CRIMSON_GRASS_BLOCK.get())); // 猩红草
    public static final DeferredBlock<SandLayerBlock> TR_CRIMSON_SAND_LAYER_BLOCK = registerWithItem("tr_crimson_sand_layer_block", SandLayerBlock::new);
    public static final DeferredBlock<Block> TR_CRIMSON_JUNGLE_GRASS_BLOCK = registerWithItem("tr_crimson_jungle_grass_block", () -> new JungleGrassBlock(ISpreadable.Type.CRIMSON, BlockBehaviour.Properties.ofFullCopy(Blocks.MUD))); // 腐化丛林草

    // 蘑菇地
    public static final DeferredBlock<MushroomGrassBlock> MUSHROOM_GRASS_BLOCK = registerWithItem("mushroom_grass_block", MushroomGrassBlock::new);
    public static final DeferredBlock<Block> GLOWING_MUSHROOM = registerWithoutItem("glowing_mushroom", () -> new MushroomBlock(ISpreadable.Type.GLOWING, MUSHROOM_GRASS_BLOCK.get())); // 发光蘑菇
    public static final DeferredBlock<IndusiumBlock> GLOWING_MUSHROOM_INDUSIUM_BLOCK = registerWithItem("glowing_mushroom_indusium_block", IndusiumBlock::new);
    public static final DeferredBlock<Block> GLOWING_MUSHROOM_STEM_BLOCK = registerWithItem("glowing_mushroom_stem_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.MUSHROOM_STEM)));
    public static final DeferredBlock<Block> GLOWING_MUSHROOM_PILEUS_BLOCK = registerWithItem("glowing_mushroom_pileus_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.MUSHROOM_STEM)));

    // 沙漠
    public static final DeferredBlock<Block> PALM_SAPLING = registerWithItem("palm_sapling", () -> new BaseSaplingBlock(ModFeatures.TreeGrowers.PALM_GROWER, Blocks.SAND, Blocks.RED_SAND, Blocks.GRASS_BLOCK));
    public static final LogBlockSet PALM_LOG_BLOCKS = LogBlockSet.builder("palm", true)
            .log(RotatedPillarBlock::new)
            .strippedLog(RotatedPillarBlock::new)
            .wood(RotatedPillarBlock::new)
            .strippedWood(RotatedPillarBlock::new)
            .leaves(PalmLeaves::new)
            .button(properties -> new ButtonBlock(PALM.SET, 30, properties))
            .fence(FenceBlock::new)
            .fenceGate(properties -> new FenceGateBlock(PALM.TYPE, properties))
            .pressurePlate(properties -> new PressurePlateBlock(PALM.SET, properties))
            .slab(SlabBlock::new)
            .stair(StairBlock::new)
            .sign(properties -> new StandingSignBlock(PALM.TYPE, properties), properties -> new WallSignBlock(PALM.TYPE, properties), SignItem::new)
            .trapdoor(properties -> new TrapDoorBlock(PALM.SET, properties))
            .door(properties -> new DoorBlock(PALM.SET, properties)).build();
    public static final DeferredBlock<Block> DESERT_TAPERED_BLOCK = registerWithItem("desert_tapered_block", TaperedTwoPartBlock::new);

    // 萨瓦纳草原
    public static final LogBlockSet BAOBAB_LOG_BLOCKS = LogBlockSet.builder("baobab", true).createDefault(BAOBAB, true).build();

    // 黄柳
    public static final LogBlockSet YELLOW_WILLOW_LOG_BLOCKS = LogBlockSet.builder("yellow_willow", true).createDefault(YELLOW_WILLOW, true).build();
    // 万圣节
    public static final LogBlockSet SPOOKY_LOG_BLOCKS = LogBlockSet.builder("spooky", true).createDefault(SPOOKY, true).build();
    // 生命树
    public static final LogBlockSet LIVING_LOG_BLOCKS = LogBlockSet.builder("living", true)
            .log(RotatedPillarBlock::new)
            .strippedLog(RotatedPillarBlock::new)
            .wood(RotatedPillarBlock::new)
            .strippedWood(RotatedPillarBlock::new)
            .leaves(properties -> new TransparentLeavesBlock(properties.noOcclusion()))
            .button(properties -> new ButtonBlock(LIVING.SET, 30, properties))
            .fence(FenceBlock::new)
            .fenceGate(properties -> new FenceGateBlock(LIVING.TYPE, properties))
            .pressurePlate(properties -> new PressurePlateBlock(LIVING.SET, properties))
            .slab(SlabBlock::new)
            .stair(StairBlock::new)
            .sign(properties -> new StandingSignBlock(LIVING.TYPE, properties), properties -> new WallSignBlock(LIVING.TYPE, properties), SignItem::new)
            .trapdoor(properties -> new TrapDoorBlock(LIVING.SET, properties))
            .door(properties -> new DoorBlock(LIVING.SET, properties)).build();
    // 生命红木
    public static final LogBlockSet LIVING_MAHOGANY_BLOCKS = LogBlockSet.builder("living_mahogany", true)
            .log(RotatedPillarBlock::new)
            .strippedLog(RotatedPillarBlock::new)
            .wood(RotatedPillarBlock::new)
            .strippedWood(RotatedPillarBlock::new)
            .leaves(properties -> new TransparentLeavesBlock(properties.noOcclusion()))
            .button(properties -> new ButtonBlock(LIVING_MAHOGANY.SET, 30, properties))
            .fence(FenceBlock::new)
            .fenceGate(properties -> new FenceGateBlock(LIVING_MAHOGANY.TYPE, properties))
            .pressurePlate(properties -> new PressurePlateBlock(LIVING_MAHOGANY.SET, properties))
            .slab(SlabBlock::new)
            .stair(StairBlock::new)
            .sign(properties -> new StandingSignBlock(LIVING_MAHOGANY.TYPE, properties), properties -> new WallSignBlock(LIVING_MAHOGANY.TYPE, properties), SignItem::new)
            .trapdoor(properties -> new TrapDoorBlock(LIVING_MAHOGANY.SET, properties))
            .door(properties -> new DoorBlock(LIVING_MAHOGANY.SET, properties)).build();
    // 灰烬
    public static final DeferredBlock<Block> ASH_SAPLING = registerWithItem("ash_sapling", () -> new AshSaplingBlock(ModFeatures.TreeGrowers.ASH_GROWER));
    public static final LogBlockSet ASH_LOG_BLOCKS = LogBlockSet.builder("ash", false).createDefault(ASH, true).build();
    public static final DeferredBlock<Block> ASH_BLOCK = registerWithItem("ash_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).strength(1.0F, 1.0F).sound(SoundType.SAND)));
    public static final DeferredBlock<Block> ASH_GRASS_BLOCK = registerWithItem("ash_grass_block", AshGrassBlock::new);
    public static final DeferredBlock<Block> ASH_GRASS = registerWithItem("ash_grass", () -> new BasePlantBlock(ASH_GRASS_BLOCK.get()));

    // 丛林
    public static final DeferredBlock<Block> JUNGLE_GRASS_BLOCK = registerWithItem("jungle_grass_block", () -> new JungleGrassBlock(ISpreadable.Type.JUNGLE, BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK)));
    public static final DeferredBlock<ThornBlock> JUNGLE_THORN = registerWithItem("jungle_thorn", () -> new ThornBlock(3.4f, Blocks.MOSS_BLOCK));
    public static final DeferredBlock<ThornBlock> PLANTERA_THORN = registerWithItem("plantera_thorn", () -> new ThornBlock(20, null));
    public static final DeferredBlock<JungleSporeBlock> JUNGLE_SPORE = registerWithoutItem("jungle_spore", JungleSporeBlock::new);
    public static final DeferredBlock<NaturesGiftBlock> NATURES_GIFT = registerWithoutItem("natures_gift", NaturesGiftBlock::new);
    public static final DeferredBlock<JungleHiveBlock> JUNGLE_HIVE_BLOCK = registerWithItem("jungle_hive_block", JungleHiveBlock::new);
    public static final DeferredBlock<NaturesGiftBlock> JUNGLE_ROSE = registerWithItem("jungle_rose", NaturesGiftBlock::new);
    public static final DeferredBlock<LarvaBlock> LARVA = registerWithItem("larva", () -> new LarvaBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.DESTROY).noOcclusion().replaceable().instabreak()));

    // 空岛
    public static final DeferredBlock<CloudBlock> CLOUD_BLOCK = registerWithItem("cloud_block", () -> new CloudBlock(BlockBehaviour.Properties.of()
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
    public static final Supplier<BlockEntityType<EvaporativeCloudBlock.Entity>> EVAPORATIVE_CLOUD_BLOCK_ENTITY = ModBlocks.BLOCK_ENTITIES.register("evaporative_cloud_block", () -> BlockEntityType.Builder.of(EvaporativeCloudBlock.Entity::new, EVAPORATIVE_CLOUD_BLOCK.get()).build(DSL.remainderType()));
    public static final DeferredBlock<ParticleCloudBlock> RAIN_CLOUD_BLOCK = registerWithItem("rain_cloud_block", () -> new ParticleCloudBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_GRAY)
            .strength(0.3F)
            .sound(SoundType.SNOW)
            .noOcclusion()
            .isValidSpawn(Blocks::never)
            .isRedstoneConductor((blockState, blockGetter, blockPos) -> false)
            .isSuffocating((blockState, blockGetter, blockPos) -> false)
            , ParticleTypes.FALLING_WATER));
    public static final DeferredBlock<ParticleCloudBlock> SNOW_CLOUD_BLOCK = registerWithItem("snow_cloud_block", () -> new ParticleCloudBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_GRAY)
            .strength(0.3F)
            .sound(SoundType.SNOW)
            .noOcclusion()
            .isValidSpawn(Blocks::never)
            .isRedstoneConductor((blockState, blockGetter, blockPos) -> false)
            .isSuffocating((blockState, blockGetter, blockPos) -> false)
            , ParticleTypes.SNOWFLAKE));
    public static final DeferredBlock<Block> STELLAR_BLOSSOM = registerWithItem("stellar_blossom", () -> new StellarBlossomBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION).offsetType(BlockBehaviour.OffsetType.NONE)));
    public static final DeferredBlock<Block> CLOUDWEAVER = registerWithItem("cloudweaver", () -> new CloudWeaverBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION).offsetType(BlockBehaviour.OffsetType.NONE)));
    public static final DeferredBlock<Block> FLOATING_WHEAT = registerWithItem("floating_wheat", () -> new FloatingWheatBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).offsetType(BlockBehaviour.OffsetType.NONE)));

    // 枝杈
    public static final DeferredBlock<Block> AMBER_BRANCHES = registerWithItem("amber_branches", () -> new BranchesBlock(ModTags.Blocks.JEWELLERY_BRANCHES_ATTACHABLE, BlockTags.BASE_STONE_OVERWORLD));
    public static final DeferredBlock<Block> RUBY_BRANCHES = registerWithItem("ruby_branches", () -> new BranchesBlock(ModTags.Blocks.JEWELLERY_BRANCHES_ATTACHABLE, BlockTags.BASE_STONE_OVERWORLD));
    public static final DeferredBlock<Block> TOPAZ_BRANCHES = registerWithItem("topaz_branches", () -> new BranchesBlock(ModTags.Blocks.JEWELLERY_BRANCHES_ATTACHABLE, BlockTags.BASE_STONE_OVERWORLD));
    public static final DeferredBlock<Block> EMERALD_BRANCHES = registerWithItem("emerald_branches", () -> new BranchesBlock(ModTags.Blocks.JEWELLERY_BRANCHES_ATTACHABLE, BlockTags.BASE_STONE_OVERWORLD));
    public static final DeferredBlock<Block> DIAMOND_BRANCHES = registerWithItem("diamond_branches", () -> new BranchesBlock(ModTags.Blocks.JEWELLERY_BRANCHES_ATTACHABLE, BlockTags.BASE_STONE_OVERWORLD));
    public static final DeferredBlock<Block> SAPPHIRE_BRANCHES = registerWithItem("sapphire_branches", () -> new BranchesBlock(ModTags.Blocks.JEWELLERY_BRANCHES_ATTACHABLE, BlockTags.BASE_STONE_OVERWORLD));
    public static final DeferredBlock<Block> TR_AMETHYST_BRANCHES = registerWithItem("tr_amethyst_branches", () -> new BranchesBlock(ModTags.Blocks.JEWELLERY_BRANCHES_ATTACHABLE, BlockTags.BASE_STONE_OVERWORLD));
    public static final DeferredBlock<Block> ASH_BRANCHES = registerWithItem("ash_branches", () -> new BranchesBlock(ModTags.Blocks.ASH_LOG_BRANCHES_ATTACHABLE, ModTags.Blocks.ASH_LOG_BRANCHES_ATTACHABLE));

    //藤蔓方块
    public static final DeferredBlock<Block> YELLOW_WILLOW_DROOPING_LEAVES = registerWithItem("yellow_willow_drooping_leaves", () -> new BaseDroopingPlantsHeadBlock(14, false, false, NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getLeaves().get()));
    public static final DeferredBlock<Block> GLOWING_MUSHROOM_VINE = registerWithItem("glowing_mushroom_vine", () -> new BaseDroopingPlantsHeadBlock(6, true, true));
    public static final DeferredBlock<Block> FOREST_DROOPING_VINE = registerWithItem("forest_drooping_vine", () -> new BaseDroopingPlantsHeadBlock(10, true, true));
    public static final DeferredBlock<Block> JUNGLE_DROOPING_VINE = registerWithItem("jungle_drooping_vine", () -> new BaseDroopingPlantsHeadBlock(10, true, true));
    public static final DeferredBlock<Block> CORRUPT_DROOPING_VINE = registerWithItem("corrupt_drooping_vine", () -> new BaseDroopingPlantsHeadBlock(10, true, true));
    public static final DeferredBlock<Block> TR_CRIMSON_DROOPING_VINE = registerWithItem("tr_crimson_drooping_vine", () -> new BaseDroopingPlantsHeadBlock(10, true, true));
    public static final DeferredBlock<Block> HALLOW_DROOPING_VINE = registerWithItem("hallow_drooping_vine", () -> new BaseDroopingPlantsHeadBlock(10, true, true));

    public static final DeferredBlock<Block> BLINKING_ROYAL_SHIMMERLILY = registerWithItem("blinking_royal_shimmerlily", () -> new BlinkingRoyalShimmerlilyBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LILY_PAD)), BlinkingRoyalShimmerlilyBlock.Item::new);

    //苔藓
    public static final DeferredBlock<BaseMossBlock> GREEN_MOSS = registerWithItem("green_moss", () -> new BaseMossBlock(5));
    public static final DeferredBlock<BaseMossBlock> BROWN_MOSS = registerWithItem("brown_moss", () -> new BaseMossBlock(5));
    public static final DeferredBlock<BaseMossBlock> RED_MOSS = registerWithItem("red_moss", () -> new BaseMossBlock(5));
    public static final DeferredBlock<BaseMossBlock> BLUE_MOSS = registerWithItem("blue_moss", () -> new BaseMossBlock(5));
    public static final DeferredBlock<BaseMossBlock> PURPLE_MOSS = registerWithItem("purple_moss", () -> new BaseMossBlock(5));
    public static final DeferredBlock<BaseMossBlock> LAVA_MOSS = registerWithItem("lava_moss", () -> new BaseMossBlock(10, true));
    public static final DeferredBlock<BaseMossBlock> KRYPTON_MOSS = registerWithItem("krypton_moss", () -> new BaseMossBlock(5));
    public static final DeferredBlock<BaseMossBlock> XENON_MOSS = registerWithItem("xenon_moss", () -> new BaseMossBlock(5));
    public static final DeferredBlock<BaseMossBlock> ARGON_MOSS = registerWithItem("argon_moss", () -> new BaseMossBlock(5));
    public static final DeferredBlock<BaseMossBlock> NEON_MOSS = registerWithItem("neon_moss", () -> new BaseMossBlock(5));
    public static final DeferredBlock<BaseMossBlock> HELIUM_MOSS = registerWithItem("helium_moss", () -> new BaseMossBlock(5));
    public static final DeferredBlock<BaseMossBlock> GLOWING_MUSHROOM_MOSS = registerWithItem("glowing_mushroom_moss", () -> new BaseMossBlock(5));

    public static final DeferredBlock<CoconutBlock> COCONUT_BLOCK = registerWithItem("coconut_block", CoconutBlock::new);
    public static final DeferredBlock<CrimsonVenusFlytrapBlock> CRIMSON_VENUS_FLYTRAP_BLOCK = registerWithItem("crimson_venus_flytrap_block", CrimsonVenusFlytrapBlock::new);
    public static final DeferredBlock<BloodthirstCrystallizedBlock> BLOODTHIRST_CRYSTALLIZED_BLOCK = registerWithItem("bloodthirst_crystallized_block", BloodthirstCrystallizedBlock::new);
    public static final Supplier<BlockEntityType<BloodthirstCrystallizedBlock.Entity>> BLOODTHIRST_CRYSTALLIZED_ENTITY = ModBlocks.BLOCK_ENTITIES.register("bloodthirst_crystallized_entity", () -> BlockEntityType.Builder.of(BloodthirstCrystallizedBlock.Entity::new, BLOODTHIRST_CRYSTALLIZED_BLOCK.get()).build(DSL.remainderType()));
    public static final DeferredBlock<CorrodedWormRootsBlock> CORRODED_WORM_ROOTS_BLOCK = registerWithItem("corroded_worm_roots_block", CorrodedWormRootsBlock::new);
    public static final DeferredBlock<CorruptedOvariesBlock> CORRUPTED_OVARIES_BLOCK = registerWithItem("corrupted_ovaries_block", CorruptedOvariesBlock::new);
    public static final DeferredBlock<DecomposeTheSourceExtractBlock> DECOMPOSE_THE_SOURCE_EXTRACT_BLOCK = registerWithItem("decompose_the_source_extract_block", DecomposeTheSourceExtractBlock::new);
    public static final Supplier<BlockEntityType<DecomposeTheSourceExtractBlock.Entity>> DECOMPOSE_THE_SOURCE_EXTRACT_ENTITY = ModBlocks.BLOCK_ENTITIES.register("decompose_the_source_extract_entity", () -> BlockEntityType.Builder.of(DecomposeTheSourceExtractBlock.Entity::new, DECOMPOSE_THE_SOURCE_EXTRACT_BLOCK.get()).build(DSL.remainderType()));


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
}
