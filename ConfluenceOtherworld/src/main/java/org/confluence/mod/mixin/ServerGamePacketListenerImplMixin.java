package org.confluence.mod.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.confluence.mod.mixed.IServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
    @WrapOperation(method = {"handleMoveVehicle", "handleMovePlayer"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;checkMovementStatistics(DDD)V"))
    private void captureSpeed(ServerPlayer instance, double x, double y, double z, Operation<Void> original) {
        IServerPlayer.of(instance).confluence$getMovementSpeed().set(x, y, z);
        original.call(instance, x, y, z);
    }
}
