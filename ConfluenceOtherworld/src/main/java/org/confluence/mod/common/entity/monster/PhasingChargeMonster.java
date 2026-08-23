package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/// 能够穿过地形的冲锋怪物基类，用于魔法剑与地牢机关类敌怪。
///
/// 穿墙能力只改变路径与碰撞约束，蓄力和冲锋阶段仍复用标准冲锋状态机。
public class PhasingChargeMonster extends SimpleFlyMonster {
    public PhasingChargeMonster(EntityType<? extends PhasingChargeMonster> type, Level level, double chargeSpeed, double wanderSpeed) {
        super(type, level, chargeSpeed, wanderSpeed);
        this.noPhysics = true;
    }
}
