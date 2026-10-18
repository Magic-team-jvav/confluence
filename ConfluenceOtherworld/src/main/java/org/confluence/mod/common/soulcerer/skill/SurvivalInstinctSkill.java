package org.confluence.mod.common.soulcerer.skill;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.init.ModEffects;

public class SurvivalInstinctSkill extends SoulSkill {
    public SurvivalInstinctSkill(ResourceLocation icon, ResourceLocation id) {
        super(icon, id, 0.0F, SoulSkillParameters.SURVIVAL_INSTINCT_SOUL_COST, SoulSkillParameters.ACTIVE_COOLDOWN_TICKS);
    }

    /// 仅低血量且已有耐药性时允许主动施放。
    @Override
    public boolean canUse(ServerPlayer player, SoulSkillStack skillStack) {
        return player.getHealth() < SoulSkillParameters.SURVIVAL_INSTINCT_HEALTH_THRESHOLD && player.hasEffect(ModEffects.POTION_SICKNESS.get());
    }

    /// 成功施放后只减半一次耐药剩余时长。
    @Override
    public void use(Player player, SoulSkillStack skillStack) {
        if (player instanceof ServerPlayer) {
            MobEffectInstance effect = player.getEffect(ModEffects.POTION_SICKNESS.get());
            if (effect != null) {
                int duration = effect.isInfiniteDuration() ? effect.getDuration() : (int) (effect.getDuration() * SoulSkillParameters.SURVIVAL_INSTINCT_DURATION_RATIO);
                player.removeEffect(ModEffects.POTION_SICKNESS.get());
                if (duration != 0) {
                    player.addEffect(new MobEffectInstance(ModEffects.POTION_SICKNESS.get(), duration, effect.getAmplifier(), effect.isAmbient(), effect.isVisible(), effect.showIcon()));
                }
            }
        } else if (player.level().isClientSide) {
            player.level().addParticle(ParticleTypes.SOUL, player.getX(), player.getEyeY(), player.getZ(), 0, 0, 0);
        }
    }
}
