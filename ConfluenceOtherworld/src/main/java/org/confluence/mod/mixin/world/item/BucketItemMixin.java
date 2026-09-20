package org.confluence.mod.mixin.world.item;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.init.ModTags;
import org.mesdag.portlib.diff.Diff;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Diff
@Mixin(BucketItem.class)
public abstract class BucketItemMixin {
    @Inject(method = "getEmptySuccessItem", at = @At("HEAD"), cancellable = true)
    private static void denyEmpty(CallbackInfoReturnable<ItemStack> cir, @Local(argsOnly = true, ordinal = 0) ItemStack bucketStack) {
        if (bucketStack.is(ModTags.Items.BOTTOMLESS)) {
            cir.setReturnValue(bucketStack);
        }
    }
}
