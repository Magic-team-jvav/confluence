package org.confluence.mod.common.gameevent;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.mod.util.OverworldUtils;

public class GoblinArmyGameEvent implements GameEvent {
    public static final ResourceKey<GoblinArmyGameEvent> KEY = GameEvent.createKey(Confluence.asResource("goblin_army"));
    public static final GoblinArmyGameEvent INSTANCE = new GoblinArmyGameEvent();
    private transient MinecraftServer server;
    private transient ServerLevel level;

    private GoblinArmyGameEvent() {}

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
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            AchievementUtils.awardAchievement(player, "goblin_punter");
        }
        KillBoard.INSTANCE.defeat(KEY);
    }

    @Override
    public boolean started() {
        return false;
    }

    @Override
    public void forceStart() {

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
    public ResourceKey<GoblinArmyGameEvent> key() {
        return KEY;
    }
}
