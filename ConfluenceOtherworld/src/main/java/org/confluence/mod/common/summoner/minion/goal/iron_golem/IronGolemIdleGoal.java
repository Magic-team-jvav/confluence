package org.confluence.mod.common.summoner.minion.goal.iron_golem;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoal;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.minion.IronGolemMinion;

public class IronGolemIdleGoal extends AttachmentEntityGoal<IronGolemMinion> {

    /** 前往待机位置的移动速度 */
    private static final double SPEED = 0.1;
    /** 离待机位置超过该距离才继续走 */
    private static final double IDLE_DISTANCE_SQR = 2.25;
    /** 超过该距离直接挪到待机位置 */
    private static final double TELEPORT_SQR = 1024.0;
    /** 重新寻路的间隔 */
    private static final int REPATH_INTERVAL = 10;

    private int repathTicks;

    public IronGolemIdleGoal(IronGolemMinion minion) {
        super(minion);
    }

    @Override
    public boolean canUse() {
        return minion.getTarget() == null;
    }

    @Override
    public void tick() {
        minion.getLookControl().lookForward();
        LivingEntity owner = minion.getOwner();
        if (minion.getPos().distanceToSqr(owner.position()) >= TELEPORT_SQR) {
            teleportToIdlePosition();
        } else if (--repathTicks <= 0) {
            repathTicks = REPATH_INTERVAL;
            Vec3 idlePos = minion.getIdlePosition();
            if (minion.getPos().distanceToSqr(idlePos) > IDLE_DISTANCE_SQR) {
                minion.getNavigation().moveTo(idlePos, 0.03);
            } else {
                minion.getNavigation().stop();
            }
        }
    }

    /**
     * 距离主人过远时直接挪到待机位置。
     */
    private void teleportToIdlePosition() {
        minion.init(new PathNode(minion.getIdlePosition(), minion.getYaw(), 0.0F, 0.0F));
        minion.setVelocity(Vec3.ZERO);
        minion.getNavigation().stop();
    }
}
