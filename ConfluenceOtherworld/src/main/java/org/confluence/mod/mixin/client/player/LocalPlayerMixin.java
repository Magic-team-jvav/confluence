package org.confluence.mod.mixin.client.player;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.confluence.mod.common.item.common.ScryingOrb;
import org.confluence.mod.common.util.VoidSeaHelper;
import org.confluence.mod.mixed.ILocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static org.confluence.mod.common.util.VoidSeaConstants.*;

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
        return original || ScryingOrb.spectatingPlayer != null;
    }

    // 客户端玩家受伤没事件的
    // 受伤让视角返回自己
    @Inject(method = "hurt", at = @At("HEAD"))
    private void hurt(CallbackInfoReturnable<Boolean> cir) {
        ScryingOrb.stopSpectating();
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void confluence$voidSeaVerticalMovement(CallbackInfo ci) {
        LocalPlayer self = (LocalPlayer) (Object) this;

        if (!VoidSeaHelper.isTrigger(self)) {
            return;
        }

        if (self.getAbilities().flying
                || self.isPassenger()
                || !self.isAffectedByFluids()) {
            return;
        }

        int vertical = 0;
        if (input.jumping) {
            vertical++;
        }

        if (input.shiftKeyDown) {
            vertical--;
        }

        if (vertical != 0) {
            self.setDeltaMovement(self.getDeltaMovement().add(0.0, vertical * VERTICAL_MOVEMENT_SPEED * self.getAttributeValue(NeoForgeMod.SWIM_SPEED), 0.0));
        }

        float exitAngle = -self.getXRot();
        if (minecraft.options.keySprint.isDown()
                && self.getY() >= VoidSeaHelper.getHeight() - SURFACE_EXIT_RANGE
                && exitAngle >= SURFACE_EXIT_MIN_ANGLE
                && exitAngle <= SURFACE_EXIT_MAX_ANGLE) {
            self.setDeltaMovement(self.getDeltaMovement().add(self.getLookAngle().scale(SURFACE_EXIT_ACCELERATION)));
        }
    }

    @ModifyExpressionValue(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isInWater()Z", ordinal = 2))
    private boolean confluence$keepVoidSeaSwimming(boolean original) {
        LocalPlayer self = (LocalPlayer) (Object) this;
        return original || VoidSeaHelper.isTrigger(self);
    }

    @ModifyExpressionValue(method = "isMovingSlowly", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isVisuallyCrawling()Z"))
    private boolean confluence$voidSeaSwimmingIsNotCrawling(boolean original) {
        LocalPlayer self = (LocalPlayer) (Object) this;
        return original && !(self.isSwimming() && VoidSeaHelper.isTrigger(self));
    }
}
