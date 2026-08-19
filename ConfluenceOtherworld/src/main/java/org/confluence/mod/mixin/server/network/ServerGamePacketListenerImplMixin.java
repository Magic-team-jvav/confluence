package org.confluence.mod.mixin.server.network;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.mixed.IServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
    /// 允许创造模式同步使用全局扩展堆叠上限的物品栈。
    @ModifyConstant(method = "handleSetCreativeModeSlot", constant = @Constant(intValue = 64))
    private int extendCreativeStackLimit(int original) {
        return LibUtils.getMaxStackSize(original);
    }

    @WrapOperation(method = {"handleMoveVehicle", "handleMovePlayer"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;checkMovementStatistics(DDD)V"))
    private void captureSpeed(ServerPlayer instance, double x, double y, double z, Operation<Void> original) {
        IServerPlayer.of(instance).confluence$getMovementSpeed().set(x, y, z);
        original.call(instance, x, y, z);
    }
}
