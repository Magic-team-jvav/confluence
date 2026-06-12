package org.confluence.mod.common.gameevent;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import org.mesdag.portlib.network.codec.PortStreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.Confluence;

@SuppressWarnings({"unchecked", "rawtypes"})
public interface GameEvent {
    ResourceKey<Registry<GameEvent>> REGISTRY_KEY = ResourceKey.createRegistryKey(Confluence.asResource("game_event"));
    Codec<ResourceKey<? extends GameEvent>> KEY_CODEC = (Codec<ResourceKey<? extends GameEvent>>) (Codec) ResourceKey.codec(REGISTRY_KEY);
    PortStreamCodec<ByteBuf, ResourceKey<? extends GameEvent>> KEY_STREAM_CODEC = (PortStreamCodec<ByteBuf, ResourceKey<? extends GameEvent>>) (PortStreamCodec) ResourceKey.streamCodec(REGISTRY_KEY);

    static <E extends GameEvent> ResourceKey<E> createKey(ResourceLocation key) {
        return (ResourceKey<E>) ResourceKey.create(REGISTRY_KEY, key);
    }

    void open(MinecraftServer server);

    void close(MinecraftServer server);

    void tick();

    default void countKilled(LivingEntity living) {}

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

    /// 返回false表示环境事件
    default boolean isNonEnvEvent() {
        return true;
    }
}
