package org.confluence.mod.common.soul.skill;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.common.entity.soul.SpiritSurgeEntity;

public class SpiritSurgeSkill extends SoulSkill {
    public SpiritSurgeSkill(ResourceLocation id) {
        super(id, id, SoulSkillParameters.DEFAULT_SKILL_DAMAGE, SoulSkillParameters.DEFAULT_SOUL_COST, SoulSkillParameters.DEFAULT_COOLDOWN_TICKS);
    }

    @Override
    public boolean canCast(SoulSkillCastContext context) {
        return true;
    }

    @Override
    public void cast(SoulSkillCastContext context) {
        context.player().serverLevel().addFreshEntity(new SpiritSurgeEntity(context));
    }
}
