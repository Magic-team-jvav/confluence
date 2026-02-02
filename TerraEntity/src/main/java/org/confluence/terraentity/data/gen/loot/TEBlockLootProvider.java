package org.confluence.terraentity.data.gen.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.terraentity.init.block.TEFigureBlocks;

import java.util.Collections;
import java.util.stream.Collectors;

public class TEBlockLootProvider extends BlockLootSubProvider {

    public TEBlockLootProvider(HolderLookup.Provider registries) {
        super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags(),registries);
    }

    @Override
    protected void generate() {
        getKnownBlocks().forEach(this::dropSelf);

    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
//        return BLOCK;
        return TEFigureBlocks.BLOCKS.getEntries().stream().map(DeferredHolder::get).collect(Collectors.toList());
    }
}