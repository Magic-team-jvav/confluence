package org.confluence.mod.util.entity.ai.goal.behavior.decoration;

import org.confluence.mod.util.entity.ai.goal.behavior.BTNode;

/**
 *
 */
public class InterruptNode extends DecorationNode {

    protected InterruptNode(BTNode child) {
        super(child);
    }

    @Override
    public BTStatus execute() {
        return null;
    }
}
