package org.confluence.mod.mixin.integration.sodium;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.client.textures.LocalBrushData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @see org.confluence.mod.mixin.client.BlockClientMixin
 */
@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockOcclusionCache", remap = false)
public abstract class BlockOcclusionCacheMixin {
    @Shadow
    @Final
    private BlockPos.MutableBlockPos cachedPositionObject;

    @Inject(method = "shouldDrawSide", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/BlockGetter;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"), cancellable = true)
    private void skip(BlockState selfState, BlockGetter view, BlockPos selfPos, Direction facing, CallbackInfoReturnable<Boolean> cir) {
        if (!ClientPacketHandler.hasEchoVisible() && LocalBrushData.hasEcho(cachedPositionObject)) {
            cir.setReturnValue(true);
        }
    }
}
