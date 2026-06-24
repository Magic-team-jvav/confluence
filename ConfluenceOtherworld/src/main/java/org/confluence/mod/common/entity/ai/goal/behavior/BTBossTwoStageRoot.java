package org.confluence.mod.common.entity.ai.goal.behavior;

import net.minecraft.world.entity.PathfinderMob;
import org.confluence.mod.common.api.entity.IStateChangeableMob;
import org.confluence.mod.common.entity.ai.goal.behavior.blackboard.Blackboard;
import org.confluence.mod.common.entity.ai.goal.behavior.blackboard.IBlackboardHolder;
import org.confluence.mod.common.entity.ai.goal.behavior.blackboard.KeyType;
import org.confluence.mod.common.entity.ai.goal.behavior.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.goal.behavior.condition.Condition;
import org.confluence.mod.common.entity.ai.goal.behavior.leaf.SyncAction;

/**
 * 两个阶段的怪物AI
 */
public abstract class BTBossTwoStageRoot<T extends PathfinderMob & IBlackboardHolder & IStateChangeableMob> extends BTCommonRoot<T> {

    public BTBossTwoStageRoot(T mob) {
        super(mob);
    }

    /**
     * 转换二阶段条件
     */
    protected abstract Condition createStageCondition();

    /**
     * 转换阶段前，可以添加延迟和共享状态位
     */
    protected abstract SequenceNode switchPre(SequenceNode sequence);

    /**
     * 转换阶段后，可以添加延迟和共享状态位
     */
    protected abstract SequenceNode switchPost(SequenceNode sequence);

    /**
     * 一阶段AI
     */
    protected abstract BTNode createStageOneAttack();

    /**
     * 二阶段AI
     */
    protected abstract BTNode createStageTwoAttack();

    @Override
    protected BTNode createStageTrigger() {
        return BTFactory.selector()
                // 二阶段
                .addWithCondition(Blackboard.containsValue(this.mob, KeyType.STAGE, v -> v == 3).setConDesc("STAGE == 3"), BTFactory.wait(10000).setDesc("二阶段"))
                // 转换阶段
                .addWithCondition(Blackboard.containsValue(this.mob, KeyType.STAGE, v -> v == 2).setConDesc("STAGE == 2"),
                        switchPost(
                                switchPre(BTFactory.sequence())
                                        .addChild(new SyncAction<>(this.mob, this.mob.get_DATA_STATUS_STATUS(), () -> 3).setDesc("sync status = 3"))
                        ).addChild(Blackboard.setValue(this.mob, KeyType.STAGE, () -> 3).setDesc("STAGE = 3"))
                                .setDesc("转换阶段")
                )
                // 一阶段
                .addWithCondition(Condition.and(this.createStageCondition(), Blackboard.containsValue(this.mob, KeyType.STAGE, v -> v == 1).setConDesc("STAGE == 1")), BTFactory.sequence()
                        .addChild(Blackboard.setValue(this.mob, KeyType.STAGE, () -> 2).setDesc("STAGE = 2"))
                        .addChild(new SyncAction<>(this.mob, this.mob.get_DATA_STATUS_STATUS(), () -> 2).setDesc("sync status = 2"))
                        .setDesc("一阶段")
                );
    }

    @Override
    protected BTNode createAttackBehavior() {
        return BTFactory.selector()
                // 一阶段
                .addWithCondition(Blackboard.containsValue(this.mob, KeyType.STAGE, v -> v == 1), "STAGE == 1" , BTFactory.infinite(this.createStageOneAttack().setDesc("一阶段AI")))
                // 二阶段
                .addWithCondition(Blackboard.containsValue(this.mob, KeyType.STAGE, v -> v == 3), "STAGE == 3" , BTFactory.infinite(this.createStageTwoAttack().setDesc("二阶段AI")))
                .setDesc("阶段选择器")

                ;
    }

}
