package org.confluence.mod.common.entity.monster;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.init.ModSoundEvents;

/// 飞龙的十二段实体链、空中盘旋与往返突袭行为。
///
/// <p>飞龙没有目标时围绕定期更换的高空中心做圆周运动，最低巡航中心位于 Y=105；
/// 发现目标后停止盘旋，先以有限角速度调整朝向，再沿身体正前方高速穿过目标。近距离且
/// 玩家脚下悬空时会进入更快的俯冲段，避免把飞龙退化成普通蠕虫的直接追踪。</p>
public class Wyvern extends BaseWormMonster {

    public Wyvern(EntityType<? extends BaseWormMonster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseWormMonster.createWormAttributes();
    }

    @Override
    protected int getSegmentCount() {
        return 12;
    }

    @Override
    protected float segmentSpacing() {
        return 1.0F;
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return createMovementAction();
            }
        };
    }

    private BTNode createMovementAction() {
        return new WyvernMovementAction(this);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.WYVERN_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.WYVERN_DEATH.get();
    }

    /// 把 1.21 的盘旋与往返突袭状态收敛到一个持续运行的行为树节点。
    private static final class WyvernMovementAction extends BTNode {
        private static final double TURN_DISTANCE_SQR = 16.0 * 16.0;
        private static final double CIRCLE_RADIUS = 20.0;
        private static final double CIRCLE_ANGLE_STEP = 0.015;
        private static final double CIRCLE_VERTICAL_STEP = 0.2;
        private static final double CIRCLE_VERTICAL_LIMIT = 6.0;
        private static final double CIRCLE_SPEED = Math.sqrt(CIRCLE_ANGLE_STEP * CIRCLE_RADIUS * CIRCLE_ANGLE_STEP * CIRCLE_RADIUS + CIRCLE_VERTICAL_STEP * CIRCLE_VERTICAL_STEP) * 0.9;

        private final Wyvern wyvern;
        private Vec3 center;
        private int centerTicks;
        private double angle;
        private double angleStep = CIRCLE_ANGLE_STEP;
        private double verticalOffset;
        private boolean rising;
        private int closeDashTicks;

        private WyvernMovementAction(Wyvern wyvern) {
            this.wyvern = wyvern;
        }

        @Override
        public void start() {
            center = null;
            centerTicks = 0;
            closeDashTicks = 0;
        }

        @Override
        public BTStatus execute() {
            LivingEntity target = wyvern.getTarget();
            if (target != null && target.isAlive() && wyvern.canAttack(target)) {
                attack(target);
            } else {
                circle();
            }
            return BTStatus.RUNNING;
        }

        private void circle() {
            if (center == null || --centerTicks <= 0) {
                centerTicks = 150 + wyvern.random.nextInt(75);
                angleStep = wyvern.random.nextBoolean()
                        ? CIRCLE_ANGLE_STEP
                        : -CIRCLE_ANGLE_STEP;
                center = new Vec3(wyvern.getX() + (wyvern.random.nextDouble() - 0.5) * 10.0, Math.max(wyvern.getY(), 95.0) + 10.0, wyvern.getZ() + (wyvern.random.nextDouble() - 0.5) * 10.0);
            }

            angle += angleStep;
            if (verticalOffset >= CIRCLE_VERTICAL_LIMIT) {
                rising = false;
            } else if (verticalOffset <= -CIRCLE_VERTICAL_LIMIT) {
                rising = true;
            }
            verticalOffset = Mth.clamp(verticalOffset + (rising ? CIRCLE_VERTICAL_STEP : -CIRCLE_VERTICAL_STEP), -CIRCLE_VERTICAL_LIMIT, CIRCLE_VERTICAL_LIMIT);

            Vec3 destination = circlePosition(angle, verticalOffset);
            Vec3 direction = destination.subtract(wyvern.position());
            if (direction.lengthSqr() > 1.0E-6) {
                wyvern.setDeltaMovement(direction.normalize().scale(CIRCLE_SPEED));
            }
            lookAlong(circlePosition(angle + angleStep * 5.0, 0.0).subtract(wyvern.position()));
        }

        private Vec3 circlePosition(double targetAngle, double yOffset) {
            return center.add(CIRCLE_RADIUS * Math.cos(targetAngle), yOffset, CIRCLE_RADIUS * Math.sin(targetAngle));
        }

        private void attack(LivingEntity target) {
            Vec3 targetDirection = target.position().subtract(wyvern.position());
            if (targetDirection.lengthSqr() < 1.0E-6) {
                return;
            }
            Vec3 forward = wyvern.getLookAngle().normalize();
            Vec3 desired = targetDirection.normalize();
            double turnAngle = Math.acos(Mth.clamp(forward.dot(desired), -1.0, 1.0));
            double distanceSqr = wyvern.distanceToSqr(target);

            if (distanceSqr > TURN_DISTANCE_SQR && turnAngle > Math.PI / 6.0) {
                lookAt(target, 5.0F);
                wyvern.setDeltaMovement(forward.scale(0.4).add(0.0, 0.4, 0.0));
                return;
            }

            if (turnAngle < Math.PI / 2.0) {
                lookAt(target, closeDashTicks > 0 ? 0.0F : 2.0F);
            }
            if (distanceSqr < 25.0 && target.level().getBlockState(target.blockPosition().below()).isAir()) {
                closeDashTicks = 15;
            }

            if (closeDashTicks > 0) {
                closeDashTicks--;
                wyvern.setDeltaMovement(wyvern.getLookAngle().normalize().scale(1.0).add(0.0, 0.1, 0.0));
            } else {
                wyvern.setDeltaMovement(wyvern.getLookAngle().normalize().scale(0.8));
            }
        }

        private void lookAt(LivingEntity target, float yawLimit) {
            wyvern.getLookControl().setLookAt(target);
            if (yawLimit > 0.0F) {
                wyvern.lookAt(target, yawLimit, 30.0F);
            }
        }

        private void lookAlong(Vec3 direction) {
            if (direction.lengthSqr() < 1.0E-6) {
                return;
            }
            Vec3 lookPosition = wyvern.position().add(direction.normalize().scale(8.0));
            wyvern.getLookControl().setLookAt(lookPosition.x, lookPosition.y, lookPosition.z, 10.0F, 30.0F);
            float yaw = (float) (Mth.atan2(direction.z, direction.x) * Mth.RAD_TO_DEG) - 90.0F;
            float pitch = (float) (-(Mth.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)) * Mth.RAD_TO_DEG));
            wyvern.setYRot(Mth.rotLerp(0.2F, wyvern.getYRot(), yaw));
            wyvern.setXRot(Mth.rotLerp(0.2F, wyvern.getXRot(), pitch));
            wyvern.setYBodyRot(wyvern.getYRot());
        }
    }
}
