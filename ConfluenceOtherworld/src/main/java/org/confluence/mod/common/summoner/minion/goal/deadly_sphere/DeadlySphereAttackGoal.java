package org.confluence.mod.common.summoner.minion.goal.deadly_sphere;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoal;
import org.confluence.mod.common.summoner.minion.DeadlySphereMinion;

public class DeadlySphereAttackGoal extends AttachmentEntityGoal<DeadlySphereMinion> {

    private int cooldown;
    private int attacks;

    public DeadlySphereAttackGoal(DeadlySphereMinion minion) {
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
        if (--cooldown < 0) {
            Vec3 direction = targetPos.subtract(minion.getPos());
            if (direction.lengthSqr() > 1.0E-6) {
                minion.addVelocity(direction.normalize().scale(2.0));
            }
            attacks++;
            if (attacks % 3 == 0) {
                minion.setForm(minion.getForm() + 1);
                cooldown = 25;
            } else {
                cooldown = 10 + minion.getRandom().nextIntBetweenInclusive(-2, 2);
            }
        } else {
            minion.setVelocity(minion.getVelocity().scale(0.8));
        }
    }
}
