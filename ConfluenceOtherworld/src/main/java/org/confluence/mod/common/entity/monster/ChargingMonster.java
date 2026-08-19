package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.data.entity.CreatureDefinition;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.*;

/// 巨型陆龟、独角兽、李小骨等敌怪共用的冲锋循环实现。
///
/// <p>基类统一管理蓄力、冲刺和恢复阶段，具体生物只提供数据化参数，
/// 避免每个实体复制一套容易产生时序差异的状态机。</p>
public class ChargingMonster extends BaseWarriorMonster {
    private final double chargeSpeed;
    private final int windupTicks;

    public ChargingMonster(EntityType<? extends ChargingMonster> type, Level level, double chargeSpeed, int windupTicks) {
        super(type, level);
        this.chargeSpeed = chargeSpeed;
        this.windupTicks = windupTicks;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseWarriorMonster.createAttributes().add(Attributes.MOVEMENT_SPEED, 0.28).add(Attributes.KNOCKBACK_RESISTANCE, 0.6);
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
                                new WaitAction(behavior.windupTicksOr(windupTicks)),
                                new ChargeAttackAction(ChargingMonster.this,
                                        behavior.chargeSpeedOr(chargeSpeed)),
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
