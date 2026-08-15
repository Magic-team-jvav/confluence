package org.confluence.mod.common.summon.terraprisma;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;

/**
 * 泰拉棱镜的穿透环绕技能。
 */
final class TerraprismaRotateGoal extends TerraprismaSkillGoal {
    static final int DURATION = 20;
    static final int BASE_COOLDOWN = 30;
    private static final int PIERCE_TICKS = 5;
    private static final int ORBIT_TICKS = 10;
    private static final double PIERCE_SPEED = 3.0;
    private static final double ORBIT_SPEED = 2.2;
    private static final double ORBIT_RADIUS = 3.0;
    private Vec3 entryDirection = Vec3.ZERO;
    private Vec3 orbitSide = Vec3.ZERO;

    TerraprismaRotateGoal(TerraprismaSummon summon) {
        super(summon, DURATION, BASE_COOLDOWN);
    }

    @Override
    public void start() {
        super.start();
        entryDirection = summon.targetPosition().subtract(summon.position()).normalize();
        if (entryDirection.lengthSqr() < 1.0E-6) entryDirection = summon.currentVelocity();
        orbitSide = entryDirection.cross(new Vec3(0.0, 1.0, 0.0)).normalize();
        if (orbitSide.lengthSqr() < 1.0E-6) orbitSide = new Vec3(1.0, 0.0, 0.0);
        summon.beginRotateAnimation();
    }

    @Override
    public void tick() {
        Vec3 center = predictedTargetPosition();
        if (elapsedTicks < PIERCE_TICKS) {
            moveToward(center.add(entryDirection.scale(ORBIT_RADIUS)), PIERCE_SPEED);
        } else if (elapsedTicks < PIERCE_TICKS + ORBIT_TICKS) {
            double progress = (elapsedTicks - PIERCE_TICKS + 1.0) / ORBIT_TICKS;
            double angle = progress * Math.PI * 1.5;
            Vec3 radial = entryDirection.scale(Math.cos(angle)).add(orbitSide.scale(Math.sin(angle)))
                    .scale(ORBIT_RADIUS).add(0.0, Math.sin(angle * 2.0) * 0.55, 0.0);
            moveToward(center.add(radial), ORBIT_SPEED);
        } else {
            Vec3 reentryDirection = center.subtract(summon.position()).normalize();
            if (reentryDirection.lengthSqr() < 1.0E-6)
                reentryDirection = entryDirection.scale(-1.0);
            moveToward(center.add(reentryDirection.scale(ORBIT_RADIUS)), PIERCE_SPEED);
        }
        elapsedTicks++;
    }

    /**
     * 读取目标当前速度进行短距离预判，避免高速目标在技能过程中持续脱离轨迹。
     */
    private Vec3 predictedTargetPosition() {
        Entity target = summon.actualTarget();
        if (target == null) {
            return summon.targetPosition();
        }
        return summon.targetPosition().add(target.getDeltaMovement().scale(2.0));
    }

    private void moveToward(Vec3 destination, double speed) {
        Vec3 movement = destination.subtract(summon.position());
        if (movement.lengthSqr() > speed * speed) movement = movement.normalize().scale(speed);
        summon.moveAndLook(movement, destination);
    }

    @Override
    public void stop() {
        super.stop();
        summon.finishRotateAnimation();
    }
}
