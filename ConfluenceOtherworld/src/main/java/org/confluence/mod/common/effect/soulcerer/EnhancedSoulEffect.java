package org.confluence.mod.common.effect.soulcerer;

import org.mesdag.portlib.wrapper.world.effect.PortMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;
import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;

public class EnhancedSoulEffect extends PortMobEffect {
    public EnhancedSoulEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x6080FF);
        addAttributeModifier(LibAttributes.getSoulDamage(), Confluence.asResource("enhanced_soul"), SoulSkillParameters.ENHANCED_SOUL_DAMAGE_MULTIPLIER - 1.0, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
}
