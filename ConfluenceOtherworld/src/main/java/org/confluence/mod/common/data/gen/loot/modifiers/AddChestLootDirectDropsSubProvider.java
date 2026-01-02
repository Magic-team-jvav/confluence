package org.confluence.mod.common.data.gen.loot.modifiers;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import org.confluence.mod.Confluence;
import org.confluence.terra_curio.common.init.TCItems;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Generates loot tables into EMI's direct_drop registry i.e. minecraft/direct_drops/chests/jungle_temple.json
 */
public class AddChestLootDirectDropsSubProvider extends AddChestLootConfluenceSubProvider {
    public AddChestLootDirectDropsSubProvider(HolderLookup.Provider provider) {
        super(provider);
    }

    protected @NotNull ResourceKey<LootTable> getResourceKey(ResourceKey<LootTable> lootTable) {
        return lootTable;
    }

    public @NotNull String getPath(ResourceKey<LootTable> lootTable) {
        return lootTable.location().getPath();
    }
}
