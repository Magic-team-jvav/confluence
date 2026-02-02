package org.confluence.terraentity.entity.ai.goal.behavior.leaf;

import org.confluence.terraentity.entity.ai.goal.behavior.BTNode;
import org.confluence.terraentity.entity.ai.goal.behavior.condition.Condition;

/**
 * 条件叶节点，用于短路sequence或selector节点
 */
public class ConditionAction extends BTNode {

    final Condition condition;

    public ConditionAction(Condition condition) {
        this.condition = condition;
    }

    @Override
    public BTStatus execute() {
        if(condition.check()) {
            return BTStatus.SUCCESS;
        }
        return BTStatus.FAILURE;
    }
}
