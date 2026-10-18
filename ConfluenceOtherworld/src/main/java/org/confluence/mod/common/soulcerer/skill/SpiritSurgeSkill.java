package org.confluence.mod.common.soulcerer.skill;

import net.minecraft.resources.ResourceLocation;

/// 灵魂激荡：默认参数的冲击波技能。
public class SpiritSurgeSkill extends SoulSurgeSkill {
    public SpiritSurgeSkill(ResourceLocation icon, ResourceLocation id) {
        super(icon, id, SoulSkillParameters.ACTIVE_DAMAGE, SoulSkillParameters.ACTIVE_SOUL_COST,
                SoulSkillParameters.ACTIVE_COOLDOWN_TICKS, SoulSkillParameters.SPIRIT_SURGE_RADIUS,
                SoulSkillParameters.SPIRIT_SURGE_VERTICAL_RANGE, SoulSkillParameters.SPIRIT_SURGE_DURATION_TICKS, 0, 0.0F);
    }
}
