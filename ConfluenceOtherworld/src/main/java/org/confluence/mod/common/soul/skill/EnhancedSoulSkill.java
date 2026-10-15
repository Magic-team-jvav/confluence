package org.confluence.mod.common.soul.skill;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import org.confluence.mod.common.init.ModEffects;

public class EnhancedSoulSkill extends SoulSkill {
    public EnhancedSoulSkill(ResourceLocation id) {
        super(id, id, SoulSkillParameters.DEFAULT_SKILL_DAMAGE, SoulSkillParameters.DEFAULT_SOUL_COST, SoulSkillParameters.DEFAULT_COOLDOWN_TICKS);
    }

    @Override
    public boolean canCast(SoulSkillCastContext context) {
        return true;
    }

    @Override
    public void cast(SoulSkillCastContext context) {
        context.player().addEffect(new MobEffectInstance(ModEffects.ENHANCED_SOUL, SoulSkillParameters.ENHANCED_SOUL_DURATION_TICKS));
    }
}
