package org.confluence.mod.mixin.client.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.TriState;
import org.confluence.mod.mixed.IAbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin implements IAbstractContainerScreen {
    @Unique
    private boolean confluence$shouldRenderGroupBackground;

    @Override
    public void confluence$setShouldRenderGroupBackground(boolean should) {
        this.confluence$shouldRenderGroupBackground = should;
    }

    @Override
    public boolean confluence$shouldRenderGroupBackground() {
        return confluence$shouldRenderGroupBackground;
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        TriState triState = confluence$onMouseClicked(mouseX, mouseY, button);
        if (!triState.isDefault()) cir.setReturnValue(triState.isTrue());
    }

    @WrapOperation(method = "renderSlotContents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderItem(Lnet/minecraft/world/item/ItemStack;III)V"))
    private void renderGroupBackground(GuiGraphics instance, ItemStack stack, int x, int y, int seed, Operation<Void> original, @Local(argsOnly = true) Slot slot) {
        if (confluence$shouldRenderGroupBackground) {
            IAbstractContainerScreen.renderGroupBackground(instance, stack, x, y, slot);
        }
        original.call(instance, stack, x, y, seed);
    }
}
