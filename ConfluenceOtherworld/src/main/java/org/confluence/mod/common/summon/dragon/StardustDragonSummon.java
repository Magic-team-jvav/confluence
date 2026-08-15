package org.confluence.mod.common.summon.dragon;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshot;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.summon.SummonTargetCache;
import org.confluence.mod.common.summon.FlyingSummon;
import org.confluence.mod.common.summon.SummonAnimation;
import org.confluence.mod.common.summon.SummonGoal;
import org.confluence.mod.common.summon.SummonPose;
import org.confluence.mod.common.summon.SummonRenderPart;
import org.confluence.mod.common.summon.SummonVisualState;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.UUID;

/**
 * 星尘龙召唤物的运行实例。
 *
 * <p>重复召唤时只增加沿历史轨迹跟随的龙身长度，不再为每一节身体创建独立实体。
 * 这样既保留 1.21 侧“越召越长”的行为，也能避免多实体同步和碰撞带来的额外不稳定性。</p>
 */
public final class StardustDragonSummon extends FlyingSummon {
    public static final int SLOT_COST = 1;
    public static final float BASE_DAMAGE = 1.0F;
    private static final double SEGMENT_SPACING = 0.55;
    private static final int SAMPLES_PER_SEGMENT = 10;
    private final Deque<Vec3> pathHistory = new ArrayDeque<>();
    private Vec3 movementTarget;
    private int movementTargetTicks;
    private int bodyAttackCooldown;
    private float yawAcceleration;

    public StardustDragonSummon(ServerPlayer owner, int slotCost, ProjectileCombatSnapshot snapshot, SummonPose initialPose) {
        super(Confluence.asResource("stardust_dragon"), owner, slotCost, snapshot, initialPose);
        addGoal(1, new AttackGoal(this));
        addGoal(9, new IdleGoal(this));
    }

    @Override
    protected LivingEntity findTarget() {
        return SummonTargetCache.acquire(owner().serverLevel(), owner(), uuid(), position(), 40.0);
    }

    @Override
    protected void beforeGoalTick() {
        recordPath();
        if (bodyAttackCooldown > 0) {
            bodyAttackCooldown--;
        }
    }

    @Override
    protected void afterPathAdvance(SummonPose previousPreviousPose, SummonPose previousPose, SummonPose currentPose) {
        if (target() == null || bodyAttackCooldown > 0) {
            return;
        }
        hurtTouchingTargets(AABB.ofSize(position().add(0.0, 0.25, 0.0), 0.5, 0.5, 0.5).inflate(0.75), 40.0, 1.0F);
        for (Vec3 segment : segmentPositions()) {
            hurtTouchingTargets(AABB.ofSize(segment.add(0.0, 0.25, 0.0), 0.5, 0.5, 0.5).inflate(0.75), 40.0, 1.0F);
        }
        bodyAttackCooldown = 5;
    }

    @Override
    public boolean canMergeAdditionalSummon() {
        return true;
    }

    @Override
    public boolean tryMergeAdditionalSummon(int additionalSlots, ProjectileCombatSnapshot snapshot) {
        if (additionalSlots <= 0) {
            return false;
        }
        increaseSlotCost(additionalSlots);
        replaceCombatSnapshot(snapshot);
        return true;
    }

    private void attack(LivingEntity target) {
        if (movementTarget == null || movementTargetTicks-- <= 0) {
            movementTarget = targetBounds().getCenter();
            movementTargetTicks = 5;
        }
        steerToward(movementTarget, 0.7F, 0.7);
    }

    private void idle() {
        if (movementTarget == null || movementTargetTicks-- <= 0) {
            var random = owner().getRandom();
            movementTarget = owner().position().add((random.nextDouble() - 0.5) * 5.0,
                    (random.nextDouble() - 0.5) * 5.0 + 2.0, (random.nextDouble() - 0.5) * 5.0);
            movementTargetTicks = 10;
        }
        if (position().distanceToSqr(owner().position()) > 128.0 * 128.0) {
            initializePose(new SummonPose(owner().getBoundingBox().getCenter(), currentPose().yaw(), 0.0F, 0.0F));
            pathHistory.clear();
            movementTarget = null;
            return;
        }
        steerToward(movementTarget, 0.3F, 0.3);
    }

