package org.confluence.lib.mixin.fixer;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.resources.ResourceLocation;
import org.confluence.lib.common.data.IdFixer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.core.component.DataComponentPatch$PatchKey")
public abstract class DataComponentPatch$PatchKeyFixer {
    @ModifyExpressionValue(method = "lambda$static$2", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;tryParse(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"))
    private static ResourceLocation fixPatchKeyNamespace(ResourceLocation original) {
        return IdFixer.fixPatchKeyNamespace(original);
    }
}
