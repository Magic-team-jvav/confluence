package org.confluence.mod.mixin.client.gui.screens.worldselection;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import org.confluence.mod.client.gui.SecretSeedsSelectionScreen;
import org.confluence.mod.mixed.IWorldOptions;
import org.mesdag.portlib.client.gui.components.PortImageButton;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin {
    @Inject(method = "createFromExisting", at = @At("HEAD"))
    private static void resetWorldOptions(CallbackInfoReturnable<CreateWorldScreen> cir, @Local(argsOnly = true) WorldCreationContext settings) {
        IWorldOptions.of(settings.options()).confluence$resetSecretFlag();
    }

    @Mixin(targets = "net.minecraft.client.gui.screens.worldselection.CreateWorldScreen$WorldTab")
    public abstract static class WorldTabMixin {
        @Shadow
        @Final
        private EditBox seedEdit;

// todo button       @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/CommonLayouts;labeledElement(Lnet/minecraft/client/gui/Font;Lnet/minecraft/client/gui/layouts/LayoutElement;Lnet/minecraft/network/chat/Component;)Lnet/minecraft/client/gui/layouts/Layout;"))
//        private Layout setSeedEditorWidthAndAddButton(Font font, LayoutElement element, Component label, Operation<Layout> original) {
//            seedEdit.setWidth(seedEdit.getWidth() - seedEdit.getHeight() - 2);
//            LinearLayout layout = LinearLayout.horizontal().spacing(8);
//            layout.addChild(element);
//            layout.addChild(new ImageButton(0, 0, 20, 20, SecretSeedsSelectionScreen.SPRITES, button -> {
//                button.setFocused(false);
//                this$0.getMinecraft().pushGuiLayer(new SecretSeedsSelectionScreen(seedEdit, this$0.getUiState()));
//            }), settings -> settings.paddingLeft(-4));
//            return original.call(font, layout, label);
//        }
        @SuppressWarnings("UnresolvedLocalCapture")
        @Inject(method = "<init>", at = @At("TAIL"))
        private void setSeedEditorWidthAndAddButton(CreateWorldScreen this$0, CallbackInfo ci, @Local GridLayout.RowHelper gridlayout$rowhelper1) {
            seedEdit.setWidth(seedEdit.getWidth() - seedEdit.getHeight() - 2);
            gridlayout$rowhelper1.addChild(new PortImageButton(0, 0, 20, 20, SecretSeedsSelectionScreen.SPRITES, button -> {
                button.setFocused(false);
                this$0.getMinecraft().pushGuiLayer(new SecretSeedsSelectionScreen(seedEdit, this$0.getUiState()));
            }), LayoutSettings.defaults().paddingLeft(-4));
        }
    }
}
