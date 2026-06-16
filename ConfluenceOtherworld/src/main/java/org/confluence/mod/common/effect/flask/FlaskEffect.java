package org.confluence.mod.common.effect.flask;

import PortLib.extensions.net.minecraft.world.entity.LivingEntity.PortLivingEntityExtension;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.common.init.ModEffects;
import org.mesdag.portlib.wrapper.common.PortEffectCure;
import org.mesdag.portlib.wrapper.common.PortTags;
import org.mesdag.portlib.wrapper.world.effect.PortMobEffect;

import java.util.Set;

public abstract class FlaskEffect extends PortMobEffect {
    public FlaskEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public FlaskEffect(MobEffectCategory category, int color, ParticleOptions particle) {
        super(category, color, particle);
    }

    @Override
    public void fillEffectCures(Set<PortEffectCure> cures, MobEffectInstance effectInstance) {
        super.fillEffectCures(cures, effectInstance);
        cures.add(ModEffects.FLASK);
    }

    public abstract void doMeleeAttack(LivingEntity attacker, LivingEntity victim, int amplifier, DamageSource damageSource, float amount);

    public static void onLivingDamage(LivingEntity victim, Entity attacker, DamageSource damageSource, float amount) {
        if (attacker instanceof LivingEntity living && PortLivingEntityExtension.getWeaponItem(living).is(PortTags.Items.MELEE_WEAPON_TOOLS)) {
            for (MobEffectInstance activeEffect : living.getActiveEffects()) {
                if (activeEffect.getEffect() instanceof FlaskEffect flaskEffect) {
                    flaskEffect.doMeleeAttack(living, victim, activeEffect.getAmplifier(), damageSource, amount);
                }
            }
        }
    }

    public static boolean cloneFlaskEffects(ServerPlayer old, ServerPlayer neo) {

        return false;
    }

    public static void removeAnotherFlaskEffects(MobEffectInstance mobEffectInstance, LivingEntity living) {
        if (mobEffectInstance.getCures().contains(ModEffects.FLASK)) {
            living.removeEffectsCuredBy(ModEffects.FLASK);
        }
    }
}
