package org.confluence.mod.common.summon.dragon;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.summon.SummonTargetCache;
import org.confluence.mod.common.summon.*;

import java.util.*;

/// 星尘龙召唤物的运行实例。
public final class StardustDragonSummon extends FlyingSummon {
    public static final int SLOT_COST = 1;
    public static final float BASE_DAMAGE = 1.0F;
    private static final double SEGMENT_SPACING = 0.55;
    private static final int SAMPLES_PER_SEGMENT = 10;
    private static final SummonVisualState VISUAL_STATE = new SummonVisualState(false, SummonAnimation.NONE, 0, 0, 0.0F, 0.5F, 0.5F);
    private final Deque<Vec3> pathHistory = new ArrayDeque<>();
    private final List<UUID> partIds = new ArrayList<>();
    private Vec3 movementTarget;
    private Vec3 dragonVelocity = Vec3.ZERO;
    private List<Vec3> cachedSegmentPositions;
    private int movementTargetTicks;
    private int bodyAttackCooldown;
    private float yawAcceleration;

    public StardustDragonSummon(ServerPlayer owner, int slotCost, SummonStats stats, SummonPose initialPose) {
        super(Confluence.asResource("stardust_dragon"), owner, slotCost, stats, initialPose, 0.5, 0.5);
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
        boolean hit = hurtTouchingTargets(AABB.ofSize(position().add(0.0, 0.25, 0.0), 0.5, 0.5, 0.5).inflate(0.75), 40.0, 1.0F);
        for (Vec3 segment : segmentPositions()) {
            hit |= hurtTouchingTargets(AABB.ofSize(segment.add(0.0, 0.25, 0.0), 0.5, 0.5, 0.5).inflate(0.75), 40.0, 1.0F);
        }
        if (hit) bodyAttackCooldown = 5;
    }

    @Override
    public boolean canMergeAdditionalSummon() {
        return true;
    }

    @Override
    public boolean tryMergeAdditionalSummon(int additionalSlots, SummonStats stats) {
        if (additionalSlots <= 0) {
            return false;
        }
        increaseSlotCost(additionalSlots);
        replaceStats(stats);
        return true;
    }

    private void attack(LivingEntity target) {
        if (movementTarget == null || --movementTargetTicks <= 0) {
            movementTarget = targetBounds().getCenter();
            movementTargetTicks = 5;
        }
        steerToward(movementTarget, 0.7F, 0.7);
    }

    private void idle() {
        if (movementTarget == null || --movementTargetTicks <= 0) {
            var random = owner().getRandom();
            movementTarget = owner().position().add((random.nextDouble() - 0.5) * 10.0, (random.nextDouble() - 0.5) * 10.0 + 2.0, (random.nextDouble() - 0.5) * 10.0);
            movementTargetTicks = 10;
        }
        steerToward(movementTarget, 0.3F, 0.3);
    }

    /// 按 1.21 龙类移动保留航向惯性与转向速度差异。
    private void steerToward(Vec3 destination, float turnSpeed, double movementSpeed) {
        Vec3 offset = destination.subtract(position());
        if (offset.lengthSqr() < 1.0E-6) {
            return;
        }
        Vec3 directionToTarget = offset.normalize();
        double yawRadians = currentPose().yaw() * Mth.DEG_TO_RAD;
        Vec3 accelerated = dragonVelocity;
        double horizontal = Math.max(1.0E-5, offset.horizontalDistance());
        double vertical = Mth.clamp(offset.y / horizontal, -movementSpeed, movementSpeed);
        accelerated = accelerated.add(0.0, vertical * 0.05, 0.0);
        Vec3 forwardDirection = new Vec3(Mth.sin((float) yawRadians), accelerated.y, Mth.cos((float) yawRadians)).normalize();
        float alignmentFactor = Math.max(((float) forwardDirection.dot(directionToTarget) + 0.5F) / 1.5F, 0.0F);
        float targetYaw = -(float) Mth.atan2(offset.x, offset.z) * Mth.RAD_TO_DEG;
        float yawError = Mth.clamp(Mth.wrapDegrees(targetYaw - currentPose().yaw()), -50.0F, 50.0F);
        float speedFactor = (float) dragonVelocity.horizontalDistance() + 1.0F;
        yawAcceleration = yawAcceleration * 0.8F + yawError * turnSpeed / Math.min(speedFactor, 40.0F) / speedFactor;
        accelerated = accelerated.add(0.0, yawAcceleration * 0.0002F, 0.0);
        float yaw = currentPose().yaw() + yawAcceleration * 0.1F;
        Vec3 forward = Vec3.directionFromRotation(0.0F, yaw).scale(0.06F * (alignmentFactor + 1.5F));
        Vec3 movement = accelerated.add(forward);
        if (position().distanceToSqr(owner().position()) >= 32.0 * 32.0) {
            Vec3 ownerDirection = owner().position().add(0.0, 1.8, 0.0).subtract(position());
            if (ownerDirection.lengthSqr() > 1.0E-8)
                movement = movement.add(ownerDirection.normalize().scale(0.02));
        }
        Vec3 movingDirection = movement.lengthSqr() > 1.0E-8 ? movement.normalize() : Vec3.ZERO;
        double directionMatch = 0.8 + 0.15 * (movingDirection.dot(forwardDirection) + 1.0) / 2.0;
        dragonVelocity = movement.multiply(directionMatch * movementSpeed, 0.91, directionMatch * movementSpeed);
        moveBy(movement, yaw, currentPose().pitch());
    }

