package org.confluence.mod.common.summoner.minion.goal.eye_laser_turret;

import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoal;
import org.confluence.mod.common.summoner.minion.EyeLaserTurretMinion;

public class EyeLaserTurretAttackGoal extends AttachmentEntityGoal<EyeLaserTurretMinion> {

    public EyeLaserTurretAttackGoal(EyeLaserTurretMinion minion) {
        super(minion);
    }

    @Override
    public boolean canUse() {
        return minion.getTarget() != null;
    }

    @Override
    public void tick() {
        LivingEntity target = minion.getTarget();
        minion.lookAtPos(target.getBoundingBox().getCenter());
        if (minion.getCooldown() <= 0) {
            minion.shootFireball(target);
            minion.setCooldown(20);
        }
    }
}
