package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/// 可正常进入区块存档的 Boss 生物从属所有权跟踪器。
///
/// 强引用只是当前加载周期的缓存，UUID 才是服务端权威身份。Boss 卸载不等于 Boss 死亡：
/// 从属会保留 UUID，并在同一 {@link ServerLevel} 重新出现后恢复双向关系。只有权威 Boss
/// 的死亡、撤离或显式清理路径才负责级联移除已加载从属。
///
/// 该类不搜索附近“类似”Boss，也不在跨维度时猜测所有者，从而避免多场同类 Boss
/// 战同时进行时从属串场。
public final class BossOwnerTracker<T extends BaseBoss> {
    private static final String OWNER_TAG = "BossOwner";

    private final Class<T> ownerType;
    private @Nullable T owner;
    private @Nullable UUID ownerUUID;
    private int unresolvedTicks;

    public BossOwnerTracker(Class<T> ownerType) {
        this.ownerType = ownerType;
    }

    public void bind(Entity dependent, T owner) {
        this.owner = owner;
        this.ownerUUID = owner.getUUID();
        owner.addSubEntity(dependent);
        if (dependent instanceof Mob mob) {
            synchronizeTarget(mob, owner);
        }
        unresolvedTicks = 0;
    }

    public @Nullable T resolve(Entity dependent) {
        if (owner != null && !owner.isRemoved()) return owner;
        owner = null;
        if (ownerUUID == null || !(dependent.level() instanceof ServerLevel serverLevel))
            return null;

        // ServerLevel#getEntity(UUID) 只会返回当前已加载的精确实体，未加载时保持 UUID 等待下次解析。
        Entity candidate = serverLevel.getEntity(ownerUUID);
        if (ownerType.isInstance(candidate)) {
            owner = ownerType.cast(candidate);
            owner.addSubEntity(dependent);
        }
        return owner;
    }

    /// 统一推进 Boss 从属的所有权恢复、目标继承和孤儿清理。
    ///
    /// 已加载且存活的主人是唯一目标权威；主人明确死亡时从属立即清理。只有 UUID 存在但
    /// 当前实体尚未加载时才进入宽限期，避免把区块加载顺序误判为 Boss 死亡。
    public @Nullable T tickDependent(Mob dependent, boolean inheritTarget, int resolutionGraceTicks) {
        T resolved = resolve(dependent);
        if (resolved != null) {
            unresolvedTicks = 0;
            if (!resolved.isAlive()) {
                dependent.setTarget(null);
                dependent.discard();
                return null;
            }
            if (inheritTarget) {
                synchronizeTarget(dependent, resolved);
            }
            return resolved;
        }

        if (ownerUUID != null) {
            // getEntity(UUID) 返回 null 也可能只是主人所在区块尚未恢复。宽限期内保留仍然
            // 合法的旧玩家目标，不能让仆从每次跨区块边界都停顿一百 tick。
            if (!isBasicCombatPlayer(dependent.getTarget(), dependent)) {
                dependent.setTarget(null);
            }
            if (++unresolvedTicks > Math.max(0, resolutionGraceTicks)) {
                dependent.discard();
            }
        } else {
            unresolvedTicks = 0;
        }
        return null;
    }

    private static void synchronizeTarget(Mob dependent, BaseBoss owner) {
        Player authoritativeTarget = owner.getAuthoritativeCombatTarget();
        if (authoritativeTarget != null) {
            if (dependent.getTarget() != authoritativeTarget) {
                dependent.setTarget(authoritativeTarget);
            }
            return;
        }

        if (!owner.shouldMaintainCombatTarget()
                || !(dependent.getTarget() instanceof Player player)
                || !owner.isValidCurrentCombatPlayer(player)) {
            dependent.setTarget(null);
        }
    }

    private static boolean isBasicCombatPlayer(@Nullable LivingEntity target, Mob dependent) {
        return target instanceof Player player
                && player.level() == dependent.level()
                && player.isAlive()
                && !player.isCreative()
                && !player.isSpectator();
    }

    public void unbind(Entity dependent) {
        // 卸载式解绑只释放强引用，不销毁持久化身份。
        if (owner != null) owner.removeSubEntity(dependent);
        owner = null;
    }

    public void clear(Entity dependent) {
        // 死亡/撤离式清理同时删除 UUID，从此不再尝试恢复所有权。
        unbind(dependent);
        ownerUUID = null;
        unresolvedTicks = 0;
    }

    public boolean isOwnedBy(UUID uuid) {
        return uuid.equals(ownerUUID);
    }

    public @Nullable UUID getOwnerUUID() {
        return owner == null ? ownerUUID : owner.getUUID();
    }

    public void save(CompoundTag tag) {
        UUID uuid = getOwnerUUID();
        if (uuid != null) tag.putUUID(OWNER_TAG, uuid);
    }

    public void load(CompoundTag tag) {
        owner = null;
        ownerUUID = tag.hasUUID(OWNER_TAG) ? tag.getUUID(OWNER_TAG) : null;
        unresolvedTicks = 0;
    }
}
