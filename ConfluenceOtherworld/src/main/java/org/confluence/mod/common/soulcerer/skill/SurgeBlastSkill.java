package org.confluence.mod.common.soulcerer.skill;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.attachment.SoulStorage;
import org.confluence.mod.common.entity.soulcerer.SpiritSurgeEntity;
import org.confluence.mod.util.PlayerUtils;

/// 灵魂激荡冲击：每次施放都放一圈激荡，并累计层数；每满 {@link SoulSkillParameters#SURGE_BLAST_CHARGE_COUNT} 次
/// 额外在脚下引爆一次范围更大的终极爆炸，然后清空层数。
public class SurgeBlastSkill extends SoulSkill {
    public SurgeBlastSkill(ResourceLocation icon, ResourceLocation id) {
        super(icon, id, SoulSkillParameters.SURGE_BLAST_DAMAGE, Kind.ACTIVE,
                SoulSkillParameters.ACTIVE_SOUL_COST, SoulSkillParameters.ACTIVE_COOLDOWN_TICKS);
    }

    @Override
    public void use(Player player, SoulSkillStack skillStack) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        float damage = PlayerUtils.getSoulSkillDamage(player, skillStack);
        serverPlayer.level().addFreshEntity(new SpiritSurgeEntity(serverPlayer, damage,
                SoulSkillParameters.SPIRIT_SURGE_RADIUS, SoulSkillParameters.SPIRIT_SURGE_VERTICAL_RANGE,
                SoulSkillParameters.SPIRIT_SURGE_DURATION_TICKS, 0, 0.0F));
        SoulStorage storage = SoulStorage.of(serverPlayer);
        int charge = storage.getSurgeBlastCharge() + 1;
        if (charge >= SoulSkillParameters.SURGE_BLAST_CHARGE_COUNT) {
            storage.setSurgeBlastCharge(0);
            serverPlayer.level().addFreshEntity(new SpiritSurgeEntity(serverPlayer, SoulSkillParameters.SURGE_BLAST_FINISHER_DAMAGE,
                    SoulSkillParameters.SURGE_BLAST_FINISHER_RADIUS, SoulSkillParameters.SPIRIT_SURGE_VERTICAL_RANGE,
                    SoulSkillParameters.SPIRIT_SURGE_DURATION_TICKS, 0, 0.0F));
        } else {
            storage.setSurgeBlastCharge(charge);
        }
    }
}
