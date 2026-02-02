package org.confluence.terra_curio.mixin.client;

import net.minecraft.client.player.KeyboardInput;
import org.confluence.terra_curio.client.handler.PlayerJumpHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin {
    @ModifyVariable(method = "tick", at = @At("HEAD"), argsOnly = true)
    private float disableSlow(float sneakingSpeedMultiplier) {
        return PlayerJumpHandler.isOnHorizontalFlight() ? 1.0F : sneakingSpeedMultiplier;
    }
}
