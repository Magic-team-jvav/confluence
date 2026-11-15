package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.data.map.CreatureDefinition;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.*;

/// 需要停步蓄力、锁定方向并冲出的地面敌怪通用循环。
///
/// 基类统一管理蓄力、冲刺和恢复阶段，具体生物只提供数据化参数，
/// 避免每个实体复制一套容易产生时序差异的状态机。
public class ChargingMonster extends BaseWarriorMonster {
    private final double chargeSpeed;
    private final int windupTicks;
    private ChargeAttackAction chargeAction;

    public ChargingMonster(EntityType<? extends ChargingMonster> type, Level level, double chargeSpeed, int windupTicks) {
        super(type, level);
        if (chargeSpeed <= 0.0 || windupTicks < 0) {
            throw new IllegalArgumentException("Charge speed must be positive; windup must be non-negative");
        }
        this.chargeSpeed = chargeSpeed;
        this.windupTicks = windupTicks;
    }

    /// 冲锋阶段依赖身体命中；专用动作只负责运动，不再维护第二套碰撞计时器。
    @Override
    protected boolean hasEntityContactAttack() {
        return chargeAction != null && chargeAction.isDashing();
    }

    @Override
    protected BTRoot createBT() {
        CreatureDefinition.BehaviorOverrides behavior = creatureDefinition().behavior();
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(ChargingMonster.this),
                                new MoveToTargetAction(ChargingMonster.this,
                                        behavior.moveSpeedOr(1.0), 7.0),
                                chargeAction = new ChargeAttackAction(ChargingMonster.this,
                                        behavior.chargeSpeedOr(chargeSpeed),
                                        behavior.windupTicksOr(windupTicks)),
                                new MeleeAttackAction(ChargingMonster.this,
                                        behavior.meleeRangeOr(1.5)),
                                new WaitAction(behavior.idleTicksOr(20))),
                        SequenceNode.of(new WaitAction(behavior.idleTicksOr(20) + random.nextInt(30)),
                                new RandomStrollAction(ChargingMonster.this,
                                        behavior.wanderSpeedOr(0.6), behavior.wanderRadiusOr(8))));
            }
        };
    }
}
