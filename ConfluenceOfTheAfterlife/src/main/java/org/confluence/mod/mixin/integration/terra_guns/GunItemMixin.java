package org.confluence.mod.mixin.integration.terra_guns;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.ItemStack;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCDataComponentTypes;
import org.confluence.terra_guns.common.item.gun.GunItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = GunItem.class, remap = false)
public abstract class GunItemMixin {
    @ModifyConstant(method = "getName", constant = @Constant(intValue = 0xFFFFFF))
    private int color(int original, @Local(argsOnly = true) ItemStack stack) {
        ModRarity rarity = stack.get(TCDataComponentTypes.MOD_RARITY);
        if (rarity != null) {
            return rarity.getColor();
        }
        return original;
    }
}
