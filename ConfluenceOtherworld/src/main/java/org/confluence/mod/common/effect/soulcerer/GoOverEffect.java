package org.confluence.mod.common.effect.soulcerer;

import net.minecraft.world.effect.MobEffectCategory;
import org.mesdag.portlib.wrapper.world.effect.PortMobEffect;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;
import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;

/// 复习：重复阅读已学会的技能书获得的短时增益，只提高灵魂伤害总倍率。
public class GoOverEffect extends PortMobEffect {
    public GoOverEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xC8A165);
        addAttributeModifier(LibAttributes.getSoulDamage(), Confluence.asResource("go_over"), SoulSkillParameters.GO_OVER_SOUL_DAMAGE_BONUS, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
}
