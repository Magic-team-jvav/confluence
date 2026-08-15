package org.confluence.mod.common.summon.terraprisma;

import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summon.SummonGoal;
import org.confluence.mod.common.summon.SummonPose;

/**
 * 让泰拉棱镜在没有目标时回到主人背后。
 *
 * <p>服务端只负责把运行位置收回背部锚点，并同步“正在跟随主人”的状态。
 * 背负角度、层级摆动和动态染色由客户端根据玩家身体朝向绘制，避免服务端与客户端重复叠加姿态。</p>
 */
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
        Vec3 targetPosition = summon.followPosition();
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
