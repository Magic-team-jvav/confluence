package org.confluence.mod.common.summoner.minion.goal.iron_golem;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
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
        Vec3 targetPos = target.getBoundingBox().getCenter();
        minion.lookAtPos(targetPos);
        cooldown--;
        if (minion.getPos().distanceTo(targetPos) < 2) {
            if (cooldown <= 0) {
                cooldown = 20;
                minion.attackTime = minion.getTickCount();
            }
            if (cooldown == 10) {
                attack(target);
            }
        } else {
            if (cooldown <= 0) {
                minion.moveTo(targetPos, 0.06f);
            }
        }
    }

    /** 造成一次近战伤害，并播放攻击动画、音效与上挑击退。 */
    private void attack(LivingEntity target) {
        minion.attack(target, minion.getDamage(), 10);
        if (!(target instanceof BaseBoss)) {
            double resistance = target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
            target.setDeltaMovement(target.getDeltaMovement().add(0.0, 0.4 * minion.getKnockback() * Math.max(0.0, 1.0 - resistance), 0.0));
            target.hasImpulse = true;
        }
        minion.getLevel().playSound(null, minion.getPos().x, minion.getPos().y, minion.getPos().z, SoundEvents.IRON_GOLEM_ATTACK, minion.getSoundSource(), 1.0F, 1.0F);
    }
}
