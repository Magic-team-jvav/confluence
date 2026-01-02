package org.confluence.mod.common.data.gen.loot.modifiers;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

/**
 * Generates loot tables into EMI's direct_drop registry i.e. minecraft/direct_drops/entities/bat.json
 */
public final class AddEntityLootDirectDropsSubProvider extends AddEntityLootConfluenceSubProvider {
    public AddEntityLootDirectDropsSubProvider(HolderLookup.Provider registries) {
        super(registries);
    }

    @Override
    public @NotNull String getPath(EntityType<?> entityType) {
        return getEntityTypeKey(entityType).getPath();
    }

    @Override
    protected @NotNull ResourceKey<LootTable> getResourceKey(EntityType<?> entityType) {
        return entityType.getDefaultLootTable();
    }

    private @NotNull ResourceLocation getEntityTypeKey(EntityType<?> entityType) {
        return entityType.getDefaultLootTable().location();
    }
}
