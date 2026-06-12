package org.confluence.mod.common.data.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.AddTableLootModifier;
import net.minecraftforge.common.loot.LootTableIdCondition;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.gen.loot.modifiers.AddBlockLootConfluenceSubProvider;
import org.confluence.mod.common.data.gen.loot.modifiers.AddChestLootConfluenceSubProvider;
import org.confluence.mod.common.data.gen.loot.modifiers.AddEntityLootConfluenceSubProvider;

import java.util.concurrent.CompletableFuture;

/**
 * Generates loot modifiers that add loot tables to existing loot tables.
 */
public class ModLootModifiersProvider extends GlobalLootModifierProvider {
    public ModLootModifiersProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Confluence.MODID);
    }

    @Override
    protected void start() {
        var provider = new AddEntityLootConfluenceSubProvider(registries);
        var entries = provider.getAddedEntitiesLoot();

        for (var entry : entries) {
            EntityType<?> entityType = entry.entityType();
            this.add(
                    entityType.getDefaultLootTable().location().getPath(),
                    new AddTableLootModifier(new LootTableIdCondition[]{
                            (LootTableIdCondition) LootTableIdCondition.builder(entityType.getDefaultLootTable().location()).build()
                    }, ResourceKey.create(Registries.LOOT_TABLE, Confluence.asResource(provider.getPath(entityType))))
            );
        }

        var blockProvider = new AddBlockLootConfluenceSubProvider(registries);
        var blockEntries = blockProvider.getAddedBlocksLoot();
        for (var entry : blockEntries) {
            var block = entry.block();
            ResourceLocation location = block.getLootTable().location();
            this.add(
                    location.getPath(),
                    new AddTableLootModifier(new LootTableIdCondition[]{
                            (LootTableIdCondition) LootTableIdCondition.builder(location).build()
                    }, ResourceKey.create(Registries.LOOT_TABLE, Confluence.asResource(blockProvider.getPath(location))))
            );
        }

        var chestProvider = new AddChestLootConfluenceSubProvider(registries);
        var chestEntries = chestProvider.getAddedEntitiesLoot();
        for (var entry : chestEntries) {
            ResourceKey<LootTable> lootTable = entry.lootTable();
            ResourceLocation location = lootTable.location();
            this.add(
                    location.getPath(),
                    new AddTableLootModifier(new LootTableIdCondition[]{
                            (LootTableIdCondition) LootTableIdCondition.builder(location).build()
                    }, ResourceKey.create(Registries.LOOT_TABLE, Confluence.asResource(chestProvider.getPath(lootTable))))
            );
        }
    }
}
