package org.confluence.mod.common.block.natural.spreadable.conversion_table;

import net.minecraft.core.Holder;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.common.Tags;
import org.confluence.mod.common.block.natural.CattailBlock;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.block.OreBlocks;
import org.jetbrains.annotations.Nullable;

public class TheCrimsonConversionTable extends ConversionTable {
    @Override
    protected @Nullable Block getTarget(BlockState source, boolean hardmode) {
        Block block = source.getBlock();

        if (block == Blocks.TALL_GRASS) {
            return source.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER ? NatureBlocks.CRIMSON_GRASS.get() : Blocks.AIR;
        }
        if (block instanceof CattailBlock && block != NatureBlocks.CRIMSON_CATTAIL_BLOCK.get()) {
            return NatureBlocks.CRIMSON_CATTAIL_BLOCK.get();
        }

        Holder<Block> holder = block.builtInRegistryHolder();

        if (holder.is(BlockTags.LOGS)) return NatureBlocks.SHADOW_LOG_BLOCKS.LOG.get();
        if (holder.is(BlockTags.LEAVES)) return NatureBlocks.SHADOW_LOG_BLOCKS.LEAVES.get();
        if (holder.is(ModTags.Blocks.CRIMSON_CONVERSION_DIRT)) return Blocks.DIRT;
        if (holder.is(ModTags.Blocks.CRIMSON_CONVERSION_GRASS_BLOCK)) {
            return NatureBlocks.CRIMSON_GRASS_BLOCK.get();
        }
        if (holder.is(ModTags.Blocks.CRIMSON_CONVERSION_JUNGLE_GRASS_BLOCK)) {
            return NatureBlocks.CRIMSON_JUNGLE_GRASS_BLOCK.get();
        }
        if (holder.is(ModTags.Blocks.CRIMSON_CONVERSION_SHORT_GRASS)) {
            return NatureBlocks.CRIMSON_GRASS.get();
        }
        if (holder.is(ModTags.Blocks.CRIMSON_CONVERSION_PACKED_ICE)) {
            return NatureBlocks.RED_PACKED_ICE.get();
        }
        if (holder.is(ModTags.Blocks.CRIMSON_CONVERSION_ICE)) return NatureBlocks.RED_ICE.get();
        if (holder.is(ModTags.Blocks.CRIMSON_CONVERSION_SAND)) return NatureBlocks.CRIMSAND.get();
        if (holder.is(ModTags.Blocks.CRIMSON_CONVERSION_SANDSTONE)) {
            return NatureBlocks.CRIMSANDSTONE.get();
        }
        if (holder.is(ModTags.Blocks.CRIMSON_CONVERSION_HARDENED_SAND_BLOCK)) {
            return NatureBlocks.HARDENED_CRIMSAND_BLOCK.get();
        }
        if (holder.is(ModTags.Blocks.CRIMSON_CONVERSION_MOIST_SAND_BLOCK)) {
            return NatureBlocks.MOISTENED_CRIMSAND_BLOCK.get();
        }
        if (holder.is(ModTags.Blocks.CRIMSON_CONVERSION_CACTUS)) {
            return NatureBlocks.CRIMSON_CACTUS.get();
        }

        if (!hardmode) return null;

        if (holder.is(BlockTags.BASE_STONE_OVERWORLD)) return NatureBlocks.CRIMSTONE.get();
        if (holder.is(Tags.Blocks.COBBLESTONES)) return NatureBlocks.COBBLED_CRIMSTONE.get();
        if (holder.is(Tags.Blocks.ORES_REDSTONE)) {
            return OreBlocks.FLESHIFICATION_REDSTONE_ORE.get();
        }
        if (holder.is(Tags.Blocks.ORES_COAL)) return OreBlocks.FLESHIFICATION_COAL_ORE.get();
        if (holder.is(Tags.Blocks.ORES_LAPIS)) return OreBlocks.FLESHIFICATION_LAPIS_ORE.get();
        if (holder.is(ModTags.Blocks.ORES_RUBY)) return OreBlocks.FLESHIFICATION_RUBY_ORE.get();
        if (holder.is(ModTags.Blocks.ORES_AMBER)) return OreBlocks.FLESHIFICATION_AMBER_ORE.get();
        if (holder.is(ModTags.Blocks.ORES_TOPAZ)) return OreBlocks.FLESHIFICATION_TOPAZ_ORE.get();
        if (holder.is(ModTags.Blocks.ORES_JADE)) return OreBlocks.FLESHIFICATION_JADE_ORE.get();
        if (holder.is(Tags.Blocks.ORES_EMERALD)) return OreBlocks.FLESHIFICATION_EMERALD_ORE.get();
        if (holder.is(ModTags.Blocks.ORES_SAPPHIRE)) {
            return OreBlocks.FLESHIFICATION_SAPPHIRE_ORE.get();
        }
        if (holder.is(ModTags.Blocks.ORES_AMETHYST)) {
            return OreBlocks.FLESHIFICATION_AMETHYST_ORE.get();
        }
        if (holder.is(Tags.Blocks.ORES_DIAMOND)) return OreBlocks.FLESHIFICATION_DIAMOND_ORE.get();
        if (holder.is(ModTags.Blocks.ORES_TIN)) return OreBlocks.FLESHIFICATION_TIN_ORE.get();
        if (holder.is(Tags.Blocks.ORES_COPPER)) return OreBlocks.FLESHIFICATION_COPPER_ORE.get();
        if (holder.is(Tags.Blocks.ORES_IRON)) return OreBlocks.FLESHIFICATION_IRON_ORE.get();
        if (holder.is(ModTags.Blocks.ORES_LEAD)) return OreBlocks.FLESHIFICATION_LEAD_ORE.get();
        if (holder.is(ModTags.Blocks.ORES_SILVER)) return OreBlocks.FLESHIFICATION_SILVER_ORE.get();
        if (holder.is(ModTags.Blocks.ORES_TUNGSTEN)) {
            return OreBlocks.FLESHIFICATION_TUNGSTEN_ORE.get();
        }
        if (holder.is(Tags.Blocks.ORES_GOLD)) return OreBlocks.FLESHIFICATION_GOLD_ORE.get();
        if (holder.is(ModTags.Blocks.ORES_PLATINUM)) {
            return OreBlocks.FLESHIFICATION_PLATINUM_ORE.get();
        }
        if (holder.is(ModTags.Blocks.ORES_DEMONITE)) {
            return OreBlocks.FLESHIFICATION_DEMONITE_ORE.get();
        }
        if (holder.is(ModTags.Blocks.ORES_CRIMTANE)) {
            return OreBlocks.FLESHIFICATION_CRIMTANE_ORE.get();
        }

        return null;
    }
}
