package org.confluence.mod.mixin.integration.terra_entity;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import org.confluence.terra_curio.common.init.TCEffects;
import org.confluence.terraentity.entity.boss.SkeletronHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SkeletronHand.class, remap = false)
public abstract class SkeletronHandMixin {
    @Inject(method = "addEffect", at = @At("HEAD"), cancellable = true)
    private void invulnerable(MobEffectInstance effectInstance, Entity entity, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(!effectInstance.is(MobEffects.POISON) && !effectInstance.is(TCEffects.CONFUSED));
    }
}
