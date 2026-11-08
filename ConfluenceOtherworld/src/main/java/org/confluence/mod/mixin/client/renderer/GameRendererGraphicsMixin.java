package org.confluence.mod.mixin.client.renderer;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.confluence.mod.client.graphics.TerraGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/// Confluence 画质后处理：世界和手都画完（renderLevel 结束），原版的后处理效果和 GUI 之前。
@Mixin(GameRenderer.class)
public abstract class GameRendererGraphicsMixin {
    @Inject(method = "renderLevel", at = @At("RETURN"))
    private void confluence$terraGraphics(DeltaTracker deltaTracker, CallbackInfo ci) {
        TerraGraphics.apply();
    }
}
