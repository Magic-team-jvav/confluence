package org.confluence.mod.common.data.gen.loot.modifiers;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

/// Generates loot tables into EMI's direct_drop registry i.e. minecraft/direct_drops/blocks/stone.json
public class AddBlockLootDirectDropsSubProvider extends AddBlockLootConfluenceSubProvider {
    public AddBlockLootDirectDropsSubProvider(HolderLookup.Provider provider) {
        super(provider);
    }

    @Override
    protected ResourceLocation getResourceKey(ResourceLocation originalResourceKey) {
        return originalResourceKey;
    }

    @Override
    public String getPath(ResourceLocation location) {
        return location.getPath();
    }
}
