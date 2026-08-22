package org.confluence.mod.common.summon.terraprisma;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summon.SummonGoal;
import org.confluence.mod.common.summon.SummonPose;

/// 控制泰拉棱镜的普通追击。
final class TerraprismaChaseGoal extends SummonGoal<TerraprismaSummon> {
    TerraprismaChaseGoal(TerraprismaSummon summon) {
        super(summon);
    }

    @Override
    public boolean canUse() {
        return summon.hasValidTarget() && summon.position().distanceToSqr(summon.owner().position()) < 16.0 * 16.0;
    }

    @Override
    public void tick() {
        Vec3 targetPosition = summon.targetPosition();
        Vec3 direction = targetPosition.subtract(summon.eyePosition()).normalize();
        SummonPose aimed = summon.aimAt(summon.position(), direction);
        Vec3 currentDirection = Vec3.directionFromRotation(summon.currentPose().pitch(), summon.currentPose().yaw()).normalize();
        double angle = Math.acos(Mth.clamp(currentDirection.dot(direction), -1.0, 1.0));
        float turnProgress = angle < 1.0E-5 ? 1.0F : (float) Math.min(1.0, Math.toRadians(30.0) / angle);
        SummonPose turned = summon.currentPose().interpolate(aimed, turnProgress);
        Vec3 movement = angle < 0.05 ? summon.velocity().add(direction).normalize().scale(0.6)
                : summon.velocity().scale(0.9);
        summon.moveTo(new SummonPose(summon.position().add(movement), turned.yaw(), turned.pitch(), turned.roll()));
    }
}
