package org.confluence.mod.common.effect.harmful;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModTags;
import org.mesdag.portlib.wrapper.common.PortEffectCure;
import org.mesdag.portlib.wrapper.common.PortTags;
import org.mesdag.portlib.wrapper.world.effect.PortMobEffect;

import java.util.Set;

public class ManaSicknessEffect extends PortMobEffect {
    public ManaSicknessEffect() {
        super(MobEffectCategory.HARMFUL, 0x0000FF);
    }

    @Override
    public void fillEffectCures(Set<PortEffectCure> cures, MobEffectInstance effectInstance) {
        super.fillEffectCures(cures, effectInstance);
        cures.add(ModEffects.CANNOT_REMOVE_BY_NURSE);
    }

    /// [魔法伤害降低是乘算而非加算，且是在所有伤害系数之后适用的。](https://terraria.wiki.gg/zh/wiki/%E8%80%90%E9%AD%94%E6%80%A7)
    public static float process(ServerPlayer attacker, DamageSource damageSource, float amount) {
        float multiply = 1.0F;
        MobEffectInstance instance = attacker.getEffect(ModEffects.MANA_SICKNESS.get());
        if (instance != null && (damageSource.is(PortTags.DamageTypes.IS_MAGIC) || attacker.getMainHandItem().is(ModTags.Items.MANA_WEAPON))) {
            if (instance.duration == -1) {
                multiply = 0.5F;
            } else if (instance.duration <= 100) {
                multiply = 0.75F;
            } else {
                multiply = 0.75F - 0.05F * Math.round((instance.duration - 100) / 20.0F);
            }
        }
        return amount * multiply;
    }
}
