package org.confluence.mod.common.gameevent;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import org.confluence.mod.Confluence;

// todo
public enum FrostMoonGameEvent implements GameEvent {
    INSTANCE;
    public static final ResourceKey<FrostMoonGameEvent> KEY = GameEvent.createKey(Confluence.asResource("frost_moon"));

    @Override
    public void open(MinecraftServer server) {

    }

    @Override
    public void close(MinecraftServer server) {

    }

    @Override
    public void tick() {

    }

    @Override
    public boolean canStart() {
        return false;
    }

    @Override
    public boolean canEnd() {
        return false;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onEnd() {

    }

    @Override
    public boolean started() {
        return false;
    }

    @Override
    public boolean forceStart() {
        return false;
    }

    @Override
    public void forceEnd() {

    }

    @Override
    public void decode(CompoundTag tag) {

    }

    @Override
    public void encode(CompoundTag tag) {

    }

    @Override
    public ResourceKey<FrostMoonGameEvent> key() {
        return KEY;
    }
}
