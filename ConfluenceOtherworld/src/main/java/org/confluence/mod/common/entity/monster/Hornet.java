package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.FlyWanderAction;
import org.confluence.mod.common.entity.ai.bt.leaf.ShootAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;

public class Hornet extends BaseFlyingMonster {

    public Hornet(EntityType<? extends BaseFlyingMonster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseFlyingMonster.createFlyingAttributes()
                .add(Attributes.MAX_HEALTH, 24.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(Hornet.this),
                                new ShootAction(Hornet.this, 6.0f, 0.4f), new WaitAction(25)),
                        SequenceNode.of(new HasTargetCondition(Hornet.this),
                                new FlyWanderAction(Hornet.this, 0.4, 8)),
                        new FlyWanderAction(Hornet.this, 0.2, 6)
                );
            }
        };
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && getTarget() == null && tickCount % 20 == 0) {
            Player nearest = level().getNearestPlayer(this, 32);
            if (nearest != null) setTarget(nearest);
        }
    }
}
