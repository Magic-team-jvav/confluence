package org.confluence.mod.common.effect.soulcerer;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;

public class EnhancedSoulEffect extends MobEffect {
    public EnhancedSoulEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x6080FF);
        addAttributeModifier(LibAttributes.getSoulDamage(), Confluence.asResource("enhanced_soul"), (double) SoulSkillParameters.ENHANCED_SOUL_DAMAGE_BONUS, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
}
