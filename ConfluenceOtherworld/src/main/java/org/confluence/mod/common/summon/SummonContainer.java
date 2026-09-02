package org.confluence.mod.common.summon;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.summon.projectile.SummonProjectileInstance;
import org.confluence.mod.network.s2c.SummonSyncPacketS2C;
import org.mesdag.portlib.wrapper.IPortNBTSerializable;

import java.util.*;

/// 维护玩家当前拥有的召唤物运行实例，并统一处理容量、顺序和生命周期。
public final class SummonContainer implements IPortNBTSerializable<CompoundTag> {
    private static final int FORMAT_VERSION = 1;
    private final List<SummonInstance> summons = new ArrayList<>();
    private final List<SummonProjectileInstance> projectiles = new ArrayList<>();
    private final List<SummonSavedState> pendingSummons = new ArrayList<>();
    private final Map<ResourceLocation, Integer> groupCounts = new HashMap<>();
    private final Map<ResourceLocation, Integer> groupOrders = new HashMap<>();
    private boolean clientHasEntries;

    public static SummonContainer of(Player player) {
        return player.getData(ModAttachmentTypes.SUMMONS);
    }

    public List<SummonInstance> entries() {
        return Collections.unmodifiableList(summons);
    }

    public int occupiedSlots() {
        int occupied = 0;
        for (SummonInstance summon : summons) {
            if (!summon.isRemoved()) occupied += summon.slotCost();
        }
        for (SummonSavedState pending : pendingSummons) occupied += pending.slotCost();
        return occupied;
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
        SummonInstance mergeTarget = null;
        for (SummonInstance existing : summons) {
            if (existing.type().equals(summon.type()) && existing.canMergeAdditionalSummon()) {
                mergeTarget = existing;
                break;
            }
        }
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

    public void addProjectile(SummonProjectileInstance projectile) {
        projectiles.add(projectile);
    }

    public int clear() {
        int size = summons.size() + projectiles.size() + pendingSummons.size();
        summons.forEach(SummonInstance::remove);
        summons.clear();
        projectiles.clear();
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
    public void sync(ServerPlayer owner) {
        SummonSyncPacketS2C.send(owner, summons, projectiles);
        clientHasEntries = !summons.isEmpty() || !projectiles.isEmpty();
    }

    public void tick(ServerPlayer owner) {
        restorePending(owner);
        projectiles.forEach(SummonProjectileInstance::tick);
        summons.forEach(SummonInstance::tick);
        removeMarked();
        refreshGroupState();
        if (!summons.isEmpty() || !projectiles.isEmpty()) {
            sync(owner);
        } else if (clientHasEntries) {
            sync(owner);
        }
    }

    private void removeMarked() {
        summons.removeIf(SummonInstance::isRemoved);
        projectiles.removeIf(SummonProjectileInstance::isRemoved);
    }

    private void refreshGroupState() {
        groupCounts.clear();
        groupOrders.clear();
        for (SummonInstance summon : summons) groupCounts.merge(summon.groupKey(), 1, Integer::sum);
        for (SummonInstance summon : summons) {
            ResourceLocation group = summon.groupKey();
            int order = groupOrders.getOrDefault(group, 0);
            summon.updateGroupState(order, groupCounts.get(group));
            groupOrders.put(group, order + 1);
        }
    }

    private void restorePending(ServerPlayer owner) {
        if (pendingSummons.isEmpty()) {
            return;
        }
        Set<UUID> restoredIds = new HashSet<>();
        for (SummonInstance summon : summons) restoredIds.add(summon.uuid());
        for (SummonSavedState savedState : pendingSummons) {
            if (!restoredIds.add(savedState.uuid())) continue;
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
        Set<UUID> pendingIds = new HashSet<>();
        for (Tag entry : entries) {
            SummonSavedState savedState = SummonSavedState.fromTag((CompoundTag) entry);
            if (savedState != null && pendingIds.add(savedState.uuid())) {
                pendingSummons.add(savedState);
            }
        }
    }

}
