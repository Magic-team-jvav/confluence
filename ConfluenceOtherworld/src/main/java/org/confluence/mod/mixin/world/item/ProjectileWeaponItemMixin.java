package org.confluence.mod.mixin.world.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.confluence.mod.integration.supplementaries.SupplementariesHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ProjectileWeaponItem.class, priority = 1100)
public abstract class ProjectileWeaponItemMixin {
    @WrapOperation(method = "useAmmo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;split(I)Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack skipWrapOperation(ItemStack instance, int amount, Operation<ItemStack> original, @Local(argsOnly = true) LivingEntity shooter) {
        if (SupplementariesHelper.shouldSkip(instance, shooter)) {
            return instance.split(amount);
        }
        return original.call(instance, amount);
    }
}
