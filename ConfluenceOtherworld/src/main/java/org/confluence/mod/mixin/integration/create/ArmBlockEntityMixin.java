package org.confluence.mod.mixin.integration.create;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.init.item.ToolItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity", remap = false)
public abstract class ArmBlockEntityMixin {
    @WrapOperation(method = "getDistributableAmount", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isSameItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean isBottomlessLavaBucket1(ItemStack stack, ItemStack other, Operation<Boolean> original) {
        boolean called = original.call(stack, other);
        if (called && stack.is(ToolItems.BOTTOMLESS_LAVA_BUCKET)) {
            return false;
        }
        return called;
    }

    @WrapOperation(method = "searchForDestination()V",at= @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;matches(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean isBottomlessLavaBucket2(ItemStack stack, ItemStack other, Operation<Boolean> original) {
        boolean called = original.call(stack, other);
        if (called && stack.is(ToolItems.BOTTOMLESS_LAVA_BUCKET)) {
            return false;
        }
        return called;
    }
}
