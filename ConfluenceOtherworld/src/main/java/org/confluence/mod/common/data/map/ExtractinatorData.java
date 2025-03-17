package org.confluence.mod.common.data.map;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public record ExtractinatorData(ResourceKey<LootTable> lootTable) {
    public static final Codec<ExtractinatorData> CODEC = ResourceKey.codec(Registries.LOOT_TABLE).xmap(ExtractinatorData::new, ExtractinatorData::lootTable);
}
