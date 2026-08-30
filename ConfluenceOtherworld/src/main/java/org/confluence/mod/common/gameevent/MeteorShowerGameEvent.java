package org.confluence.mod.common.gameevent;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.lib.util.NaturalSpawnerUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.util.OverworldUtils;
import org.confluence.terraentity.entity.animal.SimpleVariantAnimal;
import org.confluence.terraentity.entity.animal.VariantsTextureMaps;
import org.confluence.terraentity.init.entity.TEAnimals;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum MeteorShowerGameEvent implements GameEvent {
    INSTANCE;
    public static final ResourceKey<MeteorShowerGameEvent> KEY = GameEvent.createKey(Confluence.asResource("meteor_shower"));
    public static final String ENTITY_TAG = "spawn_during_meteor_shower";

    private transient boolean isCelebrationMK10;
    private transient ServerLevel level;
    private transient boolean forceStart;
    private transient boolean forceEnd;
    private transient final Set<Entity> spawned = new HashSet<>();
    private boolean started;

    public final CustomSpawner spawner = (level, spawnEnemies, spawnFriendlies) -> {
        if (!started || !spawnFriendlies) return 0;
        Long2ObjectMap<NaturalSpawnerUtils.ChunkSpawnData> map = NaturalSpawnerUtils.getDimensionChunkSpawnData(level.dimension());
        if (map == null) {
            forceEnd();
            return 0;
        }
        GameEventSystem.removeUnTracked(spawned, level);
        List<ServerPlayer> players = level.players();
        if (spawned.size() >= CommonConfigs.METEOR_SHOWER_EVENT_MAX_ENCHANTED_NIGHTCRAWLERS_BASE.get() + players.size() * CommonConfigs.METEOR_SHOWER_EVENT_MAX_ENCHANTED_NIGHTCRAWLERS_PER_PLAYER.get()) {
            return 0;
        }
        ServerChunkCache chunkCache = level.getChunkSource();
        int last = spawned.size();
        for (ServerPlayer player : players) {
            NaturalSpawnerUtils.ChunkSpawnData data = map.getOrDefault(player.chunkPosition().toLong(), NaturalSpawnerUtils.ChunkSpawnData.DEFAULT);
            double speed = data.speedMultiplier();
            if (speed <= 0) continue;
            int interval = Mth.floor(20 * CommonConfigs.METEOR_SHOWER_EVENT_SPAWN_ENCHANTED_NIGHTCRAWLERS_INTERVAL_FACTOR.get() / speed);
            if (level.random.nextInt(interval) != 0) continue;
            Vec3 position = player.position();
            int count = data.getCount(1);
            for (int j = 0; j < count; j++) {
                double x = Mth.nextDouble(level.random, position.x - 32, position.x + 32);
                double z = Mth.nextDouble(level.random, position.z - 32, position.z + 32);
                int cx = SectionPos.blockToSectionCoord(x);
                int cz = SectionPos.blockToSectionCoord(z);
                if (LibUtils.getChunkIfLoaded(chunkCache, cx, cz) == null) {
                    continue;
                }
                EntityType<SimpleVariantAnimal> type = TEAnimals.WORM.get();
                BlockPos pos = NaturalSpawner.getTopNonCollidingPos(level, type, Mth.floor(x), Mth.floor(z));
                SimpleVariantAnimal worm = type.spawn(level, pos, MobSpawnType.EVENT);
                if (worm != null) {
                    worm.setVariant(VariantsTextureMaps.ENCHANTED_NIGHTCRAWLER_ID);
                    worm.addTag(ENTITY_TAG);
                    spawned.add(worm);
                }
            }
        }
        return spawned.size() - last;
    };

    @Override
    public void open(MinecraftServer server) {
        this.isCelebrationMK10 = ModSecretSeeds.CELEBRATIONMK10.match(server);
        this.level = OverworldUtils.getLevel(server);
    }

    @Override
    public void close(MinecraftServer server) {
        this.level = null;
        for (Entity entity : spawned) {
            entity.discard();
        }
        spawned.clear();
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
                        ? CommonConfigs.METEOR_SHOWER_EVENT_CELEBRATIONMK10_FREQUENCY.get()
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
        spawned.clear();
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
