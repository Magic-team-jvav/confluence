package org.confluence.mod.util.entity.ai.goal.behavior.decoration;

import org.confluence.mod.util.entity.ai.goal.behavior.BTNode;

public class TimeControlNode extends DecorationNode {

    final int duration;
    int tick;

    public TimeControlNode(int duration, BTNode child) {
        super(child);
        this.duration = duration;
    }

    @Override
    public BTStatus execute() {
        if (tick >= duration) {
            return BTStatus.SUCCESS;
        }
        tick++;
        child.execute();
        return BTStatus.RUNNING;
    }

    @Override
    protected void cleanup() {
        super.cleanup();
        this.tick = 0;
    }
}
