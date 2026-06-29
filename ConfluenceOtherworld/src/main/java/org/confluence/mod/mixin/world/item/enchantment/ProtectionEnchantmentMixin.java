package org.confluence.mod.mixin.world.item.enchantment;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import org.confluence.mod.common.enchantment.ArcaneProtectionEnchantment;
import org.mesdag.portlib.diff.Diff;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Diff
@Mixin(ProtectionEnchantment.class)
public class ProtectionEnchantmentMixin {
    @Inject(method = "checkCompatibility", at = @At("HEAD"), cancellable = true)
    private void checkExtra(Enchantment ench, CallbackInfoReturnable<Boolean> cir) {
        if (ench instanceof ArcaneProtectionEnchantment) {
            cir.setReturnValue(false);
        }
    }
}
