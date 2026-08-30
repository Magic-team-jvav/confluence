package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/// 在空中游走一段时间后，沿锁定方向持续冲刺的循环行为。
///
/// 冲刺开始时只记录一次目标位置，之后不会每 tick 追踪转弯，因此玩家可以通过
/// 横向移动躲避。撞墙会立即结束本次冲刺并重新游走；接触伤害带独立冷却，避免实体
/// 包围盒持续相交时每 tick 重复结算。
public final class WanderDashCycleAction extends BTNode {
    private final PathfinderMob mob;
    private final int wanderTicks;
    private final int dashTicks;
    private final double dashSpeed;
    private final LookForwardWanderFlyAction wanderAction;
    private int phaseTicks;
    private boolean dashing;
    private Vec3 dashDirection = Vec3.ZERO;
    private Vec3 dashTarget = Vec3.ZERO;

    public WanderDashCycleAction(PathfinderMob mob, int wanderTicks, int dashTicks, double wanderSpeed, double dashSpeed) {
        if (wanderTicks <= 0 || dashTicks <= 0) {
            throw new IllegalArgumentException("Wander dash durations must be positive");
        }
        this.mob = mob;
        this.wanderTicks = wanderTicks;
        this.dashTicks = dashTicks;
        this.dashSpeed = dashSpeed;
        this.wanderAction = new LookForwardWanderFlyAction(mob, wanderSpeed, 0.0F, false);
    }

    @Override
    public void start() {
        beginWander();
    }

    @Override
    public BTStatus execute() {
        LivingEntity target = mob.getTarget();
        phaseTicks++;
        if (!dashing) {
            if (wanderAction.execute() != BTStatus.RUNNING) {
                wanderAction.start();
            }
            if (phaseTicks >= wanderTicks) {
                phaseTicks = 0;
                if (target != null && target.isAlive()) {
                    Vec3 targetPosition = target.getEyePosition();
                    Vec3 direction = targetPosition.subtract(mob.getEyePosition()).normalize();
                    if (direction.lengthSqr() > 1.0E-8) {
                        dashDirection = direction;
                        dashTarget = targetPosition;
                        dashing = true;
                    }
                }
            }
            if (!dashing) {
                return BTStatus.RUNNING;
            }
        }

        if (mob.horizontalCollision || mob.verticalCollision || phaseTicks >= dashTicks) {
            beginWander();
            return BTStatus.RUNNING;
        }
        if (target == null || !target.isAlive()) {
            beginWander();
            return BTStatus.RUNNING;
        }
        Vec3 lookPosition = mob.position().add(dashDirection.scale(20.0)).add(0.0, 1.0, 0.0);
        mob.getLookControl().setLookAt(lookPosition);
        mob.setYRot(mob.getYHeadRot());
        mob.setDeltaMovement(dashDirection.scale(dashSpeed));
        mob.hasImpulse = true;
        mob.getLookControl().setLookAt(dashTarget);
        return BTStatus.RUNNING;
    }

    private void beginWander() {
        dashing = false;
        phaseTicks = 0;
        dashDirection = Vec3.ZERO;
        dashTarget = Vec3.ZERO;
        wanderAction.start();
    }

    public boolean isDashing() {
        return dashing;
    }

    public Vec3 getDashDirection() {
        return dashDirection;
    }

    public void abortDash() {
        if (!dashing) return;
        beginWander();
    }
}
