package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;

/// 按 1.21 碰撞攻击属性驱动接触伤害检测。
///
/// <p>实体尚未接触目标时使用较短的检测间隔；成功命中后使用完整攻击间隔。
/// 第一次检测同样从攻击间隔开始，避免实体刚取得目标就在同一游戏刻无前摇结算伤害。
/// 计数只在对应战斗动作实际运行时推进，因此失去目标期间不会偷偷走完冷却。
/// 本类只管理检测节奏和包围盒扩展，不参与移动、目标选择或伤害数值计算。</p>
final class ContactAttackTimer {
    private final double contactInflation;
    private final int detectionInterval;
    private final int attackInterval;
    private int remainingTicks;

    ContactAttackTimer(
            double contactInflation,
            int detectionInterval,
            int attackInterval) {
        if (contactInflation < 0.0) {
            throw new IllegalArgumentException(
                    "Contact inflation must not be negative");
        }
        if (detectionInterval <= 0) {
            throw new IllegalArgumentException(
                    "Detection interval must be positive");
        }
        if (attackInterval <= 0) {
            throw new IllegalArgumentException(
                    "Attack interval must be positive");
        }
        this.contactInflation = contactInflation;
        this.detectionInterval = detectionInterval;
        this.attackInterval = attackInterval;
        this.remainingTicks = attackInterval;
    }

    void tick(PathfinderMob mob, LivingEntity target) {
        if (--remainingTicks > 0) {
            return;
        }

        if (mob.getBoundingBox().inflate(contactInflation)
                .intersects(target.getBoundingBox())) {
            mob.doHurtTarget(target);
            remainingTicks = attackInterval;
        } else {
            remainingTicks = detectionInterval;
        }
    }
}
