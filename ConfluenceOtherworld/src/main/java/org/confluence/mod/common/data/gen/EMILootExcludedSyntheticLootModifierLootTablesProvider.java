package org.confluence.mod.common.data.gen;

import net.minecraft.core.*;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.common.data.gen.loot.modifiers.EMILootDataTableExclusions;
import org.confluence.mod.common.data.gen.loot.modifiers.*;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class EMILootExcludedSyntheticLootModifierLootTablesProvider implements DataProvider {
    private final PackOutput.PathProvider pathProvider;
    private final List<Function<HolderLookup.Provider, SyntheticLootTableProvider>> providers;
    private final CompletableFuture<HolderLookup.Provider> registries;


    public EMILootExcludedSyntheticLootModifierLootTablesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "emi_loot_data");
        this.providers = List.of(
                AddEntityLootConfluenceSubProvider::new,
                AddBlockLootConfluenceSubProvider::new,
                AddChestLootConfluenceSubProvider::new
        );
        this.registries = lookup;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        return this.registries.thenCompose((provider) -> {
            var tableExclusions = new EMILootDataTableExclusions();
            this.providers.forEach(i -> {
                tableExclusions.excludedPaths.addAll(i.apply(provider).getSyntheticLootTablePaths());
            });
            Path path = this.pathProvider.json(ResourceLocation.fromNamespaceAndPath("emi_loot", "table_exclusions"));
            return DataProvider.saveStable(cachedOutput, provider, EMILootDataTableExclusions.CODEC, tableExclusions, path);
        });
    }

    @Override
    public String getName() {
        return "EMILoot Data Table Exclusions";
    }
}
