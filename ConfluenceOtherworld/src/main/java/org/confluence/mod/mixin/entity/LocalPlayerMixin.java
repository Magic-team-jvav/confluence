package org.confluence.mod.mixin.entity;

import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.common.item.bow.TerraBowItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin implements SelfGetter<LocalPlayer> {

    @Shadow public abstract boolean isUsingItem();

    @Shadow public Input input;

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/Input;tick(ZF)V", shift = At.Shift.AFTER))
    public void aiStep(CallbackInfo ci) {
        if(isUsingItem() && !confluence$self().isPassenger()) {
            if(confluence$self().getUseItem().getItem() instanceof TerraBowItem) {
                this.input.leftImpulse *= 5;
                this.input.forwardImpulse *= 5;
            }
        }
    }

}
