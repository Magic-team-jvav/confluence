package org.confluence.mod.common.block.natural.spreadable;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.block.OreBlocks;
import org.jetbrains.annotations.Nullable;

public class TheHallowConversionTable extends ConversionTable {
    @Override
    protected @Nullable Block getTarget(BlockState source) {
        if (source.is(BlockTags.LOGS)) return NatureBlocks.PEARL_LOG_BLOCKS.getLog().get();
        if (source.is(BlockTags.LEAVES)) return NatureBlocks.PEARL_LOG_BLOCKS.getLeaves().get();
        if (source.is(BlockTags.BASE_STONE_OVERWORLD)) return NatureBlocks.PEARLSTONE.get();
        if (source.is(ModTags.Blocks.HALLOW_CONVERSION_GRASS_BLOCK)) return NatureBlocks.HALLOW_GRASS_BLOCK.get();
        if (source.is(ModTags.Blocks.HALLOW_CONVERSION_SHORT_GRASS)) return NatureBlocks.HALLOW_GRASS.get();
        if (source.is(ModTags.Blocks.HALLOW_CONVERSION_PACKED_ICE)) return NatureBlocks.PINK_PACKED_ICE.get();
        if (source.is(ModTags.Blocks.HALLOW_CONVERSION_ICE)) return NatureBlocks.PINK_ICE.get();
        if (source.is(ModTags.Blocks.HALLOW_CONVERSION_SAND)) return NatureBlocks.PEARLSAND.get();
        if (source.is(ModTags.Blocks.HALLOW_CONVERSION_SANDSTONE)) return NatureBlocks.PEARLSANDSTONE.get();
        if (source.is(ModTags.Blocks.HALLOW_CONVERSION_HARDENED_SAND_BLOCK)) return NatureBlocks.HARDENED_PEARLSAND_BLOCK.get();
        if (source.is(ModTags.Blocks.HALLOW_CONVERSION_MOIST_SAND_BLOCK)) return NatureBlocks.MOISTENED_PEARLSAND_BLOCK.get();

        Block block = source.getBlock();
        if (block == Blocks.REDSTONE_ORE) return OreBlocks.SANCTIFICATION_REDSTONE_ORE.get();
        if (block == Blocks.COAL_ORE) return OreBlocks.SANCTIFICATION_COAL_ORE.get();
        if (block == Blocks.LAPIS_ORE) return OreBlocks.SANCTIFICATION_LAPIS_ORE.get();
        if (block == Blocks.COPPER_ORE) return OreBlocks.SANCTIFICATION_COPPER_ORE.get();
        if (block == Blocks.IRON_ORE) return OreBlocks.SANCTIFICATION_IRON_ORE.get();
        if (block == Blocks.EMERALD_ORE) return OreBlocks.SANCTIFICATION_EMERALD_ORE.get();
        if (block == Blocks.DIAMOND_ORE) return OreBlocks.SANCTIFICATION_DIAMOND_ORE.get();
        if (block == Blocks.GOLD_ORE) return OreBlocks.SANCTIFICATION_GOLD_ORE.get();
        if (block == OreBlocks.TIN_ORE.get()) return OreBlocks.SANCTIFICATION_TIN_ORE.get();
        if (block == OreBlocks.LEAD_ORE.get()) return OreBlocks.SANCTIFICATION_LEAD_ORE.get();
        if (block == OreBlocks.SILVER_ORE.get()) return OreBlocks.SANCTIFICATION_SILVER_ORE.get();
        if (block == OreBlocks.TUNGSTEN_ORE.get()) return OreBlocks.SANCTIFICATION_TUNGSTEN_ORE.get();
        if (block == OreBlocks.PLATINUM_ORE.get()) return OreBlocks.SANCTIFICATION_PLATINUM_ORE.get();

        if (source.is(ModTags.Blocks.ORES_TIN)) return OreBlocks.SANCTIFICATION_TIN_ORE.get();
        if (block == OreBlocks.CORRUPTION_LEAD_ORE.get()) return OreBlocks.SANCTIFICATION_LEAD_ORE.get();
        if (block == OreBlocks.CORRUPTION_SILVER_ORE.get()) return OreBlocks.SANCTIFICATION_SILVER_ORE.get();
        if (block == OreBlocks.CORRUPTION_TUNGSTEN_ORE.get()) return OreBlocks.SANCTIFICATION_TUNGSTEN_ORE.get();
        if (block == OreBlocks.CORRUPTION_PLATINUM_ORE.get()) return OreBlocks.SANCTIFICATION_PLATINUM_ORE.get();
        if (block == OreBlocks.CORRUPTION_COAL_ORE.get()) return OreBlocks.SANCTIFICATION_COAL_ORE.get();
        if (block == OreBlocks.CORRUPTION_COPPER_ORE.get()) return OreBlocks.SANCTIFICATION_COPPER_ORE.get();
        if (block == OreBlocks.CORRUPTION_IRON_ORE.get()) return OreBlocks.SANCTIFICATION_IRON_ORE.get();
        if (block == OreBlocks.CORRUPTION_GOLD_ORE.get()) return OreBlocks.SANCTIFICATION_GOLD_ORE.get();
        if (block == OreBlocks.CORRUPTION_DIAMOND_ORE.get()) return OreBlocks.SANCTIFICATION_DIAMOND_ORE.get();
        if (block == OreBlocks.CORRUPTION_REDSTONE_ORE.get()) return OreBlocks.SANCTIFICATION_REDSTONE_ORE.get();
        if (block == OreBlocks.FLESHIFICATION_TIN_ORE.get()) return OreBlocks.SANCTIFICATION_TIN_ORE.get();
        if (block == OreBlocks.FLESHIFICATION_LEAD_ORE.get()) return OreBlocks.SANCTIFICATION_LEAD_ORE.get();
        if (block == OreBlocks.FLESHIFICATION_SILVER_ORE.get()) return OreBlocks.SANCTIFICATION_SILVER_ORE.get();
        if (block == OreBlocks.FLESHIFICATION_TUNGSTEN_ORE.get()) return OreBlocks.SANCTIFICATION_TUNGSTEN_ORE.get();
        if (block == OreBlocks.FLESHIFICATION_PLATINUM_ORE.get()) return OreBlocks.SANCTIFICATION_PLATINUM_ORE.get();
        if (block == OreBlocks.FLESHIFICATION_COAL_ORE.get()) return OreBlocks.SANCTIFICATION_COAL_ORE.get();
        if (block == OreBlocks.FLESHIFICATION_COPPER_ORE.get()) return OreBlocks.SANCTIFICATION_COPPER_ORE.get();
        if (block == OreBlocks.FLESHIFICATION_IRON_ORE.get()) return OreBlocks.SANCTIFICATION_IRON_ORE.get();
        if (block == OreBlocks.FLESHIFICATION_GOLD_ORE.get()) return OreBlocks.SANCTIFICATION_GOLD_ORE.get();
        if (block == OreBlocks.FLESHIFICATION_DIAMOND_ORE.get()) return OreBlocks.SANCTIFICATION_DIAMOND_ORE.get();
        if (block == OreBlocks.FLESHIFICATION_REDSTONE_ORE.get()) return OreBlocks.SANCTIFICATION_REDSTONE_ORE.get();
        if (block == OreBlocks.DEMONITE_ORE.get()) return OreBlocks.SANCTIFICATION_DEMONITE_ORE.get();
        if (block == OreBlocks.CRIMTANE_ORE.get()) return OreBlocks.SANCTIFICATION_CRIMTANE_ORE.get();
        if (block == OreBlocks.FLESHIFICATION_DEMONITE_ORE.get()) return OreBlocks.SANCTIFICATION_DEMONITE_ORE.get();
        if (block == OreBlocks.FLESHIFICATION_CRIMTANE_ORE.get()) return OreBlocks.SANCTIFICATION_CRIMTANE_ORE.get();
        if (block == OreBlocks.CORRUPTION_DEMONITE_ORE.get()) return OreBlocks.SANCTIFICATION_DEMONITE_ORE.get();
        if (block == OreBlocks.CORRUPTION_CRIMTANE_ORE.get()) return OreBlocks.SANCTIFICATION_CRIMTANE_ORE.get();
        return null;
    }
}
