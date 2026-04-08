package org.confluence.mod.common.gameevent;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.Util;
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
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.commons.lang3.stream.Streams;
import org.confluence.lib.color.GlobalColors;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.lib.util.NaturalSpawnerUtil;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.gameevent.GameEventSpawnerDataModificationEvent;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.mod.util.OverworldUtils;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.init.entity.TEMonsterEntities;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/// [史莱姆雨](https://terraria.wiki.gg/zh/wiki/%E5%8F%B2%E8%8E%B1%E5%A7%86%E9%9B%A8)
public final class SlimeRainGameEvent implements GameEvent {
    public static final ResourceKey<SlimeRainGameEvent> KEY = GameEvent.createKey(Confluence.asResource("slime_rain"));
    public static final SlimeRainGameEvent INSTANCE = new SlimeRainGameEvent();
    public static final String ENTITY_TAG = "spawn_during_slime_rain";
    private static final int _12$00 = LibDateUtils.getDayTime(12, 0);
    private transient MinecraftServer server;
    private transient ServerLevel level;
    private boolean started;
    private int cooldown;
    private int duration;
    private int killed;
    private boolean spawnedKingSlime;
    private transient boolean forceStart;
    private transient boolean forceEnd;
    private transient boolean haveKingSlime;
    private transient final Set<Entity> spawned = new HashSet<>();
    private transient WeightedRandomList<MobSpawnSettings.SpawnerData> spawnerData = WeightedRandomList.create();

    private SlimeRainGameEvent() {}

    @Override
    public void open(MinecraftServer server) {
        this.server = server;
        this.level = OverworldUtils.getLevel(server);
        if (duration <= 0 && cooldown <= 0) {
            this.cooldown = level.getGameTime() < 24000L ? level.random.nextIntBetweenInclusive(24, 48) * 1200 : 0;
        }
        this.forceStart = false;
        this.haveKingSlime = false;
        this.spawnerData = NeoForge.EVENT_BUS.post(new GameEventSpawnerDataModificationEvent(KEY, level,
                new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLUE_SLIME.get(), 200, 1, 1),
                new MobSpawnSettings.SpawnerData(TEMonsterEntities.GREEN_SLIME.get(), 300, 1, 1),
                new MobSpawnSettings.SpawnerData(TEMonsterEntities.PURPLE_SLIME.get(), 100, 1, 1),
                new MobSpawnSettings.SpawnerData(TEMonsterEntities.PINK_SLIME.get(), 1, 1, 1)
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
        if (duration <= 0) return;
        --this.duration;
        if (duration % 20 == 4) {
            this.haveKingSlime = Streams.of(level.getAllEntities()).anyMatch(entity -> entity.getType() == TEBossEntities.KING_SLIME.get());
        }
        Long2ObjectMap<NaturalSpawnerUtil.ChunkSpawnData> map = NaturalSpawnerUtil.getDimensionChunkSpawnData(level.dimension());
        if (map == null) {
            forceEnd();
            return;
        }
        GameEventSystem.removeUnTracked(spawned, level);
        List<ServerPlayer> players = level.players();
        if (spawned.size() >= CommonConfigs.SLIME_RAIN_EVENT_MAX_ENEMIES_BASE.get() + players.size() * CommonConfigs.SLIME_RAIN_EVENT_MAX_ENEMIES_PER_PLAYER.get()) {
            return;
        }
        for (ServerPlayer player : players) {
            Vec3 position = player.position();
            double y = position.y + 64;
            NaturalSpawnerUtil.ChunkSpawnData data = map.getOrDefault(player.chunkPosition().toLong(), NaturalSpawnerUtil.ChunkSpawnData.DEFAULT);
            double speed = data.speedMultiplier();
            if (speed <= 0) continue;
            int interval = Mth.floor(20 * CommonConfigs.SLIME_RAIN_EVENT_SPAWN_INTERVAL_FACTOR.get() / speed);
            if (haveKingSlime) {
                interval *= 5;
            }
            if (level.random.nextInt(interval) != 0) continue;
            int tryTimes = data.getCount(CommonConfigs.SLIME_RAIN_EVENT_SPAWN_GROUP_SIZE.get());
            for (int i = 0; i < tryTimes; i++) {
                Optional<MobSpawnSettings.SpawnerData> random = spawnerData.getRandom(level.random);
                if (random.isEmpty()) continue;
                MobSpawnSettings.SpawnerData spawnerData = random.get();
                int count = level.random.nextIntBetweenInclusive(spawnerData.minCount, spawnerData.maxCount);
                for (int j = 0; j < count; j++) {
                    double x = LibMathUtils.randomFromTo(level.random, position.x, 24, 32);
                    double z = LibMathUtils.randomFromTo(level.random, position.z, 24, 32);
                    int cx = SectionPos.blockToSectionCoord(x);
                    int cz = SectionPos.blockToSectionCoord(z);
                    if (LibUtils.getChunkIfLoaded(level, cx, cz) == null) {
                        continue;
                    }
                    Entity entity = spawnerData.type.spawn(level, BlockPos.containing(x, y, z), MobSpawnType.EVENT);
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
            if (haveKingSlime) return;
            int count = CommonConfigs.SLIME_RAIN_EVENT_KING_SLIME_SPAWN_REQUIRED_KILL_COUNT.get();
            if (spawnedKingSlime) {
                count = count * 3 / 2;
            }
            if (KillBoard.INSTANCE.isDefeated(TEBossEntities.KING_SLIME.get())) {
                count /= 2;
            }
            if (killed >= count) {
                ServerPlayer player = Util.getRandom(level.players(), level.random);
                TEBossEntities.KING_SLIME.get().create(level, entity -> {
                    this.killed = 0;
                    this.spawnedKingSlime = true;
                    entity.addTag(ENTITY_TAG);
                    level.addFreshEntityWithPassengers(entity);
                }, BlockPos.containing(
                        player.getX() + Mth.randomBetweenInclusive(level.random, -50, 50),
                        player.getY(),
                        player.getZ() + Mth.randomBetweenInclusive(level.random, -50, 50)
                ), MobSpawnType.EVENT, true, false);
            }
        }
    }

    @Override
    public boolean canStart() {
        if (forceStart) {
            return true;
        }
        int invChance = 0;
        if (LibDateUtils.isWithinDayTime(LibDateUtils._04$30, _12$00, level) && // 时间
                !level.isRaining() && !BloodMoonGameEvent.INSTANCE.started() // 事件
        ) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                boolean expert = LibUtils.isAtLeastExpert(level, player.blockPosition());
                if (player.getMaxHealth() >= CommonConfigs.SLIME_RAIN_EVENT_REQUIRED_PLAYER_MAX_HEALTH.get() &&
                        player.getArmorValue() >= CommonConfigs.SLIME_RAIN_EVENT_REQUIRED_PLAYER_ARMOR.get()
                ) {
                    invChance = CommonConfigs.SLIME_RAIN_EVENT_FREQUENCY.get(); // 满足要求
                    break;
                } else if (expert) {
                    invChance = CommonConfigs.SLIME_RAIN_EVENT_FREQUENCY.get() * 5; // 不满足要求但是专家模式
                    break;
                }
            }
        }
        if (invChance > 0) {
            if (KillBoard.INSTANCE.isDefeated(TEBossEntities.KING_SLIME.get())) { // 击败史莱姆王
                invChance *= 2;
            }
            if (KillBoard.INSTANCE.getGamePhase().isHardmode()) { // 困难模式
                invChance = invChance * 3 / 2;
            }
            return level.random.nextInt(invChance) == 0;
        }
        return false;
    }

    @Override
    public boolean canEnd() {
        if (forceEnd) {
            return true;
        }
        return duration <= 0;
    }

    @Override
    public void onStart() {
        this.started = true;
        this.forceStart = false;
        this.killed = 0;
        this.duration = server.isDedicatedServer() ? 15 * 1200 : level.random.nextIntBetweenInclusive(9, 15) * 1200;
        Component component = Component.translatable("message.confluence.slime_rain.start").withColor(GlobalColors.MESSAGE.get());
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.sendSystemMessage(component);
        }
        this.haveKingSlime = false;
    }

