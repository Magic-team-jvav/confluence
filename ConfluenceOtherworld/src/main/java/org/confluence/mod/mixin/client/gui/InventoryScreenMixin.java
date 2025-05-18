package org.confluence.mod.mixin.client.gui;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.mixed.IInventoryScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin implements IInventoryScreen, SelfGetter<InventoryScreen> {
    @Shadow
    @Final
    private RecipeBookComponent recipeBookComponent;
    @Unique
    private Button confluence$extraButton;

    @Override
    public void confluence$setExtraButton(Button button) {
        this.confluence$extraButton = button;
    }

    @Override
    public void confluence$setExtraButtonVisibility(boolean visible, int leftPos) {
        if (confluence$extraButton != null) {
            confluence$extraButton.visible = visible;
            confluence$extraButton.setPosition(leftPos - 16, confluence$extraButton.getY());
        }
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        confluence$setExtraButtonVisibility(!recipeBookComponent.isVisible(), confluence$self().getGuiLeft());
    }

    @Inject(method = "lambda$init$0(Lnet/minecraft/client/gui/components/Button;)V", at = @At("TAIL"))
    private void toggle(Button p_313434_, CallbackInfo ci) {
        confluence$setExtraButtonVisibility(!recipeBookComponent.isVisible(), confluence$self().getGuiLeft());
    }
}
