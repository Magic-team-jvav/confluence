package org.confluence.lib.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.LibTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Shadow private ItemStack lastItemInMainHand;

    @Inject(method = "resetAttackStrengthTicker", at = @At("HEAD"), cancellable = true)
    private void denyReset(CallbackInfo ci) {
        if (lastItemInMainHand.is(LibTags.Items.SKIP_RESET_STRENGTH)) {
            ci.cancel();
        }
    }
}
