package org.confluence.terraentity.entity.ai.goal.behavior.condition;

public class NotCondition extends AbstractConditionLeaf {
    Condition child;

    public NotCondition(Condition child) {
        this.child = child;
    }

    @Override
    public boolean check() {
        return !child.check();
    }
}
