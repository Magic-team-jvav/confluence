package org.confluence.mod.common.entity.monster;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.ChargeAttackAction;
import org.confluence.mod.common.entity.ai.bt.leaf.LookForwardWanderFlyAction;

/// 先原地旋转蓄势，再锁定方向穿过地形冲锋的附魔剑。
///
/// 受伤会中止当前冲锋并重新进入旋转准备阶段。接触诅咒在 AttackEffects 中按实体类型配置，
/// 不影响复用穿墙冲锋模板的地牢机关。
public final class EnchantedSword extends PhasingChargeMonster {
    private BTNode attackCycle;

    public EnchantedSword(EntityType<? extends EnchantedSword> type, Level level) {
        super(type, level, 0.8, 0.12);
    }

    @Override
    protected BTRoot createBT() {
        attackCycle = SequenceNode.of(
                new HasTargetCondition(this),
                new SpinUpAction(),
                new ChargeAttackAction(this, stateParameters(CombatState.CHARGING).behavior().chargeSpeed(), 0)
        );
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(attackCycle, new LookForwardWanderFlyAction(EnchantedSword.this, 0.12, 0.0F));
            }
        };
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean damaged = super.hurt(source, amount);
        if (damaged && !level().isClientSide && attackCycle != null) {
            attackCycle.stop();
            attackCycle.start();
            setDeltaMovement(getDeltaMovement().scale(0.25));
        }
        return damaged;
    }

    public enum CombatState {WINDUP, CHARGING}

    /// 原地旋转并逐步消除上一轮冲刺的惯性，完成后才允许锁定新的冲刺方向。
    private final class SpinUpAction extends BTNode {
        private int ticks;

        @Override
        public void start() {
            ticks = 0;
        }

        @Override
        public BTStatus execute() {
            LivingEntity target = getTarget();
            if (target == null || !target.isAlive()) return BTStatus.FAILURE;
            if (++ticks > stateParameters(CombatState.WINDUP).duration()) return BTStatus.SUCCESS;
            setDeltaMovement(getDeltaMovement().scale(0.65));
            float yaw = getYRot() + 36.0F;
            setYRot(yaw);
            setYBodyRot(yaw);
            setYHeadRot(yaw);
            hasImpulse = true;
            return BTStatus.RUNNING;
        }
    }
}
