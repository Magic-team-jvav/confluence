package org.confluence.mod.mixin.integration.terra_entity;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.terra_curio.common.init.TCEffects;
import org.confluence.terraentity.entity.boss.KingSlime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = KingSlime.class, remap = false)
public abstract class KingSlimeMixin {
    @Inject(method = "addEffect", at = @At("HEAD"), cancellable = true)
    private void invulnerable(MobEffectInstance effectInstance, Entity entity, CallbackInfoReturnable<Boolean> cir) {
        Holder<MobEffect> effect = effectInstance.getEffect();
        if (effect == ModEffects.SHIMMER || effect == TCEffects.CONFUSED || effect == MobEffects.POISON) {
            cir.setReturnValue(false);
        }
    }
}
