package org.confluence.mod.common.summon;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/// 为一个召唤物选择当前行为。
public final class SummonGoalSelector {
    private final List<PrioritizedGoal> availableGoals = new ArrayList<>();
    private PrioritizedGoal currentGoal;
    private long nextOrder;

    public void addGoal(int priority, SummonGoal<?> goal) {
        availableGoals.add(new PrioritizedGoal(priority, nextOrder++, goal));
        availableGoals.sort(Comparator.comparingInt(PrioritizedGoal::priority).thenComparingLong(PrioritizedGoal::order));
    }

    public void removeGoal(SummonGoal<?> goal) {
        availableGoals.removeIf(entry -> entry.goal == goal);
        if (currentGoal != null && currentGoal.goal == goal) {
            currentGoal.goal.stop();
            currentGoal = null;
        }
    }

    public void tick() {
        if (currentGoal != null && !currentGoal.goal.canContinueToUse()) {
            currentGoal.goal.stop();
            currentGoal = null;
        }
        for (PrioritizedGoal candidate : availableGoals) {
            if (!candidate.goal.canUse()) {
                continue;
            }
            if (currentGoal == null) {
                start(candidate);
            } else if (candidate != currentGoal && candidate.priority < currentGoal.priority && currentGoal.goal.isInterruptible()) {
                currentGoal.goal.stop();
                start(candidate);
            }
            break;
        }
        if (currentGoal != null) {
            currentGoal.goal.tick();
        }
    }

    private void start(PrioritizedGoal goal) {
        currentGoal = goal;
        goal.goal.start();
    }

    private record PrioritizedGoal(int priority, long order, SummonGoal<?> goal) {}
}
