package org.confluence.mod.common.summoner.minion.goal.eye_laser_turret;

import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoal;
import org.confluence.mod.common.summoner.minion.EyeLaserTurretMinion;

public class EyeLaserTurretIdleGoal extends AttachmentEntityGoal<EyeLaserTurretMinion> {

    public EyeLaserTurretIdleGoal(EyeLaserTurretMinion minion) {
        super(minion);
    }

    @Override
    public boolean canUse() {
        return minion.getTarget() == null;
    }

    @Override
    public void tick() {
        if (minion.getPos().distanceTo(minion.getOwner().position()) < 6.0) {
            minion.lookAtPos(minion.getOwner().getEyePosition());
        }
    }
}
