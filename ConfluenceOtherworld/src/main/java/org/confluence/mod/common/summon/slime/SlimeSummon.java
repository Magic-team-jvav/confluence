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
///
/// <p>行为保留 1.21 侧的连续弹跳、攻击时强化跳跃、流体漂浮，以及距离主人过远时飞回主人附近。
/// 新架构只负责把真实实体 AI 改成玩家容器驱动，不能改变史莱姆召唤物可观察到的移动节奏。</p>
public final class SlimeSummon extends PhysicalSummon {
    public static final int SLOT_COST = 1;
    public static final float BASE_DAMAGE = 5.0F;
    private static final double SEARCH_RANGE = 10.0;
    private static final double RETURN_FLIGHT_DISTANCE = 25.0;
    private static final double RETURN_FLIGHT_STOP_DISTANCE = 4.0;
    private static final double JUMP_FOLLOW_DISTANCE = 16.0;
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
    protected LivingEntity findTarget() {return SummonTargetCache.acquire(owner().serverLevel(), owner(), uuid(), position(), SEARCH_RANGE);}

    @Override
    public SummonVisualState visualState() {
        return returningByFlight
                ? new SummonVisualState(false, SummonAnimation.FLY, 0, 0, 0.0F, 1.0F, 1.0F)
                : SummonVisualState.DEFAULT;
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
        if (!onGround()) {
            moveWithCollision(new Vec3(velocity.x * 0.98, velocity.y - 0.08, velocity.z * 0.98));
            return;
        }
        if (jumpDelay-- > 0) {
            moveWithCollision(new Vec3(0.0, -0.08, 0.0));
            return;
        }
        Vec3 horizontal = new Vec3(destination.x - position().x, 0.0, destination.z - position().z);
        double speed = horizontal.lengthSqr() < 0.01 ? 0.0 : movementSpeed;
        Vec3 direction = speed == 0.0 ? Vec3.ZERO : horizontal.normalize().scale(speed);
        boolean enhancedJump = aggressive && destination.distanceTo(position()) < 8.0
                && destination.y > position().y + 2.0;
        double jumpStrength = enhancedJump ? 1.0 : 0.5;
        moveWithCollision(new Vec3(direction.x, jumpStrength, direction.z));
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
            Vec3 destination = summon.target() == null ? summon.owner().position() : summon.targetBasePosition();
            Vec3 direction = destination.subtract(summon.position()).normalize().scale(0.84);
            double vertical = summon.owner().getRandom().nextFloat() < 0.8F ? 0.3 : direction.y;
            summon.moveWithCollision(new Vec3(direction.x, vertical, direction.z));
        }
    }

    private static final class AttackGoal extends SummonGoal<SlimeSummon> {
        private AttackGoal(SlimeSummon summon) {super(summon);}

        @Override
        public boolean canUse() {return summon.target() != null && summon.position().distanceTo(summon.owner().position()) <= RETURN_FLIGHT_DISTANCE;}

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
            summon.moveWithCollision(new Vec3(summon.velocity().x * 0.6, summon.velocity().y - 0.08,
                    summon.velocity().z * 0.6));
        }
    }
}
