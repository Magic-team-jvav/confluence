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
import org.confluence.mod.common.entity.ai.bt.leaf.RandomStrollAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;

/**
 * 法师怪物基类——远程攻击 + 受伤传送。TODO：弹射物射击 + 传送 Action。
 */
public abstract class BaseCasterMonster extends BaseMonster {

    public BaseCasterMonster(EntityType<? extends BaseCasterMonster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createCasterAttributes() {
        return BaseMonster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.ARMOR, 2.0)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(BaseCasterMonster.this),
                                new WaitAction(40)),
                        new RandomStrollAction(BaseCasterMonster.this, 0.8, 8));
            }
        };
    }
}
