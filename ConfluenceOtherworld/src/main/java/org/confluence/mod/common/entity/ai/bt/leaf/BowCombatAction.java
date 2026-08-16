package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/// 持弓敌怪的完整远程作战周期。
///
/// <p>该节点重现 1.21 侧 {@code TERangedAttackGoal} 的可见性、转向、拉弓、射击冷却和
/// 射后走位。状态集中在同一个节点中，避免行为树每刻重启拉弓过程；具体箭实体仍由
/// {@link SpawnArrowAction} 创建。</p>
///
/// <p>节点只在目标有效且实体仍持有弓时运行。失去弓后返回失败，由实体行为树切换到
/// 近战分支，这与 1.21 侧重新评估武器目标的结果一致。</p>
public final class BowCombatAction extends BTNode {
    private static final int REQUIRED_VISIBLE_TICKS = 5;
    private static final double LOOK_WHILE_MOVING_ANGLE = 0.85;
    private static final double FIRE_ANGLE = 0.1;

    private final PathfinderMob mob;
    private final double movementSpeed;
    private final int normalAttackInterval;
    private final int hardAttackInterval;
    private final double attackRadiusSqr;
    private final int drawDuration;
    private final float arrowVelocity;

    private int visibleTicks;
    private int attackCooldown;
    private int drawTicks;

    public BowCombatAction(
            PathfinderMob mob,
            double movementSpeed,
            int normalAttackInterval,
            int hardAttackInterval,
            double attackRadius,
            int drawDuration,
            float arrowVelocity) {
        if (normalAttackInterval <= 0 || hardAttackInterval <= 0
                || attackRadius <= 0.0 || drawDuration <= 0) {
            throw new IllegalArgumentException(
                    "Bow combat timing and radius must be positive");
        }
        this.mob = mob;
        this.movementSpeed = movementSpeed;
        this.normalAttackInterval = normalAttackInterval;
        this.hardAttackInterval = hardAttackInterval;
        this.attackRadiusSqr = attackRadius * attackRadius;
        this.drawDuration = drawDuration;
        this.arrowVelocity = arrowVelocity;
    }

    @Override
    public void start() {
        visibleTicks = 0;
        attackCooldown = -1;
        drawTicks = 0;
    }

    @Override
    public BTStatus execute() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive() || !isHoldingBow()) {
            stopCombat();
            return BTStatus.FAILURE;
        }

        mob.setAggressive(true);
        boolean canSee = mob.getSensing().hasLineOfSight(target);
        visibleTicks = canSee ? visibleTicks + 1 : 0;
        double distanceSqr = mob.distanceToSqr(target);
        double angle = angleBetween(
                mob.getLookAngle(), target.position().subtract(mob.position()));

        if (distanceSqr > attackRadiusSqr
                || visibleTicks < REQUIRED_VISIBLE_TICKS) {
            mob.getNavigation().moveTo(target, movementSpeed);
        }
        if (mob.getNavigation().isDone() || angle < LOOK_WHILE_MOVING_ANGLE) {
            mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }

        if (mob.isUsingItem()) {
            // 拉弓期间锁定目标；短暂丢失视线时保留拉弓状态，恢复后继续。
            mob.lookAt(target, 30.0F, 30.0F);
            mob.getLookControl().setLookAt(target);
            if (canSee) {
                drawTicks++;
                if (drawTicks >= drawDuration && angle < FIRE_ANGLE) {
                    fire(target, distanceSqr);
                }
            }
            return BTStatus.RUNNING;
        }

        if (--attackCooldown <= 0) {
            InteractionHand bowHand = ProjectileUtil.getWeaponHoldingHand(
                    mob, item -> item instanceof BowItem);
            mob.startUsingItem(bowHand);
            drawTicks = 0;
        }
        return BTStatus.RUNNING;
    }

    @Override
    public void stop() {
        stopCombat();
        mob.getNavigation().stop();
    }

    private boolean isHoldingBow() {
        return mob.isHolding(stack -> stack.getItem() instanceof BowItem);
    }

    private void fire(LivingEntity target, double distanceSqr) {
        mob.stopUsingItem();
        SpawnArrowAction shot = SpawnArrowAction.mobBowShot(
                mob, BowItem.getPowerForTime(drawTicks), arrowVelocity);
        shot.start();
        if (shot.execute() == BTStatus.SUCCESS) {
            attackCooldown = mob.level().getDifficulty() == Difficulty.HARD
                    ? hardAttackInterval
                    : normalAttackInterval;
            choosePostShotMovement(target, distanceSqr);
        } else {
            attackCooldown = 1;
        }
        drawTicks = 0;
    }

    private void choosePostShotMovement(
            LivingEntity target, double distanceSqr) {
        Vec3 destination;
        double speed;
        if (distanceSqr < 25.0) {
            destination = LandRandomPos.getPosAway(
                    mob, 5, 5, target.position());
            speed = movementSpeed * 1.2;
        } else if (distanceSqr > 49.0) {
            destination = LandRandomPos.getPosTowards(
                    mob, 7, 5, target.position());
            speed = movementSpeed;
        } else {
            destination = LandRandomPos.getPos(mob, 3, 2);
            speed = movementSpeed * 0.9;
        }
        if (destination != null) {
            mob.getNavigation().moveTo(
                    destination.x, destination.y, destination.z, speed);
        }
    }

    private void stopCombat() {
        if (mob.isUsingItem()) {
            mob.stopUsingItem();
        }
        mob.setAggressive(false);
        visibleTicks = 0;
        attackCooldown = -1;
        drawTicks = 0;
    }

    private static double angleBetween(Vec3 first, Vec3 second) {
        double denominator = first.length() * second.length();
        if (denominator < 1.0E-7) {
            return 0.0;
        }
        double cosine = first.dot(second) / denominator;
        return Math.acos(Math.max(-1.0, Math.min(1.0, cosine)));
    }
}
