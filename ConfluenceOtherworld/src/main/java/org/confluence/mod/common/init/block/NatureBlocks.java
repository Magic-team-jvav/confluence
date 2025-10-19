package org.confluence.mod.common.init.block;

import com.mojang.datafixers.DSL;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.block.TransparentLeavesBlock;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.natural.*;
import org.confluence.mod.common.block.natural.MushroomBlock;
import org.confluence.mod.common.block.natural.sapling.BaseSaplingBlock;
import org.confluence.mod.common.block.natural.sapling.StoneSaplingBlock;
import org.confluence.mod.common.block.natural.spreadable.*;
import org.confluence.mod.common.init.ModFeatures;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.ModItems;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.confluence.mod.common.block.natural.LogBlockSet.WoodSetType.*;
import static org.confluence.mod.common.block.natural.spreadable.PearlstoneBlock.generateCrystal;

@ParametersAreNonnullByDefault
public class NatureBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Confluence.MODID);

    // 非环境树苗
    public static final DeferredBlock<Block> RUBY_SAPLING = registerWithItem("ruby_sapling", () -> new StoneSaplingBlock(ModFeatures.TreeGrowers.RUBY_GROWER));
    public static final DeferredBlock<Block> AMBER_SAPLING = registerWithItem("amber_sapling", () -> new StoneSaplingBlock(ModFeatures.TreeGrowers.AMBER_GROWER));
    public static final DeferredBlock<Block> TOPAZ_SAPLING = registerWithItem("topaz_sapling", () -> new StoneSaplingBlock(ModFeatures.TreeGrowers.TOPAZ_GROWER));
    public static final DeferredBlock<Block> JADE_SAPLING = registerWithItem("jade_sapling", () -> new StoneSaplingBlock(ModFeatures.TreeGrowers.JADE_GROWER));
    public static final DeferredBlock<Block> DIAMOND_SAPLING = registerWithItem("diamond_sapling", () -> new StoneSaplingBlock(ModFeatures.TreeGrowers.DIAMOND_GROWER));
    public static final DeferredBlock<Block> SAPPHIRE_SAPLING = registerWithItem("sapphire_sapling", () -> new StoneSaplingBlock(ModFeatures.TreeGrowers.SAPPHIRE_GROWER));
    public static final DeferredBlock<Block> AMETHYST_SAPLING = registerWithItem("amethyst_sapling", () -> new StoneSaplingBlock(ModFeatures.TreeGrowers.AMETHYST_GROWER));

    // 流体接触块
    public static final DeferredBlock<ThinHoneyBlock> THIN_HONEY_BLOCK = registerWithItem("thin_honey_block", () -> new ThinHoneyBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.HONEY_BLOCK).mapColor(MapColor.COLOR_ORANGE).isViewBlocking((state, level, pos) -> false)));
    public static final DeferredBlock<Block> LOOSE_HONEY_BLOCK = registerWithItem("loose_honey_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.HONEY_BLOCK).mapColor(MapColor.COLOR_ORANGE)));
    public static final DeferredBlock<AetheriumBlock> AETHERIUM_BLOCK = registerWithItem("aetherium_block", () -> new AetheriumBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(MapColor.COLOR_PINK).lightLevel(blockState -> 10)));
    public static final DeferredBlock<Block> DARK_AETHERIUM_BLOCK = registerWithItem("dark_aetherium_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(MapColor.COLOR_GRAY)));

    // 香蒲
    public static final DeferredBlock<CattailsBodyBlock> CATTAILS_BODY = registerWithoutItem("cattails_body", () -> new CattailsBodyBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<CattailsBodyBlock> JUNGLE_CATTAILS_BODY = registerWithoutItem("jungle_cattails_body", () -> new CattailsBodyBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<CattailsBodyBlock> GLOWING_MUSHROOM_CATTAILS_BODY = registerWithoutItem("glowing_mushroom_cattails_body", () -> new CattailsBodyBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<CattailsBodyBlock> HALLOW_CATTAILS_BODY = registerWithoutItem("hallow_cattails_body", () -> new CattailsBodyBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<CattailsBodyBlock> EBONY_CATTAILS_BODY = registerWithoutItem("ebony_cattails_body", () -> new CattailsBodyBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<CattailsBodyBlock> CRIMSON_CATTAILS_BODY = registerWithoutItem("crimson_cattails_body", () -> new CattailsBodyBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<CattailsHeadBlock> CATTAILS_HEAD = registerWithoutItem("cattails_head", () -> new CattailsHeadBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS)));
    public static final DeferredBlock<CattailsHeadBlock> JUNGLE_CATTAILS_HEAD = registerWithoutItem("jungle_cattails_head", () -> new CattailsHeadBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS)));
    public static final DeferredBlock<CattailsHeadBlock> GLOWING_MUSHROOM_CATTAILS_HEAD = registerWithoutItem("glowing_mushroom_cattails_head", () -> new CattailsHeadBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS)));
    public static final DeferredBlock<CattailsHeadBlock> HALLOW_CATTAILS_HEAD = registerWithoutItem("hallow_cattails_head", () -> new CattailsHeadBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS)));
    public static final DeferredBlock<CattailsHeadBlock> EBONY_CATTAILS_HEAD = registerWithoutItem("ebony_cattails_head", () -> new CattailsHeadBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS)));
    public static final DeferredBlock<CattailsHeadBlock> CRIMSON_CATTAILS_HEAD = registerWithoutItem("crimson_cattails_head", () -> new CattailsHeadBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WATER).instabreak().noCollission().randomTicks().pushReaction(PushReaction.DESTROY).sound(SoundType.GRASS)));

    // 环境辅助
    public static final DeferredBlock<ThinIceBlock> THIN_ICE_BLOCK = registerWithItem("thin_ice_block", ThinIceBlock::new);
    public static final DeferredBlock<Block> HARDENED_SAND_BLOCK = registerWithItem("hardened_sand_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).mapColor(MapColor.SAND)));
    public static final DeferredBlock<Block> MOISTENED_SAND_BLOCK = registerWithItem("moistened_sand_block", () -> new MoistSandBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD).mapColor(MapColor.SAND), Blocks.SAND));
    public static final DeferredBlock<Block> HARDENED_RED_SAND_BLOCK = registerWithItem("hardened_red_sand_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).mapColor(MapColor.TERRACOTTA_RED)));
    public static final DeferredBlock<Block> MOISTENED_RED_SAND_BLOCK = registerWithItem("moistened_red_sand_block", () -> new MoistSandBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD).mapColor(MapColor.TERRACOTTA_RED), Blocks.RED_SAND));
    public static final DeferredBlock<Block> DIATOMACEOUS = registerWithItem("diatomaceous", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.TERRACOTTA_PINK)));
    public static final DeferredBlock<SandLayerBlock> SAND_LAYER_BLOCK = registerWithItem("sand_layer_block", SandLayerBlock::new);
    public static final DeferredBlock<SandLayerBlock> RED_SAND_LAYER_BLOCK = registerWithItem("red_sand_layer_block", SandLayerBlock::new);
    public static final DeferredBlock<Block> DESERT_FOSSIL = registerWithItem("desert_fossil", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).mapColor(MapColor.TERRACOTTA_RED)));
    public static final DeferredBlock<Block> SLUSH = registerWithItem("slush", () -> new ColoredFallingBlock(new ColorRGBA(1531531531), BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.TERRACOTTA_WHITE)));
    public static final DeferredBlock<Block> SILT_BLOCK = registerWithItem("silt_block", () -> new ColoredFallingBlock(new ColorRGBA(1531531531), BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.TERRACOTTA_GRAY)));
    public static final DeferredBlock<Block> MARINE_GRAVEL = registerWithItem("marine_gravel", () -> new ColoredFallingBlock(new ColorRGBA(1531531531), BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.TERRACOTTA_CYAN)));
    public static final DeferredBlock<RotatedPillarBlock> STONY_LOG = registerWithItem("stony_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.COLOR_LIGHT_GRAY)));
    public static final DeferredBlock<LifeCrystalBlock> LIFE_CRYSTAL_BLOCK = registerWithItem("life_crystal_block", () -> new LifeCrystalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_CLUSTER).mapColor(MapColor.COLOR_RED).lightLevel(state -> 7)), LifeCrystalBlock.BItem::new);
    public static final Supplier<BlockEntityType<LifeCrystalBlock.BEntity>> LIFE_CRYSTAL_BLOCK_ENTITY = ModBlocks.BLOCK_ENTITIES.register("life_crystal_block_entity", () -> BlockEntityType.Builder.of(LifeCrystalBlock.BEntity::new, LIFE_CRYSTAL_BLOCK.get()).build(DSL.remainderType()));

    public static final DeferredBlock<Block> GRANITE = registerWithItem("granite", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static final DeferredBlock<Block> MARBLE = registerWithItem("marble", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));

    public static final DeferredBlock<Block> LIFE_MUSHROOM = registerWithoutItem("life_mushroom", () -> new MushroomBlock(ISpreadable.Type.PURE, Blocks.GRASS_BLOCK)); // 生命蘑菇

    // 腐化
    public static final DeferredBlock<Block> CORRUPT_GRASS_BLOCK = registerWithItem("corrupt_grass_block", () -> new SpreadingGrassBlock(ISpreadable.Type.CORRUPT, BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK).mapColor(MapColor.COLOR_PURPLE)));
    public static final LogBlockSet EBONY_LOG_BLOCKS = LogBlockSet.builder("ebony", true, EBONY).sapling(properties -> new BaseSaplingBlock(ModFeatures.TreeGrowers.EBONY_GROWER, properties, null, CORRUPT_GRASS_BLOCK)).build();
    public static final DeferredBlock<Block> EBONSTONE = registerWithItem("ebonstone", () -> new SpreadingBlock(ISpreadable.Type.CORRUPT, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.TERRACOTTA_PURPLE)));
    public static final DeferredBlock<Block> COBBLED_EBONSTONE = registerWithItem("cobbled_ebonstone", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).mapColor(MapColor.TERRACOTTA_PURPLE)));
    public static final DeferredBlock<Block> EBONSANDSTONE = registerWithItem("ebonsandstone", () -> new SpreadingBlock(ISpreadable.Type.CORRUPT, BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).mapColor(MapColor.TERRACOTTA_PURPLE)));
    public static final DeferredBlock<Block> HARDENED_EBONSAND_BLOCK = registerWithItem("hardened_ebonsand_block", () -> new SpreadingBlock(ISpreadable.Type.CORRUPT, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.TERRACOTTA_PURPLE)));
    public static final DeferredBlock<Block> EBONSAND = registerWithItem("ebonsand", () -> new SpreadingSandBlock(ISpreadable.Type.CORRUPT, 0x372B4B, BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.COLOR_BLACK)));
    public static final DeferredBlock<Block> MOISTENED_EBONSAND_BLOCK = registerWithItem("moistened_ebonsand_block", () -> new SpreadableMoistedSandBlock(ISpreadable.Type.CORRUPT, BlockBehaviour.Properties.ofFullCopy(Blocks.MUD).mapColor(MapColor.COLOR_BLACK), EBONSAND));
    public static final DeferredBlock<Block> PURPLE_ICE = registerWithItem("purple_ice", () -> new SpreadingIceBlock(ISpreadable.Type.CORRUPT, BlockBehaviour.Properties.ofFullCopy(Blocks.ICE).mapColor(MapColor.COLOR_PURPLE)));
    public static final DeferredBlock<Block> PURPLE_PACKED_ICE = registerWithItem("purple_packed_ice", () -> new SpreadingIceBlock(ISpreadable.Type.CORRUPT, BlockBehaviour.Properties.ofFullCopy(Blocks.PACKED_ICE).mapColor(MapColor.COLOR_PURPLE)));
    public static final DeferredBlock<SandLayerBlock> EBONSAND_LAYER_BLOCK = registerWithItem("ebonsand_layer_block", SandLayerBlock::new);
    public static final DeferredBlock<Block> VILE_MUSHROOM = registerWithoutItem("vile_mushroom", () -> new MushroomBlock(ISpreadable.Type.CORRUPT, CORRUPT_GRASS_BLOCK.get())); // 魔菇
    public static final DeferredBlock<ThornBlock> CORRUPTION_THORN = registerWithItem("corruption_thorn", () -> new SpreadingThornBlock(2, CORRUPT_GRASS_BLOCK.get(), ISpreadable.Type.CORRUPT));
    public static final DeferredBlock<Block> CORRUPT_GRASS = registerWithItem("corrupt_grass", () -> new BasePlantBlock(CORRUPT_GRASS_BLOCK.get())); // 腐化草
    public static final DeferredBlock<Block> CORRUPT_JUNGLE_GRASS_BLOCK = registerWithItem("corrupt_jungle_grass_block", () -> new JungleGrassBlock(ISpreadable.Type.CORRUPT, BlockBehaviour.Properties.ofFullCopy(Blocks.MUD).mapColor(MapColor.COLOR_PURPLE))); // 腐化丛林草
    public static final DeferredBlock<ShadowOrbBlock> SHADOW_ORB = registerWithoutItem("shadow_orb", ShadowOrbBlock::new);
    public static final DeferredBlock<Block> CORRUPT_CACTUS = registerWithItem("corrupt_cactus", () -> new CactusBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CACTUS).mapColor(MapColor.COLOR_CYAN)) {
        @Override
        protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
            for(Direction direction : Direction.Plane.HORIZONTAL) {
                BlockState blockstate = level.getBlockState(pos.relative(direction));
                if (blockstate.isSolid() || level.getFluidState(pos.relative(direction)).is(FluidTags.LAVA)) {
                    return false;
                }
            }

            BlockState blockstate1 = level.getBlockState(pos.below());
            TriState soilDecision = blockstate1.canSustainPlant(level, pos.below(), Direction.UP, state);
            if (!soilDecision.isDefault()) {
                return soilDecision.isTrue();
            } else {
                return (blockstate1.is(ModTags.Blocks.CACTUS) || blockstate1.is(BlockTags.SAND)) && !level.getBlockState(pos.above()).liquid();
            }
        }

    });
    // 神圣
    public static final DeferredBlock<Block> HALLOW_GRASS_BLOCK = registerWithItem("hallow_grass_block", () -> new SpreadingGrassBlock(ISpreadable.Type.HALLOW, BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK).mapColor(MapColor.COLOR_CYAN)));
    public static final DeferredBlock<Block> HALLOW_GRASS = registerWithItem("hallow_grass", () -> new BasePlantBlock(HALLOW_GRASS_BLOCK.get())); // 神圣草
    public static final LogBlockSet PEARL_LOG_BLOCKS = LogBlockSet.builder("pearl", true, PEARL).sapling(properties -> new BaseSaplingBlock(ModFeatures.TreeGrowers.PEARL_GROWER, properties, null, HALLOW_GRASS_BLOCK)).build();
    public static final DeferredBlock<Block> PEARLSTONE = registerWithItem("pearlstone", () -> new PearlstoneBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.TERRACOTTA_MAGENTA).strength(8.0F)));
    public static final DeferredBlock<Block> COBBLED_PEARLSTONE = registerWithItem("cobbled_pearlstone", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).mapColor(MapColor.TERRACOTTA_MAGENTA)));
    public static final DeferredBlock<Block> HARDENED_PEARLSAND_BLOCK = registerWithItem("hardened_pearlsand_block", () -> new PearlstoneBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.TERRACOTTA_PINK)));
    public static final DeferredBlock<Block> PEARLSANDSTONE = registerWithItem("pearlsandstone", () -> new PearlstoneBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).mapColor(MapColor.TERRACOTTA_PINK).strength(8.0F)));
    public static final DeferredBlock<Block> PEARLSAND = registerWithItem("pearlsand", () -> new SpreadingSandBlock(ISpreadable.Type.HALLOW, 0xEDD5F6, BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.TERRACOTTA_PINK)) {
        @Override
        public void spread(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
            super.spread(blockState, serverLevel, blockPos, randomSource);
            generateCrystal(serverLevel, blockPos, randomSource);
        }
    });
    public static final DeferredBlock<Block> MOISTENED_PEARLSAND_BLOCK = registerWithItem("moistened_pearlsand_block", () -> new SpreadableMoistedSandBlock(ISpreadable.Type.HALLOW, BlockBehaviour.Properties.ofFullCopy(Blocks.MUD).mapColor(MapColor.TERRACOTTA_PINK), PEARLSAND) {
        @Override
        public void spread(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
            super.spread(blockState, serverLevel, blockPos, randomSource);
            generateCrystal(serverLevel, blockPos, randomSource);
        }
    });
    public static final DeferredBlock<SandLayerBlock> PEARLSAND_LAYER_BLOCK = registerWithItem("pearlsand_layer_block", SandLayerBlock::new);
    public static final DeferredBlock<Block> PINK_ICE = registerWithItem("pink_ice", () -> new SpreadingIceBlock(ISpreadable.Type.HALLOW, BlockBehaviour.Properties.ofFullCopy(Blocks.ICE).mapColor(MapColor.COLOR_PINK)));
    public static final DeferredBlock<Block> PINK_PACKED_ICE = registerWithItem("pink_packed_ice", () -> new SpreadingIceBlock(ISpreadable.Type.HALLOW, BlockBehaviour.Properties.ofFullCopy(Blocks.PACKED_ICE).mapColor(MapColor.COLOR_PINK).randomTicks()) {
        @Override
        public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
            if (serverLevel.isAreaLoaded(blockPos, 3)) {
                generateCrystal(serverLevel, blockPos, randomSource);
            }
        }
    });
    public static final DeferredBlock<AmethystClusterBlock> CRYSTAL_SHARDS = registerWithoutItem("crystal_shards", () -> new AmethystClusterBlock(7.0F, 3.0F, BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_CLUSTER).mapColor(MapColor.COLOR_PINK).lightLevel(state -> 7)));
    public static final DeferredBlock<AmethystClusterBlock> GELATIN_CRYSTAL = registerWithoutItem("gelatin_crystal", () -> new AmethystClusterBlock(7.0F, 3.0F, BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_CLUSTER).mapColor(MapColor.COLOR_PINK).lightLevel(state -> 9)));
    public static final DeferredBlock<Block> HALLOW_CACTUS = registerWithItem("hallow_cactus", () -> new CactusBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CACTUS).mapColor(MapColor.COLOR_CYAN)) {
        @Override
        protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
            for(Direction direction : Direction.Plane.HORIZONTAL) {
                BlockState blockstate = level.getBlockState(pos.relative(direction));
                if (blockstate.isSolid() || level.getFluidState(pos.relative(direction)).is(FluidTags.LAVA)) {
                    return false;
                }
            }

            BlockState blockstate1 = level.getBlockState(pos.below());
            TriState soilDecision = blockstate1.canSustainPlant(level, pos.below(), Direction.UP, state);
            if (!soilDecision.isDefault()) {
                return soilDecision.isTrue();
            } else {
                return (blockstate1.is(ModTags.Blocks.CACTUS) || blockstate1.is(BlockTags.SAND)) && !level.getBlockState(pos.above()).liquid();
            }
        }

    });

    // 猩红
    public static final DeferredBlock<Block> CRIMSON_GRASS_BLOCK = registerWithItem("crimson_grass_block", () -> new SpreadingGrassBlock(ISpreadable.Type.CRIMSON, BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK).mapColor(MapColor.COLOR_RED)));
    public static final LogBlockSet SHADOW_LOG_BLOCKS = LogBlockSet.builder("shadow", true, SHADOW).sapling(properties -> new BaseSaplingBlock(ModFeatures.TreeGrowers.SHADOW_GROWER, properties, null, CRIMSON_GRASS_BLOCK)).build();
    public static final DeferredBlock<Block> CRIMSTONE = registerWithItem("crimstone", () -> new SpreadingBlock(ISpreadable.Type.CRIMSON, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.COLOR_RED)));
    public static final DeferredBlock<Block> COBBLED_CRIMSTONE = registerWithItem("cobbled_crimstone", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).mapColor(MapColor.COLOR_RED)));
    public static final DeferredBlock<Block> HARDENED_CRIMSAND_BLOCK = registerWithItem("hardened_crimsand_block", () -> new SpreadingBlock(ISpreadable.Type.CRIMSON, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.TERRACOTTA_RED)));
    public static final DeferredBlock<Block> CRIMSANDSTONE = registerWithItem("crimsandstone", () -> new SpreadingBlock(ISpreadable.Type.CRIMSON, BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).mapColor(MapColor.TERRACOTTA_RED)));
    public static final DeferredBlock<Block> CRIMSAND = registerWithItem("crimsand", () -> new SpreadingSandBlock(ISpreadable.Type.CRIMSON, 0x5313E0, BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.COLOR_RED)));
    public static final DeferredBlock<Block> MOISTENED_CRIMSAND_BLOCK = registerWithItem("moistened_crimsand_block", () -> new SpreadableMoistedSandBlock(ISpreadable.Type.CRIMSON, BlockBehaviour.Properties.ofFullCopy(Blocks.MUD).mapColor(MapColor.COLOR_RED), CRIMSAND));
    public static final DeferredBlock<Block> VICIOUS_MUSHROOM = registerWithoutItem("vicious_mushroom", () -> new MushroomBlock(ISpreadable.Type.CRIMSON, CRIMSON_GRASS_BLOCK.get())); // 毒蘑菇
    public static final DeferredBlock<ThornBlock> CRIMSON_THORN = registerWithItem("crimson_thorn", () -> new SpreadingThornBlock(2, CRIMSON_GRASS_BLOCK.get(), ISpreadable.Type.CRIMSON));
    public static final DeferredBlock<Block> CRIMSON_GRASS = registerWithItem("crimson_grass", () -> new BasePlantBlock(CRIMSON_GRASS_BLOCK.get())); // 猩红草
    public static final DeferredBlock<SandLayerBlock> CRIMSAND_LAYER_BLOCK = registerWithItem("crimsand_layer_block", SandLayerBlock::new);
    public static final DeferredBlock<Block> CRIMSON_JUNGLE_GRASS_BLOCK = registerWithItem("crimson_jungle_grass_block", () -> new JungleGrassBlock(ISpreadable.Type.CRIMSON, BlockBehaviour.Properties.ofFullCopy(Blocks.MUD))); // 腐化丛林草
    public static final DeferredBlock<Block> RED_ICE = registerWithItem("red_ice", () -> new SpreadingIceBlock(ISpreadable.Type.CRIMSON, BlockBehaviour.Properties.ofFullCopy(Blocks.ICE).mapColor(MapColor.COLOR_RED)));
    public static final DeferredBlock<Block> RED_PACKED_ICE = registerWithItem("red_packed_ice", () -> new SpreadingIceBlock(ISpreadable.Type.CRIMSON, BlockBehaviour.Properties.ofFullCopy(Blocks.PACKED_ICE).mapColor(MapColor.COLOR_RED)));
    public static final DeferredBlock<CrimsonHeartBlock> CRIMSON_HEART = registerWithoutItem("crimson_heart", CrimsonHeartBlock::new);
    public static final DeferredBlock<Block> CRIMSON_CACTUS = registerWithItem("crimson_cactus", () -> new CactusBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CACTUS).mapColor(MapColor.COLOR_RED)) {
        @Override
        protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
            for(Direction direction : Direction.Plane.HORIZONTAL) {
                BlockState blockstate = level.getBlockState(pos.relative(direction));
                if (blockstate.isSolid() || level.getFluidState(pos.relative(direction)).is(FluidTags.LAVA)) {
                    return false;
                }
            }

            BlockState blockstate1 = level.getBlockState(pos.below());
            TriState soilDecision = blockstate1.canSustainPlant(level, pos.below(), Direction.UP, state);
            if (!soilDecision.isDefault()) {
                return soilDecision.isTrue();
            } else {
                return (blockstate1.is(ModTags.Blocks.CACTUS) || blockstate1.is(BlockTags.SAND)) && !level.getBlockState(pos.above()).liquid();
            }
        }

    });

    // 蘑菇地
    public static final DeferredBlock<MushroomGrassBlock> MUSHROOM_GRASS_BLOCK = registerWithItem("mushroom_grass_block", MushroomGrassBlock::new);
    public static final DeferredBlock<Block> GLOWING_MUSHROOM = registerWithoutItem("glowing_mushroom", () -> new MushroomBlock(ISpreadable.Type.GLOWING, MUSHROOM_GRASS_BLOCK.get())); // 发光蘑菇
    public static final DeferredBlock<IndusiumBlock> GLOWING_MUSHROOM_INDUSIUM_BLOCK = registerWithItem("glowing_mushroom_indusium_block", IndusiumBlock::new);
    public static final DeferredBlock<RotatedPillarBlock> GLOWING_MUSHROOM_STEM_BLOCK = registerWithItem("glowing_mushroom_stem_block", () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUSHROOM_STEM)));
    public static final DeferredBlock<GlowingMushroomPileusBlock> GLOWING_MUSHROOM_PILEUS_BLOCK = registerWithItem("glowing_mushroom_pileus_block", () -> new GlowingMushroomPileusBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUSHROOM_STEM)));

    public static final DeferredBlock<IndusiumBlock> LIFE_MUSHROOM_INDUSIUM_BLOCK = registerWithItem("life_mushroom_indusium_block", IndusiumBlock::new);
    public static final DeferredBlock<RotatedPillarBlock> LIFE_MUSHROOM_STEM_BLOCK = registerWithItem("life_mushroom_stem_block", () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUSHROOM_STEM)));
    public static final DeferredBlock<GlowingMushroomPileusBlock> LIFE_MUSHROOM_PILEUS_BLOCK = registerWithItem("life_mushroom_pileus_block", () -> new GlowingMushroomPileusBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUSHROOM_STEM)));

    public static final LogBlockSet GLOWING_MUSHROOM_LOG_BLOCKS = LogBlockSet.builder("glowing_mushroom", true, LogBlockSet.WoodSetType.GLOWING_MUSHROOM).log(null).strippedLog(null).wood(null).strippedWood(null).leaves(null).build();

    // 沙漠
    public static final LogBlockSet PALM_LOG_BLOCKS = LogBlockSet.builder("palm", true, PALM).leaves(PalmLeaves::new).sapling(properties -> new BaseSaplingBlock(ModFeatures.TreeGrowers.PALM_GROWER, properties, null, getSupplier(Blocks.SAND), getSupplier(Blocks.RED_SAND), getSupplier(Blocks.GRASS_BLOCK), NatureBlocks.MOISTENED_SAND_BLOCK, NatureBlocks.MOISTENED_RED_SAND_BLOCK)).build();
    public static final DeferredBlock<Block> DESERT_TAPERED_BLOCK = registerWithItem("desert_tapered_block", TaperedTwoPartBlock::new);
    public static final DeferredBlock<DesertPlantBlock> SMALL_DESERT_PLANT = registerWithItem("small_desert_plant", () -> new DesertPlantBlock(Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D)));
    public static final DeferredBlock<DesertPlantBlock> BIG_DESERT_PLANT = registerWithItem("big_desert_plant", () -> new DesertPlantBlock(Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D)));
    public static final DeferredBlock<SmallCactusBlock> SMALL_CACTUS = registerWithItem("small_cactus", SmallCactusBlock::new);
    public static final DeferredBlock<Block> DESERT_GRASS = registerWithItem("desert_grass", () -> new BasePlantBlock(Blocks.SAND, Blocks.RED_SAND));
    public static final DeferredBlock<Block> DESERT_TALL_GRASS = registerWithItem("desert_tall_grass", () -> new BaseTallPlantBlock(Blocks.SAND, Blocks.RED_SAND));
    public static final DeferredBlock<Block> PACKED_DIRT = registerWithItem("packed_dirt", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).strength(1.0F, 1.0F).sound(SoundType.STONE).mapColor(MapColor.COLOR_BROWN)));

    // 萨瓦纳草原
    public static final LogBlockSet BAOBAB_LOG_BLOCKS = LogBlockSet.builder("baobab", true, BAOBAB).sapling(properties -> new BaseSaplingBlock(ModFeatures.TreeGrowers.BAOBAB_GROWER, properties, null, getSupplier(Blocks.GRASS_BLOCK), getSupplier(Blocks.DIRT))).build();


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
    public static final DeferredBlock<Block> ASH_GRASS_BLOCK = registerWithItem("ash_grass_block", AshGrassBlock::new);
    public static final LogBlockSet ASH_LOG_BLOCKS = LogBlockSet.builder("ash", false, ASH).leaves(null).sapling(properties -> new BaseSaplingBlock(ModFeatures.TreeGrowers.ASH_GROWER, properties, null, ASH_BLOCK, ASH_GRASS_BLOCK)).build();
    public static final DeferredBlock<Block> ASH_GRASS = registerWithItem("ash_grass", () -> new BasePlantBlock(ASH_GRASS_BLOCK.get()));

    // 丛林
    public static final DeferredBlock<Block> JUNGLE_GRASS_BLOCK = registerWithItem("jungle_grass_block", () -> new JungleGrassBlock(ISpreadable.Type.JUNGLE, BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK).mapColor(MapColor.COLOR_GREEN)));
    public static final DeferredBlock<ThornBlock> JUNGLE_THORN = registerWithItem("jungle_thorn", () -> new ThornBlock(3.4f, Blocks.MOSS_BLOCK));
    public static final DeferredBlock<ThornBlock> PLANTERA_THORN = registerWithItem("plantera_thorn", () -> new ThornBlock(20, Blocks.MUD));
    public static final DeferredBlock<JungleSporeBlock> JUNGLE_SPORE = registerWithoutItem("jungle_spore", JungleSporeBlock::new);
    public static final DeferredBlock<NaturesGiftBlock> NATURES_GIFT = registerWithoutItem("natures_gift", NaturesGiftBlock::new);
    public static final DeferredBlock<JungleHiveBlock> JUNGLE_HIVE_BLOCK = registerWithItem("jungle_hive_block", JungleHiveBlock::new);
    public static final DeferredBlock<NaturesGiftBlock> JUNGLE_ROSE = registerWithItem("jungle_rose", NaturesGiftBlock::new);
    public static final DeferredBlock<LarvaBlock> LARVA = registerWithItem("larva", () -> new LarvaBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.DESTROY).noOcclusion().replaceable().instabreak()));

    public static final DeferredBlock<Block> JUNGLE_PATH = registerWithItem("mud_path", () -> new MudPathBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD).mapColor(MapColor.COLOR_LIGHT_GRAY)));
    public static final DeferredBlock<Block> MUSHROOM_PATH = registerWithItem("mushroom_path", () -> new MudPathBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD).mapColor(MapColor.COLOR_LIGHT_GRAY)));
    public static final DeferredBlock<Block> ASH_PATH = registerWithItem("ash_path", () -> new AshPathBlock(BlockBehaviour.Properties.ofFullCopy(NatureBlocks.ASH_BLOCK.get()).mapColor(MapColor.COLOR_GRAY)));

    // 王朝木
    public static final LogBlockSet DYNASTY_LOG_BLOCKS = LogBlockSet.builder("dynasty", true, DYNASTY).leaves(null).build();
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
    public static final DeferredBlock<Block> JADE_BRANCHES = registerWithItem("jade_branches", () -> new BranchesBlock(ModTags.Blocks.JEWELLERY_BRANCHES_ATTACHABLE, BlockTags.BASE_STONE_OVERWORLD));
    public static final DeferredBlock<Block> DIAMOND_BRANCHES = registerWithItem("diamond_branches", () -> new BranchesBlock(ModTags.Blocks.JEWELLERY_BRANCHES_ATTACHABLE, BlockTags.BASE_STONE_OVERWORLD));
    public static final DeferredBlock<Block> SAPPHIRE_BRANCHES = registerWithItem("sapphire_branches", () -> new BranchesBlock(ModTags.Blocks.JEWELLERY_BRANCHES_ATTACHABLE, BlockTags.BASE_STONE_OVERWORLD));
    public static final DeferredBlock<Block> AMETHYST_BRANCHES = registerWithItem("amethyst_branches", () -> new BranchesBlock(ModTags.Blocks.JEWELLERY_BRANCHES_ATTACHABLE, BlockTags.BASE_STONE_OVERWORLD));
    public static final DeferredBlock<Block> ASH_BRANCHES = registerWithItem("ash_branches", () -> new BranchesBlock(ModTags.Blocks.ASH_LOG_BRANCHES_ATTACHABLE, ModTags.Blocks.ASH_LOG_BRANCHES_ATTACHABLE));

    //藤蔓方块
    public static final DeferredBlock<Block> YELLOW_WILLOW_DROOPING_LEAVES = registerWithItem("yellow_willow_drooping_leaves", () -> new BaseDroopingPlantsHeadBlock(14, false, false, NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.LEAVES.get()));
    public static final DeferredBlock<Block> GLOWING_MUSHROOM_VINE = registerWithItem("glowing_mushroom_vine", () -> new BaseDroopingPlantsHeadBlock(6, true, true));
    public static final DeferredBlock<Block> FOREST_DROOPING_VINE = registerWithItem("forest_drooping_vine", () -> new BaseDroopingPlantsHeadBlock(10, true, true));
    public static final DeferredBlock<Block> JUNGLE_DROOPING_VINE = registerWithItem("jungle_drooping_vine", () -> new BaseDroopingPlantsHeadBlock(10, true, true));
    public static final DeferredBlock<Block> CORRUPT_DROOPING_VINE = registerWithItem("corrupt_drooping_vine", () -> new BaseDroopingPlantsHeadBlock(10, true, true));
    public static final DeferredBlock<Block> CRIMSON_DROOPING_VINE = registerWithItem("crimson_drooping_vine", () -> new BaseDroopingPlantsHeadBlock(10, true, true));
    public static final DeferredBlock<Block> HALLOW_DROOPING_VINE = registerWithItem("hallow_drooping_vine", () -> new BaseDroopingPlantsHeadBlock(10, true, true));

    public static final DeferredBlock<Block> SHIMMER_DROOPING_VINE = registerWithoutItem("shimmer_drooping_vine", () -> new ShimmerDroopingVinesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAVE_VINES)));
    public static final DeferredBlock<Block> SHIMMER_DROOPING_VINE_PLANT = registerWithoutItem("shimmer_drooping_vine_plant", () -> new ShimmerDroopingVinesPlantBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAVE_VINES_PLANT)));

    public static final DeferredBlock<Block> BLINKING_ROYAL_SHIMMERLILY = registerWithItem("blinking_royal_shimmerlily", () -> new BlinkingRoyalShimmerlilyBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LILY_PAD)), BlinkingRoyalShimmerlilyBlock.BItem::new);

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
