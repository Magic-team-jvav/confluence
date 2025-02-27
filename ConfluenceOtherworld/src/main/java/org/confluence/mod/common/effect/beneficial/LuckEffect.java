package org.confluence.mod.common.effect.beneficial;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEffects;

public class LuckEffect extends MobEffect {
    public static final ResourceLocation ID = Confluence.asResource("luck");

    public LuckEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x39C5BB);
        addAttributeModifier(Attributes.LUCK, ID, AttributeModifier.Operation.ADD_VALUE, amplifier -> (amplifier + 1) * 0.5);
    }

    public static void onRemove(LivingEntity living, Holder<MobEffect> mobEffect, int amplifier) {
        if (amplifier > 0 && mobEffect == ModEffects.LUCK_EFFECT) {
            living.addEffect(new MobEffectInstance(mobEffect, 6000, amplifier - 1));
        }
    }
}
