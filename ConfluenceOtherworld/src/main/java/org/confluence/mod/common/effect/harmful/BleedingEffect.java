package org.confluence.mod.common.effect.harmful;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.common.init.ModEffects;

import java.util.function.Consumer;

public class BleedingEffect extends MobEffect { //流血 不能自然恢复生命
    public BleedingEffect() {
        super(MobEffectCategory.HARMFUL, 0xA52A2A);
    }

    public static void apply(LivingEntity entity, Consumer<Boolean> consumer) {
        if (entity.hasEffect(ModEffects.BLEEDING)) {
            consumer.accept(true);
        }
    }
}
