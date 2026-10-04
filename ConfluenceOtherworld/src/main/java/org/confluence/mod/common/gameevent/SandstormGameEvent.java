package org.confluence.mod.common.gameevent;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.ConfluenceData;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.util.OverworldUtils;

public enum SandstormGameEvent implements GameEvent {
    INSTANCE;

    public static final ResourceKey<SandstormGameEvent> KEY = GameEvent.createKey(Confluence.asResource("sandstorm"));
    private ServerLevel level;
    private boolean active;
    private boolean requested;
    private int remainingTicks;

    @Override
    public void open(MinecraftServer server) {
        level = OverworldUtils.getLevel(server);
        requested = false;
    }

    @Override
    public void close(MinecraftServer server) {
        level = null;
        requested = false;
    }

    @Override
    public void tick() {
        if (active) remainingTicks = Math.max(0, remainingTicks - (strongWind() ? 1 : 15));
    }

    private boolean strongWind() {
        ConfluenceData data = ConfluenceData.get(level);
        return data.getWindSpeedX() * data.getWindSpeedX() + data.getWindSpeedZ() * data.getWindSpeedZ() >= 0.36F;
    }

    @Override
    public boolean canStart() {
        return requested || strongWind() && level.random.nextInt(KillBoard.INSTANCE.getGamePhase().isHardmode() ? 14400 : 21600) == 0;
    }

    @Override
    public boolean canEnd() {
        return remainingTicks == 0;
    }

    @Override
    public void onStart() {
        active = true;
        requested = false;
        remainingTicks = 9600 + level.random.nextInt(19201);
    }

    @Override
    public void onEnd() {
        active = false;
    }

    @Override
    public boolean started() {
        return active;
    }

    @Override
    public boolean forceStart() {
        if (active || level == null) return false;
        requested = true;
        return true;
    }

    @Override
    public void forceEnd() {
        requested = false;
        remainingTicks = 0;
    }

    @Override
    public void decode(CompoundTag tag) {
        active = tag.getBoolean("Started");
        remainingTicks = Math.max(0, tag.getInt("RemainingTicks"));
    }

    @Override
    public void encode(CompoundTag tag) {
        tag.putBoolean("Started", active);
        tag.putInt("RemainingTicks", remainingTicks);
    }

    @Override
    public ResourceKey<SandstormGameEvent> key() {
        return KEY;
    }

    @Override
    public boolean isNonEnvEvent() {
        return false;
    }
}
