package org.confluence.mod.mixin.world.entity.player;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.mixed.ILibDamageSource;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.data.saved.Bestiary;
import org.confluence.mod.common.entity.flail.BaseFlailEntity;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.mixed.IPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin implements IPlayer {
    @Unique
    private BaseFlailEntity confluence$flailEntity;
    @Unique
    private ItemStack confluence$currentBait = ItemStack.EMPTY;

    @Override
    @Deprecated
    public void confluence$setFlailBall(BaseFlailEntity entity) {
        this.confluence$flailEntity = entity;
    }

    @Override
    @Deprecated
    public BaseFlailEntity confluence$getFlailBall() {
        return confluence$flailEntity;
    }

    @Override
    public void confluence$setCurrentBait(ItemStack bait) {
        this.confluence$currentBait = bait;
    }

    @Override
    public ItemStack confluence$getCurrentBait() {
        return confluence$currentBait;
    }

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private void attack(CallbackInfo ci, @Local DamageSource damagesource, @Local(ordinal = 2) boolean flag1) {
        ILibDamageSource lds = ILibDamageSource.of(damagesource);
        if (lds != null) {
            lds.confluence$setCritical(flag1);
        }
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

    @Inject(method = "touch", at = @At("TAIL"))
    private void touch(Entity entity, CallbackInfo ci) {
        if (!confluence$self().isLocalPlayer() && entity instanceof LivingEntity living && LibEntityUtils.isAnimal(living)) {
            if (!Bestiary.INSTANCE.containsKey(living)) {
                Bestiary.INSTANCE.updateEntry(living, false);
            }
        }
    }
}
