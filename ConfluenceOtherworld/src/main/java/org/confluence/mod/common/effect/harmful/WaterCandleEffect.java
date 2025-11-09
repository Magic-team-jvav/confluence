package org.confluence.mod.common.effect.harmful;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.Confluence;
import org.confluence.mod.util.OverworldUtils;

public class WaterCandleEffect extends MobEffect {
    public static final ResourceLocation WATER_CANDLE = Confluence.asResource("water_candle");

    public WaterCandleEffect() {
        super(MobEffectCategory.HARMFUL, 0xFF0000);
        addAttributeModifier(ConfluenceMagicLib.ENEMY_SPAWN_SPEED_MULTIPLIER, WATER_CANDLE, AttributeModifier.Operation.ADD_VALUE, amplifier -> (amplifier + 1) * 0.33);
        addAttributeModifier(ConfluenceMagicLib.ENEMY_SPAWN_COUNT_MULTIPLIER, WATER_CANDLE, AttributeModifier.Operation.ADD_VALUE, amplifier -> (amplifier + 1) * 0.50);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        AttributeInstance instance = livingEntity.getAttributes().getInstance(ConfluenceMagicLib.ENEMY_SPAWN_SPEED_MULTIPLIER);
        if (instance != null) {
            double amount = (amplifier + 1) * (livingEntity.getY() >= OverworldUtils.getSpaceY() ? 1.67 : 0.33);
            AttributeModifier modifier = instance.getModifier(WATER_CANDLE);
            if (modifier == null || modifier.amount() != amount) {
                instance.addOrReplacePermanentModifier(new AttributeModifier(WATER_CANDLE, amount, AttributeModifier.Operation.ADD_VALUE));
            }
        }
        return super.applyEffectTick(livingEntity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
