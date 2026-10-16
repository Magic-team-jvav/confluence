package org.confluence.mod.common.soulcerer.skill;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.entity.soulcerer.SpiritSurgeEntity;
import org.confluence.mod.util.PlayerUtils;

public class SpiritSurgeSkill extends SoulSkill {
    public SpiritSurgeSkill(ResourceLocation icon, ResourceLocation id) {
        super(icon, id, SoulSkillParameters.ACTIVE_DAMAGE, SoulSkillParameters.ACTIVE_SOUL_COST, SoulSkillParameters.ACTIVE_COOLDOWN_TICKS);
    }

    /// 服务端创建范围冲击波，客户端仅表现施法成功。
    @Override
    public void use(Player player, SoulSkillStack skillStack) {
        if (player instanceof ServerPlayer serverPlayer) {
            player.level().addFreshEntity(new SpiritSurgeEntity(serverPlayer, PlayerUtils.getSoulSkillDamage(player, skillStack)));
        } else if (player.level().isClientSide) {
            player.level().addParticle(ParticleTypes.SOUL, player.getX(), player.getEyeY(), player.getZ(), 0, 0, 0);
        }
    }
}
