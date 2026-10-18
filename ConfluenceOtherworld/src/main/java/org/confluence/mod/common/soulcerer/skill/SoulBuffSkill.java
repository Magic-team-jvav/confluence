package org.confluence.mod.common.soulcerer.skill;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

import java.util.function.Supplier;

/// 附魂类技能：施放后给自己一层同名增益。
///
/// 增益效果按技能一一注册（`ModEffects` 中与技能同名），图标与技能图标一致；
/// 数值（持续时间、生命代价）由构造参数决定，效果本身的属性修正写在效果类里。
public class SoulBuffSkill extends SoulSkill {
    private final Supplier<Holder<MobEffect>> effect;
    private final int durationTicks;
    private final float healthCostRatio;

    public SoulBuffSkill(ResourceLocation icon, ResourceLocation id, float soulCost, int cooldown,
                         Supplier<Holder<MobEffect>> effect, int durationTicks, float healthCostRatio) {
        super(icon, id, 0.0F, Kind.BUFF, soulCost, cooldown);
        this.effect = effect;
        this.durationTicks = durationTicks;
        this.healthCostRatio = healthCostRatio;
    }

    /// 服务端施加增益；有生命代价时先扣血再上 Buff。
    @Override
    public void use(Player player, SoulSkillStack skillStack) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (healthCostRatio > 0.0F) {
            float cost = serverPlayer.getMaxHealth() * healthCostRatio;
            serverPlayer.setHealth(Math.max(1.0F, serverPlayer.getHealth() - cost));
        }
        serverPlayer.addEffect(new MobEffectInstance(effect.get(), durationTicks, 0, false, true, true));
    }
}
