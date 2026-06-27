package org.confluence.mod.common.entity.ai.bt.leaf;

import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/**
 * 等待指定 tick 数。
 */
public class WaitAction extends BTNode {
    private final int waitTicks;
    private int tick;

    public WaitAction(int waitTicks) {
        this.waitTicks = waitTicks;
    }

    @Override
    public void start() {
        tick = 0;
    }

    @Override
    public BTStatus execute() {
        tick++;
        return tick >= waitTicks ? BTStatus.SUCCESS : BTStatus.RUNNING;
    }
}
