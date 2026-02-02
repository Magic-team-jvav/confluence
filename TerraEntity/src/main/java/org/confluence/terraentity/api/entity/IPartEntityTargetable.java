package org.confluence.terraentity.api.entity;

import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.entity.PartEntity;
import org.jetbrains.annotations.Nullable;

/**
 * 支持以 PartEntity 为目标的实体接口
 */
public interface IPartEntityTargetable {
    /**
     * 获取实际目标实体（用于PartEntity）
     * @return 实际目标实体，如果不存在则返回 null
     */
    @Nullable
    Entity getActualTargetEntity();

    /**
     * 设置实际目标实体（用于PartEntity）
     * @param entity 要设置的目标实体，可以为 null 以清除目标
     */
    void setActualTargetEntity(@Nullable Entity entity);

    /**
     * 检查是否可以攻击指定的实体（用于PartEntity）
     * @param target 目标实体
     * @return 如果可以攻击则返回 true
     */
    boolean canAttackTarget(Entity target);

    /**
     * 检查实际目标是否是有效的 PartEntity
     * @return 如果实际目标是有效的 PartEntity 则返回 true
     */
    default boolean hasValidPartEntityTarget() {
        Entity target = getActualTargetEntity();
        if (!(target instanceof PartEntity<?> partEntity)) {
            return false;
        }
        return partEntity.isAlive();
    }

    /**
     * 清理无效的实际目标
     */
    default void cleanupInvalidActualTarget() {
        Entity target = getActualTargetEntity();
        if (target != null && !target.isAlive()) {
            setActualTargetEntity(null);
        }
    }
}

