package org.confluence.mod.common.effect.harmful;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.mod.Confluence;

public class StonedEffect extends MobEffect {
    public static final ResourceLocation ID = Confluence.asResource("stoned");

    public StonedEffect() {
        super(MobEffectCategory.HARMFUL, 0x999999);
        addAttributeModifier(Attributes.SAFE_FALL_DISTANCE, ID, -3.0, AttributeModifier.Operation.ADD_VALUE);
        addAttributeModifier(Attributes.GRAVITY, ID, 0.02, AttributeModifier.Operation.ADD_VALUE);
    }
}
