package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/// 复现 1.21 飞行怪物的转向冲刺过程。
///
/// 实体先在待机阶段按指定速度朝向目标。进入触发角后持续加速；当目标离开可转向角时，
/// 实体保留冲刺方向、逐渐减速并向上抬升一段时间。贴身命中后则先沿当前正面远离目标，
/// 拉开足够距离后才重新对准。转向速度、触发角和冲刺中的最大转向角是三个独立参数，
/// 不能互相代替。
///
/// 本动作只保留原 {@code DashGoal} 自己的贴身攻击；1.21 怪物公共接触伤害由飞行实体
/// 独立计时，不能绑在某个行为树节点上，否则切换动作时会错误暂停。
public final class SteeringDashAction extends BTNode {
    private static final int POINT_BLANK_COOLDOWN = 30;

    private final PathfinderMob mob;
    private final double friction;
    private final double maxSpeed;
    private final double acceleration;
    private final float turnSpeedDegrees;
    private final double triggerAngle;
    private final double steeringAngle;
    private final int backDuration;

    private Phase phase = Phase.IDLE;
    private int backTicks;
    private int pointBlankCooldown;
    private Vec3 lastDirection = Vec3.ZERO;

    public SteeringDashAction(PathfinderMob mob, double friction, double maxSpeed, double acceleration, double turnSpeedDegrees, double triggerAngleDegrees, double steeringAngleDegrees, int backDuration) {
        this.mob = mob;
        this.friction = friction;
        this.maxSpeed = maxSpeed;
        this.acceleration = acceleration;
        this.turnSpeedDegrees = (float) turnSpeedDegrees;
        this.triggerAngle = Math.toRadians(triggerAngleDegrees);
        this.steeringAngle = Math.toRadians(steeringAngleDegrees);
        this.backDuration = backDuration;
    }

    @Override
    public BTStatus execute() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) {
            return BTStatus.FAILURE;
        }

        pointBlankCooldown--;
        mob.hasImpulse = true;

        if (mob.hurtTime > 0) {
            phase = Phase.IDLE;
        }

        double distance = mob.position().distanceTo(target.getEyePosition());
        if (distance < 0.5 && mob.swingTime == 0 && pointBlankCooldown <= 0) {
            mob.doHurtTarget(target);
            mob.swing(InteractionHand.MAIN_HAND);
            phase = Phase.AWAY;
            pointBlankCooldown = POINT_BLANK_COOLDOWN;
            return BTStatus.RUNNING;
        }

        if (phase == Phase.AWAY) {
            mob.addDeltaMovement(mob.getForward().normalize().scale(0.1));
            if (distance > 5.0) {
                phase = Phase.IDLE;
            }
        }

        if (phase == Phase.DASHING_BACK) {
            tickDashingBack();
            return BTStatus.RUNNING;
        }
        if (phase == Phase.IDLE) {
            tickIdle(target);
            return BTStatus.RUNNING;
        }

        tickDash(target);
        return BTStatus.RUNNING;
    }

    private void tickIdle(LivingEntity target) {
        lookAtTarget(target);
        if (mob.hurtTime > 0) {
            return;
        }

        slowLastDirection();
        if (mob.getDeltaMovement().length() <= 0.1) {
            mob.setDeltaMovement(mob.getForward().normalize().scale(0.1));
        }
        if (angleToTarget(target) < triggerAngle) {
            phase = Phase.DASHING;
        }
    }

    private void tickDash(LivingEntity target) {
        if (angleToTarget(target) >= steeringAngle) {
            backTicks = 0;
            phase = Phase.DASHING_BACK;
            lastDirection = mob.getDeltaMovement();
            return;
        }

        lookAtTarget(target);
        Vec3 velocity = mob.getDeltaMovement();
        double speed = Math.min(maxSpeed, velocity.add(velocity.normalize().scale(acceleration)).length());
        if (speed < 0.1) {
            mob.setDeltaMovement(mob.getForward().normalize().scale(0.1));
            return;
        }

        Vec3 forward = mob.getForward().normalize();
        Vec3 towardTarget = target.getEyePosition().subtract(mob.position()).normalize();
        mob.setDeltaMovement(forward.add(towardTarget).normalize().scale(speed));
    }

    private void tickDashingBack() {
        backTicks--;
        slowLastDirection();
        mob.addDeltaMovement(new Vec3(0.0, 0.05, 0.0));
        if (backTicks <= -backDuration) {
            phase = Phase.IDLE;
        }
    }

    private void lookAtTarget(LivingEntity target) {
        mob.getLookControl().setLookAt(target, 5.0F, 85.0F);
        mob.lookAt(target, turnSpeedDegrees, 85.0F);
    }

    private void slowLastDirection() {
        lastDirection = lastDirection.scale(friction);
        mob.setDeltaMovement(lastDirection);
    }

    private double angleToTarget(LivingEntity target) {
        Vec3 targetDirection = target.position().subtract(mob.position());
        Vec3 forward = mob.getForward();
        if (targetDirection.lengthSqr() < 1.0E-6 || forward.lengthSqr() < 1.0E-6) {
            return 0.0;
        }
        double dot = targetDirection.normalize().dot(forward.normalize());
        return Math.acos(Math.max(-1.0, Math.min(1.0, dot)));
    }

    /// 让包含本动作的复合攻击在冲刺阶段之外也继续推进公共碰撞攻击计时。
    private enum Phase {
        IDLE,
        DASHING,
        DASHING_BACK,
        AWAY
    }
}
