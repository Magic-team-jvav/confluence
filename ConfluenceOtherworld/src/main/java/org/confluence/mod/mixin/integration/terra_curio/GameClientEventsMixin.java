package org.confluence.mod.mixin.integration.terra_curio;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.terra_curio.client.event.GameClientEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = GameClientEvents.class, remap = false)
public abstract class GameClientEventsMixin {
    @ModifyExpressionValue(method = "movementInputUpdate", at = @At(value = "INVOKE", target = "Lorg/confluence/terra_curio/client/handler/TCClientPacketHandler;isHasTabi()Z"))
    private static boolean modify(boolean original) {
        return original || ClientPacketHandler.isSprintable();
    }
}
