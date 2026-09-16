package org.confluence.mod.common.summon.terraprisma;

import net.minecraft.world.phys.Vec3;

/// 控制泰拉棱镜移动到目标处并向下斜劈。
final class TerraprismaSlashGoal extends TerraprismaSkillGoal {
    static final int BASE_COOLDOWN = 120;
    private static final int RAISE_TICKS = 5;
    private static final int STRIKE_TICKS = 7;
    private Vec3 startPosition;
    private Vec3 raisedPosition;
    private Vec3 raiseControl;
    private Vec3 trackedTarget;

    TerraprismaSlashGoal(TerraprismaSummon summon) {
        super(summon, 14, BASE_COOLDOWN);
    }

    @Override
    public void start() {
        super.start();
        startPosition = summon.position();
        trackedTarget = summon.targetPosition();
        Vec3 away = startPosition.subtract(trackedTarget).multiply(1.0, 0.0, 1.0);
        if (away.lengthSqr() < 1.0E-6)
            away = Vec3.directionFromRotation(0.0F, summon.owner().yBodyRot).multiply(1.0, 0.0, 1.0);
        raisedPosition = trackedTarget.add(away.normalize().scale(1.5)).add(0.0, 4.0, 0.0);
        raiseControl = startPosition.add(summon.velocity().scale(2.0));
        summon.beginSlashAnimation();
    }

    @Override
    public void tick() {
        elapsedTicks++;
        trackedTarget = trackedTarget.lerp(summon.targetPosition(), 0.3);
        Vec3 nextPosition;
        if (elapsedTicks <= RAISE_TICKS) {
            nextPosition = cubic(startPosition, raiseControl, raisedPosition, raisedPosition,
                    elapsedTicks / (double) RAISE_TICKS);
        } else if (elapsedTicks <= RAISE_TICKS + STRIKE_TICKS) {
            double progress = smooth((elapsedTicks - RAISE_TICKS) / (double) STRIKE_TICKS);
            nextPosition = raisedPosition.lerp(trackedTarget.add(0.0, -2.0, 0.0), progress);
        } else {
            Vec3 direction = summon.velocity().lengthSqr() < 1.0E-8
                    ? new Vec3(0.0, -1.0, 0.0) : summon.velocity().normalize();
            nextPosition = summon.position().add(direction.scale(0.7));
        }
        Vec3 movement = nextPosition.subtract(summon.position());
        summon.moveTo(movement.lengthSqr() < 1.0E-8 ? summon.currentPose() : summon.aimAt(nextPosition, movement));
    }

    @Override
    public void stop() {
        super.stop();
        summon.finishSlashAnimation();
    }

    private static double smooth(double progress) {
        return progress * progress * (3.0 - 2.0 * progress);
    }

    private static Vec3 cubic(Vec3 start, Vec3 first, Vec3 second, Vec3 end, double progress) {
        double inverse = 1.0 - progress;
        return start.scale(inverse * inverse * inverse)
                .add(first.scale(3.0 * inverse * inverse * progress))
                .add(second.scale(3.0 * inverse * progress * progress))
                .add(end.scale(progress * progress * progress));
    }
}
