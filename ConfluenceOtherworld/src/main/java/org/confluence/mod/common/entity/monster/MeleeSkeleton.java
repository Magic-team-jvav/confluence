package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.MoveToTargetAction;
import org.confluence.mod.common.entity.ai.bt.leaf.RandomStrollAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.entity.monster.humanoid.BaseHumanoidMonster;

public class MeleeSkeleton extends BaseHumanoidMonster {

    public MeleeSkeleton(EntityType<? extends MeleeSkeleton> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseHumanoidMonster.createHumanoidAttributes();
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(MeleeSkeleton.this),
                                new MoveToTargetAction(MeleeSkeleton.this, 1.0, 2.0)),
                        SequenceNode.of(new WaitAction(20 + random.nextInt(40)),
                                new RandomStrollAction(MeleeSkeleton.this, 0.8, 10)));
            }
        };
    }
}
