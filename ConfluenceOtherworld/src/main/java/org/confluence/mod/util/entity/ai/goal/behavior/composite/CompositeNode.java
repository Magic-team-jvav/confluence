package org.confluence.mod.util.entity.ai.goal.behavior.composite;

import org.confluence.mod.util.entity.ai.goal.behavior.BTNode;

import java.util.ArrayList;
import java.util.List;

public abstract class CompositeNode extends BTNode {
    protected List<BTNode> children = new ArrayList<>();

    public List<BTNode> getChildren() {
        return children;
    }
}
