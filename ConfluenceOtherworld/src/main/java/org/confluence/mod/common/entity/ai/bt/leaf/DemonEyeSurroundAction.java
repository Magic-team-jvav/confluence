package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.monster.DemonEye;

/// 恶魔眼持续朝目标飞行，速度逐渐转向，受击和碰撞后的惯性不会被立即覆盖。
public final class DemonEyeSurroundAction extends BTNode {
    private static final double HORIZONTAL_SPEED = 0.75D;
    private static final double VERTICAL_SPEED = 0.28125D;
    private static final double HORIZONTAL_ACCELERATION = 0.05625D;
    private static final double VERTICAL_ACCELERATION = 0.0225D;

    private final DemonEye mob;

    public DemonEyeSurroundAction(DemonEye mob) {
        this.mob = mob;
    }

    @Override
    public BTStatus execute() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive() || LibDateUtils.isDay(mob.level())) {
            return BTStatus.SUCCESS;
        }

        Vec3 aim = target.getBoundingBox().getCenter().subtract(mob.getBoundingBox().getCenter());
        Vec3 horizontal = new Vec3(aim.x, 0.0D, aim.z);
        if (horizontal.lengthSqr() > 1.0E-8D) horizontal = horizontal.normalize();
        double scale = 2.0D - mob.getVariant().scale();
        Vec3 velocity = mob.getDeltaMovement();
        double x = steer(velocity.x, horizontal.x, HORIZONTAL_SPEED * scale, HORIZONTAL_ACCELERATION, 0.5D, 2.0D);
        double z = steer(velocity.z, horizontal.z, HORIZONTAL_SPEED * scale, HORIZONTAL_ACCELERATION, 0.5D, 2.0D);
        double y = steer(velocity.y, Math.signum(aim.y), VERTICAL_SPEED * scale, VERTICAL_ACCELERATION, 0.25D, 2.25D);
        if (mob.level().getFluidState(BlockPos.containing(mob.position())).is(FluidTags.WATER)) {
            y = Math.max(y, 0.1D);
        }
        mob.setDeltaMovement(x, y, z);
        mob.hasImpulse = true;
        return BTStatus.RUNNING;
    }

    private static double steer(double velocity, double direction, double maxSpeed, double acceleration, double reverseFactor, double fastReverseFactor) {
        if (direction == 0.0D) return velocity;
        double targetSpeed = direction * maxSpeed;
        // 已经朝目标超过速度上限时保留惯性，不能直接截断击退或反弹速度。
        if (velocity * direction > 0.0D && Math.abs(velocity) >= Math.abs(targetSpeed))
            return velocity;
        double step = acceleration * Math.abs(direction);
        if (velocity * direction < 0.0D)
            step *= Math.abs(velocity) > maxSpeed ? fastReverseFactor : reverseFactor;
        return velocity < targetSpeed ? Math.min(velocity + step, targetSpeed) : Math.max(velocity - step, targetSpeed);
    }
}
