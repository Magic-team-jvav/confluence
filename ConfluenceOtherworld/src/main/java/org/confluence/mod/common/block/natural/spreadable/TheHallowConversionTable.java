package org.confluence.mod.common.block.natural.spreadable;

import net.minecraft.core.Holder;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.common.Tags;
import org.confluence.mod.common.block.natural.CattailsBodyBlock;
import org.confluence.mod.common.block.natural.CattailsHeadBlock;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.block.OreBlocks;
import org.jetbrains.annotations.Nullable;

public class TheHallowConversionTable extends ConversionTable {
    @Override
    protected @Nullable Block getTarget(BlockState source) {
        Block block = source.getBlock();

        if (block == Blocks.TALL_GRASS) {
            return source.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER ? NatureBlocks.HALLOW_GRASS.get() : Blocks.AIR;
        }
        if (block instanceof CattailsHeadBlock) return NatureBlocks.CRIMSON_CATTAILS_HEAD.get();
        if (block instanceof CattailsBodyBlock) return NatureBlocks.CRIMSON_CATTAILS_BODY.get();

        Holder<Block> holder = block.builtInRegistryHolder();

        if (holder.is(BlockTags.LOGS)) return NatureBlocks.PEARL_LOG_BLOCKS.LOG.get();
        if (holder.is(BlockTags.LEAVES)) return NatureBlocks.PEARL_LOG_BLOCKS.LEAVES.get();
        if (holder.is(BlockTags.BASE_STONE_OVERWORLD)) return NatureBlocks.PEARLSTONE.get();
        if (holder.is(Tags.Blocks.COBBLESTONES)) return NatureBlocks.COBBLED_PEARLSTONE.get();

        if (holder.is(ModTags.Blocks.HALLOW_CONVERSION_GRASS_BLOCK)) return NatureBlocks.HALLOW_GRASS_BLOCK.get();
        if (holder.is(ModTags.Blocks.HALLOW_CONVERSION_SHORT_GRASS)) return NatureBlocks.HALLOW_GRASS.get();
        if (holder.is(ModTags.Blocks.HALLOW_CONVERSION_PACKED_ICE)) return NatureBlocks.PINK_PACKED_ICE.get();
        if (holder.is(ModTags.Blocks.HALLOW_CONVERSION_ICE)) return NatureBlocks.PINK_ICE.get();
        if (holder.is(ModTags.Blocks.HALLOW_CONVERSION_SAND)) return NatureBlocks.PEARLSAND.get();
        if (holder.is(ModTags.Blocks.HALLOW_CONVERSION_SANDSTONE)) return NatureBlocks.PEARLSANDSTONE.get();
        if (holder.is(ModTags.Blocks.HALLOW_CONVERSION_HARDENED_SAND_BLOCK)) return NatureBlocks.HARDENED_PEARLSAND_BLOCK.get();
        if (holder.is(ModTags.Blocks.HALLOW_CONVERSION_MOIST_SAND_BLOCK)) return NatureBlocks.MOISTENED_PEARLSAND_BLOCK.get();

        if (holder.is(Tags.Blocks.ORES_REDSTONE)) return OreBlocks.SANCTIFICATION_REDSTONE_ORE.get();
        if (holder.is(Tags.Blocks.ORES_COAL)) return OreBlocks.SANCTIFICATION_COAL_ORE.get();
        if (holder.is(Tags.Blocks.ORES_LAPIS)) return OreBlocks.SANCTIFICATION_LAPIS_ORE.get();
        if (holder.is(ModTags.Blocks.ORES_RUBY)) return OreBlocks.SANCTIFICATION_RUBY_ORE.get();
        if (holder.is(ModTags.Blocks.ORES_AMBER)) return OreBlocks.SANCTIFICATION_AMBER_ORE.get();
        if (holder.is(ModTags.Blocks.ORES_TOPAZ)) return OreBlocks.SANCTIFICATION_TOPAZ_ORE.get();
        if (holder.is(ModTags.Blocks.ORES_JADE)) return OreBlocks.SANCTIFICATION_JADE_ORE.get();
        if (holder.is(Tags.Blocks.ORES_EMERALD)) return OreBlocks.SANCTIFICATION_EMERALD_ORE.get();
        if (holder.is(ModTags.Blocks.ORES_SAPPHIRE)) return OreBlocks.SANCTIFICATION_SAPPHIRE_ORE.get();
        if (holder.is(ModTags.Blocks.ORES_AMETHYST)) return OreBlocks.SANCTIFICATION_AMETHYST_ORE.get();
        if (holder.is(Tags.Blocks.ORES_DIAMOND)) return OreBlocks.SANCTIFICATION_DIAMOND_ORE.get();
        if (holder.is(ModTags.Blocks.ORES_TIN)) return OreBlocks.SANCTIFICATION_TIN_ORE.get();
        if (holder.is(Tags.Blocks.ORES_COPPER)) return OreBlocks.SANCTIFICATION_COPPER_ORE.get();
        if (holder.is(Tags.Blocks.ORES_IRON)) return OreBlocks.SANCTIFICATION_IRON_ORE.get();
        if (holder.is(ModTags.Blocks.ORES_LEAD)) return OreBlocks.SANCTIFICATION_LEAD_ORE.get();
        if (holder.is(ModTags.Blocks.ORES_SILVER)) return OreBlocks.SANCTIFICATION_SILVER_ORE.get();
        if (holder.is(ModTags.Blocks.ORES_TUNGSTEN)) return OreBlocks.SANCTIFICATION_TUNGSTEN_ORE.get();
        if (holder.is(Tags.Blocks.ORES_GOLD)) return OreBlocks.SANCTIFICATION_GOLD_ORE.get();
        if (holder.is(ModTags.Blocks.ORES_PLATINUM)) return OreBlocks.SANCTIFICATION_PLATINUM_ORE.get();
        if (holder.is(ModTags.Blocks.ORES_DEMONITE)) return OreBlocks.SANCTIFICATION_DEMONITE_ORE.get();
        if (holder.is(ModTags.Blocks.ORES_CRIMTANE)) return OreBlocks.SANCTIFICATION_CRIMTANE_ORE.get();

        return null;
    }
}
