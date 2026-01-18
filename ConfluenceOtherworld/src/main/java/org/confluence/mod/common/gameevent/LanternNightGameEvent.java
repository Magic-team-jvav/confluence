package org.confluence.mod.common.gameevent;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.Confluence;

public class LanternNightGameEvent implements GameEvent {
    public static final ResourceKey<LanternNightGameEvent> KEY = GameEvent.createKey(Confluence.asResource("lantern_night"));
    public static final LanternNightGameEvent INSTANCE = new LanternNightGameEvent();
    private transient MinecraftServer server;

    private LanternNightGameEvent() {}

    @Override
    public void open(MinecraftServer server) {
        this.server = server;
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
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
//            player.getAttribute(Attributes.LUCK).addTransientModifier();
        }
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
    public ResourceKey<LanternNightGameEvent> key() {
        return null;
    }

    @Override
    public boolean isNonEnvEvent() {
        return false;
    }
}
