package org.confluence.lib.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.player.LocalPlayer;
import org.confluence.lib.common.LibTags;
import org.confluence.lib.mixed.SelfGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin implements SelfGetter<LocalPlayer> {
    @ModifyExpressionValue(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isPassenger()Z", ordinal = 1))
    private boolean skipSlowdown(boolean original) {
        if (!original && confluence$self().getUseItem().is(LibTags.Items.SKIP_USING_SLOWDOWN)) {
            return true;
        }
        return original;
    }
}
