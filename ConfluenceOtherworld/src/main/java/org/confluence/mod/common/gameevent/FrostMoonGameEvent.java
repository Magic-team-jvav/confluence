package org.confluence.mod.common.gameevent;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.lib.color.GlobalColors;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.util.OverworldUtils;

// todo
public enum FrostMoonGameEvent implements GameEvent {
    INSTANCE;
    public static final ResourceKey<FrostMoonGameEvent> KEY = GameEvent.createKey(Confluence.asResource("frost_moon"));

    private transient MinecraftServer server;
    private transient ServerLevel level;
    private transient boolean forceStart;
    private transient boolean forceEnd;
    private boolean started;

    @Override
    public void open(MinecraftServer server) {
        this.server = server;
        this.level = OverworldUtils.getLevel(server);
    }

    @Override
    public void close(MinecraftServer server) {
        this.server = null;
        this.level = null;
    }

    @Override
    public void tick() {

    }

    @Override
    public boolean canStart() {
        return forceStart;
    }

    @Override
    public boolean canEnd() {
        return forceEnd || level != null && LibDateUtils.isWithinDayTime(LibDateUtils._04$30, LibDateUtils._19$30, level);
    }

    @Override
    public void onStart() {
        this.started = true;
        this.forceStart = false;
        sendMessage("message.confluence.frost_moon.start");
    }

    @Override
    public void onEnd() {
        this.started = false;
        this.forceEnd = false;
        sendMessage("message.confluence.frost_moon.end");
    }

    @Override
    public boolean started() {
        return started;
    }

    @Override
    public boolean forceStart() {
        if (started || level == null || !LibDateUtils.isWithinDayTime(LibDateUtils._19$30, LibDateUtils._04$30, level)) {
            return false;
        }
        if (GameEventSystem.anyInvasionStarted()) {
            return false;
        }
        this.forceStart = true;
        return true;
    }

    @Override
    public void forceEnd() {
        if (started) {
            this.forceEnd = true;
        }
    }

    @Override
    public void decode(CompoundTag tag) {
        this.started = tag.getBoolean("Started");
    }

    @Override
    public void encode(CompoundTag tag) {
        tag.putBoolean("Started", started);
    }

    @Override
    public ResourceKey<FrostMoonGameEvent> key() {
        return KEY;
    }

    private void sendMessage(String key) {
        if (server == null) return;
        Component message = Component.translatable(key).withColor(GlobalColors.EVENT.get());
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.sendSystemMessage(message);
        }
    }
}
