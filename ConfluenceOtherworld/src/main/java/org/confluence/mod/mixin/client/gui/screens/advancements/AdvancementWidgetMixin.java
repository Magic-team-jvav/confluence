package org.confluence.mod.mixin.client.gui.screens.advancements;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.client.gui.AchievementToast;
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
    private void disconnect(CallbackInfo ci) {
        if (AchievementToast.hideLink(advancementNode.holder().id(), false)) {
            ci.cancel();
        }
    }

    @WrapWithCondition(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderFakeItem(Lnet/minecraft/world/item/ItemStack;II)V"))
    private boolean renderIcon(GuiGraphics instance, ItemStack stack, int x, int y) {
        return AchievementToast.renderWidgetIcon(advancementNode.holder().id(), instance, x, y);
    }
}
