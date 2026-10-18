package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import org.confluence.lib.common.data.saved.IGlobalData;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.entity.BossEntities;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

// 双眼共同的持久化遭遇记录，不生成管理实体，也不驱动眼球的攻击。
public final class TwinEyeEncounter implements IGlobalData {
    public static final TwinEyeEncounter INSTANCE = new TwinEyeEncounter();
    private static final String DATA_NAME = "confluence_twin_eye_encounters";
    private final Map<UUID, State> encounters = new HashMap<>();
    private final Set<String> migratedDimensions = new HashSet<>();

    private TwinEyeEncounter() {}

    private static TwinEyeEncounter get(ServerLevel level) {
        INSTANCE.importLegacy(level);
        return INSTANCE;
    }

    // 旧版独立数据文件只读迁移一次，后续仅由 IGlobalData 写入；不再注册旧存储类型。
    private void importLegacy(ServerLevel level) {
        String dimension = level.dimension().location().toString();
        if (migratedDimensions.contains(dimension)) return;
        Path file = DimensionType.getStorageFolder(level.dimension(), level.getServer().getWorldPath(LevelResource.ROOT))
                .resolve("data").resolve(DATA_NAME + ".dat");
        if (Files.exists(file)) {
            try {
                CompoundTag legacy = NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap());
                readEntries(legacy.getCompound("data"));
            } catch (IOException exception) {
                Confluence.LOGGER.error("Failed to import legacy twin eye encounter data from {}", file, exception);
                return;
            }
        }
        migratedDimensions.add(dimension);
    }

    static State state(AbstractTwinEye eye) {
        ServerLevel level = (ServerLevel) eye.level();
        TwinEyeEncounter data = get(level);
        State state = data.encounters.computeIfAbsent(eye.getEncounterUUID(), ignored -> new State());
        state.update(eye);
        return state;
    }

    static void removed(AbstractTwinEye eye) {
        TwinEyeEncounter data = get((ServerLevel) eye.level());
        State state = data.encounters.get(eye.getEncounterUUID());
        if (state == null) return;
        state.removed |= eye.isRetinazer() ? 1 : 2;
        if (eye.isAlive()) state.abandoned = true;
        if (state.removed == 3) data.encounters.remove(eye.getEncounterUUID());
    }

    // 两个眼球先完成初始化并绑定同一个遭遇，再一起加入世界；任何一侧失败都撤销本次召唤。
    public static boolean summon(ServerLevel level, Vec3 position, @Nullable Player summoner) {
        Retinazer laser = BossEntities.RETINAZER.get().create(level);
        Spazmatism flame = BossEntities.SPAZMATISM.get().create(level);
        if (laser == null || flame == null) return false;
        UUID encounterId = UUID.randomUUID();
        laser.setEncounterUUID(encounterId);
        flame.setEncounterUUID(encounterId);
        laser.setPos(position.add(-4, 5, 0));
        flame.setPos(position.add(4, 1, 0));
        if (!prepare(level, laser, summoner) || !prepare(level, flame, summoner)) {
            laser.discard();
            flame.discard();
            get(level).encounters.remove(encounterId);
            return false;
        }
        state(laser);
        state(flame);
        if (!level.addFreshEntity(laser) || !level.addFreshEntity(flame)) {
            laser.discard();
            flame.discard();
            get(level).encounters.remove(encounterId);
            return false;
        }
        return true;
    }

    private static boolean prepare(ServerLevel level, AbstractTwinEye eye, @Nullable Player summoner) {
        EventHooks.finalizeMobSpawn(eye, level, level.getCurrentDifficultyAt(eye.blockPosition()), MobSpawnType.SPAWN_EGG, null);
        if (eye.isSpawnCancelled()) return false;
        BossMultiplayerEnhancement.apply(eye, true);
        eye.setHealth(eye.getMaxHealth());
        if (summoner != null) eye.initializeSummonedCombat(summoner);
        return true;
    }

    static final class State {
        long targetSelectionTick = Long.MIN_VALUE;
        UUID sharedTarget;
        UUID laser;
        UUID flame;
        float laserHealth;
        float flameHealth;
        float laserMaximum;
        float flameMaximum;
        int defeated;
        int removed;
        boolean settled;
        boolean abandoned;
        final Set<UUID> participants = new LinkedHashSet<>();
        final Set<UUID> mayhemParticipants = new LinkedHashSet<>();

        void update(AbstractTwinEye eye) {
            if (eye.isRetinazer()) {
                laser = eye.getUUID();
                laserHealth = (defeated & 1) == 0 ? Math.max(0, eye.getHealth()) : 0;
                laserMaximum = eye.getMaxHealth();
            } else {
                flame = eye.getUUID();
                flameHealth = (defeated & 2) == 0 ? Math.max(0, eye.getHealth()) : 0;
                flameMaximum = eye.getMaxHealth();
            }
            participants.addAll(eye.combatParticipantIdsSnapshot());
            mayhemParticipants.addAll(eye.mechanicalMayhemParticipantIdsSnapshot());
            eye.mergeEncounterParticipants(participants, mayhemParticipants);
        }

        boolean isPrimary(boolean laserEye) {
            return !abandoned && (laserEye ? (defeated & 1) == 0 : (defeated & 1) != 0);
        }

        @Nullable AbstractTwinEye partner(AbstractTwinEye eye) {
            UUID id = eye.isRetinazer() ? flame : laser;
            return id != null && ((ServerLevel) eye.level()).getEntity(id) instanceof AbstractTwinEye partner
                    && eye.getEncounterUUID().equals(partner.getEncounterUUID()) ? partner : null;
        }

        boolean recordDeath(boolean laserEye) {
            defeated |= laserEye ? 1 : 2;
            if (laserEye) laserHealth = 0;
            else flameHealth = 0;
            if (defeated != 3 || settled || abandoned) return false;
            settled = true;
            return true;
        }

        float health() {return laserHealth + flameHealth;}

        float maximumHealth() {return laserMaximum + flameMaximum;}
    }

    @Override
    public void decode(CompoundTag tag) {
        clear();
        for (Tag dimension : tag.getList("MigratedDimensions", Tag.TAG_STRING))
            migratedDimensions.add(dimension.getAsString());
        readEntries(tag);
    }

    private void readEntries(CompoundTag tag) {
        ListTag entries = tag.getList("Encounters", Tag.TAG_COMPOUND);
        for (int i = 0; i < entries.size(); i++) {
            CompoundTag entry = entries.getCompound(i);
            if (!entry.hasUUID("Id")) continue;
            State state = new State();
            state.laser = entry.hasUUID("Laser") ? entry.getUUID("Laser") : null;
            state.flame = entry.hasUUID("Flame") ? entry.getUUID("Flame") : null;
            state.laserHealth = entry.getFloat("LaserHealth");
            state.flameHealth = entry.getFloat("FlameHealth");
            state.laserMaximum = entry.getFloat("LaserMaximum");
            state.flameMaximum = entry.getFloat("FlameMaximum");
            state.defeated = entry.getInt("Defeated") & 3;
            state.removed = entry.getInt("Removed") & 3;
            state.settled = entry.getBoolean("Settled");
            state.abandoned = entry.getBoolean("Abandoned");
            readIds(entry, "Participants", state.participants);
            readIds(entry, "MayhemParticipants", state.mayhemParticipants);
            encounters.putIfAbsent(entry.getUUID("Id"), state);
        }
    }

    private static void readIds(CompoundTag tag, String key, Set<UUID> ids) {
        for (Tag id : tag.getList(key, Tag.TAG_INT_ARRAY)) ids.add(NbtUtils.loadUUID(id));
    }

    private static ListTag writeIds(Set<UUID> ids) {
        ListTag tags = new ListTag();
        ids.forEach(id -> tags.add(NbtUtils.createUUID(id)));
        return tags;
    }

    @Override
    public void encode(CompoundTag tag) {
        ListTag entries = new ListTag();
        encounters.forEach((id, state) -> {
            CompoundTag entry = new CompoundTag();
            entry.putUUID("Id", id);
            if (state.laser != null) entry.putUUID("Laser", state.laser);
            if (state.flame != null) entry.putUUID("Flame", state.flame);
            entry.putFloat("LaserHealth", state.laserHealth);
            entry.putFloat("FlameHealth", state.flameHealth);
            entry.putFloat("LaserMaximum", state.laserMaximum);
            entry.putFloat("FlameMaximum", state.flameMaximum);
            entry.putInt("Defeated", state.defeated);
            entry.putInt("Removed", state.removed);
            entry.putBoolean("Settled", state.settled);
            entry.putBoolean("Abandoned", state.abandoned);
            entry.put("Participants", writeIds(state.participants));
            entry.put("MayhemParticipants", writeIds(state.mayhemParticipants));
            entries.add(entry);
        });
        tag.put("Encounters", entries);
        ListTag dimensions = new ListTag();
        migratedDimensions.stream().sorted().forEach(id -> dimensions.add(StringTag.valueOf(id)));
        tag.put("MigratedDimensions", dimensions);
    }

    @Override
    public String serializeKey() {return "confluence:twin_eye_encounters";}

    @Override
    public void clear() {
        encounters.clear();
        migratedDimensions.clear();
    }

}
