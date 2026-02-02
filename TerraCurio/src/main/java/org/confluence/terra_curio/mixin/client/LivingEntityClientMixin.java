package org.confluence.terra_curio.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.terra_curio.client.handler.GravitationHandler;
import org.confluence.terra_curio.client.handler.StepStoolHandler;
import org.confluence.terra_curio.client.handler.TCClientPacketHandler;
import org.confluence.terra_curio.mixed.IClientLivingEntity;
import org.confluence.terra_curio.mixed.IEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityClientMixin implements IClientLivingEntity, SelfGetter<LivingEntity> {
    @Unique
    private boolean terra_curio$showingCosmetic = false;

    @Override
    public void terra_curio$setShowingCosmetic(boolean showing) {
        this.terra_curio$showingCosmetic = showing;
    }

    @Override
    public boolean terra_curio$isShowingCosmetic() {
        return terra_curio$showingCosmetic;
    }

    @Inject(method = "checkFallDamage", at = @At("HEAD"))
    private void fall(double motionY, boolean onGround, BlockState blockState, BlockPos blockPos, CallbackInfo ci) {
        if (motionY > 0.0 && GravitationHandler.isShouldRot(confluence$self())) {
            confluence$self().fallDistance += (float) motionY;
        }
    }

    @ModifyVariable(method = "travel", at = @At("HEAD"), argsOnly = true)
    private Vec3 confused(Vec3 vec3) {
        LivingEntity living = confluence$self();
        if (IEntity.of(living).terra_curio$isPlayer()) {
            if (GravitationHandler.isShouldRot(living)) {
                return new Vec3(-vec3.x, vec3.y, vec3.z);
            } else if (StepStoolHandler.onStool()) {
                return ((Player) living).isLocalPlayer() ? Vec3.ZERO : vec3;
            }
        }
        return vec3;
    }

    @WrapOperation(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;multiply(DDD)Lnet/minecraft/world/phys/Vec3;", ordinal = 0))
    private Vec3 notSlowdown(Vec3 instance, double factorX, double factorY, double factorZ, Operation<Vec3> original) {
        if (TCClientPacketHandler.floating && TCClientPacketHandler.isCanFloating()) {
            if (IEntity.of(confluence$self()).terra_curio$isPlayer()) return original.call(instance, factorX, 1.0, factorZ);
        }
        return original.call(instance, factorX, factorY, factorZ);
    }

    @ModifyExpressionValue(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/core/Holder;)Z", ordinal = 1))
    private boolean neptunesShell(boolean original) {
        return original || (TCClientPacketHandler.isHasNeptunesShell() && IEntity.of(confluence$self()).terra_curio$isPlayer());
    }
}
