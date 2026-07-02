package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/**
 * 射击动作：向目标发射弹射物。isReady 为 false 时返回 SUCCESS（跳过）。
 */
public class ShootAction extends BTNode {
    private final Mob shooter;
    private final float damage;
    private final float inaccuracy;
    private boolean done;

    public ShootAction(Mob shooter, float damage, float inaccuracy) {
        this.shooter = shooter;
        this.damage = damage;
        this.inaccuracy = inaccuracy;
    }

    @Override
    public void start() {
        done = false;
    }

    @Override
    public BTStatus execute() {
        if (done) return BTStatus.SUCCESS;

        LivingEntity target = shooter.getTarget();
        if (target == null) return BTStatus.FAILURE;

        Vec3 origin = shooter.getEyePosition();
        Vec3 toTarget = target.getEyePosition().subtract(origin);
        if (toTarget.lengthSqr() < 0.01) return BTStatus.SUCCESS;

        Vec3 dir = toTarget.normalize();
        if (inaccuracy > 0) {
            dir = dir.add(
                    (shooter.getRandom().nextFloat() - 0.5) * inaccuracy,
                    (shooter.getRandom().nextFloat() - 0.5) * inaccuracy,
                    (shooter.getRandom().nextFloat() - 0.5) * inaccuracy
            ).normalize();
        }

        // Direct damage via ray check — projectile entity can replace this later
        Vec3 end = origin.add(dir.scale(32));
        LivingEntity hit = null;
        double closest = Double.MAX_VALUE;
        for (LivingEntity e : shooter.level().getEntitiesOfClass(LivingEntity.class,
                shooter.getBoundingBox().expandTowards(dir.scale(32)).inflate(1.0))) {
            if (e == shooter || !shooter.canAttack(e)) continue;
            Vec3 hitPos = e.getBoundingBox().clip(origin, end).orElse(null);
            if (hitPos != null) {
                double dist = origin.distanceToSqr(hitPos);
                if (dist < closest) { closest = dist; hit = e; }
            }
        }
        if (hit != null) {
            hit.hurt(shooter.damageSources().mobAttack(shooter), damage);
        }

        done = true;
        return BTStatus.SUCCESS;
    }
}
