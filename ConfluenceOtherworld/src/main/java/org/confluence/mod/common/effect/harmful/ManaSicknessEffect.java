package org.confluence.mod.common.effect.harmful;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.EffectCure;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModTags;

import java.util.Set;

public class ManaSicknessEffect extends MobEffect {
    public ManaSicknessEffect() {
        super(MobEffectCategory.HARMFUL, 0x0000FF);
    }

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
        super.fillEffectCures(cures, effectInstance);
        cures.add(ModEffects.CANNOT_REMOVE_BY_NURSE);
    }

    public static float apply(DamageSource damageSource, float amount) {
        float multiply = 1.0F;
        if (damageSource.getEntity() instanceof Player player) {
            MobEffectInstance manaIssue = player.getEffect(ModEffects.MANA_SICKNESS);
            if (manaIssue != null && (damageSource.is(DamageTypes.MAGIC) || player.getMainHandItem().is(ModTags.Items.MANA_WEAPON))) {
                int duration = manaIssue.getDuration();
                if (duration == -1) {
                    multiply = 0.5F;
                } else if (duration <= 100) {
                    multiply = 0.75F;
                } else {
                    multiply = 0.75F - 0.05F * Math.round((duration - 100) / 20.0F);
                }
            }
        }
        return amount * multiply;
    }
}
