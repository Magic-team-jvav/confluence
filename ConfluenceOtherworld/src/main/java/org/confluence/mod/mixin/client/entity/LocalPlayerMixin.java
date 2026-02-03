package org.confluence.mod.mixin.client.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.confluence.mod.common.item.common.ScryingOrb;
import org.confluence.mod.mixed.ILocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin implements ILocalPlayer {
    @Shadow
    @Final
    protected Minecraft minecraft;

    @Shadow
    public Input input;

    @Unique
    private boolean confluence$canMove = true;

    @Override
    public void confluence$setCanMove(boolean canMove) {
        this.confluence$canMove = canMove;
    }

    @Override
    public boolean confluence$isCanMove() {
        return confluence$canMove;
    }

    @Inject(method = "openTextEdit", at = @At("HEAD"), cancellable = true)
    private void openTombstoneEdit(SignBlockEntity signEntity, boolean isFrontText, CallbackInfo ci) {
        if (ILocalPlayer.redirectEditScreen(signEntity, isFrontText, minecraft)) {
            ci.cancel();
        }
    }

    // 使用占卜球的时候要发送自己的位置给服务端
    @ModifyExpressionValue(method = "sendPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isControlledCamera()Z"))
    private boolean sendPos(boolean original) {
        return original || ScryingOrb.spectating;
    }
}
