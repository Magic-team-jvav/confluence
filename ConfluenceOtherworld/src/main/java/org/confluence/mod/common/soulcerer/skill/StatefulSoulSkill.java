package org.confluence.mod.common.soulcerer.skill;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.attachment.SoulStorage;

import java.util.function.Supplier;

/// 魂引天星 / 灵魂透支 / 生命归还 共用的“限时状态技能”：施放后只挂一个同名增益。
///
/// 持续期间的实际行为（坠星、免消耗、生命记录）由 `PlayerUtils.tickSoulSkillStates` 与相关事件结算；
/// 需要跨刻数据的部分写在 `SoulStorage` 里，效果只负责时长与显示。
public class StatefulSoulSkill extends SoulSkill {
    private final Supplier<Holder<MobEffect>> effect;
    private final int durationTicks;
    private final boolean resetLifeReturn;

    public StatefulSoulSkill(ResourceLocation icon, ResourceLocation id, float soulCost, int cooldown,
                             Supplier<Holder<MobEffect>> effect, int durationTicks, boolean resetLifeReturn) {
        super(icon, id, 0.0F, Kind.BUFF, soulCost, cooldown);
        this.effect = effect;
        this.durationTicks = durationTicks;
        this.resetLifeReturn = resetLifeReturn;
    }

    @Override
    public void use(Player player, SoulSkillStack skillStack) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (resetLifeReturn) {
            SoulStorage.of(serverPlayer).setLifeReturnLost(0.0F);
        }
        serverPlayer.addEffect(new MobEffectInstance(effect.get(), durationTicks, 0, false, true, true));
    }
}
