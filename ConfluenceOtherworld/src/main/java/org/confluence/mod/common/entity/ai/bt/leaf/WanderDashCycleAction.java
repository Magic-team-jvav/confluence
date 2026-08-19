package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/// 在空中游走一段时间后，沿锁定方向持续冲刺的循环行为。
///
/// <p>冲刺开始时只记录一次目标位置，之后不会每 tick 追踪转弯，因此玩家可以通过
/// 横向移动躲避。撞墙会立即结束本次冲刺并重新游走；接触伤害带独立冷却，避免实体
/// 包围盒持续相交时每 tick 重复结算。</p>
public final class WanderDashCycleAction extends BTNode {
    private final PathfinderMob mob;
    private final int wanderTicks;
    private final int dashTicks;
    private final double wanderSpeed;
    private final int wanderRange;
    private final double dashSpeed;
    private int phaseTicks;
    private boolean dashing;
    private Vec3 wanderTarget = Vec3.ZERO;
    private Vec3 dashDirection = Vec3.ZERO;

    public WanderDashCycleAction(PathfinderMob mob, int wanderTicks, int dashTicks, double wanderSpeed, int wanderRange, double dashSpeed) {
        if (wanderTicks <= 0 || dashTicks <= 0 || wanderRange <= 0) {
            throw new IllegalArgumentException("Wander dash durations and range must be positive");
        }
        this.mob = mob;
        this.wanderTicks = wanderTicks;
        this.dashTicks = dashTicks;
        this.wanderSpeed = wanderSpeed;
        this.wanderRange = wanderRange;
        this.dashSpeed = dashSpeed;
    }

    @Override
    public void start() {
        beginWander();
    }

    @Override
    public BTStatus execute() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) {
            return BTStatus.FAILURE;
        }

        phaseTicks++;
        if (!dashing) {
            moveTowardWanderTarget();
            if (phaseTicks >= wanderTicks) {
                Vec3 direction = target.position().subtract(mob.position()).normalize();
                if (direction.lengthSqr() > 1.0E-8) {
                    dashDirection = direction;
                    dashing = true;
                    phaseTicks = 0;
                }
            }
            return BTStatus.RUNNING;
        }

        if (mob.horizontalCollision || mob.verticalCollision || phaseTicks >= dashTicks) {
            beginWander();
            return BTStatus.RUNNING;
        }
        mob.setDeltaMovement(dashDirection.scale(dashSpeed));
        mob.getLookControl().setLookAt(mob.position().add(dashDirection.scale(20.0)));
        return BTStatus.RUNNING;
    }

    private void beginWander() {
        dashing = false;
        phaseTicks = 0;
        dashDirection = Vec3.ZERO;
        chooseWanderTarget();
    }

    private void moveTowardWanderTarget() {
        if (mob.position().distanceToSqr(wanderTarget) < 1.0) {
            chooseWanderTarget();
        }
        Vec3 direction = wanderTarget.subtract(mob.position());
        if (direction.lengthSqr() > 1.0E-8) {
            mob.setDeltaMovement(mob.getDeltaMovement().add(direction.normalize().scale(wanderSpeed * 0.05)).scale(0.95));
        }
    }

    private void chooseWanderTarget() {
        RandomSource random = mob.getRandom();
        wanderTarget = mob.position().add((random.nextDouble() - 0.5) * wanderRange * 2.0, (random.nextDouble() - 0.5) * wanderRange, (random.nextDouble() - 0.5) * wanderRange * 2.0);
    }

    public boolean isDashing() {
        return dashing;
    }

    public Vec3 getDashDirection() {
        return dashDirection;
    }
}
