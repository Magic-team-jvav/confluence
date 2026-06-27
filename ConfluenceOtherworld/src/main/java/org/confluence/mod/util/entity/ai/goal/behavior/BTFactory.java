package org.confluence.mod.util.entity.ai.goal.behavior;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import org.confluence.mod.util.entity.ai.goal.behavior.composite.ParallelNode;
import org.confluence.mod.util.entity.ai.goal.behavior.composite.SelectorNode;
import org.confluence.mod.util.entity.ai.goal.behavior.composite.SequenceNode;
import org.confluence.mod.util.entity.ai.goal.behavior.condition.Condition;
import org.confluence.mod.util.entity.ai.goal.behavior.decoration.*;
import org.confluence.mod.util.entity.ai.goal.behavior.leaf.GoalWrapper;
import org.confluence.mod.util.entity.ai.goal.behavior.leaf.RandomWaitAction;
import org.confluence.mod.util.entity.ai.goal.behavior.leaf.WaitAction;

/// 行为树工厂
public class BTFactory {
    public static SequenceNode sequence() {
        return new SequenceNode();
    }

    public static SelectorNode selector() {
        return new SelectorNode();
    }

    public static ParallelNode parallel(
            ParallelNode.Policy successPolicy,
            ParallelNode.Policy failurePolicy
    ) {
        return new ParallelNode(successPolicy, failurePolicy);
    }

    public static InverterNode inverter(BTNode child) {
        return new InverterNode(child);
    }

    public static RepeaterNode repeater(int count, BTNode child) {
        return new RepeaterNode(count, child);
    }

    public static RepeaterNode infinite(BTNode child) {
        return new RepeaterNode(-1, child);
    }

    public static ConditionNode condition(Condition condition, BTNode child) {
        return new ConditionNode(condition, child);
    }

    public static BTNode wait(int ticks) {
        return new WaitAction(ticks);
    }

    public static BTNode waitRandom(int minInclusive, int maxInclusive, RandomSource random) {
        return new RandomWaitAction(minInclusive, maxInclusive, random);
    }

    public static BTNode waitRandom(int minInclusive, int maxInclusive) {
        return waitRandom(minInclusive, maxInclusive, RandomSource.create());
    }

    public static BTNode success(Runnable runnable) {
        return new BTNode() {
            @Override
            public BTStatus execute() {
                runnable.run();
                return BTStatus.SUCCESS;
            }
        };
    }

    public static BTNode waitForever() {
        return new BTNode() {
            @Override
            public BTStatus execute() {
                return BTStatus.RUNNING;
            }
        };
    }

    public static ParallelNode withTimer(int duration, BTNode node) {
        return parallel(ParallelNode.Policy.REQUIRE_ONE, ParallelNode.Policy.REQUIRE_ONE)
                .addChild(wait(duration))
                .addChild(node);
    }

    public static ParallelNode withTimer(int duration) {
        return parallel(ParallelNode.Policy.REQUIRE_ONE, ParallelNode.Policy.REQUIRE_ONE)
                .addChild(wait(duration));
    }

    public static GoalWrapper goal(Goal goal) {
        return new GoalWrapper(goal);
    }

    public static DecorationNode reverse(BTNode child) {
        return new ReverseNode(child);
    }

    public static DecorationNode fail2success(BTNode child) {
        return new ReverseNode.F2TNode(child);
    }

    public static DecorationNode success2fail(BTNode child) {
        return new ReverseNode.T2FNode(child);
    }


}
