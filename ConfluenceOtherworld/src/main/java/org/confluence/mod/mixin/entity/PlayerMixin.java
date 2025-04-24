package org.confluence.mod.mixin.entity;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.mixed.IDamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin implements SelfGetter<Player> {
    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private void attack(Entity target, CallbackInfo ci, @Local DamageSource damagesource, @Local(ordinal = 2) boolean flag1) {
        ((IDamageSource) damagesource).confluence$setCritical(flag1);
    }

    @ModifyArg(method = "causeFoodExhaustion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V"))
    private float exhaustionDelay(float exhaustion) {
        if (exhaustion > 0.0F) {
            MobEffectInstance effect = confluence$self().getEffect(ModEffects.HUNGER_DELAYED);
            if (effect != null) {
                float i = Math.min(effect.getAmplifier() + 1, 5) * 0.2F;
                return Math.max(exhaustion - exhaustion * i, 0);
            }
        }
        return exhaustion;
    }
}
