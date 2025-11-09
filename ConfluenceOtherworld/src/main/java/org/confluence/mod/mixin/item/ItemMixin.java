package org.confluence.mod.mixin.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.data.saved.GlobalCloakData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Item.class)
public abstract class ItemMixin {
    @WrapOperation(method = "getName", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;getDescriptionId(Lnet/minecraft/world/item/ItemStack;)Ljava/lang/String;"))
    private String wrap(Item instance, ItemStack stack, Operation<String> original) {
        Item target = GlobalCloakData.INSTANCE.getTarget(instance);
        if (target == instance) {
            return original.call(instance, stack);
        }
        return original.call(target, stack);
    }
}
