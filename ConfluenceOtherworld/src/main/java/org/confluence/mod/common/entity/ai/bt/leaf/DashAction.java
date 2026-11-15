package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.monster.BaseMonster;

/// 直线冲刺：锁定方向后高速冲向目标，伤害由实体共用接触判定结算。
public class DashAction extends BTNode {
    private final BaseMonster mob;
    private final double speed;
    private final int duration;
    private Vec3 dashDir;
    private int tick;

    public DashAction(BaseMonster mob, double speed, int duration) {
        if (speed <= 0.0 || duration <= 0)
            throw new IllegalArgumentException("Dash speed and duration must be positive");
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
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive() || !mob.canAttack(target)) return BTStatus.FAILURE;
        tick++;
        if (tick > duration) return BTStatus.SUCCESS;

        mob.faceCombatDirection(dashDir, 180.0F, 180.0F);
        mob.setDeltaMovement(dashDir.scale(speed));
        mob.hasImpulse = true;
        return BTStatus.RUNNING;
    }

    @Override
    public void stop() {
        mob.setDeltaMovement(Vec3.ZERO);
    }
}
