package org.confluence.terraentity.data.gen.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class TELootTableProvider extends LootTableProvider {

    public TELootTableProvider(PackOutput output, Set<ResourceKey<LootTable>> requiredTables, List<SubProviderEntry> subProviders, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, requiredTables, subProviders, registries);
    }

    public static LootTableProvider getProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProviderFuture) {
        return new LootTableProvider(output, Collections.emptySet(),
                List.of(
                        new SubProviderEntry(TEBlockLootProvider::new, LootContextParamSets.BLOCK),
                        new SubProviderEntry(TEEntityLootProvider::new, LootContextParamSets.ENTITY),
                        new SubProviderEntry(TENPCLoot::new, LootContextParamSets.EMPTY),
                        new SubProviderEntry(TESubLoot::new, LootContextParamSets.EMPTY)
                ),
                lookupProviderFuture);
    }
}