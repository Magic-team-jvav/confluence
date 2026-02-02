package org.confluence.terraentity.entity.ai.goal.summon;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import org.confluence.terraentity.api.entity.ISummonMob;
import org.confluence.terraentity.init.TEEffects;

import java.util.concurrent.atomic.AtomicInteger;

public class SummonPriorAttackGoal <T extends Mob & ISummonMob> extends TargetGoal {

    LivingEntity target;

    public SummonPriorAttackGoal(Mob mob, boolean mustSee) {
        super(mob, mustSee);
    }

    @Override
    public boolean canUse() {
        target = null;
        AtomicInteger remainTick = new AtomicInteger(-0x3f3f3f3f);
        mob.level().getEntities(mob, mob.getBoundingBox().inflate(mob.getAttributeValue(Attributes.FOLLOW_RANGE)), e -> e instanceof LivingEntity living && mob.canAttack(living) && living.isPickable())
                .forEach(tar -> {
                    if (tar instanceof LivingEntity living && living.isAlive() && living.hasEffect(TEEffects.SUMMON_FOCUS)) {
                        MobEffectInstance effect = living.getEffect(TEEffects.SUMMON_FOCUS);
                        if (effect!=null) {
                            int duration = effect.getDuration();
                            if (duration > remainTick.get()) {
                                target = living;
                                remainTick.set(duration);
                            }
                        }
                    }
                });
        return target != mob.getTarget() && target != null;
    }

    public void start() {
        if (target != null)
            mob.setTarget(target);
    }

    public boolean canContinueToUse() {
        if (target != null)
            mob.setTarget(target);
        return canUse();
    }
}