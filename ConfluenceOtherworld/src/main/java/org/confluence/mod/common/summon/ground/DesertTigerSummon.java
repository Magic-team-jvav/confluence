package org.confluence.mod.common.summon.ground;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.summon.SummonTargetCache;
import org.confluence.mod.common.summon.PhysicalSummon;
import org.confluence.mod.common.summon.SummonGoal;
import org.confluence.mod.common.summon.SummonPose;
import org.confluence.mod.common.summon.SummonStats;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class DesertTigerSummon extends PhysicalSummon {
    public static final int SLOT_COST = 1;
    public static final float BASE_DAMAGE = 41.0F;
    private final Set<UUID> pounced = new HashSet<>();
    private LivingEntity pounceTarget;
    private int pounceCooldown = 120;
    private boolean pouncing;

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
        pouncing = false;
        if (pounceCooldown > 0) pounceCooldown--;
        if (pounceTarget != null && (!withinPounceRange(pounceTarget)
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
                pounceCooldown = slotCost() >= 7 ? 80 : slotCost() >= 4 ? 100 : 120;
            }
        }
        if (pounceTarget != null) {
            pouncing = true;
            Vec3 destination = resolveActualTarget(pounceTarget).getBoundingBox().getCenter();
            Vec3 offset = destination.subtract(position().add(0.0, 0.4, 0.0));
            moveWithoutCollision(offset.normalize().scale(Math.min(1.4, offset.length())));
            if (offset.lengthSqr() < 2.0) {
                pounced.add(pounceTarget.getUUID());
                pounceTarget = pounced.size() >= slotCost() + 1 ? null : owner().level().getEntitiesOfClass(LivingEntity.class,
                                owner().getBoundingBox().inflate(50.0, 25.0, 50.0), candidate -> !pounced.contains(candidate.getUUID()) && withinPounceRange(candidate)
                                        && SummonTargetCache.isValidTarget(owner(), candidate, Double.MAX_VALUE, false))
                        .stream().min(Comparator.comparingDouble(candidate -> candidate.distanceToSqr(position()))).orElse(null);
            }
        } else if (position().distanceToSqr(owner().position()) > 1600.0) {
            moveWithoutCollision(owner().position().add(0.0, 1.0, 0.0).subtract(position()).normalize());
        } else if (target() != null) {
            navigateGround(targetBasePosition(), 0.25 + Math.min(0.3, (slotCost() - 1) * 0.03), 0.65);
        } else if (position().distanceToSqr(owner().position()) > 9.0) {
            navigateGround(owner().position(), 0.25, 0.65);
        } else {
            applyIdlePhysics();
        }
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
        for (int i = 0; i <= steps; i++) {
            Vec3 point = previous.position().add(movement.scale((double) i / steps)).add(0.0, 0.4, 0.0);
            hurtTouchingTargets(AABB.ofSize(point, 1.0, 0.8, 1.0), 75.0, multiplier);
        }
    }

    @Override
    public int confluence$getImmunityDuration(DamageSource source) {
        return 3;
    }
}
