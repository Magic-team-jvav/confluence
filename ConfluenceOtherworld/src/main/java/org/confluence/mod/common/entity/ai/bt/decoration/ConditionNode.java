package org.confluence.mod.common.entity.ai.bt.decoration;

import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/**
 * if-then-else 节点：条件成立执行 then，否则执行 else。
 */
public class ConditionNode extends BTNode {
    private final BTNode condition;
    private final BTNode thenNode;
    private final BTNode elseNode;
    private BTNode activeChild;

    public ConditionNode(BTNode condition, BTNode thenNode, BTNode elseNode) {
        this.condition = condition;
        this.thenNode = thenNode;
        this.elseNode = elseNode;
    }

    @Override
    public void start() {
        condition.start();
        BTStatus cond = condition.execute();
        condition.stop();
        activeChild = (cond == BTStatus.SUCCESS) ? thenNode : elseNode;
        if (activeChild != null) {
            activeChild.start();
        }
    }

    @Override
    public BTStatus execute() {
        if (activeChild == null) return BTStatus.FAILURE;
        return activeChild.execute();
    }

    @Override
    public void stop() {
        if (activeChild != null) activeChild.stop();
    }
}
