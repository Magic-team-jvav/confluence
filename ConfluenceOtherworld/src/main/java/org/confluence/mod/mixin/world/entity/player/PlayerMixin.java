package org.confluence.mod.mixin.world.entity.player;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.mixed.ILibDamageSource;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.data.saved.Bestiary;
import org.confluence.mod.common.entity.flail.BaseFlailEntity;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.util.VoidSeaHelper;
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
    @Unique
    private boolean confluence$voidSeaSwimming;

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

    @Override
    public boolean confluence$isVoidSeaSwimming() {
        return confluence$voidSeaSwimming;
    }

    @Inject(method = "updateSwimming", at = @At("HEAD"), cancellable = true)
    private void confluence$voidSeaSwimming(CallbackInfo ci) {
        Player self = confluence$self();
        if (self.getAbilities().flying || !VoidSeaHelper.isDimensionalOverlapEffect(self)) {
            confluence$voidSeaSwimming = false;
            return;
        }
        if (self.getY() < VoidSeaHelper.getHeight()) {
            confluence$voidSeaSwimming = self.isSprinting() && !self.isPassenger();
            self.setSwimming(self.isSprinting() && !self.isPassenger());
            ci.cancel();
        } else if (confluence$voidSeaSwimming && !self.onGround() && !self.isPassenger()) {
            self.setSwimming(true);
            ci.cancel();
        } else {
            confluence$voidSeaSwimming = false;
        }
    }

    @Inject(method = "travel", at = @At("HEAD"))
    private void confluence$voidSeaSwimmingTravel(Vec3 travelVector, CallbackInfo ci) {
        Player self = confluence$self();
        if (confluence$voidSeaSwimming && !self.isInWater() && self.isSwimming() && !self.isPassenger()) {
            double lookY = self.getLookAngle().y;
            double acceleration = lookY < -0.2 ? 0.085 : 0.06;
            Vec3 movement = self.getDeltaMovement();
            self.setDeltaMovement(movement.add(0.0, (lookY - movement.y) * acceleration, 0.0));
        }
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
        if (!confluence$self().isLocalPlayer() && entity instanceof LivingEntity living && LibUtils.isAnimal(living)) {
            if (!Bestiary.INSTANCE.containsKey(living)) {
                Bestiary.INSTANCE.updateEntry(living, false);
            }
        }
    }
}
