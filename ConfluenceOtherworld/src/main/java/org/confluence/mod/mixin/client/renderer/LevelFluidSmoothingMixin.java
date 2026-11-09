package org.confluence.mod.mixin.client.renderer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.client.graphics.SmoothFluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/// 液体表面平滑：客户端方块变化前后各算一次液体表面角点高度（只在客户端列表里，服务端不加载）
@Mixin(Level.class)
public abstract class LevelFluidSmoothingMixin {
    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At("HEAD"))
    private void confluence$fluidBefore(BlockPos pos, BlockState state, int flags, int recursionLeft, CallbackInfoReturnable<Boolean> cir) {
        SmoothFluids.beforeChange((Level) (Object) this, pos, state);
    }

    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At("RETURN"))
    private void confluence$fluidAfter(BlockPos pos, BlockState state, int flags, int recursionLeft, CallbackInfoReturnable<Boolean> cir) {
        SmoothFluids.afterChange((Level) (Object) this, pos, cir.getReturnValueZ());
    }
}
