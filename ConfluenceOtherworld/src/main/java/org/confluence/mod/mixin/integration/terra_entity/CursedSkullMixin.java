package org.confluence.mod.mixin.integration.terra_entity;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.terraentity.entity.monster.CursedSkull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CursedSkull.class, remap = false)
public abstract class CursedSkullMixin {
    @Inject(method = "addEffect", at = @At("HEAD"), cancellable = true)
    private void invulnerable(MobEffectInstance effectInstance, Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (effectInstance.is(ModEffects.CURSED)) {
            cir.setReturnValue(false);
        }
    }
}
