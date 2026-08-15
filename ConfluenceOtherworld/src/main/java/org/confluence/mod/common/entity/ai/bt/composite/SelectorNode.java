package org.confluence.mod.common.entity.ai.bt.composite;

import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * 按列表顺序执行的优先级选择器。
 *
 * <p>索引越小，行为优先级越高。当前分支处于运行状态时，每 tick 仍会从首个分支
 * 重新检查到当前分支之前；一旦更高优先级行为可以运行或已经完成，便先停止原分支，
 * 再切换到新分支。这与 1.21 侧原版 {@code GoalSelector} 的抢占关系一致，避免游荡、
 * 观察等低优先级长动作阻止后来出现的战斗、漂浮或逃生条件。</p>
 *
 * <p>没有更高优先级分支可用时，当前分支不会重新启动，其内部计时和连续动作可以
 * 正常保留。需要按顺序轮换技能的场景应使用 {@link RoundRobinSelectorNode}。</p>
 */
public class SelectorNode extends BTNode {
    protected final List<BTNode> children;
    protected int currentIndex;

    public SelectorNode(List<BTNode> children) {
        this.children = children;
        this.currentIndex = 0;
    }

    @Override
    public void start() {
        currentIndex = 0;
        if (!children.isEmpty()) {
            children.get(0).start();
        }
    }

    @Override
    public BTStatus execute() {
        BTStatus preemptingStatus = tryHigherPriorityChildren();
        if (preemptingStatus != null) {
            return preemptingStatus;
        }

        while (currentIndex < children.size()) {
            BTNode child = children.get(currentIndex);
            BTStatus status = child.execute();
            if (status == BTStatus.RUNNING) {
                return BTStatus.RUNNING;
            }
            child.stop();
            if (status == BTStatus.SUCCESS) {
                return BTStatus.SUCCESS;
            }
            currentIndex++;
            if (currentIndex < children.size()) {
                children.get(currentIndex).start();
            }
        }
        return BTStatus.FAILURE;
    }

    /**
     * 只探测当前运行分支之前的节点，保持当前分支自身的运行状态不被重置。
     */
    private BTStatus tryHigherPriorityChildren() {
        for (int index = 0; index < currentIndex; index++) {
            BTNode candidate = children.get(index);
            candidate.start();
            BTStatus status = candidate.execute();
            if (status == BTStatus.FAILURE) {
                candidate.stop();
                continue;
            }

            if (currentIndex < children.size()) {
                children.get(currentIndex).stop();
            }
            currentIndex = index;
            if (status == BTStatus.SUCCESS) {
                candidate.stop();
            }
            return status;
        }
        return null;
    }

    @Override
    public void stop() {
        if (currentIndex < children.size()) {
            children.get(currentIndex).stop();
        }
    }

    public static SelectorNode of(BTNode... nodes) {
        return new SelectorNode(new ArrayList<>(List.of(nodes)));
    }
}
