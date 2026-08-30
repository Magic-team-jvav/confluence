package org.confluence.mod.common.effect.harmful;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.util.OverworldUtils;

public class WaterCandleEffect extends MobEffect {
    private final ResourceLocation id;

    public WaterCandleEffect(ResourceLocation id) {
        super(MobEffectCategory.HARMFUL, 0xa8d5e6);
        this.id = id;
        addAttributeModifier(ConfluenceMagicLib.MOB_SPAWN_SPEED_MULTIPLIER, id, AttributeModifier.Operation.ADD_VALUE, amplifier -> (amplifier + 1) * 0.17);
        addAttributeModifier(ConfluenceMagicLib.MOB_SPAWN_COUNT_MULTIPLIER, id, AttributeModifier.Operation.ADD_VALUE, amplifier -> (amplifier + 1) * 0.25);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        AttributeInstance instance = livingEntity.getAttributes().getInstance(ConfluenceMagicLib.MOB_SPAWN_SPEED_MULTIPLIER);
        if (instance != null) {
            double amount = (amplifier + 1) * (livingEntity.getY() >= OverworldUtils.getSpaceY() ? 0.8 : 0.17);
            AttributeModifier modifier = instance.getModifier(id);
            if (modifier == null || modifier.amount() != amount) {
                instance.addOrReplacePermanentModifier(new AttributeModifier(id, amount, AttributeModifier.Operation.ADD_VALUE));
            }
        }
        return super.applyEffectTick(livingEntity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
