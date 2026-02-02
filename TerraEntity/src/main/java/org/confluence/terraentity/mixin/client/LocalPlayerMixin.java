package org.confluence.terraentity.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.PlayerRideableJumping;
import org.confluence.terraentity.api.entity.IFlyRideableMob;
import org.confluence.terraentity.network.c2s.ServerBoundVehicleExtensionPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {

    @Shadow @Nullable public abstract PlayerRideableJumping jumpableVehicle();

    @Shadow private int jumpRidingTicks;

    @Shadow
    public float jumpRidingScale;
    @Shadow public Input input;

    @Unique
    boolean confluence$flag;

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;aiStep()V"), cancellable = true)
    public void aiStep(CallbackInfo ci, @Local PlayerRideableJumping jumping) {
        if(jumpableVehicle() instanceof IFlyRideableMob mob){
            this.jumpRidingScale = mob.calJumpingScale(jumpRidingTicks, this.jumpRidingScale);
            if(!this.confluence$flag && input.jumping){
                ServerBoundVehicleExtensionPacket.sendAction(ServerBoundVehicleExtensionPacket.Action.START_INPUT_JUMP);
            }
            if(this.confluence$flag && !input.jumping){
                ServerBoundVehicleExtensionPacket.sendAction(ServerBoundVehicleExtensionPacket.Action.STOP_INPUT_JUMP);
            }
        }

    }

    @Inject(method = "aiStep", at = @At(value = "HEAD"))
    public void aiStep(CallbackInfo ci) {
        this.confluence$flag = input.jumping;

    }

}
