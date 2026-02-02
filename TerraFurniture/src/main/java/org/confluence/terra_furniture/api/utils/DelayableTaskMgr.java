package org.confluence.terra_furniture.api.utils;

import io.netty.util.internal.UnstableApi;

import java.util.HashSet;
import java.util.Set;

@UnstableApi
public class DelayableTaskMgr<T> {
    //private static final Random random = new Random();

    private final Set<DelayableConsumerTask<T>> delayedTasks = new HashSet<>();

    public void tick(T element) {
        delayedTasks.removeIf(task -> task.tryTick(element));
    }

    public void createTickDelayedTask(DelayableConsumerTask<T> task) {
        delayedTasks.add(task);
    }

    public void createLoopTask(DelayableConsumerTask<T> task) {
        delayedTasks.add(task.with(task::reset)); // Reset before return can avoid removal.
    }

    /*public void createRandomLoopTask(DelayableConsumerTask<T> task, int minDelay, int maxDelay) {
        createLoopTask(task.with(() -> task.setDelayedTicks(random.nextInt(minDelay, maxDelay))));
    }*/

    public void clear() {
        if (delayedTasks.isEmpty()) return;
        delayedTasks.clear();
    }
}
