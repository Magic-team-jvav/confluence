package org.confluence.mod.common.entity.ai.bt.composite;

import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

import java.util.ArrayList;
import java.util.List;

/// 选择器：依次执行子节点，任一成功则成功，全部失败则失败。
public class SelectorNode extends BTNode {
    protected final List<BTNode> children;
    protected int currentIndex;
    private int preparedIndex = -1;

    public SelectorNode(List<BTNode> children) {
        this.children = children;
        this.currentIndex = -1;
    }

    @Override
    public boolean canStart() {
        preparedIndex = -1;
        for (int index = 0; index < children.size(); index++) {
            if (children.get(index).canStart()) {
                preparedIndex = index;
                return true;
            }
        }
        return false;
    }

    @Override
    public void start() {
        if (preparedIndex < 0) canStart();
        currentIndex = preparedIndex;
        preparedIndex = -1;
        if (currentIndex >= 0) children.get(currentIndex).start();
    }

    @Override
    public BTStatus execute() {
        if (currentIndex < 0) return BTStatus.FAILURE;
        tryHigherPriorityChildren();

        while (currentIndex < children.size()) {
            BTNode child = children.get(currentIndex);
            BTStatus status = child.execute();
            if (status == BTStatus.RUNNING) {
                return BTStatus.RUNNING;
            }
            child.stop();
            if (status == BTStatus.SUCCESS) {
                currentIndex = -1;
                return BTStatus.SUCCESS;
            }
            currentIndex++;
            while (currentIndex < children.size() && !children.get(currentIndex).canStart()) {
                currentIndex++;
            }
            if (currentIndex < children.size()) {
                children.get(currentIndex).start();
            }
        }
        currentIndex = -1;
        return BTStatus.FAILURE;
    }

    /// 只探测当前运行分支之前的节点，保持当前分支自身的运行状态不被重置。
    private void tryHigherPriorityChildren() {
        for (int index = 0; index < currentIndex; index++) {
            BTNode candidate = children.get(index);
            if (!candidate.canStart()) continue;
            children.get(currentIndex).stop();
            currentIndex = index;
            candidate.start();
            return;
        }
    }

    @Override
    public void stop() {
        preparedIndex = -1;
        if (currentIndex >= 0 && currentIndex < children.size()) {
            children.get(currentIndex).stop();
            currentIndex = -1;
        }
    }

    public static SelectorNode of(BTNode... nodes) {
        return new SelectorNode(new ArrayList<>(List.of(nodes)));
    }
}
