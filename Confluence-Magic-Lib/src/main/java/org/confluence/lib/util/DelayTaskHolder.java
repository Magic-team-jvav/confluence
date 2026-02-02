package org.confluence.lib.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.AttachmentHolder;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import org.confluence.lib.ConfluenceMagicLib;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/// 延迟任务
///
/// 更倾向与玩家或实体的自身任务的类，以[AttachmentType]的形式存储
///
/// 目前仅有在[LivingEntity]中能正常运行
///
/// 像有前摇的攻击就时候使用该类
///
/// 像肉山生成的时候要破坏一大堆方块。之类的任务请使用[TaskScheduler]
///
/// 注：实体死亡时会移除所有任务
public class DelayTaskHolder {
    private final IAttachmentHolder attachmentHolder;
    private final Map<ResourceLocation, ITask> runList = new LinkedHashMap<>();

    public DelayTaskHolder(IAttachmentHolder attachmentHolder) {
        this.attachmentHolder = attachmentHolder;
    }

    public IAttachmentHolder getAttachmentHolder() {
        return attachmentHolder;
    }

    public Map<ResourceLocation, ITask> getRunList() {
        return runList;
    }

    public void tick() {
        if (runList.isEmpty()) {
            return;
        }
        Iterator<ITask> iterator = new HashSet<>(runList.values()).iterator();
        while (iterator.hasNext()) {
            ITask consumer = iterator.next();
            if (consumer.isRemoved()) {
                iterator.remove();
                continue;
            }
            consumer.run(this);
        }
    }

    public void addTask(ResourceLocation id, ITask task) {
        runList.put(id, task);
    }

    /// 通过该方法添加的任务会在对应槽位的物品更替时移除
    public void addTask(EquipmentSlot slot, ITask task) {
        addTask(ConfluenceMagicLib.asResource(slot.getName()), task);
    }

    /// 通过该方法添加的任务会在对应手的物品更替时移除
    public void addTask(InteractionHand handUsed, ITask task) {
        addTask(handUsed == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND, task);
    }

    /// 通过该方法添加的任务会在对应槽位的物品更替时移除
    public void addTask(EquipmentSlot slot, String name, ITask task) {
        addTask(ConfluenceMagicLib.asResource(slot.getName() + "." + name), task);
    }

    /// 通过该方法添加的任务会在对应手的物品更替时移除
    public void addTask(InteractionHand handUsed, String name, ITask task) {
        addTask(handUsed == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND, name, task);
    }

    public void removeTask(ResourceLocation id) {
        if (!containsTask(id)) {
            return;
        }
        runList.remove(id);
    }

    /// 使用此方法会移除对应槽位的任务包括相关的
    public void removeTask(EquipmentSlot slot) {
        if (containsTask(slot).isEmpty()) {
            return;
        }
        for (ResourceLocation key : new HashSet<>(runList.keySet())) {
            if (key.getPath().startsWith(slot.getName())) {
                runList.remove(key);
            }
        }
    }

    public void removeTask(EquipmentSlot slot, String name) {
        if (!containsTask(slot, name)) {
            return;
        }
        removeTask(ConfluenceMagicLib.asResource(slot.getName() + "." + name));
    }

    /// 使用此方法会移除对应手的任务包括相关的
    public void removeTask(InteractionHand handUsed) {
        removeTask(handUsed == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
    }

    public void removeTask(InteractionHand handUsed, String name) {
        removeTask(handUsed == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND, name);
    }

    public boolean containsTask(ResourceLocation id) {
        return runList.containsKey(id);
    }

    /// 如果返回的是空集合就表示该槽位没有任务
    public Set<ResourceLocation> containsTask(EquipmentSlot slot) {
        return runList.keySet().stream().filter(key -> key.getPath().startsWith(slot.getName())).collect(Collectors.toSet());
    }

    public boolean containsTask(EquipmentSlot slot, String name) {
        return runList.containsKey(ConfluenceMagicLib.asResource(slot.getName() + "." + name));
    }

    /// 如果返回的是空集合就表示该槽位没有任务
    public Set<ResourceLocation> containsTask(InteractionHand handUsed) {
        return containsTask(handUsed == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
    }

    public boolean containsTask(InteractionHand handUsed, String name) {
        return containsTask(handUsed == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND, name);
    }

    public void removeAllTask() {
        runList.clear();
    }

    public static DelayTaskHolder of(AttachmentHolder attachmentHolder) {
        return attachmentHolder.getData(ConfluenceMagicLib.DELAY_TASK_HOLDER);
    }

    public static ITask.Builder createTaskBilder() {
        return ITask.Builder.create();
    }

    public interface ITask {
        void run(DelayTaskHolder delayTaskHolder);

        boolean isRemoved();

        void remove();

        /// 运行任务类，isRemoved为true时将在下一刻移除该任务
        class BaseTask implements ITask {
            protected final ResultRun resultRun;
            protected int tick = 0;
            protected final int maxTick;
            protected int repeatCount = 0;
            protected final int maxRepeatCount;
            protected boolean isRemoved;

            private BaseTask(ResultRun resultRun, int removedTick, int maxRepeatCount) {
                this.resultRun = resultRun;
                this.maxTick = removedTick;
                this.maxRepeatCount = maxRepeatCount;
            }

            @Override
            public void run(DelayTaskHolder delayTaskHolder) {
                if (maxRepeatCount > 0 && repeatCount == maxRepeatCount) {
                    isRemoved = true;
                    return;
                }

                if (tick >= maxTick) {
                    tick = resultRun.run(tick, maxTick, this);
                    if (maxRepeatCount > 0) {
                        repeatCount++;
                    }
                }

                tick++;
            }

            @Override
            public boolean isRemoved() {
                return isRemoved;
            }

            @Override
            public void remove() {
                isRemoved = true;
            }
        }

        class TickTask extends BaseTask {
            private final TickRun tickRun;

            private TickTask(TickRun tickRun, ResultRun resultRun, int removedTick, int maxRepeatCount) {
                super(resultRun, removedTick, maxRepeatCount);
                this.tickRun = tickRun;
            }

            @Override
            public void run(DelayTaskHolder delayTaskHolder) {
                super.run(delayTaskHolder);
                tick--;
                tick = tickRun.run(tick, maxTick, this);
            }
        }

        /// 每一tick执行一次可通过修改返回值来自定义结束的时间之类的逻辑
        @FunctionalInterface
        interface TickRun {
            int run(int tick, int maxTick, ITask iTask);
        }

        @FunctionalInterface
        interface ResultRun {
            int run(int tick, int maxTick, ITask iTask);
        }

        class Builder {
            private @Nullable TickRun tickRun;
            private ResultRun resultRun;
            private int removedTick;
            private int repeatCount = 1;

            private Builder() {
            }

            public static Builder create() {
                return new Builder();
            }

            public Builder tickRun(TickRun tickRun) {
                this.tickRun = tickRun;
                return this;
            }

            public Builder resultRun(ResultRun resultRun) {
                this.resultRun = resultRun;
                return this;
            }

            public Builder removedTick(int removedTick) {
                this.removedTick = removedTick;
                return this;
            }

            public Builder repeatCount(int repeatCount) {
                this.repeatCount = repeatCount;
                return this;
            }

            public ITask build() {
                assert resultRun != null : "resultRun can not be null";
                return tickRun == null ?
                        new BaseTask(resultRun, removedTick, repeatCount) :
                        new TickTask(tickRun, resultRun, removedTick, repeatCount);
            }
        }
    }
}
