package org.confluence.mod.common.effect.harmful;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.mod.Confluence;

public class IchorEffect extends MobEffect {    //灵液 护甲值-5
    public static final ResourceLocation ID = Confluence.asResource("ichor");

    public IchorEffect() {
        super(MobEffectCategory.HARMFUL, 0xFFD700);
        addAttributeModifier(Attributes.ARMOR, ID, -5, AttributeModifier.Operation.ADD_VALUE);
    }
}
