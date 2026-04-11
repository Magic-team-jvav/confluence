package org.confluence.mod.mixin.entity;

import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.confluence.mod.mixed.IDamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageSource.class)
public abstract class DamageSourceMixin implements IDamageSource {
    @Inject(method = "is(Lnet/minecraft/tags/TagKey;)Z", at = @At("HEAD"), cancellable = true)
    private void isTag(TagKey<DamageType> damageTypeKey, CallbackInfoReturnable<Boolean> cir) {
        if (confluence$checkBypassesCooldown(damageTypeKey)) {
            cir.setReturnValue(true);
        }
    }
}
