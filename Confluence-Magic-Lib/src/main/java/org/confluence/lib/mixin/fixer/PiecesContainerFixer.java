package org.confluence.lib.mixin.fixer;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import org.confluence.lib.common.data.IdFixer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PiecesContainer.class)
public abstract class PiecesContainerFixer {
    @ModifyExpressionValue(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;parse(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"))
    private static ResourceLocation fixPieceNamespace(ResourceLocation original) {
        return IdFixer.fixPieceNamespace(original);
    }
}
