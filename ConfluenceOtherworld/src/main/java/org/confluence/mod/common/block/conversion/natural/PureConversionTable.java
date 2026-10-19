package org.confluence.mod.common.block.conversion.natural;

import net.minecraft.tags.BlockTags;
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

public class PureConversionTable extends ConversionTable {
    @Override
    protected @Nullable Block getTarget(BlockState source, boolean hardmode) {
        if (source.is(Blocks.TALL_GRASS)) {
            return source.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER ? Blocks.SHORT_GRASS : Blocks.AIR;
        }
        if (source.getBlock() instanceof CattailBlock && !source.is(NatureBlocks.CATTAIL_BLOCK.get())) {
            return NatureBlocks.CATTAIL_BLOCK.get();
        }
        if (source.is(BlockTags.LOGS)) {
            return Blocks.OAK_LOG;
        }
        if (source.is(BlockTags.LEAVES)) {
            return Blocks.OAK_LEAVES;
        }
        if (source.is(ModTags.Blocks.PURE_CONVERSION_GRASS_BLOCK)) {
            return Blocks.GRASS_BLOCK;
        }
        if (source.is(ModTags.Blocks.PURE_CONVERSION_JUNGLE_GRASS_BLOCK)) {
            return NatureBlocks.JUNGLE_GRASS_BLOCK.get();
        }
        if (source.is(ModTags.Blocks.PURE_CONVERSION_SHORT_GRASS)) {
            return Blocks.SHORT_GRASS;
        }
        if (source.is(ModTags.Blocks.PURE_CONVERSION_PACKED_ICE)) {
            return Blocks.PACKED_ICE;
        }
        if (source.is(ModTags.Blocks.PURE_CONVERSION_ICE)) {
            return Blocks.ICE;
        }
        if (source.is(ModTags.Blocks.PURE_CONVERSION_ICE_TAPERED_BLOCK)) {
            return NatureBlocks.ICE_TAPERED_BLOCK.get();
        }
        if (source.is(ModTags.Blocks.PURE_CONVERSION_SAND)) {
            return Blocks.SAND;
        }
        if (source.is(ModTags.Blocks.PURE_CONVERSION_SANDSTONE)) {
            return Blocks.SANDSTONE;
        }
        if (source.is(ModTags.Blocks.PURE_CONVERSION_SANDSTONE_PILES)) {
            return NatureBlocks.SMALL_SANDSTONE_PILES.get();
        }
        if (source.is(ModTags.Blocks.PURE_CONVERSION_HARDENED_SAND_BLOCK)) {
            return NatureBlocks.HARDENED_SAND_BLOCK.get();
        }
        if (source.is(ModTags.Blocks.PURE_CONVERSION_MOIST_SAND_BLOCK)) {
            return NatureBlocks.MOISTENED_SAND_BLOCK.get();
        }
        if (source.is(ModTags.Blocks.PURE_CONVERSION_CACTUS)) {
            return Blocks.CACTUS;
        }
        if (source.is(BlockTags.BASE_STONE_OVERWORLD)) {
            return Blocks.STONE;
        }
        if (source.is(Tags.Blocks.COBBLESTONES)) {
            return Blocks.COBBLESTONE;
        }
        if (source.is(Tags.Blocks.ORES_REDSTONE)) {
            return Blocks.REDSTONE_ORE;
        }
        if (source.is(Tags.Blocks.ORES_COAL)) {
            return Blocks.COAL_ORE;
        }
        if (source.is(Tags.Blocks.ORES_LAPIS)) {
            return Blocks.LAPIS_ORE;
        }
        if (source.is(ModTags.Blocks.ORES_RUBY)) {
            return OreBlocks.RUBY_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_AMBER)) {
            return OreBlocks.AMBER_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_TOPAZ)) {
            return OreBlocks.TOPAZ_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_JADE)) {
            return OreBlocks.JADE_ORE.get();
        }
        if (source.is(Tags.Blocks.ORES_EMERALD)) {
            return Blocks.EMERALD_ORE;
        }
        if (source.is(ModTags.Blocks.ORES_SAPPHIRE)) {
            return OreBlocks.SAPPHIRE_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_AMETHYST)) {
            return OreBlocks.AMETHYST_ORE.get();
        }
        if (source.is(Tags.Blocks.ORES_DIAMOND)) {
            return Blocks.DIAMOND_ORE;
        }
        if (source.is(ModTags.Blocks.ORES_TIN)) {
            return OreBlocks.TIN_ORE.get();
        }
        if (source.is(Tags.Blocks.ORES_COPPER)) {
            return Blocks.COPPER_ORE;
        }
        if (source.is(Tags.Blocks.ORES_IRON)) {
            return Blocks.IRON_ORE;
        }
        if (source.is(ModTags.Blocks.ORES_LEAD)) {
            return OreBlocks.LEAD_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_SILVER)) {
            return OreBlocks.SILVER_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_TUNGSTEN)) {
            return OreBlocks.TUNGSTEN_ORE.get();
        }
        if (source.is(Tags.Blocks.ORES_GOLD)) {
            return Blocks.GOLD_ORE;
        }
        if (source.is(ModTags.Blocks.ORES_PLATINUM)) {
            return OreBlocks.PLATINUM_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_DEMONITE)) {
            return OreBlocks.DEMONITE_ORE.get();
        }
        if (source.is(ModTags.Blocks.ORES_CRIMTANE)) {
            return OreBlocks.CRIMTANE_ORE.get();
        }
        return null;
    }
}
