package org.confluence.mod.common.data.gen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.confluence.mod.common.data.gen.loot.*;
import org.confluence.mod.common.data.gen.loot.modifiers.AddBlockLootConfluenceSubProvider;
import org.confluence.mod.common.data.gen.loot.modifiers.AddChestLootConfluenceSubProvider;
import org.confluence.mod.common.data.gen.loot.modifiers.AddEntityLootConfluenceSubProvider;

import java.util.List;
import java.util.Set;

public class ModLootTableProvider extends LootTableProvider {
    public ModLootTableProvider(PackOutput output) {
        super(output, Set.of(), List.of(
                new SubProviderEntry(BlockSubProvider::new, LootContextParamSets.BLOCK),
                new SubProviderEntry(EntitySubProvider::new, LootContextParamSets.ENTITY),
                new SubProviderEntry(ChestSubProvider::new, LootContextParamSets.CHEST),
                new SubProviderEntry(ArchaeologySubProvider::new, LootContextParamSets.ARCHAEOLOGY),
                new SubProviderEntry(FishingSubProvider::new, LootContextParamSets.FISHING),
                new SubProviderEntry(GiftSubProvider::new, LootContextParamSets.GIFT),
                new SubProviderEntry(AddBlockLootConfluenceSubProvider::new, LootContextParamSets.BLOCK),
                new SubProviderEntry(AddEntityLootConfluenceSubProvider::new, LootContextParamSets.ENTITY),
                new SubProviderEntry(AddChestLootConfluenceSubProvider::new, LootContextParamSets.CHEST)
        ));
    }
}
