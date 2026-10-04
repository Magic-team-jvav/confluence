package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.ai.goal.Goal;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/// 将单个原版 Goal 作为行为树叶节点运行。
///
/// 该适配器只复用已经稳定的原版动作实现，例如漂浮、巡游、观察、繁殖和跟随；
/// 调度顺序、抢占关系与生命周期仍由行为树负责，因此实体不会重新安装第二套 Goal 调度器。
/// 不满足启动条件时节点立即失败，使选择节点可以继续执行后续日常行为。
public final class VanillaGoalAction extends BTNode {
    private final Goal goal;
    private boolean running;
    private boolean prepared;
    private boolean firstTick;

    public VanillaGoalAction(Goal goal) {
        this.goal = goal;
    }

    @Override
    public boolean canStart() {
        prepared = goal.canUse();
        return prepared;
    }

    @Override
    public void start() {
        running = prepared || goal.canUse();
        prepared = false;
        firstTick = true;
        if (running) {
            goal.start();
        }
    }

    @Override
    public BTStatus execute() {
        if (!running) {
            return BTStatus.FAILURE;
        }
        // 刚启动时先执行一次，避免贴脸无路径或尚未离地的动作在首刻被提前结束。
        if (!firstTick && !goal.canContinueToUse()) {
            return BTStatus.SUCCESS;
        }
        firstTick = false;
        goal.tick();
        return BTStatus.RUNNING;
    }

    @Override
    public void stop() {
        prepared = false;
        if (running) {
            goal.stop();
            running = false;
        }
    }
}
