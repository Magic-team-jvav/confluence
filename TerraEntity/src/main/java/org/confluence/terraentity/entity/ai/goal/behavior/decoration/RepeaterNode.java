package org.confluence.terraentity.entity.ai.goal.behavior.decoration;

import org.confluence.terraentity.entity.ai.goal.behavior.BTNode;

/**
 * 重复节点（重复执行指定次数）
 */
public class RepeaterNode extends DecorationNode {

    private final int repeatCount;
    private final boolean infinite;

    private int currentCount = 0;

    /**
     * @param repeatCount 当小于0时infinite为true
     */
    public RepeaterNode(int repeatCount, BTNode child) {
        super(child);


        this.infinite = repeatCount < 0;
        this.repeatCount = repeatCount < 0? 1 : repeatCount;
    }

    @Override
    public BTStatus execute() {
        while (currentCount < repeatCount) {

            child.tryStart();

            if (child.getStatus() == BTStatus.RUNNING) {
                child.tick();
            }


            if (child.getStatus() == BTStatus.RUNNING) {
                return BTStatus.RUNNING;
            }

            child.stop();

            if (child.getStatus() == BTStatus.FAILURE) {
                return BTStatus.FAILURE;
            }

            currentCount++;
        }
        if(infinite) {
            currentCount = 0;
            cleanup();
            return BTStatus.RUNNING;
        }
        return BTStatus.SUCCESS;
    }

    @Override
    protected void cleanup() {
        super.cleanup();
        currentCount = 0;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public boolean isInfinite() {
        return infinite;
    }

    public int getCurrentCount() {
        return currentCount;
    }
}
