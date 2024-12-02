package org.confluence.mod.common.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public record LootComponent(ResourceKey<LootTable> lootTable) {
    public static final Codec<LootComponent> CODEC = ResourceKey.codec(Registries.LOOT_TABLE).xmap(LootComponent::new, LootComponent::lootTable);
    public static final StreamCodec<ByteBuf, LootComponent> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.LOOT_TABLE), LootComponent::lootTable,
            LootComponent::new
    );
}
