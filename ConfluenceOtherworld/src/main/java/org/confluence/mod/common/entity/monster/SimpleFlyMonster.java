package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.ChargeAttackAction;
import org.confluence.mod.common.entity.ai.bt.leaf.FlyWanderAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;

/**
 * 简单飞行 AI 怪物——有目标时冲撞，否则飞行漫游。
 */
public class SimpleFlyMonster extends BaseFlyingMonster {
    private final double chargeSpeed;
    private final double wanderSpeed;

    public SimpleFlyMonster(EntityType<? extends SimpleFlyMonster> type, Level level, double chargeSpeed, double wanderSpeed) {
        super(type, level);
        this.chargeSpeed = chargeSpeed;
        this.wanderSpeed = wanderSpeed;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseFlyingMonster.createFlyingAttributes();
    }

    @Override
    protected BTRoot createBT() {
        SimpleFlyMonster self = this;
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(self),
                                new ChargeAttackAction(self, chargeSpeed)),
                        SequenceNode.of(new WaitAction(10),
                                new FlyWanderAction(self, wanderSpeed, 8)));
            }
        };
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && getTarget() == null && tickCount % 20 == 0) {
            Player nearest = level().getNearestPlayer(this, 24);
            if (nearest != null) setTarget(nearest);
        }
    }
}
