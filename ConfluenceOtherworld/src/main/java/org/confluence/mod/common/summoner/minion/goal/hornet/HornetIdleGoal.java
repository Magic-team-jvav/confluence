package org.confluence.mod.common.summoner.minion.goal.hornet;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoal;
import org.confluence.mod.common.summoner.minion.HornetMinion;

public class HornetIdleGoal extends AttachmentEntityGoal<HornetMinion> {
    private Vec3 offset = Vec3.ZERO;

    public HornetIdleGoal(HornetMinion minion) {
        super(minion);
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public void start() {
        LivingEntity owner = minion.getOwner();
        if (owner == null) {
            return;
        }
        Vec3 ownerCenter = owner.getBoundingBox().getCenter();
        Vec3 center = ownerCenter.add(0, owner.getBbHeight() + 1, 0).add(Vec3.ZERO.offsetRandom(minion.getRandom(), 0.25F));
        Vec3 direction = center.subtract(minion.getPos()).normalize();
        offset = center.add(direction.scale(1.25F)).subtract(ownerCenter);
    }

    @Override
    public void tick() {
        LivingEntity owner = minion.getOwner();
        if (owner == null) {
            return;
        }
        Vec3 targetPos = owner.getBoundingBox().getCenter().add(offset);
        Vec3 minionPos = minion.getPos();
        if (minionPos.distanceTo(targetPos) > 0.5) {
            minion.addVelocity(targetPos.subtract(minionPos).normalize().scale(0.04 * Math.min(1 + targetPos.distanceTo(minionPos) * 0.1, 2)));
            minion.lookAtPos(targetPos);
        } else {
            minion.setVelocity(minion.getVelocity().scale(0.6));
            if (minion.getVelocity().length() < 0.1) {
                start();
            }
        }
    }
}
