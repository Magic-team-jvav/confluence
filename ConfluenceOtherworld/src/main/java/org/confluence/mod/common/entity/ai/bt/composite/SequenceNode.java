package org.confluence.mod.common.entity.ai.bt.composite;

import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

import java.util.ArrayList;
import java.util.List;

/// 顺序节点：依次执行子节点，任一失败则失败，全部成功则成功。
public class SequenceNode extends BTNode {
    protected final List<BTNode> children;
    protected int currentIndex;

    public SequenceNode(List<BTNode> children) {
        this.children = children;
        this.currentIndex = -1;
    }

    @Override
    public void start() {
        currentIndex = children.isEmpty() ? -1 : 0;
        if (currentIndex >= 0) children.get(currentIndex).start();
    }

    @Override
    public BTStatus execute() {
        if (currentIndex < 0) return BTStatus.SUCCESS;
        while (currentIndex < children.size()) {
            BTNode child = children.get(currentIndex);
            BTStatus status = child.execute();
            if (status == BTStatus.RUNNING) {
                return BTStatus.RUNNING;
            }
            child.stop();
            if (status == BTStatus.FAILURE) {
                currentIndex = -1;
                return BTStatus.FAILURE;
            }
            currentIndex++;
            if (currentIndex < children.size()) {
                children.get(currentIndex).start();
            }
        }
        currentIndex = -1;
        return BTStatus.SUCCESS;
    }

    @Override
    public void stop() {
        if (currentIndex >= 0 && currentIndex < children.size()) {
            children.get(currentIndex).stop();
            currentIndex = -1;
        }
    }

    public static SequenceNode of(BTNode... nodes) {
        return new SequenceNode(new ArrayList<>(List.of(nodes)));
    }
}
