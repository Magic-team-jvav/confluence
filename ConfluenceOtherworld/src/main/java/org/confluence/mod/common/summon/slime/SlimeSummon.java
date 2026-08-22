package org.confluence.mod.common.summon.slime;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.summon.SummonTargetCache;
import org.confluence.mod.common.summon.*;

/// 史莱姆召唤物的运行实例。
public final class SlimeSummon extends PhysicalSummon {
    public static final int SLOT_COST = 1;
    public static final float BASE_DAMAGE = 5.0F;
    private static final double SEARCH_RANGE = 10.0;
    private static final double RETURN_FLIGHT_DISTANCE = 25.0;
    private static final double RETURN_FLIGHT_STOP_DISTANCE = 4.0;
    private static final double JUMP_FOLLOW_DISTANCE = 16.0;
    private static final SummonVisualState FLYING_VISUAL_STATE = new SummonVisualState(false, SummonAnimation.FLY, 0, 0, 0.0F, 1.0F, 1.0F);
    private int jumpDelay;
    private boolean returningByFlight;

    public SlimeSummon(ServerPlayer owner, int slotCost, SummonStats stats, SummonPose initialPose) {
        super(Confluence.asResource("slime_baby"), owner, slotCost, stats, initialPose, 0.5, 0.5);
        addGoal(1, new FluidGoal(this));
        addGoal(2, new AttackGoal(this));
        addGoal(3, new KeepJumpingGoal(this));
        addGoal(5, new ReturnToOwnerGoal(this));
        addGoal(6, new IdlePhysicsGoal(this));
    }

    @Override
    protected LivingEntity findTarget() {
        return SummonTargetCache.acquire(owner().serverLevel(), owner(), uuid(), position(), SEARCH_RANGE);
    }

    @Override
    public SummonVisualState visualState() {
        return returningByFlight ? FLYING_VISUAL_STATE : SummonVisualState.DEFAULT;
    }

    @Override
    protected void afterPathAdvance(SummonPose previousPreviousPose, SummonPose previousPose, SummonPose currentPose) {
        if (target() != null) hurtTouchingTargets(collisionBox().inflate(0.75), SEARCH_RANGE, 1.0F);
    }

    private boolean inFluid() {
        var fluid = owner().level().getFluidState(BlockPos.containing(position().add(0.0, 0.2, 0.0)));
        return fluid.is(FluidTags.WATER) || fluid.is(FluidTags.LAVA);
    }

    private void hopToward(Vec3 destination, boolean aggressive, double movementSpeed) {
        Vec3 velocity = velocity();
        Vec3 horizontal = new Vec3(destination.x - position().x, 0.0, destination.z - position().z);
        Vec3 direction = horizontal.lengthSqr() < 0.01 ? Vec3.ZERO : horizontal.normalize();
        if (!onGround()) {
            double acceleration = 0.02 * Math.min(1.0, movementSpeed);
            moveWithCollision(new Vec3(velocity.x * 0.91 + direction.x * acceleration, velocity.y * 0.98 - 0.08, velocity.z * 0.91 + direction.z * acceleration));
            return;
        }
        if (jumpDelay-- > 0) {
            moveWithCollision(new Vec3(velocity.x * 0.546, -0.08, velocity.z * 0.546));
            return;
        }
        double acceleration = movementSpeed > 1.0 ? movementSpeed * 0.1 : movementSpeed * movementSpeed * 0.1;
        LivingEntity target = target();
        boolean enhancedJump = aggressive && target != null && target.distanceToSqr(position()) < 64.0
                && target.getY() > position().y + 2.0;
        double jumpStrength = enhancedJump ? 1.0 : 0.5;
        moveWithCollision(new Vec3(velocity.x * 0.546 + direction.x * acceleration, jumpStrength, velocity.z * 0.546 + direction.z * acceleration));
        int delay = owner().getRandom().nextInt(10) + 5;
        jumpDelay = aggressive ? Math.max(1, delay / 3) : delay;
    }

