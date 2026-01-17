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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.color.GlobalColors;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.lib.util.NaturalSpawnerUtil;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.BossDelaySpawner;
import org.confluence.mod.common.data.saved.MoonPhase;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.mod.util.OverworldUtils;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.init.entity.TEMonsterEntities;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class BloodMoonGameEvent implements GameEvent {
    public static final ResourceKey<BloodMoonGameEvent> KEY = GameEvent.createKey(Confluence.asResource("blood_moon"));
    public static final BloodMoonGameEvent INSTANCE = new BloodMoonGameEvent();
    public static final String ENTITY_TAG = "spawn_during_blood_moon";
    private transient MinecraftServer server;
    private transient ServerLevel level;
    private transient boolean forceStart;
    private transient boolean forceEnd;
    private transient final Set<Entity> spawned = new HashSet<>();
    private transient WeightedRandomList<MobSpawnSettings.SpawnerData> spawnerData = WeightedRandomList.create();
    private boolean started;

    private BloodMoonGameEvent() {}

    @Override
    public void open(MinecraftServer server) {
        this.server = server;
        this.level = OverworldUtils.getLevel(server);
        this.spawnerData = WeightedRandomList.create(
                new MobSpawnSettings.SpawnerData(TEMonsterEntities.DRIPPLER.get(), 150, 1, 1),
                new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLOOD_ZOMBIE.get(), 420, 1, 1)
                // todo 事件注册
        );
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
        if (started) {
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
                int count = level.random.nextIntBetweenInclusive(spawnerData.minCount, spawnerData.maxCount);
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
    public boolean canStart() {
        if (forceStart) {
            return true;
        }
        if (LibDateUtils.getDayTime(level) == LibDateUtils._19$30) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                if (player.getMaxHealth() >= 24 && player.getArmorValue() >= 16 && // 属性
                        !MoonPhase.NEW_MOON.match(level) && // 不是新月
                        BossDelaySpawner.INSTANCE.hasSameTypeInQueue(TEBossEntities.EYE_OF_CTHULHU.get()) && // 没有克苏鲁之眼
                        level.random.nextInt(14) == 0 // 1/14的几率
                ) {
                    return true;
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
        return LibDateUtils.isWithinDayTime(LibDateUtils._04$30, LibDateUtils._19$30, level);
    }

    @Override
    public void onStart() {
        this.started = true;
        this.forceStart = false;
        Component component = Component.translatable("message.confluence.blood_moon.start").withColor(GlobalColors.MESSAGE.get());
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.sendSystemMessage(component);
        }
    }

    @Override
    public void onEnd() {
        this.started = false;
        this.forceEnd = false;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            AchievementUtils.awardAchievement(player, "bloodbath");
        }
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
        this.forceEnd = true;
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
    public ResourceKey<BloodMoonGameEvent> key() {
        return KEY;
    }
}
