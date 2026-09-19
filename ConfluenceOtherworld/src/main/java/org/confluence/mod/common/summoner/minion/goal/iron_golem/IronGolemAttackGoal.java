package org.confluence.mod.common.summoner.minion.goal.iron_golem;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.mod.common.entity.boss.BaseBoss;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoal;
import org.confluence.mod.common.summoner.minion.IronGolemMinion;

public class IronGolemAttackGoal extends AttachmentEntityGoal<IronGolemMinion> {

    private int cooldown;

    public IronGolemAttackGoal(IronGolemMinion minion) {
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
        cooldown--;
        if (minion.getPos().distanceTo(target.position()) > 2) {
            minion.getNavigation().moveTo(target, 0.1F);
        } else {
            minion.getNavigation().stop();
            if (cooldown <= 0) {
                cooldown = 20;
                minion.attackTime = minion.getTickCount();
            }
            if (cooldown == 10) {
                minion.attack(target, minion.getDamage(), 10);
                if (!(target instanceof BaseBoss)) {
                    double resistance = target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
                    target.setDeltaMovement(target.getDeltaMovement().add(0.0, 0.4 * minion.getKnockback() * Math.max(0.0, 1.0 - resistance), 0.0));
                    target.hasImpulse = true;
                }
                minion.getLevel().playSound(null, minion.getPos().x, minion.getPos().y, minion.getPos().z, SoundEvents.IRON_GOLEM_ATTACK, minion.getSoundSource(), 1.0F, 1.0F);
            }
        }
    }
}
