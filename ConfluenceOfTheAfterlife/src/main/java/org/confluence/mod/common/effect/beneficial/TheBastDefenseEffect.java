package org.confluence.mod.common.effect.beneficial;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.mod.Confluence;

public class TheBastDefenseEffect extends MobEffect {
    public static final ResourceLocation ID = Confluence.asResource("the_bast_defense");

    public TheBastDefenseEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x000000);
        addAttributeModifier(Attributes.ARMOR, ID, 5.0, AttributeModifier.Operation.ADD_VALUE);
    }
}
