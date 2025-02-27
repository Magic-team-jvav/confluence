package org.confluence.mod.common.effect.harmful;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.common.init.ModDamageTypes;

public class CursedInfernoEffect extends MobEffect {    //诅咒火 缓慢失去生命 每秒损失2点生命
    public CursedInfernoEffect() {
        super(MobEffectCategory.HARMFUL, 0x228B22);
    }

    @Override
    public boolean applyEffectTick(LivingEntity living, int amplifier) {
        living.hurt(ModDamageTypes.of(living.level(), ModDamageTypes.CURSED_INFERNO), 2);
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int tick, int amplifier) {
        return tick % 20 == 0;
    }
}
