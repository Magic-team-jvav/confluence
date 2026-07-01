package org.confluence.mod.common.data.gen.loot.modifiers;

import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
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
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.item.ConsumableItems;
import org.confluence.mod.common.init.item.FoodItems;
import org.confluence.mod.common.init.item.ModItems;

import java.util.*;
import java.util.function.BiConsumer;

/// Generates blocks loot tables into loot_modifier path specified by ModLootModifiersProvider i.e. confluence/loot_table/with/blocks/stone.json
///
/// @see org.confluence.mod.common.data.gen.ModLootModifiersProvider
public class AddBlockLootConfluenceSubProvider extends BlockLootSubProvider implements SyntheticLootTableProvider {
    public AddBlockLootConfluenceSubProvider() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    public List<AddedBlockLoot> getAddedBlocksLoot() {
        List<AddedBlockLoot> entries = new ArrayList<>();
        entries.add(new AddedBlockLoot(Blocks.BIRCH_LEAVES,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .when(this.doesNotHaveShearsOrSilkTouch())
                                .add(LootItem.lootTableItem(FoodItems.LEMON)
                                        .when(LootItemRandomChanceCondition.randomChance(0.018f))
                                ).add(LootItem.lootTableItem(ConsumableItems.SUGAR_TANGERINE)
                                        .when(LootItemRandomChanceCondition.randomChance(0.018f))
                                )
                )
        ));
        entries.add(new AddedBlockLoot(Blocks.CHERRY_LEAVES,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .when(this.doesNotHaveShearsOrSilkTouch())
                                .add(LootItem.lootTableItem(FoodItems.PEACH)
                                        .when(LootItemRandomChanceCondition.randomChance(0.018f))
                                )
                )
        ));
        entries.add(new AddedBlockLoot(Blocks.DARK_OAK_LEAVES,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .when(this.doesNotHaveShearsOrSilkTouch())
                                .add(LootItem.lootTableItem(FoodItems.APRICOT)
                                        .when(LootItemRandomChanceCondition.randomChance(0.018f))
                                ).add(LootItem.lootTableItem(FoodItems.GRAPE_FRUIT)
                                        .when(LootItemRandomChanceCondition.randomChance(0.018f))
                                )
                )
        ));
        entries.add(new AddedBlockLoot(Blocks.FERN,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .when(this.doesNotHaveShearsOrSilkTouch())
                                .add(LootItem.lootTableItem(ModItems.GRASS_SEED)
                                        .when(LootItemRandomChanceCondition.randomChance(0.02f))
                                ).add(LootItem.lootTableItem(ModItems.JUNGLE_GRASS_SEED)
                                        .when(LootItemRandomChanceCondition.randomChance(0.02f))
                                        .when(AnyOfCondition.anyOf(
                                                LocationCheck.checkLocation(LocationPredicate.Builder.location().setBiome(Biomes.JUNGLE)),
                                                LocationCheck.checkLocation(LocationPredicate.Builder.location().setBiome(Biomes.LUSH_CAVES)),
                                                LocationCheck.checkLocation(LocationPredicate.Builder.location().setBiome(Biomes.SPARSE_JUNGLE)),
                                                LocationCheck.checkLocation(LocationPredicate.Builder.location().setBiome(Biomes.BAMBOO_JUNGLE))
                                        )))
                )
        ));
        entries.add(new AddedBlockLoot(Blocks.JUNGLE_LEAVES,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .when(this.doesNotHaveShearsOrSilkTouch())
                                .add(LootItem.lootTableItem(FoodItems.MANGO)
                                        .when(LootItemRandomChanceCondition.randomChance(0.018f))
                                ).add(LootItem.lootTableItem(FoodItems.PINEAPPLE)
                                        .when(LootItemRandomChanceCondition.randomChance(0.018f))
                                )
                )
        ));
        entries.add(new AddedBlockLoot(Blocks.LARGE_FERN,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .when(this.doesNotHaveShearsOrSilkTouch())
                                .add(LootItem.lootTableItem(ModItems.GRASS_SEED)
                                        .when(LootItemRandomChanceCondition.randomChance(0.02f))
                                ).add(LootItem.lootTableItem(ModItems.JUNGLE_GRASS_SEED)
                                        .when(LootItemRandomChanceCondition.randomChance(0.02f))
                                        .when(AnyOfCondition.anyOf(
                                                LocationCheck.checkLocation(LocationPredicate.Builder.location().setBiome(Biomes.JUNGLE)),
                                                LocationCheck.checkLocation(LocationPredicate.Builder.location().setBiome(Biomes.LUSH_CAVES)),
                                                LocationCheck.checkLocation(LocationPredicate.Builder.location().setBiome(Biomes.SPARSE_JUNGLE)),
                                                LocationCheck.checkLocation(LocationPredicate.Builder.location().setBiome(Biomes.BAMBOO_JUNGLE))
                                        )))
                )
        ));
        entries.add(new AddedBlockLoot(Blocks.OAK_LEAVES,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .when(this.doesNotHaveShearsOrSilkTouch())
                                .add(LootItem.lootTableItem(FoodItems.APRICOT)
                                        .when(LootItemRandomChanceCondition.randomChance(0.018f))
                                ).add(LootItem.lootTableItem(FoodItems.GRAPE_FRUIT)
                                        .when(LootItemRandomChanceCondition.randomChance(0.018f))
                                )
                )
        ));
        entries.add(new AddedBlockLoot(Blocks.GRASS,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .when(this.doesNotHaveShearsOrSilkTouch())
                                .add(LootItem.lootTableItem(ModItems.GRASS_SEED)
                                        .when(LootItemRandomChanceCondition.randomChance(0.02f))
                                ).add(LootItem.lootTableItem(ModItems.JUNGLE_GRASS_SEED)
                                        .when(LootItemRandomChanceCondition.randomChance(0.02f))
                                        .when(AnyOfCondition.anyOf(
                                                LocationCheck.checkLocation(LocationPredicate.Builder.location().setBiome(Biomes.JUNGLE)),
                                                LocationCheck.checkLocation(LocationPredicate.Builder.location().setBiome(Biomes.LUSH_CAVES)),
                                                LocationCheck.checkLocation(LocationPredicate.Builder.location().setBiome(Biomes.SPARSE_JUNGLE)),
                                                LocationCheck.checkLocation(LocationPredicate.Builder.location().setBiome(Biomes.BAMBOO_JUNGLE))
                                        )))
                )
        ));
        entries.add(new AddedBlockLoot(Blocks.SPRUCE_LEAVES,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .when(this.doesNotHaveShearsOrSilkTouch())
                                .add(LootItem.lootTableItem(FoodItems.PLUM)
                                        .when(LootItemRandomChanceCondition.randomChance(0.018f))
                                ).add(LootItem.lootTableItem(FoodItems.CHERRY)
                                        .when(LootItemRandomChanceCondition.randomChance(0.018f))
                                )
                )
        ));
        entries.add(new AddedBlockLoot(Blocks.TALL_GRASS,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .when(this.doesNotHaveShearsOrSilkTouch())
                                .add(LootItem.lootTableItem(ModItems.GRASS_SEED)
                                        .when(LootItemRandomChanceCondition.randomChance(0.02f))
                                ).add(LootItem.lootTableItem(ModItems.JUNGLE_GRASS_SEED)
                                        .when(LootItemRandomChanceCondition.randomChance(0.02f))
                                        .when(AnyOfCondition.anyOf(
                                                LocationCheck.checkLocation(LocationPredicate.Builder.location().setBiome(Biomes.JUNGLE)),
                                                LocationCheck.checkLocation(LocationPredicate.Builder.location().setBiome(Biomes.LUSH_CAVES)),
                                                LocationCheck.checkLocation(LocationPredicate.Builder.location().setBiome(Biomes.SPARSE_JUNGLE)),
                                                LocationCheck.checkLocation(LocationPredicate.Builder.location().setBiome(Biomes.BAMBOO_JUNGLE))
                                        )))
                )
        ));
        return entries;
    }

    private LootItemCondition.Builder hasShearsOrSilkTouch() {
        return HAS_SHEARS.or(HAS_SILK_TOUCH);
    }

    private LootItemCondition.Builder doesNotHaveShearsOrSilkTouch() {
        return this.hasShearsOrSilkTouch().invert();
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
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

    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> output) {
        this.generate();
        Set<ResourceLocation> set = new HashSet<>();
        for (var block : this.getKnownBlocks()) {
            if (block.isEnabled(this.enabledFeatures)) {
                ResourceLocation originalResourceKey = block.getLootTable();
                var resourceKey = getResourceKey(originalResourceKey);
                if (originalResourceKey != BuiltInLootTables.EMPTY && set.add(originalResourceKey)) {
                    LootTable.Builder loottable$builder = this.map.remove(originalResourceKey);
                    if (loottable$builder == null) {
                        throw new IllegalStateException(String.format(Locale.ROOT, "Missing loottable '%s' for '%s'", originalResourceKey, BuiltInRegistries.BLOCK.getKey(block)));
                    }

                    output.accept(resourceKey, loottable$builder);
                }
            }
        }

        if (!this.map.isEmpty()) {
            throw new IllegalStateException("Created block loot tables for non-blocks: " + this.map.keySet());
        }
    }

    protected ResourceLocation getResourceKey(ResourceLocation originalResourceKey) {
        return Confluence.asResource(getPath(originalResourceKey));
    }

    public String getPath(ResourceLocation location) {
        return "with/" + location.getPath();
    }

    @Override
    public List<String> getSyntheticLootTablePaths() {
        var entries = getAddedBlocksLoot();
        List<String> paths = new ArrayList<>();
        for (var entry : entries) {
            paths.add(Confluence.asResource(getPath(entry.block.getLootTable())).toString());
        }
        return paths;
    }

    public record AddedBlockLoot(Block block, LootTable.Builder lootTableBuilder) {}
}
