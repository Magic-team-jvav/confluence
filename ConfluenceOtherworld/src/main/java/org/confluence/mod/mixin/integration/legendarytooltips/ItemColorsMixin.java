package org.confluence.mod.mixin.integration.legendarytooltips;

import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.integration.legendarytooltips.LegendaryTooltipsHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.anthonyhilyard.prism.item.ItemColors", remap = false)
public abstract class ItemColorsMixin {
    @Inject(method = "getColorForItem", at = @At("HEAD"), cancellable = true)
    private static void rarity(ItemStack item, TextColor defaultColor, CallbackInfoReturnable<TextColor> cir) {
        TextColor textColor = LegendaryTooltipsHelper.rarityColor(item);
        if (textColor != null) cir.setReturnValue(textColor);
    }
}
