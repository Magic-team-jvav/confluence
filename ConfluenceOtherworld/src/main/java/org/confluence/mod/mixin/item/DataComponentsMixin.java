package org.confluence.mod.mixin.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponents;
import org.confluence.lib.util.LibUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = DataComponents.class, priority = 1100)
public abstract class DataComponentsMixin {
//    @Shadow
//    @Final
//    public static DataComponentType<Integer> MAX_STACK_SIZE;

    @WrapOperation(method = "lambda$static$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ExtraCodecs;intRange(II)Lcom/mojang/serialization/Codec;"))
    private static Codec<Integer> modify(int min, int max, Operation<Codec<Integer>> original) {
        return original.call(min, LibUtils.getMaxStackSize(max));
    }

//    @WrapWithCondition(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/component/DataComponentMap$Builder;set(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Lnet/minecraft/core/component/DataComponentMap$Builder;", ordinal = 0))
//    private static <T> boolean maxStackSize(DataComponentMap.Builder instance, DataComponentType<T> component, T value) {
//        if (component == MAX_STACK_SIZE) {
//            instance.set(MAX_STACK_SIZE, LibUtils.getMaxStackSize((Integer) value));
//            return false;
//        }
//        return true;
//    }
}
