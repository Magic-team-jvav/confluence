package org.confluence.mod.common.soulcerer.skill;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.common.entity.soulcerer.SoulLureProjectile;
import org.confluence.mod.common.init.ModEffects;

import java.util.List;

/// 附魂印记：发射一枚灵魂弹幕，**命中敌人**给 3 秒强力附魂，**击杀敌人**给 8 秒强力附魂。
///
/// 施放瞬间不再给自身增益，增益改由弹幕命中结算（见 `SoulLureProjectile#grantEnhancedSoul`）。
public class SoulMarkSkill extends SoulProjectileSkill {
    public SoulMarkSkill(ResourceLocation icon, ResourceLocation id) {
        super(icon, id, builder(SoulLureProjectile.PARTICLE)
                .damage(SoulSkillParameters.ACTIVE_DAMAGE)
                .cost(SoulSkillParameters.SOUL_MARK_SOUL_COST, SoulSkillParameters.SOUL_MARK_COOLDOWN_TICKS));
    }

    @Override
    protected void modifyProjectile(SoulLureProjectile projectile) {
        projectile.setEnhancedSoulOnHit(SoulSkillParameters.SOUL_MARK_HIT_BUFF_TICKS);
    }

    /// tooltip 列出命中时长；击杀的更长时长写在说明里。
    @Override
    public List<BuffDuration> getBuffDurations(SoulSkillStack skillStack) {
        return List.of(new BuffDuration(ModEffects.ENHANCED_SOUL.get().getDisplayName(), SoulSkillParameters.SOUL_MARK_HIT_BUFF_TICKS));
    }
}
