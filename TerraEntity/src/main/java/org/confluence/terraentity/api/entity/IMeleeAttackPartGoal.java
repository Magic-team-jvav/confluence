package org.confluence.terraentity.api.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;

/**
 * 召唤物近战攻击 Goal 接口
 * 实现此接口的 Goal 可以自定义近战攻击行为
 */
public interface IMeleeAttackPartGoal {
    /**
     * 获取实际目标实体
     * 优先返回 PartEntity 目标（如果存在），否则返回 getTarget()
     * @param mob 召唤物实体
     * @return 实际目标实体（可能是 PartEntity 或 LivingEntity），如果没有目标则返回 null
     */
    default Entity getActualTarget(Mob mob) {
        // 优先使用 PartEntity 目标
        if (mob instanceof IPartEntityTargetable targetable) {
            Entity actualTarget = targetable.getActualTargetEntity();
            if (actualTarget != null) {
                return actualTarget;
            }
        }
        // 否则使用 getTarget()
        return mob.getTarget();
    }

    /**
     * 检查是否可以攻击目标实体
     * @param target 目标实体（可以是 LivingEntity 或 PartEntity）
     * @return 如果可以攻击则返回 true
     */
     boolean canMeleeAttackTarget(Entity target);

    /**
     * 获取目标的位置
     * 用于移动和路径计算
     * @param mob 召唤物实体
     * @param target 目标实体（可以是 LivingEntity 或 PartEntity）
     * @return 目标的位置，如果目标无效则返回 null
     */
    default Vec3 getTargetPosition(Mob mob, Entity target) {
        if (target == null || !target.isAlive()) {
            return null;
        }

        // 如果目标是 PartEntity 且实现了 IMovablePartEntity，使用其移动目标位置
        if (target instanceof PartEntity<?> partEntity && partEntity instanceof IMovablePartEntity movablePart) {
            if (movablePart.shouldMoveToPart()) {
                return movablePart.getMoveTargetPosition();
            }
        }

        return target.position();
    }
}

