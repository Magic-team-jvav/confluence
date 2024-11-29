package org.confluence.mod.mixin.client;

import net.minecraft.advancements.AdvancementNode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import org.confluence.mod.common.advancement.ModAchievements;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AdvancementWidget.class)
public abstract class AdvancementWidgetMixin {
    @Shadow
    @Final
    private AdvancementNode advancementNode;

    @Inject(method = "drawConnectivity", at = @At("HEAD"), cancellable = true)
    private void disconnect(GuiGraphics pGuiGraphics, int pX, int pY, boolean pDropShadow, CallbackInfo ci) {
        if (ModAchievements.DISPLAY_OFFSET.containsKey(advancementNode.holder().id())) {
            ci.cancel();
        }
    }
}
