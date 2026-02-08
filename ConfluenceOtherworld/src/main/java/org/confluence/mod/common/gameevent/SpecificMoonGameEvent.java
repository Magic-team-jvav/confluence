package org.confluence.mod.common.gameevent;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.util.OverworldUtils;

public final class SpecificMoonGameEvent implements GameEvent {
    public static final ResourceKey<SpecificMoonGameEvent> KEY = GameEvent.createKey(Confluence.asResource("specific_moon"));
    public static final SpecificMoonGameEvent INSTANCE = new SpecificMoonGameEvent();
    private boolean started;
    private transient ServerLevel level;
    private transient boolean forceStart;
    private transient boolean forceEnd;

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
        if (forceStart) {
            return true;
        }
        return LibDateUtils.getDayTime(level) == LibDateUtils._18$00 && level.random.nextInt(5) == 0;
    }

    @Override
    public boolean canEnd() {
        if (forceEnd) {
            return true;
        }
        return LibDateUtils.isWithinDayTime(LibDateUtils._06$00, LibDateUtils._18$00, level);
    }

    @Override
    public void onStart() {
        this.started = true;
        this.forceStart = false;
    }

    @Override
    public void onEnd() {
        this.started = false;
        this.forceEnd = false;
    }

    @Override
    public boolean started() {
        return started;
    }

    @Override
    public boolean forceStart() {
        if (started) return false;
        if (LibDateUtils.isWithinDayTime(LibDateUtils._19$30, LibDateUtils._04$30, level)) {
            this.forceStart = true;
            return true;
        }
        return false;
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
    public ResourceKey<SpecificMoonGameEvent> key() {
        return KEY;
    }

    @Override
    public boolean isNonEnvEvent() {
        return false;
    }
}
