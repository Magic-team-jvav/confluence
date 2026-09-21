package org.confluence.mod.common.summoner.minion.goal.vampire_frog;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoal;
import org.confluence.mod.common.summoner.minion.VampireFrogMinion;

public class VampireFrogAttackGoal extends AttachmentEntityGoal<VampireFrogMinion> {

    private int cooldown;

    public VampireFrogAttackGoal(VampireFrogMinion minion) {
        super(minion);
    }

    @Override
    public boolean canUse() {
        return minion.getTarget() != null;
    }

    @Override
    public void tick() {
        LivingEntity target = minion.getTarget();
        Vec3 targetPos = target.getBoundingBox().getCenter();
        minion.lookAtPos(targetPos);
        cooldown--;
        if (cooldown == 10) {
            minion.attack(target, minion.getDamage(), 3);
        }
        if (minion.getPos().distanceTo(targetPos) < 3) {
            if (cooldown <= 0) {
                cooldown = 20;
                minion.attackTime = minion.getTickCount();
            }
        } else {
            if (cooldown <= 0) {
                minion.moveTo(targetPos, 0.06f);
            }
        }
    }
}
