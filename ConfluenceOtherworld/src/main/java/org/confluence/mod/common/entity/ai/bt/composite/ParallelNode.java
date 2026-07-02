package org.confluence.mod.common.entity.ai.bt.composite;

import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * 并行节点：同时执行所有子节点。
 */
public class ParallelNode extends BTNode {
    private final List<BTNode> children;
    private final Policy policy;

    public enum Policy { REQUIRE_ONE, REQUIRE_ALL }

    public ParallelNode(List<BTNode> children, Policy policy) {
        this.children = children;
        this.policy = policy;
    }

    @Override
    public void start() {
        for (BTNode child : children) child.start();
    }

    @Override
    public BTStatus execute() {
        int successCount = 0;
        int failureCount = 0;
        for (BTNode child : children) {
            BTStatus status = child.execute();
            if (status == BTStatus.SUCCESS) successCount++;
            else if (status == BTStatus.FAILURE) failureCount++;
        }
        if (policy == Policy.REQUIRE_ONE) {
            if (successCount > 0) return BTStatus.SUCCESS;
            if (failureCount == children.size()) return BTStatus.FAILURE;
        } else {
            if (successCount == children.size()) return BTStatus.SUCCESS;
            if (failureCount > 0) return BTStatus.FAILURE;
        }
        return BTStatus.RUNNING;
    }

    @Override
    public void stop() {
        for (BTNode child : children) child.stop();
    }

    public static ParallelNode of(Policy policy, BTNode... nodes) {
        return new ParallelNode(new ArrayList<>(List.of(nodes)), policy);
    }
}
