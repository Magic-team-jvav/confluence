package org.confluence.mod.common.item.sword;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Tier;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.init.ModEffects;
import org.mesdag.portlib.event.entity.living.PortMobEffectEvent;

public class SweetSword extends BaseSwordItem {
    public SweetSword(Tier tier, ModRarity rarity, int rawDamage, float rawSpeed, ModifierBuilder modifierBuilder) {
        super(tier, rarity, rawDamage, rawSpeed, modifierBuilder);
    }

    public static void applyEffects(PortMobEffectEvent.PortApplicable event) {
        LivingEntity entity = event.getEntity();
        if (entity.hasEffect(ModEffects.DELICIOUS.get())) {
            MobEffectInstance effect = event.getEffectInstance();
            float factor = 1;
            if (effect.getEffect().getCategory() == MobEffectCategory.BENEFICIAL) {
                factor = 2f;
            } else if (effect.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
                factor = 0.5f;
            }
            effect.update(new MobEffectInstance(
                    effect.getEffect(),
                    (int) (effect.getDuration() * factor),
                    (int) (effect.getAmplifier() * factor),
                    effect.isAmbient(),
                    effect.isVisible(),
                    effect.showIcon()
            ));
        }
    }
}
