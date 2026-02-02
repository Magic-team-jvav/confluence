package org.confluence.mod.common.data.gen;

import com.google.common.collect.Multimap;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.RandomSequence;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.confluence.mod.common.data.gen.loot.modifiers.AddBlockLootDirectDropsSubProvider;
import org.confluence.mod.common.data.gen.loot.modifiers.AddChestLootDirectDropsSubProvider;
import org.confluence.mod.common.data.gen.loot.modifiers.AddEntityLootDirectDropsSubProvider;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class EMILootDirectDropsProvider implements DataProvider {
    private final PackOutput.PathProvider pathProvider;
    private final List<EMILootDirectDropsProvider.SubProviderEntry> subProviders;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public EMILootDirectDropsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        this(output, List.of(
                new EMILootDirectDropsProvider.SubProviderEntry(AddEntityLootDirectDropsSubProvider::new, LootContextParamSets.ENTITY),
                new EMILootDirectDropsProvider.SubProviderEntry(AddBlockLootDirectDropsSubProvider::new, LootContextParamSets.BLOCK),
                new EMILootDirectDropsProvider.SubProviderEntry(AddChestLootDirectDropsSubProvider::new, LootContextParamSets.CHEST)
        ), lookup);
    }

    public EMILootDirectDropsProvider(PackOutput output, List<EMILootDirectDropsProvider.SubProviderEntry> subProviders, CompletableFuture<HolderLookup.Provider> registries) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "direct_drops");
        this.subProviders = subProviders;
        this.registries = registries;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        return this.registries.thenCompose((provider) -> {
            return this.run(cachedOutput, provider);
        });
    }

    public List<EMILootDirectDropsProvider.SubProviderEntry> getTables() {
        return this.subProviders;
    }

    private static ResourceLocation sequenceIdForLootTable(ResourceKey<LootTable> lootTable) {
        return lootTable.location();
    }

    protected void validate(WritableRegistry<LootTable> lootTables, ValidationContext validationcontext) {
        lootTables.entrySet().forEach((entry) -> {
            LootTable lootTable = entry.getValue();
            ResourceKey<LootTable> key = entry.getKey();
            lootTable.validate(
                validationcontext.setParams(lootTable.getParamSet())
                    .enterElement("{" + key.location() + "}", key)
            );
        });
    }

    private CompletableFuture<?> run(CachedOutput output, HolderLookup.Provider provider) {
        WritableRegistry<LootTable> lootTables = new MappedRegistry(Registries.LOOT_TABLE, Lifecycle.experimental());
        Map<RandomSupport.Seed128bit, ResourceLocation> map = new Object2ObjectOpenHashMap();
        this.getTables().forEach((entry) -> {
            entry.provider().apply(provider).generate((key, builder) -> {
                ResourceLocation resourcelocation = sequenceIdForLootTable(key);
                ResourceLocation seedKey = map.put(RandomSequence.seedForKey(resourcelocation), resourcelocation);
                if (seedKey != null) {
                    String seed = String.valueOf(seedKey);
                    Util.logAndPauseIfInIde("Loot table random sequence seed collision on " + seed + " and " + key.location());
                }

                LootTable loottable = builder.setParamSet(entry.paramSet).build();
                lootTables.register(key, loottable, RegistrationInfo.BUILT_IN);
            });
        });
        var problemreporter$Collector = new ProblemReporter.Collector();
        var lootTablesProvider = (new RegistryAccess.ImmutableRegistryAccess(List.of(lootTables))).freeze().asGetterLookup();
        var validationcontext = new ValidationContext(problemreporter$Collector, LootContextParamSets.ALL_PARAMS, lootTablesProvider);
        this.validate(lootTables, validationcontext);
        Multimap<String, String> multimap = problemreporter$Collector.get();
        if (!multimap.isEmpty()) {
            multimap.forEach((file, error) -> {
                LOGGER.warn("Found validation problem in {}: {}", file, error);
            });
//            throw new IllegalStateException("Failed to validate loot tables, see logs");
            LOGGER.warn("Failed to validate loot tables, see logs");
        }
        return CompletableFuture.allOf(lootTables.entrySet().stream().map((entry) -> {
            ResourceKey<LootTable> resourcekey1 = entry.getKey();
            LootTable loottable = entry.getValue();
            Path path = this.pathProvider.json(resourcekey1.location());
            return DataProvider.saveStable(output, provider, LootTable.DIRECT_CODEC, loottable, path);
        }).toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "EMILoot Direct Drops";
    }

    public record SubProviderEntry(Function<HolderLookup.Provider, LootTableSubProvider> provider, LootContextParamSet paramSet) {}
}
