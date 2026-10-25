package org.confluence.mod.common.entity.boss;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.projectile.HostileParticleProjectile;
import org.confluence.mod.common.init.entity.ModEntities;
import org.jetbrains.annotations.Nullable;

/// 血肉墙的眼睛；每只眼睛独立锁定目标并维持自己的射击节奏。
public final class WallOfFleshEye extends WallOfFleshPart {
    /// 进入射击状态到首发激光的延迟，单位 tick。
    private static final int FIRST_SHOT_DELAY = 10;

    /// 肉墙眼睛射击目标的最远距离，单位格。
    private static final double MAX_TARGET_DISTANCE = 120.0;
    /// 肉墙眼睛激光弹体的飞行速度，单位格/tick。
    private static final double LASER_SPEED = 1.5;
    /// 激光发射时对目标移动位置的最大预判时长，单位 tick。
    private static final double MAX_TARGET_LEAD_TICKS = 12.0;
    /// 两轮激光射击间额外随机等待的范围，单位 tick。
    private static final int SHOT_INTERVAL_VARIATION = 20;
    /// 普通阶段两发激光之间的间隔，单位 tick。
    private static final int NORMAL_SHOT_INTERVAL = 20;
    /// 二阶段两发激光之间的间隔，单位 tick。
    private static final int PHASE_TWO_SHOT_INTERVAL = 14;
    /// 连发激光之间的短间隔，单位 tick。
    private static final int BURST_INTERVAL = 10;

    private int shootTimer = FIRST_SHOT_DELAY;
    private int burstRemaining = 1;

    public WallOfFleshEye(WallOfFlesh parent, int index) {
        super(parent, index, 4.0F, 4.0F);
    }

    @Override
    protected void tickAttack(WallOfFlesh master, @Nullable LivingEntity target) {
        if (target == null || !master.isValidFrontTarget(target)) {
            return;
        }
        if (distanceToSqr(target) > MAX_TARGET_DISTANCE * MAX_TARGET_DISTANCE * 1.2) {
            return;
        }
        if (--shootTimer > 0) {
            return;
        }

        shoot(master, target);
        if (master.isPhaseTwo() && --burstRemaining > 0) {
            shootTimer = Math.max(2, Math.round(BURST_INTERVAL * (0.4F + 0.6F * master.getHealth() / master.getMaxHealth())));
        } else {
            burstRemaining = master.isPhaseTwo() ? 3 : 1;
            float health = master.getHealth() / master.getMaxHealth();
            shootTimer = Math.max(2, Math.round(((master.isPhaseTwo() ? PHASE_TWO_SHOT_INTERVAL : NORMAL_SHOT_INTERVAL)
                    + random.nextInt(SHOT_INTERVAL_VARIATION)) * (0.4F + 0.6F * health)));
        }
    }

    private void shoot(WallOfFlesh master, LivingEntity target) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        HostileParticleProjectile projectile = ModEntities.WALL_OF_FLESH_LASER.get().create(serverLevel);
        if (projectile == null) {
            return;
        }

        Vec3 origin = position().add(0.0, getBbHeight() * 0.5, 0.0);
        Vec3 targetCenter = target.position().add(0.0, target.getBbHeight() * 0.5, 0.0);
        double flightTicks = origin.distanceTo(targetCenter) / LASER_SPEED;
        Vec3 predictedTarget = targetCenter.add(target.getDeltaMovement().scale(Math.min(flightTicks, MAX_TARGET_LEAD_TICKS)));
        Vec3 direction = predictedTarget.subtract(origin);
        if (direction.lengthSqr() < 1.0E-6) {
            return;
        }
        projectile.configure(master, origin, direction.normalize().scale(LASER_SPEED), (float) master.getAttributeValue(Attributes.ATTACK_DAMAGE), 100);
        serverLevel.addFreshEntity(projectile);
    }
}
