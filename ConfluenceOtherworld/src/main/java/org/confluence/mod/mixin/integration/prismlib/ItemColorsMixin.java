package org.confluence.mod.mixin.integration.prismlib;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.integration.prism_lib.PrismLibHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.anthonyhilyard.prism.item.ItemColors", remap = false)
public abstract class ItemColorsMixin {
    @Inject(method = "getColorForItem", at = @At("HEAD"), cancellable = true)
    private static void rarity(CallbackInfoReturnable<TextColor> cir, @Local(argsOnly = true) ItemStack item) {
        TextColor textColor = PrismLibHelper.getRarityColor(item);
        if (textColor != null) cir.setReturnValue(textColor);
    }
}
