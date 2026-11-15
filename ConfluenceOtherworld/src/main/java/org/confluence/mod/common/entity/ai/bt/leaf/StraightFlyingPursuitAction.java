package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.monster.BaseMonster;

/// 让飞鱼 AI 生物以有限转向速度持续直追目标。
///
/// 本动作没有冲刺周期、掠过抬升或强制后撤阶段。速度通过加速度逐步接近上限，当前飞行
/// 方向每刻最多转过指定角度，因此高速个体仍会自然形成较大的转弯半径。
public final class StraightFlyingPursuitAction extends BTNode {
    private final BaseMonster mob;
    private final double friction;
    private final double maxSpeed;
    private final double acceleration;
    private final double turnRadians;

    public StraightFlyingPursuitAction(BaseMonster mob, double friction, double maxSpeed, double acceleration, double turnSpeedDegrees) {
        if (friction < 0.0 || friction > 1.0 || maxSpeed <= 0.0 || acceleration <= 0.0 || turnSpeedDegrees <= 0.0 || turnSpeedDegrees > 180.0) {
            throw new IllegalArgumentException("Straight pursuit parameters are outside their valid ranges");
        }
        this.mob = mob;
        this.friction = friction;
        this.maxSpeed = maxSpeed;
        this.acceleration = acceleration;
        this.turnRadians = Math.toRadians(turnSpeedDegrees);
    }

    @Override
    public BTStatus execute() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) return BTStatus.FAILURE;

        Vec3 toTarget = target.getEyePosition().subtract(mob.getEyePosition());
        if (toTarget.lengthSqr() < 1.0E-7) return BTStatus.RUNNING;

        Vec3 velocity = mob.getDeltaMovement().scale(friction);
        Vec3 currentDirection = velocity.lengthSqr() < 1.0E-7 ? mob.getForward() : velocity.normalize();
        Vec3 targetDirection = toTarget.normalize();
        double angle = Math.acos(Mth.clamp(currentDirection.dot(targetDirection), -1.0, 1.0));
        Vec3 direction = angle <= turnRadians ? targetDirection : currentDirection.lerp(targetDirection, turnRadians / angle).normalize();
        if (direction.lengthSqr() < 1.0E-7) direction = targetDirection;

        double speed = Math.min(maxSpeed, velocity.length() + acceleration);
        mob.faceCombatDirection(direction, (float) Math.toDegrees(turnRadians), 85.0F);
        mob.setDeltaMovement(direction.scale(speed));
        mob.hasImpulse = true;
        return BTStatus.RUNNING;
    }
}
