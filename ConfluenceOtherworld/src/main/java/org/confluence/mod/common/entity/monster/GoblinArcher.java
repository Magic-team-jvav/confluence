package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.*;

/// 哥布林弓箭手。
///
/// <p>持弓时使用与 1.21 一致的拉弓、瞄准、难度冷却和射后走位；武器被替换后自动
/// 回退到人形怪物的近战追击。两条分支共用现有行为树，不需要在装备变化时重新注册
/// 原版目标。</p>
public class GoblinArcher extends GoblinMonster {
    public GoblinArcher(EntityType<? extends GoblinArcher> type, Level level) {
        super(type, level, Items.BOW.getDefaultInstance(),
                LandAnimationProfile.NONE);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        new BowCombatAction(
                                GoblinArcher.this,
                                1.0,
                                40,
                                20,
                                15.0,
                                20,
                                1.6F),
                        SequenceNode.of(
                                new HasTargetCondition(GoblinArcher.this),
                                new MoveToTargetAction(
                                        GoblinArcher.this, 1.2, 2.0),
                                new MeleeAttackAction(
                                        GoblinArcher.this, 2.0)),
                        SequenceNode.of(new WaitAction(20 + random.nextInt(40)),
                                new RandomStrollAction(GoblinArcher.this, 0.8, 10)));
            }
        };
    }
}
