package org.confluence.mod.common.effect.harmful;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

/// 极昼灼烧：被极昼箭矢命中的敌人会持续受到额外伤害。
public class PolarMarkEffect extends MobEffect {
    /// 每秒跳动一次的固定伤害。
    private static final float DAMAGE_PER_TICK = 2.0F;

    public PolarMarkEffect() {
        super(MobEffectCategory.HARMFUL, 0xFFD966);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        livingEntity.hurt(livingEntity.damageSources().magic(), DAMAGE_PER_TICK + amplifier);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
