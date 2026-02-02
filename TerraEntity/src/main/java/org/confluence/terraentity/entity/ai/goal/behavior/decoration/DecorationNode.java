package org.confluence.terraentity.entity.ai.goal.behavior.decoration;

import org.confluence.terraentity.entity.ai.goal.behavior.BTNode;

public abstract class DecorationNode extends BTNode {

    protected final BTNode child;

    @Override
    public void start() {
        super.start();
        child.start();
    }

    public DecorationNode(BTNode child) {
        this.child = child;
    }

    @Override
    protected void cleanup() {
//        if (child.getStatus() == BTStatus.RUNNING) {
            child.stop();
//        }
    }

    public BTNode getChild() {
        return child;
    }

}