    @Override
    public void onEnd() {
        this.started = false;
        this.forceEnd = false;
        this.duration = 0;
        this.cooldown = server.isDedicatedServer() ? 0 : level.random.nextIntBetweenInclusive(84, 180) * 1200;
        Component component = Component.translatable("message.confluence.slime_rain.end").withColor(GlobalColors.MESSAGE.get());
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.sendSystemMessage(component);
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
        this.cooldown = 0;
        this.forceStart = true;
        this.spawnedKingSlime = false;
        return true;
    }

    @Override
    public void forceEnd() {
        if (started) {
            this.forceEnd = true;
        }
    }

    public void checkEnd(LivingEntity victim) {
        if (started && victim.getType() == TEBossEntities.KING_SLIME.get()) {
            forceEnd();
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                AchievementUtils.awardAchievement(player, "sticky_situation");
            }
        }
    }

    @Override
    public void decode(CompoundTag tag) {
        this.started = tag.getBoolean("Started");
        this.cooldown = tag.getInt("Cooldown");
        this.duration = tag.getInt("Duration");
        this.killed = tag.getInt("Killed");
        this.spawnedKingSlime = tag.getBoolean("SpawnedKingSlime");
    }

    @Override
    public void encode(CompoundTag tag) {
        tag.putBoolean("Started", started);
        tag.putInt("Cooldown", cooldown);
        tag.putInt("Duration", duration);
        tag.putInt("Killed", killed);
        tag.putBoolean("SpawnedKingSlime", spawnedKingSlime);
    }

    @Override
    public ResourceKey<SlimeRainGameEvent> key() {
        return KEY;
    }
}
