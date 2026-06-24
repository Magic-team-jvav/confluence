package org.confluence.mod.common.entity.ai.goal.summon;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.confluence.mod.common.api.entity.ISummonMob;

import java.util.EnumSet;

public class SummonOwnerHurtByTargetGoal<T extends Mob & ISummonMob> extends TargetGoal {
    private static final TargetingConditions FOR_COMBAT = TargetingConditions.forCombat(); // 只有可被攻击的实体才会被设置为目标
    private final T tameAnimal;
    private LivingEntity ownerLastHurtBy;
    private int timestamp;

    public SummonOwnerHurtByTargetGoal(T tameAnimal) {
        super(tameAnimal, false);
        this.tameAnimal = tameAnimal;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (this.tameAnimal.summon_isTame()) {
            LivingEntity livingentity = this.tameAnimal.summon_getOwner();
            if (livingentity == null) {
                return false;
            } else {
                this.ownerLastHurtBy = livingentity.getLastHurtByMob();
                int i = livingentity.getLastHurtByMobTimestamp();
                return i != this.timestamp && this.canAttack(this.ownerLastHurtBy, FOR_COMBAT) && this.tameAnimal.summon_wantsToAttack(this.ownerLastHurtBy, livingentity);
            }
        } else {
            return false;
        }
    }

    @Override
    public void start() {
        this.mob.setTarget(this.ownerLastHurtBy);
        LivingEntity livingentity = this.tameAnimal.summon_getOwner();
        if (livingentity != null) {
            this.timestamp = livingentity.getLastHurtByMobTimestamp();
        }

        super.start();
    }
}
