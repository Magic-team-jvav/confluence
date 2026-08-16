package org.confluence.mod.common.summon;

/// 召唤物的一项可独立切换的行为。
public abstract class SummonGoal<T extends SummonInstance> {
    protected final T summon;

    protected SummonGoal(T summon) {
        this.summon = summon;
    }

    public abstract boolean canUse();

    public boolean canContinueToUse() {
        return canUse();
    }

    public boolean isInterruptible() {
        return true;
    }

    public void start() {}

    public void tick() {}

    public void stop() {}
}
