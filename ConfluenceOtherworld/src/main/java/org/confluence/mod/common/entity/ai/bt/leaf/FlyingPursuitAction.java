package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/**
 * 让飞行生物持续平滑追逐目标。
 *
 * <p>每 tick 在当前速度上增加朝向目标的加速度，再统一限制最大速度。该方式保留转向惯性，
 * 不会像反复设置固定冲刺向量一样在目标换边时瞬间弹转。接触伤害由飞行实体本身统一处理，
 * 不依赖当前正在执行的行为树节点。</p>
 */
public final class FlyingPursuitAction extends BTNode {
    private final PathfinderMob mob;
    private final double acceleration;
    private final double maxSpeed;

    public FlyingPursuitAction(
            PathfinderMob mob,
            double acceleration,
            double maxSpeed) {
        this.mob = mob;
        this.acceleration = acceleration;
        this.maxSpeed = maxSpeed;
    }

    @Override
    public BTStatus execute() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) {
            return BTStatus.FAILURE;
        }

        Vec3 offset = target.getEyePosition().subtract(mob.getEyePosition());
        if (offset.lengthSqr() > 1.0E-6) {
            Vec3 velocity = mob.getDeltaMovement()
                    .scale(0.96)
                    .add(offset.normalize().scale(acceleration));
            if (velocity.lengthSqr() > maxSpeed * maxSpeed) {
                velocity = velocity.normalize().scale(maxSpeed);
            }
            mob.setDeltaMovement(velocity);
            mob.hasImpulse = true;
        }
        mob.getLookControl().setLookAt(target, 10.0F, 80.0F);

        return BTStatus.RUNNING;
    }
}
