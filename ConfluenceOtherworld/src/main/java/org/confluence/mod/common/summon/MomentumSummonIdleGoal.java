package org.confluence.mod.common.summon;

import net.minecraft.world.phys.Vec3;

/// 让飞行召唤物在空闲时保留惯性，并在距离过远时主动返航。
public final class MomentumSummonIdleGoal<T extends FlyingSummon> extends SummonGoal<T> {
    private static final double START_FOLLOW_DISTANCE_SQR = 32.0 * 32.0;
    private static final double STOP_FOLLOW_DISTANCE_SQR = 2.0 * 2.0;
    private final double height;
    private final double acceleration;
    private final double maximumSpeed;
    private final int recalculateInterval;
    private boolean followingOwner;
    private int recalculateCooldown;
    private Vec3 followDestination;

    public MomentumSummonIdleGoal(T summon, double height, double acceleration, double maximumSpeed) {
        this(summon, height, acceleration, maximumSpeed, 0);
    }

    public MomentumSummonIdleGoal(T summon, double height, double acceleration, double maximumSpeed, int recalculateInterval) {
        super(summon);
        this.height = height;
        this.acceleration = acceleration;
        this.maximumSpeed = maximumSpeed;
        this.recalculateInterval = recalculateInterval;
    }

    @Override
    public boolean canUse() {
        return summon.target() == null;
    }

    @Override
    public void tick() {
        Vec3 ownerPosition = summon.owner().position();
        double distanceSqr = summon.position().distanceToSqr(ownerPosition);
        if (!followingOwner && distanceSqr >= START_FOLLOW_DISTANCE_SQR) {
            followingOwner = true;
            recalculateCooldown = 0;
        }
        if (followingOwner && distanceSqr <= STOP_FOLLOW_DISTANCE_SQR) followingOwner = false;
        if (followingOwner) {
            if (followDestination == null || recalculateInterval == 0 || --recalculateCooldown <= 0) {
                followDestination = ownerPosition.add(0.0, height, 0.0);
                recalculateCooldown = recalculateInterval;
            }
            summon.moveToward(followDestination, acceleration, maximumSpeed);
            return;
        }
        summon.moveBy(summon.idleVelocity());
    }

    @Override
    public void stop() {
        followingOwner = false;
        followDestination = null;
    }
}
