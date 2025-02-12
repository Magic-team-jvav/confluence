package org.confluence.mod.mixin.client.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import org.confluence.mod.integration.KubeJSHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {
    @Shadow
    @Final
    private PoseStack pose;

    @WrapOperation(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I"))
    private int transform(GuiGraphics instance, Font font, String text, int x, int y, int color, boolean dropShadow, Operation<Integer> original, @Local(argsOnly = true, ordinal = 0) int ox, @Local(argsOnly = true, ordinal = 1) int oy) {
        if (KubeJSHelper.shouldDrawStackSize(instance)) {
            return original.call(instance, font, text, x, y, color, dropShadow);
        }
        int w = font.width(text);
        float scale = text.length() >= 4 ? 0.5F : text.length() == 3 ? 0.75F : 1;
        pose.pushPose();
        pose.translate((int) (ox + 16 - (w - 1) * scale), (int) (oy + 16 - 7 * scale), 0);
        pose.scale(scale, scale, 1);
        int s = original.call(instance, font, text, 0, 0, color, dropShadow);
        pose.popPose();
        return Mth.ceil(s * scale);
    }
}
