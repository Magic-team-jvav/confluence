package org.confluence.mod.common.effect.harmful;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.EffectCure;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.init.ModEffects;

import java.util.Set;
import java.util.function.Consumer;

public class BleedingEffect extends MobEffect { //流血 不能自然恢复生命
    public BleedingEffect() {
        super(MobEffectCategory.HARMFUL, 0xA52A2A);
    }

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
        super.fillEffectCures(cures, effectInstance);
        cures.add(LibUtils.DENY_HEAL);
    }

    public static void apply(LivingEntity entity, Consumer<Boolean> consumer) {
        if (entity.hasEffect(ModEffects.BLEEDING)) {
            consumer.accept(true);
        }
    }
}
