package org.confluence.mod.common.effect.flask;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.common.Tags;
import org.confluence.mod.common.init.ModEffects;

import java.util.Map;
import java.util.Set;

public abstract class FlaskEffect extends MobEffect {
    public FlaskEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public FlaskEffect(MobEffectCategory category, int color, ParticleOptions particle) {
        super(category, color, particle);
    }

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
        super.fillEffectCures(cures, effectInstance);
        cures.add(ModEffects.FLASK);
    }

    public abstract void doMeleeAttack(LivingEntity attacker, LivingEntity victim, int amplifier, DamageSource damageSource, float amount);

    public static void onLivingDamage(LivingEntity victim, DamageSource damageSource, float amount) {
        if (damageSource.getEntity() instanceof LivingEntity attacker && attacker.getWeaponItem().is(Tags.Items.MELEE_WEAPON_TOOLS)) {
            for (MobEffectInstance activeEffect : attacker.getActiveEffects()) {
                if (activeEffect.getEffect().value() instanceof FlaskEffect flaskEffect) {
                    flaskEffect.doMeleeAttack(attacker, victim, activeEffect.getAmplifier(), damageSource, amount);
                }
            }
        }
    }

    public static boolean saveFlaskEffects(Map<Holder<MobEffect>, MobEffectInstance> activeEffects) {
        activeEffects.entrySet().removeIf(entry -> !entry.getValue().getCures().contains(ModEffects.FLASK));
        return false;
    }

    public static void removeAnotherFlaskEffects(MobEffectInstance mobEffectInstance, LivingEntity living) {
        if (mobEffectInstance.getCures().contains(ModEffects.FLASK)) {
            living.removeEffectsCuredBy(ModEffects.FLASK);
        }
    }
}
