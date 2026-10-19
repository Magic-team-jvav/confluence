package org.confluence.mod.common.soulcerer.skill;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.util.PlayerUtils;

import java.util.List;
import java.util.function.Supplier;

/// 血液沸腾：消耗当前灵魂的固定比例，换取“下一次近战攻击”的巨额伤害加成。
///
/// 加成在命中时由 `LivingEntityEvents` 结算并消耗掉该增益；灵魂消耗在 `use` 内完成（基础消耗为 0）。
public class BoilingBloodSkill extends SoulSkill {
    private final Supplier<Holder<MobEffect>> effect;

    public BoilingBloodSkill(ResourceLocation icon, ResourceLocation id, Supplier<Holder<MobEffect>> effect) {
        super(icon, id, 0.0F, Kind.BUFF, 0.0F, SoulSkillParameters.BOILING_BLOOD_COOLDOWN_TICKS);
        this.effect = effect;
    }

    @Override
    public boolean canUse(ServerPlayer player, SoulSkillStack skillStack) {
        return player.isCreative() || PlayerUtils.getSoul(player) >= 1.0F;
    }

    @Override
    public void use(Player player, SoulSkillStack skillStack) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        float cost = Math.max(1.0F, PlayerUtils.getSoul(serverPlayer) * SoulSkillParameters.BOILING_BLOOD_SOUL_RATIO);
        if (!PlayerUtils.consumeSoul(serverPlayer, cost)) {
            return;
        }
        serverPlayer.addEffect(new MobEffectInstance(effect.get(), SoulSkillParameters.BOILING_BLOOD_DURATION_TICKS, 0, false, true, true));
    }

    @Override
    public List<BuffDuration> getBuffDurations(SoulSkillStack skillStack) {
        return List.of(buffDuration(effect, SoulSkillParameters.BOILING_BLOOD_DURATION_TICKS));
    }

    @Override
    public boolean isBuffActive(Player player) {
        return player.hasEffect(effect.get());
    }
}
