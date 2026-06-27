package org.confluence.mod.util.entity.ai.goal.behavior.leaf;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.util.entity.ai.goal.behavior.BTNode;

public class TrackTargetAction extends BTNode {
    private final Mob mob;
    private final float speed;
    private final float maxAcceleration;

    private Vec3 currentVelocity;

    public TrackTargetAction(Mob mob, float speed) {
        this(mob, speed, 0.1f);
    }

    public TrackTargetAction(Mob mob, float speed, float maxAcceleration) {
        this.mob = mob;
        this.speed = speed;
        this.maxAcceleration = maxAcceleration;

        this.currentVelocity = Vec3.ZERO;
    }

    @Override
    public void start() {
        super.start();
        currentVelocity = Vec3.ZERO;
    }

    @Override
    public BTStatus execute() {
        LivingEntity target = this.mob.getTarget();
        if (target == null || !target.isAlive()) {
            return BTStatus.FAILURE;
        }

        Vec3 targetPos = target.position();
        Vec3 curPos = this.mob.position();

        // 计算期望速度方向
        Vec3 desiredDirection = targetPos.subtract(curPos).normalize();

        // 计算期望速度
        Vec3 desiredVelocity = desiredDirection.scale(speed);

        // 计算转向力（加速度）
        Vec3 steeringForce = desiredVelocity.subtract(currentVelocity);

        // 限制加速度
        double steeringMagnitude = steeringForce.length();
        if (steeringMagnitude > maxAcceleration) {
            steeringForce = steeringForce.normalize().scale(maxAcceleration);
        }

        // 应用加速度
        currentVelocity = currentVelocity.add(steeringForce);

        // 限制最大速度
        double currentSpeed = currentVelocity.length();
        if (currentSpeed > speed) {
            currentVelocity = currentVelocity.normalize().scale(speed);
        }

        // 应用惯性阻尼（模拟空气阻力）
        currentVelocity = currentVelocity.scale(0.95);

        // 设置实体的运动
        mob.setDeltaMovement(currentVelocity);

        // 使实体朝向运动方向
        if (currentVelocity.lengthSqr() > 0.01) {
            float yaw = (float) Math.toDegrees(Math.atan2(-currentVelocity.x, currentVelocity.z));
            mob.setYRot(yaw);
            mob.setYHeadRot(yaw);
        }

        return BTStatus.RUNNING;
    }

    @Override
    protected void cleanup() {
        super.cleanup();
        // 停止时逐渐减速
        currentVelocity = currentVelocity.scale(0.5);
        mob.setDeltaMovement(currentVelocity);
    }
}
