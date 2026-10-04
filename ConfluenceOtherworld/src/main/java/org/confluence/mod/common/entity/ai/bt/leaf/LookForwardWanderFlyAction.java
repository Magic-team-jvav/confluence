package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.util.AirRandomPos;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.monster.BaseMonster;

/// 让飞行生物沿当前朝向附近选择目标并平滑巡航。
public final class LookForwardWanderFlyAction extends BTNode {
    private final BaseMonster mob;
    private final double maxSpeed;
    private final float offsetY;
    private final boolean stopsForTarget;
    private double anchorY = Double.NaN;
    private int locateCount;
    private int ticksLeft;
    private Vec3 targetPos;

    public LookForwardWanderFlyAction(BaseMonster mob, double maxSpeed, float offsetY) {
        this(mob, maxSpeed, offsetY, true);
    }

    public LookForwardWanderFlyAction(BaseMonster mob, double maxSpeed, float offsetY, boolean stopsForTarget) {
        if (maxSpeed <= 0.0) {
            throw new IllegalArgumentException("Flying wander speed must be positive");
        }
        this.mob = mob;
        this.maxSpeed = maxSpeed;
        this.offsetY = offsetY;
        this.stopsForTarget = stopsForTarget;
    }

    @Override
    public void start() {
        locateCount++;
        mob.setDeltaMovement(mob.getDeltaMovement().with(Direction.Axis.Y, 0.0));
        if (Double.isNaN(anchorY)) anchorY = mob.getY();

        Vec3 forward = mob.getLookAngle();
        Vec3 forwardOffset = forward.lengthSqr() < 1.0E-7
                ? new Vec3(0.0, 0.0, 10.0)
                : forward.normalize().scale(10.0);
        Vec3 forwardTarget = mob.position().add(forwardOffset);
        targetPos = AirRandomPos.getPosTowards(mob, 10, 5, 1, forwardTarget, Mth.PI * 0.1F);
        if (targetPos == null) {
            double x = mob.getRandom().nextDouble() * 10.0 - 5.0;
            double z = mob.getRandom().nextDouble() * 10.0 - 5.0;
            Vec3 horizontal = new Vec3(x, 0.0, z);
            if (horizontal.lengthSqr() < 1.0E-8) horizontal = new Vec3(1.0, 0.0, 0.0);
            targetPos = horizontal.normalize().scale(15.0).add(mob.position()).with(Direction.Axis.Y, anchorY + getOffsetY() + 5.0);
        }
        ticksLeft = 30;
    }

    @Override
    public BTStatus execute() {
        if ((stopsForTarget && mob.getTarget() != null) || targetPos == null || ticksLeft <= 0 || mob.position().distanceToSqr(targetPos) <= 2.25) {
            return BTStatus.SUCCESS;
        }

        Vec3 movement = mob.getDeltaMovement();
        Vec3 acceleration = mob.position().vectorTo(targetPos).normalize().multiply(0.08, 0.03, 0.08);
        Vec3 nextMovement = movement.add(acceleration);
        if (angleBetween(acceleration, movement) > 15.0 || nextMovement.length() < maxSpeed) {
            movement = nextMovement;
            mob.setDeltaMovement(movement);
            mob.hasImpulse = true;
        }

        double speed = movement.length();
        if (speed > maxSpeed)
            mob.setDeltaMovement(movement.normalize().scale(maxSpeed + (speed - maxSpeed) * 0.5));
        mob.faceCombatMovement(10.0F, 30.0F);
        ticksLeft--;
        return BTStatus.RUNNING;
    }

    private float getOffsetY() {
        float radians = Mth.TWO_PI * (locateCount % 10.0F) / 10.0F;
        return 2.57F * Mth.cos(radians) - 3.0F + offsetY;
    }

    private static double angleBetween(Vec3 first, Vec3 second) {
        double lengths = first.length() * second.length();
        if (lengths < 1.0E-8) return 0.0;
        return Math.toDegrees(Math.acos(Mth.clamp(first.dot(second) / lengths, -1.0, 1.0)));
    }
}
