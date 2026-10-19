package org.confluence.mod.common.block.conversion.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.common.Tags;
import org.confluence.mod.common.block.conversion.ConversionTable;
import org.confluence.mod.common.block.natural.CattailBlock;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.block.OreBlocks;
import org.jetbrains.annotations.Nullable;

public class HallowMachineConversionTable extends ConversionTable {
    @Override
    protected @Nullable Block getTarget(BlockState source, boolean hardmode) {
        if (source.is(NatureBlocks.JUNGLE_THORN.get())) {
            return Blocks.AIR;
        }
        if (source.is(NatureBlocks.CORRUPTION_THORN.get())
                || source.is(NatureBlocks.CRIMSON_THORN.get())
                || source.is(NatureBlocks.PLANTERA_THORN.get())) {
            return Blocks.AIR;
        }
        if (source.is(Blocks.TALL_GRASS)) {
            return source.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER ? NatureBlocks.HALLOW_GRASS.get() : Blocks.AIR;
        }
        if (source.getBlock() instanceof CattailBlock && !source.is(NatureBlocks.HALLOW_CATTAIL_BLOCK.get())) {
            return NatureBlocks.HALLOW_CATTAIL_BLOCK.get();
        }
        if (source.is(BlockTags.LOGS)) {
            return NatureBlocks.PEARL_LOG_BLOCKS.LOG.get();
        }
        if (source.is(BlockTags.LEAVES)) {
            return NatureBlocks.PEARL_LOG_BLOCKS.LEAVES.get();
        }
        if (source.is(ModTags.Blocks.HALLOW_CONVERSION_GRASS_BLOCK)) {
            return NatureBlocks.HALLOW_GRASS_BLOCK.get();
        }
        if (source.is(ModTags.Blocks.HALLOW_CONVERSION_SHORT_GRASS)) {
            return NatureBlocks.HALLOW_GRASS.get();
        }
        if (source.is(ModTags.Blocks.HALLOW_CONVERSION_PACKED_ICE)) {
            return NatureBlocks.PINK_PACKED_ICE.get();
        }
        if (source.is(ModTags.Blocks.HALLOW_CONVERSION_ICE)) {
            return NatureBlocks.PINK_ICE.get();
        }
        if (source.is(ModTags.Blocks.HALLOW_CONVERSION_ICE_TAPERED_BLOCK)) {
            return NatureBlocks.PINK_ICE_TAPERED_BLOCK.get();
        }
        if (source.is(ModTags.Blocks.HALLOW_CONVERSION_SAND)) {
            return NatureBlocks.PEARLSAND.get();
        }
        if (source.is(ModTags.Blocks.HALLOW_CONVERSION_SANDSTONE)) {
            return NatureBlocks.PEARLSANDSTONE.get();
        }
        if (source.is(ModTags.Blocks.HALLOW_CONVERSION_SANDSTONE_PILES)) {
            return NatureBlocks.SMALL_PEARLSANDSTONE_PILES.get();
        }
        if (source.is(ModTags.Blocks.HALLOW_CONVERSION_HARDENED_SAND_BLOCK)) {
            return NatureBlocks.HARDENED_PEARLSAND_BLOCK.get();
        }
        if (source.is(ModTags.Blocks.HALLOW_CONVERSION_MOIST_SAND_BLOCK)) {
            return NatureBlocks.MOISTENED_PEARLSAND_BLOCK.get();
        }
        if (source.is(ModTags.Blocks.HALLOW_CONVERSION_CACTUS)) {
            return NatureBlocks.HALLOW_CACTUS.get();
        }
        if (source.is(BlockTags.BASE_STONE_OVERWORLD)) {
            return NatureBlocks.PEARLSTONE.get();
        }
        if (source.is(Tags.Blocks.COBBLESTONES)) {
            return NatureBlocks.COBBLED_PEARLSTONE.get();
        }
        if (source.is(Tags.Blocks.ORES_REDSTONE)) {
            return OreBlocks.SANCTIFICATION_REDSTONE_ORE.get();
        }
        if (source.is(Tags.Blocks.ORES_COAL)) {
            return OreBlocks.SANCTIFICATION_COAL_ORE.get();
        }
        if (source.is(Tags.Blocks.ORES_LAPIS)) {
            return OreBlocks.SANCTIFICATION_LAPIS_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_RUBY)) {
            return OreBlocks.SANCTIFICATION_RUBY_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_AMBER)) {
            return OreBlocks.SANCTIFICATION_AMBER_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_TOPAZ)) {
            return OreBlocks.SANCTIFICATION_TOPAZ_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_JADE)) {
            return OreBlocks.SANCTIFICATION_JADE_ORE.get();
        }
        if (source.is(Tags.Blocks.ORES_EMERALD)) {
            return OreBlocks.SANCTIFICATION_EMERALD_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_SAPPHIRE)) {
            return OreBlocks.SANCTIFICATION_SAPPHIRE_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_AMETHYST)) {
            return OreBlocks.SANCTIFICATION_AMETHYST_ORE.get();
        }
        if (source.is(Tags.Blocks.ORES_DIAMOND)) {
            return OreBlocks.SANCTIFICATION_DIAMOND_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_TIN)) {
            return OreBlocks.SANCTIFICATION_TIN_ORE.get();
        }
        if (source.is(Tags.Blocks.ORES_COPPER)) {
            return OreBlocks.SANCTIFICATION_COPPER_ORE.get();
        }
        if (source.is(Tags.Blocks.ORES_IRON)) {
            return OreBlocks.SANCTIFICATION_IRON_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_LEAD)) {
            return OreBlocks.SANCTIFICATION_LEAD_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_SILVER)) {
            return OreBlocks.SANCTIFICATION_SILVER_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_TUNGSTEN)) {
            return OreBlocks.SANCTIFICATION_TUNGSTEN_ORE.get();
        }
        if (source.is(Tags.Blocks.ORES_GOLD)) {
            return OreBlocks.SANCTIFICATION_GOLD_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_PLATINUM)) {
            return OreBlocks.SANCTIFICATION_PLATINUM_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_DEMONITE)) {
            return OreBlocks.SANCTIFICATION_DEMONITE_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_CRIMTANE)) {
            return OreBlocks.SANCTIFICATION_CRIMTANE_ORE.get();
        }
        return null;
    }

    // 只有贴着神圣草的泥块才会干燥，普通泥块不会被整片转换。
    @Override
    public @Nullable BlockState get(BlockState source, boolean hardmode, LevelReader level, BlockPos pos) {
        if (source.is(Blocks.MUD)) {
            for (Direction direction : Direction.values()) {
                BlockPos neighbor = pos.relative(direction);
                if (level.hasChunkAt(neighbor) && level.getBlockState(neighbor).is(NatureBlocks.HALLOW_GRASS_BLOCK.get())) {
                    return Blocks.DIRT.defaultBlockState();
                }
            }
        }
        return super.get(source, hardmode);
    }

}
