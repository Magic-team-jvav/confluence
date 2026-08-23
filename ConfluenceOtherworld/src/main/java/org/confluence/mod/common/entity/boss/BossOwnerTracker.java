package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
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

    public BossOwnerTracker(Class<T> ownerType) {
        this.ownerType = ownerType;
    }

    public void bind(Entity dependent, T owner) {
        this.owner = owner;
        this.ownerUUID = owner.getUUID();
        owner.addSubEntity(dependent);
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

    public void unbind(Entity dependent) {
        // 卸载式解绑只释放强引用，不销毁持久化身份。
        if (owner != null) owner.removeSubEntity(dependent);
        owner = null;
    }

    public void clear(Entity dependent) {
        // 死亡/撤离式清理同时删除 UUID，从此不再尝试恢复所有权。
        unbind(dependent);
        ownerUUID = null;
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
    }
}
