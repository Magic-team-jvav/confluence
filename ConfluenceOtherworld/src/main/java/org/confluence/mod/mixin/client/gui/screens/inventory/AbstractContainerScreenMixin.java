package org.confluence.mod.mixin.client.gui.screens.inventory;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.confluence.mod.mixed.IAbstractContainerScreen;
import org.mesdag.portlib.wrapper.common.util.PortTriState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin implements IAbstractContainerScreen {
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        PortTriState PortTriState = confluence$onMouseClicked(mouseX, mouseY, button);
        if (!PortTriState.isDefault()) cir.setReturnValue(PortTriState.isTrue());
    }
}
