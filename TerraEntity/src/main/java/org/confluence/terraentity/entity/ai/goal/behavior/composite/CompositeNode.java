package org.confluence.terraentity.entity.ai.goal.behavior.composite;

import org.confluence.terraentity.entity.ai.goal.behavior.BTNode;

import java.util.ArrayList;
import java.util.List;

public abstract class CompositeNode extends BTNode {

    protected List<BTNode> children = new ArrayList<>();

    @Override
    public BTStatus execute() {
        return null;
    }

    public List<BTNode> getChildren() {
        return children;
    }
}
