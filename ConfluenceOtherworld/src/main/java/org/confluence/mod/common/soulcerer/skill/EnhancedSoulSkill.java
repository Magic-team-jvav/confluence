package org.confluence.mod.common.soulcerer.skill;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.ModEffects;

/// 强力附魂：给自己挂一层大幅提升灵魂伤害的增益（附魂类技能）。
public class EnhancedSoulSkill extends SoulSkill {
    public EnhancedSoulSkill(ResourceLocation icon, ResourceLocation id) {
        super(icon, id, 0.0F, Kind.BUFF, SoulSkillParameters.ACTIVE_SOUL_COST, SoulSkillParameters.SOUL_BUFF_COOLDOWN_TICKS);
    }

    /// 服务端添加灵魂伤害增益，客户端只生成施法粒子。
    @Override
    public void use(Player player, SoulSkillStack skillStack) {
        if (player instanceof ServerPlayer) {
            player.addEffect(new MobEffectInstance(ModEffects.ENHANCED_SOUL, SoulSkillParameters.ENHANCED_SOUL_DURATION_TICKS));
        }
        Level level = player.level();
        if (level.isClientSide) {
            level.addParticle(ParticleTypes.SOUL, player.getX(), player.getEyeY(), player.getZ(), 0, 0, 0);
        }
    }
}
