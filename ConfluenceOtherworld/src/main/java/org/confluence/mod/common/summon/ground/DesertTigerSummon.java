package org.confluence.mod.common.summon.ground;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.summon.SummonTargetCache;
import org.confluence.mod.common.summon.*;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class DesertTigerSummon extends PhysicalSummon {
    public static final ResourceLocation TIER1 = Confluence.asResource("desert_tiger_tier1");
    public static final ResourceLocation TIER2 = Confluence.asResource("desert_tiger_tier2");
    public static final ResourceLocation TIER3 = Confluence.asResource("desert_tiger_tier3");
    public static final int SLOT_COST = 1;
    public static final float BASE_DAMAGE = 41.0F;
    private final Set<UUID> pounced = new HashSet<>();
    private LivingEntity pounceTarget;
    private int pounceCooldown = 120;
    private boolean pouncing;
    private int attackAnimationTicks;
    private int pounceTicks;

    @Override
    protected double ownerRecoveryDistanceSqr() {
        return 80.0 * 80.0;
    }

    public DesertTigerSummon(ServerPlayer owner, int slotCost, SummonStats stats, SummonPose pose) {
        super(Confluence.asResource("desert_tiger"), owner, slotCost, stats, pose, 1.0, 0.8);
        addGoal(1, new SummonGoal<DesertTigerSummon>(this) {
            @Override
            public boolean canUse() {
                return true;
            }

            @Override
            public void tick() {
                moveTiger();
            }
        });
    }

    @Override
    protected LivingEntity findTarget() {
        return SummonTargetCache.acquire(owner().serverLevel(), owner(), uuid(), owner().position(), 50.0, false);
    }

    @Override
    public boolean canMergeAdditionalSummon() {
        return true;
    }

    @Override
    public boolean tryMergeAdditionalSummon(int additionalSlots, SummonStats stats) {
        if (additionalSlots <= 0) return false;
        increaseSlotCost(additionalSlots);
        replaceStats(stats);
        return true;
    }

    private void moveTiger() {
        if (attackAnimationTicks > 0) attackAnimationTicks--;
        pouncing = false;
        if (pounceCooldown > 0) pounceCooldown--;
        if (pounceTarget != null && (--pounceTicks <= 0 || !withinPounceRange(pounceTarget)
                || !SummonTargetCache.isValidTarget(owner(), pounceTarget, Double.MAX_VALUE, true)))
            pounceTarget = null;
        if (pounceTarget == null && pounceCooldown == 0) {
            LivingEntity firstTarget = target() != null && withinPounceRange(target()) ? target()
                    : owner().level().getEntitiesOfClass(LivingEntity.class,
                            owner().getBoundingBox().inflate(50.0, 25.0, 50.0), candidate -> withinPounceRange(candidate)
                                    && SummonTargetCache.isValidTarget(owner(), candidate, Double.MAX_VALUE, false))
                    .stream().min(Comparator.comparingDouble(candidate -> candidate.distanceToSqr(position()))).orElse(null);
            if (firstTarget != null) {
                pounced.clear();
                pounceTarget = firstTarget;
                pounceTicks = 100;
                pounceCooldown = slotCost() >= 7 ? 80 : slotCost() >= 4 ? 100 : 120;
            }
        }
        if (pounceTarget != null) {
            pouncing = true;
            Vec3 destination = resolveActualTarget(pounceTarget).getBoundingBox().getCenter();
            Vec3 offset = destination.subtract(position().add(0.0, 0.4, 0.0));
            moveWithoutCollision(offset.normalize().scale(Math.min(0.45, offset.length())));
        } else if (position().distanceToSqr(owner().position()) > 1600.0) {
            moveWithoutCollision(owner().position().add(0.0, 1.0, 0.0).subtract(position()).normalize().scale(0.35));
        } else if (target() != null) {
            moveOnGround(targetBasePosition());
        } else if (position().distanceToSqr(owner().position()) > 9.0) {
            moveOnGround(owner().position());
        } else {
            applyIdlePhysics();
        }
    }

    private void moveOnGround(Vec3 destination) {
        Vec3 waypoint = groundWaypoint(destination);
        Vec3 offset = waypoint.subtract(position()).multiply(1.0, 0.0, 1.0);
        double speed = 0.22 + Math.min(0.08, (slotCost() - 1) * 0.01);
        Vec3 movement = velocity().multiply(0.6, 0.0, 0.6)
                .add(offset.normalize().scale(Math.min(speed, offset.length()) * 0.4));
        if (movement.lengthSqr() > speed * speed) movement = movement.normalize().scale(speed);
        double vertical = onGround() && waypoint.y > position().y + 0.35 ? 0.42 : velocity().y * 0.98 - 0.08;
        moveWithCollision(new Vec3(movement.x, vertical, movement.z));
    }

    private boolean withinPounceRange(LivingEntity entity) {
        Vec3 offset = entity.position().subtract(owner().position());
        return Math.abs(offset.x) <= 50.0 && Math.abs(offset.z) <= 50.0 && Math.abs(offset.y) <= 25.0;
    }

    @Override
    protected void afterPathAdvance(SummonPose older, SummonPose previous, SummonPose current) {
        float multiplier = 1.0F + 0.4F * (slotCost() - 1) + (pouncing ? 0.5F : 0.0F);
        Vec3 movement = current.position().subtract(previous.position());
        int steps = Math.max(1, (int) Math.ceil(movement.length() / 0.25));
        Set<UUID> hitEntities = new HashSet<>();
        LivingEntity attackedTarget = pounceTarget;
        boolean[] hitPounceTarget = {false};
        for (int i = 0; i <= steps; i++) {
            Vec3 point = previous.position().add(movement.scale((double) i / steps)).add(0.0, 0.4, 0.0);
            if (hurtTouchingTargets(AABB.ofSize(point, 1.0, 0.8, 1.0), 75.0, multiplier, hitEntities,
                    hit -> {if (hit == attackedTarget) hitPounceTarget[0] = true;}))
                attackAnimationTicks = 10;
        }
        if (hitPounceTarget[0]) {
            pounced.add(attackedTarget.getUUID());
            pounceTarget = pounced.size() >= slotCost() + 1 ? null : owner().level().getEntitiesOfClass(LivingEntity.class,
                            owner().getBoundingBox().inflate(50.0, 25.0, 50.0), candidate -> !pounced.contains(candidate.getUUID()) && withinPounceRange(candidate)
                                    && SummonTargetCache.isValidTarget(owner(), candidate, Double.MAX_VALUE, false))
                    .stream().min(Comparator.comparingDouble(candidate -> candidate.distanceToSqr(position()))).orElse(null);
            pounceTicks = 100;
            resetGroundPath(10);
        }
    }

    @Override
    public int confluence$getImmunityDuration(DamageSource source) {
        return 3;
    }

    @Override
    public SummonVisualState visualState() {
        SummonAnimation animation = pouncing ? SummonAnimation.SPIN_X : attackAnimationTicks > 0 ? SummonAnimation.MELEE_ATTACK : SummonAnimation.NONE;
        return new SummonVisualState(false, animation, 0, 0, 0, 1, 1);
    }

    @Override
    public void appendRenderParts(java.util.List<SummonRenderPart> output) {
        var appearance = slotCost() >= 7 ? TIER3 : slotCost() >= 4 ? TIER2 : TIER1;
        output.add(new SummonRenderPart(uuid(), appearance, currentPose(), visualState(), order()));
    }
}
