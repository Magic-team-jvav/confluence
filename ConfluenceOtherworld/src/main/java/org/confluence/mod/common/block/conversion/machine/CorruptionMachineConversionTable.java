package org.confluence.mod.common.block.conversion.machine;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.common.Tags;
import org.confluence.mod.common.block.conversion.ConversionTable;
import org.confluence.mod.common.block.natural.CattailBlock;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.block.OreBlocks;
import org.jetbrains.annotations.Nullable;

public class CorruptionMachineConversionTable extends ConversionTable {
    @Override
    protected @Nullable Block getTarget(BlockState source, boolean hardmode) {
        if (source.is(NatureBlocks.JUNGLE_THORN.get())) {
            return NatureBlocks.CORRUPTION_THORN.get();
        }
        if (source.is(NatureBlocks.CRIMSON_THORN.get()) || source.is(NatureBlocks.PLANTERA_THORN.get())) {
            return NatureBlocks.CORRUPTION_THORN.get();
        }
        if (source.is(Blocks.TALL_GRASS)) {
            return source.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER
                    ? NatureBlocks.CORRUPT_GRASS.get() : Blocks.AIR;
        }
        if (source.getBlock() instanceof CattailBlock && !source.is(NatureBlocks.EBONY_CATTAIL_BLOCK.get())) {
            return NatureBlocks.EBONY_CATTAIL_BLOCK.get();
        }
        if (source.is(BlockTags.LOGS)) {
            return NatureBlocks.EBONY_LOG_BLOCKS.LOG.get();
        }
        if (source.is(BlockTags.LEAVES)) {
            return NatureBlocks.EBONY_LOG_BLOCKS.LEAVES.get();
        }
        if (source.is(ModTags.Blocks.CORRUPTION_CONVERSION_DIRT)) {
            return Blocks.DIRT;
        }
        if (source.is(ModTags.Blocks.CORRUPTION_CONVERSION_GRASS_BLOCK)) {
            return NatureBlocks.CORRUPT_GRASS_BLOCK.get();
        }
        if (source.is(ModTags.Blocks.CORRUPTION_CONVERSION_JUNGLE_GRASS_BLOCK)) {
            return NatureBlocks.CORRUPT_JUNGLE_GRASS_BLOCK.get();
        }
        if (source.is(ModTags.Blocks.CORRUPTION_CONVERSION_SHORT_GRASS)) {
            return NatureBlocks.CORRUPT_GRASS.get();
        }
        if (source.is(ModTags.Blocks.CORRUPTION_CONVERSION_PACKED_ICE)) {
            return NatureBlocks.PURPLE_PACKED_ICE.get();
        }
        if (source.is(ModTags.Blocks.CORRUPTION_CONVERSION_ICE)) {
            return NatureBlocks.PURPLE_ICE.get();
        }
        if (source.is(ModTags.Blocks.CORRUPTION_CONVERSION_SAND)) {
            return NatureBlocks.EBONSAND.get();
        }
        if (source.is(ModTags.Blocks.CORRUPTION_CONVERSION_SANDSTONE)) {
            return NatureBlocks.EBONSANDSTONE.get();
        }
        if (source.is(ModTags.Blocks.CORRUPTION_CONVERSION_HARDENED_SAND_BLOCK)) {
            return NatureBlocks.HARDENED_EBONSAND_BLOCK.get();
        }
        if (source.is(ModTags.Blocks.CORRUPTION_CONVERSION_MOIST_SAND_BLOCK)) {
            return NatureBlocks.MOISTENED_EBONSAND_BLOCK.get();
        }
        if (source.is(ModTags.Blocks.CORRUPTION_CONVERSION_CACTUS)) {
            return NatureBlocks.CORRUPT_CACTUS.get();
        }
        if (source.is(BlockTags.BASE_STONE_OVERWORLD)) {
            return NatureBlocks.EBONSTONE.get();
        }
        if (source.is(Tags.Blocks.COBBLESTONE)) {
            return NatureBlocks.COBBLED_EBONSTONE.get();
        }
        if (source.is(Tags.Blocks.ORES_REDSTONE)) {
            return OreBlocks.CORRUPTION_REDSTONE_ORE.get();
        }
        if (source.is(Tags.Blocks.ORES_COAL)) {
            return OreBlocks.CORRUPTION_COAL_ORE.get();
        }
        if (source.is(Tags.Blocks.ORES_LAPIS)) {
            return OreBlocks.CORRUPTION_LAPIS_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_RUBY)) {
            return OreBlocks.CORRUPTION_RUBY_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_AMBER)) {
            return OreBlocks.CORRUPTION_AMBER_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_TOPAZ)) {
            return OreBlocks.CORRUPTION_TOPAZ_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_JADE)) {
            return OreBlocks.CORRUPTION_JADE_ORE.get();
        }
        if (source.is(Tags.Blocks.ORES_EMERALD)) {
            return OreBlocks.CORRUPTION_EMERALD_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_SAPPHIRE)) {
            return OreBlocks.CORRUPTION_SAPPHIRE_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_AMETHYST)) {
            return OreBlocks.CORRUPTION_AMETHYST_ORE.get();
        }
        if (source.is(Tags.Blocks.ORES_DIAMOND)) {
            return OreBlocks.CORRUPTION_DIAMOND_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_TIN)) {
            return OreBlocks.CORRUPTION_TIN_ORE.get();
        }
        if (source.is(Tags.Blocks.ORES_COPPER)) {
            return OreBlocks.CORRUPTION_COPPER_ORE.get();
        }
        if (source.is(Tags.Blocks.ORES_IRON)) {
            return OreBlocks.CORRUPTION_IRON_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_LEAD)) {
            return OreBlocks.CORRUPTION_LEAD_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_SILVER)) {
            return OreBlocks.CORRUPTION_SILVER_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_TUNGSTEN)) {
            return OreBlocks.CORRUPTION_TUNGSTEN_ORE.get();
        }
        if (source.is(Tags.Blocks.ORES_GOLD)) {
            return OreBlocks.CORRUPTION_GOLD_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_PLATINUM)) {
            return OreBlocks.CORRUPTION_PLATINUM_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_DEMONITE)) {
            return OreBlocks.CORRUPTION_DEMONITE_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_CRIMTANE)) {
            return OreBlocks.CORRUPTION_CRIMTANE_ORE.get();
        }
        return null;
    }
}
