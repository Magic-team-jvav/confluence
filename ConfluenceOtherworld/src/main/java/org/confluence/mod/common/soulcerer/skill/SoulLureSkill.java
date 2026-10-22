package org.confluence.mod.common.soulcerer.skill;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.entity.soulcerer.SoulLureProjectile;
import org.confluence.mod.util.PlayerUtils;

public class SoulLureSkill extends SoulSkill {
    public SoulLureSkill(ResourceLocation icon, ResourceLocation id) {
        super(icon, id, SoulSkillParameters.ACTIVE_DAMAGE, SoulSkillParameters.ACTIVE_SOUL_COST, SoulSkillParameters.SOUL_LURE_COOLDOWN_TICKS);
    }

    /// 服务端生成单体灵魂弹幕，客户端仅表现施法成功。
    ///
    /// 弹幕自带**弱追踪**（见 `SoulLureProjectile#setWeakHoming`）：略微朝前方敌怪偏转，改善手感。
    @Override
    public void use(Player player, SoulSkillStack skillStack) {
        if (player instanceof ServerPlayer serverPlayer) {
            SoulLureProjectile projectile = new SoulLureProjectile(serverPlayer, PlayerUtils.getSoulSkillDamage(player, skillStack));
            projectile.setWeakHoming(true);
            player.level().addFreshEntity(projectile);
        } else if (player.level().isClientSide) {
            player.level().addParticle(ParticleTypes.SOUL, player.getX(), player.getEyeY(), player.getZ(), 0, 0, 0);
        }
    }
}
