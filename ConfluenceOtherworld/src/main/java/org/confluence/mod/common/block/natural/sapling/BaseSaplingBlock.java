package org.confluence.mod.common.block.natural.sapling;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Supplier;

public class BaseSaplingBlock extends SaplingBlock {
    private Supplier<? extends Block>[] blocksSupplier;
    private Block[] blocks;
    private final TagKey<Block> tags;

    @SafeVarargs
    public BaseSaplingBlock(TreeGrower grower, Properties properties, @Nullable TagKey<Block> tags, Supplier<? extends Block>... block) {
        super(grower, properties);
        this.blocksSupplier = block;
        this.tags = tags;
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockPos below = pPos.below();
        BlockState blockBelow = pLevel.getBlockState(below);
        if (blocks == null) {
            this.blocks = Arrays.stream(blocksSupplier).map(Supplier::get).toArray(Block[]::new);
            this.blocksSupplier = null;
        }
        return (blocks.length == 0 || Arrays.stream(blocks).anyMatch(blockBelow::is)) && (tags == null || blockBelow.is(tags));
    }
}
