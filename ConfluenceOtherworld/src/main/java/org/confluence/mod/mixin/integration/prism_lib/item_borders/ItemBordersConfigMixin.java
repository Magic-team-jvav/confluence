package org.confluence.mod.mixin.integration.prism_lib.item_borders;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.integration.prism_lib.PrismLibHelper;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;
import java.util.function.Supplier;

@Pseudo
@Mixin(targets = "com.anthonyhilyard.itemborders.config.ItemBordersConfig", remap = false)
public abstract class ItemBordersConfigMixin {
    @Dynamic
    @Shadow
    private Map<Object, Pair<Supplier<Integer>, Supplier<Integer>>> cachedCustomBorders;

    @Dynamic
    @Inject(method = "getBorderColorForItem", at = @At(value = "INVOKE", target = "Ljava/util/function/Supplier;get()Ljava/lang/Object;", ordinal = 0), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void animateColor(CallbackInfoReturnable<Pair<Supplier<Integer>, Supplier<Integer>>> cir, @Local(argsOnly = true) ItemStack item, @Coerce Object itemKey) {
        Pair<Supplier<Integer>, Supplier<Integer>> pair = PrismLibHelper.getSpecialColor(item);
        if (pair != null) {
            cachedCustomBorders.put(itemKey, pair);
            cir.setReturnValue(pair);
        }
    }
}
