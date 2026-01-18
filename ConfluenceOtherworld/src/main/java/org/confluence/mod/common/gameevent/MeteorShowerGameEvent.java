package org.confluence.mod.common.gameevent;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.util.OverworldUtils;

public class MeteorShowerGameEvent implements GameEvent {
    public static final ResourceKey<MeteorShowerGameEvent> KEY = GameEvent.createKey(Confluence.asResource("meteor_shower"));
    public static final MeteorShowerGameEvent INSTANCE = new MeteorShowerGameEvent();
    private transient boolean isCelebrationMK10;
    private transient ServerLevel level;
    private transient boolean forceStart;
    private transient boolean forceEnd;
    private boolean started;

    private MeteorShowerGameEvent() {}

    @Override
    public void open(MinecraftServer server) {
        this.isCelebrationMK10 = ModSecretSeeds.CELEBRATIONMK10.match(server);
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
        return LibDateUtils.getDayTime(level) == LibDateUtils._19$30 &&
                level.random.nextInt(isCelebrationMK10
                        ? CommonConfigs.METEOR_SHOWER_EVENT_FREQUENCY_CELEBRATIONMK10.get()
                        : CommonConfigs.METEOR_SHOWER_EVENT_FREQUENCY.get()
                ) == 0;
    }

    @Override
    public boolean canEnd() {
        if (forceEnd) {
            return true;
        }
        return LibDateUtils.isWithinDayTime(LibDateUtils._04$30, LibDateUtils._19$30, level);
    }

    @Override
    public void onStart() {
        this.forceStart = false;
        this.started = true;
    }

    @Override
    public void onEnd() {
        this.forceEnd = false;
        this.started = false;
    }

    @Override
    public boolean started() {
        return started;
    }

    @Override
    public boolean forceStart() {
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
    public ResourceKey<MeteorShowerGameEvent> key() {
        return KEY;
    }

    @Override
    public boolean isNonEnvEvent() {
        return false;
    }
}
