package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

import java.util.List;

/**
 * 复现 1.21 恶魔眼围绕目标移动的行为。
 *
 * <p>恶魔眼不会按固定阶段绕圈后直线冲锋，而是每四十刻重新选择玩家周围的一个航点。
 * 航点高度按照余弦曲线变化，水平方向偶尔偏转二十度，因此整体轨迹会在玩家周围上下
 * 浮动。移动时仍保留原速度，只在尚未达到速度上限或需要明显转向时继续加速。</p>
 */
public final class DemonEyeSurroundAction extends BTNode {
    private final PathfinderMob mob;
    private int locateCount;
    private int ticksLeft;
    private Vec3 targetPos;

    public DemonEyeSurroundAction(PathfinderMob mob) {
        this.mob = mob;
    }

    @Override
    public void start() {
        LivingEntity target = mob.getTarget();
        if (target == null) {
            targetPos = null;
            return;
        }

        locateCount++;
        ticksLeft = 40;
        mob.setDeltaMovement(mob.getDeltaMovement().with(Direction.Axis.Y, 0.0));

        Vec3 horizontalDirection = mob.position()
                .with(Direction.Axis.Y, target.getY())
                .vectorTo(target.position());
        float yaw = (float) Math.toDegrees(Mth.atan2(
                -horizontalDirection.x, horizontalDirection.z));
        if (mob.getRandom().nextInt(3) == 0) {
            yaw += mob.getRandom().nextBoolean() ? 20.0F : -20.0F;
        }
        Vec3 direction = Vec3.directionFromRotation(0.0F, yaw);
        targetPos = direction.normalize().scale(4.0)
                .with(Direction.Axis.Y, offsetY())
                .add(target.position());
    }

    @Override
    public BTStatus execute() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive() || !mob.level().isNight()
                || targetPos == null || ticksLeft <= 0
                || mob.position().distanceToSqr(targetPos) <= 0.09
                || target.position().distanceToSqr(targetPos) >= 100.0) {
            return BTStatus.SUCCESS;
        }

        Vec3 position = mob.position();
        if (Math.abs(position.x - targetPos.x) <= 0.1
                || Math.abs(position.y - targetPos.y) <= 0.1
                || Math.abs(position.z - targetPos.z) <= 0.1) {
            return BTStatus.SUCCESS;
        }

        Vec3 movement = mob.getDeltaMovement();
        Vec3 acceleration = position.vectorTo(targetPos).normalize()
                .multiply(0.08, 0.03, 0.08);
        Vec3 nextMovement = movement.add(acceleration);
        if (angleBetween(acceleration, movement) > 15.0
                || nextMovement.length() < 0.4) {
            mob.setDeltaMovement(nextMovement);
            mob.hasImpulse = true;
        }

        List<Player> players = mob.level().getEntities(
                EntityType.PLAYER,
                mob.getBoundingBox().expandTowards(nextMovement).inflate(0.15),
                player -> !player.isSpectator());
        for (Player player : players) {
            mob.doHurtTarget(player);
        }

        ticksLeft--;
        return BTStatus.RUNNING;
    }

    /**
     * 碰撞改变垂直速度时同步修正当前航点，避免恶魔眼反复撞向同一块天花板或地面。
     */
    public void adjustTargetAfterVerticalCollision(boolean movingDown) {
        LivingEntity target = mob.getTarget();
        if (targetPos != null && target != null) {
            targetPos = targetPos.with(Direction.Axis.Y,
                    target.getY() + (movingDown ? 2.0 : -1.0));
        }
    }

    Vec3 targetPosition() {
        return targetPos;
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
