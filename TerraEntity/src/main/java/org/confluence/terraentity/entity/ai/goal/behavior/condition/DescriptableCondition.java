package org.confluence.terraentity.entity.ai.goal.behavior.condition;

import org.jetbrains.annotations.Nullable;

public abstract class DescriptableCondition implements Condition {
    private String description;

    public Condition setConDesc(String description) {
        this.description = description;
        return this;
    }

    @Override
    public boolean check() {
        return false;
    }

    @Override
    public @Nullable String getDesc() {
        return description;
    }
}
