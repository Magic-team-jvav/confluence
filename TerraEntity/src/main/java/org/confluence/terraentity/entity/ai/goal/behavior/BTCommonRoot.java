package org.confluence.terraentity.entity.ai.goal.behavior;

import net.minecraft.world.entity.PathfinderMob;
import org.confluence.terraentity.entity.ai.goal.behavior.composite.ParallelNode;
import org.confluence.terraentity.entity.ai.goal.behavior.condition.Condition;
import org.confluence.terraentity.entity.ai.goal.behavior.condition.TargetExistCondition;
import org.confluence.terraentity.entity.ai.goal.behavior.leaf.RandomStrollAction;
import org.jetbrains.annotations.NotNull;

/**
 * 带阶段的AI
 */
public abstract class BTCommonRoot<T extends PathfinderMob> extends BTRoot<T> {


    public BTCommonRoot(T mob) {
        super(mob);
    }

    /**
     * 创建阶段触发器
     * <p>一般用于阶段切换，也可以用做默认的并行行为</p>
     */
    protected abstract BTNode createStageTrigger();

    /**
     * 创建攻击行为
     */
    protected abstract BTNode createAttackBehavior();

    protected BTNode createWonderBehavior() {
        return BTFactory.sequence()
                .addChild(new RandomStrollAction(mob, 2.0f, 70));
    }

    @Override
    protected @NotNull BTNode createBehaviorTree() {
        return BTFactory.parallel(ParallelNode.Policy.REQUIRE_ALL, ParallelNode.Policy.REQUIRE_ALL)
                // 阶段触发器
                .addChild(BTFactory.infinite(this.createStageTrigger().setDesc("阶段触发器")))
                // AI
                .addChild(BTFactory.infinite(BTFactory.selector()
                        // 游走
                        .addWithCondition(Condition.not(new TargetExistCondition(mob)), BTFactory.infinite(this.createWonderBehavior()))
                        // 攻击
                        .addWithCondition(new TargetExistCondition(mob), this.createAttackBehavior())
                        .setDesc("AI")
                ));
    }

    @Override
    public void tick() {
        super.tick();

    }
}
