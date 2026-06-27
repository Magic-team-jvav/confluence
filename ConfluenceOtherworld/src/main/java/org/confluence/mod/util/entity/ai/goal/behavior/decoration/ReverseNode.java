package org.confluence.mod.util.entity.ai.goal.behavior.decoration;

import org.confluence.mod.util.entity.ai.goal.behavior.BTNode;

/// 反转节点
public class ReverseNode extends DecorationNode {
    public ReverseNode(BTNode child) {
        super(child);
    }

    @Override
    public BTStatus execute() {
        BTStatus state = this.child.execute();
        if (state == BTStatus.SUCCESS) {
            return BTStatus.FAILURE;
        }
        if (state == BTStatus.FAILURE) {
            return BTStatus.SUCCESS;
        }
        return state;
    }

    public static DecorationNode reverse(BTNode child) {
        return new ReverseNode(child);
    }

    public static DecorationNode fail2success(BTNode child) {
        return new F2TNode(child);
    }

    public static DecorationNode success2fail(BTNode child) {
        return new T2FNode(child);
    }

    /**
     * 当状态为失败时，将其转换为成功状态。用于sequence防止短路
     */
    public static class F2TNode extends DecorationNode {

        public F2TNode(BTNode child) {
            super(child);
        }

        @Override
        public BTStatus execute() {
            BTStatus status = child.execute();
            if (status == BTStatus.FAILURE) {
                return BTStatus.SUCCESS;
            }
            return status;
        }
    }

    /**
     * 当状态为成功时，将其转换为失败状态。用于sequence提前短路
     */
    public static class T2FNode extends DecorationNode {

        public T2FNode(BTNode child) {
            super(child);
        }

        @Override
        public BTStatus execute() {
            BTStatus status = child.execute();
            if (status == BTStatus.SUCCESS) {
                return BTStatus.FAILURE;
            }
            return status;
        }
    }
}
