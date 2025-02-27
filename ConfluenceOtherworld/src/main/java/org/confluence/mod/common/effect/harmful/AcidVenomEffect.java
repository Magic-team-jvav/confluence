package org.confluence.mod.common.effect.harmful;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.common.init.ModDamageTypes;

/**
 * 酸性毒液：缓慢失去生命 每秒损失1点生命
 */
public class AcidVenomEffect extends MobEffect {
    public AcidVenomEffect() {
        super(MobEffectCategory.HARMFUL, 0x228B22);
    }

    @Override
    public boolean applyEffectTick(LivingEntity living, int amplifier) {
        living.hurt(ModDamageTypes.of(living.level(), ModDamageTypes.ACID_VENOM), 1.0F);
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int tick, int amplifier) {
        return tick % 20 == 0;
    }
}
