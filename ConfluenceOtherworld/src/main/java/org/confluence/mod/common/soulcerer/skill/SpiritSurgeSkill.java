package org.confluence.mod.common.soulcerer.skill;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.entity.soulcerer.SpiritSurgeEntity;
import org.confluence.mod.common.item.soulcerer.SoulBooks;
import org.confluence.mod.util.PlayerUtils;

/// 灵魂激荡：默认参数的冲击波技能，**开局即解锁**（见 `SoulSkillStorage` 的默认解锁表）。
///
/// 学会「灵魂回响」技能书后，每次释放都有 {@link SoulSkillParameters#SOUL_ECHO_CHANCE} 的概率
/// 原地再触发一次灵魂激荡；这一发是书籍给的额外触发，**不消耗灵魂**，也不会继续连锁。
/// 附魂激荡的自动释放同样走 {@link #rollSoulEcho}，因此也吃这条被动。
public class SpiritSurgeSkill extends SoulSurgeSkill {
    public SpiritSurgeSkill(ResourceLocation icon, ResourceLocation id) {
        super(icon, id, SoulSkillParameters.ACTIVE_DAMAGE, SoulSkillParameters.ACTIVE_SOUL_COST,
                SoulSkillParameters.ACTIVE_COOLDOWN_TICKS, SoulSkillParameters.SPIRIT_SURGE_RADIUS,
                SoulSkillParameters.SPIRIT_SURGE_VERTICAL_RANGE, SoulSkillParameters.SPIRIT_SURGE_DURATION_TICKS, 0, 0.0F);
    }

    @Override
    public void use(Player player, SoulSkillStack skillStack) {
        super.use(player, skillStack);
        if (player instanceof ServerPlayer serverPlayer) {
            rollSoulEcho(serverPlayer, PlayerUtils.getSoulSkillDamage(player, skillStack));
        }
    }

    /// 灵魂回响：按概率再补一发**同参数、不耗魂**的灵魂激荡。
    ///
    /// 只由「灵魂激荡」系列的释放点调用（主动施放与附魂激荡的自动释放），
    /// 额外那一发不会再次掷骰，避免无限连锁。
    public static void rollSoulEcho(ServerPlayer player, float damage) {
        if (!PlayerUtils.isSoulBookLearned(player, SoulBooks.SOUL_ECHO)
                || player.getRandom().nextFloat() >= SoulSkillParameters.SOUL_ECHO_CHANCE) {
            return;
        }
        player.level().addFreshEntity(new SpiritSurgeEntity(player, damage));
    }
}
