package org.confluence.mod.common.data.gen.loot.modifiers;

import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.item.ConsumableItems;
import org.confluence.mod.common.init.item.FoodItems;
import org.confluence.mod.common.init.item.ModItems;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;

/// Generates blocks loot tables into loot_modifier path specified by ModLootModifiersProvider i.e. confluence/loot_table/with/blocks/stone.json
///
/// @see org.confluence.mod.common.data.gen.ModLootModifiersProvider
public class AddBlockLootConfluenceSubProvider extends BlockLootSubProvider implements SyntheticLootTableProvider {
    private final HolderLookup.Provider provider;

    public AddBlockLootConfluenceSubProvider(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
        this.provider = provider;
    }

    public List<AddedBlockLoot> getAddedBlocksLoot() {
        List<AddedBlockLoot> entries = new ArrayList<>();
        entries.add(new AddedBlockLoot(Blocks.BIRCH_LEAVES,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(FoodItems.LEMON)
                                        .when(LootItemRandomChanceCondition.randomChance(0.009f))
                                ).add(LootItem.lootTableItem(ConsumableItems.SUGAR_TANGERINE)
                                        .when(LootItemRandomChanceCondition.randomChance(0.009f))
                                )
                )
        ));
        entries.add(new AddedBlockLoot(Blocks.CHERRY_LEAVES,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(FoodItems.PEACH)
                                        .when(LootItemRandomChanceCondition.randomChance(0.009f))
                                )
                )
        ));
        entries.add(new AddedBlockLoot(Blocks.DARK_OAK_LEAVES,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(FoodItems.APRICOT)
                                        .when(LootItemRandomChanceCondition.randomChance(0.009f))
                                ).add(LootItem.lootTableItem(FoodItems.GRAPE_FRUIT)
                                        .when(LootItemRandomChanceCondition.randomChance(0.009f))
                                )
                )
        ));
        var biomes = provider.lookupOrThrow(Registries.BIOME);
        entries.add(new AddedBlockLoot(Blocks.FERN,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(ModItems.GRASS_SEED)
                                        .when(LootItemRandomChanceCondition.randomChance(0.02f))
                                ).add(LootItem.lootTableItem(ModItems.JUNGLE_GRASS_SEED)
                                        .when(LootItemRandomChanceCondition.randomChance(0.02f))
                                        .when(AnyOfCondition.anyOf(
                                                LocationCheck.checkLocation(LocationPredicate.Builder.inBiome(biomes.get(Biomes.JUNGLE).get())),
                                                LocationCheck.checkLocation(LocationPredicate.Builder.inBiome(biomes.get(Biomes.LUSH_CAVES).get())),
                                                LocationCheck.checkLocation(LocationPredicate.Builder.inBiome(biomes.get(Biomes.SPARSE_JUNGLE).get())),
                                                LocationCheck.checkLocation(LocationPredicate.Builder.inBiome(biomes.get(Biomes.BAMBOO_JUNGLE).get()))
                                        )))
                )
        ));
        entries.add(new AddedBlockLoot(Blocks.JUNGLE_LEAVES,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(FoodItems.MANGO)
                                        .when(LootItemRandomChanceCondition.randomChance(0.009f))
                                ).add(LootItem.lootTableItem(FoodItems.PINEAPPLE)
                                        .when(LootItemRandomChanceCondition.randomChance(0.009f))
                                )
                )
        ));
        entries.add(new AddedBlockLoot(Blocks.LARGE_FERN,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(ModItems.GRASS_SEED)
                                        .when(LootItemRandomChanceCondition.randomChance(0.02f))
                                ).add(LootItem.lootTableItem(ModItems.JUNGLE_GRASS_SEED)
                                        .when(LootItemRandomChanceCondition.randomChance(0.02f))
                                        .when(AnyOfCondition.anyOf(
                                                LocationCheck.checkLocation(LocationPredicate.Builder.inBiome(biomes.get(Biomes.JUNGLE).get())),
                                                LocationCheck.checkLocation(LocationPredicate.Builder.inBiome(biomes.get(Biomes.LUSH_CAVES).get())),
                                                LocationCheck.checkLocation(LocationPredicate.Builder.inBiome(biomes.get(Biomes.SPARSE_JUNGLE).get())),
                                                LocationCheck.checkLocation(LocationPredicate.Builder.inBiome(biomes.get(Biomes.BAMBOO_JUNGLE).get()))
                                        )))
                )
        ));
        entries.add(new AddedBlockLoot(Blocks.OAK_LEAVES,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(FoodItems.APRICOT)
                                        .when(LootItemRandomChanceCondition.randomChance(0.009f))
                                ).add(LootItem.lootTableItem(FoodItems.GRAPE_FRUIT)
                                        .when(LootItemRandomChanceCondition.randomChance(0.009f))
                                )
                )
        ));
        entries.add(new AddedBlockLoot(Blocks.SHORT_GRASS,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(ModItems.GRASS_SEED)
                                        .when(LootItemRandomChanceCondition.randomChance(0.02f))
                                ).add(LootItem.lootTableItem(ModItems.JUNGLE_GRASS_SEED)
                                        .when(LootItemRandomChanceCondition.randomChance(0.02f))
                                        .when(AnyOfCondition.anyOf(
                                                LocationCheck.checkLocation(LocationPredicate.Builder.inBiome(biomes.get(Biomes.JUNGLE).get())),
                                                LocationCheck.checkLocation(LocationPredicate.Builder.inBiome(biomes.get(Biomes.LUSH_CAVES).get())),
                                                LocationCheck.checkLocation(LocationPredicate.Builder.inBiome(biomes.get(Biomes.SPARSE_JUNGLE).get())),
                                                LocationCheck.checkLocation(LocationPredicate.Builder.inBiome(biomes.get(Biomes.BAMBOO_JUNGLE).get()))
                                        )))
                )
        ));
        entries.add(new AddedBlockLoot(Blocks.SPRUCE_LEAVES,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(FoodItems.PLUM)
                                        .when(LootItemRandomChanceCondition.randomChance(0.009f))
                                ).add(LootItem.lootTableItem(FoodItems.CHERRY)
                                        .when(LootItemRandomChanceCondition.randomChance(0.009f))
                                )
                )
        ));
        entries.add(new AddedBlockLoot(Blocks.TALL_GRASS,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(ModItems.GRASS_SEED)
                                        .when(LootItemRandomChanceCondition.randomChance(0.02f))
                                ).add(LootItem.lootTableItem(ModItems.JUNGLE_GRASS_SEED)
                                        .when(LootItemRandomChanceCondition.randomChance(0.02f))
                                        .when(AnyOfCondition.anyOf(
                                                LocationCheck.checkLocation(LocationPredicate.Builder.inBiome(biomes.get(Biomes.JUNGLE).get())),
                                                LocationCheck.checkLocation(LocationPredicate.Builder.inBiome(biomes.get(Biomes.LUSH_CAVES).get())),
                                                LocationCheck.checkLocation(LocationPredicate.Builder.inBiome(biomes.get(Biomes.SPARSE_JUNGLE).get())),
                                                LocationCheck.checkLocation(LocationPredicate.Builder.inBiome(biomes.get(Biomes.BAMBOO_JUNGLE).get()))
                                        )))
                )
        ));
        return entries;
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        var entries = getAddedBlocksLoot();
        List<Block> blocks = new ArrayList<>();
        for (var entry : entries) {
            blocks.add(entry.block);
        }
        return blocks;
    }

    @Override
    protected void generate() {
        var entries = getAddedBlocksLoot();
        for (var entry : entries) {
            this.add(entry.block, entry.lootTableBuilder);
        }
    }

    public void generate(@NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        this.generate();
        Set<ResourceKey<LootTable>> set = new HashSet();
        for (var block : this.getKnownBlocks()) {
            if (block.isEnabled(this.enabledFeatures)) {
                ResourceKey<LootTable> originalResourceKey = block.getLootTable();
                var resourceKey = getResourceKey(originalResourceKey);
                if (originalResourceKey != BuiltInLootTables.EMPTY && set.add(originalResourceKey)) {
                    LootTable.Builder loottable$builder = this.map.remove(originalResourceKey);
                    if (loottable$builder == null) {
                        throw new IllegalStateException(String.format(Locale.ROOT, "Missing loottable '%s' for '%s'", originalResourceKey.location(), BuiltInRegistries.BLOCK.getKey(block)));
                    }

                    output.accept(resourceKey, loottable$builder);
                }
            }
        }

        if (!this.map.isEmpty()) {
            throw new IllegalStateException("Created block loot tables for non-blocks: " + this.map.keySet());
        }
    }

    protected ResourceKey<LootTable> getResourceKey(ResourceKey<LootTable> originalResourceKey) {
        return Confluence.asResourceKey(Registries.LOOT_TABLE, getPath(originalResourceKey.location()));
    }

    public String getPath(ResourceLocation location) {
        return "with/" + location.getPath();
    }

    @Override
    public List<String> getSyntheticLootTablePaths() {
        var entries = getAddedBlocksLoot();
        List<String> paths = new ArrayList<>();
        for (var entry : entries) {
            paths.add(Confluence.asResource(getPath(entry.block.getLootTable().location())).toString());
        }
        return paths;
    }

    public record AddedBlockLoot(Block block, LootTable.Builder lootTableBuilder) {}
}
