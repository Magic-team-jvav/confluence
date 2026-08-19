package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/// 恶魔眼在夜间没有目标时的游荡行为。
///
/// <p>首次游荡高度会成为固定基准，之后每三十刻在周围十五格重新选择航点。该规则与
/// 1.21 的实现一致，避免通用随机飞行行为不断改变高度并逐渐贴地。</p>
public final class DemonEyeWanderAction extends BTNode {
    private final PathfinderMob mob;
    private double anchorY = Double.NaN;
    private int locateCount;
    private int ticksLeft;
    private Vec3 targetPos;

    public DemonEyeWanderAction(PathfinderMob mob) {
        this.mob = mob;
    }

    @Override
    public void start() {
        locateCount++;
        mob.setDeltaMovement(mob.getDeltaMovement().with(Direction.Axis.Y, 0.0));
        if (Double.isNaN(anchorY)) {
            anchorY = mob.getY();
        }

        double x = mob.getRandom().nextDouble() * 10.0 - 5.0;
        double z = mob.getRandom().nextDouble() * 10.0 - 5.0;
        Vec3 horizontal = new Vec3(x, 0.0, z);
        if (horizontal.lengthSqr() < 1.0E-8) {
            horizontal = new Vec3(1.0, 0.0, 0.0);
        }
        targetPos = horizontal.normalize().scale(15.0).add(mob.position()).with(Direction.Axis.Y, anchorY + offsetY() + 5.0);
        ticksLeft = 30;
    }

    @Override
    public BTStatus execute() {
        if (mob.getTarget() != null || !mob.level().isNight() || targetPos == null || ticksLeft <= 0 || mob.position().distanceToSqr(targetPos) <= 2.25) {
            return BTStatus.SUCCESS;
        }

        Vec3 movement = mob.getDeltaMovement();
        Vec3 acceleration = mob.position().vectorTo(targetPos).normalize().multiply(0.08, 0.03, 0.08);
        Vec3 nextMovement = movement.add(acceleration);
        if (angleBetween(acceleration, movement) > 15.0 || nextMovement.length() < 0.2) {
            mob.setDeltaMovement(nextMovement);
            mob.hasImpulse = true;
        }
        ticksLeft--;
        return BTStatus.RUNNING;
    }

    private float offsetY() {
        float period = 6.1F;
        float radians = Mth.TWO_PI * (locateCount % period) / period;
        return 2.57F * Mth.cos(radians) + 1.0F;
    }

    private static double angleBetween(Vec3 first, Vec3 second) {
        double lengths = first.length() * second.length();
        if (lengths < 1.0E-8) {
            return 0.0;
        }
        double cosine = Mth.clamp(first.dot(second) / lengths, -1.0, 1.0);
        return Math.toDegrees(Math.acos(cosine));
    }
}
