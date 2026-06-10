package org.confluence.mod.common.effect.harmful;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.mod.common.init.ModEffects;
import org.mesdag.particlestorm.api.MolangParticleMobEffect;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

public class StinkyEffect extends MolangParticleMobEffect {
    public StinkyEffect(ResourceLocation id) {
        super(MobEffectCategory.HARMFUL, 0x99FF00, id);
        getAttributeModifiers().put(Attributes.LUCK, new AttributeModifier(PortAttributeModifier.rl2uuid(id), id.getPath(), -0.25, AttributeModifier.Operation.ADDITION));
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.isInWater()) {
            entity.removeEffect(ModEffects.STINKY.get());
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
