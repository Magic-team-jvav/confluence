package org.confluence.mod.common.data.gen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.gen.loot.modifiers.AddBlockLootConfluenceSubProvider;
import org.confluence.mod.common.data.gen.loot.modifiers.AddChestLootConfluenceSubProvider;
import org.confluence.mod.common.data.gen.loot.modifiers.AddEntityLootConfluenceSubProvider;
import org.mesdag.portlib.loot.PortAddTableLootModifier;

/// Generates loot modifiers that add loot tables to existing loot tables.
public class ModLootModifiersProvider extends GlobalLootModifierProvider {
    public ModLootModifiersProvider(PackOutput output) {
        super(output, Confluence.MODID);
    }

    @Override
    protected void start() {
        var provider = new AddEntityLootConfluenceSubProvider();
        var entries = provider.getAddedEntitiesLoot();

        for (var entry : entries) {
            EntityType<?> entityType = entry.entityType();
            this.add(
                    entityType.getDefaultLootTable().getPath(),
                    new PortAddTableLootModifier(new LootTableIdCondition[]{
                            (LootTableIdCondition) LootTableIdCondition.builder(entityType.getDefaultLootTable()).build()
                    }, Confluence.asResource(provider.getPath(entityType)))
            );
        }

        var blockProvider = new AddBlockLootConfluenceSubProvider();
        var blockEntries = blockProvider.getAddedBlocksLoot();
        for (var entry : blockEntries) {
            var block = entry.block();
            ResourceLocation location = block.getLootTable();
            this.add(
                    location.getPath(),
                    new PortAddTableLootModifier(new LootTableIdCondition[]{
                            (LootTableIdCondition) LootTableIdCondition.builder(location).build()
                    }, Confluence.asResource(blockProvider.getPath(location)))
            );
        }

        var chestProvider = new AddChestLootConfluenceSubProvider();
        var chestEntries = chestProvider.getAddedEntitiesLoot();
        for (var entry : chestEntries) {
            ResourceLocation location = entry.lootTable();
            this.add(
                    location.getPath(),
                    new PortAddTableLootModifier(new LootTableIdCondition[]{
                            (LootTableIdCondition) LootTableIdCondition.builder(location).build()
                    }, Confluence.asResource(chestProvider.getPath(location)))
            );
        }
    }
}
