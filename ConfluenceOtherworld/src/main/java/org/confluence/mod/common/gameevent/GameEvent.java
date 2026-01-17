package org.confluence.mod.common.gameevent;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.confluence.mod.Confluence;

@SuppressWarnings({"unchecked", "rawtypes"})
public interface GameEvent {
    ResourceKey<Registry<GameEvent>> REGISTRY_KEY = ResourceKey.createRegistryKey(Confluence.asResource("game_event"));
    Codec<ResourceKey<? extends GameEvent>> KEY_CODEC = (Codec<ResourceKey<? extends GameEvent>>) (Codec) ResourceKey.codec(REGISTRY_KEY);
    StreamCodec<ByteBuf, ResourceKey<? extends GameEvent>> KEY_STREAM_CODEC = (StreamCodec<ByteBuf, ResourceKey<? extends GameEvent>>) (StreamCodec) ResourceKey.streamCodec(REGISTRY_KEY);

    static <E extends GameEvent> ResourceKey<E> createKey(ResourceLocation key) {
        return (ResourceKey<E>) ResourceKey.create(REGISTRY_KEY, key);
    }

    void open(MinecraftServer server);

    void close(MinecraftServer server);

    void tick();

    boolean canStart();

    boolean canEnd();

    void onStart();

    void onEnd();

    boolean started();

    boolean forceStart();

    void forceEnd();

    void decode(CompoundTag tag);

    void encode(CompoundTag tag);

    ResourceKey<? extends GameEvent> key();
}
