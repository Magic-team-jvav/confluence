package org.confluence.mod.util.entity.ai.goal.behavior.condition;

import java.util.List;

public class AndCondition extends CompositeCondition {


    public AndCondition() {
    }

    public AndCondition(List<Condition> children) {
        super(children);
    }

    @Override
    public AndCondition addChild(Condition child) {
        this.children.add(child);
        return this;
    }

    @Override
    public boolean check() {
        return children.stream().allMatch(Condition::check);
    }

}
