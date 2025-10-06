package org.confluence.mod.mixin.integration.terra_entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.confluence.mod.client.ModKeyBindings;
import org.confluence.terraentity.client.event.TEKeyBindings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = TEKeyBindings.class, remap = false)
public abstract class TEKeyBindingsMixin {
    @ModifyReturnValue(method = "gameplay", at = @At("RETURN"))
    private static String redirect(String original) {
        return ModKeyBindings.KEY_BINDINGS_CATEGORY;
    }
}
