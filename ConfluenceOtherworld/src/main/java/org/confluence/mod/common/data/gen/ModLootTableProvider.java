package org.confluence.mod.common.data.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.confluence.mod.common.data.gen.loot.BlockSubProvider;
import org.confluence.mod.common.data.gen.loot.ChestSubProvider;
import org.confluence.mod.common.data.gen.loot.EntitySubProvider;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends LootTableProvider {
    public ModLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, Set.of(), List.of(
                new SubProviderEntry(BlockSubProvider::new, LootContextParamSets.BLOCK),
                new SubProviderEntry(EntitySubProvider::new, LootContextParamSets.ENTITY),
                new SubProviderEntry(ChestSubProvider::new, LootContextParamSets.CHEST)
        ), lookup);
    }
}
