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
import org.confluence.mod.common.entity.ai.bt.leaf.MoveToTargetAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;

public class CrimsonMimic extends BaseMonster {

    public CrimsonMimic(EntityType<? extends CrimsonMimic> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseMonster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 80.0).add(Attributes.ATTACK_DAMAGE, 15.0);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(CrimsonMimic.this),
                                new MoveToTargetAction(CrimsonMimic.this, 0.6, 2.0),
                                new WaitAction(15)),
                        new WaitAction(40));
            }
        };
    }
}
