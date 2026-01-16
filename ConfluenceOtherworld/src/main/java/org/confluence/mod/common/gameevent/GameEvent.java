package org.confluence.mod.common.gameevent;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.confluence.mod.Confluence;

public interface GameEvent {
    ResourceKey<Registry<GameEvent>> REGISTRY_KEY = ResourceKey.createRegistryKey(Confluence.asResource("game_event"));

    @SuppressWarnings("unchecked")
    static <E extends GameEvent> ResourceKey<E> createKey(ResourceLocation key) {
        return (ResourceKey<E>) ResourceKey.create(REGISTRY_KEY, key);
    }

    void init(MinecraftServer server);

    void tick();

    boolean canStart();

    boolean canEnd();

    void onStart();

    void onEnd();

    boolean started();

    void decode(CompoundTag tag);

    void encode(CompoundTag tag);

    ResourceKey<? extends GameEvent> key();
}
