package org.confluence.mod.common.effect.harmful;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.effect.PublicMobEffect;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.util.OverworldUtils;

public class WaterCandleEffect extends PublicMobEffect {

    public static final ResourceLocation WATER_CANDLE = Confluence.asResource("water_candle");

    public WaterCandleEffect(MobEffectCategory category, int color, ParticleOptions particle) {
        super(category, color, particle);
    }

    public WaterCandleEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        var attributeInstance = livingEntity.getAttributes().getInstance(ConfluenceMagicLib.ENEMY_SPAWN_SPEED_MULTIPLIER);
        if (attributeInstance != null) {
            attributeInstance.addOrReplacePermanentModifier(
                    new AttributeModifier(WATER_CANDLE, amplifier *
                            OverworldUtils.getSpaceY() <= livingEntity.getY() && livingEntity.hasEffect(ModEffects.WATER_CANDLE) ?
                            1.67 : 0.33,
                            AttributeModifier.Operation.ADD_VALUE));
        }
        return super.applyEffectTick(livingEntity, amplifier);
    }
}
