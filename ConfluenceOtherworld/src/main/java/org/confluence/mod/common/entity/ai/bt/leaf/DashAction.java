package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/// 直线冲刺：锁定方向后高速冲向目标，触碰造成伤害。
public class DashAction extends BTNode {
    private final Mob mob;
    private final double speed;
    private final int duration;
    private Vec3 dashDir;
    private int tick;

    public DashAction(Mob mob, double speed, int duration) {
        this.mob = mob;
        this.speed = speed;
        this.duration = duration;
    }

    @Override
    public void start() {
        tick = 0;
        LivingEntity target = mob.getTarget();
        if (target != null) {
            dashDir = target.position().subtract(mob.position()).normalize();
        } else {
            dashDir = mob.getLookAngle();
        }
    }

    @Override
    public BTStatus execute() {
        tick++;
        if (tick > duration) return BTStatus.SUCCESS;

        mob.setDeltaMovement(dashDir.scale(speed));

        LivingEntity target = mob.getTarget();
        if (target != null && mob.getBoundingBox().inflate(0.5).intersects(target.getBoundingBox())) {
            mob.doHurtTarget(target);
            return BTStatus.SUCCESS;
        }
        return BTStatus.RUNNING;
    }

    @Override
    public void stop() {
        mob.setDeltaMovement(Vec3.ZERO);
    }
}
