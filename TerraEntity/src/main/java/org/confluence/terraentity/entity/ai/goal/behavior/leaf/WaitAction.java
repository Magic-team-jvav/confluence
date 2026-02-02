package org.confluence.terraentity.entity.ai.goal.behavior.leaf;

import org.confluence.terraentity.entity.ai.goal.behavior.BTNode;

/**
 * 等待节点，到达指定时间返回{@link BTStatus#SUCCESS SUCCESS}
 */
public class WaitAction extends BTNode {
    private final int waitTicks;
    private int currentTicks = 0;

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
