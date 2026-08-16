package org.confluence.mod.common.entity.ai.bt.composite;

import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

import java.util.ArrayList;
import java.util.List;

/// 轮转选择节点。
///
/// <p>普通选择节点每轮都从第一个子节点开始，前置分支长期成功时会饿死后续攻击。
/// 本节点在任一分支结束后把下一轮起点推进一位；失败分支仍会在当前 tick 内继续尝试后续节点，
/// 只有运行中的分支会跨 tick 保持当前位置。该语义用于需要轮换技能、又允许条件分支跳过的生物。</p>
public class RoundRobinSelectorNode extends BTNode {
    private final List<BTNode> children;
    private int nextIndex;
    private int currentIndex = -1;
    private int attemptedChildren;

    public RoundRobinSelectorNode(List<BTNode> children) {
        this.children = children;
    }

    @Override
    public void start() {
        // 每轮只允许检查一圈，防止所有子节点失败时在同一 tick 无限循环。
        attemptedChildren = 0;
        if (children.isEmpty()) {
            currentIndex = -1;
            return;
        }
        currentIndex = nextIndex;
        children.get(currentIndex).start();
    }

    @Override
    public BTStatus execute() {
        while (currentIndex >= 0 && attemptedChildren < children.size()) {
            BTNode child = children.get(currentIndex);
            BTStatus status = child.execute();
            if (status == BTStatus.RUNNING) {
                return BTStatus.RUNNING;
            }

            child.stop();
            // 无论成功还是失败都推进起点，确保下一轮不会再次偏向同一分支。
            nextIndex = (currentIndex + 1) % children.size();
            if (status == BTStatus.SUCCESS) {
                currentIndex = -1;
                return BTStatus.SUCCESS;
            }

            attemptedChildren++;
            if (attemptedChildren < children.size()) {
                currentIndex = nextIndex;
                children.get(currentIndex).start();
            }
        }
        currentIndex = -1;
        return BTStatus.FAILURE;
    }

    @Override
    public void stop() {
        if (currentIndex >= 0) {
            children.get(currentIndex).stop();
            currentIndex = -1;
        }
    }

    public static RoundRobinSelectorNode of(BTNode... nodes) {
        return new RoundRobinSelectorNode(new ArrayList<>(List.of(nodes)));
    }
}
