package org.confluence.terraentity.data.gen.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;


import java.util.Collections;
import java.util.Set;

public class ModBlockLootProvider extends BlockLootSubProvider {

    public static final Set<Block> BLOCK = Set.of(
//            ModBlock.YU_YAN_ORE.get(),
//            ModBlock.BA_JIN_ORE.get()
    );

    public ModBlockLootProvider(HolderLookup.Provider registries) {
        super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags(),registries);
    }

    @Override
    protected void generate() {
        getKnownBlocks().forEach(this::dropSelf);

    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BLOCK;
//        return ModBlocks.BLOCKS.getEntries().stream().map(entry -> entry.get()).collect(Collectors.toList());
    }
}