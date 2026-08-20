package org.confluence.mod.common.summon.terraprisma;

import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summon.SummonGoal;

/// 让泰拉棱镜在没有目标时回到主人背后。
///
/// <p>服务端只负责把运行位置收回背部锚点，并同步“正在跟随主人”的状态。
/// 背负角度、层级摆动和动态染色由客户端根据玩家身体朝向绘制，避免服务端与客户端重复叠加姿态。</p>
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
            summon.moveTo(summon.followPose(summon.position().add(summon.velocity().scale(0.91)), targetPosition));
            return;
        }
        Vec3 nextVelocity = summon.velocity().scale(0.91).add(direction.normalize()).normalize().scale(speed);
        Vec3 wiggle = new Vec3(summon.owner().getRandom().nextGaussian(), summon.owner().getRandom().nextGaussian(),
                summon.owner().getRandom().nextGaussian()).scale(0.01);
        Vec3 nextPosition = summon.position().add(nextVelocity).add(wiggle);
        summon.moveTo(summon.followPose(nextPosition, targetPosition));
    }
}
