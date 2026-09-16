package org.confluence.mod.common.summon.terraprisma;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summon.SummonGoal;
import org.confluence.mod.common.summon.SummonPose;

final class TerraprismaChaseGoal extends SummonGoal<TerraprismaSummon> {
    private static final int RETURN_TICKS = 14;
    private static final double STAB_SPEED = 0.72;
    private static final double MAX_TURN = Math.toRadians(10.0);
    private Vec3 curveStart;
    private Vec3 curveControlOut;
    private Vec3 curveSide;
    private int curveTick = -1;
    private int curveSign = 1;
    private int nextCurveSign = 1;
    private Vec3 smoothedTarget;
    private LivingEntity trackedTarget;

    TerraprismaChaseGoal(TerraprismaSummon summon) {
        super(summon);
    }

    @Override
    public boolean canUse() {
        return summon.targetWithinOwnerRange();
    }

    @Override
    public void start() {
        trackedTarget = summon.target();
        curveTick = -1;
        smoothedTarget = summon.targetPosition();
    }

    @Override
    public void tick() {
        if (trackedTarget != summon.target()) start();
        if (curveTick >= 0) {
            followReturnCurve();
            return;
        }
        Vec3 target = updateTarget();
        Vec3 offset = target.subtract(summon.eyePosition());
        if (offset.lengthSqr() < 1.0E-8) return;
        Vec3 current = summon.velocity().lengthSqr() < 1.0E-6
                ? Vec3.directionFromRotation(summon.currentPose().pitch(), summon.currentPose().yaw()).normalize()
                : summon.velocity().normalize();
        // 近距离时缩小转弯半径，避免固定速度和固定转角形成永远追不上的圆轨道。
        double turn = Math.min(Math.PI / 2.0, Math.max(MAX_TURN, Math.atan2(STAB_SPEED, offset.length())));
        Vec3 direction = rotateToward(current, offset.normalize(), turn);
        Vec3 movement = direction.scale(STAB_SPEED);
        Vec3 nextPosition = summon.position().add(movement);
        Vec3 remaining = target.subtract(summon.eyePosition().add(movement));
        summon.moveTo(summon.aimAt(nextPosition, direction));
        if (offset.lengthSqr() <= 9.0 && offset.dot(direction) >= 0.0 && remaining.dot(direction) < 0.0)
            beginReturnCurve(nextPosition, direction);
    }

    private void followReturnCurve() {
        Vec3 target = updateTarget();
        Vec3 controlReturn = target.add(curveSide.scale(3.5)).add(0.0, curveSign * 1.75, 0.0);
        Vec3 throughDirection = target.subtract(controlReturn).normalize();
        Vec3 curveEnd = target.add(throughDirection.scale(1.75));
        double progress = (curveTick + 1.0) / RETURN_TICKS;
        Vec3 position = cubic(curveStart, curveControlOut, controlReturn, curveEnd, progress);
        Vec3 tangent = cubicDerivative(curveStart, curveControlOut, controlReturn, curveEnd, progress);
        if (tangent.lengthSqr() < 1.0E-8) tangent = curveEnd.subtract(position);
        SummonPose aimed = summon.aimAt(position, tangent);
        float roll = (float) (Math.sin(progress * Math.PI * 2.0) * 35.0 * curveSign);
        summon.moveTo(new SummonPose(position, aimed.yaw(), aimed.pitch(), roll));
        if (++curveTick >= RETURN_TICKS) {
            beginReturnCurve(position, tangent.normalize());
        }
    }

    private void beginReturnCurve(Vec3 start, Vec3 outgoing) {
        Vec3 side = outgoing.cross(new Vec3(0.0, 1.0, 0.0));
        if (side.lengthSqr() < 1.0E-8) side = outgoing.cross(new Vec3(1.0, 0.0, 0.0));
        curveStart = start;
        curveControlOut = start.add(outgoing.scale(3.5));
        curveSign = nextCurveSign;
        curveSide = side.normalize().scale(curveSign);
        curveTick = 0;
        nextCurveSign = -nextCurveSign;
    }

    @Override
    public void stop() {
        curveTick = -1;
        curveSign = 1;
        nextCurveSign = 1;
        smoothedTarget = null;
        trackedTarget = null;
    }

    private Vec3 updateTarget() {
        Vec3 target = summon.targetPosition();
        smoothedTarget = smoothedTarget == null ? target : smoothedTarget.lerp(target, 0.35);
        return smoothedTarget;
    }

    private static Vec3 cubic(Vec3 start, Vec3 first, Vec3 second, Vec3 end, double progress) {
        double inverse = 1.0 - progress;
        return start.scale(inverse * inverse * inverse)
                .add(first.scale(3.0 * inverse * inverse * progress))
                .add(second.scale(3.0 * inverse * progress * progress))
                .add(end.scale(progress * progress * progress));
    }

    private static Vec3 cubicDerivative(Vec3 start, Vec3 first, Vec3 second, Vec3 end, double progress) {
        double inverse = 1.0 - progress;
        return first.subtract(start).scale(3.0 * inverse * inverse)
                .add(second.subtract(first).scale(6.0 * inverse * progress))
                .add(end.subtract(second).scale(3.0 * progress * progress));
    }

    private static Vec3 rotateToward(Vec3 current, Vec3 target, double maximumAngle) {
        double angle = Math.acos(Math.max(-1.0, Math.min(1.0, current.dot(target))));
        if (angle <= maximumAngle) return target;
        Vec3 axis = current.cross(target);
        if (axis.lengthSqr() < 1.0E-8) {
            axis = current.cross(new Vec3(0.0, 1.0, 0.0));
            if (axis.lengthSqr() < 1.0E-8) axis = current.cross(new Vec3(1.0, 0.0, 0.0));
        }
        axis = axis.normalize();
        return current.scale(Math.cos(maximumAngle)).add(axis.cross(current).scale(Math.sin(maximumAngle))).normalize();
    }
}
