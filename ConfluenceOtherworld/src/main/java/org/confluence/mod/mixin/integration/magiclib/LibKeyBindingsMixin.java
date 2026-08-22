package org.confluence.mod.mixin.integration.magiclib;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.confluence.lib.client.LibKeyBindings;
import org.confluence.mod.client.ModKeyBindings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LibKeyBindings.class, remap = false)
public abstract class LibKeyBindingsMixin {
    @ModifyReturnValue(method = "category", at = @At("RETURN"))
    private static String redirect(String original) {
        return ModKeyBindings.KEY_BINDINGS_CATEGORY;
    }
}
