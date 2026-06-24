package org.confluence.mod.common.entity.ai.goal.behavior.condition;

import java.util.ArrayList;
import java.util.List;

public abstract class CompositeCondition extends AbstractConditionLeaf {
    List<Condition> children;

    public CompositeCondition() {
        this.children = new ArrayList<>();
    }

    public CompositeCondition(List<Condition> children) {
        this.children = children;
    }

    public CompositeCondition addChild(Condition child) {
        this.children.add(child);
        return this;
    }

}
