package org.confluence.mod.common.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

public record LootComponent(ResourceKey<LootTable> value) implements DataComponentType<LootComponent> {
    public static final Codec<LootComponent> CODEC = ResourceKey.codec(Registries.LOOT_TABLE).xmap(LootComponent::new, LootComponent::value);
    public static final StreamCodec<ByteBuf, LootComponent> STREAM_CODEC = ResourceKey.streamCodec(Registries.LOOT_TABLE).map(LootComponent::new, LootComponent::value);

    @Override
    public @Nullable Codec<LootComponent> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, LootComponent> streamCodec() {
        return STREAM_CODEC;
    }
}
