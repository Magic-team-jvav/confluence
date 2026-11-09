package org.confluence.mod.mixin.client.renderer;

import net.minecraft.client.renderer.ShaderInstance;
import org.confluence.mod.client.graphics.SmoothFluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/// 液体表面平滑：着色器启用时绑定过渡数据（只有打过补丁的地形着色器有这些 uniform）
@Mixin(ShaderInstance.class)
public abstract class ShaderInstanceFluidSmoothingMixin {
    @Unique
    private SmoothFluids.Binding confluence$fluidBinding;

    @Inject(method = "apply", at = @At("TAIL"))
    private void confluence$applyFluids(CallbackInfo ci) {
        if (confluence$fluidBinding == null) confluence$fluidBinding = new SmoothFluids.Binding((ShaderInstance) (Object) this);
        confluence$fluidBinding.apply();
    }

    @Inject(method = {"clear", "close"}, at = @At("HEAD"))
    private void confluence$clearFluids(CallbackInfo ci) {
        if (confluence$fluidBinding != null) confluence$fluidBinding.clear();
    }
}
