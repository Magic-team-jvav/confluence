package org.confluence.mod.mixin.integration.ars_nouveau;

import org.confluence.mod.common.CommonConfigs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.hollingsworth.arsnouveau.client.gui.GuiManaHUD", remap = false)
public abstract class GuiManaHUDMixin {
    @Inject(method = "shouldDisplayBar", at = @At("HEAD"), cancellable = true)
    private static void shouldDisplayBar(CallbackInfoReturnable<Boolean> cir) {
        if (CommonConfigs.ARS_NOUVEAU_COMPATIBILITY.get()) {
            cir.setReturnValue(false);
        }
    }
}
