package org.confluence.mod.mixin.client;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.client.effect.textures.LocalBrushData;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class BlockClientMixin {
    /**
     * @see org.confluence.mod.mixin.integration.sodium.BlockOcclusionCacheMixin
     */
    @Inject(method = "shouldRenderFace", at = @At("HEAD"), cancellable = true)
    private static void skip(BlockState state, BlockGetter level, BlockPos offset, Direction face, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!ClientPacketHandler.hasEchoVisible() && LocalBrushData.hasEcho(pos)) {
            cir.setReturnValue(true);
        }
    }
}
