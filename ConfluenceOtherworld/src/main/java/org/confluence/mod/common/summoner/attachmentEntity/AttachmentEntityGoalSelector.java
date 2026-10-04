package org.confluence.mod.common.summoner.attachmentEntity;

import java.util.TreeSet;

public class AttachmentEntityGoalSelector {

    private final TreeSet<PrioritizedGoal> availableGoals = new TreeSet<>();
    private PrioritizedGoal currentGoal;

    public AttachmentEntityGoal<?> getCurrentGoal() {
        return currentGoal != null ? currentGoal.goal : null;
    }

    public void addGoal(int priority, AttachmentEntityGoal<?> goal) {
        availableGoals.add(new PrioritizedGoal(priority, goal));
    }

    public void removeGoal(AttachmentEntityGoal<?> goal) {
        availableGoals.removeIf(p -> p.goal == goal);
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
        for (PrioritizedGoal potential : availableGoals) {
            if (potential.goal.canUse()) {
                if (currentGoal == null) {
                    potential.goal.start();
                    currentGoal = potential;
                } else if (potential != currentGoal && potential.priority < currentGoal.priority) {
                    if (currentGoal.goal.isInterruptable()) {
                        currentGoal.goal.stop();
                        potential.goal.start();
                        currentGoal = potential;
                    }
                }
                break;
            }
        }
        if (currentGoal != null) {
            currentGoal.goal.tick();
        }
    }

    private record PrioritizedGoal(int priority, AttachmentEntityGoal<?> goal) implements Comparable<PrioritizedGoal> {

        @Override
        public int compareTo(PrioritizedGoal o) {
            int cmp = Integer.compare(this.priority, o.priority);
            return cmp != 0 ? cmp : Integer.compare(System.identityHashCode(this), System.identityHashCode(o));
        }
    }
}
