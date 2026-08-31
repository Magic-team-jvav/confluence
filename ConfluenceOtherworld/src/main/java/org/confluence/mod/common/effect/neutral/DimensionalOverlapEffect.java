package org.confluence.mod.common.effect.neutral;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.lib.common.effect.PublicMobEffect;
import org.confluence.mod.common.util.VoidSeaHelper;

/**
 * 位面交叠
 */
public class DimensionalOverlapEffect extends PublicMobEffect {
    public DimensionalOverlapEffect() {
        super(MobEffectCategory.NEUTRAL, 0xAA0000);
    }

    @Override
    public boolean applyEffectTick(LivingEntity living, int amplifier) {
        if (!VoidSeaHelper.isVoidErosionDeltaDamage(living)) {
            return true;
        }
        int v = (int) (living.getY() - VoidSeaHelper.getVoidErosionDeltaDamageHeight(living));
        if (v <= 0) {
            return false;
        }
        living.hurt(living.damageSources().fellOutOfWorld(), -v);
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int tick, int amplifier) {
        return tick % 10 == 0;
    }
}
