package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.ChargeAttackAction;
import org.confluence.mod.common.entity.ai.bt.leaf.CircleAroundTargetAction;
import org.confluence.mod.common.entity.ai.bt.leaf.FlyWanderAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;

public class Wraith extends BaseFlyingMonster {

    public Wraith(EntityType<? extends Wraith> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseFlyingMonster.createFlyingAttributes()
                .add(Attributes.MAX_HEALTH, 50.0).add(Attributes.ATTACK_DAMAGE, 12.0);
    }

    @Override
    protected BTRoot createBT() {
        BTNode combat = SelectorNode.of(
                SequenceNode.of(new CircleAroundTargetAction(this, 0.3, 4), new WaitAction(20)),
                SequenceNode.of(new ChargeAttackAction(this, 0.5)));
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(Wraith.this), combat),
                        new FlyWanderAction(Wraith.this, 0.15, 10));
            }
        };
    }
}
