package org.confluence.mod.common.summon.sword;

import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summon.SummonGoal;

/// 控制召唤剑在没有目标时回到玩家背后。
///
/// <p>攻击行为仍保持 1.21 侧的追击与斜劈语义；这里仅负责待机跟随。
/// 服务端锚点需要与客户端背剑绘制使用同一套坐标，避免玩家移动时出现拉扯和错位。</p>
final class SwordFollowOwnerGoal extends SummonGoal<SummonSword> {
    SwordFollowOwnerGoal(SummonSword summon) {
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
        double backDistance = Math.max(0.25, 0.6F - 0.05F * (sequence - 1));
        Vec3 targetPosition = summon.owner().position().subtract(forward.scale(backDistance))
                .add(0.0, 1.0, 0.0)
                .add(right.scale(0.2F * (sequence / 2) * ((sequence & 1) == 0 ? 1.0F : -1.0F)));
        Vec3 offset = targetPosition.subtract(summon.position());
        double distance = offset.length();
        if (distance > 4.0) {
            summon.moveTo(summon.followPose(targetPosition, targetPosition));
            return;
        }
        double speed = Math.min(distance * 0.75, 1.2);
        Vec3 movement = speed == 0.0 ? Vec3.ZERO : offset.normalize().scale(speed);
        Vec3 nextPosition = summon.position().add(movement);
        summon.moveTo(summon.followPose(nextPosition, targetPosition));
    }
}
