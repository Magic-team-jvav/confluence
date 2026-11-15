package org.confluence.mod.common.item.sword;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import org.confluence.lib.common.component.ModRarity;


public class EffectSwordItem extends BaseSwordItem {
    private final Holder<MobEffect> effect;
    private final int duration;
    private final int maxAmplifier;
    private final float chance;

    public EffectSwordItem(Tier tier, ModRarity rarity, int rawDamage, float rawSpeed, SwordDefinition.Builder builder,
                           Holder<MobEffect> effect, int duration, int maxAmplifier, float chance) {
        super(tier, rarity, rawDamage, rawSpeed, builder);
        this.effect = effect;
        this.duration = duration;
        this.maxAmplifier = maxAmplifier;
        this.chance = chance;
    }

    @Override
    protected void onDamage(ItemStack weapon, LivingEntity attacker, LivingEntity victim, DamageSource source) {
        if (victim.getRandom().nextFloat() >= chance) return;
        Holder<MobEffect> mobEffect = effect;
        MobEffectInstance current = victim.getEffect(mobEffect);
        int amplifier = current == null ? 0 : Math.min(current.getAmplifier() + 1, maxAmplifier);
        victim.addEffect(new MobEffectInstance(mobEffect, duration, amplifier, false, true, false));
    }
}
