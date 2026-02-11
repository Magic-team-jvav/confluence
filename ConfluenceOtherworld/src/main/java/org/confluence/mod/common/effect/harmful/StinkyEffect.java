package org.confluence.mod.common.effect.harmful;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEffects;
import org.mesdag.particlestorm.api.MolangParticleMobEffect;

public class StinkyEffect extends MolangParticleMobEffect {
    public static final ResourceLocation ID = Confluence.asResource("stinky");

    public StinkyEffect() {
        super(MobEffectCategory.HARMFUL, 0x99FF00, ID);
        addAttributeModifier(Attributes.LUCK, ID, -0.25, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.isInWater()) {
            entity.removeEffect(ModEffects.STINKY);
            return false;
        }
        return true;
    }
}
