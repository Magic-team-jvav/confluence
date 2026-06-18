package org.confluence.mod.common.effect.harmful;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.init.ModEffects;
import org.mesdag.portlib.wrapper.common.PortEffectCure;
import org.mesdag.portlib.wrapper.world.effect.PortMobEffect;

import java.util.Set;
import java.util.function.Consumer;

public class BleedingEffect extends PortMobEffect { //流血 不能自然恢复生命
    public BleedingEffect() {
        super(MobEffectCategory.HARMFUL, 0xA52A2A);
    }

    @Override
    public void fillEffectCures(Set<PortEffectCure> cures, MobEffectInstance effectInstance) {
        super.fillEffectCures(cures, effectInstance);
        cures.add(LibUtils.DENY_HEAL);
    }

    public static void apply(LivingEntity entity, Consumer<Boolean> consumer) {
        if (entity.hasEffect(ModEffects.BLEEDING.get())) {
            consumer.accept(true);
        }
    }
}
