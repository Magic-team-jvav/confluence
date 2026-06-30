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
import org.confluence.mod.common.entity.ai.bt.leaf.RandomStrollAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;

public class Piranha extends BaseMonster {

    public Piranha(EntityType<? extends Piranha> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseMonster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0).add(Attributes.ATTACK_DAMAGE, 5.0);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(Piranha.this),
                                new MoveToTargetAction(Piranha.this, 0.7, 2.0),
                                new WaitAction(10)),
                        SequenceNode.of(new WaitAction(20 + random.nextInt(40)),
                                new RandomStrollAction(Piranha.this, 0.4, 6)));
            }
        };
    }
}
