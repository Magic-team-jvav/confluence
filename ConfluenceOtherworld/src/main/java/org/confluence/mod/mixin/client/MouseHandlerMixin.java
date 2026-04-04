package org.confluence.mod.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.confluence.mod.client.gui.hud.HouseSelectHUD;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.mixed.ILocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @WrapWithCondition(method = "handleAccumulatedMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MouseHandler;turnPlayer(D)V"))
    private boolean canTurn(MouseHandler instance, double d6) {
        assert minecraft.player != null;
        return ILocalPlayer.of(minecraft.player).confluence$isCanMove();
    }

    @WrapWithCondition(method = "onPress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MouseHandler;grabMouse()V"))
    private boolean cancelGrab(MouseHandler instance) {
        if (HouseSelectHUD.inSelectHUD || ClientPacketHandler.isAskForSoftcoreLayer()) {
            return false;
        }
        return true;
    }
}
