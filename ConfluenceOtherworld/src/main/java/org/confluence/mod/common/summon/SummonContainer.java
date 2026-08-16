package org.confluence.mod.common.summon;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.network.s2c.SummonSyncPacketS2C;
import org.mesdag.portlib.wrapper.IPortNBTSerializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/// 维护玩家当前拥有的召唤物运行实例，并统一处理容量、顺序和生命周期。
///
/// <p>调用方只负责创建新的召唤实例；交给容器之后，成功时由容器接管，失败时也由容器清理传入实例。</p>
public final class SummonContainer implements IPortNBTSerializable<CompoundTag> {
    private static final int FORMAT_VERSION = 1;
    private final List<SummonInstance> summons = new ArrayList<>();
    private final List<SummonSavedState> pendingSummons = new ArrayList<>();
    private boolean clientHasEntries;

    public static SummonContainer of(Player player) {
        return player.getData(ModAttachmentTypes.SUMMONS);
    }

    public List<SummonInstance> entries() {
        return Collections.unmodifiableList(summons);
    }

    public int occupiedSlots() {
        return summons.stream().filter(summon -> !summon.isRemoved()).mapToInt(SummonInstance::slotCost).sum()
                + pendingSummons.stream().mapToInt(SummonSavedState::slotCost).sum();
    }

    public boolean add(ServerPlayer owner, SummonInstance summon) {
        restorePending(owner);
        if (summon.owner() != owner) {
            throw new IllegalArgumentException("Summon owner does not match container owner");
        }
        int capacity = Math.max(0, (int) Math.floor(owner.getAttributeValue(ConfluenceMagicLib.MINION_CAPACITY)));
        removeMarked();
        if (occupiedSlots() + summon.slotCost() > capacity && !summons.isEmpty()) {
            SummonInstance last = summons.remove(summons.size() - 1);
            last.remove();
        }
        if (occupiedSlots() + summon.slotCost() > capacity) {
            summon.remove();
            refreshGroupState();
            return false;
        }
        SummonInstance mergeTarget = summons.stream().filter(existing -> existing.type().equals(summon.type()))
                .filter(SummonInstance::canMergeAdditionalSummon).findFirst().orElse(null);
        if (mergeTarget != null) {
            if (!mergeTarget.tryMergeAdditionalSummon(summon.slotCost(), summon.stats())) {
                summon.remove();
                return false;
            }
            summon.remove();
            refreshGroupState();
            return true;
        }
        summons.add(summon);
        refreshGroupState();
        return true;
    }

    public boolean remove(UUID uuid) {
        for (SummonInstance summon : summons) {
            if (summon.uuid().equals(uuid)) {
                summon.remove();
                removeMarked();
                return true;
            }
        }
        return false;
    }

    public boolean remove(ServerPlayer owner, UUID uuid) {
        if (!remove(uuid)) {
            return false;
        }
        sync(owner);
        return true;
    }

    public int clear() {
        int size = summons.size() + pendingSummons.size();
        summons.forEach(SummonInstance::remove);
        summons.clear();
        pendingSummons.clear();
        return size;
    }

    public int clear(ServerPlayer owner) {
        boolean hadClientEntries = clientHasEntries;
        int size = clear();
        if (size > 0 || hadClientEntries) {
            sync(owner);
        }
        return size;
    }

    /// 主动刷新客户端召唤物列表。
    ///
    /// <p>召唤失败、长按清空、准星收回等入口都通过这里同步，避免物品层直接发包后遗漏容器自身的
    /// 客户端状态记录，造成客户端残留旧召唤物或短暂空位。</p>
    public void sync(ServerPlayer owner) {
        SummonSyncPacketS2C.send(owner, summons);
        clientHasEntries = !summons.isEmpty();
    }

    public void tick(ServerPlayer owner) {
        restorePending(owner);
        summons.forEach(SummonInstance::tick);
        removeMarked();
        refreshGroupState();
        if (!summons.isEmpty()) {
            sync(owner);
        } else if (clientHasEntries) {
            sync(owner);
        }
    }

    private void removeMarked() {
        summons.removeIf(SummonInstance::isRemoved);
    }

    private void refreshGroupState() {
        for (int index = 0; index < summons.size(); index++) {
            SummonInstance summon = summons.get(index);
            int order = 0;
            int sameTypeCount = 0;
            for (int otherIndex = 0; otherIndex < summons.size(); otherIndex++) {
                SummonInstance other = summons.get(otherIndex);
                if (other.groupKey().equals(summon.groupKey())) {
                    if (otherIndex < index) {
                        order++;
                    }
                    sameTypeCount++;
                }
            }
            summon.updateGroupState(order, sameTypeCount);
        }
    }

    private void restorePending(ServerPlayer owner) {
        if (pendingSummons.isEmpty()) {
            return;
        }
        List<UUID> restoredIds = summons.stream().map(SummonInstance::uuid).toList();
        for (SummonSavedState savedState : pendingSummons) {
            if (restoredIds.contains(savedState.uuid())) {
                continue;
            }
            SummonInstance restored = savedState.restore(owner);
            if (restored != null) {
                summons.add(restored);
            }
        }
        pendingSummons.clear();
        refreshGroupState();
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag root = new CompoundTag();
        root.putInt("Version", FORMAT_VERSION);
        ListTag entries = new ListTag();
        pendingSummons.forEach(savedState -> entries.add(savedState.toTag()));
        for (SummonInstance summon : summons) {
            if (!summon.isRemoved()) {
                entries.add(SummonSavedState.capture(summon).toTag());
            }
        }
        root.put("Entries", entries);
        return root;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag root) {
        summons.forEach(SummonInstance::remove);
        summons.clear();
        pendingSummons.clear();
        if (!root.contains("Version", Tag.TAG_INT) || root.getInt("Version") != FORMAT_VERSION
                || !root.contains("Entries", Tag.TAG_LIST)) {
            return;
        }
        ListTag entries = root.getList("Entries", Tag.TAG_COMPOUND);
        for (Tag entry : entries) {
            SummonSavedState savedState = SummonSavedState.fromTag((CompoundTag) entry);
            if (savedState != null && pendingSummons.stream().noneMatch(existing -> existing.uuid().equals(savedState.uuid()))) {
                pendingSummons.add(savedState);
            }
        }
    }

}
