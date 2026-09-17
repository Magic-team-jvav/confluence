package org.confluence.mod.common.summon.terraprisma;

import net.minecraft.world.phys.Vec3;

/// 泰拉棱镜的旋转技能。
final class TerraprismaRotateGoal extends TerraprismaSkillGoal {
    static final int DURATION = 14;
    static final int BASE_COOLDOWN = 80;
    private int orbitDirection;
    private double orbitPhase;
    private Vec3 smoothedCenter;

    TerraprismaRotateGoal(TerraprismaSummon summon) {
        super(summon, DURATION, BASE_COOLDOWN);
    }

    @Override
    public void start() {
        super.start();
        orbitDirection = summon.owner().getRandom1211().nextBoolean() ? 1 : -1;
        orbitPhase = 0.0;
        smoothedCenter = summon.targetPosition();
        summon.beginRotateAnimation();
    }

    @Override
    public void tick() {
        elapsedTicks++;
        smoothedCenter = smoothedCenter.lerp(summon.targetPosition(), 0.35);
        Vec3 center = smoothedCenter;
        Vec3 radial = summon.position().subtract(center);
        Vec3 horizontalRadial = radial.multiply(1.0, 0.0, 1.0);
        if (horizontalRadial.lengthSqr() < 1.0E-6)
            horizontalRadial = Vec3.directionFromRotation(0.0F, summon.currentPose().yaw()).multiply(1.0, 0.0, 1.0);
        Vec3 outward = horizontalRadial.normalize();
        Vec3 tangent = new Vec3(-outward.z, 0.0, outward.x).scale(orbitDirection);
        orbitPhase += Math.PI * 2.0 / DURATION;
        double radialCorrection = (1.4 - horizontalRadial.length()) * 0.22;
        double verticalCorrection = (center.y + Math.sin(orbitPhase * 2.0) * 0.65 - summon.position().y) * 0.25;
        Vec3 desiredMovement = tangent.scale(0.62).add(outward.scale(radialCorrection)).add(0.0, verticalCorrection, 0.0);
        Vec3 movement = summon.velocity().scale(0.35).add(desiredMovement.scale(0.65));
        if (movement.lengthSqr() > 0.75 * 0.75) movement = movement.normalize().scale(0.75);
        Vec3 nextPosition = summon.position().add(movement);
        summon.moveTo(movement.lengthSqr() < 1.0E-8 ? summon.currentPose() : summon.aimAt(nextPosition, movement));
    }

    @Override
    public void stop() {
        super.stop();
        summon.finishRotateAnimation();
    }
}
