package org.confluence.mod.mixin.integration.terra_curio;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.confluence.mod.client.ModKeyBindings;
import org.confluence.terra_curio.client.TCKeyBindings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = TCKeyBindings.class, remap = false)
public abstract class TCKeyBindingsMixin {
    @ModifyReturnValue(method = "category", at = @At("RETURN"))
    private static String redirect(String original) {
        return ModKeyBindings.KEY_BINDINGS_CATEGORY;
    }
}
