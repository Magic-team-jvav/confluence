package org.confluence.mod.common.gameevent;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import org.confluence.lib.color.GlobalColors;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.lib.util.NaturalSpawnerUtil;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.gameevent.GameEventSpawnerDataModificationEvent;
import org.confluence.mod.common.data.saved.ConfluenceData;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.mod.util.OverworldUtils;
import org.confluence.terraentity.init.entity.TEMonsterEntities;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class GoblinArmyGameEvent implements GameEvent {
    public static final ResourceKey<GoblinArmyGameEvent> KEY = GameEvent.createKey(Confluence.asResource("goblin_army"));
    public static final GoblinArmyGameEvent INSTANCE = new GoblinArmyGameEvent();
    public static final String ENTITY_TAG = "spawn_during_goblin_army";
    private transient MinecraftServer server;
    private transient ServerLevel level;
    private transient boolean forceStart;
    private transient boolean forceEnd;
    private transient final Set<Entity> spawned = new HashSet<>();
    private transient WeightedRandomList<MobSpawnSettings.SpawnerData> spawnerData = WeightedRandomList.create();
    private boolean started;
    private int ready;
    private int killed;
    private int required;

    private GoblinArmyGameEvent() {}

    @Override
    public void open(MinecraftServer server) {
        this.server = server;
        this.level = OverworldUtils.getLevel(server);
        this.spawnerData = NeoForge.EVENT_BUS.post(new GameEventSpawnerDataModificationEvent(KEY, level,
                new MobSpawnSettings.SpawnerData(TEMonsterEntities.GOBLIN_ARCHER.get(), 360, 2, 4),
                new MobSpawnSettings.SpawnerData(TEMonsterEntities.GOBLIN_PEON.get(), 480, 2, 3),
                new MobSpawnSettings.SpawnerData(TEMonsterEntities.GOBLIN_WARRIOR.get(), 360, 2, 3),
                new MobSpawnSettings.SpawnerData(TEMonsterEntities.GOBLIN_SORCERER.get(), 240, 1, 1),
                new MobSpawnSettings.SpawnerData(TEMonsterEntities.GOBLIN_THIEF.get(), 480, 2, 4),
                new MobSpawnSettings.SpawnerData(TEMonsterEntities.ANGER_GOBLIN.get(), 240, 1, 2)
        )).create();
    }

    @Override
    public void close(MinecraftServer server) {
        this.server = null;
        this.level = null;
        for (Entity entity : spawned) {
            entity.discard();
        }
        spawned.clear();
    }

    @Override
    public void tick() {
        if (!started) return;
        if (ready > 0) {
            --this.ready;
        } else if (ready != -1) {
            this.ready = -1;
            Component component = Component.translatable("message.confluence.goblin_army.start").withColor(GlobalColors.EVENT.get());
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                player.sendSystemMessage(component);
            }
        } else {
            Long2ObjectMap<NaturalSpawnerUtil.ChunkSpawnData> map = NaturalSpawnerUtil.getDimensionChunkSpawnData(level.dimension());
            if (map == null) {
                forceEnd();
                return;
            }
            spawned.removeIf(entity -> {
                if (LibUtils.getChunkIfLoaded(level.getChunkSource(), entity.chunkPosition()) == null) {
                    entity.discard();
                    return true;
                }
                return entity.isRemoved();
            });
            List<ServerPlayer> players = level.players();
            if (spawned.size() >= 30 + players.size() * 30) return;
            for (ServerPlayer player : players) {
                NaturalSpawnerUtil.ChunkSpawnData data = map.getOrDefault(player.chunkPosition().toLong(), NaturalSpawnerUtil.ChunkSpawnData.DEFAULT);
                double speed = data.speedMultiplier();
                if (speed <= 0) continue;
                int interval = Mth.floor(server.tickRateManager().tickrate() * 1.5 / speed);
                if (level.random.nextInt(interval) != 0) continue;
                Optional<MobSpawnSettings.SpawnerData> random = spawnerData.getRandom(level.random);
                if (random.isEmpty()) continue;
                Vec3 position = player.position();
                MobSpawnSettings.SpawnerData spawnerData = random.get();
                int count = data.getCount(level.random.nextIntBetweenInclusive(spawnerData.minCount, spawnerData.maxCount));
                for (int j = 0; j < count; j++) {
                    double x = Mth.nextDouble(level.random, position.x - 32, position.x + 32);
                    double z = Mth.nextDouble(level.random, position.z - 32, position.z + 32);
                    int cx = SectionPos.blockToSectionCoord(x);
                    int cz = SectionPos.blockToSectionCoord(z);
                    if (LibUtils.getChunkIfLoaded(level.getChunkSource(), cx, cz) == null) {
                        continue;
                    }
                    BlockPos pos = NaturalSpawner.getTopNonCollidingPos(level, spawnerData.type, Mth.floor(x), Mth.floor(z));
                    Entity entity = spawnerData.type.spawn(level, pos, MobSpawnType.EVENT);
                    if (entity != null) {
                        entity.addTag(ENTITY_TAG);
                        spawned.add(entity);
                        if (entity instanceof Mob mob && player.canBeSeenAsEnemy()) {
                            mob.setTarget(player);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void countKilled(LivingEntity living) {
        if (started && living.getTags().contains(ENTITY_TAG)) {
            ++this.killed;
        }
    }

    @Override
    public boolean canStart() {
        if (forceStart) {
            return true;
        }
        if (LibDateUtils.getDayTime(level) == LibDateUtils._04$30 &&
                !GameEventSystem.anyInvasionStarted() &&
                ConfluenceData.get(level).getEvilBrokenCount() > 0
        ) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                if (player.getMaxHealth() >= 40 && player.getArmorValue() >= 16) {
                    int chance;
                    if (KillBoard.INSTANCE.isDefeated(KEY)) {
                        if (KillBoard.INSTANCE.getGamePhase().isHardmode()) {
                            chance = 60;
                        } else {
                            chance = 30;
                        }
                    } else {
                        chance = 3;
                    }
                    return level.random.nextInt(chance) == 0;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canEnd() {
        if (forceEnd) {
            return true;
        }
        return killed >= required;
    }

    @Override
    public void onStart() {
        this.started = true;
        this.forceStart = false;
        this.ready = 53 * 20;
        int count = 0;
        Component component = Component.translatable("message.confluence.goblin_army.ready").withColor(GlobalColors.EVENT.get());
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.sendSystemMessage(component);
            if (player.getMaxHealth() >= 40) {
                ++count;
            }
        }
        this.required = 80 + Math.min(count, 255) * 40;
    }

    @Override
    public void onEnd() {
        this.started = false;
        this.forceStart = false;
        this.killed = 0;
        Component component = Component.translatable("message.confluence.goblin_army.victory").withColor(GlobalColors.EVENT.get());
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.sendSystemMessage(component);
            AchievementUtils.awardAchievement(player, "goblin_punter");
        }
        spawned.clear();
    }

    @Override
    public boolean started() {
        return started;
    }

    @Override
    public boolean forceStart() {
        if (started) return false;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.getMaxHealth() >= 40) {
                this.forceStart = true;
                return true;
            }
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
        this.ready = tag.getInt("Ready");
        this.killed = tag.getInt("Killed");
        this.required = tag.getInt("Required");
    }

    @Override
    public void encode(CompoundTag tag) {
        tag.putBoolean("Started", started);
        tag.putInt("Ready", ready);
        tag.putInt("Killed", killed);
        tag.putInt("Required", required);
    }

    @Override
    public ResourceKey<GoblinArmyGameEvent> key() {
        return KEY;
    }
}
