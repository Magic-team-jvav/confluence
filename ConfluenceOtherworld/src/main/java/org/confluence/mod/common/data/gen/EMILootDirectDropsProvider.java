package org.confluence.mod.common.data.gen;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.RandomSequence;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.confluence.mod.common.data.gen.loot.modifiers.AddBlockLootDirectDropsSubProvider;
import org.confluence.mod.common.data.gen.loot.modifiers.AddChestLootDirectDropsSubProvider;
import org.confluence.mod.common.data.gen.loot.modifiers.AddEntityLootDirectDropsSubProvider;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
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

    private CompletableFuture<?> run(CachedOutput output, HolderLookup.Provider provider) {
        Map<ResourceLocation, LootTable> lootTables = Maps.newHashMap();
        Map<RandomSupport.Seed128bit, ResourceLocation> map = new Object2ObjectOpenHashMap<>();
        getTables().forEach(entry -> {
            entry.provider().apply(provider).generate((id, builder) -> {
                ResourceLocation seedKey = map.put(RandomSequence.seedForKey(id), id);
                if (seedKey != null) {
                    String seed = String.valueOf(seedKey);
                    Util.logAndPauseIfInIde("Loot table random sequence seed collision on " + seed + " and " + id);
                }

                LootTable table = builder.setParamSet(entry.paramSet).build();
                lootTables.put(id, table);
            });
        });

        return CompletableFuture.allOf(lootTables.entrySet().stream().map(entry -> {
            ResourceLocation id = entry.getKey();
            LootTable table = entry.getValue();
            Path path = pathProvider.json(id);
            return DataProvider.saveStable(output, LootDataType.TABLE.parser().toJsonTree(table), path);
        }).toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "EMILoot Direct Drops";
    }

    public record SubProviderEntry(Function<HolderLookup.Provider, LootTableSubProvider> provider, LootContextParamSet paramSet) {}
}
