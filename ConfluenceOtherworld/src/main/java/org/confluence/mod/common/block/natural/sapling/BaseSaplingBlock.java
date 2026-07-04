package org.confluence.mod.common.block.natural.sapling;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BaseSaplingBlock extends SaplingBlock {
    private Supplier<List<? extends Block>> blocksSup;
    private Set<Block> cache;
    private final TagKey<Block> tags;

    public BaseSaplingBlock(AbstractTreeGrower grower, Properties properties, @Nullable TagKey<Block> tags, Supplier<List<? extends Block>> blocks) {
        super(grower, properties);
        this.blocksSup = blocks;
        this.tags = tags;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState blockBelow = level.getBlockState(below);
        if (cache == null) {
            this.cache = blocksSup.get().stream().collect(Collectors.toUnmodifiableSet());
            this.blocksSup = null;
        }
        return cache.contains(blockBelow.getBlock()) && (tags == null || blockBelow.is(tags));
    }
}
