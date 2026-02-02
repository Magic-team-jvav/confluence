package org.confluence.terraentity.entity.ai.goal.behavior.decoration;

import org.confluence.terraentity.entity.ai.goal.behavior.BTNode;
import org.confluence.terraentity.entity.ai.goal.behavior.condition.Condition;
import org.jetbrains.annotations.Nullable;

/**
 * 条件节点（检查条件，不满足则失败）
 */
public class ConditionNode extends DecorationNode {

    private final Condition condition;

    public ConditionNode(Condition condition, BTNode child) {
        super(child);
        this.condition = condition;
    }

    @Override
    public BTStatus execute() {
        if (!condition.check()) {
            return BTStatus.FAILURE;
        }

        child.tryStart();

        if (child.getStatus() == BTStatus.RUNNING) {
            child.tick();
        }

        return child.getStatus();
    }

    @Override
    public @Nullable String getDesc() {
        if(super.getDesc() != null){
            return super.getDesc();
        }
        return condition.getDesc();
    }
}
