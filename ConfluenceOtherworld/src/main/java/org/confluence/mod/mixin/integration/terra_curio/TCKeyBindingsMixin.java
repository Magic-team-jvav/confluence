package org.confluence.mod.mixin.integration.terra_curio;

import org.confluence.terra_curio.client.TCKeyBindings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = TCKeyBindings.class, remap = false)
public abstract class TCKeyBindingsMixin {
    @ModifyConstant(method = "category", constant = @Constant(stringValue = "key.terra_curio.gameplay"))
    private static String redirect(String original) {
        return "key.confluence.gameplay";
    }
}
