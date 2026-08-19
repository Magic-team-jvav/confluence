package org.confluence.mod.common.entity.boss;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.projectile.HostileParticleProjectile;
import org.confluence.mod.common.init.entity.ModEntities;
import org.jetbrains.annotations.Nullable;

/// 血肉墙的眼睛；每只眼睛独立锁定目标并维持自己的射击节奏。
public final class WallOfFleshEye extends WallOfFleshPart {
    private static final int FIRST_SHOT_DELAY = 10;
    private static final int NORMAL_SHOT_INTERVAL = 20;
    private static final int PHASE_TWO_SHOT_INTERVAL = 14;
    private static final int BURST_INTERVAL = 10;

    private int shootTimer = FIRST_SHOT_DELAY;
    private int burstRemaining = 1;

    public WallOfFleshEye(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    protected void tickAttack(WallOfFlesh master, @Nullable LivingEntity target) {
        if (target == null || !master.isValidFrontTarget(target)) {
            return;
        }
        if (distanceToSqr(target) > 120.0 * 120.0 * 1.2) {
            return;
        }
        if (--shootTimer > 0) {
            return;
        }

        shoot(master, target);
        if (master.isPhaseTwo() && --burstRemaining > 0) {
            shootTimer = BURST_INTERVAL;
        } else {
            burstRemaining = master.isPhaseTwo() ? 3 : 1;
            shootTimer = (master.isPhaseTwo()
                    ? PHASE_TWO_SHOT_INTERVAL
                    : NORMAL_SHOT_INTERVAL)
                    + random.nextInt(20);
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
        double flightTicks = origin.distanceTo(targetCenter) / 1.5;
        Vec3 predictedTarget = targetCenter.add(target.getDeltaMovement().scale(Math.min(flightTicks, 12.0)));
        Vec3 direction = predictedTarget.subtract(origin);
        if (direction.lengthSqr() < 1.0E-6) {
            return;
        }
        projectile.configure(master, origin, direction.normalize().scale(1.5), master.getLaserDamage(), 100);
        serverLevel.addFreshEntity(projectile);
    }
}
