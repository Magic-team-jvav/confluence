package org.confluence.mod.common.summoner.minion.goal.finch;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoal;
import org.confluence.mod.common.summoner.minion.FinchMinion;

public class FinchAttackGoal extends AttachmentEntityGoal<FinchMinion> {

    private Vec3 offset = Vec3.ZERO;

    public FinchAttackGoal(FinchMinion minion) {
        super(minion);
    }

    @Override
    public boolean canUse() {
        return minion.getTarget() != null;
    }

    @Override
    public void start() {
        Vec3 targetCenter = minion.getTarget().getBoundingBox().getCenter();
        Vec3 direction = targetCenter.subtract(minion.getPos()).normalize();
        offset = targetCenter.add(direction.scale(2F)).offsetRandom(minion.getRandom(), (float) minion.getTarget().getBoundingBox().getSize()).subtract(targetCenter);
    }

    @Override
    public void tick() {
        LivingEntity target = minion.getTarget();
        Vec3 targetPos = target.getBoundingBox().getCenter();
        Vec3 minionPos = minion.getPos();
        minion.lookAtPos(targetPos);
        Vec3 wanderPos = targetPos.add(offset);
        if (minionPos.distanceTo(wanderPos) > 1) {
            minion.addVelocity(wanderPos.subtract(minionPos).normalize().scale(0.03 * (1 + Math.min(wanderPos.distanceTo(minionPos) * 0.2, 8))));
        } else {
            minion.setVelocity(minion.getVelocity().scale(0.85));
            if (minion.getVelocity().length() < 0.1) {
                start();
            }
        }
    }
}
