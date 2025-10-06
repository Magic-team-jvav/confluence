package org.confluence.mod.common.effect.harmful;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.mod.Confluence;

public class BrokenArmorEffect extends MobEffect {
    public static final ResourceLocation ID = Confluence.asResource("broken_armor");

    public BrokenArmorEffect() {
        super(MobEffectCategory.HARMFUL, 0x330088);
        addAttributeModifier(Attributes.ARMOR, ID, -0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
}
