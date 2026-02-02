package org.confluence.mod.common.gameevent;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.util.OverworldUtils;

// todo
public final class SpecificMoonGameEvent implements GameEvent {
    public static final ResourceKey<SpecificMoonGameEvent> KEY = GameEvent.createKey(Confluence.asResource("specific_moon"));
    public static final SpecificMoonGameEvent INSTANCE = new SpecificMoonGameEvent();
    private transient ServerLevel level;

    private SpecificMoonGameEvent() {}

    @Override
    public void open(MinecraftServer server) {
        this.level = OverworldUtils.getLevel(server);
    }

    @Override
    public void close(MinecraftServer server) {
        this.level = null;
    }

    @Override
    public void tick() {}

    @Override
    public boolean canStart() {
        return LibDateUtils.getDayTime(level) == LibDateUtils._19$30;
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
    public ResourceKey<SpecificMoonGameEvent> key() {
        return KEY;
    }

    @Override
    public boolean isNonEnvEvent() {
        return false;
    }
}
