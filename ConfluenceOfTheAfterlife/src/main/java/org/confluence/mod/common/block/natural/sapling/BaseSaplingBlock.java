package org.confluence.mod.common.block.natural.sapling;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;

public class BaseSaplingBlock extends SaplingBlock {
    private final Block[] block;
    private final TagKey<Block> tags;

    public BaseSaplingBlock(TreeGrower pTreeGrower, Block... block) {
        super(pTreeGrower, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING));
        this.block = block;
        this.tags = null;
    }

    public BaseSaplingBlock(TreeGrower pTreeGrower, TagKey<Block> tags, Block... block) {
        super(pTreeGrower, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING));
        this.block = block;
        this.tags = tags;
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockPos below = pPos.below();
        BlockState blockBelow = pLevel.getBlockState(below);
        return Arrays.asList(block).contains(blockBelow.getBlock()) || (tags != null && blockBelow.is(tags));
    }
}
