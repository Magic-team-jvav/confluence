package org.confluence.mod.mixin.client.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import org.confluence.mod.client.gui.SecretSeedsSelectionScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.client.gui.screens.worldselection.CreateWorldScreen$WorldTab")
public abstract class CreateWorldScreen$WorldTabMixin {
    @Shadow
    @Final
    private EditBox seedEdit;

    @Shadow
    @Final
    CreateWorldScreen this$0;

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/CommonLayouts;labeledElement(Lnet/minecraft/client/gui/Font;Lnet/minecraft/client/gui/layouts/LayoutElement;Lnet/minecraft/network/chat/Component;)Lnet/minecraft/client/gui/layouts/Layout;"))
    private Layout setSeedEditorWidthAndAddButton(Font font, LayoutElement element, Component label, Operation<Layout> original) {
        int size = seedEdit.getHeight();
        seedEdit.setWidth(seedEdit.getWidth() - size);
        LinearLayout layout = LinearLayout.horizontal().spacing(8);
        layout.addChild(element);
        layout.addChild(new ImageButton(0, 0, size, size, SecretSeedsSelectionScreen.SPRITES, button -> {
            button.setFocused(false);
            this$0.getMinecraft().pushGuiLayer(new SecretSeedsSelectionScreen(seedEdit, this$0.getUiState()));
        }), settings -> settings.paddingLeft(-4));
        return original.call(font, layout, label);
    }
}
