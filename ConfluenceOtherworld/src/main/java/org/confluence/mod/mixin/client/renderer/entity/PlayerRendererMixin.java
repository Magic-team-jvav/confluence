package org.confluence.mod.mixin.client.renderer.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.util.VoidSeaHelper;
import org.confluence.mod.mixed.IPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin {
    @ModifyExpressionValue(method = "setupRotations*", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;isInWater()Z"))
    private boolean confluence$voidSeaWater(boolean original, @Local(argsOnly = true) AbstractClientPlayer entity) {
        if (original) return true;
        if (!VoidSeaHelper.isDimensionalOverlapEffect(entity)) return false;
        if (entity.getY() < VoidSeaHelper.getHeight()) return true;
        if (!IPlayer.of(entity).confluence$isVoidSeaSwimming()) return false;
        if (!entity.isSwimming()) return false;
        return !entity.onGround();
    }

    @ModifyExpressionValue(method = "setupRotations*", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;getXRot()F"))
    private float confluence$voidSeaSwimmingVelocityPitch(float original, @Local(argsOnly = true) AbstractClientPlayer entity, @Local(argsOnly = true, ordinal = 2) float partialTick) {
        if (!VoidSeaHelper.isDimensionalOverlapEffect(entity)) return original;
        if (entity.getY() < VoidSeaHelper.getHeight()) return original;
        if (!IPlayer.of(entity).confluence$isVoidSeaSwimming()) return original;
        if (!entity.isSwimming()) return original;
        if (entity.onGround()) return original;
        Vec3 movement = entity.getDeltaMovementLerped(partialTick);
        if (movement.lengthSqr() > 1.0E-7) {
            return (float) -Math.toDegrees(Math.atan2(movement.y, movement.horizontalDistance()));
        }
        return original;
    }
}
