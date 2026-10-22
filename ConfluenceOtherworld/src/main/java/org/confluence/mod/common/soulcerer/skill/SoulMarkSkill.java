package org.confluence.mod.common.soulcerer.skill;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.common.entity.soulcerer.SoulLureProjectile;
import org.confluence.mod.common.init.ModEffects;

import java.util.List;

/// 附魂印记：发射一枚灵魂弹幕，**命中敌人给它挂上 10 秒「附魂印记」debuff**。
///
/// 印记不加任何伤害：之后**所有技能**命中这只敌人，玩家各回 1 点灵魂；击杀它再回 2 点灵魂。
/// 命中时的挂印记在 `SoulLureProjectile#applySoulMark`，回魂结算在
/// `LivingEntityEvents#soulWeaponTraitsOnSkillHit` 与 `#deathSoul`。
public class SoulMarkSkill extends SoulProjectileSkill {
    public SoulMarkSkill(ResourceLocation icon, ResourceLocation id) {
        super(icon, id, builder(SoulLureProjectile.PARTICLE)
                .damage(SoulSkillParameters.ACTIVE_DAMAGE)
                .cost(SoulSkillParameters.SOUL_MARK_SOUL_COST, SoulSkillParameters.SOUL_MARK_COOLDOWN_TICKS));
    }

    @Override
    protected void modifyProjectile(SoulLureProjectile projectile) {
        projectile.setSoulMarkOnHit(SoulSkillParameters.SOUL_MARK_DURATION_TICKS);
    }

    /// tooltip 列出印记时长（效果名取「附魂印记」这个 debuff 本身）。
    @Override
    public List<BuffDuration> getBuffDurations(SoulSkillStack skillStack) {
        return List.of(new BuffDuration(ModEffects.SOUL_MARK.get().getDisplayName(), SoulSkillParameters.SOUL_MARK_DURATION_TICKS));
    }
}