    private void recordPath() {
        pathHistory.addFirst(position());
        cachedSegmentPositions = null;
        int maximumSamples = slotCost() * SAMPLES_PER_SEGMENT;
        while (pathHistory.size() > maximumSamples) {
            pathHistory.removeLast();
        }
    }

    private List<Vec3> segmentPositions() {
        if (cachedSegmentPositions != null) return cachedSegmentPositions;
        if (pathHistory.size() < 2) {
            Vec3 backward = Vec3.directionFromRotation(0.0F, currentPose().yaw()).scale(-SEGMENT_SPACING);
            List<Vec3> initialPositions = new ArrayList<>(slotCost());
            for (int segment = 1; segment <= slotCost(); segment++) {
                initialPositions.add(position().add(backward.scale(segment)));
            }
            return cachedSegmentPositions = initialPositions;
        }
        List<Vec3> result = new ArrayList<>(slotCost());
        Iterator<Vec3> samples = pathHistory.iterator();
        Vec3 previous = samples.next();
        double traversed = 0.0;
        int segment = 1;
        while (samples.hasNext() && segment <= slotCost()) {
            Vec3 current = samples.next();
            double step = previous.distanceTo(current);
            while (segment <= slotCost() && step > 1.0E-5 && traversed + step >= segment * SEGMENT_SPACING) {
                result.add(previous.lerp(current, (segment * SEGMENT_SPACING - traversed) / step));
                segment++;
            }
            traversed += step;
            previous = current;
        }
        while (segment <= slotCost()) {
            result.add(previous);
            segment++;
        }
        return cachedSegmentPositions = result;
    }

    @Override
    public void appendRenderParts(List<SummonRenderPart> output) {
        List<Vec3> segments = segmentPositions();
        SummonPose headPose = currentPose();
        if (!segments.isEmpty()) {
            Vec3 facing = position().subtract(segments.get(0));
            if (facing.lengthSqr() >= 1.0E-8) {
                headPose = new SummonPose(position(), currentPose().yaw(), (float) Math.toDegrees(Math.asin(-facing.normalize().y)), currentPose().roll());
            }
        }
        output.add(new SummonRenderPart(uuid(), type(), headPose, VISUAL_STATE, 0));
        Vec3 previous = position();
        for (int index = 0; index < segments.size(); index++) {
            Vec3 segment = segments.get(index);
            Vec3 next = index + 1 < segments.size() ? segments.get(index + 1) : segment;
            Vec3 facing = previous.subtract(next);
            float yaw = facing.horizontalDistanceSqr() < 1.0E-8 ? currentPose().yaw()
                    : (float) Math.toDegrees(Math.atan2(-facing.x, facing.z));
            float pitch = facing.lengthSqr() < 1.0E-8 ? currentPose().pitch()
                    : (float) Math.toDegrees(Math.asin(-facing.normalize().y));
            while (partIds.size() <= index) {
                long partIndex = partIds.size() + 1L;
                partIds.add(new UUID(uuid().getMostSignificantBits(), uuid().getLeastSignificantBits() ^ partIndex));
            }
            UUID partId = partIds.get(index);
            output.add(new SummonRenderPart(partId, type(), new SummonPose(segment, yaw, pitch, 0.0F), VISUAL_STATE, index + 1));
            previous = segment;
        }
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