    private static final class ReturnToOwnerGoal extends SummonGoal<SlimeSummon> {
        private ReturnToOwnerGoal(SlimeSummon summon) {super(summon);}

        @Override
        public boolean canUse() {
            if (summon.position().distanceTo(summon.owner().position()) > RETURN_FLIGHT_DISTANCE)
                summon.returningByFlight = true;
            return summon.returningByFlight;
        }

        @Override
        public boolean canContinueToUse() {return summon.position().distanceTo(summon.owner().position()) > RETURN_FLIGHT_STOP_DISTANCE;}

        @Override
        public void tick() {
            Vec3 direction = summon.owner().position().add(0.0, 3.0, 0.0).subtract(summon.position()).normalize();
            summon.moveWithoutCollision(direction);
        }

        @Override
        public void stop() {summon.returningByFlight = false;}
    }

    private static final class FluidGoal extends SummonGoal<SlimeSummon> {
        private FluidGoal(SlimeSummon summon) {super(summon);}

        @Override
        public boolean canUse() {return summon.inFluid();}

        @Override
        public void tick() {
            boolean aggressive = summon.target() != null && summon.position().distanceTo(summon.owner().position()) <= RETURN_FLIGHT_DISTANCE;
            Vec3 destination = aggressive ? summon.targetBasePosition()
                    : summon.position().add(Vec3.directionFromRotation(0.0F, summon.currentPose().yaw()));
            Vec3 horizontal = destination.subtract(summon.position()).multiply(1.0, 0.0, 1.0).normalize();
            Vec3 velocity = summon.velocity();
            double jump = summon.owner().getRandom().nextFloat() < 0.8F ? 0.04 : 0.0;
            double acceleration = 0.02 * (aggressive ? 1.0 : 0.84);
            summon.moveWithCollision(new Vec3(velocity.x * 0.8 + horizontal.x * acceleration, velocity.y * 0.8 + jump, velocity.z * 0.8 + horizontal.z * acceleration));
        }
    }

    private static final class AttackGoal extends SummonGoal<SlimeSummon> {
        private AttackGoal(SlimeSummon summon) {super(summon);}

        @Override
        public boolean canUse() {
            return summon.target() != null && summon.position().distanceTo(summon.owner().position()) <= RETURN_FLIGHT_DISTANCE;
        }

        @Override
        public void tick() {summon.hopToward(summon.targetBasePosition(), true, 1.05);}
    }

    private static final class KeepJumpingGoal extends SummonGoal<SlimeSummon> {
        private KeepJumpingGoal(SlimeSummon summon) {super(summon);}

        @Override
        public boolean canUse() {
            return summon.target() == null
                    && summon.position().distanceTo(summon.owner().position()) < JUMP_FOLLOW_DISTANCE;
        }

        @Override
        public void tick() {
            if (summon.position().distanceTo(summon.owner().position()) < RETURN_FLIGHT_STOP_DISTANCE) {
                summon.moveWithCollision(new Vec3(0.0, summon.velocity().y - 0.08, 0.0));
            } else {
                double distance = summon.position().distanceTo(summon.owner().position());
                summon.hopToward(summon.owner().position(), true, distance < 6.0 ? 0.56 : 1.05);
            }
        }
    }

    private static final class IdlePhysicsGoal extends SummonGoal<SlimeSummon> {
        private IdlePhysicsGoal(SlimeSummon summon) {super(summon);}

        @Override
        public boolean canUse() {return true;}

        @Override
        public void tick() {
            Vec3 velocity = summon.velocity();
            double horizontalDamping = summon.onGround() ? 0.546 : 0.91;
            summon.moveWithCollision(new Vec3(velocity.x * horizontalDamping, velocity.y * 0.98 - 0.08, velocity.z * horizontalDamping));
        }
    }
}
