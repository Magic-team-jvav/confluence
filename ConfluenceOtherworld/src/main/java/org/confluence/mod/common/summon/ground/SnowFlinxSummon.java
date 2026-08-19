package org.confluence.mod.common.summon.ground;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.summon.GroundMeleeSummon;
import org.confluence.mod.common.summon.SummonPose;
import org.confluence.mod.common.summon.SummonStats;

/// 保留 1.21 雪怪的跃击冷却和命中后突进。
public final class SnowFlinxSummon extends GroundMeleeSummon {
    public static final int SLOT_COST = 1;
    public static final float BASE_DAMAGE = 7.0F;
    private int leapCooldown = 20;
    private int leapForwardDelay;
    private int dashCooldown = 20;

    public SnowFlinxSummon(ServerPlayer owner, int slotCost, SummonStats stats, SummonPose initialPose) {
        super(Confluence.asResource("summon_snow_flinx"), owner, slotCost, stats, initialPose, 1.0, 1.0, 32.0, 0.56, 0.70);
    }

    @Override
    protected void beforeGroundGoalTick() {
        leapCooldown--;
        dashCooldown--;
        leapForwardDelay = Math.max(0, leapForwardDelay - 1);
    }

    @Override
    protected void afterPathAdvance(SummonPose previousPreviousPose, SummonPose previousPose, SummonPose currentPose) {
        if (target() != null) hurtTouchingTargets(collisionBox().inflate(0.75), 32.0, 1.0F);
    }

    @Override
    protected void moveInCombat(LivingEntity target) {
        Vec3 targetPosition = targetBasePosition();
        if (onGround() && leapCooldown <= 0 && position().distanceToSqr(targetPosition) < 25.0 && position().y < targetPosition.y + 2.0) {
            moveWithCollision(new Vec3(0.0, 0.8, 0.0));
            leapForwardDelay = 2;
            leapCooldown = 80 + owner().getRandom().nextInt(40);
            return;
        }
        if (leapForwardDelay == 1) {
            Vec3 direction = targetPosition.subtract(position()).multiply(1.0, 0.0, 1.0).normalize();
            moveWithCollision(new Vec3(direction.x * 0.7, velocity().y, direction.z * 0.7));
            return;
        }
        super.moveInCombat(target);
    }

    @Override
    protected void onSuccessfulHit(LivingEntity target) {
        if (dashCooldown <= 0) {
            moveWithCollision(velocity().add(targetPosition().subtract(position()).normalize()));
            dashCooldown = 20;
        }
    }
}
