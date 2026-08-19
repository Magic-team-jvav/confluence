package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;

/// Boss 未加载时暂存持久化从属死亡事件的世界邮箱。
///
/// <p>双子魔眼的两个眼球、脑怪飞眼等从属是独立存档实体。如果它们死亡时权威 Boss
/// 所在区块没有加载，从属无法直接调用 Boss 实例更新阶段。邮箱用 Boss UUID 分组，
/// 待 Boss 再次加载时一次性消费，保证死亡事件不会因区块边界丢失。</p>
final class BossChildDeathLedger extends SavedData {
    private static final String DATA_NAME = "confluence_boss_child_deaths";
    private static final String ENTRIES_TAG = "Entries";
    private static final String OWNER_TAG = "Owner";
    private static final String CHILD_TAG = "Child";
    private final Map<UUID, Set<UUID>> deathsByOwner = new HashMap<>();

    static void record(ServerLevel level, UUID ownerUUID, UUID childUUID) {
        get(level).record(ownerUUID, childUUID);
    }

    static void clear(ServerLevel level, UUID ownerUUID) {
        get(level).clear(ownerUUID);
    }

    void record(UUID ownerUUID, UUID childUUID) {
        if (deathsByOwner.computeIfAbsent(ownerUUID, ignored -> new HashSet<>()).add(childUUID)) {
            setDirty();
        }
    }

    void clear(UUID ownerUUID) {
        if (deathsByOwner.remove(ownerUUID) != null) {
            setDirty();
        }
    }

    static Set<UUID> consume(ServerLevel level, UUID ownerUUID) {
        BossChildDeathLedger ledger = get(level);
        Set<UUID> deaths = ledger.deathsByOwner.remove(ownerUUID);
        if (deaths == null || deaths.isEmpty()) return Set.of();
        // 先从持久化状态移除再返回不可变快照，避免 Boss 重入时重复结算同一从属死亡。
        ledger.setDirty();
        return Set.copyOf(deaths);
    }

    private static BossChildDeathLedger get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(BossChildDeathLedger::load, BossChildDeathLedger::new, DATA_NAME);
    }

    static BossChildDeathLedger load(CompoundTag tag) {
        BossChildDeathLedger ledger = new BossChildDeathLedger();
        ListTag entries = tag.getList(ENTRIES_TAG, Tag.TAG_COMPOUND);
        for (int index = 0; index < entries.size(); index++) {
            CompoundTag entry = entries.getCompound(index);
            if (!entry.hasUUID(OWNER_TAG) || !entry.hasUUID(CHILD_TAG)) continue;
            UUID ownerUUID = entry.getUUID(OWNER_TAG);
            ledger.deathsByOwner.computeIfAbsent(ownerUUID, ignored -> new HashSet<>()).add(entry.getUUID(CHILD_TAG));
        }
        return ledger;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag entries = new ListTag();
        for (Map.Entry<UUID, Set<UUID>> ownerDeaths : deathsByOwner.entrySet()) {
            for (UUID childUUID : ownerDeaths.getValue()) {
                CompoundTag entry = new CompoundTag();
                entry.putUUID(OWNER_TAG, ownerDeaths.getKey());
                entry.putUUID(CHILD_TAG, childUUID);
                entries.add(entry);
            }
        }
        tag.put(ENTRIES_TAG, entries);
        return tag;
    }
}
