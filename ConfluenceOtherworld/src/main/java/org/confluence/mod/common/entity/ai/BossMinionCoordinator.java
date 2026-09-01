package org.confluence.mod.common.entity.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

/// Boss 召唤物共用的轻量协同计算。
///
/// 所有结果只依赖实体身份和世界时钟，不保存临时编队对象；区块重载后仍能恢复相同槽位，
/// 并避免全部召唤物同一刻冲锋或射击。
public final class BossMinionCoordinator {
    private BossMinionCoordinator() {}

    public static int phaseOffset(Entity minion, int period) {
        if (period <= 1) return 0;
        return Math.floorMod(minion.getUUID().hashCode(), period);
    }

    public static void faceTargetImmediately(Mob minion, LivingEntity target) {
        if (target == null || !target.isAlive()) return;
        Vec3 direction = target.getEyePosition().subtract(minion.getEyePosition());
        if (direction.lengthSqr() <= 1.0E-7D) return;
        double horizontal = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
        float yaw = (float) (Mth.atan2(direction.z, direction.x) * Mth.RAD_TO_DEG) - 90.0F;
        float pitch = (float) (-Mth.atan2(direction.y, horizontal) * Mth.RAD_TO_DEG);
        minion.setYRot(yaw);
        minion.setXRot(pitch);
        minion.setYBodyRot(yaw);
        minion.setYHeadRot(yaw);
        // 出生插值也从同一角度开始，不能从注册项默认的 0° 慢慢转过去。
        minion.yRotO = yaw;
        minion.xRotO = pitch;
        minion.yBodyRotO = yaw;
        minion.yHeadRotO = yaw;
        minion.getLookControl().setLookAt(target, 360.0F, 360.0F);
    }

    public static boolean isAttackWindow(Entity minion, int period, int activeTicks) {
        int safePeriod = Math.max(1, period);
        int phase = Math.floorMod((int) (minion.level().getGameTime() % safePeriod)
                + phaseOffset(minion, safePeriod), safePeriod);
        return phase < Mth.clamp(activeTicks, 1, safePeriod);
    }

    public static Vec3 predict(LivingEntity target, double leadTicks, double maximumLead) {
        Vec3 lead = target.getDeltaMovement().scale(Math.max(0.0D, leadTicks));
        double max = Math.max(0.0D, maximumLead);
        if (lead.lengthSqr() > max * max && lead.lengthSqr() > 1.0E-7D) {
            lead = lead.normalize().scale(max);
        }
        return target.getEyePosition().add(lead);
    }

    public static Vec3 orbitPoint(Entity minion, LivingEntity center, double radius,
                                  double height, double angularSpeed, int slots) {
        int safeSlots = Math.max(1, slots);
        int slot = Math.floorMod(minion.getUUID().hashCode(), safeSlots);
        double angle = minion.level().getGameTime() * angularSpeed
                + slot * Math.PI * 2.0D / safeSlots;
        double verticalWave = Math.sin(angle * 0.7D + slot) * height * 0.35D;
        return center.position().add(
                Math.cos(angle) * radius,
                center.getBbHeight() * 0.65D + height + verticalWave,
                Math.sin(angle) * radius);
    }

    public static Vec3 steer(Vec3 currentVelocity, Vec3 currentPosition, Vec3 destination,
                             double acceleration, double maximumSpeed) {
        Vec3 desired = destination.subtract(currentPosition);
        Vec3 velocity = currentVelocity.scale(0.84D);
        if (desired.lengthSqr() > 1.0E-7D) {
            velocity = velocity.add(desired.normalize().scale(Math.max(0.0D, acceleration)));
        }
        double max = Math.max(0.01D, maximumSpeed);
        if (velocity.lengthSqr() > max * max) velocity = velocity.normalize().scale(max);
        return velocity;
    }
}
