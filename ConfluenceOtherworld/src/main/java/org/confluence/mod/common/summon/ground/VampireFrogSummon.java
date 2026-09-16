package org.confluence.mod.common.summon.ground;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.summon.SummonTargetCache;
import org.confluence.mod.common.summon.*;
import org.confluence.mod.mixed.Immunity;

public final class VampireFrogSummon extends PhysicalSummon {
    public static final int SLOT_COST = 1;
    public static final float BASE_DAMAGE = 11.0F;
    private static final Immunity SHARED_IMMUNITY = new Immunity() {
        @Override
        public Type confluence$getImmunityType() {
            return Type.STATIC;
        }

        @Override
        public int confluence$getImmunityDuration(DamageSource source) {
            return 3;
        }
    };
    private int jumpDelay;
    private int attackTicks;
    private boolean returning;

    public VampireFrogSummon(ServerPlayer owner, int slotCost, SummonStats stats, SummonPose pose) {
        super(Confluence.asResource("vampire_frog"), owner, slotCost, stats, pose, 0.9, 0.7);
        addGoal(1, new SummonGoal<VampireFrogSummon>(this) {
            @Override
            public boolean canUse() {
                return true;
            }

            @Override
            public void tick() {
                moveFrog();
            }
        });
    }

    @Override
    protected LivingEntity findTarget() {
        return SummonTargetCache.acquire(owner().serverLevel(), owner(), uuid(), owner().position(), 50.0);
    }

    private void moveFrog() {
        if (attackTicks > 0) attackTicks--;
        Vec3 ownerPosition = owner().position();
        double ownerDistance = position().distanceToSqr(ownerPosition);
        if (ownerDistance > 1600.0) returning = true;
        if (returning && ownerDistance < 16.0) returning = false;
        if (returning) {
            Vec3 offset = ownerPosition.add(0.0, 1.0, 0.0).subtract(position());
            moveWithoutCollision(velocity().lerp(offset.normalize().scale(Math.min(1.0, offset.length() * 0.15)), 0.2));
            return;
        }
        Vec3 destination = target() == null ? ownerPosition : targetBasePosition();
        Vec3 offset = destination.subtract(position());
        if (!owner().level().getFluidState(BlockPos.containing(position().add(0.0, 0.2, 0.0))).isEmpty()) {
            Vec3 desired = offset.normalize().scale(target() == null ? 0.2 : 0.4);
            moveWithCollision(velocity().lerp(desired, 0.15));
        } else if (!onGround()) {
            moveWithCollision(new Vec3(velocity().x * 0.98, velocity().y * 0.98 - 0.08, velocity().z * 0.98));
        } else if (jumpDelay-- <= 0 && (target() != null || offset.horizontalDistanceSqr() > 9.0)) {
            Vec3 hop = groundWaypoint(destination).subtract(position());
            Vec3 direction = hop.multiply(1.0, 0.0, 1.0).normalize();
            jumpDelay = target() == null ? 12 : 4;
            moveWithCollision(new Vec3(direction.x * 0.35, hop.y > 0.35 ? 0.8 : 0.5, direction.z * 0.35));
        } else {
            applyIdlePhysics();
        }
    }

    @Override
    protected void afterPathAdvance(SummonPose older, SummonPose previous, SummonPose current) {
        if (returning || target() == null) return;
        if (attackTicks == 0 && collisionBox().inflate(0.2).intersects(targetBounds()))
            attackTicks = 20;
        if (attackTicks > 0) hurtTouchingTargets(collisionBox(), 50.0, 1.0F);
    }

    @Override
    protected Immunity damageImmunity() {
        return SHARED_IMMUNITY;
    }

    @Override
    public SummonVisualState visualState() {
        if (returning)
            return new SummonVisualState(true, SummonAnimation.FLY, 0, 0, 0.0F, 1.0F, 1.0F);
        return attackTicks > 0 ? new SummonVisualState(false, SummonAnimation.MELEE_ATTACK, 20 - attackTicks, 20, 0.0F, 1.0F, 1.0F) : SummonVisualState.DEFAULT;
    }
}
