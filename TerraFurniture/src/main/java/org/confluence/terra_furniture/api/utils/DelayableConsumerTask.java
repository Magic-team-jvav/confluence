package org.confluence.terra_furniture.api.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 延迟刻调用，不自行控制生命周期，可反复使用。
 */
public class DelayableConsumerTask<T> {
    private final Consumer<T> singleRunTask;
    private int delayedTicks;

    private int count = 0;
    private final List<Runnable> tasksToDeal = new ArrayList<>();

    public DelayableConsumerTask(Consumer<T> singleRunTask, int delayedTicks) {
        this.singleRunTask = singleRunTask;
        this.delayedTicks = delayedTicks;
    }

    public DelayableConsumerTask<T> with(Runnable runnable) {
        tasksToDeal.add(runnable);
        return this;
    }

    /**
     * 尝试触发调用并自增时间。
     * @return false: 未到达时间; true: 到达核定时间(任务运行完成)
     * @apiNote 可以用task.with(task::reset)的方法避免返回true。
     */
    public boolean tryTick(T element) {
        if (count++ < delayedTicks) return false;
        singleRunTask.accept(element);
        tasksToDeal.forEach(Runnable::run);
        return count >= delayedTicks;
    }

    public DelayableConsumerTask<T> reset() {
        count = 0;
        return this;
    }

    public void setDelayedTicks(int delayedTicks) {
        this.delayedTicks = delayedTicks;
    }
}
