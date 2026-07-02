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
import org.confluence.mod.common.entity.ai.bt.leaf.ChargeAttackAction;
import org.confluence.mod.common.entity.ai.bt.leaf.FlyWanderAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;

public class GraniteElemental extends BaseFlyingMonster {

    public GraniteElemental(EntityType<? extends BaseFlyingMonster> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseFlyingMonster.createFlyingAttributes()
                .add(Attributes.MAX_HEALTH, 50.0)
                .add(Attributes.ATTACK_DAMAGE, 14.0)
                .add(Attributes.ARMOR, 6.0);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(GraniteElemental.this),
                                new ChargeAttackAction(GraniteElemental.this, 0.4)),
                        SequenceNode.of(new WaitAction(10),
                                new FlyWanderAction(GraniteElemental.this, 0.2, 12))
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
