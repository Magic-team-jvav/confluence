package org.confluence.mod.common.summoner.minion.goal.snow_flinx;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoal;
import org.confluence.mod.common.summoner.minion.SnowFlinxMinion;

public class SnowFlinxAttackGoal extends AttachmentEntityGoal<SnowFlinxMinion> {

    public SnowFlinxAttackGoal(SnowFlinxMinion minion) {
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
        if (target.position().subtract(minion.getPos()).horizontalDistance() < target.getBbWidth()) {
            Vec3 vec3 = targetPos.offsetRandom(minion.getRandom(), 0.25f).add(0, target.getBbHeight() - 0.75, 0);
            minion.addVelocity(vec3.subtract(minion.getPos()).scale(0.1f));
            if (minion.getPos().distanceTo(vec3) < 1) {
                minion.setVelocity(minion.getVelocity().scale(0.25f));
            }
        }
        minion.moveTo(targetPos, 0.09f);
    }
}
