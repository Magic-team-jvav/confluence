package org.confluence.mod.mixin.world.effect;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffectInstance;
import org.confluence.mod.mixed.IMobEffectInstance;
import org.confluence.mod.util.ModUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEffectInstance.class)
public abstract class MobEffectInstanceMixin implements IMobEffectInstance {
    @Unique
    private boolean confluence$enabled = true;

    @Override
    public void confluence$setEnabled(boolean enabled) {
        this.confluence$enabled = enabled;
    }

    @Override
    public boolean confluence$isEnabled() {
        return confluence$enabled;
    }

    @ModifyReturnValue(method = "save", at = @At("RETURN"))
    private Tag saveExtra(Tag original) {
        if (original instanceof CompoundTag tag) {
            tag.putBoolean("confluence:is_enabled", confluence$isEnabled());
        }
        return original;
    }

    @Inject(method = "load", at = @At("RETURN"))
    private static void loadExtra(CompoundTag nbt, CallbackInfoReturnable<MobEffectInstance> cir) {
        if (nbt.contains("confluence:is_enabled")) {
            IMobEffectInstance.of(cir.getReturnValue()).confluence$setEnabled(nbt.getBoolean("confluence:is_enabled"));
        }
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;shouldApplyEffectTickThisTick(II)Z"))
    private boolean skip(boolean original) {
        if (!confluence$enabled) {
            return false;
        }
        return original;
    }

    @Inject(method = "update", at = @At("HEAD"))
    private void merge(MobEffectInstance other, CallbackInfoReturnable<Boolean> cir) {
        if (!IMobEffectInstance.of(other).confluence$isEnabled()) {
            confluence$setEnabled(false);
        } else if (!confluence$isEnabled() && !ModUtils.isSwitchableEffect(other)) {
            confluence$setEnabled(true);
        }
    }
}
