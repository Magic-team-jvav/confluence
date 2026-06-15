package org.confluence.mod.common.block.natural.sapling;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Supplier;

public class BaseSaplingBlock extends SaplingBlock {
    private Supplier<? extends Block>[] blocksSupplier;
    private Block[] blocks;
    private final TagKey<Block> tags;

    @SafeVarargs
    public BaseSaplingBlock(AbstractTreeGrower grower, Properties properties, @Nullable TagKey<Block> tags, Supplier<? extends Block>... blocks) {
        super(grower, properties);
        this.blocksSupplier = blocks;
        this.tags = tags;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState blockBelow = level.getBlockState(below);
        if (blocks == null) {
            this.blocks = Arrays.stream(blocksSupplier).map(Supplier::get).toArray(Block[]::new);
            this.blocksSupplier = null;
        }
        return (blocks.length == 0 || Arrays.stream(blocks).anyMatch(blockBelow::is)) && (tags == null || blockBelow.is(tags));
    }
}
