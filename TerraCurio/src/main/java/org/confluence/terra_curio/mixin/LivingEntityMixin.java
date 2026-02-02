package org.confluence.terra_curio.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.confluence.terra_curio.common.init.TCEffects;
import org.confluence.terra_curio.mixed.IEntity;
import org.confluence.terra_curio.mixed.ILivingEntity;
import org.confluence.terra_curio.util.TCUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mesdag.particlestorm.particle.ParticleEmitter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements ILivingEntity, SelfGetter<LivingEntity> {
    @Unique
    private int terra_curio$totem_cooldown = -1;
    @Unique
    private Map<ResourceLocation, ParticleEmitter> terra_curio$emitters;
    @Unique
    private FluidState terra_curio$lastWalkedFluidState = null;
    @Unique
    private Set<FluidState> terra_curio$walkableFluidStates;

    @Override
    public void terra_curio$setLastWalkedFluidState(FluidState fluidState) {
        this.terra_curio$lastWalkedFluidState = fluidState;
    }

    @Override
    public @Nullable FluidState terra_curio$getLastWalkedFluidState() {
        return terra_curio$lastWalkedFluidState;
    }

    @Override
    public void terra_curio$resetLastWalkedFluidState(Set<FluidState> fluidStates) {
        this.terra_curio$lastWalkedFluidState = null;
        this.terra_curio$walkableFluidStates = fluidStates;
    }

    @Override
    public boolean terra_curio$isFluidWalkable(FluidState fluidState) {
        return terra_curio$walkableFluidStates != null && terra_curio$walkableFluidStates.contains(fluidState);
    }

    @Override
    public void terra_curio$setTotemCooldown(int cooldown) {
        this.terra_curio$totem_cooldown = cooldown;
    }

    @Override
    public int terra_curio$getTotemCooldown() {
        return terra_curio$totem_cooldown;
    }

    @Override
    public @Nullable Map<ResourceLocation, ParticleEmitter> terra_curio$getParticleEmitters() {
        return terra_curio$emitters;
    }

    @Override
    public @NotNull Map<ResourceLocation, ParticleEmitter> terra_curio$getOrCreateParticleEmitters() {
        if (terra_curio$emitters == null) {
            this.terra_curio$emitters = new Hashtable<>();
        }
        return terra_curio$emitters;
    }

    @Shadow
    public abstract boolean hasEffect(Holder<MobEffect> effect);

    @ModifyArg(method = "checkFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"), index = 2)
    private double modifyParticlePosY(double pPosY) {
        IEntity self = IEntity.of(confluence$self());
        if (self.terra_curio$isShouldRot()) {
            return pPosY + self.terra_curio$getDimensionHeight();
        }
        return pPosY;
    }

    @ModifyReturnValue(method = "canFreeze", at = @At(value = "RETURN", ordinal = 1))
    private boolean checkFreeze(boolean original) {
        return TCUtils.applyFrozenImmune(confluence$self(), original);
    }

    @WrapOperation(method = "getDamageAfterArmorAbsorb", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/CombatRules;getDamageAfterAbsorb(Lnet/minecraft/world/entity/LivingEntity;FLnet/minecraft/world/damagesource/DamageSource;FF)F"))
    private float armorPenetration(LivingEntity entity, float damage, DamageSource damageSource, float armorValue, float armorToughness, Operation<Float> original) {
        return original.call(entity, damage, damageSource, TCAttributes.applyArmorPenetration(damageSource, armorValue), armorToughness);
    }

    @ModifyVariable(method = "travel", at = @At("HEAD"), argsOnly = true)
    private Vec3 confused(Vec3 vec3) {
        return hasEffect(TCEffects.CONFUSED) ? vec3.reverse() : vec3;
    }

    @Inject(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isInWater()Z", ordinal = 0))
    private void cacheFluidWalkable(Vec3 travelVector, CallbackInfo ci, @Local FluidState fluidState, @Share("isFluidWalkable") LocalBooleanRef isFluidWalkable) {
        isFluidWalkable.set(TCUtils.isFluidWalkable(confluence$self(), fluidState));
    }

    @WrapOperation(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getAttributeValue(Lnet/minecraft/core/Holder;)D", ordinal = 0))
    private double skipEfficiency(LivingEntity instance, Holder<Attribute> attribute, Operation<Double> original, @Share("isFluidWalkable") LocalBooleanRef isFluidWalkable) {
        if (isFluidWalkable.get()) return 0;
        return original.call(instance, attribute);
    }

    @WrapOperation(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;multiply(DDD)Lnet/minecraft/world/phys/Vec3;", ordinal = 0))
    private Vec3 notSlowdown(Vec3 instance, double factorX, double factorY, double factorZ, Operation<Vec3> original, @Share("isFluidWalkable") LocalBooleanRef isFluidWalkable) {
        if (isFluidWalkable.get()) {
            return original.call(instance, 0.94, factorY, 0.94);
        }
        return original.call(instance, factorX, factorY, factorZ);
    }

    @WrapOperation(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;canStandOnFluid(Lnet/minecraft/world/level/material/FluidState;)Z"))
    private boolean onFluid(LivingEntity instance, FluidState fluidState, Operation<Boolean> original, @Share("isFluidWalkable") LocalBooleanRef isFluidWalkable) {
        if (isFluidWalkable.get()) {
            return false;
        }
        return original.call(instance, fluidState);
    }

    @Inject(method = "canStandOnFluid", at = @At("RETURN"), cancellable = true)
    private void standOnFluid(FluidState fluidState, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && TCUtils.isFluidWalkable(confluence$self(), fluidState)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "onChangedBlock", at = @At("TAIL"))
    private void onMoved(ServerLevel level, BlockPos pos, CallbackInfo ci) {
        TCUtils.onChangedBlock(confluence$self(), level);
    }

    @Inject(method = "checkTotemDeathProtection", at = @At(value = "CONSTANT", args = "nullValue=true"), cancellable = true)
    private void useTotemAbility(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (TCUtils.applyTotemAbility(confluence$self())) cir.setReturnValue(true);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        if (terra_curio$totem_cooldown > 0) this.terra_curio$totem_cooldown--;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void saveData(CompoundTag compound, CallbackInfo ci) {
        compound.putInt("terra_curio:totem_cooldown", terra_curio$totem_cooldown);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readData(CompoundTag compound, CallbackInfo ci) {
        this.terra_curio$totem_cooldown = compound.getInt("terra_curio:totem_cooldown");
    }
}
