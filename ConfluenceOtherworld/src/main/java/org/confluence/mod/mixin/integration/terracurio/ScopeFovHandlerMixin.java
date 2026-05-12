package org.confluence.mod.mixin.integration.terracurio;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.init.item.ToolItems;
import org.confluence.terra_curio.client.handler.ScopeFovHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ScopeFovHandler.class, remap = false)
public abstract class ScopeFovHandlerMixin {
    @Shadow
    private static boolean scoping;

    @Shadow
    private static float fovModifier;

    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    private static void binoculars(Player player, CallbackInfo ci) {
        if (player.isUsingItem() && player.getMainHandItem().is(ToolItems.BINOCULARS)) {
            if (!scoping) {
                if (fovModifier != 1.0F) {
                    player.playSound(SoundEvents.SPYGLASS_USE);
                }
                scoping = true;
            }
            ci.cancel();
        }
    }
}
