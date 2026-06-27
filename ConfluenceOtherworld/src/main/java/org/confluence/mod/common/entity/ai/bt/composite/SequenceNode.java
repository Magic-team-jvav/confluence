package org.confluence.mod.common.entity.ai.bt.composite;

import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * 顺序节点：依次执行子节点，任一失败则失败，全部成功则成功。
 */
public class SequenceNode extends BTNode {
    protected final List<BTNode> children;
    protected int currentIndex;

    public SequenceNode(List<BTNode> children) {
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
        while (currentIndex < children.size()) {
            BTNode child = children.get(currentIndex);
            BTStatus status = child.execute();
            if (status == BTStatus.RUNNING) {
                return BTStatus.RUNNING;
            }
            child.stop();
            if (status == BTStatus.FAILURE) {
                return BTStatus.FAILURE;
            }
            currentIndex++;
            if (currentIndex < children.size()) {
                children.get(currentIndex).start();
            }
        }
        return BTStatus.SUCCESS;
    }

    @Override
    public void stop() {
        if (currentIndex < children.size()) {
            children.get(currentIndex).stop();
        }
    }

    public static SequenceNode of(BTNode... nodes) {
        return new SequenceNode(new ArrayList<>(List.of(nodes)));
    }
}
