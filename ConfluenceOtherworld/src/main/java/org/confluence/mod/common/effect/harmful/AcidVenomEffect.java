package org.confluence.mod.common.effect.harmful;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.lib.common.LibDamageTypes;
import org.mesdag.portlib.wrapper.world.effect.PortMobEffect;

/// 酸性毒液：缓慢失去生命 每秒损失1点生命
public class AcidVenomEffect extends PortMobEffect {
    public AcidVenomEffect() {
        super(MobEffectCategory.HARMFUL, 0x228B22);
    }

    @Override
    public void applyEffectTick(LivingEntity living, int amplifier) {
        living.hurt(LibDamageTypes.of(living.level(), LibDamageTypes.ACID_VENOM), 1.0F);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int tick, int amplifier) {
        return tick % 20 == 0;
    }
}
