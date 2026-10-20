package org.confluence.mod.common.soulcerer.skill;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.mod.common.entity.EnemyDamageRules;
import org.confluence.mod.util.PlayerUtils;

/// 灭却业火：消耗全部灵魂，直接处决附近生命值低于消耗量的目标。
///
/// 灵魂消耗发生在 `use` 内部（基础消耗为 0），因为“全部灵魂”只有在施放瞬间才知道。
public class KarmaFlameSkill extends SoulSkill {
    public KarmaFlameSkill(ResourceLocation icon, ResourceLocation id) {
        super(icon, id, 0.0F, Kind.ACTIVE, 0.0F, SoulSkillParameters.KARMA_FLAME_COOLDOWN_TICKS);
    }

    /// 至少要有 1 点灵魂才有意义。
    @Override
    public boolean canUse(ServerPlayer player, SoulSkillStack skillStack) {
        return player.isCreative() || PlayerUtils.getSoul(player) >= 1.0F;
    }

    @Override
    public void use(Player player, SoulSkillStack skillStack) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        float soul = PlayerUtils.getSoul(serverPlayer);
        if (soul < 1.0F || !PlayerUtils.consumeSoul(serverPlayer, soul)) {
            return;
        }
        for (LivingEntity target : serverPlayer.level().getEntitiesOfClass(LivingEntity.class,
                serverPlayer.getBoundingBox().inflate(SoulSkillParameters.KARMA_FLAME_RADIUS),
                target -> target.isAlive() && !target.isSpectator() && !target.isAlliedTo(serverPlayer)
                        && EnemyDamageRules.isEnemy(target))) {
            if (target.getHealth() < soul) {
                target.hurt(LibDamageTypes.soul(serverPlayer.level(), serverPlayer, serverPlayer), target.getHealth() + 1.0F);
            }
        }
    }
}
