package org.confluence.mod.mixin.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import org.confluence.mod.util.ModUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DataComponents.class)
public abstract class DataComponentsMixin {
    @Shadow
    @Final
    public static DataComponentType<Integer> MAX_STACK_SIZE;

    @WrapOperation(method = "lambda$static$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ExtraCodecs;intRange(II)Lcom/mojang/serialization/Codec;"))
    private static Codec<Integer> modify(int min, int max, Operation<Codec<Integer>> original) {
        return original.call(min, ModUtils.getMaxStackSize(max));
    }
}
