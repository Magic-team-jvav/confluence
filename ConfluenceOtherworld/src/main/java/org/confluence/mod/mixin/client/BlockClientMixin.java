package org.confluence.mod.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import org.confluence.mod.client.effect.textures.LocalBrushData;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class BlockClientMixin {
    /// @see org.confluence.mod.mixin.integration.sodium.BlockOcclusionCacheMixin
    @Inject(method = "shouldRenderFace", at = @At("HEAD"), cancellable = true)
    private static void skip(CallbackInfoReturnable<Boolean> cir, @Local(argsOnly = true, ordinal = 1) BlockPos pos) {
        if (!ClientPacketHandler.hasEchoVisible() && LocalBrushData.hasEcho(pos)) {
            cir.setReturnValue(true);
        }
    }
}
