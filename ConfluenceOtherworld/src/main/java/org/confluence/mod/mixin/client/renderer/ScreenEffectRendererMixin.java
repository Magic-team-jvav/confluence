package org.confluence.mod.mixin.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import org.confluence.mod.client.gui.VoidSeaFilterRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
public abstract class ScreenEffectRendererMixin {
    @Inject(method = "renderScreenEffect", at = @At("RETURN"))
    private static void confluence$renderVoidSeaFilter(Minecraft minecraft, PoseStack poseStack, CallbackInfo ci) {
        VoidSeaFilterRenderer.render(minecraft, poseStack);
    }
}
