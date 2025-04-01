package org.confluence.mod.mixin.integration.jei;

import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "mezz.jei.library.plugins.vanilla.cooking.fuel.FurnaceFuelCategory", remap = false)
public abstract class FurnaceFuelCategoryMixin {
    @Inject(method = "createSmeltCountText", at = @At("HEAD"), cancellable = true)
    private static void infinite(int burnTime, CallbackInfoReturnable<Component> cir) {
        if (burnTime == 1061109567) { // 0x3F3F3F3F
            cir.setReturnValue(Component.translatable("gui.jei.category.fuel.smeltCount", "∞"));
        }
    }
}
