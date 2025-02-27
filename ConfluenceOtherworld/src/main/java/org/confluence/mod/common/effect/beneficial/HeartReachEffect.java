package org.confluence.mod.common.effect.beneficial;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.common.init.ModEffects;

public class HeartReachEffect extends MobEffect {
    public HeartReachEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xAA0099);
    }

    public static float getRange(LivingEntity living) {
        return living.hasEffect(ModEffects.HEART_REACH) ? 12.17F : 1.75F;
    }
}
