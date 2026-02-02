package org.confluence.terraentity.entity.ai.goal.behavior.condition;

import java.util.List;

public class OrCondition extends CompositeCondition {

    public OrCondition() {
    }

    public OrCondition(List<Condition> children) {
        super(children);
    }

    @Override
    public OrCondition addChild(Condition child) {
        this.children.add(child);
        return this;
    }

    @Override
    public boolean check() {
        return children.stream().anyMatch(Condition::check);
    }

}
