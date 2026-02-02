package org.confluence.terra_curio.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.terra_curio.client.handler.GravitationHandler;
import org.confluence.terra_curio.mixed.IEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerClientMixin implements SelfGetter<Player> {
    @Unique
    private static final float terra_curio$fix = -(Mth.EPSILON + Mth.EPSILON);

    @ModifyVariable(method = "maybeBackOffFromEdge", at = @At("HEAD"), argsOnly = true)
    private Vec3 backOff(Vec3 pVec) {
        return GravitationHandler.isShouldRot(confluence$self()) ? new Vec3(pVec.x, -pVec.y, pVec.z) : pVec;
    }

    @Inject(method = "maybeBackOffFromEdge", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    private void backOff2(Vec3 vec, MoverType mover, CallbackInfoReturnable<Vec3> cir, @Local(ordinal = 0) double d0, @Local(ordinal = 1) double d1) {
        if (GravitationHandler.isShouldRot(confluence$self())) {
            Vec3 vec3 = cir.getReturnValue();
            cir.setReturnValue(new Vec3(d0, -vec3.y, d1));
        }
    }

    @Inject(method = "maybeBackOffFromEdge", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void backOff3(Vec3 vec, MoverType mover, CallbackInfoReturnable<Vec3> cir) {
        if (GravitationHandler.isShouldRot(confluence$self())) {
            Vec3 vec3 = cir.getReturnValue();
            cir.setReturnValue(new Vec3(vec3.x, -vec3.y, vec3.z));
        }
    }

    @WrapOperation(method = "maybeBackOffFromEdge", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;maxUpStep()F"))
    private float backOff4(Player instance, Operation<Float> original) {
        float maxUpStep = original.call(instance);
        if (GravitationHandler.isShouldRot(confluence$self())) {
            return terra_curio$fix - maxUpStep - IEntity.of(instance).terra_curio$getDimensionHeight();
        }
        return maxUpStep;
    }

    @Inject(method = "jumpFromGround", at = @At("TAIL"))
    private void flipJump(CallbackInfo ci) {
        Player self = confluence$self();
        if (GravitationHandler.isShouldRot(self)) {
            Vec3 vec3 = self.getDeltaMovement();
            self.setDeltaMovement(vec3.x, -vec3.y, vec3.z);
        }
    }
}
