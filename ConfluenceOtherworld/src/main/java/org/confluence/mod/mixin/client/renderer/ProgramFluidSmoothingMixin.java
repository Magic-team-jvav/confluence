package org.confluence.mod.mixin.client.renderer;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.shaders.Program;
import org.confluence.mod.client.graphics.SmoothFluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/// 液体表面平滑和水面反射：在原版 solid / translucent 地形着色器里加上表面高度过渡（SmoothFluids.patch）
/// 和水面的反射、高光、波纹（WaterSurface.patchFragment）
@Mixin(Program.class)
public abstract class ProgramFluidSmoothingMixin {
    @ModifyArg(method = "compileShaderInternal", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/preprocessor/GlslPreprocessor;process(Ljava/lang/String;)Ljava/util/List;"))
    private static String confluence$smoothFluids(String source, @Local(argsOnly = true) Program.Type type, @Local(argsOnly = true, ordinal = 0) String name) {
        return type == Program.Type.VERTEX ? SmoothFluids.patch(source, true, name)
                : org.confluence.mod.client.graphics.WaterSurface.patchFragment(source, name);
    }
}
