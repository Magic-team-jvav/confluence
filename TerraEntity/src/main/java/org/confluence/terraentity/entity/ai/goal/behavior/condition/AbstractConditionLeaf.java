package org.confluence.terraentity.entity.ai.goal.behavior.condition;

import org.confluence.terraentity.entity.ai.goal.behavior.BTNode;

/**
 * 与sequence和selector配合实现短路，在使用web ui生成代码时，使用这种方式
 */
public abstract class AbstractConditionLeaf extends BTNode implements Condition {

    @Override
    public BTStatus execute() {
        if(check()) {
            return BTStatus.SUCCESS;
        }
        return BTStatus.FAILURE;
    }

}
