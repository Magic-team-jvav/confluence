package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/// 带短暂蓄力的直线冲锋动作。
///
/// <p>蓄力阶段允许实体继续面向目标，以便玩家能够读出即将冲锋的方向；进入冲锋阶段时
/// 会保存一次目标方向，后续只沿该方向加速。这样玩家横向闪避后，冲锋者会从身旁掠过，
/// 而不会在高速移动期间持续自动追踪。</p>
public class ChargeAttackAction extends BTNode {
    protected final PathfinderMob mob;
    protected final double speed;
    protected int tick;
    protected static final int DURATION = 30;
    protected static final int WINDUP = 10;
    private final ContactAttackTimer contactAttack;
    private Vec3 lockedDirection = Vec3.ZERO;

    public ChargeAttackAction(PathfinderMob mob, double speed) {
        this(mob, speed, 0.0);
    }

    public ChargeAttackAction(
            PathfinderMob mob,
            double speed,
            double contactInflation) {
        if (!Double.isFinite(speed) || speed <= 0.0) {
            throw new IllegalArgumentException(
                    "Charge speed must be finite and positive");
        }
        this.mob = mob;
        this.speed = speed;
        this.contactAttack = new ContactAttackTimer(
                contactInflation, 10, 20);
    }

    @Override
    public void start() {
        tick = 0;
        lockedDirection = Vec3.ZERO;
    }

    @Override
    public BTStatus execute() {
        tick++;
        LivingEntity target = mob.getTarget();
        if (target == null) return BTStatus.SUCCESS;
        contactAttack.tick(mob, target);

        if (tick < WINDUP) {
            Vec3 dir = target.position().subtract(mob.position()).normalize();
            mob.setDeltaMovement(dir.scale(speed * 0.02));
            return BTStatus.RUNNING;
        }

        if (tick >= DURATION) return BTStatus.SUCCESS;

        if (lockedDirection.lengthSqr() < 1.0E-8) {
            lockedDirection = target.position().subtract(mob.position()).normalize();
            if (lockedDirection.lengthSqr() < 1.0E-8) {
                return BTStatus.FAILURE;
            }
        }
        Vec3 acceleration = lockedDirection.scale(speed * 0.08);
        mob.setDeltaMovement(
                mob.getDeltaMovement().add(acceleration).scale(0.95));
        mob.hasImpulse = true;

        return BTStatus.RUNNING;
    }

    @Override
    public void stop() {
        mob.setDeltaMovement(Vec3.ZERO);
    }
}
