package org.confluence.mod.common.gameevent;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.ConfluenceData;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.confluence.mod.util.OverworldUtils;

import java.util.HashSet;
import java.util.Set;

public enum PirateInvasionGameEvent implements GameEvent {
    INSTANCE;

    public static final ResourceKey<PirateInvasionGameEvent> KEY = GameEvent.createKey(Confluence.asResource("pirate_invasion"));
    public static final String ENTITY_TAG = "spawn_during_pirate_invasion";
    private ServerLevel level;
    private boolean active;
    private boolean requested;
    private int ready;
    private int killed;
    private int required;
    private final Set<Entity> spawned = new HashSet<>();
    private final ServerBossEvent progress = new ServerBossEvent(Component.translatable("event.confluence.pirate_invasion"), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);
    private WeightedRandomList<MobSpawnSettings.SpawnerData> pool = WeightedRandomList.create();

    public final CustomSpawner spawner = (world, enemies, friendly) -> {
        if (!active || ready > 0 || !enemies) return 0;
        return GameEventSystem.customSpawner(this, world, spawned, 24, 8, 1, pool, ENTITY_TAG, true);
    };

    @Override
    public void open(MinecraftServer server) {
        level = OverworldUtils.getLevel(server);
        pool = WeightedRandomList.create(
                new MobSpawnSettings.SpawnerData(MonsterEntities.PIRATE_DECKHAND.get(), 30, 1, 2),
                new MobSpawnSettings.SpawnerData(MonsterEntities.PIRATE_CORSAIR.get(), 20, 1, 2),
                new MobSpawnSettings.SpawnerData(MonsterEntities.PIRATE_DEADEYE.get(), 20, 1, 1),
                new MobSpawnSettings.SpawnerData(MonsterEntities.PIRATE_CROSSBOWER.get(), 20, 1, 1),
                new MobSpawnSettings.SpawnerData(MonsterEntities.PIRATE_PARROT.get(), 10, 1, 1),
                new MobSpawnSettings.SpawnerData(MonsterEntities.PIRATE_CAPTAIN.get(), 3, 1, 1));
    }

    @Override
    public void close(MinecraftServer server) {
        progress.removeAllPlayers();
        spawned.clear();
        level = null;
        requested = false;
    }

    @Override
    public void tick() {
        if (!active) return;
        if (ready > 0 && --ready == 0) broadcast("message.confluence.pirate_invasion.start");
        for (var player : Set.copyOf(progress.getPlayers())) {
            if (player.level() != level) progress.removePlayer(player);
        }
        for (var player : level.players()) {
            if (!player.isSpectator()) progress.addPlayer(player);
        }
        progress.setVisible(ready == 0);
        progress.setProgress(required == 0 ? 0 : Math.min(1, (float) killed / required));
    }

    @Override
    public void countKilled(LivingEntity entity) {
        if (active && entity.getTags().contains(ENTITY_TAG)) {
            killed += entity.getType() == MonsterEntities.PIRATE_CAPTAIN.get() ? 5 : 1;
        }
    }

    @Override
    public boolean canSpawnEntity(ServerLevel world, EntityType<?> type) {
        if (type != MonsterEntities.PIRATE_CAPTAIN.get()) return true;
        for (Entity entity : world.getAllEntities()) {
            if (entity.isAlive() && entity.getType() == type) return false;
        }
        return true;
    }

    @Override
    public boolean canStart() {
        if (requested) return true;
        return level != null && LibDateUtils.getDayTime(level) == LibDateUtils._04$30
                && KillBoard.INSTANCE.getGamePhase().isHardmode() && ConfluenceData.get(level).getRevealStep() >= 0
                && !GameEventSystem.anyInvasionStarted() && level.players().stream().anyMatch(player -> player.getMaxHealth() >= 40)
                && level.random.nextInt(KillBoard.INSTANCE.isDefeated(KEY) ? 50 : 30) == 0;
    }

    @Override
    public boolean canEnd() {
        return active && killed >= required;
    }

    @Override
    public void onStart() {
        active = true;
        requested = false;
        ready = 600;
        killed = 0;
        required = 120 + 60 * (int) level.players().stream().filter(player -> player.getMaxHealth() >= 40).count();
        broadcast("message.confluence.pirate_invasion.ready");
    }

    @Override
    public void onEnd() {
        active = false;
        progress.removeAllPlayers();
        spawned.clear();
        broadcast("message.confluence.pirate_invasion.victory");
    }

    private void broadcast(String key) {
        level.getServer().getPlayerList().broadcastSystemMessage(Component.translatable(key), false);
    }

    @Override
    public boolean started() {return active;}

    @Override
    public boolean forceStart() {
        if (active || level == null || GameEventSystem.anyInvasionStarted()
                || level.players().stream().noneMatch(player -> player.getMaxHealth() >= 40))
            return false;
        requested = true;
        return true;
    }

    @Override
    public void forceEnd() {
        if (active) killed = required;
        requested = false;
    }

    @Override
    public void decode(CompoundTag tag) {
        active = tag.getBoolean("Started");
        ready = tag.getInt("Ready");
        killed = tag.getInt("Killed");
        required = tag.getInt("Required");
    }

    @Override
    public void encode(CompoundTag tag) {
        tag.putBoolean("Started", active);
        tag.putInt("Ready", ready);
        tag.putInt("Killed", killed);
        tag.putInt("Required", required);
    }

    @Override
    public ResourceKey<PirateInvasionGameEvent> key() {return KEY;}
}
