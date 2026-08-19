package org.confluence.mod.common.summon.projectile;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.common.summon.SummonInstance;

/// 召唤黄蜂的毒刺附件弹幕。
public final class HornetStingerAttachment extends SummonProjectileInstance {
    public HornetStingerAttachment(SummonInstance source, LivingEntity target) {
        super(SummonProjectileTypes.HORNET_STINGER.id(), source, target, 2.0F, 0.0F);
    }

    @Override
    protected void onSuccessfulHit(LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.POISON, 100), owner());
    }
}
