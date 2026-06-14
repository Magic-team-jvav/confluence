package org.confluence.mod.common.gameevent;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.MinecraftForge;
import org.confluence.lib.color.GlobalColors;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.gameevent.GameEventSpawnerDataModificationEvent;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.data.saved.BossDelaySpawner;
import org.confluence.mod.common.data.saved.MoonPhase;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.mod.util.OverworldUtils;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.init.entity.TEMonsterEntities;

import java.util.HashSet;
import java.util.Set;

public enum BloodMoonGameEvent implements GameEvent {
    INSTANCE;
    public static final ResourceKey<BloodMoonGameEvent> KEY = GameEvent.createKey(Confluence.asResource("blood_moon"));
    public static final String ENTITY_TAG = "spawn_during_blood_moon";

    private transient MinecraftServer server;
    private transient ServerLevel level;
    private transient boolean forceStart;
    private transient boolean forceEnd;
    private transient final Set<Entity> spawned = new HashSet<>();
    private transient WeightedRandomList<MobSpawnSettings.SpawnerData> spawnerData = WeightedRandomList.create();
    private boolean started;

    public final CustomSpawner spawner = (level, spawnEnemies, spawnFriendlies) -> {
        if (!started || !spawnEnemies) return 0;
        return GameEventSystem.customSpawner(this, level, spawned,
                CommonConfigs.BLOOD_MOON_EVENT_MAX_ENEMIES_BASE.get(),
                CommonConfigs.BLOOD_MOON_EVENT_MAX_ENEMIES_PER_PLAYER.get(),
                CommonConfigs.BLOOD_MOON_EVENT_SPAWN_ENEMIES_INTERVAL_FACTOR.get().floatValue(),
                spawnerData, ENTITY_TAG, true);
    };

    @Override
    public void open(MinecraftServer server) {
        this.server = server;
        this.level = OverworldUtils.getLevel(server);
        this.spawnerData = MinecraftForge.EVENT_BUS.post(new GameEventSpawnerDataModificationEvent(KEY, level,
                new MobSpawnSettings.SpawnerData(TEMonsterEntities.DRIPPLER.get(), 150, 1, 1),
                new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLOOD_ZOMBIE.get(), 420, 1, 1)
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
    public void tick() {}

    @Override
    public boolean canStart() {
        if (forceStart) {
            return true;
        }
        if (LibDateUtils.getDayTime(level) == LibDateUtils._19$30 &&
                !MoonPhase.NEW_MOON.match(level) &&
                !BossDelaySpawner.INSTANCE.hasSameTypeInQueue(TEBossEntities.EYE_OF_CTHULHU.get())
        ) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                if (player.getMaxHealth() >= CommonConfigs.BLOOD_MOON_EVENT_REQUIRED_PLAYER_MAX_HEALTH.get() &&
                        player.getArmorValue() >= CommonConfigs.BLOOD_MOON_EVENT_REQUIRED_PLAYER_ARMOR.get() &&
                        level.random.nextInt(CommonConfigs.BLOOD_MOON_EVENT_INVERT_CHANCE.get()) == 0
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
        for (Entity entity : spawned) {
            entity.discard();
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
    public ResourceKey<BloodMoonGameEvent> key() {
        return KEY;
    }
}
