package org.confluence.lib.util;

import org.confluence.lib.ConfluenceMagicLib;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * 主线程任务调度器
 * <p>
 * 该调度器设计用于在主线程中运行，通过tick方法推进时间并执行任务
 * <p>
 * 该类适合如：肉山生成的时候要破坏一大堆方块。之类的任务，如果是生物自己的任务请使用{@link DelayTaskHolder}类更合适
 */
public class TaskScheduler {
    // 任务队列（按执行时间排序）
    private final PriorityQueue<ScheduledTask> taskQueue =
            new PriorityQueue<>(Comparator.comparingLong(a -> a.executeTime));

    // 任务计数器（用于打破平局）
    private final AtomicLong sequenceNumber = new AtomicLong(0);

    // 当前时间（毫秒）
    private long currentTime;

    // 任务执行监听器
    private Consumer<ScheduledTask> taskListener;

    // 任务统计
    private int tasksExecuted;
    private int tasksScheduled;
    private boolean consumePeriodTask = false; // 当在周期任务置true时，可以在周期任务强制取消该任务。

    public TaskScheduler(long initialTime) {
        this.currentTime = initialTime;
    }

    /**
     * 推进时间并执行任务
     *
     * @param elapsed 经过的时间（毫秒）
     */
    public void tick(long elapsed) {
        currentTime += elapsed;

        // 执行所有到期的任务
        while (!taskQueue.isEmpty()) {
            ScheduledTask task = taskQueue.peek();

            // 如果队首任务还没到执行时间，停止处理
            if (task.executeTime > currentTime) {
                break;
            }

            // 移除并执行任务
            task = taskQueue.poll();
            executeTask(task);
        }
    }

    /**
     * 执行单个任务
     */
    private void executeTask(ScheduledTask task) {
        tasksExecuted++;

        try {
            // 执行任务逻辑
            task.runnable.run();

            // 如果是周期性任务，重新调度
            if (task.period > 0) {
                if (!consumePeriodTask) {
                    task.executeTime = currentTime + task.period;
                    taskQueue.offer(task);
                } else {
                    consumePeriodTask = false;
                }
            }

            // 触发监听器
            if (taskListener != null) {
                taskListener.accept(task);
            }
        } catch (Exception e) {
            ConfluenceMagicLib.LOGGER.error("Task execution failed", e);
        }
    }

    /**
     * 强制取消周期性任务
     */
    public void consumePeriodTask() {
        this.consumePeriodTask = true;
    }

    /**
     * 调度一次性任务
     *
     * @param runnable 要执行的任务
     * @param delay    延迟时间（毫秒）
     * @return 任务ID
     */
    public long schedule(Runnable runnable, long delay) {
        return schedule(runnable, delay, 0);
    }

    /**
     * 调度周期性任务
     *
     * @param runnable     要执行的任务
     * @param initialDelay 初始延迟（毫秒）
     * @param period       执行周期（毫秒）
     * @return 任务ID
     */
    public long scheduleAtFixedRate(Runnable runnable, long initialDelay, long period) {
        return schedule(runnable, initialDelay, period);
    }

    private long schedule(Runnable runnable, long delay, long period) {
        long executeTime = currentTime + delay;
        long id = sequenceNumber.getAndIncrement();

        ScheduledTask task = new ScheduledTask(id, runnable, executeTime, period);
        taskQueue.offer(task);
        tasksScheduled++;

        return id;
    }

    /**
     * 取消任务
     *
     * @param taskId 任务ID
     * @return 是否成功取消
     */
    public boolean cancel(long taskId) {
        return taskQueue.removeIf(task -> task.id == taskId);
    }

    /**
     * 设置任务执行监听器
     */
    public void setTaskListener(Consumer<ScheduledTask> listener) {
        this.taskListener = listener;
    }

    /**
     * 获取当前时间
     */
    public long getCurrentTime() {
        return currentTime;
    }

    /**
     * 获取待处理任务数量
     */
    public int getPendingTaskCount() {
        return taskQueue.size();
    }

    /**
     * 获取已执行任务数量
     */
    public int getExecutedTaskCount() {
        return tasksExecuted;
    }

    /**
     * 获取已调度任务总数
     */
    public int getScheduledTaskCount() {
        return tasksScheduled;
    }

    /**
     * 清空所有任务
     */
    public void clear() {
        taskQueue.clear();
    }

    /**
     * 任务封装类
     */
    public static class ScheduledTask {
        public final long id;
        public final Runnable runnable;
        public long executeTime;
        public final long period;

        public ScheduledTask(long id, Runnable runnable, long executeTime, long period) {
            this.id = id;
            this.runnable = runnable;
            this.executeTime = executeTime;
            this.period = period;
        }

        public boolean isPeriodic() {
            return period > 0;
        }

        @Override
        public String toString() {
            return String.format("Task[%d] @ %d (%s)",
                    id, executeTime, isPeriodic() ? "periodic" : "one-time");
        }
    }
}
