package org.confluence.mod.common.block.common;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.neoforged.neoforge.registries.DeferredBlock;

public class StoneBlockSet {
    private final Builder builder;

    public StoneBlockSet(Builder builder) {
        this.builder = builder;
    }

    public static class Builder {
        private DeferredBlock<Block> bricks;
        private DeferredBlock<StairBlock> stair;
        private DeferredBlock<SlabBlock> slab;
    }
}
