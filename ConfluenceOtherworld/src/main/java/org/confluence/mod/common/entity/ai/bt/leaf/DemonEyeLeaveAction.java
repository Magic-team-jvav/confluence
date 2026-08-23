package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/// 恶魔眼白天离场时使用的持续上升运动。
///
/// 行为只改变速度，不强制清除目标，也不在固定时间后直接删除实体；实际离场仍交给
/// 原版距离卸载规则处理，从而保持 1.21 可见的飞离过程。
public final class DemonEyeLeaveAction extends BTNode {
    private final PathfinderMob mob;
    private Vec3 acceleration = Vec3.ZERO;

    public DemonEyeLeaveAction(PathfinderMob mob) {
        this.mob = mob;
    }

    @Override
    public void start() {
        double x = mob.getRandom().nextDouble() - 0.5;
        double y = 0.1 + 0.5 * mob.getRandom().nextDouble();
        double z = mob.getRandom().nextDouble() - 0.5;
        acceleration = new Vec3(x, y, z).normalize().scale(0.25);
    }

    @Override
    public BTStatus execute() {
        if (!mob.level().isDay()) {
            return BTStatus.SUCCESS;
        }
        if (mob.getDeltaMovement().length() < 0.5) {
            mob.addDeltaMovement(acceleration);
            mob.hasImpulse = true;
        }
        return BTStatus.RUNNING;
    }
}
