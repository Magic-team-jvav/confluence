package org.confluence.mod.mixin.integration.create.ponder;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.ponder.enums.PonderKeybinds;
import net.createmod.ponder.foundation.ui.PonderUI;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.block.functional.crafting.AltarBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "net.createmod.ponder.foundation.PonderTooltipHandler", remap = false)
public abstract class PonderTooltipHandlerMixin {
    @Shadow
    static ItemStack trackingStack;

    @Shadow
    static boolean subject;

    @WrapOperation(method = "deferredTick", at = @At(value = "INVOKE", target = "Lnet/createmod/catnip/animation/LerpedFloat;setValue(D)V", ordinal = 1))
    private static void modify(LerpedFloat instance, double value, Operation<Void> original, @Local float vValue) {
        if (!subject && PonderKeybinds.PONDER.isDown() && trackingStack.getItem() instanceof AltarBlock.BItem) {
            if (vValue >= 1) {
                ScreenOpener.transitionTo(PonderUI.of(trackingStack));
                instance.startWithValue(0);
            } else {
                instance.setValue(Math.min(1, vValue + Math.max(.25f, vValue) * .25f));
            }
        } else {
            original.call(instance, value);
        }
    }
}
