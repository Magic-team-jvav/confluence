package org.confluence.terra_furniture.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.terra_furniture.common.init.TFBlocks;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class TFLootTableProvider extends LootTableProvider {
    public TFLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Set.of(), List.of(
                new SubProviderEntry(BlockSub::new, LootContextParamSets.BLOCK)
        ), registries);
    }

    public static class BlockSub extends BlockLootSubProvider {
        List<Block> blockList = TFBlocks.BLOCKS.getEntries()
                .stream()
                .map(e -> (Block) e.value())
                .toList();
        protected BlockSub(HolderLookup.Provider provider) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
        }

        @Override
        protected void generate() {
            for (Block block : blockList) {
                if (!(block instanceof DoorBlock)){
                    dropSelf(block);
                } else {
                    map.put(block.getLootTable(),createDoorTable(block));
                }
            }
        }

        @Override
        protected @NotNull Iterable<Block> getKnownBlocks() {
            return TFBlocks.BLOCKS.getEntries().stream().map(DeferredHolder::get).collect(Collectors.toUnmodifiableList());
        }
    }
}
