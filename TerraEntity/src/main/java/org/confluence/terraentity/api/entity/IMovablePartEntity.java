package org.confluence.terraentity.api.entity;

import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;

/**
 * 可移动的 PartEntity 接口
 * 实现此接口的 PartEntity 可以被实体直接移动到其位置，而不是父实体的位置
 */
public interface IMovablePartEntity {
    /**
     * 获取移动目标位置
     * 实体应该移动到这个位置，而不是父实体的位置
     * @return 移动目标位置
     */
    default Vec3 getMoveTargetPosition() {
        if (this instanceof PartEntity<?> partEntity) {
            return partEntity.position();
        }
        return Vec3.ZERO;
    }

    /**
     * 检查是否应该移动到 PartEntity 而不是父实体
     * @return 如果应该移动到 PartEntity 则返回 true
     */
    default boolean shouldMoveToPart() {
        return true;
    }
}

