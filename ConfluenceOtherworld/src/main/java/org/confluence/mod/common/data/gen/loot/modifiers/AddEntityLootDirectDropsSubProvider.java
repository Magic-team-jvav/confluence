package org.confluence.mod.common.data.gen.loot.modifiers;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

/// Generates loot tables into EMI's direct_drop registry i.e. minecraft/direct_drops/entities/bat.json
public final class AddEntityLootDirectDropsSubProvider extends AddEntityLootConfluenceSubProvider {
    public AddEntityLootDirectDropsSubProvider(HolderLookup.Provider registries) {
        super(registries);
    }

    @Override
    public String getPath(EntityType<?> entityType) {
        return getEntityTypeKey(entityType).getPath();
    }

    @Override
    protected ResourceLocation getResourceKey(EntityType<?> entityType) {
        return entityType.getDefaultLootTable();
    }

    private ResourceLocation getEntityTypeKey(EntityType<?> entityType) {
        return entityType.getDefaultLootTable();
    }
}
