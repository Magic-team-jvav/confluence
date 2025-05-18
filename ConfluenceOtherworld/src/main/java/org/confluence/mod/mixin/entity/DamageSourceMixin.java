package org.confluence.mod.mixin.entity;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.mixed.IDamageSource;
import org.confluence.mod.mixed.Immunity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageSource.class)
public abstract class DamageSourceMixin implements IDamageSource, SelfGetter<DamageSource> {
    @Unique
    private boolean confluence$critical;

    @Override
    public void confluence$setCritical(boolean critical) {
        confluence$critical = critical;
    }

    @Override
    public boolean confluence$isCritical() {
        return confluence$critical;
    }

    @Inject(method = "is(Lnet/minecraft/tags/TagKey;)Z", at = @At("HEAD"), cancellable = true)
    private void isTag(TagKey<DamageType> damageTypeKey, CallbackInfoReturnable<Boolean> cir) {
        if (damageTypeKey == DamageTypeTags.BYPASSES_COOLDOWN && Immunity.getCause(confluence$self()) != null) {
            cir.setReturnValue(true);
        }
    }
}
