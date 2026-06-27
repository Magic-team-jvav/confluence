package org.confluence.mod.util.entity.ai.goal.behavior.composite;

import org.confluence.mod.util.entity.ai.goal.behavior.BTFactory;
import org.confluence.mod.util.entity.ai.goal.behavior.BTNode;
import org.confluence.mod.util.entity.ai.goal.behavior.condition.Condition;

/**
 * 序列节点（按顺序执行，全部成功才算成功）
 */
public class SequenceNode extends CompositeNode {
    private int currentIndex = 0;

    public SequenceNode addChild(BTNode child) {
        children.add(child);
        return this;
    }

    public SequenceNode addWithCondition(Condition condition, BTNode child) {
        children.add(BTFactory.condition(condition, child));
        return this;
    }

    public SequenceNode addWithCondition(Condition condition, String desc, BTNode child) {
        children.add(BTFactory.condition(condition, child).setDesc(desc));
        return this;
    }

    @Override
    public BTStatus execute() {
        while (currentIndex < children.size()) {
            BTNode child = children.get(currentIndex);

            child.tryStart();

            child.tick();

            if (child.canContinueToUse()) {
                return BTStatus.RUNNING;
            }

            BTStatus childResult = child.getStatus();
            child.stop();

            if (childResult == BTStatus.FAILURE) {
                return BTStatus.FAILURE;
            }

            currentIndex++;
        }

        return BTStatus.SUCCESS;
    }

    @Override
    public String toString() {

        return children.stream().reduce(
                new StringBuilder("SequenceNode[").append(currentIndex).append("/").append(children.size()).append("|"),
                (sb, node)-> sb.append(",").append(node.getClass().getSimpleName()),
                (a, b)->a ).append("]").toString();
    }

    @Override
    protected void cleanup() {
        currentIndex = 0;
        for (BTNode child : children) {
//            if (child.getStatus() == BTStatus.RUNNING) {
                child.stop();
//            }
        }
    }

    public int getCurrentIndex() {
        return currentIndex;
    }
}
