package org.confluence.mod.common.entity.ai.bt.composite;

import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择器：依次执行子节点，任一成功则成功，全部失败则失败。
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

    /// 只探测当前运行分支之前的节点，保持当前分支自身的运行状态不被重置。
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
