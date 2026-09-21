package org.confluence.mod.common.summoner.minion.goal.eye_laser_turret;

import net.minecraft.world.phys.Vec3;
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
        } else {
            minion.lookAtPos(minion.getPos().add(1, 0, 0));
        }
    }
}
