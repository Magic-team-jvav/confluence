package org.confluence.mod.common.summon.flying;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.summon.SummonTargetCache;
import org.confluence.mod.common.summon.FlyingSummon;
import org.confluence.mod.common.summon.SummonGoal;
import org.confluence.mod.common.summon.SummonPose;
import org.confluence.mod.common.summon.SummonStats;

/// 飞雀召唤物的运行实例。
///
/// <p>这里保留 1.21 侧的短促俯冲、命中回旋和悬停起伏。
/// 新架构只负责把实体 AI 改为玩家容器驱动，不能改变飞雀贴近目标后再短距离冲刺的节奏。</p>
public final class FinchSummon extends FlyingSummon {
    public static final int SLOT_COST = 1;
    public static final float BASE_DAMAGE = 2.0F;
    private int attackPhaseTicks;
    private int hitMovementCooldown;

    public FinchSummon(ServerPlayer owner, int slotCost, SummonStats stats, SummonPose initialPose) {
        super(Confluence.asResource("finch_baby"), owner, slotCost, stats, initialPose);
        addGoal(1, new AttackGoal(this));
        addGoal(9, new FollowOwnerGoal(this));
    }

    @Override
    protected LivingEntity findTarget() {
        return SummonTargetCache.acquire(owner().serverLevel(), owner(), uuid(), position(), 32.0);
    }

    @Override
    protected void beforeGoalTick() {
        attackPhaseTicks--;
        hitMovementCooldown--;
    }

    @Override
    protected void afterPathAdvance(SummonPose previousPreviousPose, SummonPose previousPose, SummonPose currentPose) {
        if (target() == null) {
            return;
        }
        boolean hit = hurtTouchingTargets(AABB.ofSize(position().add(0.0, 0.25, 0.0), 0.5, 0.5, 0.5).inflate(0.75),
                32.0, 1.0F);
        if (hit && hitMovementCooldown <= -5) hitMovementCooldown = 10;
    }

    private void attack(LivingEntity target) {
        Vec3 direction = targetPosition().subtract(position());
        double distanceSqr = Math.max(0.001, direction.lengthSqr());
        if (attackPhaseTicks <= 0) {
            Rotation rotation = turnToward(direction, 90.0F, 85.0F);
            Vec3 movement = velocity();
            Vec3 look = Vec3.directionFromRotation(rotation.pitch(), rotation.yaw());
            if (angleBetween(look, direction) < 0.5 && movement.length() < 1.0) {
                movement = movement.add(direction.normalize().scale(0.1));
            }
            moveBy(movement.add(0.0, verticalBob(), 0.0), rotation.yaw(), rotation.pitch());
            if (distanceSqr < 3.0 && hitMovementCooldown < 0) attackPhaseTicks = 20;
        } else {
            Vec3 forward = Vec3.directionFromRotation(currentPose().pitch(), currentPose().yaw()).normalize();
            Rotation rotation = turnToward(direction, 10.0F, 85.0F);
            Vec3 movement = velocity().add(forward.scale(0.03)).add(0.0,
                    Math.min(0.02, 1.0 / distanceSqr) + verticalBob(), 0.0);
            moveBy(movement, rotation.yaw(), rotation.pitch());
        }
    }

    private void followOwner() {
        Vec3 destination = owner().position().add(0.0, 1.8, 0.0);
        Vec3 offset = destination.subtract(position());
        Vec3 movement = velocity().scale(0.88);
        if (offset.lengthSqr() >= 32.0 * 32.0)
            movement = movement.add(offset.normalize().scale(0.10));
        if (movement.length() > 0.70) {
            movement = movement.normalize().scale(0.70);
        }
        moveBy(movement.add(0.0, verticalBob(), 0.0));
    }

    private double verticalBob() {
        return Math.sin(tickCount() * 0.5F) * 0.03F;
    }

    private Rotation turnToward(Vec3 direction, float maximumYawChange, float maximumPitchChange) {
        Vec3 normalized = direction.normalize();
        float desiredYaw = (float) Math.toDegrees(Math.atan2(-normalized.x, normalized.z));
        float desiredPitch = (float) Math.toDegrees(Math.asin(-normalized.y));
        float yaw = currentPose().yaw() + Mth.clamp(Mth.wrapDegrees(desiredYaw - currentPose().yaw()),
                -maximumYawChange, maximumYawChange);
        float pitch = currentPose().pitch() + Mth.clamp(Mth.wrapDegrees(desiredPitch - currentPose().pitch()),
                -maximumPitchChange, maximumPitchChange);
        return new Rotation(yaw, pitch);
    }

    private static double angleBetween(Vec3 first, Vec3 second) {
        double denominator = Math.sqrt(first.lengthSqr() * second.lengthSqr());
        if (denominator < 1.0E-8) {
            return Math.PI;
        }
        return Math.acos(Mth.clamp(first.dot(second) / denominator, -1.0, 1.0));
    }

    private static final class AttackGoal extends SummonGoal<FinchSummon> {
        private AttackGoal(FinchSummon summon) {
            super(summon);
        }

        @Override
        public boolean canUse() {
            return summon.target() != null;
        }

        @Override
        public void tick() {
            summon.attack(summon.target());
        }
    }

    private static final class FollowOwnerGoal extends SummonGoal<FinchSummon> {
        private FollowOwnerGoal(FinchSummon summon) {
            super(summon);
        }

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public void tick() {
            summon.followOwner();
        }
    }

    private record Rotation(float yaw, float pitch) {}
}
