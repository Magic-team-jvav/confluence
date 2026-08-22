package org.confluence.mod.common.summon.terraprisma;

import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summon.SummonGoal;

/// 让泰拉棱镜在没有目标时回到主人背后。
final class TerraprismaFollowOwnerGoal extends SummonGoal<TerraprismaSummon> {
    TerraprismaFollowOwnerGoal(TerraprismaSummon summon) {
        super(summon);
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public void start() {
        summon.setFollowingOwner(true);
    }

    @Override
    public void stop() {
        summon.setFollowingOwner(false);
    }

    @Override
    public void tick() {
        int sequence = summon.order() + 1;
        Vec3 forward = Vec3.directionFromRotation(0.0F, summon.owner().yBodyRot).multiply(1.0, 0.0, 1.0).normalize();
        Vec3 right = forward.cross(new Vec3(0.0, 1.0, 0.0)).normalize();
        double backDistance = 0.6F - 0.05F * (sequence - 1);
        Vec3 targetPosition = summon.owner().position().subtract(forward.scale(backDistance))
                .add(0.0, 1.0, 0.0)
                .add(right.scale(0.2F * (sequence / 2) * ((sequence & 1) == 0 ? 1.0F : -1.0F)));
        Vec3 direction = targetPosition.subtract(summon.position());
        double speed = Math.min(direction.length() * 0.5, 1.0);
        if (speed == 0.0) {
            summon.moveTo(summon.followPose(summon.position(), targetPosition));
            return;
        }
        Vec3 nextVelocity = summon.velocity().add(direction.normalize()).normalize().scale(speed);
        Vec3 wiggle = new Vec3(summon.owner().getRandom().nextGaussian(), summon.owner().getRandom().nextGaussian(), summon.owner().getRandom().nextGaussian()).scale(0.01);
        Vec3 nextPosition = summon.position().add(nextVelocity).add(wiggle);
        summon.moveTo(summon.followPose(nextPosition, targetPosition));
    }
}
