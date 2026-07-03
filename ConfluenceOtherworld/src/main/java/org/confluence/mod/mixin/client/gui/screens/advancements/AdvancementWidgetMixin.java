package org.confluence.mod.mixin.client.gui.screens.advancements;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.advancements.Advancement;
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
    private Advancement advancement;

    @Inject(method = "drawConnectivity", at = @At("HEAD"), cancellable = true)
    private void disconnect(CallbackInfo ci) {
        if (AchievementToast.hideLink(advancement.getId(), false)) {
            ci.cancel();
        }
    }

    @WrapWithCondition(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderFakeItem(Lnet/minecraft/world/item/ItemStack;II)V"))
    private boolean renderIcon(GuiGraphics instance, ItemStack stack, int x, int y) {
        return AchievementToast.renderWidgetIcon(advancement.getId(), instance, x, y);
    }
}
