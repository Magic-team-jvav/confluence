package org.confluence.terra_curio.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.confluence.terra_curio.client.handler.GravitationHandler;
import org.confluence.terra_curio.client.handler.ScopeFovHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
    @WrapOperation(method = "turnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"))
    private void modify(LocalPlayer instance, double y, double x, Operation<Void> original) {
        if (GravitationHandler.isShouldRot()) {
            x = -x;
            y = -y;
        }
        if (ScopeFovHandler.isScoping()) {
            double factor = ScopeFovHandler.getCameraMoveFactor();
            x *= factor;
            y *= factor;
        }
        original.call(instance, y, x);
    }
}
