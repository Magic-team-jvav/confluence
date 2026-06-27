package org.confluence.mod.util.entity.ai.goal.behavior.leaf;

import org.confluence.mod.util.entity.ai.goal.behavior.BTNode;

/// 等待节点，到达指定时间返回[BTNode.BTStatus#SUCCESS]
public class WaitAction extends BTNode {
    protected int waitTicks;
    protected int currentTicks = 0;

    public WaitAction(int waitTicks) {
        this.waitTicks = waitTicks;
    }

    @Override
    public BTStatus execute() {
        if (++currentTicks >= waitTicks) {
            return BTStatus.SUCCESS;
        }
        return BTStatus.RUNNING;
    }

    @Override
    protected void cleanup() {
        currentTicks = 0;
    }

    public int getWaitTicks() {
        return waitTicks;
    }

    public int getCurrentTicks() {
        return currentTicks;
    }
}
