package org.confluence.mod.common.summon;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.network.s2c.SummonSyncPacketS2C;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/// 维护玩家当前拥有的召唤物运行实例，并统一处理容量、顺序和生命周期。
///
/// <p>调用方只负责创建新的召唤实例；交给容器之后，成功时由容器接管，失败时也由容器清理传入实例。
/// 容器会先计算容量和可替换对象，再真正修改列表，避免多栏位召唤物或可合并召唤物失败后留下空位。</p>
public final class SummonContainer {
    private final List<SummonInstance> summons = new ArrayList<>();
    private boolean clientHasEntries;

    public static SummonContainer of(Player player) {
        return player.getData(ModAttachmentTypes.SUMMONS);
    }

    public List<SummonInstance> entries() {
        return Collections.unmodifiableList(summons);
    }

    public int occupiedSlots() {
        return summons.stream().filter(summon -> !summon.isRemoved()).mapToInt(SummonInstance::slotCost).sum();
    }

    public boolean add(ServerPlayer owner, SummonInstance summon) {
        if (summon.owner() != owner) {
            throw new IllegalArgumentException("Summon owner does not match container owner");
        }
        int capacity = Math.max(0, (int) Math.floor(owner.getAttributeValue(ConfluenceMagicLib.MINION_CAPACITY)));
        if (summon.slotCost() > capacity) {
            summon.remove();
            return false;
        }
        removeMarked();
        SummonInstance mergeTarget = summons.stream().filter(existing -> existing.type().equals(summon.type()))
                .filter(SummonInstance::canMergeAdditionalSummon).findFirst().orElse(null);
        int requiredSlots = occupiedSlots() + summon.slotCost() - capacity;
        List<SummonInstance> displaced = new ArrayList<>();
        int releasedSlots = 0;
        for (int index = summons.size() - 1; index >= 0 && releasedSlots < requiredSlots; index--) {
            SummonInstance candidate = summons.get(index);
            if (candidate == mergeTarget) {
                continue;
            }
            displaced.add(candidate);
            releasedSlots += candidate.slotCost();
        }
        if (releasedSlots < requiredSlots) {
            summon.remove();
            return false;
        }
        if (mergeTarget != null) {
            if (!mergeTarget.tryMergeAdditionalSummon(summon.slotCost(), summon.combatSnapshot())) {
                summon.remove();
                return false;
            }
            for (SummonInstance candidate : displaced) {
                summons.remove(candidate);
                candidate.remove();
            }
            summon.remove();
            refreshGroupState();
            return true;
        }
        for (SummonInstance candidate : displaced) {
            summons.remove(candidate);
            candidate.remove();
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
        int size = summons.size();
        summons.forEach(SummonInstance::remove);
        summons.clear();
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

}
