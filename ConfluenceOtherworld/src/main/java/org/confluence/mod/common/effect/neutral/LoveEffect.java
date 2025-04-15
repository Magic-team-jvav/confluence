package org.confluence.mod.common.effect.neutral;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.init.ModEffects;

public class LoveEffect extends MobEffect {
    public LoveEffect() {
        super(MobEffectCategory.NEUTRAL, 0xEE0000);
    }

    public static void onAdd(MobEffectInstance mobEffectInstance, LivingEntity living, Entity entity) {
        if (mobEffectInstance.is(ModEffects.LOVE) && living instanceof Animal animal && !animal.isBaby()) {
            animal.setInLove(entity instanceof Player player ? player : null);
        }
    }
}
