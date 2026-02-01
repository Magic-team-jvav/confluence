package org.confluence.mod.common.data.gen.loot.modifiers;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

/// Generates loot tables into EMI's direct_drop registry i.e. minecraft/direct_drops/chests/jungle_temple.json
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
