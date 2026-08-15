package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.ai.goal.Goal;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

import java.util.Objects;

/**
 * 将单个原版 Goal 作为行为树叶节点运行。
 *
 * <p>该适配器只复用已经稳定的原版动作实现，例如漂浮、巡游、观察、繁殖和跟随；
 * 调度顺序、抢占关系与生命周期仍由行为树负责，因此实体不会重新安装第二套 Goal 调度器。
 * 不满足启动条件时节点立即失败，使选择节点可以继续执行后续日常行为。</p>
 */
public final class VanillaGoalAction extends BTNode {
    private final Goal goal;
    private boolean running;

    public VanillaGoalAction(Goal goal) {
        this.goal = Objects.requireNonNull(goal, "goal");
    }

    @Override
    public void start() {
        running = goal.canUse();
        if (running) {
            goal.start();
        }
    }

    @Override
    public BTStatus execute() {
        if (!running) {
            return BTStatus.FAILURE;
        }
        if (!goal.canContinueToUse()) {
            return BTStatus.SUCCESS;
        }
        goal.tick();
        return BTStatus.RUNNING;
    }

    @Override
    public void stop() {
        if (running) {
            goal.stop();
            running = false;
        }
    }
}
