package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/// 水生生物的限时随机游动节点。
///
/// {@link #start()} 只从附近可游泳的位置中选择目的地，并把路径交给实体当前的水中导航器；
/// 找不到候选点或导航器拒绝路径时，本轮直接失败，允许上层行为树尝试其他节点。路径完成后
/// 返回成功。
///
/// 即使导航器因为动态地形或流体变化始终无法结束，节点也会在
/// {@value #TIMEOUT_TICKS} tick 后退出，防止一次坏路径永久占用行为树。
public class RandomSwimAction extends BTNode {
    private static final int TIMEOUT_TICKS = 100;

    private final PathfinderMob mob;
    private final double speed;
    private final int horizontalRange;
    private final int verticalRange;
    private int ticks;
    private boolean pathStarted;

    public RandomSwimAction(PathfinderMob mob, double speed, int horizontalRange, int verticalRange) {
        if (!Double.isFinite(speed) || speed <= 0.0 || horizontalRange <= 0 || verticalRange <= 0) {
            throw new IllegalArgumentException("Random swim speed and ranges must be positive");
        }
        this.mob = mob;
        this.speed = speed;
        this.horizontalRange = horizontalRange;
        this.verticalRange = verticalRange;
    }

    @Override
    public void start() {
        ticks = 0;
        // 候选点生成和启动路径必须同时成功，execute 才进入持续状态。
        Vec3 target = BehaviorUtils.getRandomSwimmablePos(mob, horizontalRange, verticalRange);
        pathStarted = target != null
                && mob.getNavigation().moveTo(target.x, target.y, target.z, speed);
    }

    @Override
    public BTStatus execute() {
        if (!pathStarted) {
            return BTStatus.FAILURE;
        }
        if (++ticks > TIMEOUT_TICKS || mob.getNavigation().isDone()) {
            return BTStatus.SUCCESS;
        }
        return BTStatus.RUNNING;
    }

    @Override
    public void stop() {
        if (pathStarted) {
            mob.getNavigation().stop();
            pathStarted = false;
        }
    }
}
