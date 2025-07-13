package org.confluence.mod.mixin.integration.terra_entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Holder;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.mixed.IDamageSource;
import org.confluence.terraentity.entity.boss.Skeletron;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Skeletron.class, remap = false)
public abstract class SkeletronMixin {
    @Inject(method = "addEffect", at = @At("HEAD"), cancellable = true)
    private void invulnerable(MobEffectInstance effectInstance, Entity entity, CallbackInfoReturnable<Boolean> cir) {
        Holder<MobEffect> effect = effectInstance.getEffect();
        if (effect == ModEffects.TENTACLE_SPIKES || effect == ModEffects.BLOOD_BUTCHERED) { // todo 穿透
            cir.setReturnValue(false);
        }
    }

    @WrapOperation(method = "hurt", at = @At(value = "INVOKE", target = "Lorg/confluence/terraentity/entity/boss/AbstractTerraBossBase;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean limitDamage(Skeletron instance, DamageSource damageSource, float amount, Operation<Boolean> original) {
        if (!damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            int size = instance.hands.size();
            if (size > 2) {
                amount = ((IDamageSource) damageSource).confluence$isCritical() ? 2.0F : 1.0F;
            } else if (size != 0) {
                amount = amount * (1 - size * 0.45F);
            }
        }
        return original.call(instance, damageSource, amount);
    }
}
