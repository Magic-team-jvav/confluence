package org.confluence.mod.common.entity.ai.goal.behavior.composite;

import org.confluence.mod.common.entity.ai.goal.behavior.BTFactory;
import org.confluence.mod.common.entity.ai.goal.behavior.BTNode;
import org.confluence.mod.common.entity.ai.goal.behavior.condition.Condition;

/**
 * 选择节点（执行直到一个子节点成功）
 */
public class SelectorNode extends CompositeNode {
    private int currentIndex = 0;

    public SelectorNode addChild(BTNode child) {
        children.add(child);
        return this;
    }

    public SelectorNode addWithCondition(Condition condition, BTNode child) {
        children.add(BTFactory.condition(condition, child));
        return this;
    }

    public SelectorNode addWithCondition(Condition condition, String desc, BTNode child) {
        children.add(BTFactory.condition(condition, child).setDesc(desc));
        return this;
    }

    @Override
    public BTStatus execute() {
        while (currentIndex < children.size()) {

            BTNode child = children.get(currentIndex);
//            if(children.get(0) instanceof SequenceNode) {
//                System.out.println(currentIndex);
//            }
            if (child.isReady()) {
                if (currentIndex > 0) {
                    children.get(currentIndex - 1).stop();
                }
                child.start();
            }

            child.tick();

            if (child.canContinueToUse()) {
                return BTStatus.RUNNING;
            }

            BTStatus childResult = child.getStatus();
            child.stop();

            if (childResult == BTStatus.SUCCESS) {
                return BTStatus.SUCCESS;
            }

            currentIndex++;
        }

        return BTStatus.FAILURE;
    }

    @Override
    public String toString() {
        return children.stream().reduce(
                new StringBuilder("SelectorNode[").append(currentIndex).append("/").append(children.size()).append("|"),
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