    /**
     * 按 1.21 龙类移动保留航向惯性与转向速度差异。
     */
    private void steerToward(Vec3 destination, float turnSpeed, double movementSpeed) {
        Vec3 offset = destination.subtract(position());
        if (offset.lengthSqr() < 1.0E-6) {
            return;
        }
        Vec3 directionToTarget = offset.normalize();
        double yawRadians = currentPose().yaw() * Mth.DEG_TO_RAD;
        Vec3 forwardDirection = new Vec3(Mth.sin((float) yawRadians), velocity().y, Mth.cos((float) yawRadians)).normalize();
        float alignmentFactor = Math.max(((float) forwardDirection.dot(directionToTarget) + 0.5F) / 1.5F, 0.0F);
        double horizontal = Math.max(1.0E-5, offset.horizontalDistance());
        double vertical = Mth.clamp(offset.y / horizontal, -movementSpeed, movementSpeed);
        Vec3 accelerated = velocity().add(0.0, vertical * 0.05, 0.0);
        float targetYaw = -(float) Mth.atan2(offset.x, offset.z) * Mth.RAD_TO_DEG;
        float yawError = Mth.clamp(Mth.wrapDegrees(targetYaw - currentPose().yaw()), -50.0F, 50.0F);
        float speedFactor = (float) velocity().horizontalDistance() + 1.0F;
        yawAcceleration = yawAcceleration * 0.8F + yawError * turnSpeed / Math.min(speedFactor, 40.0F) / speedFactor;
        accelerated = accelerated.add(0.0, yawAcceleration * 0.0002F, 0.0);
        float yaw = currentPose().yaw() + yawAcceleration * 0.1F;
        Vec3 forward = Vec3.directionFromRotation(0.0F, yaw).scale(0.06F * (alignmentFactor + 1.5F));
        Vec3 moving = accelerated.add(forward);
        Vec3 movingDirection = moving.lengthSqr() > 1.0E-8 ? moving.normalize() : Vec3.ZERO;
        double directionMatch = 0.8 + 0.15 * (movingDirection.dot(forwardDirection) + 1.0) / 2.0;
        Vec3 nextVelocity = moving.multiply(directionMatch * movementSpeed, 0.91, directionMatch * movementSpeed);
        float pitch = nextVelocity.lengthSqr() < 1.0E-8 ? currentPose().pitch()
                : (float) Math.toDegrees(Math.asin(-nextVelocity.normalize().y));
        moveBy(nextVelocity, yaw, pitch);
    }

    private void recordPath() {
        if (pathHistory.isEmpty() || pathHistory.peekFirst().distanceToSqr(position()) > 1.0E-5) {
            pathHistory.addFirst(position());
        }
        int maximumSamples = Math.max(40, slotCost() * SAMPLES_PER_SEGMENT + 20);
        while (pathHistory.size() > maximumSamples) {
            pathHistory.removeLast();
        }
    }

    private List<Vec3> segmentPositions() {
        if (pathHistory.size() < 2) {
            Vec3 backward = Vec3.directionFromRotation(0.0F, currentPose().yaw()).scale(-SEGMENT_SPACING);
            List<Vec3> initialPositions = new ArrayList<>(slotCost());
            for (int segment = 1; segment <= slotCost(); segment++) {
                initialPositions.add(position().add(backward.scale(segment)));
            }
            return initialPositions;
        }
        List<Vec3> samples = new ArrayList<>(pathHistory);
        List<Vec3> result = new ArrayList<>(slotCost());
        for (int segment = 1; segment <= slotCost(); segment++) {
            result.add(positionAtDistance(samples, segment * SEGMENT_SPACING));
        }
        return result;
    }

    private static Vec3 positionAtDistance(List<Vec3> samples, double distance) {
        Vec3 previous = samples.get(0);
        double traversed = 0.0;
        for (int index = 1; index < samples.size(); index++) {
            Vec3 current = samples.get(index);
            double step = previous.distanceTo(current);
            if (step > 1.0E-5 && traversed + step >= distance) {
                return previous.lerp(current, (distance - traversed) / step);
            }
            traversed += step;
            previous = current;
        }
        return previous;
    }

    @Override
    public List<SummonRenderPart> renderParts() {
        List<SummonRenderPart> parts = new ArrayList<>(slotCost() + 1);
        parts.add(new SummonRenderPart(uuid(), type(), currentPose(),
                new SummonVisualState(false, SummonAnimation.NONE, 0, 0, 0.0F, 0.5F, 0.5F), 0));
        Vec3 previous = position();
        List<Vec3> segments = segmentPositions();
        for (int index = 0; index < segments.size(); index++) {
            Vec3 segment = segments.get(index);
            Vec3 facing = previous.subtract(segment);
            float yaw = facing.horizontalDistanceSqr() < 1.0E-8 ? currentPose().yaw()
                    : (float) Math.toDegrees(Math.atan2(-facing.x, facing.z));
            float pitch = facing.lengthSqr() < 1.0E-8 ? currentPose().pitch()
                    : (float) Math.toDegrees(Math.asin(-facing.normalize().y));
            UUID partId = new UUID(uuid().getMostSignificantBits(), uuid().getLeastSignificantBits() ^ (index + 1L));
            parts.add(new SummonRenderPart(partId, type(), new SummonPose(segment, yaw, pitch, 0.0F),
                    new SummonVisualState(false, SummonAnimation.NONE, 0, 0, 0.0F, 0.5F, 0.5F), index + 1));
            previous = segment;
        }
        return List.copyOf(parts);
    }

    private static final class AttackGoal extends SummonGoal<StardustDragonSummon> {
        private AttackGoal(StardustDragonSummon summon) {
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

    private static final class IdleGoal extends SummonGoal<StardustDragonSummon> {
        private IdleGoal(StardustDragonSummon summon) {
            super(summon);
        }

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public void tick() {
            summon.idle();
        }
    }
}
